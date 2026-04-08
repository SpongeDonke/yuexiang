<template>
  <div class="order-container">
    <div class="page-header">
      <div class="header-left">
        <i class="fas fa-clipboard-list header-icon"></i>
        <h1 class="page-title">订单管理</h1>
      </div>
      <div class="stats-cards">
        <div class="stat-card total">
          <i class="fas fa-shopping-bag"></i>
          <div class="stat-info">
            <span class="stat-number">{{ orders.length }}</span>
            <span class="stat-label">全部</span>
          </div>
        </div>
        <div class="stat-card pending">
          <i class="fas fa-clock"></i>
          <div class="stat-info">
            <span class="stat-number">{{ pendingCount }}</span>
            <span class="stat-label">待处理</span>
          </div>
        </div>
        <div class="stat-card completed">
          <i class="fas fa-check-double"></i>
          <div class="stat-info">
            <span class="stat-number">{{ completedCount }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </div>
    </div>

    <div class="search-bar">
      <div class="search-wrapper">
        <i class="fas fa-search search-icon"></i>
        <input type="text" v-model="searchOrderId" placeholder="搜索订单ID..."
               @keyup.enter="searchOrders" />
      </div>
      <div class="status-filter-wrapper">
        <i class="fas fa-filter filter-icon"></i>
        <select v-model="selectedStatus" @change="filterByStatus" class="status-filter-select">
          <option value="">全部状态</option>
          <option value="已下单">已下单</option>
          <option value="处理中">处理中</option>
          <option value="已完成">已完成</option>
          <option value="已取消">已取消</option>
        </select>
      </div>
      <button class="btn btn-primary" @click="searchOrders">
        <i class="fas fa-search"></i> 搜索
      </button>
      <button class="btn btn-outline" @click="resetSearch">
        <i class="fas fa-redo"></i> 重置
      </button>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner">
        <i class="fas fa-spinner fa-spin"></i>
        <p>加载中...</p>
      </div>
    </div>

    <div v-else-if="orders.length === 0" class="empty-state">
      <i class="fas fa-inbox empty-icon"></i>
      <h3>暂无订单</h3>
      <p>等待顾客下单中...</p>
    </div>

    <div v-else class="orders-grid">
      <transition-group name="order-fade" tag="div" class="grid-content">
        <div v-for="order in paginatedOrders" :key="order.id" class="order-card">
          <div class="card-top-bar" :class="'status-' + order.status.replace('已', '').replace('待', '')">
            <span class="order-id-badge">#{{ order.id }}</span>
            <span class="status-badge" :class="getStatusClass(order.status)">
              <i :class="getStatusIcon(order.status)"></i>
              {{ order.status }}
            </span>
          </div>

          <div class="service-info">
            <i class="fas fa-concierge-bell service-icon"></i>
            <span>{{ getServiceNames(order) }}</span>
          </div>

          <div class="amount-display">
            <span class="amount-label">总金额</span>
            <span class="amount-value">
              <i class="fas fa-yen-sign"></i> {{ order.totalAmount }}
            </span>
          </div>

          <div class="time-info">
            <i class="fas fa-calendar-alt"></i>
            <span>{{ formatDate(order.createdAt) }}</span>
          </div>

          <div v-if="order.appointmentTime" class="appointment-info">
            <i class="fas fa-clock"></i>
            <span>预约: {{ formatDateTime(order.appointmentTime) }}</span>
          </div>

          <div class="customer-info">
            <i class="fas fa-user customer-icon"></i>
            <span>客户: {{ order.contactName || '未填写' }}</span>
          </div>

          <div class="card-actions">
            <button class="btn-icon view" @click="viewOrderDetail(order.id)" title="查看详情">
              <i class="fas fa-eye"></i>
            </button>
            <select :value="order.status" @change="(e) => updateOrderStatus(order, e.target.value)"
                    class="status-select">
              <option value="已下单">已下单</option>
              <option value="处理中">处理中</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
        </div>
      </transition-group>

      <div v-if="orders.length > 0" class="pagination-wrapper">
        <div class="pagination-info">
          共 <strong>{{ orders.length }}</strong> 条记录，第 <strong>{{ currentPage }}</strong>/{{ totalPages }} 页
        </div>
        <div class="pagination-controls">
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage = 1">
            <i class="fas fa-angle-double-left"></i>
          </button>
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">
            <i class="fas fa-chevron-left"></i>
          </button>
          <template v-for="page in visiblePages" :key="page">
            <button v-if="page === '...'" class="page-btn ellipsis">...</button>
            <button v-else class="page-btn" :class="{ active: currentPage === page }" @click="currentPage = page">{{ page }}</button>
          </template>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">
            <i class="fas fa-chevron-right"></i>
          </button>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage = totalPages">
            <i class="fas fa-angle-double-right"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <transition name="modal-fade">
      <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <div class="modal-title-area">
              <i class="fas fa-file-invoice-dollar modal-icon"></i>
              <h3>订单详情</h3>
            </div>
            <button class="close-btn" @click="closeDetailModal">&times;</button>
          </div>
          <div class="modal-body">
            <div v-if="detailLoading" class="loading">加载中...</div>
            <div v-else-if="orderDetail" class="detail-grid">
              <div class="detail-item highlight">
                <label><i class="fas fa-hashtag"></i> 订单ID</label>
                <span>#{{ orderDetail.id }}</span>
              </div>
              <div class="detail-item">
                <label><i class="fas fa-user-tie"></i> 顾客ID</label>
                <span>{{ orderDetail.customerId }}</span>
              </div>
              <div class="detail-item">
                <label><i class="fas fa-store"></i> 店铺ID</label>
                <span>{{ orderDetail.shopId }}</span>
              </div>
              <div class="detail-item highlight amount">
                <label><i class="fas fa-money-bill-wave"></i> 总金额</label>
                <span class="big-amount">¥{{ orderDetail.totalAmount }}</span>
              </div>
              <div class="detail-item status-detail">
                <label><i class="fas fa-info-circle"></i> 状态</label>
                <span class="status-text">{{ orderDetail.status }}</span>
              </div>
              <div class="detail-item">
                <label><i class="fas fa-user"></i> 联系人</label>
                <span>{{ orderDetail.contactName || '无' }}</span>
              </div>
              <div class="detail-item">
                <label><i class="fas fa-phone"></i> 联系电话</label>
                <span>{{ orderDetail.contactPhone || '无' }}</span>
              </div>
              <div class="detail-item full-width">
                <label><i class="fas fa-map-marker-alt"></i> 地址</label>
                <span>{{ orderDetail.address || '无' }}</span>
              </div>
              <div class="detail-item full-width">
                <label><i class="fas fa-sticky-note"></i> 备注</label>
                <span>{{ orderDetail.remark || '无' }}</span>
              </div>
              <div class="detail-item full-width time-row">
                <div class="time-col">
                  <label><i class="fas fa-plus-circle"></i> 创建时间</label>
                  <span>{{ formatDate(orderDetail.createdAt) }}</span>
                </div>
                <div class="time-col">
                  <label><i class="fas fa-edit"></i> 更新时间</label>
                  <span>{{ formatDate(orderDetail.updatedAt) }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-modal">
              <i class="fas fa-exclamation-triangle"></i>
              <p>无法加载订单详情</p>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-primary" @click="closeDetailModal">
              <i class="fas fa-check"></i> 关闭
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])
const allOrders = ref([])
const loading = ref(true)
const shopId = ref(null)
const showDetailModal = ref(false)
const detailLoading = ref(false)
const orderDetail = ref(null)
const searchOrderId = ref('')
const selectedStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(9)

