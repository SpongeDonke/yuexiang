<template>
  <div class="login-page">
    <!-- 视频背景 -->
    <div class="video-container">
      <video autoplay muted loop playsinline class="background-video" preload="auto">
        <source src="/video.mp4" type="video/mp4">
        您的浏览器不支持视频标签。
      </video>
      <div class="video-overlay"></div>
    </div>
    
    <!-- 粒子效果 -->
    <div class="particles-container" ref="particlesContainer"></div>
    
    <!-- 登录表单 -->
    <div class="login-container" ref="loginContainer">
      <div class="login-header">
        <h2 class="login-title">洗护平台登录</h2>
        <p class="login-subtitle">欢迎回来，请选择您的角色并登录</p>
      </div>
      
      <div class="role-selector">
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'shopkeeper' }"
          @click="selectedRole = 'shopkeeper'"
        >
          <div class="role-icon shopkeeper-icon">
            <i class="fas fa-store"></i>
          </div>
          <h3>店家</h3>
          <p>管理店铺和订单</p>
        </div>
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'admin' }"
          @click="selectedRole = 'admin'"
        >
          <div class="role-icon admin-icon">
            <i class="fas fa-user-shield"></i>
          </div>
          <h3>管理员</h3>
          <p>管理系统和用户</p>
        </div>
        <div 
          class="role-card" 
          :class="{ active: selectedRole === 'customer' }"
          @click="selectedRole = 'customer'"
        >
          <div class="role-icon customer-icon">
            <i class="fas fa-user"></i>
          </div>
          <h3>顾客</h3>
          <p>下单和查看服务</p>
        </div>
      </div>

      <div class="form-group">
        <div class="input-container">
          <i class="fas fa-user input-icon"></i>
          <input 
            type="text" 
            id="username" 
            v-model="form.username" 
            placeholder="请输入用户名"
            class="form-input"
          />
        </div>
      </div>

      <div class="form-group">
        <div class="input-container">
          <i class="fas fa-lock input-icon"></i>
          <input 
            type="password" 
            id="password" 
            v-model="form.password" 
            placeholder="请输入密码"
            class="form-input"
          />
        </div>
      </div>

      <button class="btn btn-primary" @click="login">
        <span>登录</span>
        <i class="fas fa-arrow-right"></i>
      </button>

      <div v-if="errorMessage" class="error-message">
        <i class="fas fa-exclamation-circle error-icon"></i>
        {{ errorMessage }}
      </div>

      <div class="register-link">
        没有账号？<span class="register-link-text" @click="goToRegister">去注册</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const selectedRole = ref('shopkeeper')
const form = ref({
  username: '',
  password: ''
})
const errorMessage = ref('')
const particlesContainer = ref(null)
const loginContainer = ref(null)
let particles = []

// 粒子效果
const createParticles = () => {
  if (!particlesContainer.value) return
  
  const container = particlesContainer.value
  const rect = container.getBoundingClientRect()
  
  // 清空容器
  container.innerHTML = ''
  particles = []
  
  // 创建20个粒子
  for (let i = 0; i < 20; i++) {
    createParticle(container, rect)
  }
  
  // 动画循环
  animateParticles()
}

const createParticle = (container, rect) => {
  const particle = document.createElement('div')
  particle.className = 'particle'
  
  // 随机位置
  const x = Math.random() * rect.width
  const y = Math.random() * rect.height
  
  // 随机大小
  const size = Math.random() * 5 + 1
  
  // 随机颜色
  const colors = ['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1']
  const color = colors[Math.floor(Math.random() * colors.length)]
  
  // 随机速度
  const speedX = (Math.random() - 0.5) * 0.5
  const speedY = (Math.random() - 0.5) * 0.5
  
  // 设置样式
  particle.style.position = 'absolute'
  particle.style.left = x + 'px'
  particle.style.top = y + 'px'
  particle.style.width = size + 'px'
  particle.style.height = size + 'px'
  particle.style.backgroundColor = color
  particle.style.borderRadius = '50%'
  particle.style.opacity = Math.random() * 0.5 + 0.2
  particle.style.pointerEvents = 'none'
  
  container.appendChild(particle)
  
  // 存储粒子信息
  particles.push({
    element: particle,
    x,
    y,
    size,
    speedX,
    speedY,
    color
  })
}

const animateParticles = () => {
  if (!particlesContainer.value) return
  
  const rect = particlesContainer.value.getBoundingClientRect()
  
  particles.forEach(particle => {
    // 更新位置
    particle.x += particle.speedX
    particle.y += particle.speedY
    
    // 边界检测
    if (particle.x < 0 || particle.x > rect.width) {
      particle.speedX *= -1
    }
    if (particle.y < 0 || particle.y > rect.height) {
      particle.speedY *= -1
    }
    
    // 更新DOM位置
    particle.element.style.left = particle.x + 'px'
    particle.element.style.top = particle.y + 'px'
  })
  
  requestAnimationFrame(animateParticles)
}

