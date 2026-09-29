import { RouteItem } from '../types';
import { MOCK_GUIDES } from './guidesData';

export const APP_LOGO_URL =
  'https://lh3.googleusercontent.com/aida/AEtjO1Ui7VA-PCAed6sDnmiNOlEeYWhzlK6Ey1rsYOYc3Hdo8n0lWp5tvYyQW0U5-QIPQ6nFjBhnBS1JRIbfTw8746rxPOXoegLBhHHP_aLR179pbqBrBtowtdLQgby4rIt6-YK-OMysksGI762fHTG79PLzV1bcMKQ_z7XRBAxZaaJ37tGRHetofB84RIJoqqnnQpeh2VQypTn_GRM9Uq03NJYVpyg6AjdqHZLeS6GG_GxJ6_pKxXUFSPBGX94';

export const HERO_FOREST_URL =
  'https://lh3.googleusercontent.com/aida-public/AB6AXuDJsD_GV4LqUWigjOfMKmrw3LLdS0cAoJ19hNaLjgc8jYswZ7LWbrYTeMZtv9kVHadZLF8ZqemZrtu0g28o3U2fNN7Hj3mkxsj8kfsaUOa9FZPIMkLcHWuKiwX0EnXb1QDItbRYe4-1BygCfvvWAgPBi062_ThnaTbOqxv7UF1BKJVFSwlN3NAHaHouKIRXzHEvVkTr95gxfZpl-SNzIGnfqRjiI4MFpnNQJZRPLwm9vBI4QYzX28GDfw';

export const HERO_CONTRABANDO_URL =
  'https://lh3.googleusercontent.com/aida-public/AB6AXuArehOjJRSLoB92TmGgRWPYTLoDkjiIljkqHRE5yvpOdwYKG1QJ50nLeKS8MKqaUIWEYIXGVfdccqiJ58yH1cVkDrEwr2NzR5EZCOODJt-hvxYO8ECqAd1NZqrCu7_aMFGOcz5DCZndbixfyXRoBoWnV9RjzOVUE4Q2lvEBywD2colZlzhIaXiZYp99f3qPUuGYDl2DcWN3sJI1umanW5jB_ZjNLiueMyhQy5G88XRXM_6hohI5VgVS2Q';

export const HERO_COAST_URL =
  'https://lh3.googleusercontent.com/aida-public/AB6AXuDZPX9XqxSZK7KajA3QRMtnX8sS3XOmv8_i6qb2ijQnObaChy5HYVUzmjM0sIrT4s7xWg20l1bRxnPaUyatSCqM8w-j9Uppw-Zw0yWF2zWoUCauv_QjTFVaONuhkVAdFgnfnD2ItlbStpbsh8yy3prFXhagnf2NSF0EvOS0X3JrrdAyxiGDBHuxDOkzsbzHi7S5f2gbKfjgL5BTvs0GGjUyZBPTX47X9uAj6DxBcHYjGZZjuVbx0eRG4Q';

export const HERO_CANYON_URL =
  'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80';

export const HERO_OCEAN_URL =
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80';

export const HERO_MOUNTAIN_URL =
  'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=80';

export const HERO_SANTIAGO_URL =
  'https://images.unsplash.com/photo-1513584684374-8bab748fbf90?auto=format&fit=crop&w=1200&q=80';

