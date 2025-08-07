export type Currency = 'EUR' | 'USD' | 'USDC'
export type TransactionStatus = 'pending' | 'completed' | 'cancelled' | 'rejected'
export type TransactionType = 'transfer' | 'cancel'
export type TransactionParty = {
  name: string
  bic: string
  accountId: string
  amount: number
  currency: Currency
}

export type Transaction = {
  id: number
  uetr: string
  status: TransactionStatus
  type: TransactionType
  debtor: TransactionParty
  creditor: TransactionParty
  createdAt: Date
  updatedAt: Date
  comment: string | null
}

export type Account = {
  ownerId: Client['id']
  id: string
  balance: number
  currency: Currency
}

export type Client = {
  bic: string
  fullName: string
  id: string
}

export type TransactionMessageStatus = {
  type: string
  title: string
  summary: object
  timestamp: Date
}

export type JSONify<T> = T extends Date
  ? string
  : T extends Array<infer U>
    ? Array<JSONify<U>>
    : T extends object
      ? { [K in keyof T]: JSONify<T[K]> }
      : T
