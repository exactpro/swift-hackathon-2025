import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // Default domain for development
  const env = loadEnv(mode, process.cwd())
  const domain = env.VITE_DOMAIN || 'https://swift-hackathon-2025.exactpro.com'

  return {
    plugins: [
      vue(),
      tailwindcss(),
      // Plugin to replace domain placeholders in HTML
      {
        name: 'html-transform',
        transformIndexHtml(html) {
          return html.replace(/%VITE_DOMAIN%/g, domain)
        }
      }
    ],
    define: {
      __DOMAIN__: JSON.stringify(domain)
    }
  }
})
