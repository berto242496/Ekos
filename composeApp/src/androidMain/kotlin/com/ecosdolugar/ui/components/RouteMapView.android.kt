package com.ecosdolugar.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import org.json.JSONArray
import org.json.JSONObject

private data class MapLocation(val latitude: Double, val longitude: Double)

@Composable
actual fun RouteMapView(
    routes: List<RouteItem>,
    selectedRouteId: String?,
    selectedStopId: String?,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val mapStatus = remember { mutableStateOf("loading") }
    val userLocation = remember { mutableStateOf<MapLocation?>(null) }
    val requestLocationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) {
            fetchUserLocation(context) { location -> userLocation.value = location }
        } else {
            android.util.Log.i("RouteMap", "Location permission was denied; showing the default map extent")
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission(context)) {
            fetchUserLocation(context) { location -> userLocation.value = location }
        } else {
            requestLocationPermission.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val routeData = remember(routes) { encodeRoutes(routes) }
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                createRouteMap(
                    viewContext,
                    onRouteSelected,
                    onStopSelected
                ) { status -> mapStatus.value = status }
            },
            update = { webView ->
                webView.updateMap(routeData, selectedRouteId, selectedStopId, userLocation.value)
            }
        )

        if (mapStatus.value != "ready") {
            val message = when (mapStatus.value) {
                "loading" -> "Cargando mapa…"
                "leaflet-error" -> "No se pudo iniciar el mapa. Comprueba la conexión a internet."
                "tiles-error" -> "No se pudieron cargar las calles. Comprueba la conexión a internet."
                else -> null
            }
            if (message != null) {
                Text(
                    text = message,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xEE173626))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
private fun fetchUserLocation(context: Context, onLocation: (MapLocation) -> Unit) {
    if (!hasLocationPermission(context)) return
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val hasFinePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val permittedProviders = if (hasFinePermission) {
        listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
    } else {
        listOf(LocationManager.NETWORK_PROVIDER)
    }
    val providers = permittedProviders
        .filter(manager::isProviderEnabled)

    if (providers.isEmpty()) {
        android.util.Log.w("RouteMap", "No location provider is enabled")
        return
    }

    try {
        providers.forEach { provider ->
            manager.getLastKnownLocation(provider)?.let { location ->
                onLocation(location.toMapLocation())
            }
        }

        providers.forEach { provider ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                manager.getCurrentLocation(provider, null, context.mainExecutor) { location ->
                    if (location != null) onLocation(location.toMapLocation())
                }
            } else {
                @Suppress("DEPRECATION")
                manager.requestSingleUpdate(
                    provider,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            onLocation(location.toMapLocation())
                        }

                        @Deprecated("Deprecated in Android")
                        override fun onStatusChanged(providerName: String?, status: Int, extras: android.os.Bundle?) = Unit

                        override fun onProviderEnabled(providerName: String) = Unit
                        override fun onProviderDisabled(providerName: String) = Unit
                    },
                    Looper.getMainLooper()
                )
            }
        }
    } catch (error: SecurityException) {
        android.util.Log.e("RouteMap", "Location permission was revoked while requesting the current position", error)
    }
}

private fun Location.toMapLocation() = MapLocation(latitude, longitude)

@SuppressLint("SetJavaScriptEnabled")
private fun createRouteMap(
    context: Context,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    updateMapStatus: (String) -> Unit
) = RouteMapWebView(context).apply {
    setBackgroundColor(android.graphics.Color.rgb(221, 232, 226))
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = false
    settings.loadsImagesAutomatically = true
    webViewClient = object : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: android.webkit.WebResourceRequest?,
            error: android.webkit.WebResourceError?
        ) {
            if (request?.isForMainFrame == true) {
                post { updateMapStatus("leaflet-error") }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            (view as? RouteMapWebView)?.onMapPageLoaded()
        }
    }
    webChromeClient = object : WebChromeClient() {}
    addJavascriptInterface(
        object {
            @JavascriptInterface
            fun onRouteSelected(routeId: String) {
                post { onRouteSelected(routeId) }
            }

            @JavascriptInterface
            fun onStopSelected(stopId: String) {
                post { onStopSelected(stopId) }
            }

            @JavascriptInterface
            fun onMapStatus(status: String) {
                post { updateMapStatus(status) }
            }
        },
        "AndroidMap"
    )
    loadDataWithBaseURL("https://www.openstreetmap.org/", routeMapHtml(), "text/html", "UTF-8", null)
}

