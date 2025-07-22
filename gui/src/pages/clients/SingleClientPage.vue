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
import { useBankRoute } from '../../composables/useBankRoute'

const route = useRoute()
const clientId = route.meta.clientId as string

const { state: client, isLoading } = useAsyncState(fetchClientData(clientId), null)

async function refresh() {
  isLoading.value = true
  client.value = await fetchClientData(clientId)
  isLoading.value = false
}

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

const exchangeUrl = useBankRoute('exchange')
const transferUrl = useBankRoute('transfers', 'new')
function transferCurrencyUrl(currency: string) {
  return `${transferUrl.value}?from_currency=${currency}`
}
</script>
<template>
  <div>
    <div class="grid grid-cols-2 my-4">
      <div class="flex flex-wrap gap-2 items-center">
        <h1 class="text-lg font-bold inline-block">
          <Icon icon="mdi:account" class="inline-block" /> {{ displayTitle }}
        </h1>
        <span v-if="client" class="text-sm text-gray-500">
          {{ client.id }}
        </span>
      </div>
      <div class="text-right">
        <RouterLink v-if="client" class="btn btn-primary btn-sm mr-2" :to="exchangeUrl">
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
    <section v-if="client">
      <div
        v-for="account in client.accounts"
        :key="account.id"
        class="bg-base-200 p-4 mb-4 rounded-lg text-sm grid gap-2 grid-cols-[2fr_1fr]"
      >
        <div>
          <div class="font-bold">{{ account.currency }} Token Account</div>
          <div class="text-accent">Balance: {{ formatAccountBalance(account) }}</div>
        </div>
        <div class="text-right">
          <RouterLink class="btn btn-primary btn-sm" :to="transferCurrencyUrl(account.currency)"> Transfer </RouterLink>
        </div>
      </div>
    </section>
    <section v-if="client">
      <TransactionsTable
        :transactions="client.transactions"
        @transaction-accepted="refresh"
        @transaction-rejected="refresh"
      />
    </section>
  </div>
</template>
