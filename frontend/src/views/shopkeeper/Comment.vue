<template>
  <div class="comment-page">
    <div class="page-header">
      <div class="header-icon">
        <i class="fas fa-store"></i>
      </div>
      <h2 class="page-title">客户评价管理</h2>
      <p class="page-subtitle">查看并回复客户评价</p>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载评价中...</p>
    </div>

    <div v-else>
      <div class="comments-section">
        <div class="section-header">
          <i class="fas fa-star-half-alt"></i>
          <span>客户评价列表</span>
          <span class="comment-count">{{ comments.length }} 条</span>
        </div>

        <div v-if="comments.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="far fa-comments"></i>
          </div>
          <p>暂无评价</p>
          <p class="empty-hint">等待客户的评价反馈</p>
        </div>

        <div v-else class="comments-list">
          <transition-group name="comment-fade" tag="div">
            <div class="comment-card" v-for="comment in paginatedComments" :key="comment.id">
              <div class="card-badge" :class="'rating-' + comment.rating">
                {{ comment.rating }}星
              </div>

              <div class="comment-body">
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
                    <span>订单 #{{ comment.orderId }}</span>
                  </div>
                  <div class="rating-stars">
                    <i v-for="star in 5" :key="star"
                       :class="['fas', star <= comment.rating ? 'fa-star' : 'fa-star']"
                       :style="{ color: star <= comment.rating ? '#fadb14' : '#e8e8e8' }">
                    </i>
                  </div>
                </div>

                <div class="comment-content">
                  <div class="quote-decoration"></div>
                  <p>{{ comment.content }}</p>
                </div>

                <div class="comment-meta">
                  <span class="time-stamp">
                    <i class="far fa-clock"></i>
                    {{ formatDate(comment.createdAt) }}
                  </span>
                </div>

                <!-- 商家回复区域 -->
                <div v-if="comment.reply" class="reply-section replied">
                  <div class="reply-header">
                    <div class="reply-avatar">
                      <i class="fas fa-user-tie"></i>
                    </div>
                    <div class="reply-info">
                      <strong>商家回复</strong>
                      <span class="reply-time">{{ formatDate(comment.replyTime) }}</span>
                    </div>
                    <div class="replied-badge">
                      <i class="fas fa-check-circle"></i>
                      已回复
                    </div>
                  </div>
                  <p class="reply-content">{{ comment.reply }}</p>
                </div>

                <!-- 回复表单 -->
                <div v-if="!comment.reply && replyCommentId === comment.id" class="reply-form-section">
                  <div class="form-header">
                    <i class="fas fa-reply-all"></i>
                    撰写回复
                  </div>
                  <textarea v-model="replyContent" class="reply-textarea"
                            rows="4" placeholder="请输入您的回复内容，感谢客户的评价..."></textarea>
                  <div class="form-actions">
                    <button class="btn-cancel-reply" @click="cancelReply">
                      <i class="fas fa-times"></i>
                      取消
                    </button>
                    <button class="btn-submit-reply" @click="submitReply(comment.id)">
                      <i class="fas fa-paper-plane"></i>
                      发送回复
                    </button>
                  </div>
                </div>

                <div class="comment-actions">
                  <button v-if="!comment.reply" class="reply-btn" @click="showReplyForm(comment.id)">
                    <i class="fas fa-reply"></i>
                    回复评价
                  </button>
                  <button v-else class="replied-status-btn">
                    <i class="fas fa-check-double"></i>
                    已处理
                  </button>
                </div>
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const comments = ref([])
const loading = ref(true)
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
  const shopId = ref(null)
const replyCommentId = ref(null)
const replyContent = ref('')

