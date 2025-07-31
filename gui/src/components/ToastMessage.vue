<script setup lang="ts">
import { Icon } from '@iconify/vue'
import { useToasts, type Toast } from '../composables/useToasts'

const props = defineProps<{
  toast: Toast
}>()

const { removeToast } = useToasts()
</script>

<template>
  <div
    class="alert alert-soft flex justify-between shadow-lg max-w-sm"
    :class="{
      'alert-success': props.toast.type === 'success',
      'alert-error': props.toast.type === 'error',
      'alert-info': props.toast.type === 'info'
    }"
  >
    {{ props.toast.message }}
    <button @click="removeToast(props.toast.id)" class="btn btn-xs btn-circle btn-outline">
      <Icon icon="mdi:close" class="text-sm" />
    </button>
  </div>
</template>
