-- Account deletion: every user-owned table references auth.users with ON DELETE CASCADE
-- (routes, route_stops, route_challenges, user_challenge_progress, user_profiles, route_reviews).
-- Storage files must be removed through the Storage API by the client before calling this.
create or replace function public.delete_my_account()
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  current_user_id uuid := (select auth.uid());
begin
  if current_user_id is null then
    raise exception 'Not authenticated' using errcode = '28000';
  end if;
  delete from auth.users where id = current_user_id;
end;
$$;

revoke all on function public.delete_my_account() from public, anon;
grant execute on function public.delete_my_account() to authenticated;

-- Blocking: a user hides another user's routes and reviews.
create table if not exists public.user_blocks (
  blocker_id uuid not null references auth.users (id) on delete cascade,
  blocked_id uuid not null references auth.users (id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (blocker_id, blocked_id),
  check (blocker_id <> blocked_id)
);

alter table public.user_blocks enable row level security;

grant select, insert, delete on public.user_blocks to authenticated;

create policy "Users read their own blocks"
  on public.user_blocks for select to authenticated
  using (blocker_id = (select auth.uid()));

create policy "Users create their own blocks"
  on public.user_blocks for insert to authenticated
  with check (blocker_id = (select auth.uid()));

create policy "Users remove their own blocks"
  on public.user_blocks for delete to authenticated
  using (blocker_id = (select auth.uid()));

-- Reports of user-generated content. Reporters cannot read them back; moderators use the dashboard.
create table if not exists public.content_reports (
  id uuid primary key default gen_random_uuid(),
  reporter_id uuid not null references auth.users (id) on delete cascade,
  target_type text not null check (target_type in ('route', 'review', 'guide')),
  target_id text not null check (char_length(target_id) between 1 and 200),
  reason text not null check (reason in ('inappropriate', 'spam', 'inaccurate', 'harassment', 'other')),
  status text not null default 'pending' check (status in ('pending', 'reviewed', 'dismissed')),
  created_at timestamptz not null default now()
);

create index if not exists content_reports_status_idx on public.content_reports (status, created_at);

alter table public.content_reports enable row level security;

grant insert on public.content_reports to authenticated;

create policy "Users create their own reports"
  on public.content_reports for insert to authenticated
  with check (reporter_id = (select auth.uid()));
