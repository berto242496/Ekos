import React from 'react';
import { Compass, Sparkles, MapPin, Coins, Users, ArrowRight, BookOpen, ShieldCheck, ChevronLeft } from 'lucide-react';

interface GuideLandingScreenProps {
  onStartCreateRoute: () => void;
  onGoToDashboard: () => void;
  onBackToVisitor: () => void;
}

export const GuideLandingScreen: React.FC<GuideLandingScreenProps> = ({
  onStartCreateRoute,
  onGoToDashboard,
  onBackToVisitor,
}) => {
  const benefits = [
    {
      icon: Compass,
      title: 'Crea rutas propias',
      desc: 'Diseña itinerarios a tu ritmo, destacando los rincones, historias y leyendas que solo los locales conocen.',
    },
    {
      icon: BookOpen,
      title: 'Publica experiencias culturales',
      desc: 'Enriquece cada parada con relatos orales, misterios históricos, pistas secretas y retos participativos.',
    },
    {
      icon: Users,
      title: 'Llega a más visitantes',
      desc: 'Conecta con viajeros y amantes del patrimonio que buscan descubrir el territorio de forma inmersiva y respetuosa.',
    },
    {
      icon: Coins,
      title: 'Monetiza tu conocimiento',
      desc: 'Fija el precio de tus rutas con micropagos accesibles (0,99 € – 4,99 €) u ofrécelas gratis con total libertad.',
    },
  ];

  return (
    <div className="min-h-screen bg-[#fcf9f2] text-[#1b3022] selection:bg-[#c48b47]/20 flex flex-col justify-between">
      {/* Top Header */}
      <header className="border-b border-[#1b3022]/10 bg-[#fcf9f2]/80 backdrop-blur-md sticky top-0 z-20">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <button
            id="guide-landing-back-visitor-btn"
            onClick={onBackToVisitor}
            className="inline-flex items-center gap-2 text-sm font-serif text-[#1b3022]/80 hover:text-[#1b3022] px-3 py-1.5 rounded-lg border border-[#1b3022]/15 hover:border-[#1b3022]/30 transition-colors"
          >
            <ChevronLeft className="w-4 h-4" />
            <span>Volver como visitante</span>
          </button>

          <div className="flex items-center gap-2">
            <span className="inline-flex items-center gap-1.5 text-xs tracking-wider uppercase font-semibold px-2.5 py-1 rounded-full bg-[#1b3022]/5 text-[#1b3022] border border-[#1b3022]/10">
              <ShieldCheck className="w-3.5 h-3.5 text-[#c48b47]" />
              Comunidad de Guías Locales
            </span>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="max-w-4xl mx-auto px-4 sm:px-6 py-12 sm:py-16 w-full">
        {/* Badge & Title */}
        <div className="text-center max-w-2xl mx-auto mb-12">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-[#c48b47]/15 border border-[#c48b47]/30 text-[#8c5717] text-xs font-semibold uppercase tracking-widest mb-6">
            <Sparkles className="w-3.5 h-3.5" />
            Espacio para Guías y Divulgadores
          </div>

          <h1 className="text-3xl sm:text-5xl font-serif font-medium tracking-tight text-[#0f2416] leading-tight mb-5">
            Crea y monetiza tus propias rutas
          </h1>

          <p className="text-lg sm:text-xl text-[#334b39] font-serif leading-relaxed">
            Convierte tu conocimiento local en experiencias digitales para visitantes.
          </p>
        </div>

        {/* Benefits Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5 mb-12">
          {benefits.map((benefit, idx) => {
            const Icon = benefit.icon;
            return (
              <div
                key={idx}
                className="bg-white/70 border border-[#1b3022]/10 rounded-2xl p-6 hover:border-[#c48b47]/40 hover:shadow-sm transition-all flex flex-col justify-between"
              >
                <div className="flex items-start gap-4 mb-3">
                  <div className="p-2.5 rounded-xl bg-[#1b3022]/5 text-[#1b3022] border border-[#1b3022]/10">
                    <Icon className="w-5 h-5 text-[#c48b47]" />
                  </div>
                  <div>
                    <h2 className="text-base font-serif font-bold text-[#0f2416] mb-1">
                      {benefit.title}
                    </h2>
                    <p className="text-sm text-[#47654e] leading-relaxed">
                      {benefit.desc}
                    </p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Call to Action Box */}
        <div className="bg-[#1b3022] text-[#fcf9f2] rounded-3xl p-8 sm:p-10 shadow-lg text-center relative overflow-hidden border border-[#c48b47]/20">
          <div className="absolute -right-12 -bottom-12 w-64 h-64 rounded-full bg-[#c48b47]/10 blur-2xl pointer-events-none" />
          <div className="relative z-10 max-w-xl mx-auto">
            <h2 className="text-2xl sm:text-3xl font-serif font-medium mb-3">
              ¿Listo para publicar tu primer itinerario?
            </h2>
            <p className="text-sm sm:text-base text-[#fcf9f2]/80 mb-8 font-serif leading-relaxed">
              Configura tus paradas, enigmas y precio en pocos minutos. Podrás guardar un borrador o publicarla directamente en el catálogo.
            </p>

            <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
              <button
                id="guide-landing-create-first-route-btn"
                onClick={onStartCreateRoute}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-8 py-3.5 rounded-xl bg-[#c48b47] hover:bg-[#d59850] text-[#0f2416] font-semibold text-base shadow-md transition-all active:scale-[0.98]"
              >
                <span>Crear mi primera ruta</span>
                <ArrowRight className="w-4 h-4" />
              </button>

              <button
                id="guide-landing-view-dashboard-btn"
                onClick={onGoToDashboard}
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl bg-white/10 hover:bg-white/15 text-[#fcf9f2] border border-white/20 font-serif text-sm transition-all"
              >
                <span>Ir a mi panel de guía</span>
              </button>
            </div>
          </div>
        </div>

        {/* Social Proof Quote */}
        <div className="mt-12 text-center border-t border-[#1b3022]/10 pt-8 max-w-xl mx-auto">
          <p className="text-sm italic font-serif text-[#47654e] mb-2">
            «Ecos do Lugar permítenos aos historiadores e guías da Raia compartir o patrimonio vivo con visitantes de todo o mundo sen intermediarios.»
          </p>
          <span className="text-xs font-semibold text-[#1b3022]/70 uppercase tracking-wider">
            Ana Rodríguez · Guía oficial en Tui
          </span>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-[#1b3022]/10 py-6 text-center text-xs text-[#1b3022]/60 font-serif">
        Ecos do Lugar · Espazo de guías turísticos e patrimonio cultural
      </footer>
    </div>
  );
};
