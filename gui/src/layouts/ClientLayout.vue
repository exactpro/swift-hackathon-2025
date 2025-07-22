<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute.js'

const route = useRoute()
const clientName = route.meta.clientName as string

const homeLink = useBankRoute()
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink :to="homeLink" class="btn btn-ghost text-xl">
        <Icon icon="mdi:bank" class="inline-block mr-2" />
        Bank A Client
      </RouterLink>
    </div>
    <div class="flex-none">
      <ul class="menu menu-horizontal px-1">
        <li>
          <button class="menu-active">{{ clientName }} <Icon icon="mdi:account" class="inline-block ml-1" /></button>
        </li>
      </ul>
    </div>
  </nav>
  <main class="p-3 lg:p-10 md:p-6">
    <RouterView />
  </main>
</template>
