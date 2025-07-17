import {
  createMemoryHistory,
  createRouter,
  type RouteRecordRaw
} from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', component: () => import('./pages/HomePage.vue') },
  { path: '/about', component: () => import('./pages/HomePage.vue') }
]

export const router = createRouter({
  history: createMemoryHistory(),
  routes
})
