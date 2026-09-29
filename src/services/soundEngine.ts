import { VoicePersona, VoicePersonaId } from '../types';
import { getStoredLanguage } from './i18n';

export const VOICE_PERSONAS: VoicePersona[] = [
  {
    id: 'breogan',
    name: 'Breogán',
    title: 'Voz Épica y Mitológica',
    description: 'Tono grave, profundo y solemne. Evoca la fuerza de los antiguos castros y las leyendas marineras.',
    gender: 'male',
    tone: 'Profundo y Solemne (0.85x grave)',
    sampleText: 'Bienvenido a la tierra de los castros y de las leyendas marineras. Escucha el eco de las piedras milenarias que guardan la memoria de este lugar sagrado.',
    rate: 0.88,
    pitch: 0.82,
    lang: 'es-ES',
    avatarEmoji: '⚡',
    recommendedFor: 'Leyendas, fortalezas celtas y costas bravas',
  },
  {
    id: 'rosalia',
    name: 'Rosalía',
    title: 'Voz Lírica y Cálida',
    description: 'Tono poético, dulce y envolvente, pausado e íntimo. Ideal para transmitir la emoción y el alma del paisaje.',
    gender: 'female',
    tone: 'Cálida y Poética (1.10x melódica)',
    sampleText: 'La niebla acaricia los robles y las aguas del río cantan su memoria viva. Déjate llevar por el latido sereno de esta tierra.',
    rate: 0.90,
    pitch: 1.12,
    lang: 'es-ES',
    avatarEmoji: '🌿',
    recommendedFor: 'Naturaleza, cantigas, ríos y memoria oral',
  },
  {
    id: 'iria',
    name: 'Iria',
    title: 'Voz Clara y Didáctica',
    description: 'Tono fresco, brillante y dinámico de narración contemporánea. Perfecto para guiar con dinamismo a familias y grupos.',
    gender: 'female',
    tone: 'Fresca y Didáctica (1.0x equilibrada)',
    sampleText: '¡En este punto de la ruta vamos a descubrir un secreto que muy pocos viajeros conocen! Abre bien los ojos y prepara tu ingenio.',
    rate: 1.0,
    pitch: 1.04,
    lang: 'es-ES',
    avatarEmoji: '✨',
    recommendedFor: 'Familias, senderismo y retos de observación',
  },
  {
    id: 'xacobe',
    name: 'Xacobe',
    title: 'Voz de Cronista Histórico',
    description: 'Tono solemne y documentado, como un maestro cronista o archivero medieval que revela secretos del pasado.',
    gender: 'male',
    tone: 'Solemne e Histórico (0.90x cadencioso)',
    sampleText: 'Constan en las crónicas del siglo doce los pasos de peregrinos y comerciantes por esta misma calzada de piedra. He aquí el testimonio de la historia.',
    rate: 0.91,
    pitch: 0.88,
    lang: 'es-ES',
    avatarEmoji: '📜',
    recommendedFor: 'Patrimonio arquitectónico e historia medieval',
  },
  {
    id: 'guia_propia',
    name: 'Voz Propia del Guía',
    title: 'Audio Grabado por el Autor',
    description: 'Permite grabar directamente con el micrófono del navegador o subir tu propia locución de autor.',
    gender: 'male',
    tone: 'Auténtica y Personal',
    sampleText: 'Audio narrado directamente por el guía local con su propia voz, acento y matices personales.',
    rate: 1.0,
    pitch: 1.0,
    lang: 'es-ES',
    avatarEmoji: '🎙️',
    recommendedFor: 'Máxima autenticidad y cercanía humana',
  },
];

/**
 * Sound engine for Ecos do Lugar:
 * Generates natural ambient soundscape of the Miño river (water flow, gentle breeze, distant bells)
 * and provides speech synthesis for route narratives.
 */

class SoundEngine {
  private audioCtx: AudioContext | null = null;
  private ambientGain: GainNode | null = null;
  private isAmbientPlaying = false;
  private noiseNode: AudioBufferSourceNode | null = null;
  private lfoNode: OscillatorNode | null = null;
  private onAmbientChangeCallbacks: ((playing: boolean) => void)[] = [];

