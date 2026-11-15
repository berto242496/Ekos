-- La unicidad aplica solo a compras activas: tras un reembolso (revoked_at) el usuario puede volver a comprar la ruta.
drop index if exists public.route_purchases_user_route_unique;

create unique index if not exists route_purchases_user_route_active_unique
  on public.route_purchases (user_id, route_id)
  where revoked_at is null;
