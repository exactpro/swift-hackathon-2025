<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute.js'
import Logo from '../components/Logo.vue'

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
      <RouterLink :to="homeLink" class="btn btn-primary">
        <Logo />
      </RouterLink>
    </div>
    <div class="flex-none flex items-center">
      <ul class="menu menu-horizontal px-1 gap-1">
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
      <div class="badge badge-primary"><Icon icon="mdi:account" class="inline-block" /> {{ clientName }}</div>
      <details class="dropdown dropdown-end sm:hidden">
        <summary class="btn btn-sm btn-ghost btn-circle">
          <Icon icon="mdi:dots-vertical" class="inline-block text-lg" />
        </summary>
        <ul class="menu dropdown-content bg-base-100 text-base-content rounded-box z-1 w-52 p-2 shadow-sm">
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
