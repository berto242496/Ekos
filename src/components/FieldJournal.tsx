import React, { useState } from 'react';
import { RouteItem, FieldNote, UserProfile } from '../types';
import { getCharacterById } from '../data/charactersData';
import { CharacterAvatar } from './CharacterAvatar';
import {
  BookOpen,
  Bookmark,
  Award,
  Plus,
  Trash2,
  Calendar,
  Compass,
  CheckCircle2,
  ArrowRight,
  Shield,
  Sparkles,
} from 'lucide-react';

interface FieldJournalProps {
  user: UserProfile;
  routes: RouteItem[];
  notes: FieldNote[];
  onSelectRoute: (route: RouteItem) => void;
  onAddNote: (note: Omit<FieldNote, 'id' | 'timestamp'>) => void;
  onDeleteNote: (noteId: string) => void;
  onOpenProfile: () => void;
  onOpenCharacterSelection?: () => void;
}

export const FieldJournal: React.FC<FieldJournalProps> = ({
  user,
  routes,
  notes,
  onSelectRoute,
  onAddNote,
  onDeleteNote,
  onOpenProfile,
  onOpenCharacterSelection,
}) => {
  const [activeTab, setActiveTab] = useState<'guardadas' | 'notas' | 'sellos'>('guardadas');
  const [showNoteForm, setShowNoteForm] = useState(false);
  const [newNoteContent, setNewNoteContent] = useState('');
  const [selectedRouteId, setSelectedRouteId] = useState(routes[0]?.id || '');
  const activeChar = getCharacterById(user.characterId || 'meiga');

  const savedRoutes = routes.filter(r => user.savedRouteIds.includes(r.id));

  const handleCreateNote = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newNoteContent.trim()) return;
    const r = routes.find(item => item.id === selectedRouteId);
    onAddNote({
      routeId: selectedRouteId,
      routeTitle: r?.title || 'Expedición Galega',
      content: newNoteContent.trim(),
    });
    setNewNoteContent('');
    setShowNoteForm(false);
  };

  const STAMPS = [
    {
      id: 'ribeira',
      title: 'Cañones del Sil',
      place: 'Ribeira Sacra & Santo Estevo',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('rs') || id.includes('c1')),
      icon: '🍇',
    },
    {
      id: 'fisterra',
      title: 'Finis Terrae',
      place: 'Costa da Morte & Faro Fisterra',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('cm') || id.includes('c2')),
      icon: '🌊',
    },
    {
      id: 'eume',
      title: 'Fragas do Eume',
      place: 'Bosque Atlántico & Caaveiro',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('fe') || id.includes('l1')),
      icon: '🌿',
    },
    {
      id: 'santiago',
      title: 'Plaza del Obradoiro',
      place: 'Catedral de Santiago de Compostela',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('sc') || id.includes('c3')),
      icon: '⭐',
    },
    {
      id: 'courel',
      title: 'Devesa da Rogueira',
      place: 'Sierra del Courel & Aldeas de Pizarra',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('cou') || id.includes('m1')),
      icon: '⛰️',
    },
    {
      id: 'rias',
      title: 'Rías Baixas',
      place: 'Hórreos de Combarro e Islas Atlánticas',
      date: 'Desbloqueado',
      unlocked: user.visitedStopIds.some(id => id.includes('rb') || id.includes('m2')),
      icon: '⛵',
    },
  ];

  return (
    <div className="flex flex-col w-full min-h-screen bg-[#fcf9f2] text-[#1c1c18] pb-28">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-[#fcf9f2]/90 backdrop-blur-xl border-b border-[#1b3022]/10 px-5 py-3">
        <div className="flex items-center justify-between max-w-lg mx-auto">
          <div className="flex flex-col">
            <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
              Archivo Vivo Personal
            </span>
            <h1 className="font-serif text-[22px] font-semibold text-[#1c1c18]">
              Mi aventura
            </h1>
          </div>

          <button
            onClick={onOpenProfile}
            className="flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#f1eee7] text-[#1c1c18] font-sans text-[12px] font-medium border border-[#c3c8c1]/40 hover:bg-[#ebe8e1] cursor-pointer"
            title="Abrir perfil / Cerrar sesión y empezar de nuevo"
          >
            <span>{user.name || 'Perfil'}</span>
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main className="w-full max-w-lg mx-auto px-5 pt-4 flex flex-col gap-5">
        {/* Explorer Passport Card */}
        <div className="p-5 rounded-2xl bg-[#061b0e] text-[#fcf9f2] border border-[#1b3022] shadow-md flex flex-col gap-4 relative overflow-hidden">
          <div className="absolute -right-8 -bottom-8 w-32 h-32 opacity-10 text-[#faba71] pointer-events-none">
            <Compass className="w-full h-full" />
          </div>

          <div className="flex items-start justify-between">
            <div className="flex flex-col">
              <span className="font-sans text-[11px] uppercase tracking-widest text-[#ffddba] font-semibold">
                Cuaderno de Campo de Galicia
              </span>
              <h2 className="font-serif text-[22px] font-semibold text-white">
                {user.name}
              </h2>
              <span className="font-sans text-[12px] text-[#c3c8c1] mt-0.5">
                {user.isGoogleAuthenticated ? (
                  <span className="text-emerald-300 font-semibold flex items-center gap-1">
                    ✓ Google: {user.email}
                  </span>
                ) : (
                  user.email
                )}
              </span>
            </div>

            {/* Character Badge & Quick Switch */}
            <div className="flex flex-col items-end gap-1.5">
              <button
                onClick={onOpenCharacterSelection}
                className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/10 hover:bg-white/15 border border-[#faba71]/40 transition-all cursor-pointer"
                title="Cambiar personaje"
              >
                <CharacterAvatar character={activeChar} className="h-6 w-6" />
                <div className="text-left hidden sm:block">
                  <p className="text-[11px] font-serif font-bold text-white leading-tight">
                    {activeChar.name}
                  </p>
                  <p className="text-[9px] text-[#faba71] leading-tight">
                    {activeChar.archetype}
                  </p>
                </div>
              </button>
            </div>
          </div>

          {/* Quick Metrics Grid */}
          <div className="grid grid-cols-4 gap-2 pt-3 border-t border-white/10 text-center">
            <div className="flex flex-col">
              <span className="font-sans text-[10px] text-[#ebe8e1] uppercase tracking-wider">
                Completadas
              </span>
              <span className="font-serif text-[17px] font-bold text-[#fcf9f2]">
                {user.completedRouteIds?.length || 0}
              </span>
            </div>

            <div className="flex flex-col border-l border-white/10">
              <span className="font-sans text-[10px] text-[#ebe8e1] uppercase tracking-wider">
                Paradas
              </span>
              <span className="font-serif text-[17px] font-bold text-[#c5e8ca]">
                {user.visitedStopIds.length}
              </span>
            </div>

            <div className="flex flex-col border-l border-white/10">
              <span className="font-sans text-[10px] text-[#ebe8e1] uppercase tracking-wider">
                Enigmas
              </span>
              <span className="font-serif text-[17px] font-bold text-[#faba71]">
                {user.solvedEnigmaIds.length}
              </span>
            </div>

            <div className="flex flex-col border-l border-white/10">
              <span className="font-sans text-[10px] text-[#ebe8e1] uppercase tracking-wider">
                Logros
              </span>
              <span className="font-serif text-[17px] font-bold text-amber-300">
                {user.unlockedAchievementIds?.length || 0}
              </span>
            </div>
          </div>

          {/* Quick shortcut to open Profile Achievements */}
          <button
            onClick={onOpenProfile}
            className="w-full py-2 px-3 rounded-xl bg-white/10 hover:bg-white/15 border border-[#faba71]/30 text-[#ffddba] text-xs font-semibold flex items-center justify-center gap-2 transition-all cursor-pointer"
          >
            <span>🏆 Ver Logros e Hitos Desbloqueables en tu Perfil</span>
            <ArrowRight className="w-3.5 h-3.5 text-[#faba71]" />
          </button>
        </div>

        {/* Sub Navigation Tabs */}
        <div className="flex items-center gap-1.5 p-1 bg-[#ebe8e1] rounded-xl">
          <button
            onClick={() => setActiveTab('guardadas')}
            className={`flex-1 py-2 rounded-lg font-sans text-[13px] font-semibold transition-all cursor-pointer ${
              activeTab === 'guardadas'
                ? 'bg-white text-[#061b0e] shadow-2xs'
                : 'text-[#434843] hover:text-[#1c1c18]'
            }`}
          >
            Guardadas ({savedRoutes.length})
          </button>

          <button
            onClick={() => setActiveTab('notas')}
            className={`flex-1 py-2 rounded-lg font-sans text-[13px] font-semibold transition-all cursor-pointer ${
              activeTab === 'notas'
                ? 'bg-white text-[#061b0e] shadow-2xs'
                : 'text-[#434843] hover:text-[#1c1c18]'
            }`}
          >
            Notas ({notes.length})
          </button>

          <button
            onClick={() => setActiveTab('sellos')}
            className={`flex-1 py-2 rounded-lg font-sans text-[13px] font-semibold transition-all cursor-pointer ${
              activeTab === 'sellos'
                ? 'bg-white text-[#061b0e] shadow-2xs'
                : 'text-[#434843] hover:text-[#1c1c18]'
            }`}
          >
            Sellos
          </button>
        </div>

        {/* TAB 1: Guardadas */}
        {activeTab === 'guardadas' && (
          <div className="flex flex-col gap-3">
            {savedRoutes.length === 0 ? (
              <div className="p-8 text-center bg-white rounded-2xl border border-[#e5e2db] shadow-2xs">
                <Bookmark className="w-8 h-8 text-[#737973] mx-auto mb-2 opacity-50" />
                <h3 className="font-serif text-[17px] font-semibold text-[#1c1c18] mb-1">
                  Aún no has guardado rutas
                </h3>
                <p className="font-sans text-[13px] text-[#434843] mb-4">
                  Pulsa el icono de marcapáginas en cualquier ruta para conservarla en tu cuaderno.
                </p>
                <button
                  onClick={() => onSelectRoute(routes[0])}
                  className="px-4 py-2 bg-[#1b3022] text-white font-sans text-[13px] font-medium rounded-xl"
                >
                  Explorar Cañones del Sil y Ribeira Sacra
                </button>
              </div>
            ) : (
              savedRoutes.map(route => (
                <div
                  key={route.id}
                  onClick={() => onSelectRoute(route)}
                  className="p-3.5 bg-white rounded-2xl border border-[#1b3022]/8 shadow-2xs flex items-center gap-3.5 hover:shadow-sm transition-all cursor-pointer"
                >
                  <img
                    src={route.image}
                    alt={route.title}
                    className="w-20 h-20 rounded-xl object-cover shrink-0"
                  />
                  <div className="flex flex-col min-w-0 flex-1">
                    <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
                      {route.location}
                    </span>
                    <h3 className="font-serif text-[17px] font-semibold text-[#1c1c18] truncate">
                      {route.title}
                    </h3>
                    <p className="font-sans text-[13px] text-[#434843] line-clamp-1 mt-0.5">
                      {route.description}
                    </p>
                    <div className="flex items-center gap-3 mt-1.5 text-[11px] font-sans text-[#737973]">
                      <span>{route.duration}</span>
                      <span>•</span>
                      <span>{route.stopsCount} paradas</span>
                    </div>
                  </div>
                  <ArrowRight className="w-4 h-4 text-[#737973] shrink-0" />
                </div>
              ))
            )}
          </div>
        )}

        {/* TAB 2: Cuaderno de Notas */}
        {activeTab === 'notas' && (
          <div className="flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <span className="font-sans text-[13px] text-[#434843]">
                Tus reflexiones y anotaciones a pie de ruta
              </span>
              <button
                onClick={() => setShowNoteForm(!showNoteForm)}
                className="px-3 py-1.5 bg-[#1b3022] text-white rounded-lg font-sans text-[12px] font-semibold flex items-center gap-1 active:scale-95 transition-transform cursor-pointer"
              >
                <Plus className="w-3.5 h-3.5" />
                <span>Nueva nota</span>
              </button>
            </div>

            {showNoteForm && (
              <form
                onSubmit={handleCreateNote}
                className="p-4 bg-white rounded-2xl border border-[#1b3022]/15 shadow-sm flex flex-col gap-3 animate-in fade-in duration-200"
              >
                <div className="flex flex-col gap-1">
                  <label className="font-sans text-[12px] font-semibold text-[#1c1c18]">
                    Vincular a ruta:
                  </label>
                  <select
                    value={selectedRouteId}
                    onChange={e => setSelectedRouteId(e.target.value)}
                    className="bg-[#f6f3ec] border border-[#c3c8c1] rounded-lg px-3 py-2 text-[13px] font-sans text-[#1c1c18] focus:outline-none"
                  >
                    {routes.map(r => (
                      <option key={r.id} value={r.id}>
                        {r.title}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1">
                  <label className="font-sans text-[12px] font-semibold text-[#1c1c18]">
                    Anotación o detalle hallado:
                  </label>
                  <textarea
                    rows={3}
                    value={newNoteContent}
                    onChange={e => setNewNoteContent(e.target.value)}
                    placeholder="Escribe lo que viste, escuchaste o sentiste..."
                    className="bg-[#f6f3ec] border border-[#c3c8c1] rounded-lg p-3 text-[14px] font-sans text-[#1c1c18] focus:outline-none focus:border-[#1b3022]"
                  />
                </div>

                <div className="flex items-center justify-end gap-2 pt-1">
                  <button
                    type="button"
                    onClick={() => setShowNoteForm(false)}
                    className="px-3 py-1.5 font-sans text-[12px] text-[#737973] hover:text-[#1c1c18]"
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-1.5 bg-[#1b3022] text-white rounded-lg font-sans text-[12px] font-semibold cursor-pointer"
                  >
                    Guardar nota
                  </button>
                </div>
              </form>
            )}

            {notes.length === 0 ? (
              <div className="p-8 text-center bg-white rounded-2xl border border-[#e5e2db] shadow-2xs">
                <BookOpen className="w-8 h-8 text-[#737973] mx-auto mb-2 opacity-50" />
                <h3 className="font-serif text-[17px] font-semibold text-[#1c1c18] mb-1">
                  Cuaderno en blanco
                </h3>
                <p className="font-sans text-[13px] text-[#434843]">
                  Apunta tus impresiones, nombres de lugareños o marcas de cantero que descubras en el camino.
                </p>
              </div>
            ) : (
              notes.map(note => (
                <div
                  key={note.id}
                  className="p-4 bg-white rounded-2xl border border-[#1b3022]/8 shadow-2xs flex flex-col gap-2 relative group"
                >
                  <div className="flex items-center justify-between">
                    <span className="font-sans text-[11px] uppercase tracking-wider text-[#47654e] font-semibold">
                      {note.routeTitle} {note.stopTitle ? `· ${note.stopTitle}` : ''}
                    </span>
                    <div className="flex items-center gap-2">
                      <span className="font-sans text-[11px] text-[#737973] flex items-center gap-1">
                        <Calendar className="w-3 h-3" />
                        {note.timestamp}
                      </span>
                      <button
                        onClick={() => onDeleteNote(note.id)}
                        className="text-[#737973] hover:text-red-600 transition-colors p-1 cursor-pointer"
                        title="Eliminar nota"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                  <p className="font-sans text-[14px] text-[#1c1c18] whitespace-pre-wrap leading-relaxed">
                    {note.content}
                  </p>
                </div>
              ))
            )}
          </div>
        )}

        {/* TAB 3: Pasaporte de Galicia y Sellos */}
        {activeTab === 'sellos' && (
          <div className="flex flex-col gap-3">
            <p className="font-sans text-[13px] text-[#434843]">
              Sellos coleccionables al completar paradas y enigmas en las cuatro provincias de Galicia.
            </p>

            <div className="grid grid-cols-2 gap-3">
              {STAMPS.map(stamp => (
                <div
                  key={stamp.id}
                  className={`p-4 rounded-2xl border flex flex-col items-center text-center gap-2 transition-all ${
                    stamp.unlocked
                      ? 'bg-white border-[#1b3022]/15 shadow-2xs'
                      : 'bg-[#f6f3ec]/60 border-dashed border-[#c3c8c1] opacity-60'
                  }`}
                >
                  <div
                    className={`w-14 h-14 rounded-full flex items-center justify-center text-2xl border-2 ${
                      stamp.unlocked
                        ? 'bg-[#f1eee7] border-[#1b3022] shadow-xs'
                        : 'bg-[#e5e2db] border-[#c3c8c1]'
                    }`}
                  >
                    {stamp.icon}
                  </div>

                  <div className="flex flex-col">
                    <h4 className="font-serif text-[16px] font-semibold text-[#1c1c18]">
                      {stamp.title}
                    </h4>
                    <span className="font-sans text-[11px] text-[#434843]">
                      {stamp.place}
                    </span>
                  </div>

                  <span
                    className={`px-2 py-0.5 rounded-full font-sans text-[10px] font-semibold uppercase tracking-wider ${
                      stamp.unlocked
                        ? 'bg-[#c5e8ca] text-[#03210e]'
                        : 'bg-[#e5e2db] text-[#737973]'
                    }`}
                  >
                    {stamp.unlocked ? 'Desbloqueado' : 'Por descubrir'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};
