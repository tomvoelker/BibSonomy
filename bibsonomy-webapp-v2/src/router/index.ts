/**
 * Vue Router configuration
 */

import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

// Route definitions
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/pages/HomePage.vue'),
    meta: {
      title: 'Home',
      requiresAuth: false,
    },
  },
  {
    path: '/error/404',
    name: 'error-404',
    component: () => import('@/pages/Error404Page.vue'),
    meta: {
      title: '404 Not Found',
      requiresAuth: false,
    },
  },
  {
    path: '/error/500',
    name: 'error-500',
    component: () => import('@/pages/Error500Page.vue'),
    meta: {
      title: '500 Internal Server Error',
      requiresAuth: false,
    },
  },
  {
    path: '/error/not-implemented',
    name: 'not-implemented',
    component: () => import('@/pages/NotImplementedPage.vue'),
    meta: {
      title: 'Not Implemented',
      requiresAuth: false,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/pages/Error404Page.vue'),
    meta: {
      title: '404 Not Found',
      requiresAuth: false,
    },
  },
]

// Create router instance
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  // This is a placeholder - implement actual auth check
  const isAuthenticated = false // TODO: Check auth store

  if (to.meta.requiresAuth && !isAuthenticated) {
    // Redirect to login if not authenticated
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

// Update document title
router.afterEach((to) => {
  const title = to.meta.title as string | undefined
  document.title = title ? `${title} - BibSonomy` : 'BibSonomy'
})

export default router
