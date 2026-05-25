import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  ResponsiveContainer, AreaChart, Area, XAxis, YAxis, CartesianGrid,
  Tooltip,
} from 'recharts';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { getSpotPrice, getFullHistory } from '../api/metals';
import { METALS, WEIGHT_UNITS, type Metal, type WeightUnit } from '../types';

function fmt(n: number) {
  return new Intl.NumberFormat('en-PK', { maximumFractionDigits: 2 }).format(n);
}

const CURRENCIES = ['PKR', 'USD', 'EUR', 'GBP'];

export default function Markets() {
  const [activeMetal,  setActiveMetal]  = useState<Metal>('XAU');
  const [currency,     setCurrency]     = useState('PKR');
  const [weightUnit,   setWeightUnit]   = useState<WeightUnit>('g');

  const meta = METALS.find((m) => m.symbol === activeMetal)!;

  const { data: spot } = useQuery({
    queryKey: ['spot', activeMetal, currency, weightUnit],
    queryFn: () => getSpotPrice(activeMetal, currency, weightUnit),
    refetchInterval: 60_000,
  });

  const { data: history, isLoading } = useQuery({
    queryKey: ['fullHistory', activeMetal, currency, weightUnit],
    queryFn:  () => getFullHistory(activeMetal, currency, weightUnit),
  });

  const positive = (spot?.changePercent ?? 0) >= 0;
  const stable   = Math.abs(spot?.changePercent ?? 0) < 0.01;

  const CustomTooltip = ({ active, payload, label }: any) => {
    if (!active || !payload?.length) return null;
    return (
      <div className="bg-[#0d0d1a] border border-white/10 rounded-xl px-4 py-3 shadow-2xl">
        <p className="text-slate-400 text-xs mb-1">{label}</p>
        <p className="text-white font-bold">{currency} {fmt(payload[0].value)}</p>
      </div>
    );
  };

  const chartData = (history ?? []).slice(-180).map((h) => ({
    date:  h.date.slice(0, 10),
    price: Number(h.price),
  }));

  return (
    <div className="space-y-6 animate-fade-up">
      <div>
        <h1 className="text-2xl font-bold text-white">Markets</h1>
        <p className="text-slate-400 text-sm mt-1">Historical price charts for precious metals</p>
      </div>

      {/* Metal tabs */}
      <div className="flex flex-wrap gap-2">
        {METALS.map((m) => (
          <button
            key={m.symbol}
            onClick={() => setActiveMetal(m.symbol as Metal)}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium transition-all
              ${activeMetal === m.symbol
                ? 'text-black font-semibold'
                : 'glass text-slate-400 hover:text-white'}`}
            style={activeMetal === m.symbol ? { backgroundColor: m.color } : undefined}
          >
            <span>{m.icon}</span>
            {m.name}
          </button>
        ))}
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3">
        <div className="flex items-center gap-2">
          <label className="text-slate-500 text-xs uppercase tracking-wider">Currency</label>
          <select
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-1.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
          >
            {CURRENCIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
        <div className="flex items-center gap-2">
          <label className="text-slate-500 text-xs uppercase tracking-wider">Unit</label>
          <select
            value={weightUnit}
            onChange={(e) => setWeightUnit(e.target.value as WeightUnit)}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-1.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
          >
            {WEIGHT_UNITS.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
          </select>
        </div>
      </div>

      {/* Spot price banner */}
      <div
        className="glass rounded-2xl p-6 border"
        style={{ borderColor: `${meta.color}30` }}
      >
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <div
              className="w-14 h-14 rounded-2xl flex items-center justify-center text-3xl"
              style={{ backgroundColor: `${meta.color}20` }}
            >
              {meta.icon}
            </div>
            <div>
              <p className="text-slate-400 text-sm">{meta.name} / {currency}</p>
              <p className="text-4xl font-bold text-white mt-1 tracking-tight">
                {spot ? fmt(spot.price) : '—'}
              </p>
            </div>
          </div>

          <div className="flex gap-6 text-sm">
            <div>
              <p className="text-slate-500 text-xs">Change</p>
              <p className={`font-semibold mt-0.5 ${positive ? 'text-emerald-400' : stable ? 'text-slate-400' : 'text-red-400'}`}>
                {spot ? `${positive ? '+' : ''}${fmt(spot.change)}` : '—'}
              </p>
            </div>
            <div>
              <p className="text-slate-500 text-xs">Change %</p>
              <span className={`flex items-center gap-1 font-semibold mt-0.5
                ${stable   ? 'text-slate-400'
                : positive ? 'text-emerald-400'
                           : 'text-red-400'}`}>
                {stable ? <Minus size={12} /> : positive ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
                {Math.abs(spot?.changePercent ?? 0).toFixed(2)}%
              </span>
            </div>
            <div>
              <p className="text-slate-500 text-xs">Bid</p>
              <p className="text-white font-semibold mt-0.5">{spot ? fmt(spot.bid) : '—'}</p>
            </div>
            <div>
              <p className="text-slate-500 text-xs">Ask</p>
              <p className="text-white font-semibold mt-0.5">{spot ? fmt(spot.ask) : '—'}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Chart */}
      <div className="glass rounded-2xl p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-white font-semibold">Price History</h2>
            <p className="text-slate-500 text-xs mt-0.5">Last 180 data points · per {weightUnit} in {currency}</p>
          </div>
        </div>

        {isLoading ? (
          <div className="skeleton h-80 w-full rounded-xl" />
        ) : chartData.length === 0 ? (
          <div className="h-80 flex items-center justify-center text-slate-500">No data available</div>
        ) : (
          <ResponsiveContainer width="100%" height={320}>
            <AreaChart data={chartData} margin={{ top: 5, right: 5, left: 10, bottom: 5 }}>
              <defs>
                <linearGradient id="priceGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%"  stopColor={meta.color} stopOpacity={0.25} />
                  <stop offset="95%" stopColor={meta.color} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
              <XAxis
                dataKey="date"
                tick={{ fill: '#64748b', fontSize: 11 }}
                axisLine={false}
                tickLine={false}
                interval="preserveStartEnd"
                tickFormatter={(v) => v.slice(5)}
              />
              <YAxis
                tick={{ fill: '#64748b', fontSize: 11 }}
                axisLine={false}
                tickLine={false}
                tickFormatter={(v) => fmt(v)}
                width={70}
              />
              <Tooltip content={<CustomTooltip />} />
              <Area
                type="monotone"
                dataKey="price"
                stroke={meta.color}
                strokeWidth={2}
                fill="url(#priceGradient)"
                dot={false}
                activeDot={{ r: 5, fill: meta.color, strokeWidth: 0 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}