export const ROUTES: RouteItem[] = [
  {
    id: 'contrabando',
    title: 'Ruta del Contrabando',
    subtitle: 'La Raya y el Estraperlo: pasos clandestinos de café, bacalao y memorias entre Tui y Valença',
    description:
      'Recorre los senderos clandestinos de la frontera hispano-lusa junto al río Miño. Descubre cómo las gentes de Tui y Valença tejieron lazos de complicidad burlando garitas de carabineros durante los años del estraperlo.',
    location: 'Tui / Valença',
    region: 'BAJO MIÑO · FRONTERA DE TUI Y VALENÇA',
    image: HERO_CONTRABANDO_URL,
    isFeatured: true,
    stopsCount: 4,
    duration: '1h 45 min',
    distance: '3,8 km',
    difficulty: 'Fácil',
    categories: ['historia', 'patrimonio'],
    audioTag: 'Relato de la Raya',
    badgeTone: 'secondary',
    tomoRef: 'Cuaderno de la Frontera · Tomo VIII',
    price: '2,99 €',
    isFree: false,
    guide: MOCK_GUIDES[0], // Ana Rodríguez, guía oficial en Tui
    status: 'publicada',
    viewsCount: 1420,
    startedCount: 285,
    revenue: 852.15,
    overviewFeatures: [
      {
        icon: 'bridge',
        title: 'Puente Internacional Eiffel',
        description: 'Construido en 1886 uniendo Galicia y Portugal sobre las corrientes del Miño.',
        accentColor: 'primary',
      },
      {
        icon: 'visibility',
        title: 'Garitas secretas de carabineros',
        description: 'Puestos de vigilancia nocturna camuflados en las murallas de la fortaleza.',
        accentColor: 'secondary',
      },
      {
        icon: 'coffee',
        title: 'El aroma clandestino del café',
        description: 'La mítica ruta de sacos de café portugués y toallas cruzados en barcas silenciosas.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-cb1',
        order: 1,
        title: 'Puente Internacional de Tui y Valença',
        subtitle: 'El paso de hierro forjado sobre el espejo del Miño',
        locationName: 'Puente Internacional, Tui',
        story:
          'Inaugurado en 1886, este puente metálico se convirtió en la arteria vital de la Raya. En las noches sin luna, cuando la niebla subía del río, las sombras cruzaban fardos de café de contrabando bajo la mirada cómplice del río.',
        historicalNote:
          'Permitió la primera conexión ferroviaria y peatonal directa entre España y Portugal en el noroeste peninsular.',
        enigma: {
          question: '¿En qué año fue inaugurado el histórico puente internacional metálico?',
          hint: 'Último tercio del siglo XIX (188X).',
          answer: '1886.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Enigma del Paso de Hierro',
          loreContext: 'Justo en el punto medio del puente donde la línea divisoria separa ambos países, la brisa del río guarda esta pregunta histórica.',
          question: '«Cruzo dos países sin moverme del sitio; sostengo trenes y pasos nocturnos de estraperlo con remaches de hierro forjado. ¿En qué año comencé a unir Tui con Valença?»',
          options: ['Año 1886', 'Año 1923', 'Año 1789', 'Año 1950'],
          correctOptionIndex: 0,
          generalHint: 'El puente fue inaugurado en la segunda mitad del siglo XIX, antes de la llegada del siglo XX.',
          characterClues: {
            meiga: 'La Meiga dice: «1886, cuando el hierro sustituyó a las viejas barcas de paso.»',
            caminante: 'El Caminante recuerda: «En el año 1886 se selló la unión de hierro sobre el Miño.»',
          },
          solutionExplanation: 'El Puente Internacional fue abierto al tráfico en el año 1886, cambiando para siempre la historia de la frontera.',
          magicalReward: 'Llave de Hierro de la Raya.',
        },
        audioDuration: '2:50',
        ambientSoundName: 'vento_ponte_tui',
        coordinates: { lat: 42.046, lng: -8.644, x: 28, y: 82 },
      },
      {
        id: 'stop-cb2',
        order: 2,
        title: 'Las Murallas de Valença y la Puerta de la Coronada',
        subtitle: 'La gran fortaleza abaluartada y los pasadizos ocultos',
        locationName: 'Fortaleza de Valença do Minho',
        story:
          'Una de las mayores fortificaciones militares de Europa, con doble línea de fosos y baluartes del siglo XVII. Aquí, tras las puertas de cantería, los comerciantes portugueses guardaban el café en grano y las piezas de lino destinadas al mercado gallego.',
        historicalNote:
          'Diseñada según el sistema de fortificación abaluartada del maestro Vauban para defender la frontera tras la Guerra de Restauración.',
        enigma: {
          question: '¿Qué forma geométrica tienen los baluartes defensivos que salen en punta hacia el río?',
          hint: 'Forma de punta de flecha o estrella.',
          answer: 'Baluartes en punta de flecha o estrella.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado de la Mercancía Prohibida',
          loreContext: 'Un viejo cajón de madera hallado en la Puerta de la Coronada lleva grabada la palabra del tesoro aromático más codiciado.',
          question: 'Ordena las letras para descubrir el producto rey del estraperlo en la Raya:',
          cipherWord: 'CAFE',
          scrambledLetters: ['F', 'A', 'E', 'C'],
          cipherClue: 'C A _ E — El grano tostado que subía de Portugal a Galicia por el río.',
          generalHint: 'Cuatro letras: bebida negra y aromática que despertó a generaciones.',
          characterClues: {
            trasno: 'El Trasno salta: «¡C-A-F-É! ¡Qué aroma más rico por las mañanas!»',
            moura: 'La Moura sabe: «El grano oscuro del CAFÉ era más codiciado que la plata.»',
          },
          solutionExplanation: 'El café era la mercancía estrella del contrabando miñoto por su alto valor y demanda.',
          magicalReward: 'Grano de Oro de Valença.',
        },
        audioDuration: '3:10',
        ambientSoundName: 'murallas_valenca',
        coordinates: { lat: 42.031, lng: -8.644, x: 30, y: 84 },
      },
    ],
  },
  {
    id: 'leyendas-mino',
    title: 'Leyendas del Miño',
    subtitle: 'El Río Encantado: mouras de las rocas, corrientes vivas y fortalezas fronterizas',
    description:
      'El río más caudaloso de Galicia es también la cuna de mitos inmemoriales. Siente el murmullo de las mouras que tejen hilos de oro bajo las rocas de los molinos y el eco de las fortalezas que custodian la Raya desde tiempos medievales.',
    location: 'Frontera del Miño',
    region: 'LA RAYA · FRONTERA DEL MIÑO (PONTEVEDRA Y PORTUGAL)',
    image: HERO_CANYON_URL,
    isFeatured: false,
    stopsCount: 4,
    duration: '2h',
    distance: '4,2 km',
    difficulty: 'Media',
    categories: ['leyendas', 'historia'],
    audioTag: 'Susurros fluviales',
    badgeTone: 'tertiary',
    tomoRef: 'Mitología de la Raya · Tomo IX',
    price: '1,99 €',
    isFree: false,
    guide: MOCK_GUIDES[1], // Marcos Varela, experto en patrimonio local
    status: 'publicada',
    viewsCount: 980,
    startedCount: 194,
    revenue: 386.06,
    overviewFeatures: [
      {
        icon: 'water',
        title: 'El Pozo de las Mouras Encantadas',
        description: 'Leyendas de seres sobrenaturales que habitan en las profundidades del Miño.',
        accentColor: 'primary',
      },
      {
        icon: 'castle',
        title: 'Castillo de San Lorenzo de Goián',
        description: 'Fortaleza abaluartada fronteriza del siglo XVII en Tomiño.',
        accentColor: 'secondary',
      },
      {
        icon: 'sailing',
        title: 'La Barca de Paso de Goián a Vila Nova',
        description: 'Siglos de viajeros que cruzaban el río antes de la construcción de los puentes.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-lm1',
        order: 1,
        title: 'Fuerte de San Lorenzo de Goián',
        subtitle: 'El centinela de Tomiño frente a Vila Nova de Cerveira',
        locationName: 'Goián, Tomiño',
        story:
          'Erigido en la orilla del río en 1665, este fuerte protegía el paso fluvial durante la guerra de Restauración. Sus piedras guardan la leyenda del centinela invisible que hace sonar la trompeta cuando las aguas del Miño bajan cubiertas de niebla.',
        historicalNote:
          'Edificación militar de sillería con fosos y baluartes angulados para baterías de artillería.',
        enigma: {
          question: '¿Qué villa portuguesa fortificada se alza justo enfrente al otro lado del río?',
          hint: 'Vila Nova de...',
          answer: 'Vila Nova de Cerveira.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Acertijo de la Villa Hermana',
          loreContext: 'Desde la tronera del Fuerte de San Lorenzo, mirando hacia Portugal por la corriente.',
          question: '«Me miro en el mismo espejo de agua que el Fuerte de Goián; tengo nombre de villa marinera y un ciervo legendario en la cumbre de mi monte. ¿Qué villa soy?»',
          options: [
            'Vila Nova de Cerveira',
            'Caminha de la Desembocadura',
            'Ponte de Lima',
            'Monção de las Murallas',
          ],
          correctOptionIndex: 0,
          generalHint: 'La villa de los artistas y del ciervo de bronce que mira hacia el Miño.',
          characterClues: {
            lavandeira: 'La Lavandeira canta: «De Goián a Cerveira va un hilo de seda por el Miño.»',
            caminante: 'El Caminante señala: «Vila Nova de Cerveira, villa hermana al otro lado del río.»',
          },
          solutionExplanation: 'Vila Nova de Cerveira es la localidad portuguesa gemela de Tomiño unida históricamente por las aguas del Miño.',
          magicalReward: 'Sello de las Dos Orillas del Miño.',
        },
        audioDuration: '3:05',
        ambientSoundName: 'rio_mino_suave',
        coordinates: { lat: 41.942, lng: -8.742, x: 26, y: 86 },
      },
    ],
  },
  {
    id: 'memoria-marinera',
    title: 'Memoria Marinera',
    subtitle: 'Secretos de la Costa Atlántica: naufragios, vidas de puerto y el cantar de las rederas',
    description:
      'Un viaje sonoro y visual por las villas costeras gallegas. Escucha la memoria oral de los hombres que desafiaron el mar embravecido, la labor secular de las rederas y las leyendas de sirenas y tormentas guardadas en las piedras de los muelles antiguos.',
    location: 'Costa Atlántica',
    region: 'COSTA ATLÁNTICA · RÍAS BAIXAS Y ALTAS',
    image: HERO_COAST_URL,
    isFeatured: false,
    stopsCount: 4,
    duration: '1h 30 min',
    distance: '3,5 km',
    difficulty: 'Fácil',
    categories: ['patrimonio', 'naturaleza'],
    audioTag: 'Bate de olas',
    badgeTone: 'secondary',
    tomoRef: 'Cuaderno del Salitre · Tomo X',
    price: 'Gratis',
    isFree: true,
    guide: MOCK_GUIDES[2], // Lucía Fernández, guía cultural en Galicia
    status: 'publicada',
    viewsCount: 2150,
    startedCount: 620,
    revenue: 0,
    overviewFeatures: [
      {
        icon: 'anchor',
        title: 'Muelles centenarios de piedra',
        description: 'Los puertos tradicionales donde atracaban los galeones de cabotaje.',
        accentColor: 'primary',
      },
      {
        icon: 'hand',
        title: 'La sabiduría de las rederas',
        description: 'El arte patrimonial de tejer redes y reparar los nudos del oficio marinero.',
        accentColor: 'secondary',
      },
      {
        icon: 'waves',
        title: 'Naufragios y faros del fin del mundo',
        description: 'Historias de naufragios heroicos y la solidaridad de los pueblos costeros.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-mm1',
        order: 1,
        title: 'Puerto Histórico y la Lonja Vieja',
        subtitle: 'El bullicio de la mañana y el pescado de plata fresca',
        locationName: 'Muelle Viejo, Costa Gallega',
        story:
          'Cuando las dornas y lanchas tradicionales regresaban al amanecer con las redes llenas de sardina, las campanas del puerto tocaban a rebato. Las mujeres corrían con las cestas en la cabeza descalzas por las lajas de piedra mojada por el salitre.',
        historicalNote:
          'El puerto tradicional gallego era el centro económico y comunitario donde la tradición oral se transmitía de generación en generación.',
        enigma: {
          question: '¿Cómo se llama la embarcación tradicional gallega de fondo plano y madera de roble que navega en las rías?',
          hint: 'Empieza por D y tiene cinco letras.',
          answer: 'La dorna gallega.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado del Barco Tradicional',
          loreContext: 'Grabado en un viejo remo de haya junto a la lonja vieja.',
          question: 'Reordena las letras para descubrir el nombre de la embarcación señera de las rías:',
          cipherWord: 'DORNA',
          scrambledLetters: ['R', 'A', 'N', 'D', 'O'],
          cipherClue: 'D O _ N A — El barco que corta las olas con vela mística.',
          generalHint: 'Embarcación típica de las rías gallegas, de popa redonda y proa afilada.',
          characterClues: {
            lavandeira: 'La Lavandeira recuerda: «¡D-O-R-N-A! Como la que lleva a los marineros a pescar.»',
            trasno: 'El Trasno brinca: «¡Yo subí en una DORNA y me mareé con las olas!»',
          },
          solutionExplanation: 'La dorna es el símbolo vivo de la navegación tradicional y del ingenio carpintero de ribera de Galicia.',
          magicalReward: 'Estela de Salitre y Madera de Dorna.',
        },
        audioDuration: '2:45',
        ambientSoundName: 'porto_mariñeiro',
        coordinates: { lat: 42.485, lng: -8.874, x: 18, y: 72 },
      },
    ],
  },
  {
    id: 'ribeira-sacra',
    title: 'El Aliento de los Nueve Obispos y el Silencio Sagrado',
    subtitle: 'Leyenda de los Anillos Milagrosos y los Monasterios Hechizados del Sil',
    description:
      'Un viaje místico entre los cañones vertiginosos del río Sil, donde nueve obispos santos dejaron anillos milagrosos y las terrazas de vid desafían el abismo desde tiempos de los romanos.',
    location: 'Ribeira Sacra (Ourense / Lugo)',
    region: 'RIBEIRA SACRA · CAÑONES DEL SIL (OURENSE Y LUGO)',
    image: HERO_CANYON_URL,
    isFeatured: true,
    stopsCount: 6,
    duration: '2h 15 min',
    distance: '4,6 km',
    difficulty: 'Media',
    categories: ['patrimonio', 'historia', 'leyendas'],
    audioTag: 'Relato sonoro inmersivo',
    badgeTone: 'secondary',
    tomoRef: 'Grimorio Sacro · Tomo I',
    price: '2,99 €',
    isFree: false,
    guide: MOCK_GUIDES[1], // Marcos Varela
    status: 'publicada',
    viewsCount: 1650,
    startedCount: 320,
    revenue: 956.80,
    overviewFeatures: [
      {
        icon: 'church',
        title: 'Monasterios medievales legendarios',
        description:
          'Explora Santo Estevo y Santa Cristina, donde los cantos gregorianos se funden con el murmullo de los robles.',
        accentColor: 'primary',
      },
      {
        icon: 'terrain',
        title: 'Terrazas milenarias de Amandi',
        description:
          'Pendientes de más del 60% donde la leyenda dice que los romanos recogían el vino sagrado de los emperadores.',
        accentColor: 'secondary',
      },
      {
        icon: 'visibility',
        title: 'Balcones de Madrid',
        description:
          'Mirador sobre el abismo de 500 metros donde las meigas escuchaban los cantos de despedida de la emigración.',
        accentColor: 'tertiary',
      },
      {
        icon: 'auto_stories',
        title: 'Los Anillos de los Nueve Obispos',
        description:
          'El enigma de los anillos que curaban el mal de ojo y la ceguera, guardados en un cofre de plata medieval.',
        accentColor: 'primary',
      },
    ],
    stops: [
      {
        id: 'stop-rs1',
        order: 1,
        title: 'Monasterio de Santo Estevo de Ribas de Sil',
        subtitle: 'Los tres claustros sagrados y el arca de los nueve obispos',
        locationName: 'Nogueira de Ramuín (Ourense)',
        story:
          'Oculto en el corazón de un bosque de castaños milenarios, este monasterio benedictino fue el retiro de nueve obispos ermitaños. La tradición asegura que sus anillos milagrosos protegían las cosechas y ahuyentaban cualquier maleficio de las riberas.',
        historicalNote:
          'Sus orígenes se remontan al siglo X, mezclando románico, gótico y renacentista con tres claustros singulares: de los Caballeros, de los Obispos y del Vivero.',
        enigma: {
          question: '¿Qué ser mitológico esculpido en piedra custodia la entrada del claustro gótico?',
          hint: 'Una criatura mitológica mitad águila y mitad león.',
          answer: 'El grifo alado que guarda los libros de liturgia sagrada.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'La Alineación de los Símbolos de los Nueve Obispos',
          loreContext: 'Para abrir el arca sagrada de los obispos debes ordenar los cuatro glifos protectores según la tradición benedictina.',
          question: 'Ordena los símbolos arcanos: desde el guardián celestial hasta la raíz de la tierra.',
          runes: [
            { id: 'grifon', glyph: '🦅', name: 'Grifo Alado', desc: 'Custodio del templo' },
            { id: 'anel', glyph: '💍', name: 'Anillo Sagrado', desc: 'Sanación y milagros' },
            { id: 'cruz', glyph: '✝️', name: 'Cruz Románica', desc: 'Refugio de los viajeros' },
            { id: 'carballo', glyph: '🌳', name: 'Robledal Eterno', desc: 'Poder telúrico del bosque' },
          ],
          correctRuneOrder: ['grifon', 'anel', 'cruz', 'carballo'],
          generalHint: 'Comienza por el animal que vigila el cielo, sigue por la joya episcopal y concluye en el árbol sagrado.',
          characterClues: {
            meiga: 'La Meiga dice: «El grifo abre el portal, y el anillo de los obispos concentra la energía antes de bendecir el roble.»',
            trasno: 'El Trasno ríe: «¡🦅 primero, después pon la joya brillante 💍, la cruz ✝️ y el árbol 🌳!»',
            moura: 'La Moura susurra: «El metal y la pluma preceden siempre a la madera vieja.»',
          },
          solutionExplanation: 'El grifo alado, el anillo milagroso, la cruz románica y el roble sagrado completan el sello ancestral de los obispos de Ribas de Sil.',
          magicalReward: 'Sello de los Nueve Obispos grabado en tu pasaporte.',
        },
        audioDuration: '3:15',
        ambientSoundName: 'canons_sil_mist',
        coordinates: { lat: 42.4172, lng: -7.6853, x: 58, y: 64 },
      },
      {
        id: 'stop-rs2',
        order: 2,
        title: 'Mirador de los Balcones de Madrid',
        subtitle: 'La grieta colosal sobre el abismo del río Sil',
        locationName: 'Parada de Sil (Ourense)',
        story:
          'Desde esta atalaya natural de granito que cae a pico 500 metros hacia las aguas oscuras del Sil, sonaban las bocinas de despedida de los barberos ambulantes que partían hacia la corte de Madrid. Los antiguos decían que la niebla del cañón guardaba los ecos de los que nunca regresaron.',
        historicalNote:
          'El río Sil labró este cañón durante millones de años a través de fallas tectónicas del macizo hercínico ibérico.',
        enigma: {
          question: '¿Qué ave rapaz de gran envergadura anida en los riscos justo debajo del banco de piedra?',
          hint: 'Es la señora de los cielos de la Ribeira Sacra con plumaje castaño.',
          answer: 'El águila real (Aquila chrysaetos).',
        },
        trial: {
          type: 'acertijo',
          title: 'El Acertijo del Abismo y la Reina de los Cielos',
          loreContext: 'Ante el vacío de 500 metros sobre el río Sil, la niebla revela un enigma esculpido en el viento.',
          question: '«No toco el agua pero vigilo su fondo oscuro; tengo corona de plumas doradas y mi grito resuena por el cañón. ¿Quién soy?»',
          options: [
            'La Lavandeira de la niebla fluvial',
            'El Águila Real que anida en los riscos graníticos',
            'El Mochuelo de los bosques que aguarda la luna',
            'El Halcón peregrino que corta el aire',
          ],
          correctOptionIndex: 1,
          generalHint: 'Es la reina de los riscos de la Ribeira Sacra que extiende sus alas sobre el abismo.',
          characterClues: {
            meiga: 'La Meiga siente su sombra: «Su ojo de oro mira desde los nidos de la roca más alta.»',
            lobishome: 'El Lobishome olfatea el viento: «Plumas doradas y garras que cazan en el abismo: el águila real.»',
          },
          solutionExplanation: 'El águila real (Aquila chrysaetos) reina en los cielos del cañón del Sil desde tiempos inmemoriales.',
          magicalReward: 'Pluma Dorada de los Balcones de Madrid.',
        },
        audioDuration: '2:50',
        ambientSoundName: 'vento_canons',
        coordinates: { lat: 42.3985, lng: -7.5678, x: 65, y: 67 },
      },
      {
        id: 'stop-rs3',
        order: 3,
        title: 'Terrazas Heroicas de Amandi',
        subtitle: 'Viñas que desafían la gravedad y el hechizo del vino imperial',
        locationName: 'Sober (Lugo)',
        story:
          'Los legionarios de Roma llamaban a este vino «el oro líquido del Sil» y lo enviaban directamente a la mesa del Emperador en ánforas selladas con resina de pino. Las terrazas fueron construidas piedra a piedra por manos heroicas sobre pendientes vertiginosas.',
        historicalNote:
          'La inclinación llega al 70%, requiriendo raíles y poleas manuales para la vendimia tradicional.',
        enigma: {
          question: '¿Cómo se llaman los muros de piedra seca que sostienen las estrechas terrazas de tierra fértil?',
          hint: 'En la Ribeira Sacra reciben el nombre tradicional de bancales o socalcos.',
          answer: 'Socalcos o bancales de piedra granítica.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado del Oro Líquido de Roma',
          loreContext: 'Una vieja ánfora romana hallada en las terrazas guarda un sello de seis letras con el nombre de la subzona más mítica del Sil.',
          question: 'Ordena las letras para recomponer el nombre de la tierra sagrada de las viñas verticales:',
          cipherWord: 'AMANDI',
          scrambledLetters: ['D', 'A', 'M', 'I', 'N', 'A'],
          cipherClue: 'A M _ N D I — Corazón de la viticultura heroica de la Ribeira Sacra.',
          generalHint: 'Comienza por la A y concluye por la I. Es la denominación histórica más codiciada por los emperadores.',
          characterClues: {
            moura: 'La Moura recuerda: «Los romanos escribían AMANDI en los sellos de barro que bajaban por el río.»',
            trasno: 'El Trasno juega con las letras: «¡Pon A-M-A-N-D-I y déjate de cuentos!»',
          },
          solutionExplanation: 'AMANDI es el territorio mágico donde la uva mencía absorbe el calor del granito y la luz reflejada en las aguas del Sil.',
          magicalReward: 'Estela de Oro Líquido de Amandi.',
        },
        audioDuration: '3:30',
        ambientSoundName: 'rio_sil_suave',
        coordinates: { lat: 42.421, lng: -7.534, x: 69, y: 62 },
      },
    ],
  },
  {
    id: 'costa-da-morte',
    title: 'El Barco de Piedra y el Atardecer del Fin del Mundo',
    subtitle: 'El Mar Tenebroso, Piedras que Oscilan y la Dama de las Olas de Muxía',
    description:
      'Recorre la costa más telúrica de Europa. Donde el sol muere cada ocaso sobre el océano infinito, las piedras oscilantes curan la memoria de los navegantes y el cabo Fisterra custodia la mítica Ara Solis.',
    location: 'Fisterra y Muxía (A Coruña)',
    region: 'COSTA DA MORTE · FISTERRA, MUXÍA Y CABO VILÁN (A CORUÑA)',
    image: HERO_OCEAN_URL,
    isFeatured: false,
    stopsCount: 5,
    duration: '2h',
    distance: '5,2 km',
    difficulty: 'Fácil',
    categories: ['leyendas', 'historia', 'patrimonio'],
    audioTag: 'Viento y salitre',
    badgeTone: 'secondary',
    tomoRef: 'Crónicas de la Finis Terrae · Tomo II',
    price: '1,99 €',
    isFree: false,
    guide: MOCK_GUIDES[2], // Lucía Fernández
    status: 'publicada',
    viewsCount: 1820,
    startedCount: 390,
    revenue: 776.10,
    overviewFeatures: [
      {
        icon: 'waves',
        title: 'Cabo Fisterra y la Ara Solis',
        description:
          'El promontorio más mítico de la antigüedad romana, donde los viajeros quemaban sus ropas para renacer.',
        accentColor: 'primary',
      },
      {
        icon: 'cyclone',
        title: 'Santuario de la Barca en Muxía',
        description:
          'La legendaria barca de piedra en la que arribó la Virgen María y las piedras que oscilan al batir del mar bravo.',
        accentColor: 'secondary',
      },
      {
        icon: 'wb_sunny',
        title: 'El último atardecer de Europa',
        description:
          'El destello verde cuando el disco solar desaparece en el Atlántico abierto frente al cabo de los naufragios.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-cm1',
        order: 1,
        title: 'Faro del Cabo Fisterra y el Altar del Sol',
        subtitle: 'La linterna del fin del mundo y el fuego purificador',
        locationName: 'Cabo Fisterra (A Coruña)',
        story:
          'Los romanos conducidos por Décimo Junio Bruto llegaron a este acantilado en el año 137 a.C. y contemplaron con pavor cómo el mar tragaba el sol entre chispas de fuego. Aquí estuvo la mítica Ara Solis céltica, donde los peregrinos quemaban sus ropas de viaje como ritual de renacimiento.',
        historicalNote:
          'El faro fue construido en 1853 a 143 metros sobre el nivel del mar para guiar a los navíos que cruzaban la peligrosa ruta de América.',
        enigma: {
          question: '¿Qué ritual milenario realizaban los peregrinos llegados a este monte tras completar el Camino de Santiago?',
          hint: 'Relacionado con el renacer y el fuego purificador.',
          answer: 'Quemar las ropas viejas y bañarse en las aguas bravas de la playa de Langosteira.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'La Alineación del Atardecer en la Ara Solis',
          loreContext: 'Para despertar el rito ancestral del Finis Terrae debes alinear los cuatro elementos del renacimiento espiritual.',
          question: 'Alinea los elementos sagrados: desde la venera del camino hasta la luz infinita del océano.',
          runes: [
            { id: 'vieira', glyph: '🐚', name: 'Concha de Vieira', desc: 'Guía del peregrino' },
            { id: 'bordon', glyph: '🪄', name: 'Bordón de Fresno', desc: 'Apoyo en el camino' },
            { id: 'fogo', glyph: '🔥', name: 'Fuego Purificador', desc: 'Quema del pasado' },
            { id: 'solpor', glyph: '🌅', name: 'Atardecer del Finis Terrae', desc: 'Renacimiento en el mar' },
          ],
          correctRuneOrder: ['vieira', 'bordon', 'fogo', 'solpor'],
          generalHint: 'La concha abre la marcha, el bordón sostiene el paso, el fuego quema lo viejo y el sol alumbra al nuevo ser.',
          characterClues: {
            caminante: 'El Caminante sabe: «La vieira viene con nosotros desde Compostela, el fuego purifica el alma y el atardecer sella el destino.»',
            meiga: 'La Meiga atiza la llama: «🔥 El fuego quema la vieja túnica antes de que el sol muera en el océano.»',
          },
          solutionExplanation: 'El peregrino deposita la vieira, apoya el bordón, entrega la ropa vieja al fuego y contempla el nacimiento de un nuevo espíritu frente al Atlántico.',
          magicalReward: 'Sello del Finis Terrae y la Luz de la Ara Solis.',
        },
        audioDuration: '3:20',
        ambientSoundName: 'mar_bravio_fisterra',
        coordinates: { lat: 42.8821, lng: -9.2718, x: 12, y: 45 },
      },
      {
        id: 'stop-cm2',
        order: 2,
        title: 'Piedra de Abalar y Santuario de la Barca',
        subtitle: 'El barco pétreo y los ritos de fertilidad de Muxía',
        locationName: 'Punta de la Barca, Muxía',
        story:
          'La gigantesca Piedra de Abalar es según la tradición la vela del barco de piedra en el que la Virgen llegó para confortar al Apóstol Santiago. Cuando oscila, emite un quejido sordo que los viejos marineros interpretaban como aviso de tempestad.',
        historicalNote:
          'Muxía es un enclave precristiano cristianizado en el siglo XII por el monasterio de San Julián de Moraime.',
        enigma: {
          question: '¿Qué piedra hueca hay que pasar por debajo nueve veces para sanar de los dolores de riñones y espalda?',
          hint: 'Al lado de la Piedra de Abalar.',
          answer: 'La Piedra de los Riñones (Pedra dos Cadrís).',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Verbo Mágico de la Piedra Oscilante',
          loreContext: 'El granito batido por las olas de Muxía responde únicamente cuando se pronuncia el verbo sagrado que hace estremecer a la roca.',
          question: 'Compón la palabra mágica que describe el movimiento de la gran piedra de la Barca:',
          cipherWord: 'ABALAR',
          scrambledLetters: ['L', 'A', 'A', 'B', 'R', 'A'],
          cipherClue: 'A _ A L A R — El oscilar solemne de la losa gigante sobre el mar.',
          generalHint: 'Seis letras que comienzan por A y terminan por R: el balanceo secular sobre el acantilado.',
          characterClues: {
            lavandeira: 'La Lavandeira escucha la roca: «A-B-A-L-A-R, como la marea que va y viene sin descanso.»',
            trasno: 'El Trasno salta sobre la piedra: «¡Abalar, balancéate conmigo!»',
          },
          solutionExplanation: 'ABALAR es el verbo milenario que da nombre a la piedra que habla con el mar y detecta la falsedad en los corazones.',
          magicalReward: 'Amuleto de la Piedra de Abalar de Muxía.',
        },
        audioDuration: '3:05',
        ambientSoundName: 'ondas_muxia',
        coordinates: { lat: 43.1098, lng: -9.2195, x: 14, y: 35 },
      },
    ],
  },
  {
    id: 'fragas-do-eume',
    title: 'El Bosque Encantado de las Meigas y el Cenobio de Caaveiro',
    subtitle: 'Helechos Milenarios del Terciario, Hechizos Fluviales y Nieblas del Eume',
    description:
      'Un santuario vegetal donde los helechos gigantes de la época terciaria, el murmullo del río Eume y la abadía románica de Caaveiro guardan la esencia de las meigas y de la botánica mágica gallega.',
    location: 'Pontedeume / A Capela (A Coruña)',
    region: 'LAS FRAGAS DO EUME · PARQUE NATURAL (A CORUÑA)',
    image: HERO_FOREST_URL,
    isFeatured: false,
    stopsCount: 6,
    duration: '2h 30 min',
    distance: '6,1 km',
    difficulty: 'Media',
    categories: ['naturaleza', 'leyendas', 'patrimonio'],
    audioTag: 'Folclore y vegetación',
    badgeTone: 'secondary',
    tomoRef: 'Herbario de las Fragas Mágicas · Tomo III',
    price: 'Gratis',
    isFree: true,
    guide: MOCK_GUIDES[2], // Lucía Fernández
    status: 'publicada',
    viewsCount: 2310,
    startedCount: 710,
    revenue: 0,
    overviewFeatures: [
      {
        icon: 'forest',
        title: 'Bosque atlántico de las meigas',
        description:
          'Uno de los bosques relictos mejor conservados de Europa, donde las meigas recogen la hierba de San Juan.',
        accentColor: 'secondary',
      },
      {
        icon: 'castle',
        title: 'Monasterio aislado de Caaveiro',
        description:
          'Abadía fundada en el siglo X por San Rosendo sobre un peñasco inaccesible rodeado por la niebla del río.',
        accentColor: 'primary',
      },
      {
        icon: 'water_drop',
        title: 'Leyenda de las Lágrimas del Eume',
        description:
          'El río nació según el mito de las lágrimas de un dios engañado por el sol, que corre hacia el mar cubierto de musgo.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-fe1',
        order: 1,
        title: 'Puente del Cal Grande y el Río Encantado',
        subtitle: 'El paso colgante sobre la niebla esmeralda',
        locationName: 'Entrada de las Fragas, Pontedeume',
        story:
          'Al pisar las tablas de madera sobre el río Eume, la temperatura desciende y la luz se torna esmeralda. Las leyendas cuentan que las meigas y espíritus del agua dejan hilos de niebla sobre la corriente para guiar a los viajeros puros.',
        historicalNote:
          'El Parque Natural de las Fragas do Eume comprende más de 9.000 hectáreas de bosque autóctono con microclima subtropical relicto.',
        enigma: {
          question: '¿Qué especie de helecho gigante superviviente del Terciario crece en las riberas más húmedas?',
          hint: 'Woodwardia radicans, con frondes de más de dos metros.',
          answer: 'El helecho de botón o helecho real (Woodwardia radicans).',
        },
        trial: {
          type: 'acertijo',
          title: 'El Enigma del Viejo Helecho del Terciario',
          loreContext: 'Entre el musgo húmedo de la ribera del Eume, una voz vegetal propone este acertijo milenario.',
          question: '«Sobreviví a los glaciares cuando el mundo era nuevo; mis frondes de dos metros tocan el agua y echo botones que echan raíces sin flor. ¿Quién soy?»',
          options: [
            'El Helecho de Botón (Woodwardia radicans), joya terciaria',
            'El Roble albar de las sendas',
            'La Hierba Mágica de San Cipriano',
            'El Laurel silvestre de las riberas',
          ],
          correctOptionIndex: 0,
          generalHint: 'Es un helecho gigante que solo crece en lugares donde la humedad y la sombra son eternas.',
          characterClues: {
            meiga: 'La Meiga sonríe: «¡El Woodwardia radicans! Sus hojas guardan la memoria de los primeros tiempos del bosque.»',
            lobishome: 'El Lobishome gruñe suave: «Helecho gigante donde los jabalíes se refugian del calor.»',
          },
          solutionExplanation: 'El Woodwardia radicans es un auténtico fósil vegetal viviente que sobrevive en las Fragas do Eume gracias a su cañón húmedo.',
          magicalReward: 'Hoja Mística del Helecho Terciario.',
        },
        audioDuration: '2:40',
        ambientSoundName: 'bosque_eume_fentos',
        coordinates: { lat: 43.418, lng: -8.072, x: 38, y: 18 },
      },
      {
        id: 'stop-fe2',
        order: 2,
        title: 'Monasterio de San Juan de Caaveiro',
        subtitle: 'La abadía de los ermitaños sobre el peñasco escarpado',
        locationName: 'Corazón de las Fragas, A Capela',
        story:
          'En el año 936, San Rosendo otorgó estas tierras a eremitas que buscaban la comunión con el bosque virgen. La torre de los canónigos se alza sobre el abismo protegida por la niebla, donde la campana ahuyentaba a los malos espíritus de la noche.',
        historicalNote:
          'Declarado Monumento Histórico-Artístico en 1975, conserva elementos románicos del siglo XII.',
        enigma: {
          question: '¿Qué relieve medieval adorna el tímpano de la iglesia románica de Caaveiro?',
          hint: 'El cordero místico o Agnus Dei sosteniendo la cruz.',
          answer: 'El Agnus Dei esculpido en piedra granítica.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'El Sello de los Ermitaños de Caaveiro',
          loreContext: 'Para franquear el portal románico elevado sobre el río Sesín, hay que activar los cuatro símbolos del refugio cenobítico.',
          question: 'Alinea los sellos de los ermitaños: desde la roca base hasta la luz del campanario.',
          runes: [
            { id: 'penedo', glyph: '🪨', name: 'Peñasco Escarpado', desc: 'Cimentación inexpugnable' },
            { id: 'auga', glyph: '💧', name: 'Río Sesín', desc: 'Agua pura de los ermitaños' },
            { id: 'agnus', glyph: '🐑', name: 'Agnus Dei', desc: 'Relieve protector del tímpano' },
            { id: 'campa', glyph: '🔔', name: 'Campana de los Canónigos', desc: 'Sonido que dispersa el maleficio' },
          ],
          correctRuneOrder: ['penedo', 'auga', 'agnus', 'campa'],
          generalHint: 'La roca sostiene el edificio, el río rodea el peñasco, el Agnus protege el tímpano y la campana suena en lo más alto.',
          characterClues: {
            moura: 'La Moura sabe de piedras: «El peñasco sostiene el agua, que lava la puerta del cordero místico.»',
            meiga: 'La Meiga toca la campana: «La campana es la última en sonar para dispersar el hechizo oscuro.»',
          },
          solutionExplanation: 'El peñasco escarpado, el río Sesín, el Agnus Dei y la campana bendecida conforman el escudo espiritual de Caaveiro.',
          magicalReward: 'Sello de Caaveiro y Protección de las Fragas.',
        },
        audioDuration: '3:45',
        ambientSoundName: 'campas_caaveiro',
        coordinates: { lat: 43.421, lng: -8.041, x: 44, y: 20 },
      },
    ],
  },
  {
    id: 'serra-do-courel',
    title: 'El Aullido del Lobishome y la Devesa de los Mil Encantos',
    subtitle: 'Soutos Mágicos de Seiscientos Años, Pizarra Negra y Fuentes Ferruginosas',
    description:
      'La sierra más profunda y misteriosa de Galicia. Un territorio de tejados de pizarra negra, castañares de seiscientos años, donde habita el espíritu del lobishome y la reserva botánica de la Devesa da Rogueira.',
    location: 'Folgoso do Courel (Lugo)',
    region: 'SIERRA DEL COUREL · LOS ANCARES Y FOLGOSO (LUGO)',
    image: HERO_MOUNTAIN_URL,
    isFeatured: false,
    stopsCount: 5,
    duration: '3h',
    distance: '7,4 km',
    difficulty: 'Exigente',
    categories: ['naturaleza', 'patrimonio', 'leyendas'],
    audioTag: 'Relato de montaña',
    badgeTone: 'tertiary',
    tomoRef: 'Mitología del Courel · Tomo IV',
    price: '2,99 €',
    isFree: false,
    guide: MOCK_GUIDES[1], // Marcos Varela
    status: 'publicada',
    viewsCount: 1120,
    startedCount: 240,
    revenue: 717.60,
    overviewFeatures: [
      {
        icon: 'landscape',
        title: 'Devesa da Rogueira',
        description:
          'El bosque más biodiverso de Galicia, donde conviven hayas, tejos y fuentes de agua ferruginosa y calcárea.',
        accentColor: 'secondary',
      },
      {
        icon: 'cottage',
        title: 'Aldeas medievales de pizarra negra',
        description:
          'Seceda, Froxán y Meiraos, con pasadizos de castaño que protegían de los temporales y del lobo.',
        accentColor: 'primary',
      },
      {
        icon: 'pets',
        title: 'Leyenda del Lobishome y del Oso',
        description:
          'La última gran frontera salvaje donde el lobo ibérico y los robledales conservan su territorio mágico.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-sc1',
        order: 1,
        title: 'Aldea Histórica de Seceda y los Sequeiros',
        subtitle: 'La joya de la pizarra negra y los secretos de la castaña',
        locationName: 'Seceda, Folgoso do Courel',
        story:
          'Caminar por Seceda es entrar en la Galicia de nuestros antepasados. Las calles están cubiertas por pasadizos elevados de castaño. En los sequeiros se secaban las castañas con el humo del fuego del hogar, mientras los mayores contaban leyendas de hombres que mudaban la piel en las noches de luna llena.',
        historicalNote:
          'Declarada Conjunto Histórico-Artístico por su conservación ejemplar de la arquitectura tradicional de montaña en pizarra.',
        enigma: {
          question: '¿Cómo se llaman las construcciones tradicionales de piedra donde se secaban las castañas al humo?',
          hint: 'En el Courel reciben el nombre de sequeiros.',
          answer: 'Los sequeiros de castañas.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado del Sequeiro Ancestral',
          loreContext: 'Una viga de castaño curada al humo guarda grabada la palabra que alimentó a los pobladores del Courel durante siglos de invierno.',
          question: 'Recompón la palabra que designa el templo de la castaña y el humo:',
          cipherWord: 'SEQUEIRO',
          scrambledLetters: ['U', 'S', 'E', 'E', 'Q', 'I', 'R', 'O'],
          cipherClue: 'S E _ U E I R O — El refugio donde el humo y la castaña obran el milagro del invierno.',
          generalHint: 'Ocho letras: empieza por S, contiene QUEI y concluye en RO.',
          characterClues: {
            lobishome: 'El Lobishome gruñe: «En el sequeiro el humo caliente aleja el frío de la nieve y el hambre del invierno.»',
            trasno: 'El Trasno ríe: «¡Yo siempre robo castañas asadas del SEQUEIRO cuando nadie mira!»',
          },
          solutionExplanation: 'El SEQUEIRO es el monumento humilde y vital donde la castaña se transformaba en el pan de los pueblos de montaña.',
          magicalReward: 'Castaña de Plata y Pizarra de Seceda.',
        },
        audioDuration: '3:10',
        ambientSoundName: 'vento_courel_soutos',
        coordinates: { lat: 42.615, lng: -7.142, x: 82, y: 48 },
      },
      {
        id: 'stop-sc2',
        order: 2,
        title: 'Devesa da Rogueira y las Fuentes Gemelas',
        subtitle: 'La fuente roja de sangre y la fuente blanca de leche',
        locationName: 'Pico de Montouto, O Courel',
        story:
          'En este bosque prodigioso crecen 21 especies de árboles diferentes. En la parte alta brotan dos fuentes a escasos metros: una teñida de rojo sangre por el hierro y otra blanca calcárea. Los druidas las consideraban los dos pechos nutricios de la madre Tierra.',
        historicalNote:
          'La Devesa acoge más del 80% de las especies vegetales catalogadas en toda Galicia en un valle glaciar único.',
        enigma: {
          question: '¿Qué dos colores tienen las aguas de las dos fuentes gemelas de Camporramiro?',
          hint: 'Una tiene hierro y la otra calcio.',
          answer: 'Una roja ferruginosa y la otra blanca calcárea.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Acertijo de las Dos Venas de la Tierra',
          loreContext: 'Junto a las dos aguas mágicas de la Rogueira, la sierra te pregunta por el misterio mineral.',
          question: '«Brotamos de la misma roca a un palmo de distancia: mi hermana tiñe la hierba de rojo hierro, y yo hago blanquear la piedra como la leche. ¿Qué minerales nos dan vida?»',
          options: [
            'El Hierro rojo y el Calcio blanco',
            'El Oro brillante y el Cobre oscuro',
            'La Plata lunar y el Plomo gris',
            'El Azufre amarillo y el Granito azul',
          ],
          correctOptionIndex: 0,
          generalHint: 'Un elemento tiñe de óxido rojo y el otro deposita carbonato blanco.',
          characterClues: {
            lobishome: 'El Lobishome baja a beber: «Una huele a sangre de hierro, la otra a leche de piedra calcárea.»',
            meiga: 'La Meiga recoge el agua: «El agua ferruginosa da fuerza al corazón y la calcárea sanaba los huesos quebrados.»',
          },
          solutionExplanation: 'Las dos fuentes gemelas de la Rogueira ofrecen agua ferruginosa y calcárea que los antiguos consideraban medicinal.',
          magicalReward: 'Gota Doble de las Fuentes de la Rogueira.',
        },
        audioDuration: '3:35',
        ambientSoundName: 'fontes_rogueira',
        coordinates: { lat: 42.593, lng: -7.124, x: 88, y: 52 },
      },
    ],
  },
  {
    id: 'santiago-cidade',
    title: 'La Sombra de la Quintana y los Siete Secretos Alquímicos',
    subtitle: 'Campanas Espectrales de la Berenguela, Criptas Ocultas y Amor Eterno de Granito',
    description:
      'Descubre los rincones prohibidos y las leyendas alquímicas de la capital espiritual de Galicia. Donde las piedras de granito lloran con la lluvia, la campana Berenguela espanta a la Santa Compaña y la sombra misteriosa aguarda cada noche junto a la Puerta Santa.',
    location: 'Santiago de Compostela (A Coruña)',
    region: 'SANTIAGO DE COMPOSTELA · CIUDAD HISTÓRICA (A CORUÑA)',
    image: HERO_SANTIAGO_URL,
    isFeatured: true,
    stopsCount: 7,
    duration: '1h 45 min',
    distance: '3,2 km',
    difficulty: 'Fácil',
    categories: ['historia', 'leyendas', 'patrimonio'],
    audioTag: 'Crónica urbana mágica',
    badgeTone: 'mist',
    tomoRef: 'Códice Oculto de Compostela · Tomo V',
    price: '2,99 €',
    isFree: false,
    guide: MOCK_GUIDES[0], // Ana Rodríguez
    status: 'publicada',
    viewsCount: 2890,
    startedCount: 540,
    revenue: 1614.60,
    overviewFeatures: [
      {
        icon: 'wb_shade',
        title: 'La Sombra del Peregrino de la Quintana',
        description:
          'La legendaria silueta nocturna proyectada en la Plaza de la Quintana junto a la Puerta Santa: un amor que desafía al tiempo.',
        accentColor: 'secondary',
      },
      {
        icon: 'notifications_active',
        title: 'La Berenguela y el Pórtico de la Gloria',
        description:
          'La campana de diez toneladas cuyo tañido grave se escucha a 10 km y los ángeles de piedra del Maestro Mateo.',
        accentColor: 'primary',
      },
      {
        icon: 'church',
        title: 'Criptas y Callejuelas de los Alquimistas',
        description:
          'Los pasadizos secretos de San Martín Pinario y las boticas medievales donde se preparaba el elixir de Santiago.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-st1',
        order: 1,
        title: 'Plaza de la Quintana y la Sombra del Peregrino',
        subtitle: 'Quintana de Muertos y el amante fiel que aguarda desde el siglo XVIII',
        locationName: 'Plaza de la Quintana, Santiago',
        story:
          'Cuando cae la noche sobre Compostela y se enciende el farol de la Quintana, en la pared de granito de la catedral se proyecta la silueta de un peregrino con bordón y sombrero. La leyenda cuenta que es un sacerdote de la catedral que aguarda eternamente por su amor, una monja de San Paio.',
        historicalNote:
          'La plaza está dividida en dos partes: Quintana de Vivos arriba y Quintana de Muertos abajo, sobre el camposanto medieval de la ciudad.',
        enigma: {
          question: '¿Qué elemento de la catedral genera la sombra proyectada en la esquina de la torre de la Berenguela?',
          hint: 'Un soporte metálico de un pararrayos o columna.',
          answer: 'La columna de granito y el hierro del pararrayos bajo la luz del farol.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado de la Plaza de los Muertos y Vivos',
          loreContext: 'El granito mojado de la plaza más misteriosa de Galicia oculta el nombre del camposanto sagrado.',
          question: 'Ordena las letras del nombre de la plaza donde la sombra monta guardia:',
          cipherWord: 'QUINTANA',
          scrambledLetters: ['N', 'Q', 'U', 'I', 'A', 'T', 'A', 'N'],
          cipherClue: 'Q U _ N T A N A — Donde los muertos descansan y los peregrinos velan.',
          generalHint: 'Ocho letras: comienza con Q y tiene tres letras A.',
          characterClues: {
            caminante: 'El Caminante se reverencia: «En la Quintana de Muertos, la sombra del viajero nunca abandona su puesto.»',
            meiga: 'La Meiga ve la sombra: «¡Q-U-I-N-T-A-N-A! El farol enciende el alma del peregrino.»',
          },
          solutionExplanation: 'La QUINTANA es el corazón de granito donde la leyenda y la arquitectura crean la sombra más famosa de Galicia.',
          magicalReward: 'Llave de la Sombra de la Quintana.',
        },
        audioDuration: '2:55',
        ambientSoundName: 'choiva_santiago_quintana',
        coordinates: { lat: 42.8805, lng: -8.5442, x: 38, y: 48 },
      },
      {
        id: 'stop-st2',
        order: 2,
        title: 'Plaza del Obradoiro y el Pórtico de los Ángeles',
        subtitle: 'La piedra filosofal del Maestro Mateo y la campana Berenguela',
        locationName: 'Plaza del Obradoiro, Santiago',
        story:
          'El Obradoiro es el núcleo del mundo jacobeo. Aquí el Maestro Mateo labró en el Pórtico de la Gloria más de doscientos personajes de piedra que parecen conversar y tañer instrumentos medievales en polifonía eterna.',
        historicalNote:
          'La campana Berenguela, fundida en 1729, pesa 9.600 kilos y marca los cuartos y horas con un tañido profundo inconfundible.',
        enigma: {
          question: '¿Qué losa en el centro del Obradoiro marca el kilómetro cero de todos los Caminos?',
          hint: 'Marca las coordenadas en el pavimento.',
          answer: 'La placa de granito conmemorativa Patrimonio de la Humanidad.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'La Alineación de los Cuatro Poderes del Obradoiro',
          loreContext: 'Cuatro colosos de granito presiden la gran plaza. Alinea sus símbolos para sintonizar la fuerza de la ciudad.',
          question: 'Ordena los poderes: desde lo espiritual hasta el saber universitario.',
          runes: [
            { id: 'catedral', glyph: '⛪', name: 'Catedral Jacobea', desc: 'Poder espiritual y meta del Camino' },
            { id: 'hostal', glyph: '🏨', name: 'Hostal de los Reyes', desc: 'Hospitalidad y medicina de peregrinos' },
            { id: 'raxoi', glyph: '🏛️', name: 'Pazo de Raxoi', desc: 'Gobierno y justicia de la tierra' },
            { id: 'xerome', glyph: '📚', name: 'Colegio San Jerónimo', desc: 'Saber y letras universales' },
          ],
          correctRuneOrder: ['catedral', 'hostal', 'raxoi', 'xerome'],
          generalHint: 'Primero la Catedral frente a nosotros, después el Hostal a la izquierda, el Palacio enfrente y el saber a la derecha.',
          characterClues: {
            caminante: 'El Caminante guía: «La Catedral acoge el alma, el Hostal cuida los pies cansados, Raxoi rige la villa y San Jerónimo enseña.»',
            moura: 'La Moura admira la cantería: «Catedral, hospital real, ayuntamiento y colegio... el cuadrado mágico de Santiago.»',
          },
          solutionExplanation: 'La Catedral, el Hostal de los Reyes Católicos, el Pazo de Raxoi y el Colegio de San Jerónimo delimitan el epicentro espiritual de Galicia.',
          magicalReward: 'Estrella de Oro del Obradoiro.',
        },
        audioDuration: '3:25',
        ambientSoundName: 'campas_berenguela',
        coordinates: { lat: 42.8808, lng: -8.5456, x: 36, y: 47 },
      },
    ],
  },
  {
    id: 'rias-baixas',
    title: 'El Tesoro de la Moura y los Hórreos del Mar',
    subtitle: 'Islas de los Dioses, Galeones Hundidos de Rande y Cantigas de las Sirenas',
    description:
      'Navega por las aguas templadas de las Rías Baixas, donde los hórreos de Combarro tocan las olas en marea viva, las Islas Cíes guardan el descanso de los dioses antiguos y la batalla de Rande hundió millones en plata bajo las bateas.',
    location: 'Combarro y Vigo (Pontevedra)',
    region: 'RÍAS BAIXAS · RÍA DE PONTEVEDRA Y RÍA DE VIGO (PONTEVEDRA)',
    image: HERO_COAST_URL,
    isFeatured: false,
    stopsCount: 6,
    duration: '2h',
    distance: '3,8 km',
    difficulty: 'Fácil',
    categories: ['familias', 'patrimonio', 'naturaleza'],
    audioTag: 'Brisa marinera',
    badgeTone: 'secondary',
    tomoRef: 'Leyendas de la Costa Salada · Tomo VI',
    price: 'Gratis',
    isFree: true,
    guide: MOCK_GUIDES[0], // Ana Rodríguez
    status: 'publicada',
    viewsCount: 1780,
    startedCount: 460,
    revenue: 0,
    overviewFeatures: [
      {
        icon: 'house',
        title: 'Hórreos marineros de Combarro',
        description:
          'Más de 30 hórreos de granito construidos a la orilla del mar para guardar el maíz a salvo de los roedores y de la marea.',
        accentColor: 'primary',
      },
      {
        icon: 'wb_sunny',
        title: 'Islas Cíes (Insulae Deorum)',
        description:
          'La playa de Rodas y las leyendas de sirenas que guiaban a los marineros a través de las brumas de la ensenada.',
        accentColor: 'secondary',
      },
      {
        icon: 'sailing',
        title: 'El oro hundido de Rande',
        description:
          'La batalla naval de 1702 donde los galeones españoles cargados de plata americana yacen bajo el lodo de la ría.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-rb1',
        order: 1,
        title: 'Hórreos de Granito y Cruceros de Combarro',
        subtitle: 'Las despensas pétreas que besan la marea alta',
        locationName: 'Combarro, Poio (Pontevedra)',
        story:
          'En Combarro los hórreos se alinean directamente sobre las rocas de la ría. Los campesinos marineros desembarcaban las espigas de maíz directamente desde sus chalanas en las mareas altas de otoño, mientras los cruceros velaban por la tripulación.',
        historicalNote:
          'Combarro reúne la mayor densidad de hórreos y cruceros marineros de toda Galicia, datando la mayoría de los siglos XVIII y XIX.',
        enigma: {
          question: '¿Hacia dónde miran las figuras de la Virgen esculpidas en los cruceros de la villa de Combarro?',
          hint: 'Miran hacia donde está la protección de los que faenan.',
          answer: 'Siempre miran hacia el mar para proteger a los marineros.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Enigma de los Ojos de los Cruceros Marineros',
          loreContext: 'Por la rúa da Rúa de Combarro, entre el salitre y el granito, los cruceros guardan un voto sagrado.',
          question: '«En el crucero de piedra el Cristo mira hacia la tierra para velar por los campesinos. ¿Hacia dónde mira la Virgen del Socorro en sus espaldas?»',
          options: [
            'Siempre hacia el mar, para amparar a los marineros en las faenas',
            'Hacia el norte, huyendo de los vientos del sur',
            'Hacia el campanario de la iglesia parroquial',
            'Hacia el monte Castrove donde duermen las leyendas',
          ],
          correctOptionIndex: 0,
          generalHint: 'La Virgen es la patrona de los mareantes y fija sus ojos en las olas de la ría.',
          characterClues: {
            lavandeira: 'La Lavandeira mira a la ría: «La Virgen mira las aguas saladas para que ningún barco se quede sin regresar a puerto.»',
            moura: 'La Moura confirma: «Cristo a la tierra y la Señora al mar: doble custodia para la villa.»',
          },
          solutionExplanation: 'En Combarro, los cruceros tienen la doble protección: el Cristo hacia las huertas y la Virgen contemplando permanentemente las aguas de la ría.',
          magicalReward: 'Crucero de Coral y Granito de Combarro.',
        },
        audioDuration: '2:35',
        ambientSoundName: 'mar_combarro_mareas',
        coordinates: { lat: 42.4312, lng: -8.7042, x: 26, y: 72 },
      },
      {
        id: 'stop-rb2',
        order: 2,
        title: 'Islas Cíes y la Leyenda de los Galeones de Rande',
        subtitle: 'La ensenada de los dioses romanos y la plata sumergida',
        locationName: 'Islas Cíes, Vigo',
        story:
          'Plinio el Viejo bautizó este archipiélago como «Insulae Deorum». En el fondo de la ría, en la ensenada de San Simón, duermen los galeones hundidos en la batalla de Rande de 1702, cuyos cañones y lingotes de plata aún alimentan las fantasías de los buscadores de tesoros.',
        historicalNote:
          'Declaradas Parque Natural en 1980 y hoy parte del Parque Nacional Marítimo-Terrestre de las Islas Atlánticas de Galicia.',
        enigma: {
          question: '¿Qué laguna salada conecta las islas de Monteagudo y de O Faro formando el arenal de Rodas?',
          hint: 'La Laguna de los Niños (Lagoa dos Nenos).',
          answer: 'La Laguna de los Niños.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Nombre Latino de las Islas de los Dioses',
          loreContext: 'Los geógrafos de Roma grabaron en latín el carácter sagrado del archipiélago de las Cíes:',
          question: 'Descifra la palabra que acompañaba a «Insulae» para nombrar a las islas divinas:',
          cipherWord: 'DEORUM',
          scrambledLetters: ['R', 'D', 'E', 'U', 'M', 'O'],
          cipherClue: 'D E _ R U M — La morada de los dioses del océano.',
          generalHint: 'Seis letras latinas: comienza por D y concluye por UM.',
          characterClues: {
            moura: 'La Moura relata: «Los soldados de César las llamaban Insulae DEORUM por su luz cristalina.»',
            trasno: 'El Trasno juega con caracolas: «¡D-E-O-R-U-M, dioses del mar!»',
          },
          solutionExplanation: 'INSULAE DEORUM (Islas de los Dioses) fue el nombre con el que el mundo clásico reconoció la belleza incontestable de las Islas Cíes.',
          magicalReward: 'Concha Dorada de las Insulae Deorum.',
        },
        audioDuration: '3:15',
        ambientSoundName: 'ondas_cies',
        coordinates: { lat: 42.2285, lng: -8.904, x: 20, y: 80 },
      },
    ],
  },
  {
    id: 'baixo-mino',
    title: 'El Trisquel de Oro y el Vigilante de los Castros Atlánticos',
    subtitle: 'Santa Trega, Petroglifos Solsticiales y el Abrazo Sagrado del Río Miño',
    description:
      'Desde la cumbre más mítica del sur de Galicia, contempla la desembocadura donde el padre Miño se funde con el Atlántico y camina por las cabañas circulares de la mayor ciudad castreña de la Edad del Hierro galaica.',
    location: 'A Guarda (Pontevedra)',
    region: 'BAJO MIÑO · A GUARDA Y SANTA TREGA (PONTEVEDRA)',
    image: HERO_CONTRABANDO_URL,
    isFeatured: false,
    stopsCount: 6,
    duration: '2h',
    distance: '4,1 km',
    difficulty: 'Media',
    categories: ['historia', 'patrimonio', 'naturaleza'],
    audioTag: 'Cultura castreña',
    badgeTone: 'tertiary',
    tomoRef: 'Memoria Telúrica Castreña · Tomo VII',
    price: '1,99 €',
    isFree: false,
    guide: MOCK_GUIDES[1], // Marcos Varela
    status: 'publicada',
    viewsCount: 1340,
    startedCount: 290,
    revenue: 577.10,
    overviewFeatures: [
      {
        icon: 'fort',
        title: 'Ciudad Castreña de Santa Trega',
        description:
          'Una de las mayores metrópolis castreñas de Europa, con cientos de cabañas circulares de cantería.',
        accentColor: 'primary',
      },
      {
        icon: 'brush',
        title: 'Petroglifos y el Trisquel Sagrado',
        description:
          'Espirales, laberintos y el gran trisquel grabado en el granito hace más de dos mil años mirando hacia el sol.',
        accentColor: 'secondary',
      },
      {
        icon: 'water',
        title: 'Desembocadura del Padre Miño',
        description:
          'El punto exacto donde las aguas dulces del Miño besan el océano frente a la fortaleza portuguesa de la Ínsua.',
        accentColor: 'tertiary',
      },
    ],
    stops: [
      {
        id: 'stop-bt1',
        order: 1,
        title: 'Castro Galaico de Santa Trega y el Trisquel de Piedra',
        subtitle: 'La gran metrópoli castreña a 341 metros sobre las olas',
        locationName: 'Monte de Santa Trega, A Guarda',
        story:
          'Aquí vivieron más de cinco mil personas en el siglo I a.C. Molían el grano en molinos de mano, fundían bronce y esculpían trisqueles en la piedra para canalizar la energía del sol y de las mareas.',
        historicalNote:
          'Descubierto en 1913 durante la construcción de la carretera de subida a la cumbre del monte.',
        enigma: {
          question: '¿Qué grabado rupestre marca la losa delante de la cabaña reconstruida?',
          hint: 'Un trisquel o espiral de tres brazos simétricos.',
          answer: 'El trisquel sagrado de Santa Trega.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'El Puzle de los Tres Brazos del Trisquel Castreño',
          loreContext: 'El trisquel milenario de Santa Trega representa los tres elementos cósmicos de la cultura galaico-céltica.',
          question: 'Alinea los tres principios sagrados del trisquel de los castros:',
          runes: [
            { id: 'auga', glyph: '🌊', name: 'Agua del Miño', desc: 'El fluir de la vida y de las mareas' },
            { id: 'terra', glyph: '⛰️', name: 'Tierra de Granito', desc: 'La firmeza del castro y de los muros' },
            { id: 'aire', glyph: '💨', name: 'Viento Atlántico', desc: 'El espíritu libre de los antepasados' },
            { id: 'sol', glyph: '☀️', name: 'Solsticio Dorado', desc: 'La luz que orienta los petroglifos' },
          ],
          correctRuneOrder: ['auga', 'terra', 'aire', 'sol'],
          generalHint: 'El agua dulce del río abraza la tierra del castro, el viento bate en las piedras y el sol del solsticio corona el monte.',
          characterClues: {
            moura: 'La Moura alza la mano: «El trisquel gira con el agua, la tierra y el viento bajo la mirada del sol dorado.»',
            trasno: 'El Trasno gira sobre un pie: «¡🌊 ⛰️ 💨 ☀️ y el trisquel empieza a girar!»',
          },
          solutionExplanation: 'El trisquel de Santa Trega canaliza la armonía de los elementos de la naturaleza y la rotación solar sobre el estuario.',
          magicalReward: 'Trisquel de Oro de los Castros Atlánticos.',
        },
        audioDuration: '3:05',
        ambientSoundName: 'vento_santa_trega',
        coordinates: { lat: 41.895, lng: -8.868, x: 22, y: 88 },
      },
      {
        id: 'stop-bt2',
        order: 2,
        title: 'Mirador del Estuario y la Fortaleza de la Ínsua',
        subtitle: 'La barra del río Miño y la isla de los baluartes solitarios',
        locationName: 'Pico de San Francisco',
        story:
          'Desde el mirador se aprecia la famosa Ínsua con una fortaleza medieval en medio de las olas bravas del océano, y el cordón dunar de la playa de O Muíño donde las aguas del Miño se funden con el Atlántico en un abrazo infinito.',
        historicalNote:
          'El estuario es Lugar de Interés Comunitario y Zona de Especial Protección para las Aves (ZEPA).',
        enigma: {
          question: '¿Qué fortaleza rodeada completamente por el mar se divisa en la desembocadura del río Miño?',
          hint: 'El Fuerte de la Ínsua.',
          answer: 'El Fuerte de la Ínsua.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Enigma del Castillo de en Medio del Mar',
          loreContext: 'Mirando hacia el límite donde concluye Galicia y comienza el mar fronterizo, la niebla descubre este misterio marítimo.',
          question: '«Estoy rodeado de agua salada por los cuatro costados, guardo murallas y un faro sobre la roca; nadie puede llegar a mí sin barco. ¿Qué fortaleza soy?»',
          options: [
            'El Fuerte de la Ínsua, aislado en la desembocadura del Miño',
            'El Castillo de San Lorenzo de Goián',
            'La Fortaleza de Valença do Minho',
            'La Torre defensiva de A Guarda',
          ],
          correctOptionIndex: 0,
          generalHint: 'Es la única fortaleza insular del estuario del Miño, construida sobre un islote rocoso batido por las olas.',
          characterClues: {
            lavandeira: 'La Lavandeira escucha las olas: «¡La Ínsua! Donde las olas besan las murallas de piedra en medio del mar.»',
            caminante: 'El Caminante señala el horizonte: «El Fuerte de la Ínsua, centinela solitario de la desembocadura.»',
          },
          solutionExplanation: 'El Fuerte de la Ínsua es un enclave histórico erigido en un islote en la desembocadura del Miño que protegió el estuario fronterizo durante siglos.',
          magicalReward: 'Sello de la Raya Fluvial del Miño.',
        },
        audioDuration: '2:45',
        ambientSoundName: 'mar_foz_mino',
        coordinates: { lat: 41.892, lng: -8.863, x: 25, y: 92 },
      },
    ],
  },
];
