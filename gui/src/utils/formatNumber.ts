import type { Currency } from '../services/mock-backend/types'

export function formatNumber(value: number, locale: string = 'en-US', options?: Intl.NumberFormatOptions): string {
  return new Intl.NumberFormat(locale, options).format(value)
}

export function formatAccountBalance(currency: Currency, amount: number) {
  const formattedBalance = formatNumber(amount)
  if (currency === 'EUR') {
    return `€${formattedBalance}`
  } else if (currency === 'USD') {
    return `$${formattedBalance}`
  } else if (currency === 'S-USDC') {
    return `${formattedBalance} S-USDC`
  }
  return `${formattedBalance} ${currency}`
}
