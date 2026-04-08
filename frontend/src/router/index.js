import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  // 店家路由
  {
    path: '/shopkeeper',
    name: 'Shopkeeper',
    component: () => import('../views/shopkeeper/Layout.vue'),
    children: [
      {
        path: '',
        name: 'ShopkeeperHome',
        component: () => import('../views/shopkeeper/Home.vue')
      },
      {
        path: 'personal',
        name: 'ShopkeeperPersonal',
        component: () => import('../views/shopkeeper/Personal.vue')
      },
      {
        path: 'shop',
        name: 'ShopkeeperShop',
        component: () => import('../views/shopkeeper/Shop.vue')
      },
      {
        path: 'wash-type',
        name: 'ShopkeeperWashType',
        component: () => import('../views/shopkeeper/WashType.vue')
      },
      {
        path: 'wash-service',
        name: 'ShopkeeperWashService',
        component: () => import('../views/shopkeeper/WashService.vue')
      },
      {
        path: 'order',
        name: 'ShopkeeperOrder',
        component: () => import('../views/shopkeeper/Order.vue')
      },
      {
        path: 'order-progress',
        name: 'ShopkeeperOrderProgress',
        component: () => import('../views/shopkeeper/OrderProgress.vue')
      },
      {
        path: 'comment',
        name: 'ShopkeeperComment',
        component: () => import('../views/shopkeeper/Comment.vue')
      }
    ]
  },
  // 管理员路由
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/Layout.vue'),
    children: [
      {
        path: '',
        name: 'AdminHome',
        component: () => import('../views/admin/Home.vue')
      },
      {
        path: 'personal',
        name: 'AdminPersonal',
        component: () => import('../views/admin/Personal.vue')
      },
      {
        path: 'customer',
        name: 'AdminCustomer',
        component: () => import('../views/admin/Customer.vue')
      },
      {
        path: 'shopkeeper',
        name: 'AdminShopkeeper',
        component: () => import('../views/admin/Shopkeeper.vue')
      },
      {
        path: 'shop',
        name: 'AdminShop',
        component: () => import('../views/admin/Shop.vue')
      },
      {
        path: 'wash-service',
        name: 'AdminWashService',
        component: () => import('../views/admin/WashService.vue')
      },
      {
        path: 'wash-type',
        name: 'AdminWashType',
        component: () => import('../views/admin/WashType.vue')
      },
      {
        path: 'order-progress',
        name: 'AdminOrderProgress',
        component: () => import('../views/admin/OrderProgress.vue')
      },
      {
        path: 'system',
        name: 'AdminSystem',
        component: () => import('../views/admin/System.vue')
      },
      {
        path: 'comment',
        name: 'AdminComment',
        component: () => import('../views/admin/Comment.vue')
      },
      {
        path: 'customer-homepage',
        name: 'AdminCustomerHomepage',
        component: () => import('../views/admin/CustomerHomepage.vue')
      }
    ]
  },
  // 顾客路由
  {
    path: '/customer',
    name: 'Customer',
    component: () => import('../views/customer/Layout.vue'),
    children: [
      {
        path: 'home',
        name: 'CustomerHome',
        component: () => import('../views/customer/Home.vue')
      },
      {
        path: 'personal',
        name: 'CustomerPersonal',
        component: () => import('../views/customer/Personal.vue')
      },
      {
        path: 'wash-service',
        name: 'CustomerWashService',
        component: () => import('../views/customer/WashService.vue')
      },
      {
        path: 'order',
        name: 'CustomerOrder',
        component: () => import('../views/customer/Order.vue')
      },
      {
        path: 'order-progress',
        name: 'CustomerOrderProgress',
        component: () => import('../views/customer/OrderProgress.vue')
      },
      {
        path: 'comment',
        name: 'CustomerComment',
        component: () => import('../views/customer/Comment.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if ((to.path !== '/login' && to.path !== '/register') && !user) {
    next('/login')
  } else {
    next()
  }
})

export default router
