import type {
  Account as BackendAccount,
  Transfer as BackendTransfer,
  TransferDetails as BackendTransferDetails,
  TransferStatus as BackendTransferStatus
} from './models'
import type {
  Client,
  Account as FrontendAccount,
  Transaction,
  TransactionParty,
  Currency,
  TransactionStatus,
  JSONify
} from '../mock-backend/types'

/**
 * Convert backend account to frontend account format
 */
export function convertBackendAccountToFrontend(backendAccount: BackendAccount): FrontendAccount {
  return {
    id: backendAccount.iban,
    ownerId: backendAccount.clientId.toString(),
    balance: backendAccount.balance,
    currency: backendAccount.currencyCode as Currency
  }
}

/**
 * Convert array of backend accounts to frontend accounts
 */
export function convertBackendAccountsToFrontend(backendAccounts: BackendAccount[]): FrontendAccount[] {
  return backendAccounts.map(convertBackendAccountToFrontend)
}

/**
 * Convert backend transfer status to frontend transaction status
 */
export function convertBackendTransferStatusToFrontend(backendStatus: BackendTransferStatus): TransactionStatus {
  switch (backendStatus) {
    case 'PENDING':
      return 'pending'
    case 'COMPLETED':
      return 'completed'
    case 'FAILED':
      return 'rejected'
    default:
      return 'pending'
  }
}

/**
 * Convert backend transfer to frontend transaction format
 */
export function convertBackendTransferToFrontend(backendTransfer: BackendTransfer): Transaction {
  // Create debtor party
  const debtor: TransactionParty = {
    name: backendTransfer.debtorFullName || 'Unknown',
    bic: backendTransfer.debtorBic || '',
    accountId: backendTransfer.debtorIban,
    amount: backendTransfer.amount,
    currency: backendTransfer.currency as Currency
  }

  // Create creditor party
  const creditor: TransactionParty = {
    name: backendTransfer.creditorFullName,
    bic: backendTransfer.creditorBic,
    accountId: backendTransfer.creditorIban,
    amount: backendTransfer.amount,
    currency: backendTransfer.currency as Currency
  }

  return {
    uetr:
      backendTransfer.transferId?.toString() || backendTransfer.endToEndId || backendTransfer.messageId || 'unknown',
    status: convertBackendTransferStatusToFrontend(backendTransfer.status),
    type: 'transfer',
    debtor,
    creditor,
    createdAt: backendTransfer.transferTimestamp ? new Date(backendTransfer.transferTimestamp) : new Date(),
    updatedAt: backendTransfer.transferTimestamp ? new Date(backendTransfer.transferTimestamp) : new Date()
  }
}

/**
 * Convert array of backend transfers to frontend transactions
 */
export function convertBackendTransfersToFrontend(backendTransfers: BackendTransfer[]): Transaction[] {
  return backendTransfers.map(convertBackendTransferToFrontend)
}

export function convertBackendTransferToFrontendJSON(backendTransfer: BackendTransfer): JSONify<Transaction> {
  const converted = convertBackendTransferToFrontend(backendTransfer)
  return {
    ...converted,
    createdAt: new Date(converted.createdAt).toISOString(),
    updatedAt: new Date(converted.updatedAt).toISOString()
  }
}

/**
 * Convert array of backend transfers to JSONified frontend transactions
 */
export function convertBackendTransfersToFrontendJSON(backendTransfers: BackendTransfer[]): JSONify<Transaction>[] {
  return backendTransfers.map(convertBackendTransferToFrontendJSON)
}

/**
 * Convert frontend transaction to backend transfer details for creation
 */
export function convertFrontendTransactionToBackendTransferDetails(
  transaction: Omit<Transaction, 'uetr' | 'createdAt' | 'updatedAt' | 'status'> & { comment: string | null }
): BackendTransferDetails {
  return {
    debtorIban: transaction.debtor.accountId,
    creditorFullName: transaction.creditor.name,
    creditorBic: transaction.creditor.bic,
    creditorIban: transaction.creditor.accountId,
    currencyCode: transaction.debtor.currency,
    amount: transaction.debtor.amount,
    comment: transaction.comment || undefined
  }
}

/**
 * Create a client data response from backend account data
 */
export function createClientDataResponseFromBackendAccounts(
  clientId: string,
  clientName: string,
  clientBic: string,
  backendAccounts: BackendAccount[]
): JSONify<Client> & { accounts: JSONify<FrontendAccount>[] } {
  const client: JSONify<Client> = {
    id: clientId,
    fullName: clientName,
    bic: clientBic
  }

  const accounts: JSONify<FrontendAccount>[] = backendAccounts.map((account) => ({
    id: account.iban,
    ownerId: account.clientId.toString(),
    balance: account.balance,
    currency: account.currencyCode as Currency
  }))

  return {
    ...client,
    accounts
  }
}
