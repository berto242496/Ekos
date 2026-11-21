create table public.routes (
  id text primary key,
  creator_id uuid not null references auth.users (id) on delete cascade,
  title text not null,
  location text not null,
  status text not null check (status in ('publicada', 'borrador')),
  metadata jsonb not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table public.route_stops (
  id text primary key,
  route_id text not null references public.routes (id) on delete cascade,
  position integer not null check (position > 0),
  latitude double precision not null check (latitude between -90 and 90),
  longitude double precision not null check (longitude between -180 and 180),
  map_x double precision not null check (map_x between 0 and 100),
  map_y double precision not null check (map_y between 0 and 100),
  content jsonb not null,
  unique (route_id, position)
);

create table public.route_challenges (
  id uuid primary key default gen_random_uuid(),
  stop_id text not null unique references public.route_stops (id) on delete cascade,
  type text not null check (
    type in ('acertijo', 'puzle_runico', 'cifrado_magico', 'alineacion_simbolos')
  ),
  payload jsonb not null,
  created_at timestamptz not null default now()
);

create table public.user_challenge_progress (
  user_id uuid not null references auth.users (id) on delete cascade,
  challenge_id uuid not null references public.route_challenges (id) on delete cascade,
  status text not null default 'iniciado' check (status in ('iniciado', 'completado')),
  attempts integer not null default 0 check (attempts >= 0),
  score integer check (score between 0 and 100),
  started_at timestamptz not null default now(),
  completed_at timestamptz,
  primary key (user_id, challenge_id),
  check ((status = 'iniciado' and completed_at is null) or status = 'completado')
);

create index route_stops_route_position_idx on public.route_stops (route_id, position);
create index routes_published_idx on public.routes (status, created_at desc);
create index user_challenge_progress_user_idx on public.user_challenge_progress (user_id);

alter table public.routes enable row level security;
alter table public.route_stops enable row level security;
alter table public.route_challenges enable row level security;
alter table public.user_challenge_progress enable row level security;

grant select on public.routes, public.route_stops, public.route_challenges to anon, authenticated;
grant insert, update, delete on public.routes, public.route_stops, public.route_challenges to authenticated;
grant select, insert, update on public.user_challenge_progress to authenticated;

create policy "Published routes are readable by everyone"
  on public.routes for select
  using (status = 'publicada' or creator_id = (select auth.uid()));

create policy "Authenticated users create their own routes"
  on public.routes for insert to authenticated
  with check (creator_id = (select auth.uid()));

create policy "Creators update their own routes"
  on public.routes for update to authenticated
  using (creator_id = (select auth.uid()))
  with check (creator_id = (select auth.uid()));

create policy "Creators delete their own routes"
  on public.routes for delete to authenticated
  using (creator_id = (select auth.uid()));

create policy "Stops follow route visibility"
  on public.route_stops for select
  using (
    exists (
      select 1 from public.routes r
      where r.id = route_id
        and (r.status = 'publicada' or r.creator_id = (select auth.uid()))
    )
  );

create policy "Route creators manage stops"
  on public.route_stops for all to authenticated
  using (
    exists (
      select 1 from public.routes r
      where r.id = route_id and r.creator_id = (select auth.uid())
    )
  )
  with check (
    exists (
      select 1 from public.routes r
      where r.id = route_id and r.creator_id = (select auth.uid())
    )
  );

create policy "Challenges follow route visibility"
  on public.route_challenges for select
  using (
    exists (
      select 1
      from public.route_stops s
      join public.routes r on r.id = s.route_id
      where s.id = stop_id
        and (r.status = 'publicada' or r.creator_id = (select auth.uid()))
    )
  );

create policy "Route creators manage challenges"
  on public.route_challenges for all to authenticated
  using (
    exists (
      select 1
      from public.route_stops s
      join public.routes r on r.id = s.route_id
      where s.id = stop_id and r.creator_id = (select auth.uid())
    )
  )
  with check (
    exists (
      select 1
      from public.route_stops s
      join public.routes r on r.id = s.route_id
      where s.id = stop_id and r.creator_id = (select auth.uid())
    )
  );

create policy "Users read their own challenge progress"
  on public.user_challenge_progress for select to authenticated
  using (user_id = (select auth.uid()));

create policy "Users create their own challenge progress"
  on public.user_challenge_progress for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy "Users update their own challenge progress"
  on public.user_challenge_progress for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));

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
  );

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
    );

    if jsonb_typeof(stop_data -> 'trial') = 'object' then
      insert into public.route_challenges (stop_id, type, payload)
      values (
        stop_data ->> 'id',
        stop_data #>> '{trial,type}',
        stop_data -> 'trial'
      );
    end if;
  end loop;

  return route_id;
end;
$$;

revoke all on function public.save_route(jsonb) from public, anon;
grant execute on function public.save_route(jsonb) to authenticated;

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values (
  'route-media',
  'route-media',
  true,
  52428800,
  array['image/jpeg', 'image/png', 'image/webp', 'audio/mpeg', 'audio/mp4', 'audio/webm', 'audio/ogg']
)
on conflict (id) do nothing;

create policy "Route media is publicly readable"
  on storage.objects for select
  using (bucket_id = 'route-media');

create policy "Users upload media to their own folder"
  on storage.objects for insert to authenticated
  with check (
    bucket_id = 'route-media'
    and (storage.foldername(name))[1] = (select auth.uid())::text
  );

create policy "Users update media in their own folder"
  on storage.objects for update to authenticated
  using (
    bucket_id = 'route-media'
    and (storage.foldername(name))[1] = (select auth.uid())::text
  )
  with check (
    bucket_id = 'route-media'
    and (storage.foldername(name))[1] = (select auth.uid())::text
  );

create policy "Users delete media in their own folder"
  on storage.objects for delete to authenticated
  using (
    bucket_id = 'route-media'
    and (storage.foldername(name))[1] = (select auth.uid())::text
  );
