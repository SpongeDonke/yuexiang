<template>
  <div class="shop-management">
    <div class="header-section">
      <div class="header-icon">
        <i class="fas fa-store"></i>
      </div>
      <h2 class="page-title">我的店铺</h2>
      <p class="subtitle">管理您的店铺信息和基本资料</p>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="shop" class="content-area">
      <div class="shop-card">
        <div class="shop-header">
          <div class="shop-avatar">
            <i class="fas fa-building"></i>
          </div>
          <div class="shop-info">
            <h3>{{ shop.name || '未命名店铺' }}</h3>
            <p>店铺ID: #{{ shop.id }}</p>
          </div>
        </div>

        <div class="shop-details">
          <div class="detail-item">
            <i class="fas fa-map-marker-alt"></i>
            <span><strong>店铺地址：</strong></span>
            <span>{{ shop.address || '未填写' }}</span>
          </div>
          <div class="detail-item">
            <i class="fas fa-phone"></i>
            <span><strong>联系电话：</strong></span>
            <span>{{ shop.phone || '未填写' }}</span>
          </div>
          <div class="detail-item desc-detail">
            <i class="fas fa-info-circle"></i>
            <span><strong>店铺描述：</strong></span>
            <p>{{ shop.description || '暂无描述信息' }}</p>
          </div>
        </div>

        <div class="action-bar">
          <button class="btn-primary" @click="editShop">
            <i class="fas fa-edit"></i> 编辑店铺信息
          </button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <i class="fas fa-store-slash"></i>
      <p>暂无店铺信息</p>
    </div>

    <!-- 编辑店铺对话框 -->
    <div v-if="showEditDialog" class="modal-overlay" @click.self="showEditDialog = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3><i class="fas fa-edit"></i> 编辑店铺</h3>
          <button class="close-btn" @click="showEditDialog = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label><i class="fas fa-building"></i> 店铺名称</label>
            <input type="text" v-model="editForm.name" placeholder="请输入店铺名称" />
          </div>
          <div class="form-group">
            <label><i class="fas fa-map-marker-alt"></i> 地址</label>
            <input type="text" v-model="editForm.address" placeholder="请输入店铺地址" />
          </div>
          <div class="form-group">
            <label><i class="fas fa-phone"></i> 电话</label>
            <input type="text" v-model="editForm.phone" placeholder="请输入联系电话" />
          </div>
          <div class="form-group">
            <label><i class="fas fa-info-circle"></i> 描述</label>
            <textarea v-model="editForm.description" rows="4" placeholder="请输入店铺描述"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showEditDialog = false">取消</button>
          <button class="btn-save" @click="saveShop"><i class="fas fa-save"></i> 保存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const shop = ref(null)
const loading = ref(true)
const showEditDialog = ref(false)
const editForm = ref({})

onMounted(async () => {
  await loadShop()
})

const loadShop = async () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      const response = await axios.post('/api/shops/get-or-create', {
        userId: user.id,
        userName: user.name
      })
      shop.value = response.data
    }
  } catch (error) {
    console.error('Load shop error:', error)
  } finally {
    loading.value = false
  }
}

const editShop = () => {
  editForm.value = { ...shop.value }
  showEditDialog.value = true
}

const saveShop = async () => {
  try {
    await axios.put(`/api/shops/${editForm.value.id}`, editForm.value)
    await loadShop()
    showEditDialog.value = false
  } catch (error) {
    console.error('Save shop error:', error)
  }
}
</script>

<style scoped>
.shop-management {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.header-section {
  background: linear-gradient(135deg, #28a745 0%, #218838 100%);
  padding: 30px;
  border-radius: 15px;
  color: white;
  margin-bottom: 25px;
  box-shadow: 0 8px 20px rgba(40, 167, 69, 0.25);
  position: relative;
  overflow: hidden;
}

.header-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.header-icon {
  font-size: 48px;
  margin-bottom: 10px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
  position: relative;
  z-index: 1;
}

.shop-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f9f4;
}

.shop-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #28a745, #5cb85c);
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  flex-shrink: 0;
}

.shop-info h3 {
  font-size: 24px;
  color: #28a745;
  font-weight: bold;
  margin-bottom: 8px;
}

.shop-info p {
  color: #888;
  font-size: 13px;
}

.shop-details {
  margin-bottom: 25px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 18px;
  margin-bottom: 12px;
  background: #f8fdf9;
  border-radius: 12px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.detail-item:hover {
  background: #f0f9f4;
  border-left-color: #28a745;
  transform: translateX(5px);
}

.detail-item i {
  color: #28a745;
  font-size: 20px;
  margin-top: 2px;
  width: 24px;
  text-align: center;
}

.detail-item span {
  color: #555;
  font-size: 14px;
  line-height: 1.6;
}

.detail-item strong {
  color: #333;
}

.desc-detail {
  flex-direction: column;
  align-items: stretch;
}

.desc-detail p {
  margin-top: 8px;
  padding-left: 36px;
  color: #666;
  line-height: 1.7;
}

.action-bar {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 2px solid #f0f9f4;
}

.btn-primary {
  background: linear-gradient(135deg, #28a745, #218838);
  color: white;
  padding: 14px 35px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(40, 167, 69, 0.4);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.empty-state i {
  font-size: 64px;
  color: #ccc;
  margin-bottom: 15px;
}

.empty-state p {
  color: #999;
  font-size: 16px;
}

.loading-container {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #28a745;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(30px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  background: linear-gradient(135deg, #28a745 0%, #218838 100%);
  color: white;
  padding: 20px 25px;
  border-radius: 16px 16px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 22px;
  line-height: 1;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

.modal-body {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 13px;
}

.form-group label i {
  color: #28a745;
  margin-right: 6px;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #28a745;
  box-shadow: 0 0 0 4px rgba(40, 167, 69, 0.1);
}

.modal-footer {
  padding: 20px 25px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: #fafafa;
  border-radius: 0 0 16px 16px;
}

.btn-cancel,
.btn-save {
  padding: 10px 24px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-cancel {
  background: #e0e0e0;
  color: #666;
}

.btn-cancel:hover {
  background: #d0d0d0;
  transform: translateY(-2px);
}

.btn-save {
  background: linear-gradient(135deg, #28a745, #218838);
  color: white;
}

.btn-save:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
}
</style>
