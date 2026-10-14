import React, { useEffect, useMemo, useRef, useState } from 'react';
import { RouteItem, RouteStop } from '../types';
import { getCharacterById } from '../data/charactersData';
import {
  Layers,
  Play,
  Volume2,
  X,
  Plus,
  Minus,
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
  const [activeFilter, setActiveFilter] = useState<string>(
    isModal ? selectedRouteId || 'all' : 'all',
  );
  const [selectedStop, setSelectedStop] = useState<{
    stop: RouteStop;
    route: RouteItem;
  } | null>(null);
  const [zoom, setZoom] = useState(8);
  const [mapReady, setMapReady] = useState(false);
  const [pan, setPan] = useState({ x: 0, y: 0 });
  const mapRef = useRef<HTMLDivElement>(null);
  const dragStart = useRef<{
    pointerId: number;
    x: number;
    y: number;
    panX: number;
    panY: number;
  } | null>(null);
  const [mapSize, setMapSize] = useState({ width: 0, height: 0 });
  const activeChar = getCharacterById(characterId);

  const activeRoutes =
    activeFilter === 'all'
      ? routes
      : routes.filter(r => r.id === activeFilter);

  const visibleStops = useMemo(
    () =>
      activeRoutes.flatMap(route =>
        route.stops
          .filter(
            stop =>
              Number.isFinite(stop.coordinates.lat) &&
              Number.isFinite(stop.coordinates.lng),
          )
          .map(stop => ({ stop, route })),
      ),
    [activeRoutes],
  );

  const mapCenter = useMemo(() => {
    if (visibleStops.length === 0) return { lat: 42.65, lng: -8.2 };

    const latitudes = visibleStops.map(({ stop }) => stop.coordinates.lat);
    const longitudes = visibleStops.map(({ stop }) => stop.coordinates.lng);
    return {
      lat: (Math.min(...latitudes) + Math.max(...latitudes)) / 2,
      lng: (Math.min(...longitudes) + Math.max(...longitudes)) / 2,
    };
  }, [visibleStops]);

  useEffect(() => {
    const element = mapRef.current;
    if (!element) return;

    const observer = new ResizeObserver(([entry]) => {
      setMapSize({
        width: entry.contentRect.width,
        height: entry.contentRect.height,
      });
    });
    observer.observe(element);
    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    if (mapSize.width === 0 || mapSize.height === 0) return;
    if (visibleStops.length < 2) {
      setMapReady(true);
      return;
    }

    const zoomZeroPoints = visibleStops.map(({ stop }) => {
      const latitude = Math.max(-85.0511, Math.min(85.0511, stop.coordinates.lat));
      const sinLatitude = Math.sin((latitude * Math.PI) / 180);
      return {
        x: ((stop.coordinates.lng + 180) / 360) * 256,
        y:
          (0.5 -
            Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) *
          256,
      };
    });
    const spanX =
      Math.max(...zoomZeroPoints.map(point => point.x)) -
      Math.min(...zoomZeroPoints.map(point => point.x));
    const spanY =
      Math.max(...zoomZeroPoints.map(point => point.y)) -
      Math.min(...zoomZeroPoints.map(point => point.y));
    const scaleX = spanX === 0 ? Number.POSITIVE_INFINITY : (mapSize.width - 100) / spanX;
    const scaleY = spanY === 0 ? Number.POSITIVE_INFINITY : (mapSize.height - 100) / spanY;
    const fittedZoom = Math.floor(Math.log2(Math.min(scaleX, scaleY)));

    setZoom(Math.max(6, Math.min(15, fittedZoom)));
    setMapReady(true);
  }, [mapSize, visibleStops]);

  useEffect(() => {
    setSelectedStop(null);
    setPan({ x: 0, y: 0 });
  }, [activeFilter]);

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

  const worldSize = 256 * 2 ** zoom;
  const project = (lat: number, lng: number) => {
    const latitude = Math.max(-85.0511, Math.min(85.0511, lat));
    const sinLatitude = Math.sin((latitude * Math.PI) / 180);
    return {
      x: ((lng + 180) / 360) * worldSize,
      y:
        ((0.5 -
          Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) *
          worldSize),
    };
  };
  const centerPoint = project(mapCenter.lat, mapCenter.lng);
  const toMapPosition = (lat: number, lng: number) => {
    const point = project(lat, lng);
    return {
      x: point.x - centerPoint.x + mapSize.width / 2 + pan.x,
      y: point.y - centerPoint.y + mapSize.height / 2 + pan.y,
    };
  };
  const mapTiles = useMemo(() => {
    if (!mapReady || mapSize.width === 0 || mapSize.height === 0) return [];

    const center = project(mapCenter.lat, mapCenter.lng);
    const tileCount = 2 ** zoom;
    const firstX = Math.floor((center.x - mapSize.width / 2 - pan.x) / 256) - 1;
    const lastX = Math.floor((center.x + mapSize.width / 2 - pan.x) / 256) + 1;
    const firstY = Math.floor((center.y - mapSize.height / 2 - pan.y) / 256) - 1;
    const lastY = Math.floor((center.y + mapSize.height / 2 - pan.y) / 256) + 1;
    const tiles: { x: number; y: number; left: number; top: number }[] = [];

    for (let x = firstX; x <= lastX; x += 1) {
      for (let y = firstY; y <= lastY; y += 1) {
        if (x < 0 || x >= tileCount || y < 0 || y >= tileCount) continue;
        tiles.push({
          x,
          y,
          left: x * 256 - center.x + mapSize.width / 2 + pan.x,
          top: y * 256 - center.y + mapSize.height / 2 + pan.y,
        });
      }
    }
    return tiles;
  }, [mapCenter, mapReady, mapSize, pan, zoom]);

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

          <div className="flex items-center rounded-full border border-[#1b3022]/15 bg-white">
            <button
              onClick={() => setZoom(current => Math.max(6, current - 1))}
              aria-label="Alejar mapa"
              className="p-2 text-[#1b3022] hover:bg-[#f1eee7] rounded-l-full cursor-pointer"
            >
              <Minus className="w-4 h-4" />
            </button>
            <button
              onClick={() => setZoom(current => Math.min(15, current + 1))}
              aria-label="Acercar mapa"
              className="p-2 text-[#1b3022] hover:bg-[#f1eee7] rounded-r-full cursor-pointer"
            >
              <Plus className="w-4 h-4" />
            </button>
          </div>

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
          Toda Galicia ({routes.length} rutas)
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

      <div
        ref={mapRef}
        className="relative flex-1 w-full min-h-[420px] bg-[#e8eee8] overflow-hidden select-none cursor-grab active:cursor-grabbing"
        role="region"
        aria-label="Mapa de rutas de Galicia"
        style={{ touchAction: 'none' }}
        onPointerDown={event => {
          if (event.button !== 0) return;
          if (event.target instanceof Element && event.target.closest('button, a')) return;
          dragStart.current = {
            pointerId: event.pointerId,
            x: event.clientX,
            y: event.clientY,
            panX: pan.x,
            panY: pan.y,
          };
          event.currentTarget.setPointerCapture(event.pointerId);
        }}
        onPointerMove={event => {
          const start = dragStart.current;
          if (!start || start.pointerId !== event.pointerId) return;
          setPan({
            x: start.panX + event.clientX - start.x,
            y: start.panY + event.clientY - start.y,
          });
        }}
        onPointerUp={event => {
          if (dragStart.current?.pointerId === event.pointerId) {
            dragStart.current = null;
          }
        }}
        onPointerCancel={() => {
          dragStart.current = null;
        }}
      >
        {mapTiles.map(tile => (
          <img
            key={`${zoom}-${tile.x}-${tile.y}`}
            src={`https://tile.openstreetmap.org/${zoom}/${tile.x}/${tile.y}.png`}
            alt=""
            draggable={false}
            className="absolute max-w-none"
            style={{ left: tile.left, top: tile.top, width: 256, height: 256 }}
          />
        ))}

        {mapSize.width > 0 && mapSize.height > 0 && (
          <svg
            className="absolute inset-0 w-full h-full pointer-events-none"
            viewBox={`0 0 ${mapSize.width} ${mapSize.height}`}
            preserveAspectRatio="none"
            aria-hidden="true"
          >
            {activeRoutes.map(route => {
              const routeStops = route.stops.filter(
                stop =>
                  Number.isFinite(stop.coordinates.lat) &&
                  Number.isFinite(stop.coordinates.lng),
              );
              if (routeStops.length < 2) return null;

              const points = routeStops
                .map(stop => {
                  const point = toMapPosition(
                    stop.coordinates.lat,
                    stop.coordinates.lng,
                  );
                  return `${point.x},${point.y}`;
                })
                .join(' ');

              return (
                <polyline
                  key={route.id}
                  points={points}
                  fill="none"
                  stroke={getRouteColor(route.id)}
                  strokeWidth="4"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeOpacity="0.85"
                  vectorEffect="non-scaling-stroke"
                />
              );
            })}
          </svg>
        )}

        {visibleStops.map(({ stop, route }) => {
          const isSelected = selectedStop?.stop.id === stop.id;
          const point = toMapPosition(stop.coordinates.lat, stop.coordinates.lng);
          return (
            <button
              key={`${route.id}-${stop.id}`}
              onClick={() => setSelectedStop({ stop, route })}
              style={{
                left: point.x,
                top: point.y,
                backgroundColor: getRouteColor(route.id),
              }}
              className={`absolute -translate-x-1/2 -translate-y-1/2 flex items-center justify-center rounded-full border-2 border-white text-white shadow-md transition-transform z-20 cursor-pointer ${
                isSelected
                  ? 'w-10 h-10 ring-4 ring-[#faba71]/70 scale-110'
                  : 'w-8 h-8 hover:scale-110'
              }`}
              title={`${route.title}: ${stop.title}`}
              aria-label={`${route.title}, parada ${stop.order}: ${stop.title}`}
            >
              <span className="font-serif text-[12px] font-bold">{stop.order}</span>
            </button>
          );
        })}

        {visibleStops.length === 0 && (
          <div className="absolute inset-0 flex items-center justify-center p-6 pointer-events-none">
            <p className="rounded-xl bg-white/95 px-4 py-3 text-center text-sm text-[#434843] shadow-md">
              Esta ruta todavía no tiene paradas con coordenadas.
            </p>
          </div>
        )}

        <div className="absolute bottom-4 left-4 z-10 max-w-[min(24rem,calc(100%-2rem))] rounded-xl border border-[#1b3022]/10 bg-white/95 p-3 shadow-md">
          <div className="mb-1.5 flex items-center gap-1.5 font-sans text-[11px] font-semibold text-[#061b0e]">
            <Layers className="w-3.5 h-3.5 text-[#47654e]" />
            <span>Paradas de las rutas visibles</span>
          </div>
          {visibleStops.length > 0 ? (
            <div className="flex max-h-16 flex-wrap gap-x-3 gap-y-1 overflow-y-auto text-[11px] text-[#434843]">
              {activeRoutes
                .filter(route =>
                  route.stops.some(
                    stop =>
                      Number.isFinite(stop.coordinates.lat) &&
                      Number.isFinite(stop.coordinates.lng),
                  ),
                )
                .map(route => (
                  <div key={route.id} className="flex items-center gap-1.5">
                    <span
                      className="h-2.5 w-2.5 shrink-0 rounded-full"
                      style={{ backgroundColor: getRouteColor(route.id) }}
                    />
                    <span className="max-w-36 truncate">{route.title}</span>
                  </div>
                ))}
            </div>
          ) : (
            <span className="text-[11px] text-[#737973]">Sin puntos para mostrar</span>
          )}
        </div>

        <a
          href="https://www.openstreetmap.org/copyright"
          target="_blank"
          rel="noreferrer"
          className="absolute bottom-1 right-1 z-10 rounded bg-white/90 px-1.5 py-0.5 font-sans text-[10px] text-[#434843] shadow-sm"
        >
          © OpenStreetMap
        </a>
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
