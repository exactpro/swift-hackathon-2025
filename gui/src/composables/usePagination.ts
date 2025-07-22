import { computed, ref, type Ref } from 'vue'
export interface PaginationOptions {
  itemsPerPage: number
  aroundButtons: number
  initPage: number
}

export interface PaginationButton {
  label?: string
  icon?: string
  action: (() => void) | null
}

export function usePagination<T>(
  items: Ref<T[]>,
  { itemsPerPage = 10, aroundButtons = 2, initPage = 1 }: Partial<PaginationOptions>
) {
  const totalItems = computed(() => items.value.length)
  const totalPages = computed(() => Math.ceil(totalItems.value / itemsPerPage))
  const currentPage = ref(initPage)
  const currentPageItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage
    const end = start + itemsPerPage
    return items.value.slice(start, end)
  })
  function setPage(page: number) {
    if (page < 1 || page > totalPages.value) {
      return
    }
    currentPage.value = page
  }
  function nextPage() {
    if (currentPage.value < totalPages.value) {
      currentPage.value++
    }
  }
  function prevPage() {
    if (currentPage.value > 1) {
      currentPage.value--
    }
  }
  function firstPage() {
    currentPage.value = 1
  }
  function lastPage() {
    currentPage.value = totalPages.value
  }

  const beforeButtons = computed(() => {
    const buttons: PaginationButton[] = [
      {
        icon: 'mdi:chevron-left',
        action: currentPage.value > 1 ? prevPage : null
      }
    ]
    if (currentPage.value > 1) {
      buttons.push({
        label: '1',
        action: () => setPage(1)
      })
    }
    if (currentPage.value > aroundButtons + 1) {
      buttons.push({
        label: '...',
        action: null
      })
    }
    for (let i = Math.max(2, currentPage.value - aroundButtons); i < currentPage.value; i++) {
      buttons.push({
        label: String(i),
        action: () => setPage(i)
      })
    }
    return buttons
  })

  const afterButtons = computed(() => {
    const buttons: PaginationButton[] = []
    for (let i = currentPage.value + 1; i <= Math.min(totalPages.value, currentPage.value + aroundButtons); i++) {
      buttons.push({
        label: String(i),
        action: () => setPage(i)
      })
    }
    if (currentPage.value < totalPages.value - aroundButtons) {
      buttons.push({
        label: '...',
        action: null
      })
    }
    if (totalPages.value > currentPage.value + aroundButtons) {
      buttons.push({
        label: String(totalPages.value),
        action: () => setPage(totalPages.value)
      })
    }
    if (currentPage.value < totalPages.value) {
      buttons.push({
        icon: 'mdi:chevron-right',
        action: nextPage
      })
    }
    return buttons
  })

  return {
    currentPage,
    totalPages,
    currentPageItems,
    setPage,
    nextPage,
    prevPage,
    firstPage,
    lastPage,
    beforeButtons,
    afterButtons
  }
}
