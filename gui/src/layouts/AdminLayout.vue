<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import { Icon } from '@iconify/vue'
import Header from '../components/header/Header.vue'
import { useBankRoute } from '../composables/useBankRoute'
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
  <Header :home-link="bankRoute">
    <div class="badge badge-primary"><Icon icon="mdi:administrator" class="inline-block" /> Admin</div>
  </Header>
  <main class="p-3 lg:p-10 md:p-6">
    <RouterView />
  </main>
</template>

<style>
html {
  scrollbar-gutter: unset !important;
}
</style>
