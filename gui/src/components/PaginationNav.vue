<script setup lang="ts">
import { Icon } from '@iconify/vue'
import type { PaginationButton } from '../composables/usePagination.js'
defineProps<{
  currentPage: number
  beforeButtons: PaginationButton[]
  afterButtons: PaginationButton[]
}>()
</script>

<template>
  <nav class="join">
    <button
      v-for="(btn, i) in beforeButtons"
      class="join-item btn btn-sm md:btn-md"
      @click="
        () => {
          if (btn.action) btn.action()
        }
      "
      :class="{
        'btn-disabled': !btn.action
      }"
      :key="i"
    >
      <Icon v-if="btn.icon" :icon="btn.icon" />
      <span v-else-if="btn.label">{{ btn.label }}</span>
    </button>
    <button class="join-item btn btn-primary btn-sm md:btn-md">
      {{ currentPage }}
    </button>
    <button
      v-for="(btn, i) in afterButtons"
      class="join-item btn btn-sm md:btn-md"
      @click="
        () => {
          if (btn.action) btn.action()
        }
      "
      :class="{
        'btn-disabled': !btn.action
      }"
      :key="i"
    >
      <Icon v-if="btn.icon" :icon="btn.icon" />
      <span v-else-if="btn.label">{{ btn.label }}</span>
    </button>
  </nav>
</template>
