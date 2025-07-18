import config from '../../config.js'
import type { Transaction, JSONify } from './mock-backend/types.js'

export async function fetchTransactions(): Promise<JSONify<Transaction[]>> {
  if (config.useMock) {
    const { getTransactions } = await import('./mock-backend/api.js')
    return getTransactions()
  }
  return []
}

export async function subscribeToTransactionsUpdates(
  callback: (transactions: JSONify<Transaction>[]) => void
): Promise<() => void> {
  if (config.useMock) {
    const { subscribeToTransactionsUpdates } = await import(
      './mock-backend/api.js'
    )
    return subscribeToTransactionsUpdates(callback)
  }
  return () => {}
}
