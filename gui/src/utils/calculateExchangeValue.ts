import type { Currency } from '../services/mock-backend/types'

export function calculateExchangeValue(
  amount: number,
  fromCurrency: Currency,
  toCurrency: Currency,
  exchangeValues: Record<'EUR' | 'USD' | 'USDC', number>
): number {
  const fromValue = exchangeValues[fromCurrency]
  const toValue = exchangeValues[toCurrency]
  if (!fromValue || !toValue) {
    console.error('Exchange values not available for currencies:', fromCurrency, toCurrency)
    return amount
  }
  const exchangedEmount = (amount * fromValue) / toValue
  return parseFloat(exchangedEmount.toFixed(2))
}
