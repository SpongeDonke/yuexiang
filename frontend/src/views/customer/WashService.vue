<template>
  <div class="wash-service-page">
    <div class="hero-section">
      <h1 class="hero-title">洗护服务</h1>
      <p class="hero-subtitle">选择专业的洗护服务，让您的物品焕然一新</p>
    </div>
    
    <div class="content-section">
      <div v-if="loading">
        <div class="loading">加载中...</div>
      </div>
      <div v-else>
        <div class="shop-selector">
          <label class="shop-selector-label">选择店铺</label>
          <select v-model="selectedShopId" @change="loadServices" class="shop-select">
            <option value="">请选择店铺</option>
            <option v-for="shop in shops" :key="shop.id" :value="shop.id">
              {{ shop.name }}
            </option>
          </select>
        </div>
        
        <div v-if="selectedShopId" class="services-section">
          <h2 class="services-title">服务列表</h2>
          <div class="services-grid">
            <div class="service-card" v-for="service in services" :key="service.id">
              <div class="service-card-content">
                <h3 class="service-name">{{ service.name }} <span class="service-shop">{{ service.shopName || '未知商家' }}</span></h3>
                <div class="service-price">¥{{ service.price }}</div>
                <p class="service-description">{{ service.description }}</p>
                <button class="btn btn-primary" @click="orderService(service.id)">立即下单</button>
              </div>
            </div>
          </div>
          <div v-if="services.length === 0" class="no-services">
            该店铺暂无服务
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const shops = ref([])
const services = ref([])
const selectedShopId = ref('')
const loading = ref(true)

onMounted(async () => {
  await loadShops()
  // 从路由参数中获取shopId
  if (route.query.shopId) {
    selectedShopId.value = route.query.shopId
    await loadServices()
  }
})

const loadShops = async () => {
  try {
    // 调用后端API获取所有店铺
    console.log('开始加载店铺列表')
    const response = await axios.get('/api/shops')
    console.log('店铺列表响应:', response.data)
    shops.value = response.data
    console.log('店铺列表:', shops.value)
  } catch (error) {
    console.error('Load shops error:', error)
    // 模拟数据，用于测试
    shops.value = [
      {
        id: 1,
        name: '洁净洗护店'
      },
      {
        id: 2,
        name: '清新洗衣店'
      },
      {
        id: 3,
        name: '奢护之家'
      },
      {
        id: 4,
        name: '阳光洗衣店'
      },
      {
        id: 5,
        name: '洁净家园'
      },
      {
        id: 6,
        name: '时尚洗护'
      }
    ]
    console.log('使用模拟店铺数据:', shops.value)
  } finally {
    loading.value = false
    console.log('店铺加载完成，loading:', loading.value)
  }
}

const loadServices = async () => {
  if (selectedShopId.value) {
    try {
      // 调用后端API获取店铺服务
      console.log('开始加载店铺服务，shopId:', selectedShopId.value)
      const response = await axios.get(`/api/wash-services/shop/${selectedShopId.value}`)
      console.log('服务列表响应:', response.data)
      console.log('服务列表长度:', response.data.length)
      if (response.data.length > 0) {
        console.log('第一个服务:', response.data[0])
        console.log('第一个服务的shop:', response.data[0].shop)
        console.log('第一个服务的shop.user:', response.data[0].shop ? response.data[0].shop.user : 'No user')
        console.log('第一个服务的shop.user.username:', response.data[0].shop && response.data[0].shop.user ? response.data[0].shop.user.username : 'No username')
      }
      services.value = response.data
      console.log('服务列表:', services.value)
      console.log('服务列表长度:', services.value.length)
    } catch (error) {
      console.error('Load services error:', error)
      console.error('Error message:', error.message)
      console.error('Error response:', error.response)
      // 模拟数据，用于测试
      services.value = [
        {
          id: 1,
          name: '普通衣物清洗',
          price: 20.00,
          description: '普通衣物的日常清洗',
          shop: {
            user: {
              username: '测试商家'
            }
          }
        },
        {
          id: 2,
          name: '高端衣物护理',
          price: 50.00,
          description: '高端衣物的专业护理',
          shop: {
            user: {
              username: '测试商家'
            }
          }
        },
        {
          id: 3,
          name: '普通鞋类清洗',
          price: 30.00,
          description: '普通鞋子的清洗',
          shop: {
            user: {
              username: '测试商家'
            }
          }
        },
        {
          id: 4,
          name: '高端鞋类护理',
          price: 80.00,
          description: '高端鞋子的专业护理',
          shop: {
            user: {
              username: '测试商家'
            }
          }
        }
      ]
      console.log('使用模拟服务数据:', services.value)
      console.log('模拟服务数据长度:', services.value.length)
    }
  } else {
    console.log('selectedShopId为空，不加载服务')
  }
}

const orderService = (serviceId) => {
  // 跳转到下单页面
  router.push({ path: '/customer/order', query: { serviceId } })
}
</script>

<style scoped>
.wash-service-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 0;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 40px;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.hero-subtitle {
  font-size: 18px;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

.content-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.shop-selector {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
  transition: all 0.3s ease;
}

.shop-selector:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.shop-selector-label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.shop-select {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
}

.shop-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.services-section {
  margin-top: 40px;
}

.services-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.service-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
}

.service-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.16);
}

.service-card-content {
  padding: 24px;
}

.service-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.service-shop {
  font-size: 14px;
  font-weight: 400;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  padding: 4px 12px;
  border-radius: 12px;
}

.service-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
  margin-bottom: 12px;
}

.service-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.btn {
  display: inline-block;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  width: 100%;
  text-align: center;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.loading {
  text-align: center;
  padding: 60px;
  font-size: 18px;
  color: #666;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 28px;
  }
  
  .services-grid {
    grid-template-columns: 1fr;
  }
  
  .service-card {
    margin-bottom: 20px;
  }
}
</style>