  private isSpeaking = false;
  private speechUtterance: SpeechSynthesisUtterance | null = null;
  private onSpeechChangeCallbacks: ((speaking: boolean) => void)[] = [];

  private initAudio() {
    if (!this.audioCtx) {
      const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      this.audioCtx = new AudioContextClass();
    }
    if (this.audioCtx.state === 'suspended') {
      this.audioCtx.resume();
    }
  }

  public subscribeAmbient(cb: (playing: boolean) => void) {
    this.onAmbientChangeCallbacks.push(cb);
    return () => {
      this.onAmbientChangeCallbacks = this.onAmbientChangeCallbacks.filter(c => c !== cb);
    };
  }

  public subscribeSpeech(cb: (speaking: boolean) => void) {
    this.onSpeechChangeCallbacks.push(cb);
    return () => {
      this.onSpeechChangeCallbacks = this.onSpeechChangeCallbacks.filter(c => c !== cb);
    };
  }

  public toggleAmbient(): boolean {
    if (this.isAmbientPlaying) {
      this.stopAmbient();
      return false;
    } else {
      this.startAmbient();
      return true;
    }
  }

  public getIsAmbientPlaying(): boolean {
    return this.isAmbientPlaying;
  }

  public startAmbient() {
    try {
      this.initAudio();
      if (!this.audioCtx) return;

      // Create pink noise buffer for river water & mist flow
      const bufferSize = this.audioCtx.sampleRate * 3;
      const noiseBuffer = this.audioCtx.createBuffer(1, bufferSize, this.audioCtx.sampleRate);
      const output = noiseBuffer.getChannelData(0);
      let b0 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;
      for (let i = 0; i < bufferSize; i++) {
        const white = Math.random() * 2 - 1;
        b0 = 0.99886 * b0 + white * 0.0555179;
        b1 = 0.99332 * b1 + white * 0.0750759;
        b2 = 0.96900 * b2 + white * 0.1538520;
        b3 = 0.86650 * b3 + white * 0.3104856;
        b4 = 0.55000 * b4 + white * 0.5329522;
        b5 = -0.7616 * b5 - white * 0.0168980;
        output[i] = (b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362) * 0.06;
        b6 = white * 0.115926;
      }

      const noise = this.audioCtx.createBufferSource();
      noise.buffer = noiseBuffer;
      noise.loop = true;

      // Low pass filter to create water murmur
      const filter = this.audioCtx.createBiquadFilter();
      filter.type = 'lowpass';
      filter.frequency.setValueAtTime(420, this.audioCtx.currentTime);

      // Low frequency oscillator for wave movement
      const lfo = this.audioCtx.createOscillator();
      lfo.frequency.setValueAtTime(0.18, this.audioCtx.currentTime); // gentle wave rhythm
      const lfoGain = this.audioCtx.createGain();
      lfoGain.gain.setValueAtTime(140, this.audioCtx.currentTime);
      lfo.connect(lfoGain);
      lfoGain.connect(filter.frequency);

      const gain = this.audioCtx.createGain();
      gain.gain.setValueAtTime(0.01, this.audioCtx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.35, this.audioCtx.currentTime + 1.2);

      noise.connect(filter);
      filter.connect(gain);
      gain.connect(this.audioCtx.destination);

      noise.start();
      lfo.start();

      this.noiseNode = noise;
      this.lfoNode = lfo;
      this.ambientGain = gain;
      this.isAmbientPlaying = true;
      this.notifyAmbient(true);

      // Occasional gentle bell/chime harmonic
      this.scheduleOccasionalChime();
    } catch {
      this.isAmbientPlaying = false;
      this.notifyAmbient(false);
    }
  }

  private scheduleOccasionalChime() {
    if (!this.isAmbientPlaying || !this.audioCtx) return;
    const delay = 6000 + Math.random() * 8000;
    setTimeout(() => {
      if (this.isAmbientPlaying && this.audioCtx) {
        this.playDistantBell();
        this.scheduleOccasionalChime();
      }
    }, delay);
  }

