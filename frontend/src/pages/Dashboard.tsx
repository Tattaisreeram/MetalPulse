import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { TrendingUp, TrendingDown, Minus, Wallet, RefreshCw, Activity } from 'lucide-react';
import { getSpotPrice, getFullHistory } from '../api/metals';
import { getBalance } from '../api/trades';
import { METALS } from '../types';
import SparklineChart from '../components/SparklineChart';
import { useAuth } from '../context/AuthContext';

const CURRENCY_LOCALE: Record<string, string> = {
  PKR: 'en-PK',
  USD: 'en-US',
  EUR: 'de-DE',
  GBP: 'en-GB',
};

function fmt(n: number, currency = 'PKR') {
  const locale = CURRENCY_LOCALE[currency] ?? 'en-US';
  return new Intl.NumberFormat(locale, { style: 'currency', currency, maximumFractionDigits: 2 }).format(n);
}

function MetalCard({ metal, currency, weightUnit }: { metal: typeof METALS[0]; currency: string; weightUnit: string }) {
  const { data: spot, isLoading: loadingSpot } = useQuery({
    queryKey: ['spot', metal.symbol, currency, weightUnit],
    queryFn: () => getSpotPrice(metal.symbol, currency, weightUnit),
    refetchInterval: 60_000,
  });

  const { data: history } = useQuery({
    queryKey: ['history', metal.symbol, currency, weightUnit],
    queryFn: () => getFullHistory(metal.symbol, currency, weightUnit),
  });

  // Use last historical close vs current spot for a metal-specific daily change,
  // because Goldbroker's `performance` field reflects PKR currency shift (same for all metals).
  const prevClose  = history && history.length > 0 ? history[history.length - 1].price : null;
  const trueChange = prevClose !== null ? (spot?.price ?? 0) - prevClose : (spot?.change ?? 0);
  const trueChangePercent = prevClose && prevClose > 0
    ? ((spot?.price ?? 0) - prevClose) / prevClose * 100
    : (spot?.changePercent ?? 0);

  const positive = trueChange >= 0;
  const stable   = Math.abs(trueChangePercent) < 0.01;

  return (
    <div className="relative glass rounded-2xl overflow-hidden flex flex-col gap-4 hover:border-white/15 transition-all duration-300">
      {/* Card background image */}
      <div
        className="absolute inset-0 bg-cover bg-center opacity-20"
        style={{ backgroundImage: `url(${metal.cardImage})` }}
      />
      <div className="absolute inset-0 bg-gradient-to-b from-[#080810]/60 via-[#080810]/70 to-[#080810]/90" />

      <div className="relative z-10 p-5 flex flex-col gap-4">
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div
            className="w-10 h-10 rounded-xl flex items-center justify-center text-xl"
            style={{ backgroundColor: `${metal.color}20`, border: `1px solid ${metal.color}30` }}
          >
            {metal.icon}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <p className="text-white font-semibold text-sm">{metal.name}</p>
              {metal.symbol === 'XAU' && (
                <span className="text-xs font-bold px-1.5 py-0.5 rounded-md bg-amber-500/15 text-amber-400 border border-amber-500/30">
                  24K
                </span>
              )}
            </div>
            <p className="text-slate-500 text-xs">{metal.symbol}</p>
          </div>
        </div>

        {loadingSpot ? (
          <div className="skeleton w-20 h-5 rounded" />
        ) : (
          <span
            className={`flex items-center gap-1 text-xs font-semibold px-2 py-1 rounded-full
              ${stable   ? 'bg-slate-500/15 text-slate-400'
              : positive ? 'bg-emerald-500/15 text-emerald-400'
                         : 'bg-red-500/15 text-red-400'}`}
          >
            {stable ? <Minus size={12} /> : positive ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
            {Math.abs(trueChangePercent).toFixed(2)}%
          </span>
        )}
      </div>

      {/* Price */}
      {loadingSpot ? (
        <div className="skeleton w-36 h-7 rounded" />
      ) : (
        <div>
          <p className="text-2xl font-bold text-white tracking-tight">
            {fmt(spot?.price ?? 0, currency)}
          </p>
          <p className={`text-xs mt-0.5 ${positive ? 'text-emerald-400' : 'text-red-400'}`}>
            {positive ? '+' : ''}{fmt(trueChange, currency)} today
          </p>
        </div>
      )}

      {/* Sparkline */}
      {history && history.length > 0 && (
        <SparklineChart data={history} color={metal.color} positive={positive} />
      )}

      <div className="flex justify-between text-xs text-slate-500 border-t border-white/5 pt-3">
        <span>Bid: <span className="text-slate-300">{spot ? fmt(spot.bid, currency) : '–'}</span></span>
        <span>Ask: <span className="text-slate-300">{spot ? fmt(spot.ask, currency) : '–'}</span></span>
      </div>
      </div> {/* z-10 content wrapper */}
    </div>
  );
}

const CURRENCIES: { value: string; label: string; sub: string }[] = [
  { value: 'PKR', label: 'PKR', sub: 'Pakistani Rupee' },
  { value: 'USD', label: 'USD', sub: 'US Dollar' },
  { value: 'EUR', label: 'EUR', sub: 'Euro' },
  { value: 'GBP', label: 'GBP', sub: 'British Pound' },
];

export default function Dashboard() {
  const { user } = useAuth();
  const [currency, setCurrency] = useState('PKR');
  const weightUnit = 'g';

  const { data: balance, isLoading: loadingBalance } = useQuery({
    queryKey: ['balance'],
    queryFn: getBalance,
    refetchInterval: 30_000,
  });

  const { data: fxRates } = useQuery<Record<string, number>>({
    queryKey: ['fxRates'],
    queryFn: async () => {
      const res = await fetch('https://open.er-api.com/v6/latest/USD');
      const json = await res.json();
      return { USD: 1, ...json.rates } as Record<string, number>;
    },
    staleTime: 60 * 60 * 1000,
    refetchInterval: 60 * 60 * 1000,
  });

  const convertedBalance = (balance?.balance ?? 0) * (fxRates?.[currency] ?? 1);

  const hour = new Date().getHours();
  const greeting = hour < 12 ? 'Good morning' : hour < 18 ? 'Good afternoon' : 'Good evening';

  return (
    <div className="space-y-8 animate-fade-up">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">
            {greeting}, {user?.username} 👋
          </h1>
          <p className="text-slate-400 text-sm mt-1">Here's your metals overview</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1 p-1 glass rounded-xl">
            {CURRENCIES.map((c) => (
              <button
                key={c.value}
                type="button"
                onClick={() => setCurrency(c.value)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all
                  ${currency === c.value
                    ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                    : 'text-slate-400 hover:text-white'}`}
              >
                {c.label}
              </button>
            ))}
          </div>
          <div className="flex items-center gap-2 text-xs text-slate-500">
            <Activity size={14} className="text-amber-500" />
            Live via Goldbroker
          </div>
        </div>
      </div>

      {/* Balance card */}
      <div className="relative glass rounded-2xl overflow-hidden flex flex-col sm:flex-row sm:items-center justify-between gap-4 border border-amber-500/10 gold-glow">
        <div className="absolute inset-0 bg-cover bg-center opacity-15" style={{ backgroundImage: 'url(/dashboard-hero.jpg)' }} />
        <div className="absolute inset-0 bg-gradient-to-r from-[#080810]/90 via-[#080810]/75 to-[#080810]/60" />
        <div className="relative z-10 w-full flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-6">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 rounded-2xl bg-amber-500/15 border border-amber-500/30 flex items-center justify-center">
            <Wallet size={22} className="text-amber-400" />
          </div>
          <div>
            <p className="text-slate-400 text-xs uppercase tracking-wider">Account Balance</p>
            {loadingBalance ? (
              <div className="skeleton w-40 h-8 rounded mt-1" />
            ) : (
              <p className="text-3xl font-bold text-white mt-0.5">
                {fmt(convertedBalance, currency)}
              </p>
            )}
          </div>
        </div>
        <div className="flex gap-3">
          <a
            href="/portfolio"
            className="px-4 py-2 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-400 text-sm font-medium hover:bg-amber-500/20 transition-all"
          >
            Deposit
          </a>
          <a
            href="/trade"
            className="px-4 py-2 rounded-xl bg-amber-500 text-black text-sm font-semibold hover:bg-amber-400 transition-all"
          >
            Trade Now
          </a>
        </div>
        </div> {/* z-10 content wrapper */}
      </div>

      {/* Metal cards */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-white font-semibold">Live Spot Prices</h2>
          <span className="flex items-center gap-1.5 text-xs text-slate-500">
            <RefreshCw size={12} className="animate-spin [animation-duration:3s]" />
            Updates every 60s
          </span>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          {METALS.map((m) => (
            <MetalCard key={m.symbol} metal={m} currency={currency} weightUnit={weightUnit} />
          ))}
        </div>
      </div>

      {/* Quick stats row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          { label: 'Metals Tracked', value: '4',         sub: 'Gold · Silver · Pt · Pd' },
          { label: 'Data Source',    value: 'Live',       sub: 'Goldbroker API' },
          { label: 'Currency',       value: currency,     sub: CURRENCIES.find((c) => c.value === currency)?.sub ?? '' },
          { label: 'Weight Unit',    value: 'g',          sub: 'per gram' },
        ].map((s) => (
          <div key={s.label} className="glass rounded-xl p-4">
            <p className="text-slate-500 text-xs uppercase tracking-wider">{s.label}</p>
            <p className="text-white font-bold text-xl mt-1">{s.value}</p>
            <p className="text-slate-500 text-xs mt-0.5">{s.sub}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
