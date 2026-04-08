<template>
  <div class="main-layout">
    <!-- 侧边导航栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <i class="icon">🧼</i>
          <span>洗护平台</span>
        </div>
      </div>
      <ul class="sidebar-menu">
        <li class="sidebar-menu-item">
          <router-link to="/customer/home" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/home' }">
            <i class="icon">🏠</i>
            <span>首页</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/customer/personal" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/personal' }">
            <i class="icon">👤</i>
            <span>个人中心</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/customer/wash-service" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/wash-service' }">
            <i class="icon">🧺</i>
            <span>洗护服务</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/customer/order" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/order' }">
            <i class="icon">📋</i>
            <span>订单信息</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/customer/order-progress" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/order-progress' }">
            <i class="icon">📊</i>
            <span>订单进度</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <router-link to="/customer/comment" class="sidebar-menu-link" :class="{ active: $route.path === '/customer/comment' }">
            <i class="icon">⭐</i>
            <span>评论管理</span>
          </router-link>
        </li>
        <li class="sidebar-menu-item">
          <a href="#" @click="logout" class="sidebar-menu-link">
            <i class="icon">🚪</i>
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
          <h1 class="top-nav-title">顾客中心</h1>
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
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

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