  private playDistantBell() {
    try {
      if (!this.audioCtx) return;
      const osc = this.audioCtx.createOscillator();
      const gain = this.audioCtx.createGain();
      osc.type = 'sine';
      // Low cathedral chime frequency (approx G3 / C4)
      osc.frequency.setValueAtTime(261.63, this.audioCtx.currentTime);
      
      gain.gain.setValueAtTime(0.001, this.audioCtx.currentTime);
      gain.gain.linearRampToValueAtTime(0.04, this.audioCtx.currentTime + 0.1);
      gain.gain.exponentialRampToValueAtTime(0.0001, this.audioCtx.currentTime + 4.5);

      osc.connect(gain);
      gain.connect(this.audioCtx.destination);

      osc.start();
      osc.stop(this.audioCtx.currentTime + 4.6);
    } catch {
      // ignore
    }
  }

  public stopAmbient() {
    if (this.ambientGain && this.audioCtx) {
      try {
        this.ambientGain.gain.linearRampToValueAtTime(0.001, this.audioCtx.currentTime + 0.5);
        setTimeout(() => {
          this.noiseNode?.stop();
          this.lfoNode?.stop();
          this.noiseNode?.disconnect();
          this.lfoNode?.disconnect();
          this.noiseNode = null;
          this.lfoNode = null;
          this.ambientGain = null;
        }, 550);
      } catch {
        // ignore
      }
    }
    this.isAmbientPlaying = false;
    this.notifyAmbient(false);
  }

  public speakWithPersona(
    text: string,
    personaId: VoicePersonaId = 'breogan',
    onEnd?: () => void,
    speedMultiplier: number = 1.0
  ) {
    if (!('speechSynthesis' in window)) return;
    window.speechSynthesis.cancel();

    const persona = VOICE_PERSONAS.find(p => p.id === personaId) || VOICE_PERSONAS[0];

    const appLang = getStoredLanguage();
    const langCodeMap: Record<string, string> = {
      es: 'es-ES',
      gl: 'gl-ES',
      en: 'en-US',
      pt: 'pt-PT',
    };
    const targetLang = langCodeMap[appLang] || persona.lang || 'es-ES';

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = targetLang;
    utterance.rate = Math.max(0.6, Math.min(1.8, persona.rate * speedMultiplier));
    utterance.pitch = persona.pitch;

    // Pick best matching voice
    const voices = window.speechSynthesis.getVoices();
    const prefix = targetLang.slice(0, 2);
    let matchedVoices = voices.filter(v => v.lang.startsWith(prefix));
    if (matchedVoices.length === 0 && prefix === 'gl') {
      // Fallback for Galician if system has no gl voice
      matchedVoices = voices.filter(v => v.lang.startsWith('es'));
    }
    if (matchedVoices.length === 0) {
      matchedVoices = voices;
    }

    if (matchedVoices.length > 0) {
      if (persona.gender === 'female') {
        const femaleVoice = matchedVoices.find(v =>
          v.name.toLowerCase().includes('female') ||
          v.name.toLowerCase().includes('monica') ||
          v.name.toLowerCase().includes('elvira') ||
          v.name.toLowerCase().includes('helena') ||
          v.name.toLowerCase().includes('laura') ||
          v.name.toLowerCase().includes('maria') ||
          v.name.toLowerCase().includes('sofia') ||
          v.name.toLowerCase().includes('samantha')
        );
        utterance.voice = femaleVoice || matchedVoices[0];
      } else {
        const maleVoice = matchedVoices.find(v =>
          v.name.toLowerCase().includes('male') ||
          v.name.toLowerCase().includes('jorge') ||
          v.name.toLowerCase().includes('pablo') ||
          v.name.toLowerCase().includes('alvaro') ||
          v.name.toLowerCase().includes('diego') ||
          v.name.toLowerCase().includes('carlos') ||
          v.name.toLowerCase().includes('daniel')
        );
        utterance.voice = maleVoice || matchedVoices[0];
      }
    }

    utterance.onstart = () => {
      this.isSpeaking = true;
      this.notifySpeech(true);
    };

    utterance.onend = () => {
      this.isSpeaking = false;
      this.notifySpeech(false);
      if (onEnd) onEnd();
    };

    utterance.onerror = () => {
      this.isSpeaking = false;
      this.notifySpeech(false);
    };

    this.speechUtterance = utterance;
    window.speechSynthesis.speak(utterance);
  }

