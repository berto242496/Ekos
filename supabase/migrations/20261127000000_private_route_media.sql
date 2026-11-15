-- route-media deja de ser público. La app pide URL firmadas al cargar cada ruta,
-- y solo se firman los archivos de rutas que el usuario puede ver.
-- Ruta del objeto: <creator_id>/<route_id>/<archivo>, así que el route_id es el segundo segmento.

update storage.buckets
set public = false
where id = 'route-media';

drop policy if exists "Route media follows route visibility" on storage.objects;
create policy "Route media follows route visibility"
  on storage.objects for select to anon, authenticated
  using (
    bucket_id = 'route-media'
    and public.can_access_route_content((storage.foldername(name))[2])
  );