// 登录动画
const login = async () => {
  // 添加登录按钮动画
  const loginBtn = document.querySelector('.btn-primary')
  if (loginBtn) {
    loginBtn.classList.add('loading')
  }
  
  try {
    // 将角色映射为对应的roleId
    const roleIdMap = {
      'shopkeeper': 1,
      'admin': 2,
      'customer': 3
    }
    const selectedRoleId = roleIdMap[selectedRole.value]
    
    const response = await axios.post('/api/login', {
      username: form.value.username,
      password: form.value.password,
      roleId: selectedRoleId
    })
    
    if (response.data.success) {
      // 登录成功动画
      if (loginContainer.value) {
        loginContainer.value.classList.add('success')
      }
      
      const user = response.data.user
      localStorage.setItem('user', JSON.stringify(user))
      
      // 延迟跳转，显示动画效果
      setTimeout(() => {
        // 根据用户角色跳转到对应的首页
        switch (user.roleId) {
          case 1:
            router.push('/shopkeeper/personal')
            break
          case 2:
            router.push('/admin/personal')
            break
          case 3:
            router.push('/customer/home')
            break
          default:
            router.push('/login')
        }
      }, 1000)
    } else {
      // 登录失败动画
      if (loginContainer.value) {
        loginContainer.value.classList.add('shake')
        setTimeout(() => {
          loginContainer.value.classList.remove('shake')
        }, 500)
      }
      errorMessage.value = response.data.message
    }
  } catch (error) {
    // 登录失败动画
    if (loginContainer.value) {
      loginContainer.value.classList.add('shake')
      setTimeout(() => {
        loginContainer.value.classList.remove('shake')
      }, 500)
    }
    errorMessage.value = '登录失败，请稍后重试'
    console.error('Login error:', error)
  } finally {
    // 移除登录按钮动画
    if (loginBtn) {
      loginBtn.classList.remove('loading')
    }
  }
}

const goToRegister = () => {
  router.push('/register')
}

onMounted(() => {
  createParticles()
  
  // 监听窗口大小变化，重新创建粒子
  window.addEventListener('resize', createParticles)
})

onUnmounted(() => {
  window.removeEventListener('resize', createParticles)
})
</script>

<style scoped>
/* 引入Font Awesome */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 视频背景 */
.video-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -1;
  overflow: hidden;
}

.background-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  min-width: 100%;
  min-height: 100%;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
}

/* 粒子容器 */
.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  animation: particleFloat 6s ease-in-out infinite;
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 登录表单 */
.login-container {
  max-width: 500px;
  width: 90%;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(15px);
  z-index: 1;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* 登录容器动画 */
.login-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transform: rotate(45deg);
  animation: shine 6s linear infinite;
  opacity: 0;
  pointer-events: none;
}

@keyframes shine {
  0% { transform: translateX(-100%) rotate(45deg); }
  100% { transform: translateX(100%) rotate(45deg); }
}

.login-container:hover::before {
  opacity: 1;
}

.login-container:hover {
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.3);
  transform: translateY(-5px) scale(1.02);
}

/* 登录成功动画 */
.login-container.success {
  animation: successPulse 1s ease-in-out;
}

@keyframes successPulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); box-shadow: 0 25px 50px rgba(82, 196, 26, 0.4); }
  100% { transform: scale(1); }
}

/* 登录失败动画 */
.login-container.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-10px); }
  40%, 80% { transform: translateX(10px); }
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  margin: 0;
  color: #666;
  font-size: 16px;
  font-weight: 400;
}

/* 角色选择器 */
.role-selector {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
  gap: 15px;
}

.role-card {
  flex: 1;
  padding: 25px 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 2px solid #e0e0e0;
  border-radius: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.role-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(24, 144, 255, 0.1), transparent);
  transition: left 0.5s ease;
}

.role-card:hover::before {
  left: 100%;
}

.role-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  border-color: #1890ff;
}

.role-card.active {
  border-color: #1890ff;
  background-color: rgba(24, 144, 255, 0.1);
  box-shadow: 0 10px 25px rgba(24, 144, 255, 0.3);
}

.role-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 15px;
  font-size: 24px;
  color: white;
  transition: all 0.3s ease;
}

.shopkeeper-icon {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
}

.admin-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.customer-icon {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
}

.role-card:hover .role-icon {
  transform: scale(1.1) rotate(5deg);
}

.role-card h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.role-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 表单组 */
.form-group {
  margin-bottom: 25px;
}

.input-container {
  position: relative;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
  overflow: hidden;
}

.input-container:focus-within {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
}

.input-icon {
  position: absolute;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 18px;
  transition: all 0.3s ease;
}

.input-container:focus-within .input-icon {
  color: #1890ff;
}

.form-input {
  width: 100%;
  padding: 18px 20px 18px 55px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  background-color: transparent;
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
}

.form-input::placeholder {
  color: #999;
  font-size: 14px;
}

/* 登录按钮 */
.btn {
  display: inline-block;
  padding: 16px 32px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  width: 100%;
  margin-top: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  padding: 18px;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-primary:hover::before {
  left: 100%;
}

/* 登录按钮加载动画 */
.btn-primary.loading {
  pointer-events: none;
}

.btn-primary.loading::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
  right: 20px;
}

.btn-primary.loading span {
  opacity: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误消息 */
.error-message {
  margin-top: 20px;
  padding: 15px;
  background-color: rgba(255, 241, 240, 0.9);
  border: 2px solid #ffccc7;
  border-radius: 10px;
  color: #f5222d;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  animation: slideIn 0.3s ease;
}

.error-icon {
  font-size: 18px;
}

@keyframes slideIn {
  from { transform: translateY(-10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

/* 注册链接 */
.register-link {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-link-text {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  position: relative;
}

.register-link-text::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #1890ff;
  transition: width 0.3s ease;
}

.register-link-text:hover {
  color: #40a9ff;
}

.register-link-text:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    padding: 30px 20px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .role-selector {
    flex-direction: column;
  }
  
  .role-card {
    padding: 20px 15px;
  }
  
  .form-input {
    padding: 15px 18px 15px 50px;
  }
  
  .btn-primary {
    padding: 15px;
  }
}
</style>
