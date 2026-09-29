import React, { useEffect } from 'react';
import { Achievement } from '../types';
import { Sparkles, X } from 'lucide-react';

interface AchievementToastProps {
  achievement: Achievement | null;
  onClose: () => void;
}

export const AchievementToast: React.FC<AchievementToastProps> = ({ achievement, onClose }) => {
  useEffect(() => {
    if (!achievement) return;
    const timer = setTimeout(() => {
      onClose();
    }, 4500);
    return () => clearTimeout(timer);
  }, [achievement, onClose]);

  if (!achievement) return null;

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-[100] w-[90%] max-w-sm animate-in slide-in-from-top duration-300 pointer-events-auto">
      <div className="bg-[#0b2416] text-[#fcf9f2] p-3.5 rounded-2xl shadow-xl border border-[#faba71]/40 flex items-center justify-between gap-3">
        <div className="flex items-center gap-3 min-w-0">
          <div className="w-10 h-10 rounded-xl bg-white/10 text-white flex items-center justify-center text-xl shrink-0">
            {achievement.icon || '🏆'}
          </div>

          <div className="min-w-0">
            <div className="flex items-center gap-1">
              <Sparkles className="w-3 h-3 text-[#faba71]" />
              <span className="text-[10px] font-sans uppercase tracking-wider font-semibold text-[#faba71]">
                ¡Logro desbloqueado!
              </span>
            </div>
            <h4 className="font-serif font-bold text-xs text-white truncate mt-0.5">
              {achievement.title}
            </h4>
          </div>
        </div>

        <button
          onClick={onClose}
          className="w-6 h-6 rounded-full bg-white/10 hover:bg-white/20 text-white flex items-center justify-center cursor-pointer transition-colors shrink-0"
        >
          <X className="w-3 h-3" />
        </button>
      </div>
    </div>
  );
};
