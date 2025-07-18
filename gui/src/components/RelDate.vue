<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue'

const props = defineProps<{
  date: string | Date
}>()

const parsedDate = new Date(props.date)
const timeout = ref<ReturnType<typeof setTimeout> | null>(null)
const formattedDate = ref(formatDate())

function formatDate(): String {
  if (timeout.value) {
    clearTimeout(timeout.value)
    timeout.value = null
  }
  const now = new Date()
  const diff = now.getTime() - parsedDate.getTime()
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  if (seconds < 60) {
    timeout.value = setTimeout(() => {
      formattedDate.value = formatDate()
    }, 1000)
    return `${seconds}s ago`
  } else if (minutes < 60) {
    timeout.value = setTimeout(() => {
      formattedDate.value = formatDate()
    }, 60000)
    return `${minutes}m ago`
  } else if (hours < 12) {
    timeout.value = setTimeout(() => {
      formattedDate.value = formatDate()
    }, 3600000)
    return `${hours}h ago`
  }
  return parsedDate.toLocaleDateString('fr', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onBeforeUnmount(() => {
  if (timeout.value) {
    clearTimeout(timeout.value)
  }
})
</script>

<template>
  <span class="badge badge-xs">
    {{ formattedDate }}
  </span>
</template>
