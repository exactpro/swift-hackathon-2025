<script setup lang="ts">
import { useRoute } from 'vue-router'
import type { JSONify, Transaction } from '../../services/mock-backend/types'
import { useBankRoute } from '../../composables/useBankRoute.js'
import { getDirection } from '../../utils/transactions.js'
import UUID from '../UUID.vue'
import RelDate from '../RelDate.vue'
import TransactionActions from '../TransactionActions.vue'
const props = defineProps<{
  transaction: JSONify<Transaction>
}>()

const route = useRoute()
const ownBic = route.meta.bic as string
const clientId = route.meta.clientId as string | undefined
const clientPageLink = useBankRoute()
const direction = getDirection(ownBic, props.transaction)

const emit = defineEmits<{
  (e: 'transactionAccepted', transaction: JSONify<Transaction>): void
  (e: 'transactionRejected', transaction: JSONify<Transaction>): void
}>()
</script>

<template>
  <tr>
    <td>
      <span
        class="badge badge-xs"
        :class="{
          'badge-primary': transaction.type === 'transfer',
          'badge-accent': transaction.type === 'cancel'
        }"
      >
        {{ transaction.type }}
      </span>
    </td>
    <td>
      <span
        class="badge badge-xs"
        :class="{
          'badge-info': direction === 'out',
          'badge-success': direction === 'in',
          'badge-ghost': direction === 'N/A'
        }"
      >
        {{ direction }}
      </span>
    </td>
    <td>
      <span
        class="badge badge-xs"
        :class="{
          'badge-success': transaction.status === 'completed',
          'badge-warning': transaction.status === 'pending',
          'badge-info': transaction.status === 'cancelled',
          'badge-error': transaction.status === 'rejected'
        }"
      >
        {{ transaction.status }}
      </span>
    </td>
    <td><UUID :uuid="transaction.uetr" /></td>
    <td
      class="border-l-2 border-base-content"
      :class="{
        'text-accent': transaction.debtor.bic === ownBic
      }"
    >
      {{ transaction.debtor.bic }}
    </td>
    <td>
      <div v-if="clientId && transaction.debtor.clientId === clientId">
        <RouterLink class="link text-accent" :to="clientPageLink"> {{ transaction.debtor.name }}</RouterLink>
      </div>
      <div v-else>
        {{ transaction.debtor.name }}
      </div>

      <UUID :uuid="transaction.debtor.clientId" />
    </td>
    <td>{{ transaction.debtor.amount }}</td>
    <td>{{ transaction.debtor.currency }}</td>
    <td
      class="border-l-2 border-base-content"
      :class="{
        'text-accent': transaction.creditor.bic === ownBic
      }"
    >
      {{ transaction.creditor.bic }}
    </td>
    <td>
      <div v-if="clientId && transaction.creditor.clientId === clientId">
        <RouterLink class="link text-accent" creditor :to="clientPageLink"> {{ transaction.creditor.name }}</RouterLink>
      </div>
      <div v-else>
        {{ transaction.creditor.name }}
      </div>
      <UUID :uuid="transaction.creditor.clientId" />
    </td>
    <td>{{ transaction.creditor.amount }}</td>
    <td class="border-r-2 border-base-content">
      {{ transaction.creditor.currency }}
    </td>
    <td>
      <RelDate class="badge-success" :date="transaction.createdAt" />
    </td>
    <td>
      <RelDate class="badge-info" :date="transaction.updatedAt" />
    </td>
    <td>
      <TransactionActions
        :transaction="transaction"
        @transactionAccepted="emit('transactionAccepted', $event)"
        @transactionRejected="emit('transactionRejected', $event)"
      />
    </td>
  </tr>
</template>
