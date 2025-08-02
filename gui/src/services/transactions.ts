import { useRoute } from 'vue-router'
import config, { type BankName } from '../../config.js'
import type { Transaction, JSONify, TransactionMessageStatus } from './mock-backend/types.js'
import * as api from './backend/api.js'

function currentBankName(): BankName {
  const route = useRoute()
  return route.meta.bankName as BankName
}

export async function fetchTransactions(bic: string): Promise<JSONify<Transaction[]>> {
  if (config.useMock) {
    const { getTransactions } = await import('./mock-backend/api.js')
    return getTransactions().filter((tx) => tx.debtor.bic === bic || tx.creditor.bic === bic)
  }
  return api.getTransfers(currentBankName())
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
  const interval = setInterval(async () => {
    const transactions = await fetchTransactions(bic)
    callback(transactions)
  }, 5000)
  return () => clearInterval(interval)
}

export async function fetchTransactionFormData() {
  if (config.useMock) {
    const { getTransactionFormData } = await import('./mock-backend/api.js')
    return getTransactionFormData(currentBankName())
  }
  throw new Error('Transaction form data is not available in non-mock mode.')
}

export async function newTransaction(
  transaction: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'> & {
    comment: string | null
  }
): Promise<JSONify<Transaction> | null> {
  if (config.useMock) {
    const { newTransaction } = await import('./mock-backend/api.js')
    return newTransaction(transaction)
  }
  throw new Error('Creating a new transaction is not available in non-mock mode.')
}

export async function fetchTransactionDetails(
  uetr: string
): Promise<JSONify<{ transaction: Transaction; messages: JSONify<TransactionMessageStatus>[] }> | null> {
  if (config.useMock) {
    const { getTransactionDetails } = await import('./mock-backend/api.js')
    return getTransactionDetails(uetr)
  }
  return api.getTransferStatus(currentBankName(), uetr)
}
