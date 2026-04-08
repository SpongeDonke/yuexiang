<template>
  <div class="comment-page">
    <div class="page-header">
      <div class="header-icon">
        <i class="fas fa-comments"></i>
      </div>
      <h2 class="page-title">我的评论</h2>
      <p class="page-subtitle">分享您的洗护体验</p>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载评论中...</p>
    </div>

    <div v-else>
      <!-- 评论列表 -->
      <div class="comments-section">
        <div class="section-header">
          <i class="fas fa-list-alt"></i>
          <span>我的评论记录</span>
          <span class="comment-count">{{ comments.length }} 条</span>
        </div>

        <div v-if="comments.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="far fa-comment-dots"></i>
          </div>
          <p>暂无评论</p>
          <p class="empty-hint">完成订单后即可发表评论</p>
        </div>

        <div v-else class="comments-list">
          <transition-group name="comment-fade" tag="div">
            <div class="comment-card" v-for="comment in paginatedComments" :key="comment.id">
              <div class="shop-service-info">
                <div class="shop-tag">
                  <i class="fas fa-store"></i>
                  {{ comment.shopName || '未知店铺' }}
                </div>
                <div class="service-tag">
                  <i class="fas fa-concierge-bell"></i>
                  {{ comment.serviceName || '未知服务' }}
                </div>
              </div>

              <div class="comment-header">
                <div class="order-info">
                  <i class="fas fa-receipt"></i>
                  <span class="order-label">订单号:</span>
                  <span class="order-id">#{{ comment.orderId }}</span>
                </div>
                <div class="rating-display">
                  <div class="stars">
                    <i v-for="star in 5" :key="star"
                       :class="['fas', star <= comment.rating ? 'fa-star' : 'fa-star']"
                       :style="{ color: star <= comment.rating ? '#fadb14' : '#d9d9d9' }">
                    </i>
                  </div>
                  <span class="rating-text">{{ comment.rating }}.0 分</span>
                </div>
              </div>

              <div class="comment-content">
                <i class="fas fa-quote-left quote-icon"></i>
                <p>{{ comment.content }}</p>
              </div>

              <div v-if="comment.reply" class="reply-box">
                <div class="reply-header">
                  <i class="fas fa-reply"></i>
                  <span>商家回复</span>
                </div>
                <p class="reply-content">{{ comment.reply }}</p>
                <span class="reply-time">
                  <i class="far fa-clock"></i>
                  {{ formatDate(comment.replyTime) }}
                </span>
              </div>

              <div class="comment-footer">
                <span class="comment-time">
                  <i class="far fa-calendar-alt"></i>
                  {{ formatDate(comment.createdAt) }}
                </span>
                <button class="delete-btn" @click="showDeleteConfirm(comment.id)">
                  <i class="far fa-trash-alt"></i>
                  删除
                </button>
              </div>
            </div>
          </transition-group>

          <div v-if="comments.length > 0" class="pagination-wrapper">
            <div class="pagination-info">
              共 <strong>{{ comments.length }}</strong> 条评论，第 <strong>{{ currentPage }}</strong>/{{ totalPages }} 页
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
      </div>

      <!-- 添加评论表单 -->
      <div class="add-comment-section">
        <div class="section-header">
          <i class="fas fa-pen-fancy"></i>
          <span>发表新评论</span>
        </div>

        <div class="form-card">
          <div class="form-row">
            <label class="form-label">
              <i class="fas fa-shopping-cart"></i>
              选择订单
            </label>
            <select v-model="newComment.orderId" class="form-select" @change="onOrderChange">
              <option value="">请选择已完成订单</option>
              <option v-for="order in orders" :key="order.id" :value="order.id">
                #{{ order.id }} - {{ order.shopName || '未知店铺' }} - {{ getOrderServices(order) }}
              </option>
            </select>
          </div>

          <div class="form-row">
            <label class="form-label">
              <i class="fas fa-star"></i>
              服务评分
            </label>
            <div class="rating-input">
              <div class="star-rating">
                <i v-for="star in 5" :key="star"
                   class="fas fa-star star-btn"
                   :class="{ active: star <= (newComment.rating || 0) }"
                   @click="newComment.rating = star">
                </i>
              </div>
              <span class="rating-value" v-if="newComment.rating">
                {{ newComment.rating }} 星
              </span>
            </div>
          </div>

          <div class="form-row">
            <label class="form-label">
              <i class="fas fa-edit"></i>
              评论内容
              <span class="char-count" :class="{ 'error': newComment.content && newComment.content.length < 5 }">
                ({{ newComment.content.length }}/5字)
              </span>
            </label>
            <textarea v-model="newComment.content" class="form-textarea"
                      rows="4" placeholder="请详细描述您的使用体验（至少5个字）..."></textarea>
            <div v-if="newComment.content && newComment.content.length > 0 && newComment.content.length < 5" class="validation-hint error">
              <i class="fas fa-exclamation-circle"></i>
              评论内容至少需要5个字符，当前 {{ newComment.content.length }} 个字符
            </div>
          </div>

          <button class="submit-btn" @click="addComment">
            <i class="fas fa-paper-plane"></i>
            发布评论
          </button>

          <transition name="message-fade">
            <div v-if="message" class="message" :class="message.includes('成功') ? 'success' : 'error'">
              <i :class="message.includes('成功') ? 'fas fa-check-circle' : 'fas fa-exclamation-circle'"></i>
              {{ message }}
            </div>
          </transition>
        </div>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <transition name="modal-fade">
      <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
        <div class="modal-container">
          <div class="modal-icon warning">
            <i class="fas fa-exclamation-triangle"></i>
          </div>
          <h3 class="modal-title">确认删除</h3>
          <p class="modal-text">确定要删除这条评论吗？此操作无法撤销。</p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="showDeleteDialog = false">
              <i class="fas fa-times"></i>
              取消
            </button>
            <button class="btn-confirm-delete" @click="confirmDelete">
              <i class="fas fa-trash-alt"></i>
              确认删除
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

