<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import { Icon } from '@iconify/vue'

import type { JSONify, Transaction } from '../services/mock-backend/types.js'
import { getDirection } from '../utils/transactions.js'

// Type for transformed transaction data used in DataTable
type TransformedTransaction = JSONify<Transaction> & {
  direction: string
  debtorBic: string
  debtorName: string
  debtorAmount: number
  debtorCurrency: string
  debtorAccountId: string
  creditorBic: string
  creditorName: string
  creditorAccountId: string
  creditorAmount: number
  creditorCurrency: string
  createdAtDate: Date
  updatedAtDate: Date
}

import UUID from './UUID.vue'
import TransactionActions from './TransactionActions.vue'

const props = defineProps<{
  transactions: JSONify<Transaction>[]
  clientMode?: boolean
}>()

const route = useRoute()
const ownBic = route.meta.bic as string

// Transform transactions for DataTable
const transformedTransactions = computed<TransformedTransaction[]>(() => {
  return props.transactions.map((transaction) => ({
    ...transaction,
    direction: getDirection(ownBic, transaction),
    debtorBic: transaction.debtor.bic,
    debtorName: transaction.debtor.name,
    debtorAccountId: transaction.debtor.accountId,
    debtorAmount:
      typeof transaction.debtor.amount === 'string' ? parseFloat(transaction.debtor.amount) : transaction.debtor.amount,
    debtorCurrency: transaction.debtor.currency,
    creditorBic: transaction.creditor.bic,
    creditorName: transaction.creditor.name,
    creditorAccountId: transaction.creditor.accountId,
    creditorAmount:
      typeof transaction.creditor.amount === 'string'
        ? parseFloat(transaction.creditor.amount)
        : transaction.creditor.amount,
    creditorCurrency: transaction.creditor.currency,
    createdAtDate: new Date(transaction.createdAt),
    updatedAtDate: new Date(transaction.updatedAt)
  }))
})

// Filters configuration for menu-based filtering
const filters = ref({
  global: { value: null, matchMode: 'contains' },
  type: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'in' }]
  },
  direction: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'in' }]
  },
  status: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'in' }]
  },
  uetr: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  debtorBic: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  debtorName: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  debtorAccountId: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  debtorCurrency: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'in' }]
  },
  debtorAmount: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'equals' }]
  },
  creditorBic: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  creditorName: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  creditorAccountId: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'contains' }]
  },
  creditorCurrency: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'in' }]
  },
  creditorAmount: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'equals' }]
  },
  createdAtDate: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'dateIs' }]
  },
  updatedAtDate: {
    operator: 'and',
    constraints: [{ value: null, matchMode: 'dateIs' }]
  }
})

// Options for dropdown filters
// const typeOptions = ref(['transfer', 'cancel'])
const directionOptions = ref(['in', 'out', 'N/A'])
const statusOptions = ref(['completed', 'pending', 'cancelled', 'rejected'])
const currencyOptions = computed(() => {
  const currencies = new Set([
    ...props.transactions.map((t) => t.debtor.currency),
    ...props.transactions.map((t) => t.creditor.currency)
  ])
  return Array.from(currencies)
})

// Pagination
const first = ref(0)
const rows = ref(10)

// Selected rows
const selectedTransactions = ref([])

defineEmits<{
  (e: 'transactionAccepted', acceptedTransaction: JSONify<Transaction> | null): void
  (e: 'transactionRejected', rejectedTransaction: JSONify<Transaction> | null): void
}>()

// Status severity mapping for PrimeVue Tag
const getStatusSeverity = (status: string) => {
  switch (status) {
    case 'completed':
      return 'success'
    case 'pending':
      return 'warn'
    case 'cancelled':
      return 'info'
    case 'rejected':
      return 'danger'
    default:
      return 'secondary'
  }
}

// Direction severity mapping
const getDirectionSeverity = (direction: string) => {
  switch (direction) {
    case 'out':
      return 'info'
    case 'in':
      return 'success'
    case 'N/A':
      return 'secondary'
    default:
      return 'secondary'
  }
}

// Check if BIC belongs to current bank
const isOwnBic = (bic: string) => bic === ownBic

