<template>
  <div class="home-container">
    <div class="dashboard-header">
      <h1>管理员首页</h1>
      <p>欢迎回来，{{ user.name }}</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon customer">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <h3>顾客数量</h3>
          <p class="stat-value">{{ statistics.customerCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon shopkeeper">
          <i class="fas fa-store"></i>
        </div>
        <div class="stat-content">
          <h3>店家数量</h3>
          <p class="stat-value">{{ statistics.shopkeeperCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon approved">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h3>通过审核</h3>
          <p class="stat-value">{{ statistics.approvedCount }}</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending">
          <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
          <h3>未通过审核</h3>
          <p class="stat-value">{{ statistics.pendingCount }}</p>
        </div>
      </div>
    </div>

    <div class="charts-container">
      <div class="chart-card">
        <h3>用户类型分布</h3>
        <div ref="userTypeChart" class="chart"></div>
      </div>
      <div class="chart-card">
        <h3>店家审核状态</h3>
        <div ref="shopStatusChart" class="chart"></div>
      </div>
      <div class="chart-card full-width">
        <h3>注册用户趋势</h3>
        <div ref="userTrendChart" class="chart"></div>
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
  return userStr ? JSON.parse(userStr) : { name: '管理员' }
})

const statistics = ref({
  customerCount: 0,
  shopkeeperCount: 0,
  approvedCount: 0,
  pendingCount: 0
})

const userTypeChart = ref(null)
const shopStatusChart = ref(null)
const userTrendChart = ref(null)

const userTypeChartInstance = ref(null)
const shopStatusChartInstance = ref(null)
const userTrendChartInstance = ref(null)

const loadStatistics = async () => {
  try {
    // 获取所有用户
    const usersResponse = await axios.get('/api/users')
    const users = usersResponse.data
    
    // 计算用户统计
    statistics.value.customerCount = users.filter(user => user.roleId === 3).length
    statistics.value.shopkeeperCount = users.filter(user => user.roleId === 1).length
    statistics.value.approvedCount = users.filter(user => user.roleId === 1 && user.status === 'active').length
    statistics.value.pendingCount = users.filter(user => user.roleId === 1 && user.status !== 'active').length
    
    // 更新图表
    updateUserTypeChart(users)
    updateShopStatusChart(users)
    updateUserTrendChart(users)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const updateUserTypeChart = (users) => {
  if (!userTypeChart.value) return
  
  if (userTypeChartInstance.value) {
    userTypeChartInstance.value.dispose()
  }
  
  userTypeChartInstance.value = echarts.init(userTypeChart.value)
  
  // 统计用户类型
  const userTypeCount = {
    '顾客': users.filter(user => user.roleId === 3).length,
    '店家': users.filter(user => user.roleId === 1).length,
    '管理员': users.filter(user => user.roleId === 2).length
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10,
      data: Object.keys(userTypeCount)
    },
    series: [
      {
        name: '用户类型',
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
        data: Object.entries(userTypeCount).map(([name, value]) => ({ name, value }))
      }
    ]
  }
  
  userTypeChartInstance.value.setOption(option)
}

const updateShopStatusChart = (users) => {
  if (!shopStatusChart.value) return
  
  if (shopStatusChartInstance.value) {
    shopStatusChartInstance.value.dispose()
  }
  
  shopStatusChartInstance.value = echarts.init(shopStatusChart.value)
  
  // 统计店家状态
  const shopStatusCount = {
    '通过审核': users.filter(user => user.roleId === 1 && user.status === 'active').length,
    '未通过审核': users.filter(user => user.roleId === 1 && user.status !== 'active').length
  }
  
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
      data: Object.keys(shopStatusCount)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '店家数量',
        type: 'bar',
        data: Object.values(shopStatusCount),
        itemStyle: {
          color: function(params) {
            const colors = ['#52c41a', '#faad14']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }
  
  shopStatusChartInstance.value.setOption(option)
}

const updateUserTrendChart = (users) => {
  if (!userTrendChart.value) return
  
  if (userTrendChartInstance.value) {
    userTrendChartInstance.value.dispose()
  }
  
  userTrendChartInstance.value = echarts.init(userTrendChart.value)
  
  // 按日期分组用户
  const userByDate = {}
  
  users.forEach(user => {
    if (user.createdAt) {
      const date = new Date(user.createdAt).toISOString().split('T')[0]
      userByDate[date] = (userByDate[date] || 0) + 1
    }
  })
  
  // 按日期排序
  const sortedDates = Object.keys(userByDate).sort()
  const userCounts = sortedDates.map(date => userByDate[date])
  
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
        name: '注册用户数量',
        type: 'line',
        stack: 'Total',
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 99, 132, 0.5)' },
            { offset: 1, color: 'rgba(255, 99, 132, 0.1)' }
          ])
        },
        emphasis: {
          focus: 'series'
        },
        data: userCounts
      }
    ]
  }
  
  userTrendChartInstance.value.setOption(option)
}

onMounted(() => {
  loadStatistics()
  
  // 监听窗口大小变化，调整图表大小
  window.addEventListener('resize', () => {
    userTypeChartInstance.value?.resize()
    shopStatusChartInstance.value?.resize()
    userTrendChartInstance.value?.resize()
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

.stat-icon.customer {
  background-color: #e6f7ff;
  color: #1890ff;
}

.stat-icon.shopkeeper {
  background-color: #f6ffed;
  color: #52c41a;
}

.stat-icon.approved {
  background-color: #f6ffed;
  color: #52c41a;
}

.stat-icon.pending {
  background-color: #fff7e6;
  color: #faad14;
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