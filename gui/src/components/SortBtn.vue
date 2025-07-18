<script setup lang="ts">
import { ref } from 'vue'
import type { JSONify, Transaction } from '../services/mock-backend/types.js'
import { Icon } from '@iconify/vue'
export interface SortItem {
  label: string
  getProp: (item: JSONify<Transaction>) => string | number | Date
  direction: 'asc' | 'desc'
}

const direction = ref<'asc' | 'desc' | null>(null)

const props = defineProps<{
  label: string
  getProp: (item: any) => string | number | Date
}>()
const emit = defineEmits<{
  (e: 'sort', sortItem: SortItem): void
}>()

function onClick() {
  direction.value = direction.value === 'asc' ? 'desc' : 'asc'
  emit('sort', {
    label: props.label,
    getProp: props.getProp,
    direction: direction.value
  })
}
</script>

<template>
  <button class="btn btn-xs text-base-content" @click="onClick">
    <Icon v-if="!direction" icon="mdi:sort" />
    <Icon v-else-if="direction === 'asc'" icon="mdi:sort-ascending" />
    <Icon v-else icon="mdi:sort-descending" />
  </button>
</template>
