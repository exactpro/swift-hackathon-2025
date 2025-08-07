import state from './state.js'
import type { Client, Transaction, Account, Currency, TransactionStatus } from './types.js'
import { faker } from '@faker-js/faker'
import type { BackendUpdates } from './utils.js'
import config from '../../../config.js'

let lastTransactionId = 0

export function newTransactionId(): number {
  return ++lastTransactionId
}

/**
 * Initializes start data in state and cron jobs for changing state from the other end.
 * @param ownBIC - BIC of the own bank
 */
export function simulate(emitter: BackendUpdates) {
  // @ts-ignore
  window.mockState = state // For debugging purposes
  const currencies: Currency[] = ['EUR', 'USD', 'USDC']
  const statuses: TransactionStatus[] = ['pending', 'completed']

  function emitTransactionsUpdated() {
    emitter.updateTransactions(state.transactions)
  }

  for (let bankInfo of [config.bankA, config.bankB]) {
    const client: Client = {
      fullName: bankInfo.client.name,
      id: bankInfo.client.id,
      bic: bankInfo.bic
    }
    state.clients.push(client)

    for (let currency of currencies) {
      const account: Account = {
        ownerId: client.id,
        id: faker.finance.iban(),
        balance: faker.number.int({ min: 1000, max: 100000 }),
        currency
      }
      state.accounts.push(account)

      for (let j = 0; j < 5; j++) {
        const transaction: Transaction = {
          uetr: faker.string.uuid(),
          id: newTransactionId(),
          comment: null,
          status: faker.helpers.arrayElement(statuses),
          type: 'transfer',
          debtor: {
            name: client.fullName,
            bic: client.bic,
            accountId: account.id,
            amount: faker.number.int({ min: 10, max: 500 }),
            currency
          },
          creditor: {
            name: faker.person.fullName(),
            bic: faker.finance.bic(),
            accountId: faker.finance.iban(),
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
    // Choose half of the pending transactions to complete
    const transactions = state.transactions.filter(
      (transaction) => transaction.status === 'pending' && Math.random() < 0.5
    )
    for (const transaction of transactions) {
      transaction.updatedAt = new Date()
    }
    emitTransactionsUpdated()
  }, 5000)

  setInterval(() => {
    // Choose half of the pending transactions to complete
    const transactions = state.transactions.filter(
      (transaction) => transaction.status === 'pending' && Math.random() < 0.5
    )
    for (const transaction of transactions) {
      transaction.status = 'completed'
      transaction.updatedAt = new Date()
      console.log(`Transaction ${transaction.uetr} status changed to completed`, transaction)
      if ([config.bankA.bic, config.bankB.bic].includes(transaction.creditor.bic)) {
        const account = state.accounts.find((a) => a.id === transaction.debtor.accountId)
        if (account) {
          account.balance += transaction.debtor.amount
          console.log(`Debtor account ${account.id} balance updated: ${account.balance}`)
        } else {
          console.error('Debtor account not found:', transaction.debtor)
        }
      }
    }
    emitTransactionsUpdated()
  }, 30000)

  setInterval(() => {
    const recipient = faker.helpers.arrayElement(state.clients)
    const currency = faker.helpers.arrayElement(currencies)
    const recipientAccount = state.accounts.find(
      (account) => account.ownerId === recipient.id && account.currency === currency
    )
    if (!recipientAccount) {
      console.error(`No account found for recipient ${recipient.fullName} with currency ${currency}`)
      return
    }
    const incomingTransaction: Transaction = {
      uetr: faker.string.uuid(),
      id: newTransactionId(),
      comment: null,
      status: 'pending',
      type: 'transfer',
      debtor: {
        name: faker.person.fullName(),
        bic: faker.finance.bic(),
        accountId: faker.finance.iban(),
        amount: faker.number.int({ min: 10, max: 500 }),
        currency: faker.helpers.arrayElement(currencies)
      },
      creditor: {
        name: recipient.fullName,
        bic: recipient.bic,
        accountId: recipientAccount.id,
        amount: faker.number.int({ min: 10, max: 500 }),
        currency: faker.helpers.arrayElement(currencies)
      },
      createdAt: new Date(),
      updatedAt: new Date()
    }
    console.log(`Incoming transaction for ${recipient.fullName}:`, incomingTransaction)
    state.transactions.push(incomingTransaction)
    emitTransactionsUpdated()
  }, 50000)
  return state
}
