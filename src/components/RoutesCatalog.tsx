import React, { useState, useEffect, useMemo } from 'react';
import { RouteItem, Category, UserProfile, GuideAuthor } from '../types';
import { APP_LOGO_URL } from '../data/routesData';
import { MOCK_GUIDES } from '../data/guidesData';
import { getCharacterById, getCharacterRouteAffinity } from '../data/charactersData';
import { CharacterAvatar } from './CharacterAvatar';
import { soundEngine } from '../services/soundEngine';
import { useI18n } from '../services/i18n';
import { LanguageSelector } from './LanguageSelector';
import {
  Search,
  SlidersHorizontal,
  Navigation,
  Bookmark,
  Radio,
  Clock,
  Headphones,
  Trees,
  Waves,
  ArrowRight,
  Play,
  Square,
  User,
  Sparkles,
  CheckCircle2,
  Compass,
  Tag,
  MapPin,
  UserCheck,
  X,
  Check,
  ChevronDown,
  RotateCcw,
} from 'lucide-react';

interface RoutesCatalogProps {
  routes: RouteItem[];
  savedRouteIds: string[];
  user?: UserProfile;
  onSelectRoute: (route: RouteItem) => void;
  onOpenProfile: () => void;
  onToggleSaved: (routeId: string) => void;
  onOpenCharacterSelection?: () => void;
  onOpenGuideMode?: () => void;
  isGuideAccount?: boolean;
}

const CATEGORIES: { id: Category | 'all'; label: string }[] = [
  { id: 'all', label: 'filter_all' },
  { id: 'historia', label: 'filter_history' },
  { id: 'leyendas', label: 'filter_legends' },
  { id: 'naturaleza', label: 'filter_nature' },
  { id: 'gastronomia', label: 'filter_gastronomy' },
  { id: 'arte', label: 'filter_art' },
  { id: 'tradiciones', label: 'filter_traditions' },
];

const CATEGORY_TRANSLATION_KEYS: Record<string, string> = {
  historia: 'filter_history',
  leyendas: 'filter_legends',
  naturaleza: 'filter_nature',
  gastronomia: 'filter_gastronomy',
  arte: 'filter_art',
  tradiciones: 'filter_traditions',
};

const DIFFICULTY_TRANSLATION_KEYS: Record<string, string> = {
  fácil: 'difficulty_easy',
  facil: 'difficulty_easy',
  easy: 'difficulty_easy',
  media: 'difficulty_medium',
  medium: 'difficulty_medium',
  exigente: 'difficulty_hard',
  hard: 'difficulty_hard',
};

export type PriceFilterOption = 'all' | 'free' | 'paid' | 'under2' | 'over2';
export type ActiveFilterDrawer = 'none' | 'price' | 'guide' | 'zone';

// Geographic zone presets
const ZONE_PRESETS = [
  {
    id: 'baixo-mino',
    name: 'Baixo Miño & Fronteira',
    short: 'Baixo Miño',
    landmarks: 'Tui, Valença do Minho, Río Miño e Raia',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('tui') ||
        t.includes('valença') ||
        t.includes('valenca') ||
        t.includes('baixo miño') ||
        t.includes('raia')
      );
    },
  },
  {
    id: 'ribeira-sacra',
    name: 'Ribeira Sacra & Canóns do Sil',
    short: 'Ribeira Sacra',
    landmarks: 'Canóns do Sil, Ourense, Lugo e Mosteiros',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('sacra') ||
        t.includes('sil') ||
        t.includes('ourense') ||
        t.includes('monforte') ||
        t.includes('amandi')
      );
    },
  },
  {
    id: 'costa-rias',
    name: 'Costa Atlántica & Rías',
    short: 'Costa & Rías',
    landmarks: 'Vilas mariñeiras, peiraos tradicionais e rías',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('costa') ||
        t.includes('rías') ||
        t.includes('rias') ||
        t.includes('mariñ') ||
        t.includes('combarro') ||
        t.includes('mar')
      );
    },
  },
  {
    id: 'santiago',
    name: 'Santiago de Compostela & Comarca',
    short: 'Santiago',
    landmarks: 'Cidade histórica, Praza da Quintana e camiños',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('santiago') ||
        t.includes('compostela') ||
        t.includes('quintana') ||
        t.includes('ulla')
      );
    },
  },
  {
    id: 'fragas-eume',
    name: 'As Fragas do Eume & Ártabro',
    short: 'Fragas do Eume',
    landmarks: 'Pontedeume, A Capela, Caaveiro e bosque atlántico',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('eume') ||
        t.includes('caaveiro') ||
        t.includes('pontedeume') ||
        t.includes('ártabro') ||
        t.includes('artabro')
      );
    },
  },
  {
    id: 'serra-courel',
    name: 'Serra do Courel & Ancares',
    short: 'Courel & Ancares',
    landmarks: 'Folgoso do Courel, Devesa da Rogueira e montañas de lousa',
    match: (r: RouteItem) => {
      const t = `${r.location} ${r.region || ''}`.toLowerCase();
      return (
        t.includes('courel') ||
        t.includes('ancares') ||
        t.includes('folgoso') ||
        t.includes('rogueira')
      );
    },
  },
];

const parsePriceNumber = (priceStr?: string): number => {
  if (!priceStr || priceStr.toLowerCase().includes('gratis')) return 0;
  const match = priceStr.replace(',', '.').match(/[\d.]+/);
  return match ? parseFloat(match[0]) : 0;
};

