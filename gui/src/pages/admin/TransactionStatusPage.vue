<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { fetchTransactionDetails } from '../../services/transactions'
import { useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { computed, ref } from 'vue'
import { formatAccountBalance } from '../../utils/formatNumber'
import { useFakeSocket } from '../../composables/useFakeSocket'

const route = useRoute()
const uetr = route.params.uetr as string

const { state: transactionDetails, isLoading } = useAsyncState(() => fetchTransactionDetails(uetr), null)

// Track expanded items
const expandedItems = ref<Set<string>>(new Set())

// Computed property to sort messages by timestamp
const sortedMessages = computed(() => {
  if (!transactionDetails.value?.messages) return []
  return [...transactionDetails.value.messages].sort(
    (a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()
  )
})

function toggleExpanded(messageId: string) {
  if (expandedItems.value.has(messageId)) {
    expandedItems.value.delete(messageId)
  } else {
    expandedItems.value.add(messageId)
  }
}

function isExpanded(messageId: string) {
  return expandedItems.value.has(messageId)
}

async function refresh() {
  isLoading.value = true
  transactionDetails.value = await fetchTransactionDetails(uetr)
  isLoading.value = false
}

useFakeSocket(refresh)
</script>

<template>
  <div class="container mx-auto p-6 max-w-6xl">
    <!-- Header Section -->
    <div class="mb-8">
      <div class="mb-4">
        <h1 class="text-3xl font-bold">Transaction Status</h1>
      </div>
      <p class="text-base-content/70" v-if="uetr">
        Tracking transaction: <code class="badge badge-neutral font-mono">{{ uetr }}</code>
      </p>
    </div>

    <!-- Loading State -->
    <div v-if="!transactionDetails && isLoading" class="flex justify-center items-center py-12">
      <span class="loading loading-spinner loading-lg"></span>
    </div>

    <!-- Transaction Details -->
    <div v-else-if="transactionDetails" class="space-y-6">
      <!-- Basic Transaction Info -->
      <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
          <h2 class="card-title">
            <Icon icon="mdi:bank-transfer" />
            Transaction Details
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="stat">
              <div class="stat-title">Status</div>
              <div class="stat-value text-lg">
                <div
                  class="badge"
                  :class="{
                    'badge-success': transactionDetails.transaction.status === 'completed',
                    'badge-warning': transactionDetails.transaction.status === 'pending',
                    'badge-error': transactionDetails.transaction.status === 'rejected',
                    'badge-info': transactionDetails.transaction.status === 'cancelled'
                  }"
                >
                  {{ transactionDetails.transaction.status }}
                </div>
              </div>
            </div>
            <div class="stat">
              <div class="stat-title">Type</div>
              <div class="stat-value text-lg">
                <div class="badge badge-outline">{{ transactionDetails.transaction.type }}</div>
              </div>
            </div>
            <div class="stat">
              <div class="stat-title">Amount</div>
              <div class="stat-value text-lg">
                {{
                  formatAccountBalance(
                    transactionDetails.transaction.creditor?.currency,
                    transactionDetails.transaction.creditor?.amount
                  )
                }}
              </div>
            </div>
            <div class="stat">
              <div class="stat-title">Created</div>
              <div class="stat-value text-lg">
                {{ new Date(transactionDetails.transaction.createdAt).toLocaleString() }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Debtor and Creditor Info -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Debtor -->
        <div class="card bg-base-200 shadow-xl">
          <div class="card-body">
            <h3 class="card-title">
              <Icon icon="mdi:arrow-up" class="text-error" />
              Debtor
            </h3>
            <div class="space-y-2">
              <div><strong>Name:</strong> {{ transactionDetails.transaction.debtor?.name }}</div>
              <div>
                <strong>BIC:</strong>
                <code class="badge badge-ghost">{{ transactionDetails.transaction.debtor?.bic }}</code>
              </div>
              <div>
                <strong>Account:</strong>
                <code class="badge badge-ghost">{{ transactionDetails.transaction.debtor?.accountId }}</code>
              </div>
            </div>
          </div>
        </div>

        <!-- Creditor -->
        <div class="card bg-base-200 shadow-xl">
          <div class="card-body">
            <h3 class="card-title">
              <Icon icon="mdi:arrow-down" class="text-success" />
              Creditor
            </h3>
            <div class="space-y-2">
              <div><strong>Name:</strong> {{ transactionDetails.transaction.creditor?.name }}</div>
              <div>
                <strong>BIC:</strong>
                <code class="badge badge-ghost">{{ transactionDetails.transaction.creditor?.bic }}</code>
              </div>
              <div>
                <strong>Account:</strong>
                <code class="badge badge-ghost">{{ transactionDetails.transaction.creditor?.accountId }}</code>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Messages Timeline -->
      <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
          <h2 class="card-title">
            <Icon icon="mdi:message-text-clock" />
            Message Timeline
            <div class="badge badge-neutral">{{ sortedMessages.length }} messages</div>
          </h2>

          <div v-if="sortedMessages.length === 0" class="text-center py-8 text-base-content/50">
            <Icon icon="mdi:message-off" class="text-4xl mb-2" />
            <p>No messages found</p>
          </div>

          <ul v-else class="menu bg-base-200 rounded-box w-full">
            <li v-for="(message, index) in sortedMessages" :key="index">
              <details :open="isExpanded(index.toString())">
                <summary
                  @click.prevent="toggleExpanded(index.toString())"
                  class="flex items-center justify-between cursor-pointer"
                >
                  <div class="flex items-center gap-3">
                    <Icon icon="mdi:message" class="text-primary" />
                    <div>
                      <div class="font-medium">{{ message.type || 'Message' }}</div>
                      <div class="text-sm text-base-content/70">
                        {{ new Date(message.timestamp).toLocaleString() }}
                      </div>
                    </div>
                  </div>
                  <div class="flex items-center gap-2">
                    <div class="badge badge-outline">
                      {{ message.title || 'Message' }}
                    </div>
                  </div>
                </summary>

                <!-- Message Summary (Key-Value pairs) - Always rendered, visibility controlled by v-show -->
                <div
                  v-show="isExpanded(index.toString())"
                  class="ml-8 mt-4 p-4 bg-neutral text-neutral-content rounded-lg transition-all duration-300"
                >
                  <h4 class="font-semibold mb-3 flex items-center gap-2">
                    <Icon icon="mdi:information" />
                    Message Summary
                  </h4>
                  <div v-if="message.summary && Object.keys(message.summary).length > 0" class="grid grid-cols-1 gap-2">
                    <div
                      v-for="[key, value] in Object.entries(message.summary)"
                      :key="key"
                      class="flex justify-between items-center p-2 bg-base-300 rounded gap-3"
                    >
                      <span class="font-medium text-sm">{{ key }}:</span>
                      <span class="text-sm font-mono">{{ value }}</span>
                    </div>
                  </div>
                  <div v-else class="text-base-content/50 text-sm">No summary data available</div>
                </div>
              </details>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else class="alert alert-error">
      <Icon icon="mdi:alert-circle" />
      <span>Failed to load transaction details</span>
    </div>
  </div>
</template>
