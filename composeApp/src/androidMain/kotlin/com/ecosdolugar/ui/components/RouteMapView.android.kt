package com.ecosdolugar.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.view.MotionEvent
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ecosdolugar.localization.AppLanguage
import com.ecosdolugar.localization.interactiveMapStrings
import com.ecosdolugar.model.RouteItem
import com.ecosdolugar.model.RouteStop
import org.json.JSONArray
import org.json.JSONObject

private data class MapLocation(val latitude: Double, val longitude: Double, val accuracy: Float)

// An expedition needs fresh GPS fixes to detect arrival at a stop; plain browsing only draws a marker.
private const val EXPEDITION_UPDATE_INTERVAL_MS = 2_000L
private const val EXPEDITION_MIN_DISTANCE_METERS = 3f
private const val BROWSING_UPDATE_INTERVAL_MS = 15_000L
private const val BROWSING_MIN_DISTANCE_METERS = 20f

@Composable
actual fun RouteMapView(
    routes: List<RouteItem>,
    selectedRouteId: String?,
    selectedStopId: String?,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    modifier: Modifier,
    onCoordinateSelected: ((Double, Double) -> Unit)?,
    selectedCoordinates: Pair<Double, Double>?,
    pickerCoordinates: List<Pair<Double, Double>>,
    onUserLocationChanged: ((Double, Double) -> Unit)?,
    completedStopIds: Set<String>,
    language: AppLanguage
) {
    val context = LocalContext.current
    val strings = interactiveMapStrings(language)
    val mapStatus = remember { mutableStateOf("loading") }
    val userLocation = remember { mutableStateOf<MapLocation?>(null) }
    val permissionGranted = remember { mutableStateOf(hasLocationPermission(context)) }
    val locationUnavailable = remember { mutableStateOf(false) }
    val isExpedition = onUserLocationChanged != null
    val lifecycleOwner = LocalLifecycleOwner.current
    val isResumed = remember { mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, _ ->
            isResumed.value = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val requestLocationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        permissionGranted.value = grants.values.any { it }
        if (!permissionGranted.value) {
            locationUnavailable.value = true
            android.util.Log.i("RouteMap", "Location permission was denied; showing the default map extent")
        } else {
            locationUnavailable.value = false
        }
    }
    LaunchedEffect(Unit) {
        permissionGranted.value = hasLocationPermission(context)
        if (!permissionGranted.value) {
            requestLocationPermission.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    LaunchedEffect(userLocation.value, onUserLocationChanged) {
        userLocation.value?.let { location ->
            onUserLocationChanged?.invoke(location.latitude, location.longitude)
        }
    }

    // Updates stop while the app is in the background and resume with the last known fix.
    DisposableEffect(context, permissionGranted.value, isResumed.value, isExpedition) {
        if (!permissionGranted.value || !isResumed.value) {
            onDispose { }
        } else {
            val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val hasFinePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val providers = (
                if (hasFinePermission && isExpedition) {
                    listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                } else {
                    listOf(LocationManager.NETWORK_PROVIDER)
                }
            ).filter(manager::isProviderEnabled)
            locationUnavailable.value = providers.isEmpty()
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    userLocation.value = location.toMapLocation()
                    locationUnavailable.value = false
                }

                @Deprecated("Deprecated in Android")
                override fun onStatusChanged(providerName: String?, status: Int, extras: android.os.Bundle?) = Unit

                override fun onProviderEnabled(providerName: String) = Unit
                override fun onProviderDisabled(providerName: String) = Unit
            }
            try {
                providers.forEach { provider ->
                    manager.getLastKnownLocation(provider)?.let { userLocation.value = it.toMapLocation() }
                    manager.requestLocationUpdates(
                        provider,
                        if (isExpedition) EXPEDITION_UPDATE_INTERVAL_MS else BROWSING_UPDATE_INTERVAL_MS,
                        if (isExpedition) EXPEDITION_MIN_DISTANCE_METERS else BROWSING_MIN_DISTANCE_METERS,
                        listener,
                        Looper.getMainLooper()
                    )
                }
            } catch (error: SecurityException) {
                locationUnavailable.value = true
                android.util.Log.e("RouteMap", "Location permission was revoked while tracking the user", error)
            }
            onDispose {
                manager.removeUpdates(listener)
            }
        }
    }

    val routeData = remember(routes, completedStopIds) { encodeRoutes(routes, completedStopIds) }
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                createRouteMap(
                    viewContext,
                    onRouteSelected,
                    onStopSelected,
                    onCoordinateSelected
                ) { status -> mapStatus.value = status }
            },
            update = { webView ->
                webView.updateMap(
                    routeData,
                    selectedRouteId,
                    selectedStopId,
                    userLocation.value,
                    selectedCoordinates,
                    pickerCoordinates
                )
            }
        )

        if (onUserLocationChanged != null && locationUnavailable.value) {
            Text(
                text = if (permissionGranted.value) {
                strings.enableGps
                } else {
                    strings.grantLocation
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp, start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xEE173626))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (mapStatus.value != "ready") {
            val message = when (mapStatus.value) {
                "loading" -> strings.loadingMap
                "leaflet-error" -> strings.mapStartError
                "tiles-error" -> strings.tilesError
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

private fun Location.toMapLocation() = MapLocation(latitude, longitude, accuracy)

@SuppressLint("SetJavaScriptEnabled")
private fun createRouteMap(
    context: Context,
    onRouteSelected: (String) -> Unit,
    onStopSelected: (String) -> Unit,
    onCoordinateSelected: ((Double, Double) -> Unit)?,
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
            fun onCoordinateSelected(latitude: Double, longitude: Double) {
                post { onCoordinateSelected?.invoke(latitude, longitude) }
            }

            @JavascriptInterface
            fun onMapStatus(status: String) {
                post { updateMapStatus(status) }
            }
        },
        "AndroidMap"
    )
    loadDataWithBaseURL("file:///android_asset/", routeMapHtml(), "text/html", "UTF-8", null)
}

private class RouteMapWebView(context: Context) : WebView(context) {
    private var pageLoaded = false
    private var routeData = "[]"
    private var selectedRouteId: String? = null
    private var selectedStopId: String? = null
    private var userLocation: MapLocation? = null
    private var selectedCoordinates: Pair<Double, Double>? = null
    private var pickerCoordinates: List<Pair<Double, Double>> = emptyList()

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN ||
            event.actionMasked == MotionEvent.ACTION_MOVE
        ) {
            parent?.requestDisallowInterceptTouchEvent(true)
        }
        val handled = super.dispatchTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP ||
            event.actionMasked == MotionEvent.ACTION_CANCEL
        ) {
            parent?.requestDisallowInterceptTouchEvent(false)
        }
        return handled
    }

    fun updateMap(
        routesJson: String,
        routeId: String?,
        stopId: String?,
        location: MapLocation?,
        pickedCoordinates: Pair<Double, Double>?,
        pickerPoints: List<Pair<Double, Double>>
    ) {
        val changed = routeData != routesJson || selectedRouteId != routeId ||
            selectedStopId != stopId || userLocation != location ||
            selectedCoordinates != pickedCoordinates || pickerCoordinates != pickerPoints
        routeData = routesJson
        selectedRouteId = routeId
        selectedStopId = stopId
        userLocation = location
        selectedCoordinates = pickedCoordinates
        pickerCoordinates = pickerPoints
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
        val points = JSONArray().apply {
            pickerCoordinates.forEach { (latitude, longitude) ->
                put(JSONArray().put(latitude).put(longitude))
            }
        }
        val picked = selectedCoordinates
        evaluateJavascript(
            "window.setPickerData && window.setPickerData(${picked?.first ?: "null"}, ${picked?.second ?: "null"}, ${JSONObject.quote(points.toString())});",
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

private fun encodeRoutes(routes: List<RouteItem>, completedStopIds: Set<String>): String {
    val encoded = JSONArray()
    routes.forEach { route ->
        val stops = JSONArray()
        route.stops
            .filter { it.latitude.isFinite() && it.longitude.isFinite() }
            .sortedBy { it.order }
            .forEach { stop -> stops.put(stop.toJson(completed = stop.id in completedStopIds)) }
        encoded.put(
            JSONObject()
                .put("id", route.id)
                .put("title", route.title)
                .put("stops", stops)
        )
    }
    return encoded.toString()
}

private fun RouteStop.toJson(completed: Boolean) = JSONObject()
    .put("id", id)
    .put("order", order)
    .put("title", title)
    .put("lat", latitude)
    .put("lng", longitude)
    .put("completed", completed)

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
        const pickerLayer = L.layerGroup().addTo(map);
        let allRoutes = [];
        let activeRouteId = '';
        let selectedStopId = '';
        let userMarker = null;
        let hasUserMovedMap = false;
        let settingUserLocation = false;
        map.on('click', event => {
          if (window.AndroidMap) window.AndroidMap.onCoordinateSelected(event.latlng.lat, event.latlng.lng);
        });
        map.on('dragstart', () => { hasUserMovedMap = true; });
        map.on('zoomstart', () => { if (!settingUserLocation) hasUserMovedMap = true; });

        function mapIcon(file, size) {
          return L.icon({
            iconUrl: 'map/' + file,
            iconSize: [size, size],
            iconAnchor: [size / 2, size / 2]
          });
        }
        function startIcon() {
          return mapIcon('map_inicio.png', 32);
        }
        function stopIcon(stop, index, total) {
          const size = stop.id === selectedStopId ? 48 : 40;
          if (stop.completed) return mapIcon('map_completada.png', size);
          if (index === total - 1) return mapIcon('map_final.png', size);
          const number = String(Math.min(index + 1, 15)).padStart(2, '0');
          return mapIcon('parada_' + number + '.png', size);
        }
        function redrawVisibleStarts() {
          routeStartLayer.clearLayers();
          const bounds = map.getBounds();
          allRoutes.forEach(route => {
            const start = route.stops[0];
            if (!start || route.id === activeRouteId || !bounds.contains([start.lat, start.lng])) return;
            const marker = L.marker([start.lat, start.lng], { icon: startIcon() }).addTo(routeStartLayer);
            const label = document.createElement('div');
            label.textContent = route.title;
            marker.bindPopup(label);
            marker.on('click', () => {
              if (window.AndroidMap) window.AndroidMap.onRouteSelected(route.id);
            });
          });
        }
        function redrawSelectedRoute(fit) {
          selectedRouteLayer.clearLayers();
          const route = allRoutes.find(item => item.id === activeRouteId);
          if (!route || route.stops.length === 0) return;
          const points = route.stops.map(stop => [stop.lat, stop.lng]);
          route.stops.forEach((stop, index) => {
            const marker = L.marker([stop.lat, stop.lng], { icon: stopIcon(stop, index, route.stops.length) }).addTo(selectedRouteLayer);
            const label = document.createElement('div');
            label.textContent = stop.title;
            marker.bindPopup(label);
            marker.on('click', () => {
              selectedStopId = stop.id;
              redrawSelectedRoute(false);
              if (window.AndroidMap) window.AndroidMap.onStopSelected(stop.id);
            });
          });
          const bounds = L.latLngBounds(points);
          if (fit && bounds.isValid() && !hasUserMovedMap) {
            settingUserLocation = true;
            map.fitBounds(bounds, { padding: [36, 36], maxZoom: 16 });
            settingUserLocation = false;
          }
        }
        window.setMapData = function(routesJson, routeId, stopId) {
          const previousRouteId = activeRouteId;
          allRoutes = JSON.parse(routesJson);
          activeRouteId = routeId;
          selectedStopId = stopId;
          const routeChanged = previousRouteId !== activeRouteId;
          if (routeChanged) hasUserMovedMap = false;
          redrawSelectedRoute(routeChanged);
          redrawVisibleStarts();
          if (routeChanged && !activeRouteId && userMarker && !hasUserMovedMap) {
            settingUserLocation = true;
            map.setView(userMarker.getLatLng(), 14);
            settingUserLocation = false;
          }
        };
        window.setUserLocation = function(latitude, longitude) {
          const position = [latitude, longitude];
          if (!userMarker) {
            userMarker = L.marker(position, { icon: mapIcon('ubicacion_actual', 32) }).addTo(map);
            userMarker.bindPopup('Tu ubicación');
          } else {
            userMarker.setLatLng(position);
          }
          if (!hasUserMovedMap && !activeRouteId) {
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
        window.setPickerData = function(latitude, longitude, pointsJson) {
          pickerLayer.clearLayers();
          const points = JSON.parse(pointsJson);
          points.forEach((point, index) => {
            L.circleMarker(point, {
              radius: 9, color: '#fff', weight: 2, fillColor: '#47654e', fillOpacity: 1
            }).bindTooltip(String(index + 1)).addTo(pickerLayer);
          });
          if (latitude !== null && longitude !== null) {
            L.marker([latitude, longitude]).addTo(pickerLayer);
          }
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
