import client from './client';
import type { ApiResponse, BalanceDto, TradeDto } from '../types';

interface TradeRequest {
  metal: string;
  currency: string;
  weightUnit: string;
  quantity: number;
}

interface DepositWithdrawRequest {
  amount: number;
  currency: string;
}

export const buy  = (data: TradeRequest) =>
  client.post<ApiResponse<TradeDto>>('/trades/buy', data).then((r) => r.data);

export const sell = (data: TradeRequest) =>
  client.post<ApiResponse<TradeDto>>('/trades/sell', data).then((r) => r.data);

export const hold = (data: TradeRequest) =>
  client.post<ApiResponse<TradeDto>>('/trades/hold', data).then((r) => r.data);

export const deposit = (data: DepositWithdrawRequest) =>
  client.post<ApiResponse<BalanceDto>>('/trades/deposit', data).then((r) => r.data);

export const withdraw = (data: DepositWithdrawRequest) =>
  client.post<ApiResponse<BalanceDto>>('/trades/withdraw', data).then((r) => r.data);

export const getBalance = () =>
  client.get<ApiResponse<BalanceDto>>('/trades/balance').then((r) => r.data.data);

export const getTradeHistory = (page = 0, size = 20) =>
  client
    .get<ApiResponse<TradeDto[]>>('/trades/history', { params: { page, size } })
    .then((r) => r.data.data);
