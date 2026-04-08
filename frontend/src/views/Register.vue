<template>
  <div class="register-page">
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
    
    <!-- 注册表单 -->
    <div class="register-container" ref="registerContainer">
      <div class="register-header">
        <h2 class="register-title">洗护平台注册</h2>
        <p class="register-subtitle">欢迎加入，请选择您的角色并完成注册</p>
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
            @input="validateUsername"
          />
          <div v-if="errors.username" class="field-error">{{ errors.username }}</div>
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
            @input="validatePassword"
          />
          <div v-if="errors.password" class="field-error">{{ errors.password }}</div>
        </div>
      </div>

      <div class="form-group">
        <div class="input-container">
          <i class="fas fa-user-tag input-icon"></i>
          <input 
            type="text" 
            id="name" 
            v-model="form.name" 
            placeholder="请输入姓名"
            class="form-input"
            @input="validateName"
          />
          <div v-if="errors.name" class="field-error">{{ errors.name }}</div>
        </div>
      </div>

      <div class="form-group">
        <div class="input-container">
          <i class="fas fa-phone input-icon"></i>
          <input 
            type="text" 
            id="phone" 
            v-model="form.phone" 
            placeholder="请输入联系电话"
            class="form-input"
            @input="validatePhone"
          />
          <div v-if="errors.phone" class="field-error">{{ errors.phone }}</div>
        </div>
      </div>

      <!-- 商家资质信息 -->
      <div v-if="selectedRole === 'shopkeeper'" class="shopkeeper-info">
        <h3 class="section-title">商家资质信息</h3>
        <div class="form-group">
          <div class="input-container">
            <i class="fas fa-id-card input-icon"></i>
            <input 
              type="text" 
              id="businessLicense" 
              v-model="form.businessLicense" 
              placeholder="请输入营业执照号"
              class="form-input"
              @input="validateBusinessLicense"
            />
            <div v-if="errors.businessLicense" class="field-error">{{ errors.businessLicense }}</div>
          </div>
        </div>
        <div class="form-group">
          <div class="input-container">
            <i class="fas fa-store input-icon"></i>
            <input 
              type="text" 
              id="shopName" 
              v-model="form.shopName" 
              placeholder="请输入店铺名称"
              class="form-input"
              @input="validateShopName"
            />
            <div v-if="errors.shopName" class="field-error">{{ errors.shopName }}</div>
          </div>
        </div>
        <div class="form-group">
          <div class="input-container">
            <i class="fas fa-map-marker-alt input-icon"></i>
            <input 
              type="text" 
              id="shopAddress" 
              v-model="form.shopAddress" 
              placeholder="请输入店铺地址"
              class="form-input"
              @input="validateShopAddress"
            />
            <div v-if="errors.shopAddress" class="field-error">{{ errors.shopAddress }}</div>
          </div>
        </div>
      </div>

      <button class="btn btn-primary" @click="register">
        <span>注册</span>
        <i class="fas fa-arrow-right"></i>
      </button>

      <div v-if="errorMessage" class="error-message">
        <i class="fas fa-exclamation-circle error-icon"></i>
        {{ errorMessage }}
      </div>

      <div class="login-link">
        已有账号？<span class="login-link-text" @click="goToLogin">去登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const selectedRole = ref('customer')
const form = ref({
  username: '',
  password: '',
  name: '',
  phone: '',
  businessLicense: '',
  shopName: '',
  shopAddress: ''
})
const errorMessage = ref('')
const errors = ref({
  username: '',
  password: '',
  name: '',
  phone: '',
  businessLicense: '',
  shopName: '',
  shopAddress: ''
})
const usernameCheckTimer = ref(null)
const particlesContainer = ref(null)
const registerContainer = ref(null)
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

const checkUsernameExists = async (username) => {
  try {
    const response = await axios.get(`/api/check-username?username=${username}`)
    return response.data.exists
  } catch (error) {
    console.error('Check username error:', error)
    return false
  }
}

const validateUsername = async () => {
  const value = form.value.username
  
  if (!value || value.trim() === '') {
    errors.value.username = '用户名不能为空'
    return
  }
  
  const usernameRegex = /^[a-zA-Z0-9_]{6,20}$/
  if (!usernameRegex.test(value.trim())) {
    errors.value.username = '用户名应由字母、数字、下划线组成，长度在6-20之间'
    return
  }
  
  // 使用防抖检查用户名是否存在
  if (usernameCheckTimer.value) {
    clearTimeout(usernameCheckTimer.value)
  }
  
  usernameCheckTimer.value = setTimeout(async () => {
    const exists = await checkUsernameExists(value.trim())
    if (exists) {
      errors.value.username = '该用户名已存在'
    } else {
      errors.value.username = ''
    }
  }, 500)
}

const validatePassword = () => {
  const value = form.value.password
  
  if (!value || value.trim() === '') {
    errors.value.password = '密码不能为空'
    return
  }
  
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/
  if (!passwordRegex.test(value)) {
    errors.value.password = '密码至少包含8个字符，包括大小写字母和数字'
    return
  }
  
  errors.value.password = ''
}

const validateName = () => {
  const value = form.value.name
  
  if (!value || value.trim() === '') {
    errors.value.name = '姓名不能为空'
    return
  }
  
  const nameRegex = /^[\u4e00-\u9fa5]{2,10}$/
  if (!nameRegex.test(value.trim())) {
    errors.value.name = '姓名应为中文，长度在2-10之间'
    return
  }
  
  errors.value.name = ''
}

const validatePhone = () => {
  const value = form.value.phone
  
  if (!value || value.trim() === '') {
    errors.value.phone = '联系电话不能为空'
    return
  }
  
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(value.trim())) {
    errors.value.phone = '联系电话应为11位的中国手机号'
    return
  }
  
  errors.value.phone = ''
}

