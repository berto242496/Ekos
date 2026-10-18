import { useState, useEffect, useRef } from 'react';
import { RouteItem, FieldNote, UserProfile, RouteStop, GuideAuthor, UserPurchaseRecord, Achievement } from './types';
import { ROUTES } from './data/routesData';
import { MOCK_GUIDES } from './data/guidesData';
import { syncUserAchievements } from './data/achievementsData';
import { WelcomeScreen } from './components/WelcomeScreen';
import { CharacterSelectionScreen } from './components/CharacterSelectionScreen';
import { RoutesCatalog } from './components/RoutesCatalog';
import { RouteDetail } from './components/RouteDetail';
import { ActiveExpedition } from './components/ActiveExpedition';
import { InteractiveMap } from './components/InteractiveMap';
import { FieldJournal } from './components/FieldJournal';
import { AuthModal } from './components/AuthModal';
import { AchievementToast } from './components/AchievementToast';
import { KmpCodeViewerModal } from './components/KmpCodeViewerModal';
import { ApkDownloadModal } from './components/ApkDownloadModal';
import { GuideLandingScreen } from './components/GuideLandingScreen';
import { GuideDashboardScreen } from './components/GuideDashboardScreen';
import { GuideCreateRouteScreen } from './components/GuideCreateRouteScreen';
import { RoutePublishedSuccessScreen } from './components/RoutePublishedSuccessScreen';
import { GuideProfileModal } from './components/GuideProfileModal';
import { useI18n } from './services/i18n';
import {
  getMyRoutes,
  getPublishedRoutes,
  getSavedRouteIds,
  saveChallengeProgress,
  saveRoute,
  saveSavedRouteIds,
} from './services/routesRepository';
import { getSupabaseClient, signInWithGoogle, signOut } from './services/supabaseClient';
import { Compass, Map as MapIcon, BookOpen } from 'lucide-react';

type Screen =
  | 'welcome'
  | 'display-name'
  | 'character-selection'
  | 'catalog'
  | 'detail'
  | 'expedition-map'
  | 'expedition'
  | 'guide-landing'
  | 'guide-dashboard'
  | 'guide-create'
  | 'guide-success';
type BottomTab = 'rutas' | 'mapa' | 'mi-aventura';