// Clear all filters
const clearFilter = () => {
  filters.value = {
    global: { value: null, matchMode: 'contains' },
    type: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'in' }]
    },
    direction: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'in' }]
    },
    status: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'in' }]
    },
    uetr: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    debtorBic: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    debtorName: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    debtorAccountId: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    debtorCurrency: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'in' }]
    },
    debtorAmount: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'equals' }]
    },
    creditorBic: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    creditorName: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    creditorAccountId: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'contains' }]
    },
    creditorCurrency: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'in' }]
    },
    creditorAmount: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'equals' }]
    },
    createdAtDate: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'dateIs' }]
    },
    updatedAtDate: {
      operator: 'and',
      constraints: [{ value: null, matchMode: 'dateIs' }]
    }
  }
}
</script>

<template>
  <div class="card">
    <DataTable
      size="small"
      :value="transformedTransactions"
      v-model:filters="filters"
      v-model:selection="selectedTransactions"
      :paginator="true"
      :rows="rows"
      :first="first"
      :rowsPerPageOptions="[5, 10, 25, 50]"
      filterDisplay="menu"
      :globalFilterFields="[
        'type',
        'direction',
        'status',
        'uetr',
        'debtorBic',
        'debtorName',
        'debtorAccountId',
        'creditorBic',
        'creditorName',
        'creditorAccountId'
      ]"
      sortMode="multiple"
      removableSort
      :loading="false"
      dataKey="uetr"
      stripedRows
      showGridlines
    >
      <template #header>
        <div class="flex justify-end items-center">
          <Button type="button" label="Clear" outlined @click="clearFilter()">
            <template #icon>
              <Icon icon="mdi:filter-off" />
            </template>
          </Button>
        </div>
      </template>

      <!-- Direction Column -->
      <Column field="direction" header="Dir" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.direction" :severity="getDirectionSeverity(data.direction)" />
        </template>
        <template #filter="{ filterModel }">
          <MultiSelect v-model="filterModel.value" :options="directionOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- Status Column -->
      <Column field="status" header="Status" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.status" :severity="getStatusSeverity(data.status)" />
        </template>
        <template #filter="{ filterModel }">
          <MultiSelect v-model="filterModel.value" :options="statusOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- UETR Column -->
      <Column field="uetr" header="UETR" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <UUID :uuid="data.uetr" />
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by UETR" />
        </template>
      </Column>

      <!-- Debtor BIC Column -->
      <Column field="debtorBic" header="Debtor BIC" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <span
            :style="{
              fontWeight: isOwnBic(data.debtorBic) ? 'bold' : 'normal',
              color: isOwnBic(data.debtorBic) ? 'var(--primary-color)' : 'inherit'
            }"
          >
            {{ data.debtorBic }}
          </span>
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by BIC" />
        </template>
      </Column>

      <!-- Debtor Client ID Column -->
      <Column field="debtorAccountId" header="Debtor Account ID" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          {{ data.debtorAccountId }}
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by account ID" />
        </template>
      </Column>

      <!-- Creditor BIC Column -->
      <Column field="creditorBic" header="Creditor BIC" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <span
            :style="{
              fontWeight: isOwnBic(data.creditorBic) ? 'bold' : 'normal',
              color: isOwnBic(data.creditorBic) ? 'var(--primary-color)' : 'inherit'
            }"
          >
            {{ data.creditorBic }}
          </span>
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by BIC" />
        </template>
      </Column>

      <!-- Creditor Client ID Column -->
      <Column field="creditorAccountId" header="Creditor Account ID" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          {{ data.creditorAccountId }}
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by account ID" />
        </template>
      </Column>

      <!-- Creditor Amount Column -->
      <Column field="creditorAmount" header="Amount" sortable dataType="numeric">
        <template #body="{ data }: { data: TransformedTransaction }">
          <span>{{ data.creditorAmount.toFixed(2) }}</span>
        </template>
        <template #filter="{ filterModel }">
          <InputText v-model="filterModel.value" type="number" placeholder="Amount" />
        </template>
      </Column>

      <!-- Creditor Currency Column -->
      <Column field="creditorCurrency" header="Currency" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.creditorCurrency" severity="secondary" />
        </template>
        <template #filter="{ filterModel }">
          <MultiSelect v-model="filterModel.value" :options="currencyOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- Actions Column -->
      <Column v-if="!clientMode" header="Actions" :exportable="false">
        <template #body="{ data }: { data: TransformedTransaction }">
          <TransactionActions :transaction="data" />
        </template>
      </Column>

      <!-- Empty state -->
      <template #empty>
        <div>
          <Icon icon="mdi:inbox" />
          <p>No transactions found.</p>
        </div>
      </template>

      <!-- Loading state -->
      <template #loading> Loading transactions data. Please wait... </template>
    </DataTable>
  </div>
</template>
