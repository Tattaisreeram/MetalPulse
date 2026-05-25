import client from './client';
import type { ApiResponse, PriceAnalyticsDto, ReturnsDto } from '../types';

export const getPriceChange = (
  metal: string, currency: string, weightUnit: string, fromDate: string, toDate: string
) =>
  client
    .get<ApiResponse<PriceAnalyticsDto>>('/analytics/price-change', {
      params: { metal, currency, weight_unit: weightUnit, fromDate, toDate },
    })
    .then((r) => r.data.data);

export const calculateReturns = (
  metal: string, currency: string, weightUnit: string, durationDays: number, investmentAmount: number
) =>
  client
    .get<ApiResponse<ReturnsDto>>('/analytics/returns', {
      params: { metal, currency, weight_unit: weightUnit, durationDays, investmentAmount },
    })
    .then((r) => r.data.data);