const comments = ref([])
const orders = ref([])
const orderMap = ref({})
const newComment = ref({ orderId: '', rating: '', content: '' })
const loading = ref(true)
const message = ref('')
const showDeleteDialog = ref(false)
const currentDeleteId = ref(null)
const currentPage = ref(1)
  const pageSize = ref(3)

  const totalPages = computed(() => Math.ceil(comments.value.length / pageSize.value) || 1)

  const paginatedComments = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return comments.value.slice(start, end)
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
  await loadComments()
  await loadOrders()
})

const loadComments = async () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)
      const response = await axios.get(`/api/comments/user/${user.id}`)
      const commentsData = response.data
      
      for (const comment of commentsData) {
        try {
          const orderRes = await axios.get(`/api/orders/${comment.orderId}`)
          const order = orderRes.data
          comment.shopName = order.shopName || '未知店铺'
          
          if (order.orderDetails && order.orderDetails.length > 0) {
            comment.serviceName = order.orderDetails.map(d => d.serviceName || '服务').join('、')
          } else {
            comment.serviceName = '未知服务'
          }
        } catch (e) {
          comment.shopName = '未知店铺'
          comment.serviceName = '未知服务'
        }
      }
      
      comments.value = commentsData
    }
  } catch (error) {
    console.error('Load comments error:', error)
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
      const completedOrders = response.data.filter(order => order.status === 'completed' || order.status === '已完成')
      orders.value = completedOrders
      
      for (const order of completedOrders) {
        orderMap.value[order.id] = order
      }
    }
  } catch (error) {
    console.error('Load orders error:', error)
  }
}

const getOrderServices = (order) => {
  if (order.orderDetails && order.orderDetails.length > 0) {
    return order.orderDetails.map(d => d.serviceName || '服务').join('、')
  }
  return '未知服务'
}

const onOrderChange = () => {
}

const addComment = async () => {
  try {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      const user = JSON.parse(storedUser)

      if (!newComment.value.orderId) {
        message.value = '请选择订单'
        setTimeout(() => message.value = '', 3000)
        return
      }

      if (!newComment.value.rating) {
        message.value = '请选择评分'
        setTimeout(() => message.value = '', 3000)
        return
      }

      if (!newComment.value.content) {
        message.value = '请填写评论内容'
        setTimeout(() => message.value = '', 3000)
        return
      }

      if (newComment.value.content.length < 5) {
        message.value = '评论内容至少需要5个字符'
        setTimeout(() => message.value = '', 3000)
        return
      }

      const order = orders.value.find(o => o.id == newComment.value.orderId)
      if (!order || (order.status !== 'completed' && order.status !== '已完成')) {
        message.value = '只能对已完成的订单进行评论'
        setTimeout(() => message.value = '', 3000)
        return
      }

      const comment = {
        orderId: parseInt(newComment.value.orderId),
        userId: user.id,
        content: newComment.value.content,
        rating: parseInt(newComment.value.rating)
      }

      await axios.post('/api/comments', comment)
      newComment.value = { orderId: '', rating: '', content: '' }
      message.value = '评论添加成功'
      setTimeout(() => message.value = '', 3000)
      await loadComments()
    }
  } catch (error) {
    console.error('Add comment error:', error)
    message.value = '添加评论失败，请稍后重试'
    setTimeout(() => message.value = '', 3000)
  }
}

