create or replace function public.save_route(route_data jsonb)
returns text
language plpgsql
set search_path = ''
as $$
declare
  stop_data jsonb;
  route_id text := route_data ->> 'id';
begin
  if (select auth.uid()) is null then
    raise exception 'Authentication is required to save a route.';
  end if;
  if route_id is null or jsonb_typeof(route_data -> 'stops') <> 'array' then
    raise exception 'Route data must include an id and a stops array.';
  end if;

  insert into public.routes (id, creator_id, title, location, status, metadata)
  values (
    route_id,
    (select auth.uid()),
    route_data ->> 'title',
    route_data ->> 'location',
    route_data ->> 'status',
    route_data - 'stops'
  )
  on conflict (id) do update
  set title = excluded.title,
      location = excluded.location,
      status = excluded.status,
      metadata = excluded.metadata,
      updated_at = now();

  for stop_data in
    select value from jsonb_array_elements(route_data -> 'stops')
  loop
    insert into public.route_stops (
      id, route_id, position, latitude, longitude, map_x, map_y, content
    )
    values (
      stop_data ->> 'id',
      route_id,
      (stop_data ->> 'order')::integer,
      (stop_data #>> '{coordinates,lat}')::double precision,
      (stop_data #>> '{coordinates,lng}')::double precision,
      (stop_data #>> '{coordinates,x}')::double precision,
      (stop_data #>> '{coordinates,y}')::double precision,
      stop_data - 'coordinates' - 'trial' - 'challengeId'
    )
    on conflict (id) do update
    set position = excluded.position,
        latitude = excluded.latitude,
        longitude = excluded.longitude,
        map_x = excluded.map_x,
        map_y = excluded.map_y,
        content = excluded.content;

    if jsonb_typeof(stop_data -> 'trial') = 'object' then
      insert into public.route_challenges (id, stop_id, type, payload)
      values (
        (stop_data ->> 'challengeId')::uuid,
        stop_data ->> 'id',
        stop_data #>> '{trial,type}',
        stop_data -> 'trial'
      )
      on conflict (stop_id) do update
      set id = excluded.id,
          type = excluded.type,
          payload = excluded.payload;
    end if;
  end loop;

  return route_id;
end;
$$;

grant select on public.routes, public.route_stops, public.route_challenges to anon, authenticated;
grant insert, update, delete on public.routes, public.route_stops, public.route_challenges to authenticated;
grant select, insert, update on public.user_challenge_progress to authenticated;

alter table public.user_challenge_progress
  alter column user_id set default auth.uid();
