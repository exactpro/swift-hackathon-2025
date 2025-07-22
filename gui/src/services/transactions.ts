import config from '../../config.js'
import type { Transaction, JSONify } from './mock-backend/types.js'

export async function fetchTransactions(bic: string): Promise<JSONify<Transaction[]>> {
  if (config.useMock) {
    const { getTransactions } = await import('./mock-backend/api.js')
    return getTransactions().filter((tx) => tx.debtor.bic === bic || tx.creditor.bic === bic)
  }
  return []
}

export async function subscribeToTransactionsUpdates(
  bic: string,
  callback: (transactions: JSONify<Transaction>[]) => void
): Promise<() => void> {
  if (config.useMock) {
    function filteredCallback(transactions: JSONify<Transaction>[]) {
      const filtered = transactions.filter((tx) => tx.debtor.bic === bic || tx.creditor.bic === bic)
      callback(filtered)
    }
    const { subscribeToTransactionsUpdates } = await import('./mock-backend/api.js')
    return subscribeToTransactionsUpdates(filteredCallback)
  }
  return () => {}
}

export async function fetchTransactionFormData() {
  if (config.useMock) {
    const { getTransactionFormData } = await import('./mock-backend/api.js')
    return getTransactionFormData()
  }
  return null
}

export async function newTransaction(
  transaction: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'>
): Promise<JSONify<Transaction> | null> {
  if (config.useMock) {
    const { newTransaction } = await import('./mock-backend/api.js')
    return newTransaction(transaction)
  }
  return null
}
