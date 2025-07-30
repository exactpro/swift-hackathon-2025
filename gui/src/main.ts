import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { createHead } from '@unhead/vue/client'
import { router } from './router'

import PrimeVue from 'primevue/config'
import Aura from '@primeuix/themes/aura'
import { Tooltip } from 'primevue'

createApp(App)
  .use(
    createHead({
      init: [
        {
          title: 'Complient Crypto Transfers',
          titleTemplate: '%s | Coincento'
        }
      ]
    })
  )
  .use(router)
  .use(PrimeVue, {
    theme: {
      preset: Aura,
      options: {
        darkModeSelector: '[data-theme="dim"]'
      }
    }
  })
  .directive('tooltip', Tooltip)
  .mount('#app')
