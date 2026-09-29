import React, { useState } from 'react';
import { GuideAuthor, GuidePayoutRecord, GuidePayoutSettings } from '../types';
import {
  DEFAULT_IRPF_TAX_RATE,
  saveGuideBalance,
  saveGuidePayoutHistory,
  saveGuidePayoutSettings,
} from '../services/paymentService';
import {
  X,
  Euro,
  Building2,
  Smartphone,
  CheckCircle2,
  AlertCircle,
  FileText,
  ArrowRight,
  ShieldCheck,
  CreditCard,
  Receipt,
  Download,
  Calendar,
  Sparkles,
  Edit2,
  Save,
} from 'lucide-react';

interface GuidePayoutModalProps {
  guide: GuideAuthor;
  availableBalance: number;
  payoutSettings: GuidePayoutSettings;
  isOpen: boolean;
  onClose: () => void;
  onPayoutSuccess: (record: GuidePayoutRecord, updatedBalance: number) => void;
  onUpdateSettings: (updated: GuidePayoutSettings) => void;
}

export const GuidePayoutModal: React.FC<GuidePayoutModalProps> = ({
  guide,
  availableBalance,
  payoutSettings,
  isOpen,
  onClose,
  onPayoutSuccess,
  onUpdateSettings,
}) => {
  const [amountMode, setAmountMode] = useState<'all' | 'custom'>('all');
  const [customAmount, setCustomAmount] = useState<string>(Math.min(100, Math.floor(availableBalance)).toString());
  const [selectedMethod, setSelectedMethod] = useState<'iban' | 'bizum'>(payoutSettings.method || 'iban');
  const [isEditingSettings, setIsEditingSettings] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [completedRecord, setCompletedRecord] = useState<GuidePayoutRecord | null>(null);

  // Settings form state
  const [holder, setHolder] = useState(payoutSettings.accountHolder);
  const [iban, setIban] = useState(payoutSettings.iban);
  const [bizumPhone, setBizumPhone] = useState(payoutSettings.bizumPhone);
  const [bankName, setBankName] = useState(payoutSettings.bankName);
  const [taxId, setTaxId] = useState(payoutSettings.taxId);

  if (!isOpen) return null;

  const requestedGross = amountMode === 'all' ? availableBalance : parseFloat(customAmount) || 0;
  const withholdingTax = parseFloat((requestedGross * (DEFAULT_IRPF_TAX_RATE / 100)).toFixed(2));
  const netPayout = parseFloat((requestedGross - withholdingTax).toFixed(2));

  const handleSaveSettings = (e: React.FormEvent) => {
    e.preventDefault();
    const updated: GuidePayoutSettings = {
      method: selectedMethod,
      accountHolder: holder,
      iban,
      bizumPhone,
      bankName,
      taxId,
      isVerified: true,
    };
    saveGuidePayoutSettings(guide.id, updated);
    onUpdateSettings(updated);
    setIsEditingSettings(false);
  };

  const handleConfirmPayout = () => {
    if (requestedGross <= 0 || requestedGross > availableBalance) return;

    setIsSubmitting(true);
    setTimeout(() => {
      const invRef = `FAC-${guide.id.toUpperCase().slice(0, 3)}-${Date.now().toString().slice(-4)}`;
      const destinationMasked =
        selectedMethod === 'iban'
          ? `${iban.slice(0, 4)} •••• •••• •••• ${iban.slice(-4)} (${bankName.split(' ')[0]})`
          : `Bizum (${bizumPhone})`;

      const record: GuidePayoutRecord = {
        id: `payout-${Date.now()}`,
        date: new Date().toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' }),
        amount: requestedGross,
        withholdingTax,
        netAmount: netPayout,
        method: selectedMethod,
        destination: destinationMasked,
        status: 'completada',
        invoiceRef: invRef,
      };

      const newBalance = Math.max(0, availableBalance - requestedGross);
      saveGuideBalance(guide.id, newBalance);

      setIsSubmitting(false);
      setCompletedRecord(record);
      onPayoutSuccess(record, newBalance);
    }, 1200);
  };

  const handleCloseAll = () => {
    setCompletedRecord(null);
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
        {/* Top Header */}
        <div className="sticky top-0 bg-[#fcf9f2]/95 backdrop-blur-md z-10 px-5 py-3.5 border-b border-[#1b3022]/10 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-[#1b3022] text-[#fcf9f2] flex items-center justify-center font-serif text-xs font-bold shrink-0">
              <Euro className="w-4 h-4 text-[#c48b47]" />
            </div>
            <div>
              <h3 className="font-serif text-[17px] font-bold text-[#061b0e] leading-tight">
                {completedRecord ? 'Liquidación Emitida' : 'Cobro de Honorarios de Guía'}
              </h3>
              <p className="text-[11px] text-[#47654e] font-sans">
                {guide.name} · {guide.title}
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

        {/* Content: SUCCESS STATE */}
        {completedRecord ? (
          <div className="p-6 flex flex-col gap-5 text-center">
            <div className="w-16 h-16 rounded-2xl bg-emerald-100 border border-emerald-300 text-emerald-800 flex items-center justify-center mx-auto shadow-sm animate-bounce duration-1000">
              <CheckCircle2 className="w-9 h-9" />
            </div>

            <div>
              <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-emerald-50 text-emerald-900 border border-emerald-300 text-xs font-semibold uppercase tracking-wider mb-1.5">
                <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
                Transferencia Procesada
              </span>
              <h2 className="font-serif text-2xl font-bold text-[#061b0e]">
                Liquidación por {completedRecord.netAmount.toFixed(2).replace('.', ',')} €
              </h2>
              <p className="text-xs text-[#434843] max-w-sm mx-auto mt-1 font-serif">
                La orden de pago SEPA / Bizum se emitió correctamente hacia tu cuenta bancaria.
              </p>
            </div>

            {/* Official Settlement Receipt */}
            <div className="p-4 rounded-2xl bg-white border border-[#1b3022]/10 shadow-xs text-left text-xs font-serif space-y-2.5">
              <div className="flex items-center justify-between pb-2 border-b border-[#1b3022]/10 font-sans">
                <span className="text-[#47654e] font-medium text-[11px]">Número de factura oficial:</span>
                <span className="font-mono font-bold text-[#061b0e]">{completedRecord.invoiceRef}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">Destino de los fondos:</span>
                <span className="font-bold text-[#061b0e]">{completedRecord.destination}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">Titular acreditado:</span>
                <span className="font-bold text-[#061b0e]">{payoutSettings.accountHolder}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-[#47654e]">NIF / CIF fiscal:</span>
                <span className="font-mono text-[#061b0e]">{payoutSettings.taxId}</span>
              </div>

              <div className="pt-2 border-t border-[#1b3022]/10 space-y-1 font-sans">
                <div className="flex items-center justify-between text-[#47654e]">
                  <span>Importe bruto liquidado:</span>
                  <span className="font-mono">{completedRecord.amount.toFixed(2).replace('.', ',')} €</span>
                </div>
                <div className="flex items-center justify-between text-[#8c5717]">
                  <span>Retención IRPF (15%):</span>
                  <span className="font-mono">- {completedRecord.withholdingTax.toFixed(2).replace('.', ',')} €</span>
                </div>
                <div className="flex items-center justify-between font-bold text-sm text-[#061b0e] pt-1">
                  <span>Neto transferido a tu cuenta:</span>
                  <span className="text-emerald-800 font-mono text-base">
                    {completedRecord.netAmount.toFixed(2).replace('.', ',')} €
                  </span>
                </div>
              </div>
            </div>

            <div className="p-3.5 rounded-xl bg-[#f1eee7] border border-[#e5e2db] text-left text-xs flex items-center gap-3">
              <FileText className="w-5 h-5 text-[#47654e] shrink-0" />
              <p className="text-[11px] text-[#434843]">
                Puedes consultar este movimiento y descargar tu extracto contable desde tu panel de liquidaciones.
              </p>
            </div>

            <button
              onClick={handleCloseAll}
              className="w-full py-3.5 px-4 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] text-[#fcf9f2] font-semibold text-sm shadow-md transition-all cursor-pointer"
            >
              Volver al panel de control de guía
            </button>
          </div>
        ) : (
          /* Content: PAYOUT WITHDRAWAL FORM */
          <div className="p-5 flex flex-col gap-4">
            {/* Balance Card */}
            <div className="p-4 rounded-2xl bg-gradient-to-br from-[#1b3022] to-[#0a1a10] text-[#fcf9f2] border border-[#2b5239] shadow-sm flex items-center justify-between">
              <div>
                <span className="text-[11px] uppercase font-serif tracking-wider text-[#c48b47] block mb-1">
                  Saldo disponible para cobro
                </span>
                <span className="font-serif text-3xl font-bold text-white tracking-tight">
                  {availableBalance.toFixed(2).replace('.', ',')} €
                </span>
                <p className="text-[11px] text-white/70 font-serif mt-0.5">
                  Ingresos acumulados de tus rutas culturales (85% neto)
                </p>
              </div>
              <div className="w-12 h-12 rounded-2xl bg-white/10 flex items-center justify-center text-2xl border border-white/20 shrink-0">
                💰
              </div>
            </div>

            {/* If balance is 0 */}
            {availableBalance <= 0 ? (
              <div className="p-6 text-center bg-white rounded-2xl border border-[#1b3022]/10 space-y-2">
                <AlertCircle className="w-8 h-8 text-[#8c5717] mx-auto" />
                <h4 className="font-serif text-base font-bold text-[#061b0e]">No tienes saldo disponible por ahora</h4>
                <p className="text-xs text-[#434843] font-serif">
                  Los ingresos aumentarán a medida que los viajeros adquieran tus rutas en el catálogo de Ecos do Lugar.
                </p>
                <button
                  onClick={handleCloseAll}
                  className="mt-2 px-4 py-2 rounded-xl bg-[#1b3022] text-white text-xs font-semibold"
                >
                  Entendido
                </button>
              </div>
            ) : (
              <>
                {/* Amount Selector */}
                <div className="space-y-2">
                  <label className="text-xs font-serif uppercase tracking-wider font-semibold text-[#47654e]">
                    Cantidade a retirar
                  </label>

                  <div className="grid grid-cols-2 gap-2">
                    <button
                      type="button"
                      onClick={() => setAmountMode('all')}
                      className={`p-3 rounded-xl border text-left transition-all cursor-pointer ${
                        amountMode === 'all'
                          ? 'bg-white border-[#1b3022] shadow-sm ring-2 ring-[#1b3022]/20 text-[#061b0e]'
                          : 'bg-white/70 border-[#1b3022]/15 text-[#434843] hover:bg-white'
                      }`}
                    >
                      <span className="text-[11px] font-semibold text-[#47654e] block">Retirar todo o saldo</span>
                      <span className="font-serif text-base font-bold text-[#061b0e]">
                        {availableBalance.toFixed(2).replace('.', ',')} €
                      </span>
                    </button>

                    <button
                      type="button"
                      onClick={() => setAmountMode('custom')}
                      className={`p-3 rounded-xl border text-left transition-all cursor-pointer ${
                        amountMode === 'custom'
                          ? 'bg-white border-[#1b3022] shadow-sm ring-2 ring-[#1b3022]/20 text-[#061b0e]'
                          : 'bg-white/70 border-[#1b3022]/15 text-[#434843] hover:bg-white'
                      }`}
                    >
                      <span className="text-[11px] font-semibold text-[#47654e] block">Cantidade parcial</span>
                      <div className="flex items-center gap-1 font-serif text-base font-bold text-[#061b0e]">
                        <span>Personalizar €</span>
                      </div>
                    </button>
                  </div>

                  {amountMode === 'custom' && (
                    <div className="p-3 bg-white rounded-xl border border-[#1b3022]/15 flex items-center gap-2">
                      <span className="text-sm font-bold text-[#061b0e]">€</span>
                      <input
                        type="number"
                        min="1"
                        max={availableBalance}
                        step="0.01"
                        value={customAmount}
                        onChange={e => setCustomAmount(e.target.value)}
                        className="w-full text-base font-mono font-bold text-[#061b0e] bg-transparent focus:outline-none"
                      />
                      <span className="text-xs text-[#737973] shrink-0 font-serif">Máx: {availableBalance.toFixed(2)} €</span>
                    </div>
                  )}
                </div>

                {/* Payout Destination Card & Method Selector */}
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <label className="text-xs font-serif uppercase tracking-wider font-semibold text-[#47654e]">
                      Conta de destino dos fondos
                    </label>
                    <button
                      type="button"
                      onClick={() => setIsEditingSettings(!isEditingSettings)}
                      className="text-xs text-[#8c5717] hover:text-[#061b0e] font-serif font-semibold flex items-center gap-1"
                    >
                      <Edit2 className="w-3 h-3" />
                      <span>{isEditingSettings ? 'Cancelar edición' : 'Modificar datos bancarios'}</span>
                    </button>
                  </div>

                  {/* Settings editing form */}
                  {isEditingSettings ? (
                    <form onSubmit={handleSaveSettings} className="p-4 bg-white rounded-2xl border border-[#1b3022]/20 shadow-xs space-y-3">
                      <h4 className="font-serif text-sm font-bold text-[#061b0e]">
                        Datos Bancarios e Fiscais de Cobro
                      </h4>

                      <div>
                        <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                          Titular da conta ou autónomo
                        </label>
                        <input
                          type="text"
                          value={holder}
                          onChange={e => setHolder(e.target.value)}
                          className="w-full text-xs bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] font-medium"
                          required
                        />
                      </div>

                      <div>
                        <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                          NIF / CIF Oficial do Guía
                        </label>
                        <input
                          type="text"
                          value={taxId}
                          onChange={e => setTaxId(e.target.value)}
                          className="w-full text-xs bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] font-mono font-medium"
                          required
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-2">
                        <div>
                          <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                            Entidade Bancaria
                          </label>
                          <input
                            type="text"
                            value={bankName}
                            onChange={e => setBankName(e.target.value)}
                            className="w-full text-xs bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1]"
                            required
                          />
                        </div>
                        <div>
                          <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                            Teléfono para Bizum
                          </label>
                          <input
                            type="tel"
                            value={bizumPhone}
                            onChange={e => setBizumPhone(e.target.value)}
                            className="w-full text-xs bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] font-mono"
                          />
                        </div>
                      </div>

                      <div>
                        <label className="text-[11px] font-semibold text-[#47654e] block mb-1">
                          IBAN para Transferencia SEPA
                        </label>
                        <input
                          type="text"
                          value={iban}
                          onChange={e => setIban(e.target.value)}
                          className="w-full text-xs bg-[#fcf9f2] px-3 py-2 rounded-lg border border-[#c3c8c1] font-mono font-bold"
                          required
                        />
                      </div>

                      <button
                        type="submit"
                        className="w-full py-2 px-3 rounded-lg bg-[#1b3022] hover:bg-[#061b0e] text-white text-xs font-semibold flex items-center justify-center gap-1.5 cursor-pointer"
                      >
                        <Save className="w-3.5 h-3.5 text-[#ffddba]" />
                        <span>Gardar datos bancarios</span>
                      </button>
                    </form>
                  ) : (
                    /* Active destination display with radio choice */
                    <div className="space-y-2">
                      {/* Option 1: IBAN */}
                      <div
                        onClick={() => setSelectedMethod('iban')}
                        className={`p-3.5 rounded-xl border transition-all cursor-pointer flex items-center justify-between gap-3 ${
                          selectedMethod === 'iban'
                            ? 'bg-white border-[#1b3022] shadow-xs ring-2 ring-[#1b3022]/20'
                            : 'bg-white/70 border-[#1b3022]/10 hover:bg-white'
                        }`}
                      >
                        <div className="flex items-center gap-3 min-w-0">
                          <div className="w-9 h-9 rounded-lg bg-[#1b3022]/10 flex items-center justify-center text-[#1b3022] shrink-0">
                            <Building2 className="w-4 h-4" />
                          </div>
                          <div className="min-w-0 text-xs">
                            <div className="flex items-center gap-1.5">
                              <span className="font-semibold text-[#061b0e]">Transferencia SEPA</span>
                              <span className="text-[10px] px-1.5 py-0.2 bg-emerald-100 text-emerald-800 rounded font-medium">
                                Verificada
                              </span>
                            </div>
                            <p className="font-mono text-[11px] text-[#47654e] truncate mt-0.5">
                              {payoutSettings.iban}
                            </p>
                            <p className="text-[10px] text-[#737973] truncate">
                              {payoutSettings.bankName} · {payoutSettings.accountHolder}
                            </p>
                          </div>
                        </div>
                        <input
                          type="radio"
                          name="payout_method"
                          checked={selectedMethod === 'iban'}
                          onChange={() => setSelectedMethod('iban')}
                          className="w-4 h-4 text-[#1b3022] focus:ring-0 cursor-pointer"
                        />
                      </div>

                      {/* Option 2: Bizum */}
                      <div
                        onClick={() => setSelectedMethod('bizum')}
                        className={`p-3.5 rounded-xl border transition-all cursor-pointer flex items-center justify-between gap-3 ${
                          selectedMethod === 'bizum'
                            ? 'bg-white border-[#009999] shadow-xs ring-2 ring-[#009999]/20'
                            : 'bg-white/70 border-[#1b3022]/10 hover:bg-white'
                        }`}
                      >
                        <div className="flex items-center gap-3 min-w-0">
                          <div className="w-9 h-9 rounded-lg bg-[#009999]/10 flex items-center justify-center text-[#009999] shrink-0">
                            <Smartphone className="w-4 h-4" />
                          </div>
                          <div className="min-w-0 text-xs">
                            <div className="flex items-center gap-1.5">
                              <span className="font-semibold text-[#061b0e]">Bizum Profesional</span>
                              <span className="text-[10px] px-1.5 py-0.2 bg-[#009999]/10 text-[#009999] rounded font-medium">
                                Instantáneo
                              </span>
                            </div>
                            <p className="font-mono text-[11px] text-[#47654e] mt-0.5">
                              {payoutSettings.bizumPhone}
                            </p>
                          </div>
                        </div>
                        <input
                          type="radio"
                          name="payout_method"
                          checked={selectedMethod === 'bizum'}
                          onChange={() => setSelectedMethod('bizum')}
                          className="w-4 h-4 text-[#009999] focus:ring-0 cursor-pointer"
                        />
                      </div>
                    </div>
                  )}
                </div>

                {/* Fiscal Breakdown Strip */}
                <div className="p-3.5 rounded-xl bg-white border border-[#1b3022]/10 shadow-xs space-y-1.5 text-xs font-serif">
                  <div className="flex items-center justify-between text-[#47654e]">
                    <span>Importe bruto a liquidar:</span>
                    <span className="font-mono font-bold text-[#061b0e]">
                      {requestedGross.toFixed(2).replace('.', ',')} €
                    </span>
                  </div>
                  <div className="flex items-center justify-between text-[#8c5717]">
                    <span>Retención IRPF actividades profesionais (15%):</span>
                    <span className="font-mono font-semibold">
                      - {withholdingTax.toFixed(2).replace('.', ',')} €
                    </span>
                  </div>
                  <div className="flex items-center justify-between pt-1.5 border-t border-[#1b3022]/10 font-bold text-[#061b0e]">
                    <span className="text-sm">Total neto a recibir en conta:</span>
                    <span className="text-emerald-800 font-mono text-base">
                      {netPayout.toFixed(2).replace('.', ',')} €
                    </span>
                  </div>
                </div>

                {/* Confirmation Action Button */}
                <div className="pt-2 border-t border-[#1b3022]/10 space-y-2">
                  <button
                    id="btn-confirm-payout-transfer"
                    type="button"
                    onClick={handleConfirmPayout}
                    disabled={isSubmitting || requestedGross <= 0 || requestedGross > availableBalance}
                    className="w-full py-3.5 px-4 rounded-xl bg-[#1b3022] hover:bg-[#061b0e] active:scale-[0.99] text-[#fcf9f2] font-semibold text-sm flex items-center justify-center gap-2 shadow-lg transition-all cursor-pointer disabled:opacity-75"
                  >
                    {isSubmitting ? (
                      <div className="flex items-center gap-2">
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        <span>Xerando transferencia bancaria...</span>
                      </div>
                    ) : (
                      <>
                        <Building2 className="w-4 h-4 text-[#ffddba]" />
                        <span>
                          Transferir {netPayout.toFixed(2).replace('.', ',')} € á miña conta
                        </span>
                      </>
                    )}
                  </button>

                  <div className="flex items-center justify-center gap-1.5 text-[11px] text-[#737973]">
                    <ShieldCheck className="w-3.5 h-3.5 text-emerald-700" />
                    <span>Transacción bancaria SEPA procesada con certificación fiscal galega</span>
                  </div>
                </div>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
