// Edge Function: recibe las notificaciones RTDN de Google Play (Pub/Sub push) y retira el acceso
// a una ruta de pago cuando la compra se reembolsa o se contracarga (voidedPurchaseNotification).
// Desplegar sin verificación JWT, porque Pub/Sub no envía tokens de sesión:
//   supabase functions deploy google-play-rtdn --no-verify-jwt
// Secretos requeridos:
//   supabase secrets set GOOGLE_PLAY_PACKAGE_NAME=com.ecosdolugar
//   supabase secrets set RTDN_PUSH_SECRET="<valor aleatorio largo>"
// La suscripción push de Pub/Sub debe apuntar a:
//   https://<proyecto>.supabase.co/functions/v1/google-play-rtdn?secret=<RTDN_PUSH_SECRET>
import { createClient } from 'npm:@supabase/supabase-js@2';

const ONE_TIME_PRODUCT_TYPE = 2;

const json = (body: unknown, status = 200) =>
  new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });

interface VoidedPurchaseNotification { purchaseToken?: string; productType?: number }
interface DeveloperNotification {
  packageName?: string;
  voidedPurchaseNotification?: VoidedPurchaseNotification;
}

Deno.serve(async (request) => {
  if (request.method !== 'POST') return json({ error: 'Method not allowed' }, 405);

  const supabaseUrl = Deno.env.get('SUPABASE_URL');
  const serviceRoleKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY');
  const packageName = Deno.env.get('GOOGLE_PLAY_PACKAGE_NAME');
  const pushSecret = Deno.env.get('RTDN_PUSH_SECRET');
  if (!supabaseUrl || !serviceRoleKey || !packageName || !pushSecret) {
    return json({ error: 'Servicio no configurado' }, 500);
  }

  // Sin el secreto cualquiera podría retirar el acceso de compradores ajenos.
  if (new URL(request.url).searchParams.get('secret') !== pushSecret) {
    return json({ error: 'No autorizado' }, 401);
  }

  let envelope: { message?: { data?: string } };
  try {
    envelope = await request.json();
  } catch {
    return json({ error: 'JSON no válido' }, 400);
  }
  if (!envelope.message?.data) return json({ error: 'Mensaje vacío' }, 400);

  let notification: DeveloperNotification;
  try {
    notification = JSON.parse(atob(envelope.message.data));
  } catch {
    return json({ error: 'Notificación no válida' }, 400);
  }

  // Notificaciones de otras apps, de prueba o que no son anulaciones: se confirman sin cambios.
  if (notification.packageName !== packageName) return json({ ok: true, ignored: 'package' });
  const voided = notification.voidedPurchaseNotification;
  if (!voided?.purchaseToken || voided.productType !== ONE_TIME_PRODUCT_TYPE) {
    return json({ ok: true, ignored: 'not-voided-one-time' });
  }

  const admin = createClient(supabaseUrl, serviceRoleKey, { auth: { persistSession: false } });
  const { error } = await admin
    .from('route_purchases')
    .update({ revoked_at: new Date().toISOString() })
    .eq('purchase_token', voided.purchaseToken)
    .is('revoked_at', null);
  // Un error de base de datos devuelve 500 para que Pub/Sub reintente la notificación.
  if (error) {
    console.error('google-play-rtdn', error);
    return json({ error: 'No se pudo revocar la compra' }, 500);
  }
  return json({ ok: true });
});
