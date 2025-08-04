import type { BankName } from '../../../config'
import config from '../../../config'
import { bankAIbans, bankBIbans, currencies, exchangeValues } from '../hardcoded'
import { simulate } from './simulation'
import type { Transaction, JSONify, Currency, TransactionMessageStatus } from './types'
import { BackendUpdates, deepCopy } from './utils'
import { faker } from '@faker-js/faker'

const emitter = new BackendUpdates()

const state = simulate(emitter)

export function getTransactions() {
  return deepCopy(state.transactions)
}

export function subscribeToTransactionsUpdates(callback: (transactions: JSONify<Transaction>[]) => void) {
  function callbackWrapper(event: CustomEvent) {
    callback(deepCopy(event.detail))
  }
  // @ts-ignore
  emitter.addEventListener('transactions-updated', callbackWrapper)
  return () => {
    // @ts-ignore
    emitter.removeEventListener('transactions-updated', callbackWrapper)
  }
}

export function getClientData(clientId: string) {
  const client = state.clients.find((c) => c.id === clientId)
  if (!client) {
    return null
  }

  return deepCopy({
    ...client,
    accounts: state.accounts.filter((account) => account.ownerId === client.id)
  })
}

export function getClientTransactions(clientId: string): JSONify<Transaction>[] {
  const accountsToSearch = state.accounts.filter((account) => account.ownerId === clientId)
  if (accountsToSearch.length === 0) {
    return []
  }
  const accountIds = accountsToSearch.map((account) => account.id)
  return deepCopy(
    state.transactions.filter(
      (t) => accountIds.includes(t.debtor.accountId) || accountIds.includes(t.creditor.accountId)
    )
  )
}

export function getTransactionFormData(senderBank: BankName) {
  return {
    currencies,
    exchangeValues,
    bics: senderBank === 'Bank A' ? [config.bankB.bic] : [config.bankA.bic],
    ibans: senderBank === 'Bank A' ? bankBIbans : bankAIbans
  }
}

export function newTransaction(transaction: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'>): void {
  const newTransaction: Transaction = {
    ...transaction,
    uetr: faker.string.uuid(),
    status: 'pending',
    createdAt: new Date(),
    updatedAt: new Date()
  }

  // Update debtor account balance
  const account = state.accounts.find((a) => a.id === newTransaction.debtor.accountId)
  if (!account) {
    console.error('Debtor account not found:', newTransaction.debtor)
  } else {
    account.balance -= newTransaction.debtor.amount
    if (account.balance < newTransaction.debtor.amount) {
      console.error('Insufficient funds in debtor account:', account)
    }
  }

  state.transactions.unshift(newTransaction)
  emitter.updateTransactions(state.transactions)
}

export interface ExchangeProps {
  clientId: string
  fromCurrency: Currency
  toCurrency: Currency
  amount: number
}

export function exchange(props: ExchangeProps): boolean {
  const { clientId, fromCurrency, toCurrency, amount } = props
  const client = state.clients.find((c) => c.id === clientId)
  if (!client) {
    console.error('Client not found:', clientId)
    return false
  }

  const account = state.accounts.find((a) => a.ownerId === clientId && a.currency === fromCurrency)
  if (!account || account.balance < amount) {
    console.error('Insufficient funds in account:', account)
    return false
  }

  const exchangeValues = getTransactionFormData('Bank A').exchangeValues

  const fromValue = exchangeValues[fromCurrency]
  const toValue = exchangeValues[toCurrency]
  if (!fromValue || !toValue) {
    console.error('Exchange values not available for currencies:', fromCurrency, toCurrency)
    return false
  }

  const exchangedAmount = (amount * fromValue) / toValue
  const toAccount = state.accounts.find((a) => a.ownerId === clientId && a.currency === toCurrency)
  if (!toAccount) {
    console.error('Target account not found:', toCurrency)
    return false
  }

  // Update balances
  if (account.balance < amount) {
    console.error('Insufficient funds in account:', account)
    return false
  }
  account.balance -= amount
  toAccount.balance += exchangedAmount

  return true
}

export function getTransactionDetails(uetr: string): JSONify<{
  transaction: Transaction
  messages: TransactionMessageStatus[]
}> | null {
  const transaction = state.transactions.find((t) => t.uetr === uetr)
  if (!transaction) {
    return null
  }

  const isCompleted = transaction.status === 'completed'

  const now = new Date()

  const details: TransactionMessageStatus[] = []
  details.push({
    type: 'SWIFT',
    title: 'pacs.008',
    summary: {
      debtor: transaction.debtor.name,
      creditor: transaction.creditor.name,
      currency: transaction.creditor.currency,
      amount: transaction.creditor.amount
    },
    timestamp: new Date(Number(now) - 1000 * 60)
  })
  if (isCompleted) {
    details.push({
      type: 'DLT',
      title: 'DLT Confirmation',
      summary: {
        status: 'Confirmed'
      },
      timestamp: new Date(Number(now) - 1000 * 30)
    })
  }

  return {
    transaction: deepCopy(transaction),
    messages: deepCopy(details)
  }
}
