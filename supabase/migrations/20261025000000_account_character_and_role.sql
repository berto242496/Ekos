-- Stores the traveler character and whether the account is a guide, so onboarding survives logout/login.
alter table public.user_profiles
    add column if not exists character_id text,
    add column if not exists is_guide boolean not null default false;
