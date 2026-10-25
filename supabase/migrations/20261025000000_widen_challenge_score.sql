-- Las puntuaciones ya no están limitadas a 0..100: Urco suma +15 por prueba y
-- Nubeiro recibe 300 al completar la ruta, y cada fila guarda la puntuación acumulada de la parada.
do $$
declare
  constraint_name text;
begin
  for constraint_name in
    select c.conname
    from pg_constraint c
    join pg_attribute a on a.attrelid = c.conrelid and a.attnum = any (c.conkey)
    where c.conrelid = 'public.user_challenge_progress'::regclass
      and c.contype = 'c'
      and a.attname = 'score'
  loop
    execute format('alter table public.user_challenge_progress drop constraint %I', constraint_name);
  end loop;
end $$;

alter table public.user_challenge_progress
  add constraint user_challenge_progress_score_check check (score >= 0);
