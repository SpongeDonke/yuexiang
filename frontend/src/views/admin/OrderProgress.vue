<template>
  <div class="progress-container">
    <div class="page-header">
      <div class="header-left">
        <i class="fas fa-tasks header-icon"></i>
        <h1 class="page-title">订单进度管理</h1>
      </div>
      <div class="header-badge">
        <i class="fas fa-shield-alt"></i>
        管理权限
      </div>
    </div>

    <div class="main-content">
      <div class="select-section">
        <label><i class="fas fa-list-ol"></i> 选择订单</label>
        <div class="select-wrapper">
          <select v-model="selectedOrderId" @change="loadOrderProgress" class="order-select">
            <option value="">请选择订单查看进度</option>
            <option v-for="order in orders" :key="order.id" :value="order.id">
              #{{ order.id }} - {{ order.status }} - {{ order.shopName || '未知店铺' }} - {{ getServiceNames(order) }} - {{ formatDateShort(order.createdAt) }}
            </option>
          </select>
          <button v-if="selectedOrderId" class="btn btn-primary" @click="showAddDialog = true">
            <i class="fas fa-plus-circle"></i>
            添加进度
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner">
          <i class="fas fa-spinner fa-spin"></i>
          <p>加载中...</p>
        </div>
      </div>

      <div v-else-if="!selectedOrderId" class="empty-state">
        <i class="fas fa-hand-point-left empty-icon"></i>
        <h3>请选择订单</h3>
        <p>从上方下拉菜单选择一个订单以查看其进度记录</p>
      </div>

      <div v-else-if="orderProgresses.length === 0" class="empty-state">
        <i class="fas fa-history empty-icon"></i>
        <h3>暂无进度记录</h3>
        <p>该订单还没有任何进度记录，点击上方按钮添加</p>
      </div>

      <div v-else class="timeline-container">
        <h3 class="section-title">
          <i class="fas fa-stream"></i>
          进度时间线
        </h3>
        <div class="timeline">
          <transition-group name="timeline-fade" tag="div">
            <div v-for="(progress, index) in orderProgresses" :key="progress.id"
                 class="timeline-item">
              <div class="timeline-dot" :class="'status-' + progress.status.replace('已', '').replace('待', '')">
                <i :class="getTimelineIcon(progress.status)"></i>
              </div>
              <div class="timeline-line" v-if="index < orderProgresses.length - 1"></div>
              <div class="timeline-content">
                <div class="timeline-header">
                  <span class="timeline-status" :class="'badge-' + getStatusBadge(progress.status)">
                    {{ progress.status }}
                  </span>
                  <span class="timeline-time">
                    <i class="fas fa-clock"></i>
                    {{ formatDate(progress.createdAt) }}
                  </span>
                </div>
                <p class="timeline-desc">{{ progress.description }}</p>
              </div>
            </div>
          </transition-group>
        </div>
      </div>
    </div>

    <!-- 添加进度对话框 -->
    <transition name="modal-fade">
      <div v-if="showAddDialog" class="modal-overlay" @click="showAddDialog = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <div class="modal-title-area">
              <i class="fas fa-plus-circle modal-icon"></i>
              <h3>添加进度记录</h3>
            </div>
            <button class="close-btn" @click="showAddDialog = false">&times;</button>
          </div>
          <form @submit.prevent="addProgress" class="modal-body">
            <div class="modal-form-group">
              <label><i class="fas fa-tag"></i> 状态</label>
              <input type="text" v-model="addForm.status"
                     placeholder="例如：已下单、处理中、已完成等" />
            </div>
            <div class="modal-form-group">
              <label><i class="fas fa-align-left"></i> 描述</label>
              <textarea v-model="addForm.description" rows="4"
                        placeholder="详细描述当前进度的状态和操作..."></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="showAddDialog = false">
                <i class="fas fa-times"></i> 取消
              </button>
              <button type="submit" class="btn btn-primary">
                <i class="fas fa-check"></i> 保存
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])
const orderProgresses = ref([])
const selectedOrderId = ref('')
const loading = ref(true)
const showAddDialog = ref(false)
const addForm = ref({ status: '', description: '' })

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  try {
    const response = await axios.get('/api/orders')
    const statusMap = {
      'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
      'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
      'processing': '处理中'
    }
    orders.value = response.data.map(order => ({
      ...order,
      status: statusMap[order.status.toLowerCase()] || order.status
    }))
  } catch (error) {
    console.error('Load orders error:', error)
  } finally {
    loading.value = false
  }
}

const loadOrderProgress = async () => {
  if (selectedOrderId.value) {
    try {
      const response = await axios.get(`/api/order-progresses/order/${selectedOrderId.value}`)
      const statusMap = {
        'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
        'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
        'processing': '处理中'
      }
      orderProgresses.value = response.data.map(progress => ({
        ...progress,
        status: statusMap[progress.status.toLowerCase()] || progress.status
      }))
    } catch (error) {
      console.error('Load order progress error:', error)
    }
  }
}

const addProgress = async () => {
  try {
    const progressData = {
      orderId: selectedOrderId.value,
      status: addForm.value.status,
      description: addForm.value.description
    }
    await axios.post('/api/order-progresses', progressData)
    await loadOrderProgress()
    showAddDialog.value = false
    addForm.value = { status: '', description: '' }
  } catch (error) {
    console.error('Add progress error:', error)
  }
}

