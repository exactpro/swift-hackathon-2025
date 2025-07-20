<script setup lang="ts">
import { onMounted, watch, reactive, toRef, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncState } from '@vueuse/core'
import {
  fetchTransactionFormData,
  newTransaction
} from '../../../services/transactions'
import type { Currency } from '../../../services/mock-backend/types'
import config from '../../../../config'

const { state: utils } = useAsyncState(fetchTransactionFormData(), null)

interface TransferForm {
  debtorClientId: string
  debtorName: string
  debtorCurrency: string
  debtorAmount: number | null
  creditorBank: string
  creditorClientId: string
  creditorName: string
  creditorCurrency: string
  creditorAmount: number | null
}

const route = useRoute()
const router = useRouter()

const form = reactive<TransferForm>({
  debtorClientId: '',
  debtorName: '',
  debtorCurrency: '',
  debtorAmount: null,
  creditorBank: '',
  creditorClientId: '',
  creditorName: '',
  creditorCurrency: '',
  creditorAmount: null
})

const CURRENCY_KEYS: Record<Currency, 'EUR' | 'USD' | 'SUSDC'> = {
  EUR: 'EUR',
  USD: 'USD',
  'S-USDC': 'SUSDC'
}

function calculateExchangeValue(
  amount: number,
  fromCurrency: Currency,
  toCurrency: Currency
): number {
  const fromValue = utils.value?.exchangeValues[CURRENCY_KEYS[fromCurrency]]
  const toValue = utils.value?.exchangeValues[CURRENCY_KEYS[toCurrency]]
  if (!fromValue || !toValue) {
    console.error(
      'Exchange values not available for currencies:',
      fromCurrency,
      toCurrency
    )
    return amount
  }
  const exchangedEmount = (amount * fromValue) / toValue
  return parseFloat(exchangedEmount.toFixed(2))
}

watch(
  toRef(() => form.debtorCurrency),
  (newCurrency, oldCurrency) => {
    if (!form.creditorCurrency && !newCurrency) return
    if (form.debtorAmount) {
      form.debtorAmount = calculateExchangeValue(
        form.debtorAmount,
        oldCurrency as Currency,
        newCurrency as Currency
      )
      return
    }
    if (form.creditorAmount) {
      form.debtorAmount = calculateExchangeValue(
        form.creditorAmount,
        form.creditorCurrency as Currency,
        newCurrency as Currency
      )
    }
  }
)

watch(
  toRef(() => form.creditorCurrency),
  (newCurrency, oldCurrency) => {
    if (!newCurrency && !form.debtorCurrency) return
    if (form.creditorAmount) {
      form.creditorAmount = calculateExchangeValue(
        form.creditorAmount,
        oldCurrency as Currency,
        newCurrency as Currency
      )
      return
    }
    if (form.debtorAmount) {
      form.creditorAmount = calculateExchangeValue(
        form.debtorAmount,
        form.debtorCurrency as Currency,
        newCurrency as Currency
      )
    }
  }
)

watch(
  toRef(() => form.debtorAmount),
  (newAmount) => {
    if (form.debtorCurrency && form.creditorCurrency) {
      form.creditorAmount = calculateExchangeValue(
        newAmount ?? 0,
        form.debtorCurrency as Currency,
        form.creditorCurrency as Currency
      )
      return
    }
  }
)

watch(
  toRef(() => form.creditorAmount),
  (newAmount) => {
    if (form.debtorCurrency && form.creditorCurrency) {
      form.debtorAmount = calculateExchangeValue(
        newAmount ?? 0,
        form.creditorCurrency as Currency,
        form.debtorCurrency as Currency
      )
      return
    }
  }
)

const isFormComplete = computed(() => {
  return (
    form.debtorClientId &&
    form.debtorCurrency &&
    form.debtorAmount !== null &&
    form.creditorBank &&
    form.creditorClientId &&
    form.creditorCurrency &&
    form.creditorAmount !== null &&
    form.debtorName &&
    form.creditorName
  )
})

