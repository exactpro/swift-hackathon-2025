import config, { type BankName } from '../../config.js'
import type { Transaction, JSONify, TransactionMessageStatus } from './mock-backend/types.js'
import * as api from './backend/api.js'

export async function fetchTransactions(currentBank: BankName, bic: string): Promise<JSONify<Transaction[]>> {
  if (config.useMock) {
    const { getTransactions } = await import('./mock-backend/api.js')
    return getTransactions().filter((tx) => tx.debtor.bic === bic || tx.creditor.bic === bic)
  }
  return api.getTransfers(currentBank)
}

export async function subscribeToTransactionsUpdates(
  currentBank: BankName,
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
    const transactions = await fetchTransactions(currentBank, bic)
    callback(transactions)
  }, 5000)
  return () => clearInterval(interval)
}

export async function fetchTransactionFormData(currentBank: BankName): ReturnType<typeof api.getTransferHelpers> {
  if (config.useMock) {
    const { getTransactionFormData } = await import('./mock-backend/api.js')
    return getTransactionFormData(currentBank)
  }
  return api.getTransferHelpers(currentBank)
}

export async function newTransaction(
  currentBank: BankName,
  transaction: Omit<Transaction, 'id' | 'uetr' | 'createdAt' | 'updatedAt' | 'status'>
): Promise<void> {
  if (config.useMock) {
    const { newTransaction } = await import('./mock-backend/api.js')
    newTransaction(transaction)
    return
  }
  await api.makeTransfer(currentBank, transaction)
}

export async function fetchTransactionDetails(
  currentBank: BankName,
  transferId: string
): Promise<JSONify<{ transaction: Transaction; messages: JSONify<TransactionMessageStatus>[] }> | null> {
  if (config.useMock) {
    const { getTransactionDetails } = await import('./mock-backend/api.js')
    return getTransactionDetails(transferId)
  }
  return api.getTransferStatus(currentBank, transferId)
}
