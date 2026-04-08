<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <div class="carousel" v-if="carousels.length > 0">
      <div class="carousel-container" ref="carouselContainer">
        <div class="carousel-slide" v-for="(item, index) in carousels" :key="item.id">
          <img :src="item.imageUrl" :alt="item.title" />
          <div class="carousel-content">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </div>
      <div class="carousel-indicators">
        <div 
          v-for="(item, index) in carousels" 
          :key="item.id"
          class="indicator"
          :class="{ active: currentSlide === index }"
          @click="goToSlide(index)"
        ></div>
      </div>
    </div>

    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎使用洗护平台</h1>
      <p class="welcome-subtitle">选择您需要的洗护服务，享受专业的洗护体验</p>
    </div>

    <!-- 推荐店铺 -->
    <div class="section">
      <h2 class="section-title">推荐店铺</h2>
      <div class="shops-grid">
        <div class="shop-card" v-for="item in recommendedShops" :key="item.id">
          <div class="shop-card-content">
            <h3 class="shop-name">{{ item.shop?.name || '未知店铺' }}</h3>
            <p class="shop-address">{{ item.shop?.address || '' }}</p>
            <p class="shop-phone">{{ item.shop?.phone || '' }}</p>
            <p class="shop-description">{{ item.shop?.description || '' }}</p>
            <button class="btn btn-primary" @click="viewShopServices(item.shop?.id)">查看服务</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门服务 -->
    <div class="section">
      <h2 class="section-title">热门服务</h2>
      <div class="services-grid">
        <div class="service-card" v-for="item in recommendedServices" :key="item.id">
          <div class="service-card-content">
            <h3 class="service-name">{{ item.service?.name || '未知服务' }} - {{ item.service?.shopName || '未知商家' }}</h3>
            <p class="service-price">价格: ¥{{ item.service?.price || 0 }}</p>
            <p class="service-description">{{ item.service?.description || '' }}</p>
            <button class="btn btn-primary" @click="orderService(item.service?.id)">立即下单</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const carousels = ref([])
const recommendedShops = ref([])
const recommendedServices = ref([])

// 轮播图相关
const currentSlide = ref(0)
const carouselInterval = ref(null)
const carouselContainer = ref(null)

onMounted(async () => {
  await loadCarousels()
  await loadRecommendedShops()
  await loadRecommendedServices()
  // 初始化轮播图位置
  if (carouselContainer.value) {
    carouselContainer.value.style.transform = `translateX(0)`
  }
  startCarousel()
})

onUnmounted(() => {
  stopCarousel()
})

// 加载轮播图
const loadCarousels = async () => {
  try {
    console.log('开始加载轮播图...')
    const response = await axios.get('http://localhost:8081/api/carousels/status/1')
    console.log('轮播图加载成功:', response.data)
    // 按id排序，确保轮播图顺序正确
    carousels.value = response.data.sort((a, b) => a.id - b.id)
    console.log('排序后轮播图:', carousels.value)
  } catch (error) {
    console.error('加载轮播图失败:', error)
    // 模拟数据，用于测试
    carousels.value = [
      {
        id: 1,
        imageUrl: '/home/1.png',
        title: '欢迎使用洗护平台',
        description: '专业洗护服务，品质保证'
      },
      {
        id: 2,
        imageUrl: '/home/2.jpg',
        title: '夏季特惠',
        description: '夏季洗护套餐，享受8折优惠'
      },
      {
        id: 3,
        imageUrl: '/home/3.jpg',
        title: '会员专享',
        description: '会员享受更多优惠和服务'
      }
    ]
  }
}

// 加载推荐店铺
const loadRecommendedShops = async () => {
  try {
    console.log('开始加载推荐店铺...')
    const response = await axios.get('http://localhost:8081/api/recommended-shops/status/1')
    console.log('推荐店铺加载成功:', response.data)
    recommendedShops.value = response.data
  } catch (error) {
    console.error('加载推荐店铺失败:', error)
    console.error('错误详情:', error.response || '无响应信息')
    console.error('错误状态:', error.response?.status)
    console.error('错误数据:', error.response?.data)
    // 模拟数据，用于测试
    recommendedShops.value = [
      {
        id: 1,
        shop: {
          id: 1,
          name: '洁净洗护店',
          address: '北京市朝阳区建国路88号',
          phone: '010-12345678',
          description: '专业洗护服务，品质保证'
        }
      },
      {
        id: 2,
        shop: {
          id: 2,
          name: '洁净洗护店2',
          address: '北京市朝阳区建国路89号',
          phone: '010-12345679',
          description: '专业洗护服务，品质保证2'
        }
      }
    ]
  }
}

