-- Duración a pie (segundos) calculada por OpenRouteService, guardada junto al trazado.
alter table public.route_path_cache add column if not exists duration_seconds integer;
