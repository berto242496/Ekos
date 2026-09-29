import React, { useState } from 'react';
import { RouteItem, UserProfile, UserPurchaseRecord } from '../types';
import { APP_LOGO_URL } from '../data/routesData';
import {
  parsePriceNumber,
  GUIDE_REVENUE_PERCENT,
  PLATFORM_COMMISSION_PERCENT,
  getStoredGuideBalance,
  saveGuideBalance,
  getStoredGuideSalesFeed,
  saveGuideSalesFeed,
} from '../services/paymentService';
import {
  X,
  Lock,
  CheckCircle2,
  ShieldCheck,
  CreditCard,
  Smartphone,
  Sparkles,
  ArrowRight,
  Headphones,
  MapPin,
  Clock,
  Radio,
  FileText,
  Star,
  Download,
  AlertCircle,
} from 'lucide-react';

interface UserCheckoutModalProps {
  route: RouteItem;
  user: UserProfile;
  isOpen: boolean;
  onClose: () => void;
  onPurchaseSuccess: (purchaseRecord: UserPurchaseRecord) => void;
  onStartRouteImmediately?: () => void;
}

type PaymentMethodType = 'gpay' | 'bizum' | 'card';

export const UserCheckoutModal: React.FC<UserCheckoutModalProps> = ({
  route,
  user,
  isOpen,
  onClose,
  onPurchaseSuccess,
  onStartRouteImmediately,
}) => {
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethodType>('gpay');
  const [isProcessing, setIsProcessing] = useState(false);
  const [purchaseSuccessRecord, setPurchaseSuccessRecord] = useState<UserPurchaseRecord | null>(null);

  // Card fields state
  const [cardNumber, setCardNumber] = useState('4532 •••• •••• 8821');
  const [cardExpiry, setCardExpiry] = useState('08/29');
  const [cardCvc, setCardCvc] = useState('382');
  const [cardHolder, setCardHolder] = useState(user.name || 'Explorador de Galicia');

  // Bizum field state
  const [bizumPhone, setBizumPhone] = useState('682 91 44 20');

  // Promo code
  const [promoCode, setPromoCode] = useState('');
  const [promoApplied, setPromoApplied] = useState(false);
  const [promoError, setPromoError] = useState('');

  if (!isOpen) return null;

  const grossPrice = parsePriceNumber(route.price);
  const finalPrice = promoApplied ? Math.max(0, grossPrice - 1.0) : grossPrice;
  const guideNet = (finalPrice * (GUIDE_REVENUE_PERCENT / 100)).toFixed(2);
  const platformFee = (finalPrice * (PLATFORM_COMMISSION_PERCENT / 100)).toFixed(2);

  const handleApplyPromo = (e: React.FormEvent) => {
    e.preventDefault();
    if (promoCode.trim().toUpperCase() === 'GALICIAMAXICA' || promoCode.trim().toUpperCase() === 'CAMINO') {
      setPromoApplied(true);
      setPromoError('');
    } else {
      setPromoError('Código non válido. Proba con GALICIAMAXICA');
    }
  };

  const handleProcessPayment = () => {
    setIsProcessing(true);

    setTimeout(() => {
      const txRef = `ECOS-GA-${Date.now().toString().slice(-6)}`;
      const record: UserPurchaseRecord = {
        id: `purch-${Date.now()}`,
        routeId: route.id,
        routeTitle: route.title,
        guideName: route.guide?.name || 'Guía Oficial',
        pricePaid: finalPrice,
        date: new Date().toLocaleDateString('es-ES', {
          day: '2-digit',
          month: 'short',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
        }),
        paymentMethod,
        transactionRef: txRef,
      };

      // Update guide's balance and sales feed dynamically in localStorage
      if (route.guide?.id) {
        const currentBal = getStoredGuideBalance(route.guide.id, 426.08);
        const netToAdd = parseFloat(guideNet);
        saveGuideBalance(route.guide.id, currentBal + netToAdd);

        const currentSales = getStoredGuideSalesFeed(route.guide.id);
        const newSale = {
          id: `sale-${Date.now()}`,
          date: 'Agora mesmo',
          routeId: route.id,
          routeTitle: route.title,
          grossAmount: finalPrice,
          netGuideAmount: netToAdd,
          buyerName: user.name || 'Explorador',
          paymentMethod,
        };
        saveGuideSalesFeed(route.guide.id, [newSale, ...currentSales]);
      }

      setIsProcessing(false);
      setPurchaseSuccessRecord(record);
      onPurchaseSuccess(record);
    }, 1200);
  };

  const handleCloseAll = () => {
    setPurchaseSuccessRecord(null);
    onClose();
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-[#061b0e]/80 backdrop-blur-md flex items-end sm:items-center justify-center p-0 sm:p-4 animate-in fade-in duration-200"
      onClick={handleCloseAll}
    >
      <div
        className="w-full max-w-lg bg-[#fcf9f2] rounded-t-3xl sm:rounded-3xl max-h-[92vh] overflow-y-auto shadow-2xl border border-[#1b3022]/15 flex flex-col animate-in slide-in-from-bottom sm:zoom-in-95 duration-200 text-[#1c1c18]"
        onClick={e => e.stopPropagation()}
      >
        {/* Modal Top Bar */}
        <div className="sticky top-0 bg-[#fcf9f2]/95 backdrop-blur-md z-10 px-5 py-3.5 border-b border-[#1b3022]/10 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <img src={APP_LOGO_URL} alt="Ecos do Lugar" className="h-6 w-auto" />
            <div>
              <h3 className="font-serif text-[17px] font-bold text-[#061b0e] leading-tight">
                {purchaseSuccessRecord ? 'Ticket de Compra' : 'Desbloquear Ruta'}
              </h3>
              <p className="text-[11px] text-[#47654e] font-sans">
                {purchaseSuccessRecord ? 'Acceso Permanente Activado' : 'Pago Seguro e Apoio ao Guía'}
              </p>
            </div>
          </div>

          <button
            onClick={handleCloseAll}
            className="w-8 h-8 rounded-full bg-[#ebe8e1] flex items-center justify-center text-[#1c1c18] hover:bg-[#dcdad3] cursor-pointer transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Content Body: SUCCESS STATE */}
        {purchaseSuccessRecord ? (
          <div className="p-6 flex flex-col gap-5 text-center">
            <div className="w-16 h-16 rounded-2xl bg-emerald-100 border border-emerald-300 text-emerald-800 flex items-center justify-center mx-auto shadow-sm animate-bounce duration-1000">
              <CheckCircle2 className="w-9 h-9" />
            </div>

            <div>
              <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-emerald-50 text-emerald-900 border border-emerald-300 text-xs font-semibold uppercase tracking-wider mb-1.5">
                <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
                Pago Confirmado
              </span>
              <h2 className="font-serif text-2xl font-bold text-[#061b0e]">
                ¡Ruta Desbloqueada con Éxito!
              </h2>
              <p className="text-xs text-[#434843] max-w-sm mx-auto mt-1 font-serif">
                Tes acceso ilimitado para sempre a todos os relatos en audio, enigmas e segredos desta experiencia cultural.
              </p>
            </div>

            {/* Receipt Summary Card */}
            <div className="p-4 rounded-2xl bg-white border border-[#1b3022]/10 shadow-xs text-left text-xs font-serif space-y-2.5">
              <div className="flex items-center justify-between pb-2 border-b border-[#1b3022]/10 font-sans">
                <span className="text-[#47654e] font-medium text-[11px]">Referencia de transacción:</span>
                <span className="font-mono font-bold text-[#061b0e]">{purchaseSuccessRecord.transactionRef}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">Ruta adquirida:</span>
                <span className="font-bold text-[#061b0e] truncate max-w-[200px]">{route.title}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">Guía acreditado:</span>
                <span className="font-bold text-[#061b0e]">{route.guide?.name}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">Método empregado:</span>
                <span className="capitalize font-semibold text-[#061b0e]">
                  {purchaseSuccessRecord.paymentMethod === 'gpay'
                    ? 'Google Pay'
                    : purchaseSuccessRecord.paymentMethod === 'bizum'
                    ? 'Bizum'
                    : 'Tarxeta bancaria'}
                </span>
              </div>

              <div className="flex items-center justify-between pt-2 border-t border-[#1b3022]/10 font-bold text-sm">
                <span className="text-[#061b0e]">Total pagado:</span>
                <span className="text-emerald-800 font-mono text-base">
                  {purchaseSuccessRecord.pricePaid.toFixed(2).replace('.', ',')} €
                </span>
              </div>
            </div>

            {/* Guide direct support badge */}
            <div className="p-3.5 rounded-xl bg-[#c48b47]/15 border border-[#c48b47]/30 text-left flex items-start gap-3">
              <div className="w-8 h-8 rounded-full bg-[#c48b47]/20 flex items-center justify-center shrink-0 text-[#8c5717]">
                <Star className="w-4 h-4 fill-current" />
              </div>
              <div className="text-xs">
                <p className="font-bold text-[#8c5717] font-serif">Apoio Directo ao Patrimonio Galego</p>
                <p className="text-[11px] text-[#434843] leading-relaxed mt-0.5">
                  <span className="font-semibold text-[#061b0e]">{guideNet} €</span> transferíronse directamente ao balance de {route.guide?.name}. Moitas grazas por apoiar aos creadores locais.
                </p>
              </div>
            </div>

            {/* Action buttons */}
            <div className="flex flex-col gap-2 pt-2">
              <button
                id="btn-start-purchased-route"
                onClick={() => {
                  handleCloseAll();
                  if (onStartRouteImmediately) {
                    onStartRouteImmediately();
                  }
                }}
                className="w-full py-3.5 px-4 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] text-[#fcf9f2] font-semibold text-sm flex items-center justify-center gap-2 shadow-md transition-all cursor-pointer"
              >
                <span>Comezar expedición agora</span>
                <ArrowRight className="w-4 h-4 text-[#ffddba]" />
              </button>

              <button
                onClick={handleCloseAll}
                className="py-2.5 text-xs text-[#47654e] hover:text-[#1c1c18] font-sans font-medium transition-colors"
              >
                Volver ao catálogo de rutas
              </button>
            </div>
          </div>
        ) : (
          /* Content Body: CHECKOUT FORM */
          <div className="p-5 flex flex-col gap-4">
            {/* Route Summary Card */}
            <div className="p-4 rounded-2xl bg-white border border-[#1b3022]/10 shadow-xs flex items-start gap-3.5">
              <img
                src={route.image}
                alt={route.title}
                className="w-20 h-20 rounded-xl object-cover border border-[#1b3022]/10 shrink-0"
              />
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-[#1b3022]/5 text-[#47654e]">
                    {route.region.split('·')[0].trim()}
                  </span>
                  <span className="font-mono text-xs font-bold text-emerald-800 ml-auto">
                    {finalPrice.toFixed(2).replace('.', ',')} €
                  </span>
                </div>
                <h4 className="font-serif text-base font-bold text-[#061b0e] leading-snug line-clamp-1">
                  {route.title}
                </h4>
                <div className="flex items-center gap-1.5 text-xs text-[#47654e] font-serif mt-1">
                  <Radio className="w-3.5 h-3.5 text-[#c48b47]" />
                  <span>{route.stopsCount} paradas con audio</span>
                  <span>·</span>
                  <span>{route.duration}</span>
                </div>
                {route.guide && (
                  <div className="flex items-center gap-1 text-[11px] text-[#8c5717] font-serif mt-1">
                    <span>Guía:</span>
                    <span className="font-semibold text-[#061b0e]">{route.guide.name}</span>
                    {route.guide.verified && <CheckCircle2 className="w-3 h-3 text-[#c48b47]" />}
                  </div>
                )}
              </div>
            </div>

            {/* Cultural Fair-Share Badge (85% Guide Guarantee) */}
            <div className="p-3.5 rounded-xl bg-gradient-to-r from-[#1b3022] to-[#0b2414] text-white border border-[#2b4c37] shadow-sm flex items-center justify-between gap-3">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-lg bg-white/10 flex items-center justify-center text-[#ffddba] shrink-0 border border-white/15">
                  <Star className="w-4 h-4 fill-current text-[#faba71]" />
                </div>
                <div className="flex flex-col min-w-0">
                  <span className="font-serif text-xs font-bold text-[#ffddba]">
                    85% directo ao guía local ({guideNet} €)
                  </span>
                  <p className="font-sans text-[11px] text-white/80 truncate">
                    Apoias a creación de contido cultural galego independente
                  </p>
                </div>
              </div>
              <span className="text-[10px] px-2 py-0.5 rounded-full bg-white/15 font-mono text-[#ffddba] shrink-0 font-bold">
                85% / 15%
              </span>
            </div>

            {/* Payment Method Selector */}
            <div className="space-y-2">
              <label className="text-xs font-serif uppercase tracking-wider font-semibold text-[#47654e]">
                Selecciona método de pago seguro
              </label>

              <div className="grid grid-cols-3 gap-2">
                {/* Option 1: Google Pay */}
                <button
                  type="button"
                  onClick={() => setPaymentMethod('gpay')}
                  className={`p-3 rounded-xl border flex flex-col items-center justify-center gap-1.5 transition-all cursor-pointer ${
                    paymentMethod === 'gpay'
                      ? 'bg-white border-[#1b3022] shadow-sm ring-2 ring-[#1b3022]/20'
                      : 'bg-white/70 border-[#1b3022]/15 hover:bg-white text-[#434843]'
                  }`}
                >
                  <div className="h-5 flex items-center justify-center">
                    <svg className="h-4.5 w-auto" viewBox="0 0 24 24">
                      <path
                        fill="#4285F4"
                        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                      />
                      <path
                        fill="#34A853"
                        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                      />
                      <path
                        fill="#FBBC05"
                        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                      />
                      <path
                        fill="#EA4335"
                        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                      />
                    </svg>
                  </div>
                  <span className="text-xs font-semibold text-[#061b0e]">Google Pay</span>
                </button>

                {/* Option 2: Bizum */}
                <button
                  type="button"
                  onClick={() => setPaymentMethod('bizum')}
                  className={`p-3 rounded-xl border flex flex-col items-center justify-center gap-1.5 transition-all cursor-pointer ${
                    paymentMethod === 'bizum'
                      ? 'bg-white border-[#009999] shadow-sm ring-2 ring-[#009999]/25'
                      : 'bg-white/70 border-[#1b3022]/15 hover:bg-white text-[#434843]'
                  }`}
                >
                  <div className="h-5 flex items-center justify-center">
                    <span className="font-sans font-black text-sm text-[#009999] tracking-tighter">
                      bizum
                    </span>
                  </div>
                  <span className="text-xs font-semibold text-[#061b0e]">Bizum</span>
                </button>

                {/* Option 3: Tarjeta Bancaria */}
                <button
                  type="button"
                  onClick={() => setPaymentMethod('card')}
                  className={`p-3 rounded-xl border flex flex-col items-center justify-center gap-1.5 transition-all cursor-pointer ${
                    paymentMethod === 'card'
                      ? 'bg-white border-[#1b3022] shadow-sm ring-2 ring-[#1b3022]/20'
                      : 'bg-white/70 border-[#1b3022]/15 hover:bg-white text-[#434843]'
                  }`}
                >
                  <CreditCard className="w-5 h-5 text-[#1b3022]" />
                  <span className="text-xs font-semibold text-[#061b0e]">Tarxeta</span>
                </button>
              </div>
            </div>

            {/* Dynamic Payment Method Fields */}
            {paymentMethod === 'gpay' && (
              <div className="p-3.5 rounded-xl bg-white border border-[#1b3022]/10 shadow-xs flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-slate-100 flex items-center justify-center shrink-0">
                  <Lock className="w-4 h-4 text-slate-700" />
                </div>
                <div className="min-w-0 flex-1 text-xs">
                  <p className="font-semibold text-[#061b0e]">Pago biométrico en 1 toque</p>
                  <p className="text-[11px] text-[#434843]">
                    Conta vinculada con Google: <span className="font-mono text-[#061b0e]">{user.email || 'bert2424.b2@gmail.com'}</span>
                  </p>
                </div>
              </div>
            )}

            {paymentMethod === 'bizum' && (
              <div className="p-3.5 rounded-xl bg-white border border-[#009999]/30 shadow-xs space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-[#009999] flex items-center gap-1">
                    <Smartphone className="w-3.5 h-3.5" />
                    Número de teléfono para Bizum
                  </span>
                  <span className="text-[10px] text-[#737973]">Instantáneo</span>
                </div>
                <input
                  type="tel"
                  value={bizumPhone}
                  onChange={e => setBizumPhone(e.target.value)}
                  placeholder="682 91 44 20"
                  className="w-full text-sm font-mono font-bold text-[#061b0e] bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] focus:outline-none focus:border-[#009999]"
                />
                <p className="text-[11px] text-[#737973] font-serif">
                  Recibirás una solicitud en la aplicación de tu banco para confirmar el pago de {finalPrice.toFixed(2)} €.
                </p>
              </div>
            )}

            {paymentMethod === 'card' && (
              <div className="p-3.5 rounded-xl bg-white border border-[#1b3022]/15 shadow-xs space-y-2.5">
                <div>
                  <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                    Número de tarjeta
                  </label>
                  <input
                    type="text"
                    value={cardNumber}
                    onChange={e => setCardNumber(e.target.value)}
                    className="w-full text-xs font-mono font-bold text-[#061b0e] bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] focus:outline-none focus:border-[#1b3022]"
                  />
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                      Caducidad
                    </label>
                    <input
                      type="text"
                      value={cardExpiry}
                      onChange={e => setCardExpiry(e.target.value)}
                      placeholder="MM/AA"
                      className="w-full text-xs font-mono font-bold text-[#061b0e] bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] focus:outline-none focus:border-[#1b3022]"
                    />
                  </div>
                  <div>
                    <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                      CVC / CVV
                    </label>
                    <input
                      type="password"
                      maxLength={4}
                      value={cardCvc}
                      onChange={e => setCardCvc(e.target.value)}
                      className="w-full text-xs font-mono font-bold text-[#061b0e] bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] focus:outline-none focus:border-[#1b3022]"
                    />
                  </div>
                </div>

                <div>
                  <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                    Titular de la tarjeta
                  </label>
                  <input
                    type="text"
                    value={cardHolder}
                    onChange={e => setCardHolder(e.target.value)}
                    className="w-full text-xs font-sans text-[#061b0e] bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] focus:outline-none focus:border-[#1b3022]"
                  />
                </div>
              </div>
            )}

            {/* Promo Code Toggle / Field */}
            {!promoApplied ? (
              <form onSubmit={handleApplyPromo} className="flex gap-2">
                <input
                  type="text"
                  value={promoCode}
                  onChange={e => setPromoCode(e.target.value)}
                  placeholder="¿Tienes un código promocional o bono peregrino?"
                  className="flex-1 text-xs bg-white px-3 py-2 rounded-xl border border-[#c3c8c1] focus:outline-none uppercase font-mono"
                />
                <button
                  type="submit"
                  className="px-3 py-2 rounded-xl bg-[#1b3022]/10 hover:bg-[#1b3022]/20 text-[#1b3022] font-semibold text-xs transition-colors"
                >
                  Aplicar
                </button>
              </form>
            ) : (
              <div className="p-2.5 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 flex items-center justify-between text-xs">
                <span>✓ Código aplicado: -1,00 € de descuento</span>
                <button
                  type="button"
                  onClick={() => setPromoApplied(false)}
                  className="text-[11px] underline font-medium"
                >
                  Eliminar
                </button>
              </div>
            )}
            {promoError && <p className="text-[11px] text-red-600 font-sans">{promoError}</p>}

            {/* Included in Purchase Checklist */}
            <div className="p-3.5 rounded-xl bg-[#f1eee7]/60 border border-[#e5e2db] text-xs font-serif space-y-1.5">
              <span className="font-semibold text-[#061b0e] block mb-1">Tu compra incluye:</span>
              <div className="flex items-center gap-2 text-[#434843]">
                <CheckCircle2 className="w-3.5 h-3.5 text-[#1b3022] shrink-0" />
                <span>Audios narrados en alta fidelidad en cada parada</span>
              </div>
              <div className="flex items-center gap-2 text-[#434843]">
                <CheckCircle2 className="w-3.5 h-3.5 text-[#1b3022] shrink-0" />
                <span>Modo sin conexión con mapa GPS descargable</span>
              </div>
              <div className="flex items-center gap-2 text-[#434843]">
                <CheckCircle2 className="w-3.5 h-3.5 text-[#1b3022] shrink-0" />
                <span>Enigmas históricos y recompensas mitológicas</span>
              </div>
              <div className="flex items-center gap-2 text-[#434843]">
                <CheckCircle2 className="w-3.5 h-3.5 text-[#1b3022] shrink-0" />
                <span>Acceso para siempre sin caducidad</span>
              </div>
            </div>

            {/* Price breakdown and checkout CTA */}
            <div className="pt-2 border-t border-[#1b3022]/10 space-y-3">
              <div className="flex items-center justify-between text-xs font-serif">
                <span className="text-[#47654e]">Total a pagar (IVE incluído):</span>
                <span className="font-bold font-mono text-lg text-[#061b0e]">
                  {finalPrice.toFixed(2).replace('.', ',')} €
                </span>
              </div>

              <button
                id="btn-confirm-payment"
                type="button"
                onClick={handleProcessPayment}
                disabled={isProcessing}
                className="w-full py-3.5 px-4 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] active:scale-[0.99] text-[#fcf9f2] font-semibold text-sm flex items-center justify-center gap-2 shadow-lg transition-all cursor-pointer disabled:opacity-75"
              >
                {isProcessing ? (
                  <div className="flex items-center gap-2">
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                    <span>Procesando pago seguro...</span>
                  </div>
                ) : (
                  <>
                    <Lock className="w-4 h-4 text-[#ffddba]" />
                    <span>
                      Pagar {finalPrice.toFixed(2).replace('.', ',')} € con{' '}
                      {paymentMethod === 'gpay'
                        ? 'Google Pay'
                        : paymentMethod === 'bizum'
                        ? 'Bizum'
                        : 'Tarxeta'}
                    </span>
                  </>
                )}
              </button>

              <div className="flex items-center justify-center gap-1.5 text-[11px] text-[#737973]">
                <ShieldCheck className="w-3.5 h-3.5 text-emerald-700" />
                <span>Pago cifrado SSL de 256 bits · Garantía de devolución de 14 días</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
