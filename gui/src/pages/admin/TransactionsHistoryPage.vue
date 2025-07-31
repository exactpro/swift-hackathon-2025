<script setup lang="ts">
import { onBeforeUnmount } from 'vue'
import { useAsyncState } from '@vueuse/core'
import { fetchTransactions, subscribeToTransactionsUpdates } from '../../services/transactions.js'
import TransactionsTable from '../../components/TransactionsTable.vue'
import { useRoute } from 'vue-router'
import { useHead } from '@unhead/vue'

useHead({
  title: 'Transactions History'
})

const route = useRoute()
const bic = route.meta.bic as string

const { state: transactions } = useAsyncState(() => fetchTransactions(bic), [])

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
    <div class="grid grid-cols-2 my-4">
      <h1 class="text-lg font-bold inline-block">Transactions History</h1>
    </div>

    <TransactionsTable :transactions="transactions" />
  </div>
</template>
