import { CharacterItem } from '../types';

export const CHARACTERS: CharacterItem[] = [
  {
    id: 'meiga',
    name: 'Meiga',
    galicianTitle: 'La Sabia de los Hechizos y Hierbas',
    archetype: 'Mística Ancestral',
    badge: 'Purificadora de Engaños',
    summary: 'Maestra del fuego purificador y de las hierbas que descarta engaños y quema opciones falsas en los enigmas.',
    lore: 'Las meigas son las custodias del saber milenario que guarda la tierra gallega. Conocen las fuentes sagradas, las hierbas medicinales y las fuerzas ocultas del fuego purificador. Cuando un viajero encuentra un enigma confuso o lleno de engaños en los senderos, la Meiga aviva el fuego sagrado para quemar las trampas y desvelar la pura verdad.',
    abilityName: 'Purificación y Descarte de Engaños',
    abilityDescription: 'Su fuego purificador quema y descarta opciones falsas en los acertijos, eliminando respuestas engañosas para desvelar la verdad.',
    quote: '«Yo no creo en las meigas, pero el fuego sagrado siempre quema la mentira.»',
    sacredItem: 'Hierba de San Juan y Caldero de Cobre',
    element: 'Fuego Purificador y Hierbas Mágicas',
    accentColor: '#c084fc',
    cardGradient: 'from-[#2b1238] via-[#1a0f27] to-[#090b14]',
    glowColor: 'rgba(192, 132, 252, 0.25)',
    stats: [
      { label: 'Descarte de Engaños', value: 98 },
      { label: 'Sabiduría Ancestral', value: 96 },
      { label: 'Conexión Mística', value: 95 },
      { label: 'Clarividencia en Ruta', value: 92 },
    ],
    avatarEmoji: '🔮',
    iconName: 'Sparkles',
  },
  {
    id: 'moura',
    name: 'Moura de los Castros',
    galicianTitle: 'La Guardiana de las Piedras Milenarias',
    archetype: 'Guardiana Telúrica de la Piedra',
    badge: 'Memoria de los Petroglifos',
    summary: 'Espíritu telúrico de los castros celtas que desvela y ordena la secuencia exacta de los puzles de piedra.',
    lore: 'Las mouras son dueñas de los tesoros subterráneos y de los muros de piedra de los castros de Baroña, Santa Trega y Dombate. Hablan el lenguaje del granito y de los petroglifos que los hombres olvidaron. Ante cualquier puzle rúnico o símbolo ancestral de piedra, la Moura reconoce el orden exacto grabado por los antiguos antepasados.',
    abilityName: 'Orden Ancestral de los Petroglifos',
    abilityDescription: 'Conecta con el granito de los castros y coloca de inmediato los primeros glifos en el orden exacto en los puzles rúnicos.',
    quote: '«El granito no olvida: cada línea grabada tiene su hora y su orden.»',
    sacredItem: 'Peine de Oro y Huso de Piedra',
    element: 'Granito de los Castros y Oro Telúrico',
    accentColor: '#fbbf24',
    cardGradient: 'from-[#382a0d] via-[#241b0b] to-[#09140f]',
    glowColor: 'rgba(251, 191, 36, 0.25)',
    stats: [
      { label: 'Ordenación Rúnica', value: 99 },
      { label: 'Memoria de los Castros', value: 97 },
      { label: 'Descifrado de Piedra', value: 96 },
      { label: 'Resonancia Telúrica', value: 94 },
    ],
    avatarEmoji: '👑',
    iconName: 'Shield',
  },
  {
    id: 'trasno',
    name: 'Trasno',
    galicianTitle: 'El Duendecillo Burlón de los Caminos',
    archetype: 'Guía Pillo y Veloz',
    badge: 'Atajo de Cifrados',
    summary: 'Espíritu travieso que no tiene paciencia para cerraduras y desvela al instante letras de los cifrados mágicos.',
    lore: 'Pequeño, ágil y revoltoso como el viento de las corredoiras. Al trasno no le gustan las cerraduras complicadas ni los códices herméticos: mete mano al mecanismo y descifra de golpe la primera mitad de la palabra secreta para que puedas seguir la expedición sin detenerte.',
    abilityName: 'Atajo Pillo de Cifrados',
    abilityDescription: 'Perfora y abre el enigma escribiendo al instante la primera mitad de las letras en cualquier cifrado mágico o palabra secreta.',
    quote: '«¡Tanto código y tanta vuelta! Déjame trastear que te pongo las letras en un santiamén.»',
    sacredItem: 'Gorro Rojo y Castaña Agujereada',
    element: 'Viento de Corredoira y Llaves Perdidas',
    accentColor: '#f97316',
    cardGradient: 'from-[#3a1d0d] via-[#24130b] to-[#0b0f14]',
    glowColor: 'rgba(249, 115, 22, 0.25)',
    stats: [
      { label: 'Apertura de Cifrados', value: 99 },
      { label: 'Astucia y Pillería', value: 96 },
      { label: 'Atajos de Expedición', value: 97 },
      { label: 'Velocidad de Solución', value: 98 },
    ],
    avatarEmoji: '👺',
    iconName: 'Footprints',
  },
  {
    id: 'lavandeira',
    name: 'Lavandeira',
    galicianTitle: 'La Voz de las Corrientes y Molinos',
    archetype: 'Espíritu Fluvial y Cantiga Oral',
    badge: 'Cantiga Reveladora',
    summary: 'Figura mítica de los ríos que lava lienzos y canta la estrofa poética donde se esconde la respuesta del misterio.',
    lore: 'Vaga entre las orillas del Sil, del Eume y del Miño lavando sus lienzos bajo la niebla. Al escuchar el murmullo de las corrientes, la Lavandeira entona una cantiga tradicional en verso rimado que desvela explícitamente la solución del enigma a quien sabe escucharla con respeto.',
    abilityName: 'Cantiga Oral de las Corrientes',
    abilityDescription: 'Entona la estrofa poética tradicional en verso donde se encuentra la respuesta exacta del enigma, activando un eco fluvial revelador.',
    quote: '«El agua del río no miente: escucha mi cantiga y en ella hallarás la palabra perdida.»',
    sacredItem: 'Lienzo de Lino Crudo y Concha de Vieira',
    element: 'Bruma Fluvial y Agua de los Ríos',
    accentColor: '#38bdf8',
    cardGradient: 'from-[#0e2a3b] via-[#0b1d2b] to-[#06141a]',
    glowColor: 'rgba(56, 189, 248, 0.25)',
    stats: [
      { label: 'Revelación Cantada', value: 99 },
      { label: 'Sensibilidad Fluvial', value: 96 },
      { label: 'Eco de la Tradición Oral', value: 98 },
      { label: 'Armonía del Agua', value: 94 },
    ],
    avatarEmoji: '🌊',
    iconName: 'Waves',
  },
  {
    id: 'lobishome',
    name: 'Lobishome',
    galicianTitle: 'El Señor del Rastro de los Montes',
    archetype: 'Rastreador Salvaje del Rastro',
    badge: 'Faro del Instinto',
    summary: 'Rastreador indómito cuyo olfato salvaje proyecta un aura dorada sobre la respuesta verdadera.',
    lore: 'No es un monstruo, sino el guardián del instinto puro de los bosques y montañas de Galicia. Sus ojos penetran cualquier oscuridad y su olfato salvaje detecta el rastro verdadero del enigma, iluminando con una huella y un brillo dorado de luna llena la opción correcta.',
    abilityName: 'Faro del Instinto (Huella Dorada)',
    abilityDescription: 'Su instinto animal ilumina directamente la respuesta o la pieza correcta con un aura dorada brillante para no errar.',
    quote: '«El rastro de la verdad es como el olor de la tierra húmeda: para mis ojos no hay pérdida.»',
    sacredItem: 'Amuleto de Astas y Diente de Lobo',
    element: 'Musgo de los Bosques y Luz de Luna',
    accentColor: '#4ade80',
    cardGradient: 'from-[#122e1a] via-[#0d2214] to-[#08170c]',
    glowColor: 'rgba(74, 222, 128, 0.25)',
    stats: [
      { label: 'Rastreo del Acierto', value: 99 },
      { label: 'Visión Nocturna en Ruta', value: 97 },
      { label: 'Instinto Certero', value: 98 },
      { label: 'Detección de Trampas', value: 93 },
    ],
    avatarEmoji: '🐺',
    iconName: 'Compass',
  },
  {
    id: 'caminante',
    name: 'Caminante de los Caminos',
    galicianTitle: 'El Cronista de los Senderos y Mojones',
    archetype: 'Custodio de los Viejos Caminos',
    badge: 'Crónica de los Mojones',
    summary: 'El sabio viajero que posee la memoria documental de los archivos, cruceros y puentes de Galicia.',
    lore: 'Galicia es una encrucijada milenaria de calzadas romanas, senderos medievales y caminos a Fisterra. El Caminante lleva en su zurrón los viejos cuadernos de viajes y la memoria de los códices antiguos, desvelando la explicación histórica irrefutable que resuelve cualquier enigma sin fallo.',
    abilityName: 'Crónica Documental de los Mojones',
    abilityDescription: 'Abre la memoria de los archivos y cruceros milenarios para mostrar la prueba documental exacta que resuelve el misterio.',
    quote: '«Lo que está escrito en las piedras de los mojones milenarios es la llave que abre todas las puertas.»',
    sacredItem: 'Bordón de Fresno y Concha Grabada',
    element: 'Granito de los Caminos y Viejos Códices',
    accentColor: '#faba71',
    cardGradient: 'from-[#3a2713] via-[#26180c] to-[#0f140e]',
    glowColor: 'rgba(250, 186, 113, 0.25)',
    stats: [
      { label: 'Certeza Documental', value: 99 },
      { label: 'Memoria de los Cruceros', value: 96 },
      { label: 'Historia de Galicia', value: 98 },
      { label: 'Protección contra Fallos', value: 95 },
    ],
    avatarEmoji: '🧭',
    iconName: 'Navigation',
  },
];

