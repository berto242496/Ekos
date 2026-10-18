create table if not exists public.route_reviews (
  user_id uuid not null references auth.users (id) on delete cascade,
  route_id text not null references public.routes (id) on delete cascade,
  guide_id text not null,
  route_rating integer not null check (route_rating between 1 and 5),
  guide_rating integer not null check (guide_rating between 1 and 5),
  comment text not null default '' check (char_length(comment) <= 500),
  created_at timestamptz not null default now(),
  primary key (user_id, route_id)
);

create index if not exists route_reviews_route_idx on public.route_reviews (route_id);

alter table public.route_reviews enable row level security;

grant select, insert, update on public.route_reviews to authenticated;
grant select on public.route_reviews to anon;

create policy "Route reviews are readable by everyone"
  on public.route_reviews for select to anon, authenticated
  using (true);

create policy "Users create their own route reviews"
  on public.route_reviews for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy "Users update their own route reviews"
  on public.route_reviews for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));
