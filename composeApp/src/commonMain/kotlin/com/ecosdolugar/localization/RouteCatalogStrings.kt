package com.ecosdolugar.localization

import com.ecosdolugar.model.Category

data class RouteCatalogStrings(
    val routesTitle: String,
    val guideButton: String,
    val profile: String,
    val regionCoverage: String,
    val savedRoutes: String,
    val showAllRoutes: String,
    val filterSaved: String,
    val heading: String,
    val experienceSingular: String,
    val experiencePlural: String,
    val catalogSuffix: String,
    val countOf: String,
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
            regionCoverage = "TODA GALICIA · LAS 4 PROVINCIAS",
            savedRoutes = "Rutas guardadas",
            showAllRoutes = "Ver todas las rutas",
            filterSaved = "Filtrar guardadas",
            heading = "Elige tu ruta mágica",
            experienceSingular = "experiencia",
            experiencePlural = "experiencias",
            catalogSuffix = "en catálogo",
            countOf = "de",
            clearFilters = "Limpiar filtros",
            activeGift = "Don activo:",
            changeCharacter = "Cambiar",
            searchPlaceholder = "Buscar por título, pueblo, zona o guía...",
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
            regionCoverage = "TODA GALICIA · AS 4 PROVINCIAS",
            savedRoutes = "Rutas gardadas",
            showAllRoutes = "Ver todas as rutas",
            filterSaved = "Filtrar gardadas",
            heading = "Elixe a túa ruta máxica",
            experienceSingular = "experiencia",
            experiencePlural = "experiencias",
            catalogSuffix = "no catálogo",
            countOf = "de",
            clearFilters = "Limpar filtros",
            activeGift = "Don activo:",
            changeCharacter = "Cambiar",
            searchPlaceholder = "Buscar por título, vila, zona ou guía...",
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
            regionCoverage = "ALL GALICIA · ALL 4 PROVINCES",
            savedRoutes = "Saved routes",
            showAllRoutes = "Show all routes",
            filterSaved = "Show saved routes",
            heading = "Choose your magical route",
            experienceSingular = "experience",
            experiencePlural = "experiences",
            catalogSuffix = "in the catalogue",
            countOf = "of",
            clearFilters = "Clear filters",
            activeGift = "Active gift:",
            changeCharacter = "Change",
            searchPlaceholder = "Search by title, town, area or guide...",
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
            Category.FAMILIAS -> "Familias"
            Category.PATRIMONIO -> "Patrimonio"
            Category.NATURALEZA -> "Naturaleza"
        }
        AppLanguage.GALICIAN -> when (category) {
            Category.ALL -> "Todas"
            Category.HISTORIA -> "Historia"
            Category.LEYENDAS -> "Lendas"
            Category.FAMILIAS -> "Familias"
            Category.PATRIMONIO -> "Patrimonio"
            Category.NATURALEZA -> "Natureza"
        }
        AppLanguage.ENGLISH -> when (category) {
            Category.ALL -> "All"
            Category.HISTORIA -> "History"
            Category.LEYENDAS -> "Legends"
            Category.FAMILIAS -> "Families"
            Category.PATRIMONIO -> "Heritage"
            Category.NATURALEZA -> "Nature"
        }
    }
