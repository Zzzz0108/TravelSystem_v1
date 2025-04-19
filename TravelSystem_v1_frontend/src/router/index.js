import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ForgotPassword from '@/views/ForgotPassword.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/diary',
    name: 'DiaryView',
    component: () => import('@/views/DiaryView.vue')
  },
  {
    path: '/diary/create',
    name: 'DiaryEditor',
    component: () => import('@/components/diary/DiaryEditor.vue')
  },
  {
    path: '/diary/:id',
    name: 'DiaryDetail',
    component: () => import('@/components/diary/DiaryDetail.vue'),
    props: true
  },
  {
    path: '/navigation',
    name: 'Navigation',
    component: () => import('@/views/NavigationView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword
  },
  {
    path: '/visited',
    name: 'Visited',
    component: () => import('@/views/VisitedSpots.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/navigation/:spotId',
    name: 'SpotNavigation',
    component: () => import('@/components/Navigation.vue'),
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token')
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router