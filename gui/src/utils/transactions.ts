import type { JSONify, Transaction } from '../services/mock-backend/types'

export function getDirection(bic: string, item: JSONify<Transaction>) {
  if (item.debtor.bic === bic) {
    return 'out'
  } else if (item.creditor.bic === bic) {
    return 'in'
  }
  return 'N/A'
}
