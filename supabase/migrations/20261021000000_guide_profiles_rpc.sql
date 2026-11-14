-- Lets signed-in users read the public guide profile (name, bio, photo) of the
-- guides that appear in routes, without opening user_profiles to everyone.
create or replace function public.get_guide_profiles(guide_ids uuid[])
returns table (
  user_id uuid,
  display_name text,
  bio text,
  avatar_url text
)
language sql
stable
security definer
set search_path = public
as $$
  select p.user_id, p.display_name, p.bio, p.avatar_url
  from public.user_profiles p
  where p.user_id = any (guide_ids);
$$;

revoke all on function public.get_guide_profiles(uuid[]) from public;
revoke execute on function public.get_guide_profiles(uuid[]) from anon;
grant execute on function public.get_guide_profiles(uuid[]) to authenticated;
