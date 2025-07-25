<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncState } from '@vueuse/core'
import { formatAccountBalance } from '../utils/formatNumber'
import { fetchTransactionFormData, newTransaction } from '../services/transactions'
import type { Account, Currency } from '../services/mock-backend/types'
import { calculateExchangeValue } from '../utils/calculateExchangeValue'

const { state: utils } = useAsyncState(fetchTransactionFormData(), null)

const props = defineProps<{
  debtorAccounts: Account[]
}>()

const emit = defineEmits<{
  (e: 'completed'): void
}>()

const chosenAccount = defineModel<Account>('account', { required: true })

interface TransferForm {
  creditorBic: string
  creditorAccountId: string
  amount: number | null
  currency: Currency | null
}

const route = useRoute()

const ownBic = route.meta.bic as string
const debtorName = route.meta.clientName as string

const form = reactive<TransferForm>({
  creditorBic: '',
  creditorAccountId: '',
  amount: 0,
  currency: props.debtorAccounts[0].currency
})

const convertedAmount = computed(() => {
  if (!utils.value) return null
  if (!chosenAccount.value || !form.currency) return null
  if (!form.amount) return null
  return calculateExchangeValue(form.amount, chosenAccount.value.currency, form.currency, utils.value.exchangeValues)
})

const exchangeRate = computed(() => {
  if (!utils.value || !chosenAccount.value || !form.currency) return null
  return calculateExchangeValue(1, chosenAccount.value.currency, form.currency, utils.value.exchangeValues)
})

const isFormComplete = computed(() => {
  return (
    form.amount !== null &&
    form.amount > 0 &&
    form.currency !== null &&
    chosenAccount.value !== null &&
    form.creditorBic.trim() !== '' &&
    form.creditorAccountId.trim() !== ''
  )
})

const isSending = ref(false)

async function startTransaction() {
  if (!isFormComplete.value || isSending.value) {
    console.warn('Form is not complete, cannot start transaction')
    return
  }
  isSending.value = true
  await newTransaction({
    debtor: {
      bic: ownBic,
      accountId: chosenAccount.value.id,
      name: debtorName,
      currency: chosenAccount.value.currency,
      amount: convertedAmount.value!
    },
    creditor: {
      bic: form.creditorBic,
      accountId: form.creditorAccountId,
      name: '',
      currency: form.currency!,
      amount: form.amount!
    },
    type: 'transfer'
  })
  isSending.value = false
  chosenAccount.value = props.debtorAccounts[0]
  form.creditorBic = ''
  form.creditorAccountId = ''
  form.amount = 0
  form.currency = props.debtorAccounts[0].currency
  emit('completed')
}
</script>

<template>
  <form v-if="utils" @submit.prevent="startTransaction" class="container mx-auto max-w-2xl">
    <!-- Debtor Section -->
    <section class="card bg-neutral text-neutral-content shadow-lg">
      <div class="card-body">
        <h2 class="card-title text-xl mb-6">Transfer Details</h2>

        <!-- Currency -->
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">From Account</span>
          </label>
          <div>
            <select v-model="chosenAccount" placeholder="Select account" class="select select-bordered w-full">
              <option v-for="account in props.debtorAccounts" :key="account.id" :value="account">
                {{ account.currency }} Token Account — {{ formatAccountBalance(account.currency, account.balance) }}
              </option>
            </select>
          </div>
        </div>

        <!-- Creditor Section -->
        <h2 class="card-title text-xl mb-6">Recipient</h2>

        <!-- Bank -->
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">Creditor BIC</span>
          </label>
          <div>
            <input v-model="form.creditorBic" placeholder="Enter BIC/SWIFT code" class="input input-bordered w-full" />
          </div>
        </div>

        <!-- Client ID -->
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">Creditor Account (IBAN)</span>
          </label>
          <input
            v-model="form.creditorAccountId"
            type="text"
            placeholder="Enter IBAN"
            class="input input-bordered w-full"
          />
        </div>

        <!-- Currency -->
        <div class="form-control mb-4">
          <label class="label">
            <span class="label-text font-medium">Currency</span>
          </label>
          <div class="relative">
            <select v-model="form.currency" placeholder="Select currency" class="select select-bordered w-full">
              <option v-for="currency in utils.currencies" :key="currency" :value="currency">
                {{ currency }}
              </option>
            </select>
          </div>
        </div>

        <!-- Amount -->
        <div class="form-control mb-6">
          <label class="label">
            <span class="label-text font-medium">Amount</span>
          </label>
          <div class="relative">
            <input
              v-model.number="form.amount"
              type="number"
              placeholder="250"
              class="input input-bordered w-full pr-12"
              step="0.01"
              min="0"
            />
          </div>
        </div>

        <div class="alert alert-soft flex flex-col">
          <div v-if="convertedAmount && chosenAccount">
            Converted Amount: {{ formatAccountBalance(chosenAccount.currency, convertedAmount) }}
          </div>
          <div class="text-sm" v-if="exchangeRate && chosenAccount && form.currency">
            Exchange Rate: {{ formatAccountBalance(chosenAccount.currency, 1) }} =
            {{ formatAccountBalance(form.currency, exchangeRate) }}
          </div>
        </div>

        <!-- Start Transaction Button -->
        <button
          type="submit"
          class="btn btn-primary btn-lg w-full"
          :disabled="!isFormComplete || isSending"
          :class="{ 'btn-disabled': !isFormComplete || isSending }"
        >
          <template v-if="!isSending"> Send Transfer </template>
          <template v-else>
            <span class="loading loading-spinner loading-sm"></span>
          </template>
        </button>
      </div>
    </section>
  </form>
</template>

<style scoped>
@reference '../style.css';
.label {
  margin-bottom: 0.5rem;
}
input,
select {
  @apply text-base-content;
}
</style>
