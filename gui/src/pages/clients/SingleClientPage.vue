<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { fetchClientData } from '../../services/clients'
import { computed } from 'vue'
import { formatNumber } from '../../utils/formatNumber'
import type { Account } from '../../services/mock-backend/types'
import { RouterLink } from 'vue-router'
import TransactionsTable from '../../components/TransactionsTable.vue'
import { Icon } from '@iconify/vue'

const route = useRoute()
const clientId = route.params.client_id as string

const {
  state: client,
  executeImmediate: refresh,
  isLoading
} = useAsyncState(fetchClientData(clientId), null)

const displayTitle = computed(() => {
  return client.value ? client.value.fullName : clientId
})

function formatAccountBalance(account: Account) {
  const formattedBalance = formatNumber(account.balance)
  if (account.currency === 'EUR') {
    return `€${formattedBalance}`
  } else if (account.currency === 'USD') {
    return `$${formattedBalance}`
  } else if (account.currency === 'S-USDC') {
    return `${formattedBalance} S-USDC`
  }
  return `${formattedBalance} ${account.currency}`
}
</script>
<template>
  <div>
    <div class="grid grid-cols-2 my-4">
      <h1 class="text-lg font-bold">Client {{ displayTitle }}</h1>
      <div class="text-right">
        <button
          @click="refresh"
          :disabled="isLoading"
          class="btn btn-secondary btn-sm"
          :class="{ disabled: isLoading }"
        >
          <template v-if="!isLoading">
            Refresh <Icon icon="mdi:refresh" class="inline-block" />
          </template>
          <template v-else>
            <span class="loading loading-spinner loading-sm"></span>
          </template>
        </button>
      </div>
    </div>
    <section v-if="client">
      <div
        v-for="account in client.accounts"
        :key="account.id"
        class="bg-base-200 p-4 mb-4 rounded-lg text-sm grid gap-2 grid-cols-[2fr_1fr]"
      >
        <div>
          <div class="font-bold">{{ account.currency }} Token Account</div>
          <div class="text-accent">
            Balance: {{ formatAccountBalance(account) }}
          </div>
        </div>
        <div class="text-right">
          <RouterLink
            class="btn btn-primary btn-sm"
            :to="`/transactions/new?debtor=${client.id}&from_currency=${account.currency}`"
          >
            Transfer
          </RouterLink>
        </div>
      </div>
    </section>
    <section v-if="client">
      <TransactionsTable :transactions="client.transactions" />
    </section>
  </div>
</template>
