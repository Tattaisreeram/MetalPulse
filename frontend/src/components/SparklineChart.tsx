import { ResponsiveContainer, AreaChart, Area, Tooltip } from 'recharts';
import type { HistoricalPriceDto } from '../types';

interface Props {
  data: HistoricalPriceDto[];
  color: string;
  positive: boolean;
}

export default function SparklineChart({ data, color, positive }: Props) {
  const sliced = data.slice(-30);
  return (
    <ResponsiveContainer width="100%" height={60}>
      <AreaChart data={sliced} margin={{ top: 4, right: 0, left: 0, bottom: 0 }}>
        <defs>
          <linearGradient id={`sg-${color.replace('#', '')}`} x1="0" y1="0" x2="0" y2="1">
            <stop offset="5%"  stopColor={color} stopOpacity={positive ? 0.3 : 0.15} />
            <stop offset="95%" stopColor={color} stopOpacity={0} />
          </linearGradient>
        </defs>
        <Tooltip
          content={() => null}
        />
        <Area
          type="monotone"
          dataKey="price"
          stroke={color}
          strokeWidth={1.5}
          fill={`url(#sg-${color.replace('#', '')})`}
          dot={false}
        />
      </AreaChart>
    </ResponsiveContainer>
  );
}
