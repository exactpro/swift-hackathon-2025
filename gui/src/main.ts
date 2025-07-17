import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { createHead } from '@unhead/vue/client'
import { router } from './router'

createApp(App)
  .use(
    createHead({
      init: [
        {
          title: 'Exactpro Swift Hackathon 2025',
          titleTemplate: '%s | Exactpro Swift 2025'
        }
      ]
    })
  )
  .use(router)
  .mount('#app')
