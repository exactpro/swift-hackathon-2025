<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAsyncState } from '@vueuse/core'
import { Icon } from '@iconify/vue'
import { formatAccountBalance } from '../utils/formatNumber'
import { fetchTransactionFormData, newTransaction } from '../services/transactions'
import type { Account, Currency } from '../services/mock-backend/types'
import { calculateExchangeValue } from '../utils/calculateExchangeValue'
import { useToasts } from '../composables/useToasts'

const { state: utils } = useAsyncState(fetchTransactionFormData(), null)

const { addToast } = useToasts()

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
  creditorName: string
  amount: number | null
  currency: Currency | null
  comment: string | null
}

const route = useRoute()

const ownBic = route.meta.bic as string
const debtorName = route.meta.clientName as string

const form = reactive<TransferForm>({
  creditorBic: '',
  creditorAccountId: '',
  creditorName: '',
  amount: 0,
  currency: props.debtorAccounts[0].currency,
  comment: ''
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
    form.creditorAccountId.trim() !== '' &&
    form.creditorName.trim() !== ''
  )
})

const isSending = ref(false)

async function startTransaction() {
  if (!isFormComplete.value || isSending.value) {
    addToast('Form is not complete, cannot start transaction', 'error')
    return
  }
  isSending.value = true
  try {
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
        name: form.creditorName,
        currency: form.currency!,
        amount: form.amount!
      },
      type: 'transfer',
      comment: form.comment || null
    })
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error'
    addToast(`Failed to initiate transfer: ${errorMessage}`, 'error')
    isSending.value = false
    return
  }
  isSending.value = false
  chosenAccount.value = props.debtorAccounts[0]
  form.creditorBic = ''
  form.creditorAccountId = ''
  form.creditorName = ''
  form.amount = 0
  form.currency = props.debtorAccounts[0].currency
  emit('completed')
  addToast('Transfer initiated successfully', 'success')
}
</script>

