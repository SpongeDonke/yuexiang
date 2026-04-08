<template>
  <div class="main-layout">
    <!-- 侧边导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <i class="icon">🏪</i>
          <span>洗护平台</span>
        </div>
      </div>
      <ul class="sidebar-menu">
        <li class="sidebar-menu-item">
          <router-link to="/shopkeeper" class="sidebar-menu-link" :class="{ active: $route.path === '/shopkeeper' }">
            <i class="fas fa-home"></i>
            <span>首页</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item has-submenu" :class="{ open: isSubmenuOpen }">
          <div class="sidebar-menu-link submenu-toggle" @click="toggleSubmenu">
            <i class="fas fa-store"></i>
            <span>店家平台管理</span>
            <i class="fas fa-chevron-down submenu-arrow"></i>
          </div>
          <ul class="submenu" v-show="isSubmenuOpen">
            <li class="submenu-item">
              <router-link to="/shopkeeper/shop" class="submenu-link" :class="{ active: $route.path === '/shopkeeper/shop' }">
                <i class="fas fa-store-alt"></i>
                <span>店铺管理</span>
              </router-link>
            </li>
            <li class="submenu-item">
              <router-link to="/shopkeeper/wash-type" class="submenu-link" :class="{ active: $route.path === '/shopkeeper/wash-type' }">
                <i class="fas fa-box"></i>
                <span>洗护类型</span>
              </router-link>
            </li>
            <li class="submenu-item">
              <router-link to="/shopkeeper/wash-service" class="submenu-link" :class="{ active: $route.path === '/shopkeeper/wash-service' }">
                <i class="fas fa-tshirt"></i>
                <span>洗护服务</span>
              </router-link>
            </li>
          </ul>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/shopkeeper/order" class="sidebar-menu-link" :class="{ active: $route.path === '/shopkeeper/order' }">
            <i class="fas fa-file-invoice"></i>
            <span>订单信息</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/shopkeeper/order-progress" class="sidebar-menu-link" :class="{ active: $route.path === '/shopkeeper/order-progress' }">
            <i class="fas fa-chart-line"></i>
            <span>订单进度</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/shopkeeper/comment" class="sidebar-menu-link" :class="{ active: $route.path === '/shopkeeper/comment' }">
            <i class="fas fa-star"></i>
            <span>评论管理</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/shopkeeper/personal" class="sidebar-menu-link" :class="{ active: $route.path === '/shopkeeper/personal' }">
            <i class="fas fa-user"></i>
            <span>个人中心</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <a href="#" @click="logout" class="sidebar-menu-link">
            <i class="fas fa-sign-out-alt"></i>
            <span>退出登录</span>
          </a>
        </li>
      </ul>
    </aside>
    
    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <header class="top-nav">
        <div class="top-nav-left">
          <h1 class="top-nav-title">商家中心</h1>
        </div>
        <div class="top-nav-right">
          <div class="user-info">
            <div class="user-avatar">
              {{ getUserInitial() }}
            </div>
            <span class="user-name">{{ getUserName() }}</span>
          </div>
        </div>
      </header>
      
      <!-- 内容区域 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()
const isSubmenuOpen = ref(false)

const toggleSubmenu = () => {
  isSubmenuOpen.value = !isSubmenuOpen.value
}

const logout = () => {
  localStorage.removeItem('user')
  router.push('/login')
}

const getUserInitial = () => {
  const user = localStorage.getItem('user')
  if (user) {
    const parsedUser = JSON.parse(user)
    return parsedUser.username ? parsedUser.username.charAt(0).toUpperCase() : 'U'
  }
  return 'U'
}

const getUserName = () => {
  const user = localStorage.getItem('user')
  if (user) {
    const parsedUser = JSON.parse(user)
    return parsedUser.username || '用户'
  }
  return '用户'
}
</script>
