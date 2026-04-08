<template>
  <div class="card">
    <h2 class="page-title">店家管理</h2>
    <div v-if="loading">
      <div class="loading">加载中...</div>
    </div>
    <div v-else>
      <table class="table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>姓名</th>
            <th>手机号</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in shopkeepers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.phone }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(user.status)">
                {{ user.status === 'active' ? '已激活' : user.status === 'pending' ? '待审核' : user.status }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <button class="btn btn-secondary" @click="editUser(user)">编辑</button>
              <button class="btn btn-secondary" style="margin-left: 10px;" @click="showDeleteConfirm(user.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 编辑用户对话框 -->
    <div v-if="showEditDialog" class="modal-overlay">
      <div class="modal">
        <h3>编辑用户</h3>
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="editForm.username" disabled />
        </div>
        <div class="form-group">
          <label>姓名</label>
          <input type="text" v-model="editForm.name" />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input type="text" v-model="editForm.phone" />
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="editForm.status">
            <option value="active">已激活</option>
            <option value="pending">待审核</option>
          </select>
        </div>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showEditDialog = false">取消</button>
          <button class="btn btn-primary" style="margin-left: 10px;" @click="saveUser">保存</button>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="modal-overlay">
      <div class="modal">
        <h3>确认删除</h3>
        <p>确定要删除这个用户吗？</p>
        <div class="modal-actions">
          <button class="btn btn-secondary" @click="showDeleteDialog = false">取消</button>
          <button class="btn btn-primary" style="margin-left: 10px;" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const shopkeepers = ref([])
const loading = ref(true)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editForm = ref({})
const currentDeleteId = ref(null)

onMounted(async () => {
  await loadShopkeepers()
})

const loadShopkeepers = async () => {
  try {
    // 调用后端API获取所有商家
    const response = await axios.get('/api/shopkeepers')
    shopkeepers.value = response.data
  } catch (error) {
    console.error('Load shopkeepers error:', error)
  } finally {
    loading.value = false
  }
}

const editUser = (user) => {
  editForm.value = { ...user }
  showEditDialog.value = true
}

const saveUser = async () => {
  try {
    // 调用后端API更新用户
    await axios.post('/api/users', editForm.value)
    // 重新加载用户列表
    await loadShopkeepers()
    showEditDialog.value = false
  } catch (error) {
    console.error('Save user error:', error)
  }
}

const showDeleteConfirm = (id) => {
  currentDeleteId.value = id
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (currentDeleteId.value) {
    try {
      // 调用后端API删除用户
      await axios.delete(`/api/users/${currentDeleteId.value}`)
      // 重新加载用户列表
      await loadShopkeepers()
      // 关闭删除确认对话框
      showDeleteDialog.value = false
      // 重置当前删除ID
      currentDeleteId.value = null
    } catch (error) {
      console.error('Delete user error:', error)
    }
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case 'active':
      return 'status-active'
    case 'pending':
      return 'status-pending'
    default:
      return ''
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status-active {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-pending {
  background-color: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}
</style>
