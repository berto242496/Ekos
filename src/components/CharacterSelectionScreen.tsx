import React, { useState } from 'react';
import { UserProfile } from '../types';
import { CHARACTERS, getCharacterById } from '../data/charactersData';
import { LanguageSelector } from './LanguageSelector';
import { useI18n } from '../services/i18n';
import { ArrowLeft, Check, CheckCircle2, Sparkles } from 'lucide-react';

interface CharacterSelectionScreenProps {
  user: UserProfile;
  onConfirmCharacter: (characterId: string) => void;
  onBack: () => void;
}

export const CharacterSelectionScreen: React.FC<CharacterSelectionScreenProps> = ({
  user,
  onConfirmCharacter,
  onBack,
}) => {
  const { t } = useI18n();
  const [selectedId, setSelectedId] = useState<string>(
    getCharacterById(user.characterId || 'meiga').id,
  );
  const activeChar = getCharacterById(selectedId);
  const confirmText = t('character_selection_confirm').replace('{name}', activeChar.name);

  return (
    <div className="relative w-full min-h-screen bg-[#07160d] text-[#f7f2ea] flex flex-col justify-between selection:bg-[#faba71]/30">
      <div
        className="fixed inset-0 pointer-events-none opacity-30 blur-3xl transition-all duration-700"
        style={{
          background: `radial-gradient(circle at 50% 20%, ${activeChar.accentColor} 0%, transparent 55%), radial-gradient(circle at 80% 80%, #061b0e 0%, transparent 60%)`,
        }}
      />
      <div className="absolute top-0 inset-x-0 h-40 bg-gradient-to-b from-[#0b2416] via-transparent to-transparent pointer-events-none" />

      <div className="relative z-10 w-full max-w-2xl mx-auto px-4 sm:px-6 py-6 flex flex-col gap-5">
        <div className="flex items-center justify-between border-b border-white/10 pb-4">
          <button
            onClick={onBack}
            className="w-10 h-10 rounded-full bg-white/10 hover:bg-white/15 active:scale-95 flex items-center justify-center text-[#ebe8e1] transition-all cursor-pointer"
            title={t('back')}
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <div className="text-center">
            <span className="text-[10px] font-mono tracking-widest text-[#faba71] uppercase font-semibold">
              {t('character_selection_eyebrow')}
            </span>
            <h1 className="font-serif text-[22px] sm:text-[24px] font-bold text-white tracking-tight">
              {t('character_selection_title')}
            </h1>
          </div>

          <div className="flex items-center gap-2">
            <LanguageSelector variant="compact" />
            <div className="w-10 h-10 rounded-full bg-[#132e1e] border border-[#faba71]/30 flex items-center justify-center text-xl shadow-inner">
              {activeChar.avatarEmoji}
            </div>
          </div>
        </div>

        <p className="text-xs sm:text-sm text-[#cfbc9f] leading-relaxed">
          {t('character_selection_intro')}
        </p>

        <div className="space-y-2">
          <label className="text-[11px] uppercase font-bold tracking-wider text-[#faba71]">
            {t('character_selection_selector')}
          </label>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {CHARACTERS.map((char) => {
              const isSelected = char.id === activeChar.id;
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
                </button>
              );
            })}
          </div>
        </div>

        <div
          className={`rounded-3xl p-5 sm:p-6 border shadow-2xl transition-all duration-300 relative overflow-hidden bg-gradient-to-br ${activeChar.cardGradient}`}
          style={{
            borderColor: `${activeChar.accentColor}60`,
            boxShadow: `0 8px 30px ${activeChar.glowColor}`,
          }}
        >
          <div className="absolute -right-8 -bottom-8 opacity-10 pointer-events-none text-9xl">
            {activeChar.avatarEmoji}
          </div>

          <div className="relative z-10 flex flex-col gap-4">
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
              <h2 className="font-serif text-2xl font-bold text-white tracking-tight">
                {activeChar.name}
              </h2>
            </div>

            <p className="text-xs sm:text-sm text-[#f1eee7] leading-relaxed">
              {t(`character_selection_${activeChar.id}_description`)}
            </p>

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
                  {t('character_selection_power')}
                </span>
              </div>
              <p className="text-xs text-[#f7f2ea]/90 leading-relaxed pl-6">
                {t(`character_selection_${activeChar.id}_power`)}
              </p>
            </div>
          </div>
        </div>

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
            <span>{confirmText}</span>
          </button>

          <p className="text-center text-[11px] text-[#cfbc9f]/60">
            {t('character_selection_change_hint')}
          </p>
        </div>
      </div>
    </div>
  );
};
