import type { RouteItem, RouteStop, StopTrial } from '../types';
import { getSupabaseClient } from './supabaseClient';

interface ChallengeRow {
  id: string;
  type: StopTrial['type'];
  payload: StopTrial;
}

interface StopRow {
  id: string;
  position: number;
  latitude: number;
  longitude: number;
  map_x: number;
  map_y: number;
  content: Omit<RouteStop, 'trial' | 'coordinates'>;
  route_challenges: ChallengeRow | ChallengeRow[] | null;
}

interface RouteRow {
  id: string;
  title: string;
  status: 'publicada' | 'pendiente' | 'borrador';
  location: string;
  metadata: Omit<RouteItem, 'stops'>;
  route_stops: StopRow[];
}

const MEDIA_BUCKET = 'route-media';
const ROUTE_SELECTION =
  'id, title, status, location, metadata, route_stops(id, position, latitude, longitude, map_x, map_y, content, route_challenges(id, type, payload))';
const MAX_MEDIA_SIZE = 50 * 1024 * 1024;
const ALLOWED_MEDIA_TYPES = new Set([
  'image/jpeg',
  'image/png',
  'image/webp',
  'audio/mpeg',
  'audio/mp4',
  'audio/webm',
  'audio/ogg',
]);

export async function getSavedRouteIds(): Promise<string[]> {
  const client = getSupabaseClient();
  const { data: authData, error: authError } = await client.auth.getUser();
  if (authError) throw new Error(`No se pudo verificar la sesión: ${authError.message}`);
  if (!authData.user) throw new Error('Inicia sesión para cargar tus rutas guardadas.');

  const { data, error } = await client
    .from('user_profiles')
    .select('saved_route_ids')
    .eq('user_id', authData.user.id)
    .maybeSingle();
  if (error) throw new Error(`No se pudieron cargar las rutas guardadas: ${error.message}`);
  return data?.saved_route_ids ?? [];
}

export async function saveSavedRouteIds(routeIds: string[], displayName: string): Promise<void> {
  const client = getSupabaseClient();
  const { data: authData, error: authError } = await client.auth.getUser();
  if (authError) throw new Error(`No se pudo verificar la sesión: ${authError.message}`);
  if (!authData.user) throw new Error('Inicia sesión para guardar tus rutas favoritas.');

  const { data: profile, error: profileError } = await client
    .from('user_profiles')
    .select('display_name')
    .eq('user_id', authData.user.id)
    .maybeSingle();
  if (profileError) throw new Error(`No se pudo cargar el perfil: ${profileError.message}`);

  const metadata = authData.user.user_metadata;
  const name = profile?.display_name ||
    displayName.trim() ||
    (typeof metadata.full_name === 'string' ? metadata.full_name.trim() : '') ||
    (typeof metadata.name === 'string' ? metadata.name.trim() : '') ||
    authData.user.email?.split('@')[0] ||
    '';
  if (!name) throw new Error('No se encontró un nombre para completar el perfil del usuario.');

  const { error } = await client.from('user_profiles').upsert(
    {
      user_id: authData.user.id,
      display_name: name,
      saved_route_ids: [...new Set(routeIds)],
    },
    { onConflict: 'user_id' },
  );
  if (error) throw new Error(`No se pudieron sincronizar las rutas guardadas: ${error.message}`);
}

function mapRoute(row: RouteRow): RouteItem {
  const stops = [...row.route_stops]
    .sort((a, b) => a.position - b.position)
    .map(({ route_challenges: challengeRelation, ...stop }): RouteStop => {
      const challenge = Array.isArray(challengeRelation)
        ? challengeRelation[0]
        : challengeRelation;
      return {
        ...stop.content,
        id: stop.id,
        order: stop.position,
        coordinates: {
          lat: stop.latitude,
          lng: stop.longitude,
          x: stop.map_x,
          y: stop.map_y,
        },
        ...(challenge ? { challengeId: challenge.id, trial: challenge.payload } : {}),
      };
    });

  return {
    ...row.metadata,
    id: row.id,
    title: row.title,
    location: row.location,
    status: row.status,
    stops,
  };
}

export async function getPublishedRoutes(): Promise<RouteItem[]> {
  const { data, error } = await getSupabaseClient()
    .from('routes')
    .select(ROUTE_SELECTION)
    .eq('status', 'publicada')
    .order('created_at', { ascending: false });

  if (error) throw new Error(`No se pudieron cargar las rutas: ${error.message}`);
  return (data as RouteRow[]).map(mapRoute);
}

