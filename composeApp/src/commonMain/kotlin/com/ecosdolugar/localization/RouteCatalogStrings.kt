package com.ecosdolugar.localization

import com.ecosdolugar.model.Category

data class RouteCatalogStrings(
    val routesTitle: String,
    val guideButton: String,
    val profile: String,
    val topLabel: String,
    val savedRoutes: String,
    val showAllRoutes: String,
    val filterSaved: String,
    val heading: String,
    val routesAvailable: (count: Int) -> String,
    val clearFilters: String,
    val activeGift: String,
    val changeCharacter: String,
    val searchPlaceholder: String,
    val clearSearch: String,
    val resetFilters: String,
    val featured: String,
    val stopsSingular: String,
    val stopsPlural: String,
    val geolocatedStopsSingular: String,
    val geolocatedStopsPlural: String,
    val saveRoute: String,
    val byGuide: String,
    val guideLabel: String,
    val geolocatedStops: String,
    val viewRoute: String,
    val soundscapeTitle: String,
    val soundscapePlaying: String,
    val soundscapeEnable: String,
    val noRoutesTitle: String,
    val noRoutesDescription: String
)

fun routeCatalogStrings(language: AppLanguage): RouteCatalogStrings =
    when (language) {
        AppLanguage.SPANISH -> RouteCatalogStrings(
            routesTitle = "Rutas",
            guideButton = "Guía",
            profile = "Perfil",
            topLabel = "Rutas creadas por guías locales",
            savedRoutes = "Rutas guardadas",
            showAllRoutes = "Ver todas las rutas",
            filterSaved = "Filtrar guardadas",
            heading = "Descubre tu próxima ruta.",
            routesAvailable = { count -> if (count == 1) "1 ruta disponible." else "$count rutas disponibles." },
            clearFilters = "Limpiar filtros",
            activeGift = "Don activo:",
            changeCharacter = "Cambiar",
            searchPlaceholder = "Buscar rutas, lugares o guías...",
            clearSearch = "Limpiar",
            resetFilters = "Limpiar filtros",
            featured = "DESTACADA",
            stopsSingular = "PARADA",
            stopsPlural = "PARADAS",
            geolocatedStopsSingular = "parada geolocalizada",
            geolocatedStopsPlural = "paradas geolocalizadas",
            saveRoute = "Guardar ruta",
            byGuide = "Por",
            guideLabel = "Guía:",
            geolocatedStops = "paradas geolocalizadas",
            viewRoute = "Ver ruta",
            soundscapeTitle = "PAISAJE SONORO GALLEGO",
            soundscapePlaying = "Reproduciendo sonidos de agua y bosque",
            soundscapeEnable = "Activar ambiente natural",
            noRoutesTitle = "No hay rutas disponibles con estos criterios",
            noRoutesDescription = "Prueba a cambiar la búsqueda o la categoría."
        )
        AppLanguage.GALICIAN -> RouteCatalogStrings(
            routesTitle = "Rutas",
            guideButton = "Guía",
            profile = "Perfil",
            topLabel = "Rutas creadas por guías locales",
            savedRoutes = "Rutas gardadas",
            showAllRoutes = "Ver todas as rutas",
            filterSaved = "Filtrar gardadas",
            heading = "Descubre a túa próxima ruta.",
            routesAvailable = { count -> if (count == 1) "1 ruta dispoñible." else "$count rutas dispoñibles." },
            clearFilters = "Limpar filtros",
            activeGift = "Don activo:",
            changeCharacter = "Cambiar",
            searchPlaceholder = "Buscar rutas, lugares ou guías...",
            clearSearch = "Limpar",
            resetFilters = "Limpar filtros",
            featured = "DESTACADA",
            stopsSingular = "PARADA",
            stopsPlural = "PARADAS",
            geolocatedStopsSingular = "parada xeolocalizada",
            geolocatedStopsPlural = "paradas xeolocalizadas",
            saveRoute = "Gardar ruta",
            byGuide = "Por",
            guideLabel = "Guía:",
            geolocatedStops = "paradas xeolocalizadas",
            viewRoute = "Ver ruta",
            soundscapeTitle = "PAISAXE SONORA GALEGA",
            soundscapePlaying = "Reproducindo o rumor da auga e do bosque",
            soundscapeEnable = "Activar ambiente natural",
            noRoutesTitle = "Non hai rutas dispoñibles con estes criterios",
            noRoutesDescription = "Proba a cambiar a busca ou a categoría."
        )
        AppLanguage.ENGLISH -> RouteCatalogStrings(
            routesTitle = "Routes",
            guideButton = "Guide",
            profile = "Profile",
            topLabel = "Routes created by local guides",
            savedRoutes = "Saved routes",
            showAllRoutes = "Show all routes",
            filterSaved = "Show saved routes",
            heading = "Discover your next route.",
            routesAvailable = { count -> if (count == 1) "1 route available." else "$count routes available." },
            clearFilters = "Clear filters",
            activeGift = "Active gift:",
            changeCharacter = "Change",
            searchPlaceholder = "Search routes, places or guides...",
            clearSearch = "Clear",
            resetFilters = "Clear filters",
            featured = "FEATURED",
            stopsSingular = "STOP",
            stopsPlural = "STOPS",
            geolocatedStopsSingular = "geolocated stop",
            geolocatedStopsPlural = "geolocated stops",
            saveRoute = "Save route",
            byGuide = "By",
            guideLabel = "Guide:",
            geolocatedStops = "geolocated stops",
            viewRoute = "View route",
            soundscapeTitle = "GALICIAN SOUNDSCAPE",
            soundscapePlaying = "Playing the sounds of water and forest",
            soundscapeEnable = "Play nature sounds",
            noRoutesTitle = "No routes match these criteria",
            noRoutesDescription = "Try changing your search or category."
        )
    }

fun categoryDisplayName(category: Category, language: AppLanguage): String =
    when (language) {
        AppLanguage.SPANISH -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Leyendas"
            Category.NATURALEZA -> "Naturaleza"
            Category.GASTRONOMIA -> "Gastronomía"
            Category.ARTE -> "Arte"
            Category.TRADICIONES -> "Tradiciones"
        }
        AppLanguage.GALICIAN -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Lendas"
            Category.NATURALEZA -> "Natureza"
            Category.GASTRONOMIA -> "Gastronomía"
            Category.ARTE -> "Arte"
            Category.TRADICIONES -> "Tradicións"
        }
        AppLanguage.ENGLISH -> when (category) {
            Category.ALL -> "All"
            Category.HISTORIA -> "History"
            Category.LEYENDAS -> "Legends"
            Category.NATURALEZA -> "Nature"
            Category.GASTRONOMIA -> "Gastronomy"
            Category.ARTE -> "Art"
            Category.TRADICIONES -> "Traditions"
        }
    }
