import Vue from 'vue'
import VueRouter from 'vue-router'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Index',
    meta:{
      title:'登录',
    },
    component: () => import('../views/Index.vue') 
  },
  {
    path: '/chat/index',
    name: 'Chat-Index',
		meta:{
      title:'聊天',
    },
    component: () => import('../views/chat/Index.vue') 
  },
]

const router = new VueRouter({
  mode: 'history', // 访问路径不带井号  需要使用 history模式
  routes:routes
})

export default router
