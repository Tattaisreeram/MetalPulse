export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  role: string;
}

export interface SpotPriceDto {
  metal: string;
  metalName: string;
  currency: string;
  weightUnit: string;
  price: number;
  bid: number;
  ask: number;
  change: number;
  changePercent: number;
  fetchedAt: string;
}

export interface HistoricalPriceDto {
  date: string;
  price: number;
}

export interface HistoricalPricePageDto {
  metal: string;
  currency: string;
  weightUnit: string;
  prices: HistoricalPriceDto[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export interface TradeDto {
  id: number;
  tradeType: string;
  metal: string;
  metalName: string;
  currency: string;
  weightUnit: string;
  quantity: number;
  pricePerUnit: number;
  totalAmount: number;
  status: string;
  createdAt: string;
}

export interface BalanceDto {
  userId: number;
  username: string;
  balance: number;
}

export interface PriceAnalyticsDto {
  metal: string;
  metalName: string;
  currency: string;
  weightUnit: string;
  fromDate: string;
  toDate: string;
  startPrice: number;
  endPrice: number;
  absoluteChange: number;
  percentageChange: number;
  trend: 'UP' | 'DOWN' | 'STABLE';
}

export interface ReturnsDto {
  metal: string;
  metalName: string;
  currency: string;
  weightUnit: string;
  durationDays: number;
  investmentAmount: number;
  priceAtInvestment: number;
  currentPrice: number;
  quantityPurchased: number;
  currentValue: number;
  profitLoss: number;
  returnPercentage: number;
}

export type Metal = 'XAU' | 'XAG' | 'XPT' | 'XPD';
export type WeightUnit = 'g' | 'oz' | 'kg' | 'tola';

export const METALS: { symbol: Metal; name: string; color: string; icon: string; cardImage: string }[] = [
  { symbol: 'XAU', name: 'Gold',      color: '#f59e0b', icon: '🥇', cardImage: '/gold-card.jpg'      },
  { symbol: 'XAG', name: 'Silver',    color: '#94a3b8', icon: '🥈', cardImage: '/silver-card.jpg'    },
  { symbol: 'XPT', name: 'Platinum',  color: '#7dd3fc', icon: '💎', cardImage: '/platinum-card.jpg'  },
  { symbol: 'XPD', name: 'Palladium', color: '#a78bfa', icon: '✨', cardImage: '/palladium-card.jpg' },
];

export const WEIGHT_UNITS: { value: WeightUnit; label: string }[] = [
  { value: 'g',    label: 'Gram' },
  { value: 'oz',   label: 'Troy Oz' },
  { value: 'kg',   label: 'Kilogram' },
  { value: 'tola', label: 'Tola' },
];
