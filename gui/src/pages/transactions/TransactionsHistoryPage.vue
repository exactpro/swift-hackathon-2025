<script setup lang="ts">
import { onBeforeUnmount, isRef, ref } from 'vue'
import { useAsyncState, type MaybeRef } from '@vueuse/core'
import {
  fetchTransactions,
  subscribeToTransactionsUpdates
} from '../../services/transactions.js'

import UUID from '../../components/UUID.vue'
import RelDate from '../../components/RelDate.vue'
import { usePagination } from '../../composables/usePagination.js'
import config from '../../../config.js'
import SortBtn from '../../components/SortBtn.vue'
import PaginationNav from '../../components/PaginationNav.vue'
import FilterControls from '../../components/FilterControls.vue'
import type { SortItem } from '../../components/SortBtn.vue'
import type { JSONify, Transaction } from '../../services/mock-backend/types.js'

const { state: transactions } = useAsyncState(fetchTransactions, [])
const filteredTransactions = ref(transactions.value)

const sortQueue: SortItem[] = []

function sortBy(
  items: MaybeRef<JSONify<Transaction>[]>,
  { label: _, getProp, direction = 'asc' }: SortItem
) {
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
  sortBy(filteredTransactions, sortItem)
  // Delete new sort item if it already exists
  const existingIndex = sortQueue.findIndex(
    (item) => item.label === sortItem.label
  )
  if (existingIndex !== -1) {
    sortQueue.splice(existingIndex, 1)
  }
  sortQueue.push(sortItem)
}

const { state: unsubscribe } = useAsyncState(
  subscribeToTransactionsUpdates((transactionsUpdated) => {
    transactions.value = transactionsUpdated
  }),
  () => {}
)

const {
  currentPageItems: displayedTransactions,
  beforeButtons,
  currentPage,
  afterButtons
} = usePagination(filteredTransactions, {})

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
          for (const sortItem of sortQueue) {
            sortBy(filteredTransactions, sortItem)
          }
        }
      "
    />

    <div class="mb-4 flex justify-center">
      <PaginationNav
        :currentPage="currentPage"
        :beforeButtons="beforeButtons"
        :afterButtons="afterButtons"
      />
    </div>

    <div class="overflow-x-auto">
      <table class="table table-xs">
        <thead>
          <tr>
            <th></th>
            <th></th>
            <th></th>
            <th colspan="4" class="border-l-2 border-base-content">Debtor</th>
            <th colspan="4" class="border-x-2 border-base-content">Creditor</th>
            <th></th>
            <th></th>
            <th></th>
          </tr>
          <tr>
            <th>
              Type
              <SortBtn
                label="Type"
                :getProp="(item) => item.type"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Status
              <SortBtn
                label="Status"
                :getProp="(item) => item.status"
                @sort="sortFromTable"
              />
            </th>
            <th>
              UETR
              <SortBtn
                label="UETR"
                :getProp="(item) => item.uetr"
                @sort="sortFromTable"
              />
            </th>
            <th class="border-l-2 border-base-content">
              BIC
              <SortBtn
                label="Debtor BIC"
                :getProp="(item) => item.debtor.bic"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Client
              <SortBtn
                label="Debtor Client"
                :getProp="(item) => item.debtor.clientId"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Amount
              <SortBtn
                label="Debtor Amount"
                :getProp="(item) => parseFloat(item.debtor.amount)"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Currency
              <SortBtn
                label="Debtor Currency"
                :getProp="(item) => item.debtor.currency"
                @sort="sortFromTable"
              />
            </th>
            <th class="border-l-2 border-base-content">
              BIC
              <SortBtn
                label="Creditor BIC"
                :getProp="(item) => item.creditor.bic"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Client
              <SortBtn
                label="Creditor Client"
                :getProp="(item) => item.creditor.clientId"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Amount
              <SortBtn
                label="Creditor Amount"
                :getProp="(item) => parseFloat(item.creditor.amount)"
                @sort="sortFromTable"
              />
            </th>
            <th class="border-r-2 border-base-content">
              Currency
              <SortBtn
                label="Creditor Currency"
                :getProp="(item) => item.creditor.currency"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Created
              <SortBtn
                label="Created"
                :getProp="(item) => new Date(item.createdAt)"
                @sort="sortFromTable"
              />
            </th>
            <th>
              Updated
              <SortBtn
                label="Updated"
                :getProp="(item) => new Date(item.updatedAt)"
                @sort="sortFromTable"
              />
            </th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody v-if="transactions">
          <tr
            v-for="transaction in displayedTransactions"
            :key="transaction.uetr"
          >
            <td>
              <span
                class="badge badge-xs"
                :class="{
                  'badge-primary': transaction.type === 'transfer',
                  'badge-secondary': transaction.type === 'incoming transfer',
                  'badge-accent': transaction.type === 'cancel'
                }"
              >
                {{ transaction.type }}
              </span>
            </td>
            <td>
              <span
                class="badge badge-xs"
                :class="{
                  'badge-success': transaction.status === 'completed',
                  'badge-warning': transaction.status === 'pending',
                  'badge-info': transaction.status === 'cancelled',
                  'badge-error': transaction.status === 'rejected'
                }"
              >
                {{ transaction.status }}
              </span>
            </td>
            <td><UUID :uuid="transaction.uetr" /></td>
            <td
              class="border-l-2 border-base-content"
              :class="{
                'text-accent': transaction.debtor.bic === config.ownBic
              }"
            >
              {{ transaction.debtor.bic }}
            </td>
            <td><UUID :uuid="transaction.debtor.clientId" /></td>
            <td>{{ transaction.debtor.amount }}</td>
            <td>{{ transaction.debtor.currency }}</td>
            <td
              class="border-l-2 border-base-content"
              :class="{
                'text-accent': transaction.creditor.bic === config.ownBic
              }"
            >
              {{ transaction.creditor.bic }}
            </td>
            <td><UUID :uuid="transaction.creditor.clientId" /></td>
            <td>{{ transaction.creditor.amount }}</td>
            <td class="border-r-2 border-base-content">
              {{ transaction.creditor.currency }}
            </td>
            <td>
              <RelDate class="badge-success" :date="transaction.createdAt" />
            </td>
            <td>
              <RelDate class="badge-info" :date="transaction.updatedAt" />
            </td>
            <td>
              <RouterLink
                :to="`/transactions/${transaction.uetr}`"
                class="btn btn-primary btn-sm"
              >
                View
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="mt-4 flex justify-center">
      <PaginationNav
        :currentPage="currentPage"
        :beforeButtons="beforeButtons"
        :afterButtons="afterButtons"
      />
    </div>
  </div>
</template>
