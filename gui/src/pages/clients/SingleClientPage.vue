<script setup lang="ts">
import ClientInfoWidget from '../../components/ClientInfoWidget.vue'
import TransactionsTable from '../../components/TransactionsTable.vue'
import { useClientInfo } from '../../composables/useClientInfo'
import { useAsyncState } from '@vueuse/core'
import { fetchClientTransactions } from '../../services/clients'
import { useRoute } from 'vue-router'
import { useFakeSocket } from '../../composables/useFakeSocket'

const route = useRoute()
const clientId = route.meta.clientId as string
const { client, isLoading, refresh: refreshClientInfo } = useClientInfo()
const { state: transactions, isLoading: isLoadingTransactions } = useAsyncState(
  () => fetchClientTransactions(clientId),
  []
)

async function refreshTransactions() {
  isLoadingTransactions.value = true
  transactions.value = await fetchClientTransactions(clientId)
  isLoadingTransactions.value = false
}

async function refresh() {
  await Promise.all([refreshClientInfo(), refreshTransactions()])
}

useFakeSocket(refresh)
</script>
<template>
  <div>
    <ClientInfoWidget :client="client" :isLoading="isLoading" show-accounts />
    <section class="mt-10">
      <h2 class="section-title max-w-6xl mx-auto">Transaction Table</h2>
      <TransactionsTable :transactions="transactions" :isLoading="isLoadingTransactions" client-mode />
    </section>
  </div>
</template>
