<template>
  <div class="customer-homepage">
    <el-card class="card-container">
      <template #header>
        <div class="card-header">
          <h2>顾客首页管理</h2>
          <p>管理顾客首页的轮播图、推荐店铺和推荐服务</p>
        </div>
      </template>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="tabs-container">
        <!-- 轮播图管理 -->
        <el-tab-pane label="轮播图管理" name="carousel">
          <div class="carousel-section">
            <el-button type="primary" @click="openCarouselDialog" class="add-button">
              <el-icon><Plus /></el-icon>
              添加轮播图
            </el-button>
            <p class="carousel-info">最多可添加5张轮播图</p>
            
            <div class="carousel-list">
              <template v-for="(item, index) in carousels" :key="item.id">
                <el-card 
                  v-if="item"
                  class="carousel-card"
                >
                <div class="carousel-image">
                  <img :src="item.imageUrl" :alt="item.title">
                </div>
                <div class="carousel-details">
                  <h3>{{ item.title }}</h3>
                  <p>{{ item.description }}</p>
                  <div class="carousel-actions">
                    <el-button type="primary" size="small" @click="editCarousel(item)">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteCarousel(item.id)">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
                </el-card>
              </template>
            </div>
          </div>
        </el-tab-pane>

        <!-- 推荐店铺管理 -->
        <el-tab-pane label="推荐店铺管理" name="recommended-shops">
          <div class="recommended-section">
            <el-button type="primary" @click="openShopDialog" class="add-button">
              <el-icon><Plus /></el-icon>
              添加推荐店铺
            </el-button>
            
            <el-table :data="recommendedShops" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column label="店铺信息">
                <template #default="scope">
                  <div v-if="scope && scope.row" class="shop-info">
                    <h4>{{ scope.row.shop?.name || '未知店铺' }}</h4>
                    <p>{{ scope.row.shop?.address || '' }}</p>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="排序" width="120">
                <template #default="scope">
                  <el-input-number 
                    v-if="scope && scope.row"
                    v-model="scope.row.sort" 
                    :min="1" 
                    @change="updateShopSort(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="scope">
                  <el-switch 
                    v-if="scope && scope.row"
                    v-model="scope.row.status" 
                    :active-value="1" 
                    :inactive-value="0" 
                    @change="updateShopStatus(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="scope">
                  <el-button v-if="scope && scope.row" type="danger" size="small" @click="deleteShop(scope.row.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- 推荐服务管理 -->
        <el-tab-pane label="推荐服务管理" name="recommended-services">
          <div class="recommended-section">
            <el-button type="primary" @click="openServiceDialog" class="add-button">
              <el-icon><Plus /></el-icon>
              添加推荐服务
            </el-button>
            
            <el-table :data="recommendedServices" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column label="服务信息">
                <template #default="scope">
                  <div v-if="scope && scope.row" class="service-info">
                    <h4>{{ scope.row.service?.name || '未知服务' }}</h4>
                    <p>{{ scope.row.service?.shop?.name || '未知店铺' }}</p>
                    <p class="price">￥{{ scope.row.service?.price || 0 }}</p>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="排序" width="120">
                <template #default="scope">
                  <el-input-number 
                    v-if="scope && scope.row"
                    v-model="scope.row.sort" 
                    :min="1" 
                    @change="updateServiceSort(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="状态" width="120">
                <template #default="scope">
                  <el-switch 
                    v-if="scope && scope.row"
                    v-model="scope.row.status" 
                    :active-value="1" 
                    :inactive-value="0" 
                    @change="updateServiceStatus(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180">
                <template #default="scope">
                  <el-button v-if="scope && scope.row" type="danger" size="small" @click="deleteService(scope.row.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 轮播图对话框 -->
    <el-dialog
      v-model="carouselDialogVisible"
      :title="carouselDialogTitle"
      width="500px"
    >
      <el-form :model="carouselForm" label-width="80px">
        <el-form-item label="图片URL">
          <el-input v-model="carouselForm.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="carouselForm.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="carouselForm.description" placeholder="请输入轮播图描述" />
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="carouselForm.link" placeholder="请输入轮播图链接" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="carouselForm.sort" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="carouselForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="carouselDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCarousel">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 推荐店铺对话框 -->
    <el-dialog
      v-model="shopDialogVisible"
      title="选择店铺"
      width="600px"
    >
      <el-select v-model="selectedShopId" placeholder="请选择店铺">
        <el-option 
          v-for="shop in shops" 
          :key="shop.id" 
          :label="shop.name" 
          :value="shop.id"
        />
      </el-select>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shopDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveShop">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 推荐服务对话框 -->
    <el-dialog
      v-model="serviceDialogVisible"
      title="选择服务"
      width="600px"
    >
      <el-select v-model="selectedServiceId" placeholder="请选择服务">
        <el-option 
            v-for="service in services" 
            :key="service.id" 
            :label="service.name + ' - ' + (service.shop?.name || service.shopName || '未知店铺')" 
            :value="service.id"
          />
      </el-select>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="serviceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveService">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

