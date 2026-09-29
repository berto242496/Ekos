import React, { useState } from 'react';
import { RouteItem, RouteStop } from '../types';
import { getCharacterById } from '../data/charactersData';
import {
  Navigation,
  Compass,
  Layers,
  MapPin,
  Play,
  Volume2,
  X,
  ChevronRight,
  Sparkles,
} from 'lucide-react';

interface InteractiveMapProps {
  routes: RouteItem[];
  selectedRouteId?: string;
  characterId?: string;
  onSelectRoute: (route: RouteItem) => void;
  onStartRoute: (route: RouteItem) => void;
  onClose?: () => void;
  isModal?: boolean;
}

export const InteractiveMap: React.FC<InteractiveMapProps> = ({
  routes,
  selectedRouteId,
  characterId = 'meiga',
  onSelectRoute,
  onStartRoute,
  onClose,
  isModal = false,
}) => {
  const [activeFilter, setActiveFilter] = useState<string>(selectedRouteId || 'all');
  const [selectedStop, setSelectedStop] = useState<{
    stop: RouteStop;
    route: RouteItem;
  } | null>(null);
  const [gpsLocked, setGpsLocked] = useState(true);
  const activeChar = getCharacterById(characterId);

  const activeRoutes =
    activeFilter === 'all'
      ? routes
      : routes.filter(r => r.id === activeFilter);

  const getRouteColor = (routeId: string) => {
    switch (routeId) {
      case 'ribeira-sacra':
        return '#8b263e';
      case 'costa-da-morte':
        return '#1b4965';
      case 'fragas-do-eume':
        return '#2d5a27';
      case 'serra-do-courel':
        return '#4a5759';
      case 'santiago-compostela':
        return '#d4a373';
      case 'rias-baixas':
        return '#0077b6';
      case 'baixo-mino':
        return '#588157';
      default:
        return '#1b3022';
    }
  };

  return (
    <div className={`flex flex-col w-full bg-[#fcf9f2] text-[#1c1c18] ${isModal ? 'h-[85vh]' : 'min-h-screen pb-24'}`}>
      {/* Header */}
      <header className="sticky top-0 z-30 bg-[#fcf9f2]/95 backdrop-blur-md px-5 py-3 border-b border-[#1b3022]/10 flex items-center justify-between">
        <div className="flex flex-col">
          <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
            Cartografía de Galicia · As 4 Provincias
          </span>
          <h2 className="font-serif text-[18px] font-semibold text-[#1c1c18]">
            Mapa de Rutas & Patrimonio
          </h2>
        </div>

        <div className="flex items-center gap-2">
          {/* Active Companion Chip */}
          <div
            className="h-8 px-2.5 rounded-full bg-[#1b3022]/10 border border-[#1b3022]/20 flex items-center gap-1.5"
            title={`Compañeiro Activo: ${activeChar.name}`}
          >
            <span className="text-base leading-none">{activeChar.avatarEmoji}</span>
            <span className="font-serif text-[12px] font-semibold text-[#1b3022] hidden sm:inline">
              {activeChar.name}
            </span>
          </div>

          <button
            onClick={() => setGpsLocked(!gpsLocked)}
            className={`px-3 py-1.5 rounded-full font-sans text-[12px] font-semibold flex items-center gap-1.5 transition-all cursor-pointer ${
              gpsLocked
                ? 'bg-[#c5e8ca] text-[#03210e]'
                : 'bg-[#f1eee7] text-[#434843] hover:bg-[#ebe8e1]'
            }`}
          >
            <Navigation className={`w-3.5 h-3.5 ${gpsLocked ? 'fill-current' : ''}`} />
            <span>{gpsLocked ? 'GPS Conectado' : 'Calibrar GPS'}</span>
          </button>

          {isModal && onClose && (
            <button
              onClick={onClose}
              className="w-8 h-8 rounded-full bg-[#ebe8e1] flex items-center justify-center text-[#1c1c18] hover:bg-[#dcdad3] cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
      </header>

      {/* Filter Chips on Map */}
      <div className="px-5 py-2.5 bg-[#f6f3ec] border-b border-[#e5e2db] flex items-center gap-2 overflow-x-auto no-scrollbar">
        <button
          onClick={() => setActiveFilter('all')}
          className={`px-3 py-1 rounded-full text-[12px] font-sans font-semibold tracking-wide whitespace-nowrap transition-all cursor-pointer ${
            activeFilter === 'all'
              ? 'bg-[#1b3022] text-white shadow-2xs'
              : 'bg-white text-[#434843] border border-[#c3c8c1]/40 hover:bg-[#ebe8e1]'
          }`}
        >
          Toda Galicia (7 rutas)
        </button>

        {routes.map(r => {
          const isSelected = activeFilter === r.id;
          return (
            <button
              key={r.id}
              onClick={() => setActiveFilter(r.id)}
              className={`px-3 py-1 rounded-full text-[12px] font-sans font-semibold tracking-wide whitespace-nowrap transition-all cursor-pointer ${
                isSelected
                  ? 'bg-[#1b3022] text-white shadow-2xs'
                  : 'bg-white text-[#434843] border border-[#c3c8c1]/40 hover:bg-[#ebe8e1]'
              }`}
            >
              {r.title}
            </button>
          );
        })}
      </div>

      {/* Map Canvas / Stylized Topographical Visualizer */}
      <div className="relative flex-1 w-full min-h-[420px] bg-[#eef1ed] overflow-hidden select-none">
        {/* Cartographic SVG of Galicia */}
        <svg
          className="absolute inset-0 w-full h-full"
          viewBox="0 0 1000 700"
          preserveAspectRatio="none"
        >
          <defs>
            <linearGradient id="seaGrad" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%" stopColor="#d5e3e8" stopOpacity="0.8" />
              <stop offset="100%" stopColor="#bdd3dd" stopOpacity="0.6" />
            </linearGradient>

            <linearGradient id="galiciaGrad" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%" stopColor="#f3f6ee" />
              <stop offset="50%" stopColor="#eaf1e7" />
              <stop offset="100%" stopColor="#e3ede0" />
            </linearGradient>

            <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#d5dcce" strokeWidth="0.5" />
            </pattern>
          </defs>

          {/* Sea background */}
          <rect width="1000" height="700" fill="url(#seaGrad)" />
          <rect width="1000" height="700" fill="url(#grid)" />

          {/* Galicia Landmass Outline (Stylized polygon shape) */}
          <path
            d="M 480,50 C 580,40 700,70 780,110 C 850,150 920,280 920,380 C 910,500 870,620 780,660 C 650,670 420,680 250,670 C 180,630 180,560 210,500 C 190,440 130,400 130,340 C 140,240 220,180 320,130 C 380,90 420,60 480,50 Z"
            fill="url(#galiciaGrad)"
            stroke="#9eb39e"
            strokeWidth="2.5"
          />

          {/* Mountain elevations in Courel and Macizo Central */}
          <path
            d="M 720,280 Q 820,320 860,450 Q 810,520 740,480 Z"
            fill="#dbe4d5"
            fillOpacity="0.7"
            stroke="#819986"
            strokeWidth="1"
            strokeDasharray="3 3"
          />
          <text
            x="760"
            y="390"
            fill="#556b5b"
            fontSize="12"
            fontFamily="Playfair Display"
            fontStyle="italic"
            fontWeight="bold"
            letterSpacing="2"
          >
            Serra do Courel
          </text>

          {/* Mountain elevations in Fragas do Eume (North) */}
          <path
            d="M 440,110 Q 550,120 570,200 Q 480,210 440,150 Z"
            fill="#cbe3cb"
            fillOpacity="0.6"
          />
          <text
            x="465"
            y="170"
            fill="#2d5a27"
            fontSize="11"
            fontFamily="Playfair Display"
            fontStyle="italic"
            fontWeight="bold"
          >
            Fragas do Eume
          </text>

          {/* Rivers: Miño and Sil */}
          {/* Sil joining Miño */}
          <path
            d="M 850,440 Q 720,450 630,480 Q 520,520 400,560 Q 300,620 240,670"
            fill="none"
            stroke="#457b9d"
            strokeWidth="3.5"
            strokeLinecap="round"
          />
          <text
            x="580"
            y="470"
            fill="#1d3557"
            fontSize="11"
            fontFamily="Playfair Display"
            fontStyle="italic"
            fontWeight="600"
          >
            Canón do Sil & Miño
          </text>

          {/* River Ulla towards Ría de Arousa */}
          <path
            d="M 520,380 Q 420,390 320,430 Q 240,460 200,470"
            fill="none"
            stroke="#457b9d"
            strokeWidth="2"
            strokeLinecap="round"
          />

          {/* Ocean Labels */}
          <text
            x="60"
            y="220"
            fill="#5b7f95"
            fontSize="15"
            fontFamily="Playfair Display"
            fontStyle="italic"
            fontWeight="600"
            letterSpacing="5"
          >
            OCÉANO ATLÁNTICO
          </text>
          <text
            x="580"
            y="50"
            fill="#5b7f95"
            fontSize="13"
            fontFamily="Playfair Display"
            fontStyle="italic"
            fontWeight="600"
            letterSpacing="3"
          >
            MAR CANTÁBRICO
          </text>

          {/* The 4 Provinces Subtle Labels */}
          <text
            x="360"
            y="250"
            fill="#7b997e"
            fontSize="18"
            fontFamily="Playfair Display"
            fontWeight="bold"
            letterSpacing="4"
            opacity="0.5"
          >
            A CORUÑA
          </text>
          <text
            x="650"
            y="260"
            fill="#7b997e"
            fontSize="18"
            fontFamily="Playfair Display"
            fontWeight="bold"
            letterSpacing="4"
            opacity="0.5"
          >
            LUGO
          </text>
          <text
            x="300"
            y="560"
            fill="#7b997e"
            fontSize="18"
            fontFamily="Playfair Display"
            fontWeight="bold"
            letterSpacing="4"
            opacity="0.5"
          >
            PONTEVEDRA
          </text>
          <text
            x="620"
            y="570"
            fill="#7b997e"
            fontSize="18"
            fontFamily="Playfair Display"
            fontWeight="bold"
            letterSpacing="4"
            opacity="0.5"
          >
            OURENSE
          </text>
        </svg>

        {/* Interactive Waypoint Pins Overlay across all Galicia */}
        {activeRoutes.map(route => {
          const routeColor = getRouteColor(route.id);
          return route.stops.map(stop => {
            const isSelected = selectedStop?.stop.id === stop.id;
            return (
              <button
                key={stop.id}
                onClick={() => setSelectedStop({ stop, route })}
                style={{
                  left: `${stop.coordinates.x}%`,
                  top: `${stop.coordinates.y}%`,
                }}
                className={`absolute -translate-x-1/2 -translate-y-1/2 transition-all duration-300 z-20 group cursor-pointer ${
                  isSelected ? 'scale-125 z-30' : 'hover:scale-110'
                }`}
                title={`${route.title}: ${stop.title}`}
              >
                <div
                  className={`relative flex items-center justify-center rounded-full shadow-md border-2 ${
                    isSelected ? 'w-10 h-10 ring-4 ring-[#faba71]/70' : 'w-8 h-8'
                  }`}
                  style={{
                    backgroundColor: routeColor,
                    borderColor: '#ffffff',
                    color: '#ffffff',
                  }}
                >
                  <span className="font-serif text-[12px] font-bold">
                    {stop.order}
                  </span>
                  {isSelected && (
                    <span className="absolute -top-1 -right-1 w-3 h-3 bg-[#faba71] rounded-full animate-ping" />
                  )}
                </div>

                {/* Micro tooltip */}
                <div className="absolute top-full left-1/2 -translate-x-1/2 mt-1 px-2 py-0.5 rounded bg-black/80 backdrop-blur-xs text-white text-[10px] font-sans whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-40">
                  {stop.title}
                </div>
              </button>
            );
          });
        })}

        {/* GPS user position pulse (Santiago / Center of Galicia) */}
        {gpsLocked && (
          <div
            className="absolute left-[41%] top-[46%] -translate-x-1/2 -translate-y-1/2 z-20 pointer-events-none"
            title="Posición calibrada en Galicia"
          >
            <div className="w-5 h-5 bg-emerald-600 rounded-full border-2 border-white shadow-md flex items-center justify-center">
              <div className="w-2 h-2 bg-white rounded-full" />
            </div>
            <div className="absolute inset-0 w-5 h-5 rounded-full bg-emerald-400 animate-ping opacity-75" />
          </div>
        )}

        {/* Map Legend Overlay for All Galicia */}
        <div className="absolute bottom-4 left-4 z-10 bg-white/95 backdrop-blur-md rounded-xl p-3 border border-[#1b3022]/10 shadow-md max-w-xs text-[11px] font-sans">
          <div className="flex items-center gap-1.5 font-semibold text-[#061b0e] mb-1.5">
            <Layers className="w-3.5 h-3.5 text-[#47654e]" />
            <span>Rutas en Toda Galicia</span>
          </div>
          <div className="grid grid-cols-2 gap-x-2 gap-y-1 text-[#434843]">
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#8b263e' }} />
              <span className="truncate">Ribeira Sacra</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#1b4965' }} />
              <span className="truncate">Costa da Morte</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#2d5a27' }} />
              <span className="truncate">Fragas do Eume</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#4a5759' }} />
              <span className="truncate">Serra do Courel</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#d4a373' }} />
              <span className="truncate">Santiago</span>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: '#0077b6' }} />
              <span className="truncate">Rías Baixas</span>
            </div>
          </div>
        </div>
      </div>

      {/* Selected Stop Drawer Sheet */}
      {selectedStop && (
        <div className="bg-white border-t border-[#1b3022]/10 p-4 shadow-xl z-30 max-w-lg mx-auto w-full flex flex-col gap-2.5 animate-in slide-in-from-bottom duration-200">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span
                className="px-2 py-0.5 rounded-full text-white font-sans text-[11px] font-semibold uppercase"
                style={{ backgroundColor: getRouteColor(selectedStop.route.id) }}
              >
                {selectedStop.route.title}
              </span>
              <span className="font-sans text-[12px] text-[#737973]">
                Parada {selectedStop.stop.order} de {selectedStop.route.stopsCount}
              </span>
            </div>

            <button
              onClick={() => setSelectedStop(null)}
              className="text-[#737973] hover:text-[#1c1c18] p-1 cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          <div>
            <h3 className="font-serif text-[18px] font-bold text-[#1c1c18] leading-snug">
              {selectedStop.stop.title}
            </h3>
            <p className="font-sans text-[13px] text-[#47654e] font-medium">
              {selectedStop.stop.locationName}
            </p>
          </div>

          <p className="font-sans text-[13px] text-[#434843] line-clamp-2 leading-relaxed">
            {selectedStop.stop.story}
          </p>

          <div className="flex items-center justify-between pt-1 border-t border-[#f1eee7] mt-1">
            <div className="flex items-center gap-2 text-[12px] text-[#737973] font-sans">
              <Volume2 className="w-4 h-4 text-[#47654e]" />
              <span>Audio: {selectedStop.stop.audioDuration}</span>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => {
                  onSelectRoute(selectedStop.route);
                  if (onClose) onClose();
                }}
                className="px-3 py-1.5 rounded-lg border border-[#c3c8c1]/40 text-[#1c1c18] hover:bg-[#f6f3ec] text-[12px] font-sans font-semibold cursor-pointer"
              >
                Ver ruta
              </button>
              <button
                onClick={() => {
                  onStartRoute(selectedStop.route);
                  if (onClose) onClose();
                }}
                className="px-3.5 py-1.5 rounded-lg bg-[#061b0e] text-white hover:bg-[#1b3022] text-[12px] font-sans font-semibold flex items-center gap-1 cursor-pointer"
              >
                <Play className="w-3 h-3 fill-current" />
                <span>Explorar</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
