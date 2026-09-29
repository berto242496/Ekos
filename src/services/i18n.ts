import { useState, useEffect } from 'react';
import { AppLanguage } from '../types';

export interface LanguageOption {
  code: AppLanguage;
  name: string;
  flag: string;
  shortLabel: string;
}

export const SUPPORTED_LANGUAGES: LanguageOption[] = [
  { code: 'es', name: 'Español', flag: '🇪🇸', shortLabel: 'ES' },
  { code: 'gl', name: 'Galego', flag: '🏴', shortLabel: 'GL' },
  { code: 'en', name: 'English', flag: '🇬🇧', shortLabel: 'EN' },
  { code: 'pt', name: 'Português', flag: '🇵🇹', shortLabel: 'PT' },
];

const STORAGE_KEY = 'ecos_app_language';

const translations: Record<AppLanguage, Record<string, string>> = {
  es: {
    // Top Nav & Common
    app_title: 'Ecos del Lugar',
    app_subtitle: 'Rutas gamificadas y patrimonio vivo de Galicia',
    nav_routes: 'Rutas',
    nav_map: 'Mapa',
    nav_my_adventure: 'Mi Aventura',
    nav_guide_panel: 'Panel de Guía',
    nav_visitor_mode: 'Ver como visitante',
    nav_profile: 'Mi Perfil',
    back: 'Volver',
    cancel: 'Cancelar',
    save: 'Guardar',
    close: 'Cerrar',
    share: 'Compartir',
    active: 'Activo',

    // Language Selector
    lang_select: 'Idioma',
    lang_es: 'Español',
    lang_gl: 'Galego',
    lang_en: 'English',
    lang_pt: 'Português',

    // Catalog & Filters
    catalog_title: 'Explora Galicia con Guías Locales',
    catalog_subtitle: 'Elige tu ruta, resuelve enigmas sobre el terreno y descubre la memoria viva.',
    filter_all: 'Todas',
    filter_history: 'Historia',
    filter_legends: 'Leyendas',
    filter_families: 'Familias',
    filter_heritage: 'Patrimonio',
    filter_nature: 'Naturaleza',
    search_placeholder: 'Buscar rutas, lugares o misterios...',
    duration: 'Duración',
    difficulty: 'Dificultad',
    difficulty_easy: 'Fácil',
    difficulty_medium: 'Media',
    difficulty_hard: 'Exigente',
    free: 'Gratis',
    stops: 'paradas',

    // Route Detail & Expedition
    btn_start_route: 'Empezar ruta',
    btn_unlock_route: 'Desbloquear ruta',
    btn_view_map: 'Ver mapa',
    companion_active: 'Compañero Activo',
    stops_itinerary_title: 'Paradas y Retos del Itinerario',
    what_you_will_discover: 'Qué descubrirás',
    privacy_note: 'La ubicación se usará solo durante la experiencia para activar relatos acústicos en el punto exacto.',
    next_stop: 'Siguiente parada',
    prev_stop: 'Parada anterior',
    listen_audio: 'Escuchar audioguía',
    stop_audio: 'Pausar audioguía',
    companion_advice: 'Consejo del compañero',
    use_don: 'Usar don especial',

    // Guide Panel
    guide_panel_title: 'Panel de Creador y Cobros',
    guide_routes_tab: 'Mis Rutas',
    guide_payouts_tab: 'Cobros y Balance',
    guide_sales_tab: 'Ventas y Descargas',
    guide_create_route_btn: 'Crear nueva ruta',
    guide_active: 'Guía activo:',
    guide_balance: 'Balance disponible',
    guide_request_payout: 'Solicitar cobro',

    // Route Creator Template (Spanish by default)
    creator_badge: 'Plantilla de Creación de Rutas',
    creator_title: 'Crea tu Ruta de Autor',
    creator_subtitle: 'Diseña paso a paso tu itinerario: pon el sitio, carga tu imagen, elige el juego que hacer y qué voz reproducirá ese texto o audio.',
    creator_choose_template: 'Elige una plantilla de partida (o personaliza la tuya):',
    creator_load_template: 'Cargar plantilla',
    creator_template_active: 'Plantilla activa',

    // Step 1: Place & Site
    creator_step1_badge: 'Paso 1',
    creator_step1_title: 'El Sitio y Datos de la Ruta',
    creator_step1_desc: 'Indica el nombre, el sitio geográfico y la descripción cultural del recorrido.',
    creator_route_name: 'Nombre de la ruta *',
    creator_route_name_ph: 'Ej. Leyendas del Miño y Piedras Sagradas de la Frontera...',
    creator_site_location: 'El Sitio / Enclave principal *',
    creator_site_location_ph: 'Ej. Tui / Valença do Minho, Costa da Morte, Ribeira Sacra...',
    creator_quick_sites: 'Sugerencias rápidas de sitios con 1 clic:',
    creator_desc_label: 'Texto escrito de introducción',
    creator_desc_ph: 'Explica a los viajeros qué misterios descubrirán, qué hechos históricos conocerán y qué hace único a este itinerario...',
    creator_category: 'Categoría',
    creator_duration_ph: '1h 30 min',
    creator_price_label: 'Precio del itinerario',

    // Step 2: Image
    creator_step2_badge: 'Paso 2',
    creator_step2_title: 'Imagen Principal de la Ruta',
    creator_step2_desc: 'Sube tu fotografía, pega un enlace o elige una imagen precurada de Galicia.',
    creator_img_gallery: 'Galería de Galicia',
    creator_img_upload: 'Subir archivo',
    creator_img_url: 'Enlace URL',
    creator_img_preview: 'Vista previa',
    creator_img_drag: 'Arrastra o selecciona una imagen de tu dispositivo',
    creator_img_formats: 'Formatos JPG, PNG o WebP. Se muestra al instante.',
    creator_img_btn: 'Seleccionar foto',
    creator_img_url_ph: 'https://...',
    creator_img_apply: 'Aplicar',

    // Step 3: Text, Audio & Voice
    creator_step3_badge: 'Paso 3',
    creator_step3_title: 'Locución: ¿Qué voz va a reproducir ese texto o audio?',
    creator_step3_desc: 'Decide si la ruta tendrá narración oral y escoge la voz que dará vida a los relatos con previsualización en directo.',
    creator_audio_mode: 'Modalidad de reproducción',
    creator_audio_tts_title: 'Texto escrito + Audioguía sintetizada',
    creator_audio_tts_desc: 'El texto se leerá en voz alta con la voz elegida por el guía.',
    creator_audio_rec_title: 'Texto escrito + Grabación propia',
    creator_audio_rec_desc: 'Graba tu propia voz con el micrófono del navegador.',
    creator_audio_written_title: 'Solo lectura escrita',
    creator_audio_written_desc: 'Lectura silenciosa para el viajero sin audio automático.',
    creator_voice_catalog: 'Catálogo de Voces de Narración',
    creator_voice_catalog_desc: 'Pulsa en "Probar voz" para escuchar una muestra en vivo antes de elegir:',
    creator_voice_test: 'Probar voz',
    creator_voice_stop: 'Detener voz',

    // Step 4: Stops & Games
    creator_step4_badge: 'Paso 4',
    creator_step4_title: 'Paradas y Juegos',
    creator_step4_desc: 'Añade cada punto del mapa con su sitio, imagen, historia y juego interactivo.',
    creator_add_stop_btn: 'Añadir nueva parada',
    creator_stop_name: 'Nombre de la parada *',
    creator_stop_name_ph: 'Ej. Mirador de la Barca, Baluarte de Santa Lucía...',
    creator_stop_site: 'Sitio / Enclave concreto',
    creator_stop_site_ph: 'Ej. Punta norte, Muralla vieja, Ribera...',
    creator_stop_img: 'Imagen de la Parada',
    creator_stop_story: 'Texto escrito: Relato histórico o leyenda de la parada',
    creator_stop_story_ph: 'Relato cultural que el viajero leerá al llegar a este punto...',
    creator_stop_spoken: 'Guión de locución oral (opcional si difiere del texto escrito)',
    creator_stop_voice: '¿Qué voz reproducirá el audio?',
    creator_stop_voice_test: 'Escuchar narración de esta parada',
    creator_game_selection: '¿Qué juego hacer en esta parada? (Opciones fáciles de programar)',
    creator_game_selection_desc: 'Escoge entre acertijo con opciones, cifrado de letras, puzle de símbolos rúnicos o pregunta de observación:',
    creator_game_riddle: '1. Acertijo con Opciones',
    creator_game_cipher: '2. Cifrado de Letras',
    creator_game_rune: '3. Puzle de Runas',
    creator_game_observation: '4. Pregunta y Pista',
    creator_save_stop: 'Guardar parada',
    creator_update_stop: 'Actualizar parada',

    // Bottom Actions
    creator_bottom_back: 'Volver al panel sin guardar',
    creator_bottom_draft: 'Guardar borrador',
    creator_bottom_publish: 'Publicar ruta ahora',
  },

  gl: {
    // Top Nav & Common
    app_title: 'Ecos do Lugar',
    app_subtitle: 'Rutas gamificadas e patrimonio vivo de Galicia',
    nav_routes: 'Rutas',
    nav_map: 'Mapa',
    nav_my_adventure: 'A miña Aventura',
    nav_guide_panel: 'Panel de Guía',
    nav_visitor_mode: 'Ver como visitante',
    nav_profile: 'O meu Perfil',
    back: 'Volver',
    cancel: 'Cancelar',
    save: 'Gardar',
    close: 'Pechar',
    share: 'Compartir',
    active: 'Activo',

    // Language Selector
    lang_select: 'Idioma',
    lang_es: 'Español',
    lang_gl: 'Galego',
    lang_en: 'English',
    lang_pt: 'Português',

    // Catalog & Filters
    catalog_title: 'Explora Galicia con Guías Locais',
    catalog_subtitle: 'Elixe o teu roteiro, resolve enigmas sobre o terreo e descobre a memoria viva.',
    filter_all: 'Todas',
    filter_history: 'Historia',
    filter_legends: 'Lendas',
    filter_families: 'Familias',
    filter_heritage: 'Patrimonio',
    filter_nature: 'Natureza',
    search_placeholder: 'Buscar rutas, sitios ou misterios...',
    duration: 'Duración',
    difficulty: 'Dificultade',
    difficulty_easy: 'Fácil',
    difficulty_medium: 'Media',
    difficulty_hard: 'Exixente',
    free: 'Gratis',
    stops: 'paradas',

    // Route Detail & Expedition
    btn_start_route: 'Empezar ruta',
    btn_unlock_route: 'Desbloquear ruta',
    btn_view_map: 'Ver mapa',
    companion_active: 'Compañeiro Activo',
    stops_itinerary_title: 'Paradas e Retos do Itinerario',
    what_you_will_discover: 'Que descubrirás',
    privacy_note: 'A ubicación empregarase unicamente durante a experiencia para activar relatos acústicos no punto exacto.',
    next_stop: 'Seguinte parada',
    prev_stop: 'Parada anterior',
    listen_audio: 'Escoitar audioguía',
    stop_audio: 'Pausar audioguía',
    companion_advice: 'Consello do compañeiro',
    use_don: 'Usar don especial',

    // Guide Panel
    guide_panel_title: 'Panel de Creador e Cobros',
    guide_routes_tab: 'As miñas Rutas',
    guide_payouts_tab: 'Cobros e Balance',
    guide_sales_tab: 'Vendas e Descargas',
    guide_create_route_btn: 'Crear nova ruta',
    guide_active: 'Guía activo:',
    guide_balance: 'Balance dispoñible',
    guide_request_payout: 'Solicitar cobro',

    // Route Creator Template
    creator_badge: 'Plantilla de Creación de Rutas',
    creator_title: 'Crea a túa Ruta de Autor',
    creator_subtitle: 'Deseña paso a paso o teu itinerario: pon o sitio, carga a túa imaxe, elixe o xogo que facer e que voz vai reproducir ese texto ou audio.',
    creator_choose_template: 'Escolle unha plantilla de partida (ou personaliza a túa):',
    creator_load_template: 'Cargar plantilla',
    creator_template_active: 'Plantilla activa',

    // Step 1: Place & Site
    creator_step1_badge: 'Paso 1',
    creator_step1_title: 'O Sitio e Datos da Ruta',
    creator_step1_desc: 'Indica o nome, o sitio xeográfico e a descrición cultural do percorrido.',
    creator_route_name: 'Nome da ruta *',
    creator_route_name_ph: 'Ex. Lendas do Miño e Pedras Sagradas da Raia...',
    creator_site_location: 'O Sitio / Enclave principal *',
    creator_site_location_ph: 'Ex. Tui / Valença do Minho, Costa da Morte, Ribeira Sacra...',
    creator_quick_sites: 'Suxestións rápidas de sitios con 1 clic:',
    creator_desc_label: 'Texto escrito de introdución',
    creator_desc_ph: 'Explica aos viaxeiros que misterios descubrirán, que feitos históricos coñecerán e que fai único a este roteiro...',
    creator_category: 'Categoría',
    creator_duration_ph: '1h 30 min',
    creator_price_label: 'Prezo do itinerario',

    // Step 2: Image
    creator_step2_badge: 'Paso 2',
    creator_step2_title: 'Imaxe Principal da Ruta',
    creator_step2_desc: 'Sube a túa fotografía, pega un enlace ou escolle unha imaxe pre-curada de Galicia.',
    creator_img_gallery: 'Galería de Galicia',
    creator_img_upload: 'Subir arquivo',
    creator_img_url: 'Enlace URL',
    creator_img_preview: 'Vista previa',
    creator_img_drag: 'Arrastra ou selecciona unha imaxe do teu dispositivo',
    creator_img_formats: 'Formatos JPG, PNG ou WebP. Amósase ao instante.',
    creator_img_btn: 'Seleccionar foto',
    creator_img_url_ph: 'https://...',
    creator_img_apply: 'Aplicar',

    // Step 3: Text, Audio & Voice
    creator_step3_badge: 'Paso 3',
    creator_step3_title: 'Locución: Que voz vai reproducir ese texto ou audio?',
    creator_step3_desc: 'Decide se a ruta terá narración oral e escolle a voz que dará vida aos relatos con previsualización en directo.',
    creator_audio_mode: 'Modalidade de reprodución',
    creator_audio_tts_title: 'Texto escrito + Audioguía sintetizada',
    creator_audio_tts_desc: 'O texto lérase en voz alta coa voz elixida polo guía.',
    creator_audio_rec_title: 'Texto escrito + Gravación propia',
    creator_audio_rec_desc: 'Grava a túa propia voz co micrófono do navegador.',
    creator_audio_written_title: 'Só lectura escrita',
    creator_audio_written_desc: 'Lectura silenciosa para o viaxeiro sen son automático.',
    creator_voice_catalog: 'Catálogo de Voces de Narración',
    creator_voice_catalog_desc: 'Preme en "Probar voz" para escoitar unha mostra antes de elixir:',
    creator_voice_test: 'Probar voz',
    creator_voice_stop: 'Deter voz',

    // Step 4: Stops & Games
    creator_step4_badge: 'Paso 4',
    creator_step4_title: 'Paradas e Xogos',
    creator_step4_desc: 'Engade cada punto do mapa co seu sitio, imaxe, historia e xogo interactivo.',
    creator_add_stop_btn: 'Engadir nova parada',
    creator_stop_name: 'Nome da parada *',
    creator_stop_name_ph: 'Ex. Mirador da Barca, Baluarte de Santa Luzia...',
    creator_stop_site: 'Sitio / Enclave concreto',
    creator_stop_site_ph: 'Ex. Punta norte, Muralla vella, Ribeira...',
    creator_stop_img: 'Imaxe da Parada',
    creator_stop_story: 'Texto escrito: Relato histórico ou lenda da parada',
    creator_stop_story_ph: 'Relato cultural que o viaxeiro lerá ao chegar a este punto...',
    creator_stop_spoken: 'Guión de locución oral (se difire do texto escrito)',
    creator_stop_voice: 'Que voz reproducirá o audio?',
    creator_stop_voice_test: 'Escoitar narración desta parada',
    creator_game_selection: 'Que xogo facer nesta parada? (Opcións fáciles de programar)',
    creator_game_selection_desc: 'Escolle entre acertijo con opcións, cifrado de letras, puzle de símbolos rúnicos ou pregunta de observación:',
    creator_game_riddle: '1. Acertijo con Opcións',
    creator_game_cipher: '2. Cifrado de Letras',
    creator_game_rune: '3. Puzle de Runas',
    creator_game_observation: '4. Pregunta e Pista',
    creator_save_stop: 'Gardar parada',
    creator_update_stop: 'Actualizar parada',

    // Bottom Actions
    creator_bottom_back: 'Volver ao panel sen gardar',
    creator_bottom_draft: 'Gardar borrador',
    creator_bottom_publish: 'Publicar ruta agora',
  },

  en: {
    // Top Nav & Common
    app_title: 'Echoes of the Land',
    app_subtitle: 'Gamified cultural routes & living heritage of Galicia',
    nav_routes: 'Routes',
    nav_map: 'Map',
    nav_my_adventure: 'My Adventure',
    nav_guide_panel: 'Guide Studio',
    nav_visitor_mode: 'Visitor Mode',
    nav_profile: 'My Profile',
    back: 'Back',
    cancel: 'Cancel',
    save: 'Save',
    close: 'Close',
    share: 'Share',
    active: 'Active',

    // Language Selector
    lang_select: 'Language',
    lang_es: 'Español',
    lang_gl: 'Galego',
    lang_en: 'English',
    lang_pt: 'Português',

    // Catalog & Filters
    catalog_title: 'Explore Galicia with Local Guides',
    catalog_subtitle: 'Choose your route, solve enigmas on the ground, and uncover living heritage.',
    filter_all: 'All',
    filter_history: 'History',
    filter_legends: 'Legends',
    filter_families: 'Families',
    filter_heritage: 'Heritage',
    filter_nature: 'Nature',
    search_placeholder: 'Search routes, places, or mysteries...',
    duration: 'Duration',
    difficulty: 'Difficulty',
    difficulty_easy: 'Easy',
    difficulty_medium: 'Moderate',
    difficulty_hard: 'Challenging',
    free: 'Free',
    stops: 'stops',

    // Route Detail & Expedition
    btn_start_route: 'Start route',
    btn_unlock_route: 'Unlock route',
    btn_view_map: 'View map',
    companion_active: 'Active Companion',
    stops_itinerary_title: 'Itinerary Stops & Trials',
    what_you_will_discover: 'What you will discover',
    privacy_note: 'Location is only used during the experience to trigger acoustic narratives at each landmark.',
    next_stop: 'Next stop',
    prev_stop: 'Previous stop',
    listen_audio: 'Play audio guide',
    stop_audio: 'Pause audio guide',
    companion_advice: 'Companion Advice',
    use_don: 'Use special gift',

    // Guide Panel
    guide_panel_title: 'Guide Studio & Payouts',
    guide_routes_tab: 'My Routes',
    guide_payouts_tab: 'Payouts & Balance',
    guide_sales_tab: 'Sales & Downloads',
    guide_create_route_btn: 'Create new route',
    guide_active: 'Active guide:',
    guide_balance: 'Available balance',
    guide_request_payout: 'Request payout',

    // Route Creator Template
    creator_badge: 'Route Creation Template',
    creator_title: 'Create Your Author Route',
    creator_subtitle: 'Design your itinerary step by step: set the site, upload images, choose games to play, and decide which voice reproduces the text or audio.',
    creator_choose_template: 'Choose a starter template (or customize from scratch):',
    creator_load_template: 'Load template',
    creator_template_active: 'Active template',

    // Step 1: Place & Site
    creator_step1_badge: 'Step 1',
    creator_step1_title: 'Site Location & Route Info',
    creator_step1_desc: 'Specify the route name, geographical site, and cultural description.',
    creator_route_name: 'Route Name *',
    creator_route_name_ph: 'e.g. Legends of the Miño River & Celtic Standing Stones...',
    creator_site_location: 'Site / Main Location *',
    creator_site_location_ph: 'e.g. Tui / Valença do Minho, Costa da Morte, Ribeira Sacra...',
    creator_quick_sites: 'Quick 1-click Galician location presets:',
    creator_desc_label: 'Written introduction text',
    creator_desc_ph: 'Explain to travelers what mysteries they will solve, what historical facts they will discover...',
    creator_category: 'Category',
    creator_duration_ph: '1h 30 min',
    creator_price_label: 'Route Price',

    // Step 2: Image
    creator_step2_badge: 'Step 2',
    creator_step2_title: 'Main Route Cover Image',
    creator_step2_desc: 'Upload your photo, paste a link, or pick a curated Galician photography preset.',
    creator_img_gallery: 'Galicia Gallery',
    creator_img_upload: 'Upload file',
    creator_img_url: 'Image URL',
    creator_img_preview: 'Preview',
    creator_img_drag: 'Drag or select an image from your device',
    creator_img_formats: 'JPG, PNG, or WebP. Instant preview.',
    creator_img_btn: 'Select photo',
    creator_img_url_ph: 'https://...',
    creator_img_apply: 'Apply',

    // Step 3: Text, Audio & Voice
    creator_step3_badge: 'Step 3',
    creator_step3_title: 'Voiceover: Which voice will reproduce this text or audio?',
    creator_step3_desc: 'Choose audio narration mode and pick the voice persona that brings legends to life with live testing.',
    creator_audio_mode: 'Playback mode',
    creator_audio_tts_title: 'Written text + Synthesized Audio Guide',
    creator_audio_tts_desc: 'The text is read aloud using the guide’s selected voice persona.',
    creator_audio_rec_title: 'Written text + Guide Voice Recording',
    creator_audio_rec_desc: 'Record your own voice directly in the browser.',
    creator_audio_written_title: 'Reading only',
    creator_audio_written_desc: 'Silent reading for travelers without automatic audio.',
    creator_voice_catalog: 'Narrator Voice Catalog',
    creator_voice_catalog_desc: 'Click "Test voice" to hear live audio sample before picking:',
    creator_voice_test: 'Test voice',
    creator_voice_stop: 'Stop voice',

    // Step 4: Stops & Games
    creator_step4_badge: 'Step 4',
    creator_step4_title: 'Stops & Interactive Games',
    creator_step4_desc: 'Add landmark points with their site name, image, story, and interactive game.',
    creator_add_stop_btn: 'Add new stop',
    creator_stop_name: 'Stop name *',
    creator_stop_name_ph: 'e.g. Barca Sanctuary Viewpoint, Fortified Gate...',
    creator_stop_site: 'Specific site location',
    creator_stop_site_ph: 'e.g. North wall, River shore, Ancient square...',
    creator_stop_img: 'Stop Image',
    creator_stop_story: 'Written text: History or legend of this stop',
    creator_stop_story_ph: 'Cultural storytelling that the traveler reads upon reaching this point...',
    creator_stop_spoken: 'Spoken script (optional if different from written text)',
    creator_stop_voice: 'Which voice will play the audio?',
    creator_stop_voice_test: 'Listen to narration for this stop',
    creator_game_selection: 'Which game to play at this stop? (Easy-to-build options)',
    creator_game_selection_desc: 'Choose between multiple-choice riddles, letter ciphers, rune puzzles, or observation questions:',
    creator_game_riddle: '1. Multiple-Choice Riddle',
    creator_game_cipher: '2. Letter Anagram Cipher',
    creator_game_rune: '3. Ancient Rune Puzzle',
    creator_game_observation: '4. Observation & Clue',
    creator_save_stop: 'Save stop',
    creator_update_stop: 'Update stop',

    // Bottom Actions
    creator_bottom_back: 'Back to dashboard without saving',
    creator_bottom_draft: 'Save draft',
    creator_bottom_publish: 'Publish route now',
  },

  pt: {
    // Top Nav & Common
    app_title: 'Ecos do Lugar',
    app_subtitle: 'Rotas gamificadas e património vivo da Galiza e do Minho',
    nav_routes: 'Rotas',
    nav_map: 'Mapa',
    nav_my_adventure: 'A Minha Aventura',
    nav_guide_panel: 'Painel do Guia',
    nav_visitor_mode: 'Ver como visitante',
    nav_profile: 'O Meu Perfil',
    back: 'Voltar',
    cancel: 'Cancelar',
    save: 'Guardar',
    close: 'Fechar',
    share: 'Partilhar',
    active: 'Ativo',

    // Language Selector
    lang_select: 'Idioma',
    lang_es: 'Español',
    lang_gl: 'Galego',
    lang_en: 'English',
    lang_pt: 'Português',

    // Catalog & Filters
    catalog_title: 'Explora a Galiza e a Raia com Guias Locais',
    catalog_subtitle: 'Escolhe o teu itinerário, resolve enigmas no terreno e descobre a memória viva.',
    filter_all: 'Todas',
    filter_history: 'História',
    filter_legends: 'Lendas',
    filter_families: 'Famílias',
    filter_heritage: 'Património',
    filter_nature: 'Natureza',
    search_placeholder: 'Pesquisar rotas, locais ou mistérios...',
    duration: 'Duração',
    difficulty: 'Dificuldade',
    difficulty_easy: 'Fácil',
    difficulty_medium: 'Média',
    difficulty_hard: 'Exigente',
    free: 'Grátis',
    stops: 'paragens',

    // Route Detail & Expedition
    btn_start_route: 'Começar rota',
    btn_unlock_route: 'Desbloquear rota',
    btn_view_map: 'Ver mapa',
    companion_active: 'Companheiro Ativo',
    stops_itinerary_title: 'Paragens e Desafios do Itinerário',
    what_you_will_discover: 'O que vais descobrir',
    privacy_note: 'A localização será utilizada apenas durante a experiência para ativar narrações acústicas no local exato.',
    next_stop: 'Próxima paragem',
    prev_stop: 'Paragem anterior',
    listen_audio: 'Ouvir audioguia',
    stop_audio: 'Pausar audioguia',
    companion_advice: 'Conselho do companheiro',
    use_don: 'Usar dom especial',

    // Guide Panel
    guide_panel_title: 'Painel do Criador e Cobranças',
    guide_routes_tab: 'As Minhas Rotas',
    guide_payouts_tab: 'Cobranças e Saldo',
    guide_sales_tab: 'Vendas e Downloads',
    guide_create_route_btn: 'Criar nova rota',
    guide_active: 'Guia ativo:',
    guide_balance: 'Saldo disponível',
    guide_request_payout: 'Solicitar levantamento',

    // Route Creator Template
    creator_badge: 'Modelo de Criação de Rotas',
    creator_title: 'Cria a Tua Rota de Autor',
    creator_subtitle: 'Desenha passo a passo o teu itinerário: define o local, carrega a tua imagem, escolhe o jogo e que voz vai reproduzir esse texto ou áudio.',
    creator_choose_template: 'Escolhe um modelo de partida (ou personaliza o teu):',
    creator_load_template: 'Carregar modelo',
    creator_template_active: 'Modelo ativo',

    // Step 1: Place & Site
    creator_step1_badge: 'Passo 1',
    creator_step1_title: 'O Local e Dados da Rota',
    creator_step1_desc: 'Indica o nome, o local geográfico e a descrição cultural do percurso.',
    creator_route_name: 'Nome da rota *',
    creator_route_name_ph: 'Ex. Lendas do Minho e Pedras Sagradas da Fronteira...',
    creator_site_location: 'O Local / Ponto principal *',
    creator_site_location_ph: 'Ex. Tui / Valença do Minho, Costa da Morte, Ribeira Sacra...',
    creator_quick_sites: 'Sugestões rápidas de locais com 1 clique:',
    creator_desc_label: 'Texto escrito de introdução',
    creator_desc_ph: 'Explica aos viajantes que mistérios vão descobrir e que factos históricos vão conhecer...',
    creator_category: 'Categoria',
    creator_duration_ph: '1h 30 min',
    creator_price_label: 'Preço do itinerário',

    // Step 2: Image
    creator_step2_badge: 'Passo 2',
    creator_step2_title: 'Imagem Principal da Rota',
    creator_step2_desc: 'Carrega a tua fotografia, cola uma ligação ou escolhe uma imagem da Galiza.',
    creator_img_gallery: 'Galeria da Galiza',
    creator_img_upload: 'Carregar ficheiro',
    creator_img_url: 'Ligação URL',
    creator_img_preview: 'Pré-visualização',
    creator_img_drag: 'Arrasta ou seleciona uma imagem do teu dispositivo',
    creator_img_formats: 'Formatos JPG, PNG ou WebP. Apresentação imediata.',
    creator_img_btn: 'Selecionar foto',
    creator_img_url_ph: 'https://...',
    creator_img_apply: 'Aplicar',

    // Step 3: Text, Audio & Voice
    creator_step3_badge: 'Passo 3',
    creator_step3_title: 'Locução: Que voz vai reproduzir esse texto ou áudio?',
    creator_step3_desc: 'Decide se a rota terá narração oral e escolhe a voz que dará vida às histórias com pré-visualização ao vivo.',
    creator_audio_mode: 'Modalidade de reprodução',
    creator_audio_tts_title: 'Texto escrito + Audioguia sintetizado',
    creator_audio_tts_desc: 'O texto será lido em voz alta com a voz escolhida pelo guia.',
    creator_audio_rec_title: 'Texto escrito + Gravação própria',
    creator_audio_rec_desc: 'Grava a tua própria voz com o microfone do navegador.',
    creator_audio_written_title: 'Apenas leitura escrita',
    creator_audio_written_desc: 'Leitura silenciosa para o viajante sem áudio automático.',
    creator_voice_catalog: 'Catálogo de Vozes de Narração',
    creator_voice_catalog_desc: 'Clica em "Testar voz" para ouvir uma amostra ao vivo antes de escolher:',
    creator_voice_test: 'Testar voz',
    creator_voice_stop: 'Parar voz',

    // Step 4: Stops & Games
    creator_step4_badge: 'Passo 4',
    creator_step4_title: 'Paragens e Jogos',
    creator_step4_desc: 'Adiciona cada ponto do mapa com o seu local, imagem, história e jogo interativo.',
    creator_add_stop_btn: 'Adicionar nova paragem',
    creator_stop_name: 'Nome da paragem *',
    creator_stop_name_ph: 'Ex. Miradouro da Barca, Baluarte de Santa Luzia...',
    creator_stop_site: 'Local / Enclave concreto',
    creator_stop_site_ph: 'Ex. Muralha norte, Rio Minho, Praça antiga...',
    creator_stop_img: 'Imagem da Paragem',
    creator_stop_story: 'Texto escrito: História ou lenda da paragem',
    creator_stop_story_ph: 'Relato cultural que o viajante lerá ao chegar a este ponto...',
    creator_stop_spoken: 'Guião de locução oral (se diferente do texto escrito)',
    creator_stop_voice: 'Que voz vai reproduzir o áudio?',
    creator_stop_voice_test: 'Ouvir narração desta paragem',
    creator_game_selection: 'Que jogo fazer nesta paragem? (Opções fáceis de programar)',
    creator_game_selection_desc: 'Escolhe entre adivinha com opções, cifra de letras, puzzle de símbolos rúnicos ou pergunta de observação:',
    creator_game_riddle: '1. Adivinha com Opções',
    creator_game_cipher: '2. Cifra de Letras',
    creator_game_rune: '3. Puzzle de Runas',
    creator_game_observation: '4. Pergunta e Pista',
    creator_save_stop: 'Guardar paragem',
    creator_update_stop: 'Atualizar paragem',

    // Bottom Actions
    creator_bottom_back: 'Voltar ao painel sem guardar',
    creator_bottom_draft: 'Guardar rascunho',
    creator_bottom_publish: 'Publicar rota agora',
  },
};

