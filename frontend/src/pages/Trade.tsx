import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ShoppingCart, TrendingDown, Bookmark, AlertCircle, CheckCircle2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { getSpotPrice } from '../api/metals';
import { buy, sell, hold, getBalance } from '../api/trades';
import { METALS, WEIGHT_UNITS, type Metal, type WeightUnit } from '../types';

type TradeType = 'BUY' | 'SELL' | 'HOLD';

const TABS: { type: TradeType; label: string; icon: typeof ShoppingCart; color: string }[] = [
  { type: 'BUY',  label: 'Buy',  icon: ShoppingCart, color: 'emerald' },
  { type: 'SELL', label: 'Sell', icon: TrendingDown,  color: 'red' },
  { type: 'HOLD', label: 'Hold', icon: Bookmark,      color: 'blue' },
];

const CURRENCIES = ['PKR', 'USD', 'EUR', 'GBP'];

// Purity ratios: 24K = 99.9% pure (benchmark), 22K = 22/24 of spot price
const GOLD_PURITIES = [
  { label: '24K', sublabel: '99.9% Pure', ratio: 1,        badge: 'bg-amber-500/20 text-amber-300 border-amber-500/40' },
  { label: '22K', sublabel: '91.67% Pure', ratio: 22 / 24, badge: 'bg-yellow-600/20 text-yellow-400 border-yellow-600/40' },
];

const CURRENCY_LOCALE: Record<string, string> = { PKR: 'en-PK', USD: 'en-US', EUR: 'de-DE', GBP: 'en-GB' };

function fmt(n: number, currency = 'USD') {
  const locale = CURRENCY_LOCALE[currency] ?? 'en-US';
  return new Intl.NumberFormat(locale, { style: 'currency', currency, maximumFractionDigits: 2 }).format(n);
}

