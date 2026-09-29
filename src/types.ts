export type Category = 'historia' | 'leyendas' | 'familias' | 'patrimonio' | 'naturaleza';

export type AppLanguage = 'es' | 'gl' | 'en' | 'pt';

export type TrialType = 'acertijo' | 'puzle_runico' | 'cifrado_magico' | 'alineacion_simbolos';

export interface RuneGlyph {
  id: string;
  glyph: string;
  name: string;
  desc: string;
}

export interface StopTrial {
  type: TrialType;
  title: string;
  loreContext: string;
  question: string;
  // For 'acertijo':
  options?: string[];
  correctOptionIndex?: number;
  // For 'puzle_runico' / 'alineacion_simbolos':
  runes?: RuneGlyph[];
  correctRuneOrder?: string[];
  // For 'cifrado_magico':
  cipherWord?: string;
  scrambledLetters?: string[];
  cipherClue?: string;
  // Hints & Rewards:
  generalHint: string;
  characterClues: Record<string, string>; // characterId -> custom secret hint
  solutionExplanation: string;
  magicalReward: string;
}

export type VoicePersonaId = 'breogan' | 'rosalia' | 'iria' | 'xacobe' | 'guia_propia';

export interface VoicePersona {
  id: VoicePersonaId;
  name: string;
  title: string;
  description: string;
  gender: 'male' | 'female';
  tone: string;
  sampleText: string;
  rate: number;
  pitch: number;
  lang: string;
  avatarEmoji: string;
  recommendedFor: string;
}

export interface RouteStop {
  id: string;
  order: number;
  title: string;
  subtitle: string;
  locationName: string;
  story: string;
  historicalNote: string;
  imageUrl?: string;
  audioNarration?: string; // Text to be spoken or transcribed
  voiceId?: VoicePersonaId; // Selected voice persona
  audioMode?: 'tts' | 'recorded' | 'written'; // TTS, guide recording, or text-only
  recordedAudioUrl?: string; // blob or data url if recorded directly
  enigma: {
    question: string;
    hint: string;
    answer: string;
  };
  trial?: StopTrial;
  audioDuration: string;
  ambientSoundName?: string;
  coordinates: {
    lat: number;
    lng: number;
    x: number; // for SVG cartography (0-100%)
    y: number; // for SVG cartography (0-100%)
  };
}

export type RoutePrice = 'Gratis' | '0,99 €' | '1,99 €' | '2,99 €' | '4,99 €';

export interface GuideAuthor {
  id: string;
  name: string;
  title: string;
  bio: string;
  avatarUrl?: string;
  rating: number;
  reviewsCount: number;
  location?: string;
  verified?: boolean;
  routesCount?: number;
  availableBalance?: number;
  totalWithdrawn?: number;
  payoutSettings?: GuidePayoutSettings;
  payoutHistory?: GuidePayoutRecord[];
  salesHistory?: GuideSalesRecord[];
}

export interface RouteItem {
  id: string;
  title: string;
  subtitle: string;
  description: string;
  location: string;
  region: string;
  image: string;
  isFeatured?: boolean;
  stopsCount: number;
  duration: string;
  distance: string;
  difficulty: 'Fácil' | 'Media' | 'Exigente';
  categories: Category[];
  audioTag: string;
  badgeTone?: 'tertiary' | 'secondary' | 'mist';
  tomoRef: string;
  // Guide and monetization:
  price: string;
  isFree?: boolean;
  guide: GuideAuthor;
  status?: 'publicada' | 'borrador';
  defaultVoiceId?: VoicePersonaId;
  defaultAudioMode?: 'tts' | 'recorded' | 'written';
  viewsCount?: number;
  startedCount?: number;
  revenue?: number;
  overviewFeatures: {
    icon: string;
    title: string;
    description: string;
    accentColor: string;
  }[];
  stops: RouteStop[];
}

export interface GuideStats {
  publishedRoutesCount: number;
  draftRoutesCount: number;
  totalViews: number;
  startedRoutesCount: number;
  totalRevenue: number;
}

export interface FieldNote {
  id: string;
  routeId: string;
  routeTitle: string;
  stopTitle?: string;
  content: string;
  timestamp: string;
}

export interface PassportStamp {
  id: string;
  name: string;
  location: string;
  unlockedAt?: string;
  icon: string;
}

export interface CharacterStat {
  label: string;
  value: number; // 0 - 100
}

export interface CharacterItem {
  id: string;
  name: string;
  galicianTitle: string;
  archetype: string;
  badge: string;
  summary: string;
  lore: string;
  abilityName: string;
  abilityDescription: string;
  quote: string;
  sacredItem: string;
  element: string;
  accentColor: string; // e.g. '#9333ea'
  cardGradient: string; // Tailwind gradient classes
  glowColor: string;
  stats: CharacterStat[];
  avatarEmoji: string;
  iconName: string;
}

export interface Achievement {
  id: string;
  title: string;
  description: string;
  icon: string;
}

export interface UserAchievementStatus extends Achievement {
  isUnlocked: boolean;
}

export interface UserProfile {
  name: string;
  email: string;
  isVisitor: boolean;
  avatarUrl?: string;
  characterId: string;
  googleId?: string;
  isGoogleAuthenticated?: boolean;
  savedRouteIds: string[];
  visitedStopIds: string[];
  solvedEnigmaIds: string[];
  completedRouteIds?: string[];
  characterDonUsedCount?: number;
  purchasedRouteIds?: string[];
  purchaseHistory?: UserPurchaseRecord[];
  unlockedAchievementIds?: string[];
  achievementsUnlockedAt?: Record<string, string>;
}

export interface UserPurchaseRecord {
  id: string;
  routeId: string;
  routeTitle: string;
  guideName: string;
  pricePaid: number; // in euros
  date: string;
  paymentMethod: 'gpay' | 'bizum' | 'card';
  transactionRef: string;
}

export interface GuidePayoutSettings {
  method: 'iban' | 'bizum';
  accountHolder: string;
  iban: string;
  bizumPhone: string;
  bankName: string;
  taxId: string; // NIF / DNI / CIF
  isVerified: boolean;
}

export interface GuidePayoutRecord {
  id: string;
  date: string;
  amount: number;
  netAmount: number;
  withholdingTax: number; // Retención IRPF
  method: 'iban' | 'bizum';
  destination: string;
  status: 'completada' | 'procesando';
  invoiceRef: string;
}

export interface GuideSalesRecord {
  id: string;
  date: string;
  routeId: string;
  routeTitle: string;
  grossAmount: number;
  netGuideAmount: number; // 85% for the guide
  buyerName: string;
  paymentMethod: 'gpay' | 'bizum' | 'card';
}
