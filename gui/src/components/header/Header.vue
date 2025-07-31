<script setup lang="ts">
import { RouterLink } from 'vue-router'
import HeaderBgImage from './HeaderBgImage.vue'
import CoincentoLogo from './CoincentoLogo.vue'

defineProps<{
  homeLink?: string
}>()
</script>

<template>
  <header class="relative h-20 bg-linear-to-r from-grad-bg-1 to-grad-bg-2">
    <figure class="absolute inset-0 z-0 overflow-hidden flex justify-end">
      <HeaderBgImage class="header-bg-image" />
    </figure>
    <div class="relative z-10 flex items-center h-full pl-5">
      <RouterLink :to="homeLink || '/'">
        <CoincentoLogo class="h-12 w-auto" />
      </RouterLink>
    </div>
  </header>
</template>

<style scoped>
@reference "../../style.css";
.header-bg-image {
  height: 150%;
  @apply -mt-4 mr-4 sm:mr-10;
}
</style>
