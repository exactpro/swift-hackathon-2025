import { ref } from 'vue'

export type ToastType = 'success' | 'error' | 'info'

export interface Toast {
  id: number
  message: string
  type: ToastType
}

const toasts = ref<Toast[]>([])

export function useToasts() {
  function addToast(message: string, type: ToastType = 'info') {
    const id = Date.now()
    toasts.value.push({ id, message, type })
  }

  function removeToast(id: number) {
    toasts.value = toasts.value.filter((toast) => toast.id !== id)
  }

  return {
    toasts,
    addToast,
    removeToast
  }
}
