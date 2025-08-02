import { useRoute } from 'vue-router'
import config from '../../config.js'
import type { Client, JSONify, Account, Currency, Transaction } from './mock-backend/types.js'
import type { BankName } from '../../config.js'
import * as api from './backend/api.js'

function currentBankName(): BankName {
  const route = useRoute()
  return route.meta.bankName as BankName
}

export interface ClientDataResponse extends JSONify<Client> {
  accounts: JSONify<Account>[]
}

export async function fetchClientData(clientId: string): Promise<ClientDataResponse | null> {
  if (config.useMock) {
    const { getClientData } = await import('./mock-backend/api.js')
    return getClientData(clientId)
  }
  return api.getClientPageData(currentBankName(), clientId)
}

export async function fetchClientTransactions(clientId: string): Promise<JSONify<Transaction>[]> {
  if (config.useMock) {
    const { getClientTransactions } = await import('./mock-backend/api.js')
    return getClientTransactions(clientId)
  }
  return api.getClientTransfers(currentBankName(), clientId)
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
