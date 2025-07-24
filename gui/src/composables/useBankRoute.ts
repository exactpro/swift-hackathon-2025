import { computed, isRef } from 'vue'
import { useRoute } from 'vue-router'
import { joinURL } from 'ufo'
import type { MaybeRef } from 'vue'

export function useBankRoute(...sections: MaybeRef<string>[]) {
  const route = useRoute()
  return computed(() => {
    return joinURL(route.path.split('/').slice(0, 2).join('/'), ...sections.map((s) => (isRef(s) ? s.value : s)))
  })
}
