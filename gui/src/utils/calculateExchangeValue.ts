import type { Currency } from '../services/mock-backend/types'
export const CURRENCY_KEYS: Record<Currency, 'EUR' | 'USD' | 'SUSDC'> = {
  EUR: 'EUR',
  USD: 'USD',
  'S-USDC': 'SUSDC'
}

export function calculateExchangeValue(
  amount: number,
  fromCurrency: Currency,
  toCurrency: Currency,
  exchangeValues: Record<'EUR' | 'USD' | 'SUSDC', number>
): number {
  const fromValue = exchangeValues[CURRENCY_KEYS[fromCurrency]]
  const toValue = exchangeValues[CURRENCY_KEYS[toCurrency]]
  if (!fromValue || !toValue) {
    console.error(
      'Exchange values not available for currencies:',
      fromCurrency,
      toCurrency
    )
    return amount
  }
  const exchangedEmount = (amount * fromValue) / toValue
  return parseFloat(exchangedEmount.toFixed(2))
}
