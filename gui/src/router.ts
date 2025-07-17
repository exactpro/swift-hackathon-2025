import {
  createMemoryHistory,
  createRouter,
  type RouteRecordRaw
} from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('./pages/HomePage.vue')
  },

  // Client routes with nested children
  {
    path: '/clients',
    children: [
      {
        path: '',
        component: () => import('./pages/clients/AllClientsPage.vue')
      },
      {
        path: ':client_id',
        component: () => import('./pages/clients/SingleClientPage.vue')
      }
    ]
  },

  {
    path: '/exchange',
    component: () => import('./pages/clients/ExchangePage.vue')
  },

  // Transaction routes with nested children
  {
    path: '/transactions',
    children: [
      {
        path: '',
        component: () =>
          import('./pages/transactions/TransactionsHistoryPage.vue')
      },
      {
        path: 'status/:transaction_id',
        component: () =>
          import('./pages/transactions/TransactionStatusPage.vue')
      },
      {
        path: 'new',
        component: () =>
          import('./pages/transactions/init/InitTransferPage.vue')
      },
      {
        path: 'cancel',
        component: () =>
          import('./pages/transactions/init/CancelTransferPage.vue')
      }
    ]
  }
]

export const router = createRouter({
  history: createMemoryHistory(),
  routes
})
