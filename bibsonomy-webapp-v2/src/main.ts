/**
 * Application entry point
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import i18n from './plugins/i18n'
import { installVueQuery } from './plugins/query'
import { startMockWorker } from './mocks/browser'

// Import global styles
import './assets/main.css'

// Start MSW if enabled
if (import.meta.env.VITE_ENABLE_MOCKS === 'true') {
  void startMockWorker().then(() => {
    initApp()
  })
} else {
  initApp()
}

function initApp() {
  const app = createApp(App as Parameters<typeof createApp>[0])

  // Create Pinia instance
  const pinia = createPinia()

  // Install plugins
  app.use(pinia)
  app.use(router)
  app.use(i18n)
  installVueQuery(app)

  // Mount app
  app.mount('#app')
}
