import { onMounted, onBeforeUnmount } from 'vue'

export function useFakeSocket(refreshCallback: () => Promise<void>, waitTime = 1000) {
  let intervalId: ReturnType<typeof setInterval>

  onMounted(() => {
    intervalId = setInterval(() => {
      refreshCallback()
    }, waitTime)
  })

  onBeforeUnmount(() => {
    clearInterval(intervalId)
  })
}
