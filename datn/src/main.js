import '@/assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router'
import VueVirtualScroller from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
import { useAuthStore } from './stores/useAuthStore'

// Dashboard JS

import Scrollbar from 'smooth-scrollbar'

window.scrollbars = Scrollbar

import Chart from 'chart.js/auto'

window.Chart = Chart

const app = createApp(App)

app.use(VueVirtualScroller)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

const authStore = useAuthStore()

await authStore.me()

app.use(router)

app.mount('#app')