onMounted(() => {
  // Auto-fill from query parameters
  if (route.query.debtor_id) {
    form.debtorClientId = route.query.debtor_id as string
  }
  if (route.query.debtor_name) {
    form.debtorName = route.query.debtor_name as string
  }
  if (route.query.from_currency) {
    form.debtorCurrency = route.query.from_currency as string
  }
})

const isSending = ref(false)

async function startTransaction() {
  if (!isFormComplete.value) {
    console.warn('Form is not complete, cannot start transaction')
    return
  }
  isSending.value = true
  await newTransaction({
    debtor: {
      bic: config.ownBic,
      clientId: form.debtorClientId,
      name: form.debtorName,
      currency: form.debtorCurrency as Currency,
      amount: form.debtorAmount!
    },
    creditor: {
      bic: form.creditorBank,
      clientId: form.creditorClientId,
      name: form.creditorName,
      currency: form.creditorCurrency as Currency,
      amount: form.creditorAmount!
    },
    type: 'transfer'
  })
  isSending.value = false
  router.push('/transactions')
}
</script>

<template>
  <div class="container mx-auto p-6 max-w-4xl">
    <h1 class="text-2xl font-bold mb-8">Transfer Funds</h1>

    <form
      v-if="utils"
      @submit.prevent="startTransaction"
      class="grid grid-cols-1 lg:grid-cols-2 gap-8"
    >
      <!-- Debtor Section -->
      <section class="card bg-base-200 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-6">Debtor</h2>

          <!-- Client ID -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Client ID</span>
            </label>
            <input
              v-model="form.debtorClientId"
              type="text"
              placeholder="XXX"
              class="input input-bordered w-full"
            />
          </div>

          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Name</span>
            </label>
            <input
              v-model="form.debtorName"
              type="text"
              placeholder="John Doe"
              class="input input-bordered w-full"
            />
          </div>

          <!-- Currency -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Currency</span>
            </label>
            <div class="relative">
              <select
                v-model="form.debtorCurrency"
                placeholder="Select currency"
                class="select select-bordered w-full"
              >
                <option
                  v-for="currency in utils.currencies"
                  :key="currency"
                  :value="currency"
                >
                  {{ currency }}
                </option>
              </select>
            </div>
          </div>

          <!-- Amount -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Amount</span>
            </label>
            <div class="relative">
              <input
                v-model.number="form.debtorAmount"
                type="number"
                placeholder="256"
                class="input input-bordered w-full pr-12"
                step="0.01"
                min="0"
              />
            </div>
          </div>
        </div>
      </section>

      <!-- Creditor Section -->
      <section class="card bg-base-200 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-6">Creditor</h2>

          <!-- Bank -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">BIC</span>
            </label>
            <div class="relative">
              <input
                v-model="form.creditorBank"
                class="input input-bordered w-full"
              />
            </div>
          </div>

          <!-- Client ID -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Client ID</span>
            </label>
            <input
              v-model="form.creditorClientId"
              type="text"
              placeholder="XXX"
              class="input input-bordered w-full"
            />
          </div>

          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Name</span>
            </label>
            <input
              v-model="form.creditorName"
              type="text"
              placeholder="John Doe"
              class="input input-bordered w-full"
            />
          </div>

          <!-- Currency -->
          <div class="form-control mb-4">
            <label class="label">
              <span class="label-text font-medium">Currency</span>
            </label>
            <div class="relative">
              <select
                v-model="form.creditorCurrency"
                placeholder="Select currency"
                class="select select-bordered w-full"
              >
                <option
                  v-for="currency in utils.currencies"
                  :key="currency"
                  :value="currency"
                >
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
                v-model.number="form.creditorAmount"
                type="number"
                placeholder="250"
                class="input input-bordered w-full pr-12"
                step="0.01"
                min="0"
              />
            </div>
          </div>

          <!-- Start Transaction Button -->
          <button
            type="submit"
            class="btn btn-primary btn-lg w-full"
            :disabled="!isFormComplete || isSending"
            :class="{ 'btn-disabled': !isFormComplete || isSending }"
          >
            <template v-if="!isSending"> Start Transaction </template>
            <template v-else>
              <span class="loading loading-spinner loading-sm"></span>
            </template>
          </button>
        </div>
      </section>
    </form>
  </div>
</template>
