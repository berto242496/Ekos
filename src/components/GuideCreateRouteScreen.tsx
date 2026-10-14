import React, { useState, useRef, useEffect } from 'react';
import {
  RouteItem,
  RouteStop,
  GuideAuthor,
  RoutePrice,
  Category,
  VoicePersonaId,
  StopTrial,
  RuneGlyph,
} from '../types';
import { soundEngine, VOICE_PERSONAS } from '../services/soundEngine';
import { useI18n } from '../services/i18n';
import { LanguageSelector } from './LanguageSelector';
import {
  ChevronLeft,
  Plus,
  Trash2,
  MapPin,
  Clock,
  Sparkles,
  HelpCircle,
  FileEdit,
  Send,
  AlertCircle,
  Volume2,
  Mic,
  Square,
  Play,
  Upload,
  Link as LinkIcon,
  Wand2,
  KeyRound,
  Check,
  Headphones,
} from 'lucide-react';

interface GuideCreateRouteScreenProps {
  currentGuide: GuideAuthor;
  onSaveDraft: (route: RouteItem) => void;
  onPublishRoute: (route: RouteItem) => void;
  onCancel: () => void;
}

// Pre-curated high-resolution Galician photography presets
const GALICIAN_IMAGE_PRESETS = [
  {
    id: 'ponte-mino',
    name: 'Puente Romano y Río Miño',
    tag: 'Ríos y Puentes',
    url: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'muralla-fortaleza',
    name: 'Muralla y Fortaleza Histórica',
    tag: 'Fortalezas',
    url: 'https://images.unsplash.com/photo-1513584684374-8bab748fbf90?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'costa-faros',
    name: 'Costa Ártabra y Faros Bravos',
    tag: 'Costa Atlántica',
    url: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'fragas-carballeira',
    name: 'Bosque Mágico y Robledal',
    tag: 'Bosques',
    url: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'ribeira-sacra',
    name: 'Cañones del Sil y Viñedos',
    tag: 'Ribeira Sacra',
    url: 'https://images.unsplash.com/photo-1511497584788-87676104235f?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'castro-petroglifos',
    name: 'Castro Celta y Petroglifos',
    tag: 'Arqueología',
    url: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'casco-vello',
    name: 'Casco Histórico y Calles de Granito',
    tag: 'Patrimonio',
    url: 'https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=1200&q=80',
  },
  {
    id: 'pazo-horreo',
    name: 'Pazo Tradicional y Hórreo',
    tag: 'Tradición',
    url: 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1200&q=80',
  },
];

// Quick sites suggestions (in Spanish)
const QUICK_SITES = [
  'Tui / Valença do Minho',
  'Santiago de Compostela',
  'Ribeira Sacra / Cañones del Sil',
  'Costa da Morte / Fisterra',
  'Combarro / Rías Baixas',
  'Allariz / Río Arnoia',
  'Baiona / Val Miñor',
  'Mondoñedo / Mariña Lucense',
];

// Rune glyph options for ancient rune puzzle games
const AVAILABLE_RUNES: RuneGlyph[] = [
  { id: 'trisquel', glyph: '𖥑', name: 'Trisquel Celta', desc: 'Espiral triple de agua, fuego y tierra' },
  { id: 'espiral', glyph: '🌀', name: 'Espiral de Mogor', desc: 'Laberinto y renacimiento solar' },
  { id: 'labirinto', glyph: '𖤓', name: 'Laberinto de Meis', desc: 'Camino interior de los antepasados' },
  { id: 'flor_vida', glyph: '💮', name: 'Flor del Agua', desc: 'Geometría sagrada de los manantiales' },
  { id: 'sol', glyph: '☀️', name: 'Sol Petroglífico', desc: 'Luz radiante grabada en el granito' },
  { id: 'cervo', glyph: '🦌', name: 'Ciervo Sagrado', desc: 'Guía protector de los bosques atlánticos' },
];

// Starter Route Templates (all in Spanish by default)
interface RouteTemplatePreset {
  id: string;
  name: string;
  badge: string;
  tagline: string;
  icon: string;
  location: string;
  title: string;
  description: string;
  category: Category;
  duration: string;
  difficulty: 'Fácil' | 'Media' | 'Exigente';
  price: RoutePrice;
  coverImage: string;
  defaultVoiceId: VoicePersonaId;
  defaultAudioMode: 'tts' | 'recorded' | 'written';
  stops: RouteStop[];
}

const TEMPLATE_PRESETS: RouteTemplatePreset[] = [
  {
    id: 'tpl-lendas',
    name: 'Ruta de Leyendas y Misterios',
    badge: 'Mitología Celta',
    tagline: 'Ideal para enigmas de piedras sagradas, mouras y tradición oral',
    icon: '⚡',
    title: 'Misterios y Petroglifos de la Costa Gallega',
    location: 'Costa da Morte / Fisterra',
    description:
      'Un viaje iniciático a través de leyendas marineras, piedras con propiedades curativas y vestigios arqueológicos esculpidos por el viento atlántico.',
    category: 'leyendas',
    duration: '1h 45 min',
    difficulty: 'Media',
    price: '1,99 €',
    coverImage: GALICIAN_IMAGE_PRESETS[2].url,
    defaultVoiceId: 'breogan',
    defaultAudioMode: 'tts',
    stops: [
      {
        id: 'tpl-lendas-s1',
        order: 1,
        title: 'La Piedra de los Riñones y el Mar Bravo',
        subtitle: 'Santuario ancestral de curación y leyendas',
        locationName: 'Punta de la Barca, Costa da Morte',
        imageUrl: GALICIAN_IMAGE_PRESETS[2].url,
        story:
          'Cuenta la tradición que quien pasa nueve veces bajo esta roca milenaria libera el cuerpo de dolores y renueva el espíritu. Las olas rompen con fuerza sagrada.',
        historicalNote:
          'Los cultos litolátricos en estos acantilados tienen raíces célticas prerromanas cristianizadas en la época medieval.',
        audioNarration:
          'Delante de ti se encuentra la mítica roca legendaria. Escucha el rugir del océano y respira la bruma salada antes de resolver su enigma.',
        voiceId: 'breogan',
        audioMode: 'tts',
        audioDuration: '2:45',
        coordinates: { lat: 43.11, lng: -9.28, x: 25, y: 35 },
        enigma: {
          question: '¿Cuál es el ritual milenario asociado a esta roca de granito?',
          hint: 'Cuenta los pasos bajo la roca con respeto a los antiguos.',
          answer: 'Pasar nueve veces por debajo de ella para lograr la curación.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Cifrado de la Moura Marinera',
          loreContext:
            'La guardiana de las aguas escondió el nombre de la criatura protectora en las letras desordenadas.',
          question: 'Ordena las letras para descubrir la palabra mágica grabada en el granito:',
          cipherWord: 'MOURA',
          scrambledLetters: ['U', 'R', 'A', 'M', 'O'],
          cipherClue: 'Espíritu encantado que peina sus cabellos de oro junto al mar.',
          generalHint: 'Comienza por la letra M y termina con la A.',
          characterClues: {
            meiga: 'El caldero hierve: la piedra guarda la energía de la antigua Moura.',
            moura: '¡Yo misma grabé ese nombre en la caliza hace dos mil inviernos!',
          },
          solutionExplanation: 'La Moura es el símbolo del encanto eterno y de las piedras vivas gallegas.',
          magicalReward: 'Runa de Granito y Agua Salada desbloqueada.',
        },
      },
      {
        id: 'tpl-lendas-s2',
        order: 2,
        title: 'El Faro del Fin del Mundo',
        subtitle: 'Último rayo de sol sobre el Finisterre mítico',
        locationName: 'Cabo Fisterra',
        imageUrl: GALICIAN_IMAGE_PRESETS[2].url,
        story:
          'Aquí donde los antiguos romanos temían que el sol se apagase en las aguas hirvientes del abismo oceánico.',
        historicalNote:
          'El Ara Solis era el antiguo altar dedicado al astro rey antes de la construcción del faro actual en 1853.',
        audioNarration:
          'Llegamos al cabo donde la tierra se acaba. Las gaviotas trazan círculos sobre el abismo azul.',
        voiceId: 'breogan',
        audioMode: 'tts',
        audioDuration: '3:10',
        coordinates: { lat: 42.88, lng: -9.27, x: 20, y: 55 },
        enigma: {
          question: '¿Qué deidad solar veneraban los pueblos galaicos en esta punta?',
          hint: 'El altar sagrado recibía el nombre latino de Ara...',
          answer: 'El Dios Sol en el altar Ara Solis.',
        },
        trial: {
          type: 'puzle_runico',
          title: 'Alineación de los Glifos del Ocaso',
          loreContext:
            'Para abrir la crónica del faro, debes activar las tres runas en el orden de la energía solar.',
          question: 'Toca los glifos en el orden solar ancestral:',
          runes: [AVAILABLE_RUNES[0], AVAILABLE_RUNES[1], AVAILABLE_RUNES[4]],
          correctRuneOrder: ['trisquel', 'espiral', 'sol'],
          generalHint: 'Primero el Trisquel del movimiento, después la Espiral y termina en el Sol.',
          characterClues: {
            meiga: 'El movimiento espiralado conduce la fuerza hacia el astro rey.',
          },
          solutionExplanation: 'La armonía solar ha quedado restablecida sobre el promontorio de Fisterra.',
          magicalReward: 'Antorcha de Luz Guía para el Cuaderno.',
        },
      },
    ],
  },
  {
    id: 'tpl-historia',
    name: 'Ruta Histórica de Murallas y Fortalezas',
    badge: 'Patrimonio & Fortaleza',
    tagline: 'Ideal para cascos históricos, castillos, murallas y puentes medievales',
    icon: '🏰',
    title: 'La Puerta Real y los Secretos de la Frontera del Miño',
    location: 'Tui / Valença do Minho',
    description:
      'Un itinerario patrimonial riguroso a través de puertas centenarias, baluartes militares fronterizos y el paso sobre el río Miño.',
    category: 'historia',
    duration: '1h 30 min',
    difficulty: 'Fácil',
    price: '2,99 €',
    coverImage: GALICIAN_IMAGE_PRESETS[1].url,
    defaultVoiceId: 'xacobe',
    defaultAudioMode: 'tts',
    stops: [
      {
        id: 'tpl-historia-s1',
        order: 1,
        title: 'Puerta de la Pía y Atrio Catedralicio',
        subtitle: 'La entrada noble a la ciudad amurallada',
        locationName: 'Catedral de Santa María de Tui',
        imageUrl: GALICIAN_IMAGE_PRESETS[1].url,
        story:
          'Una de las joyas del gótico gallego con aspecto de fortaleza defensiva vigilando el cauce internacional del río.',
        historicalNote:
          'Construida entre los siglos XII y XIII, combina elementos románicos con el primer pórtico gótico de la Península.',
        audioNarration:
          'Observa el perfil almenado de esta catedral única, erigida como castillo sagrado frente a la orilla portuguesa.',
        voiceId: 'xacobe',
        audioMode: 'tts',
        audioDuration: '2:50',
        coordinates: { lat: 42.04, lng: -8.64, x: 45, y: 50 },
        enigma: {
          question: '¿Por qué la catedral tiene almenas como si fuese una fortaleza militar?',
          hint: 'Su situación estratégica en la frontera del reino exigía capacidad defensiva.',
          answer: 'Porque cumplía una doble función de templo y fortaleza fronteriza.',
        },
        trial: {
          type: 'acertijo',
          title: 'El Desafío del Pórtico Gótico',
          loreContext:
            'El cronista de la catedral te desafía a identificar la iconografía esculpida en el tímpano principal.',
          question: '¿Qué escena está representada en la clave superior del tímpano?',
          options: [
            'La Adoración de los Reyes Magos',
            'El Juicio Final con el pesado de almas',
            'La Batalla del Miño entre reyes vecinos',
            'La leyenda de la Barca de Piedra',
          ],
          correctOptionIndex: 0,
          generalHint: 'Fíjate en las tres figuras coronadas que portan presentes.',
          characterClues: {
            caminante: '¡En mis notas de viaje recuerdo claramente a los tres monarcas de Oriente!',
          },
          solutionExplanation:
            'La Adoración de los Reyes Magos de Tui es una de las obras cumbres de la escultura gótica.',
          magicalReward: 'Sello Medieval de Santa María.',
        },
      },
    ],
  },
  {
    id: 'tpl-familias',
    name: 'Senda Familiar de Bosques Mágicos',
    badge: 'Aventura Familiar',
    tagline: 'Ruta divertida con pistas didácticas y retos accesibles para todas las edades',
    icon: '🌿',
    title: 'El Enigma del Trasno en los Bosques del Río',
    location: 'Fragas do Eume / Bosque Atlántico',
    description:
      'Un juego de exploración en la naturaleza más exuberante de Galicia, descubriendo árboles centenarios, puentes colgantes y seres mitológicos traviesos.',
    category: 'familias',
    duration: '1h 15 min',
    difficulty: 'Fácil',
    price: '0,99 €',
    coverImage: GALICIAN_IMAGE_PRESETS[3].url,
    defaultVoiceId: 'iria',
    defaultAudioMode: 'tts',
    stops: [
      {
        id: 'tpl-familias-s1',
        order: 1,
        title: 'El Puente de los Helechos y el Río Murmurante',
        subtitle: 'Punto de encuentro de los seres del bosque',
        locationName: 'Puente de Cal Grande',
        imageUrl: GALICIAN_IMAGE_PRESETS[3].url,
        story:
          'El bosque atlántico conserva aquí helechos gigantes milenarios. Se dice que los trasnos esconden castañas bajo las piedras del río.',
        historicalNote:
          'Las Fragas do Eume son uno de los bosques caducifolios costeros mejor conservados de Europa.',
        audioNarration:
          '¡Bienvenidos pequeños y grandes exploradores! ¿Escucháis el murmullo del río? ¡Aquí comienza nuestra búsqueda del Trasno!',
        voiceId: 'iria',
        audioMode: 'tts',
        audioDuration: '2:15',
        coordinates: { lat: 43.41, lng: -8.08, x: 60, y: 25 },
        enigma: {
          question: '¿Qué árbol milenario es el auténtico rey de estos bosques?',
          hint: 'Da bellotas y madera noble muy fuerte.',
          answer: 'El roble atlántico.',
        },
        trial: {
          type: 'cifrado_magico',
          title: 'El Nombre del Duendecillo Burlón',
          loreContext: 'El trasno dejó sus letras desordenadas en una hoja de helecho.',
          question: 'Descifra el nombre del ser travieso que mueve las cosas de tu bolsillo:',
          cipherWord: 'TRASNO',
          scrambledLetters: ['A', 'S', 'T', 'O', 'R', 'N'],
          cipherClue: '¡Lleva gorro rojo y le echa la culpa a los demás!',
          generalHint: 'Comienza por TR...',
          characterClues: {
            trasno: '¡Soy yo mismo! ¡A ver si eres capaz de escribir mi nombre sin tropezar!',
          },
          solutionExplanation: '¡Descubriste al Trasno burlón! El bosque ahora sonríe.',
          magicalReward: 'Gorro Rojo de los Bosques.',
        },
      },
    ],
  },
];

