package com.ecosdolugar.localization

data class InteractiveMapStrings(
    val back: String,
    val close: String,
    val waitingLocation: String,
    val locationReady: String,
    val approachStop: String,
    val noRoutes: String,
    val viewDetailOf: String,
    val stopsLabel: String,
    val loadingMap: String,
    val mapStartError: String,
    val tilesError: String,
    val enableGps: String,
    val grantLocation: String
)

fun interactiveMapStrings(language: AppLanguage): InteractiveMapStrings =
    when (language) {
        AppLanguage.SPANISH -> InteractiveMapStrings(
            back = "Volver",
            close = "Cerrar",
            waitingLocation = "Esperando ubicación…",
            locationReady = "Acércate a menos de 10 m de una parada para abrir su reto.",
            approachStop = "Acércate a una parada",
            noRoutes = "No hay rutas disponibles para mostrar.",
            viewDetailOf = "Ver detalle de",
            stopsLabel = "paradas",
            loadingMap = "Cargando mapa…",
            mapStartError = "No se pudo iniciar el mapa. Comprueba la conexión a internet.",
            tilesError = "No se pudieron cargar las calles. Comprueba la conexión a internet.",
            enableGps = "Activa el GPS para mostrar tu ubicación y abrir los retos al llegar a las paradas.",
            grantLocation = "Permite el acceso a la ubicación para activar los retos al llegar a las paradas."
        )
        AppLanguage.GALICIAN -> InteractiveMapStrings(
            back = "Volver",
            close = "Pechar",
            waitingLocation = "Agardando ubicación…",
            locationReady = "Achégate a menos de 10 m dunha parada para abrir o seu reto.",
            approachStop = "Achégate a unha parada",
            noRoutes = "Non hai rutas dispoñibles para mostrar.",
            viewDetailOf = "Ver detalle de",
            stopsLabel = "paradas",
            loadingMap = "Cargando mapa…",
            mapStartError = "Non se puido iniciar o mapa. Comproba a conexión a internet.",
            tilesError = "Non se puideron cargar as rúas. Comproba a conexión a internet.",
            enableGps = "Activa o GPS para mostrar a túa ubicación e abrir os retos ao chegar ás paradas.",
            grantLocation = "Permite o acceso á ubicación para activar os retos ao chegar ás paradas."
        )
        AppLanguage.ENGLISH -> InteractiveMapStrings(
            back = "Back",
            close = "Close",
            waitingLocation = "Waiting for location…",
            locationReady = "Get within 10 m of a stop to open its challenge.",
            approachStop = "Get close to a stop",
            noRoutes = "No routes available to show.",
            viewDetailOf = "View details of",
            stopsLabel = "stops",
            loadingMap = "Loading map…",
            mapStartError = "The map could not start. Check your internet connection.",
            tilesError = "The streets could not be loaded. Check your internet connection.",
            enableGps = "Turn on GPS to show your location and open the challenges when you reach the stops.",
            grantLocation = "Allow location access to activate the challenges when you reach the stops."
        )
    }
