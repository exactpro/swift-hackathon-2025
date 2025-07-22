<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { fetchTransactionDetails } from '../../services/transactions'
import { useRoute } from 'vue-router'
import TransactionsTableHeader from '../../components/TransactionsTable/TransactionsTableHeader.vue'
import TransactionsTableRow from '../../components/TransactionsTable/TransactionsTableRow.vue'
import MessageBlock from '../../components/TransactionStatus/MessageBlock.vue'
import { Icon } from '@iconify/vue'

const route = useRoute()
const uetr = route.params.uetr as string

const { state: transactionDetails, isLoading } = useAsyncState(() => fetchTransactionDetails(uetr), null)

async function refresh() {
  isLoading.value = true
  transactionDetails.value = await fetchTransactionDetails(uetr)
  isLoading.value = false
}
</script>

<template>
  <div class="container mx-auto p-6 max-w-6xl">
    <div class="prose max-w-none mb-8">
      <h1 class="text-3xl font-bold mb-4">Transaction Status</h1>
      <p class="text-lg text-gray-600" v-if="uetr">
        Tracking transaction: <code class="bg-base-200 px-2 py-1 rounded">{{ uetr }}</code>
      </p>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center items-center py-12">
      <span class="loading loading-spinner loading-lg"></span>
    </div>

    <!-- Transaction Details -->
    <div v-else-if="transactionDetails" class="space-y-8">
      <!-- Transaction Overview -->
      <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
          <h2 class="card-title text-xl mb-6">Transaction Overview</h2>
          <div class="overflow-x-auto">
            <table class="table table-zebra">
              <TransactionsTableHeader />
              <tbody>
                <TransactionsTableRow :transaction="transactionDetails.transaction" />
              </tbody>
            </table>
          </div>

          <div class="card-actions justify-end mt-4">
            <button @click="refresh" class="btn btn-primary btn-sm">
              <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path
                  fill-rule="evenodd"
                  d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z"
                  clip-rule="evenodd"
                />
              </svg>
              Refresh
            </button>
          </div>
        </div>
      </div>

      <!-- Business Flow Visualization -->
      <div class="card bg-base-200 shadow-xl">
        <div class="card-body">
          <h2 class="card-title text-xl mb-6">Business Flow Progress</h2>

          <!-- Flow Steps -->
          <div class="space-y-4 overflow-x-auto">
            <div
              class="grid gap-4 mb-6"
              :style="`grid-template-columns: 200px repeat(${transactionDetails.details.length}, 1fr)`"
            >
              <!-- Header Row -->
              <div class="text-center font-bold text-lg">Network</div>
              <div
                v-for="(detail, index) in transactionDetails.details"
                :key="index"
                class="text-center font-bold text-lg"
              >
                {{ detail.businessStep?.title }}
              </div>

              <!-- Business Flow Row -->
              <div class="card bg-primary text-primary-content shadow-sm">
                <div class="card-body p-3 text-center">
                  <div class="badge badge-primary-content mb-2 badge-sm">Business Flow</div>
                  <h4 class="font-medium text-sm">Core transaction steps</h4>
                </div>
              </div>
              <div v-for="(detail, index) in transactionDetails.details" :key="index" class="text-center">
                <MessageBlock :message="detail.businessStep" network-type="primary" no-message-text="No step" no-link />
              </div>

              <!-- SWIFT Net Row -->
              <div class="card bg-secondary text-secondary-content shadow-sm">
                <div class="card-body p-3 text-center">
                  <div class="badge badge-secondary-content mb-2 badge-sm">SWIFT Net</div>
                  <h4 class="font-medium text-sm">Traditional banking</h4>
                </div>
              </div>
              <div v-for="(detail, index) in transactionDetails.details" :key="index" class="text-center">
                <MessageBlock :message="detail.swiftMessage" network-type="secondary" no-message-text="No message" />
              </div>

              <!-- DLT Net Row -->
              <div class="card bg-accent text-accent-content shadow-sm">
                <div class="card-body p-3 text-center">
                  <div class="badge badge-accent-content mb-2 badge-sm">DLT Net</div>
                  <h4 class="font-medium text-sm">Distributed ledger</h4>
                </div>
              </div>
              <div v-for="(detail, index) in transactionDetails.details" :key="index" class="text-center">
                <MessageBlock
                  :message="detail.dltMessage"
                  network-type="accent"
                  no-message-text="No message"
                  :show-in-progress-badge="true"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Network Legend -->
      <div class="card bg-base-200 shadow-lg">
        <div class="card-body">
          <h3 class="card-title text-lg mb-4">Network Legend</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="flex items-center gap-3">
              <div class="w-4 h-4 bg-primary rounded"></div>
              <span class="text-sm">Business Flow - Core transaction steps</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-4 h-4 bg-secondary rounded"></div>
              <span class="text-sm">SWIFT Net - Traditional banking messages</span>
            </div>
            <div class="flex items-center gap-3">
              <div class="w-4 h-4 bg-accent rounded"></div>
              <span class="text-sm">DLT Net - Distributed ledger transactions</span>
            </div>
          </div>

          <div class="divider"></div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex items-center gap-2">
              <Icon icon="mdi:check-circle" class="w-5 h-5 text-success" />
              <span class="text-sm">Received - Step completed successfully</span>
            </div>
            <div class="flex items-center gap-2">
              <Icon icon="mdi:clock-outline" class="w-5 h-5 text-warning" />
              <span class="text-sm">Expecting - Step pending completion</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-else class="card bg-error text-error-content shadow-xl">
      <div class="card-body text-center">
        <h2 class="card-title justify-center">Transaction Not Found</h2>
        <p>Unable to load transaction details for UETR: {{ uetr }}</p>
        <div class="card-actions justify-center">
          <button @click="refresh" class="btn btn-outline">Try Again</button>
        </div>
      </div>
    </div>
  </div>
</template>
