<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import Footer from './components/footer/Footer.vue'
import config from '../config'
import { watch } from 'vue'
import ToastMessage from './components/ToastMessage.vue'
import { useToasts } from './composables/useToasts'

const bankATheme = 'banka'
const bankBTheme = 'dim'

const route = useRoute()
const { toasts } = useToasts()

watch(
  () => route.meta.bankName,
  (newBankName) => {
    if (newBankName === config.bankA.name) {
      document.documentElement.setAttribute('data-theme', bankATheme)
    } else if (newBankName === config.bankB.name) {
      document.documentElement.setAttribute('data-theme', bankBTheme)
    } else {
      document.documentElement.removeAttribute('data-theme')
    }
  }
)
</script>

<template>
  <div class="min-h-screen contain-layout grid grid-rows-[1fr_auto]">
    <main class="min-w-0">
      <RouterView />
    </main>
    <Footer />
  </div>
  <div class="toast">
    <ToastMessage v-for="toast in toasts" :key="toast.id" :toast="toast" />
  </div>
</template>

<style>
html {
  line-height: 1;
}
</style>
