<script setup lang="ts">
import { RouterView, RouterLink, useRoute } from 'vue-router'
import config from '../config'
import { watch } from 'vue'

const bankATheme = 'pastel'
const bankBTheme = 'dim'

const route = useRoute()

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
    <footer class="footer sm:footer-horizontal footer-center bg-base-300 text-base-content p-4">
      <aside>
        <RouterLink to="/" class="btn btn-secondary btn-xs">
          <span class="font-bold">Root Navigation</span>
        </RouterLink>
        <span class="text-sm" v-if="config.useMock">
          You are using GUI with mock data. Pay attention, that data is syncronized only within the same browser tab.
        </span>
      </aside>
    </footer>
  </div>
</template>

<style>
html {
  line-height: 1;
}
</style>
