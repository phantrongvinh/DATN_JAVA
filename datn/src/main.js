import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import VueVirtualScroller from 'vue-virtual-scroller'
import 'vue-virtual-scroller/dist/vue-virtual-scroller.css'
import { useAuthStore } from './stores/useAuthStore'

const app = createApp(App)

app.use(VueVirtualScroller)

app.use(createPinia())

const authStore = useAuthStore()

await authStore.me()

app.use(router)

app.mount('#app')
