import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { TrendingUp, TrendingDown, Minus, Calculator, BarChart3, DollarSign, Percent } from 'lucide-react';
import { getPriceChange, calculateReturns } from '../api/analytics';
import { METALS, WEIGHT_UNITS, type Metal, type WeightUnit } from '../types';
import type { PriceAnalyticsDto, ReturnsDto } from '../types';

function fmt(n: number, currency = 'PKR') {
  return new Intl.NumberFormat('en-PK', { style: 'currency', currency, maximumFractionDigits: 2 }).format(n);
}

function fmtNum(n: number) {
  return new Intl.NumberFormat('en-PK', { maximumFractionDigits: 4 }).format(n);
}

const CURRENCIES = ['PKR', 'USD', 'EUR', 'GBP'];

function StatCard({ label, value, sub, color }: { label: string; value: string; sub?: string; color?: string }) {
  return (
    <div className="glass rounded-xl p-4">
      <p className="text-slate-500 text-xs uppercase tracking-wider mb-1">{label}</p>
      <p className={`text-xl font-bold ${color ?? 'text-white'}`}>{value}</p>
      {sub && <p className="text-slate-500 text-xs mt-0.5">{sub}</p>}
    </div>
  );
}

export default function Analytics() {
  const [metal,      setMetal]      = useState<Metal>('XAU');
  const [currency,   setCurrency]   = useState('PKR');
  const [weightUnit, setWeightUnit] = useState<WeightUnit>('g');

  // Price change
  const [fromDate, setFromDate] = useState('');
  const [toDate,   setToDate]   = useState(new Date().toISOString().slice(0, 10));
  const [priceResult, setPriceResult] = useState<PriceAnalyticsDto | null>(null);

  // Returns
  const [duration,    setDuration]    = useState('365');
  const [investment,  setInvestment]  = useState('');
  const [returnResult, setReturnResult] = useState<ReturnsDto | null>(null);

  const priceChangeMutation = useMutation({
    mutationFn: () => getPriceChange(metal, currency, weightUnit, fromDate, toDate),
    onSuccess: (data) => setPriceResult(data),
  });

  const returnsMutation = useMutation({
    mutationFn: () => calculateReturns(metal, currency, weightUnit, Number(duration), Number(investment)),
    onSuccess: (data) => setReturnResult(data),
  });

  const trendIcon = priceResult?.trend === 'UP'
    ? <TrendingUp size={20} className="text-emerald-400" />
    : priceResult?.trend === 'DOWN'
    ? <TrendingDown size={20} className="text-red-400" />
    : <Minus size={20} className="text-slate-400" />;

  const trendColor = priceResult?.trend === 'UP'
    ? 'text-emerald-400'
    : priceResult?.trend === 'DOWN'
    ? 'text-red-400'
    : 'text-slate-400';

  return (
    <div className="space-y-8 animate-fade-up">
      <div>
        <h1 className="text-2xl font-bold text-white">Analytics</h1>
        <p className="text-slate-400 text-sm mt-1">Price change analysis and investment returns calculator</p>
      </div>

      {/* Shared filters */}
      <div className="glass rounded-2xl p-5">
        <p className="text-slate-400 text-xs uppercase tracking-wider mb-3">Instrument</p>
        <div className="flex flex-wrap gap-3">
          {/* Metal */}
          <div className="flex gap-1.5">
            {METALS.map((m) => (
              <button
                key={m.symbol}
                onClick={() => { setMetal(m.symbol as Metal); setPriceResult(null); setReturnResult(null); }}
                className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium transition-all
                  ${metal === m.symbol ? 'text-black font-semibold' : 'glass text-slate-400 hover:text-white'}`}
                style={metal === m.symbol ? { backgroundColor: m.color } : undefined}
              >
                <span>{m.icon}</span>{m.name}
              </button>
            ))}
          </div>
          <select
            value={currency}
            onChange={(e) => setCurrency(e.target.value)}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-1.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
          >
            {CURRENCIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
          <select
            value={weightUnit}
            onChange={(e) => setWeightUnit(e.target.value as WeightUnit)}
            className="bg-white/5 border border-white/10 rounded-lg px-3 py-1.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
          >
            {WEIGHT_UNITS.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
        {/* Price Change */}
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <BarChart3 size={18} className="text-amber-400" />
            <h2 className="text-white font-semibold">Price Change Analysis</h2>
          </div>

          <div className="glass rounded-2xl p-5 space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">From</label>
                <input
                  type="date"
                  value={fromDate}
                  onChange={(e) => setFromDate(e.target.value)}
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-white text-sm focus:outline-none focus:border-amber-500/60 [color-scheme:dark]"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">To</label>
                <input
                  type="date"
                  value={toDate}
                  onChange={(e) => setToDate(e.target.value)}
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-white text-sm focus:outline-none focus:border-amber-500/60 [color-scheme:dark]"
                />
              </div>
            </div>

            <button
              onClick={() => {
                if (!fromDate || !toDate) { return; }
                priceChangeMutation.mutate();
              }}
              disabled={priceChangeMutation.isPending || !fromDate || !toDate}
              className="w-full py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-semibold text-sm transition-all disabled:opacity-40"
            >
              {priceChangeMutation.isPending ? 'Analysing…' : 'Analyse Price Change'}
            </button>

            {priceChangeMutation.isError && (
              <p className="text-red-400 text-sm text-center">
                {(priceChangeMutation.error as any)?.response?.data?.message ?? 'No data for this range'}
              </p>
            )}
          </div>

          {priceResult && (
            <div className="glass rounded-2xl p-5 space-y-4 border border-white/8">
              <div className="flex items-center gap-3">
                {trendIcon}
                <div>
                  <p className="text-white font-semibold">{priceResult.metalName}</p>
                  <p className="text-slate-500 text-xs">{priceResult.fromDate} → {priceResult.toDate}</p>
                </div>
                <span className={`ml-auto text-lg font-bold ${trendColor}`}>
                  {priceResult.trend}
                </span>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <StatCard label="Start Price"   value={fmt(priceResult.startPrice, currency)} />
                <StatCard label="End Price"     value={fmt(priceResult.endPrice, currency)} />
                <StatCard
                  label="Absolute Change"
                  value={`${priceResult.absoluteChange >= 0 ? '+' : ''}${fmt(priceResult.absoluteChange, currency)}`}
                  color={priceResult.absoluteChange >= 0 ? 'text-emerald-400' : 'text-red-400'}
                />
                <StatCard
                  label="% Change"
                  value={`${priceResult.percentageChange >= 0 ? '+' : ''}${Number(priceResult.percentageChange).toFixed(2)}%`}
                  color={priceResult.percentageChange >= 0 ? 'text-emerald-400' : 'text-red-400'}
                />
              </div>
            </div>
          )}
        </div>

        {/* Returns Calculator */}
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <Calculator size={18} className="text-amber-400" />
            <h2 className="text-white font-semibold">Returns Calculator</h2>
          </div>

          <div className="glass rounded-2xl p-5 space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Duration (days ago)
              </label>
              <div className="flex gap-2 mb-2">
                {[30, 90, 180, 365].map((d) => (
                  <button
                    key={d}
                    onClick={() => setDuration(String(d))}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all
                      ${duration === String(d)
                        ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                        : 'glass text-slate-400 hover:text-white'}`}
                  >
                    {d}d
                  </button>
                ))}
              </div>
              <input
                type="number"
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                min="1"
                placeholder="Days"
                className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2.5 text-white text-sm placeholder-slate-600 focus:outline-none focus:border-amber-500/60"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Investment Amount ({currency})
              </label>
              <input
                type="number"
                value={investment}
                onChange={(e) => setInvestment(e.target.value)}
                min="0.01"
                step="100"
                placeholder="e.g. 100000"
                className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-2.5 text-white text-sm placeholder-slate-600 focus:outline-none focus:border-amber-500/60"
              />
            </div>

            <button
              onClick={() => {
                if (!investment || !duration) return;
                returnsMutation.mutate();
              }}
              disabled={returnsMutation.isPending || !investment || !duration}
              className="w-full py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-semibold text-sm transition-all disabled:opacity-40"
            >
              {returnsMutation.isPending ? 'Calculating…' : 'Calculate Returns'}
            </button>

            {returnsMutation.isError && (
              <p className="text-red-400 text-sm text-center">
                {(returnsMutation.error as any)?.response?.data?.message ?? 'Calculation failed'}
              </p>
            )}
          </div>

          {returnResult && (
            <div className="glass rounded-2xl p-5 space-y-4 border border-white/8">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-amber-500/15 flex items-center justify-center">
                  <DollarSign size={18} className="text-amber-400" />
                </div>
                <div>
                  <p className="text-white font-semibold">{returnResult.metalName} — {returnResult.durationDays}d ago</p>
                  <p className="text-slate-500 text-xs">
                    Invested {fmt(returnResult.investmentAmount, currency)}
                  </p>
                </div>
                <span className={`ml-auto flex items-center gap-1 text-lg font-bold
                  ${returnResult.returnPercentage >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                  <Percent size={16} />
                  {returnResult.returnPercentage >= 0 ? '+' : ''}
                  {Number(returnResult.returnPercentage).toFixed(2)}%
                </span>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <StatCard label="Price Then"     value={fmt(returnResult.priceAtInvestment, currency)} />
                <StatCard label="Price Now"      value={fmt(returnResult.currentPrice, currency)} />
                <StatCard
                  label="Current Value"
                  value={fmt(returnResult.currentValue, currency)}
                  color="text-amber-400"
                />
                <StatCard
                  label="Profit / Loss"
                  value={`${returnResult.profitLoss >= 0 ? '+' : ''}${fmt(returnResult.profitLoss, currency)}`}
                  color={returnResult.profitLoss >= 0 ? 'text-emerald-400' : 'text-red-400'}
                />
              </div>
              <div className="glass rounded-xl p-3 text-center">
                <p className="text-slate-400 text-xs">Quantity that could be purchased</p>
                <p className="text-white font-bold mt-0.5">
                  {fmtNum(returnResult.quantityPurchased)} {returnResult.weightUnit}
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
