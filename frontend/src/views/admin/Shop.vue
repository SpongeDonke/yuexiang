<template>
  <div class="shop-management">
    <div class="header-section">
      <div class="header-icon">
        <i class="fas fa-store"></i>
      </div>
      <h2 class="page-title">店铺信息管理</h2>
      <p class="subtitle">管理系统中的所有店铺信息</p>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="content-area">
      <div class="stats-bar">
        <div class="stat-item">
          <i class="fas fa-store"></i>
          <span>店铺总数</span>
          <strong>{{ shops.length }}</strong>
        </div>
        <div class="stat-item">
          <i class="fas fa-map-marker-alt"></i>
          <span>已填写地址</span>
          <strong>{{ shops.filter(s => s.address).length }}</strong>
        </div>
        <div class="stat-item">
          <i class="fas fa-phone"></i>
          <span>已填写电话</span>
          <strong>{{ shops.filter(s => s.phone).length }}</strong>
        </div>
      </div>

      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th><i class="fas fa-hashtag"></i> 店铺ID</th>
              <th><i class="fas fa-building"></i> 店铺名称</th>
              <th><i class="fas fa-map-marker-alt"></i> 地址</th>
              <th><i class="fas fa-phone"></i> 电话</th>
              <th><i class="fas fa-info-circle"></i> 描述</th>
              <th><i class="fas fa-cogs"></i> 操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="shop in shops" :key="shop.id" :class="{ 'row-hover': true }">
              <td><span class="id-badge">{{ shop.id }}</span></td>
              <td><strong>{{ shop.name || '未命名' }}</strong></td>
              <td>{{ shop.address || '-' }}</td>
              <td>{{ shop.phone || '-' }}</td>
              <td class="desc-cell">{{ shop.description || '-' }}</td>
              <td>
                <div class="action-btns">
                  <button class="btn-edit" @click="editShop(shop)" title="编辑">
                    <i class="fas fa-edit"></i> 编辑
                  </button>
                  <button class="btn-delete" @click="showDeleteConfirm(shop.id)" title="删除">
                    <i class="fas fa-trash"></i> 删除
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="shops.length === 0" class="empty-row">
              <td colspan="6"><i class="fas fa-inbox"></i> 暂无店铺数据</td>
            </tr>
          </tbody>
        </table>
      </div>
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
          <button class="btn-save" @click="saveShop"><i class="fas fa-save"></i> 保存</button>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
      <div class="modal-content modal-sm">
        <div class="modal-header warning">
          <h3><i class="fas fa-exclamation-triangle"></i> 确认删除</h3>
          <button class="close-btn" @click="showDeleteDialog = false">&times;</button>
        </div>
        <div class="modal-body text-center">
          <i class="fas fa-question-circle warning-icon"></i>
          <p>确定要删除这个店铺吗？</p>
          <p class="warning-text">此操作不可恢复！</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showDeleteDialog = false">取消</button>
          <button class="btn-danger" @click="confirmDelete"><i class="fas fa-trash"></i> 确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const shops = ref([])
const loading = ref(true)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editForm = ref({})
const currentDeleteId = ref(null)

onMounted(async () => {
  await loadShops()
})

const loadShops = async () => {
  try {
    console.log('开始加载店铺数据')
    const response = await fetch('http://localhost:8081/api/shops')
    console.log('Response status:', response.status)
    console.log('Response headers:', response.headers)
    const data = await response.json()
    console.log('获取店铺数据成功:', data)
    shops.value = data
  } catch (error) {
    console.error('Load shops error:', error)
    console.error('Error message:', error.message)
    console.error('Error stack:', error.stack)
  } finally {
    loading.value = false
    console.log('加载店铺数据完成')
  }
}

const editShop = (shop) => {
  editForm.value = { ...shop }
  showEditDialog.value = true
}

const saveShop = async () => {
  try {
    await axios.post('/api/shops', editForm.value)
    await loadShops()
    showEditDialog.value = false
  } catch (error) {
    console.error('Save shop error:', error)
  }
}

const showDeleteConfirm = (id) => {
  currentDeleteId.value = id
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (currentDeleteId.value) {
    try {
      await axios.delete(`/api/shops/${currentDeleteId.value}`)
      await loadShops()
      showDeleteDialog.value = false
      currentDeleteId.value = null
    } catch (error) {
      console.error('Delete shop error:', error)
    }
  }
}
</script>

<style scoped>
.shop-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-section {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  padding: 30px;
  border-radius: 15px;
  color: white;
  margin-bottom: 25px;
  box-shadow: 0 8px 20px rgba(220, 53, 69, 0.25);
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

.stats-bar {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
}

.stat-item {
  background: white;
  padding: 18px 22px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 14px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-left: 4px solid #dc3545;
}

.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.stat-item i {
  font-size: 28px;
  color: #dc3545;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(220, 53, 69, 0.1);
  border-radius: 50%;
}

.stat-item span {
  flex: 1;
  font-size: 13px;
  color: #666;
}

.stat-item strong {
  font-size: 24px;
  color: #dc3545;
  font-weight: bold;
}

.table-wrapper {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 700px;
}

.data-table thead tr {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
}

.data-table th {
  padding: 16px 12px;
  text-align: left;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.5px;
  white-space: nowrap;
}

.data-table th i {
  margin-right: 6px;
}

.data-table tbody tr {
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.data-table tbody tr.row-hover:hover {
  background: rgba(220, 53, 69, 0.05);
  transform: scale(1.01);
}

.data-table td {
  padding: 16px 12px;
  font-size: 13px;
  vertical-align: middle;
}

.id-badge {
  background: linear-gradient(135deg, #dc3545, #ff6b6b);
  color: white;
  padding: 5px 12px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 11px;
  display: inline-block;
}

.desc-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
}

.action-btns {
  display: flex;
  gap: 8px;
}

.btn-edit,
.btn-delete {
  padding: 7px 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.btn-edit {
  background: linear-gradient(135deg, #17a2b8, #138496);
  color: white;
}

.btn-edit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(23, 162, 184, 0.4);
}

.btn-delete {
  background: linear-gradient(135deg, #dc3545, #c82333);
  color: white;
}

.btn-delete:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.4);
}

.empty-row td {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}

.empty-row i {
  font-size: 36px;
  margin-right: 10px;
  opacity: 0.5;
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
  border-top: 4px solid #dc3545;
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

.modal-content.modal-sm {
  width: 400px;
}

.modal-header {
  background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
  color: white;
  padding: 20px 25px;
  border-radius: 16px 16px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header.warning {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
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

.text-center {
  text-align: center;
}

.warning-icon {
  font-size: 56px;
  color: #f39c12;
  margin-bottom: 15px;
}

.modal-body p {
  font-size: 15px;
  color: #555;
  margin-bottom: 8px;
}

.warning-text {
  color: #dc3545 !important;
  font-weight: 600;
  font-size: 13px;
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
  color: #dc3545;
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
  border-color: #dc3545;
  box-shadow: 0 0 0 4px rgba(220, 53, 69, 0.1);
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
.btn-save,
.btn-danger {
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

.btn-danger {
  background: linear-gradient(135deg, #dc3545, #c82333);
  color: white;
}

.btn-danger:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.4);
}
</style>