export default function Trade() {
  const qc = useQueryClient();
  const [tradeType,  setTradeType]  = useState<TradeType>('BUY');
  const [metal,      setMetal]      = useState<Metal>('XAU');
  const [currency,   setCurrency]   = useState('USD');
  const [weightUnit, setWeightUnit] = useState<WeightUnit>('g');
  const [quantity,   setQuantity]   = useState('');
  const [purityIdx,  setPurityIdx]  = useState(0);
  const [lastTrade,  setLastTrade]  = useState<any>(null);

  const meta        = METALS.find((m) => m.symbol === metal)!;
  const isGold      = metal === 'XAU';
  const purity      = GOLD_PURITIES[purityIdx];
  const purityRatio = isGold ? purity.ratio : 1;

  const { data: spot, isLoading: loadingSpot } = useQuery({
    queryKey: ['spot', metal, currency, weightUnit],
    queryFn:  () => getSpotPrice(metal, currency, weightUnit),
    refetchInterval: 60_000,
  });

  const { data: balance } = useQuery({
    queryKey: ['balance'],
    queryFn:  getBalance,
  });

  const { data: fxRates } = useQuery<Record<string, number>>({
    queryKey: ['fxRates'],
    queryFn: async () => {
      const res = await fetch('https://open.er-api.com/v6/latest/USD');
      const json = await res.json();
      return { USD: 1, ...json.rates } as Record<string, number>;
    },
    staleTime: 60 * 60 * 1000,
  });

  const convertedBalance = (balance?.balance ?? 0) * (fxRates?.[currency] ?? 1);
  const adjustedPrice    = spot ? spot.price * purityRatio : 0;
  const totalCost        = adjustedPrice * Number(quantity || 0);
  const canAfford        = tradeType !== 'BUY' || convertedBalance >= totalCost;

  const mutationFn = tradeType === 'BUY' ? buy : tradeType === 'SELL' ? sell : hold;

  const { mutate, isPending } = useMutation({
    mutationFn: () => mutationFn({ metal, currency, weightUnit, quantity: Number(quantity) }),
    onSuccess: (res) => {
      setLastTrade({ ...res.data, purityLabel: isGold ? purity.label : null });
      setQuantity('');
      qc.invalidateQueries({ queryKey: ['balance'] });
      qc.invalidateQueries({ queryKey: ['tradeHistory'] });
      toast.success(`${tradeType === 'BUY' ? 'Bought' : tradeType === 'SELL' ? 'Sold' : 'Hold recorded'} successfully!`);
    },
    onError: (err: any) => toast.error(err?.response?.data?.message ?? 'Trade failed'),
  });

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!quantity || Number(quantity) <= 0) { toast.error('Enter a valid quantity'); return; }
    mutate();
  }

  const activeTab = TABS.find((t) => t.type === tradeType)!;

  return (
    <div className="space-y-6 animate-fade-up max-w-2xl mx-auto">
      <div>
        <h1 className="text-2xl font-bold text-white">Trade</h1>
        <p className="text-slate-400 text-sm mt-1">Buy, sell, or hold precious metals at live prices</p>
      </div>

      {/* Trade type tabs */}
      <div className="flex gap-2 p-1 glass rounded-2xl">
        {TABS.map(({ type, label, icon: Icon, color }) => (
          <button
            key={type}
            type="button"
            onClick={() => { setTradeType(type); setLastTrade(null); }}
            className={`flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-sm font-semibold transition-all
              ${tradeType === type
                ? color === 'emerald' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                : color === 'red'     ? 'bg-red-500/20 text-red-400 border border-red-500/30'
                :                      'bg-blue-500/20 text-blue-400 border border-blue-500/30'
                : 'text-slate-400 hover:text-white'}`}
          >
            <Icon size={16} />
            {label}
          </button>
        ))}
      </div>

      {/* Success banner */}
      {lastTrade && (
        <div className="flex items-start gap-3 p-4 glass rounded-xl border border-emerald-500/20 bg-emerald-500/5">
          <CheckCircle2 size={18} className="text-emerald-400 mt-0.5 shrink-0" />
          <div className="text-sm">
            <p className="text-emerald-400 font-semibold">Trade executed!</p>
            <p className="text-slate-400 mt-0.5">
              {lastTrade.tradeType} {lastTrade.quantity} {lastTrade.weightUnit} of{' '}
              {lastTrade.metalName}
              {lastTrade.purityLabel && <span className="ml-1 text-amber-400 font-medium">({lastTrade.purityLabel})</span>}
              {' '}· Total: {fmt(lastTrade.totalAmount, lastTrade.currency)}
            </p>
          </div>
        </div>
      )}

      {/* Form */}
      <form onSubmit={handleSubmit} className="glass rounded-2xl p-6 space-y-5">

        {/* Metal select */}
        <div>
          <label className="block text-xs font-medium text-slate-400 mb-3 uppercase tracking-wider">
            Select Metal
          </label>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {METALS.map((m) => (
              <button
                key={m.symbol}
                type="button"
                onClick={() => { setMetal(m.symbol as Metal); setPurityIdx(0); }}
                className={`flex items-center gap-2 px-3 py-2.5 rounded-xl text-sm font-medium border transition-all
                  ${metal === m.symbol
                    ? 'border-amber-500/50 bg-amber-500/10 text-amber-300'
                    : 'border-white/8 bg-white/3 text-slate-400 hover:text-white hover:border-white/15'}`}
              >
                <span className="text-base">{m.icon}</span>
                <span>{m.name}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Gold purity selector — only shown for XAU */}
        {isGold && (
          <div>
            <label className="block text-xs font-medium text-slate-400 mb-2 uppercase tracking-wider">
              Gold Purity
            </label>
            <div className="flex gap-3">
              {GOLD_PURITIES.map((p, i) => (
                <button
                  key={p.label}
                  type="button"
                  onClick={() => setPurityIdx(i)}
                  className={`flex-1 flex flex-col items-center py-3 px-4 rounded-xl border text-sm font-semibold transition-all
                    ${purityIdx === i
                      ? p.badge
                      : 'border-white/8 bg-white/3 text-slate-400 hover:text-white hover:border-white/15'}`}
                >
                  <span className="text-lg font-bold">{p.label}</span>
                  <span className="text-xs font-normal opacity-70 mt-0.5">{p.sublabel}</span>
                </button>
              ))}
            </div>
            <p className="text-slate-500 text-xs mt-2">
              {purityIdx === 0
                ? '24K is pure investment-grade gold — the benchmark spot price.'
                : '22K is jewellery-grade gold at 91.67% purity. Price is adjusted accordingly.'}
            </p>
          </div>
        )}

        {/* Currency + Weight */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">Currency</label>
            <select
              value={currency}
              onChange={(e) => setCurrency(e.target.value)}
              className="w-full bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
            >
              {CURRENCIES.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">Unit</label>
            <select
              value={weightUnit}
              onChange={(e) => setWeightUnit(e.target.value as WeightUnit)}
              className="w-full bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
            >
              {WEIGHT_UNITS.map((u) => <option key={u.value} value={u.value}>{u.label}</option>)}
            </select>
          </div>
        </div>

        {/* Live price */}
        <div className="rounded-xl bg-white/3 border border-white/8 overflow-hidden">
          <div className="flex items-center justify-between px-4 py-3">
            <span className="text-slate-400 text-sm">
              {meta.icon} {meta.name}
              {isGold && <span className="ml-2 text-xs font-semibold text-amber-400">{purity.label}</span>}
              {' '}spot price
            </span>
            {loadingSpot ? (
              <div className="skeleton w-28 h-5 rounded" />
            ) : (
              <span className="text-white font-bold">
                {spot ? fmt(adjustedPrice, currency) : '—'} / {weightUnit}
              </span>
            )}
          </div>
          {isGold && purityIdx === 1 && spot && (
            <div className="px-4 pb-3 flex items-center justify-between text-xs text-slate-500 border-t border-white/5 pt-2">
              <span>24K base: {fmt(spot.price, currency)}</span>
              <span>× 22/24 = <span className="text-amber-400 font-medium">{fmt(adjustedPrice, currency)}</span></span>
            </div>
          )}
        </div>

        {/* Quantity input */}
        <div>
          <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
            Quantity ({weightUnit})
          </label>
          <input
            type="number"
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
            min="0.001"
            step="any"
            placeholder="0.00"
            className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white text-lg placeholder-slate-600 focus:outline-none focus:border-amber-500/60 focus:bg-amber-500/5 transition-all"
          />
        </div>

        {/* Total cost */}
        {quantity && Number(quantity) > 0 && (
          <div className={`flex items-center justify-between p-4 rounded-xl border
            ${!canAfford && tradeType === 'BUY' ? 'bg-red-500/5 border-red-500/20' : 'bg-amber-500/5 border-amber-500/20'}`}>
            <div>
              <p className="text-slate-400 text-sm">Estimated total</p>
              {isGold && <p className="text-slate-500 text-xs mt-0.5">{purity.label} · {purity.sublabel}</p>}
            </div>
            <div className="text-right">
              <p className={`text-lg font-bold ${!canAfford && tradeType === 'BUY' ? 'text-red-400' : 'text-amber-400'}`}>
                {fmt(totalCost, currency)}
              </p>
              {tradeType === 'BUY' && (
                <p className="text-slate-500 text-xs mt-0.5">
                  Balance: {fmt(convertedBalance, currency)}
                </p>
              )}
            </div>
          </div>
        )}

        {!canAfford && tradeType === 'BUY' && quantity && (
          <div className="flex items-center gap-2 text-red-400 text-sm">
            <AlertCircle size={16} />
            Insufficient balance. Please deposit funds first.
          </div>
        )}

        <button
          type="submit"
          disabled={isPending || (tradeType === 'BUY' && !canAfford)}
          className={`w-full py-3.5 rounded-xl font-semibold text-sm transition-all disabled:opacity-40 disabled:cursor-not-allowed
            ${tradeType === 'BUY'  ? 'bg-emerald-500 hover:bg-emerald-400 text-white'
            : tradeType === 'SELL' ? 'bg-red-500 hover:bg-red-400 text-white'
            :                        'bg-blue-500 hover:bg-blue-400 text-white'}`}
        >
          {isPending
            ? 'Processing…'
            : `${activeTab.label} ${quantity || '0'} ${weightUnit} of ${meta.name}${isGold ? ` (${purity.label})` : ''}`}
        </button>
      </form>
    </div>
  );
}
