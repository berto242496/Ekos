import React, { useState, useEffect } from 'react';
import { RouteItem, GuideAuthor, GuidePayoutRecord, GuidePayoutSettings, GuideSalesRecord } from '../types';
import { GuidePayoutModal } from './GuidePayoutModal';
import { LanguageSelector } from './LanguageSelector';
import { useI18n } from '../services/i18n';
import {
  getStoredGuideBalance,
  saveGuideBalance,
  getStoredGuidePayoutSettings,
  saveGuidePayoutSettings,
  getStoredGuidePayoutHistory,
  saveGuidePayoutHistory,
  getStoredGuideSalesFeed,
  saveGuideSalesFeed,
} from '../services/paymentService';
import {
  Compass,
  PlusCircle,
  Eye,
  Play,
  Euro,
  FileEdit,
  CheckCircle2,
  ChevronLeft,
  ArrowUpRight,
  Sparkles,
  MapPin,
  Clock,
  Award,
  Layers,
  ExternalLink,
  Building2,
  Smartphone,
  CreditCard,
  Receipt,
  Download,
  Calendar,
  ShieldCheck,
  TrendingUp,
  Wallet,
  ArrowDownToLine,
  FileText,
  UserCheck,
  Radio,
} from 'lucide-react';

interface GuideDashboardScreenProps {
  guide: GuideAuthor;
  availableGuides: GuideAuthor[];
  onSelectGuide: (guideId: string) => void;
  guideRoutes: RouteItem[];
  onCreateRoute: () => void;
  onEditRoute: (routeId: string) => void;
  onViewRouteDetail: (routeId: string) => void;
  onBackToVisitor: () => void;
}