// 标签页
const activeTab = ref('carousel')

// 轮播图
const carousels = ref([])
const carouselDialogVisible = ref(false)
const carouselDialogTitle = ref('添加轮播图')
const carouselForm = ref({
  id: null,
  imageUrl: '',
  title: '',
  description: '',
  link: '',
  sort: 1,
  status: 1
})

// 推荐店铺
const recommendedShops = ref([])
const shops = ref([])
const shopDialogVisible = ref(false)
const selectedShopId = ref(null)

// 推荐服务
const recommendedServices = ref([])
const services = ref([])
const serviceDialogVisible = ref(false)
const selectedServiceId = ref(null)

// 加载轮播图
const loadCarousels = async () => {
  try {
    console.log('开始加载轮播图...')
    const response = await axios.get('http://localhost:8081/api/carousels')
    console.log('轮播图加载成功:', response.data)
    carousels.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载轮播图失败:', error)
    console.error('错误详情:', error.response || '无响应信息')
    carousels.value = []
  }
}

// 加载推荐店铺
const loadRecommendedShops = async () => {
  try {
    console.log('开始加载推荐店铺...')
    const response = await axios.get('http://localhost:8081/api/recommended-shops')
    console.log('推荐店铺加载成功:', response.data)
    recommendedShops.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载推荐店铺失败:', error)
    console.error('错误详情:', error.response)
    recommendedShops.value = []
  }
}

// 加载店铺列表
const loadShops = async () => {
  try {
    console.log('开始加载店铺列表...')
    const response = await axios.get('http://localhost:8081/api/shops')
    console.log('店铺列表加载成功:', response.data)
    shops.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    console.error('错误详情:', error.response)
    shops.value = []
  }
}

// 加载推荐服务
const loadRecommendedServices = async () => {
  try {
    console.log('开始加载推荐服务...')
    const response = await axios.get('http://localhost:8081/api/recommended-services')
    console.log('推荐服务加载成功:', response.data)
    recommendedServices.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载推荐服务失败:', error)
    console.error('错误详情:', error.response)
    recommendedServices.value = []
  }
}

// 加载服务列表
const loadServices = async () => {
  try {
    console.log('开始加载服务列表...')
    const response = await axios.get('http://localhost:8081/api/wash-services')
    console.log('服务列表加载成功:', response.data)
    services.value = Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('加载服务列表失败:', error)
    console.error('错误详情:', error.response)
    services.value = []
  }
}

// 打开轮播图对话框
const openCarouselDialog = () => {
  carouselForm.value = {
    id: null,
    imageUrl: '',
    title: '',
    description: '',
    link: '',
    sort: 1,
    status: 1
  }
  carouselDialogTitle.value = '添加轮播图'
  carouselDialogVisible.value = true
}

// 编辑轮播图
const editCarousel = (item) => {
  // 复制轮播图数据，排除createdAt和updatedAt字段
  const { createdAt, updatedAt, ...rest } = item
  carouselForm.value = { ...rest }
  carouselDialogTitle.value = '编辑轮播图'
  carouselDialogVisible.value = true
}

// 保存轮播图
const saveCarousel = async () => {
  console.log('开始保存轮播图...')
  console.log('carouselForm:', carouselForm.value)
  console.log('carousels.length:', carousels.value.length)
  try {
    // 验证表单数据
    if (!carouselForm.value.imageUrl) {
      console.error('图片URL不能为空')
      return
    }
    if (!carouselForm.value.title) {
      console.error('标题不能为空')
      return
    }
    
    let response
    if (carouselForm.value.id) {
      // 更新轮播图
      console.log('更新轮播图...')
      console.log('更新URL:', `http://localhost:8081/api/carousels/${carouselForm.value.id}`)
      response = await axios.put(`http://localhost:8081/api/carousels/${carouselForm.value.id}`, carouselForm.value)
      console.log('更新轮播图成功:', response.data)
    } else {
      // 检查轮播图数量
      if (carousels.value.length >= 5) {
        alert('最多只能添加5张轮播图')
        return
      }
      // 添加轮播图
      console.log('添加轮播图...')
      console.log('添加URL:', 'http://localhost:8081/api/carousels')
      console.log('请求数据:', carouselForm.value)
      response = await axios.post('http://localhost:8081/api/carousels', carouselForm.value)
      console.log('添加轮播图成功:', response.data)
    }
    console.log('保存成功，关闭对话框...')
    carouselDialogVisible.value = false
    console.log('重新加载轮播图...')
    loadCarousels()
  } catch (error) {
    console.error('保存轮播图失败:', error)
    console.error('错误详情:', error.response || '无响应信息')
    console.error('错误消息:', error.message)
    console.error('错误堆栈:', error.stack)
  }
}

