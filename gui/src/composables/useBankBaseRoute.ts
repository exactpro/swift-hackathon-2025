import { computed } from 'vue'
import { useRoute } from 'vue-router'

export function useBankBaseRoute() {
  const route = useRoute()
  return computed(() => {
    return route.path.split('/').slice(0, 2).join('/')
  })
}
