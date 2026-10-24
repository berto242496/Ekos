import React, { useState } from 'react';
import { RouteItem, UserProfile, GuideAuthor, UserPurchaseRecord } from '../types';
import { APP_LOGO_URL } from '../data/routesData';
import { getCharacterById, getCharacterRouteAffinity } from '../data/charactersData';
import { CharacterAvatar } from './CharacterAvatar';
import { UserCheckoutModal } from './UserCheckoutModal';
import { LanguageSelector } from './LanguageSelector';
import { useI18n } from '../services/i18n';
import {
  ArrowLeft,
  Bookmark,
  Share2,
  Clock,
  Ruler,
  Compass,
  Footprints,
  Map as MapIcon,
  Mic,
  Brain,
  Search,
  Castle,
  ShieldCheck,
  ArrowRight,
  User,
  Check,
  Sparkles,
  Star,
  CheckCircle2,
  Lock,
  CreditCard,
  MapPin,
} from 'lucide-react';

interface RouteDetailProps {
  route: RouteItem;
  isSaved: boolean;
  user?: UserProfile;
  onBack: () => void;
  onToggleSaved: (routeId: string) => void;
  onOpenMap: () => void;
  onStartRoute: (route: RouteItem) => void;
  onOpenProfile: () => void;
  onOpenGuideProfile?: (guide: GuideAuthor) => void;
  onPurchaseRoute?: (route: RouteItem, purchaseRecord: UserPurchaseRecord) => void;
}