const pendingCount = computed(() => orders.value.filter(o =>
  ['已下单', '待处理', '洗涤中', '处理中'].includes(o.status)).length)

const completedCount = computed(() => orders.value.filter(o =>
  o.status === '已完成').length)

const totalPages = computed(() => Math.ceil(orders.value.length / pageSize.value) || 1)

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return orders.value.slice(start, end)
})

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...', total)
    } else if (current >= total - 3) {
      pages.push(1, '...')
      for (let i = total - 4; i <= total; i++) pages.push(i)
    } else {
      pages.push(1, '...', current - 1, current, current + 1, '...', total)
    }
  }
  return pages
})

onMounted(async () => {
  await loadShopId()
  await loadOrders()
})

const loadShopId = async () => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    const user = JSON.parse(storedUser)
    try {
      const response = await axios.get(`/api/shops/user/${user.id}`)
      if (response.data.length > 0) {
        shopId.value = response.data[0].id
      }
    } catch (error) {
      console.error('Get shop error:', error)
    }
  }
}

const loadOrders = async () => {
  if (shopId.value) {
    try {
      const response = await axios.get(`/api/orders/shop/${shopId.value}`)
      const statusMap = {
        'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
        'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
        'processing': '处理中'
      }
      const processedOrders = response.data.map(order => ({
        ...order,
        status: statusMap[order.status.toLowerCase()] || order.status,
        shop: order.shop || { name: '未知店铺' }
      }))
      allOrders.value = processedOrders
      applyFilters(processedOrders)
    } catch (error) {
      console.error('Load orders error:', error)
      orders.value = []
    }
  } else {
    orders.value = []
  }
  loading.value = false
}

