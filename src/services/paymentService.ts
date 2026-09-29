import {
  GuideAuthor,
  GuidePayoutRecord,
  GuidePayoutSettings,
  GuideSalesRecord,
  UserPurchaseRecord,
  RouteItem,
} from '../types';

export const PLATFORM_COMMISSION_PERCENT = 15; // 15% platform, 85% guide
export const GUIDE_REVENUE_PERCENT = 85;
export const DEFAULT_IRPF_TAX_RATE = 15; // 15% retención IRPF profesional en España

export const DEFAULT_GUIDE_PAYOUT_SETTINGS: Record<string, GuidePayoutSettings> = {
  'ana-rodriguez': {
    method: 'iban',
    accountHolder: 'Ana Rodríguez Carballo',
    iban: 'ES76 2080 0123 4567 8901 2345',
    bizumPhone: '+34 682 91 44 20',
    bankName: 'Abanca (Oficina Tui - Praza do Concello)',
    taxId: '35489102-K (Guía Oficial de Galicia GO-842)',
    isVerified: true,
  },
  'marcos-varela': {
    method: 'bizum',
    accountHolder: 'Marcos Varela Souto',
    iban: 'ES21 0049 1500 0512 3456 7890',
    bizumPhone: '+34 654 32 18 90',
    bankName: 'Banco Santander (Ourense)',
    taxId: '36128490-M (Colexiado Arqueoloxía Galicia)',
    isVerified: true,
  },
  'lucia-fernandez': {
    method: 'iban',
    accountHolder: 'Lucía Fernández Rial',
    iban: 'ES88 2100 0418 4502 0005 1234',
    bizumPhone: '+34 699 44 21 08',
    bankName: 'CaixaBank (Combarro - Poio)',
    taxId: '32987654-P (Asociación Guías Rías Baixas)',
    isVerified: true,
  },
};

export const INITIAL_GUIDE_PAYOUT_HISTORY: Record<string, GuidePayoutRecord[]> = {
  'ana-rodriguez': [
    {
      id: 'payout-ar-01',
      date: '02 Sep 2026',
      amount: 250.0,
      withholdingTax: 37.5,
      netAmount: 212.5,
      method: 'iban',
      destination: 'ES76 •••• •••• •••• 2345 (Abanca)',
      status: 'completada',
      invoiceRef: 'FAC-AR-2026-08',
    },
    {
      id: 'payout-ar-02',
      date: '15 Ago 2026',
      amount: 176.07,
      withholdingTax: 26.41,
      netAmount: 149.66,
      method: 'iban',
      destination: 'ES76 •••• •••• •••• 2345 (Abanca)',
      status: 'completada',
      invoiceRef: 'FAC-AR-2026-07',
    },
  ],
  'marcos-varela': [
    {
      id: 'payout-mv-01',
      date: '28 Ago 2026',
      amount: 150.0,
      withholdingTax: 22.5,
      netAmount: 127.5,
      method: 'bizum',
      destination: 'Bizum (+34 654 ••• 890)',
      status: 'completada',
      invoiceRef: 'FAC-MV-2026-04',
    },
  ],
  'lucia-fernandez': [
    {
      id: 'payout-lf-01',
      date: '20 Ago 2026',
      amount: 120.0,
      withholdingTax: 18.0,
      netAmount: 102.0,
      method: 'iban',
      destination: 'ES88 •••• •••• •••• 1234 (CaixaBank)',
      status: 'completada',
      invoiceRef: 'FAC-LF-2026-03',
    },
  ],
};

