import React, { useState, useEffect } from 'react';
import { StopTrial } from '../types';
import { getCharacterById } from '../data/charactersData';
import { CharacterAvatar } from './CharacterAvatar';
import { soundEngine } from '../services/soundEngine';
import {
  Sparkles,
  Award,
  HelpCircle,
  RotateCcw,
  Check,
  Zap,
  Volume2,
  Lock,
  Unlock,
  KeyRound,
} from 'lucide-react';

interface TrialCardProps {
  trial?: StopTrial;
  enigma: {
    question: string;
    hint: string;
    answer: string;
  };
  isSolved: boolean;
  characterId: string;
  onSolve: (attempts: number) => void;
  onUseDon?: () => void;
  isDonActive?: boolean;
}

export const TrialCard: React.FC<TrialCardProps> = ({
  trial,
  enigma,
  isSolved,
  characterId,
  onSolve,
  onUseDon,
  isDonActive = false,
}) => {
  const char = getCharacterById(characterId);
  const charClue = trial?.characterClues?.[characterId]
    || (characterId === 'urco' ? trial?.characterClues?.['lobishome'] : undefined)
    || trial?.characterClues?.['meiga'];

  // State for 'acertijo':
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [showAcertijoError, setShowAcertijoError] = useState(false);
  const [attemptCount, setAttemptCount] = useState(0);

  // State for 'puzle_runico':
  const [selectedRunes, setSelectedRunes] = useState<string[]>([]);
  const [runeFeedback, setRuneFeedback] = useState<'idle' | 'success' | 'error'>('idle');

  // State for 'cifrado_magico':
  const [typedLetters, setTypedLetters] = useState<string[]>([]);
  const [cipherInput, setCipherInput] = useState('');
  const [cipherFeedback, setCipherFeedback] = useState<'idle' | 'success' | 'error'>('idle');

  // Character Don states
  const [discardedOptions, setDiscardedOptions] = useState<number[]>([]);
  const [highlightedCorrectOption, setHighlightedCorrectOption] = useState(false);
  const [donMessage, setDonMessage] = useState<string | null>(null);

  const completeAttempt = () => {
    const attempts = attemptCount + 1;
    setAttemptCount(attempts);
    onSolve(attempts);
  };

  // General toggles:
  const [showGeneralHint, setShowGeneralHint] = useState(false);
  const [showCharacterClue, setShowCharacterClue] = useState(isDonActive);

  // Auto-fill puzzle with Don if active
  const handleApplyDonToPuzzle = () => {
    if (onUseDon) onUseDon();
    soundEngine.playMysticChime('magic');
    setShowCharacterClue(true);

    if (characterId === 'meiga') {
      // 1. MEIGA: Purificación y Descarte de Engaños
      if (trial?.type === 'acertijo' && trial.options && trial.correctOptionIndex !== undefined) {
        const wrong = trial.options
          .map((_, i) => i)
          .filter(i => i !== trial.correctOptionIndex);
        setDiscardedOptions(wrong.slice(0, 2));
        setDonMessage('🔥 ¡El fuego purificador de la Meiga quemó 2 opciones engañosas!');
      } else if (trial?.type === 'puzle_runico' && trial.correctRuneOrder) {
        setSelectedRunes(trial.correctRuneOrder.slice(0, 1));
        setDonMessage('🔮 ¡La Meiga purificó y reveló la primera runa sagrada!');
      } else if (trial?.type === 'cifrado_magico' && trial.cipherWord) {
        setTypedLetters([trial.cipherWord[0].toUpperCase()]);
        setDonMessage('🔮 ¡La Meiga iluminó la primera letra con su caldero!');
      }
    } else if (getCharacterById(characterId).id === 'nubeiro') {
      // 2. NUBEIRO: Orden Ancestral de los Petroglifos
      if (trial?.type === 'puzle_runico' && trial.correctRuneOrder) {
        setSelectedRunes(trial.correctRuneOrder.slice(0, Math.min(3, Math.max(2, trial.correctRuneOrder.length - 1))));
        setDonMessage('⛈️ ¡El Nubeiro ordenó los glifos milenarios sobre la piedra de los castros!');
      } else if (trial?.type === 'acertijo') {
        setHighlightedCorrectOption(true);
        setDonMessage('⛈️ ¡El Nubeiro recordó la inscripción milenaria de piedra!');
      } else if (trial?.type === 'cifrado_magico' && trial.cipherWord) {
        const firstTwo = trial.cipherWord.slice(0, 2).toUpperCase().split('');
        setTypedLetters(firstTwo);
        setDonMessage('⛈️ ¡El Nubeiro grabó las primeras runas de piedra en el código!');
      }
    } else if (characterId === 'trasno') {
      // 3. TRASNO: Atajo Pillo de Cifrados
      if (trial?.type === 'cifrado_magico' && trial.cipherWord) {
        const half = Math.max(2, Math.ceil(trial.cipherWord.length / 2));
        setTypedLetters(trial.cipherWord.slice(0, half).toUpperCase().split(''));
        setDonMessage('👺 ¡El Trasno trasteó y escribió la mitad de la palabra secreta!');
      } else if (trial?.type === 'acertijo' && trial.options && trial.correctOptionIndex !== undefined) {
        const wrong = trial.options
          .map((_, i) => i)
          .filter(i => i !== trial.correctOptionIndex);
        setDiscardedOptions(wrong.slice(0, 1));
        setDonMessage('👺 ¡El Trasno tachó una opción trampa con su gorro rojo!');
      } else if (trial?.type === 'puzle_runico' && trial.correctRuneOrder) {
        setSelectedRunes(trial.correctRuneOrder.slice(0, 2));
        setDonMessage('👺 ¡El Trasno se saltó el puzle y adelantó dos runas!');
      }
    } else if (characterId === 'urco') {
      setHighlightedCorrectOption(true);
      setDonMessage('🐺 ¡El instinto del Urco percibe la respuesta oculta entre las sombras!');
    }
  };

  useEffect(() => {
    if (isDonActive) {
      setShowCharacterClue(true);
      handleApplyDonToPuzzle();
    }
  }, [isDonActive]);

  // Reset local interactive state if trial changes
  useEffect(() => {
    setSelectedOption(null);
    setShowAcertijoError(false);
    setAttemptCount(0);
    setSelectedRunes([]);
    setRuneFeedback('idle');
    setTypedLetters([]);
    setCipherInput('');
    setCipherFeedback('idle');
    setShowGeneralHint(false);
    setDiscardedOptions([]);
    setHighlightedCorrectOption(false);
    setDonMessage(null);
  }, [trial?.title, isSolved]);

  // ACERTIJO Handler
  const handleSelectOption = (idx: number) => {
    if (isSolved) return;
    setSelectedOption(idx);
    soundEngine.playMysticChime('click');

    if (idx === trial?.correctOptionIndex) {
      soundEngine.playMysticChime('success');
      completeAttempt();
    } else {
      setAttemptCount(count => count + 1);
      setShowAcertijoError(true);
      setTimeout(() => setShowAcertijoError(false), 1800);
    }
  };

  // PUZLE RÚNICO Handlers
  const handleTapRune = (runeId: string) => {
    if (isSolved || selectedRunes.includes(runeId)) return;
    soundEngine.playMysticChime('click');

    const nextRunes = [...selectedRunes, runeId];
    setSelectedRunes(nextRunes);

    // Check if full sequence entered
    if (trial?.correctRuneOrder && nextRunes.length === trial.correctRuneOrder.length) {
      const isCorrect = nextRunes.every((id, idx) => id === trial.correctRuneOrder?.[idx]);
      if (isCorrect) {
        setRuneFeedback('success');
        soundEngine.playMysticChime('success');
        completeAttempt();
      } else {
        setAttemptCount(count => count + 1);
        setRuneFeedback('error');
        setTimeout(() => {
          setSelectedRunes([]);
          setRuneFeedback('idle');
        }, 1200);
      }
    }
  };

  const handleResetRunes = () => {
    soundEngine.playMysticChime('click');
    setSelectedRunes([]);
    setRuneFeedback('idle');
  };

  // CIFRADO MÁXICO Handlers
  const handleTapLetter = (letter: string) => {
    if (isSolved) return;
    soundEngine.playMysticChime('click');
    const targetWord = (trial?.cipherWord || '').toUpperCase();
    const nextLetters = [...typedLetters, letter];
    setTypedLetters(nextLetters);

    if (nextLetters.length === targetWord.length) {
      if (nextLetters.join('') === targetWord) {
        setCipherFeedback('success');
        soundEngine.playMysticChime('success');
        completeAttempt();
      } else {
        setAttemptCount(count => count + 1);
        setCipherFeedback('error');
        setTimeout(() => {
          setTypedLetters([]);
          setCipherFeedback('idle');
        }, 1200);
      }
    }
  };

  const handleBackspaceLetter = () => {
    soundEngine.playMysticChime('click');
    setTypedLetters(prev => prev.slice(0, -1));
  };

  const handleResetCipher = () => {
    soundEngine.playMysticChime('click');
    setTypedLetters([]);
    setCipherFeedback('idle');
  };

  const handleManualCipherSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!cipherInput.trim() || isSolved) return;
    const targetWord = (trial?.cipherWord || '').toUpperCase().trim();
    if (cipherInput.toUpperCase().trim() === targetWord) {
      setCipherFeedback('success');
      soundEngine.playMysticChime('success');
      completeAttempt();
    } else {
      setAttemptCount(count => count + 1);
      setCipherFeedback('error');
      setTimeout(() => setCipherFeedback('idle'), 1600);
    }
  };

  // Fallback if no specific trial object is attached
  if (!trial) {
    return (
      <div className="p-4 rounded-2xl bg-[#ebe8e1]/60 border border-[#dcdad3] flex flex-col gap-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2 text-[#412500]">
            <HelpCircle className="w-4 h-4 text-[#c48b47]" />
            <span className="font-sans text-[12px] font-bold uppercase tracking-wider">
              Enigma Sagrado
            </span>
          </div>
          {isSolved ? (
            <span className="flex items-center gap-1 text-emerald-700 font-sans text-xs font-semibold">
              <Award className="w-3.5 h-3.5" />
              Resuelto
            </span>
          ) : (
            <span className="text-[11px] text-[#434843] font-sans font-medium">
              +1 sello mítico
            </span>
          )}
        </div>

        <p className="font-serif text-[15px] text-[#1c1c18] font-medium leading-snug">
          {enigma.question}
        </p>

        {!isSolved && (
          <button
            onClick={() => {
              soundEngine.playMysticChime('success');
              completeAttempt();
            }}
            className="w-full py-2 bg-[#1b3022] text-white text-xs font-semibold rounded-xl active:scale-95 transition-all cursor-pointer"
          >
            Desvelar Enigma
          </button>
        )}
      </div>
    );
  }

  return (
    <div
      className={`rounded-2xl border transition-all duration-300 overflow-hidden ${
        isSolved
          ? 'bg-gradient-to-b from-[#f3f9f4] to-white border-emerald-300 shadow-sm'
          : 'bg-[#faf8f3] border-[#1b3022]/15 shadow-sm'
      }`}
    >
      {/* Top Banner: Trial Header & Type */}
      <div className="px-4 py-3 bg-[#1b3022]/8 border-b border-[#1b3022]/10 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-6 h-6 rounded-lg bg-[#1b3022] text-white flex items-center justify-center text-xs">
            {trial.type === 'puzle_runico' ? '🧩' : trial.type === 'cifrado_magico' ? '🗝️' : '📜'}
          </div>
          <div>
            <span className="text-[10px] font-sans uppercase font-bold tracking-wider text-[#47654e] block">
              {trial.type === 'puzle_runico'
                ? 'Puzle Rúnico Ancestral'
                : trial.type === 'cifrado_magico'
                ? 'Cifrado Mágico'
                : 'Acertijo Mitológico'}
            </span>
            <h4 className="font-serif text-[14px] font-bold text-[#061b0e] leading-tight">
              {trial.title}
            </h4>
          </div>
        </div>

        {isSolved ? (
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-semibold border border-emerald-300">
            <Check className="w-3.5 h-3.5" />
            <span>Resuelto</span>
          </div>
        ) : (
          <button
            onClick={handleApplyDonToPuzzle}
            className="flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#faba71]/20 hover:bg-[#faba71]/35 text-[#543000] border border-[#faba71]/50 text-[11px] font-sans font-bold transition-transform active:scale-95 cursor-pointer"
            title="Invocar don de tu personaje para obtener auxilio en esta prueba"
          >
            <Zap className="w-3 h-3 text-[#b45309]" />
            <span>Usar Don</span>
          </button>
        )}
      </div>

      <div className="p-4 flex flex-col gap-3.5">
        {/* Don feedback message */}
        {donMessage && (
          <div className="p-3 rounded-xl bg-[#1b3022] text-[#f7f2ea] text-xs font-sans flex items-center justify-between gap-2 shadow-sm animate-in fade-in slide-in-from-top-1">
            <div className="flex items-center gap-2">
              <CharacterAvatar character={char} className="h-6 w-6" />
              <span className="font-medium leading-snug">{donMessage}</span>
            </div>
            <button
              type="button"
              onClick={() => setDonMessage(null)}
              className="text-[#f7f2ea]/70 hover:text-white text-xs font-bold px-1.5 py-0.5 rounded hover:bg-white/10 cursor-pointer"
            >
              ✕
            </button>
          </div>
        )}

        {/* Lore Context */}
        <p className="font-sans text-[13px] text-[#4a554d] italic border-l-2 border-[#c48b47] pl-3 py-0.5">
          {trial.loreContext}
        </p>

        {/* The Question / Mission */}
        <div className="font-serif text-[15px] font-semibold text-[#1c1c18] leading-snug">
          {trial.question}
        </div>

        {/* 1. ACERTIJO INTERFACE */}
        {trial.type === 'acertijo' && trial.options && (
          <div className="flex flex-col gap-2 mt-1">
            {trial.options.map((option, idx) => {
              const isSelected = selectedOption === idx;
              const isCorrect = idx === trial.correctOptionIndex;
              const isWrong = isSelected && !isCorrect;
              const isDiscarded = discardedOptions.includes(idx);
              const isHighlighted = highlightedCorrectOption && isCorrect;

              let btnStyle = 'bg-white border-[#dcdad3] hover:border-[#1b3022]/40 text-[#1c1c18]';
              if (isSolved && isCorrect) {
                btnStyle = 'bg-emerald-50 border-emerald-500 text-emerald-900 font-semibold ring-1 ring-emerald-500';
              } else if (isWrong) {
                btnStyle = 'bg-rose-50 border-rose-400 text-rose-900 animate-shake';
              } else if (isDiscarded) {
                btnStyle = 'bg-stone-100/70 border-stone-200 text-stone-400 opacity-40 line-through pointer-events-none cursor-not-allowed';
              } else if (isHighlighted && !isSolved) {
                btnStyle = 'bg-amber-50/90 border-amber-400 text-amber-950 ring-2 ring-amber-400 shadow-md animate-pulse';
              }

              return (
                <button
                  key={idx}
                  type="button"
                  disabled={isSolved || isDiscarded}
                  onClick={() => handleSelectOption(idx)}
                  className={`w-full p-3 rounded-xl border text-left text-[13px] font-sans transition-all flex items-start gap-2.5 cursor-pointer ${btnStyle}`}
                >
                  <span className="w-5 h-5 rounded-full bg-[#1b3022]/10 flex items-center justify-center font-bold text-xs shrink-0 text-[#1b3022]">
                    {String.fromCharCode(65 + idx)}
                  </span>
                  <span className="flex-1 leading-snug">{option}</span>
                  {isDiscarded && (
                    <span className="text-[10px] font-mono font-bold text-stone-500 px-1.5 py-0.5 rounded bg-stone-200 shrink-0">
                      {characterId === 'meiga' ? '🔥 Quemada' : '👺 Tachada'}
                    </span>
                  )}
                  {isHighlighted && !isSolved && (
                    <span className="text-[10px] font-sans font-bold text-amber-900 px-2 py-0.5 rounded-full bg-amber-200/90 flex items-center gap-1 shrink-0">
                      {characterId === 'urco' ? '🐺 Instinto' : '✨ Pista'}
                    </span>
                  )}
                  {isSolved && isCorrect && <Check className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />}
                </button>
              );
            })}

            {showAcertijoError && (
              <p className="text-xs text-rose-700 font-sans font-medium text-center">
                ¡No es la respuesta correcta! Prueba otra opción o pide auxilio a tu personaje.
              </p>
            )}
          </div>
        )}

        {/* 2. PUZLE RÚNICO INTERFACE */}
        {trial.type === 'puzle_runico' && trial.runes && (
          <div className="flex flex-col gap-3 mt-1">
            {/* Sequence Slots */}
            <div className="flex items-center justify-between gap-2 p-3 rounded-xl bg-white border border-[#dcdad3]">
              <div className="flex items-center gap-2">
                <span className="text-[11px] uppercase font-bold text-[#47654e]">Secuencia:</span>
                <div className="flex items-center gap-1.5 min-h-[32px]">
                  {Array.from({ length: trial.correctRuneOrder?.length || 4 }).map((_, i) => {
                    const runeId = selectedRunes[i];
                    const runeObj = trial.runes?.find(r => r.id === runeId);
                    return (
                      <div
                        key={i}
                        className={`w-9 h-9 rounded-lg flex items-center justify-center text-lg border transition-all ${
                          runeObj
                            ? 'bg-amber-50 border-amber-400 shadow-xs'
                            : 'bg-[#f6f3ec] border-dashed border-[#c3c8c1]'
                        }`}
                      >
                        {runeObj?.glyph || (
                          <span className="text-xs text-[#999] font-serif">{i + 1}</span>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>

              {!isSolved && selectedRunes.length > 0 && (
                <button
                  type="button"
                  onClick={handleResetRunes}
                  className="p-1.5 rounded-lg text-[#555] hover:bg-slate-100 active:scale-95 transition-transform"
                  title="Reiniciar orden"
                >
                  <RotateCcw className="w-4 h-4" />
                </button>
              )}
            </div>

            {/* Clickable Runes Grid */}
            {!isSolved && (
              <div className="grid grid-cols-2 gap-2">
                {trial.runes.map(rune => {
                  const isPlaced = selectedRunes.includes(rune.id);
                  const nextTargetRuneId = trial.correctRuneOrder?.[selectedRunes.length];
                  const isHighlightedRune = highlightedCorrectOption && rune.id === nextTargetRuneId && !isPlaced;

                  let runeStyle = 'bg-white border-[#dcdad3] hover:border-[#b45309] active:scale-95 shadow-2xs';
                  if (isPlaced) {
                    runeStyle = 'opacity-40 bg-slate-100 border-slate-200 cursor-not-allowed';
                  } else if (isHighlightedRune) {
                    runeStyle = 'bg-amber-50 border-amber-400 ring-2 ring-amber-400 shadow-md animate-pulse';
                  }

                  return (
                    <button
                      key={rune.id}
                      type="button"
                      disabled={isPlaced}
                      onClick={() => handleTapRune(rune.id)}
                      className={`p-2.5 rounded-xl border text-left flex items-center gap-2.5 transition-all cursor-pointer ${runeStyle}`}
                    >
                      <span className="text-2xl shrink-0">{rune.glyph}</span>
                      <div className="flex flex-col min-w-0">
                        <div className="flex items-center gap-1">
                          <span className="font-serif text-[13px] font-bold text-[#1c1c18] truncate leading-tight">
                            {rune.name}
                          </span>
                          {isHighlightedRune && (
                            <span className="text-[9px] px-1 rounded bg-amber-200 text-amber-900 font-bold shrink-0">
                              Rastro
                            </span>
                          )}
                        </div>
                        <span className="font-sans text-[11px] text-[#555] truncate">
                          {rune.desc}
                        </span>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}

            {runeFeedback === 'error' && (
              <p className="text-xs text-rose-700 font-sans font-medium text-center">
                El orden ancestral no cuadra. Los espíritus piden restablecer el ritual...
              </p>
            )}
          </div>
        )}

        {/* 3. CIFRADO MÁGICO INTERFACE */}
        {trial.type === 'cifrado_magico' && (
          <div className="flex flex-col gap-3 mt-1">
            {/* Mystery Word Slots */}
            <div className="flex flex-col items-center justify-center p-3.5 rounded-xl bg-white border border-[#dcdad3]">
              <div className="text-[11px] font-sans text-[#47654e] font-semibold mb-2">
                {trial.cipherClue || 'Descifra el término sagrado:'}
              </div>
              <div className="flex items-center gap-1.5 flex-wrap justify-center">
                {Array.from({ length: (trial.cipherWord || '').length }).map((_, i) => {
                  const letter = typedLetters[i];
                  return (
                    <div
                      key={i}
                      className={`w-9 h-11 rounded-lg flex items-center justify-center font-serif text-[18px] font-bold border transition-all ${
                        letter
                          ? 'bg-amber-100 border-amber-500 text-amber-950 shadow-xs'
                          : 'bg-[#f6f3ec] border-dashed border-[#c3c8c1] text-[#999]'
                      }`}
                    >
                      {letter || '_'}
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Clickable Scrambled Tiles */}
            {!isSolved && trial.scrambledLetters && (
              <div className="flex flex-col gap-2">
                <div className="flex items-center justify-center gap-2 flex-wrap">
                  {trial.scrambledLetters.map((letter, idx) => (
                    <button
                      key={idx}
                      type="button"
                      onClick={() => handleTapLetter(letter)}
                      className="w-10 h-10 rounded-xl bg-white border border-[#dcdad3] hover:border-[#1b3022] font-serif text-[17px] font-bold text-[#1c1c18] active:scale-90 transition-transform shadow-2xs cursor-pointer"
                    >
                      {letter}
                    </button>
                  ))}
                  {typedLetters.length > 0 && (
                    <button
                      type="button"
                      onClick={handleBackspaceLetter}
                      className="h-10 px-2.5 rounded-xl bg-[#f1eee7] hover:bg-[#e5e2db] text-xs font-sans font-semibold text-[#1c1c18] active:scale-95 cursor-pointer"
                    >
                      Borrar
                    </button>
                  )}
                </div>

                {/* Direct Text Input as alternative */}
                <form onSubmit={handleManualCipherSubmit} className="flex gap-2 mt-1">
                  <input
                    type="text"
                    value={cipherInput}
                    onChange={e => setCipherInput(e.target.value.toUpperCase())}
                    placeholder="O escribe la palabra completa..."
                    className="flex-1 bg-white border border-[#c3c8c1] rounded-xl px-3 py-1.5 text-[13px] font-sans uppercase text-[#1c1c18] focus:outline-none focus:border-[#1b3022]"
                  />
                  <button
                    type="submit"
                    className="px-3.5 py-1.5 bg-[#1b3022] text-white text-[12px] font-semibold font-sans rounded-xl active:scale-95 cursor-pointer"
                  >
                    Probar
                  </button>
                </form>
              </div>
            )}

            {cipherFeedback === 'error' && (
              <p className="text-xs text-rose-700 font-sans font-medium text-center">
                La palabra no coincide con el códice antiguo. ¡Inténtalo de nuevo!
              </p>
            )}
          </div>
        )}

        {/* Character Mystical Advice / Don Section */}
        {charClue && (
          <div className="mt-1">
            {showCharacterClue ? (
              <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 flex items-start gap-2.5">
                <CharacterAvatar character={char} className="mt-0.5 h-6 w-6" />
                <div className="flex flex-col min-w-0">
                  <div className="flex items-center gap-1.5">
                    <span className="font-serif text-[12px] font-bold text-amber-950">
                      Pista Secreta de {char.name}
                    </span>
                    <span className="text-[10px] px-1.5 py-0.2 rounded bg-amber-200 text-amber-900 font-semibold uppercase">
                      Don Activo
                    </span>
                  </div>
                  <p className="font-sans text-[12px] text-amber-900/90 leading-relaxed mt-0.5 italic">
                    {charClue}
                  </p>
                </div>
              </div>
            ) : (
              <button
                type="button"
                onClick={() => {
                  soundEngine.playMysticChime('magic');
                  setShowCharacterClue(true);
                  if (onUseDon) onUseDon();
                }}
                className="w-full py-2 px-3 rounded-xl bg-amber-500/10 hover:bg-amber-500/15 border border-amber-500/25 flex items-center justify-between text-left cursor-pointer transition-all active:scale-[0.99]"
              >
                <div className="flex items-center gap-2">
                  <CharacterAvatar character={char} className="h-6 w-6" />
                  <span className="font-sans text-[12px] font-medium text-amber-950">
                    Preguntar secreto oculto a {char.name}
                  </span>
                </div>
                <span className="text-[11px] text-amber-800 font-bold uppercase tracking-wider">
                  Desvelar
                </span>
              </button>
            )}
          </div>
        )}

        {/* Solved Explanation & Reward Display */}
        {isSolved && (
          <div className="p-3.5 rounded-xl bg-emerald-50 border border-emerald-300 flex flex-col gap-1.5 animate-in fade-in zoom-in-95 duration-200">
            <div className="flex items-center gap-1.5 text-emerald-900 font-sans text-xs font-bold uppercase tracking-wider">
              <Award className="w-4 h-4 text-emerald-600" />
              <span>{trial.magicalReward || 'Sello Mítico Conquistado'}</span>
            </div>
            <p className="font-sans text-[12px] text-emerald-800 leading-relaxed">
              {trial.solutionExplanation}
            </p>
          </div>
        )}

        {/* Cartographic hint toggler */}
        {!isSolved && (
          <div className="flex items-center justify-between pt-1 border-t border-[#1b3022]/10 text-[12px]">
            <button
              type="button"
              onClick={() => setShowGeneralHint(!showGeneralHint)}
              className="text-[#47654e] hover:underline font-sans cursor-pointer flex items-center gap-1"
            >
              <HelpCircle className="w-3.5 h-3.5" />
              <span>{showGeneralHint ? 'Ocultar pista' : 'Pista general del terreno'}</span>
            </button>

            <button
              type="button"
              onClick={() => {
                soundEngine.playMysticChime('success');
                completeAttempt();
              }}
              className="text-[#b45309] hover:underline font-sans cursor-pointer font-medium"
            >
              Revelar solución
            </button>
          </div>
        )}

        {showGeneralHint && !isSolved && (
          <div className="p-2.5 rounded-xl bg-white border border-[#c3c8c1]/60 text-[12px] font-sans text-[#434843] italic">
            💡 {trial.generalHint}
          </div>
        )}
      </div>
    </div>
  );
};
