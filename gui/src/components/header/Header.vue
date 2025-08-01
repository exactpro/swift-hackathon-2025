<script setup lang="ts">
import { RouterLink } from 'vue-router'
import HeaderBgImage from './HeaderBgImage.vue'
import CoincentoLogo from './CoincentoLogo.vue'

defineProps<{
  homeLink?: string
  noBgImage?: boolean
}>()
</script>

<template>
  <header class="relative h-16 bg-linear-to-r from-grad-bg-1 to-grad-bg-2">
    <figure v-if="!noBgImage" class="absolute inset-0 z-0 overflow-hidden flex justify-end">
      <HeaderBgImage class="header-bg-image" />
    </figure>
    <div class="relative z-10 flex items-center h-full px-3 md:px-6 lg:px-10 justify-between">
      <RouterLink to="/">
        <CoincentoLogo class="h-6 sm:h-8 w-auto" />
      </RouterLink>
      <div class="flex-none flex items-center text-white gap-1">
        <slot />
      </div>
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
