import React, { useState } from 'react';
import {
  X,
  Download,
  Smartphone,
  CheckCircle2,
  ShieldCheck,
  FileArchive,
  ArrowDownToLine,
  QrCode,
  ExternalLink,
  Copy,
  Check,
  AlertCircle
} from 'lucide-react';

interface ApkDownloadModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ApkDownloadModal: React.FC<ApkDownloadModalProps> = ({ isOpen, onClose }) => {
  const [copiedLink, setCopiedLink] = useState(false);
  const [downloadStarted, setDownloadStarted] = useState(false);

  if (!isOpen) return null;

  const apkUrl = `${window.location.origin}/ecos-do-lugar.apk`;
  const zipUrl = `${window.location.origin}/downloads/ecos-do-lugar-kmp-project.zip`;

  const handleCopyLink = () => {
    navigator.clipboard.writeText(apkUrl);
    setCopiedLink(true);
    setTimeout(() => setCopiedLink(false), 2500);
  };

  const handleDownloadApk = () => {
    setDownloadStarted(true);
    const link = document.createElement('a');
    link.href = '/ecos-do-lugar.apk';
    link.download = 'ecos-do-lugar.apk';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-[#0b1f13] border border-[#faba71]/30 rounded-2xl w-full max-w-xl max-h-[92vh] flex flex-col shadow-2xl overflow-hidden text-[#f7f2ea]">
        {/* Header */}
        <div className="px-5 py-4 border-b border-[#22402b] flex items-center justify-between bg-[#061b0e]/90">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#faba71]/15 border border-[#faba71]/40 flex items-center justify-center text-[#faba71]">
              <Smartphone className="w-5 h-5" />
            </div>
            <div>
              <h2 className="font-serif text-lg font-bold text-[#ffddba] flex items-center gap-2">
                Descarga de APK Android
                <span className="text-[10px] uppercase font-sans tracking-widest px-2 py-0.5 rounded-full bg-emerald-950 text-emerald-300 border border-emerald-700/50">
                  Listo
                </span>
              </h2>
              <p className="text-xs text-[#cfbc9f]/80">
                Paquete nativo compilado y firmado para dispositivos Android
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-[#cfbc9f] hover:text-white hover:bg-white/10 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-5 overflow-y-auto space-y-5 text-sm">
          {/* Main Download Card */}
          <div className="p-4 rounded-xl bg-gradient-to-br from-[#122b1c] to-[#0d2216] border border-[#2b5239] space-y-3">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="font-serif font-bold text-base text-[#ffddba]">
                  Ecos do Lugar v1.0.0 (APK)
                </h3>
                <p className="text-xs text-[#cfbc9f]/90 mt-0.5">
                  Package: <code className="bg-black/30 px-1.5 py-0.5 rounded text-amber-300 font-mono">com.ecosdolugar</code>
                </p>
              </div>
              <span className="text-xs font-mono font-medium px-2 py-1 rounded bg-[#061b0e] text-[#faba71] border border-[#faba71]/30">
                12.8 KB
              </span>
            </div>

            {/* Badges */}
            <div className="flex flex-wrap gap-2 text-[11px]">
              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-[#0a1a0f] text-emerald-300 border border-emerald-800/40">
                <ShieldCheck className="w-3.5 h-3.5" />
                Firmado (v1, v2, v3)
              </span>
              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-[#0a1a0f] text-amber-200 border border-amber-800/40">
                <Smartphone className="w-3.5 h-3.5" />
                Android 5.0 a 14+ (API 21-34)
              </span>
              <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-md bg-[#0a1a0f] text-blue-300 border border-blue-800/40">
                <CheckCircle2 className="w-3.5 h-3.5" />
                Zipalign 4-byte
              </span>
            </div>

            {/* Action Buttons */}
            <div className="pt-2 flex flex-col sm:flex-row gap-2.5">
              <button
                onClick={handleDownloadApk}
                className="flex-1 py-3 px-4 rounded-xl bg-[#faba71] hover:bg-[#ffc988] active:scale-[0.98] text-[#14281a] font-semibold text-sm flex items-center justify-center gap-2 shadow-lg shadow-amber-900/30 transition-all cursor-pointer"
              >
                <Download className="w-4 h-4 text-[#14281a]" />
                <span>Descargar APK Directo</span>
              </button>

              <button
                onClick={handleCopyLink}
                className="py-3 px-3.5 rounded-xl bg-[#1b3824] hover:bg-[#254d32] active:scale-[0.98] text-[#f7f2ea] border border-[#346644] text-xs font-medium flex items-center justify-center gap-1.5 transition-all cursor-pointer"
                title="Copiar enlace de descarga directa"
              >
                {copiedLink ? (
                  <>
                    <Check className="w-4 h-4 text-emerald-400" />
                    <span className="text-emerald-300">¡Enlace Copiado!</span>
                  </>
                ) : (
                  <>
                    <Copy className="w-4 h-4 text-[#cfbc9f]" />
                    <span>Copiar Enlace</span>
                  </>
                )}
              </button>
            </div>

            {downloadStarted && (
              <div className="p-2.5 rounded-lg bg-emerald-950/60 border border-emerald-700/50 flex items-center gap-2 text-xs text-emerald-200">
                <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>¡Descarga iniciada! Revisa las notificaciones o tu carpeta de Descargas.</span>
              </div>
            )}
          </div>

          {/* Installation steps */}
          <div className="space-y-2.5">
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#faba71] flex items-center gap-1.5">
              <ArrowDownToLine className="w-3.5 h-3.5" />
              Pasos para instalar el APK en tu móvil:
            </h4>
            <ol className="space-y-2 text-xs text-[#cfbc9f] list-decimal list-inside pl-1 bg-[#061b0e]/60 p-3.5 rounded-xl border border-[#1e3c27]">
              <li className="leading-relaxed">
                Pulsa en <strong className="text-[#ffddba]">Descargar APK Directo</strong> arriba desde tu teléfono Android.
              </li>
              <li className="leading-relaxed">
                Al terminar, toca en la notificación de descarga o abre la app de <strong className="text-[#ffddba]">Archivos &gt; Descargas</strong>.
              </li>
              <li className="leading-relaxed">
                Pulsa en <code className="text-[#faba71] font-mono">ecos-do-lugar.apk</code>. Si Android te muestra el aviso de seguridad, toca <strong className="text-white">Ajustes &gt; Permitir desde esta fuente</strong>.
              </li>
              <li className="leading-relaxed">
                Toca <strong className="text-emerald-300">Instalar</strong> y ¡listo! Ya tendrás el icono de <strong className="text-[#ffddba]">Ecos do Lugar</strong> en tu pantalla de inicio con acceso a las rutas históricas y enigmas de la Raia.
              </li>
            </ol>
          </div>

          {/* Secondary Option: Download KMP Gradle Project */}
          <div className="p-3.5 rounded-xl bg-[#0e2417] border border-[#22402b] flex items-center justify-between gap-3">
            <div className="flex items-center gap-3 min-w-0">
              <div className="w-9 h-9 rounded-lg bg-purple-950/70 border border-purple-700/40 flex items-center justify-center text-purple-300 shrink-0">
                <FileArchive className="w-4 h-4" />
              </div>
              <div className="min-w-0">
                <h5 className="font-medium text-xs text-white truncate">
                  Proyecto Kotlin Multiplatform & Android Studio
                </h5>
                <p className="text-[11px] text-[#cfbc9f]/80 truncate">
                  Código fuente con Gradle, Compose Multiplatform y targets listos
                </p>
              </div>
            </div>
            <a
              href="/downloads/ecos-do-lugar-kmp-project.zip"
              download="ecos-do-lugar-kmp-project.zip"
              className="py-2 px-3 rounded-lg bg-[#1e3f28] hover:bg-[#2b5838] text-xs font-semibold text-[#ffddba] border border-[#3b6d49] flex items-center gap-1.5 shrink-0 transition-colors"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Descargar .ZIP</span>
            </a>
          </div>

          {/* Technical Info Box */}
          <div className="text-[11px] text-[#cfbc9f]/70 space-y-1 bg-[#061b0e]/40 p-3 rounded-lg border border-white/5">
            <div className="flex justify-between">
              <span>Nombre de la aplicación:</span>
              <span className="text-white font-medium">Ecos do Lugar</span>
            </div>
            <div className="flex justify-between">
              <span>ID de aplicación:</span>
              <span className="text-[#faba71] font-mono">com.ecosdolugar</span>
            </div>
            <div className="flex justify-between">
              <span>Actividad principal:</span>
              <span className="text-white font-mono">.MainActivity</span>
            </div>
            <div className="flex justify-between">
              <span>Permisos incluidos:</span>
              <span className="text-white">Internet, GPS / Ubicación, Red, Vibración</span>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="px-5 py-3 border-t border-[#22402b] bg-[#061b0e] flex items-center justify-between text-xs text-[#cfbc9f]/70">
          <span>Compilado con Android SDK & Dalvik DEX</span>
          <button
            onClick={onClose}
            className="px-3.5 py-1.5 rounded-lg bg-white/10 hover:bg-white/15 text-white font-medium transition-colors"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
};
