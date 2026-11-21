alter table public.route_challenges
  drop constraint if exists route_challenges_type_check;

alter table public.route_challenges
  add constraint route_challenges_type_check check (
    type in (
      'acertijo',
      'puzle_runico',
      'cifrado_magico',
      'alineacion_simbolos',
      'puzle_imagen',
      'ordenar_palabra',
      'completar_frase'
    )
  );
