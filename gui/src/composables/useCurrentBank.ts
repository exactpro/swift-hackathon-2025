import { useRoute } from 'vue-router'
import { type BankName } from '../../config.js'

export function useCurrentBank(): BankName {
  const route = useRoute()
  return route.meta.bankName as BankName
}
