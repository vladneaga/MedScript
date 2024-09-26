import './assets/main.css'
import router from './router'
import { createApp } from 'vue'
import App from './App.vue'
import './axios'
import store from './vuex'

const app = createApp(App)

app.use(router) // Use the router instance in your Vue app
app.use(store)
app.mount('#app');