// 删除轮播图
const deleteCarousel = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/carousels/${id}`)
    loadCarousels()
  } catch (error) {
    console.error('删除轮播图失败:', error)
  }
}

// 打开店铺对话框
const openShopDialog = () => {
  selectedShopId.value = null
  shopDialogVisible.value = true
}

// 保存推荐店铺
const saveShop = async () => {
  try {
    const recommendedShop = {
      shopId: selectedShopId.value,
      sort: recommendedShops.value.length + 1,
      status: 1
    }
    await axios.post('http://localhost:8081/api/recommended-shops', recommendedShop)
    shopDialogVisible.value = false
    loadRecommendedShops()
  } catch (error) {
    console.error('保存推荐店铺失败:', error)
  }
}

// 更新店铺排序
const updateShopSort = async (shop) => {
  try {
    await axios.put(`http://localhost:8081/api/recommended-shops/${shop.id}`, shop)
  } catch (error) {
    console.error('更新店铺排序失败:', error)
  }
}

// 更新店铺状态
const updateShopStatus = async (shop) => {
  try {
    await axios.put(`http://localhost:8081/api/recommended-shops/${shop.id}`, shop)
  } catch (error) {
    console.error('更新店铺状态失败:', error)
  }
}

// 删除推荐店铺
const deleteShop = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/recommended-shops/${id}`)
    loadRecommendedShops()
  } catch (error) {
    console.error('删除推荐店铺失败:', error)
  }
}

// 打开服务对话框
const openServiceDialog = () => {
  selectedServiceId.value = null
  serviceDialogVisible.value = true
}

// 保存推荐服务
const saveService = async () => {
  try {
    const recommendedService = {
      serviceId: selectedServiceId.value,
      sort: recommendedServices.value.length + 1,
      status: 1
    }
    await axios.post('http://localhost:8081/api/recommended-services', recommendedService)
    serviceDialogVisible.value = false
    loadRecommendedServices()
  } catch (error) {
    console.error('保存推荐服务失败:', error)
  }
}

// 更新服务排序
const updateServiceSort = async (service) => {
  try {
    await axios.put(`http://localhost:8081/api/recommended-services/${service.id}`, service)
  } catch (error) {
    console.error('更新服务排序失败:', error)
  }
}

// 更新服务状态
const updateServiceStatus = async (service) => {
  try {
    await axios.put(`http://localhost:8081/api/recommended-services/${service.id}`, service)
  } catch (error) {
    console.error('更新服务状态失败:', error)
  }
}

// 删除推荐服务
const deleteService = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/recommended-services/${id}`)
    loadRecommendedServices()
  } catch (error) {
    console.error('删除推荐服务失败:', error)
  }
}

// 初始化数据
onMounted(() => {
  loadCarousels()
  loadRecommendedShops()
  loadShops()
  loadRecommendedServices()
  loadServices()
})
</script>

<style scoped>
.customer-homepage {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-container {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: fadeIn 0.5s ease-in-out;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.card-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.tabs-container {
  margin-top: 20px;
}

.add-button {
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

.carousel-info {
  margin-bottom: 20px;
  color: #909399;
  font-size: 14px;
}

.carousel-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.carousel-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.carousel-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.carousel-image {
  height: 200px;
  overflow: hidden;
}

.carousel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.carousel-card:hover .carousel-image img {
  transform: scale(1.05);
}

.carousel-details {
  padding: 16px;
}

.carousel-details h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.carousel-details p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

.carousel-actions {
  display: flex;
  gap: 8px;
}

.recommended-section {
  margin-top: 20px;
}

.shop-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.shop-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.service-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.service-info p {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #909399;
}

.service-info .price {
  color: #f56c6c;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(103, 194, 58, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0);
  }
}
</style>