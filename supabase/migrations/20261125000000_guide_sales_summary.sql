-- Monthly transfers to guides. Rows are inserted by the team once a transfer is made.
create table if not exists public.guide_payouts (
  id uuid primary key default gen_random_uuid(),
  guide_id uuid not null references auth.users (id) on delete cascade,
  amount_cents integer not null check (amount_cents > 0),
  period text not null,
  paid_at timestamptz not null default now()
);

create index if not exists guide_payouts_guide_idx on public.guide_payouts (guide_id);

alter table public.guide_payouts enable row level security;

drop policy if exists guide_payouts_select_own on public.guide_payouts;
create policy guide_payouts_select_own
  on public.guide_payouts
  for select
  to authenticated
  using (guide_id = (select auth.uid()));

-- Sales summary for the signed-in guide: per-route sales and totals.
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