const validateBusinessLicense = () => {
  if (selectedRole.value !== 'shopkeeper') {
    errors.value.businessLicense = ''
    return
  }
  
  const value = form.value.businessLicense
  
  if (!value || value.trim() === '') {
    errors.value.businessLicense = '营业执照号不能为空'
    return
  }
  
  const licenseRegex = /^[A-Z0-9]{18}$/
  if (!licenseRegex.test(value.trim())) {
    errors.value.businessLicense = '营业执照号应为18位大写字母和数字'
    return
  }
  
  errors.value.businessLicense = ''
}

const validateShopName = () => {
  if (selectedRole.value !== 'shopkeeper') {
    errors.value.shopName = ''
    return
  }
  
  const value = form.value.shopName
  
  if (!value || value.trim() === '') {
    errors.value.shopName = '店铺名称不能为空'
    return
  }
  
  if (value.trim().length < 2 || value.trim().length > 50) {
    errors.value.shopName = '店铺名称长度应在2-50个字符之间'
    return
  }
  
  errors.value.shopName = ''
}

const validateShopAddress = () => {
  if (selectedRole.value !== 'shopkeeper') {
    errors.value.shopAddress = ''
    return
  }
  
  const value = form.value.shopAddress
  
  if (!value || value.trim() === '') {
    errors.value.shopAddress = '店铺地址不能为空'
    return
  }
  
  if (value.trim().length < 5 || value.trim().length > 200) {
    errors.value.shopAddress = '店铺地址长度应在5-200个字符之间'
    return
  }
  
  errors.value.shopAddress = ''
}

// 注册动画
const register = async () => {
  // 验证所有字段
  validateUsername()
  validatePassword()
  validateName()
  validatePhone()
  
  if (selectedRole.value === 'shopkeeper') {
    validateBusinessLicense()
    validateShopName()
    validateShopAddress()
  }
  
  // 检查是否有错误
  if (errors.value.username || errors.value.password || errors.value.name || errors.value.phone ||
      errors.value.businessLicense || errors.value.shopName || errors.value.shopAddress) {
    return
  }
  
  // 添加注册按钮动画
  const registerBtn = document.querySelector('.btn-primary')
  if (registerBtn) {
    registerBtn.classList.add('loading')
  }
  
  try {
    // 将角色映射为对应的roleId
    const roleIdMap = {
      'shopkeeper': 1,
      'customer': 3
    }
    const roleId = roleIdMap[selectedRole.value]
    
    const response = await axios.post('/api/register', {
      username: form.value.username,
      password: form.value.password,
      roleId: roleId,
      name: form.value.name,
      phone: form.value.phone
    })
    
    if (response.data.success) {
      // 注册成功动画
      if (registerContainer.value) {
        registerContainer.value.classList.add('success')
      }
      
      // 延迟跳转，显示动画效果
      setTimeout(() => {
        alert(response.data.message)
        router.push('/login')
      }, 1000)
    } else {
      // 注册失败动画
      if (registerContainer.value) {
        registerContainer.value.classList.add('shake')
        setTimeout(() => {
          registerContainer.value.classList.remove('shake')
        }, 500)
      }
      errorMessage.value = response.data.message
    }
  } catch (error) {
    // 注册失败动画
    if (registerContainer.value) {
      registerContainer.value.classList.add('shake')
      setTimeout(() => {
        registerContainer.value.classList.remove('shake')
      }, 500)
    }
    errorMessage.value = '注册失败，请稍后重试'
    console.error('Register error:', error)
  } finally {
    // 移除注册按钮动画
    if (registerBtn) {
      registerBtn.classList.remove('loading')
    }
  }
}

const goToLogin = () => {
  router.push('/login')
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

.register-page {
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

/* 注册表单 */
.register-container {
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
  overflow-y: auto;
  max-height: 90vh;
}

/* 注册容器动画 */
.register-container::before {
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

.register-container:hover::before {
  opacity: 1;
}

.register-container:hover {
  box-shadow: 0 20px 45px rgba(0, 0, 0, 0.3);
  transform: translateY(-5px) scale(1.02);
}

/* 注册成功动画 */
.register-container.success {
  animation: successPulse 1s ease-in-out;
}

@keyframes successPulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); box-shadow: 0 25px 50px rgba(82, 196, 26, 0.4); }
  100% { transform: scale(1); }
}

/* 注册失败动画 */
.register-container.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-10px); }
  40%, 80% { transform: translateX(10px); }
}

/* 注册头部 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
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

/* 商家资质信息 */
.shopkeeper-info {
  margin: 30px 0;
  padding: 25px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 2px solid #e0e0e0;
  border-radius: 15px;
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from { transform: translateY(-10px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.section-title {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

/* 注册按钮 */
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

/* 注册按钮加载动画 */
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

.field-error {
  margin-top: 5px;
  padding: 8px;
  background-color: rgba(255, 241, 240, 0.9);
  border: 1px solid #ffccc7;
  border-radius: 6px;
  color: #f5222d;
  font-size: 12px;
  text-align: left;
}

.input-container:has(+ .field-error) {
  border-color: #f5222d;
}

.input-container:focus-within {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
}

.error-icon {
  font-size: 18px;
}

/* 登录链接 */
.login-link {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link-text {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  position: relative;
}

.login-link-text::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #1890ff;
  transition: width 0.3s ease;
}

.login-link-text:hover {
  color: #40a9ff;
}

.login-link-text:hover::after {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 30px 20px;
  }
  
  .register-title {
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
  
  .shopkeeper-info {
    padding: 20px;
  }
}
</style>