private class RouteMapWebView(context: Context) : WebView(context) {
    private var pageLoaded = false
    private var routeData = "[]"
    private var selectedRouteId: String? = null
    private var selectedStopId: String? = null
    private var userLocation: MapLocation? = null

    fun updateMap(
        routesJson: String,
        routeId: String?,
        stopId: String?,
        location: MapLocation?
    ) {
        val changed = routeData != routesJson || selectedRouteId != routeId ||
            selectedStopId != stopId || userLocation != location
        routeData = routesJson
        selectedRouteId = routeId
        selectedStopId = stopId
        userLocation = location
        if (changed && pageLoaded) applyMapState()
    }

    fun onMapPageLoaded() {
        pageLoaded = true
        applyMapState()
    }

    private fun applyMapState() {
        val height = (height / resources.displayMetrics.density).toInt()
        evaluateJavascript("window.setMapHeight && window.setMapHeight($height);", null)
        evaluateJavascript(
            "window.setMapData && window.setMapData(${JSONObject.quote(routeData)}, ${JSONObject.quote(selectedRouteId.orEmpty())}, ${JSONObject.quote(selectedStopId.orEmpty())});",
            null
        )
        userLocation?.let { location ->
            evaluateJavascript(
                "window.setUserLocation && window.setUserLocation(${location.latitude}, ${location.longitude});",
                null
            )
        }
    }
}

private fun encodeRoutes(routes: List<RouteItem>): String {
    val encoded = JSONArray()
    routes.forEach { route ->
        val stops = JSONArray()
        route.stops
            .filter { it.latitude.isFinite() && it.longitude.isFinite() }
            .sortedBy { it.order }
            .forEach { stop -> stops.put(stop.toJson()) }
        encoded.put(
            JSONObject()
                .put("id", route.id)
                .put("title", route.title)
                .put("stops", stops)
        )
    }
    return encoded.toString()
}

private fun RouteStop.toJson() = JSONObject()
    .put("id", id)
    .put("order", order)
    .put("title", title)
    .put("lat", latitude)
    .put("lng", longitude)

