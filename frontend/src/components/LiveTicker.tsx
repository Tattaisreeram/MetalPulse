import { motion, useReducedMotion } from 'framer-motion';
import { useEffect, useReducer } from 'react';

interface TickerState {
  price: number;
  change: number;
}

type TickerAction = { type: 'tick' };

function tickerReducer(state: TickerState, _action: TickerAction): TickerState {
  const delta = (Math.random() * 0.04 + 0.01) * (Math.random() < 0.5 ? 1 : -1);
  return {
    price: Math.max(1900, +(state.price + delta).toFixed(2)),
    change: +(state.change + delta * 0.002).toFixed(2),
  };
}

export default function LiveTicker() {
  const shouldReduce = useReducedMotion();
  const [state, dispatch] = useReducer(tickerReducer, { price: 2050.5, change: 0.42 });

  useEffect(() => {
    const id = setInterval(() => dispatch({ type: 'tick' }), 3000);
    return () => clearInterval(id);
  }, []);

  const positive = state.change >= 0;

  return (
    <div className="inline-flex items-center gap-2.5 px-4 py-2 rounded-full bg-white/5 border border-white/10 text-xs text-slate-300 w-fit">
      <span className="w-1.5 h-1.5 rounded-full bg-amber-500 animate-pulse flex-shrink-0" />
      <span className="font-semibold text-white">XAU</span>
      <motion.span
        key={state.price}
        initial={shouldReduce ? false : { scale: 1.1, opacity: 0.6 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.4 }}
        className="font-mono tabular-nums"
      >
        ${state.price.toFixed(2)}
      </motion.span>
      <span className={positive ? 'text-emerald-400 font-medium' : 'text-red-400 font-medium'}>
        {positive ? '↑' : '↓'} {Math.abs(state.change).toFixed(2)}%
      </span>
    </div>
  );
}