export const GuideCreateRouteScreen: React.FC<GuideCreateRouteScreenProps> = ({
  currentGuide,
  onSaveDraft,
  onPublishRoute,
  onCancel,
}) => {
  const { t } = useI18n();

  // Current active template key
  const [selectedTemplateId, setSelectedTemplateId] = useState<string>('tpl-lendas');

  // Form State: Route Metadata (Spanish by default)
  const [title, setTitle] = useState(TEMPLATE_PRESETS[0].title);
  const [location, setLocation] = useState(TEMPLATE_PRESETS[0].location);
  const [description, setDescription] = useState(TEMPLATE_PRESETS[0].description);
  const [category, setCategory] = useState<Category>(TEMPLATE_PRESETS[0].category);
  const [duration, setDuration] = useState(TEMPLATE_PRESETS[0].duration);
  const [difficulty, setDifficulty] = useState<'Fácil' | 'Media' | 'Exigente'>(TEMPLATE_PRESETS[0].difficulty);
  const [price, setPrice] = useState<RoutePrice>(TEMPLATE_PRESETS[0].price);
  const [coverImage, setCoverImage] = useState(TEMPLATE_PRESETS[0].coverImage);

  // Global Route Voice & Audio Configuration
  const [defaultVoiceId, setDefaultVoiceId] = useState<VoicePersonaId>(TEMPLATE_PRESETS[0].defaultVoiceId);
  const [defaultAudioMode, setDefaultAudioMode] = useState<'tts' | 'recorded' | 'written'>('tts');

  // Stops State
  const [stops, setStops] = useState<RouteStop[]>(TEMPLATE_PRESETS[0].stops);

  // Stop Form State
  const [isAddingStop, setIsAddingStop] = useState(false);
  const [editingStopIndex, setEditingStopIndex] = useState<number | null>(null);

  // Form fields for current stop
  const [stopTitle, setStopTitle] = useState('');
  const [stopSubtitle, setStopSubtitle] = useState('');
  const [stopLocation, setStopLocation] = useState('');
  const [stopStory, setStopStory] = useState('');
  const [stopHistoricalNote, setStopHistoricalNote] = useState('');
  const [stopImage, setStopImage] = useState(GALICIAN_IMAGE_PRESETS[0].url);
  const [stopAudioMode, setStopAudioMode] = useState<'tts' | 'recorded' | 'written'>('tts');
  const [stopSpokenNarration, setStopSpokenNarration] = useState('');
  const [stopVoiceId, setStopVoiceId] = useState<VoicePersonaId>('breogan');

  // Recorded Audio State (using MediaRecorder in browser)
  const [isRecording, setIsRecording] = useState(false);
  const [recordingSeconds, setRecordingSeconds] = useState(0);
  const [recordedAudioBlobUrl, setRecordedAudioBlobUrl] = useState<string | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const recordingTimerRef = useRef<any>(null);

  // Game Selection: 'acertijo' | 'cifrado_magico' | 'puzle_runico' | 'enigma_simple'
  const [stopGameType, setStopGameType] = useState<
    'acertijo' | 'cifrado_magico' | 'puzle_runico' | 'enigma_simple'
  >('acertijo');

  // Acertijo fields
  const [acertijoQuestion, setAcertijoQuestion] = useState('¿Qué figura sagrada custodia la entrada de este lugar?');
  const [acertijoOptions, setAcertijoOptions] = useState<string[]>([
    'Un lobo alado con ojos de ámbar',
    'Una serpiente de agua bicéfala',
    'Un carnero de oro solar',
  ]);
  const [acertijoCorrectIndex, setAcertijoCorrectIndex] = useState(0);
  const [acertijoHint, setAcertijoHint] = useState('Fíjate en el dintel superior grabado en la roca.');

  // Cifrado Mágico fields
  const [cipherWord, setCipherWord] = useState('GRANITO');
  const [cipherClue, setCipherClue] = useState('La piedra con la que se erigieron nuestros castros y fortalezas.');
  const [cipherHint, setCipherHint] = useState('Comienza por la letra G.');

  // Puzle Rúnico fields
  const [selectedRuneIds, setSelectedRuneIds] = useState<string[]>(['trisquel', 'espiral', 'sol']);
  const [runePuzzleHint, setRunePuzzleHint] = useState('Sigue el orden de la fuerza solar sobre el granito.');

  // Enigma Simple fields
  const [simpleQuestion, setSimpleQuestion] = useState('¿Qué detalle insólito llama la atención en la parte alta?');
  const [simpleHint, setSimpleHint] = useState('Dirige la mirada hacia el arco izquierdo.');
  const [simpleAnswer, setSimpleAnswer] = useState('Una cruz grabada por los canteros medievales.');

  // Voice playback testing state
  const [isPlayingVoiceSample, setIsPlayingVoiceSample] = useState(false);
  const [playingVoiceId, setPlayingVoiceId] = useState<VoicePersonaId | null>(null);

  // Image mode tabs
  const [coverImageMode, setCoverImageMode] = useState<'presets' | 'upload' | 'url'>('presets');
  const [customCoverUrl, setCustomCoverUrl] = useState('');

  const [validationError, setValidationError] = useState<string | null>(null);

  // Handle template selection
  const handleSelectTemplate = (tplId: string) => {
    soundEngine.stopSpeech();
    setIsPlayingVoiceSample(false);
    setSelectedTemplateId(tplId);

    const tpl = TEMPLATE_PRESETS.find((t) => t.id === tplId);
    if (!tpl) return;

    setTitle(tpl.title);
    setLocation(tpl.location);
    setDescription(tpl.description);
    setCategory(tpl.category);
    setDuration(tpl.duration);
    setDifficulty(tpl.difficulty);
    setPrice(tpl.price);
    setCoverImage(tpl.coverImage);
    setDefaultVoiceId(tpl.defaultVoiceId);
    setDefaultAudioMode(tpl.defaultAudioMode);
    setStops(tpl.stops);
    setValidationError(null);
  };

  // Sound Engine clean up
  useEffect(() => {
    return () => {
      soundEngine.stopSpeech();
    };
  }, []);

  // Voice sample playback handler
  const handlePlayVoiceSample = (personaId: VoicePersonaId, customText?: string) => {
    if (isPlayingVoiceSample && playingVoiceId === personaId) {
      soundEngine.stopSpeech();
      setIsPlayingVoiceSample(false);
      setPlayingVoiceId(null);
      return;
    }

    soundEngine.stopSpeech();
    const persona = VOICE_PERSONAS.find((p) => p.id === personaId) || VOICE_PERSONAS[0];
    const textToRead = customText?.trim() || persona.sampleText;

    setIsPlayingVoiceSample(true);
    setPlayingVoiceId(personaId);

    soundEngine.speakWithPersona(textToRead, personaId, () => {
      setIsPlayingVoiceSample(false);
      setPlayingVoiceId(null);
    });
  };

  // Image Upload handler (Data URL via FileReader)
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>, target: 'cover' | 'stop') => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Por favor selecciona un archivo de imagen válido.');
      return;
    }

    const reader = new FileReader();
    reader.onload = (event) => {
      const dataUrl = event.target?.result as string;
      if (dataUrl) {
        if (target === 'cover') {
          setCoverImage(dataUrl);
        } else {
          setStopImage(dataUrl);
        }
      }
    };
    reader.readAsDataURL(file);
  };

  // Voice recording handlers (MediaRecorder)
  const handleStartRecording = async () => {
    try {
      if (!navigator.mediaDevices?.getUserMedia) {
        alert('Tu navegador no soporta grabación directa de sonido.');
        return;
      }
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;
      audioChunksRef.current = [];

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
        const blobUrl = URL.createObjectURL(audioBlob);
        setRecordedAudioBlobUrl(blobUrl);
        stream.getTracks().forEach((t) => t.stop());
      };

      mediaRecorder.start();
      setIsRecording(true);
      setRecordingSeconds(0);
      recordingTimerRef.current = setInterval(() => {
        setRecordingSeconds((prev) => prev + 1);
      }, 1000);
    } catch {
      alert('No se pudo acceder al micrófono. Comprueba los permisos del navegador.');
    }
  };

  const handleStopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop();
      setIsRecording(false);
      clearInterval(recordingTimerRef.current);
    }
  };

  // Open Add Stop Form
  const handleOpenAddStop = () => {
    soundEngine.stopSpeech();
    setIsPlayingVoiceSample(false);
    setEditingStopIndex(null);
    setStopTitle('');
    setStopSubtitle('');
    setStopLocation(location || 'Galicia');
    setStopStory('');
    setStopHistoricalNote('');
    setStopImage(GALICIAN_IMAGE_PRESETS[stops.length % GALICIAN_IMAGE_PRESETS.length].url);
    setStopAudioMode(defaultAudioMode);
    setStopSpokenNarration('');
    setStopVoiceId(defaultVoiceId);
    setRecordedAudioBlobUrl(null);
    setStopGameType('acertijo');
    setAcertijoQuestion('¿Qué figura o misterio esconde este enclave?');
    setAcertijoOptions(['Opción A: Marca de canteros', 'Opción B: Escudo nobiliario', 'Opción C: Cruz solar']);
    setAcertijoCorrectIndex(0);
    setAcertijoHint('Observa atentamente el entorno.');
    setIsAddingStop(true);
  };

  // Open Edit Stop
  const handleOpenEditStop = (index: number) => {
    const s = stops[index];
    setEditingStopIndex(index);
    setStopTitle(s.title);
    setStopSubtitle(s.subtitle);
    setStopLocation(s.locationName);
    setStopStory(s.story);
    setStopHistoricalNote(s.historicalNote || '');
    setStopImage(s.imageUrl || coverImage);
    setStopAudioMode(s.audioMode || defaultAudioMode);
    setStopSpokenNarration(s.audioNarration || s.story);
    setStopVoiceId(s.voiceId || defaultVoiceId);
    setRecordedAudioBlobUrl(s.recordedAudioUrl || null);

    if (s.trial?.type === 'acertijo') {
      setStopGameType('acertijo');
      setAcertijoQuestion(s.trial.question || s.enigma.question);
      setAcertijoOptions(s.trial.options || ['Opción A', 'Opción B', 'Opción C']);
      setAcertijoCorrectIndex(s.trial.correctOptionIndex || 0);
      setAcertijoHint(s.trial.generalHint || s.enigma.hint);
    } else if (s.trial?.type === 'cifrado_magico') {
      setStopGameType('cifrado_magico');
      setCipherWord(s.trial.cipherWord || 'MOURA');
      setCipherClue(s.trial.cipherClue || 'Palabra mágica');
      setCipherHint(s.trial.generalHint || s.enigma.hint);
    } else if (s.trial?.type === 'puzle_runico') {
      setStopGameType('puzle_runico');
      setSelectedRuneIds(s.trial.correctRuneOrder || ['trisquel', 'espiral', 'sol']);
      setRunePuzzleHint(s.trial.generalHint || s.enigma.hint);
    } else {
      setStopGameType('enigma_simple');
      setSimpleQuestion(s.enigma.question);
      setSimpleHint(s.enigma.hint);
      setSimpleAnswer(s.enigma.answer);
    }

    setIsAddingStop(true);
  };

  // Save Stop handler
  const handleSaveStop = () => {
    if (!stopTitle.trim()) {
      alert('Por favor indica el nombre de la parada.');
      return;
    }

    let trialObj: StopTrial | undefined = undefined;

    if (stopGameType === 'acertijo') {
      trialObj = {
        type: 'acertijo',
        title: `Reto de Observación: ${stopTitle}`,
        loreContext: `Misterio patrimonial documentado por el guía para ${stopTitle}.`,
        question: acertijoQuestion.trim() || '¿Cuál es la respuesta correcta?',
        options: acertijoOptions.map((o) => o.trim()).filter((o) => o.length > 0),
        correctOptionIndex: acertijoCorrectIndex,
        generalHint: acertijoHint.trim() || 'Observa con atención los vestigios históricos.',
        characterClues: {
          meiga: 'La intuición de la Meiga descarta las opciones falsas.',
          caminante: 'El archivo del Caminante confirma la inscripción correcta.',
        },
        solutionExplanation: 'Misterio resuelto por la atenta mirada del viajero.',
        magicalReward: 'Sello Patrimonial de la Parada.',
      };
    } else if (stopGameType === 'cifrado_magico') {
      const cleanWord = cipherWord.trim().toUpperCase() || 'MOURA';
      const scrambled = cleanWord.split('').sort(() => Math.random() - 0.5);
      trialObj = {
        type: 'cifrado_magico',
        title: `Cifrado Ancestral: ${cleanWord}`,
        loreContext: 'Las letras sagradas han sido dispersadas por los espíritus del lugar.',
        question: 'Ordena las letras para descifrar la palabra oculta:',
        cipherWord: cleanWord,
        scrambledLetters: scrambled,
        cipherClue: cipherClue.trim() || 'Palabra clave de la tradición oral.',
        generalHint: cipherHint.trim() || `Comienza por la letra ${cleanWord[0]}.`,
        characterClues: {
          trasno: '¡El Trasno conoce el código exacto!',
        },
        solutionExplanation: `¡La palabra ${cleanWord} fue descifrada con éxito!`,
        magicalReward: `Emblema de ${cleanWord} desbloqueado.`,
      };
    } else if (stopGameType === 'puzle_runico') {
      const runes = selectedRuneIds
        .map((id) => AVAILABLE_RUNES.find((r) => r.id === id))
        .filter(Boolean) as RuneGlyph[];
      trialObj = {
        type: 'puzle_runico',
        title: 'Puzle Rúnico de Petroglifos',
        loreContext: 'Alinea la secuencia de símbolos grabados sobre el granito.',
        question: 'Toca los glifos en el orden ritual indicado:',
        runes: runes.length > 0 ? runes : AVAILABLE_RUNES.slice(0, 3),
        correctRuneOrder: selectedRuneIds,
        generalHint: runePuzzleHint.trim() || 'Sigue la energía de los antiguos petroglifos.',
        characterClues: {
          moura: 'La Moura recuerda el orden exacto de los glifos.',
        },
        solutionExplanation: 'Los glifos de piedra se han armonizado.',
        magicalReward: 'Runa Sagrada del Granito.',
      };
    }

    const compiledStop: RouteStop = {
      id:
        editingStopIndex !== null
          ? stops[editingStopIndex].id
          : `stop-${Date.now()}-${Math.floor(Math.random() * 1000)}`,
      order: editingStopIndex !== null ? editingStopIndex + 1 : stops.length + 1,
      title: stopTitle.trim(),
      subtitle: stopSubtitle.trim() || 'Enclave con historia y misterio',
      locationName: stopLocation.trim() || location || 'Galicia',
      imageUrl: stopImage,
      story: stopStory.trim() || 'Historia oral transmitida por los guías locales de este paraje.',
      historicalNote:
        stopHistoricalNote.trim() || 'Anotación patrimonial y documental sobre los vestigios históricos.',
      audioNarration: stopSpokenNarration.trim() || stopStory.trim(),
      voiceId: stopVoiceId,
      audioMode: stopAudioMode,
      recordedAudioUrl: recordedAudioBlobUrl || undefined,
      audioDuration: stopAudioMode === 'written' ? 'Lectura' : '2:30 min',
      coordinates: {
        lat: 42.04 + stops.length * 0.02,
        lng: -8.64 - stops.length * 0.02,
        x: Math.min(85, Math.max(15, 20 + stops.length * 18)),
        y: Math.min(85, Math.max(20, 25 + stops.length * 15)),
      },
      enigma: {
        question:
          stopGameType === 'enigma_simple'
            ? simpleQuestion.trim()
            : trialObj?.question || '¿Qué secreto esconde este lugar?',
        hint:
          stopGameType === 'enigma_simple'
            ? simpleHint.trim()
            : trialObj?.generalHint || 'Fíjate en las marcas del entorno.',
        answer:
          stopGameType === 'enigma_simple'
            ? simpleAnswer.trim()
            : 'Solución revelada al completar el reto.',
      },
      trial: trialObj,
    };

    if (editingStopIndex !== null) {
      const updated = [...stops];
      updated[editingStopIndex] = compiledStop;
      setStops(updated);
    } else {
      setStops([...stops, compiledStop]);
    }

    setIsAddingStop(false);
    setEditingStopIndex(null);
  };

  const handleRemoveStop = (idx: number) => {
    const updated = stops.filter((_, i) => i !== idx).map((s, i) => ({ ...s, order: i + 1 }));
    setStops(updated);
  };

  // Compile full RouteItem
  const assembleRoute = (status: 'pendiente' | 'borrador'): RouteItem => {
    const generatedId = `route-${Date.now()}`;
    return {
      id: generatedId,
      title: title.trim() || 'Ruta Cultural de Autor',
      subtitle: `Itinerario guiado en ${location || 'Galicia'} con retos y relatos`,
      description:
        description.trim() ||
        'Una experiencia cultural única diseñada por el guía para descubrir el patrimonio y la memoria local.',
      location: location.trim() || 'Galicia',
      region: `${(location || 'GALICIA').toUpperCase()} · ITINERARIO CULTURAL`,
      image: coverImage,
      isFeatured: false,
      stopsCount: stops.length,
      duration: duration || '1h 30 min',
      distance: `${(stops.length * 0.85).toFixed(1).replace('.', ',')} km`,
      difficulty,
      categories: [category],
      audioTag:
        defaultAudioMode === 'written'
          ? 'Guía escrita'
          : `Locución ${VOICE_PERSONAS.find((p) => p.id === defaultVoiceId)?.name || 'Narrada'}`,
      badgeTone: 'secondary',
      tomoRef: `Cuaderno del Guía · ${currentGuide.name}`,
      price,
      isFree: price === 'Gratis',
      guide: currentGuide,
      status,
      defaultVoiceId,
      defaultAudioMode,
      viewsCount: 1,
      startedCount: 0,
      revenue: 0,
      overviewFeatures: [
        {
          icon: 'compass',
          title: 'Guía local experto',
          description: `Ruta diseñada con rigor y tradición oral por ${currentGuide.name}.`,
          accentColor: 'primary',
        },
        {
          icon: 'map-pin',
          title: `${stops.length} paradas con sitio e imagen`,
          description: 'Itinerario a pie pensado para disfrutar con calma del patrimonio vivo.',
          accentColor: 'secondary',
        },
        {
          icon: 'help-circle',
          title: 'Juegos y enigmas interactivos',
          description: 'Acertijos, cifrados mágicos y puzles rúnicos diseñados en la plataforma.',
          accentColor: 'tertiary',
        },
      ],
      stops,
    };
  };

  const validate = (): boolean => {
    if (!title.trim()) {
      setValidationError('Por favor, indica un nombre para la ruta.');
      return false;
    }
    if (!location.trim()) {
      setValidationError('Por favor, indica el sitio o ubicación de la ruta.');
      return false;
    }
    if (stops.length === 0) {
      setValidationError('Debes incluir al menos 1 parada con su sitio y juego.');
      return false;
    }
    setValidationError(null);
    return true;
  };

  const handlePublish = () => {
    if (!validate()) return;
    const item = assembleRoute('pendiente');
    soundEngine.stopSpeech();
    onPublishRoute(item);
  };

  const handleDraft = () => {
    if (!title.trim()) {
      setValidationError('Indica al menos un nombre provisional para el borrador.');
      return;
    }
    const item = assembleRoute('borrador');
    soundEngine.stopSpeech();
    onSaveDraft(item);
  };

  return (
    <div className="min-h-screen bg-[#fcf9f2] text-[#1b3022] selection:bg-[#c48b47]/20 flex flex-col justify-between">
      {/* Top Header */}
      <header className="border-b border-[#1b3022]/10 bg-[#fcf9f2]/95 backdrop-blur-md sticky top-0 z-30 shadow-2xs">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <button
            id="create-route-cancel-btn"
            onClick={() => {
              soundEngine.stopSpeech();
              onCancel();
            }}
            className="inline-flex items-center gap-1.5 text-xs sm:text-sm font-serif text-[#1b3022]/80 hover:text-[#1b3022] px-3 py-1.5 rounded-lg border border-[#1b3022]/15 hover:border-[#1b3022]/30 transition-colors cursor-pointer"
          >
            <ChevronLeft className="w-4 h-4" />
            <span>{t('back')}</span>
          </button>

          <div className="flex items-center gap-2 sm:gap-3">
            {/* Multi-language Selector Pill */}
            <LanguageSelector variant="compact" />

            <button
              id="create-route-save-draft-btn"
              onClick={handleDraft}
              className="inline-flex items-center gap-1.5 px-3 sm:px-4 py-1.5 rounded-xl border border-[#1b3022]/20 hover:border-[#1b3022]/40 bg-white/70 text-[#1b3022] text-xs font-serif font-medium transition-all cursor-pointer"
            >
              <FileEdit className="w-3.5 h-3.5 text-[#c48b47]" />
              <span className="hidden sm:inline">{t('creator_bottom_draft')}</span>
              <span className="sm:hidden">Borrador</span>
            </button>

            <button
              id="create-route-publish-btn"
              onClick={handlePublish}
              className="inline-flex items-center gap-1.5 px-4 sm:px-5 py-1.5 rounded-xl bg-[#1b3022] hover:bg-[#274430] text-[#fcf9f2] text-xs font-serif font-semibold shadow-xs transition-all cursor-pointer"
            >
              <Send className="w-3.5 h-3.5 text-[#c48b47]" />
              <span>{t('creator_bottom_publish')}</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Body */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 py-8 w-full flex-1 space-y-8">
        {/* Banner de Bienvenida y Selector de Plantillas */}
        <div className="bg-gradient-to-br from-[#1b3022] via-[#24422e] to-[#16291d] text-[#fcf9f2] rounded-3xl p-6 sm:p-8 shadow-sm border border-[#1b3022]/20">
          <div className="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-[#c48b47] px-3 py-1 rounded-full bg-white/10 border border-white/10 mb-3">
            <Wand2 className="w-3.5 h-3.5" />
            {t('creator_badge')}
          </div>
          <h1 className="text-2xl sm:text-4xl font-serif font-bold text-white tracking-tight">
            {t('creator_title')}
          </h1>
          <p className="text-sm sm:text-base text-[#fcf9f2]/80 font-serif max-w-2xl mt-2 leading-relaxed">
            {t('creator_subtitle')}
          </p>

          {/* Plantillas Predefinidas para Cargar con 1 Clic */}
          <div className="mt-6 pt-5 border-t border-white/15">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-serif font-semibold uppercase tracking-wider text-[#c48b47] flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5" />
                {t('creator_choose_template')}
              </span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {TEMPLATE_PRESETS.map((tpl) => {
                const isSelected = selectedTemplateId === tpl.id;
                return (
                  <button
                    key={tpl.id}
                    type="button"
                    onClick={() => handleSelectTemplate(tpl.id)}
                    className={`text-left p-3.5 rounded-2xl border transition-all cursor-pointer flex flex-col justify-between ${
                      isSelected
                        ? 'bg-[#c48b47]/20 border-[#c48b47] ring-1 ring-[#c48b47] shadow-sm'
                        : 'bg-white/5 border-white/15 hover:bg-white/10 hover:border-white/30'
                    }`}
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1.5">
                        <span className="text-lg">{tpl.icon}</span>
                        <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-white/10 text-[#fcf9f2]">
                          {tpl.badge}
                        </span>
                      </div>
                      <h4 className="font-serif font-bold text-sm text-white line-clamp-1">{tpl.name}</h4>
                      <p className="text-[11px] text-[#fcf9f2]/70 font-serif line-clamp-2 mt-1">
                        {tpl.tagline}
                      </p>
                    </div>

                    <div className="mt-3 pt-2 border-t border-white/10 flex items-center justify-between text-[11px] text-[#c48b47] font-semibold">
                      <span>{tpl.location}</span>
                      {isSelected ? (
                        <span className="flex items-center gap-1 text-xs">
                          <Check className="w-3.5 h-3.5 text-[#c48b47]" /> {t('creator_template_active')}
                        </span>
                      ) : (
                        <span>{t('creator_load_template')} ➔</span>
                      )}
                    </div>
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        {/* Notificación de Error de Validación */}
        {validationError && (
          <div className="p-4 rounded-2xl bg-red-50 border border-red-200 text-red-800 text-xs sm:text-sm flex items-start gap-2.5 font-serif shadow-xs">
            <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-red-600" />
            <span>{validationError}</span>
          </div>
        )}

        {/* ========================================================
            SECCIÓN 1: EL SITIO Y DATOS DE LA RUTA
        ======================================================== */}
        <section className="bg-white rounded-3xl p-6 sm:p-8 border border-[#1b3022]/10 shadow-xs space-y-6">
          <div className="border-b border-[#1b3022]/10 pb-4">
            <div className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-[#8c5717] px-2.5 py-0.5 rounded-full bg-[#c48b47]/15 mb-1.5">
              {t('creator_step1_badge')}
            </div>
            <h2 className="text-xl sm:text-2xl font-serif font-bold text-[#0f2416]">
              {t('creator_step1_title')}
            </h2>
            <p className="text-xs sm:text-sm text-[#47654e] font-serif">
              {t('creator_step1_desc')}
            </p>
          </div>

          <div className="grid grid-cols-1 gap-5">
            {/* Nombre de la ruta */}
            <div>
              <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-1.5">
                {t('creator_route_name')}
              </label>
              <input
                id="route-name-input"
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder={t('creator_route_name_ph')}
                className="w-full px-4 py-3 rounded-2xl border border-[#1b3022]/20 focus:border-[#c48b47] focus:ring-2 focus:ring-[#c48b47]/20 bg-white font-serif text-sm outline-none transition-all"
              />
            </div>

            {/* El Sitio / Ubicación con selector rápido de enclaves */}
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39]">
                  {t('creator_site_location')}
                </label>
                <span className="text-[11px] text-[#8c5717] font-serif">{t('creator_quick_sites')}</span>
              </div>

              <div className="relative mb-2.5">
                <MapPin className="w-4 h-4 text-[#c48b47] absolute left-3.5 top-3.5" />
                <input
                  id="route-location-input"
                  type="text"
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  placeholder={t('creator_site_location_ph')}
                  className="w-full pl-10 pr-4 py-3 rounded-2xl border border-[#1b3022]/20 focus:border-[#c48b47] focus:ring-2 focus:ring-[#c48b47]/20 bg-white font-serif text-sm outline-none transition-all"
                />
              </div>

              {/* Chips de sitios rápidos */}
              <div className="flex flex-wrap gap-1.5">
                {QUICK_SITES.map((site) => (
                  <button
                    key={site}
                    type="button"
                    onClick={() => setLocation(site)}
                    className={`text-[11px] px-2.5 py-1 rounded-xl border font-serif transition-colors cursor-pointer ${
                      location === site
                        ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022]'
                        : 'bg-[#fcf9f2] text-[#1b3022]/80 border-[#1b3022]/15 hover:border-[#c48b47]'
                    }`}
                  >
                    📍 {site}
                  </button>
                ))}
              </div>
            </div>

            {/* Descripción Cultural */}
            <div>
              <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-1.5">
                {t('creator_desc_label')}
              </label>
              <textarea
                id="route-desc-textarea"
                rows={3}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder={t('creator_desc_ph')}
                className="w-full px-4 py-3 rounded-2xl border border-[#1b3022]/20 focus:border-[#c48b47] focus:ring-2 focus:ring-[#c48b47]/20 bg-white font-serif text-sm outline-none transition-all"
              />
            </div>

            {/* Categoría, Duración y Precio */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
              <div>
                <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-1.5">
                  {t('creator_category')}
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value as Category)}
                  className="w-full px-3 py-2.5 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                >
                  <option value="leyendas">{t('filter_legends')}</option>
                  <option value="historia">{t('filter_history')}</option>
                  <option value="familias">{t('filter_families')}</option>
                  <option value="patrimonio">{t('filter_heritage')}</option>
                  <option value="naturaleza">{t('filter_nature')}</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-1.5">
                  {t('duration')}
                </label>
                <div className="relative">
                  <Clock className="w-3.5 h-3.5 text-[#c48b47] absolute left-3 top-3" />
                  <input
                    type="text"
                    value={duration}
                    onChange={(e) => setDuration(e.target.value)}
                    placeholder={t('creator_duration_ph')}
                    className="w-full pl-9 pr-3 py-2.5 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-1.5">
                  {t('difficulty')}
                </label>
                <select
                  value={difficulty}
                  onChange={(e) => setDifficulty(e.target.value as any)}
                  className="w-full px-3 py-2.5 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                >
                  <option value="Fácil">{t('difficulty_easy')} (Paseo accesible)</option>
                  <option value="Media">{t('difficulty_medium')} (Camino tradicional)</option>
                  <option value="Exigente">{t('difficulty_hard')} (Costa o desnivel)</option>
                </select>
              </div>
            </div>

            {/* Precio */}
            <div className="pt-2 border-t border-[#1b3022]/10">
              <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-2">
                {t('creator_price_label')}
              </label>
              <div className="grid grid-cols-2 sm:grid-cols-5 gap-2">
                {(['Gratis', '0,99 €', '1,99 €', '2,99 €', '4,99 €'] as RoutePrice[]).map((p) => {
                  const isSelected = price === p;
                  return (
                    <button
                      key={p}
                      type="button"
                      onClick={() => setPrice(p)}
                      className={`py-2 px-3 rounded-xl border font-serif text-xs font-bold transition-all cursor-pointer ${
                        isSelected
                          ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022] shadow-xs'
                          : 'bg-white text-[#1b3022] border-[#1b3022]/20 hover:border-[#c48b47]'
                      }`}
                    >
                      {p}
                    </button>
                  );
                })}
              </div>
            </div>
          </div>
        </section>

        {/* ========================================================
            SECCIÓN 2: CARGAR IMAGEN DE LA RUTA
        ======================================================== */}
        <section className="bg-white rounded-3xl p-6 sm:p-8 border border-[#1b3022]/10 shadow-xs space-y-6">
          <div className="border-b border-[#1b3022]/10 pb-4">
            <div className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-[#8c5717] px-2.5 py-0.5 rounded-full bg-[#c48b47]/15 mb-1.5">
              {t('creator_step2_badge')}
            </div>
            <h2 className="text-xl sm:text-2xl font-serif font-bold text-[#0f2416]">
              {t('creator_step2_title')}
            </h2>
            <p className="text-xs sm:text-sm text-[#47654e] font-serif">
              {t('creator_step2_desc')}
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            {/* Previsualización de la Imagen */}
            <div className="md:col-span-5">
              <div className="relative rounded-2xl overflow-hidden aspect-4/3 border-2 border-[#1b3022]/15 shadow-sm group">
                <img
                  src={coverImage}
                  alt="Imagen de Portada"
                  className="w-full h-full object-cover transition-transform group-hover:scale-105 duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent flex items-end p-4">
                  <div className="text-white text-xs font-serif">
                    <span className="bg-[#c48b47] px-2 py-0.5 rounded font-bold uppercase text-[10px] mr-1.5">
                      {t('creator_img_preview')}
                    </span>
                    Portada de la ruta
                  </div>
                </div>
              </div>
            </div>

            {/* Selector de modo de carga de imagen */}
            <div className="md:col-span-7 space-y-4">
              <div className="flex border-b border-[#1b3022]/15">
                <button
                  type="button"
                  onClick={() => setCoverImageMode('presets')}
                  className={`pb-2 px-3 text-xs font-serif font-bold border-b-2 transition-colors flex items-center gap-1.5 cursor-pointer ${
                    coverImageMode === 'presets'
                      ? 'border-[#c48b47] text-[#0f2416]'
                      : 'border-transparent text-[#1b3022]/60 hover:text-[#1b3022]'
                  }`}
                >
                  <Sparkles className="w-3.5 h-3.5 text-[#c48b47]" />
                  {t('creator_img_gallery')}
                </button>

                <button
                  type="button"
                  onClick={() => setCoverImageMode('upload')}
                  className={`pb-2 px-3 text-xs font-serif font-bold border-b-2 transition-colors flex items-center gap-1.5 cursor-pointer ${
                    coverImageMode === 'upload'
                      ? 'border-[#c48b47] text-[#0f2416]'
                      : 'border-transparent text-[#1b3022]/60 hover:text-[#1b3022]'
                  }`}
                >
                  <Upload className="w-3.5 h-3.5 text-[#c48b47]" />
                  {t('creator_img_upload')}
                </button>

                <button
                  type="button"
                  onClick={() => setCoverImageMode('url')}
                  className={`pb-2 px-3 text-xs font-serif font-bold border-b-2 transition-colors flex items-center gap-1.5 cursor-pointer ${
                    coverImageMode === 'url'
                      ? 'border-[#c48b47] text-[#0f2416]'
                      : 'border-transparent text-[#1b3022]/60 hover:text-[#1b3022]'
                  }`}
                >
                  <LinkIcon className="w-3.5 h-3.5 text-[#c48b47]" />
                  {t('creator_img_url')}
                </button>
              </div>

              {/* Modo: Galería Precurada */}
              {coverImageMode === 'presets' && (
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {GALICIAN_IMAGE_PRESETS.map((preset) => {
                    const isSelected = coverImage === preset.url;
                    return (
                      <button
                        key={preset.id}
                        type="button"
                        onClick={() => setCoverImage(preset.url)}
                        className={`relative rounded-xl overflow-hidden aspect-video border-2 transition-all cursor-pointer group ${
                          isSelected
                            ? 'border-[#c48b47] ring-2 ring-[#c48b47]/40 scale-95'
                            : 'border-transparent hover:opacity-90'
                        }`}
                      >
                        <img src={preset.url} alt={preset.name} className="w-full h-full object-cover" />
                        <div className="absolute inset-0 bg-black/40 flex items-end p-1 text-[9px] text-white font-serif line-clamp-1 leading-tight">
                          {preset.name}
                        </div>
                        {isSelected && (
                          <div className="absolute top-1 right-1 w-4 h-4 rounded-full bg-[#c48b47] text-[#0f2416] flex items-center justify-center">
                            <Check className="w-3 h-3 stroke-[3]" />
                          </div>
                        )}
                      </button>
                    );
                  })}
                </div>
              )}

              {/* Modo: Subida de Archivo Local */}
              {coverImageMode === 'upload' && (
                <div className="p-5 border-2 border-dashed border-[#1b3022]/20 rounded-2xl text-center bg-[#fcf9f2] space-y-2">
                  <div className="w-10 h-10 rounded-full bg-[#c48b47]/15 text-[#8c5717] mx-auto flex items-center justify-center">
                    <Upload className="w-5 h-5" />
                  </div>
                  <div className="text-xs font-serif font-bold text-[#0f2416]">
                    {t('creator_img_drag')}
                  </div>
                  <p className="text-[11px] text-[#47654e] font-serif">
                    {t('creator_img_formats')}
                  </p>
                  <label className="inline-block mt-2 px-4 py-2 rounded-xl bg-[#1b3022] hover:bg-[#25422e] text-[#fcf9f2] text-xs font-serif font-semibold cursor-pointer transition-colors shadow-xs">
                    <span>{t('creator_img_btn')}</span>
                    <input
                      type="file"
                      accept="image/*"
                      onChange={(e) => handleFileUpload(e, 'cover')}
                      className="hidden"
                    />
                  </label>
                </div>
              )}

              {/* Modo: Enlace URL */}
              {coverImageMode === 'url' && (
                <div className="space-y-2">
                  <label className="block text-xs font-serif font-semibold text-[#334b39]">
                    {t('creator_img_url')}
                  </label>
                  <div className="flex gap-2">
                    <input
                      type="url"
                      value={customCoverUrl}
                      onChange={(e) => setCustomCoverUrl(e.target.value)}
                      placeholder={t('creator_img_url_ph')}
                      className="flex-1 px-3 py-2 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                    />
                    <button
                      type="button"
                      onClick={() => {
                        if (customCoverUrl.trim()) setCoverImage(customCoverUrl.trim());
                      }}
                      className="px-4 py-2 rounded-xl bg-[#1b3022] text-[#fcf9f2] text-xs font-serif font-semibold cursor-pointer"
                    >
                      {t('creator_img_apply')}
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* ========================================================
            SECCIÓN 3: TEXTO ESCRITO Y/O HABLADO + QUÉ VOZ LO REPRODUCE
        ======================================================== */}
        <section className="bg-white rounded-3xl p-6 sm:p-8 border border-[#1b3022]/10 shadow-xs space-y-6">
          <div className="border-b border-[#1b3022]/10 pb-4">
            <div className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-[#8c5717] px-2.5 py-0.5 rounded-full bg-[#c48b47]/15 mb-1.5">
              {t('creator_step3_badge')}
            </div>
            <h2 className="text-xl sm:text-2xl font-serif font-bold text-[#0f2416]">
              {t('creator_step3_title')}
            </h2>
            <p className="text-xs sm:text-sm text-[#47654e] font-serif">
              {t('creator_step3_desc')}
            </p>
          </div>

          {/* Modalidad de Reproducción: Escrita, TTS o Grabación propia */}
          <div>
            <label className="block text-xs font-serif font-semibold uppercase tracking-wider text-[#334b39] mb-2.5">
              {t('creator_audio_mode')}
            </label>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {[
                {
                  id: 'tts',
                  title: t('creator_audio_tts_title'),
                  desc: t('creator_audio_tts_desc'),
                  icon: Headphones,
                },
                {
                  id: 'recorded',
                  title: t('creator_audio_rec_title'),
                  desc: t('creator_audio_rec_desc'),
                  icon: Mic,
                },
                {
                  id: 'written',
                  title: t('creator_audio_written_title'),
                  desc: t('creator_audio_written_desc'),
                  icon: FileEdit,
                },
              ].map((m) => {
                const isSelected = defaultAudioMode === m.id;
                const Icon = m.icon;
                return (
                  <button
                    key={m.id}
                    type="button"
                    onClick={() => setDefaultAudioMode(m.id as any)}
                    className={`text-left p-4 rounded-2xl border transition-all cursor-pointer ${
                      isSelected
                        ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022] shadow-xs'
                        : 'bg-[#fcf9f2] text-[#1b3022] border-[#1b3022]/15 hover:border-[#c48b47]'
                    }`}
                  >
                    <div className="flex items-center gap-2 mb-1.5">
                      <Icon className={`w-4 h-4 ${isSelected ? 'text-[#c48b47]' : 'text-[#8c5717]'}`} />
                      <h4 className="font-serif font-bold text-xs">{m.title}</h4>
                    </div>
                    <p
                      className={`text-[11px] font-serif ${
                        isSelected ? 'text-[#fcf9f2]/80' : 'text-[#47654e]'
                      }`}
                    >
                      {m.desc}
                    </p>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Selector de Voces con Prueba en Vivo */}
          {defaultAudioMode !== 'written' && (
            <div className="pt-4 border-t border-[#1b3022]/10 space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-sm font-serif font-bold text-[#0f2416]">
                    {t('creator_voice_catalog')}
                  </h3>
                  <p className="text-xs text-[#47654e] font-serif">
                    {t('creator_voice_catalog_desc')}
                  </p>
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
                {VOICE_PERSONAS.map((persona) => {
                  const isSelected = defaultVoiceId === persona.id;
                  const isPlayingThis = isPlayingVoiceSample && playingVoiceId === persona.id;

                  return (
                    <div
                      key={persona.id}
                      onClick={() => setDefaultVoiceId(persona.id)}
                      className={`p-4 rounded-2xl border transition-all cursor-pointer flex flex-col justify-between ${
                        isSelected
                          ? 'bg-[#fcf9f2] border-[#c48b47] ring-2 ring-[#c48b47]/40 shadow-xs'
                          : 'bg-white border-[#1b3022]/15 hover:border-[#1b3022]/30'
                      }`}
                    >
                      <div>
                        <div className="flex items-center justify-between mb-1.5">
                          <div className="flex items-center gap-2">
                            <span className="text-xl">{persona.avatarEmoji}</span>
                            <div>
                              <h4 className="text-sm font-serif font-bold text-[#0f2416]">
                                {persona.name}
                              </h4>
                              <span className="text-[10px] text-[#8c5717] font-semibold uppercase tracking-wider">
                                {persona.title}
                              </span>
                            </div>
                          </div>

                          {isSelected && (
                            <span className="w-5 h-5 rounded-full bg-[#1b3022] text-[#fcf9f2] flex items-center justify-center">
                              <Check className="w-3 h-3 stroke-[3]" />
                            </span>
                          )}
                        </div>

                        <p className="text-xs text-[#334b39] font-serif my-2 leading-relaxed">
                          {persona.description}
                        </p>

                        <div className="text-[10px] text-[#47654e] font-serif bg-white/80 p-2 rounded-xl border border-[#1b3022]/10 mb-3 italic">
                          "{persona.sampleText.slice(0, 100)}..."
                        </div>
                      </div>

                      {/* Botón de Prueba de Voz con Sintetizador en Directo */}
                      <div className="pt-2 border-t border-[#1b3022]/10 flex items-center justify-between">
                        <span className="text-[10px] font-serif text-[#8c5717] font-semibold">
                          {persona.tone}
                        </span>

                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handlePlayVoiceSample(persona.id);
                          }}
                          className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-serif font-bold transition-all shadow-2xs cursor-pointer ${
                            isPlayingThis
                              ? 'bg-red-600 text-white animate-pulse'
                              : 'bg-[#1b3022] hover:bg-[#284632] text-[#fcf9f2]'
                          }`}
                        >
                          {isPlayingThis ? (
                            <>
                              <Square className="w-3 h-3 fill-current" />
                              <span>{t('creator_voice_stop')}</span>
                            </>
                          ) : (
                            <>
                              <Play className="w-3 h-3 fill-current text-[#c48b47]" />
                              <span>{t('creator_voice_test')}</span>
                            </>
                          )}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </section>

        {/* ========================================================
            SECCIÓN 4: PARADAS DEL ITINERARIO + ELECCIÓN DEL JUEGO
        ======================================================== */}
        <section className="bg-white rounded-3xl p-6 sm:p-8 border border-[#1b3022]/10 shadow-xs space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1b3022]/10 pb-4">
            <div>
              <div className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-[#8c5717] px-2.5 py-0.5 rounded-full bg-[#c48b47]/15 mb-1.5">
                {t('creator_step4_badge')}
              </div>
              <h2 className="text-xl sm:text-2xl font-serif font-bold text-[#0f2416]">
                {t('creator_step4_title')} ({stops.length})
              </h2>
              <p className="text-xs sm:text-sm text-[#47654e] font-serif">
                {t('creator_step4_desc')}
              </p>
            </div>

            <button
              type="button"
              id="open-add-stop-btn"
              onClick={handleOpenAddStop}
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-2xl bg-[#c48b47] hover:bg-[#d59850] text-[#0f2416] text-xs font-serif font-bold shadow-xs transition-colors cursor-pointer self-start sm:self-auto"
            >
              <Plus className="w-4 h-4" />
              <span>{t('creator_add_stop_btn')}</span>
            </button>
          </div>

          {/* Lista de Paradas Creadas */}
          <div className="space-y-4">
            {stops.map((stop, idx) => (
              <div
                key={stop.id}
                className="bg-[#fcf9f2] border border-[#1b3022]/15 rounded-2xl p-4 sm:p-5 flex flex-col sm:flex-row items-start justify-between gap-4 hover:border-[#1b3022]/30 transition-all shadow-2xs"
              >
                <div className="flex items-start gap-4 w-full">
                  {/* Número de parada */}
                  <span className="w-8 h-8 rounded-full bg-[#1b3022] text-[#fcf9f2] text-xs font-bold flex items-center justify-center shrink-0 mt-0.5 shadow-2xs">
                    {idx + 1}
                  </span>

                  {/* Thumbnail de la parada si tiene */}
                  {stop.imageUrl && (
                    <div className="w-20 h-16 rounded-xl overflow-hidden shrink-0 border border-[#1b3022]/15 hidden sm:block">
                      <img src={stop.imageUrl} alt={stop.title} className="w-full h-full object-cover" />
                    </div>
                  )}

                  <div className="flex-1 min-w-0">
                    <div className="flex flex-wrap items-center gap-2 mb-1">
                      <h3 className="text-sm sm:text-base font-serif font-bold text-[#0f2416] truncate">
                        {stop.title}
                      </h3>
                      <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-[#1b3022]/10 text-[#1b3022]">
                        📍 {stop.locationName}
                      </span>
                    </div>

                    <p className="text-xs text-[#334b39] line-clamp-2 font-serif mb-2">{stop.story}</p>

                    {/* Badge del Juego */}
                    <div className="flex flex-wrap items-center gap-2">
                      {stop.trial?.type === 'acertijo' && (
                        <span className="inline-flex items-center gap-1 text-[11px] text-[#8c5717] bg-[#c48b47]/15 px-2.5 py-0.5 rounded-lg font-serif font-semibold">
                          <HelpCircle className="w-3 h-3" />
                          <span>Acertijo de Opción Múltiple</span>
                        </span>
                      )}
                      {stop.trial?.type === 'cifrado_magico' && (
                        <span className="inline-flex items-center gap-1 text-[11px] text-[#2b5338] bg-[#47654e]/15 px-2.5 py-0.5 rounded-lg font-serif font-semibold">
                          <KeyRound className="w-3 h-3" />
                          <span>Cifrado Mágico: "{stop.trial.cipherWord}"</span>
                        </span>
                      )}
                      {stop.trial?.type === 'puzle_runico' && (
                        <span className="inline-flex items-center gap-1 text-[11px] text-[#553c8b] bg-[#9333ea]/15 px-2.5 py-0.5 rounded-lg font-serif font-semibold">
                          <Sparkles className="w-3 h-3" />
                          <span>Puzle Rúnico Ancestral</span>
                        </span>
                      )}
                      {stop.voiceId && (
                        <span className="inline-flex items-center gap-1 text-[11px] text-[#47654e] bg-white px-2 py-0.5 rounded-lg border border-[#1b3022]/10 font-serif">
                          <Volume2 className="w-3 h-3 text-[#c48b47]" />
                          Voz: {VOICE_PERSONAS.find((v) => v.id === stop.voiceId)?.name || 'Default'}
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Acciones sobre la parada */}
                <div className="flex items-center gap-2 self-end sm:self-center shrink-0">
                  <button
                    type="button"
                    onClick={() => handleOpenEditStop(idx)}
                    className="p-2 text-xs font-serif text-[#1b3022] hover:bg-white rounded-xl border border-[#1b3022]/15 transition-colors cursor-pointer"
                  >
                    Editar
                  </button>

                  <button
                    type="button"
                    onClick={() => handleRemoveStop(idx)}
                    title="Eliminar parada"
                    className="p-2 text-[#1b3022]/50 hover:text-red-700 hover:bg-red-50 rounded-xl transition-colors cursor-pointer"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* ========================================================
              FORMULARIO DINÁMICO DE CREACIÓN/EDICIÓN DE PARADA
          ======================================================== */}
          {isAddingStop && (
            <div className="bg-[#fcf9f2] border-2 border-dashed border-[#c48b47] rounded-3xl p-6 sm:p-7 space-y-6 animate-in fade-in duration-300 shadow-sm">
              <div className="flex items-center justify-between border-b border-[#1b3022]/15 pb-3">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-full bg-[#c48b47] text-[#0f2416] flex items-center justify-center font-bold text-xs">
                    {editingStopIndex !== null ? editingStopIndex + 1 : stops.length + 1}
                  </div>
                  <h3 className="font-serif font-bold text-base text-[#0f2416]">
                    {editingStopIndex !== null ? 'Editar Parada' : 'Configurar Nueva Parada de la Ruta'}
                  </h3>
                </div>

                <button
                  type="button"
                  onClick={() => setIsAddingStop(false)}
                  className="text-xs text-[#1b3022]/70 hover:text-[#1b3022] font-serif cursor-pointer underline"
                >
                  {t('cancel')}
                </button>
              </div>

              {/* 1. Sitio y Nombre de la parada */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-serif font-semibold text-[#334b39] mb-1">
                    {t('creator_stop_name')}
                  </label>
                  <input
                    type="text"
                    value={stopTitle}
                    onChange={(e) => setStopTitle(e.target.value)}
                    placeholder={t('creator_stop_name_ph')}
                    className="w-full px-3 py-2.5 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none focus:border-[#c48b47]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-serif font-semibold text-[#334b39] mb-1">
                    {t('creator_stop_site')}
                  </label>
                  <input
                    type="text"
                    value={stopLocation}
                    onChange={(e) => setStopLocation(e.target.value)}
                    placeholder={t('creator_stop_site_ph')}
                    className="w-full px-3 py-2.5 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none focus:border-[#c48b47]"
                  />
                </div>
              </div>

              {/* 2. Cargar Imagen de la Parada */}
              <div>
                <label className="block text-xs font-serif font-semibold text-[#334b39] mb-2">
                  {t('creator_stop_img')}
                </label>
                <div className="flex flex-col sm:flex-row items-start gap-4">
                  <div className="w-28 h-20 rounded-xl overflow-hidden shrink-0 border border-[#1b3022]/20 shadow-2xs">
                    <img src={stopImage} alt="Parada" className="w-full h-full object-cover" />
                  </div>

                  <div className="flex-1 space-y-2 w-full">
                    <div className="flex gap-2">
                      <label className="px-3 py-1.5 rounded-xl bg-white border border-[#1b3022]/20 text-xs font-serif text-[#1b3022] hover:border-[#c48b47] cursor-pointer inline-flex items-center gap-1.5">
                        <Upload className="w-3.5 h-3.5 text-[#c48b47]" />
                        <span>{t('creator_img_upload')}</span>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={(e) => handleFileUpload(e, 'stop')}
                          className="hidden"
                        />
                      </label>
                    </div>

                    {/* Presets rápidos */}
                    <div className="flex flex-wrap gap-1.5">
                      {GALICIAN_IMAGE_PRESETS.slice(0, 4).map((p) => (
                        <button
                          key={p.id}
                          type="button"
                          onClick={() => setStopImage(p.url)}
                          className={`text-[10px] px-2 py-0.5 rounded-lg border font-serif cursor-pointer ${
                            stopImage === p.url
                              ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022]'
                              : 'bg-white border-[#1b3022]/15 text-[#1b3022]'
                          }`}
                        >
                          {p.name}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
              </div>

              {/* 3. Texto escrito de la parada */}
              <div>
                <label className="block text-xs font-serif font-semibold text-[#334b39] mb-1">
                  {t('creator_stop_story')}
                </label>
                <textarea
                  rows={2}
                  value={stopStory}
                  onChange={(e) => {
                    setStopStory(e.target.value);
                    if (!stopSpokenNarration) setStopSpokenNarration(e.target.value);
                  }}
                  placeholder={t('creator_stop_story_ph')}
                  className="w-full px-3 py-2 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none focus:border-[#c48b47]"
                />
              </div>

              {/* 4. Texto hablado / Audioguía y Voz */}
              <div className="p-4 bg-white rounded-2xl border border-[#1b3022]/15 space-y-3">
                <div className="flex items-center justify-between">
                  <label className="block text-xs font-serif font-bold text-[#0f2416] flex items-center gap-1.5">
                    <Volume2 className="w-3.5 h-3.5 text-[#c48b47]" />
                    Locución y Voz para esta parada
                  </label>
                  <span className="text-[11px] text-[#8c5717] font-serif">
                    Voz activa: <strong>{VOICE_PERSONAS.find((v) => v.id === stopVoiceId)?.name}</strong>
                  </span>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                      {t('creator_stop_voice')}
                    </label>
                    <select
                      value={stopVoiceId}
                      onChange={(e) => setStopVoiceId(e.target.value as VoicePersonaId)}
                      className="w-full px-3 py-2 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                    >
                      {VOICE_PERSONAS.map((vp) => (
                        <option key={vp.id} value={vp.id}>
                          {vp.avatarEmoji} {vp.name} ({vp.title})
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                      {t('creator_audio_mode')}
                    </label>
                    <select
                      value={stopAudioMode}
                      onChange={(e) => setStopAudioMode(e.target.value as any)}
                      className="w-full px-3 py-2 rounded-xl border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                    >
                      <option value="tts">Síntesis de Voz (TTS) con la voz elegida</option>
                      <option value="recorded">Grabar con mi propio micrófono</option>
                      <option value="written">Solo texto para leer en silencio</option>
                    </select>
                  </div>
                </div>

                {/* Grabación directa con micrófono si está en 'recorded' */}
                {stopAudioMode === 'recorded' && (
                  <div className="p-3 bg-[#fcf9f2] rounded-xl border border-[#1b3022]/15 flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-center gap-2">
                      <div
                        className={`w-3 h-3 rounded-full ${
                          isRecording ? 'bg-red-600 animate-ping' : 'bg-gray-400'
                        }`}
                      />
                      <span className="text-xs font-serif font-semibold text-[#0f2416]">
                        {isRecording ? `Grabando voz... (${recordingSeconds}s)` : 'Micrófono preparado'}
                      </span>
                    </div>

                    <div className="flex items-center gap-2">
                      {!isRecording ? (
                        <button
                          type="button"
                          onClick={handleStartRecording}
                          className="px-3 py-1.5 rounded-xl bg-red-600 hover:bg-red-700 text-white text-xs font-serif font-bold inline-flex items-center gap-1 shadow-2xs cursor-pointer"
                        >
                          <Mic className="w-3.5 h-3.5" />
                          <span>Comenzar a grabar</span>
                        </button>
                      ) : (
                        <button
                          type="button"
                          onClick={handleStopRecording}
                          className="px-3 py-1.5 rounded-xl bg-gray-900 text-white text-xs font-serif font-bold inline-flex items-center gap-1 cursor-pointer"
                        >
                          <Square className="w-3.5 h-3.5 fill-current" />
                          <span>Detener grabación</span>
                        </button>
                      )}

                      {recordedAudioBlobUrl && (
                        <audio src={recordedAudioBlobUrl} controls className="h-8 max-w-[200px]" />
                      )}
                    </div>
                  </div>
                )}

                {/* Prueba del texto hablado */}
                {stopAudioMode === 'tts' && (
                  <div className="flex items-center justify-between pt-1">
                    <span className="text-[11px] text-[#47654e] font-serif">
                      Prueba cómo suena la voz con el relato de esta parada:
                    </span>
                    <button
                      type="button"
                      onClick={() => handlePlayVoiceSample(stopVoiceId, stopSpokenNarration || stopStory)}
                      className="px-3 py-1 rounded-xl bg-[#1b3022] text-[#fcf9f2] text-xs font-serif font-semibold inline-flex items-center gap-1.5 cursor-pointer"
                    >
                      <Play className="w-3 h-3 text-[#c48b47] fill-current" />
                      <span>{t('creator_stop_voice_test')}</span>
                    </button>
                  </div>
                )}
              </div>

              {/* 5. ELECCIÓN DEL JUEGO (Opciones fáciles de programar) */}
              <div className="p-4 bg-white rounded-2xl border border-[#1b3022]/15 space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <label className="block text-xs font-serif font-bold text-[#0f2416] flex items-center gap-1.5">
                      <HelpCircle className="w-3.5 h-3.5 text-[#c48b47]" />
                      {t('creator_game_selection')}
                    </label>
                    <p className="text-[11px] text-[#47654e] font-serif">
                      {t('creator_game_selection_desc')}
                    </p>
                  </div>
                </div>

                {/* Pestañas de selección de juego */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
                  {[
                    { id: 'acertijo', label: t('creator_game_riddle'), icon: '❓' },
                    { id: 'cifrado_magico', label: t('creator_game_cipher'), icon: '🔤' },
                    { id: 'puzle_runico', label: t('creator_game_rune'), icon: '🌀' },
                    { id: 'enigma_simple', label: t('creator_game_observation'), icon: '👁️' },
                  ].map((g) => {
                    const isSelected = stopGameType === g.id;
                    return (
                      <button
                        key={g.id}
                        type="button"
                        onClick={() => setStopGameType(g.id as any)}
                        className={`p-2.5 rounded-xl border text-xs font-serif font-bold transition-all text-left flex items-center gap-1.5 cursor-pointer ${
                          isSelected
                            ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022] shadow-xs'
                            : 'bg-[#fcf9f2] text-[#1b3022] border-[#1b3022]/15 hover:border-[#c48b47]'
                        }`}
                      >
                        <span>{g.icon}</span>
                        <span className="truncate">{g.label}</span>
                      </button>
                    );
                  })}
                </div>

                {/* Configuración JUEGO 1: Acertijo con opciones */}
                {stopGameType === 'acertijo' && (
                  <div className="p-3.5 bg-[#fcf9f2] rounded-xl border border-[#1b3022]/10 space-y-3">
                    <div>
                      <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                        Pregunta del acertijo:
                      </label>
                      <input
                        type="text"
                        value={acertijoQuestion}
                        onChange={(e) => setAcertijoQuestion(e.target.value)}
                        placeholder="Ej. ¿Qué animal mítico custodia la entrada?"
                        className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                      />
                    </div>

                    <div>
                      <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                        Opciones de respuesta (marca la correcta con el radio selector):
                      </label>
                      <div className="space-y-2">
                        {acertijoOptions.map((opt, i) => (
                          <div key={i} className="flex items-center gap-2">
                            <input
                              type="radio"
                              name="acertijo-correct-opt"
                              checked={acertijoCorrectIndex === i}
                              onChange={() => setAcertijoCorrectIndex(i)}
                              className="accent-[#c48b47] w-4 h-4 cursor-pointer"
                            />
                            <input
                              type="text"
                              value={opt}
                              onChange={(e) => {
                                const next = [...acertijoOptions];
                                next[i] = e.target.value;
                                setAcertijoOptions(next);
                              }}
                              className="flex-1 px-3 py-1.5 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                            />
                            {acertijoCorrectIndex === i && (
                              <span className="text-[10px] text-green-700 font-bold bg-green-100 px-2 py-0.5 rounded">
                                Correcta
                              </span>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>

                    <div>
                      <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                        Pista para orientar al viajero:
                      </label>
                      <input
                        type="text"
                        value={acertijoHint}
                        onChange={(e) => setAcertijoHint(e.target.value)}
                        placeholder="Ej. Fíjate en el dintel superior grabado en la roca..."
                        className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                      />
                    </div>
                  </div>
                )}

                {/* Configuración JUEGO 2: Cifrado Mágico */}
                {stopGameType === 'cifrado_magico' && (
                  <div className="p-3.5 bg-[#fcf9f2] rounded-xl border border-[#1b3022]/10 space-y-3">
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div>
                        <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                          Palabra secreta a descifrar *
                        </label>
                        <input
                          type="text"
                          value={cipherWord}
                          onChange={(e) => setCipherWord(e.target.value.toUpperCase())}
                          placeholder="Ej. MOURA, CASTRO, MEIGA..."
                          className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs font-bold tracking-wider outline-none"
                        />
                      </div>

                      <div>
                        <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                          Pista temática
                        </label>
                        <input
                          type="text"
                          value={cipherClue}
                          onChange={(e) => setCipherClue(e.target.value)}
                          placeholder="Ej. Espíritu encantado que peina sus cabellos..."
                          className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                        />
                      </div>
                    </div>

                    <div className="text-[11px] text-[#8c5717] font-serif bg-[#c48b47]/10 p-2 rounded-lg flex items-center gap-1.5">
                      <Sparkles className="w-3.5 h-3.5 shrink-0" />
                      <span>
                        La aplicación desordenará automáticamente las letras ({cipherWord.split('').join(' · ')}) y el
                        viajero deberá pulsarlas en el orden correcto.
                      </span>
                    </div>
                  </div>
                )}

                {/* Configuración JUEGO 3: Puzle Rúnico */}
                {stopGameType === 'puzle_runico' && (
                  <div className="p-3.5 bg-[#fcf9f2] rounded-xl border border-[#1b3022]/10 space-y-3">
                    <label className="block text-[11px] font-serif font-semibold text-[#334b39]">
                      Selecciona los símbolos para activar en secuencia:
                    </label>

                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                      {AVAILABLE_RUNES.map((rune) => {
                        const isChosen = selectedRuneIds.includes(rune.id);
                        const orderNum = selectedRuneIds.indexOf(rune.id);

                        return (
                          <button
                            key={rune.id}
                            type="button"
                            onClick={() => {
                              if (isChosen) {
                                setSelectedRuneIds(selectedRuneIds.filter((id) => id !== rune.id));
                              } else {
                                setSelectedRuneIds([...selectedRuneIds, rune.id]);
                              }
                            }}
                            className={`p-2 rounded-xl border text-left flex items-center justify-between transition-all cursor-pointer ${
                              isChosen
                                ? 'bg-[#1b3022] text-[#fcf9f2] border-[#1b3022]'
                                : 'bg-white border-[#1b3022]/15 text-[#1b3022]'
                            }`}
                          >
                            <div className="flex items-center gap-2">
                              <span className="text-xl">{rune.glyph}</span>
                              <div className="text-[11px] font-serif">
                                <div className="font-bold">{rune.name}</div>
                              </div>
                            </div>
                            {isChosen && (
                              <span className="w-5 h-5 rounded-full bg-[#c48b47] text-[#0f2416] text-[11px] font-bold flex items-center justify-center">
                                {orderNum + 1}
                              </span>
                            )}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                )}

                {/* Configuración JUEGO 4: Pregunta y Pista simple */}
                {stopGameType === 'enigma_simple' && (
                  <div className="p-3.5 bg-[#fcf9f2] rounded-xl border border-[#1b3022]/10 space-y-3">
                    <div>
                      <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                        Pregunta de observación directa:
                      </label>
                      <input
                        type="text"
                        value={simpleQuestion}
                        onChange={(e) => setSimpleQuestion(e.target.value)}
                        placeholder="Ej. ¿Qué año está grabado en el dintel de la puerta?"
                        className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                      />
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      <div>
                        <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                          Pista:
                        </label>
                        <input
                          type="text"
                          value={simpleHint}
                          onChange={(e) => setSimpleHint(e.target.value)}
                          placeholder="Ej. Dirige la mirada hacia el arco izquierdo..."
                          className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                        />
                      </div>

                      <div>
                        <label className="block text-[11px] font-serif font-semibold text-[#334b39] mb-1">
                          Solución revelada:
                        </label>
                        <input
                          type="text"
                          value={simpleAnswer}
                          onChange={(e) => setSimpleAnswer(e.target.value)}
                          placeholder="Ej. El año 1684 grabado en números romanos..."
                          className="w-full px-3 py-2 rounded-lg border border-[#1b3022]/20 bg-white font-serif text-xs outline-none"
                        />
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* Botones para Guardar o Cancelar Parada */}
              <div className="flex justify-end gap-2 pt-2 border-t border-[#1b3022]/10">
                <button
                  type="button"
                  onClick={() => setIsAddingStop(false)}
                  className="px-4 py-2 text-xs font-serif text-[#1b3022]/70 hover:text-[#1b3022] cursor-pointer"
                >
                  {t('cancel')}
                </button>
                <button
                  type="button"
                  id="save-new-stop-btn"
                  onClick={handleSaveStop}
                  className="px-5 py-2.5 rounded-xl bg-[#1b3022] hover:bg-[#25422e] text-[#fcf9f2] text-xs font-serif font-bold shadow-xs cursor-pointer inline-flex items-center gap-1.5"
                >
                  <Check className="w-3.5 h-3.5 text-[#c48b47]" />
                  <span>{editingStopIndex !== null ? t('creator_update_stop') : t('creator_save_stop')}</span>
                </button>
              </div>
            </div>
          )}
        </section>

        {/* ========================================================
            BARRA DE ACCIÓN INFERIOR: GUARDAR BORRADOR O PUBLICAR
        ======================================================== */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-4 border-t border-[#1b3022]/15">
          <button
            id="bottom-cancel-btn"
            type="button"
            onClick={() => {
              soundEngine.stopSpeech();
              onCancel();
            }}
            className="text-xs font-serif text-[#1b3022]/70 hover:text-[#1b3022] underline cursor-pointer"
          >
            {t('creator_bottom_back')}
          </button>

          <div className="flex items-center gap-3 w-full sm:w-auto">
            <button
              id="bottom-draft-btn"
              type="button"
              onClick={handleDraft}
              className="w-full sm:w-auto px-5 py-3 rounded-2xl border border-[#1b3022]/20 bg-white hover:bg-[#fcf9f2] text-[#1b3022] font-serif text-xs font-bold transition-all shadow-2xs cursor-pointer"
            >
              {t('creator_bottom_draft')}
            </button>

            <button
              id="bottom-publish-btn"
              type="button"
              onClick={handlePublish}
              className="w-full sm:w-auto px-7 py-3 rounded-2xl bg-[#1b3022] hover:bg-[#25422e] text-[#fcf9f2] font-serif text-sm font-bold shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer"
            >
              <Send className="w-4 h-4 text-[#c48b47]" />
              <span>{t('creator_bottom_publish')}</span>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
};
