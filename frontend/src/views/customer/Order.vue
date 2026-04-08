<template>
  <div class="order-container">
    <!-- 订单表单 -->
    <transition name="slide-fade">
      <div v-if="showOrderForm" class="form-card">
        <div class="card-header">
          <i class="fas fa-file-alt header-icon"></i>
          <h2 class="header-title">填写订单信息</h2>
        </div>

        <div class="service-preview">
          <div class="preview-item">
            <i class="fas fa-concierge-bell"></i>
            <span>{{ orderForm.serviceName }}</span>
          </div>
          <div class="price-tag">
            <i class="fas fa-yen-sign"></i>
            {{ orderForm.price }}
          </div>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label><i class="fas fa-user"></i> 联系人</label>
            <input type="text" v-model="orderForm.contactName" @input="validateContactName"
                   :class="{ 'error': errors.contactName }" placeholder="请输入联系人姓名" />
            <div v-if="errors.contactName" class="error-msg">
              <i class="fas fa-exclamation-circle"></i> {{ errors.contactName }}
            </div>
          </div>

          <div class="form-group">
            <label><i class="fas fa-phone-alt"></i> 联系电话</label>
            <input type="text" v-model="orderForm.contactPhone" @input="validateContactPhone"
                   :class="{ 'error': errors.contactPhone }" placeholder="请输入手机号码" />
            <div v-if="errors.contactPhone" class="error-msg">
              <i class="fas fa-exclamation-circle"></i> {{ errors.contactPhone }}
            </div>
          </div>

          <div class="form-group full-width">
            <label><i class="fas fa-map-marker-alt"></i> 地址</label>
            <input type="text" v-model="orderForm.address" @input="validateAddress"
                   :class="{ 'error': errors.address }" placeholder="请输入详细地址" />
            <div v-if="errors.address" class="error-msg">
              <i class="fas fa-exclamation-circle"></i> {{ errors.address }}
            </div>
          </div>

          <div class="form-group full-width">
            <label><i class="fas fa-clock"></i> 预约时间</label>
            <input type="datetime-local" v-model="orderForm.appointmentTime" class="time-picker" />
          </div>

          <div class="form-group full-width">
            <label><i class="fas fa-sticky-note"></i> 备注</label>
            <textarea v-model="orderForm.remark" rows="3" @input="validateRemark"
                      :class="{ 'error': errors.remark }" placeholder="特殊要求或备注（选填）"></textarea>
            <div v-if="errors.remark" class="error-msg">
              <i class="fas fa-exclamation-circle"></i> {{ errors.remark }}
            </div>
          </div>

          <div class="form-group full-width">
            <label><i class="fas fa-image"></i> 支付凭证</label>
            <div class="file-upload">
              <input type="file" id="fileInput" @change="handleFileChange" accept="image/*" hidden />
              <label for="fileInput" class="file-label">
                <i class="fas fa-cloud-upload-alt"></i>
                <span v-if="!selectedFile">点击上传凭证</span>
                <span v-else class="selected-file">
                  <i class="fas fa-check-circle"></i> {{ selectedFile.name }}
                </span>
              </label>
            </div>
          </div>
        </div>

        <div class="action-buttons">
          <button class="btn btn-primary btn-lg" @click="submitOrder">
            <i class="fas fa-paper-plane"></i> 提交订单
          </button>
          <button class="btn btn-secondary btn-lg" @click="cancelOrderForm">
            <i class="fas fa-times"></i> 取消
          </button>
        </div>
      </div>
    </transition>

    <!-- 订单列表 -->
    <transition name="fade">
      <div v-if="!showOrderForm" class="list-container">
        <div class="page-header">
          <div class="header-left">
            <i class="fas fa-shopping-cart header-icon"></i>
            <h1 class="page-title">我的订单</h1>
          </div>
          <div class="stats-cards">
            <div class="stat-card total">
              <i class="fas fa-list-ul"></i>
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
              <i class="fas fa-check-circle"></i>
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
          <p>快去选择服务下单吧！</p>
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

              <div class="shop-info">
                <i class="fas fa-store shop-icon"></i>
                <span class="shop-name">{{ order.shopName || '未知店铺' }}</span>
              </div>

              <div class="service-name">
                <i class="fas fa-concierge-bell service-icon"></i>
                <span>{{ order.serviceName || '未知服务' }}</span>
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

              <div class="card-actions">
                <button class="btn-icon view" @click="viewOrderDetail(order.id)" title="查看详情">
                  <i class="fas fa-eye"></i>
                </button>
                <button class="btn-icon progress" @click="viewOrderProgress(order.id)" title="查看进度">
                  <i class="fas fa-tasks"></i>
                </button>
                <button v-if="order.status === '已完成'" class="btn-icon comment" @click="addComment(order.id)" title="评价">
                  <i class="fas fa-star"></i>
                </button>
                <button v-if="canCancel(order.status)" class="btn-icon cancel" @click="cancelOrder(order.id)" title="取消订单">
                  <i class="fas fa-ban"></i>
                </button>
                <button v-if="order.status === '已取消'" class="btn-icon delete" @click="deleteOrder(order.id)" title="删除订单">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
          </transition-group>
        </div>

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
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const allOrders = ref([])
const loading = ref(true)
const showOrderForm = ref(false)
const currentPage = ref(1)
const pageSize = ref(9)
const orderForm = ref({
  serviceId: '',
  serviceName: '',
  price: '',
  shopId: '',
  contactName: '',
  contactPhone: '',
  address: '',
  remark: '',
  appointmentTime: ''
})
const selectedFile = ref(null)
const searchOrderId = ref('')
const selectedStatus = ref('')
const errorMessage = ref('')
const errors = ref({
  contactName: '',
  contactPhone: '',
  address: '',
  remark: ''
})

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
  if (route.query.serviceId) {
    await loadServiceInfo(route.query.serviceId)
    showOrderForm.value = true
  } else {
    await loadOrders()
  }
})

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0]
}

