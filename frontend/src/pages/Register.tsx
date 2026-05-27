import { Eye, EyeOff, Gift, Loader2 } from 'lucide-react';
import { motion, useReducedMotion } from 'framer-motion';
import { useState, type FormEvent } from 'react';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';
import { register as registerApi } from '../api/auth';
import { useAuth } from '../context/AuthContext';
import AuthLayout from '../components/AuthLayout';
import PasswordStrength from '../components/PasswordStrength';
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

export default function Register() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const shouldReduce = useReducedMotion();

  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [showPw,  setShowPw]  = useState(false);
  const [loading, setLoading] = useState(false);

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await registerApi(form);
      login(res.data);
      toast.success('Account created! 🎁 $5,000 joining bonus added to your account.', { duration: 5000 });
      navigate('/dashboard');
    } catch (err: unknown) {
      toast.error(apiMessage(err, 'Registration failed'));
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
        {/* Signup bonus callout */}
        <div className="flex items-center gap-3 p-3 rounded-lg bg-amber-500/10 border border-amber-500/20 mb-5">
          <Gift size={18} className="text-amber-400 flex-shrink-0" />
          <span className="text-sm text-amber-300">Get $5,000 in virtual trading credit on signup</span>
        </div>

        <div className="glass rounded-2xl p-8">
          <h1 className="text-2xl font-bold text-white mb-1">Create account</h1>
          <p className="text-slate-400 text-sm mb-8">Start trading precious metals today</p>

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
                value={form.username}
                onChange={set('username')}
                required
                minLength={3}
                placeholder="Sreeram123"
                className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-amber-500/60 focus:bg-amber-500/5 transition-all"
              />
            </motion.div>

            {/* Email */}
            <motion.div variants={item}>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Email
              </label>
              <input
                type="email"
                value={form.email}
                onChange={set('email')}
                required
                placeholder="sreeram@example.com"
                className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-white placeholder-slate-600 focus:outline-none focus:border-amber-500/60 focus:bg-amber-500/5 transition-all"
              />
            </motion.div>

            {/* Password + strength meter */}
            <motion.div variants={item}>
              <label className="block text-xs font-medium text-slate-400 mb-1.5 uppercase tracking-wider">
                Password
              </label>
              <div className="relative">
                <input
                  type={showPw ? 'text' : 'password'}
                  value={form.password}
                  onChange={set('password')}
                  required
                  minLength={8}
                  placeholder="Min. 8 characters"
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
              <PasswordStrength password={form.password} />
            </motion.div>

            {/* Submit */}
            <motion.div variants={item}>
              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 bg-amber-500 hover:bg-amber-400 disabled:opacity-50 disabled:cursor-not-allowed text-black font-semibold py-3 rounded-xl transition-all duration-200 mt-2"
              >
                {loading && <Loader2 size={17} className="animate-spin" />}
                {loading ? 'Creating account…' : 'Create account'}
              </button>
            </motion.div>
          </motion.form>

          <p className="text-center text-sm text-slate-500 mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-amber-400 hover:text-amber-300 font-medium transition-colors">
              Sign in
            </Link>
          </p>
        </div>
      </motion.div>
    </AuthLayout>
  );
}
