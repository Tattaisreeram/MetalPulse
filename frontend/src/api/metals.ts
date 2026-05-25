import client from './client';
import type { ApiResponse, SpotPriceDto, HistoricalPricePageDto, HistoricalPriceDto } from '../types';

export const getSpotPrice = (metal: string, currency = 'PKR', weightUnit = 'g') =>
  client
    .get<ApiResponse<SpotPriceDto>>('/metals/spot-price', { params: { metal, currency, weight_unit: weightUnit } })
    .then((r) => r.data.data);

export const getHistoricalPrices = (
  metal: string, currency = 'PKR', weightUnit = 'g', page = 0, size = 30
) =>
  client
    .get<ApiResponse<HistoricalPricePageDto>>('/metals/historical', {
      params: { metal, currency, weight_unit: weightUnit, page, size },
    })
    .then((r) => r.data.data);

export const getFullHistory = (metal: string, currency = 'PKR', weightUnit = 'g') =>
  client
    .get<ApiResponse<HistoricalPriceDto[]>>('/metals/full-history', {
      params: { metal, currency, weight_unit: weightUnit },
    })
    .then((r) => r.data.data);
