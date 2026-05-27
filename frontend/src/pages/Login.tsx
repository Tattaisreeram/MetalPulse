import { Eye, EyeOff, Loader2 } from 'lucide-react';
import { motion, useReducedMotion } from 'framer-motion';
import { useState, type FormEvent } from 'react';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';
import { login as loginApi } from '../api/auth';
import { useAuth } from '../context/AuthContext';
import AuthLayout from '../components/AuthLayout';
import type { AxiosError } from 'axios';

function apiMessage(err: unknown, fallback: string): string {
  const e = err as AxiosError<{ message?: string }>;
  return e?.response?.data?.message ?? fallback;
}

const container = {
  hidden: {},
  show: { transition: { staggerChildren: 0.1 } },
};

const item = {
  hidden: { opacity: 0, y: 12 },
  show:   { opacity: 1, y: 0, transition: { duration: 0.3 } },
};

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const shouldReduce = useReducedMotion();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPw,   setShowPw]   = useState(false);
  const [loading,  setLoading]  = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await loginApi({ username, password });
      login(res.data);
      toast.success(`Welcome back, ${res.data.username}!`);
      navigate('/dashboard');
    } catch (err: unknown) {
      toast.error(apiMessage(err, 'Login failed'));
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout>
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -8 }}
        transition={{ duration: 0.2 }}
      >
        <div className="glass rounded-2xl p-8">
          <h1 className="text-2xl font-bold text-white mb-1">Sign in</h1>
          <p className="text-slate-400 text-sm mb-8">Access your precious metals portfolio</p>

          <motion.form
            onSubmit={handleSubmit}
            className="space-y-5"
            variants={container}
            initial={shouldReduce ? false : 'hidden'}
            animate="show"
          >
            {/* Username */}
            <motion.div variants={item}>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Username
              </label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                placeholder="Sreeram123"
                className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-amber-500/60 focus:bg-amber-500/5 transition-all"
              />
            </motion.div>

            {/* Password */}
            <motion.div variants={item}>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Password
              </label>
              <div className="relative">
                <input
                  type={showPw ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  placeholder="••••••••"
                  className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 pr-12 text-white placeholder-slate-600 focus:outline-none focus:border-amber-500/60 focus:bg-amber-500/5 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPw((v) => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors"
                >
                  {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </motion.div>

            {/* Remember me — cosmetic only */}
            {/* TODO: backend JWT expiration is server-controlled; this checkbox has no effect on token lifetime */}
            <motion.label variants={item} className="flex items-center gap-2.5 cursor-pointer group w-fit">
              <input
                type="checkbox"
                title="Remember me for 30 days"
                className="w-4 h-4 rounded border-white/20 bg-white/5 accent-amber-500 cursor-pointer"
              />
              <span className="text-sm text-slate-400 group-hover:text-slate-300 transition-colors select-none">
                Remember me for 30 days
              </span>
            </motion.label>

            {/* Submit */}
            <motion.div variants={item}>
              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 bg-amber-500 hover:bg-amber-400 disabled:opacity-50 disabled:cursor-not-allowed text-black font-semibold py-3 rounded-xl transition-all duration-200 mt-2"
              >
                {loading && <Loader2 size={17} className="animate-spin" />}
                {loading ? 'Signing in…' : 'Sign in'}
              </button>
            </motion.div>
          </motion.form>

          <p className="text-center text-sm text-slate-500 mt-6">
            Don't have an account?{' '}
            <Link to="/register" className="text-amber-400 hover:text-amber-300 font-medium transition-colors">
              Create one
            </Link>
          </p>
        </div>
      </motion.div>
    </AuthLayout>
  );
}
