<script setup lang="ts">
import { useRoute } from 'vue-router'
import { type ClientDataResponse } from '../services/clients'
import { computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps<{
  client?: ClientDataResponse | null
  isLoading?: boolean
  refresh?: () => void
}>()

const route = useRoute()
const bic = route.meta.bic as string
const clientId = route.meta.clientId as string

const displayTitle = computed(() => {
  return props.client ? props.client.fullName : clientId
})

const avatarPlaceholder = computed(() => {
  if (!props.client) return 'C'
  return props.client.fullName
    .split(' ')
    .map((name) => name[0])
    .join('')
    .toUpperCase()
})
</script>
<template>
  <div class="grid grid-cols-1 md:grid-cols-2 my-4 items-center gap-4">
    <div>
      <div class="flex gap-2 items-start">
        <figure class="avatar avatar-placeholder">
          <div class="bg-primary text-primary-content w-14 md:w-16 rounded-full">
            <span class="text-xl md:text-2xl">{{ avatarPlaceholder }}</span>
          </div>
        </figure>
        <div>
          <h1 class="text-2xl sm:text-3xl md:text-4xl font-bold">
            {{ displayTitle }}
          </h1>
          <div v-if="client" class="text-sm text-gray-500">
            {{ bic }}
          </div>
        </div>
      </div>
    </div>

    <div v-if="refresh" class="flex flex-wrap md:justify-end items-center gap-2">
      <button @click="refresh" :disabled="isLoading" class="btn btn-secondary btn-sm" :class="{ disabled: isLoading }">
        <template v-if="!isLoading"> Refresh <Icon icon="mdi:refresh" class="inline-block" /> </template>
        <template v-else>
          <span class="loading loading-spinner loading-sm"></span>
        </template>
      </button>
    </div>
  </div>
</template>
