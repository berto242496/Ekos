// Edge Function: calcula el trazado a pie entre paradas con OpenRouteService y lo cachea en BD.
// Secreto requerido: supabase secrets set ORS_API_KEY=...
import { createClient } from 'npm:@supabase/supabase-js@2';

const ORS_URL = 'https://api.openrouteservice.org/v2/directions/foot-walking';
const MAX_ORS_WAYPOINTS = 50;
const MAX_ROUTES_PER_REQUEST = 20;

type LatLng = [number, number];
interface RouteInput { id: string; coordinates: LatLng[] }

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });

async function cacheKey(coordinates: LatLng[]): Promise<string> {
  // Redondeo a 6 decimales: suficiente precisión y claves estables.
  const canonical = JSON.stringify(coordinates.map(([lat, lng]) => [lat.toFixed(6), lng.toFixed(6)]));
  const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(canonical));
  return Array.from(new Uint8Array(digest), (b) => b.toString(16).padStart(2, '0')).join('');
}

function isValidCoordinates(coordinates: unknown): coordinates is LatLng[] {
  return Array.isArray(coordinates) && coordinates.length >= 2 && coordinates.length <= MAX_ORS_WAYPOINTS &&
    coordinates.every((c) => Array.isArray(c) && c.length === 2 &&
      typeof c[0] === 'number' && typeof c[1] === 'number' &&
      Math.abs(c[0]) <= 90 && Math.abs(c[1]) <= 180);
}

async function fetchOrsPath(coordinates: LatLng[], apiKey: string): Promise<string | null> {
  // ORS espera [longitud, latitud].
  const response = await fetch(ORS_URL, {
    method: 'POST',
    headers: { 'Authorization': apiKey, 'Content-Type': 'application/json' },
    body: JSON.stringify({ coordinates: coordinates.map(([lat, lng]) => [lng, lat]) })
  });
  if (!response.ok) {
    console.error('ORS error', response.status, await response.text());
    return null;
  }
  const payload = await response.json();
  const geometry = payload?.routes?.[0]?.geometry;
  return typeof geometry === 'string' ? geometry : null;
}

Deno.serve(async (request) => {
  if (request.method !== 'POST') return json({ error: 'Method not allowed' }, 405);

  const apiKey = Deno.env.get('ORS_API_KEY');
  const supabaseUrl = Deno.env.get('SUPABASE_URL');
  const serviceRoleKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY');
  if (!apiKey || !supabaseUrl || !serviceRoleKey) return json({ error: 'Servicio no configurado' }, 500);

  let body: { routes?: RouteInput[] };
  try {
    body = await request.json();
  } catch {
    return json({ error: 'JSON no válido' }, 400);
  }
  const routes = Array.isArray(body.routes) ? body.routes.slice(0, MAX_ROUTES_PER_REQUEST) : [];
  const admin = createClient(supabaseUrl, serviceRoleKey, { auth: { persistSession: false } });

  const paths: Record<string, string | null> = {};
  for (const route of routes) {
    if (typeof route?.id !== 'string') continue;
    if (!isValidCoordinates(route.coordinates)) {
      paths[route.id] = null;
      continue;
    }
    try {
      const key = await cacheKey(route.coordinates);
      const { data: cached } = await admin
        .from('route_path_cache')
        .select('encoded_polyline')
        .eq('cache_key', key)
        .maybeSingle();

      if (cached) {
        paths[route.id] = cached.encoded_polyline;
        continue;
      }

      const encoded = await fetchOrsPath(route.coordinates, apiKey);
      if (encoded) {
        await admin.from('route_path_cache').upsert({
          cache_key: key,
          coordinates: route.coordinates,
          encoded_polyline: encoded
        });
      }
      paths[route.id] = encoded;
    } catch (error) {
      console.error('route-path failed', route.id, error);
      paths[route.id] = null;
    }
  }

  return json({ paths });
});
