interface Props {
  password: string;
}

const LABELS = ['Too short', 'Weak', 'Fair', 'Good', 'Strong'] as const;
const FILLED_COLOR = ['', 'bg-red-500', 'bg-orange-400', 'bg-yellow-400', 'bg-amber-500'] as const;

function score(pw: string): number {
  let s = 0;
  if (pw.length >= 8) s++;
  if (/\d/.test(pw)) s++;
  if (/[A-Z]/.test(pw)) s++;
  if (/[^A-Za-z0-9]/.test(pw)) s++;
  return s;
}

export default function PasswordStrength({ password }: Props) {
  if (!password) return null;
  const s = score(password);
  const barsLit = s === 0 ? 1 : s; // show at least 1 red bar when something is typed

  return (
    <div className="mt-2 space-y-1.5">
      <div className="flex gap-1">
        {[0, 1, 2, 3].map((i) => (
          <div
            key={i}
            className={`flex-1 h-1 rounded-full transition-all duration-300 ${
              i < barsLit ? (FILLED_COLOR[s] || 'bg-red-500') : 'bg-white/10'
            }`}
          />
        ))}
      </div>
      <p className="text-xs text-slate-400">{LABELS[s]}</p>
    </div>
  );
}
