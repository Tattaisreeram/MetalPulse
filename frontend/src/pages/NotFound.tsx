import { Link } from 'react-router-dom';

export default function NotFound() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-[#080810] px-4 text-center">
      <img
        src="/404.jpg"
        alt="Page not found"
        className="w-72 h-48 object-cover rounded-2xl mb-8 opacity-80"
      />
      <h1 className="text-5xl font-bold text-white mb-3">404</h1>
      <p className="text-slate-400 text-lg mb-8">This page doesn't exist.</p>
      <Link
        to="/dashboard"
        className="px-6 py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-semibold transition-all"
      >
        Back to Dashboard
      </Link>
    </div>
  );
}
