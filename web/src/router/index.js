import { createRouter, createWebHistory } from 'vue-router'
import UserBotIndexView from '@/views/user/bot/UserBotIndexView'
import RanklistIndexView from '@/views/ranklist/RanklistIndexView'
import RecordIndexView from '@/views/record/RecordIndexView'
import PkIndexView from '@/views/pk/PkIndexView'
import NotFoundView from '@/views/error/NotFound'
import HomeView from '@/views/HomeView'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView'


const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/ranklist/',
    name: 'ranklist_index',
    component: RanklistIndexView
  },
  {
    path: '/record/',
    name: 'record_index',
    component: RecordIndexView
  },
  {
    path: '/pk/',
    name: 'pk_index',
    component: PkIndexView
  },
  {
    path: '/user/bot',
    name: 'user_bot_index',
    component: UserBotIndexView
  },
  {
    path: '/user/account/login',
    name: 'user_account_login',
    component: UserAccountLoginView
  }, {
    path: '/user/account/register',
    name: 'user_account_register',
    component: UserAccountRegisterView
  },
  {
    path: '/404/',
    name: '404',
    component: NotFoundView
    
  },
  {
    path: '/:catchAll(.*)',
    redirect: "/404/"
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router