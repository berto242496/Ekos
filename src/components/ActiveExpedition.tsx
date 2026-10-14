import React, { useState, useEffect } from 'react';
import { RouteItem, RouteStop, UserProfile } from '../types';
import { soundEngine } from '../services/soundEngine';
import {
  getCharacterById,
  getCharacterRouteAffinity,
  getCharacterCompanionAdvice,
} from '../data/charactersData';
import { TrialCard } from './TrialCard';
import {
  ArrowLeft,
  Volume2,
  VolumeX,
  Play,
  Pause,
  CheckCircle2,
  MapPin,
  ChevronRight,
  ChevronLeft,
  Sparkles,
  BookOpen,
  Zap,
  Compass,
  Eye,
} from 'lucide-react';

interface ActiveExpeditionProps {
  route: RouteItem;
  user: UserProfile;
  visitedStopIds: string[];
  solvedEnigmaIds: string[];
  onBack: () => void;
  onCheckInStop: (stopId: string) => void;
  onSolveEnigma: (stopId: string, attempts: number) => void;
  onAddNote: (stop: RouteStop) => void;
  onUseCharacterDon?: () => void;
  onCompleteRoute?: (routeId: string) => void;
}

export const ActiveExpedition: React.FC<ActiveExpeditionProps> = ({
  route,
  user,
  visitedStopIds,
  solvedEnigmaIds,
  onBack,
  onCheckInStop,
  onSolveEnigma,
  onAddNote,
  onUseCharacterDon,
  onCompleteRoute,
}) => {
  const [currentStopIndex, setCurrentStopIndex] = useState(0);
  const [isPlayingAudio, setIsPlayingAudio] = useState(false);
  const [isPlayingCharAudio, setIsPlayingCharAudio] = useState(false);
  const [isDonActive, setIsDonActive] = useState(false);
  const [donFlash, setDonFlash] = useState(false);
  const [previewLayout, setPreviewLayout] = useState<'carousel' | 'grid'>('carousel');

  const character = getCharacterById(user.characterId || 'meiga');
  const affinity = getCharacterRouteAffinity(character.id, route.id);
  const currentStop = route.stops[currentStopIndex] || route.stops[0];
  const isVisited = visitedStopIds.includes(currentStop.id);
  const isEnigmaSolved = solvedEnigmaIds.includes(currentStop.id);

  // Visited stops count for the current route
  const totalStops = route.stops.length;
  const visitedStopsCount = route.stops.filter(s => visitedStopIds.includes(s.id)).length;
  const progressPercent = totalStops > 0 ? Math.round((visitedStopsCount / totalStops) * 100) : 0;

  const companionAdvice = getCharacterCompanionAdvice(character.id, currentStop.title);

  useEffect(() => {
    const unsub = soundEngine.subscribeSpeech(speaking => {
      if (!speaking) {
        setIsPlayingAudio(false);
        setIsPlayingCharAudio(false);
      }
    });
    return () => {
      unsub();
      soundEngine.stopSpeech();
    };
  }, []);

  // Stop speech if stop changes
  useEffect(() => {
    soundEngine.stopSpeech();
    setIsPlayingAudio(false);
    setIsPlayingCharAudio(false);
    setIsDonActive(false);
  }, [currentStopIndex]);

  const toggleNarrativeAudio = () => {
    if (isPlayingAudio) {
      soundEngine.stopSpeech();
      setIsPlayingAudio(false);
    } else {
      soundEngine.stopSpeech();
      setIsPlayingCharAudio(false);
      setIsPlayingAudio(true);
      const effectiveVoice = currentStop.voiceId || route.defaultVoiceId || 'breogan';
      const textToSpeak =
        currentStop.audioNarration?.trim() ||
        `${currentStop.title}. ${currentStop.subtitle}. ${currentStop.story}. ${
          currentStop.historicalNote ? `Nota histórica: ${currentStop.historicalNote}` : ''
        }`;
      soundEngine.speak(textToSpeak, () => setIsPlayingAudio(false), effectiveVoice);
    }
  };

  const toggleCompanionAudio = () => {
    if (isPlayingCharAudio) {
      soundEngine.stopSpeech();
      setIsPlayingCharAudio(false);
    } else {
      soundEngine.stopSpeech();
      setIsPlayingAudio(false);
      setIsPlayingCharAudio(true);
      soundEngine.speak(
        `${character.name} advierte: ${companionAdvice}`,
        () => setIsPlayingCharAudio(false)
      );
    }
  };

  const handleTriggerDon = () => {
    setIsDonActive(true);
    setDonFlash(true);
    soundEngine.playMysticChime('magic');
    if (onUseCharacterDon) {
      onUseCharacterDon();
    }
    setTimeout(() => setDonFlash(false), 1200);
  };

  const handleNext = () => {
    if (currentStopIndex < route.stops.length - 1) {
      setCurrentStopIndex(currentStopIndex + 1);
    }
  };

  const handlePrev = () => {
    if (currentStopIndex > 0) {
      setCurrentStopIndex(currentStopIndex - 1);
    }
  };

  return (
    <div className="flex flex-col w-full min-h-screen bg-[#fcf9f2] text-[#1c1c18] pb-24">
      {/* Top Header */}
      <header className="sticky top-0 w-full z-40 pt-safe bg-[#fcf9f2]/90 backdrop-blur-xl border-b border-[#1b3022]/10 shadow-2xs">
        <div className="h-16 px-5 flex items-center justify-between gap-3 max-w-lg mx-auto">
          <button
            onClick={() => {
              soundEngine.stopSpeech();
              onBack();
            }}
            className="w-10 h-10 -ml-2 rounded-full flex items-center justify-center text-[#1c1c18] hover:bg-[#ebe8e1] transition-colors cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <div className="flex flex-col items-center min-w-0">
            <span className="font-sans text-[11px] font-semibold text-[#47654e] uppercase tracking-wider truncate max-w-[200px]">
              {route.title}
            </span>
            <h2 className="font-serif text-[17px] font-semibold text-[#1c1c18]">
              Parada {currentStopIndex + 1} de {route.stops.length}
            </h2>
          </div>

          {/* Active Companion Avatar in Header */}
          <div
            className="w-10 h-10 rounded-full bg-[#1b3022] flex items-center justify-center text-xl shadow-xs border border-white/40 cursor-pointer"
            title={`Compañero Activo: ${character.name}`}
          >
            {character.avatarEmoji}
          </div>
        </div>
      </header>

      {/* Main Expedition Content */}
      <main className="w-full max-w-lg mx-auto px-5 pt-4 flex flex-col gap-4">
        {/* Barra visual de progreso de paradas visitadas */}
        <div className="bg-white rounded-2xl p-3.5 border border-[#1b3022]/10 shadow-xs flex flex-col gap-2">
          <div className="flex items-center justify-between text-xs">
            <div className="flex items-center gap-1.5 font-semibold text-[#061b0e]">
              <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>Progreso de la ruta</span>
            </div>
            <span className="font-medium text-[#47654e]">
              {visitedStopsCount} de {totalStops} visitadas ({progressPercent}%)
            </span>
          </div>

          {/* Barra de progreso continua */}
          <div className="w-full h-2.5 bg-[#ebe8e1] rounded-full overflow-hidden">
            <div
              className="h-full bg-emerald-700 rounded-full transition-all duration-300"
              style={{ width: `${progressPercent}%` }}
            />
          </div>

          {/* Segmentos clickables de paradas */}
          <div className="flex gap-1.5 w-full pt-0.5">
            {route.stops.map((st, idx) => {
              const isStopVisited = visitedStopIds.includes(st.id);
              const isCurrent = idx === currentStopIndex;
              return (
                <button
                  key={st.id}
                  onClick={() => setCurrentStopIndex(idx)}
                  className={`h-2 flex-1 rounded-full transition-all cursor-pointer ${
                    isCurrent
                      ? 'ring-2 ring-emerald-600 ring-offset-1 bg-[#1b3022]'
                      : isStopVisited
                      ? 'bg-emerald-600'
                      : 'bg-[#e5e2db]'
                  }`}
                  title={`Parada ${idx + 1}: ${st.title} (${isStopVisited ? 'Visitada' : 'Pendiente'})`}
                />
              );
            })}
          </div>
        </div>

        {/* Previsualización visual de todas las paradas (Carrusel horizontal móvil / Grid) */}
        <div className="bg-white rounded-2xl p-3 border border-[#1b3022]/10 shadow-xs flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-1.5 font-semibold text-xs text-[#061b0e]">
              <Eye className="w-3.5 h-3.5 text-[#47654e]" />
              <span>Paradas de la ruta</span>
              <span className="text-[10px] font-normal text-[#737973]">
                ({route.stops.length})
              </span>
            </div>

            {/* Selector de vista: Carrusel o Grid */}
            <div className="flex items-center gap-1 bg-[#f1eee7] p-0.5 rounded-lg text-xs">
              <button
                onClick={() => setPreviewLayout('carousel')}
                className={`px-2 py-0.5 rounded-md font-sans text-[10px] transition-all cursor-pointer ${
                  previewLayout === 'carousel'
                    ? 'bg-white text-[#061b0e] font-semibold shadow-xs'
                    : 'text-[#555] hover:text-[#061b0e]'
                }`}
                title="Ver en carrusel horizontal"
              >
                Carrusel
              </button>
              <button
                onClick={() => setPreviewLayout('grid')}
                className={`px-2 py-0.5 rounded-md font-sans text-[10px] transition-all cursor-pointer ${
                  previewLayout === 'grid'
                    ? 'bg-white text-[#061b0e] font-semibold shadow-xs'
                    : 'text-[#555] hover:text-[#061b0e]'
                }`}
                title="Ver en cuadrícula"
              >
                Grid
              </button>
            </div>
          </div>

          {/* Carrusel Horizontal o Grid */}
          {previewLayout === 'carousel' ? (
            <div className="flex gap-2 overflow-x-auto pb-1 pt-0.5 -mx-1 px-1 scroll-smooth snap-x snap-mandatory">
              {route.stops.map((st, idx) => {
                const isCurrent = idx === currentStopIndex;
                const isStopVisited = visitedStopIds.includes(st.id);
                const stopThumb = st.imageUrl || route.image;

                return (
                  <button
                    key={st.id}
                    onClick={() => setCurrentStopIndex(idx)}
                    className={`w-28 shrink-0 snap-start rounded-xl overflow-hidden text-left border transition-all cursor-pointer flex flex-col active:scale-95 ${
                      isCurrent
                        ? 'ring-2 ring-emerald-600 border-emerald-500 bg-emerald-50/60 shadow-xs'
                        : 'border-[#e2dfd7] bg-[#faf8f3] hover:border-[#c3c8c1]'
                    }`}
                  >
                    {/* Miniatura */}
                    <div className="relative h-16 w-full bg-slate-200 overflow-hidden">
                      <img
                        src={stopThumb}
                        alt={st.title}
                        className="w-full h-full object-cover"
                        loading="lazy"
                      />
                      <span className="absolute top-1 left-1 px-1 py-0.2 rounded bg-[#061b0e]/80 backdrop-blur-xs text-white text-[8px] font-bold">
                        #{idx + 1}
                      </span>
                      {isStopVisited && (
                        <span className="absolute top-1 right-1 p-0.5 rounded-full bg-emerald-600 text-white shadow-xs">
                          <CheckCircle2 className="w-2.5 h-2.5" />
                        </span>
                      )}
                    </div>

                    {/* Título */}
                    <div className="p-1.5 flex flex-col justify-between flex-1">
                      <p
                        className={`font-serif text-[10px] font-semibold line-clamp-2 leading-tight ${
                          isCurrent ? 'text-emerald-950 font-bold' : 'text-[#1c1c18]'
                        }`}
                        title={st.title}
                      >
                        {st.title}
                      </p>
                      {isCurrent && (
                        <span className="text-[8px] text-emerald-700 font-sans font-semibold mt-0.5">
                          En pantalla
                        </span>
                      )}
                    </div>
                  </button>
                );
              })}
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-2 pt-0.5">
              {route.stops.map((st, idx) => {
                const isCurrent = idx === currentStopIndex;
                const isStopVisited = visitedStopIds.includes(st.id);
                const stopThumb = st.imageUrl || route.image;

                return (
                  <button
                    key={st.id}
                    onClick={() => setCurrentStopIndex(idx)}
                    className={`rounded-xl overflow-hidden text-left border transition-all cursor-pointer flex flex-col active:scale-95 ${
                      isCurrent
                        ? 'ring-2 ring-emerald-600 border-emerald-500 bg-emerald-50/60 shadow-xs'
                        : 'border-[#e2dfd7] bg-[#faf8f3] hover:border-[#c3c8c1]'
                    }`}
                  >
                    <div className="relative h-18 w-full bg-slate-200 overflow-hidden">
                      <img
                        src={stopThumb}
                        alt={st.title}
                        className="w-full h-full object-cover"
                        loading="lazy"
                      />
                      <span className="absolute top-1 left-1 px-1 py-0.2 rounded bg-[#061b0e]/80 backdrop-blur-xs text-white text-[8px] font-bold">
                        #{idx + 1}
                      </span>
                      {isStopVisited && (
                        <span className="absolute top-1 right-1 p-0.5 rounded-full bg-emerald-600 text-white shadow-xs">
                          <CheckCircle2 className="w-2.5 h-2.5" />
                        </span>
                      )}
                    </div>

                    <div className="p-1.5 flex flex-col justify-between flex-1">
                      <p
                        className={`font-serif text-[11px] font-semibold line-clamp-2 leading-tight ${
                          isCurrent ? 'text-emerald-950 font-bold' : 'text-[#1c1c18]'
                        }`}
                        title={st.title}
                      >
                        {st.title}
                      </p>
                      {isCurrent && (
                        <span className="text-[8px] text-emerald-700 font-sans font-semibold mt-0.5">
                          En pantalla
                        </span>
                      )}
                    </div>
                  </button>
                );
              })}
            </div>
          )}
        </div>

        {/* Selected Character Companion Box */}
        <div
          className={`rounded-2xl p-4 transition-all duration-300 border ${
            donFlash
              ? 'bg-amber-100 border-amber-500 ring-2 ring-amber-400 scale-[1.01]'
              : 'bg-gradient-to-br from-[#1b3022] to-[#0a1e12] text-white border-[#2b4c37] shadow-sm'
          }`}
        >
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-2xl bg-white/10 flex items-center justify-center text-2xl border border-white/20 shrink-0 shadow-inner">
                {character.avatarEmoji}
              </div>
              <div className="flex flex-col min-w-0">
                <div className="flex items-center gap-2">
                  <span className="font-serif text-[15px] font-bold text-white leading-none">
                    {character.name}
                  </span>
                  <span className="text-[10px] px-2 py-0.5 rounded-full bg-[#faba71]/20 text-[#ffddba] border border-[#faba71]/40 font-semibold tracking-wide uppercase">
                    {character.archetype}
                  </span>
                </div>
                <span className="font-sans text-[11px] text-[#ebe8e1]/80 mt-1 truncate">
                  {character.abilityName}
                </span>
              </div>
            </div>

            {/* Don Invocation Button */}
            <button
              onClick={handleTriggerDon}
              className={`px-3 py-1.5 rounded-xl font-sans text-xs font-bold flex items-center gap-1.5 transition-all active:scale-95 cursor-pointer shrink-0 ${
                isDonActive
                  ? 'bg-amber-400 text-amber-950 shadow-md'
                  : 'bg-white/15 text-[#ffddba] hover:bg-white/25 border border-white/10'
              }`}
              title="Invocar el don de tu personaje para desvelar pistas y secretos"
            >
              <Zap className="w-3.5 h-3.5 fill-current" />
              <span>{isDonActive ? 'Don Activo' : 'Invocar Don'}</span>
            </button>
          </div>

          {/* Route Affinity Badge */}
          {affinity.hasAffinity && (
            <div className="mt-3 pt-2.5 border-t border-white/10 flex items-center gap-2 text-xs text-[#ffddba] font-sans">
              <Sparkles className="w-3.5 h-3.5 text-[#faba71] shrink-0" />
              <span className="font-medium">{affinity.badgeLabel}:</span>
              <span className="text-[#ebe8e1]/90 truncate">{affinity.bonusDescription}</span>
            </div>
          )}

          {/* Contextual Companion Advice Speech Bubble */}
          <div className="mt-3 p-3 rounded-xl bg-white/10 backdrop-blur-sm border border-white/10 flex items-start justify-between gap-2">
            <p className="font-sans text-[12px] text-[#fcf9f2] leading-relaxed italic">
              «{companionAdvice}»
            </p>
            <button
              onClick={toggleCompanionAudio}
              className={`p-2 rounded-lg shrink-0 transition-all cursor-pointer ${
                isPlayingCharAudio
                  ? 'bg-[#c48b47] text-white'
                  : 'bg-white/20 text-[#ffddba] hover:bg-white/30'
              }`}
              title="Escuchar la voz del personaje"
            >
              <Volume2 className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Audio Guide Player Card */}
        <div className="p-4 rounded-2xl bg-[#061b0e] text-white shadow-md flex flex-col gap-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-2.5 h-2.5 rounded-full bg-[#faba71] animate-pulse" />
              <span className="font-sans text-[11px] font-semibold uppercase tracking-wider text-[#ffddba]">
                Audio Crónica de la Leyenda
              </span>
            </div>
            <span className="font-sans text-[12px] text-[#c3c8c1]">
              Duración: {currentStop.audioDuration}
            </span>
          </div>

          <div className="flex items-center justify-between gap-3">
            <div className="flex flex-col min-w-0">
              <h3 className="font-serif text-[17px] font-semibold text-[#fcf9f2] truncate">
                {currentStop.title}
              </h3>
              <p className="font-sans text-[12px] text-[#ebe8e1] truncate">
                {currentStop.subtitle}
              </p>
            </div>

            <button
              onClick={toggleNarrativeAudio}
              className={`h-11 px-4 rounded-full flex items-center gap-2 font-sans text-[13px] font-semibold shrink-0 active:scale-95 transition-all cursor-pointer ${
                isPlayingAudio
                  ? 'bg-[#c48b47] text-white'
                  : 'bg-white text-[#061b0e] hover:bg-[#fcf9f2]'
              }`}
            >
              {isPlayingAudio ? (
                <>
                  <Pause className="w-4 h-4 fill-current" />
                  <span>Pausar</span>
                </>
              ) : (
                <>
                  <Play className="w-4 h-4 fill-current ml-0.5" />
                  <span>Escuchar</span>
                </>
              )}
            </button>
          </div>

          {/* Equalizer animation when playing */}
          {isPlayingAudio && (
            <div className="flex items-center gap-1 h-3 pt-1">
              <div className="w-1 h-full bg-[#faba71] animate-bounce" style={{ animationDelay: '0ms' }} />
              <div className="w-1 h-2/3 bg-[#faba71] animate-bounce" style={{ animationDelay: '150ms' }} />
              <div className="w-1 h-full bg-[#faba71] animate-bounce" style={{ animationDelay: '300ms' }} />
              <div className="w-1 h-1/2 bg-[#faba71] animate-bounce" style={{ animationDelay: '75ms' }} />
              <div className="w-1 h-4/5 bg-[#faba71] animate-bounce" style={{ animationDelay: '220ms' }} />
              <span className="text-[11px] text-[#ffddba] ml-2 font-sans">
                Narrando leyendas vivas de Galicia...
              </span>
            </div>
          )}
        </div>

        {/* Stop Narrative Sheet */}
        <div className="bg-white rounded-2xl p-5 border border-[#1b3022]/8 shadow-sm flex flex-col gap-4">
          <div className="flex items-center gap-1.5 text-[#47654e] font-sans text-[12px] font-semibold">
            <MapPin className="w-4 h-4" />
            <span>{currentStop.locationName}</span>
          </div>

          <div>
            <h3 className="font-serif text-[22px] font-semibold text-[#1c1c18] leading-tight">
              {currentStop.title}
            </h3>
            <p className="font-serif italic text-[15px] text-[#47654e] mt-1">
              "{currentStop.subtitle}"
            </p>
          </div>

          <div className="text-[#434843] font-sans text-[15px] leading-relaxed border-l-2 border-[#1b3022]/20 pl-3.5 py-0.5">
            {currentStop.story}
          </div>

          {/* Archival Historical Note */}
          <div className="p-3.5 rounded-xl bg-[#f6f3ec] border border-[#e5e2db] flex flex-col gap-1">
            <div className="flex items-center gap-1.5 text-[#061b0e] font-sans text-[11px] font-semibold uppercase tracking-wider">
              <BookOpen className="w-3.5 h-3.5 text-[#47654e]" />
              <span>Cuaderno de Archivo y Mitología</span>
            </div>
            <p className="font-sans text-[13px] text-[#434843]">
              {currentStop.historicalNote}
            </p>
          </div>

          {/* Interactive Trial / Puzzle / Riddle Component */}
          <TrialCard
            trial={currentStop.trial}
            enigma={currentStop.enigma}
            isSolved={isEnigmaSolved}
            characterId={character.id}
            isDonActive={isDonActive}
            onUseDon={handleTriggerDon}
            onSolve={attempts => onSolveEnigma(currentStop.id, attempts)}
          />

          {/* Action Row: Check-in & Notes */}
          <div className="flex items-center justify-between gap-3 pt-2 border-t border-[#f1eee7]">
            <button
              onClick={() => onCheckInStop(currentStop.id)}
              className={`flex-1 h-11 rounded-xl flex items-center justify-center gap-2 font-sans text-[13px] font-semibold active:scale-95 transition-all cursor-pointer ${
                isVisited
                  ? 'bg-[#c5e8ca] text-[#03210e]'
                  : 'bg-[#f1eee7] text-[#061b0e] hover:bg-[#ebe8e1]'
              }`}
            >
              <CheckCircle2 className={`w-4 h-4 ${isVisited ? 'text-emerald-700' : ''}`} />
              <span>{isVisited ? 'Parada Conquistada' : 'Marcar como Visitada'}</span>
            </button>

            <button
              onClick={() => onAddNote(currentStop)}
              className="h-11 px-3.5 rounded-xl border border-[#c3c8c1]/60 text-[#1c1c18] hover:bg-[#f6f3ec] font-sans text-[13px] font-medium flex items-center gap-1.5 active:scale-95 transition-all cursor-pointer"
              title="Anotar en mi cuaderno de campo"
            >
              <BookOpen className="w-4 h-4 text-[#47654e]" />
              <span>Apuntar</span>
            </button>
          </div>
        </div>
      </main>

      {/* Sticky Mobile Bottom Navigation Bar */}
      <div className="sticky bottom-0 z-30 w-full bg-[#fcf9f2]/95 backdrop-blur-xl border-t border-[#1b3022]/10 p-3 shadow-lg flex items-center justify-between gap-2.5">
        <button
          onClick={handlePrev}
          disabled={currentStopIndex === 0}
          className={`h-11 px-3.5 rounded-xl font-sans text-xs font-semibold flex items-center gap-1 transition-all cursor-pointer ${
            currentStopIndex === 0
              ? 'opacity-30 cursor-not-allowed bg-[#ebe8e1] text-[#737973]'
              : 'bg-white text-[#1c1c18] border border-[#c3c8c1]/40 hover:bg-[#f6f3ec] active:scale-95'
          }`}
        >
          <ChevronLeft className="w-4 h-4" />
          <span>Anterior</span>
        </button>

        <div className="flex flex-col items-center min-w-0 px-1">
          <span className="text-[10px] font-sans uppercase font-bold text-[#47654e]">
            {currentStopIndex + 1} de {route.stops.length}
          </span>
          <span className="text-[11px] font-serif font-bold text-[#061b0e] truncate max-w-[120px]">
            {currentStop.title}
          </span>
        </div>

        {currentStopIndex < route.stops.length - 1 ? (
          <button
            onClick={handleNext}
            className="h-11 px-4 rounded-xl bg-[#1b3022] text-white font-sans text-xs font-semibold flex items-center gap-1 hover:bg-[#061b0e] active:scale-95 transition-all cursor-pointer shadow-xs"
          >
            <span>Siguiente</span>
            <ChevronRight className="w-4 h-4" />
          </button>
        ) : (
          <button
            onClick={() => {
              soundEngine.stopSpeech();
              onCheckInStop(currentStop.id);
              if (onCompleteRoute) {
                onCompleteRoute(route.id);
              }
              onBack();
            }}
            className="h-11 px-4 rounded-xl bg-[#c48b47] text-white font-sans text-xs font-semibold flex items-center gap-1 hover:opacity-95 active:scale-95 transition-all cursor-pointer shadow-xs"
          >
            <span>Finalizar</span>
            <Sparkles className="w-4 h-4" />
          </button>
        )}
      </div>
    </div>
  );
};
