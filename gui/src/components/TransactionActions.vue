<script setup lang="ts">
import type { JSONify, Transaction } from '../services/mock-backend/types'
import { useBankRoute } from '../composables/useBankRoute'

const props = defineProps<{
  transaction: JSONify<Transaction>
}>()

const transactionStatusLink = useBankRoute('admin', 'transfers', props.transaction.uetr)

defineEmits<{
  (e: 'transactionAccepted', acceptedTransaction: JSONify<Transaction>): void
  (e: 'transactionRejected', rejectedTransaction: JSONify<Transaction>): void
}>()
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <RouterLink class="btn btn-xs btn-primary" :to="transactionStatusLink"> View </RouterLink>
  </div>
</template>
