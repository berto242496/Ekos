import React, { useState, useRef, useEffect } from 'react';
import { useI18n, SUPPORTED_LANGUAGES } from '../services/i18n';
import { Globe, ChevronDown, Check } from 'lucide-react';
import { AppLanguage } from '../types';

interface LanguageSelectorProps {
  variant?: 'pill' | 'compact' | 'drawer';
  className?: string;
}

export const LanguageSelector: React.FC<LanguageSelectorProps> = ({
  variant = 'pill',
  className = '',
}) => {
  const { lang, setLang } = useI18n();
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const currentOption =
    SUPPORTED_LANGUAGES.find((opt) => opt.code === lang) || SUPPORTED_LANGUAGES[0];

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSelect = (code: AppLanguage) => {
    setLang(code);
    setIsOpen(false);
  };

  if (variant === 'compact') {
    return (
      <div className={`relative ${className}`} ref={containerRef}>
        <button
          type="button"
          onClick={() => setIsOpen(!isOpen)}
          className="inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-xl bg-white/80 hover:bg-white text-[#1b3022] text-xs font-serif font-semibold border border-[#1b3022]/15 shadow-2xs transition-all cursor-pointer"
          title="Cambiar idioma / Change language"
        >
          <span className="text-sm">{currentOption.flag}</span>
          <span className="text-[11px] font-bold">{currentOption.shortLabel}</span>
          <ChevronDown className={`w-3 h-3 text-[#c48b47] transition-transform ${isOpen ? 'rotate-180' : ''}`} />
        </button>

        {isOpen && (
          <div className="absolute right-0 mt-1.5 w-36 py-1 bg-white rounded-2xl border border-[#1b3022]/15 shadow-lg z-50 animate-in fade-in zoom-in-95 duration-150">
            {SUPPORTED_LANGUAGES.map((opt) => (
              <button
                key={opt.code}
                type="button"
                onClick={() => handleSelect(opt.code)}
                className={`w-full px-3 py-2 text-left text-xs font-serif flex items-center justify-between transition-colors cursor-pointer ${
                  opt.code === lang
                    ? 'bg-[#1b3022]/5 text-[#1b3022] font-bold'
                    : 'text-[#334b39] hover:bg-[#fcf9f2]'
                }`}
              >
                <div className="flex items-center gap-2">
                  <span className="text-base">{opt.flag}</span>
                  <span>{opt.name}</span>
                </div>
                {opt.code === lang && <Check className="w-3.5 h-3.5 text-[#c48b47]" />}
              </button>
            ))}
          </div>
        )}
      </div>
    );
  }

  return (
    <div className={`relative ${className}`} ref={containerRef}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/90 hover:bg-white text-[#1b3022] text-xs font-serif font-semibold border border-[#1b3022]/15 shadow-2xs transition-all cursor-pointer"
        title="Cambiar idioma / Switch language"
      >
        <Globe className="w-3.5 h-3.5 text-[#c48b47]" />
        <span className="text-sm">{currentOption.flag}</span>
        <span className="text-xs">{currentOption.name}</span>
        <ChevronDown className={`w-3 h-3 text-[#1b3022]/60 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-44 py-1.5 bg-white rounded-2xl border border-[#1b3022]/15 shadow-xl z-50 animate-in fade-in zoom-in-95 duration-150">
          <div className="px-3 py-1 text-[10px] uppercase font-bold text-[#8c5717] tracking-wider border-b border-[#1b3022]/10 mb-1">
            Seleccionar idioma
          </div>
          {SUPPORTED_LANGUAGES.map((opt) => (
            <button
              key={opt.code}
              type="button"
              onClick={() => handleSelect(opt.code)}
              className={`w-full px-3 py-2 text-left text-xs font-serif flex items-center justify-between transition-colors cursor-pointer ${
                opt.code === lang
                  ? 'bg-[#1b3022] text-[#fcf9f2] font-bold'
                  : 'text-[#1b3022] hover:bg-[#fcf9f2]'
              }`}
            >
              <div className="flex items-center gap-2.5">
                <span className="text-base">{opt.flag}</span>
                <span>{opt.name}</span>
              </div>
              {opt.code === lang ? (
                <Check className="w-3.5 h-3.5 text-[#c48b47]" />
              ) : (
                <span className="text-[10px] text-[#47654e] font-sans uppercase">{opt.shortLabel}</span>
              )}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};