// Listeners for reactive language change across components
const listeners = new Set<(lang: AppLanguage) => void>();

export function getStoredLanguage(): AppLanguage {
  try {
    const saved = localStorage.getItem(STORAGE_KEY) as AppLanguage;
    if (saved && ['es', 'gl', 'en', 'pt'].includes(saved)) {
      return saved;
    }
  } catch {
    // fallback
  }
  return 'es'; // Default is Spanish as requested ("En español todo")
}

export function setStoredLanguage(lang: AppLanguage) {
  try {
    localStorage.setItem(STORAGE_KEY, lang);
  } catch {
    // ignore
  }
  listeners.forEach((listener) => listener(lang));
}

export function translate(key: string, lang?: AppLanguage): string {
  const currentLang = lang || getStoredLanguage();
  const dict = translations[currentLang] || translations.es;
  return dict[key] || translations.es[key] || key;
}

export function useI18n() {
  const [lang, setLangState] = useState<AppLanguage>(getStoredLanguage);

  useEffect(() => {
    const handler = (newLang: AppLanguage) => setLangState(newLang);
    listeners.add(handler);
    return () => {
      listeners.delete(handler);
    };
  }, []);

  const changeLanguage = (newLang: AppLanguage) => {
    setStoredLanguage(newLang);
  };

  const t = (key: string) => translate(key, lang);

  return {
    lang,
    setLang: changeLanguage,
    t,
    supportedLanguages: SUPPORTED_LANGUAGES,
  };
}
