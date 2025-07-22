<script setup lang="ts">
import { computed } from 'vue'
import { joinURL } from 'ufo'
import type { JSONify, Transaction } from '../services/mock-backend/types'
import { useBankBaseRoute } from '../composables/useBankBaseRoute'

const props = defineProps<{
  transaction: JSONify<Transaction>
}>()

const bankBasePath = useBankBaseRoute()

const transactionStatusLink = computed(() => {
  const transactionId = props.transaction.uetr
  return joinURL(bankBasePath.value, 'transfers', transactionId)
})

defineEmits<{
  (e: 'transactionAccepted', acceptedTransaction: JSONify<Transaction> | null): void
  (e: 'transactionRejected', rejectedTransaction: JSONify<Transaction> | null): void
}>()
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <RouterLink class="btn btn-xs btn-primary" :to="transactionStatusLink"> View </RouterLink>
  </div>
</template>
