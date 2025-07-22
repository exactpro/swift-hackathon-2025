<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import config from '../../config'

const router = useRouter()

interface RouteNode {
  path: string
  fullPath: string
  name: string
  meta?: any
  children?: RouteNode[]
}

// Build the route tree from the router configuration
const routeTree = computed(() => {
  const tree: RouteNode[] = []

  // Bank A routes
  tree.push({
    path: '/banka',
    fullPath: '/banka',
    name: `${config.bankA.name} (${config.bankA.bic})`,
    meta: { bankName: config.bankA.name, bic: config.bankA.bic },
    children: [
      {
        path: 'admin',
        fullPath: '/banka/admin',
        name: 'Admin Panel',
        children: [
          {
            path: 'dashboard',
            fullPath: '/banka/admin/dashboard',
            name: 'Dashboard (Transactions History)'
          }
        ]
      },
      {
        path: '',
        fullPath: '/banka',
        name: `Client Portal (${config.bankA.client.name})`,
        meta: { clientName: config.bankA.client.name, clientId: config.bankA.client.id },
        children: [
          {
            path: '',
            fullPath: '/banka',
            name: 'Client Dashboard'
          },
          {
            path: 'exchange',
            fullPath: '/banka/exchange',
            name: 'Currency Exchange'
          },
          {
            path: 'transfers/new',
            fullPath: '/banka/transfers/new',
            name: 'New Transfer'
          },
          {
            path: 'transfers/:uetr',
            fullPath: '/banka/transfers/[uetr]',
            name: 'Transaction Status (Dynamic)'
          }
        ]
      }
    ]
  })

  // Bank B routes
  tree.push({
    path: '/bankb',
    fullPath: '/bankb',
    name: `${config.bankB.name} (${config.bankB.bic})`,
    meta: { bankName: config.bankB.name, bic: config.bankB.bic },
    children: [
      {
        path: 'admin',
        fullPath: '/bankb/admin',
        name: 'Admin Panel',
        children: [
          {
            path: 'dashboard',
            fullPath: '/bankb/admin/dashboard',
            name: 'Dashboard (Transactions History)'
          }
        ]
      },
      {
        path: '',
        fullPath: '/bankb',
        name: `Client Portal (${config.bankB.client.name})`,
        meta: { clientName: config.bankB.client.name, clientId: config.bankB.client.id },
        children: [
          {
            path: '',
            fullPath: '/bankb',
            name: 'Client Dashboard'
          },
          {
            path: 'exchange',
            fullPath: '/bankb/exchange',
            name: 'Currency Exchange'
          },
          {
            path: 'transfers/new',
            fullPath: '/bankb/transfers/new',
            name: 'New Transfer'
          },
          {
            path: 'transfers/:uetr',
            fullPath: '/bankb/transfers/[uetr]',
            name: 'Transaction Status (Dynamic)'
          }
        ]
      }
    ]
  })

  return tree
})

function navigateToRoute(fullPath: string) {
  if (fullPath.includes('[') || fullPath.includes(':')) {
    // Skip dynamic routes
    return
  }
  router.push(fullPath)
}

function isNavigableRoute(fullPath: string): boolean {
  return !fullPath.includes('[') && !fullPath.includes(':')
}
</script>

