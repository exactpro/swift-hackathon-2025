import { simulate } from './simulation'
import type { Transaction, JSONify, Currency } from './types'
import { BackendUpdates, deepCopy } from './utils'
import config from '../../../config.js'
import { faker } from '@faker-js/faker'

const emitter = new BackendUpdates()

const state = simulate(config.ownBic, emitter)

export function getTransactions() {
  return deepCopy(state.transactions)
}

export function subscribeToTransactionsUpdates(
  callback: (transactions: JSONify<Transaction>[]) => void
) {
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

export function getClients() {
  const clients = deepCopy(state.clients)
  return clients.map((client) => {
    const accounts = deepCopy(
      state.accounts.filter((account) => account.ownerId === client.id)
    )
    return {
      ...client,
      accounts: {
        EUR: accounts.find((account) => account.currency === 'EUR'),
        USD: accounts.find((account) => account.currency === 'USD'),
        SUSDC: accounts.find((account) => account.currency === 'S-USDC')
      }
    }
  })
}

export function getClientData(clientId: string) {
  const client = state.clients.find((c) => c.id === clientId)
  if (!client) {
    return null
  }

  return {
    ...client,
    accounts: deepCopy(
      state.accounts.filter((account) => account.ownerId === client.id)
    ),
    transactions: deepCopy(
      state.transactions.filter(
        (transaction) =>
          transaction.debtor.clientId === client.id ||
          transaction.creditor.clientId === client.id
      )
    ).sort((a, b) => {
      return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    })
  }
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
): JSONify<Transaction> {
  const newTransaction: Transaction = {
    ...transaction,
    uetr: faker.string.uuid(),
    status: 'pending',
    createdAt: new Date(),
    updatedAt: new Date()
  }
  state.transactions.push(newTransaction)
  emitter.updateTransactions(state.transactions)
  return deepCopy(newTransaction)
}
