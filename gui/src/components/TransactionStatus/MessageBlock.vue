<script setup lang="ts">
import { Icon } from '@iconify/vue'

interface Message {
  title: string
  status: 'expecting' | 'received'
  viewerUrl?: string
}

interface Props {
  message?: Message | null
  noLink?: boolean
  networkType: 'primary' | 'secondary' | 'accent'
  noMessageText?: string
  showInProgressBadge?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  noMessageText: 'No message',
  showInProgressBadge: false,
  noLink: false
})

function getStatusIcon(status: 'expecting' | 'received') {
  return status === 'received' ? 'mdi:check-circle' : 'mdi:clock-outline'
}

const getNetworkStyles = (status: 'received' | 'expecting') => {
  const baseClasses = 'card shadow-sm h-full'
  const backgroundClasses = {
    primary: status === 'expecting' ? 'bg-base-100 border-dashed border-2 border-primary' : 'bg-primary',
    secondary: status === 'expecting' ? 'bg-base-100 border-dashed border-2 border-secondary' : 'bg-secondary',
    accent: status === 'expecting' ? 'bg-base-100 border-dashed border-2 border-accent' : 'bg-accent'
  }

  return `${baseClasses} ${backgroundClasses[props.networkType]}`
}
</script>

<template>
  <div v-if="message" :class="getNetworkStyles(message.status)">
    <a
      v-if="!props.noLink && message.status === 'received' && message.viewerUrl"
      :href="message.viewerUrl"
      target="_blank"
      rel="noopener noreferrer"
      class="block"
    >
      <div class="card-body p-2 text-center hover:opacity-80 transition-opacity">
        <h4 class="font-medium text-sm">{{ message.title }}</h4>
        <div class="flex items-center justify-center gap-1 mt-1">
          <Icon :icon="getStatusIcon(message.status)" class="w-4 h-4" />
          <span class="text-xs">{{ message.status }}</span>
        </div>
      </div>
    </a>
    <div v-else class="card-body p-2 text-center">
      <h4 class="font-medium text-sm">{{ message.title }}</h4>
      <div class="flex items-center justify-center gap-1 mt-1">
        <Icon :icon="getStatusIcon(message.status)" class="w-4 h-4" />
        <span class="text-xs">{{ message.status }}</span>
      </div>
    </div>
  </div>
  <div v-else class="card bg-base-300 border-2 border-dashed border-gray-400 shadow-sm h-full">
    <div class="card-body p-2 text-center text-gray-500">
      <span class="text-xs">{{ props.noMessageText }}</span>
    </div>
  </div>
</template>