<template>
  <div class="container mx-auto p-6 max-w-6xl">
    <div class="prose max-w-none mb-8">
      <h1 class="text-3xl font-bold mb-4">Application Routes</h1>
      <p class="text-lg text-gray-600">
        Navigate through all available routes in the SWIFT Hackathon 2025 application. This shows the complete route
        structure for both Bank A and Bank B.
      </p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Route Tree for each bank -->
      <div v-for="bank in routeTree" :key="bank.fullPath" class="card bg-base-200 shadow-xl">
        <div class="card-body">
          <h2 class="card-title text-xl mb-4 flex items-center gap-2">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M4 4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2H4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1z"
                clip-rule="evenodd"
              />
            </svg>
            {{ bank.name }}
          </h2>

          <!-- Bank root route -->
          <div class="mb-4">
            <button
              @click="navigateToRoute(bank.fullPath)"
              class="btn btn-outline btn-sm w-full justify-start"
              :class="{ 'btn-disabled': !isNavigableRoute(bank.fullPath) }"
            >
              <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path
                  d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"
                />
              </svg>
              {{ bank.fullPath }}
            </button>
          </div>

          <!-- Bank children routes -->
          <div v-for="section in bank.children" :key="section.fullPath" class="mb-6">
            <div class="collapse collapse-arrow bg-base-300">
              <input type="checkbox" class="peer" />
              <div class="collapse-title text-lg font-medium">
                <div class="flex items-center gap-2">
                  <svg v-if="section.name.includes('Admin')" class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fill-rule="evenodd"
                      d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-6-3a2 2 0 11-4 0 2 2 0 014 0zm-2 4a5 5 0 00-4.546 2.916A5.986 5.986 0 0010 16a5.986 5.986 0 004.546-2.084A5 5 0 0010 11z"
                      clip-rule="evenodd"
                    />
                  </svg>
                  <svg v-else class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fill-rule="evenodd"
                      d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                      clip-rule="evenodd"
                    />
                  </svg>
                  {{ section.name }}
                </div>
              </div>
              <div class="collapse-content">
                <div class="space-y-2 pt-2">
                  <div v-for="route in section.children" :key="route.fullPath">
                    <button
                      @click="navigateToRoute(route.fullPath)"
                      class="btn btn-ghost btn-sm w-full justify-start text-left"
                      :class="{
                        'btn-disabled': !isNavigableRoute(route.fullPath),
                        'text-orange-600': route.fullPath.includes('[') || route.fullPath.includes(':')
                      }"
                    >
                      <svg
                        v-if="route.name.includes('Dashboard')"
                        class="w-4 h-4 mr-2"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z"
                        />
                      </svg>
                      <svg
                        v-else-if="route.name.includes('Exchange')"
                        class="w-4 h-4 mr-2"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M4 2a1 1 0 011 1v2.101a7.002 7.002 0 0111.601 2.566 1 1 0 11-1.885.666A5.002 5.002 0 005.999 7H9a1 1 0 010 2H4a1 1 0 01-1-1V3a1 1 0 011-1zm.008 9.057a1 1 0 011.276.61A5.002 5.002 0 0014.001 13H11a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0v-2.101a7.002 7.002 0 01-11.601-2.566 1 1 0 01.61-1.276z"
                          clip-rule="evenodd"
                        />
                      </svg>
                      <svg
                        v-else-if="route.name.includes('Transfer')"
                        class="w-4 h-4 mr-2"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z"
                        />
                      </svg>
                      <svg
                        v-else-if="route.name.includes('Status')"
                        class="w-4 h-4 mr-2"
                        fill="currentColor"
                        viewBox="0 0 20 20"
                      >
                        <path
                          fill-rule="evenodd"
                          d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                          clip-rule="evenodd"
                        />
                      </svg>
                      <svg v-else class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                        <path
                          fill-rule="evenodd"
                          d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                          clip-rule="evenodd"
                        />
                      </svg>
                      <div class="flex flex-col items-start">
                        <span class="font-medium">{{ route.name }}</span>
                        <span class="text-xs opacity-60">{{ route.fullPath }}</span>
                      </div>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Route Legend -->
    <div class="card bg-base-300 shadow-lg mt-8">
      <div class="card-body">
        <h3 class="card-title text-lg mb-4">Legend</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 bg-primary rounded"></div>
            <span class="text-sm">Clickable routes - navigate by clicking</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-4 h-4 bg-orange-600 rounded"></div>
            <span class="text-sm">Dynamic routes - require parameters</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path
                fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-6-3a2 2 0 11-4 0 2 2 0 014 0zm-2 4a5 5 0 00-4.546 2.916A5.986 5.986 0 0010 16a5.986 5.986 0 004.546-2.084A5 5 0 0010 11z"
                clip-rule="evenodd"
              />
            </svg>
            <span class="text-sm">Admin sections</span>
          </div>
          <div class="flex items-center gap-2">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd" />
            </svg>
            <span class="text-sm">Client sections</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
