import state from './state.js'
import type {
  Client,
  Transaction,
  Account,
  Currency,
  TransactionStatus
} from './types.js'
import { faker } from '@faker-js/faker'
import type { BackendUpdates } from './utils.js'

/**
 * Initializes start data in state and cron jobs for changing state from the other end.
 * @param ownBIC - BIC of the own bank
 */
export function simulate(ownBIC: string, emitter: BackendUpdates) {
  const currencies: Currency[] = ['EUR', 'USD', 'S-USDC']
  const statuses: TransactionStatus[] = [
    'pending',
    'completed',
    'cancelled',
    'rejected'
  ]

  function emitTransactionsUpdated() {
    state.transactions.sort((a, b) => {
      return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    })
    emitter.updateTransactions(state.transactions)
  }

  for (let i = 0; i < 10; i++) {
    const client: Client = {
      fullName: faker.person.fullName(),
      id: faker.string.uuid()
    }
    state.clients.push(client)

    for (let currency of currencies) {
      const account: Account = {
        ownerId: client.id,
        id: faker.finance.accountNumber(),
        balance: faker.number.int({ min: 1000, max: 100000 }),
        currency
      }
      state.accounts.push(account)

      for (let j = 0; j < 5; j++) {
        const transaction: Transaction = {
          uetr: faker.string.uuid(),
          status: faker.helpers.arrayElement(statuses),
          type: 'transfer',
          debtor: {
            name: client.fullName,
            bic: ownBIC,
            clientId: client.id,
            amount: faker.number.int({ min: 10, max: 500 }),
            currency
          },
          creditor: {
            name: faker.person.fullName(),
            bic: faker.finance.bic(),
            clientId: faker.string.uuid(),
            amount: faker.number.int({ min: 10, max: 500 }),
            currency
          },
          createdAt: new Date(),
          updatedAt: new Date()
        }
        state.transactions.push(transaction)
      }
    }
  }

  state.transactions.reverse()
  console.log('Mock backend initialized with data:', state)

  setInterval(() => {
    const transaction = faker.helpers.arrayElement(
      state.transactions.filter(
        (t) =>
          t.status === 'pending' &&
          t.type === 'transfer' &&
          t.creditor.bic !== ownBIC
      )
    )
    transaction.status = 'completed'
    transaction.updatedAt = new Date()
    emitTransactionsUpdated()
    console.log(
      `Transaction ${transaction.uetr} status changed to completed`,
      transaction
    )
  }, 30000)

  setInterval(() => {
    const transaction = faker.helpers.arrayElement(
      state.transactions.filter(
        (t) => t.status === 'pending' && t.type === 'transfer'
      )
    )
    transaction.status = 'rejected'
    transaction.updatedAt = new Date()
    emitTransactionsUpdated()
    console.log(
      `Transaction ${transaction.uetr} status changed to rejected`,
      transaction
    )
  }, 40000)

  setInterval(() => {
    const recipient = faker.helpers.arrayElement(state.clients)
    const incomingTransaction: Transaction = {
      uetr: faker.string.uuid(),
      status: 'pending',
      type: 'transfer',
      debtor: {
        name: faker.person.fullName(),
        bic: faker.finance.bic(),
        clientId: faker.string.uuid(),
        amount: faker.number.int({ min: 10, max: 500 }),
        currency: faker.helpers.arrayElement(currencies)
      },
      creditor: {
        name: recipient.fullName,
        bic: ownBIC,
        clientId: recipient.id,
        amount: faker.number.int({ min: 10, max: 500 }),
        currency: faker.helpers.arrayElement(currencies)
      },
      createdAt: new Date(),
      updatedAt: new Date()
    }
    console.log(
      `Incoming transaction for ${recipient.fullName}:`,
      incomingTransaction
    )
    state.transactions.push(incomingTransaction)
    emitTransactionsUpdated()
  }, 50000)
  return state
}
