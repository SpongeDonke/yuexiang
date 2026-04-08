<template>
  <div class="order-progress">
    <div class="header-section">
      <div class="header-icon">
        <i class="fas fa-tasks"></i>
      </div>
      <h2 class="page-title">订单进度</h2>
      <p class="subtitle">实时查看您的订单处理状态</p>
    </div>

    <div class="filter-section">
      <div class="filter-group">
        <label><i class="fas fa-filter"></i> 订单筛选：</label>
        <select v-model="selectedOrderId" @change="loadProgress" class="filter-select">
          <option value="">请选择订单</option>
          <option v-for="order in orders" :key="order.id" :value="order.id">
            #{{ order.id }} - {{ getStatusText(order.status) }} - {{ order.shopName || '未知店铺' }} - {{ getServiceNames(order) }} - {{ formatDate(order.createdAt) }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="selectedOrderId && currentOrder" class="order-info-card">
      <div class="info-row">
        <span class="info-label"><i class="fas fa-hashtag"></i> 订单编号：</span>
        <span class="info-value">#{{ currentOrder.id }}</span>
      </div>
      <div class="info-row">
        <span class="info-label"><i class="fas fa-store"></i> 店铺名称：</span>
        <span class="info-value">{{ currentOrder.shopName || '未知店铺' }}</span>
      </div>
      <div class="info-row">
        <span class="info-label"><i class="fas fa-concierge-bell"></i> 服务项目：</span>
        <span class="info-value">{{ getServiceNames(currentOrder) }}</span>
      </div>
      <div class="info-row">
        <span class="info-label"><i class="fas fa-yen-sign"></i> 订单金额：</span>
        <span class="info-value price">¥{{ currentOrder.totalAmount?.toFixed(2) || currentOrder.totalAmount?.toFixed(2) || '0.00' }}</span>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="progressList.length > 0" class="timeline-wrapper">
      <div class="timeline">
        <div
          v-for="(progress, index) in progressList"
          :key="progress.id"
          class="timeline-item"
          :class="{ 'active': index === 0, 'completed': index > 0 }"
        >
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="progress-header">
              <h3>{{ getStatusText(progress.status) }}</h3>
              <span class="time-badge">{{ formatTime(progress.createdAt) }}</span>
            </div>
            <p class="progress-desc">{{ progress.description || '暂无描述' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="selectedOrderId" class="empty-state">
      <i class="fas fa-clipboard-list"></i>
      <h3>暂无进度记录</h3>
      <p>该订单暂无进度更新信息</p>
    </div>

    <div v-else class="empty-state">
      <i class="fas fa-clipboard-list"></i>
      <h3>暂无订单进度</h3>
      <p>请选择一个订单查看进度详情</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const orders = ref([])
const progressList = ref([])
const loading = ref(false)
const selectedOrderId = ref('')

const currentOrder = computed(() => {
  return orders.value.find(o => o.id === Number(selectedOrderId.value))
})

const statusMap = {
  'ordered': '待接单',
  'accepted': '已接单',
  'processing': '处理中',
  'completed': '已完成',
  'cancelled': '已取消',
  'paid': '已支付',
  'unpaid': '未支付'
}

const getStatusText = (status) => {
  return statusMap[status] || status || '未知状态'
}

const getServiceNames = (order) => {
  if (!order?.orderDetails || order.orderDetails.length === 0) {
    return '暂无服务信息'
  }
  return order.orderDetails.map(d => d.serviceName || '未知服务').join('、')
}

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  try {
    const response = await axios.get('/api/orders')
    orders.value = response.data
  } catch (error) {
    console.error('Load orders error:', error)
  }
}

const loadProgress = async () => {
  if (!selectedOrderId.value) {
    progressList.value = []
    return
  }

  loading.value = true
  try {
    const response = await axios.get(`/api/order-progresses/order/${selectedOrderId.value}`)
    progressList.value = response.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } catch (error) {
    console.error('Load progress error:', error)
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatDate = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}/${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.order-progress {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.header-section {
  background: linear-gradient(135deg, #6f42c1 0%, #5a32a3 100%);
  padding: 30px;
  border-radius: 15px;
  color: white;
  margin-bottom: 25px;
  box-shadow: 0 8px 20px rgba(111, 66, 193, 0.25);
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

.filter-section {
  background: white;
  padding: 22px 28px;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-group label {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label i {
  color: #6f42c1;
}

.filter-select {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  font-family: inherit;
  cursor: pointer;
}

.filter-select:focus {
  outline: none;
  border-color: #6f42c1;
  box-shadow: 0 0 0 4px rgba(111, 66, 193, 0.1);
}

.order-info-card {
  background: white;
  padding: 22px 28px;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.12);
  margin-bottom: 25px;
  border-left: 4px solid #6f42c1;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  width: 110px;
  font-size: 13px;
  color: #666;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.info-label i {
  color: #6f42c1;
  font-size: 14px;
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.info-value.price {
  color: #dc3545;
  font-size: 18px;
  font-weight: bold;
}

.timeline-wrapper {
  background: white;
  padding: 35px;
  border-radius: 15px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.timeline {
  position: relative;
  padding-left: 40px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 14px;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #6f42c1, #e9ecef);
  border-radius: 4px;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
  animation: slideInRight 0.5s ease-out;
}

@keyframes slideInRight {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: -34px;
  top: 5px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #e9ecef;
  border: 4px solid white;
  box-shadow: 0 0 0 3px #e9ecef;
  transition: all 0.3s ease;
  z-index: 1;
}

.timeline-item.active .timeline-dot {
  background: linear-gradient(135deg, #6f42c1, #7b5dd6);
  box-shadow: 0 0 0 3px rgba(111, 66, 193, 0.3), 0 0 20px rgba(111, 66, 193, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 3px rgba(111, 66, 193, 0.3), 0 0 20px rgba(111, 66, 193, 0.4); }
  50% { box-shadow: 0 0 0 3px rgba(111, 66, 193, 0.3), 0 0 25px rgba(111, 66, 193, 0.6); }
  100% { box-shadow: 0 0 0 3px rgba(111, 66, 193, 0.3), 0 0 20px rgba(111, 66, 193, 0.4); }
}

.timeline-item.completed .timeline-dot {
  background: linear-gradient(135deg, #28a745, #34ce57);
  box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.3);
}

.timeline-content {
  background: #fafafa;
  padding: 20px 24px;
  border-radius: 12px;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.timeline-item.active .timeline-content {
  background: linear-gradient(135deg, rgba(111, 66, 193, 0.05), rgba(123, 93, 214, 0.03));
  border-left-color: #6f42c1;
  box-shadow: 0 4px 15px rgba(111, 66, 193, 0.1);
}

.timeline-item.completed .timeline-content {
  background: #f8fdf9;
  border-left-color: #28a745;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.progress-header h3 {
  font-size: 17px;
  font-weight: 600;
  color: #333;
}

.timeline-item.active .progress-header h3 {
  color: #6f42c1;
}

.time-badge {
  font-size: 11px;
  color: #888;
  background: #f0f0f0;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 600;
}

.timeline-item.active .time-badge {
  background: rgba(111, 66, 193, 0.1);
  color: #6f42c1;
}

.progress-desc {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.empty-state i {
  font-size: 72px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #999;
  font-size: 20px;
  margin-bottom: 10px;
}

.empty-state p {
  color: #bbb;
  font-size: 14px;
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
  border-top: 4px solid #6f42c1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
