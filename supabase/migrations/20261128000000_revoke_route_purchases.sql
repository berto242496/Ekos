-- Reembolsos y contracargos: Google Play notifica la anulación por RTDN y la edge function
-- google-play-rtdn marca la compra con revoked_at. La fila se conserva como histórico contable.
alter table public.route_purchases add column if not exists revoked_at timestamptz;

create or replace function public.can_access_route_content(p_route_id text)
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
  select exists (
    select 1
    from public.routes r
    where r.id = p_route_id
      and (
        r.creator_id = (select auth.uid())
        or (
          r.status = 'publicada'
          and (
            coalesce(r.metadata ->> 'priceCents', '0') = '0'
            or exists (
              select 1
              from public.route_purchases p
              where p.route_id = r.id
                and p.user_id = (select auth.uid())
                and p.revoked_at is null
            )
          )
        )
      )
  );
$$;

-- Las ventas revocadas no suman al saldo del guía.
create or replace function public.get_guide_sales_summary()
returns jsonb
language sql
stable
security definer
set search_path = public
as $$
  with my_routes as (
    select r.id
    from public.routes r
    where r.creator_id = (select auth.uid())
  ),
  sales as (
    select
      p.route_id,
      count(*)::int as sales_count,
      coalesce(sum(p.guide_share_cents), 0)::bigint as earned_cents
    from public.route_purchases p
    join my_routes m on m.id = p.route_id
    where p.revoked_at is null
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
