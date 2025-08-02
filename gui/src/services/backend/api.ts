import { ofetch, FetchError } from 'ofetch'
import { joinURL } from 'ufo'
import { z } from 'zod'
import type { getTransactions, getTransactionDetails } from '../mock-backend/api'
import { useToasts } from '../../composables/useToasts'
import type { BankName } from '../../../config'
import { type Transfer, type TransferMessage, TransferMessageSchema, TransferSchema } from './models'
import { convertBackendTransfersToFrontendJSON, convertBackendTransferToFrontendJSON } from './converters'

function bankBaseRoute(bank: BankName): string {
  return `/${bank.toLowerCase().replace(/ /g, '')}/api`
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
    const response = await ofetch<Transfer | null>(joinURL(bankBaseRoute(bank), `transfers/${uetr}`), {
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
    const response = await ofetch<TransferMessage[]>(joinURL(bankBaseRoute(bank), `transfers/${uetr}/messages`), {
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