export const RoutesCatalog: React.FC<RoutesCatalogProps> = ({
  routes,
  savedRouteIds,
  user,
  onSelectRoute,
  onOpenProfile,
  onToggleSaved,
  onOpenCharacterSelection,
  onOpenGuideMode,
  isGuideAccount = false,
}) => {
  const { t } = useI18n();
  const [selectedCategory, setSelectedCategory] = useState<Category | 'all'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [onlySaved, setOnlySaved] = useState(false);
  const [isAmbientPlaying, setIsAmbientPlaying] = useState(soundEngine.getIsAmbientPlaying());
  const activeChar = getCharacterById(user?.characterId || 'meiga');

  // Filters State
  const [selectedPriceFilter, setSelectedPriceFilter] = useState<PriceFilterOption>('all');
  const [selectedGuideId, setSelectedGuideId] = useState<string>('all');
  const [selectedZone, setSelectedZone] = useState<string>('all');
  const [activeDrawer, setActiveDrawer] = useState<ActiveFilterDrawer>('none');

  useEffect(() => {
    const unsub = soundEngine.subscribeAmbient(playing => {
      setIsAmbientPlaying(playing);
    });
    return unsub;
  }, []);

  const handleToggleAmbient = () => {
    soundEngine.toggleAmbient();
  };

  // Extract available guides with real route counts
  const availableGuides = useMemo(() => {
    const map = new Map<string, { id: string; name: string; title: string; avatarUrl?: string; verified?: boolean; count: number }>();

    routes.forEach(route => {
      if (route.guide) {
        const existing = map.get(route.guide.id);
        if (existing) {
          existing.count += 1;
        } else {
          map.set(route.guide.id, {
            id: route.guide.id,
            name: route.guide.name,
            title: route.guide.title,
            avatarUrl: route.guide.avatarUrl,
            verified: route.guide.verified,
            count: 1,
          });
        }
      }
    });

    MOCK_GUIDES.forEach(g => {
      if (!map.has(g.id)) {
        const count = routes.filter(r => r.guide?.id === g.id).length;
        if (count > 0) {
          map.set(g.id, {
            id: g.id,
            name: g.name,
            title: g.title,
            avatarUrl: g.avatarUrl,
            verified: g.verified,
            count,
          });
        }
      }
    });

    return Array.from(map.values());
  }, [routes]);

  // Extract available zones with real route counts
  const availableZones = useMemo(() => {
    const list = ZONE_PRESETS.map(preset => ({
      id: preset.id,
      name: preset.name,
      short: preset.short,
      landmarks: preset.landmarks,
      count: routes.filter(r => preset.match(r)).length,
    })).filter(z => z.count > 0);

    const othersCount = routes.filter(r => !ZONE_PRESETS.some(p => p.match(r))).length;
    if (othersCount > 0) {
      list.push({
        id: 'outras',
        name: 'Outras comarcas de Galicia',
        short: 'Outras zonas',
        landmarks: 'Enclaves e aldeas diversas de Galicia',
        count: othersCount,
      });
    }
    return list;
  }, [routes]);

  // Price counts
  const priceCounts = useMemo(() => {
    let free = 0;
    let paid = 0;
    let under2 = 0;
    let over2 = 0;

    routes.forEach(r => {
      const isFree = r.isFree || r.price === 'Gratis';
      const num = parsePriceNumber(r.price);
      if (isFree || num === 0) {
        free += 1;
      } else {
        paid += 1;
        if (num < 2) under2 += 1;
        if (num >= 2) over2 += 1;
      }
    });

    return { total: routes.length, free, paid, under2, over2 };
  }, [routes]);

  const matchesPrice = (route: RouteItem, filter: PriceFilterOption): boolean => {
    if (filter === 'all') return true;
    const isFree = route.isFree || route.price === 'Gratis';
    const num = parsePriceNumber(route.price);

    if (filter === 'free') return isFree || num === 0;
    if (filter === 'paid') return !isFree && num > 0;
    if (filter === 'under2') return !isFree && num > 0 && num < 2;
    if (filter === 'over2') return !isFree && num >= 2;
    return true;
  };

  const filteredRoutes = routes.filter(route => {
    const matchesCategory =
      selectedCategory === 'all' || route.categories.includes(selectedCategory);
    const matchesSearch =
      searchQuery.trim() === '' ||
      route.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      route.location.toLowerCase().includes(searchQuery.toLowerCase()) ||
      route.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (route.guide?.name && route.guide.name.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesSaved = !onlySaved || savedRouteIds.includes(route.id);

    return matchesCategory && matchesSearch && matchesSaved;
  });

  const activeFiltersCount =
    (selectedCategory !== 'all' ? 1 : 0) +
    (onlySaved ? 1 : 0);

  const handleResetAllFilters = () => {
    setSelectedPriceFilter('all');
    setSelectedGuideId('all');
    setSelectedZone('all');
    setSelectedCategory('all');
    setOnlySaved(false);
    setSearchQuery('');
    setActiveDrawer('none');
  };

  const selectedGuideObj = availableGuides.find(g => g.id === selectedGuideId);
  const selectedZoneObj = availableZones.find(z => z.id === selectedZone);

  return (
    <div className="flex flex-col w-full min-h-screen pb-28 bg-[#fcf9f2] text-[#1c1c18]">
      {/* Top Fixed Header */}
      <header className="sticky top-0 w-full z-40 pt-safe bg-[#fcf9f2]/90 backdrop-blur-xl border-b border-[#1b3022]/5 shadow-[0_1px_12px_rgba(27,48,34,0.03)]">
        <div className="h-16 px-4 sm:px-5 flex items-center justify-between gap-2 max-w-lg mx-auto">
          <div className="flex items-center gap-2.5 min-w-0">
            <img
              alt={t('app_title')}
              className="h-8 w-auto object-contain shrink-0"
              src={APP_LOGO_URL}
            />
            <div className="flex flex-col min-w-0">
              <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
                {t('app_title')}
              </span>
              <h1 className="font-serif text-[18px] text-[#1c1c18] font-semibold truncate leading-tight">
                {t('nav_routes')}
              </h1>
            </div>
          </div>

          <div className="flex items-center gap-3 shrink-0">
            {/* Language Selector */}
            <LanguageSelector variant="compact" />

            {/* Soy Guía Link */}
            {isGuideAccount && onOpenGuideMode && (
              <button
                id="catalog-soy-guia-btn"
                onClick={onOpenGuideMode}
                className="h-8 px-2.5 rounded-full bg-[#c48b47]/15 hover:bg-[#c48b47]/25 text-[#8c5717] border border-[#c48b47]/30 flex items-center gap-1.5 text-xs font-serif font-semibold active:scale-95 transition-all cursor-pointer shadow-xs"
                title={t('catalog_guide_access')}
              >
                <Compass className="w-3.5 h-3.5 text-[#c48b47]" />
                <span className="hidden xs:inline">{t('catalog_guide_button')}</span>
              </button>
            )}

            {/* Quick Character Badge Button */}
            {onOpenCharacterSelection && (
              <button
                onClick={onOpenCharacterSelection}
                className="h-8 px-2.5 rounded-full bg-[#1b3022]/10 hover:bg-[#1b3022]/15 active:scale-95 flex items-center gap-1.5 transition-all cursor-pointer border border-[#1b3022]/15"
                title={t('catalog_character_title').replace('{name}', activeChar.name)}
              >
                <CharacterAvatar character={activeChar} className="h-6 w-6" />
                <span className="font-serif text-xs font-semibold text-[#1b3022] hidden xs:inline">
                  {activeChar.name}
                </span>
              </button>
            )}

            {/* Google Profile & Session Button */}
            <button
              onClick={onOpenProfile}
              aria-label={t('catalog_profile_aria')}
              className="h-8 ml-1 px-2.5 rounded-full bg-[#061b0e] hover:bg-[#13351f] flex items-center gap-1.5 text-white active:scale-95 transition-all cursor-pointer shadow-xs border border-white/10"
              title={t('catalog_profile_title')}
            >
              {user?.isGoogleAuthenticated ? (
                <div className="w-5 h-5 rounded-full flex items-center justify-center font-bold text-[10px] bg-emerald-700 text-white border border-emerald-400 shrink-0">
                  {user.name ? user.name.charAt(0) : 'G'}
                </div>
              ) : (
                <User className="w-3.5 h-3.5 text-white shrink-0" />
              )}
              <span className="text-[11px] font-sans font-medium text-[#fcf9f2]">
                {t('catalog_profile')}
              </span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="w-full max-w-lg mx-auto px-5 pt-3">
        {/* Top Location & Action Bar */}
        <div className="flex items-center justify-between py-1 mb-2">
          <div className="flex items-center gap-1.5">
            <Navigation className="w-4 h-4 text-[#47654e]" />
            <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
              {t('catalog_top_text')}
            </span>
          </div>

          <button
            onClick={() => setOnlySaved(!onlySaved)}
            aria-label={t('catalog_saved_routes')}
            className={`w-9 h-9 rounded-full flex items-center justify-center transition-all active:scale-95 cursor-pointer ${
              onlySaved
                ? 'bg-[#1b3022] text-white shadow-xs'
                : 'bg-[#f1eee7] text-[#434843] hover:bg-[#ebe8e1]'
            }`}
            title={onlySaved ? t('catalog_show_all_routes') : t('catalog_filter_saved')}
          >
            <Bookmark className={`w-4 h-4 ${onlySaved ? 'fill-current' : ''}`} />
          </button>
        </div>

        {/* Editorial Title Block */}
        <div className="flex flex-col mb-3">
          <h2 className="font-serif text-[28px] font-semibold text-[#061b0e] tracking-tight leading-tight">
            {t('catalog_heading')}
          </h2>
          <div className="flex items-center justify-between gap-2 mt-1">
            <p className="font-serif text-[15px] text-[#434843] italic">
              {t(routes.length === 1 ? 'catalog_available_one' : 'catalog_available_many').replace('{count}', String(routes.length))}
            </p>
            {activeFiltersCount > 0 && (
              <button
                onClick={handleResetAllFilters}
                className="text-xs font-sans text-[#c48b47] hover:text-[#8c5717] font-semibold flex items-center gap-1 cursor-pointer"
              >
                <RotateCcw className="w-3 h-3" />
                {t('catalog_clear_filters')}
              </button>
            )}
          </div>
        </div>

        {/* Search Input Bar */}
        <div className="flex flex-col gap-2.5 mb-3">
          <div className="flex items-center gap-2 bg-[#f6f3ec] px-3.5 py-2.5 rounded-xl border border-[#c3c8c1]/30 shadow-2xs">
            <Search className="w-4 h-4 text-[#737973] shrink-0" />
            <input
              type="text"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              placeholder={t('catalog_search_placeholder')}
              className="bg-transparent font-sans text-[15px] text-[#1c1c18] placeholder:text-[#737973] focus:outline-none w-full"
            />
            {searchQuery && (
              <button
                onClick={() => setSearchQuery('')}
                className="text-[12px] text-[#737973] hover:text-[#1c1c18] font-sans px-1"
              >
                {t('catalog_clear_search')}
              </button>
            )}
            <button
              onClick={handleResetAllFilters}
              aria-label={t('catalog_reset_filters')}
              className={`w-7 h-7 rounded-lg flex items-center justify-center shrink-0 active:scale-95 transition-transform ${
                activeFiltersCount > 0
                  ? 'bg-[#c48b47] text-white'
                  : 'bg-[#f1eee7] text-[#47654e]'
              }`}
              title={t('catalog_reset_filters')}
            >
              <SlidersHorizontal className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Interactive Expandable Filter Panel: Precio / Guía / Zona */}
          {activeDrawer !== 'none' && (
            <div className="p-3.5 rounded-2xl bg-white border border-[#1b3022]/15 shadow-sm animate-in fade-in slide-in-from-top-2 duration-200">
              {/* Drawer Header */}
              <div className="flex items-center justify-between pb-2.5 mb-2 border-b border-[#f1eee7]">
                <div className="flex items-center gap-1.5">
                  {activeDrawer === 'price' && <Tag className="w-4 h-4 text-[#c48b47]" />}
                  {activeDrawer === 'guide' && <UserCheck className="w-4 h-4 text-[#1b3022]" />}
                  {activeDrawer === 'zone' && <MapPin className="w-4 h-4 text-[#2b4c37]" />}
                  <span className="font-serif text-sm font-semibold text-[#061b0e]">
                    {activeDrawer === 'price' && 'Filtro por Precio'}
                    {activeDrawer === 'guide' && 'Filtro por Guías y Creadores'}
                    {activeDrawer === 'zone' && 'Filtro por Zona de Galicia'}
                  </span>
                </div>
                <button
                  onClick={() => setActiveDrawer('none')}
                  className="w-6 h-6 rounded-full flex items-center justify-center text-[#737973] hover:text-[#1c1c18] hover:bg-[#f1eee7] transition-colors"
                  aria-label="Cerrar panel de filtro"
                >
                  <X className="w-3.5 h-3.5" />
                </button>
              </div>

              {/* Option List: PRECIO */}
              {activeDrawer === 'price' && (
                <div className="flex flex-col gap-1.5">
                  {[
                    { id: 'all', label: 'Todos los precios', desc: 'Rutas gratuitas y de pago', count: priceCounts.total },
                    { id: 'free', label: 'Gratis', desc: 'Experiencias de acceso libre (0,00 €)', count: priceCounts.free },
                    { id: 'paid', label: 'De pago', desc: 'Con micropago para apoyar al guía local', count: priceCounts.paid },
                    { id: 'under2', label: 'Económicas (< 2,00 €)', desc: 'Rutas de 0,99 € o 1,99 €', count: priceCounts.under2 },
                    { id: 'over2', label: 'Completas (≥ 2,00 €)', desc: 'Rutas de 2,99 € o 4,99 € con audio extendido', count: priceCounts.over2 },
                  ].map(item => {
                    const isSelected = selectedPriceFilter === item.id;
                    return (
                      <button
                        key={item.id}
                        onClick={() => {
                          setSelectedPriceFilter(item.id as PriceFilterOption);
                          setActiveDrawer('none');
                        }}
                        className={`flex items-center justify-between p-2 rounded-xl text-left transition-all active:scale-[0.99] cursor-pointer ${
                          isSelected
                            ? 'bg-[#c48b47]/15 border border-[#c48b47]/40 text-[#8c5717]'
                            : 'hover:bg-[#fcf9f2] text-[#1c1c18]'
                        }`}
                      >
                        <div className="flex items-center gap-2 min-w-0">
                          <div
                            className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 ${
                              isSelected
                                ? 'bg-[#c48b47] border-[#c48b47] text-white'
                                : 'border-[#c3c8c1]'
                            }`}
                          >
                            {isSelected && <Check className="w-2.5 h-2.5" />}
                          </div>
                          <div className="flex flex-col min-w-0">
                            <span className="font-serif text-xs font-semibold leading-tight">
                              {item.label}
                            </span>
                            <span className="font-sans text-[10px] text-[#737973] truncate">
                              {item.desc}
                            </span>
                          </div>
                        </div>
                        <span className="text-[11px] font-mono px-2 py-0.5 rounded-full bg-[#f1eee7] text-[#434843] shrink-0 font-medium">
                          {item.count}
                        </span>
                      </button>
                    );
                  })}
                </div>
              )}

              {/* Option List: GUÍA */}
              {activeDrawer === 'guide' && (
                <div className="flex flex-col gap-1.5">
                  <button
                    onClick={() => {
                      setSelectedGuideId('all');
                      setActiveDrawer('none');
                    }}
                    className={`flex items-center justify-between p-2 rounded-xl text-left transition-all active:scale-[0.99] cursor-pointer ${
                      selectedGuideId === 'all'
                        ? 'bg-[#1b3022]/10 border border-[#1b3022]/30 text-[#1b3022]'
                        : 'hover:bg-[#fcf9f2] text-[#1c1c18]'
                    }`}
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <div
                        className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 ${
                          selectedGuideId === 'all'
                            ? 'bg-[#1b3022] border-[#1b3022] text-white'
                            : 'border-[#c3c8c1]'
                        }`}
                      >
                        {selectedGuideId === 'all' && <Check className="w-2.5 h-2.5" />}
                      </div>
                      <div className="flex flex-col min-w-0">
                        <span className="font-serif text-xs font-semibold leading-tight">
                          Todos los guías
                        </span>
                        <span className="font-sans text-[10px] text-[#737973]">
                          Ver rutas de todos los autores y expertos
                        </span>
                      </div>
                    </div>
                    <span className="text-[11px] font-mono px-2 py-0.5 rounded-full bg-[#f1eee7] text-[#434843] shrink-0 font-medium">
                      {routes.length}
                    </span>
                  </button>

                  {availableGuides.map(guide => {
                    const isSelected = selectedGuideId === guide.id;
                    return (
                      <button
                        key={guide.id}
                        onClick={() => {
                          setSelectedGuideId(guide.id);
                          setActiveDrawer('none');
                        }}
                        className={`flex items-center justify-between p-2 rounded-xl text-left transition-all active:scale-[0.99] cursor-pointer ${
                          isSelected
                            ? 'bg-[#1b3022] text-white shadow-xs'
                            : 'hover:bg-[#fcf9f2] text-[#1c1c18]'
                        }`}
                      >
                        <div className="flex items-center gap-2.5 min-w-0">
                          {guide.avatarUrl ? (
                            <img
                              src={guide.avatarUrl}
                              alt={guide.name}
                              className="w-7 h-7 rounded-full object-cover border border-white/40 shrink-0"
                            />
                          ) : (
                            <div className="w-7 h-7 rounded-full bg-[#c48b47]/20 flex items-center justify-center text-xs font-bold text-[#8c5717] shrink-0">
                              {guide.name.charAt(0)}
                            </div>
                          )}
                          <div className="flex flex-col min-w-0">
                            <div className="flex items-center gap-1">
                              <span
                                className={`font-serif text-xs font-semibold truncate ${
                                  isSelected ? 'text-white' : 'text-[#061b0e]'
                                }`}
                              >
                                {guide.name}
                              </span>
                              {guide.verified && (
                                <CheckCircle2
                                  className={`w-3 h-3 shrink-0 ${
                                    isSelected ? 'text-[#ffddba]' : 'text-[#c48b47]'
                                  }`}
                                />
                              )}
                            </div>
                            <span
                              className={`font-sans text-[10px] truncate ${
                                isSelected ? 'text-white/80' : 'text-[#737973]'
                              }`}
                            >
                              {guide.title}
                            </span>
                          </div>
                        </div>
                        <span
                          className={`text-[11px] font-mono px-2 py-0.5 rounded-full shrink-0 font-medium ${
                            isSelected
                              ? 'bg-white/20 text-white'
                              : 'bg-[#f1eee7] text-[#434843]'
                          }`}
                        >
                          {guide.count} ruta{guide.count === 1 ? '' : 's'}
                        </span>
                      </button>
                    );
                  })}
                </div>
              )}

              {/* Option List: ZONA */}
              {activeDrawer === 'zone' && (
                <div className="flex flex-col gap-1.5">
                  <button
                    onClick={() => {
                      setSelectedZone('all');
                      setActiveDrawer('none');
                    }}
                    className={`flex items-center justify-between p-2 rounded-xl text-left transition-all active:scale-[0.99] cursor-pointer ${
                      selectedZone === 'all'
                        ? 'bg-[#2b4c37]/15 border border-[#2b4c37]/30 text-[#1b3022]'
                        : 'hover:bg-[#fcf9f2] text-[#1c1c18]'
                    }`}
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <div
                        className={`w-4 h-4 rounded-full border flex items-center justify-center shrink-0 ${
                          selectedZone === 'all'
                            ? 'bg-[#2b4c37] border-[#2b4c37] text-white'
                            : 'border-[#c3c8c1]'
                        }`}
                      >
                        {selectedZone === 'all' && <Check className="w-2.5 h-2.5" />}
                      </div>
                      <div className="flex flex-col min-w-0">
                        <span className="font-serif text-xs font-semibold leading-tight">
                          Todas las zonas de Galicia
                        </span>
                        <span className="font-sans text-[10px] text-[#737973]">
                          Ver todas as comarcas e provincias
                        </span>
                      </div>
                    </div>
                    <span className="text-[11px] font-mono px-2 py-0.5 rounded-full bg-[#f1eee7] text-[#434843] shrink-0 font-medium">
                      {routes.length}
                    </span>
                  </button>

                  {availableZones.map(zone => {
                    const isSelected = selectedZone === zone.id;
                    return (
                      <button
                        key={zone.id}
                        onClick={() => {
                          setSelectedZone(zone.id);
                          setActiveDrawer('none');
                        }}
                        className={`flex items-center justify-between p-2 rounded-xl text-left transition-all active:scale-[0.99] cursor-pointer ${
                          isSelected
                            ? 'bg-[#2b4c37] text-white shadow-xs'
                            : 'hover:bg-[#fcf9f2] text-[#1c1c18]'
                        }`}
                      >
                        <div className="flex items-center gap-2 min-w-0">
                          <MapPin
                            className={`w-3.5 h-3.5 shrink-0 ${
                              isSelected ? 'text-[#ffddba]' : 'text-[#47654e]'
                            }`}
                          />
                          <div className="flex flex-col min-w-0">
                            <span
                              className={`font-serif text-xs font-semibold leading-tight ${
                                isSelected ? 'text-white' : 'text-[#061b0e]'
                              }`}
                            >
                              {zone.name}
                            </span>
                            <span
                              className={`font-sans text-[10px] truncate ${
                                isSelected ? 'text-white/80' : 'text-[#737973]'
                              }`}
                            >
                              {zone.landmarks}
                            </span>
                          </div>
                        </div>
                        <span
                          className={`text-[11px] font-mono px-2 py-0.5 rounded-full shrink-0 font-medium ${
                            isSelected
                              ? 'bg-white/20 text-white'
                              : 'bg-[#f1eee7] text-[#434843]'
                          }`}
                        >
                          {zone.count} ruta{zone.count === 1 ? '' : 's'}
                        </span>
                      </button>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {/* Active Filter Chips (Removable) */}
          {activeFiltersCount > 0 && (
            <div className="flex items-center gap-1.5 flex-wrap py-1">
              <span className="text-[11px] font-sans text-[#737973] mr-0.5 font-medium">
                {t('catalog_active_filters')}
              </span>

              {/* Price Active Chip */}
              {selectedPriceFilter !== 'all' && (
                <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#c48b47]/15 text-[#8c5717] border border-[#c48b47]/30 text-xs font-sans font-semibold">
                  <Tag className="w-3 h-3 text-[#c48b47]" />
                  <span>
                    Precio:{' '}
                    {selectedPriceFilter === 'free'
                      ? 'Gratis'
                      : selectedPriceFilter === 'paid'
                      ? 'De pago'
                      : selectedPriceFilter === 'under2'
                      ? '< 2 €'
                      : '≥ 2 €'}
                  </span>
                  <button
                    onClick={() => setSelectedPriceFilter('all')}
                    className="hover:text-black ml-0.5"
                    aria-label="Quitar filtro de precio"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}

              {/* Guide Active Chip */}
              {selectedGuideId !== 'all' && (
                <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#1b3022] text-white text-xs font-sans font-semibold shadow-2xs">
                  <UserCheck className="w-3 h-3 text-[#ffddba]" />
                  <span>Guía: {selectedGuideObj?.name || selectedGuideId}</span>
                  <button
                    onClick={() => setSelectedGuideId('all')}
                    className="hover:text-[#ffddba] ml-0.5"
                    aria-label="Quitar filtro de guía"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}

              {/* Zone Active Chip */}
              {selectedZone !== 'all' && (
                <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#2b4c37] text-white text-xs font-sans font-semibold shadow-2xs">
                  <MapPin className="w-3 h-3 text-[#ffddba]" />
                  <span>Zona: {selectedZoneObj?.short || selectedZone}</span>
                  <button
                    onClick={() => setSelectedZone('all')}
                    className="hover:text-[#ffddba] ml-0.5"
                    aria-label="Quitar filtro de zona"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}

              {/* Category Active Chip */}
              {selectedCategory !== 'all' && (
                <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#f1eee7] text-[#434843] border border-[#c3c8c1]/40 text-xs font-sans font-semibold">
                  <span>{t('catalog_category_prefix')} {t(CATEGORY_TRANSLATION_KEYS[selectedCategory] || selectedCategory)}</span>
                  <button
                    onClick={() => setSelectedCategory('all')}
                    className="hover:text-black ml-0.5"
                    aria-label={t('catalog_remove_category')}
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}

              {/* Saved Active Chip */}
              {onlySaved && (
                <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#f1eee7] text-[#434843] border border-[#c3c8c1]/40 text-xs font-sans font-semibold">
                  <Bookmark className="w-3 h-3 fill-current" />
                  <span>{t('catalog_saved')}</span>
                  <button
                    onClick={() => setOnlySaved(false)}
                    className="hover:text-black ml-0.5"
                    aria-label={t('catalog_remove_saved')}
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              )}

              {/* Reset all link */}
              <button
                onClick={handleResetAllFilters}
                className="text-[11px] font-sans text-[#737973] hover:text-[#1c1c18] underline ml-1 cursor-pointer"
              >
                {t('catalog_reset')}
              </button>
            </div>
          )}

          {/* Scrollable Category Chips */}
          <div className="flex items-center gap-2 overflow-x-auto no-scrollbar py-0.5">
            {CATEGORIES.map(cat => {
              const isActive = selectedCategory === cat.id;
              return (
                <button
                  key={cat.id}
                  onClick={() => setSelectedCategory(cat.id)}
                  className={`px-3.5 py-1.5 rounded-full font-sans text-[12px] font-semibold tracking-wide whitespace-nowrap transition-all active:scale-95 cursor-pointer ${
                    isActive
                      ? 'bg-[#1b3022] text-white shadow-xs'
                      : 'bg-[#f1eee7] text-[#434843] hover:bg-[#ebe8e1]'
                  }`}
                >
                  {t(cat.label)}
                </button>
              );
            })}
          </div>
        </div>

        {/* Route Cards Stack */}
        <div className="flex flex-col gap-5 mb-6">
          {filteredRoutes.length === 0 ? (
            <div className="p-8 text-center bg-white rounded-2xl border border-[#e5e2db] my-4 shadow-xs">
              <div className="w-12 h-12 rounded-full bg-[#f1eee7] text-[#47654e] flex items-center justify-center mx-auto mb-3">
                <SlidersHorizontal className="w-5 h-5" />
              </div>
              <p className="font-serif text-[18px] text-[#061b0e] font-semibold mb-1">
                {t('catalog_empty_title')}
              </p>
              <p className="font-sans text-[13px] text-[#434843] mb-4 max-w-xs mx-auto leading-relaxed">
                {t('catalog_empty_description')}
              </p>
              <button
                onClick={handleResetAllFilters}
                className="px-4 py-2.5 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] text-white font-sans text-[13px] font-medium shadow-xs active:scale-95 transition-all inline-flex items-center gap-1.5 cursor-pointer"
              >
                <RotateCcw className="w-3.5 h-3.5" />
                {t('catalog_reset_all')}
              </button>
            </div>
          ) : (
            filteredRoutes.map(route => {
              const isSaved = savedRouteIds.includes(route.id);
              const affinity = getCharacterRouteAffinity(activeChar.id, route.id);
              return (
                <article
                  key={route.id}
                  className="flex flex-col bg-white rounded-2xl border border-[#1b3022]/8 shadow-[0_4px_16px_rgba(27,48,34,0.05)] overflow-hidden transition-all duration-300 hover:shadow-md"
                >
                  {/* Card Media Header */}
                  <div
                    onClick={() => onSelectRoute(route)}
                    className="relative w-full aspect-[16/9] overflow-hidden cursor-pointer group"
                  >
                    <img
                      src={route.image}
                      alt={route.title}
                      className="w-full h-full object-cover object-center transform transition-transform duration-700 group-hover:scale-105"
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-[#061b0e]/85 via-[#061b0e]/20 to-transparent" />

                    {/* Top Badges Overlay */}
                    <div className="absolute top-3 left-3 right-3 flex items-center justify-between pointer-events-none">
                      {route.isFeatured ? (
                        <span className="px-2.5 py-1 rounded-full bg-[#412500]/90 text-[#ffddba] font-sans text-[11px] font-semibold tracking-wide uppercase shadow-xs backdrop-blur-xs">
                          {t('catalog_featured')}
                        </span>
                      ) : (
                        <span className="px-2.5 py-1 rounded-full bg-black/40 text-white font-sans text-[11px] font-medium backdrop-blur-xs">
                          {route.region.split('·')[0].trim()}
                        </span>
                      )}

                      <span className="px-2.5 py-1 rounded-full bg-[#e5e2db]/90 text-[#061b0e] font-sans text-[11px] font-semibold shadow-xs backdrop-blur-xs flex items-center gap-1">
                        <Radio className="w-3 h-3 text-[#412500]" />
                        {route.stopsCount} {t(route.stopsCount === 1 ? 'catalog_stops_one' : 'stops')}
                      </span>
                    </div>

                    {/* Floating Location Tag on Image Bottom */}
                    <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-white font-sans text-[12px] font-medium tracking-wide">
                      <div className="flex items-center gap-1.5">
                        <Navigation className="w-3.5 h-3.5 text-[#ffddba]" />
                        <span className="text-white drop-shadow-xs">{route.location}</span>
                      </div>

                      {/* Bookmark button on card */}
                      <button
                        type="button"
                        onClick={e => {
                          e.stopPropagation();
                          onToggleSaved(route.id);
                        }}
                        className={`w-8 h-8 rounded-full flex items-center justify-center backdrop-blur-md transition-all active:scale-90 cursor-pointer ${
                          isSaved
                            ? 'bg-[#c48b47] text-white shadow-xs'
                            : 'bg-white/30 text-white hover:bg-white/50'
                        }`}
                        aria-label={t('catalog_save_route')}
                      >
                        <Bookmark className={`w-3.5 h-3.5 ${isSaved ? 'fill-current' : ''}`} />
                      </button>
                    </div>
                  </div>

                  {/* Content Area */}
                  <div className="p-4 flex flex-col gap-2">
                    {/* Tagline & Meta Attributes */}
                    <div className="flex items-center justify-between gap-2 flex-wrap">
                      <div className="flex items-center gap-1.5 flex-wrap">
                        {route.categories.slice(0, 2).map(cat => (
                          <span
                            key={cat}
                            className="px-2 py-0.5 rounded bg-[#f1eee7] text-[#434843] font-sans text-[11px] font-medium uppercase tracking-wider"
                          >
                            {t(CATEGORY_TRANSLATION_KEYS[cat.toLowerCase()] || cat)}
                          </span>
                        ))}

                        {!route.isFree && route.price.toLowerCase() !== 'gratis' && (
                          <span className="px-2 py-0.5 rounded bg-[#1b3022] text-[#ffddba] font-serif text-[11px] font-bold">
                            {route.price}
                          </span>
                        )}
                      </div>

                      <div className="flex items-center gap-2">
                        <span className="flex items-center gap-1 font-sans text-[12px] text-[#434843]">
                          <Clock className="w-3.5 h-3.5" />
                          {route.duration}
                        </span>
                        <span className="px-2 py-0.5 rounded-full bg-[#c5e8ca] text-[#4b6a52] font-sans text-[11px] font-semibold">
                          {t(DIFFICULTY_TRANSLATION_KEYS[route.difficulty.toLowerCase()] || route.difficulty)}
                        </span>
                      </div>
                    </div>

                    <h3
                      onClick={() => onSelectRoute(route)}
                      className="font-serif text-[22px] font-semibold text-[#061b0e] hover:text-[#2c4a34] cursor-pointer transition-colors mt-0.5"
                    >
                      {route.title}
                    </h3>

                    {/* Location & Guide Author Line */}
                    <div className="flex items-center justify-between gap-2 flex-wrap text-xs text-[#47654e] font-serif border-y border-[#f1eee7] py-1.5">
                      <span className="flex items-center gap-1">
                        <Navigation className="w-3.5 h-3.5 text-[#c48b47]" />
                        <span className="text-[#1c1c18] font-medium">{route.location}</span>
                      </span>

                      {route.guide && (
                        <span className="flex items-center gap-1.5">
                          {route.guide.avatarUrl ? (
                            <img
                              src={route.guide.avatarUrl}
                              alt={route.guide.name}
                              className="w-4 h-4 rounded-full object-cover border border-[#c48b47]/40"
                            />
                          ) : (
                            <User className="w-3.5 h-3.5 text-[#c48b47]" />
                          )}
                          <span className="text-[#47654e]">{t('catalog_guide_label')}</span>
                          <span className="font-semibold text-[#0f2416]">
                            {route.guide.name}
                          </span>
                          {route.guide.verified && (
                            <CheckCircle2 className="w-3 h-3 text-[#c48b47]" />
                          )}
                        </span>
                      )}
                    </div>

                    <p className="font-sans text-[14px] text-[#434843] line-clamp-2 leading-snug">
                      {route.description}
                    </p>

                    {/* Character Affinity Highlight */}
                    {affinity.hasAffinity && (
                      <div className="flex items-center gap-2 p-2 rounded-xl bg-amber-50 border border-amber-300/80 text-amber-950 text-xs font-sans">
                        <CharacterAvatar character={activeChar} className="h-6 w-6 shrink-0" />
                        <div className="flex flex-col min-w-0">
                          <span className="font-bold text-[11px] text-amber-900 leading-tight">
                            {affinity.badgeLabel}
                          </span>
                          <span className="text-[10px] text-amber-800/90 truncate">
                            {affinity.bonusDescription}
                          </span>
                        </div>
                      </div>
                    )}

                    {/* CTA Action */}
                    <div className="mt-2 pt-2 border-t border-[#f1eee7] flex items-center justify-between">
                      <div className="flex items-center gap-1.5 text-[#434843] font-sans text-[13px]">
                        {route.id === 'contrabando' && <Headphones className="w-4 h-4 text-[#47654e]" />}
                        {route.id === 'leyendas' && <Trees className="w-4 h-4 text-[#47654e]" />}
                        {route.id === 'marinera' && <Waves className="w-4 h-4 text-[#47654e]" />}
                        <span>{route.audioTag}</span>
                      </div>

                      <button
                        onClick={() => onSelectRoute(route)}
                        className={`h-10 px-4 rounded-lg font-sans text-[13px] font-semibold flex items-center gap-1.5 shadow-xs active:scale-95 transition-all cursor-pointer ${
                          route.isFeatured
                            ? 'bg-[#1b3022] text-white hover:bg-[#061b0e]'
                            : 'bg-[#f1eee7] text-[#061b0e] hover:bg-[#ebe8e1]'
                        }`}
                      >
                        <span>{t('catalog_view_route')}</span>
                        <ArrowRight className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </article>
              );
            })
          )}
        </div>

        {/* Ambient Micro-Audio Bar */}
        <div className="mb-6 p-4 rounded-2xl bg-[#ebe8e1] border border-[#dcdad3] flex items-center justify-between gap-3 shadow-xs">
          <div className="flex items-center gap-3 min-w-0">
            <div
              className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 transition-colors ${
                isAmbientPlaying ? 'bg-[#c48b47] text-white animate-pulse' : 'bg-[#1b3022] text-white'
              }`}
            >
              <Radio className="w-5 h-5" />
            </div>

            <div className="flex flex-col min-w-0">
              <span className="font-sans text-[11px] font-semibold uppercase tracking-wider text-[#47654e]">
                {t('catalog_ambient_title')}
              </span>
              <span className="font-sans text-[13px] text-[#1c1c18] font-medium truncate">
                {isAmbientPlaying
                  ? t('catalog_ambient_playing')
                  : t('catalog_ambient_idle')}
              </span>
            </div>
          </div>

          <button
            onClick={handleToggleAmbient}
            aria-label={isAmbientPlaying ? t('catalog_pause_sound') : t('catalog_play_ambient')}
            className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 active:scale-90 transition-transform shadow-xs cursor-pointer ${
              isAmbientPlaying ? 'bg-[#061b0e] text-white' : 'bg-white text-[#061b0e]'
            }`}
          >
            {isAmbientPlaying ? <Square className="w-4 h-4 fill-current" /> : <Play className="w-4 h-4 fill-current ml-0.5" />}
          </button>
        </div>
      </main>
    </div>
  );
};
