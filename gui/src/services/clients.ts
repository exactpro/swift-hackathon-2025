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
  throw new Error('Client data is not available in non-mock mode.')
}

export async function fetchClientTransactions(clientId: string): Promise<JSONify<Transaction>[]> {
  if (config.useMock) {
    const { getClientTransactions } = await import('./mock-backend/api.js')
    return getClientTransactions(clientId)
  }
  throw new Error('Client transactions are not available in non-mock mode.')
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
  throw new Error('Currency exchange is not available in non-mock mode.')
}