export const INITIAL_GUIDE_SALES_FEED: Record<string, GuideSalesRecord[]> = {
  'ana-rodriguez': [
    {
      id: 'sale-ar-01',
      date: 'Hoy a las 14:15',
      routeId: 'contrabando',
      routeTitle: 'Ruta del Contrabando',
      grossAmount: 2.99,
      netGuideAmount: 2.54,
      buyerName: 'Peregrino Camiño Portugués',
      paymentMethod: 'gpay',
    },
    {
      id: 'sale-ar-02',
      date: 'Ayer a las 18:30',
      routeId: 'contrabando',
      routeTitle: 'Ruta del Contrabando',
      grossAmount: 2.99,
      netGuideAmount: 2.54,
      buyerName: 'Viajero de Madrid',
      paymentMethod: 'bizum',
    },
    {
      id: 'sale-ar-03',
      date: '15 Sep 2026',
      routeId: 'contrabando',
      routeTitle: 'Ruta del Contrabando',
      grossAmount: 2.99,
      netGuideAmount: 2.54,
      buyerName: 'Familia Valença do Minho',
      paymentMethod: 'card',
    },
  ],
  'marcos-varela': [
    {
      id: 'sale-mv-01',
      date: 'Ayer a las 11:20',
      routeId: 'ribeira-sacra',
      routeTitle: 'Canóns do Sil e Ribeira Sacra',
      grossAmount: 1.99,
      netGuideAmount: 1.69,
      buyerName: 'Senderista Galicia',
      paymentMethod: 'gpay',
    },
  ],
  'lucia-fernandez': [
    {
      id: 'sale-lf-01',
      date: '16 Sep 2026',
      routeId: 'combarro',
      routeTitle: 'Combarro e a Ría Mítica',
      grossAmount: 1.99,
      netGuideAmount: 1.69,
      buyerName: 'Explorador Atlántico',
      paymentMethod: 'bizum',
    },
  ],
};

export const parsePriceNumber = (priceStr?: string): number => {
  if (!priceStr || priceStr.toLowerCase().includes('gratis')) return 0;
  const match = priceStr.replace(',', '.').match(/[\d.]+/);
  return match ? parseFloat(match[0]) : 0;
};

// Storage helper functions
export const getStoredGuidePayoutSettings = (guideId: string): GuidePayoutSettings => {
  try {
    const raw = localStorage.getItem(`ecos_payout_settings_${guideId}`);
    if (raw) return JSON.parse(raw);
  } catch {
    // fallback
  }
  return DEFAULT_GUIDE_PAYOUT_SETTINGS[guideId] || {
    method: 'iban',
    accountHolder: 'Guía Acreditado de Galicia',
    iban: 'ES00 0000 0000 0000 0000 0000',
    bizumPhone: '+34 600 00 00 00',
    bankName: 'Banco Nacional',
    taxId: '12345678-Z',
    isVerified: true,
  };
};

export const saveGuidePayoutSettings = (guideId: string, settings: GuidePayoutSettings): void => {
  try {
    localStorage.setItem(`ecos_payout_settings_${guideId}`, JSON.stringify(settings));
  } catch {
    // ignore
  }
};

export const getStoredGuidePayoutHistory = (guideId: string): GuidePayoutRecord[] => {
  try {
    const raw = localStorage.getItem(`ecos_payout_history_${guideId}`);
    if (raw) return JSON.parse(raw);
  } catch {
    // fallback
  }
  return INITIAL_GUIDE_PAYOUT_HISTORY[guideId] || [];
};

export const saveGuidePayoutHistory = (guideId: string, history: GuidePayoutRecord[]): void => {
  try {
    localStorage.setItem(`ecos_payout_history_${guideId}`, JSON.stringify(history));
  } catch {
    // ignore
  }
};

export const getStoredGuideSalesFeed = (guideId: string): GuideSalesRecord[] => {
  try {
    const raw = localStorage.getItem(`ecos_sales_feed_${guideId}`);
    if (raw) return JSON.parse(raw);
  } catch {
    // fallback
  }
  return INITIAL_GUIDE_SALES_FEED[guideId] || [];
};

export const saveGuideSalesFeed = (guideId: string, sales: GuideSalesRecord[]): void => {
  try {
    localStorage.setItem(`ecos_sales_feed_${guideId}`, JSON.stringify(sales));
  } catch {
    // ignore
  }
};

export const getStoredGuideBalance = (guideId: string, defaultBalance: number): number => {
  try {
    const raw = localStorage.getItem(`ecos_guide_balance_${guideId}`);
    if (raw !== null) return parseFloat(raw);
  } catch {
    // fallback
  }
  return defaultBalance;
};

export const saveGuideBalance = (guideId: string, balance: number): void => {
  try {
    localStorage.setItem(`ecos_guide_balance_${guideId}`, balance.toFixed(2));
  } catch {
    // ignore
  }
};
