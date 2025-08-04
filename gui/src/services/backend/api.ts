import { ofetch, FetchError } from 'ofetch'
import { joinURL } from 'ufo'
import { z } from 'zod'
import type {
  getTransactions,
  getTransactionDetails,
  getClientTransactions,
  getClientData,
  newTransaction
} from '../mock-backend/api'
import { useToasts } from '../../composables/useToasts'
import type { BankName } from '../../../config'
import type { Client, Account as FrontendAccount, Transaction } from '../mock-backend/types'
import {
  type Transfer,
  type TransferMessage,
  TransferMessageSchema,
  TransferSchema,
  AccountSchema,
  TransferDetailsSchema,
  type Account
} from './models'
import {
  convertBackendAccountsToFrontend,
  convertBackendTransfersToFrontendJSON,
  convertBackendTransferToFrontendJSON,
  convertFrontendTransactionToBackendTransferDetails
} from './converters'
import config from '../../../config'
import * as hardcoded from '../hardcoded'

function bankBaseRoute(bank: BankName): string {
  return `/${bank.toLowerCase().replace(/ /g, '-')}/api`
}

function handleError(action: string, error: unknown): void {
  const { addToast } = useToasts()
  if (error instanceof FetchError) {
    addToast(`Error occurred while ${action}: ${error.data}`, 'error')
  } else if (error instanceof z.ZodError) {
    addToast(`Wrong message structure while ${action}: ${error.issues.map((e) => e.message).join(', ')}`, 'error')
  } else if (typeof error === 'string') {
    addToast(`Error occurred while ${action}: ${error}`, 'error')
  } else if (error instanceof Error) {
    addToast(`Error occurred while ${action}: ${error.message}`, 'error')
  } else {
    addToast(`Error occurred while ${action}: An unknown error occurred.`, 'error')
  }
}

// Bank admin handler

export async function getTransfers(bank: BankName): Promise<ReturnType<typeof getTransactions>> {
  try {
    const response = await ofetch<Transfer[]>(joinURL(bankBaseRoute(bank), 'transfers'), {
      method: 'GET'
    })
    const parsedResponse = TransferSchema.array().parse(response)
    return convertBackendTransfersToFrontendJSON(parsedResponse)
  } catch (error) {
    handleError('fetching transfers', error)
    return []
  }
}

async function getTransferById(bank: BankName, uetr: string): Promise<Transfer | null> {
  try {
    const response = await ofetch<Transfer | null>(joinURL(bankBaseRoute(bank), 'transfers', uetr), {
      method: 'GET'
    })
    if (!response) {
      return null
    }
    return TransferSchema.parse(response)
  } catch (error) {
    handleError(`fetching transfer with UETR ${uetr}`, error)
    return null
  }
}

async function getTransferMessages(bank: BankName, uetr: string): Promise<TransferMessage[]> {
  try {
    const response = await ofetch<TransferMessage[]>(joinURL(bankBaseRoute(bank), 'transfers', uetr, 'messages'), {
      method: 'GET'
    })
    return TransferMessageSchema.array().parse(response)
  } catch (error) {
    handleError(`fetching transfer messages for UETR ${uetr}`, error)
    return []
  }
}

export async function getTransferStatus(
  bank: BankName,
  uetr: string
): Promise<ReturnType<typeof getTransactionDetails>> {
  const [transfer, messages] = await Promise.all([getTransferById(bank, uetr), getTransferMessages(bank, uetr)])
  if (!transfer) {
    return null
  }
  return { transaction: convertBackendTransferToFrontendJSON(transfer), messages }
}

// Bank client handler

async function getClientAccounts(bank: BankName, clientId: string): Promise<FrontendAccount[]> {
  try {
    const response = await ofetch<Account[]>(joinURL(bankBaseRoute(bank), 'client', 'account'), {
      method: 'GET'
    })
    const parsedResponse = AccountSchema.array().parse(response)
    return convertBackendAccountsToFrontend(parsedResponse)
  } catch (error) {
    handleError(`fetching accounts for client ${clientId}`, error)
    return []
  }
}

