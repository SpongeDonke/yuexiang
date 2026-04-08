<template>
  <div class="card">
    <h2 class="page-title">订单进度管理</h2>
    <div v-if="loading">
      <div class="loading">加载中...</div>
    </div>
    <div v-else>
      <div class="form-group">
        <label>选择订单</label>
        <select v-model="selectedOrderId" @change="loadOrderProgress">
          <option value="">请选择订单</option>
          <option v-for="order in orders" :key="order.id" :value="order.id">
            #{{ order.id }} - {{ order.status }} - {{ order.shopName || '未知店铺' }} - {{ getServiceNames(order) }} - {{ formatDateShort(order.createdAt) }}
          </option>
        </select>
      </div>
      
      <div v-if="selectedOrderId">
        <h3 class="section-title">订单进度记录</h3>
        <div v-if="orderProgresses.length === 0">
          <p>暂无进度记录</p>
        </div>
        <div v-else>
          <div class="list-item" v-for="progress in orderProgresses" :key="progress.id">
            <h4>{{ progress.status }}</h4>
            <p>{{ progress.description }}</p>
            <p style="font-size: 12px; color: #999;">{{ formatDate(progress.createdAt) }}</p>
          </div>
        </div>
        
        <h3 class="section-title" style="margin-top: 30px;">添加进度</h3>
        <div class="form-group">
          <label>进度状态</label>
          <input type="text" v-model="newProgress.status" />
        </div>
        <div class="form-group">
          <label>进度描述</label>
          <textarea v-model="newProgress.description" rows="3"></textarea>
        </div>
        <button class="btn btn-primary" @click="addProgress">添加进度</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const orders = ref([])
const orderProgresses = ref([])
const selectedOrderId = ref('')
const newProgress = ref({ status: '', description: '' })
const loading = ref(true)
const shopId = ref(null)

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
      // 将后端返回的英文状态映射为中文状态
      const statusMap = {
        'pending': '待处理',
        'washing': '洗涤中',
        'ready': '待取件',
        'completed': '已完成',
        'canceled': '已取消',
        'ordered': '已下单',
        'processing': '处理中'
      }
      orders.value = response.data.map(order => ({
        ...order,
        status: statusMap[order.status.toLowerCase()] || order.status
      }))
    } catch (error) {
      console.error('Load orders error:', error)
    }
  }
  loading.value = false
}

const loadOrderProgress = async () => {
  if (selectedOrderId.value) {
    try {
      const response = await axios.get(`/api/order-progresses/order/${selectedOrderId.value}`)
      // 将后端返回的英文状态映射为中文状态
      const statusMap = {
        'pending': '待处理',
        'washing': '洗涤中',
        'ready': '待取件',
        'completed': '已完成',
        'canceled': '已取消',
        'ordered': '已下单',
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
  if (selectedOrderId.value) {
    try {
      const progress = {
        orderId: selectedOrderId.value,
        status: newProgress.value.status,
        description: newProgress.value.description
      }
      await axios.post('/api/order-progresses', progress)
      newProgress.value = { status: '', description: '' }
      await loadOrderProgress()
    } catch (error) {
      console.error('Add progress error:', error)
    }
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
