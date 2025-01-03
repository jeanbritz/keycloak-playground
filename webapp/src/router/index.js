
// Composables
import { createRouter, createWebHistory } from 'vue-router/auto'
import Login from "@/pages/Login.vue";
import UserInfo from "@/pages/UserInfo.vue";
import Tokens from "@/pages/Tokens.vue";
import Logout from "@/pages/Logout.vue";
import SwaggerUI from "@/pages/SwaggerUI.vue";
import Movies from "@/pages/Movies.vue";

function configRoutes() {
  return [
    {
      path: '/',
      name: 'Movies',
      component: Movies,
      meta: {
        title: 'My Movie Watchlist'
      }
    },
    {
      path: '/userinfo',
      name: 'UserInfo',
      component: UserInfo,
      meta: {
        title: 'User Info'
      }
    },
    {
      path: '/tokens',
      name: 'Tokens',
      component: Tokens,
      meta: {
        title: 'Tokens'
      }
    },
    {
      path: '/logout',
      name: "Logout",
      component: Logout,
      meta: {
        title: 'Log out'
      }
    },
    {
      path: '/swagger-ui',
      name: "SwaggerUI",
      component: SwaggerUI,
      meta: {
        title: 'Swagger UI'
      }
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: {
        title: 'Login'
      }
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
