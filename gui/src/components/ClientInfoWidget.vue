<script setup lang="ts">
import { useRoute } from 'vue-router'
import { type ClientDataResponse } from '../services/clients'
import { computed } from 'vue'
import { formatAccountBalance, formatNumber } from '../utils/formatNumber'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute'
import type { Currency } from '../services/mock-backend/types'

const props = defineProps<{
  client?: ClientDataResponse | null
  isLoading?: boolean
  showAccounts?: boolean
  refresh: () => void
}>()

const route = useRoute()
const bankName = route.meta.bankName as string
const bic = route.meta.bic as string
const clientId = route.meta.clientId as string

const displayTitle = computed(() => {
  return props.client ? props.client.fullName : clientId
})

const exchangeUrl = useBankRoute('exchange')
const transferUrl = useBankRoute('transfer')

function transferUrlWithDetails(currency: Currency) {
  return `${transferUrl.value}?from_currency=${currency}#transfer-form`
}
</script>
<template>
  <div>
    <div class="grid grid-cols-1 md:grid-cols-2 my-4 items-center gap-4 max-w-6xl mx-auto">
      <div>
        <div class="flex flex-wrap gap-2 items-center">
          <h1 class="text-lg font-bold inline-block">
            <Icon icon="mdi:account" class="inline-block" /> {{ displayTitle }}
          </h1>
          <span v-if="client" class="text-sm text-gray-500">
            {{ client.id }}
          </span>
        </div>
        <div class="flex flex-wrap gap-2 items-center">
          <div class="text-lg font-bold inline-block"><Icon icon="mdi:bank" class="inline-block" /> {{ bankName }}</div>
          <span v-if="client" class="text-sm text-gray-500">
            {{ bic }}
          </span>
        </div>
      </div>

      <div class="flex flex-wrap md:justify-end items-center gap-2">
        <RouterLink v-if="client" class="btn btn-primary btn-sm" :to="exchangeUrl">
          Exchange <Icon icon="mdi:swap-horizontal" class="inline-block" />
        </RouterLink>
        <button
          @click="refresh"
          :disabled="isLoading"
          class="btn btn-secondary btn-sm"
          :class="{ disabled: isLoading }"
        >
          <template v-if="!isLoading"> Refresh <Icon icon="mdi:refresh" class="inline-block" /> </template>
          <template v-else>
            <span class="loading loading-spinner loading-sm"></span>
          </template>
        </button>
      </div>
    </div>
    <section>
      <h2 class="section-title">Account Balance</h2>
      <div class="balance-container--scroll mb-4 max-w-6xl mx-auto" v-if="showAccounts && client">
        <div v-for="account in client.accounts" :key="account.id" class="card card-sm bg-base-200 shadow-md">
          <div class="card-body">
            <div class="card-title">{{ account.currency }}</div>
            <div>
              <div class="card-title text-success-content">{{ formatNumber(account.balance) }}</div>
              <div class="text-gray-500 text-xs">Account ID: {{ account.id }}</div>
            </div>
            <div class="card-actions mt-2">
              <slot name="transfer-button" :account="account">
                <RouterLink
                  class="btn btn-primary btn-sm rounded-md shadow-md"
                  :to="transferUrlWithDetails(account.currency)"
                >
                  Transfer <Icon icon="mdi:arrow-right" class="inline-block" />
                </RouterLink>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
@reference "../style.css";

.balance-container--scroll {
  @apply flex gap-4 overflow-x-auto p-4 rounded-md shadow-lg bg-base-300;
}

.balance-container--scroll > .card {
  @apply flex-shrink-0 w-52;
}

.balance-container--grid {
  @apply grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4;
}
</style>
