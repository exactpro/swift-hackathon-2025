<script setup lang="ts">
import { ref, watch } from 'vue'
import TransferFundsWidget from '../../components/TransferFundsWidget.vue'
import type { Account } from '../../services/mock-backend/types'
import { useClientInfo } from '../../composables/useClientInfo'
import { useRoute, useRouter } from 'vue-router'
import { useBankRoute } from '../../composables/useBankRoute'
import { useFakeSocket } from '../../composables/useFakeSocket'

const router = useRouter()
const route = useRoute()
const { client, refresh } = useClientInfo()
const accountToTransferFrom = ref<Account | null>(null)

useFakeSocket(refresh)

const homeLink = useBankRoute()

watch(
  () => client.value,
  (newClient) => {
    const fromCurrency = route.query.from_currency as string | undefined
    if (fromCurrency && newClient) {
      const chosenAccount = newClient.accounts.find((account) => account.currency === fromCurrency)
      if (!accountToTransferFrom.value && chosenAccount) {
        accountToTransferFrom.value = chosenAccount
      }
    }
    if (newClient && newClient.accounts.length > 0) {
      if (!accountToTransferFrom.value) {
        accountToTransferFrom.value = newClient.accounts[0]
      }
    }
  },
  { immediate: true }
)

function onCompleted() {
  router.push(homeLink.value)
}
</script>
<template>
  <div>
    <TransferFundsWidget
      id="transfer-form"
      v-if="client && accountToTransferFrom"
      v-model:account="accountToTransferFrom"
      @completed="onCompleted"
      :debtorAccounts="client.accounts"
    />
  </div>
</template>
