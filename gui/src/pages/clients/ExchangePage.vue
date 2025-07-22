<script setup lang="ts">
import { onMounted, watch, reactive, toRef, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAsyncState } from '@vueuse/core'
import { fetchTransactionFormData } from '../../services/transactions'
import type { Currency } from '../../services/mock-backend/types'
import { calculateExchangeValue, CURRENCY_KEYS } from '../../utils/calculateExchangeValue'
import { exchangeCurrency } from '../../services/clients'
import Breadcrumbs from '../../components/Breadcrumbs.vue'
import { useBankRoute } from '../../composables/useBankRoute'

const { state: utils } = useAsyncState(fetchTransactionFormData(), null)

interface ExchangeForm {
  clientId: string
  clientName: string
  fromCurrency: string
  fromAmount: number | null
  toCurrency: string
  toAmount: number | null
}

const route = useRoute()
const router = useRouter()

const form = reactive<ExchangeForm>({
  clientId: '',
  clientName: '',
  fromCurrency: '',
  fromAmount: null,
  toCurrency: '',
  toAmount: null
})

// Watch for currency changes and auto-calculate amounts
watch(
  toRef(() => form.fromCurrency),
  (newCurrency, oldCurrency) => {
    if (!utils.value) return
    if (!form.toCurrency && !newCurrency) return
    if (form.fromAmount) {
      form.fromAmount = calculateExchangeValue(
        form.fromAmount,
        oldCurrency as Currency,
        newCurrency as Currency,
        utils.value.exchangeValues
      )
      return
    }
    if (form.toAmount) {
      form.fromAmount = calculateExchangeValue(
        form.toAmount,
        form.toCurrency as Currency,
        newCurrency as Currency,
        utils.value.exchangeValues
      )
    }
  }
)

watch(
  toRef(() => form.toCurrency),
  (newCurrency, oldCurrency) => {
    if (!utils.value) return
    if (!newCurrency && !form.fromCurrency) return
    if (form.toAmount) {
      form.toAmount = calculateExchangeValue(
        form.toAmount,
        oldCurrency as Currency,
        newCurrency as Currency,
        utils.value.exchangeValues
      )
      return
    }
    if (form.fromAmount) {
      form.toAmount = calculateExchangeValue(
        form.fromAmount,
        form.fromCurrency as Currency,
        newCurrency as Currency,
        utils.value.exchangeValues
      )
    }
  }
)

watch(
  toRef(() => form.fromAmount),
  (newAmount) => {
    if (!utils.value) return
    if (form.fromCurrency && form.toCurrency) {
      form.toAmount = calculateExchangeValue(
        newAmount ?? 0,
        form.fromCurrency as Currency,
        form.toCurrency as Currency,
        utils.value.exchangeValues
      )
    }
  }
)

watch(
  toRef(() => form.toAmount),
  (newAmount) => {
    if (!utils.value) return
    if (form.fromCurrency && form.toCurrency) {
      form.fromAmount = calculateExchangeValue(
        newAmount ?? 0,
        form.toCurrency as Currency,
        form.fromCurrency as Currency,
        utils.value.exchangeValues
      )
    }
  }
)

const isFormComplete = computed(() => {
  return (
    form.clientId &&
    form.clientName &&
    form.fromCurrency &&
    form.fromAmount !== null &&
    form.toCurrency &&
    form.toAmount !== null &&
    form.fromCurrency !== form.toCurrency
  )
})

onMounted(() => {
  // Auto-fill from query parameters
  if (route.query.client_id) {
    form.clientId = route.query.client_id as string
  }
  if (route.query.client_name) {
    form.clientName = route.query.client_name as string
  }
  if (route.query.from_currency) {
    form.fromCurrency = route.query.from_currency as string
  }
  if (route.query.to_currency) {
    form.toCurrency = route.query.to_currency as string
  }
})

const isExchanging = ref(false)

function getExchangeRate(fromCurrency: string, toCurrency: string): string {
  if (!utils.value?.exchangeValues) return 'N/A'

  const fromKey = CURRENCY_KEYS[fromCurrency as Currency]
  const toKey = CURRENCY_KEYS[toCurrency as Currency]

  if (!fromKey || !toKey) return 'N/A'

  const fromValue = utils.value.exchangeValues[fromKey]
  const toValue = utils.value.exchangeValues[toKey]

  if (!fromValue || !toValue) return 'N/A'

  const rate = toValue / fromValue
  return rate.toFixed(4)
}

