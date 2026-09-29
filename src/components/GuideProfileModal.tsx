import React from 'react';
import { GuideAuthor, RouteItem } from '../types';
import { X, CheckCircle2, Star, MapPin, Compass, Award, ExternalLink, Calendar } from 'lucide-react';

interface GuideProfileModalProps {
  guide: GuideAuthor;
  allRoutes: RouteItem[];
  onSelectRoute: (routeId: string) => void;
  onClose: () => void;
}

export const GuideProfileModal: React.FC<GuideProfileModalProps> = ({
  guide,
  allRoutes,
  onSelectRoute,
  onClose,
}) => {
  // Routes created by this guide
  const guideRoutes = allRoutes.filter((r) => r.guide?.id === guide.id);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[#061b0e]/70 backdrop-blur-sm animate-fade-in">
      <div className="bg-[#fcf9f2] text-[#1b3022] w-full max-w-lg rounded-3xl border border-[#1b3022]/15 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Modal Header */}
        <div className="px-6 py-5 border-b border-[#1b3022]/10 flex items-center justify-between bg-white/60">
          <div className="flex items-center gap-2">
            <Award className="w-4 h-4 text-[#c48b47]" />
            <span className="text-xs font-serif uppercase tracking-wider font-semibold text-[#8c5717]">
              Perfil Oficial del Guía
            </span>
          </div>

          <button
            id="close-guide-profile-btn"
            onClick={onClose}
            className="p-1.5 rounded-full hover:bg-[#1b3022]/10 text-[#1b3022]/70 hover:text-[#1b3022] transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-6 overflow-y-auto space-y-6">
          {/* Avatar and Basic Info */}
          <div className="flex items-start gap-4">
            {guide.avatarUrl ? (
              <img
                src={guide.avatarUrl}
                alt={guide.name}
                className="w-20 h-20 rounded-2xl object-cover border-2 border-[#c48b47]/40 shadow-sm shrink-0"
              />
            ) : (
              <div className="w-20 h-20 rounded-2xl bg-[#1b3022] text-[#fcf9f2] flex items-center justify-center font-serif text-2xl font-bold shrink-0">
                {guide.name.charAt(0)}
              </div>
            )}

            <div>
              <div className="flex items-center gap-1.5 mb-1">
                <h2 className="text-xl font-serif font-bold text-[#0f2416]">
                  {guide.name}
                </h2>
                {guide.verified && (
                  <CheckCircle2 className="w-4 h-4 text-[#c48b47] shrink-0" title="Guía Oficial Verificado" />
                )}
              </div>

              <p className="text-xs font-serif font-semibold text-[#8c5717] mb-1.5">
                {guide.title}
              </p>

              <div className="flex items-center gap-3 text-xs text-[#47654e] font-serif">
                <span className="inline-flex items-center gap-1">
                  <MapPin className="w-3 h-3 text-[#c48b47]" />
                  {guide.location || 'Galicia'}
                </span>
                <span>·</span>
                <span className="inline-flex items-center gap-1 text-[#0f2416] font-bold">
                  <Star className="w-3 h-3 fill-[#c48b47] text-[#c48b47]" />
                  {guide.rating} ({guide.reviewsCount} reseñas)
                </span>
              </div>
            </div>
          </div>

          {/* Bio */}
          <div className="bg-white/80 border border-[#1b3022]/10 rounded-2xl p-4">
            <h3 className="text-xs font-serif font-semibold uppercase tracking-wider text-[#47654e] mb-1.5">
              Sobre el guía
            </h3>
            <p className="text-sm font-serif text-[#1b3022] leading-relaxed">
              {guide.bio}
            </p>
          </div>

          {/* Published Routes by this guide */}
          <div>
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-xs font-serif font-semibold uppercase tracking-wider text-[#47654e]">
                Rutas creadas por {guide.name.split(' ')[0]} ({guideRoutes.length})
              </h3>
            </div>

            <div className="space-y-2.5">
              {guideRoutes.map((route) => (
                <div
                  key={route.id}
                  className="bg-white border border-[#1b3022]/10 rounded-xl p-3 flex items-center justify-between gap-3 hover:border-[#c48b47]/40 transition-all"
                >
                  <div>
                    <h4 className="text-sm font-serif font-bold text-[#0f2416]">
                      {route.title}
                    </h4>
                    <p className="text-xs text-[#47654e] font-serif">
                      {route.location} · {route.duration} · {route.stopsCount || route.stops?.length || 0} paradas
                    </p>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <span className="text-xs font-serif font-bold px-2 py-0.5 rounded bg-[#1b3022]/5 text-[#1b3022]">
                      {route.price || (route.isFree ? 'Gratis' : '1,99 €')}
                    </span>
                    <button
                      onClick={() => {
                        onSelectRoute(route.id);
                        onClose();
                      }}
                      className="p-1.5 rounded-lg text-[#1b3022]/60 hover:text-[#0f2416] hover:bg-[#1b3022]/5 transition-colors"
                      title="Ver ruta"
                    >
                      <ExternalLink className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="px-6 py-4 border-t border-[#1b3022]/10 bg-white/40 flex justify-end">
          <button
            id="guide-modal-close-action-btn"
            onClick={onClose}
            className="px-5 py-2 rounded-xl bg-[#1b3022] text-[#fcf9f2] text-xs font-serif font-semibold shadow-xs hover:bg-[#274430] transition-colors"
          >
            Entendido
          </button>
        </div>
      </div>
    </div>
  );
};
