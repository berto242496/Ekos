import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import fs from 'fs';
import {defineConfig} from 'vite';

export default defineConfig(() => {
  return {
    plugins: [
      react(),
      tailwindcss(),
      {
        name: 'serve-apk-and-downloads',
        configureServer(server) {
          server.middlewares.use((req, res, next) => {
            const cleanUrl = (req.url || '').split('?')[0];
            if (cleanUrl === '/ecos-do-lugar.apk' || cleanUrl === '/downloads/ecos-do-lugar.apk') {
              const apkPath = path.resolve(__dirname, 'public/ecos-do-lugar.apk');
              if (fs.existsSync(apkPath)) {
                res.setHeader('Content-Type', 'application/vnd.android.package-archive');
                res.setHeader('Content-Disposition', 'attachment; filename="ecos-do-lugar.apk"');
                const stat = fs.statSync(apkPath);
                res.setHeader('Content-Length', stat.size);
                fs.createReadStream(apkPath).pipe(res);
                return;
              }
            }
            if (cleanUrl === '/downloads/ecos-do-lugar-kmp-project.zip') {
              const zipPath = path.resolve(__dirname, 'public/downloads/ecos-do-lugar-kmp-project.zip');
              if (fs.existsSync(zipPath)) {
                res.setHeader('Content-Type', 'application/zip');
                res.setHeader('Content-Disposition', 'attachment; filename="ecos-do-lugar-kmp-project.zip"');
                const stat = fs.statSync(zipPath);
                res.setHeader('Content-Length', stat.size);
                fs.createReadStream(zipPath).pipe(res);
                return;
              }
            }
            next();
          });
        },
      },
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, '.'),
      },
    },
    server: {
      host: '0.0.0.0',
      port: 3000,
      allowedHosts: true,
      // HMR is disabled in AI Studio via DISABLE_HMR env var.
      // Do not modify - file watching is disabled to prevent flickering during agent edits.
      hmr: process.env.DISABLE_HMR !== 'true',
      // Disable file watching when DISABLE_HMR is true to save CPU during agent edits.
      watch: process.env.DISABLE_HMR === 'true' ? null : {},
    },
  };
});