const loadServiceInfo = async (serviceId) => {
  try {
    const response = await axios.get(`/api/wash-services/${serviceId}`)
    const service = response.data
    orderForm.value.serviceId = service.id
    orderForm.value.serviceName = service.name
    orderForm.value.price = service.price
    orderForm.value.shopId = service.shopId
  } catch (error) {
    console.error('Load service info error:', error)
    orderForm.value.serviceId = serviceId
    orderForm.value.serviceName = '普通衣物清洗'
    orderForm.value.price = 20.00
    orderForm.value.shopId = 1
    alert('获取服务信息失败，使用默认服务信息')
  } finally {
    loading.value = false
  }
}

const loadOrders = async () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      const response = await axios.get(`/api/orders/customer/${user.id}`)
      const statusMap = {
        'pending': '待处理',
        'washing': '洗涤中',
        'ready': '待取件',
        'completed': '已完成',
        'canceled': '已取消',
        'ordered': '已下单',
        'processing': '处理中'
      }

      const ordersWithServiceName = await Promise.all(
        response.data.map(async order => {
          let serviceName = '未知服务';
          try {
            const orderDetailsResponse = await axios.get(`/api/order-details/order/${order.id}`);
            if (orderDetailsResponse.data && orderDetailsResponse.data.length > 0) {
              const orderDetail = orderDetailsResponse.data[0];
              if (orderDetail.serviceId) {
                const serviceResponse = await axios.get(`/api/wash-services/${orderDetail.serviceId}`);
                if (serviceResponse.data) {
                  serviceName = serviceResponse.data.name || '未知服务';
                }
              }
            }
          } catch (error) {
            console.error('获取服务名称失败:', error);
          }
          return {
            ...order,
            status: statusMap[order.status.toLowerCase()] || order.status,
            shopName: order.shopName || (order.shop ? order.shop.name : '未知店铺'),
            serviceName: serviceName
          };
        })
      );
      allOrders.value = ordersWithServiceName
      applyFilters(ordersWithServiceName)
    } else {
      orders.value = []
      allOrders.value = []
    }
  } catch (error) {
    console.error('Load orders error:', error)
    orders.value = []
    allOrders.value = []
  } finally {
    loading.value = false
  }
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

