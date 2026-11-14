import L from 'leaflet';
import { maplibreGL } from '@maplibre/maplibre-gl-leaflet';
import { setWorkerUrl } from 'maplibre-gl';

window.L = L;
window.EkosMapLibre = { maplibreGL, setWorkerUrl };
