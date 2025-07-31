import { useAsyncState } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { fetchClientData } from '../services/clients'

export function useClientInfo() {
  const route = useRoute()
  const clientId = route.meta.clientId as string

  const { state: client, isLoading } = useAsyncState(() => fetchClientData(clientId), null)

  async function refresh() {
    isLoading.value = true
    const newData = await fetchClientData(clientId)
    if (JSON.stringify(newData) === JSON.stringify(client.value)) {
      isLoading.value = false
      return
    }
    client.value = newData
    isLoading.value = false
  }

  return {
    client,
    isLoading,
    refresh
  }
}
