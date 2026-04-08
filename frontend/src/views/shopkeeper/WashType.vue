<template>
  <div class="type-management">
    <div class="header-section">
      <div class="header-left">
        <div class="header-icon">
          <i class="fas fa-tags"></i>
        </div>
        <div>
          <h2 class="page-title">洗护类型管理</h2>
          <p class="subtitle">管理系统中的洗护服务分类</p>
        </div>
      </div>
      <button class="btn-add" @click="showAddDialog = true">
        <i class="fas fa-plus-circle"></i> 添加类型
      </button>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="content-area">
      <div class="stats-bar">
        <div class="stat-item">
          <i class="fas fa-tags"></i>
          <span>类型总数</span>
          <strong>{{ types.length }}</strong>
        </div>
        <div class="stat-item">
          <i class="fas fa-concierge-bell"></i>
          <span>关联服务</span>
          <strong>{{ totalServices }}</strong>
        </div>
      </div>

      <div class="types-grid">
        <div v-for="type in types" :key="type.id" class="type-card">
          <div class="type-header">
            <div class="type-icon">
              <i :class="getTypeIcon(type.name)"></i>
            </div>
            <h3>{{ type.name }}</h3>
            <span class="type-id">#{{ type.id }}</span>
          </div>
          <p class="type-desc">{{ type.description || '暂无描述' }}</p>
          <div class="type-footer">
            <button class="btn-edit" @click="editType(type)">
              <i class="fas fa-edit"></i> 编辑
            </button>
            <button class="btn-delete" @click="showDeleteConfirm(type.id)">
              <i class="fas fa-trash"></i> 删除
            </button>
          </div>
        </div>

        <div v-if="types.length === 0" class="empty-state">
          <i class="fas fa-inbox"></i>
          <p>暂无类型数据</p>
        </div>
      </div>
    </div>

    <!-- 添加类型对话框 -->
    <div v-if="showAddDialog" class="modal-overlay" @click.self="showAddDialog = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3><i class="fas fa-plus-circle"></i> 添加类型</h3>
          <button class="close-btn" @click="showAddDialog = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label><i class="fas fa-tag"></i> 类型名称</label>
            <input type="text" v-model="addForm.name" placeholder="请输入类型名称" />
          </div>
          <div class="form-group">
            <label><i class="fas fa-info-circle"></i> 描述</label>
            <textarea v-model="addForm.description" rows="4" placeholder="请输入描述"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddDialog = false">取消</button>
          <button class="btn-save" @click="addType"><i class="fas fa-plus"></i> 添加</button>
        </div>
      </div>
    </div>

    <!-- 编辑类型对话框 -->
    <div v-if="showEditDialog" class="modal-overlay" @click.self="showEditDialog = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3><i class="fas fa-edit"></i> 编辑类型</h3>
          <button class="close-btn" @click="showEditDialog = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label><i class="fas fa-tag"></i> 类型名称</label>
            <input type="text" v-model="editForm.name" placeholder="请输入类型名称" />
          </div>
          <div class="form-group">
            <label><i class="fas fa-info-circle"></i> 描述</label>
            <textarea v-model="editForm.description" rows="4" placeholder="请输入描述"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showEditDialog = false">取消</button>
          <button class="btn-save" @click="saveType"><i class="fas fa-save"></i> 保存修改</button>
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
          <p>确定要删除这个类型吗？</p>
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
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const types = ref([])
const loading = ref(true)
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const addForm = ref({ name: '', description: '' })
const editForm = ref({})
const currentDeleteId = ref(null)

const totalServices = computed(() => {
  return types.value.reduce((sum, type) => sum + (type.serviceCount || 0), 0)
})

onMounted(async () => {
  await loadTypes()
})

const loadTypes = async () => {
  try {
    const response = await axios.get('/api/wash-types')
    types.value = response.data
  } catch (error) {
    console.error('Load types error:', error)
  } finally {
    loading.value = false
  }
}

const addType = async () => {
  try {
    await axios.post('/api/wash-types', addForm.value)
    await loadTypes()
    showAddDialog.value = false
    addForm.value = { name: '', description: '' }
  } catch (error) {
    console.error('Add type error:', error)
  }
}

const getTypeIcon = (name) => {
  const icons = {
    '干洗': 'fa-tshirt',
    '水洗': 'fa-water',
    '皮具护理': 'fa-shoe-prints',
    '鞋类清洗': 'fa-socks',
    '家纺清洗': 'fa-couch',
    '奢侈品护理': 'fa-gem',
    '其他': 'fa-tag'
  }
  return icons[name] || 'fa-tag'
}

const editType = (type) => {
  editForm.value = { ...type }
  showEditDialog.value = true
}

const saveType = async () => {
  try {
    await axios.post('/api/wash-types', editForm.value)
    await loadTypes()
    showEditDialog.value = false
  } catch (error) {
    console.error('Save type error:', error)
  }
}

const showDeleteConfirm = (id) => {
  currentDeleteId.value = id
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (currentDeleteId.value) {
    try {
      await axios.delete(`/api/wash-types/${currentDeleteId.value}`)
      await loadTypes()
      showDeleteDialog.value = false
      currentDeleteId.value = null
    } catch (error) {
      console.error('Delete type error:', error)
    }
  }
}
</script>

<style scoped>
.type-management {
  padding: 20px;
  max-width: 1200px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.header-icon {
  font-size: 48px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
}

.subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.btn-add {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  padding: 12px 24px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.btn-add:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
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
  border-left: 4px solid #28a745;
}

.stat-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.stat-item i {
  font-size: 28px;
  color: #28a745;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(40, 167, 69, 0.1);
  border-radius: 50%;
}

.stat-item span {
  flex: 1;
  font-size: 13px;
  color: #666;
}

.stat-item strong {
  font-size: 24px;
  color: #28a745;
  font-weight: bold;
}

.types-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.type-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 4px solid transparent;
}

.type-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border-top-color: #28a745;
}

.type-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.type-icon {
  width: 55px;
  height: 55px;
  background: linear-gradient(135deg, #28a745, #5cb85c);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.type-header h3 {
  font-size: 18px;
  color: #333;
  font-weight: bold;
  margin-bottom: 2px;
}

.type-id {
  font-size: 11px;
  color: #999;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.type-desc {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 18px;
  padding-left: 70px;
  min-height: 42px;
}

.type-footer {
  display: flex;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.btn-edit,
.btn-delete {
  flex: 1;
  padding: 9px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
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

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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

.modal-content.modal-sm {
  width: 400px;
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
