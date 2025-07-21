<script setup lang="ts">
import { RouterLink } from 'vue-router'
const props = defineProps<{
  items: { title: string; link: string }[]
}>()
</script>

<template>
  <nav class="breadcrumbs">
    <ul>
      <li v-for="item in items" :key="item.link" class="link">
        <RouterLink :to="item.link">{{ item.title }}</RouterLink>
      </li>
    </ul>
  </nav>
</template>
