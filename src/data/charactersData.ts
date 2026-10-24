import { CharacterItem } from '../types';

export const CHARACTERS: CharacterItem[] = [
  {
    id: 'meiga',
    name: 'Meiga',
    galicianTitle: 'A Meiga',
    archetype: 'Mística ancestral',
    badge: 'Sabiduría ancestral',
    summary: 'Conocedora de las hierbas, los remedios y los saberes mágicos.',
    lore: 'Mujer sabia de la tradición gallega, conocedora de las hierbas, los remedios y los saberes mágicos. Entre la realidad y la leyenda, es una de las figuras más misteriosas de nuestro folclore.',
    abilityName: 'Sabiduría entre hierbas y encantamientos',
    abilityDescription: 'Entre hierbas y encantamientos, conoce secretos que pocos pueden ver. Escucha sus murmullos y quizá encuentres la luz que necesitas cuando la respuesta se oculta en la niebla.',
    quote: '«Escucha los murmullos de las hierbas y encontrarás tu camino.»',
    sacredItem: 'Hierbas y caldero',
    element: 'Naturaleza y magia',
    accentColor: '#c084fc',
    cardGradient: 'from-[#2b1238] via-[#1a0f27] to-[#090b14]',
    glowColor: 'rgba(192, 132, 252, 0.25)',
    stats: [],
    avatarImage: '/assets/characters/meiga.jpeg',
    iconName: 'Sparkles',
  },
  {
    id: 'trasno',
    name: 'Trasno',
    galicianTitle: 'O Trasno',
    archetype: 'Travieso y juguetón',
    badge: 'Astucia de los caminos',
    summary: 'Pequeño ser mágico y travieso, siempre dispuesto a sorprender a quien se cruza con él.',
    lore: 'Pequeño ser mágico y travieso de la mitología gallega, famoso por sus trastadas y por hacer de las suyas en las casas. Le gusta enredar, esconder cosas y sorprender a quien se cruza con él.',
    abilityName: 'Sorpresas en cada rincón',
    abilityDescription: 'Donde otros solo ven un camino, él ve una oportunidad para hacer de las suyas. Cada rincón que descubras puede guardar una pequeña sorpresa esperándote.',
    quote: '«Si has perdido algo por el camino, ¡quizá lo tenga yo!»',
    sacredItem: 'Gorro rojo',
    element: 'Viento y caminos',
    accentColor: '#f97316',
    cardGradient: 'from-[#3a1d0d] via-[#24130b] to-[#0b0f14]',
    glowColor: 'rgba(249, 115, 22, 0.25)',
    stats: [],
    avatarImage: '/assets/characters/trasno.jpeg',
    iconName: 'Footprints',
  },
  {
    id: 'nubeiro',
    name: 'Nubeiro',
    galicianTitle: 'O Nubeiro',
    archetype: 'Señor de las nubes y las tormentas',
    badge: 'Señor de las tormentas',
    summary: 'Figura legendaria de la tradición gallega, señor de las nubes y las tormentas.',
    lore: 'Figura legendaria de la tradición gallega, señor de las nubes y las tormentas. Se dice que recorre los cielos provocando truenos, rayos y lluvia, envuelto en nieblas y vientos misteriosos.',
    abilityName: 'Viaje entre truenos y relámpagos',
    abilityDescription: 'Recorre los cielos entre truenos y relámpagos, y sabe que todo viaje tiene su destino. Si sigues su rastro hasta el final, quizás las nubes se abran para ti y revelen un regalo digno de los dioses del cielo.',
    quote: '«Quien sigue el rastro de las nubes llega siempre a su destino.»',
    sacredItem: 'Relámpago y niebla',
    element: 'Cielo y tormenta',
    accentColor: '#60a5fa',
    cardGradient: 'from-[#0d1f38] via-[#0b1626] to-[#080d18]',
    glowColor: 'rgba(96, 165, 250, 0.25)',
    stats: [],
    avatarImage: '/assets/characters/nubeiro.jpeg',
    iconName: 'CloudLightning',
  },
  {
    id: 'urco',
    name: 'Urco',
    galicianTitle: 'O Urco',
    archetype: 'Criatura de la niebla',
    badge: 'Magia ancestral',
    summary: 'Criatura legendaria de las leyendas marineras y la tradición popular de Pontevedra.',
    lore: 'Criatura legendaria, representada habitualmente como un enorme perro negro con cuernos que aparece entre la niebla. Está especialmente ligado a las leyendas marineras y a la tradición popular de Pontevedra.',
    abilityName: 'Instinto entre sombras y misterios',
    abilityDescription: 'Entre sombras y misterios, percibe verdades que escapan a los demás. Cuando aciertes allí donde otros dudan, sentirás cómo crece contigo su magia ancestral.',
    quote: '«Entre la niebla hay verdades que solo el instinto puede encontrar.»',
    sacredItem: 'Niebla marinera',
    element: 'Mar y misterio',
    accentColor: '#4ade80',
    cardGradient: 'from-[#122e1a] via-[#0d2214] to-[#08170c]',
    glowColor: 'rgba(74, 222, 128, 0.25)',
    stats: [],
    avatarImage: '/assets/characters/urco.jpeg',
    iconName: 'Compass',
  },
];

