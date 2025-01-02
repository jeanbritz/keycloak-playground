
// Composables
import { createRouter, createWebHistory } from 'vue-router/auto'
import Login from "@/components/Login.vue";
import HelloWorld from "@/components/Movies.vue";
import UserInfo from "@/components/UserInfo.vue";
import Tokens from "@/components/Tokens.vue";
import Logout from "@/components/Logout.vue";

function configRoutes() {
  return [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld,
    },
    {
      path: '/userinfo',
      name: 'UserInfo',
      component: UserInfo,
    },
    {
      path: '/tokens',
      name: 'Tokens',
      component: Tokens,
    },
    {
      path: '/logout',
      name: "Logout",
      component: Logout,
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
  ]
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: configRoutes(),
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (!localStorage.getItem('vuetify:dynamic-reload')) {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    } else {
      console.error('Dynamic import error, reloading page did not fix it', err)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router