export async function getMyRoutes(): Promise<RouteItem[]> {
  const client = getSupabaseClient();
  const { data: authData, error: authError } = await client.auth.getUser();
  if (authError) throw new Error(`No se pudo verificar la sesión: ${authError.message}`);
  if (!authData.user) throw new Error('Inicia sesión para ver tus rutas y borradores.');

  const { data, error } = await client
    .from('routes')
    .select(ROUTE_SELECTION)
    .eq('creator_id', authData.user.id)
    .order('created_at', { ascending: false });

  if (error) throw new Error(`No se pudieron cargar tus rutas: ${error.message}`);
  return (data as RouteRow[]).map(mapRoute);
}

async function persistMedia(
  value: string | undefined,
  userId: string,
  routeId: string,
): Promise<string | undefined> {
  if (!value || (!value.startsWith('data:') && !value.startsWith('blob:'))) return value;

  const response = await fetch(value);
  if (!response.ok) throw new Error('No se pudo leer uno de los archivos multimedia de la ruta.');
  const file = await response.blob();
  const contentType = file.type.split(';')[0].trim().toLowerCase() || 'application/octet-stream';
  if (!ALLOWED_MEDIA_TYPES.has(contentType)) {
    throw new Error(`Tipo de archivo multimedia no permitido: ${contentType}.`);
  }
  if (file.size > MAX_MEDIA_SIZE) {
    throw new Error('Cada archivo multimedia debe ocupar como máximo 50 MB.');
  }

  const extension = contentType.split('/')[1].replace('jpeg', 'jpg');
  const path = `${userId}/${routeId}/${crypto.randomUUID()}.${extension}`;
  const client = getSupabaseClient();
  const { error } = await client.storage.from(MEDIA_BUCKET).upload(path, file, {
    contentType,
    upsert: false,
  });
  if (error) throw new Error(`No se pudo guardar un archivo multimedia: ${error.message}`);

  return client.storage.from(MEDIA_BUCKET).getPublicUrl(path).data.publicUrl;
}

export async function saveRoute(route: RouteItem): Promise<RouteItem> {
  const client = getSupabaseClient();
  const { data: authData, error: authError } = await client.auth.getUser();
  if (authError) throw new Error(`No se pudo verificar la sesión: ${authError.message}`);
  if (!authData.user) throw new Error('Inicia sesión para guardar una ruta.');

  const routeId = route.id.replace(/[^a-zA-Z0-9_-]/g, '-');
  const routeToSave = {
    ...route,
    status: route.status === 'publicada' ? 'pendiente' : route.status,
  };
  const image = await persistMedia(route.image, authData.user.id, routeId);
  const stops = await Promise.all(
    route.stops.map(async stop => ({
      ...stop,
      imageUrl: await persistMedia(stop.imageUrl, authData.user.id, routeId),
      recordedAudioUrl: await persistMedia(stop.recordedAudioUrl, authData.user.id, routeId),
      challengeId: stop.trial ? stop.challengeId || crypto.randomUUID() : undefined,
    })),
  );
  const guide = {
    ...route.guide,
    id: authData.user.id,
    name:
      authData.user.user_metadata.full_name ||
      authData.user.user_metadata.name ||
      authData.user.email ||
      route.guide.name,
    avatarUrl: authData.user.user_metadata.avatar_url || route.guide.avatarUrl,
  };
  const { error } = await client.rpc('save_route', {
    route_data: {
      ...routeToSave,
      id: routeId,
      image,
      stops,
      guide: {
        id: guide.id,
        name: guide.name,
        title: guide.title,
        bio: guide.bio,
        avatarUrl: guide.avatarUrl,
        rating: guide.rating,
        reviewsCount: guide.reviewsCount,
        location: guide.location,
        verified: guide.verified,
        routesCount: guide.routesCount,
      },
    },
  });
  if (error) throw new Error(`No se pudo guardar la ruta: ${error.message}`);
  return { ...routeToSave, id: routeId, image, stops };
}

export async function saveChallengeProgress(
  challengeId: string,
  attempts: number,
  score: number,
  completed: boolean,
): Promise<void> {
  if (!Number.isInteger(attempts) || attempts < 0) {
    throw new Error('El número de intentos debe ser un entero no negativo.');
  }
  if (!Number.isInteger(score) || score < 0 || score > 100) {
    throw new Error('La puntuación debe ser un entero entre 0 y 100.');
  }

  const client = getSupabaseClient();
  const { data: authData, error: authError } = await client.auth.getUser();
  if (authError) throw new Error(`No se pudo verificar la sesión: ${authError.message}`);
  if (!authData.user) throw new Error('Inicia sesión para guardar tu progreso.');

  const { error } = await client.from('user_challenge_progress').upsert(
    {
      user_id: authData.user.id,
      challenge_id: challengeId,
      status: completed ? 'completado' : 'iniciado',
      attempts,
      score,
      completed_at: completed ? new Date().toISOString() : null,
    },
    { onConflict: 'user_id,challenge_id' },
  );
  if (error) throw new Error(`No se pudo guardar el progreso del reto: ${error.message}`);
}