<template>
  <form v-if="utils" @submit.prevent="startTransaction" class="container mx-auto max-w-2xl">
    <!-- Debtor Section -->
    <section>
      <h2 class="section-title">Transfer Details</h2>

      <div class="p-5 bg-base-200 rounded-lg shadow-lg mb-6 flex flex-col gap-4">
        <!-- Currency -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">From Account</span>
          </div>
          <select v-model="chosenAccount" class="select w-full validator" required>
            <option disabled value="">Select account</option>
            <option v-for="account in props.debtorAccounts" :key="account.id" :value="account">
              {{ account.currency }} Account — {{ formatAccountBalance(account.currency, account.balance) }}
            </option>
          </select>
          <div class="validator-hint">Please select an account</div>
        </label>

        <!-- Creditor Section -->
        <h3 class="card-title text-xl">Recipient</h3>

        <!-- Bank -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Creditor BIC</span>
          </div>
          <input
            id="creditorBic"
            v-model="form.creditorBic"
            placeholder="Enter BIC/SWIFT code"
            class="input w-full validator"
            required
            pattern="^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$"
            title="BIC/SWIFT code must be 8 or 11 characters (e.g., DEUTDEFF)"
            list="bicList"
          />
          <datalist id="bicList">
            <option v-for="bic in utils.bics" :key="bic" :value="bic">{{ bic }}</option>
          </datalist>
          <div class="validator-hint">BIC/SWIFT code must be 8 or 11 characters</div>
          <div class="transfer-hint alert-soft">
            <Icon icon="mdi:information-variant" />
            <div>
              <p>
                To perform a successful test payment transaction, please use the following mock BIC: {{ utils.bics[0] }}
              </p>
              <p>
                You may also enter other BIC values for demonstration purposes, but transfers will fail or stay in
                pending status by design to showcase error handling.
              </p>
            </div>
          </div>
        </label>

        <!-- Client ID -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Creditor Account (IBAN)</span>
          </div>
          <input
            id="creditorIban"
            list="ibanList"
            v-model="form.creditorAccountId"
            type="text"
            placeholder="Enter IBAN"
            class="input w-full validator"
            required
            pattern="^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$"
            title="IBAN must start with 2 letters, followed by 2 digits, then up to 30 alphanumeric characters"
          />
          <datalist id="ibanList">
            <option v-for="iban in utils.ibans" :key="iban" :value="iban">{{ iban }}</option>
          </datalist>
          <div class="validator-hint">Valid IBAN required (e.g., DE89370400440532013000)</div>
          <div class="transfer-hint alert-soft">
            <Icon icon="mdi:information-variant" />
            <div>
              <p>To perform a successful test payment transaction, please use one of the following mock IBANs:</p>
              <ul class="list-disc pl-5">
                <li v-for="iban in utils.ibans" :key="iban">{{ iban }}</li>
              </ul>
              <p>
                You may also enter other IBAN values for demonstration purposes, but transfers will fail or stay in
                pending status by design to showcase error handling.
              </p>
            </div>
          </div>
        </label>

        <!-- Creditor Name -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Creditor Name</span>
          </div>
          <input
            v-model="form.creditorName"
            type="text"
            placeholder="Enter recipient name"
            class="input w-full validator"
            required
            minlength="2"
            maxlength="100"
            title="Creditor name is required"
          />
          <div class="validator-hint">Recipient name is required (2-100 characters)</div>
        </label>

        <!-- Currency -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Currency</span>
          </div>
          <select v-model="form.currency" class="select w-full validator" required>
            <option disabled value="">Select currency</option>
            <option v-for="currency in utils.currencies" :key="currency" :value="currency">
              {{ currency }}
            </option>
          </select>
          <div class="validator-hint">Please select a currency</div>
        </label>

        <!-- Amount -->
        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Amount</span>
          </div>
          <input
            v-model.number="form.amount"
            type="number"
            placeholder="250"
            class="input w-full pr-12 validator"
            step="0.01"
            min="0.01"
            required
            title="Amount must be greater than 0"
          />
          <div class="validator-hint">Amount must be greater than 0</div>
        </label>

        <label class="form-control w-full">
          <div class="label">
            <span class="label-text">Comment (Optional)</span>
          </div>
          <textarea
            v-model="form.comment"
            class="textarea w-full"
            placeholder="Enter additional payment details or reference"
            maxlength="255"
          ></textarea>
          <div class="validator-hint">Optional payment reference (max 255 characters)</div>
        </label>

        <div v-if="chosenAccount.currency !== form.currency" class="alert alert-soft flex flex-col mb-2">
          <div v-if="convertedAmount && chosenAccount">
            Converted Amount: {{ formatAccountBalance(chosenAccount.currency, convertedAmount) }}
          </div>
          <div class="text-sm" v-if="exchangeRate && chosenAccount && form.currency">
            Exchange Rate: {{ formatAccountBalance(chosenAccount.currency, 1) }} ≈
            {{ formatAccountBalance(form.currency, exchangeRate) }}
          </div>
        </div>

        <!-- Start Transaction Button -->
        <button
          type="submit"
          class="btn btn-primary btn-lg w-full shadow-lg/40 shadow-primary"
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

<style>
.validator-hint {
  display: none;
}
.validator {
  &:user-invalid,
  &:has(:user-invalid),
  &[aria-invalid]:not([aria-invalid='false']) {
    & ~ .validator-hint {
      display: block;
    }
  }
}
</style>

<style scoped>
@reference '../style.css';

.transfer-hint {
  display: none;
}
label:hover .transfer-hint,
label:has(:focus) .transfer-hint {
  @apply alert alert-info mt-2;
}

label > div:nth-child(1) {
  @apply mb-2 text-secondary-content text-sm;
}
input,
select {
  @apply text-base-content;
}
</style>
