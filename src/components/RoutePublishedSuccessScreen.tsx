import React from 'react';
import { RouteItem } from '../types';
import { CheckCircle2, Sparkles, MapPin, Clock, Eye, ArrowRight, PlusCircle, Compass, LayoutDashboard } from 'lucide-react';

interface RoutePublishedSuccessScreenProps {
  route: RouteItem;
  onViewInCatalog: () => void;
  onCreateAnother: () => void;
  onGoToDashboard: () => void;
}

export const RoutePublishedSuccessScreen: React.FC<RoutePublishedSuccessScreenProps> = ({
  route,
  onViewInCatalog,
  onCreateAnother,
  onGoToDashboard,
}) => {
  return (
    <div className="min-h-screen bg-[#fcf9f2] text-[#1b3022] flex flex-col justify-between selection:bg-[#c48b47]/20">
      {/* Empty Top Bar for balance */}
      <div className="h-16 border-b border-[#1b3022]/10 bg-[#fcf9f2]/80 backdrop-blur-md px-6 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Compass className="w-5 h-5 text-[#c48b47]" />
          <span className="font-serif font-bold text-sm tracking-wide text-[#0f2416]">
            Ecos do Lugar · Espazo de Guías
          </span>
        </div>

        <button
          id="success-dash-quick-btn"
          onClick={onGoToDashboard}
          className="text-xs font-serif font-semibold text-[#1b3022]/70 hover:text-[#1b3022] inline-flex items-center gap-1.5"
        >
          <LayoutDashboard className="w-3.5 h-3.5" />
          <span>Mi panel</span>
        </button>
      </div>

      {/* Main Card */}
      <main className="max-w-xl mx-auto px-4 py-12 text-center w-full">
        <div className="w-16 h-16 rounded-full bg-emerald-100 border border-emerald-300 text-emerald-800 flex items-center justify-center mx-auto mb-6 shadow-sm">
          <CheckCircle2 className="w-9 h-9" />
        </div>

        <div className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-widest text-[#8c5717] px-3 py-1 rounded-full bg-[#c48b47]/15 border border-[#c48b47]/20 mb-3">
          <Sparkles className="w-3.5 h-3.5" />
          Itinerario Verificado
        </div>

        <h1 className="text-3xl sm:text-4xl font-serif font-bold text-[#0f2416] mb-3">
          Tu ruta ha sido publicada
        </h1>

        <p className="text-sm sm:text-base text-[#47654e] font-serif max-w-md mx-auto mb-8 leading-relaxed">
          Ya está disponible para que todos los visitantes de Ecos do Lugar descubran su historia, paradas y misterios.
        </p>

        {/* Route Preview Card */}
        <div className="bg-white/80 border border-[#1b3022]/10 rounded-2xl p-5 text-left mb-8 shadow-xs">
          <div className="flex items-start justify-between gap-3 mb-2">
            <div>
              <span className="text-[11px] font-semibold uppercase tracking-wider text-[#8c5717]">
                {route.categories?.[0] || 'Patrimonio'}
              </span>
              <h2 className="text-lg font-serif font-bold text-[#0f2416]">
                {route.title}
              </h2>
            </div>
            <span className="px-2.5 py-1 rounded-lg bg-[#1b3022]/5 text-[#1b3022] font-serif font-bold text-xs shrink-0 border border-[#1b3022]/10">
              {route.price || 'Gratis'}
            </span>
          </div>

          <div className="flex items-center gap-3 text-xs text-[#47654e] font-serif mb-3">
            <span className="inline-flex items-center gap-1">
              <MapPin className="w-3.5 h-3.5 text-[#c48b47]" />
              {route.location}
            </span>
            <span>·</span>
            <span className="inline-flex items-center gap-1">
              <Clock className="w-3.5 h-3.5 text-[#47654e]" />
              {route.duration}
            </span>
            <span>·</span>
            <span>{route.stopsCount || route.stops?.length || 0} paradas</span>
          </div>

          <p className="text-xs text-[#334b39] line-clamp-2 font-serif border-t border-[#1b3022]/5 pt-2.5">
            {route.description}
          </p>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row items-center justify-center gap-3 mb-6">
          <button
            id="success-view-catalog-btn"
            onClick={onViewInCatalog}
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl bg-[#1b3022] hover:bg-[#284632] text-[#fcf9f2] font-serif font-semibold text-sm shadow-sm transition-all"
          >
            <span>Ver en catálogo</span>
            <ArrowRight className="w-4 h-4 text-[#c48b47]" />
          </button>

          <button
            id="success-create-another-btn"
            onClick={onCreateAnother}
            className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-5 py-3.5 rounded-xl bg-white border border-[#1b3022]/20 hover:border-[#1b3022]/40 text-[#1b3022] font-serif text-sm transition-all"
          >
            <PlusCircle className="w-4 h-4 text-[#c48b47]" />
            <span>Crear otra ruta</span>
          </button>
        </div>

        <button
          id="success-goto-dash-btn"
          onClick={onGoToDashboard}
          className="text-xs font-serif text-[#47654e] hover:text-[#0f2416] underline inline-flex items-center gap-1"
        >
          <LayoutDashboard className="w-3.5 h-3.5" />
          <span>Ir a mi panel de control de guía</span>
        </button>
      </main>

      {/* Footer */}
      <footer className="border-t border-[#1b3022]/10 py-5 text-center text-xs text-[#1b3022]/60 font-serif">
        Ecos do Lugar · Experiencias de autor e memoria viva
      </footer>
    </div>
  );
};