const applyFilters = (ordersData) => {
  let filtered = ordersData || allOrders.value
  if (selectedStatus.value) {
    filtered = filtered.filter(order => order.status === selectedStatus.value)
  }
  if (searchOrderId.value) {
    filtered = filtered.filter(order =>
      order.id.toString().includes(searchOrderId.value))
  }
  orders.value = filtered
  currentPage.value = 1
}

const filterByStatus = () => {
  applyFilters()
}

const viewOrderDetail = async (orderId) => {
  showDetailModal.value = true
  detailLoading.value = true

  try {
    const response = await axios.get(`/api/orders/${orderId}`)
    const statusMap = {
      'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
      'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
      'processing': '处理中'
    }

    const order = { ...response.data, status: statusMap[response.data.status.toLowerCase()] || response.data.status }

    try {
      const paymentResponse = await axios.get(`/api/payment-evidence/order/${orderId}`)
      if (paymentResponse.data && paymentResponse.data.length > 0) {
        order.paymentEvidence = paymentResponse.data[0]
      }
    } catch (e) {}

    orderDetail.value = order
  } catch (error) {
    console.error('Load order detail error:', error)
    orderDetail.value = null
  } finally {
    detailLoading.value = false
  }
}

const closeDetailModal = () => {
  showDetailModal.value = false
  orderDetail.value = null
}

const updateOrderStatus = async (order, newStatus) => {
  try {
    const statusMap = {
      '已下单': 'ordered', '处理中': 'processing',
      '已完成': 'completed', '已取消': 'canceled'
    }
    const englishStatus = statusMap[newStatus] || newStatus

    const updatedOrder = { ...order, status: englishStatus }
    await axios.put(`/api/orders/${order.id}`, updatedOrder)

    const orderProgress = {
      orderId: order.id,
      status: englishStatus,
      description: `订单状态变更为${newStatus}`
    }
    await axios.post('/api/order-progresses', orderProgress)

    await loadOrders()
  } catch (error) {
    console.error('Update order status error:', error)
    await loadOrders()
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case '已下单':
    case '待处理': return 'badge-pending'
    case '处理中':
    case '洗涤中': return 'badge-processing'
    case '已完成': return 'badge-completed'
    case '已取消': return 'badge-canceled'
    default: return ''
  }
}

const getStatusIcon = (status) => {
  switch (status) {
    case '已下单':
    case '待处理': return 'fas fa-hourglass-start'
    case '处理中':
    case '洗涤中': return 'fas fa-spinner fa-spin'
    case '已完成': return 'fas fa-check-circle'
    case '已取消': return 'fas fa-times-circle'
    default: return 'fas fa-info-circle'
  }
}

