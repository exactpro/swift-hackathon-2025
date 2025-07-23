<script setup lang="ts">
import SortBtn from '../SortBtn.vue'
import { isRef, toRef, ref, watch } from 'vue'
import { type MaybeRef } from '@vueuse/core'

import { usePagination } from '../../composables/usePagination.js'
import PaginationNav from '../PaginationNav.vue'
import type { SortItem } from '../SortBtn.vue'
import type { JSONify, Transaction } from '../../services/mock-backend/types.js'
import TransactionsTableHeader from './TransactionsTableHeader.vue'
import TransactionsTableRow from './TransactionsTableRow.vue'
import { getDirection } from '../../utils/transactions.js'
import { useRoute } from 'vue-router'

const props = defineProps<{
  transactions: JSONify<Transaction>[]
}>()

const route = useRoute()
const ownBic = route.meta.bic as string

const localTransactions = ref(props.transactions)

const sortQueue: SortItem[] = []

function sortBy(items: MaybeRef<JSONify<Transaction>[]>, { label: _, getProp, direction = 'asc' }: SortItem) {
  function sortCallback(a: JSONify<Transaction>, b: JSONify<Transaction>) {
    const aValue = getProp(a)
    const bValue = getProp(b)

    if (aValue < bValue) return direction === 'asc' ? -1 : 1
    if (aValue > bValue) return direction === 'asc' ? 1 : -1
    return 0
  }
  if (isRef(items)) {
    // Pagination computed does not detect sorting of a ref array
    items.value = items.value.slice().sort(sortCallback)
    return
  }
  // If items is not a ref, we assume it's a plain array
  items.sort(sortCallback)
}

function sortFromTable(sortItem: SortItem) {
  sortBy(localTransactions, sortItem)
  // Delete new sort item if it already exists
  const existingIndex = sortQueue.findIndex((item) => item.label === sortItem.label)
  if (existingIndex !== -1) {
    sortQueue.splice(existingIndex, 1)
  }
  sortQueue.push(sortItem)
}

watch(
  toRef(props, 'transactions'),
  (newTransactions) => {
    localTransactions.value = newTransactions
    for (const sortItem of sortQueue) {
      sortBy(localTransactions, sortItem)
    }
  },
  { immediate: true }
)

const {
  currentPageItems: displayedTransactions,
  beforeButtons,
  currentPage,
  afterButtons
} = usePagination(localTransactions, {})

const emit = defineEmits<{
  (e: 'transactionAccepted', acceptedTransaction: JSONify<Transaction> | null): void
  (e: 'transactionRejected', rejectedTransaction: JSONify<Transaction> | null): void
}>()
</script>

<template>
  <div>
    <div class="mb-4 flex justify-center">
      <PaginationNav :currentPage="currentPage" :beforeButtons="beforeButtons" :afterButtons="afterButtons" />
    </div>
    <div class="overflow-x-auto">
      <table class="table table-xs">
        <TransactionsTableHeader>
          <template #type-btn>
            <SortBtn label="Type" :getProp="(item) => item.type" @sort="sortFromTable" />
          </template>
          <template #dir-btn>
            <SortBtn label="Direction" :getProp="(item) => getDirection(ownBic, item)" @sort="sortFromTable" />
          </template>
          <template #status-btn>
            <SortBtn label="Status" :getProp="(item) => item.status" @sort="sortFromTable" />
          </template>
          <template #uetr-btn>
            <SortBtn label="UETR" :getProp="(item) => item.uetr" @sort="sortFromTable" />
          </template>
          <template #debtor-bic-btn>
            <SortBtn label="Debtor BIC" :getProp="(item) => item.debtor.bic" @sort="sortFromTable" />
          </template>
          <template #debtor-client-btn>
            <SortBtn label="Debtor Client" :getProp="(item) => item.debtor.name" @sort="sortFromTable" />
          </template>
          <template #debtor-amount-btn>
            <SortBtn label="Debtor Amount" :getProp="(item) => parseFloat(item.debtor.amount)" @sort="sortFromTable" />
          </template>
          <template #debtor-currency-btn>
            <SortBtn label="Debtor Currency" :getProp="(item) => item.debtor.currency" @sort="sortFromTable" />
          </template>
          <template #creditor-bic-btn>
            <SortBtn label="Creditor BIC" :getProp="(item) => item.creditor.bic" @sort="sortFromTable" />
          </template>
          <template #creditor-client-btn>
            <SortBtn label="Creditor Client" :getProp="(item) => item.creditor.name" @sort="sortFromTable" />
          </template>
          <template #creditor-amount-btn>
            <SortBtn
              label="Creditor Amount"
              :getProp="(item) => parseFloat(item.creditor.amount)"
              @sort="sortFromTable"
            />
          </template>
          <template #creditor-currency-btn>
            <SortBtn label="Creditor Currency" :getProp="(item) => item.creditor.currency" @sort="sortFromTable" />
          </template>
          <template #created-btn>
            <SortBtn label="Created" :getProp="(item) => new Date(item.createdAt)" @sort="sortFromTable" />
          </template>
          <template #updated-btn>
            <SortBtn label="Updated" :getProp="(item) => new Date(item.updatedAt)" @sort="sortFromTable" />
          </template>
        </TransactionsTableHeader>
        <tbody v-if="displayedTransactions">
          <TransactionsTableRow
            v-for="transaction in displayedTransactions"
            :transaction="transaction"
            :key="transaction.uetr"
            @transaction-accepted="emit('transactionAccepted', $event)"
            @transaction-rejected="emit('transactionRejected', $event)"
          />
        </tbody>
      </table>
    </div>
    <div class="mt-4 flex justify-center">
      <PaginationNav :currentPage="currentPage" :beforeButtons="beforeButtons" :afterButtons="afterButtons" />
    </div>
  </div>
</template>
