<template>
  <div class="card">
    <h2 class="page-title">顾客管理</h2>
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
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in customers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.name }}</td>
            <td>{{ user.phone }}</td>
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

const customers = ref([])
const loading = ref(true)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const editForm = ref({})
const deleteUserId = ref(null)

onMounted(async () => {
  await loadCustomers()
})

const loadCustomers = async () => {
  try {
    // 调用后端API获取所有用户
    const response = await axios.get('/api/users')
    // 过滤出顾客用户（roleId = 3）
    customers.value = response.data.filter(user => user.roleId === 3)
  } catch (error) {
    console.error('Load customers error:', error)
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
    await loadCustomers()
    showEditDialog.value = false
  } catch (error) {
    console.error('Save user error:', error)
  }
}

const showDeleteConfirm = (id) => {
  deleteUserId.value = id
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  try {
    // 调用后端API删除用户
    await axios.delete(`/api/users/${deleteUserId.value}`)
    // 重新加载用户列表
    await loadCustomers()
    showDeleteDialog.value = false
  } catch (error) {
    console.error('Delete user error:', error)
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
</style>
