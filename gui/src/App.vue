<script setup lang="ts">
import { RouterView, RouterLink } from 'vue-router'
import config from '../config'
</script>

<template>
  <div>
    <div class="bg-base-300 p-2">
      <RouterLink to="/" class="btn btn-secondary btn-xs">
        <span class="font-bold">Root Navigation</span>
      </RouterLink>
      <span class="text-sm" v-if="config.useMock">
        You are using GUI with mock data. Pay attention, that data is syncronized only within the same browser tab.
      </span>
    </div>
    <RouterView />
  </div>
</template>

<style>
html {
  line-height: 1;
}
</style>
