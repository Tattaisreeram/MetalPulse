# MetalPulse Knowledge Base — Precious Metals Basics

## This is paper trading

MetalPulse is a **paper trading** platform. No real money, metal, or brokerage account
is involved. Trades adjust a simulated USD balance using live spot prices, so users can
practice buying, selling, and holding precious metals without financial risk.

## The metals

MetalPulse supports four precious metals, identified by their ISO 4217-style codes:

- **XAU** — Gold
- **XAG** — Silver
- **XPT** — Platinum
- **XPD** — Palladium

Prices are quoted per troy ounce, gram, kilogram, or tola, in a range of supported
currencies. Tola is a South Asian unit (1 tola ≈ 11.6638 grams).

## Spot price

The "spot price" is the current market price for immediate delivery of a metal. It
fluctuates continuously based on global supply, demand, currency strength, and investor
sentiment. MetalPulse fetches live spot prices from the Goldbroker API, caches them
briefly, and uses them to price every buy, sell, and hold trade at execution time. A
circuit breaker protects the platform if the upstream price feed becomes unavailable.

## How a trade flows through the system

1. A user submits a buy, sell, or hold request with a metal, currency, weight unit, and
   quantity.
2. The current spot price is fetched and the trade amount is calculated.
3. The user's balance is updated and the trade is saved to the database, all within a
   single transaction.
4. Only **after that transaction commits** does MetalPulse publish a `TradeExecutedEvent`
   to Kafka (the `AFTER_COMMIT` pattern). This guarantees the event is never sent for a
   trade that was rolled back, avoiding a "dual write" where the event and the database
   disagree.
5. Downstream consumers of the Kafka topic can react to completed trades (for example,
   analytics or notifications) without slowing down the trade request itself.

## Returns

"Returns" measure how an investment in a metal has performed over time. MetalPulse
calculates returns by comparing the price at the time of investment to the current spot
price for the same quantity, yielding a profit/loss amount and a return percentage. This
is informational and does not affect the user's actual balance unless they execute a
real sell trade.
