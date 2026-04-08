<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar-ring">
            <i class="fas fa-store avatar-icon"></i>
          </div>
          <div class="user-badge" :class="'role-' + user.roleId">
            <i class="fas fa-crown"></i>
            <span>{{ getRoleName(user.roleId) }}</span>
          </div>
        </div>
        <h1 class="profile-title">商家中心</h1>
        <p class="welcome-text">管理您的店铺信息</p>
      </div>

      <form @submit.prevent="updateProfile" class="profile-form">
        <div class="form-row">
          <div class="input-group">
            <label><i class="fas fa-user-circle"></i> 用户名</label>
            <div class="input-wrapper disabled">
              <i class="fas fa-lock"></i>
              <input type="text" v-model="user.username" disabled />
              <span class="disabled-tag">不可修改</span>
            </div>
          </div>

          <div class="input-group">
            <label><i class="fas fa-id-card"></i> 姓名</label>
            <div class="input-wrapper">
              <i class="fas fa-pen"></i>
              <input type="text" v-model="user.name" placeholder="请输入您的姓名" />
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="input-group full-width">
            <label><i class="fas fa-mobile-alt"></i> 手机号</label>
            <div class="input-wrapper">
              <i class="fas fa-phone"></i>
              <input type="text" v-model="user.phone" placeholder="请输入手机号码" />
              <span class="phone-prefix">+86</span>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="input-group full-width">
            <label><i class="fas fa-user-shield"></i> 角色</label>
            <div class="input-wrapper disabled role-display">
              <i class="fas fa-store"></i>
              <input type="text" :value="getRoleName(user.roleId)" disabled />
              <span class="role-badge">{{ getRoleName(user.roleId) }}</span>
            </div>
          </div>
        </div>

        <div class="action-area">
          <button type="submit" class="btn-save">
            <i class="fas fa-check"></i>
            更新信息
          </button>
        </div>
      </form>

      <transition name="message-fade">
        <div v-if="message" class="success-message">
          <i class="fas fa-check-circle"></i>
          {{ message }}
        </div>
      </transition>
    </div>

    <div class="info-cards">
      <div class="info-card shop-info">
        <i class="fas fa-chart-line"></i>
        <div class="card-content">
          <h4>经营数据</h4>
          <p>定期查看订单数据，优化服务流程提升效率。</p>
        </div>
      </div>
      <div class="info-card support">
        <i class="fas fa-headset"></i>
        <div class="card-content">
          <h4>技术支持</h4>
          <p>如遇问题请联系平台客服获取帮助。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const user = ref({})
const message = ref('')

onMounted(() => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    user.value = JSON.parse(storedUser)
  }
})

const getRoleName = (roleId) => {
  switch (roleId) {
    case 1: return '店家'
    case 2: return '管理员'
    case 3: return '顾客'
    default: return '未知角色'
  }
}

const updateProfile = async () => {
  try {
    await axios.post('/api/users', user.value)
    localStorage.setItem('user', JSON.stringify(user.value))
    message.value = '信息更新成功'
    setTimeout(() => { message.value = '' }, 3000)
  } catch (error) {
    console.error('Update profile error:', error)
  }
}
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 15px 50px rgba(82, 196, 26, 0.25);
  animation: slideUp 0.6s ease;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.profile-header {
  text-align: center;
  margin-bottom: 36px;
}

.avatar-section {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.avatar-ring {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255,255,255,0.2), rgba(255,255,255,0.05));
  border: 4px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.avatar-icon {
  font-size: 48px;
  color: white;
}

.user-badge {
  position: absolute;
  bottom: -8px;
  right: -10px;
  background: white;
  color: #333;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}
.user-badge i { font-size: 13px; }

.role-1 { border-left: 4px solid #fa8c16; }
.role-2 { border-left: 4px solid #ff4d4f; }
.role-3 { border-left: 4px solid #1890ff; }

.profile-title {
  color: white;
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.welcome-text {
  color: rgba(255, 255, 255, 0.85);
  font-size: 15px;
  margin: 0;
}

.profile-form {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.input-group.full-width { grid-column: span 2; }

.input-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 10px;
}
.input-group label i { color: #52c41a; font-size: 16px; }

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #fafafa;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: hidden;
}

.input-wrapper:focus-within {
  border-color: #52c41a;
  box-shadow: 0 0 0 4px rgba(82, 196, 26, 0.08);
  background: white;
}

.input-wrapper.disabled {
  background: #f5f5f5;
  opacity: 0.75;
}

.input-wrapper i:first-child {
  padding: 0 14px;
  color: #999;
  font-size: 16px;
}

.input-wrapper input {
  flex: 1;
  padding: 14px 16px;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #333;
  outline: none;
}
.input-wrapper input::placeholder { color: #bbb; }

.disabled-tag,
.phone-prefix,
.role-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  margin-right: 12px;
}

.disabled-tag { background: #e8e8e8; color: #999; }
.phone-prefix { background: #f6ffed; color: #52c41a; font-weight: 600; }
.role-display .role-badge {
  background: linear-gradient(135deg, #52c41a, #95de64);
  color: white;
}

.action-area { text-align: center; margin-top: 28px; }

.btn-save {
  padding: 14px 48px;
  background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(250, 140, 22, 0.35);
}
.btn-save:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 22px rgba(250, 140, 22, 0.45);
}
.btn-save:active { transform: translateY(0); }

.success-message {
  margin-top: 20px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  border-radius: 12px;
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.3);
}

.message-fade-enter-active,
.message-fade-leave-active { transition: all 0.4s ease; }
.message-fade-enter-from,
.message-fade-leave-to { opacity: 0; transform: translateY(-10px); }

.info-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 28px;
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  gap: 18px;
  align-items: start;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}
.info-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.info-card > i {
  font-size: 32px;
  padding: 12px;
  border-radius: 12px;
}

.shop-info > i { color: #52c41a; background: #f6ffed; }
.support > i { color: #1890ff; background: #e6f7ff; }

.card-content h4 {
  margin: 0 0 6px 0;
  color: #333;
  font-size: 15px;
  font-weight: 600;
}
.card-content p {
  margin: 0;
  color: #888;
  font-size: 13px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .form-row { grid-template-columns: 1fr; }
  .input-group.full-width { grid-column: span 1; }
  .info-cards { grid-template-columns: 1fr; }
  .profile-card { padding: 24px; }
  .profile-form { padding: 20px; }
}
</style>