async function startExchange() {
  if (!isFormComplete.value) {
    console.warn('Form is not complete, cannot start exchange')
    return
  }
  isExchanging.value = true
  await exchangeCurrency(
    form.clientId,
    form.fromCurrency as Currency,
    form.toCurrency as Currency,
    form.fromAmount as number
  )
  isExchanging.value = false
  router.push(`/clients/${form.clientId}`)
}

const homeLink = useBankRoute()
const exchangeLink = useBankRoute('exchange')

const breadcrumbs = computed(() => [
  { title: 'Home', link: homeLink.value },
  { title: 'Exchange', link: exchangeLink.value }
])
</script>

<template>
  <div class="container mx-auto p-6 max-w-4xl">
    <Breadcrumbs :items="breadcrumbs" />
    <h1 class="text-2xl font-bold mb-8">Currency Exchange</h1>

    <form v-if="utils" @submit.prevent="startExchange" class="space-y-8">
      <!-- Client Information Section -->
      <section class="card bg-base-200 shadow-lg">
        <div class="card-body">
          <h2 class="card-title text-xl mb-6">Client Information</h2>

          <!-- Client ID -->
          <div class="input input-bordered w-full max-w-md mx-auto">
            <label class="label">
              <span class="label-text font-medium">Client ID</span>
            </label>
            <input v-model="form.clientId" type="text" placeholder="XXX" />
          </div>

          <!-- Client Name -->
          <div class="input input-bordered w-full max-w-md mx-auto mt-4">
            <label class="label">
              <span class="label-text font-medium">Client Name</span>
            </label>
            <input v-model="form.clientName" type="text" placeholder="John Doe" />
          </div>
        </div>
      </section>

      <!-- Exchange Section -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- From Section -->
        <section class="card bg-base-200 shadow-lg">
          <div class="card-body">
            <h2 class="card-title text-xl mb-6">From</h2>

            <!-- From Currency -->
            <div class="form-control mb-4">
              <label class="label">
                <span class="label-text font-medium">Currency</span>
              </label>
              <div class="relative">
                <select v-model="form.fromCurrency" class="select select-bordered w-full">
                  <option value="" disabled>Select currency</option>
                  <option v-for="currency in utils.currencies" :key="currency" :value="currency">
                    {{ currency }}
                  </option>
                </select>
              </div>
            </div>

            <!-- From Amount -->
            <div class="form-control mb-4">
              <label class="label">
                <span class="label-text font-medium">Amount</span>
              </label>
              <div class="relative">
                <input
                  v-model.number="form.fromAmount"
                  type="number"
                  placeholder="256"
                  class="input input-bordered w-full"
                  step="0.01"
                  min="0"
                />
              </div>
            </div>
          </div>
        </section>

        <!-- To Section -->
        <section class="card bg-base-200 shadow-lg">
          <div class="card-body">
            <h2 class="card-title text-xl mb-6">To</h2>

            <!-- To Currency -->
            <div class="form-control mb-4">
              <label class="label">
                <span class="label-text font-medium">Currency</span>
              </label>
              <div class="relative">
                <select v-model="form.toCurrency" class="select select-bordered w-full">
                  <option value="" disabled>Select currency</option>
                  <option v-for="currency in utils.currencies" :key="currency" :value="currency">
                    {{ currency }}
                  </option>
                </select>
              </div>
            </div>

            <!-- To Amount -->
            <div class="form-control mb-6">
              <label class="label">
                <span class="label-text font-medium">Amount</span>
              </label>
              <div class="relative">
                <input
                  v-model.number="form.toAmount"
                  type="number"
                  placeholder="250"
                  class="input input-bordered w-full"
                  step="0.01"
                  min="0"
                />
              </div>
            </div>
          </div>
        </section>
      </div>

      <!-- Exchange Rate Info -->
      <div class="card bg-base-200 shadow">
        <div class="card-body text-center">
          <p
            v-if="form.fromCurrency && form.toCurrency && form.fromCurrency !== form.toCurrency"
            class="text-sm text-gray-600"
          >
            Exchange Rate: 1 {{ form.fromCurrency }} =
            {{ getExchangeRate(form.fromCurrency, form.toCurrency) }}
            {{ form.toCurrency }}
          </p>
          <!-- Exchange Button -->
          <div class="flex justify-center">
            <button
              type="submit"
              class="btn btn-primary btn-lg px-12"
              :disabled="!isFormComplete || isExchanging"
              :class="{ 'btn-disabled': !isFormComplete || isExchanging }"
            >
              <template v-if="!isExchanging"> Exchange </template>
              <template v-else>
                <span class="loading loading-spinner loading-sm"></span>
                Exchanging...
              </template>
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>