export default function App() {
  const { t } = useI18n();
  const [currentScreen, setCurrentScreen] = useState<Screen>('welcome');
  const [isGuideSignInLoading, setIsGuideSignInLoading] = useState(false);
  const [postNameEntryScreen, setPostNameEntryScreen] = useState<Screen>('character-selection');
  const [displayNameDraft, setDisplayNameDraft] = useState('');
  const [displayNameError, setDisplayNameError] = useState<string | null>(null);
  const [isSavingDisplayName, setIsSavingDisplayName] = useState(false);
  const [activeTab, setActiveTab] = useState<BottomTab>('rutas');

  // Dynamic routes collection allowing guides to add and publish routes
  const [allRoutes, setAllRoutes] = useState<RouteItem[]>(() => {
    const saved = localStorage.getItem('ecos_routes_v3_es') || localStorage.getItem('ecos_routes');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length > 0) {
          // Merge with latest ROUTES in Spanish: replace default routes with the updated Spanish versions, while preserving user-created routes
          const defaultRouteIds = new Set(ROUTES.map(r => r.id));
          const userCreatedRoutes = parsed.filter((r: RouteItem) => !defaultRouteIds.has(r.id));
          return [...userCreatedRoutes, ...ROUTES];
        }
      } catch {
        // fallback
      }
    }
    return ROUTES;
  });
  const [catalogRoutes, setCatalogRoutes] = useState<RouteItem[]>([]);

  const [selectedRoute, setSelectedRoute] = useState<RouteItem>(() => allRoutes[0] || ROUTES[0]);
  const [pendingChallengeStopId, setPendingChallengeStopId] = useState<string | null>(null);
  const [isMapModalOpen, setIsMapModalOpen] = useState(false);
  const [isKmpModalOpen, setIsKmpModalOpen] = useState(false);
  const [isApkModalOpen, setIsApkModalOpen] = useState(false);
  const [routeStorageError, setRouteStorageError] = useState<string | null>(null);
  const savedRoutesSyncRef = useRef<Promise<void>>(Promise.resolve());

  // Guide Simulation State
  const [currentGuideId, setCurrentGuideId] = useState<string>(() => {
    return localStorage.getItem('ecos_active_guide') || MOCK_GUIDES[0].id;
  });
  const currentGuide = MOCK_GUIDES.find(g => g.id === currentGuideId) || MOCK_GUIDES[0];
  const [lastCreatedRoute, setLastCreatedRoute] = useState<RouteItem | null>(null);
  const [editingRoute, setEditingRoute] = useState<RouteItem | null>(null);
  const [profileModalGuide, setProfileModalGuide] = useState<GuideAuthor | null>(null);

  // Cache the catalog locally; Supabase remains the source for shared, user-created routes.
  useEffect(() => {
    localStorage.setItem('ecos_routes_v3_es', JSON.stringify(allRoutes));
    localStorage.setItem('ecos_routes', JSON.stringify(allRoutes));
  }, [allRoutes]);

  const handleSelectGuide = (guideId: string) => {
    setCurrentGuideId(guideId);
    localStorage.setItem('ecos_active_guide', guideId);
  };

  const handlePublishRoute = async (newRoute: RouteItem) => {
    try {
      const savedRoute = await saveRoute(newRoute);
      setAllRoutes(prev => [savedRoute, ...prev.filter(route => route.id !== savedRoute.id)]);
      setCatalogRoutes(prev => [
        ...(savedRoute.status === 'publicada' ? [savedRoute] : []),
        ...prev.filter(route => route.id !== savedRoute.id),
      ]);
      setLastCreatedRoute(savedRoute);
      setRouteStorageError(null);
      if (editingRoute?.id === savedRoute.id) {
        setEditingRoute(null);
        setCurrentScreen('guide-dashboard');
      } else {
        setCurrentScreen('guide-success');
      }
    } catch (error) {
      setRouteStorageError(error instanceof Error ? error.message : 'No se pudo publicar la ruta.');
    }
  };

  const handleSaveDraft = async (draftRoute: RouteItem) => {
    try {
      const savedRoute = await saveRoute(draftRoute);
      setAllRoutes(prev => [savedRoute, ...prev.filter(route => route.id !== savedRoute.id)]);
      setCatalogRoutes(prev => prev.filter(route => route.id !== savedRoute.id));
      setRouteStorageError(null);
      setEditingRoute(null);
      setCurrentScreen('guide-dashboard');
    } catch (error) {
      setRouteStorageError(error instanceof Error ? error.message : 'No se pudo guardar el borrador.');
    }
  };

  // Auth modal state
  const [authModal, setAuthModal] = useState<{
    isOpen: boolean;
    mode: 'signup' | 'login' | 'profile';
  }>({
    isOpen: false,
    mode: 'signup',
  });

  // Achievement unlock notification toast
  const [unlockedAchievementToast, setUnlockedAchievementToast] = useState<Achievement | null>(null);

  // User Profile with localStorage persistence for local gameplay preferences.
  const [user, setUser] = useState<UserProfile>(() => {
    const saved = localStorage.getItem('ecos_user');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed && typeof parsed === 'object') {
          return {
            ...parsed,
            isGoogleAuthenticated: false,
            googleId: undefined,
            isGuideAccount: false,
            purchasedRouteIds: parsed.purchasedRouteIds || [],
            purchaseHistory: parsed.purchaseHistory || [],
            completedRouteIds: parsed.completedRouteIds || [],
            unlockedAchievementIds: parsed.unlockedAchievementIds || [],
            achievementsUnlockedAt: parsed.achievementsUnlockedAt || {},
          };
        }
      } catch {
        // fallback
      }
    }
    return {
      name: '',
      email: '',
      isVisitor: false,
      isGoogleAuthenticated: false,
      savedRouteIds: [],
      visitedStopIds: [],
      solvedEnigmaIds: [],
      completedRouteIds: [],
      characterId: 'meiga',
      characterDonUsedCount: 0,
      purchasedRouteIds: [],
      purchaseHistory: [],
      unlockedAchievementIds: [],
      achievementsUnlockedAt: {},
    };
  });

  useEffect(() => {
    let subscription: ReturnType<ReturnType<typeof getSupabaseClient>['auth']['onAuthStateChange']>['data']['subscription'] | undefined;
    try {
      const client = getSupabaseClient();
      const { data } = client.auth.onAuthStateChange((_event, session) => {
        if (!session?.user) {
          setUser(previous => ({
            ...previous,
            name: '',
            email: '',
            avatarUrl: undefined,
            isVisitor: false,
            isGoogleAuthenticated: false,
            googleId: undefined,
            isGuideAccount: false,
          }));
          setCurrentScreen('welcome');
          return;
        }

        const metadata = session.user.user_metadata;
        const requestedTarget = sessionStorage.getItem('ecos_post_auth_screen');
        setUser(previous => ({
          ...previous,
          name: metadata.full_name || metadata.name || session.user.email || 'Explorador',
          email: session.user.email || '',
          avatarUrl: metadata.avatar_url,
          isVisitor: false,
          isGoogleAuthenticated: true,
          googleId: session.user.id,
          isGuideAccount: requestedTarget === 'guide-landing'
            ? true
            : previous.isGuideAccount,
        }));
        sessionStorage.removeItem('ecos_post_auth_screen');
        const validTargets: Screen[] = [
          'welcome',
          'character-selection',
          'catalog',
          'detail',
          'expedition',
          'guide-landing',
          'guide-dashboard',
          'guide-create',
          'guide-success',
        ];
        if (requestedTarget && validTargets.includes(requestedTarget as Screen)) {
          setPostNameEntryScreen(requestedTarget as Screen);
          setDisplayNameDraft(metadata.full_name || metadata.name || session.user.email?.split('@')[0] || '');
          setDisplayNameError(null);
          setCurrentScreen('display-name');
        } else {
          setCurrentScreen(previous => previous === 'welcome' ? 'catalog' : previous);
        }
      });
      subscription = data.subscription;
    } catch (error) {
      setRouteStorageError(error instanceof Error ? error.message : 'No se pudo configurar la autenticación.');
    }
    return () => subscription?.unsubscribe();
  }, []);

  useEffect(() => {
    let isActive = true;
    const loadRoutes = async () => {
      const published = await getPublishedRoutes();
      const owned = user.isGoogleAuthenticated ? await getMyRoutes() : [];
      const savedRouteIds = user.isGoogleAuthenticated ? await getSavedRouteIds() : null;
      return { published, routes: [...owned, ...published], savedRouteIds };
    };

    loadRoutes()
      .then(({ published, routes, savedRouteIds }) => {
        if (!isActive) return;
        setCatalogRoutes(published);
        if (savedRouteIds) {
          setUser(previous => previous.googleId === user.googleId
            ? { ...previous, savedRouteIds }
            : previous);
        }
        setAllRoutes(previous => {
          const defaultIds = new Set(ROUTES.map(route => route.id));
          const remoteIds = new Set(routes.map(route => route.id));
          const localRoutes = previous.filter(
            route => !defaultIds.has(route.id) && !remoteIds.has(route.id),
          );
          return [...routes, ...localRoutes, ...ROUTES];
        });
      })
      .catch(error => {
        if (isActive) {
          setRouteStorageError(error instanceof Error ? error.message : 'No se pudieron cargar las rutas compartidas.');
        }
      });

    return () => {
      isActive = false;
    };
  }, [user.isGoogleAuthenticated, user.googleId]);

  // Field Notes with localStorage persistence
  const [fieldNotes, setFieldNotes] = useState<FieldNote[]>(() => {
    const saved = localStorage.getItem('ecos_notes');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        // fallback
      }
    }
    return [
      {
        id: 'note-init-1',
        routeId: 'ribeira-sacra',
        routeTitle: 'Canóns do Sil e Ribeira Sacra',
        stopTitle: 'Mosteiro de Santo Estevo',
        content:
          'Camiñando ao amencer xunto ao claustro dos bispos. O aroma dos castiñeiros mestúrase co silencio sagrado do canón do Sil.',
        timestamp: 'Hoxe ás 09:30',
      },
    ];
  });

  // Sync user state to localStorage
  useEffect(() => {
    localStorage.setItem('ecos_user', JSON.stringify(user));
  }, [user]);

  // Sync notes to localStorage
  useEffect(() => {
    localStorage.setItem('ecos_notes', JSON.stringify(fieldNotes));
  }, [fieldNotes]);

  // Helper to sync achievements and trigger notification toast
  const checkAchievementsOnUpdate = (
    nextUser: UserProfile,
    currentNotes: FieldNote[] = fieldNotes,
    currentRoutes: RouteItem[] = allRoutes
  ): UserProfile => {
    const { updatedUser, newlyUnlocked } = syncUserAchievements(
      nextUser,
      currentNotes.length,
      currentRoutes
    );
    if (newlyUnlocked.length > 0) {
      setUnlockedAchievementToast(newlyUnlocked[0]);
    }
    return updatedUser;
  };

  // Handler to toggle saved route
  const handleToggleSavedRoute = (routeId: string) => {
    const savedIds = user.savedRouteIds.includes(routeId)
      ? user.savedRouteIds.filter(id => id !== routeId)
      : [...user.savedRouteIds, routeId];
    setUser(prev => {
      const nextUser = { ...prev, savedRouteIds: savedIds };
      return checkAchievementsOnUpdate(nextUser);
    });
    if (user.isGoogleAuthenticated) {
      savedRoutesSyncRef.current = savedRoutesSyncRef.current
        .then(() => saveSavedRouteIds(savedIds, user.name))
        .catch(error => {
          setRouteStorageError(error instanceof Error
            ? error.message
            : 'No se pudieron sincronizar las rutas guardadas.');
        });
    }
  };

  // Handler to complete a route
  const handleCompleteRoute = (routeId: string) => {
    setUser(prev => {
      const completed = prev.completedRouteIds || [];
      const updatedCompleted = completed.includes(routeId) ? completed : [...completed, routeId];
      const nextUser: UserProfile = {
        ...prev,
        completedRouteIds: updatedCompleted,
      };
      return checkAchievementsOnUpdate(nextUser);
    });
  };

  // Handler to check in a visited stop
  const handleCheckInStop = (stopId: string) => {
    setUser(prev => {
      if (prev.visitedStopIds.includes(stopId)) return prev;
      const nextUser = { ...prev, visitedStopIds: [...prev.visitedStopIds, stopId] };
      return checkAchievementsOnUpdate(nextUser);
    });
  };

  // Handler to mark enigma solved
  const handleSolveEnigma = (stopId: string, attempts = 1) => {
    const stop = selectedRoute.stops.find(item => item.id === stopId);
    if (stop?.challengeId && user.isGoogleAuthenticated) {
      saveChallengeProgress(stop.challengeId, attempts, 100, true).catch(error => {
        setRouteStorageError(error instanceof Error ? error.message : 'No se pudo guardar el progreso del reto.');
      });
    }
    setUser(prev => {
      if (prev.solvedEnigmaIds.includes(stopId)) return prev;
      const nextUser = { ...prev, solvedEnigmaIds: [...prev.solvedEnigmaIds, stopId] };
      return checkAchievementsOnUpdate(nextUser);
    });
  };

  // Handler to add a new note
  const handleAddNote = (newNote: Omit<FieldNote, 'id' | 'timestamp'>) => {
    const noteObj: FieldNote = {
      ...newNote,
      id: `note-${Date.now()}`,
      timestamp: 'Hoy a las ' + new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };
    const nextNotes = [noteObj, ...fieldNotes];
    setFieldNotes(nextNotes);
    setUser(prev => checkAchievementsOnUpdate(prev, nextNotes));
  };

  // Handler from active expedition to add quick note
  const handleExpeditionAddNote = (stop: RouteStop) => {
    const defaultContent = `Anotación en ${stop.title}: `;
    const content = prompt('Escribe una nota para tu cuaderno de campo:', defaultContent);
    if (content && content.trim()) {
      handleAddNote({
        routeId: selectedRoute.id,
        routeTitle: selectedRoute.title,
        stopTitle: stop.title,
        content: content.trim(),
      });
    }
  };

  // Handler to delete a note
  const handleDeleteNote = (noteId: string) => {
    setFieldNotes(prev => prev.filter(n => n.id !== noteId));
  };

  // Welcome Screen Navigation Actions
  const beginGoogleSignIn = async (target: Screen) => {
    try {
      sessionStorage.setItem('ecos_post_auth_screen', target);
      await signInWithGoogle();
    } catch (error) {
      sessionStorage.removeItem('ecos_post_auth_screen');
      throw error;
    }
  };

  const handleSignInWithGoogle = () => {
    beginGoogleSignIn('character-selection').catch(error => {
      setRouteStorageError(error instanceof Error ? error.message : 'No se pudo iniciar sesión con Google.');
    });
  };

  const handleSaveDisplayName = async () => {
    const displayName = displayNameDraft.trim();
    if (!displayName || displayName.length > 60 || !user.googleId) {
      setDisplayNameError('Escribe un nombre de entre 1 y 60 caracteres.');
      return;
    }
    setIsSavingDisplayName(true);
    setDisplayNameError(null);
    try {
      const { error } = await getSupabaseClient()
        .from('user_profiles')
        .upsert(
          { user_id: user.googleId, display_name: displayName },
          { onConflict: 'user_id' },
        );
      if (error) throw new Error(`No se pudo guardar el nombre: ${error.message}`);
      setUser(previous => ({ ...previous, name: displayName }));
      setCurrentScreen(postNameEntryScreen);
    } catch (error) {
      setDisplayNameError(error instanceof Error ? error.message : 'No se pudo guardar el nombre.');
    } finally {
      setIsSavingDisplayName(false);
    }
  };

  const handleConfirmCharacter = (characterId: string) => {
    setUser(prev => {
      const updated: UserProfile = { ...prev, characterId };
      try {
        localStorage.setItem('ecos_user', JSON.stringify(updated));
      } catch {
        // ignore
      }
      return updated;
    });
    setCurrentScreen('catalog');
    setActiveTab('rutas');
  };

  const handleOpenCharacterSelection = () => {
    if (user.isGoogleAuthenticated) {
      setCurrentScreen('character-selection');
    } else {
      // Must sign in with Google to choose & save character
      setAuthModal({ isOpen: true, mode: 'login' });
    }
  };

  const handleOpenSignUp = () => {
    setAuthModal({ isOpen: true, mode: 'signup' });
  };

  const handleOpenLogin = () => {
    setAuthModal({ isOpen: true, mode: 'login' });
  };

  const handleOpenProfile = () => {
    setAuthModal({ isOpen: true, mode: 'profile' });
  };

  const handleLogoutToWelcome = async () => {
    try {
      await signOut();
      setFieldNotes([]);
      localStorage.removeItem('ecos_user');
      localStorage.removeItem('ecos_field_notes');
      setCurrentScreen('welcome');
      setActiveTab('rutas');
    } catch (error) {
      setRouteStorageError(error instanceof Error ? error.message : 'No se pudo cerrar sesión.');
    }
  };

  // Navigation handlers
  const handleSelectRoute = (route: RouteItem) => {
    setSelectedRoute(route);
    setCurrentScreen('detail');
  };

  const handleStartRoute = (route: RouteItem) => {
    setSelectedRoute(route);
    setPendingChallengeStopId(null);
    setCurrentScreen('expedition-map');
  };

  const handlePurchaseRoute = (route: RouteItem, purchaseRecord: UserPurchaseRecord) => {
    setUser(prev => {
      const updatedUser: UserProfile = {
        ...prev,
        purchasedRouteIds: [...(prev.purchasedRouteIds || []), route.id],
        purchaseHistory: [purchaseRecord, ...(prev.purchaseHistory || [])],
      };
      return checkAchievementsOnUpdate(updatedUser);
    });

    setAllRoutes(prevRoutes => {
      const updated = prevRoutes.map(r => {
        if (r.id === route.id) {
          return {
            ...r,
            startedCount: (r.startedCount || 0) + 1,
            viewsCount: (r.viewsCount || 0) + 1,
            revenue: (r.revenue || 0) + purchaseRecord.pricePaid,
          };
        }
        return r;
      });
      try {
        localStorage.setItem('ecos_routes', JSON.stringify(updated));
      } catch {
        // ignore
      }
      return updated;
    });

    setSelectedRoute(prev => ({
      ...prev,
      startedCount: (prev.startedCount || 0) + 1,
      viewsCount: (prev.viewsCount || 0) + 1,
      revenue: (prev.revenue || 0) + purchaseRecord.pricePaid,
    }));
  };

  return (
    <div className="w-full min-h-screen bg-[#07130a] flex justify-center items-start sm:py-4 md:py-6 overflow-x-hidden selection:bg-[#c5e8ca]">
      {/* Mobile Device Viewport Container - Configured specifically for mobile */}
      <div className="w-full max-w-[430px] min-h-screen sm:min-h-[844px] sm:max-h-[915px] bg-[#fcf9f2] text-[#1c1c18] font-sans antialiased flex flex-col relative sm:rounded-[44px] sm:shadow-[0_25px_70px_rgba(0,0,0,0.95)] sm:border-[8px] sm:border-[#132218] overflow-y-auto overflow-x-hidden">
        {/* Dynamic Island / Mobile Camera Notch on desktop frame */}
        <div className="hidden sm:block absolute top-2.5 left-1/2 -translate-x-1/2 w-24 h-4 bg-[#132218] rounded-full z-50 pointer-events-none" />
        {routeStorageError && (
          <div role="alert" className="sticky top-0 z-[60] flex items-start justify-between gap-3 bg-red-50 border-b border-red-200 px-4 py-3 text-xs text-red-800">
            <span>{routeStorageError}</span>
            <button
              type="button"
              aria-label="Cerrar aviso"
              onClick={() => setRouteStorageError(null)}
              className="font-bold text-red-900"
            >
              ×
            </button>
          </div>
        )}

        {/* 1. Welcome Screen */}
        {currentScreen === 'welcome' && (
          <WelcomeScreen
            onSignInWithGoogle={handleSignInWithGoogle}
            onOpenGuideMode={() => {
              setIsGuideSignInLoading(true);
              beginGoogleSignIn('guide-landing').catch(error => {
                setIsGuideSignInLoading(false);
                setRouteStorageError(error instanceof Error ? error.message : 'No se pudo iniciar sesión con Google.');
              });
            }}
            isGuideSignInLoading={isGuideSignInLoading}
          />
        )}

      {currentScreen === 'display-name' && (
        <main className="min-h-screen bg-[#061b0e] px-5 text-[#fcf9f2] flex items-center justify-center">
          <section className="w-full max-w-md rounded-2xl border border-white/10 bg-white/10 p-6 shadow-xl">
            <h1 className="font-serif text-2xl font-semibold">¿Qué nombre quieres mostrar?</h1>
            <p className="mt-2 text-sm leading-relaxed text-[#ebe8e1]">
              Este nombre aparecerá en tu perfil y en las rutas que publiques.
            </p>
            <label htmlFor="display-name" className="mt-6 block text-sm font-medium">
              Nombre para mostrar
            </label>
            <input
              id="display-name"
              type="text"
              maxLength={60}
              autoComplete="nickname"
              value={displayNameDraft}
              onChange={event => setDisplayNameDraft(event.target.value)}
              className="mt-2 w-full rounded-xl border border-white/20 bg-[#061b0e]/70 px-4 py-3 text-sm outline-none focus:border-[#c48b47]"
            />
            {displayNameError && <p role="alert" className="mt-2 text-sm text-red-200">{displayNameError}</p>}
            <button
              type="button"
              onClick={handleSaveDisplayName}
              disabled={isSavingDisplayName}
              className="mt-5 w-full rounded-xl bg-[#c48b47] px-4 py-3 font-semibold text-[#061b0e] disabled:opacity-60"
            >
              {isSavingDisplayName ? 'Guardando…' : 'Continuar'}
            </button>
          </section>
        </main>
      )}

      {/* 2. Character Selection Screen */}
      {currentScreen === 'character-selection' && (
        <CharacterSelectionScreen
          user={user}
          onConfirmCharacter={handleConfirmCharacter}
          onBack={() => setCurrentScreen(user.isGoogleAuthenticated ? 'catalog' : 'welcome')}
        />
      )}

      {/* 3. Guide Screens */}
      {currentScreen === 'guide-landing' && (
        <GuideLandingScreen
        onStartCreateRoute={() => {
          if (user.isGoogleAuthenticated) setCurrentScreen('guide-create');
          else setAuthModal({ isOpen: true, mode: 'login' });
        }}
        onGoToDashboard={() => {
          if (user.isGoogleAuthenticated) setCurrentScreen('guide-dashboard');
          else setAuthModal({ isOpen: true, mode: 'login' });
        }}
          onBackToVisitor={() => {
            setCurrentScreen(user.isGoogleAuthenticated ? 'catalog' : 'welcome');
          }}
        />
      )}

      {currentScreen === 'guide-dashboard' && (
        <GuideDashboardScreen
          guide={currentGuide}
          availableGuides={MOCK_GUIDES}
          onSelectGuide={handleSelectGuide}
          guideRoutes={allRoutes.filter(
            route => route.guide?.id === (user.googleId || currentGuide.id),
          )}
          onCreateRoute={() => {
            setEditingRoute(null);
            setCurrentScreen('guide-create');
          }}
          onEditRoute={routeId => {
            const route = allRoutes.find(item => item.id === routeId);
            if (route && route.guide?.id === (user.googleId || currentGuide.id)) {
              setEditingRoute(route);
              setCurrentScreen('guide-create');
            }
          }}
          onViewRouteDetail={routeId => {
            const found = allRoutes.find(r => r.id === routeId);
            if (found) {
              setSelectedRoute(found);
              setCurrentScreen('detail');
            }
          }}
          onBackToVisitor={() => {
            setCurrentScreen(user.isGoogleAuthenticated ? 'catalog' : 'welcome');
          }}
        />
      )}

      {currentScreen === 'guide-create' && (
        <GuideCreateRouteScreen
          currentGuide={currentGuide}
          initialRoute={editingRoute}
          onSaveDraft={handleSaveDraft}
          onPublishRoute={handlePublishRoute}
          onCancel={() => {
            setEditingRoute(null);
            setCurrentScreen('guide-dashboard');
          }}
        />
      )}

      {currentScreen === 'guide-success' && lastCreatedRoute && (
        <RoutePublishedSuccessScreen
          route={lastCreatedRoute}
          onViewInCatalog={() => {
            setSelectedRoute(lastCreatedRoute);
            setCurrentScreen('detail');
          }}
          onCreateAnother={() => setCurrentScreen('guide-create')}
          onGoToDashboard={() => setCurrentScreen('guide-dashboard')}
        />
      )}

      {/* 4. Visitor Screen: Detail View */}
      {currentScreen === 'detail' && (
        <RouteDetail
          route={selectedRoute}
          user={user}
          isSaved={user.savedRouteIds.includes(selectedRoute.id)}
          onBack={() => {
            setCurrentScreen('catalog');
            setActiveTab('rutas');
          }}
          onToggleSaved={handleToggleSavedRoute}
          onOpenMap={() => setIsMapModalOpen(true)}
          onStartRoute={handleStartRoute}
          onOpenProfile={handleOpenProfile}
          onOpenGuideProfile={guide => setProfileModalGuide(guide)}
          onPurchaseRoute={handlePurchaseRoute}
        />
      )}

      {/* 5. Visitor Screen: Expedition Active Mode */}
      {currentScreen === 'expedition-map' && (
        <InteractiveMap
          routes={allRoutes}
          selectedRouteId={selectedRoute.id}
          characterId={user.characterId}
          expeditionMode
          solvedStopIds={user.solvedEnigmaIds}
          onSelectRoute={handleSelectRoute}
          onStartRoute={handleStartRoute}
          onStopReached={stop => {
            setPendingChallengeStopId(stop.id);
            handleCheckInStop(stop.id);
            setCurrentScreen('expedition');
          }}
          onClose={() => setCurrentScreen('detail')}
        />
      )}

      {currentScreen === 'expedition' && (
        <ActiveExpedition
          route={selectedRoute}
          user={user}
          initialStopId={pendingChallengeStopId || undefined}
          visitedStopIds={user.visitedStopIds}
          solvedEnigmaIds={user.solvedEnigmaIds}
          onBack={() => setCurrentScreen('expedition-map')}
          onCheckInStop={handleCheckInStop}
          onSolveEnigma={handleSolveEnigma}
          onAddNote={handleExpeditionAddNote}
          onCompleteRoute={handleCompleteRoute}
          onUseCharacterDon={() => {
            setUser(prev => {
              const nextUser = {
                ...prev,
                characterDonUsedCount: (prev.characterDonUsedCount || 0) + 1,
              };
              return checkAchievementsOnUpdate(nextUser);
            });
          }}
        />
      )}

      {/* 6. Visitor Screen: Catalog & Primary Bottom Tabs View */}
      {currentScreen === 'catalog' && (
        <>
          {activeTab === 'rutas' && (
            <RoutesCatalog
              routes={catalogRoutes}
              savedRouteIds={user.savedRouteIds}
              user={user}
              onSelectRoute={handleSelectRoute}
              onOpenProfile={handleOpenProfile}
              onToggleSaved={handleToggleSavedRoute}
              onOpenCharacterSelection={handleOpenCharacterSelection}
              onOpenGuideMode={() => setCurrentScreen('guide-landing')}
              isGuideAccount={user.isGuideAccount === true}
            />
          )}

          {activeTab === 'mapa' && (
            <InteractiveMap
              routes={allRoutes}
              selectedRouteId={selectedRoute.id}
              characterId={user.characterId}
              onSelectRoute={handleSelectRoute}
              onStartRoute={handleStartRoute}
            />
          )}

          {activeTab === 'mi-aventura' && (
            <FieldJournal
              user={user}
              routes={allRoutes}
              notes={fieldNotes}
              onSelectRoute={handleSelectRoute}
              onAddNote={handleAddNote}
              onDeleteNote={handleDeleteNote}
              onOpenProfile={handleOpenProfile}
              onOpenCharacterSelection={handleOpenCharacterSelection}
            />
          )}

          {/* Bottom Navigation Dock matching design specifications */}
          <nav className="sticky bottom-0 w-full z-40 pb-safe bg-[#fcf9f2]/95 backdrop-blur-xl border-t border-[#1b3022]/10 shadow-[0_-4px_20px_rgba(27,48,34,0.06)] mt-auto">
            <div className="flex justify-around items-center h-16 w-full px-4">
              {/* Tab 1: Rutas */}
              <button
                onClick={() => setActiveTab('rutas')}
                className={`flex flex-col items-center justify-center gap-1 min-w-[56px] min-h-[44px] py-1 transition-all active:scale-95 cursor-pointer ${
                  activeTab === 'rutas'
                    ? 'text-[#061b0e] font-semibold'
                    : 'text-[#434843] hover:text-[#1c1c18]'
                }`}
              >
                <Compass
                  className={`w-6 h-6 ${activeTab === 'rutas' ? 'stroke-[2.2]' : 'stroke-[1.5]'}`}
                />
                <span className="font-sans text-[11px] tracking-normal">{t('nav_routes')}</span>
              </button>

              {/* Tab 2: Mapa */}
              <button
                onClick={() => setActiveTab('mapa')}
                className={`flex flex-col items-center justify-center gap-1 min-w-[56px] min-h-[44px] py-1 transition-all active:scale-95 cursor-pointer ${
                  activeTab === 'mapa'
                    ? 'text-[#061b0e] font-semibold'
                    : 'text-[#434843] hover:text-[#1c1c18]'
                }`}
              >
                <MapIcon
                  className={`w-6 h-6 ${activeTab === 'mapa' ? 'stroke-[2.2]' : 'stroke-[1.5]'}`}
                />
                <span className="font-sans text-[11px] tracking-normal">{t('nav_map')}</span>
              </button>

              {/* Tab 3: Mi aventura */}
              <button
                onClick={() => setActiveTab('mi-aventura')}
                className={`flex flex-col items-center justify-center gap-1 min-w-[56px] min-h-[44px] py-1 transition-all active:scale-95 cursor-pointer ${
                  activeTab === 'mi-aventura'
                    ? 'text-[#061b0e] font-semibold'
                    : 'text-[#434843] hover:text-[#1c1c18]'
                }`}
              >
                <BookOpen
                  className={`w-6 h-6 ${activeTab === 'mi-aventura' ? 'stroke-[2.2]' : 'stroke-[1.5]'}`}
                />
                <span className="font-sans text-[11px] tracking-normal">{t('nav_my_adventure')}</span>
              </button>
            </div>
          </nav>
        </>
      )}

      {/* Interactive Map Preview Sheet / Modal (Opened from RouteDetail "Ver mapa") */}
      {isMapModalOpen && (
        <div
          className="fixed inset-0 z-50 bg-[#061b0e]/60 backdrop-blur-xs flex items-end justify-center p-0 animate-in fade-in duration-200"
          onClick={() => setIsMapModalOpen(false)}
        >
          <div
            className="w-full max-w-lg bg-[#fcf9f2] rounded-t-3xl overflow-hidden shadow-2xl border-t border-[#1b3022]/10 animate-in slide-in-from-bottom duration-300"
            onClick={e => e.stopPropagation()}
          >
            <div className="w-12 h-1.5 rounded-full bg-[#c3c8c1] mx-auto mt-2.5 mb-1" />
            <InteractiveMap
              routes={allRoutes}
              selectedRouteId={selectedRoute.id}
              characterId={user.characterId}
              isModal={true}
              onClose={() => setIsMapModalOpen(false)}
              onSelectRoute={route => {
                setSelectedRoute(route);
                setIsMapModalOpen(false);
              }}
              onStartRoute={route => {
                setSelectedRoute(route);
                setIsMapModalOpen(false);
                setCurrentScreen('expedition');
              }}
            />
          </div>
        </div>
      )}

      {/* Guide Profile Modal (Opened from "Ver perfil del guía") */}
      {profileModalGuide && (
        <GuideProfileModal
          guide={profileModalGuide}
          allRoutes={allRoutes}
          onSelectRoute={routeId => {
            const found = allRoutes.find(r => r.id === routeId);
            if (found) {
              setSelectedRoute(found);
              setProfileModalGuide(null);
              setCurrentScreen('detail');
            }
          }}
          onClose={() => setProfileModalGuide(null)}
        />
      )}

      {/* Auth / Profile Modal */}
      <AuthModal
        isOpen={authModal.isOpen}
        mode={authModal.mode}
        user={user}
        allRoutes={allRoutes}
        fieldNotesCount={fieldNotes.length}
        onClose={() => setAuthModal(prev => ({ ...prev, isOpen: false }))}
        onGoogleSignIn={() => beginGoogleSignIn(
          currentScreen === 'guide-landing'
            ? 'guide-create'
            : currentScreen === 'character-selection'
              ? 'character-selection'
              : 'catalog',
        )}
        onLogoutToWelcome={handleLogoutToWelcome}
        onOpenCharacterSelection={handleOpenCharacterSelection}
        onOpenApkModal={() => setIsApkModalOpen(true)}
        onOpenKmpModal={() => setIsKmpModalOpen(true)}
        onNavigateToRoute={routeId => {
          const found = allRoutes.find(r => r.id === routeId);
          if (found) {
            setSelectedRoute(found);
            setCurrentScreen('detail');
          }
        }}
      />

      {/* Achievement Unlock Toast Notification */}
      <AchievementToast
        achievement={unlockedAchievementToast}
        onClose={() => setUnlockedAchievementToast(null)}
      />

      {/* APK Download Modal */}
      <ApkDownloadModal
        isOpen={isApkModalOpen}
        onClose={() => setIsApkModalOpen(false)}
      />

      {/* Kotlin Multiplatform Code & Architecture Modal */}
      <KmpCodeViewerModal
        isOpen={isKmpModalOpen}
        onClose={() => setIsKmpModalOpen(false)}
      />
      </div>
    </div>
  );
}
