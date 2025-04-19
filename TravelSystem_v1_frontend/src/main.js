import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import router from './router'
import infiniteScroll from 'vue-infinite-scroll'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(infiniteScroll)
app.mount('#app')