import React, { useState } from 'react';
import { UserProfile, RouteItem } from '../types';
import { APP_LOGO_URL } from '../data/routesData';
import { getCharacterById } from '../data/charactersData';
import { CharacterAvatar } from './CharacterAvatar';
import { getUserAchievements } from '../data/achievementsData';
import {
  X,
  ShieldCheck,
  LogOut,
  Sparkles,
  Check,
  Lock,
  Smartphone,
  Code,
  Trophy,
} from 'lucide-react';

interface AuthModalProps {
  mode?: 'signup' | 'login' | 'profile';
  user: UserProfile;
  isOpen: boolean;
  onClose: () => void;
  onGoogleSignIn: () => Promise<void>;
  onLogoutToWelcome: () => void;
  onOpenCharacterSelection?: () => void;
  onOpenApkModal?: () => void;
  onOpenKmpModal?: () => void;
  allRoutes?: RouteItem[];
  fieldNotesCount?: number;
  onNavigateToRoute?: (routeId: string) => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({
  user,
  isOpen,
  onClose,
  onGoogleSignIn,
  onLogoutToWelcome,
  onOpenCharacterSelection,
  onOpenApkModal,
  onOpenKmpModal,
  allRoutes = [],
  fieldNotesCount = 0,
}) => {
  const [isSigningIn, setIsSigningIn] = useState(false);
  const [signInError, setSignInError] = useState<string | null>(null);

  const activeChar = getCharacterById(user.characterId || 'meiga');
  const achievements = getUserAchievements(user, fieldNotesCount, allRoutes);
  const unlockedCount = achievements.filter(a => a.isUnlocked).length;

  if (!isOpen) return null;

  const handleGoogleSignIn = async () => {
    setIsSigningIn(true);
    setSignInError(null);
    try {
      await onGoogleSignIn();
    } catch (error) {
      setSignInError(error instanceof Error ? error.message : 'No se pudo iniciar sesión con Google.');
      setIsSigningIn(false);
    }
  };

  const handleSignOut = () => {
    onClose();
    onLogoutToWelcome();
  };

  return (
    <div
      className="fixed inset-0 z-50 bg-[#061b0e]/75 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        className="w-full max-w-md bg-[#fcf9f2] rounded-t-3xl sm:rounded-3xl p-5 sm:p-6 shadow-2xl border border-[#1b3022]/10 flex flex-col gap-4 animate-in slide-in-from-bottom sm:zoom-in-95 duration-200 text-[#1c1c18] max-h-[90vh] overflow-y-auto"
        onClick={e => e.stopPropagation()}
      >
        {/* Modal Header */}
        <div className="flex items-center justify-between pb-3 border-b border-[#1b3022]/10 sticky -top-5 sm:-top-6 bg-[#fcf9f2] z-10 pt-1">
          <div className="flex items-center gap-2.5">
            <img src={APP_LOGO_URL} alt="Logo" className="h-6 w-auto" />
            <div>
              <h3 className="font-serif text-[17px] font-bold text-[#061b0e]">
                {user.isGoogleAuthenticated ? 'Perfil de Usuario' : 'Acceso con Google'}
              </h3>
              <p className="text-[11px] text-[#47654e] font-sans">
                Ecos do Lugar
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-[#ebe8e1] flex items-center justify-center text-[#1c1c18] hover:bg-[#dcdad3] cursor-pointer transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Not authenticated view */}
        {!user.isGoogleAuthenticated ? (
          <div className="flex flex-col gap-4 py-1">
            <div className="text-center px-2">
              <div className="w-12 h-12 rounded-2xl bg-white border border-slate-200 shadow-sm mx-auto flex items-center justify-center mb-2">
                <svg className="w-6 h-6" viewBox="0 0 24 24">
                  <path
                    fill="#4285F4"
                    d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  />
                  <path
                    fill="#34A853"
                    d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  />
                  <path
                    fill="#FBBC05"
                    d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                  />
                  <path
                    fill="#EA4335"
                    d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                  />
                </svg>
              </div>
              <h4 className="font-serif text-[17px] font-bold text-[#061b0e]">
                Iniciar sesión con Google
              </h4>
              <p className="text-[12px] text-[#434843] leading-relaxed mt-1">
                Guarda tus rutas, desbloquea logros y lleva tu cuaderno de campo contigo.
              </p>
            </div>

            <button
              onClick={handleGoogleSignIn}
              disabled={isSigningIn}
              className="w-full h-12 bg-white text-[#1f1f1f] hover:bg-slate-50 active:scale-[0.98] border border-slate-300 rounded-xl font-sans text-[14px] font-semibold flex items-center justify-center gap-3 shadow transition-all cursor-pointer"
            >
              {isSigningIn ? (
                <span>Iniciando sesión...</span>
              ) : (
                <>
                  <svg className="w-5 h-5 shrink-0" viewBox="0 0 24 24">
                    <path
                      fill="#4285F4"
                      d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                    />
                    <path
                      fill="#34A853"
                      d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                    />
                    <path
                      fill="#FBBC05"
                      d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                    />
                    <path
                      fill="#EA4335"
                      d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                    />
                  </svg>
                  <span>Continuar con Google</span>
                </>
              )}
            </button>

            {signInError && (
              <p role="alert" className="text-xs text-red-700 bg-red-50 border border-red-200 rounded-xl p-3">
                {signInError}
              </p>
            )}

            <div className="flex items-center gap-2 p-2.5 rounded-xl bg-[#ebe8e1]/60 text-[11px] text-[#434843]">
              <ShieldCheck className="w-4 h-4 text-emerald-700 shrink-0" />
              <span>Solo se utiliza autenticación segura. No se almacenan contraseñas.</span>
            </div>
          </div>
        ) : (
          /* Profile view for signed-in user */
          <div className="flex flex-col gap-3 py-1 text-xs">
            {/* Google User Info */}
            <div className="p-3 rounded-2xl bg-white border border-[#c3c8c1]/70 shadow-xs flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-emerald-900 text-white font-bold flex items-center justify-center text-sm shadow shrink-0">
                  {user.name.charAt(0) || 'E'}
                </div>
                <div>
                  <h4 className="font-bold text-sm text-[#061b0e]">{user.name}</h4>
                  <p className="text-[11px] text-[#47654e] font-mono">{user.email}</p>
                </div>
              </div>
              <span className="px-2 py-0.5 rounded bg-emerald-100 text-emerald-800 text-[10px] font-semibold">
                Google
              </span>
            </div>

            {/* Active Character */}
            <div className="p-3 rounded-2xl bg-[#0e2718] text-[#f7f2ea] border border-[#2b5239] flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <CharacterAvatar character={activeChar} className="h-8 w-8" />
                <div>
                  <p className="text-[10px] text-[#faba71] uppercase tracking-wide">Personaje</p>
                  <p className="font-serif font-bold text-sm text-white">{activeChar.name}</p>
                </div>
              </div>

              {onOpenCharacterSelection && (
                <button
                  onClick={() => {
                    onClose();
                    onOpenCharacterSelection();
                  }}
                  className="px-2.5 py-1 rounded-lg bg-[#faba71] text-[#14281a] font-semibold text-[11px] flex items-center gap-1 cursor-pointer hover:bg-[#ffc988] transition-colors"
                >
                  <Sparkles className="w-3 h-3" />
                  <span>Cambiar</span>
                </button>
              )}
            </div>

            {/* Stats row */}
            <div className="grid grid-cols-3 gap-2 text-center">
              <div className="p-2 rounded-xl bg-white border border-[#c3c8c1]/50">
                <span className="font-serif text-base font-bold text-[#061b0e]">
                  {user.savedRouteIds.length}
                </span>
                <p className="text-[10px] text-[#434843]">Rutas guardadas</p>
              </div>
              <div className="p-2 rounded-xl bg-white border border-[#c3c8c1]/50">
                <span className="font-serif text-base font-bold text-[#061b0e]">
                  {user.visitedStopIds.length}
                </span>
                <p className="text-[10px] text-[#434843]">Paradas</p>
              </div>
              <div className="p-2 rounded-xl bg-white border border-[#c3c8c1]/50">
                <span className="font-serif text-base font-bold text-[#061b0e]">
                  {user.solvedEnigmaIds.length}
                </span>
                <p className="text-[10px] text-[#434843]">Enigmas</p>
              </div>
            </div>

            {/* SIMPLE LOGROS SECTION */}
            <div className="p-3.5 rounded-2xl bg-white border border-[#c3c8c1]/70 shadow-xs flex flex-col gap-2.5">
              <div className="flex items-center justify-between pb-1 border-b border-[#f1eee7]">
                <div className="flex items-center gap-1.5">
                  <Trophy className="w-4 h-4 text-amber-600" />
                  <h4 className="font-serif text-[13px] font-bold text-[#061b0e]">
                    Tus Logros
                  </h4>
                </div>
                <span className="text-[11px] font-semibold text-[#47654e]">
                  {unlockedCount} de {achievements.length} conseguidos
                </span>
              </div>

              <div className="flex flex-col gap-2">
                {achievements.map((ach) => (
                  <div
                    key={ach.id}
                    className={`p-2.5 rounded-xl border flex items-center justify-between gap-2.5 transition-all ${
                      ach.isUnlocked
                        ? 'bg-emerald-50/60 border-emerald-200 text-[#061b0e]'
                        : 'bg-[#faf8f3] border-[#e5e2db] text-[#555]'
                    }`}
                  >
                    <div className="flex items-center gap-2.5 min-w-0">
                      <span className={`text-xl ${ach.isUnlocked ? '' : 'opacity-50 grayscale'}`}>
                        {ach.icon}
                      </span>
                      <div className="min-w-0">
                        <h5 className="font-serif font-bold text-xs truncate">
                          {ach.title}
                        </h5>
                        <p className="text-[11px] text-[#666] leading-tight truncate">
                          {ach.description}
                        </p>
                      </div>
                    </div>

                    <div className="shrink-0">
                      {ach.isUnlocked ? (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 text-[10px] font-bold">
                          <Check className="w-3 h-3" />
                          <span>Conseguido</span>
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-slate-100 text-slate-500 text-[10px] font-medium">
                          <Lock className="w-2.5 h-2.5" />
                          <span>Pendiente</span>
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Developer links if needed */}
            {(onOpenApkModal || onOpenKmpModal) && (
              <div className="flex items-center gap-2 pt-1">
                {onOpenApkModal && (
                  <button
                    onClick={() => {
                      onClose();
                      onOpenApkModal();
                    }}
                    className="flex-1 py-1.5 px-2 rounded-xl bg-[#0b2416] text-white text-[11px] flex items-center justify-center gap-1.5 cursor-pointer"
                  >
                    <Smartphone className="w-3 h-3 text-emerald-400" />
                    <span>Descargar APK</span>
                  </button>
                )}
                {onOpenKmpModal && (
                  <button
                    onClick={() => {
                      onClose();
                      onOpenKmpModal();
                    }}
                    className="flex-1 py-1.5 px-2 rounded-xl bg-[#1b3022]/10 text-[#1b3022] text-[11px] flex items-center justify-center gap-1.5 cursor-pointer"
                  >
                    <Code className="w-3 h-3 text-amber-700" />
                    <span>Kotlin KMP</span>
                  </button>
                )}
              </div>
            )}

            {/* Logout button */}
            <button
              onClick={handleSignOut}
              className="w-full mt-1 py-2.5 px-3 rounded-xl text-red-700 bg-red-50 hover:bg-red-100 font-semibold text-xs flex items-center justify-center gap-1.5 transition-colors cursor-pointer border border-red-200"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Cerrar sesión</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
