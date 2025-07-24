<script setup lang="ts">
import { useRoute } from 'vue-router'
import { type ClientDataResponse } from '../services/clients'
import { computed } from 'vue'
import { formatAccountBalance } from '../utils/formatNumber'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute'
import type { Currency } from '../services/mock-backend/types'

const props = defineProps<{
  client?: ClientDataResponse | null
  isLoading?: boolean
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
    <div class="grid grid-cols-2 my-4 items-center gap-4">
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

      <div class="flex flex-wrap justify-end items-center gap-2">
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
    <section v-if="client">
      <div
        v-for="account in client.accounts"
        :key="account.id"
        class="bg-base-200 p-4 mb-4 rounded-lg text-sm grid gap-2 grid-cols-[2fr_1fr]"
      >
        <div>
          <div class="font-bold">{{ account.currency }} Token Account</div>
          <div class="text-gray-500">Account ID: {{ account.id }}</div>
          <div class="text-accent">Balance: {{ formatAccountBalance(account.currency, account.balance) }}</div>
        </div>
        <div class="flex justify-end items-center">
          <slot name="transfer-button" :account="account">
            <RouterLink class="btn btn-primary btn-sm" :to="transferUrlWithDetails(account.currency)"
              >Transfer</RouterLink
            >
          </slot>
        </div>
      </div>
    </section>
  </div>
</template>
