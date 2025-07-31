<script setup lang="ts">
import { type ClientDataResponse } from '../services/clients'
import { formatNumber } from '../utils/formatNumber'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute'
import type { Currency } from '../services/mock-backend/types'

defineProps<{
  accounts: ClientDataResponse['accounts']
}>()

const transferUrl = useBankRoute('transfer')

function transferUrlWithDetails(currency: Currency) {
  return `${transferUrl.value}?from_currency=${currency}#transfer-form`
}
</script>
<template>
  <div class="balance-container--scroll">
    <div v-for="account in accounts" :key="account.id" class="card card-sm bg-base-200 shadow-md">
      <div class="card-body">
        <div class="card-title font-bold">{{ account.currency }}</div>
        <div>
          <div class="card-title text-success font-bold">{{ formatNumber(account.balance) }}</div>
          <div class="text-gray-500 text-xs">Account ID: {{ account.id }}</div>
        </div>
        <div class="card-actions mt-2">
          <slot name="transfer-button" :account="account">
            <RouterLink
              class="btn btn-primary btn-sm shadow-md/40 shadow-primary"
              :to="transferUrlWithDetails(account.currency)"
            >
              Transfer <Icon icon="mdi:arrow-right" class="inline-block" />
            </RouterLink>
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@reference "../style.css";

.balance-container--scroll {
  @apply flex gap-4 overflow-x-auto snap-x py-4 pr-12;
}

.balance-container--scroll > .card {
  @apply flex-shrink-0 w-52 scroll-m-7 md:scroll-m-10 lg:scroll-m-14 snap-start;
}

.balance-container--grid {
  @apply grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4;
}
</style>
