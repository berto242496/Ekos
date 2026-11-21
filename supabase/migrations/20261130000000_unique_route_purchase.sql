-- Una sola compra activa por usuario y ruta. Si ya existen duplicados, la migración falla
-- en lugar de borrar filas: esas ventas hay que revisarlas a mano (no se borra dinero registrado).
do $$
begin
  if exists (
    select 1
    from public.route_purchases
    group by user_id, route_id
    having count(*) > 1
  ) then
    raise exception 'Hay compras duplicadas en route_purchases; revisarlas antes de aplicar esta migración.';
  end if;
end;
$$;

drop index if exists public.route_purchases_user_route_idx;

create unique index if not exists route_purchases_user_route_unique
  on public.route_purchases (user_id, route_id);