const getTimelineIcon = (status) => {
  switch (status) {
    case '已下单':
    case '待处理': return 'fas fa-hourglass-start'
    case '处理中':
    case '洗涤中': return 'fas fa-spinner fa-spin'
    case '已完成': return 'fas fa-check'
    case '已取消': return 'fas fa-times'
    default: return 'fas fa-info-circle'
  }
}

const getStatusBadge = (status) => {
  switch (status) {
    case '已下单':
    case '待处理': return 'pending'
    case '处理中':
    case '洗涤中': return 'processing'
    case '已完成': return 'completed'
    case '已取消': return 'canceled'
    default: return ''
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

const getServiceNames = (order) => {
  if (!order?.orderDetails || order.orderDetails.length === 0) {
    return '暂无服务信息'
  }
  return order.orderDetails.map(d => d.serviceName || d.service?.name || '未知服务').join('、')
}

const formatDateShort = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}/${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.progress-container {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  font-size: 32px;
  color: #ff4d4f;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.header-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(255, 77, 79, 0.35);
}

.main-content {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.select-section label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #555;
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 12px;
}

.select-section label i { color: #ff4d4f; }

.select-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 24px;
}

.order-select {
  flex: 1;
  padding: 14px 18px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: #fafafa;
  cursor: pointer;
}

.order-select:focus {
  outline: none;
  border-color: #ff4d4f;
  box-shadow: 0 0 0 4px rgba(255, 77, 79, 0.08);
  background: white;
}

.btn {
  padding: 12px 22px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(255, 77, 79, 0.35);
}
.btn-primary:hover { transform: translateY(-2px); }

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}
.btn-secondary:hover { background: #e8e8e8; }

.loading-container {
  text-align: center;
  padding: 60px;
}
.loading-spinner { font-size: 48px; color: #ff4d4f; }
.loading-spinner p { margin-top: 16px; color: #999; }

.empty-state {
  text-align: center;
  padding: 60px 20px;
}
.empty-icon { font-size: 64px; color: #ddd; margin-bottom: 20px; }
.empty-state h3 { color: #999; font-size: 20px; margin-bottom: 8px; }
.empty-state p { color: #bbb; font-size: 14px; }

.timeline-container { margin-top: 8px; }

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  padding-bottom: 14px;
  border-bottom: 2px solid #f5f5f5;
}
.section-title i { color: #ff4d4f; }

.timeline {
  position: relative;
  padding-left: 40px;
}

.timeline-item {
  position: relative;
  padding-bottom: 30px;
}

.timeline-dot {
  position: absolute;
  left: -40px;
  top: 2px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.12);
}

.timeline-dot i { font-size: 16px; color: white; }

.timeline-dot.status-下单,
.timeline-dot.status-处理,
.timeline-dot.status-洗涤中 {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
}
.timeline-dot.status-完成 {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}
.timeline-dot.status-取消 {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.timeline-line {
  position: absolute;
  left: -22px;
  top: 38px;
  width: 4px;
  height: calc(100% + 10px);
  background: linear-gradient(to bottom, #e8e8e8, #f5f5f5);
  border-radius: 2px;
}

.timeline-content {
  background: #fafafa;
  border-radius: 12px;
  padding: 18px 22px;
  border-left: 4px solid #e8e8e8;
  transition: all 0.3s ease;
}

.timeline-content:hover {
  background: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateX(4px);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.timeline-status {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.badge-pending { background: #e6f7ff; color: #1890ff; }
.badge-processing { background: #fffbe6; color: #faad14; }
.badge-completed { background: #f6ffed; color: #52c41a; }
.badge-canceled { background: #fff1f0; color: #ff4d4f; }

.timeline-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
}

.timeline-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.timeline-fade-enter-active,
.timeline-fade-leave-active { transition: all 0.4s ease; }
.timeline-fade-enter-from { opacity: 0; transform: translateX(-20px); }
.timeline-fade-leave-to { opacity: 0; transform: translateX(20px); }

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.25s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.modal-content {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}
@keyframes slideUp { from { transform: translateY(30px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 26px;
  background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
}
.modal-title-area { display: flex; align-items: center; gap: 12px; }
.modal-icon { font-size: 22px; color: white; }
.modal-header h3 { margin: 0; color: white; font-size: 19px; font-weight: 600; }
.close-btn {
  background: rgba(255,255,255,0.2);
  border: none;
  font-size: 26px;
  cursor: pointer;
  color: white;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}
.close-btn:hover { background: rgba(255,255,255,0.3); }

.modal-body { padding: 26px; }

.modal-form-group { margin-bottom: 20px; }
.modal-form-group:last-of-type { margin-bottom: 24px; }

.modal-form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #555;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}
.modal-form-group label i { color: #ff4d4f; }

.modal-form-group input,
.modal-form-group textarea {
  width: 100%;
  padding: 13px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.modal-form-group input:focus,
.modal-form-group textarea:focus {
  outline: none;
  border-color: #ff4d4f;
  box-shadow: 0 0 0 4px rgba(255, 77, 79, 0.08);
  background: white;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-fade-enter-active,
.modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .page-header { flex-direction: column; align-items: start; gap: 14px; }
  .select-wrapper { flex-direction: column; align-items: stretch; }
  .modal-actions { flex-direction: column-reverse; }
}
</style>
