-- Histórico contable de ventas: borrar una ruta o una cuenta no elimina las compras ni los pagos al guía.
-- route_purchases guarda al guía (guide_id) para seguir sumando sus ventas aunque la ruta ya no exista.

alter table public.route_purchases add column if not exists guide_id uuid;

update public.route_purchases p
set guide_id = r.creator_id
from public.routes r
where r.id = p.route_id
  and p.guide_id is null;

alter table public.route_purchases alter column route_id drop not null;
alter table public.route_purchases drop constraint if exists route_purchases_route_id_fkey;
alter table public.route_purchases
  add constraint route_purchases_route_id_fkey
  foreign key (route_id) references public.routes (id) on delete set null;

alter table public.route_purchases alter column user_id drop not null;
alter table public.route_purchases drop constraint if exists route_purchases_user_id_fkey;
alter table public.route_purchases
  add constraint route_purchases_user_id_fkey
  foreign key (user_id) references auth.users (id) on delete set null;

alter table public.route_purchases drop constraint if exists route_purchases_guide_id_fkey;
alter table public.route_purchases
  add constraint route_purchases_guide_id_fkey
  foreign key (guide_id) references auth.users (id) on delete set null;

create index if not exists route_purchases_guide_idx on public.route_purchases (guide_id);

-- Los pagos a guías también se conservan si la cuenta del guía se borra.
alter table public.guide_payouts alter column guide_id drop not null;
alter table public.guide_payouts drop constraint if exists guide_payouts_guide_id_fkey;
alter table public.guide_payouts
  add constraint guide_payouts_guide_id_fkey
  foreign key (guide_id) references auth.users (id) on delete set null;

-- Ventas por guía según el snapshot guide_id (no depende de que la ruta siga existiendo).
create or replace function public.get_guide_sales_summary()
returns jsonb
language sql
stable
security definer
set search_path = public
as $$
  with sales as (
    select
      p.route_id,
      count(*)::int as sales_count,
      coalesce(sum(p.guide_share_cents), 0)::bigint as earned_cents
    from public.route_purchases p
    where p.guide_id = (select auth.uid())
      and p.revoked_at is null
    group by p.route_id
  ),
  totals as (
    select coalesce(sum(earned_cents), 0)::bigint as total_cents from sales
  ),
  paid as (
    select coalesce(sum(g.amount_cents), 0)::bigint as paid_cents
    from public.guide_payouts g
    where g.guide_id = (select auth.uid())
  )
  select jsonb_build_object(
    'total_cents', (select total_cents from totals),
    'paid_cents', (select paid_cents from paid),
    'pending_cents', (select total_cents from totals) - (select paid_cents from paid),
    'routes', coalesce(
      (select jsonb_agg(jsonb_build_object(
        'route_id', s.route_id,
        'sales_count', s.sales_count,
        'earned_cents', s.earned_cents
      )) from sales s),
      '[]'::jsonb
    )
  );
$$;

revoke execute on function public.get_guide_sales_summary() from public, anon;
grant execute on function public.get_guide_sales_summary() to authenticated;

-- Al borrar la cuenta, la compra se conserva sin el comprador ni su token de Google Play.
create or replace function public.delete_my_account()
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  current_user_id uuid := (select auth.uid());
begin
  if current_user_id is null then
    raise exception 'Not authenticated' using errcode = '28000';
  end if;
  update public.route_purchases
  set user_id = null,
      purchase_token = 'deleted:' || id::text
  where user_id = current_user_id;
  delete from auth.users where id = current_user_id;
end;
$$;

revoke all on function public.delete_my_account() from public, anon;
grant execute on function public.delete_my_account() to authenticated;
