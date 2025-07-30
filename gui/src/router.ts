import { createWebHistory, createRouter, type RouteRecordRaw } from 'vue-router'
import { joinURL } from 'ufo'
import config from '../config'

const adminRoutes: RouteRecordRaw[] = [
  {
    path: '',
    redirect: (to) => {
      return {
        path: joinURL(to.path, 'dashboard')
      }
    },
    children: [
      {
        path: 'dashboard',
        component: () => import('./pages/admin/TransactionsHistoryPage.vue')
      },
      {
        path: 'transfers/:uetr',
        component: () => import('./pages/admin/TransactionStatusPage.vue')
      }
    ]
  }
]

const clientRoutes: RouteRecordRaw[] = [
  {
    path: '',
    component: () => import('./pages/clients/SingleClientPage.vue')
  },
  {
    path: 'transfer',
    component: () => import('./pages/clients/TransferFormPage.vue')
  }
]

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('./pages/RootNav.vue')
  },
  {
    path: '/banka',
    meta: {
      bankName: config.bankA.name,
      bic: config.bankA.bic
    },
    children: [
      {
        path: 'admin',
        component: () => import('./layouts/AdminLayout.vue'),
        children: adminRoutes
      },
      {
        path: '',
        meta: {
          clientName: config.bankA.client.name,
          clientId: config.bankA.client.id
        },
        component: () => import('./layouts/ClientLayout.vue'),
        children: clientRoutes
      }
    ]
  },
  {
    path: '/bankb',
    meta: {
      bankName: config.bankB.name,
      bic: config.bankB.bic
    },
    children: [
      {
        path: 'admin',
        component: () => import('./layouts/AdminLayout.vue'),
        children: adminRoutes
      },
      {
        path: '',
        meta: {
          clientName: config.bankB.client.name,
          clientId: config.bankB.client.id
        },
        component: () => import('./layouts/ClientLayout.vue'),
        children: clientRoutes
      }
    ]
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})
