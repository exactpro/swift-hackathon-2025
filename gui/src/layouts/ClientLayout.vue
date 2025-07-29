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
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink :to="homeLink" class="btn btn-ghost sm:text-lg md:text-xl">
        <Icon icon="mdi:bank" class="inline-block mr-2" />
        {{ bankName }} Client
      </RouterLink>
    </div>
    <div class="flex-none flex items-center">
      <ul class="menu menu-horizontal menu-sm md:menu-md px-1 gap-1">
        <li class="hidden sm:block">
          <RouterLink :to="transferLink" active-class="menu-active">
            <Icon icon="mdi:transfer" class="inline-block" />
            Transfer
          </RouterLink>
        </li>
        <li class="hidden sm:block">
          <RouterLink :to="homeLink" exact-active-class="menu-active">
            <Icon icon="mdi:view-dashboard" class="inline-block" />
            Dashboard
          </RouterLink>
        </li>
      </ul>
      <details class="dropdown dropdown-end">
        <summary class="btn btn-sm btn-primary">
          {{ clientName }} <Icon icon="mdi:account" class="inline-block ml-1" />
        </summary>
        <ul class="menu dropdown-content bg-base-100 text-base-content rounded-box z-1 w-52 p-2 shadow-sm">
          <li>{{ bankName }}</li>
          <li>BIC: {{ bic }}</li>
          <li>
            <RouterLink :to="homeLink" exact-active-class="menu-active">
              <Icon icon="mdi:view-dashboard" class="inline-block" />
              Dashboard
            </RouterLink>
          </li>
          <li>
            <RouterLink :to="transferLink" active-class="menu-active">
              <Icon icon="mdi:transfer" class="inline-block" />
              Transfer
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
