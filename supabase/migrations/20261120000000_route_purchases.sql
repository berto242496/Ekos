-- Compras de rutas de pago. Una fila por compra de Google Play, identificada por purchase_token.
-- Solo la edge function verify-route-purchase (service role) inserta filas.
create table if not exists public.route_purchases (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users (id) on delete cascade,
  route_id text not null references public.routes (id) on delete cascade,
  product_id text not null,
  purchase_token text not null unique,
  amount_cents integer not null check (amount_cents > 0),
  guide_share_cents integer not null check (guide_share_cents >= 0),
  created_at timestamptz not null default now()
);

create index if not exists route_purchases_user_route_idx
  on public.route_purchases (user_id, route_id);

alter table public.route_purchases enable row level security;

drop policy if exists "route_purchases_select_own" on public.route_purchases;

create policy "route_purchases_select_own"
  on public.route_purchases
  for select
  to authenticated
  using (user_id = (select auth.uid()));

revoke all on public.route_purchases from anon, authenticated;
grant select on public.route_purchases to authenticated;