export const DEFAULT_CHARACTER = CHARACTERS[0]; // Meiga

export function getCharacterById(id: string): CharacterItem {
  // Support fallback if previously saved with 'contrabandista'
  if (id === 'contrabandista') {
    return CHARACTERS.find(c => c.id === 'caminante') || DEFAULT_CHARACTER;
  }
  return CHARACTERS.find(c => c.id === id) || DEFAULT_CHARACTER;
}

export interface CharacterAffinity {
  hasAffinity: boolean;
  badgeLabel: string;
  bonusDescription: string;
}

export function getCharacterRouteAffinity(characterId: string, routeId: string): CharacterAffinity {
  const char = getCharacterById(characterId);
  const id = char.id;

  if (id === 'meiga' && (routeId === 'fragas-do-eume' || routeId === 'ribeira-sacra')) {
    return {
      hasAffinity: true,
      badgeLabel: '✨ Sintonía Mística de la Meiga',
      bonusDescription: '+50% percepción de hierbas y pistas ocultas en los robledales y claustros.',
    };
  }
  if (id === 'moura' && (routeId === 'baixo-mino' || routeId === 'rias-baixas' || routeId === 'contrabando' || routeId === 'leyendas-mino')) {
    return {
      hasAffinity: true,
      badgeLabel: '👑 Sintonía Telúrica de la Moura',
      bonusDescription: 'Desvela tesoros de oro y grabados rúnicos de los castros milenarios.',
    };
  }
  if (id === 'lobishome' && (routeId === 'serra-do-courel' || routeId === 'fragas-do-eume')) {
    return {
      hasAffinity: true,
      badgeLabel: '🐺 Sintonía Salvaje del Lobishome',
      bonusDescription: 'Sentidos agudos en los bosques más impenetrables y senderos de montaña.',
    };
  }
  if (id === 'lavandeira' && (routeId === 'ribeira-sacra' || routeId === 'rias-baixas' || routeId === 'memoria-marinera')) {
    return {
      hasAffinity: true,
      badgeLabel: '🌊 Sintonía Fluvial de la Lavandeira',
      bonusDescription: 'Escucha murmullos y cantigas en el batir de las aguas del Sil y de las rías.',
    };
  }
  if (id === 'caminante' && (routeId === 'santiago-cidade' || routeId === 'costa-da-morte' || routeId === 'contrabando')) {
    return {
      hasAffinity: true,
      badgeLabel: '🧭 Sintonía Jacobea del Caminante',
      bonusDescription: 'Memoria de los caminos que conducen al Fin del Mundo y secretos de granito.',
    };
  }
  if (id === 'trasno') {
    return {
      hasAffinity: true,
      badgeLabel: '👺 Don de los Atajos del Trasno',
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
  const char = getCharacterById(characterId);
  switch (char.id) {
    case 'meiga':
      return `«Siento un aroma a hierba de San Juan y agua fresca cerca de ${stopTitle}. Abre los ojos del alma y usa mi don si el enigma te parece oscuro.»`;
    case 'trasno':
      return `«¡Caray! En esta parada de ${stopTitle} hay un sendero secreto... Si te atascas con la prueba, ¡déjame trastear y lo descifro en un abrir y cerrar de ojos!»`;
    case 'moura':
      return `«Las piedras de ${stopTitle} guardan el peso de tres mil años. Escucha el silencio de la tierra y recuerda el valor del trisquel y del oro telúrico.»`;
    case 'lavandeira':
      return `«La niebla y el murmullo del río en ${stopTitle} traen la voz de nuestros antepasados. Presta atención a los ecos de las corrientes.»`;
    case 'lobishome':
      return `«El aire huele a tierra húmeda y musgo en ${stopTitle}. Mis sentidos me dicen que el misterio está grabado muy cerca de nosotros.»` ;
    case 'caminante':
    default:
      return `«Cada paso en ${stopTitle} fue andado por miles de almas antes que nosotros. Observa los mojones y las estrellas del camino.»`;
  }
}
