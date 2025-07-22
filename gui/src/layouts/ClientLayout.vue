<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'

const sidebarLinks = [
  { name: 'Transactions', path: '/transactions' },
  { name: 'Clients', path: '/clients' }
]
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink to="/" class="btn btn-ghost text-xl">
        <Icon icon="mdi:bank" class="inline-block mr-2" />
        Bank A Client
      </RouterLink>
    </div>
    <div class="flex-none">
      <ul class="menu menu-horizontal px-1">
        <li v-for="link of sidebarLinks">
          <RouterLink :to="link.path" active-class="menu-active">{{ link.name }}</RouterLink>
        </li>
      </ul>
    </div>
  </nav>
  <main class="drawer-content p-3 lg:p-10 md:p-6">
    <RouterView />
  </main>
</template>

<style>
html {
  scrollbar-gutter: unset !important;
}
</style>
