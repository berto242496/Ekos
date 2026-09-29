import { Achievement, UserAchievementStatus, UserProfile, RouteItem } from '../types';

export const ACHIEVEMENTS: Achievement[] = [
  {
    id: 'primera-ruta',
    title: 'Primera ruta completada',
    description: 'Completa al menos una expedición o ruta en la app.',
    icon: '🏁',
  },
  {
    id: 'visitante-raia',
    title: 'Visitante incansable de la Raia',
    description: 'Explora o completa la ruta del Contrabando en la frontera.',
    icon: '🏰',
  },
  {
    id: 'mente-aguda',
    title: 'Mente Aguda',
    description: 'Resuelve con éxito tu primer enigma mitológico.',
    icon: '🔮',
  },
  {
    id: 'cronista-camino',
    title: 'Cronista del Camino',
    description: 'Escribe tu primera anotación en el cuaderno de campo.',
    icon: '📖',
  },
];

export function getUserAchievements(
  user: UserProfile,
  fieldNotesCount: number = 0,
  _allRoutes: RouteItem[] = []
): UserAchievementStatus[] {
  const completedCount = user.completedRouteIds?.length || 0;
  const isRaiaVisited =
    (user.completedRouteIds || []).includes('contrabando') ||
    (user.visitedStopIds || []).some(
      id => id.includes('cb') || id.includes('tui') || id.includes('valenca')
    );
  const enigmasCount = user.solvedEnigmaIds?.length || 0;
  const unlockedIds = new Set(user.unlockedAchievementIds || []);

  return [
    {
      id: 'primera-ruta',
      title: 'Primera ruta completada',
      description: 'Completa al menos una expedición o ruta en la app.',
      icon: '🏁',
      isUnlocked: completedCount > 0 || unlockedIds.has('primera-ruta'),
    },
    {
      id: 'visitante-raia',
      title: 'Visitante incansable de la Raia',
      description: 'Explora o completa la ruta del Contrabando en la frontera.',
      icon: '🏰',
      isUnlocked: isRaiaVisited || unlockedIds.has('visitante-raia'),
    },
    {
      id: 'mente-aguda',
      title: 'Mente Aguda',
      description: 'Resuelve con éxito tu primer enigma mitológico.',
      icon: '🔮',
      isUnlocked: enigmasCount > 0 || unlockedIds.has('mente-aguda'),
    },
    {
      id: 'cronista-camino',
      title: 'Cronista del Camino',
      description: 'Escribe tu primera anotación en el cuaderno de campo.',
      icon: '📖',
      isUnlocked: fieldNotesCount > 0 || unlockedIds.has('cronista-camino'),
    },
  ];
}

export function syncUserAchievements(
  user: UserProfile,
  fieldNotesCount: number = 0,
  allRoutes: RouteItem[] = []
): { updatedUser: UserProfile; newlyUnlocked: Achievement[] } {
  const currentList = getUserAchievements(user, fieldNotesCount, allRoutes);
  const existingSet = new Set(user.unlockedAchievementIds || []);
  const newlyUnlocked: Achievement[] = [];
  const nextUnlocked = [...(user.unlockedAchievementIds || [])];

  currentList.forEach(item => {
    if (item.isUnlocked && !existingSet.has(item.id)) {
      newlyUnlocked.push({
        id: item.id,
        title: item.title,
        description: item.description,
        icon: item.icon,
      });
      nextUnlocked.push(item.id);
    }
  });

  return {
    updatedUser: {
      ...user,
      unlockedAchievementIds: nextUnlocked,
    },
    newlyUnlocked,
  };
}
