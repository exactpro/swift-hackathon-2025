<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import InputText from 'primevue/inputtext'
import MultiSelect from 'primevue/multiselect'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
import DatePicker from 'primevue/datepicker'
import { Icon } from '@iconify/vue'

import type { JSONify, Transaction } from '../services/mock-backend/types.js'
import { getDirection } from '../utils/transactions.js'

// Type for transformed transaction data used in DataTable
type TransformedTransaction = JSONify<Transaction> & {
  direction: string
  debtorBic: string
  debtorName: string
  debtorClientId: string
  debtorAmount: number
  debtorCurrency: string
  creditorBic: string
  creditorName: string
  creditorClientId: string
  creditorAmount: number
  creditorCurrency: string
  createdAtDate: Date
  updatedAtDate: Date
}

// Type for filter model used in filter templates
interface FilterModel {
  value: any
  matchMode?: string
}
import { useBankRoute } from '../composables/useBankRoute.js'
import UUID from './UUID.vue'
import RelDate from './RelDate.vue'
import TransactionActions from './TransactionActions.vue'

const props = defineProps<{
  transactions: JSONify<Transaction>[]
}>()

const route = useRoute()
const ownBic = route.meta.bic as string
const clientId = route.meta.accountId as string | undefined
const clientPageLink = useBankRoute()

// Transform transactions for DataTable
const transformedTransactions = computed(() => {
  return props.transactions.map((transaction) => ({
    ...transaction,
    direction: getDirection(ownBic, transaction),
    debtorBic: transaction.debtor.bic,
    debtorName: transaction.debtor.name,
    debtorClientId: transaction.debtor.accountId,
    debtorAmount:
      typeof transaction.debtor.amount === 'string' ? parseFloat(transaction.debtor.amount) : transaction.debtor.amount,
    debtorCurrency: transaction.debtor.currency,
    creditorBic: transaction.creditor.bic,
    creditorName: transaction.creditor.name,
    creditorClientId: transaction.creditor.accountId,
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
  debtorClientId: {
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
  creditorClientId: {
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
const typeOptions = ref(['transfer', 'cancel'])
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

// Type severity mapping
const getTypeSeverity = (type: string) => {
  switch (type) {
    case 'transfer':
      return 'info'
    case 'cancel':
      return 'warn'
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

// Check if client belongs to current user
const isOwnClient = (clientIdToCheck: string) => clientId && clientIdToCheck === clientId

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
    debtorClientId: {
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
    creditorClientId: {
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
        'debtorClientId',
        'creditorBic',
        'creditorName',
        'creditorClientId'
      ]"
      sortMode="multiple"
      removableSort
      :loading="false"
      dataKey="uetr"
      stripedRows
      showGridlines
    >
      <template #header>
        <div class="flex justify-between items-center">
          <Button type="button" label="Clear" outlined @click="clearFilter()">
            <template #icon>
              <Icon icon="mdi:filter-off" />
            </template>
          </Button>
          <IconField iconPosition="left">
            <InputIcon>
              <Icon icon="mdi:magnify" />
            </InputIcon>
            <InputText v-model="filters.global.value" placeholder="Keyword Search" />
          </IconField>
        </div>
      </template>

      <!-- Type Column -->
      <Column field="type" header="Type" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.type" :severity="getTypeSeverity(data.type)" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <MultiSelect v-model="filterModel.value" :options="typeOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- Direction Column -->
      <Column field="direction" header="Dir" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.direction" :severity="getDirectionSeverity(data.direction)" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <MultiSelect v-model="filterModel.value" :options="directionOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- Status Column -->
      <Column field="status" header="Status" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.status" :severity="getStatusSeverity(data.status)" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <MultiSelect v-model="filterModel.value" :options="statusOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- UETR Column -->
      <Column field="uetr" header="UETR" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <UUID :uuid="data.uetr" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
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
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by BIC" />
        </template>
      </Column>

      <!-- Debtor Name Column -->
      <Column field="debtorName" header="Debtor Name" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <div>
            <div v-if="isOwnClient(data.debtorClientId)">
              <RouterLink :to="clientPageLink">
                {{ data.debtorName }}
              </RouterLink>
            </div>
            <div v-else>
              {{ data.debtorName }}
            </div>
          </div>
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by name" />
        </template>
      </Column>

      <!-- Debtor Client ID Column -->
      <Column field="debtorClientId" header="Debtor Client ID" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <UUID :uuid="data.debtorClientId" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by client ID" />
        </template>
      </Column>

      <!-- Debtor Amount Column -->
      <Column field="debtorAmount" header="Debtor Amount" sortable dataType="numeric">
        <template #body="{ data }: { data: TransformedTransaction }">
          <span>{{ data.debtorAmount.toFixed(2) }}</span>
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="number" placeholder="Amount" />
        </template>
      </Column>

      <!-- Debtor Currency Column -->
      <Column field="debtorCurrency" header="Debtor Currency" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.debtorCurrency" severity="secondary" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <MultiSelect v-model="filterModel.value" :options="currencyOptions" placeholder="Any" />
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
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by BIC" />
        </template>
      </Column>

      <!-- Creditor Name Column -->
      <Column field="creditorName" header="Creditor Name" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <div>
            <div v-if="isOwnClient(data.creditorClientId)">
              <RouterLink :to="clientPageLink">
                {{ data.creditorName }}
              </RouterLink>
            </div>
            <div v-else>
              {{ data.creditorName }}
            </div>
          </div>
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by name" />
        </template>
      </Column>

      <!-- Creditor Client ID Column -->
      <Column field="creditorClientId" header="Creditor Client ID" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <UUID :uuid="data.creditorClientId" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="text" placeholder="Search by client ID" />
        </template>
      </Column>

      <!-- Creditor Amount Column -->
      <Column field="creditorAmount" header="Creditor Amount" sortable dataType="numeric">
        <template #body="{ data }: { data: TransformedTransaction }">
          <span>{{ data.creditorAmount.toFixed(2) }}</span>
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <InputText v-model="filterModel.value" type="number" placeholder="Amount" />
        </template>
      </Column>

      <!-- Creditor Currency Column -->
      <Column field="creditorCurrency" header="Creditor Currency" sortable>
        <template #body="{ data }: { data: TransformedTransaction }">
          <Tag :value="data.creditorCurrency" severity="secondary" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <MultiSelect v-model="filterModel.value" :options="currencyOptions" placeholder="Any" />
        </template>
      </Column>

      <!-- Created At Column -->
      <Column field="createdAtDate" header="Created" sortable dataType="date">
        <template #body="{ data }: { data: TransformedTransaction }">
          <RelDate :date="data.createdAt" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <DatePicker
            v-model="filterModel.value"
            dateFormat="yy-mm-dd"
            showTime
            showSeconds
            showIcon
            placeholder="Select date and time"
          />
        </template>
      </Column>

      <!-- Updated At Column -->
      <Column field="updatedAtDate" header="Updated" sortable dataType="date">
        <template #body="{ data }: { data: TransformedTransaction }">
          <RelDate :date="data.updatedAt" />
        </template>
        <template #filter="{ filterModel }: { filterModel: FilterModel }">
          <DatePicker
            v-model="filterModel.value"
            dateFormat="yy-mm-dd"
            showTime
            showSeconds
            showIcon
            placeholder="Select date and time"
          />
        </template>
      </Column>

      <!-- Actions Column -->
      <Column header="Actions" :exportable="false">
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
