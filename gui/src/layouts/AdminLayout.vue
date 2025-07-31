<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useBankRoute } from '../composables/useBankRoute'
import Logo from '../components/header/CoincentoLogo.vue'
import { computed } from 'vue'
import { useHead } from '@unhead/vue'

const bankRoute = useBankRoute('admin')
const route = useRoute()
const bankName = route.meta.bankName as string

const titleTemplate = computed(() => {
  return `%s | ${bankName} - Coincento`
})

useHead({ titleTemplate })
</script>

<template>
  <nav class="navbar bg-primary text-primary-content shadow-sm">
    <div class="flex-1">
      <RouterLink :to="bankRoute" class="btn btn-primary text-xl">
        <Logo />
      </RouterLink>
    </div>
    <div class="flex-none">
      <div class="badge badge-primary"><Icon icon="mdi:administrator" class="inline-block" /> Admin</div>
    </div>
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
