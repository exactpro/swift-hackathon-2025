<script setup lang="ts">
import ClientInfoWidget from '../../components/ClientInfoWidget.vue'
import TransactionsTable from '../../components/TransactionsTable.vue'
import { useClientInfo } from '../../composables/useClientInfo'
import { useAsyncState } from '@vueuse/core'
import { fetchClientTransactions } from '../../services/clients'
import { useRoute } from 'vue-router'
import { useFakeSocket } from '../../composables/useFakeSocket'
import BalanceWidget from '../../components/BalanceWidget.vue'
import { computed } from 'vue'
import { useHead } from '@unhead/vue'

const route = useRoute()
const clientId = route.meta.clientId as string
const { client, isLoading, refresh: refreshClientInfo } = useClientInfo()
const { state: transactions, isLoading: isLoadingTransactions } = useAsyncState(
  () => fetchClientTransactions(clientId),
  []
)

const title = computed(() => {
  return client.value ? `${client.value.fullName}'s Dashboard` : 'Client Dashboard'
})

useHead({ title })

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
  <div class="">
    <ClientInfoWidget :client="client" :isLoading="isLoading" />
    <section class="mt-10" v-if="client">
      <h2 class="section-title">Balance</h2>
      <BalanceWidget :accounts="client.accounts" class="-mx-3 md:-mx-6 lg:-mx-10 pl-3 md:pl-6 lg:pl-10" />
    </section>
    <section class="mt-10">
      <h2 class="section-title">Transaction Table</h2>
      <TransactionsTable :transactions="transactions" :isLoading="isLoadingTransactions" client-mode />
    </section>
  </div>
</template>
