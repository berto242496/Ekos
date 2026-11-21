-- Las paradas, retos y archivos multimedia de una ruta de pago solo se entregan al creador
-- y a quien la ha comprado. Sin esto, cualquiera con la anon key podía leerlos sin pagar.

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
            )
          )
        )
      )
  );
$$;

revoke all on function public.can_access_route_content(text) from public;
grant execute on function public.can_access_route_content(text) to anon, authenticated;

drop policy if exists "Stops follow route visibility" on public.route_stops;
create policy "Stops follow route visibility"
  on public.route_stops for select
  using (public.can_access_route_content(route_id));

drop policy if exists "Challenges follow route visibility" on public.route_challenges;
create policy "Challenges follow route visibility"
  on public.route_challenges for select
  using (
    exists (
      select 1
      from public.route_stops s
      where s.id = stop_id
        and public.can_access_route_content(s.route_id)
    )
  );

-- El bucket es público: las URL con token aleatorio siguen sirviéndose por /object/public,
-- pero ya no se puede listar su contenido para descubrir el audio de rutas de pago.
drop policy if exists "Route media is publicly readable" on storage.objects;
drop policy if exists "Users read media in their own folder" on storage.objects;
create policy "Users read media in their own folder"
  on storage.objects for select to authenticated
  using (
    bucket_id = 'route-media'
    and (storage.foldername(name))[1] = (select auth.uid())::text
  );
