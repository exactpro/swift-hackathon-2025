<script setup lang="ts">
import ClientInfoWidget from '../../components/ClientInfoWidget.vue'
import TransactionsTable from '../../components/TransactionsTable.vue'
import { useClientInfo } from '../../composables/useClientInfo'
import { useAsyncState } from '@vueuse/core'
import { fetchClientTransactions } from '../../services/clients'
import { useRoute } from 'vue-router'
import Breadcrumbs from '../../components/Breadcrumbs.vue'
import { useBankRoute } from '../../composables/useBankRoute'

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

const homeLink = useBankRoute()
</script>
<template>
  <div>
    <Breadcrumbs :items="[{ title: 'Home', link: homeLink }]" />
    <ClientInfoWidget :client="client" :isLoading="isLoading" :refresh="refresh" />
    <TransactionsTable :transactions="transactions" :isLoading="isLoadingTransactions" client-mode />
  </div>
</template>
