-- Caché de trazados a pie calculados con OpenRouteService.
-- La clave es el hash de las coordenadas ordenadas de las paradas, así una misma
-- secuencia de paradas nunca se vuelve a consultar al servicio externo.
create table if not exists public.route_path_cache (
  cache_key text primary key,
  coordinates jsonb not null,
  encoded_polyline text not null,
  created_at timestamptz not null default now()
);

-- Solo la Edge Function (service role) lee y escribe la caché.
alter table public.route_path_cache enable row level security;
