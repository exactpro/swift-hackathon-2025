<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { useRoute } from 'vue-router'
import { fetchClientData } from '../../services/clients'
import { computed, ref, watch } from 'vue'
import { formatAccountBalance } from '../../utils/formatNumber'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../../composables/useBankRoute'
import TransferFundsWidget from '../../components/TransferFundsWidget.vue'
import type { Account } from '../../services/mock-backend/types'

const route = useRoute()
const bankName = route.meta.bankName as string
const bic = route.meta.bic as string
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

const exchangeUrl = useBankRoute('exchange')

const transferFormRef = ref<InstanceType<typeof TransferFundsWidget> | null>(null)
const accountToTransferFrom = ref<Account | null>(null)
watch(
  () => client.value,
  (newClient) => {
    if (newClient && newClient.accounts.length > 0) {
      if (!accountToTransferFrom.value) {
        accountToTransferFrom.value = newClient.accounts[0]
      }
    }
  },
  { immediate: true }
)

function chooseTransferAccount(account: Account) {
  accountToTransferFrom.value = account
  if (transferFormRef.value) {
    transferFormRef.value.$el.scrollIntoView({
      behavior: 'smooth'
    })
  }
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
          <button class="btn btn-primary btn-sm" @click="chooseTransferAccount(account)">Transfer</button>
        </div>
      </div>
    </section>
    <TransferFundsWidget
      ref="transferFormRef"
      v-if="client && accountToTransferFrom"
      v-model:account="accountToTransferFrom"
      @completed="refresh"
      :debtorAccounts="client.accounts"
    />
  </div>
</template>