export const GuideDashboardScreen: React.FC<GuideDashboardScreenProps> = ({
  guide,
  availableGuides,
  onSelectGuide,
  guideRoutes,
  onCreateRoute,
  onEditRoute,
  onViewRouteDetail,
  onBackToVisitor,
}) => {
  const { t } = useI18n();
  const [activeTab, setActiveTab] = useState<'rutas' | 'cobros' | 'ventas'>('rutas');
  const [isPayoutModalOpen, setIsPayoutModalOpen] = useState(false);
  const [downloadNotice, setDownloadNotice] = useState<string | null>(null);

  // Synchronized persistent state for the active guide
  const [availableBalance, setAvailableBalance] = useState<number>(() =>
    getStoredGuideBalance(guide.id, 426.08)
  );
  const [payoutSettings, setPayoutSettings] = useState<GuidePayoutSettings>(() =>
    getStoredGuidePayoutSettings(guide.id)
  );
  const [payoutHistory, setPayoutHistory] = useState<GuidePayoutRecord[]>(() =>
    getStoredGuidePayoutHistory(guide.id)
  );
  const [salesFeed, setSalesFeed] = useState<GuideSalesRecord[]>(() =>
    getStoredGuideSalesFeed(guide.id)
  );

  useEffect(() => {
    setAvailableBalance(getStoredGuideBalance(guide.id, 426.08));
    setPayoutSettings(getStoredGuidePayoutSettings(guide.id));
    setPayoutHistory(getStoredGuidePayoutHistory(guide.id));
    setSalesFeed(getStoredGuideSalesFeed(guide.id));
  }, [guide.id]);

  // Compute metrics from current guide's routes
  const publishedRoutes = guideRoutes.filter((r) => (r.status || 'publicada') === 'publicada');
  const pendingRoutes = guideRoutes.filter((r) => r.status === 'pendiente');

  const totalViews = guideRoutes.reduce((acc, r) => acc + (r.viewsCount || 0), 0);
  const totalStarted = guideRoutes.reduce((acc, r) => acc + (r.startedCount || 0), 0);
  const totalGrossRevenue = guideRoutes.reduce((acc, r) => acc + (r.revenue || 0), 0);

  const totalWithdrawnGross = payoutHistory.reduce((acc, p) => acc + p.amount, 0);
  const totalNetReceived = payoutHistory.reduce((acc, p) => acc + p.netAmount, 0);

  const handlePayoutSuccess = (record: GuidePayoutRecord, updatedBalance: number) => {
    const updatedHistory = [record, ...payoutHistory];
    setPayoutHistory(updatedHistory);
    saveGuidePayoutHistory(guide.id, updatedHistory);
    setAvailableBalance(updatedBalance);
  };

  const handleUpdateSettings = (updated: GuidePayoutSettings) => {
    setPayoutSettings(updated);
    saveGuidePayoutSettings(guide.id, updated);
  };

  const handleDownloadInvoice = (invoiceRef: string) => {
    setDownloadNotice(`Descargando justificante fiscal oficial ${invoiceRef}.pdf...`);
    setTimeout(() => {
      setDownloadNotice(null);
    }, 2800);
  };

  return (
    <div className="min-h-screen bg-[#fcf9f2] text-[#1b3022] flex flex-col justify-between selection:bg-[#c48b47]/20">
      {/* Top Header */}
      <header className="border-b border-[#1b3022]/10 bg-[#fcf9f2]/90 backdrop-blur-md sticky top-0 z-20">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              id="guide-dash-back-btn"
              onClick={onBackToVisitor}
              className="inline-flex items-center gap-1.5 text-xs sm:text-sm font-serif text-[#1b3022]/80 hover:text-[#1b3022] px-3 py-1.5 rounded-lg border border-[#1b3022]/15 hover:border-[#1b3022]/30 transition-colors cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" />
              <span>{t('nav_visitor_mode')}</span>
            </button>
            <span className="hidden sm:inline text-xs text-[#1b3022]/40">|</span>
            <span className="hidden sm:inline text-xs uppercase tracking-widest font-semibold text-[#8c5717]">
              {t('guide_panel_title')}
            </span>
          </div>

          <div className="flex items-center gap-2.5">
            {/* Language Selector */}
            <LanguageSelector variant="compact" />

            {/* Guide Switcher (Mock Simulation) */}
            <div className="flex items-center gap-1.5 bg-white/80 border border-[#1b3022]/15 px-2.5 py-1 rounded-xl shadow-xs">
              <span className="text-[11px] text-[#47654e] font-serif hidden md:inline">{t('guide_active')}</span>
              <select
                id="guide-switcher-select"
                value={guide.id}
                onChange={(e) => onSelectGuide(e.target.value)}
                className="text-xs font-serif font-bold text-[#0f2416] bg-transparent outline-none cursor-pointer"
              >
                {availableGuides.map((g) => (
                  <option key={g.id} value={g.id}>
                    {g.name} ({g.title.split(' ')[0]}...)
                  </option>
                ))}
              </select>
            </div>

            <button
              id="guide-dash-create-btn"
              onClick={onCreateRoute}
              className="inline-flex items-center gap-2 px-3.5 sm:px-4 py-2 rounded-xl bg-[#1b3022] hover:bg-[#284632] text-[#fcf9f2] font-semibold text-xs sm:text-sm shadow-sm transition-all cursor-pointer"
            >
              <PlusCircle className="w-4 h-4 text-[#c48b47]" />
              <span>{t('guide_create_route_btn')}</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-8 sm:py-10 w-full flex-1">
        {/* Notice banner for invoice download */}
        {downloadNotice && (
          <div className="mb-6 p-3.5 rounded-xl bg-emerald-100 border border-emerald-300 text-emerald-900 text-xs font-serif flex items-center gap-2 animate-in fade-in duration-200">
            <CheckCircle2 className="w-4 h-4 text-emerald-700 shrink-0" />
            <span>{downloadNotice}</span>
          </div>
        )}

        {/* Guide Identity Banner */}
        <div className="bg-white/80 border border-[#1b3022]/10 rounded-2xl p-5 sm:p-6 mb-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-xs">
          <div className="flex items-center gap-4">
            {guide.avatarUrl ? (
              <img
                src={guide.avatarUrl}
                alt={guide.name}
                className="w-14 h-14 rounded-2xl object-cover border-2 border-[#c48b47]/40 shadow-xs"
              />
            ) : (
              <div className="w-14 h-14 rounded-2xl bg-[#1b3022]/10 flex items-center justify-center text-[#1b3022] font-serif font-bold text-xl">
                {guide.name.charAt(0)}
              </div>
            )}
            <div>
              <div className="flex items-center gap-2 mb-0.5">
                <h1 className="text-xl sm:text-2xl font-serif font-bold text-[#0f2416]">
                  {guide.name}
                </h1>
                {guide.verified && (
                  <span className="inline-flex items-center text-[11px] font-medium px-2 py-0.5 rounded-full bg-[#1b3022]/5 text-[#1b3022] border border-[#1b3022]/15">
                    <CheckCircle2 className="w-3 h-3 text-[#c48b47] mr-1" />
                    Guía Oficial Acreditado
                  </span>
                )}
              </div>
              <p className="text-xs sm:text-sm text-[#47654e] font-serif">
                {guide.title} · {guide.location}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2 self-end sm:self-auto text-xs font-serif text-[#1b3022]/80">
            <span className="bg-[#fcf9f2] px-3 py-1.5 rounded-lg border border-[#1b3022]/10">
              ★ {guide.rating} ({guide.reviewsCount} valoracións)
            </span>
          </div>
        </div>

        {/* Navigation Tabs for Guide Area */}
        <div className="flex items-center gap-2 border-b border-[#1b3022]/10 mb-6 pb-2">
          <button
            id="guide-tab-rutas"
            type="button"
            onClick={() => setActiveTab('rutas')}
            className={`px-4 py-2 rounded-xl font-serif text-sm font-semibold transition-all flex items-center gap-2 cursor-pointer ${
              activeTab === 'rutas'
                ? 'bg-[#1b3022] text-[#fcf9f2] shadow-xs'
                : 'text-[#47654e] hover:bg-[#1b3022]/5 hover:text-[#0f2416]'
            }`}
          >
            <Compass className="w-4 h-4" />
            <span>Itinerarios e Rutas ({guideRoutes.length})</span>
          </button>

          <button
            id="guide-tab-cobros"
            type="button"
            onClick={() => setActiveTab('cobros')}
            className={`px-4 py-2 rounded-xl font-serif text-sm font-semibold transition-all flex items-center gap-2 cursor-pointer ${
              activeTab === 'cobros'
                ? 'bg-[#1b3022] text-[#fcf9f2] shadow-xs'
                : 'text-[#47654e] hover:bg-[#1b3022]/5 hover:text-[#0f2416]'
            }`}
          >
            <Wallet className="w-4 h-4" />
            <span>Cobros e Liquidacións ({payoutHistory.length})</span>
          </button>

          <button
            id="guide-tab-ventas"
            type="button"
            onClick={() => setActiveTab('ventas')}
            className={`px-4 py-2 rounded-xl font-serif text-sm font-semibold transition-all flex items-center gap-2 cursor-pointer ${
              activeTab === 'ventas'
                ? 'bg-[#1b3022] text-[#fcf9f2] shadow-xs'
                : 'text-[#47654e] hover:bg-[#1b3022]/5 hover:text-[#0f2416]'
            }`}
          >
            <TrendingUp className="w-4 h-4" />
            <span>Vendas en Tempo Real ({salesFeed.length})</span>
          </button>
        </div>

        {/* TAB 1: ITINERARIOS Y RUTAS */}
        {activeTab === 'rutas' && (
          <div className="space-y-8">
            {/* Bento Metrics Grid */}
            <div className="grid grid-cols-2 sm:grid-cols-5 gap-3.5 sm:gap-4">
              {/* Published */}
              <div className="bg-white/70 border border-[#1b3022]/10 rounded-2xl p-4 sm:p-5 shadow-xs">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-serif uppercase tracking-wider text-[#47654e]">Rutas Publicadas</span>
                  <CheckCircle2 className="w-4 h-4 text-emerald-700" />
                </div>
                <div className="text-2xl sm:text-3xl font-serif font-bold text-[#0f2416]">
                  {publishedRoutes.length}
                </div>
                <p className="text-[11px] text-[#47654e] mt-1 font-serif">Dispoñibles no catálogo</p>
              </div>

              <div className="bg-amber-50 border border-amber-200 rounded-2xl p-4 sm:p-5 shadow-xs">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-serif uppercase tracking-wider text-amber-800">Pendientes de revisión</span>
                  <Clock className="w-4 h-4 text-amber-700" />
                </div>
                <div className="text-2xl sm:text-3xl font-serif font-bold text-amber-900">
                  {pendingRoutes.length}
                </div>
                <p className="text-[11px] text-amber-800 mt-1 font-serif">Ocultas del catálogo</p>
              </div>

              {/* Views */}
              <div className="bg-white/70 border border-[#1b3022]/10 rounded-2xl p-4 sm:p-5 shadow-xs">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-serif uppercase tracking-wider text-[#47654e]">Visualizaciones</span>
                  <Eye className="w-4 h-4 text-[#1b3022]" />
                </div>
                <div className="text-2xl sm:text-3xl font-serif font-bold text-[#0f2416]">
                  {totalViews.toLocaleString('es-ES')}
                </div>
                <p className="text-[11px] text-[#47654e] mt-1 font-serif">Visitantes en tus fichas</p>
              </div>

              {/* Started */}
              <div className="bg-white/70 border border-[#1b3022]/10 rounded-2xl p-4 sm:p-5 shadow-xs">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-serif uppercase tracking-wider text-[#47654e]">Rutas Iniciadas</span>
                  <Play className="w-4 h-4 text-emerald-800" />
                </div>
                <div className="text-2xl sm:text-3xl font-serif font-bold text-[#0f2416]">
                  {totalStarted.toLocaleString('es-ES')}
                </div>
                <p className="text-[11px] text-[#47654e] mt-1 font-serif">Expediciones completadas</p>
              </div>

              {/* Total Gross Revenue */}
              <div className="bg-[#1b3022] text-[#fcf9f2] border border-[#c48b47]/30 rounded-2xl p-4 sm:p-5 shadow-sm">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-serif uppercase tracking-wider text-[#c48b47]">Ingresos Brutos</span>
                  <Euro className="w-4 h-4 text-[#c48b47]" />
                </div>
                <div className="text-2xl sm:text-3xl font-serif font-bold text-[#fcf9f2]">
                  {totalGrossRevenue.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 })} €
                </div>
                <p className="text-[11px] text-[#fcf9f2]/70 mt-1 font-serif">Ventas totales acumuladas</p>
              </div>
            </div>

            {/* Routes List */}
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-serif font-bold text-[#0f2416]">Mis Itinerarios y Rutas</h2>
                  <p className="text-xs sm:text-sm text-[#47654e] font-serif">
                    Gestiona el contenido, las paradas con audio y el precio de cada experiencia
                  </p>
                </div>
                <button
                  onClick={onCreateRoute}
                  className="text-xs font-serif font-semibold text-[#8c5717] hover:text-[#0f2416] flex items-center gap-1 cursor-pointer"
                >
                  <span>+ Añadir otra ruta</span>
                </button>
              </div>

              {guideRoutes.length === 0 ? (
                <div className="bg-white/60 border border-dashed border-[#1b3022]/20 rounded-2xl p-10 text-center">
                  <Compass className="w-10 h-10 text-[#c48b47] mx-auto mb-3" />
                  <h3 className="text-lg font-serif font-bold text-[#0f2416] mb-1">Aún no tienes rutas creadas</h3>
                  <p className="text-sm text-[#47654e] font-serif max-w-md mx-auto mb-5">
                    Crea tu primera ruta cultural para compartir tu memoria de la zona y comenzar a generar ingresos directos.
                  </p>
                  <button
                    onClick={onCreateRoute}
                    className="px-5 py-2.5 rounded-xl bg-[#1b3022] text-[#fcf9f2] font-semibold text-sm cursor-pointer"
                  >
                    Crear mi primera ruta
                  </button>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {guideRoutes.map((route) => {
                    const isDraft = route.status === 'borrador';
                    const isPending = route.status === 'pendiente';
                    return (
                      <div
                        key={route.id}
                        className="bg-white/80 border border-[#1b3022]/10 rounded-2xl overflow-hidden hover:border-[#c48b47]/40 hover:shadow-md transition-all flex flex-col justify-between"
                      >
                        <div className="p-5">
                          {/* Top Bar with Status & Price */}
                          <div className="flex items-center justify-between gap-2 mb-3">
                            <span
                              className={`text-[11px] font-semibold tracking-wider uppercase px-2.5 py-0.5 rounded-full border ${
                                isDraft
                                  ? 'bg-amber-50 text-amber-800 border-amber-300'
                                  : isPending
                                    ? 'bg-orange-50 text-orange-800 border-orange-300'
                                    : 'bg-emerald-50 text-emerald-800 border-emerald-300'
                              }`}
                            >
                              {isDraft ? 'Borrador' : isPending ? 'Pendiente de verificación' : 'Publicada'}
                            </span>

                            <span className="text-xs font-serif font-bold px-2.5 py-1 rounded-lg bg-[#1b3022]/5 text-[#1b3022] border border-[#1b3022]/10">
                              {route.price || (route.isFree ? 'Gratis' : '1,99 €')}
                            </span>
                          </div>

                          {/* Title & Location */}
                          <h3 className="text-base sm:text-lg font-serif font-bold text-[#0f2416] mb-1 leading-snug">
                            {route.title}
                          </h3>
                          <div className="flex items-center gap-1.5 text-xs text-[#47654e] font-serif mb-3">
                            <MapPin className="w-3.5 h-3.5 text-[#c48b47]" />
                            <span>{route.location}</span>
                            <span>·</span>
                            <Clock className="w-3.5 h-3.5 text-[#47654e]" />
                            <span>{route.duration}</span>
                            <span>·</span>
                            <Layers className="w-3.5 h-3.5 text-[#47654e]" />
                            <span>{route.stopsCount || route.stops?.length || 0} paradas</span>
                          </div>

                          <p className="text-xs text-[#334b39] line-clamp-2 mb-4 font-serif leading-relaxed">
                            {route.description}
                          </p>

                          {/* Route Mini Metrics */}
                          <div className="grid grid-cols-3 gap-2 py-2.5 px-3 rounded-xl bg-[#fcf9f2] border border-[#1b3022]/5 text-center text-xs font-serif">
                            <div>
                              <span className="text-[10px] uppercase text-[#47654e] block">Vistas</span>
                              <span className="font-bold text-[#0f2416]">
                                {(route.viewsCount || 0).toLocaleString('es-ES')}
                              </span>
                            </div>
                            <div>
                              <span className="text-[10px] uppercase text-[#47654e] block">Inicios</span>
                              <span className="font-bold text-[#0f2416]">
                                {(route.startedCount || 0).toLocaleString('es-ES')}
                              </span>
                            </div>
                            <div>
                              <span className="text-[10px] uppercase text-[#47654e] block">Ingresos</span>
                              <span className="font-bold text-emerald-800">
                                {(route.revenue || 0).toLocaleString('es-ES', {
                                  minimumFractionDigits: 2,
                                  maximumFractionDigits: 2,
                                })}{' '}
                                €
                              </span>
                            </div>
                          </div>
                        </div>

                        {/* Bottom Action bar */}
                        <div className="border-t border-[#1b3022]/10 bg-[#fcf9f2]/50 px-5 py-3 flex items-center justify-between">
                          <button
                            id={`view-route-${route.id}-btn`}
                            onClick={() => onViewRouteDetail(route.id)}
                            className="inline-flex items-center gap-1.5 text-xs font-serif font-semibold text-[#1b3022] hover:text-[#c48b47] transition-colors cursor-pointer"
                          >
                            <ExternalLink className="w-3.5 h-3.5" />
                            <span>Ver no catálogo</span>
                          </button>

                          <div className="flex items-center gap-3">
                            <span className="hidden text-[11px] text-[#47654e] font-serif sm:inline">
                              {route.stops?.length || 0} paradas con audio e enigmas
                            </span>
                            <button
                              id={`edit-route-${route.id}-btn`}
                              type="button"
                              onClick={() => onEditRoute(route.id)}
                              className="inline-flex items-center gap-1.5 rounded-lg border border-[#1b3022]/15 bg-white px-3 py-1.5 text-xs font-serif font-semibold text-[#1b3022] transition-colors hover:border-[#c48b47] hover:text-[#8c5717]"
                            >
                              <FileEdit className="h-3.5 w-3.5" />
                              Editar ruta
                            </button>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB 2: COBROS, LIQUIDACIONES Y FACTURAS */}
        {activeTab === 'cobros' && (
          <div className="space-y-6">
            {/* Destination Account Card */}
            <div className="bg-white rounded-2xl p-5 border border-[#1b3022]/10 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div className="flex items-start gap-4">
                <div className="w-12 h-12 rounded-xl bg-[#1b3022]/10 text-[#1b3022] flex items-center justify-center shrink-0">
                  {payoutSettings.method === 'iban' ? (
                    <Building2 className="w-6 h-6" />
                  ) : (
                    <Smartphone className="w-6 h-6 text-[#009999]" />
                  )}
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="font-serif text-base font-bold text-[#061b0e]">
                      Conta de Transferencia SEPA Acreditada
                    </h3>
                    <span className="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 text-[10px] font-bold border border-emerald-300">
                      Activa e Verificada
                    </span>
                  </div>
                  <p className="font-mono text-xs font-semibold text-[#47654e] mt-0.5">
                    {payoutSettings.iban}
                  </p>
                  <p className="text-xs text-[#737973] font-serif mt-0.5">
                    Titular: {payoutSettings.accountHolder} · NIF: {payoutSettings.taxId} · {payoutSettings.bankName}
                  </p>
                </div>
              </div>

              <button
                type="button"
                onClick={() => setIsPayoutModalOpen(true)}
                className="px-4 py-2 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] text-white font-serif text-xs font-semibold shrink-0 transition-colors shadow-xs flex items-center justify-center gap-1.5 cursor-pointer"
              >
                <span>Xestionar cobro</span>
                <ArrowUpRight className="w-3.5 h-3.5 text-[#ffddba]" />
              </button>
            </div>

            {/* Payouts History Table */}
            <div className="bg-white rounded-2xl border border-[#1b3022]/10 overflow-hidden shadow-xs">
              <div className="p-5 border-b border-[#1b3022]/10 flex items-center justify-between flex-wrap gap-2">
                <div>
                  <h3 className="font-serif text-lg font-bold text-[#061b0e]">
                    Historial de Liquidacións Bancarias e Facturas
                  </h3>
                  <p className="text-xs text-[#47654e] font-serif">
                    Liquidacións periódicas emitidas co modelo de retención fiscal do 15% de IRPF
                  </p>
                </div>

                <button
                  type="button"
                  onClick={() => setIsPayoutModalOpen(true)}
                  className="px-3.5 py-1.5 rounded-lg bg-[#c48b47] hover:bg-[#d89e5a] text-[#061b0e] font-serif font-bold text-xs flex items-center gap-1.5 shadow-2xs transition-colors cursor-pointer"
                >
                  <ArrowDownToLine className="w-3.5 h-3.5" />
                  <span>Nova liquidación</span>
                </button>
              </div>

              {payoutHistory.length === 0 ? (
                <div className="p-8 text-center text-xs text-[#737973] font-serif">
                  Aínda non realizaches ningunha retirada de fondos.
                </div>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-xs font-serif">
                    <thead className="bg-[#fcf9f2] text-[#47654e] uppercase text-[10px] tracking-wider border-b border-[#1b3022]/10">
                      <tr>
                        <th className="py-3 px-4 font-semibold">Ref. Factura</th>
                        <th className="py-3 px-4 font-semibold">Data</th>
                        <th className="py-3 px-4 font-semibold">Destino</th>
                        <th className="py-3 px-4 font-semibold text-right">Bruto</th>
                        <th className="py-3 px-4 font-semibold text-right">IRPF (15%)</th>
                        <th className="py-3 px-4 font-semibold text-right">Neto Cobrado</th>
                        <th className="py-3 px-4 font-semibold text-center">Xustificante</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-[#1b3022]/5">
                      {payoutHistory.map((item) => (
                        <tr key={item.id} className="hover:bg-[#fcf9f2]/50 transition-colors">
                          <td className="py-3 px-4 font-mono font-bold text-[#061b0e]">{item.invoiceRef}</td>
                          <td className="py-3 px-4 text-[#434843]">{item.date}</td>
                          <td className="py-3 px-4 text-[#47654e]">{item.destination}</td>
                          <td className="py-3 px-4 font-mono text-right text-[#434843]">
                            {item.amount.toFixed(2).replace('.', ',')} €
                          </td>
                          <td className="py-3 px-4 font-mono text-right text-[#8c5717]">
                            - {item.withholdingTax.toFixed(2).replace('.', ',')} €
                          </td>
                          <td className="py-3 px-4 font-mono font-bold text-right text-emerald-800 text-sm">
                            {item.netAmount.toFixed(2).replace('.', ',')} €
                          </td>
                          <td className="py-3 px-4 text-center">
                            <button
                              type="button"
                              onClick={() => handleDownloadInvoice(item.invoiceRef)}
                              className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-[#fcf9f2] hover:bg-[#ebe6db] border border-[#1b3022]/15 text-[11px] font-semibold text-[#061b0e] transition-colors cursor-pointer"
                            >
                              <Download className="w-3 h-3 text-[#c48b47]" />
                              <span>Descargar</span>
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB 3: VENTAS EN TIEMPO REAL */}
        {activeTab === 'ventas' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-serif text-lg font-bold text-[#061b0e]">
                  Ventas Directas de Rutas a Peregrinos y Viajeros
                </h3>
                <p className="text-xs text-[#47654e] font-serif">
                  Registro de las compras realizadas por los usuarios. Cada compra transfiere el 85% directamente a tu saldo.
                </p>
              </div>

              <span className="text-xs px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 font-mono font-bold border border-emerald-300">
                85% automático
              </span>
            </div>

            <div className="grid grid-cols-1 gap-3">
              {salesFeed.map((sale) => (
                <div
                  key={sale.id}
                  className="p-4 rounded-2xl bg-white border border-[#1b3022]/10 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3"
                >
                  <div className="flex items-center gap-3.5">
                    <div className="w-10 h-10 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 flex items-center justify-center shrink-0">
                      <Euro className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <h4 className="font-serif text-sm font-bold text-[#061b0e]">
                          {sale.routeTitle}
                        </h4>
                        <span className="text-[10px] uppercase font-mono px-2 py-0.2 rounded-full bg-[#1b3022]/5 text-[#47654e]">
                          {sale.paymentMethod === 'gpay'
                            ? 'Google Pay'
                            : sale.paymentMethod === 'bizum'
                            ? 'Bizum'
                            : 'Tarxeta'}
                        </span>
                      </div>
                      <p className="text-xs text-[#47654e] font-serif mt-0.5">
                        Adquirida por {sale.buyerName} · <span className="text-[#737973]">{sale.date}</span>
                      </p>
                    </div>
                  </div>

                  <div className="flex sm:flex-col items-end justify-between sm:justify-center border-t sm:border-t-0 pt-2 sm:pt-0 border-[#1b3022]/5">
                    <span className="font-mono text-base font-bold text-emerald-800">
                      + {sale.netGuideAmount.toFixed(2).replace('.', ',')} € <span className="text-xs font-normal text-[#47654e]">(neto)</span>
                    </span>
                    <span className="text-[11px] text-[#737973] font-mono">
                      Prezo pagado: {sale.grossAmount.toFixed(2).replace('.', ',')} €
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>

      {/* Guide Payout Modal */}
      <GuidePayoutModal
        guide={guide}
        availableBalance={availableBalance}
        payoutSettings={payoutSettings}
        isOpen={isPayoutModalOpen}
        onClose={() => setIsPayoutModalOpen(false)}
        onPayoutSuccess={handlePayoutSuccess}
        onUpdateSettings={handleUpdateSettings}
      />

      {/* Footer */}
      <footer className="border-t border-[#1b3022]/10 py-5 text-center text-xs text-[#1b3022]/60 font-serif">
        Panel de Guía e Liquidacións de Galicia · Ecos do Lugar © 2026
      </footer>
    </div>
  );
};