const cancelOrderForm = async () => {
  showOrderForm.value = false
  orderForm.value = {
    serviceId: '', serviceName: '', price: '', shopId: '',
    contactName: '', contactPhone: '', address: '', remark: '',
    appointmentTime: ''
  }
  selectedFile.value = null
  errorMessage.value = ''
  errors.value = {
    contactName: '',
    contactPhone: '',
    address: '',
    remark: ''
  }
  await loadOrders()
}

const submitOrder = async () => {
  if (!validateOrderForm()) return

  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      const statusMap = {
        '待处理': 'pending',
        '洗涤中': 'washing',
        '待取件': 'ready',
        '已完成': 'completed',
        '已取消': 'canceled',
        '已下单': '已下单'
      }

      const order = {
        customerId: user.id,
        shopId: orderForm.value.shopId,
        totalAmount: parseFloat(orderForm.value.price),
        status: statusMap['已下单'],
        contactName: orderForm.value.contactName,
        contactPhone: orderForm.value.contactPhone,
        address: orderForm.value.address,
        remark: orderForm.value.remark,
        appointmentTime: orderForm.value.appointmentTime || null
      }

      const response = await axios.post('/api/orders', order)
      const newOrder = response.data

      const orderProgress = {
        orderId: newOrder.id,
        status: statusMap['已下单'],
        description: '订单已创建'
      }
      await axios.post('/api/order-progresses', orderProgress)

      const orderDetail = {
        orderId: newOrder.id,
        serviceId: orderForm.value.serviceId,
        quantity: 1,
        price: parseFloat(orderForm.value.price)
      }
      await axios.post('/api/order-details', orderDetail)

      if (selectedFile.value) {
        const formData = new FormData()
        formData.append('orderId', newOrder.id)
        formData.append('userId', user.id)
        formData.append('file', selectedFile.value)
        try {
          await axios.post('/api/payment-evidence/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
          })
          alert('订单提交成功，支付凭证上传成功！')
        } catch (uploadError) {
          alert(`订单提交成功，但支付凭证上传失败`)
        }
      } else {
        alert('订单提交成功！')
      }

      selectedFile.value = null
      orderForm.value = {
        serviceId: '', serviceName: '', price: '', shopId: '',
        contactName: '', contactPhone: '', address: '', remark: '',
        appointmentTime: ''
      }
      showOrderForm.value = false
      await loadOrders()
    } else {
      alert('请先登录')
    }
  } catch (error) {
    console.error('Submit order error:', error)
    alert('订单提交失败，请稍后重试')
  }
}

const viewOrderDetail = async (orderId) => {
  try {
    const response = await axios.get(`/api/orders/${orderId}`)
    const order = response.data || {}
    const statusMap = {
      'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
      'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单', 'processing': '处理中'
    }
    const status = statusMap[order.status] || order.status || '未知状态'

    let paymentEvidenceInfo = '无'
    try {
      const paymentResponse = await axios.get(`/api/payment-evidence/order/${orderId}`)
      if (paymentResponse.data && paymentResponse.data.length > 0) {
        const paymentEvidence = paymentResponse.data[0]
        paymentEvidenceInfo = `${paymentEvidence.fileName}\n文件大小: ${paymentEvidence.fileSize} bytes`
      }
    } catch (e) {}

    let serviceName = '未知服务';
    try {
      const orderDetailsResponse = await axios.get(`/api/order-details/order/${order.id}`);
      if (orderDetailsResponse.data && orderDetailsResponse.data.length > 0) {
        const orderDetail = orderDetailsResponse.data[0];
        if (orderDetail.serviceId) {
          const serviceResponse = await axios.get(`/api/wash-services/${orderDetail.serviceId}`);
          if (serviceResponse.data) {
            serviceName = serviceResponse.data.name || '未知服务';
          }
        }
      }
    } catch (e) {}

    alert(`订单ID: ${order.id || '未知'}\n店铺: ${order.shopName || '未知店铺'}\n服务名称: ${serviceName}\n总金额: ¥${order.totalAmount || 0}\n状态: ${status}\n联系人: ${order.contactName || '无'}\n联系电话: ${order.contactPhone || '无'}\n地址: ${order.address || '无'}\n备注: ${order.remark || '无'}\n创建时间: ${order.createdAt ? formatDate(order.createdAt) : '未知'}`)
  } catch (error) {
    alert('获取订单详情失败，请稍后重试')
  }
}

