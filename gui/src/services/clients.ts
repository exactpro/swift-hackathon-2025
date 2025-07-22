import config from '../../config.js'
import type { Client, JSONify, Account, Transaction, Currency } from './mock-backend/types.js'

interface ClientDataResponse extends JSONify<Client> {
  accounts: JSONify<Account>[]
  transactions: JSONify<Transaction>[]
}

export async function fetchClientData(clientId: string): Promise<ClientDataResponse | null> {
  if (config.useMock) {
    const { getClientData } = await import('./mock-backend/api.js')
    return getClientData(clientId)
  }
  return null
}

export async function exchangeCurrency(
  clientId: string,
  fromCurrency: Currency,
  toCurrency: Currency,
  amount: number
): Promise<boolean> {
  if (config.useMock) {
    const { exchange } = await import('./mock-backend/api.js')
    return exchange({
      clientId,
      fromCurrency,
      toCurrency,
      amount
    })
  }
  // In a real application, this would call the backend API to perform the exchange
  console.warn('Exchange operation is not implemented in mock mode.')
  return false
}
