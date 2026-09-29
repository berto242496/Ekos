import React, { useState } from 'react';
import { CharacterItem, UserProfile } from '../types';
import { CHARACTERS, getCharacterById } from '../data/charactersData';
import { LanguageSelector } from './LanguageSelector';
import { useI18n } from '../services/i18n';
import {
  Sparkles,
  ArrowLeft,
  Check,
  ShieldCheck,
  Compass,
  Footprints,
  Shield,
  Waves,
  Navigation,
  CheckCircle2,
} from 'lucide-react';

interface CharacterSelectionScreenProps {
  user: UserProfile;
  onConfirmCharacter: (characterId: string) => void;
  onBack: () => void;
  onOpenGoogleAuth?: () => void;
}

export const CharacterSelectionScreen: React.FC<CharacterSelectionScreenProps> = ({
  user,
  onConfirmCharacter,
  onBack,
  onOpenGoogleAuth,
}) => {
  const { t } = useI18n();
  const [selectedId, setSelectedId] = useState<string>(user.characterId || 'meiga');
  const activeChar = getCharacterById(selectedId);

  return (
    <div className="relative w-full min-h-screen bg-[#07160d] text-[#f7f2ea] flex flex-col justify-between selection:bg-[#faba71]/30">
      {/* Ambient background glows */}
      <div
        className="fixed inset-0 pointer-events-none opacity-30 blur-3xl transition-all duration-700"
        style={{
          background: `radial-gradient(circle at 50% 20%, ${activeChar.accentColor} 0%, transparent 55%), radial-gradient(circle at 80% 80%, #061b0e 0%, transparent 60%)`,
        }}
      />

      {/* Decorative top pattern */}
      <div className="absolute top-0 inset-x-0 h-40 bg-gradient-to-b from-[#0b2416] via-transparent to-transparent pointer-events-none" />

      {/* Main Container */}
      <div className="relative z-10 w-full max-w-2xl mx-auto px-4 sm:px-6 py-6 flex flex-col gap-5">
        {/* Top Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-4">
          <button
            onClick={onBack}
            className="w-10 h-10 rounded-full bg-white/10 hover:bg-white/15 active:scale-95 flex items-center justify-center text-[#ebe8e1] transition-all cursor-pointer"
            title="Volver"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <div className="text-center">
            <span className="text-[10px] font-mono tracking-widest text-[#faba71] uppercase font-semibold">
              Mitología y Leyendas de Galicia
            </span>
            <h1 className="font-serif text-[22px] sm:text-[24px] font-bold text-white tracking-tight">
              Elige tu personaje
            </h1>
          </div>

          <div className="flex items-center gap-2">
            <LanguageSelector variant="compact" />
            <div className="w-10 h-10 rounded-full bg-[#132e1e] border border-[#faba71]/30 flex items-center justify-center text-xl shadow-inner">
              {activeChar.avatarEmoji}
            </div>
          </div>
        </div>

        {/* Google Authentication Status Banner */}
        <div className="p-3 rounded-xl bg-[#0e2316]/90 border border-[#23452f] flex items-center justify-between gap-3 text-xs">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-7 h-7 rounded-lg bg-white flex items-center justify-center shrink-0 shadow-sm">
              <span className="font-black text-sm text-[#4285F4]">G</span>
            </div>
            <div className="min-w-0">
              {user.isGoogleAuthenticated ? (
                <>
                  <p className="text-emerald-300 font-semibold truncate flex items-center gap-1.5">
                    <ShieldCheck className="w-3.5 h-3.5 shrink-0" />
                    Cuenta Google verificada
                  </p>
                  <p className="text-[#cfbc9f]/80 text-[11px] truncate font-mono">
                    {user.email}
                  </p>
                </>
              ) : (
                <>
                  <p className="text-[#ffddba] font-medium truncate">
                    Modo visitante activo
                  </p>
                  <p className="text-[#cfbc9f]/70 text-[11px] truncate">
                    Inicia sesión con Google para guardar tu personaje en la nube
                  </p>
                </>
              )}
            </div>
          </div>

          {!user.isGoogleAuthenticated && onOpenGoogleAuth && (
            <button
              onClick={onOpenGoogleAuth}
              className="px-3 py-1.5 rounded-lg bg-white text-[#1f1f1f] hover:bg-slate-100 font-medium text-[11px] font-sans flex items-center gap-1.5 shrink-0 transition-colors shadow cursor-pointer"
            >
              <span className="font-bold text-[#4285F4]">G</span>
              <span>Conectar Google</span>
            </button>
          )}
        </div>

        {/* Intro subtitle */}
        <p className="text-xs sm:text-sm text-[#cfbc9f] leading-relaxed">
          Cada personaje posee una naturaleza mitológica y un <strong className="text-[#ffddba]">don especial</strong> para desvelar los enigmas y senderos de toda Galicia.
        </p>

        {/* Character Selector Carousel / Grid */}
        <div className="space-y-2">
          <label className="text-[11px] uppercase font-bold tracking-wider text-[#faba71]">
            Selecciona un arquetipo mitológico:
          </label>

          <div className="grid grid-cols-3 sm:grid-cols-6 gap-2">
            {CHARACTERS.map((char) => {
              const isSelected = char.id === selectedId;
              return (
                <button
                  key={char.id}
                  onClick={() => setSelectedId(char.id)}
                  className={`relative p-2.5 rounded-2xl flex flex-col items-center gap-1.5 transition-all text-center cursor-pointer border ${
                    isSelected
                      ? 'bg-white/15 scale-[1.03] shadow-lg'
                      : 'bg-white/5 hover:bg-white/10 border-white/10 opacity-75 hover:opacity-100'
                  }`}
                  style={{
                    borderColor: isSelected ? char.accentColor : undefined,
                    boxShadow: isSelected ? `0 4px 20px ${char.glowColor}` : undefined,
                  }}
                >
                  {isSelected && (
                    <span
                      className="absolute -top-1.5 -right-1.5 w-5 h-5 rounded-full flex items-center justify-center text-white text-[10px] shadow"
                      style={{ backgroundColor: char.accentColor }}
                    >
                      <Check className="w-3 h-3" />
                    </span>
                  )}
                  <div
                    className="w-12 h-12 rounded-xl flex items-center justify-center text-2xl transition-transform"
                    style={{
                      backgroundColor: isSelected ? `${char.accentColor}30` : 'rgba(0,0,0,0.3)',
                    }}
                  >
                    {char.avatarEmoji}
                  </div>
                  <span className="font-serif text-xs font-bold text-white truncate w-full">
                    {char.name}
                  </span>
                  <span
                    className="text-[9px] font-sans truncate w-full font-medium"
                    style={{ color: isSelected ? char.accentColor : '#cfbc9f' }}
                  >
                    {char.archetype}
                  </span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Character Card In-Depth View */}
        <div
          className={`rounded-3xl p-5 sm:p-6 border shadow-2xl transition-all duration-300 relative overflow-hidden bg-gradient-to-br ${activeChar.cardGradient}`}
          style={{
            borderColor: `${activeChar.accentColor}60`,
            boxShadow: `0 8px 30px ${activeChar.glowColor}`,
          }}
        >
          {/* Watermark Element */}
          <div className="absolute -right-8 -bottom-8 opacity-10 pointer-events-none text-9xl">
            {activeChar.avatarEmoji}
          </div>

          <div className="relative z-10 flex flex-col gap-4">
            {/* Header of character */}
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-3.5">
                <div
                  className="w-14 h-14 rounded-2xl flex items-center justify-center text-3xl border shadow-inner shrink-0"
                  style={{
                    backgroundColor: `${activeChar.accentColor}25`,
                    borderColor: `${activeChar.accentColor}80`,
                  }}
                >
                  {activeChar.avatarEmoji}
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h2 className="font-serif text-2xl font-bold text-white tracking-tight">
                      {activeChar.name}
                    </h2>
                    <span
                      className="px-2 py-0.5 rounded-md text-[10px] font-sans font-bold uppercase tracking-wider text-white"
                      style={{ backgroundColor: `${activeChar.accentColor}40` }}
                    >
                      {activeChar.badge}
                    </span>
                  </div>
                  <p
                    className="text-xs font-sans font-medium mt-0.5"
                    style={{ color: activeChar.accentColor }}
                  >
                    {activeChar.galicianTitle}
                  </p>
                </div>
              </div>
            </div>

            {/* Lore & Narrative */}
            <p className="text-xs sm:text-sm text-[#f1eee7] leading-relaxed">
              {activeChar.lore}
            </p>

            {/* Quote */}
            <div className="p-3 rounded-xl bg-black/40 border border-white/5 text-center">
              <p className="font-serif italic text-xs text-[#ffddba]">
                {activeChar.quote}
              </p>
            </div>

            {/* Special Ability: Don del Personaje */}
            <div
              className="p-3.5 rounded-2xl border flex flex-col gap-1.5"
              style={{
                backgroundColor: `${activeChar.accentColor}18`,
                borderColor: `${activeChar.accentColor}50`,
              }}
            >
              <div className="flex items-center gap-2">
                <Sparkles className="w-4 h-4" style={{ color: activeChar.accentColor }} />
                <span
                  className="text-xs font-bold uppercase tracking-wider font-mono"
                  style={{ color: activeChar.accentColor }}
                >
                  Don del Personaje: {activeChar.abilityName}
                </span>
              </div>
              <p className="text-xs text-[#f7f2ea]/90 leading-relaxed pl-6">
                {activeChar.abilityDescription}
              </p>
            </div>

            {/* Sacred Item & Element Tags */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
              <div className="p-2.5 rounded-xl bg-black/30 border border-white/5 flex flex-col gap-0.5">
                <span className="text-[10px] uppercase font-mono tracking-wider text-[#cfbc9f]/70">
                  Objeto Sagrado
                </span>
                <span className="font-medium text-white text-[11px] truncate">
                  {activeChar.sacredItem}
                </span>
              </div>
              <div className="p-2.5 rounded-xl bg-black/30 border border-white/5 flex flex-col gap-0.5">
                <span className="text-[10px] uppercase font-mono tracking-wider text-[#cfbc9f]/70">
                  Elemento / Vínculo
                </span>
                <span className="font-medium text-white text-[11px] truncate">
                  {activeChar.element}
                </span>
              </div>
            </div>

            {/* Stats section */}
            <div className="space-y-2 pt-1">
              <span className="text-[10px] uppercase font-bold tracking-wider text-[#cfbc9f]/80 font-mono">
                Atributos del Espíritu:
              </span>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-2">
                {activeChar.stats.map((stat, i) => (
                  <div key={i} className="flex flex-col gap-1">
                    <div className="flex justify-between text-[11px]">
                      <span className="text-[#cfbc9f]">{stat.label}</span>
                      <span className="font-bold font-mono text-white">{stat.value}%</span>
                    </div>
                    <div className="w-full h-1.5 rounded-full bg-white/10 overflow-hidden">
                      <div
                        className="h-full rounded-full transition-all duration-500"
                        style={{
                          width: `${stat.value}%`,
                          backgroundColor: activeChar.accentColor,
                        }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Action Button: Confirm and proceed */}
        <div className="pt-2 pb-6 flex flex-col gap-2">
          <button
            onClick={() => onConfirmCharacter(activeChar.id)}
            className="w-full py-4 px-6 rounded-2xl font-sans font-bold text-sm sm:text-base flex items-center justify-center gap-2.5 shadow-xl transition-all duration-200 active:scale-[0.98] cursor-pointer"
            style={{
              backgroundColor: activeChar.accentColor,
              color: '#0a160d',
              boxShadow: `0 8px 25px ${activeChar.glowColor}`,
            }}
          >
            <CheckCircle2 className="w-5 h-5" />
            <span>Comenzar expedición por Galicia como {activeChar.name}</span>
          </button>

          <p className="text-center text-[11px] text-[#cfbc9f]/60">
            Podrás cambiar de personaje en cualquier momento desde tu perfil o cuaderno de campo.
          </p>
        </div>
      </div>
    </div>
  );
};