const addComment = (orderId) => {
  router.push({ path: '/customer/comment', query: { orderId } })
}

const viewOrderProgress = (orderId) => {
  router.push({ path: '/customer/order-progress', query: { orderId } })
}

const cancelOrder = async (orderId) => {
  if (confirm('确定要取消该订单吗？')) {
    try {
      const statusMap = {
        '待处理': 'pending', '洗涤中': 'washing', '待取件': 'ready',
        '已完成': 'completed', '已取消': 'canceled', '已下单': '已下单'
      }
      const orderResponse = await axios.get(`/api/orders/${orderId}`)
      const order = orderResponse.data
      const updatedOrder = { ...order, status: statusMap['已取消'] }
      await axios.put(`/api/orders/${orderId}`, updatedOrder)

      const orderProgress = { orderId, status: statusMap['已取消'], description: '订单已取消' }
      await axios.post('/api/order-progresses', orderProgress)

      alert('订单取消成功！')
      await loadOrders()
    } catch (error) {
      alert('订单取消失败: ' + error.message)
    }
  }
}

const deleteOrder = async (orderId) => {
  if (confirm('确定要删除该订单吗？')) {
    try {
      await axios.delete(`/api/orders/${orderId}`)
      alert('订单删除成功！')
      await loadOrders()
    } catch (error) {
      alert('订单删除失败: ' + error.message)
    }
  }
}

const canCancel = (status) => {
  return ['已下单', '待处理', '洗涤中', '处理中'].includes(status)
}

const getStatusClass = (status) => {
  switch (status) {
    case '待处理':
    case '已下单':
      return 'badge-pending'
    case '洗涤中':
    case '处理中':
      return 'badge-processing'
    case '待取件':
      return 'badge-ready'
    case '已完成':
      return 'badge-completed'
    case '已取消':
      return 'badge-canceled'
    default:
      return ''
  }
}

const getStatusIcon = (status) => {
  switch (status) {
    case '待处理':
    case '已下单':
      return 'fas fa-hourglass-start'
    case '洗涤中':
    case '处理中':
      return 'fas fa-spinner fa-spin'
    case '待取件':
      return 'fas fa-box-open'
    case '已完成':
      return 'fas fa-check-circle'
    case '已取消':
      return 'fas fa-times-circle'
    default:
      return 'fas fa-info-circle'
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
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      loading.value = true
      const response = await axios.get(`/api/orders/customer/${user.id}`)
      const statusMap = {
        'pending': '待处理', 'washing': '洗涤中', 'ready': '待取件',
        'completed': '已完成', 'canceled': '已取消', 'ordered': '已下单',
        'processing': '处理中'
      }

      const processedOrders = await Promise.all(
        response.data.map(async order => {
          let serviceName = '未知服务';
          try {
            const orderDetailsResponse = await axios.get(`/api/order-details/order/${order.id}`);
            if (orderDetailsResponse.data && orderDetailsResponse.data.length > 0) {
              const orderDetail = orderDetailsResponse.data[0];
              if (orderDetail.serviceId) {
                const serviceResponse = await axios.get(`/api/wash-services/${orderDetail.serviceId}`);
                if (serviceResponse.data) {
                  serviceName = serviceResponse.data.name || '未知服务';
                }
              }
            }
          } catch (e) {}
          return {
            ...order,
            status: statusMap[order.status.toLowerCase()] || order.status,
            shopName: order.shopName || (order.shop ? order.shop.name : '未知店铺'),
            serviceName: serviceName
          };
        })
      )
      
      allOrders.value = processedOrders
      applyFilters(processedOrders)
    }
  } catch (error) {
    alert('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchOrderId.value = ''
  selectedStatus.value = ''
  loadOrders()
}

const validateContactName = () => {
  const value = orderForm.value.contactName
  if (!value || value.trim() === '') {
    errors.value.contactName = '联系人不能为空'; return
  }
  const nameRegex = /^[\u4e00-\u9fa5a-zA-Z\s]+$/
  if (!nameRegex.test(value.trim())) {
    errors.value.contactName = '联系人只能包含中文、英文和空格'; return
  }
  if (value.trim().length < 2 || value.trim().length > 20) {
    errors.value.contactName = '联系人姓名长度应在2-20个字符之间'; return
  }
  errors.value.contactName = ''
}

const validateContactPhone = () => {
  const value = orderForm.value.contactPhone
  if (!value || value.trim() === '') {
    errors.value.contactPhone = '联系电话不能为空'; return
  }
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(value.trim())) {
    errors.value.contactPhone = '联系电话应为11位的中国手机号'; return
  }
  errors.value.contactPhone = ''
}

