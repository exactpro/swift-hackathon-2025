import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { createHead } from '@unhead/vue/client'
import { router } from './router'

createApp(App).use(createHead()).use(router).mount('#app')
