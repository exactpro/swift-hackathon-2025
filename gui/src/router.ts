import {
  createMemoryHistory,
  createRouter,
  type RouteRecordRaw
} from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: '/', component: () => import('./pages/Home.vue') },
  { path: '/about', component: () => import('./pages/Home.vue') }
]

export const router = createRouter({
  history: createMemoryHistory(),
  routes
})
