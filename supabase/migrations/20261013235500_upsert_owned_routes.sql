create or replace function public.save_route(route_data jsonb)
returns text
language plpgsql
set search_path = ''
as $$
declare
  stop_data jsonb;
  v_route_id text := route_data ->> 'id';
  affected_rows integer;
begin
  if (select auth.uid()) is null then
    raise exception 'Authentication is required to save a route.';
  end if;
  if v_route_id is null
    or jsonb_typeof(route_data -> 'stops') <> 'array'
    or route_data ->> 'status' not in ('publicada', 'borrador')
  then
    raise exception 'Route data must include an id, valid status, and stops array.';
  end if;

  insert into public.routes (id, creator_id, title, location, status, metadata)
  values (
    v_route_id,
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
      updated_at = now()
  where public.routes.creator_id = (select auth.uid());

  get diagnostics affected_rows = row_count;
  if affected_rows = 0 then
    raise exception 'This route does not exist or is owned by another user.';
  end if;

  delete from public.route_stops
  where route_id = v_route_id
    and id not in (
      select value ->> 'id'
      from jsonb_array_elements(route_data -> 'stops')
    );

  for stop_data in
    select value from jsonb_array_elements(route_data -> 'stops')
  loop
    insert into public.route_stops (
      id, route_id, position, latitude, longitude, map_x, map_y, content
    )
    values (
      stop_data ->> 'id',
      v_route_id,
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
        content = excluded.content
    where public.route_stops.route_id = excluded.route_id;

    get diagnostics affected_rows = row_count;
    if affected_rows = 0 then
      raise exception 'A stop id is already used by another route.';
    end if;

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
    else
      delete from public.route_challenges
      where stop_id = stop_data ->> 'id';
    end if;
  end loop;

  return v_route_id;
end;
$$;

revoke all on function public.save_route(jsonb) from public, anon;
grant execute on function public.save_route(jsonb) to authenticated;
