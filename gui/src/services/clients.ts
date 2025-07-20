import config from '../../config.js'
import type { Client, JSONify, Account } from './mock-backend/types.js'

interface AllClientsResponseItem extends JSONify<Client> {
  accounts: {
    EUR?: JSONify<Account>
    USD?: JSONify<Account>
    SUSDC?: JSONify<Account>
  }
}

export async function fetchAllClients(): Promise<AllClientsResponseItem[]> {
  if (config.useMock) {
    const { getClients } = await import('./mock-backend/api.js')
    return getClients()
  }
  return []
}
