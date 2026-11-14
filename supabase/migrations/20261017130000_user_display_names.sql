create table public.user_profiles (
  user_id uuid primary key references auth.users (id) on delete cascade,
  display_name text not null check (
    char_length(btrim(display_name)) between 1 and 60
  ),
  updated_at timestamptz not null default now()
);

alter table public.user_profiles enable row level security;

grant select, insert, update on public.user_profiles to authenticated;

create policy "Users read their own profile"
  on public.user_profiles for select to authenticated
  using (user_id = (select auth.uid()));

create policy "Users create their own profile"
  on public.user_profiles for insert to authenticated
  with check (user_id = (select auth.uid()));

create policy "Users update their own profile"
  on public.user_profiles for update to authenticated
  using (user_id = (select auth.uid()))
  with check (user_id = (select auth.uid()));
