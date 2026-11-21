# Flujo de expedición geolocalizada

1. Detalle de ruta → «Empezar ruta» abre el mapa en modo expedición (web: pantalla `expedition-map`; nativo: `MAP` con `mapIsExpedition`).
2. El mapa muestra las paradas y la ubicación del usuario. Al tocar una parada se muestra su relato (panel en web, `AlertDialog` en nativo).
3. A ≤5 m de una parada no resuelta se hace check-in y se abre la pantalla de pregunta (`ActiveExpedition` / `ActiveExpeditionScreen`, con `initialStopId`).
4. Al volver desde la pregunta se regresa al mapa de expedición.

## Archivos clave
- Web: `src/components/InteractiveMap.tsx`, `src/App.tsx`, `src/components/ActiveExpedition.tsx`.
- Nativo: `InteractiveMapScreen.kt`, `RouteMapView*.kt` (`onUserLocationChanged`), `App.kt`, `ActiveExpeditionScreen.kt`.

## Limitaciones / pendiente
- Ubicación real solo en Android y web; Desktop/iOS ignoran `onUserLocationChanged`.
- El GPS rara vez baja de 5 m: valorar un botón «Estoy aquí» o aviso de precisión.
- Textos del mapa de expedición internacionalizados: web (`map_*` en `src/services/i18n.ts`, es/gl/en/pt) y nativo (`InteractiveMapStrings.kt`, es/gl/en).
- En expedición el mapa muestra solo la ruta seleccionada (web y nativo).
- El editor de rutas (`RouteEditorScreen.kt`) también recibe el idioma actual para su mapa.
