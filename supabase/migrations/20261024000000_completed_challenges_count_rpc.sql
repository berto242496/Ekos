-- Public counter of completed challenges across all users for the welcome screen.
-- Returns only an aggregate number, so individual progress rows stay private under RLS.
create or replace function public.get_completed_challenges_count()
returns bigint
language sql
stable
security definer
set search_path = public
as $$
  select count(*)
  from public.user_challenge_progress p
  where p.status = 'completado';
$$;

revoke all on function public.get_completed_challenges_count() from public;
grant execute on function public.get_completed_challenges_count() to anon, authenticated;
