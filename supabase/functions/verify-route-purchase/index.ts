// Edge Function: verifica una compra de Google Play de una ruta de pago, la consume y la registra en route_purchases.
// Secretos requeridos:
//   supabase secrets set GOOGLE_PLAY_PACKAGE_NAME=com.ecosdolugar
//   supabase secrets set GOOGLE_PLAY_SERVICE_ACCOUNT_JSON="$(cat service-account.json)"
// La cuenta de servicio necesita acceso a la API de Google Play Android Developer (permiso de finanzas/pedidos).
import { createClient } from 'npm:@supabase/supabase-js@2';

const PUBLISHER_BASE = 'https://androidpublisher.googleapis.com/androidpublisher/v3/applications';
const SCOPE = 'https://www.googleapis.com/auth/androidpublisher';
const PURCHASED_STATE = 0;
const CONSUMPTION_YET_TO_BE_CONSUMED = 0;

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });

function base64Url(input: Uint8Array | string): string {
  const bytes = typeof input === 'string' ? new TextEncoder().encode(input) : input;
  let binary = '';
  bytes.forEach((b) => (binary += String.fromCharCode(b)));
  return btoa(binary).replace(/=+$/, '').replace(/\+/g, '-').replace(/\//g, '_');
}

async function googleAccessToken(serviceAccountJson: string): Promise<string> {
  const account = JSON.parse(serviceAccountJson);
  const now = Math.floor(Date.now() / 1000);
  const header = base64Url(JSON.stringify({ alg: 'RS256', typ: 'JWT' }));
  const claims = base64Url(JSON.stringify({
    iss: account.client_email,
    scope: SCOPE,
    aud: 'https://oauth2.googleapis.com/token',
    iat: now,
    exp: now + 3600
  }));
  const pem = String(account.private_key).replace(/-----[^-]+-----/g, '').replace(/\s+/g, '');
  const keyBytes = Uint8Array.from(atob(pem), (c) => c.charCodeAt(0));
  const key = await crypto.subtle.importKey(
    'pkcs8', keyBytes, { name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' }, false, ['sign']
  );
  const signature = await crypto.subtle.sign(
    'RSASSA-PKCS1-v1_5', key, new TextEncoder().encode(`${header}.${claims}`)
  );
  const assertion = `${header}.${claims}.${base64Url(new Uint8Array(signature))}`;
  const response = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ grant_type: 'urn:ietf:params:oauth:grant-type:jwt-bearer', assertion })
  });
  if (!response.ok) throw new Error(`Google OAuth respondió HTTP ${response.status}`);
  return (await response.json()).access_token;
}

interface ProductPurchase { purchaseState: number; consumptionState: number }

function purchaseUrl(packageName: string, productId: string, token: string): string {
  return `${PUBLISHER_BASE}/${encodeURIComponent(packageName)}/purchases/products/` +
    `${encodeURIComponent(productId)}/tokens/${encodeURIComponent(token)}`;
}

async function getProductPurchase(
  packageName: string, productId: string, token: string, accessToken: string
): Promise<ProductPurchase> {
  const response = await fetch(purchaseUrl(packageName, productId, token), {
    headers: { Authorization: `Bearer ${accessToken}` }
  });
  if (!response.ok) throw new Error(`Google Play respondió HTTP ${response.status} al verificar la compra`);
  return await response.json();
}

async function consumePurchase(
  packageName: string, productId: string, token: string, accessToken: string
): Promise<void> {
  const response = await fetch(`${purchaseUrl(packageName, productId, token)}:consume`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${accessToken}` }
  });
  if (!response.ok) throw new Error(`Google Play respondió HTTP ${response.status} al consumir la compra`);
}

Deno.serve(async (request) => {
  if (request.method !== 'POST') return json({ error: 'Method not allowed' }, 405);

  const supabaseUrl = Deno.env.get('SUPABASE_URL');
  const serviceRoleKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY');
  const packageName = Deno.env.get('GOOGLE_PLAY_PACKAGE_NAME');
  const serviceAccountJson = Deno.env.get('GOOGLE_PLAY_SERVICE_ACCOUNT_JSON');
  if (!supabaseUrl || !serviceRoleKey || !packageName || !serviceAccountJson) {
    return json({ error: 'Servicio no configurado' }, 500);
  }

  const bearer = request.headers.get('Authorization')?.replace(/^Bearer\s+/i, '') ?? '';
  const admin = createClient(supabaseUrl, serviceRoleKey, { auth: { persistSession: false } });
  const { data: userData, error: userError } = await admin.auth.getUser(bearer);
  if (userError || !userData.user) return json({ error: 'Sesión no válida' }, 401);
  const userId = userData.user.id;

  let body: { routeId?: string; productId?: string; purchaseToken?: string };
  try {
    body = await request.json();
  } catch {
    return json({ error: 'JSON no válido' }, 400);
  }
  const { routeId, productId, purchaseToken } = body;
  if (!routeId || !productId || !purchaseToken) return json({ error: 'Faltan datos de la compra' }, 400);

  // El importe se lee del servidor: el cliente nunca decide cuánto cuesta la ruta.
  const { data: route, error: routeError } = await admin
    .from('routes')
    .select('id, status, metadata')
    .eq('id', routeId)
    .maybeSingle();
  if (routeError) return json({ error: 'No se pudo leer la ruta' }, 500);
  if (!route || route.status !== 'publicada') return json({ error: 'La ruta no está disponible' }, 404);

  const priceCents = Number(route.metadata?.priceCents ?? 0);
  if (!Number.isInteger(priceCents) || priceCents <= 0) return json({ error: 'La ruta es gratuita' }, 400);
  if (productId !== `route_tier_${priceCents}`) return json({ error: 'Producto no válido para esta ruta' }, 400);

  try {
    const accessToken = await googleAccessToken(serviceAccountJson);
    const purchase = await getProductPurchase(packageName, productId, purchaseToken, accessToken);
    if (purchase.purchaseState !== PURCHASED_STATE) return json({ error: 'El pago no se ha completado' }, 402);

    const { data: existing, error: existingError } = await admin
      .from('route_purchases')
      .select('user_id, route_id')
      .eq('purchase_token', purchaseToken)
      .maybeSingle();
    if (existingError) return json({ error: 'No se pudo comprobar la compra' }, 500);
    if (existing && (existing.user_id !== userId || existing.route_id !== routeId)) {
      return json({ error: 'Esta compra ya está asociada a otra ruta o cuenta' }, 409);
    }

    if (!existing) {
      const { error: insertError } = await admin.from('route_purchases').insert({
        user_id: userId,
        route_id: routeId,
        product_id: productId,
        purchase_token: purchaseToken,
        amount_cents: priceCents,
        guide_share_cents: Math.floor(priceCents * 65 / 100)
      });
      if (insertError) return json({ error: 'No se pudo registrar la compra' }, 500);
    }

    // Se consume después de registrar: si falla, el reintento del cliente no duplica la fila.
    if (purchase.consumptionState === CONSUMPTION_YET_TO_BE_CONSUMED) {
      await consumePurchase(packageName, productId, purchaseToken, accessToken);
    }
    return json({ ok: true, routeId });
  } catch (error) {
    console.error('verify-route-purchase', error);
    return json({ error: 'No se pudo verificar la compra' }, 502);
  }
});
