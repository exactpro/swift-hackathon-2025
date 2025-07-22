<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute'
const bankRoute = useBankRoute('admin')
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink :to="bankRoute" class="btn btn-ghost text-xl">
        <Icon icon="mdi:bank" class="inline-block mr-2" />
        Bank A Portal
      </RouterLink>
    </div>
    <div class="flex-none"></div>
  </nav>
  <main class="p-3 lg:p-10 md:p-6">
    <RouterView />
  </main>
</template>

<style>
html {
  scrollbar-gutter: unset !important;
}
</style>
