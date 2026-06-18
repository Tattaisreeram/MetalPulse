import { useState, useRef, useEffect, useCallback } from 'react';
import { Send, Bot, User, Sparkles, Loader2, AlertCircle, RotateCcw } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { ask } from '../api/assistant';

interface Message {
  id: number;
  role: 'user' | 'assistant';
  text: string;
  error?: boolean;
}

const SUGGESTIONS = [
  'What is my current balance?',
  'Show me my recent trades.',
  'What is the spot price of gold and how is it calculated?',
  'How does paper trading work on MetalPulse?',
  'What is the difference between XAU and XAG?',
];

let nextId = 1;

export default function Assistant() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, loading]);

  const send = useCallback(async (question: string) => {
    const q = question.trim();
    if (!q || loading) return;

    const userMsg: Message = { id: nextId++, role: 'user', text: q };
    setMessages((prev) => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const res = await ask(q);
      setMessages((prev) => [
        ...prev,
        { id: nextId++, role: 'assistant', text: res.answer },
      ]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          id: nextId++,
          role: 'assistant',
          text: 'The assistant is temporarily unavailable. Please try again.',
          error: true,
        },
      ]);
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  }, [loading]);

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      send(input);
    }
  }

  function handleReset() {
    setMessages([]);
    setInput('');
    inputRef.current?.focus();
  }

  const empty = messages.length === 0;

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)] lg:h-[calc(100vh-4rem)] animate-fade-up">
      {/* Header */}
      <div className="flex items-center justify-between mb-4 shrink-0">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Sparkles size={22} className="text-amber-400" />
            AI Assistant
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Ask about your portfolio, trades, or precious metals
          </p>
        </div>
        {!empty && (
          <button
            onClick={handleReset}
            className="flex items-center gap-2 px-3 py-2 rounded-xl text-slate-400 hover:text-white hover:bg-white/5 text-sm transition-all"
          >
            <RotateCcw size={14} />
            New chat
          </button>
        )}
      </div>

      {/* Chat area */}
      <div className="flex-1 overflow-y-auto glass rounded-2xl p-4 lg:p-6 flex flex-col gap-4 min-h-0">
        {empty ? (
          /* Welcome state */
          <div className="flex-1 flex flex-col items-center justify-center gap-6 text-center px-4">
            <div className="w-16 h-16 rounded-2xl bg-amber-500/15 border border-amber-500/30 flex items-center justify-center">
              <Bot size={32} className="text-amber-400" />
            </div>
            <div>
              <h2 className="text-white font-semibold text-lg">MetalPulse Assistant</h2>
              <p className="text-slate-400 text-sm mt-1 max-w-sm">
                I can answer questions about your balance, trade history, and how precious
                metals markets work.
              </p>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2 w-full max-w-2xl">
              {SUGGESTIONS.map((s) => (
                <button
                  key={s}
                  onClick={() => send(s)}
                  className="text-left px-4 py-3 rounded-xl bg-white/3 border border-white/8 text-slate-300 text-sm hover:bg-amber-500/10 hover:border-amber-500/30 hover:text-amber-300 transition-all"
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        ) : (
          /* Messages */
          <AnimatePresence initial={false}>
            {messages.map((msg) => (
              <motion.div
                key={msg.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.2 }}
                className={`flex gap-3 ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}
              >
                {/* Avatar */}
                <div
                  className={`shrink-0 w-8 h-8 rounded-xl flex items-center justify-center
                    ${msg.role === 'user'
                      ? 'bg-amber-500/20 border border-amber-500/30'
                      : msg.error
                        ? 'bg-red-500/20 border border-red-500/30'
                        : 'bg-white/8 border border-white/10'
                    }`}
                >
                  {msg.role === 'user'
                    ? <User size={14} className="text-amber-400" />
                    : msg.error
                      ? <AlertCircle size={14} className="text-red-400" />
                      : <Bot size={14} className="text-slate-300" />
                  }
                </div>

                {/* Bubble */}
                <div
                  className={`max-w-[80%] px-4 py-3 rounded-2xl text-sm leading-relaxed whitespace-pre-wrap
                    ${msg.role === 'user'
                      ? 'bg-amber-500/15 border border-amber-500/20 text-amber-50 rounded-tr-sm'
                      : msg.error
                        ? 'bg-red-500/10 border border-red-500/20 text-red-300 rounded-tl-sm'
                        : 'bg-white/5 border border-white/8 text-slate-200 rounded-tl-sm'
                    }`}
                >
                  {msg.text}
                </div>
              </motion.div>
            ))}

            {/* Typing indicator */}
            {loading && (
              <motion.div
                key="typing"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex gap-3"
              >
                <div className="shrink-0 w-8 h-8 rounded-xl bg-white/8 border border-white/10 flex items-center justify-center">
                  <Bot size={14} className="text-slate-300" />
                </div>
                <div className="px-4 py-3 rounded-2xl rounded-tl-sm bg-white/5 border border-white/8 flex items-center gap-1.5">
                  {[0, 1, 2].map((i) => (
                    <span
                      key={i}
                      className="w-1.5 h-1.5 rounded-full bg-slate-400 animate-bounce"
                      style={{ animationDelay: `${i * 150}ms` }}
                    />
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        )}
        <div ref={bottomRef} />
      </div>

      {/* Input area */}
      <div className="shrink-0 mt-3">
        <div className="glass rounded-2xl flex items-end gap-3 p-3">
          <textarea
            ref={inputRef}
            rows={1}
            value={input}
            onChange={(e) => {
              setInput(e.target.value);
              e.target.style.height = 'auto';
              e.target.style.height = `${Math.min(e.target.scrollHeight, 120)}px`;
            }}
            onKeyDown={handleKeyDown}
            placeholder="Ask me anything about your portfolio or metals…"
            disabled={loading}
            className="flex-1 bg-transparent text-white text-sm placeholder-slate-500 resize-none outline-none py-1 max-h-[120px] disabled:opacity-50"
          />
          <button
            onClick={() => send(input)}
            disabled={!input.trim() || loading}
            className="shrink-0 w-9 h-9 rounded-xl bg-amber-500 hover:bg-amber-400 disabled:opacity-40 disabled:cursor-not-allowed flex items-center justify-center transition-all"
          >
            {loading
              ? <Loader2 size={16} className="text-black animate-spin" />
              : <Send size={16} className="text-black" />
            }
          </button>
        </div>
        <p className="text-slate-600 text-xs text-center mt-2">
          Press Enter to send · Shift+Enter for new line
        </p>
      </div>
    </div>
  );
}
