import config from '../../config.js'
import type { Client, JSONify, Account, Currency, Transaction } from './mock-backend/types.js'

export interface ClientDataResponse extends JSONify<Client> {
  accounts: JSONify<Account>[]
}

export async function fetchClientData(clientId: string): Promise<ClientDataResponse | null> {
  if (config.useMock) {
    const { getClientData } = await import('./mock-backend/api.js')
    return getClientData(clientId)
  }
  return null
}

export async function fetchClientTransactions(clientId: string): Promise<JSONify<Transaction>[]> {
  if (config.useMock) {
    const { getClientTransactions } = await import('./mock-backend/api.js')
    return getClientTransactions(clientId)
  }
  return []
}

// Not used at the moment
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