// 加载推荐服务
const loadRecommendedServices = async () => {
  try {
    console.log('开始加载推荐服务...')
    const response = await axios.get('http://localhost:8081/api/recommended-services/status/1')
    console.log('推荐服务加载成功:', response.data)
    recommendedServices.value = response.data
  } catch (error) {
    console.error('加载推荐服务失败:', error)
    console.error('错误详情:', error.response || '无响应信息')
    console.error('错误状态:', error.response?.status)
    console.error('错误数据:', error.response?.data)
    // 模拟数据，用于测试
    recommendedServices.value = [
      {
        id: 1,
        service: {
          id: 1,
          name: '普通衣物清洗',
          price: 20.00,
          description: '普通衣物的日常清洗',
          shop: {
            name: '测试商家'
          }
        }
      },
      {
        id: 2,
        service: {
          id: 2,
          name: '高端衣物护理',
          price: 50.00,
          description: '高端衣物的专业护理',
          shop: {
            name: '测试商家'
          }
        }
      },
      {
        id: 3,
        service: {
          id: 3,
          name: '普通鞋类清洗',
          price: 30.00,
          description: '普通鞋子的清洗',
          shop: {
            name: '测试商家'
          }
        }
      }
    ]
  }
}

const viewShopServices = (shopId) => {
  if (shopId) {
    // 跳转到店铺服务页面
    router.push({ path: '/customer/wash-service', query: { shopId } })
  }
}

const orderService = (serviceId) => {
  if (serviceId) {
    // 跳转到下单页面
    router.push({ path: '/customer/order', query: { serviceId } })
  }
}

// 轮播图方法
const startCarousel = () => {
  if (carousels.value.length > 0) {
    carouselInterval.value = setInterval(() => {
      goToSlide((currentSlide.value + 1) % carousels.value.length)
    }, 3000)
  }
}

const stopCarousel = () => {
  if (carouselInterval.value) {
    clearInterval(carouselInterval.value)
    carouselInterval.value = null
  }
}

const goToSlide = (index) => {
  currentSlide.value = index
  if (carouselContainer.value) {
    carouselContainer.value.style.transform = `translateX(-${index * 100}%)`
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40px;
}

/* 轮播图样式 */
.carousel {
  position: relative;
  width: 100%;
  height: 500px;
  overflow: hidden;
  margin-bottom: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.carousel-container {
  display: flex;
  transition: transform 0.5s ease;
  height: 100%;
  width: 100%;
}

.carousel-slide {
  position: relative;
  width: 100%;
  height: 100%;
  flex-shrink: 0;
  min-width: 100%;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.7), rgba(0,0,0,0));
  color: white;
  padding: 40px;
}

.carousel-content h3 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.carousel-content p {
  font-size: 16px;
  margin: 0;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.indicator.active {
  background-color: #1890ff;
  width: 30px;
  border-radius: 6px;
}

/* 欢迎信息样式 */
.welcome-section {
  text-align: center;
  padding: 40px 20px;
  background-color: white;
  margin: 0 20px 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-title {
  font-size: 36px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.welcome-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

/* 通用部分样式 */
.section {
  padding: 0 20px;
  margin-bottom: 30px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

/* 店铺网格样式 */
.shops-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.shop-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.shop-card-content {
  padding: 24px;
}

.shop-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.shop-address {
  color: #666;
  margin-bottom: 8px;
}

.shop-phone {
  color: #666;
  margin-bottom: 8px;
}

.shop-description {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

/* 服务网格样式 */
.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.service-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.service-card-content {
  padding: 24px;
}

.service-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.service-price {
  color: #1890ff;
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.service-description {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.5;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
}

.btn-primary:hover {
  background-color: #40a9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel {
    height: 300px;
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .welcome-subtitle {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .shops-grid,
  .services-grid {
    grid-template-columns: 1fr;
  }
}
</style>
