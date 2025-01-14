import Vue from 'vue'
import Router from 'vue-router'
import NProgress from 'nprogress'
import store from '../store/index'
// 登录
import logins from './login/index'
// 定标准备
import scalingPrepare from './scaling-prepare/index'
// 定标结束
import scalingEnd from './scaling-end/index'
// 竞价室
import biddingRoom from './bidding-room/index'
// 抽签
import drawReady from './draw/index'
// 集体议事
import chatRoom from './chat-room/index'
// 默认页面
import defaultPage from './default/index'
Vue.use(Router)
const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/admin',
      name: 'left-menu',
      component: resolve => require(['@/views/admin/menu/left-menu.vue'], resolve),
      redirect: '/admin/default',
      children: [
        ...defaultPage,
        {
          path: '/admin/proinfo',
          name: 'proinfo',
          meta: {
            title: '项目信息'
          },
          component: resolve => require(['@/views/admin/proinfo/index.vue'], resolve)
        },
        {
          path: '/admin/proinfo/update/:type',
          name: 'update',
          meta: {
            title: '项目编辑'
          },
          component: resolve => require(['@/views/admin/proinfo/edit.vue'], resolve)
        },
        {
          path: '/admin/proinfo/detail',
          name: 'detail',
          meta: {
            title: '项目详情'
          },
          component: resolve => require(['@/views/admin/proinfo/detail.vue'], resolve)
        },
        ...scalingPrepare,
        ...scalingEnd,
        ...drawReady,
        ...biddingRoom,
        ...chatRoom
      ]
    },
    ...logins
  ]
})
// 标题改变
router.afterEach((to, from) => {
  document.title = to.meta.title || ''
  NProgress.done()
})
router.beforeEach((to, from, next) => {
  NProgress.start()
  // token权限拦截
  if (to.path === '/') {
    next('/login')
  } else if (!to.meta.noRequireAuth) {
    if (store.getters.token) {
      if (!store.getters.authUser) {
        store.dispatch('GetLoginInfo').then(() => {
          next()
        })
      } else {
        next()
      }
    } else {
      next('/login')
    }
  } else {
    next()
  }
})
export default router