onMounted(async () => {
  await loadShopId()
  await loadComments()
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

const loadComments = async () => {
  try {
    if (shopId.value) {
      const ordersResponse = await axios.get(`/api/orders/shop/${shopId.value}`)
      const orders = ordersResponse.data
      const orderIds = orders.map(order => order.id)

      if (orderIds.length > 0) {
        const commentsResponse = await axios.get('/api/comments')
        const filteredComments = commentsResponse.data.filter(comment => orderIds.includes(comment.orderId))
        
        for (const comment of filteredComments) {
          const order = orders.find(o => o.id === comment.orderId)
          if (order) {
            comment.shopName = order.shopName || '未知店铺'
            if (order.orderDetails && order.orderDetails.length > 0) {
              comment.serviceName = order.orderDetails.map(d => d.serviceName || '服务').join('、')
            } else {
              comment.serviceName = '未知服务'
            }
          } else {
            comment.shopName = '未知店铺'
            comment.serviceName = '未知服务'
          }
        }
        
        comments.value = filteredComments
      }
    }
  } catch (error) {
    console.error('Load comments error:', error)
  } finally {
    loading.value = false
  }
}

const showReplyForm = (commentId) => {
  replyCommentId.value = commentId
  replyContent.value = ''
}

const cancelReply = () => {
  replyCommentId.value = null
  replyContent.value = ''
}

const submitReply = async (commentId) => {
  try {
    if (!replyContent.value.trim()) {
      return
    }

    await axios.post(`/api/comments/${commentId}/reply`, {
      reply: replyContent.value
    })

    await loadComments()
    replyCommentId.value = null
    replyContent.value = ''
  } catch (error) {
    console.error('Submit reply error:', error)
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
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
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
  border-top: 4px solid #38ef7d;
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
  border-bottom: 3px solid #38ef7d;
}

.section-header i {
  color: #38ef7d;
  font-size: 20px;
}

.comment-count {
  margin-left: auto;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.comments-section {
  background: white;
  border-radius: 16px;
  padding: 28px;
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
  gap: 20px;
}

.comment-card {
  position: relative;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
}

.comment-card:hover {
  box-shadow: 0 6px 24px rgba(17, 153, 142, 0.15);
  transform: translateY(-3px);
  border-color: #38ef7d;
}

.card-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  color: white;
  z-index: 10;
}

.card-badge.rating-5,
.card-badge.rating-4 {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.card-badge.rating-3 {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
}

.card-badge.rating-2,
.card-badge.rating-1 {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.comment-body {
  padding: 24px;
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

.shop-tag i,
.service-tag i {
  font-size: 14px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 2px dashed #f0f0f0;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.order-info i {
  color: #11998e;
  font-size: 18px;
}

.rating-stars {
  display: flex;
  gap: 4px;
}

.rating-stars i {
  font-size: 18px;
}

.comment-content {
  position: relative;
  padding-left: 30px;
  margin-bottom: 16px;
}

.quote-decoration {
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #11998e 0%, #38ef7d 100%);
  border-radius: 2px;
}

.comment-content p {
  color: #555;
  line-height: 1.8;
  font-size: 15px;
}

.comment-meta {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.time-stamp {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.reply-section {
  background: linear-gradient(135deg, #e6fffb 0%, #d9f7be 100%);
  border-left: 4px solid #52c41a;
  border-radius: 12px;
  padding: 18px;
  margin-top: 16px;
}

.reply-section.replied {
  background: linear-gradient(135deg, #fff7e6 0%, #fffbe6 100%);
  border-left-color: #faad14;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reply-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.reply-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.reply-info strong {
  color: #333;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.replied-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #52c41a;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
}

.reply-content {
  color: #666;
  line-height: 1.7;
  font-size: 14px;
  padding-left: 48px;
}

.reply-form-section {
  background: #fafafa;
  border: 2px dashed #38ef7d;
  border-radius: 12px;
  padding: 20px;
  margin-top: 16px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #11998e;
  margin-bottom: 12px;
  font-size: 15px;
}

.reply-textarea {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  font-size: 14px;
  resize: vertical;
  min-height: 80px;
  transition: all 0.3s ease;
  line-height: 1.6;
  font-family: inherit;
}

.reply-textarea:focus {
  outline: none;
  border-color: #38ef7d;
  box-shadow: 0 0 0 3px rgba(56, 239, 125, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 14px;
}

.btn-cancel-reply,
.btn-submit-reply {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel-reply {
  background: #f0f2f5;
  color: #666;
}

.btn-cancel-reply:hover {
  background: #e8e8e8;
}

.btn-submit-reply {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(56, 239, 125, 0.3);
}

.btn-submit-reply:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(56, 239, 125, 0.4);
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.reply-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #e6fffb 0%, #d9f7be 100%);
  color: #11998e;
  border: 2px solid #38ef7d;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 600;
}

.reply-btn:hover {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(56, 239, 125, 0.3);
}

.replied-status-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f6ffed;
  color: #52c41a;
  border: 2px solid #b7eb8f;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: default;
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
