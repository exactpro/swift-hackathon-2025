import { simulate } from './simulation'
import type { Transaction, JSONify } from './types'
import { BackendUpdates, deepCopy } from './utils'
import config from '../../../config.js'

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
