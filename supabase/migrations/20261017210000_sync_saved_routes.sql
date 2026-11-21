alter table public.user_profiles
  add column saved_route_ids text[] not null default '{}';