const showDeleteConfirm = (id) => {
  currentDeleteId.value = id
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (currentDeleteId.value) {
    try {
      await axios.delete(`/api/comments/${currentDeleteId.value}`)
      await loadComments()
      showDeleteDialog.value = false
      currentDeleteId.value = null
    } catch (error) {
      console.error('Delete comment error:', error)
    }
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}
</script>

<style scoped>
.comment-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -5%;
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.header-icon {
  font-size: 48px;
  margin-bottom: 12px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
  border-radius: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 3px solid #667eea;
}

.section-header i {
  color: #667eea;
  font-size: 20px;
}

.comment-count {
  margin-left: auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.comments-section,
.add-comment-section {
  background: white;
  border-radius: 16px;
  padding: 28px;
  margin-bottom: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-hint {
  font-size: 14px;
  margin-top: 8px;
  color: #bbb;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-card {
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  background: linear-gradient(to right, #fafafa, white);
}

.comment-card:hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
  border-color: #667eea;
}

.shop-service-info {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.shop-tag,
.service-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.shop-tag {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.service-tag {
  background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
  color: #fa8c16;
  border: 1px solid #ffd591;
}

.shop-tag i {
  font-size: 14px;
}

.service-tag i {
  font-size: 14px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e8e8e8;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.order-info i {
  color: #667eea;
}

.order-label {
  font-size: 14px;
  font-weight: 500;
}

.order-id {
  font-weight: 600;
  color: #333;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stars {
  display: flex;
  gap: 3px;
}

.stars i {
  font-size: 18px;
}

.rating-text {
  font-weight: 600;
  color: #fadb14;
  font-size: 15px;
}

.comment-content {
  position: relative;
  padding-left: 24px;
  margin-bottom: 12px;
}

.quote-icon {
  position: absolute;
  left: 0;
  top: 0;
  color: #667eea;
  opacity: 0.3;
  font-size: 20px;
}

.comment-content p {
  color: #555;
  line-height: 1.7;
  font-size: 15px;
}

.reply-box {
  background: linear-gradient(135deg, #fff7e6 0%, #fffbe6 100%);
  border-left: 4px solid #faad14;
  padding: 14px 16px;
  border-radius: 8px;
  margin-top: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #faad14;
  font-weight: 600;
  margin-bottom: 8px;
  font-size: 14px;
}

.reply-content {
  color: #666;
  line-height: 1.6;
  margin-bottom: 6px;
}

.reply-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.comment-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
  font-weight: 500;
}

.delete-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
  transform: scale(1.05);
}

.form-card {
  background: #fafafa;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e8e8e8;
}

.form-row {
  margin-bottom: 22px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
  font-size: 14px;
}

.form-label i {
  color: #667eea;
}

.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: white;
}

.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  line-height: 1.6;
}

.rating-input {
  display: flex;
  align-items: center;
  gap: 16px;
}

.star-rating {
  display: flex;
  gap: 8px;
}

.star-btn {
  font-size: 28px;
  color: #d9d9d9;
  cursor: pointer;
  transition: all 0.2s ease;
}

.star-btn:hover {
  transform: scale(1.2);
  color: #fadb14;
}

.star-btn.active {
  color: #fadb14;
  animation: starPop 0.3s ease;
}

@keyframes starPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.rating-value {
  font-weight: 600;
  color: #667eea;
  font-size: 16px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.message {
  margin-top: 16px;
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.message.success {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.message.error {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 28px;
}

.modal-icon.warning {
  background: #fff7e6;
  color: #faad14;
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.modal-text {
  color: #666;
  margin-bottom: 24px;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-cancel,
.btn-confirm-delete {
  flex: 1;
  padding: 12px 20px;
  border-radius: 10px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #f0f2f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e8e8e8;
}

.btn-confirm-delete {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
}

.btn-confirm-delete:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 77, 79, 0.3);
}

.comment-fade-enter-active,
.comment-fade-leave-active {
  transition: all 0.3s ease;
}

.comment-fade-enter-from,
.comment-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.message-fade-enter-active,
.message-fade-leave-active {
  transition: all 0.3s ease;
}

.message-fade-enter-from,
.message-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.char-count {
  margin-left: auto;
  font-size: 12px;
  color: #999;
  font-weight: 400;
}

.char-count.error {
  color: #ff4d4f;
  font-weight: 600;
}

.validation-hint {
  margin-top: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.validation-hint.error {
  background: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
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
  color: #6f42c1;
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
  border-color: #6f42c1;
  color: #6f42c1;
  transform: translateY(-1px);
}

.page-btn.active {
  background: linear-gradient(135deg, #6f42c1, #5a32a3);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(111, 66, 193, 0.35);
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
