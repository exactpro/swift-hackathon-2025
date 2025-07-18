<script setup lang="ts">
import { computed, onBeforeUnmount } from 'vue'
import { useAsyncState } from '@vueuse/core'
import {
  fetchTransactions,
  subscribeToTransactionsUpdates
} from '../../services/transactions.js'

import UUID from '../../components/UUID.vue'
import RelDate from '../../components/RelDate.vue'
import config from '../../../config.js'

const { state: transactions } = useAsyncState(fetchTransactions, null)
const { state: unsubscribe } = useAsyncState(
  subscribeToTransactionsUpdates(
    (transactionsUpdated) => (transactions.value = transactionsUpdated)
  ),
  () => {}
)
const displayedTransactions = computed(() => {
  if (!transactions.value) return []
  // First 10 transactions for display
  return transactions.value.slice(0, 10)
})

onBeforeUnmount(() => {
  unsubscribe.value()
})
</script>

<template>
  <div>
    <h1>Transactions History</h1>
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
            <th>Type</th>
            <th>Status</th>
            <th>UETR</th>
            <th class="border-l-2 border-base-content">BIC</th>
            <th>Client</th>
            <th>Amount</th>
            <th>Currency</th>
            <th class="border-l-2 border-base-content">BIC</th>
            <th>Client</th>
            <th>Amount</th>
            <th class="border-r-2 border-base-content">Currency</th>
            <th>Created</th>
            <th>Updated</th>
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
  </div>
</template>