private fun routeMapHtml(): String = """
    <!doctype html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
            onerror="window.AndroidMap.onMapStatus('leaflet-error')">
      <style>
        html, body { width: 100%; height: 100%; min-height: 100vh; margin: 0; }
        #map { position: absolute; inset: 0; width: 100vw; height: 100vh; background: #dde8e2; }
        .route-pin {
          display: flex; align-items: center; justify-content: center;
          width: 30px; height: 30px; border: 2px solid white; border-radius: 50%;
          background: #17452f; color: white; font: 700 13px sans-serif;
          box-shadow: 0 2px 7px #0006;
        }
        .route-pin.selected { width: 36px; height: 36px; background: #d49b45; color: #17291e; }
        .route-start {
          display: flex; align-items: center; justify-content: center;
          width: 32px; height: 32px; border: 2px solid white; border-radius: 50%;
          background: #17452f; color: white; font: 700 12px sans-serif;
          box-shadow: 0 2px 7px #0006;
        }
        .user-location { border: 3px solid white; border-radius: 50%; background: #2878d0; box-shadow: 0 1px 8px #0008; }
        .leaflet-popup-content { font: 14px sans-serif; }
        .leaflet-control-attribution { max-width: 80%; font-size: 9px; line-height: 1.25; }
      </style>
    </head>
    <body>
      <div id="map"></div>
      <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
              onerror="window.AndroidMap.onMapStatus('leaflet-error')"></script>
      <script>
        window.onerror = function(message, source, line, column) {
          if (window.AndroidMap) window.AndroidMap.onMapStatus('leaflet-error');
          console.error(message + ' (' + source + ':' + line + ':' + column + ')');
        };
        const map = L.map('map', {
          zoomControl: true,
          scrollWheelZoom: true,
          attributionControl: false
        }).setView([42.65, -8.2], 8);
        L.control.attribution({ position: 'topright' }).addTo(map);
        const tiles = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
          maxZoom: 19,
          attribution: 'Tiles &copy; Esri — Source: Esri, DeLorme, NAVTEQ, TomTom, Intermap, iPC, USGS, FAO, NPS, NRCAN, GeoBase, Kadaster NL, Ordnance Survey, Esri Japan, METI, Esri China (Hong Kong), and the GIS User Community'
        }).addTo(map);
        let loadedTiles = 0;
        tiles.on('tileload', () => {
          loadedTiles += 1;
          if (map.getSize().y > 0 && window.AndroidMap) window.AndroidMap.onMapStatus('ready');
        });
        tiles.on('tileerror', () => {
          if (loadedTiles === 0 && window.AndroidMap) window.AndroidMap.onMapStatus('tiles-error');
        });

        const routeStartLayer = L.layerGroup().addTo(map);
        const selectedRouteLayer = L.layerGroup().addTo(map);
        let allRoutes = [];
        let activeRouteId = '';
        let selectedStopId = '';
        let userMarker = null;
        let hasUserMovedMap = false;
        let settingUserLocation = false;
        map.on('dragstart', () => { hasUserMovedMap = true; });
        map.on('zoomstart', () => { if (!settingUserLocation) hasUserMovedMap = true; });

        function startIcon(order) {
          return L.divIcon({
            className: '',
            html: '<span class="route-start">' + order + '</span>',
            iconSize: [36, 36],
            iconAnchor: [18, 18]
          });
        }
        function stopIcon(stop) {
          return L.divIcon({
            className: '',
            html: '<span class="route-pin' + (stop.id === selectedStopId ? ' selected' : '') + '">' + stop.order + '</span>',
            iconSize: [40, 40],
            iconAnchor: [20, 20]
          });
        }
        function redrawVisibleStarts() {
          routeStartLayer.clearLayers();
          const bounds = map.getBounds();
          allRoutes.forEach(route => {
            const start = route.stops[0];
            if (!start || route.id === activeRouteId || !bounds.contains([start.lat, start.lng])) return;
            const marker = L.marker([start.lat, start.lng], { icon: startIcon(start.order) }).addTo(routeStartLayer);
            const label = document.createElement('div');
            label.textContent = route.title;
            marker.bindPopup(label);
            marker.on('click', () => {
              if (window.AndroidMap) window.AndroidMap.onRouteSelected(route.id);
            });
          });
        }
        function redrawSelectedRoute() {
          selectedRouteLayer.clearLayers();
          const route = allRoutes.find(item => item.id === activeRouteId);
          if (!route || route.stops.length === 0) return;
          const points = route.stops.map(stop => [stop.lat, stop.lng]);
          if (points.length > 1) {
            L.polyline(points, {
              color: '#d49b45', weight: 5, opacity: 0.9, lineCap: 'round', lineJoin: 'round'
            }).addTo(selectedRouteLayer);
          }
          route.stops.forEach(stop => {
            const marker = L.marker([stop.lat, stop.lng], { icon: stopIcon(stop) }).addTo(selectedRouteLayer);
            const label = document.createElement('div');
            label.textContent = stop.title;
            marker.bindPopup(label);
            marker.on('click', () => {
              selectedStopId = stop.id;
              redrawSelectedRoute();
              if (window.AndroidMap) window.AndroidMap.onStopSelected(stop.id);
            });
          });
          const bounds = L.latLngBounds(points);
          if (bounds.isValid()) map.fitBounds(bounds, { padding: [36, 36], maxZoom: 16 });
        }
        window.setMapData = function(routesJson, routeId, stopId) {
          const previousRouteId = activeRouteId;
          allRoutes = JSON.parse(routesJson);
          activeRouteId = routeId;
          selectedStopId = stopId;
          redrawSelectedRoute();
          redrawVisibleStarts();
          if (previousRouteId !== activeRouteId && !activeRouteId && userMarker && !hasUserMovedMap) {
            settingUserLocation = true;
            map.setView(userMarker.getLatLng(), 14);
            settingUserLocation = false;
          }
        };
        window.setUserLocation = function(latitude, longitude) {
          const position = [latitude, longitude];
          if (!userMarker) {
            userMarker = L.circleMarker(position, {
              radius: 9, color: '#fff', weight: 3, fillColor: '#2878d0', fillOpacity: 1
            }).addTo(map);
            userMarker.bindPopup('Tu ubicación');
          } else {
            userMarker.setLatLng(position);
          }
          if (!hasUserMovedMap) {
            settingUserLocation = true;
            map.setView(position, 14);
            settingUserLocation = false;
          }
        };
        window.setMapHeight = function(height) {
          if (height <= 0) return;
          document.getElementById('map').style.height = height + 'px';
          map.invalidateSize({ pan: false });
          redrawVisibleStarts();
        };
        map.on('moveend', redrawVisibleStarts);
        setTimeout(() => {
          map.invalidateSize();
          if (loadedTiles === 0 && window.AndroidMap) window.AndroidMap.onMapStatus('tiles-error');
        }, 5000);
      </script>
    </body>
    </html>
""".trimIndent()
