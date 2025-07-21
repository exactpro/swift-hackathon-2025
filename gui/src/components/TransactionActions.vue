<script setup lang="ts">
import { ref } from 'vue'
import type { JSONify, Transaction } from '../services/mock-backend/types'
import { acceptTransaction, rejectTransaction } from '../services/transactions'
import config from '../../config'

const props = defineProps<{
  transaction: JSONify<Transaction>
}>()

const emit = defineEmits<{
  (
    e: 'transactionAccepted',
    acceptedTransaction: JSONify<Transaction> | null
  ): void
  (
    e: 'transactionRejected',
    rejectedTransaction: JSONify<Transaction> | null
  ): void
}>()

const isLoading = ref(false)

async function accept() {
  isLoading.value = true
  const acceptedTransaction = await acceptTransaction(props.transaction.uetr)
  if (acceptedTransaction) {
    emit('transactionAccepted', acceptedTransaction)
  }
  isLoading.value = false
}

async function reject() {
  isLoading.value = true
  const rejectedTransaction = await rejectTransaction(props.transaction.uetr)
  if (rejectedTransaction) {
    emit('transactionRejected', rejectedTransaction)
  }
  isLoading.value = false
}
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <template
      v-if="
        transaction.status === 'pending' &&
        transaction.creditor.bic === config.ownBic
      "
    >
      <button
        class="btn btn-xs btn-success"
        :disabled="isLoading"
        @click="accept"
      >
        <template v-if="!isLoading">Accept</template>
        <template v-else
          ><span class="loading loading-spinner loading-sm"></span
        ></template>
      </button>
      <button
        class="btn btn-xs btn-error"
        :disabled="isLoading"
        @click="reject"
      >
        <template v-if="!isLoading">Reject</template>
        <template v-else
          ><span class="loading loading-spinner loading-sm"></span
        ></template>
      </button>
    </template>
  </div>
</template>
