import type { JSONify, Transaction } from './types'

export function deepCopy<T>(obj: T): JSONify<T> {
  return JSON.parse(JSON.stringify(obj))
}

export class BackendUpdates extends EventTarget {
  updateTransactions(transactions: Transaction[]) {
    transactions.sort((a, b) => {
      return b.updatedAt.getTime() - a.updatedAt.getTime()
    })
    this.dispatchEvent(
      new CustomEvent('transactions-updated', {
        detail: deepCopy(transactions)
      })
    )
  }
}