async function getClient(bank: BankName): Promise<Client> {
  const bankInfo = bank === 'Bank A' ? config.bankA : config.bankB
  return {
    id: bankInfo.client.id,
    fullName: bankInfo.client.name,
    bic: bankInfo.bic
  }
}

export async function getClientPageData(bank: BankName, clientId: string): Promise<ReturnType<typeof getClientData>> {
  const [client, accounts] = await Promise.all([getClient(bank), getClientAccounts(bank, clientId)])
  return { ...client, accounts }
}

export async function getClientTransfers(
  bank: BankName,
  clientId: string
): Promise<ReturnType<typeof getClientTransactions>> {
  try {
    const response = await ofetch<Transfer[]>(joinURL(bankBaseRoute(bank), 'client', 'transfer'), {
      method: 'GET'
    })
    const parsedResponse = TransferSchema.array().parse(response)
    return convertBackendTransfersToFrontendJSON(parsedResponse)
  } catch (error) {
    handleError(`fetching transfers for client ${clientId}`, error)
    return []
  }
}

export async function makeTransfer(
  bank: BankName,
  transferDetails: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'> & { comment: string | null }
): Promise<ReturnType<typeof newTransaction>> {
  try {
    const convertedDetails = convertFrontendTransactionToBackendTransferDetails(transferDetails)
    const response = await ofetch<Transfer>(joinURL(bankBaseRoute(bank), 'client', 'makeTransfer'), {
      method: 'POST',
      body: TransferDetailsSchema.parse(convertedDetails)
    })
    if (!response) {
      return null
    }
    const createdTransfer = TransferSchema.parse(response)
    return convertBackendTransferToFrontendJSON(createdTransfer)
  } catch (error) {
    handleError('creating a new transfer', error)
    throw new Error('Failed to create transfer')
  }
}

async function getAvailableCurrencies(bank: BankName): Promise<string[]> {
  try {
    const response = await ofetch<string[]>(joinURL(bankBaseRoute(bank), 'helpers', 'currencies'), {
      method: 'GET'
    })
    return z.string().array().parse(response)
  } catch (error) {
    handleError(`fetching available currencies for ${bank}`, error)
    return []
  }
}

async function getExchangeValues(bank: BankName): Promise<Record<string, number>> {
  try {
    const response = await ofetch<Record<string, number>>(joinURL(bankBaseRoute(bank), 'helpers', 'exchangeValues'), {
      method: 'GET'
    })
    return z.record(z.string(), z.number()).parse(response)
  } catch (error) {
    handleError(`fetching exchange values for ${bank}`, error)
    return {}
  }
}

async function getIbans(bank: BankName): Promise<string[]> {
  try {
    const contraBank = bank === 'Bank A' ? 'Bank B' : 'Bank A'
    const response = await ofetch<string[]>(joinURL(bankBaseRoute(contraBank), 'helpers', 'ibans'), {
      method: 'GET'
    })
    return z.string().array().parse(response)
  } catch (error) {
    handleError(`fetching IBANs for ${bank}`, error)
    return []
  }
}

export async function getTransferHelpers(bank: BankName): Promise<{
  currencies: string[]
  exchangeValues: Record<string, number>
  ibans: string[]
  bics: string[]
}> {
  let [currencies, exchangeValues, ibans] = await Promise.all([
    getAvailableCurrencies(bank),
    getExchangeValues(bank),
    getIbans(bank)
  ])
  if (currencies.length === 0) {
    currencies = hardcoded.currencies
  }
  if (Object.keys(exchangeValues).length === 0) {
    exchangeValues = hardcoded.exchangeValues
  }
  if (ibans.length === 0) {
    ibans = bank === 'Bank A' ? hardcoded.bankBIbans : hardcoded.bankAIbans
  }
  return { currencies, exchangeValues, ibans, bics: bank === 'Bank A' ? [config.bankB.bic] : [config.bankA.bic] }
}
