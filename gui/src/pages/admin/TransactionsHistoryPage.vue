<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { useAsyncState } from '@vueuse/core'
import { fetchTransactions, subscribeToTransactionsUpdates } from '../../services/transactions.js'
import FilterControls from '../../components/FilterControls.vue'
import TransactionsTable from '../../components/TransactionsTable/TransactionsTable.vue'
import Breadcrumbs from '../../components/Breadcrumbs.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const bic = route.meta.bic as string

const { state: transactions } = useAsyncState(() => fetchTransactions(bic), [])
const filteredTransactions = ref(transactions.value)

const { state: unsubscribe } = useAsyncState(
  subscribeToTransactionsUpdates(bic, (transactionsUpdated) => {
    transactions.value = transactionsUpdated
  }),
  () => {}
)

onBeforeUnmount(() => {
  unsubscribe.value()
})
</script>

<template>
  <div>
    <Breadcrumbs :items="[{ title: 'Transactions', link: '/transactions' }]" />
    <div class="grid grid-cols-2 my-4">
      <h1 class="text-lg font-bold inline-block">Transactions History</h1>
      <div class="flex justify-end flex-wrap gap-2">
        <RouterLink class="btn btn-primary btn-sm" :to="`/transactions/new`"> Transfer Funds </RouterLink>
        <RouterLink class="btn btn-primary btn-sm" :to="`/exchange`"> Exchange Currency </RouterLink>
      </div>
    </div>
    <FilterControls
      :items="transactions"
      @filtered="
        (filteredTransactionsNew) => {
          filteredTransactions = filteredTransactionsNew
        }
      "
    />

    <TransactionsTable :transactions="filteredTransactions" />
  </div>
</template>
