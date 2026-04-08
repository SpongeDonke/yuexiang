<template>
  <div class="home-container">
    <div class="dashboard-header">
      <h1>店家首页</h1>
      <p>欢迎回来，{{ user.name }}</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon order">
          <i class="fas fa-shopping-cart"></i>
        </div>
        <div class="stat-content">
          <h3>订单数量</h3>
          <p class="stat-value">{{ statistics.totalOrders }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon cancelled">
          <i class="fas fa-times-circle"></i>
        </div>
        <div class="stat-content">
          <h3>取消订单</h3>
          <p class="stat-value">{{ statistics.cancelledOrders }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon completed">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h3>已完成订单</h3>
          <p class="stat-value">{{ statistics.completedOrders }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon service">
          <i class="fas fa-tshirt"></i>
        </div>
        <div class="stat-content">
          <h3>洗护类型</h3>
          <p class="stat-value">{{ statistics.serviceTypes }}</p>
        </div>
      </div>
    </div>

    <div class="charts-container">
      <div class="chart-card">
        <h3>订单状态分布</h3>
        <div ref="orderStatusChart" class="chart"></div>
      </div>
      <div class="chart-card">
        <h3>洗护类型分布</h3>
        <div ref="serviceTypeChart" class="chart"></div>
      </div>
      <div class="chart-card full-width">
        <h3>订单趋势</h3>
        <div ref="orderTrendChart" class="chart"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const user = computed(() => {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : { name: '店家' }
})

const statistics = ref({
  totalOrders: 0,
  cancelledOrders: 0,
  completedOrders: 0,
  serviceTypes: 0
})

const orderStatusChart = ref(null)
const serviceTypeChart = ref(null)
const orderTrendChart = ref(null)

const orderStatusChartInstance = ref(null)
const serviceTypeChartInstance = ref(null)
const orderTrendChartInstance = ref(null)

const loadStatistics = async () => {
  try {
    // 获取店家信息
    const userResponse = await axios.get(`/api/shops/user/${user.value.id}`)
    const shop = userResponse.data[0]
    
    if (shop) {
      // 获取订单统计
      const ordersResponse = await axios.get(`/api/orders/shop/${shop.id}`)
      const orders = ordersResponse.data
      
      // 计算订单统计
      statistics.value.totalOrders = orders.length
      statistics.value.cancelledOrders = orders.filter(order => order.status === '已取消' || order.status === 'canceled').length
      statistics.value.completedOrders = orders.filter(order => order.status === '已完成' || order.status === 'completed').length
      
      // 获取洗护类型
      const servicesResponse = await axios.get(`/api/wash-services/shop/${shop.id}`)
      const services = servicesResponse.data
      
      // 计算洗护类型数量
      const serviceTypes = new Set(services.map(service => service.typeId))
      statistics.value.serviceTypes = serviceTypes.size
      
      // 更新图表
      updateOrderStatusChart(orders)
      updateServiceTypeChart(services)
      updateOrderTrendChart(orders)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const updateOrderStatusChart = (orders) => {
  if (!orderStatusChart.value) return
  
  if (orderStatusChartInstance.value) {
    orderStatusChartInstance.value.dispose()
  }
  
  orderStatusChartInstance.value = echarts.init(orderStatusChart.value)
  
  // 状态映射
  const statusMap = {
    'ordered': '已下单',
    'processing': '处理中',
    'completed': '已完成',
    'canceled': '已取消',
    'pending': '待处理',
    'washing': '洗涤中',
    'ready': '待取件'
  }
  
  // 统计订单状态
  const statusCount = {
    '已下单': 0,
    '处理中': 0,
    '已完成': 0,
    '已取消': 0
  }
  
  orders.forEach(order => {
    // 转换英文状态为中文
    const status = statusMap[order.status.toLowerCase()] || order.status
    if (statusCount.hasOwnProperty(status)) {
      statusCount[status]++
    }
  })
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: Object.keys(statusCount)
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: Object.entries(statusCount).map(([name, value]) => ({ name, value }))
      }
    ]
  }
  
  orderStatusChartInstance.value.setOption(option)
}

const updateServiceTypeChart = (services) => {
  if (!serviceTypeChart.value) return
  
  if (serviceTypeChartInstance.value) {
    serviceTypeChartInstance.value.dispose()
  }
  
  serviceTypeChartInstance.value = echarts.init(serviceTypeChart.value)
  
  // 统计服务类型
  const typeCount = {}
  
  services.forEach(service => {
    const typeName = service.typeName || (service.type ? service.type.name : '未分类')
    typeCount[typeName] = (typeCount[typeName] || 0) + 1
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Object.keys(typeCount),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '服务数量',
        type: 'bar',
        data: Object.values(typeCount),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  serviceTypeChartInstance.value.setOption(option)
}

const updateOrderTrendChart = (orders) => {
  if (!orderTrendChart.value) return
  
  if (orderTrendChartInstance.value) {
    orderTrendChartInstance.value.dispose()
  }
  
  orderTrendChartInstance.value = echarts.init(orderTrendChart.value)
  
  // 按日期分组订单
  const orderByDate = {}
  
  orders.forEach(order => {
    const date = new Date(order.createdAt).toISOString().split('T')[0]
    orderByDate[date] = (orderByDate[date] || 0) + 1
  })
  
  // 按日期排序
  const sortedDates = Object.keys(orderByDate).sort()
  const orderCounts = sortedDates.map(date => orderByDate[date])
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: sortedDates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '订单数量',
        type: 'line',
        stack: 'Total',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(24, 141, 240, 0.5)' },
            { offset: 1, color: 'rgba(24, 141, 240, 0.1)' }
          ])
        },
        emphasis: {
          focus: 'series'
        },
        data: orderCounts
      }
    ]
  }
  
  orderTrendChartInstance.value.setOption(option)
}

onMounted(() => {
  loadStatistics()
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', () => {
    orderStatusChartInstance.value?.resize()
    serviceTypeChartInstance.value?.resize()
    orderTrendChartInstance.value?.resize()
  })
})
</script>

<style scoped>
.home-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.dashboard-header p {
  font-size: 16px;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
}

.stat-icon.order {
  background-color: #e6f7ff;
  color: #1890ff;
}

.stat-icon.cancelled {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.stat-icon.completed {
  background-color: #f6ffed;
  color: #52c41a;
}

.stat-icon.service {
  background-color: #f9f0ff;
  color: #722ed1;
}

.stat-content h3 {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(480px, 1fr));
  gap: 20px;
}

.chart-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .chart-card.full-width {
    grid-column: 1;
  }
}
</style>