const validateAddress = () => {
  const value = orderForm.value.address
  if (!value || value.trim() === '') {
    errors.value.address = '地址不能为空'; return
  }
  const addressRegex = /^[\u4e00-\u9fa5a-zA-Z0-9\s，。！？、,.?!]+$/
  if (!addressRegex.test(value.trim())) {
    errors.value.address = '地址只能包含中文、英文、数字和常见标点符号'; return
  }
  if (value.trim().length < 5 || value.trim().length > 100) {
    errors.value.address = '地址长度应在5-100个字符之间'; return
  }
  errors.value.address = ''
}

const validateRemark = () => {
  const value = orderForm.value.remark
  if (!value || value.trim() === '') {
    errors.value.remark = ''; return
  }
  const remarkRegex = /^[\u4e00-\u9fa5a-zA-Z0-9\s，。！？、,.?!]+$/
  if (!remarkRegex.test(value.trim())) {
    errors.value.remark = '备注只能包含中文、英文、数字和常见标点符号'; return
  }
  if (value.trim().length > 200) {
    errors.value.remark = '备注长度不能超过200个字符'; return
  }
  errors.value.remark = ''
}

const validateOrderForm = () => {
  validateContactName(); validateContactPhone(); validateAddress(); validateRemark()
  return !(errors.value.contactName || errors.value.contactPhone || errors.value.address || errors.value.remark)
}
</script>

<style scoped>
.order-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.form-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.25);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}

.header-icon {
  font-size: 28px;
  color: white;
  animation: bounce 2s infinite;
}

.header-title {
  color: white;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.service-preview {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.preview-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 18px;
  font-weight: 500;
}

.preview-item i {
  font-size: 22px;
}

.price-tag {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  font-size: 22px;
  font-weight: 700;
  padding: 10px 24px;
  border-radius: 30px;
  box-shadow: 0 4px 15px rgba(245, 87, 108, 0.4);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-group.full-width {
  grid-column: span 2;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.95);
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.form-group label i {
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.95);
  font-size: 15px;
  transition: all 0.3s ease;
  color: #333;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #fff;
  box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.2);
}

.form-group input.error,
.form-group textarea.error {
  border-color: #ff6b6b;
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-8px); }
  75% { transform: translateX(8px); }
}

.error-msg {
  margin-top: 6px;
  padding: 8px 12px;
  background: rgba(255, 107, 107, 0.2);
  border-radius: 6px;
  color: #fff;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.file-upload {
  width: 100%;
}

.file-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 40px;
  border: 2px dashed rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
}

.file-label:hover {
  border-color: rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.1);
}

.selected-file {
  color: #51cf66 !important;
  font-weight: 500;
}