export const DEFAULT_CHARACTER = CHARACTERS[0];

export function getCharacterById(id: string): CharacterItem {
  return CHARACTERS.find(character => character.id === id) || DEFAULT_CHARACTER;
}

export interface CharacterAffinity {
  hasAffinity: boolean;
  badgeLabel: string;
  bonusDescription: string;
}

export function getCharacterRouteAffinity(characterId: string, routeId: string): CharacterAffinity {
  const id = getCharacterById(characterId).id;

  if (id === 'meiga' && (routeId === 'fragas-do-eume' || routeId === 'ribeira-sacra')) {
    return {
      hasAffinity: true,
      badgeLabel: '✨ Sintonía mística de la Meiga',
      bonusDescription: '+50% de percepción de hierbas y pistas ocultas en robledales y claustros.',
    };
  }
  if (id === 'nubeiro' && (routeId === 'costa-da-morte' || routeId === 'serra-do-courel')) {
    return {
      hasAffinity: true,
      badgeLabel: '⛈️ Sintonía de tormenta del Nubeiro',
      bonusDescription: 'Sus nubes revelan rutas ocultas y señales del cielo en costas y montañas abiertas.',
    };
  }
  if (id === 'urco' && (routeId === 'serra-do-courel' || routeId === 'fragas-do-eume')) {
    return {
      hasAffinity: true,
      badgeLabel: '🐺 Sintonía salvaje del Urco',
      bonusDescription: 'Sus sentidos perciben señales ocultas en los bosques y senderos de montaña.',
    };
  }
  if (id === 'trasno') {
    return {
      hasAffinity: true,
      badgeLabel: '👺 Don de los atajos del Trasno',
      bonusDescription: 'Atajos rápidos y revelación inmediata de llaves ocultas en cualquier ruta.',
    };
  }

  return {
    hasAffinity: false,
    badgeLabel: 'Compañero en ruta',
    bonusDescription: 'Tu personaje acompaña tus pasos por Galicia.',
  };
}

export function getCharacterCompanionAdvice(characterId: string, stopTitle: string): string {
  switch (getCharacterById(characterId).id) {
    case 'meiga':
      return `«Siento un aroma a hierba de San Juan y agua fresca cerca de ${stopTitle}. Abre los ojos del alma y usa mi don si el enigma te parece oscuro.»`;
    case 'trasno':
      return `«¡Caray! En esta parada de ${stopTitle} hay un sendero secreto... Si te atascas con la prueba, ¡déjame trastear y lo descifro en un abrir y cerrar de ojos!»`;
    case 'nubeiro':
      return `«Oigo un trueno lejano sobre ${stopTitle}. Sigue mi rastro y las nubes podrían abrirse para ti.»`;
    case 'urco':
      return `«Entre a néboa de ${stopTitle}, o meu instinto percibe verdades que pasan desapercibidas.»`;
    default:
      return `«Acompáñame por ${stopTitle}; cada paso pode revelar un novo segredo.»`;
  }
}
