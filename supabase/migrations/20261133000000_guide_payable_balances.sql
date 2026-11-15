-- Saldo liquidable por guía, para el operador (service role / SQL editor); no se expone a la app.
-- Las ventas de los últimos 15 días se retienen: cubre los reembolsos y contracargos habituales
-- antes de transferir. Un saldo negativo es un reembolso posterior a un pago: se descuenta en la siguiente liquidación.
create or replace view public.guide_payable_balances as
with guides as (
  select guide_id from public.route_purchases where guide_id is not null
  union
  select guide_id from public.guide_payouts where guide_id is not null
),
sales as (
  select
    guide_id,
    coalesce(sum(guide_share_cents), 0)::bigint as earned_cents,
    coalesce(sum(guide_share_cents) filter (where created_at > now() - interval '15 days'), 0)::bigint as held_cents
  from public.route_purchases
  where guide_id is not null and revoked_at is null
  group by guide_id
),
paid as (
  select guide_id, coalesce(sum(amount_cents), 0)::bigint as paid_cents
  from public.guide_payouts
  where guide_id is not null
  group by guide_id
)
select
  g.guide_id,
  coalesce(s.earned_cents, 0) as earned_cents,
  coalesce(s.held_cents, 0) as held_cents,
  coalesce(p.paid_cents, 0) as paid_cents,
  coalesce(s.earned_cents, 0) - coalesce(s.held_cents, 0) - coalesce(p.paid_cents, 0) as payable_cents
from guides g
left join sales s on s.guide_id = g.guide_id
left join paid p on p.guide_id = g.guide_id;

revoke all on public.guide_payable_balances from public, anon, authenticated;