.time-picker {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  font-size: 15px;
  color: white;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  font-family: inherit;
}

.time-picker:focus {
  outline: none;
  border-color: rgba(102, 126, 234, 0.6);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
  background: rgba(255, 255, 255, 0.15);
}

.time-picker::-webkit-calendar-picker-indicator {
  filter: invert(1);
  cursor: pointer;
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-top: 28px;
  justify-content: center;
}

.btn {
  padding: 12px 28px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(56, 239, 125, 0.35);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(56, 239, 125, 0.45);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

.btn-lg {
  padding: 14px 36px;
  font-size: 16px;
}

.list-container {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
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

.page-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.stat-card i {
  font-size: 26px;
}

.stat-card.total i { color: #667eea; }
.stat-card.pending i { color: #ffa940; }
.stat-card.completed i { color: #52c41a; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 22px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

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
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.08);
  background-color: white;
}

.search-input {
  width: 100% !important;
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
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.08);
  background: white;
}

.btn-outline {
  background: white;
  border: 2px solid #d9d9d9;
  color: #666;
}

.btn-outline:hover {
  border-color: #667eea;
  color: #667eea;
}

.loading-container {
  text-align: center;
  padding: 60px;
}

.loading-spinner {
  font-size: 48px;
  color: #667eea;
}

.loading-spinner p {
  margin-top: 16px;
  color: #999;
  font-size: 16px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 72px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #999;
  font-size: 20px;
  margin-bottom: 8px;
}

.empty-state p {
  color: #bbb;
  font-size: 15px;
}

.orders-grid {
  display: block;
}

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
  position: relative;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-top-bar.status-完成 {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.card-top-bar.status-取消 {
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
}

.order-id-badge {
  font-weight: 600;
  font-size: 15px;
}

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

.badge-pending { color: #ffd666; }
.badge-processing { color: #91caff; }
.badge-ready { color: #b7eb8f; }
.badge-completed { color: #b7eb8f; }
.badge-canceled { color: #ffccc7; }

.shop-info,
.service-name {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-bottom: 1px solid #f5f5f5;
  font-size: 14px;
  color: #555;
}

.shop-icon { color: #1890ff; font-size: 16px; }
.service-icon { color: #fa8c16; font-size: 16px; }
.shop-name { font-weight: 500; }

.amount-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
}

.amount-label {
  color: #999;
  font-size: 14px;
}

.amount-value {
  font-size: 24px;
  font-weight: 700;
  color: #ff4d4f;
}

.amount-value i {
  font-size: 16px;
  margin-right: 4px;
}

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
  color: #667eea;
  font-size: 13px;
  background: rgba(102, 126, 234, 0.06);
  border-left: 3px solid #667eea;
}

.appointment-info i {
  font-size: 14px;
}

.card-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
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

.btn-icon.view { background: #e6f7ff; color: #1890ff; }
.btn-icon.view:hover { background: #1890ff; color: white; }

.btn-icon.progress { background: #f0f5ff; color: #597ef7; }
.btn-icon.progress:hover { background: #597ef7; color: white; }

.btn-icon.comment { background: #fffbe6; color: #faad14; }
.btn-icon.comment:hover { background: #faad14; color: white; }

.btn-icon.cancel { background: #fff1f0; color: #ff4d4f; }
.btn-icon.cancel:hover { background: #ff4d4f; color: white; }

.btn-icon.delete { background: #f5f5f5; color: #999; }
.btn-icon.delete:hover { background: #999; color: white; }

.order-fade-enter-active,
.order-fade-leave-active {
  transition: all 0.4s ease;
}
.order-fade-enter-from {
  opacity: 0;
  transform: scale(0.9);
}
.order-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.4s ease;
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .form-group.full-width {
    grid-column: span 1;
  }
  .grid-content {
    grid-template-columns: 1fr;
  }
  .stats-cards {
    width: 100%;
    justify-content: center;
  }
  .search-bar {
    flex-wrap: wrap;
  }
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
  color: #667eea;
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
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-1px);
}

.page-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
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
