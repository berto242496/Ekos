import React, { useEffect, useMemo, useRef, useState } from 'react';
import { MapPin, Minus, Plus } from 'lucide-react';
import { useI18n } from '../services/i18n';

interface Coordinates {
  lat: number;
  lng: number;
}

interface CoordinateMapPickerProps {
  selectedCoordinates?: Coordinates;
  existingCoordinates: Coordinates[];
  onSelect: (coordinates: Coordinates) => void;
}

const DEFAULT_CENTER = { lat: 42.8, lng: -8.1 };
const INITIAL_ZOOM = 9;

export const CoordinateMapPicker: React.FC<CoordinateMapPickerProps> = ({
  selectedCoordinates,
  existingCoordinates,
  onSelect,
}) => {
  const { t } = useI18n();
  const mapRef = useRef<HTMLDivElement>(null);
  const pointerStart = useRef<{ id: number; x: number; y: number; panX: number; panY: number } | null>(null);
  const [mapSize, setMapSize] = useState({ width: 0, height: 0 });
  const [zoom, setZoom] = useState(INITIAL_ZOOM);
  const [pan, setPan] = useState({ x: 0, y: 0 });
  const [center] = useState(selectedCoordinates || DEFAULT_CENTER);

  useEffect(() => {
    const element = mapRef.current;
    if (!element) return;
    const observer = new ResizeObserver(([entry]) => {
      setMapSize({ width: entry.contentRect.width, height: entry.contentRect.height });
    });
    observer.observe(element);
    return () => observer.disconnect();
  }, []);

  const worldSize = 256 * 2 ** zoom;
  const project = (coordinates: Coordinates) => {
    const latitude = Math.max(-85.0511, Math.min(85.0511, coordinates.lat));
    const sinLatitude = Math.sin((latitude * Math.PI) / 180);
    return {
      x: ((coordinates.lng + 180) / 360) * worldSize,
      y: (0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * worldSize,
    };
  };
  const centerPoint = project(center);

  const toMapPosition = (coordinates: Coordinates) => {
    const point = project(coordinates);
    return {
      x: point.x - centerPoint.x + mapSize.width / 2 + pan.x,
      y: point.y - centerPoint.y + mapSize.height / 2 + pan.y,
    };
  };

  const mapTiles = useMemo(() => {
    if (mapSize.width === 0 || mapSize.height === 0) return [];
    const firstX = Math.floor((centerPoint.x - mapSize.width / 2 - pan.x) / 256) - 1;
    const lastX = Math.floor((centerPoint.x + mapSize.width / 2 - pan.x) / 256) + 1;
    const firstY = Math.floor((centerPoint.y - mapSize.height / 2 - pan.y) / 256) - 1;
    const lastY = Math.floor((centerPoint.y + mapSize.height / 2 - pan.y) / 256) + 1;
    const tileCount = 2 ** zoom;
    const tiles: { x: number; y: number; left: number; top: number }[] = [];

    for (let x = firstX; x <= lastX; x += 1) {
      for (let y = firstY; y <= lastY; y += 1) {
        if (x < 0 || x >= tileCount || y < 0 || y >= tileCount) continue;
        tiles.push({
          x,
          y,
          left: x * 256 - centerPoint.x + mapSize.width / 2 + pan.x,
          top: y * 256 - centerPoint.y + mapSize.height / 2 + pan.y,
        });
      }
    }
    return tiles;
  }, [centerPoint.x, centerPoint.y, mapSize, pan, zoom]);

  const handlePointerUp = (event: React.PointerEvent<HTMLDivElement>) => {
    const start = pointerStart.current;
    if (!start || start.id !== event.pointerId) return;
    pointerStart.current = null;
    if (Math.hypot(event.clientX - start.x, event.clientY - start.y) > 5) return;

    const rect = event.currentTarget.getBoundingClientRect();
    const worldX = event.clientX - rect.left - mapSize.width / 2 - pan.x + centerPoint.x;
    const worldY = event.clientY - rect.top - mapSize.height / 2 - pan.y + centerPoint.y;
    const mapY = Math.max(0, Math.min(worldSize, worldY));
    const longitude = (worldX / worldSize) * 360 + 180;
    onSelect({
      lat: (Math.atan(Math.sinh(Math.PI * (1 - (2 * mapY) / worldSize))) * 180) / Math.PI,
      lng: ((longitude % 360) + 360) % 360 - 180,
    });
  };

  return (
    <div className="space-y-2">
      <div
        ref={mapRef}
        className="relative h-72 w-full overflow-hidden rounded-2xl border border-[#1b3022]/20 bg-[#e8eee8] select-none cursor-crosshair"
        role="application"
        aria-label={t('creator_map_aria')}
        style={{ touchAction: 'none' }}
        onPointerDown={event => {
          if (event.button !== 0 || (event.target instanceof Element && event.target.closest('button'))) return;
          pointerStart.current = {
            id: event.pointerId,
            x: event.clientX,
            y: event.clientY,
            panX: pan.x,
            panY: pan.y,
          };
          event.currentTarget.setPointerCapture(event.pointerId);
        }}
        onPointerMove={event => {
          const start = pointerStart.current;
          if (!start || start.id !== event.pointerId) return;
          setPan({
            x: start.panX + event.clientX - start.x,
            y: start.panY + event.clientY - start.y,
          });
        }}
        onPointerUp={handlePointerUp}
        onPointerCancel={() => {
          pointerStart.current = null;
        }}
      >
        {mapTiles.map(tile => (
          <img
            key={`${zoom}-${tile.x}-${tile.y}`}
            src={`https://tile.openstreetmap.org/${zoom}/${tile.x}/${tile.y}.png`}
            alt=""
            draggable={false}
            className="absolute max-w-none pointer-events-none"
            style={{ left: tile.left, top: tile.top, width: 256, height: 256 }}
          />
        ))}

        {existingCoordinates.map((coordinates, index) => {
          const point = toMapPosition(coordinates);
          return (
            <span
              key={`${coordinates.lat}-${coordinates.lng}-${index}`}
              className="absolute z-10 flex h-7 w-7 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full border-2 border-white bg-[#47654e] text-xs font-bold text-white shadow"
              style={{ left: point.x, top: point.y }}
              aria-hidden="true"
            >
              {index + 1}
            </span>
          );
        })}

        {selectedCoordinates && (
          <MapPin
            className="absolute z-20 h-9 w-9 -translate-x-1/2 -translate-y-full fill-[#c48b47] text-[#1b3022] drop-shadow-md pointer-events-none"
            style={{
              left: toMapPosition(selectedCoordinates).x,
              top: toMapPosition(selectedCoordinates).y,
            }}
            aria-hidden="true"
          />
        )}

        <div className="absolute right-3 top-3 z-30 flex overflow-hidden rounded-xl border border-[#1b3022]/15 bg-white shadow">
          <button
            type="button"
            onClick={() => setZoom(value => Math.max(6, value - 1))}
            aria-label="Alejar mapa"
            className="p-2 text-[#1b3022] hover:bg-[#f1eee7]"
          >
            <Minus className="h-4 w-4" />
          </button>
          <button
            type="button"
            onClick={() => setZoom(value => Math.min(17, value + 1))}
            aria-label="Acercar mapa"
            className="border-l border-[#1b3022]/10 p-2 text-[#1b3022] hover:bg-[#f1eee7]"
          >
            <Plus className="h-4 w-4" />
          </button>
        </div>
        <a
          href="https://www.openstreetmap.org/copyright"
          target="_blank"
          rel="noreferrer"
          onPointerDown={event => event.stopPropagation()}
          className="absolute bottom-1 right-1 z-30 rounded bg-white/90 px-1.5 py-0.5 text-[10px] text-[#434843] shadow-sm"
        >
          © OpenStreetMap
        </a>
      </div>
      <p className="text-[11px] font-serif text-[#47654e]">
        {t('creator_map_instructions')}
      </p>
    </div>
  );
};
