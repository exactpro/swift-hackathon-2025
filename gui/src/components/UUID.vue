<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  uuid: string
}>()

const shortUuid = computed(() => {
  return props.uuid.length > 8 ? props.uuid.slice(0, 4) + '...' + props.uuid.slice(-4) : props.uuid
})
</script>

<template>
  <div class="tooltip" :data-tip="uuid">
    {{ shortUuid }}
  </div>
</template>
