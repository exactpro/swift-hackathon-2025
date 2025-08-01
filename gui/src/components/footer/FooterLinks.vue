<script setup lang="ts">
import { Icon } from '@iconify/vue'

const socialLinks = [
  {
    name: 'GitHub',
    icon: 'fa6-brands:square-github',
    url: 'https://github.com/Exactpro',
    color: 'hover:text-gray-900'
  },
  {
    name: 'Facebook',
    icon: 'fa6-brands:square-facebook',
    url: 'https://www.facebook.com/exactpro/',
    color: 'hover:text-blue-600'
  },
  {
    name: 'X (Twitter)',
    icon: 'fa6-brands:square-x-twitter',
    url: 'https://twitter.com/exactpro',
    color: 'hover:text-gray-900'
  },
  {
    name: 'LinkedIn',
    icon: 'fa6-brands:linkedin',
    url: 'https://www.linkedin.com/company/exactpro-systems-llc?trk=biz-companies-cym',
    color: 'hover:text-blue-700'
  },
  {
    name: 'YouTube',
    icon: 'fa6-brands:square-youtube',
    url: 'https://www.youtube.com/c/exactprosystems',
    color: 'hover:text-red-600'
  }
]
</script>

<template>
  <div class="flex items-center gap-2">
    <a
      v-for="link in socialLinks"
      :key="link.name"
      :href="link.url"
      target="_blank"
      rel="noopener noreferrer"
      class="text-white hover:scale-110 transition-transform duration-200"
      :title="link.name"
    >
      <Icon :icon="link.icon" class="w-6 h-6" />
    </a>
  </div>
</template>
