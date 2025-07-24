<script setup lang="ts">
import { ref, watch } from 'vue'
import TransferFundsWidget from '../../components/TransferFundsWidget.vue'
import type { Account } from '../../services/mock-backend/types'
import ClientInfoWidget from '../../components/ClientInfoWidget.vue'
import { useClientInfo } from '../../composables/useClientInfo'
import { useRoute, useRouter } from 'vue-router'
import { useBankRoute } from '../../composables/useBankRoute'
import Breadcrumbs from '../../components/Breadcrumbs.vue'

const router = useRouter()
const route = useRoute()
const { client, isLoading, refresh } = useClientInfo()
const transferFormRef = ref<InstanceType<typeof TransferFundsWidget> | null>(null)
const accountToTransferFrom = ref<Account | null>(null)

const homeLink = useBankRoute()

watch(
  () => client.value,
  (newClient) => {
    const fromCurrency = route.query.from_currency as string | undefined
    if (fromCurrency && newClient) {
      const chosenAccount = newClient.accounts.find((account) => account.currency === fromCurrency)
      if (chosenAccount) {
        chooseTransferAccount(chosenAccount)
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

function chooseTransferAccount(account: Account) {
  accountToTransferFrom.value = account
  if (transferFormRef.value) {
    transferFormRef.value.$el.scrollIntoView({
      behavior: 'smooth'
    })
  }
}

const transferLink = useBankRoute('transfer')
</script>
<template>
  <div>
    <Breadcrumbs
      :items="[
        { title: 'Home', link: homeLink },
        { title: 'Transfer', link: transferLink }
      ]"
    />
    <ClientInfoWidget :client="client" :isLoading="isLoading" :refresh="refresh">
      <template #transfer-button="{ account }">
        <button class="btn btn-primary btn-sm" @click="chooseTransferAccount(account)">Transfer</button>
      </template>
    </ClientInfoWidget>
    <TransferFundsWidget
      id="transfer-form"
      ref="transferFormRef"
      v-if="client && accountToTransferFrom"
      v-model:account="accountToTransferFrom"
      @completed="onCompleted"
      :debtorAccounts="client.accounts"
    />
  </div>
</template>
