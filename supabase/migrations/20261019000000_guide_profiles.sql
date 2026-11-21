alter table public.user_profiles
  add column if not exists bio text not null default ''
    check (char_length(bio) <= 500),
  add column if not exists avatar_url text;
