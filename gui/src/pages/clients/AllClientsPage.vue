<script setup lang="ts">
import { useAsyncState } from '@vueuse/core'
import { fetchAllClients } from '../../services/clients.js'
import { formatNumber } from '../../utils/formatNumber.js'
import UUID from '../../components/UUID.vue'
import { RouterLink } from 'vue-router'

const { state: clients } = useAsyncState(fetchAllClients, [])
</script>

<template>
  <div>
    <h1>Clients</h1>
    <section>
      <div v-for="client in clients" :key="client.id" class="client-card">
        <div class="font-bold col-span-2 md:col-span-1">
          {{ client.fullName }}
        </div>
        <UUID class="col-span-2 md:col-span-1" :uuid="client.id" />
        <div class="whitespace-nowrap">
          <span class="text-accent">€</span
          >{{ formatNumber(client.accounts.EUR?.balance || 0) }}
        </div>
        <div class="whitespace-nowrap">
          <span class="text-accent">$</span
          >{{ formatNumber(client.accounts.USD?.balance || 0) }}
        </div>
        <div class="whitespace-nowrap">
          {{ formatNumber(client.accounts.SUSDC?.balance || 0) }}
          <span class="text-accent">S-USDC</span>
        </div>
        <div class="row-span-2 md:row-span-1 text-right">
          <RouterLink class="btn btn-primary btn-sm" :to="`/clients/${client.id}`">View</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
@reference "../../style.css";

.client-card {
  @apply bg-base-200 p-4 mb-4 rounded-lg text-sm
         grid gap-2
         grid-cols-4
         md:grid-cols-6;
}
</style>