  public speak(text: string, onEnd?: () => void, personaId?: VoicePersonaId) {
    if (personaId) {
      this.speakWithPersona(text, personaId, onEnd);
      return;
    }

    if (!('speechSynthesis' in window)) return;
    window.speechSynthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'es-ES';
    utterance.rate = 0.93; // deliberate editorial pacing
    utterance.pitch = 0.98;

    // Try to find Spanish or Galician voice
    const voices = window.speechSynthesis.getVoices();
    const esVoice = voices.find(v => v.lang.startsWith('es') || v.lang.startsWith('gl'));
    if (esVoice) {
      utterance.voice = esVoice;
    }

    utterance.onstart = () => {
      this.isSpeaking = true;
      this.notifySpeech(true);
    };

    utterance.onend = () => {
      this.isSpeaking = false;
      this.notifySpeech(false);
      if (onEnd) onEnd();
    };

    utterance.onerror = () => {
      this.isSpeaking = false;
      this.notifySpeech(false);
    };

    this.speechUtterance = utterance;
    window.speechSynthesis.speak(utterance);
  }

  public playMysticChime(mode: 'success' | 'magic' | 'click' = 'magic') {
    try {
      this.initAudio();
      if (!this.audioCtx) return;
      const now = this.audioCtx.currentTime;

      if (mode === 'success') {
        // Pentatonic Celtic chime chord: C5, E5, G5, C6
        [523.25, 659.25, 783.99, 1046.5].forEach((freq, i) => {
          if (!this.audioCtx) return;
          const osc = this.audioCtx.createOscillator();
          const gain = this.audioCtx.createGain();
          osc.type = 'sine';
          osc.frequency.setValueAtTime(freq, now + i * 0.08);

          gain.gain.setValueAtTime(0, now + i * 0.08);
          gain.gain.linearRampToValueAtTime(0.12, now + i * 0.08 + 0.03);
          gain.gain.exponentialRampToValueAtTime(0.001, now + i * 0.08 + 1.2);

          osc.connect(gain);
          gain.connect(this.audioCtx.destination);
          osc.start(now + i * 0.08);
          osc.stop(now + i * 0.08 + 1.3);
        });
      } else if (mode === 'magic') {
        // Shimmering mystical harp sweep
        [440, 554.37, 659.25, 880, 1108.73].forEach((freq, i) => {
          if (!this.audioCtx) return;
          const osc = this.audioCtx.createOscillator();
          const gain = this.audioCtx.createGain();
          osc.type = 'triangle';
          osc.frequency.setValueAtTime(freq, now + i * 0.06);

          gain.gain.setValueAtTime(0, now + i * 0.06);
          gain.gain.linearRampToValueAtTime(0.1, now + i * 0.06 + 0.02);
          gain.gain.exponentialRampToValueAtTime(0.001, now + i * 0.06 + 0.9);

          osc.connect(gain);
          gain.connect(this.audioCtx.destination);
          osc.start(now + i * 0.06);
          osc.stop(now + i * 0.06 + 1.0);
        });
      } else {
        // Gentle tactile click
        const osc = this.audioCtx.createOscillator();
        const gain = this.audioCtx.createGain();
        osc.type = 'sine';
        osc.frequency.setValueAtTime(800, now);
        gain.gain.setValueAtTime(0.06, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.08);
        osc.connect(gain);
        gain.connect(this.audioCtx.destination);
        osc.start(now);
        osc.stop(now + 0.09);
      }
    } catch {
      // AudioContext might be blocked until user gesture
    }
  }

  public stopSpeech() {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    this.isSpeaking = false;
    this.notifySpeech(false);
  }

  public getIsSpeaking(): boolean {
    return this.isSpeaking;
  }

  private notifyAmbient(val: boolean) {
    this.onAmbientChangeCallbacks.forEach(cb => cb(val));
  }

  private notifySpeech(val: boolean) {
    this.onSpeechChangeCallbacks.forEach(cb => cb(val));
  }
}

export const soundEngine = new SoundEngine();
