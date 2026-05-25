import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Wallet, ArrowDownLeft, ArrowUpRight, Clock } from 'lucide-react';
import toast from 'react-hot-toast';
import { getBalance, deposit, withdraw, getTradeHistory } from '../api/trades';

const CURRENCY_LOCALE: Record<string, string> = { PKR: 'en-PK', USD: 'en-US', EUR: 'de-DE', GBP: 'en-GB' };

function fmt(n: number, currency = 'USD') {
  const locale = CURRENCY_LOCALE[currency] ?? 'en-US';
  return new Intl.NumberFormat(locale, { style: 'currency', currency, maximumFractionDigits: 2 }).format(n);
}

function fmtDate(s: string) {
  return new Date(s).toLocaleDateString('en-PK', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

const TRADE_TYPE_STYLES: Record<string, { cls: string; label: string }> = {
  BUY:      { cls: 'text-emerald-400 bg-emerald-500/10',  label: 'BUY' },
  SELL:     { cls: 'text-red-400 bg-red-500/10',          label: 'SELL' },
  HOLD:     { cls: 'text-blue-400 bg-blue-500/10',        label: 'HOLD' },
  DEPOSIT:  { cls: 'text-teal-400 bg-teal-500/10',        label: 'DEPOSIT' },
  WITHDRAW: { cls: 'text-orange-400 bg-orange-500/10',    label: 'WITHDRAW' },
  BONUS:    { cls: 'text-amber-400 bg-amber-500/15 border border-amber-500/30', label: '🎁 BONUS' },
};

export default function Portfolio() {
  const qc = useQueryClient();
  const [amount,   setAmount]   = useState('');
  const [currency, setCurrency] = useState('PKR');
  const [page,     setPage]     = useState(0);

  const { data: balance, isLoading: loadingBalance } = useQuery({
    queryKey: ['balance'],
    queryFn:  getBalance,
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
  });

  const convertedBalance = (balance?.balance ?? 0) * (fxRates?.[currency] ?? 1);

  const { data: trades, isLoading: loadingTrades } = useQuery({
    queryKey: ['tradeHistory', page],
    queryFn:  () => getTradeHistory(page, 20),
  });

  const depositMutation = useMutation({
    mutationFn: () => deposit({ amount: Number(amount), currency }),
    onSuccess:  (res) => {
      setAmount('');
      qc.invalidateQueries({ queryKey: ['balance'] });
      toast.success(`Deposited ${fmt(res.data.balance, currency)} balance updated!`);
    },
    onError: (err: any) => toast.error(err?.response?.data?.message ?? 'Deposit failed'),
  });

  const withdrawMutation = useMutation({
    mutationFn: () => withdraw({ amount: Number(amount), currency }),
    onSuccess:  () => {
      setAmount('');
      qc.invalidateQueries({ queryKey: ['balance'] });
      toast.success('Withdrawal successful!');
    },
    onError: (err: any) => toast.error(err?.response?.data?.message ?? 'Withdrawal failed'),
  });

  return (
    <div className="space-y-6 animate-fade-up">
      <div>
        <h1 className="text-2xl font-bold text-white">Portfolio</h1>
        <p className="text-slate-400 text-sm mt-1">Manage your balance and view trade history</p>
      </div>

      {/* Balance + deposit/withdraw */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Balance */}
        <div className="glass rounded-2xl p-6 border border-amber-500/10 gold-glow">
          <div className="flex items-center gap-3 mb-5">
            <div className="w-10 h-10 rounded-xl bg-amber-500/15 flex items-center justify-center">
              <Wallet size={20} className="text-amber-400" />
            </div>
            <div>
              <p className="text-slate-400 text-xs uppercase tracking-wider">Total Balance</p>
              <p className="text-white font-semibold text-sm">{balance?.username ?? '—'}</p>
            </div>
          </div>
          {loadingBalance ? (
            <div className="skeleton w-48 h-10 rounded" />
          ) : (
            <p className="text-4xl font-bold text-white tracking-tight">
              {fmt(convertedBalance, currency)}
            </p>
          )}
          <p className="text-slate-500 text-xs mt-2">Available to trade</p>
        </div>

        {/* Deposit / Withdraw */}
        <div className="glass rounded-2xl p-6">
          <h3 className="text-white font-semibold mb-4">Funds</h3>
          <div className="flex gap-3 mb-4">
            <select
              value={currency}
              onChange={(e) => setCurrency(e.target.value)}
              className="bg-white/5 border border-white/10 rounded-xl px-3 py-2.5 text-white text-sm focus:outline-none focus:border-amber-500/60"
            >
              {['PKR','USD','EUR','GBP'].map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="Amount"
              min="0.01"
              className="flex-1 bg-white/5 border border-white/10 rounded-xl px-4 py-2.5 text-white placeholder-slate-600 focus:outline-none focus:border-amber-500/60 transition-all"
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <button
              onClick={() => { if (!amount) { toast.error('Enter an amount'); return; } depositMutation.mutate(); }}
              disabled={depositMutation.isPending}
              className="flex items-center justify-center gap-2 py-2.5 rounded-xl bg-emerald-500/15 border border-emerald-500/25 text-emerald-400 font-semibold text-sm hover:bg-emerald-500/25 transition-all disabled:opacity-40"
            >
              <ArrowDownLeft size={16} />
              {depositMutation.isPending ? 'Processing…' : 'Deposit'}
            </button>
            <button
              onClick={() => { if (!amount) { toast.error('Enter an amount'); return; } withdrawMutation.mutate(); }}
              disabled={withdrawMutation.isPending}
              className="flex items-center justify-center gap-2 py-2.5 rounded-xl bg-red-500/15 border border-red-500/25 text-red-400 font-semibold text-sm hover:bg-red-500/25 transition-all disabled:opacity-40"
            >
              <ArrowUpRight size={16} />
              {withdrawMutation.isPending ? 'Processing…' : 'Withdraw'}
            </button>
          </div>
        </div>
      </div>

      {/* Trade history */}
      <div className="glass rounded-2xl overflow-hidden">
        <div className="flex items-center justify-between px-6 py-4 border-b border-white/5">
          <div className="flex items-center gap-2">
            <Clock size={16} className="text-slate-400" />
            <h2 className="text-white font-semibold">Trade History</h2>
          </div>
          <div className="flex items-center gap-2">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => p - 1)}
              className="px-3 py-1 rounded-lg bg-white/5 text-slate-400 hover:text-white disabled:opacity-30 text-sm transition-all"
            >
              ← Prev
            </button>
            <span className="text-slate-500 text-sm">Page {page + 1}</span>
            <button
              disabled={(trades?.length ?? 0) < 20}
              onClick={() => setPage((p) => p + 1)}
              className="px-3 py-1 rounded-lg bg-white/5 text-slate-400 hover:text-white disabled:opacity-30 text-sm transition-all"
            >
              Next →
            </button>
          </div>
        </div>

        {loadingTrades ? (
          <div className="p-6 space-y-3">
            {[...Array(5)].map((_, i) => <div key={i} className="skeleton h-14 w-full rounded-xl" />)}
          </div>
        ) : !trades?.length ? (
          <div className="py-16 text-center text-slate-500">
            <Clock size={32} className="mx-auto mb-3 opacity-30" />
            <p>No trades yet</p>
            <p className="text-sm mt-1">Go to Trade to make your first trade</p>
          </div>
        ) : (
          <div className="divide-y divide-white/5">
            {trades.map((trade) => (
              <div key={trade.id} className="flex items-center gap-4 px-6 py-4 hover:bg-white/2 transition-all">
                {(() => {
                  const style = TRADE_TYPE_STYLES[trade.tradeType] ?? { cls: 'text-slate-400 bg-slate-500/10', label: trade.tradeType };
                  return (
                    <div className={`px-2.5 py-1 rounded-lg text-xs font-bold ${style.cls}`}>
                      {style.label}
                    </div>
                  );
                })()}
                <div className="flex-1 min-w-0">
                  {trade.tradeType === 'BONUS' ? (
                    <>
                      <p className="text-amber-300 text-sm font-medium">Welcome Joining Bonus 🎉</p>
                      <p className="text-slate-500 text-xs mt-0.5">{fmtDate(trade.createdAt)}</p>
                    </>
                  ) : (
                    <>
                      <p className="text-white text-sm font-medium">
                        {trade.quantity ? `${trade.quantity} ${trade.weightUnit} · ${trade.metalName}` : trade.tradeType}
                      </p>
                      <p className="text-slate-500 text-xs mt-0.5">{fmtDate(trade.createdAt)}</p>
                    </>
                  )}
                </div>
                <div className="text-right">
                  <p className={`text-sm font-semibold ${trade.tradeType === 'BONUS' ? 'text-amber-400' : 'text-white'}`}>
                    {trade.tradeType === 'BONUS' ? '+' : ''}{fmt(trade.totalAmount, trade.currency ?? 'USD')}
                  </p>
                  {trade.pricePerUnit ? (
                    <p className="text-slate-500 text-xs">{fmt(trade.pricePerUnit, trade.currency)}/{trade.weightUnit}</p>
                  ) : null}
                </div>
                <div className={`w-2 h-2 rounded-full shrink-0 ${trade.status === 'COMPLETED' ? 'bg-emerald-400' : 'bg-amber-400'}`} />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