const getServiceNames = (order) => {
  if (order.orderDetails && order.orderDetails.length > 0) {
    return order.orderDetails.map(detail => {
      if (detail.service) return detail.service.name
      else if (detail.serviceName) return detail.serviceName
      else return '未知服务'
    }).join(', ')
  } else if (order.serviceName) {
    return order.serviceName
  } else if (order.service) {
    return order.service
  } else {
    return '未知服务'
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

const searchOrders = async () => {
  if (shopId.value) {
    try {
      loading.value = true
      const response = await axios.get(`/api/orders/shop/${shopId.value}`)
      const statusMap = {
        'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
        'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
        'processing': '处理中'
      }

      const processedOrders = response.data.map(order => ({
        ...order,
        status: statusMap[order.status.toLowerCase()] || order.status,
        shop: order.shop || { name: '未知店铺' }
      }))
      
      allOrders.value = processedOrders
      applyFilters(processedOrders)
    } catch (error) {
      alert('搜索失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }
}

const resetSearch = () => {
  searchOrderId.value = ''
  selectedStatus.value = ''
  loadOrders()
}
</script>

<style scoped>
.order-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  flex-wrap: wrap;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  font-size: 32px;
  color: #52c41a;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.stats-cards {
  display: flex;
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 22px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  min-width: 110px;
}

.stat-card i { font-size: 26px; }
.stat-card.total i { color: #52c41a; }
.stat-card.pending i { color: #fa8c16; }
.stat-card.completed i { color: #1890ff; }

.stat-info { display: flex; flex-direction: column; }
.stat-number { font-size: 22px; font-weight: 700; color: #333; }
.stat-label { font-size: 12px; color: #999; }

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.search-wrapper {
  flex: 1;
  position: relative;
}

.status-filter-wrapper {
  position: relative;
  min-width: 160px;
}

.filter-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  z-index: 1;
  font-size: 13px;
}

.status-filter-select {
  width: 100%;
  padding: 13px 16px 13px 38px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #fafafa;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23999' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
}

.status-filter-select:focus {
  outline: none;
  border-color: #52c41a;
  box-shadow: 0 0 0 4px rgba(82, 196, 26, 0.08);
  background-color: white;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  z-index: 1;
}

.search-wrapper input {
  width: 100%;
  padding: 13px 18px 13px 44px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.search-wrapper input:focus {
  outline: none;
  border-color: #52c41a;
  box-shadow: 0 0 0 4px rgba(82, 196, 26, 0.08);
  background: white;
}

.btn {
  padding: 12px 24px;
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
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.35);
}
.btn-primary:hover { transform: translateY(-2px); }

.btn-outline {
  background: white;
  border: 2px solid #d9d9d9;
  color: #666;
}
.btn-outline:hover { border-color: #52c41a; color: #52c41a; }

.loading-container {
  text-align: center;
  padding: 60px;
}
.loading-spinner { font-size: 48px; color: #52c41a; }
.loading-spinner p { margin-top: 16px; color: #999; }

.empty-state {
  text-align: center;
  padding: 80px 20px;
}
.empty-icon { font-size: 72px; color: #ddd; margin-bottom: 20px; }
.empty-state h3 { color: #999; font-size: 20px; margin-bottom: 8px; }
.empty-state p { color: #bbb; font-size: 15px; }

.orders-grid { display: block; }
.grid-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.order-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}
.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(82, 196, 26, 0.15);
}

.card-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  color: white;
}
.card-top-bar.status-下单,
.card-top-bar.status-处理,
.card-top-bar.status-洗涤中 {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
}
.card-top-bar.status-完成 {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
}
.card-top-bar.status-取消 {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.order-id-badge { font-weight: 600; font-size: 15px; }

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.2);
}
.badge-pending { color: #fff566; }
.badge-processing { color: #b7eb8f; }
.badge-completed { color: #91caff; }
.badge-canceled { color: #ffccc7; }

.service-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-bottom: 1px solid #f5f5f5;
  font-size: 14px;
  color: #555;
}
.service-icon { color: #fa8c16; font-size: 16px; }

.amount-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
}
.amount-label { color: #999; font-size: 14px; }
.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #52c41a;
}
.amount-value i { font-size: 16px; margin-right: 4px; }

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  color: #999;
  font-size: 13px;
  border-top: 1px solid #f5f5f5;
}

.appointment-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  color: #52c41a;
  font-size: 13px;
  background: rgba(82, 196, 26, 0.06);
  border-left: 3px solid #52c41a;
}

.appointment-info i {
  font-size: 14px;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  background: #fffbe6;
  border-top: 1px solid #ffe58f;
  font-size: 13px;
  color: #ad6800;
}
.customer-icon { color: #fa8c16; }

.card-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #f5f5f5;
}

.btn-icon {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 15px;
}
.btn-icon.view { background: #f6ffed; color: #52c41a; }
.btn-icon.view:hover { background: #52c41a; color: white; }

.status-select {
  padding: 8px 14px;
  border: 2px solid #d9d9d9;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
  color: #333;
}
.status-select:focus {
  outline: none;
  border-color: #52c41a;
  box-shadow: 0 0 0 3px rgba(82, 196, 26, 0.1);
}

.order-fade-enter-active,
.order-fade-leave-active { transition: all 0.4s ease; }
.order-fade-enter-from { opacity: 0; transform: scale(0.9); }
.order-fade-leave-to { opacity: 0; transform: translateX(-30px); }

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
  max-width: 600px;
  max-height: 85vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease;
}
@keyframes slideUp { from { transform: translateY(30px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  border-radius: 20px 20px 0 0;
}
.modal-title-area { display: flex; align-items: center; gap: 12px; }
.modal-icon { font-size: 24px; color: white; }
.modal-header h3 { margin: 0; color: white; font-size: 20px; font-weight: 600; }
.close-btn {
  background: rgba(255,255,255,0.2);
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}
.close-btn:hover { background: rgba(255,255,255,0.3); }

.modal-body { padding: 28px; }

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}
.detail-item.full-width { grid-column: span 2; }

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px;
  border-radius: 10px;
  background: #fafafa;
  border-left: 3px solid #e8e8e8;
}
.detail-item.highlight { background: #f6ffed; border-left-color: #52c41a; }
.detail-item.amount { background: #fff7e6; border-left-color: #fa8c16; }
.detail-item.status-detail { background: #e6f7ff; border-left-color: #1890ff; }

.detail-item label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}
.detail-item label i { font-size: 13px; }

.detail-item span {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}
.big-amount { font-size: 22px !important; font-weight: 700 !important; color: #ff4d4f !important; }
.status-text {
  font-weight: 600 !important;
  padding: 4px 12px;
  background: #e6f7ff;
  border-radius: 6px;
  color: #1890ff !important;
  display: inline-block;
}

.time-row { display: flex; gap: 16px; }
.time-col { flex: 1; }

.empty-modal {
  text-align: center;
  padding: 40px;
  color: #999;
}
.empty-modal i { font-size: 48px; color: #faad14; margin-bottom: 12px; display: block; }

.modal-footer {
  padding: 20px 28px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.modal-fade-enter-active,
.modal-fade-leave-active { transition: opacity 0.3s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .grid-content { grid-template-columns: 1fr; }
  .stats-cards { width: 100%; justify-content: center; }
  .search-bar { flex-wrap: wrap; }
  .detail-grid { grid-template-columns: 1fr; }
  .detail-item.full-width { grid-column: span 1; }
}

.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  margin-top: 10px;
}

.pagination-info {
  font-size: 13px;
  color: #888;
}

.pagination-info strong {
  color: #52c41a;
  font-weight: 600;
}

.pagination-controls {
  display: flex;
  gap: 6px;
  align-items: center;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  background: white;
  color: #666;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10px;
}

.page-btn:hover:not(:disabled):not(.ellipsis) {
  border-color: #52c41a;
  color: #52c41a;
  transform: translateY(-1px);
}

.page-btn.active {
  background: linear-gradient(135deg, #52c41a, #389e0d);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.35);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-btn.ellipsis {
  border: none;
  background: none;
  cursor: default;
}
</style>
