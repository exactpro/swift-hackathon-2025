<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '../components/header/Header.vue'
import { useBankRoute } from '../composables/useBankRoute.js'
import { computed } from 'vue'
import { useHead } from '@unhead/vue'

const route = useRoute()
const clientName = route.meta.clientName as string
const bankName = route.meta.bankName as string

const titleTemplate = computed(() => {
  return `%s | ${bankName} - Coincento`
})

useHead({ titleTemplate })

const homeLink = useBankRoute()
const transferLink = useBankRoute('transfer')
</script>

<template>
  <Header no-bg-image>
    <ul class="px-1 gap-1 flex">
      <li class="hidden sm:block">
        <RouterLink :to="transferLink" class="btn btn-sm btn-ghost" active-class="btn-active">
          <Icon icon="mdi:transfer" class="inline-block" />
          Transfer
        </RouterLink>
      </li>
      <li class="hidden sm:block">
        <RouterLink :to="homeLink" class="btn btn-sm btn-ghost" exact-active-class="btn-active">
          <Icon icon="mdi:view-dashboard" class="inline-block" />
          Dashboard
        </RouterLink>
      </li>
    </ul>
    <div class="font-bold text-sm flex items-center gap-1">
      <Icon icon="mdi:account" class="inline-block" /> {{ clientName }}
    </div>
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
  </Header>
  <main class="p-3 md:p-6 lg:p-10">
    <RouterView />
  </main>
</template>
