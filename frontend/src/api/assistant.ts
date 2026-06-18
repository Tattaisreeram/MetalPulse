import client from './client';
import type { ApiResponse } from '../types';

export interface AssistantResponse {
  answer: string;
}

export const ask = (question: string) =>
  client
    .post<ApiResponse<AssistantResponse>>('/assistant/ask', { question })
    .then((r) => r.data.data);
