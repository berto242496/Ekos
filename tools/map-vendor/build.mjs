import { build } from 'esbuild';
import { copyFile, mkdir, rm } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const here = path.dirname(fileURLToPath(import.meta.url));
const nodeModules = path.join(here, 'node_modules');
const outDir = path.resolve(here, '../../androidApp/src/main/assets/map/vendor');
const licensesDir = path.join(outDir, 'licenses');

await rm(outDir, { recursive: true, force: true });
await mkdir(licensesDir, { recursive: true });

// Classic script: Leaflet, MapLibre GL JS (ESM dependencies resolved here) and the Leaflet adapter.
await build({
  entryPoints: [path.join(here, 'entry.js')],
  bundle: true,
  format: 'iife',
  platform: 'browser',
  target: 'es2020',
  minify: true,
  outfile: path.join(outDir, 'ekos-map-vendor.js')
});

// Module worker used by MapLibre GL JS. Its shared chunk is inlined so the file is self-contained.
await build({
  entryPoints: [path.join(nodeModules, 'maplibre-gl/dist/maplibre-gl-worker.mjs')],
  bundle: true,
  format: 'esm',
  platform: 'browser',
  target: 'es2020',
  minify: true,
  outfile: path.join(outDir, 'maplibre-gl-worker.js')
});

await copyFile(path.join(nodeModules, 'leaflet/dist/leaflet.css'), path.join(outDir, 'leaflet.css'));
await copyFile(path.join(nodeModules, 'maplibre-gl/dist/maplibre-gl.css'), path.join(outDir, 'maplibre-gl.css'));

await copyFile(path.join(nodeModules, 'leaflet/LICENSE'), path.join(licensesDir, 'leaflet-BSD-2-Clause.txt'));
await copyFile(path.join(nodeModules, 'maplibre-gl/LICENSE.txt'), path.join(licensesDir, 'maplibre-gl-BSD-3-Clause.txt'));
await copyFile(path.join(nodeModules, '@maplibre/maplibre-gl-leaflet/LICENSE'), path.join(licensesDir, 'maplibre-gl-leaflet-ISC.txt'));

console.log(`Map vendor assets written to ${outDir}`);
