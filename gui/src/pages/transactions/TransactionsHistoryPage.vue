<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import { useAsyncState } from '@vueuse/core'
import {
  fetchTransactions,
  subscribeToTransactionsUpdates
} from '../../services/transactions.js'
import FilterControls from '../../components/FilterControls.vue'
import TransactionsTable from '../../components/TransactionsTable.vue'

const { state: transactions } = useAsyncState(fetchTransactions, [])
const filteredTransactions = ref(transactions.value)

const { state: unsubscribe } = useAsyncState(
  subscribeToTransactionsUpdates((transactionsUpdated) => {
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
    <h1>Transactions History</h1>
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
