<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type {
  JSONify,
  Transaction,
  TransactionStatus,
  TransactionType,
  Currency
} from '../services/mock-backend/types.js'

interface FilterConfig {
  status: TransactionStatus[]
  type: TransactionType[]
  uetr: string
  debtorBic: string
  debtorClientId: string
  debtorAmountFrom: number | null
  debtorAmountTo: number | null
  debtorCurrency: Currency[]
  creditorBic: string
  creditorClientId: string
  creditorAmountFrom: number | null
  creditorAmountTo: number | null
  creditorCurrency: Currency[]
  createdFrom: string
  createdTo: string
  updatedFrom: string
  updatedTo: string
}

interface Props {
  items: JSONify<Transaction>[]
}

interface Emits {
  (e: 'filtered', items: JSONify<Transaction>[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// Filter configuration
const filters = ref<FilterConfig>({
  status: [],
  type: [],
  uetr: '',
  debtorBic: '',
  debtorClientId: '',
  debtorAmountFrom: null,
  debtorAmountTo: null,
  debtorCurrency: [],
  creditorBic: '',
  creditorClientId: '',
  creditorAmountFrom: null,
  creditorAmountTo: null,
  creditorCurrency: [],
  createdFrom: '',
  createdTo: '',
  updatedFrom: '',
  updatedTo: ''
})

// Available options from the data
const availableOptions = computed(() => {
  const statuses = new Set<TransactionStatus>()
  const types = new Set<TransactionType>()
  const uetrs = new Set<string>()
  const debtorBics = new Set<string>()
  const debtorClientIds = new Set<string>()
  const debtorCurrencies = new Set<Currency>()
  const creditorBics = new Set<string>()
  const creditorClientIds = new Set<string>()
  const creditorCurrencies = new Set<Currency>()
  let minDebtorAmount = Infinity
  let maxDebtorAmount = -Infinity
  let minCreditorAmount = Infinity
  let maxCreditorAmount = -Infinity

  props.items.forEach((item) => {
    statuses.add(item.status)
    types.add(item.type)
    uetrs.add(item.uetr)
    debtorBics.add(item.debtor.bic)
    debtorClientIds.add(item.debtor.clientId)
    debtorCurrencies.add(item.debtor.currency)
    creditorBics.add(item.creditor.bic)
    creditorClientIds.add(item.creditor.clientId)
    creditorCurrencies.add(item.creditor.currency)

    const debtorAmount = parseFloat(item.debtor.amount.toString())
    const creditorAmount = parseFloat(item.creditor.amount.toString())

    if (debtorAmount < minDebtorAmount) minDebtorAmount = debtorAmount
    if (debtorAmount > maxDebtorAmount) maxDebtorAmount = debtorAmount
    if (creditorAmount < minCreditorAmount) minCreditorAmount = creditorAmount
    if (creditorAmount > maxCreditorAmount) maxCreditorAmount = creditorAmount
  })

  return {
    statuses: Array.from(statuses),
    types: Array.from(types),
    uetrs: Array.from(uetrs),
    debtorBics: Array.from(debtorBics),
    debtorClientIds: Array.from(debtorClientIds),
    debtorCurrencies: Array.from(debtorCurrencies),
    creditorBics: Array.from(creditorBics),
    creditorClientIds: Array.from(creditorClientIds),
    creditorCurrencies: Array.from(creditorCurrencies),
    debtorAmountRange: { min: minDebtorAmount, max: maxDebtorAmount },
    creditorAmountRange: { min: minCreditorAmount, max: maxCreditorAmount }
  }
})

// Amount ranges are kept empty by default - no initialization needed
// onMounted(() => {
//   if (props.items.length > 0) {
//     const options = availableOptions.value
//     filters.value.debtorAmountFrom = options.debtorAmountRange.min
//     filters.value.debtorAmountTo = options.debtorAmountRange.max
//     filters.value.creditorAmountFrom = options.creditorAmountRange.min
//     filters.value.creditorAmountTo = options.creditorAmountRange.max
//   }
// })

// Filter logic
const filteredItems = computed(() => {
  return props.items.filter((item) => {
    // Status filter
    if (filters.value.status.length > 0 && !filters.value.status.includes(item.status)) {
      return false
    }

    // Type filter
    if (filters.value.type.length > 0 && !filters.value.type.includes(item.type)) {
      return false
    }

    // UETR filter
    if (filters.value.uetr && !item.uetr.toLowerCase().includes(filters.value.uetr.toLowerCase())) {
      return false
    }

    // Debtor BIC filter
    if (filters.value.debtorBic && !item.debtor.bic.toLowerCase().includes(filters.value.debtorBic.toLowerCase())) {
      return false
    }

    // Debtor Client ID filter
    if (
      filters.value.debtorClientId &&
      !item.debtor.clientId.toLowerCase().includes(filters.value.debtorClientId.toLowerCase())
    ) {
      return false
    }

    // Debtor Amount range filter
    const debtorAmount = parseFloat(item.debtor.amount.toString())
    if (filters.value.debtorAmountFrom !== null && debtorAmount < filters.value.debtorAmountFrom) {
      return false
    }
    if (filters.value.debtorAmountTo !== null && debtorAmount > filters.value.debtorAmountTo) {
      return false
    }

    // Debtor Currency filter
    if (filters.value.debtorCurrency.length > 0 && !filters.value.debtorCurrency.includes(item.debtor.currency)) {
      return false
    }

    // Creditor BIC filter
    if (
      filters.value.creditorBic &&
      !item.creditor.bic.toLowerCase().includes(filters.value.creditorBic.toLowerCase())
    ) {
      return false
    }

    // Creditor Client ID filter
    if (
      filters.value.creditorClientId &&
      !item.creditor.clientId.toLowerCase().includes(filters.value.creditorClientId.toLowerCase())
    ) {
      return false
    }

    // Creditor Amount range filter
    const creditorAmount = parseFloat(item.creditor.amount.toString())
    if (filters.value.creditorAmountFrom !== null && creditorAmount < filters.value.creditorAmountFrom) {
      return false
    }
    if (filters.value.creditorAmountTo !== null && creditorAmount > filters.value.creditorAmountTo) {
      return false
    }

    // Creditor Currency filter
    if (filters.value.creditorCurrency.length > 0 && !filters.value.creditorCurrency.includes(item.creditor.currency)) {
      return false
    }

    // Date filters
    if (filters.value.createdFrom) {
      const createdDate = new Date(item.createdAt)
      const fromDate = new Date(filters.value.createdFrom)
      if (createdDate < fromDate) return false
    }

    if (filters.value.createdTo) {
      const createdDate = new Date(item.createdAt)
      const toDate = new Date(filters.value.createdTo)
      if (createdDate > toDate) return false
    }

    if (filters.value.updatedFrom) {
      const updatedDate = new Date(item.updatedAt)
      const fromDate = new Date(filters.value.updatedFrom)
      if (updatedDate < fromDate) return false
    }

    if (filters.value.updatedTo) {
      const updatedDate = new Date(item.updatedAt)
      const toDate = new Date(filters.value.updatedTo)
      if (updatedDate > toDate) return false
    }

    return true
  })
})

// Watch for changes and emit filtered results
watch(
  filteredItems,
  (newFilteredItems) => {
    emit('filtered', newFilteredItems)
  },
  { immediate: true }
)

// Clear all filters
function clearFilters() {
  filters.value = {
    status: [],
    type: [],
    uetr: '',
    debtorBic: '',
    debtorClientId: '',
    debtorAmountFrom: null,
    debtorAmountTo: null,
    debtorCurrency: [],
    creditorBic: '',
    creditorClientId: '',
    creditorAmountFrom: null,
    creditorAmountTo: null,
    creditorCurrency: [],
    createdFrom: '',
    createdTo: '',
    updatedFrom: '',
    updatedTo: ''
  }
}
</script>

<template>
  <div class="card bg-base-200 shadow-xl mb-6">
    <div class="card-body">
      <details class="collapse collapse-arrow bg-base-300 rounded-box">
        <summary class="collapse-title text-lg font-medium">
          <h2 class="card-title">Filter Transactions</h2>
        </summary>
        <div class="collapse-content">
          <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4 pt-4">
            <!-- Transaction Type -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Type</span>
              </label>
              <div class="flex flex-wrap gap-3">
                <label
                  v-for="type in availableOptions.types"
                  :key="type"
                  class="label cursor-pointer justify-start gap-2"
                >
                  <input
                    type="checkbox"
                    :value="type"
                    v-model="filters.type"
                    class="checkbox checkbox-primary checkbox-sm"
                  />
                  <span class="label-text">{{ type }}</span>
                </label>
              </div>
            </div>

            <!-- Transaction Status -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">Status</span>
              </label>
              <div class="flex flex-wrap gap-3">
                <label
                  v-for="status in availableOptions.statuses"
                  :key="status"
                  class="label cursor-pointer justify-start gap-2"
                >
                  <input
                    type="checkbox"
                    :value="status"
                    v-model="filters.status"
                    class="checkbox checkbox-secondary checkbox-sm"
                  />
                  <span class="label-text">{{ status }}</span>
                </label>
              </div>
            </div>

            <!-- UETR -->
            <div class="form-control">
              <label class="label">
                <span class="label-text font-semibold">UETR</span>
              </label>
              <input
                v-model="filters.uetr"
                type="text"
                placeholder="Search UETR..."
                class="input input-bordered input-xs w-full"
                list="uetr-list"
              />
              <datalist id="uetr-list">
                <option v-for="uetr in availableOptions.uetrs" :key="uetr" :value="uetr" />
              </datalist>
            </div>

            <!-- Debtor Section -->
            <div class="col-span-full">
              <h3 class="text-lg font-semibold text-blue-600 border-l-4 border-blue-500 pl-2 mb-4">Debtor Filters</h3>
            </div>

            <!-- Debtor BIC -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Debtor BIC</span>
              </label>
              <input
                v-model="filters.debtorBic"
                type="text"
                placeholder="Search BIC..."
                class="input input-bordered input-xs w-full"
                list="debtor-bic-list"
              />
              <datalist id="debtor-bic-list">
                <option v-for="bic in availableOptions.debtorBics" :key="bic" :value="bic" />
              </datalist>
            </div>

            <!-- Debtor Client ID -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Debtor Client</span>
              </label>
              <input
                v-model="filters.debtorClientId"
                type="text"
                placeholder="Search Client ID..."
                class="input input-bordered input-xs w-full"
                list="debtor-client-list"
              />
              <datalist id="debtor-client-list">
                <option v-for="clientId in availableOptions.debtorClientIds" :key="clientId" :value="clientId" />
              </datalist>
            </div>

            <!-- Debtor Amount Range -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Debtor Amount Range</span>
              </label>
              <div class="space-y-2">
                <div class="flex gap-2">
                  <input
                    v-model.number="filters.debtorAmountFrom"
                    type="number"
                    placeholder="From"
                    class="input input-bordered input-xs flex-1"
                    :min="availableOptions.debtorAmountRange.min"
                    :max="availableOptions.debtorAmountRange.max"
                  />
                  <input
                    v-model.number="filters.debtorAmountTo"
                    type="number"
                    placeholder="To"
                    class="input input-bordered input-xs flex-1"
                    :min="availableOptions.debtorAmountRange.min"
                    :max="availableOptions.debtorAmountRange.max"
                  />
                </div>
                <div class="text-xs text-gray-500">
                  Range: {{ availableOptions.debtorAmountRange.min }} -
                  {{ availableOptions.debtorAmountRange.max }}
                </div>
              </div>
            </div>

            <!-- Debtor Currency -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Debtor Currency</span>
              </label>
              <div class="flex flex-wrap gap-3">
                <label
                  v-for="currency in availableOptions.debtorCurrencies"
                  :key="currency"
                  class="label cursor-pointer justify-start gap-2"
                >
                  <input
                    type="checkbox"
                    :value="currency"
                    v-model="filters.debtorCurrency"
                    class="checkbox checkbox-info checkbox-sm"
                  />
                  <span class="label-text">{{ currency }}</span>
                </label>
              </div>
            </div>

            <!-- Creditor Section -->
            <div class="col-span-full">
              <h3 class="text-lg font-semibold text-green-600 border-l-4 border-green-500 pl-2 mb-4">
                Creditor Filters
              </h3>
            </div>

            <!-- Creditor BIC -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Creditor BIC</span>
              </label>
              <input
                v-model="filters.creditorBic"
                type="text"
                placeholder="Search BIC..."
                class="input input-bordered input-xs w-full"
                list="creditor-bic-list"
              />
              <datalist id="creditor-bic-list">
                <option v-for="bic in availableOptions.creditorBics" :key="bic" :value="bic" />
              </datalist>
            </div>

            <!-- Creditor Client ID -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Creditor Client</span>
              </label>
              <input
                v-model="filters.creditorClientId"
                type="text"
                placeholder="Search Client ID..."
                class="input input-bordered input-xs w-full"
                list="creditor-client-list"
              />
              <datalist id="creditor-client-list">
                <option v-for="clientId in availableOptions.creditorClientIds" :key="clientId" :value="clientId" />
              </datalist>
            </div>

            <!-- Creditor Amount Range -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Creditor Amount Range</span>
              </label>
              <div class="space-y-2">
                <div class="flex gap-2">
                  <input
                    v-model.number="filters.creditorAmountFrom"
                    type="number"
                    placeholder="From"
                    class="input input-bordered input-xs flex-1"
                    :min="availableOptions.creditorAmountRange.min"
                    :max="availableOptions.creditorAmountRange.max"
                  />
                  <input
                    v-model.number="filters.creditorAmountTo"
                    type="number"
                    placeholder="To"
                    class="input input-bordered input-xs flex-1"
                    :min="availableOptions.creditorAmountRange.min"
                    :max="availableOptions.creditorAmountRange.max"
                  />
                </div>
                <div class="text-xs text-gray-500">
                  Range: {{ availableOptions.creditorAmountRange.min }} -
                  {{ availableOptions.creditorAmountRange.max }}
                </div>
              </div>
            </div>

            <!-- Creditor Currency -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Creditor Currency</span>
              </label>
              <div class="flex flex-wrap gap-3">
                <label
                  v-for="currency in availableOptions.creditorCurrencies"
                  :key="currency"
                  class="label cursor-pointer justify-start gap-2"
                >
                  <input
                    type="checkbox"
                    :value="currency"
                    v-model="filters.creditorCurrency"
                    class="checkbox checkbox-success checkbox-sm"
                  />
                  <span class="label-text">{{ currency }}</span>
                </label>
              </div>
            </div>

            <!-- Date Filters -->
            <div class="col-span-full">
              <h3 class="text-lg font-semibold text-purple-600 border-l-4 border-purple-500 pl-2 mb-4">Date Filters</h3>
            </div>

            <!-- Created Date Range -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Created Date Range</span>
              </label>
              <div class="flex flex-col sm:flex-row gap-2">
                <div class="flex-1">
                  <label class="label-text-alt">From</label>
                  <input
                    v-model="filters.createdFrom"
                    type="datetime-local"
                    class="input input-bordered input-xs w-full"
                    step="1"
                  />
                </div>
                <div class="flex-1">
                  <label class="label-text-alt">To</label>
                  <input
                    v-model="filters.createdTo"
                    type="datetime-local"
                    class="input input-bordered input-xs w-full"
                    step="1"
                  />
                </div>
              </div>
            </div>

            <!-- Updated Date Range -->
            <div class="form-control">
              <label class="label">
                <span class="label-text">Updated Date Range</span>
              </label>
              <div class="flex flex-col sm:flex-row gap-2">
                <div class="flex-1">
                  <label class="label-text-alt">From</label>
                  <input
                    v-model="filters.updatedFrom"
                    type="datetime-local"
                    class="input input-bordered input-xs w-full"
                    step="1"
                  />
                </div>
                <div class="flex-1">
                  <label class="label-text-alt">To</label>
                  <input
                    v-model="filters.updatedTo"
                    type="datetime-local"
                    class="input input-bordered input-xs w-full"
                    step="1"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </details>

      <!-- Actions -->
      <div class="card-actions justify-end">
        <button @click="clearFilters" class="btn btn-outline">Clear All Filters</button>
        <div class="badge badge-info">{{ filteredItems.length }} / {{ items.length }} transactions</div>
      </div>
    </div>
  </div>
</template>
