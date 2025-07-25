<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute.js'

const route = useRoute()
const clientName = route.meta.clientName as string
const bic = route.meta.bic as string
const bankName = route.meta.bankName as string

const homeLink = useBankRoute()
const transferLink = useBankRoute('transfer')
const exchangeLink = useBankRoute('exchange')
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink :to="homeLink" class="btn btn-ghost text-xl">
        <Icon icon="mdi:bank" class="inline-block mr-2" />
        {{ bankName }} Client
      </RouterLink>
    </div>
    <div class="flex-none">
      <details class="dropdown dropdown-end">
        <summary class="btn btn-sm btn-neutral rounded-full">
          {{ clientName }} <Icon icon="mdi:account" class="inline-block ml-1" />
        </summary>
        <ul class="menu dropdown-content bg-base-100 rounded-box z-1 w-52 p-2 shadow-sm">
          <li>{{ bankName }}</li>
          <li>BIC: {{ bic }}</li>
          <li>
            <RouterLink :to="homeLink" exact-active-class="menu-active">
              <Icon icon="mdi:home" class="inline-block" />
              Home
            </RouterLink>
          </li>
          <li>
            <RouterLink :to="transferLink" active-class="menu-active">
              <Icon icon="mdi:transfer" class="inline-block" />
              Transfer
            </RouterLink>
          </li>
          <li>
            <RouterLink :to="exchangeLink" active-class="menu-active">
              <Icon icon="mdi:swap-horizontal" class="inline-block" />
              Exchange
            </RouterLink>
          </li>
        </ul>
      </details>
    </div>
  </nav>
  <main class="p-3 lg:p-10 md:p-6">
    <RouterView />
  </main>
</template>