export const RouteDetail: React.FC<RouteDetailProps> = ({
  route,
  isSaved,
  user,
  onBack,
  onToggleSaved,
  onOpenMap,
  onStartRoute,
  onOpenProfile,
  onOpenGuideProfile,
  onPurchaseRoute,
}) => {
  const { t } = useI18n();
  const [copiedShare, setCopiedShare] = useState(false);
  const [isCheckoutOpen, setIsCheckoutOpen] = useState(false);
  const activeChar = getCharacterById(user?.characterId || 'meiga');
  const affinity = getCharacterRouteAffinity(activeChar.id, route.id);

  const isPaidRoute = !route.isFree && Boolean(route.price) && !route.price.toLowerCase().includes('gratis');
  const isPurchased = Boolean(user?.purchasedRouteIds?.includes(route.id));
  const hasAccess = !isPaidRoute || isPurchased;

  const handleShare = () => {
    if (navigator.share) {
      navigator
        .share({
          title: `${route.title} - Ecos do Lugar`,
          text: route.description,
          url: window.location.href,
        })
        .catch(() => {});
    } else {
      navigator.clipboard.writeText(window.location.href);
      setCopiedShare(true);
      setTimeout(() => setCopiedShare(false), 2000);
    }
  };

  return (
    <div className="flex flex-col w-full min-h-screen bg-[#fcf9f2] text-[#1c1c18] pb-28">
      {/* Fixed Navigation Bar */}
      <header className="fixed top-0 w-full z-50 pt-safe bg-[#fcf9f2]/85 backdrop-blur-xl border-b border-[#1b3022]/5 shadow-[0_1px_12px_rgba(27,48,34,0.03)]">
        <div className="h-16 px-5 flex items-center justify-between gap-3 max-w-lg mx-auto">
          <div className="flex items-center gap-2 min-w-0">
            <button
              onClick={onBack}
              aria-label="Volver a rutas"
              className="w-10 h-10 -ml-2 rounded-full flex items-center justify-center text-[#1c1c18] hover:bg-[#ebe8e1] transition-colors cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <img
              alt="Ecos do Lugar Logo"
              className="h-8 w-auto object-contain shrink-0"
              src={APP_LOGO_URL}
            />
            <h1 className="font-serif text-[18px] text-[#1c1c18] font-semibold truncate leading-tight">
              Detalle De Ruta
            </h1>
          </div>

          <div className="flex items-center gap-2 shrink-0">
            <LanguageSelector variant="compact" />

            <button
              onClick={onOpenProfile}
              aria-label="Perfil de usuario"
              className="w-8 h-8 rounded-full bg-[#061b0e] flex items-center justify-center text-white active:scale-95 transition-transform cursor-pointer"
            >
              <User className="w-4 h-4 text-white" />
            </button>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <div className="w-full max-w-lg mx-auto pt-16">
        {/* Hero Section with High-Res Photography */}
        <div className="relative w-full h-80 overflow-hidden bg-[#061b0e]">
          <img
            src={route.image}
            alt={route.title}
            className="w-full h-full object-cover object-center scale-105 transition-transform duration-1000 ease-out"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#fcf9f2] via-[#061b0e]/30 to-transparent" />

          {/* Floating Top Action Badges */}
          <div className="absolute top-4 inset-x-0 px-5 flex items-center justify-between pointer-events-none">
            <span className="pointer-events-auto inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/90 backdrop-blur-md shadow-sm text-[#47654e] font-sans text-[12px] font-semibold uppercase tracking-wider">
              <span className="w-1.5 h-1.5 rounded-full bg-[#c48b47] animate-pulse" />
              Ruta Cultural de Galicia
            </span>

            <div className="pointer-events-auto flex items-center gap-2">
              <button
                onClick={() => onToggleSaved(route.id)}
                aria-label="Guardar ruta en cuaderno de campo"
                className={`w-10 h-10 rounded-full backdrop-blur-md flex items-center justify-center shadow-md active:scale-95 transition-all cursor-pointer ${
                  isSaved
                    ? 'bg-[#1b3022] text-white'
                    : 'bg-white/90 text-[#1c1c18] hover:bg-white'
                }`}
              >
                <Bookmark className={`w-5 h-5 ${isSaved ? 'fill-current' : ''}`} />
              </button>

              <button
                onClick={handleShare}
                aria-label="Compartir expedición"
                className="w-10 h-10 rounded-full bg-white/90 backdrop-blur-md text-[#1c1c18] flex items-center justify-center shadow-md active:scale-95 transition-all hover:bg-white cursor-pointer"
                title="Compartir ruta"
              >
                {copiedShare ? <Check className="w-5 h-5 text-emerald-600" /> : <Share2 className="w-5 h-5" />}
              </button>
            </div>
          </div>

          {/* Bottom Vignette Micro-Badge */}
          <div className="absolute bottom-8 left-5 flex items-center gap-2">
            <span className="px-2.5 py-0.5 rounded-full bg-[#1b3022]/85 backdrop-blur-sm text-[#819986] font-sans text-[11px] font-medium border border-white/10">
              {route.tomoRef}
            </span>
          </div>
        </div>

        {/* Main Overlapping Editorial Sheet */}
        <div className="relative -mt-6 rounded-t-3xl bg-[#fcf9f2] px-5 pt-6 shadow-xl flex flex-col gap-6">
          {/* Title & Geo Context */}
          <div className="flex flex-col gap-2">
            <div className="flex items-center justify-between gap-2 flex-wrap">
              <span className="text-[#47654e] font-sans text-[12px] font-bold tracking-wide uppercase">
                {route.region || route.location}
              </span>

              {/* Price / Gratis / Purchased Tag */}
              {isPurchased ? (
                <span className="px-3 py-1 rounded-full font-serif text-xs font-bold shadow-xs bg-emerald-100 text-emerald-900 border border-emerald-300 flex items-center gap-1.5">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-700" />
                  <span>Ruta Adquirida</span>
                </span>
              ) : (
                <span
                  className={`px-3 py-1 rounded-full font-serif text-xs font-bold shadow-xs ${
                    route.isFree
                      ? 'bg-emerald-100 text-emerald-900 border border-emerald-300'
                      : 'bg-[#1b3022] text-[#ffddba] border border-[#c48b47]/40'
                  }`}
                >
                  {route.price || (route.isFree ? 'Gratis' : '1,99 €')}
                </span>
              )}
            </div>

            <h2 className="font-serif text-[32px] font-semibold text-[#1c1c18] tracking-tight leading-tight">
              {route.title}
            </h2>

            <p className="font-sans text-[15px] text-[#434843] leading-relaxed pt-1">
              {route.description}
            </p>
          </div>

          {/* Banner de Adquisición para Rutas de Pago (se aún non foi adquirida) */}
          {isPaidRoute && !isPurchased && (
            <div className="p-4 sm:p-5 rounded-2xl bg-gradient-to-r from-[#1b3022] to-[#0c2214] text-[#fcf9f2] border border-[#c48b47]/30 shadow-md flex flex-col sm:flex-row sm:items-center justify-between gap-3.5">
              <div className="flex items-start gap-3.5 min-w-0">
                <div className="w-10 h-10 rounded-xl bg-white/10 border border-white/15 flex items-center justify-center shrink-0 text-[#ffddba]">
                  <CreditCard className="w-5 h-5" />
                </div>
                <div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <span className="font-serif text-base font-bold text-white">
                      Desbloquear experiencia completa
                    </span>
                    <span className="px-2 py-0.5 rounded-full bg-[#c48b47] text-[#061b0e] font-mono text-xs font-bold">
                      {route.price}
                    </span>
                  </div>
                  <p className="text-xs text-[#ffddba]/90 font-serif mt-0.5 leading-relaxed">
                    Audios narrados en alta fidelidade, enigmas e mapa sen conexión. O 85% transfírese ao guía local.
                  </p>
                </div>
              </div>

              <button
                id="btn-banner-unlock"
                type="button"
                onClick={() => setIsCheckoutOpen(true)}
                className="py-2.5 px-4 rounded-xl bg-[#c48b47] hover:bg-[#d89e5a] text-[#061b0e] font-serif font-bold text-xs shrink-0 transition-colors shadow-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <Lock className="w-3.5 h-3.5" />
                <span>Desbloquear agora</span>
              </button>
            </div>
          )}

          {/* Section: Autor y Guía Creador de la Ruta */}
          {route.guide && (
            <div className="p-4 sm:p-5 rounded-2xl bg-white border border-[#1b3022]/10 shadow-xs flex flex-col gap-3">
              <div className="flex items-center justify-between">
                <span className="text-xs font-serif uppercase tracking-wider font-semibold text-[#8c5717]">
                  Guía da Ruta
                </span>
                <span
                  className={`text-xs font-serif font-bold px-2.5 py-0.5 rounded-full ${
                    isPurchased
                      ? 'bg-emerald-50 text-emerald-800 border border-emerald-300'
                      : route.isFree
                      ? 'bg-emerald-50 text-emerald-800 border border-emerald-300'
                      : 'bg-[#1b3022]/5 text-[#1b3022] border border-[#1b3022]/15'
                  }`}
                >
                  {isPurchased ? 'Adquirida' : route.price || (route.isFree ? 'Gratis' : '1,99 €')}
                </span>
              </div>

              <div className="flex items-start gap-3.5">
                {route.guide.avatarUrl ? (
                  <img
                    src={route.guide.avatarUrl}
                    alt={route.guide.name}
                    className="w-13 h-13 rounded-2xl object-cover border-2 border-[#c48b47]/40 shadow-xs shrink-0"
                  />
                ) : (
                  <div className="w-13 h-13 rounded-2xl bg-[#1b3022] text-[#fcf9f2] flex items-center justify-center font-serif text-lg font-bold shrink-0">
                    {route.guide.name.charAt(0)}
                  </div>
                )}

                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-1.5 mb-0.5">
                    <h4 className="font-serif text-[17px] font-bold text-[#0f2416]">
                      {route.guide.name}
                    </h4>
                    {route.guide.verified && (
                      <CheckCircle2 className="w-4 h-4 text-[#c48b47] shrink-0" title="Guía Oficial Verificado" />
                    )}
                  </div>
                  <p className="text-xs text-[#47654e] font-serif mb-1 truncate">
                    {route.guide.title}
                  </p>
                  
                  {/* Valoración */}
                  <div className="flex items-center gap-1.5 text-xs font-serif">
                    <span className="inline-flex items-center gap-1 font-bold text-[#8c5717]">
                      <Star className="w-3.5 h-3.5 fill-[#c48b47] text-[#c48b47]" />
                      {route.guide.rating}
                    </span>
                    <span className="text-[#47654e]">
                      ({route.guide.reviewsCount} reseñas)
                    </span>
                  </div>
                </div>
              </div>

              {/* Breve descripción del guía */}
              <p className="text-xs sm:text-sm font-serif text-[#334b39] leading-relaxed pt-2 border-t border-[#1b3022]/5">
                {route.guide.bio}
              </p>

              {/* Botón Ver perfil del guía */}
              <button
                id="btn-view-guide-profile"
                type="button"
                onClick={() => onOpenGuideProfile && onOpenGuideProfile(route.guide!)}
                className="w-full mt-1 py-2 px-3 rounded-xl bg-[#fcf9f2] hover:bg-[#f3eee2] border border-[#1b3022]/15 text-[#0f2416] font-serif font-semibold text-xs transition-colors flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <span>Ver perfil del guía</span>
                <ArrowRight className="w-3.5 h-3.5 text-[#c48b47]" />
              </button>
            </div>
          )}

          {/* Quick Metrics Bento Grid */}
          <div className="grid grid-cols-2 gap-2.5">
            {/* Metric 1: Duration */}
            <div className="p-3.5 rounded-2xl bg-[#f6f3ec] border border-[#c3c8c1]/20 shadow-2xs flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#e5e2db] flex items-center justify-center shrink-0 text-[#061b0e]">
                <Clock className="w-5 h-5" />
              </div>
              <div className="flex flex-col min-w-0">
                <span className="font-serif text-[18px] font-semibold text-[#1c1c18] leading-none truncate">
                  {route.duration}
                </span>
                <span className="font-sans text-[11px] text-[#434843] mt-1 truncate">
                  Duración estimada
                </span>
              </div>
            </div>

            {/* Metric 2: Distance */}
            <div className="p-3.5 rounded-2xl bg-[#f6f3ec] border border-[#c3c8c1]/20 shadow-2xs flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#e5e2db] flex items-center justify-center shrink-0 text-[#061b0e]">
                <Ruler className="w-5 h-5" />
              </div>
              <div className="flex flex-col min-w-0">
                <span className="font-serif text-[18px] font-semibold text-[#1c1c18] leading-none truncate">
                  {route.distance}
                </span>
                <span className="font-sans text-[11px] text-[#434843] mt-1 truncate">
                  Distancia a pie
                </span>
              </div>
            </div>

            {/* Metric 3: Stops */}
            <div className="p-3.5 rounded-2xl bg-[#f6f3ec] border border-[#c3c8c1]/20 shadow-2xs flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#e5e2db] flex items-center justify-center shrink-0 text-[#061b0e]">
                <Compass className="w-5 h-5" />
              </div>
              <div className="flex flex-col min-w-0">
                <span className="font-serif text-[18px] font-semibold text-[#1c1c18] leading-none truncate">
                  {route.stopsCount} paradas
                </span>
                <span className="font-sans text-[11px] text-[#434843] mt-1 truncate">
                  Puntos clave
                </span>
              </div>
            </div>

            {/* Metric 4: Difficulty */}
            <div className="p-3.5 rounded-2xl bg-[#f6f3ec] border border-[#c3c8c1]/20 shadow-2xs flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-[#e5e2db] flex items-center justify-center shrink-0 text-[#47654e]">
                <Footprints className="w-5 h-5" />
              </div>
              <div className="flex flex-col min-w-0">
                <span className="font-serif text-[18px] font-semibold text-[#1c1c18] leading-none truncate">
                  {route.difficulty}
                </span>
                <span className="font-sans text-[11px] text-[#434843] mt-1 truncate">
                  Dificultad baja
                </span>
              </div>
            </div>
          </div>

          {/* Active Character Expedition Synergy Box */}
          <div className="p-4 rounded-2xl bg-gradient-to-r from-[#1b3022] to-[#091b10] text-white border border-[#2b4c37] shadow-sm flex flex-col gap-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <CharacterAvatar character={activeChar} className="h-8 w-8" />
                <div>
                  <span className="text-[10px] uppercase font-bold text-[#ffddba] tracking-wider block">
                    Compañeiro Activo
                  </span>
                  <span className="font-serif text-[15px] font-bold text-white">
                    {activeChar.name} · {activeChar.archetype}
                  </span>
                </div>
              </div>
              {affinity.hasAffinity && (
                <span className="px-2 py-0.5 rounded-full bg-amber-400 text-amber-950 font-sans text-[11px] font-bold">
                  Sintonía Mágica
                </span>
              )}
            </div>

            <p className="font-sans text-[12px] text-[#ebe8e1] leading-relaxed">
              {affinity.hasAffinity
                ? `${affinity.badgeLabel}: ${affinity.bonusDescription}`
                : `Tu personaje aportará su don (${activeChar.abilityName}) para ayudarte a resolver los enigmas y retos de esta ruta.`}
            </p>
          </div>

          {/* Interactive Map Peek Strip */}
          <div className="w-full p-4 rounded-2xl bg-[#ebe8e1] border border-[#dcdad3] shadow-2xs flex items-center justify-between gap-4">
            <div className="flex items-center gap-3 min-w-0">
              <div className="relative w-12 h-12 rounded-xl bg-[#1b3022] overflow-hidden shrink-0 flex items-center justify-center shadow-xs">
                <MapIcon className="w-6 h-6 text-white" />
              </div>
              <div className="flex flex-col min-w-0">
                <span className="font-serif text-[16px] font-semibold text-[#1c1c18] truncate">
                  Trazado cartográfico
                </span>
                <span className="font-sans text-[13px] text-[#434843] truncate">
                  {route.subtitle}
                </span>
              </div>
            </div>

            <button
              onClick={onOpenMap}
              className="shrink-0 h-10 px-3.5 rounded-full bg-white text-[#1c1c18] font-sans text-[12px] font-semibold shadow-2xs hover:bg-[#fcf9f2] active:scale-95 transition-all flex items-center gap-1 cursor-pointer border border-[#c3c8c1]/30"
            >
              <span>Ver mapa</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Section: Qué descubrirás */}
          <div className="flex flex-col gap-3 pt-2">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Compass className="w-5 h-5 text-[#47654e]" />
                <h3 className="font-serif text-[22px] font-semibold text-[#1c1c18]">
                  Qué descubrirás
                </h3>
              </div>
              <span className="font-sans text-[11px] font-semibold text-[#434843] uppercase tracking-wider">
                {route.overviewFeatures.length} HITOS EDITORIALES
              </span>
            </div>

            {/* Discovery Cards Stack */}
            <div className="flex flex-col gap-3">
              {route.overviewFeatures.map((feat, index) => (
                <div
                  key={index}
                  className="p-4 rounded-2xl bg-white border border-[#1b3022]/8 shadow-[0_2px_8px_rgba(27,48,34,0.03)] flex items-start gap-3.5 transition-all"
                >
                  <div className="w-9 h-9 rounded-full bg-[#f1eee7] text-[#061b0e] flex items-center justify-center shrink-0 mt-0.5 border border-[#e5e2db]">
                    {index === 0 && <Mic className="w-4 h-4 text-[#47654e]" />}
                    {index === 1 && <Brain className="w-4 h-4 text-[#061b0e]" />}
                    {index === 2 && <Search className="w-4 h-4 text-[#c48b47]" />}
                    {index === 3 && <Castle className="w-4 h-4 text-[#061b0e]" />}
                  </div>

                  <div className="flex flex-col min-w-0">
                    <h4 className="font-serif text-[17px] font-semibold text-[#1c1c18]">
                      {feat.title}
                    </h4>
                    <p className="font-sans text-[14px] text-[#434843] mt-1 leading-normal">
                      {feat.description}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Paradas del Itinerario y Retos de Autor */}
          {route.stops && route.stops.length > 0 && (
            <div className="flex flex-col gap-3 pt-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <MapPin className="w-5 h-5 text-[#c48b47]" />
                  <h3 className="font-serif text-[20px] font-semibold text-[#1c1c18]">
                    Paradas y Retos ({route.stops.length})
                  </h3>
                </div>
                <span className="font-sans text-[11px] font-semibold text-[#434843] uppercase tracking-wider">
                  {route.defaultVoiceId ? `Voz: ${route.defaultVoiceId}` : 'Ruta guiada'}
                </span>
              </div>

              <div className="space-y-3">
                {route.stops.map((stop, i) => (
                  <div
                    key={stop.id || i}
                    className="p-3.5 rounded-2xl bg-white border border-[#1b3022]/8 shadow-2xs flex items-start gap-3.5"
                  >
                    <span className="w-7 h-7 rounded-full bg-[#1b3022] text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">
                      {i + 1}
                    </span>

                    {stop.imageUrl && (
                      <div className="w-16 h-14 rounded-xl overflow-hidden shrink-0 border border-[#1b3022]/10">
                        <img src={stop.imageUrl} alt={stop.title} className="w-full h-full object-cover" />
                      </div>
                    )}

                    <div className="flex-1 min-w-0">
                      <h4 className="font-serif text-[15px] font-bold text-[#1c1c18] truncate">
                        {stop.title}
                      </h4>
                      <p className="font-sans text-[11px] text-[#47654e] font-semibold">
                        📍 {stop.locationName}
                      </p>
                      <p className="font-sans text-[12px] text-[#434843] line-clamp-2 mt-0.5">
                        {stop.story}
                      </p>

                      <div className="flex flex-wrap items-center gap-1.5 mt-2">
                        {stop.trial?.type === 'acertijo' && (
                          <span className="text-[10px] font-serif font-bold text-[#8c5717] bg-[#c48b47]/15 px-2 py-0.5 rounded-md">
                            ❓ Acertijo con Opciones
                          </span>
                        )}
                        {stop.trial?.type === 'cifrado_magico' && (
                          <span className="text-[10px] font-serif font-bold text-[#2b5338] bg-[#47654e]/15 px-2 py-0.5 rounded-md">
                            🔤 Cifrado de Letras
                          </span>
                        )}
                        {stop.trial?.type === 'puzle_runico' && (
                          <span className="text-[10px] font-serif font-bold text-[#553c8b] bg-[#9333ea]/15 px-2 py-0.5 rounded-md">
                            🌀 Puzle Rúnico
                          </span>
                        )}
                        {stop.voiceId && (
                          <span className="text-[10px] font-serif text-[#47654e] bg-[#f1eee7] px-2 py-0.5 rounded-md">
                            🎙️ Voz: {stop.voiceId}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Reassurance & Privacy Note */}
          <div className="py-3 px-4 rounded-xl bg-[#f1eee7] border border-[#e5e2db] flex items-center gap-3 mb-2">
            <ShieldCheck className="w-5 h-5 text-[#47654e] shrink-0" />
            <p className="font-sans text-[11px] text-[#434843] leading-relaxed">
              La ubicación se usará solo durante la experiencia para activar relatos acústicos en el punto exacto.
            </p>
          </div>
        </div>
      </div>

      {/* Persistent Sticky Bottom Action Bar */}
      <div className="sticky bottom-0 inset-x-0 z-40 bg-[#fcf9f2]/95 backdrop-blur-xl border-t border-[#1b3022]/10 shadow-[0_-8px_24px_rgba(27,48,34,0.08)] pb-safe w-full mt-auto">
        <div className="h-16 px-4 w-full flex items-center gap-2.5">
          <button
            onClick={onOpenMap}
            className="h-11 px-3.5 rounded-full bg-transparent text-[#1c1c18] font-sans text-xs font-medium hover:bg-[#f1eee7] transition-colors shrink-0 active:scale-95 cursor-pointer border border-[#1b3022]/15"
          >
            Ver mapa
          </button>

          {hasAccess ? (
            <button
              id="btn-start-route"
              onClick={() => onStartRoute(route)}
              className="flex-1 h-11 rounded-full bg-[#1b3022] text-white flex items-center justify-center gap-1.5 shadow-md hover:bg-[#061b0e] active:scale-[0.98] transition-all cursor-pointer min-w-0"
            >
              <span className="font-serif text-sm font-semibold tracking-wide truncate">
                Empezar con {activeChar.name.split(' ')[0]}
              </span>
              <CharacterAvatar character={activeChar} className="h-5 w-5" />
              <ArrowRight className="w-3.5 h-3.5 text-[#ffddba] shrink-0" />
            </button>
          ) : (
            <button
              id="btn-unlock-route"
              onClick={() => setIsCheckoutOpen(true)}
              className="flex-1 h-11 rounded-full bg-[#1b3022] text-white flex items-center justify-center gap-1.5 shadow-md hover:bg-[#061b0e] active:scale-[0.98] transition-all cursor-pointer min-w-0"
            >
              <Lock className="w-3.5 h-3.5 text-[#c48b47] shrink-0" />
              <span className="font-serif text-xs font-semibold tracking-wide truncate">
                Desbloquear · {route.price || '1,99 €'}
              </span>
              <ArrowRight className="w-3.5 h-3.5 text-[#ffddba] shrink-0" />
            </button>
          )}
        </div>
      </div>

      {/* User Checkout Modal */}
      <UserCheckoutModal
        route={route}
        user={
          user || {
            name: 'Explorador de Galicia',
            email: 'bert2424.b2@gmail.com',
            isVisitor: true,
            characterId: 'meiga',
            savedRouteIds: [],
            visitedStopIds: [],
            solvedEnigmaIds: [],
          }
        }
        isOpen={isCheckoutOpen}
        onClose={() => setIsCheckoutOpen(false)}
        onPurchaseSuccess={(record) => {
          if (onPurchaseRoute) {
            onPurchaseRoute(route, record);
          }
        }}
        onStartRouteImmediately={() => {
          onStartRoute(route);
        }}
      />
    </div>
  );
};
