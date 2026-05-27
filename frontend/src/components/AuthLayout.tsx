import { Shield, TrendingUp, Zap } from 'lucide-react';
import type { ReactNode } from 'react';
import LiveTicker from './LiveTicker';

interface Props {
  children: ReactNode;
}

const TRUST_SIGNALS = [
  { Icon: Shield,     text: 'Bank-grade JWT authentication' },
  { Icon: TrendingUp, text: 'Live market data from Goldbroker API' },
  { Icon: Zap,        text: 'Instant trade execution' },
] as const;

export default function AuthLayout({ children }: Props) {
  return (
    <div className="min-h-screen flex bg-[#080810]">
      {/* ── Left hero panel (md+) ── */}
      <div
        className="hidden md:flex md:w-1/2 flex-col relative overflow-hidden"
        style={{
          backgroundImage: 'url(/auth-hero.jpg)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }}
      >
        {/* gradient overlay */}
        <div
          className="absolute inset-0"
          style={{
            background:
              'linear-gradient(135deg, rgba(8,8,16,0.78) 0%, rgba(8,8,16,0.94) 100%)',
          }}
        />

        <div className="relative z-10 flex flex-col h-full p-10">
          {/* Logo wordmark */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-amber-500 flex items-center justify-center flex-shrink-0">
              <Zap size={22} className="text-black" />
            </div>
            <span className="text-xl font-bold tracking-tight text-white">MetalPulse</span>
          </div>

          {/* Tagline */}
          <div className="flex-1 flex flex-col justify-center max-w-md">
            <h2
              className="font-bold leading-tight text-white mb-4"
              style={{ fontSize: 'clamp(2rem, 4vw, 3rem)' }}
            >
              The modern way to trade precious metals.
            </h2>
            <p className="text-slate-300 text-base leading-relaxed max-w-md">
              Real-time spot prices. Zero-commission paper trading. Start with $5,000.
            </p>
          </div>

          {/* Trust signals */}
          <div className="space-y-3 mb-8">
            {TRUST_SIGNALS.map(({ Icon, text }) => (
              <div key={text} className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-amber-500/15 border border-amber-500/20 flex items-center justify-center flex-shrink-0">
                  <Icon size={15} className="text-amber-400" />
                </div>
                <span className="text-slate-300 text-sm">{text}</span>
              </div>
            ))}
          </div>

          {/* Live ticker pill */}
          <LiveTicker />
        </div>
      </div>

      {/* ── Right form panel ── */}
      <div className="w-full md:w-1/2 flex items-center justify-center px-4 py-12 overflow-y-auto">
        <div className="w-full max-w-md">
          {/* Mobile-only logo */}
          <div className="flex items-center justify-center gap-3 mb-10 md:hidden">
            <div className="w-10 h-10 rounded-xl bg-amber-500 flex items-center justify-center">
              <Zap size={22} className="text-black" />
            </div>
            <span className="text-2xl font-bold tracking-tight text-white">MetalPulse</span>
          </div>

          {children}
        </div>
      </div>
    </div>
  );
}
