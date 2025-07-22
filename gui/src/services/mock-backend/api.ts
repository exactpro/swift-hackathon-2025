import { simulate } from './simulation'
import type { Transaction, JSONify, Currency } from './types'
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
    accounts: state.accounts.filter((account) => account.ownerId === client.id),
    transactions: state.transactions
      .filter((transaction) => transaction.debtor.clientId === client.id || transaction.creditor.clientId === client.id)
      .sort((a, b) => {
        return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
      })
  })
}

export function getTransactionFormData() {
  const currencies: Currency[] = ['EUR', 'USD', 'S-USDC']
  return {
    currencies,
    exchangeValues: {
      EUR: 1.16,
      USD: 1,
      SUSDC: 1.01
    }
  }
}

export function newTransaction(
  transaction: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'>
): JSONify<Transaction> | null {
  const newTransaction: Transaction = {
    ...transaction,
    uetr: faker.string.uuid(),
    status: 'pending',
    createdAt: new Date(),
    updatedAt: new Date()
  }

  // Update debtor account balance
  const account = state.accounts.find(
    (a) => a.ownerId === newTransaction.debtor.clientId && a.currency === newTransaction.debtor.currency
  )
  if (!account) {
    console.error('Debtor account not found:', newTransaction.debtor)
    return null
  }
  if (account.balance < newTransaction.debtor.amount) {
    console.error('Insufficient funds in debtor account:', account)
    return null
  }
  account.balance -= newTransaction.debtor.amount

  state.transactions.unshift(newTransaction)
  emitter.updateTransactions(state.transactions)
  return deepCopy(newTransaction)
}

export interface ExchangeProps {
  clientId: string
  fromCurrency: Currency
  toCurrency: Currency
  amount: number
}

const CURRENCY_KEYS: Record<Currency, 'EUR' | 'USD' | 'SUSDC'> = {
  EUR: 'EUR',
  USD: 'USD',
  'S-USDC': 'SUSDC'
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

  const exchangeValues = getTransactionFormData().exchangeValues

  const fromValue = exchangeValues[CURRENCY_KEYS[fromCurrency]]
  const toValue = exchangeValues[CURRENCY_KEYS[toCurrency]]
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
