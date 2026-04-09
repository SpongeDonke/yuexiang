<template>
  <div class="comment-page">
    <div class="page-header">
      <div class="header-icon">
        <i class="fas fa-shield-alt"></i>
      </div>
      <h2 class="page-title">评论管理</h2>
      <p class="page-subtitle">平台评论内容监管与维护</p>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载评论数据...</p>
    </div>

    <div v-else>
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card total">
          <div class="stat-icon">
            <i class="fas fa-comments"></i>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ comments.length }}</span>
            <span class="stat-label">总评论数</span>
          </div>
        </div>

        <div class="stat-card high-rating">
          <div class="stat-icon">
            <i class="fas fa-thumbs-up"></i>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ getHighRatingCount() }}</span>
            <span class="stat-label">好评 (4-5星)</span>
          </div>
        </div>

        <div class="stat-card medium-rating">
          <div class="stat-icon">
            <i class="fas fa-meh"></i>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ getMediumRatingCount() }}</span>
            <span class="stat-label">中评 (3星)</span>
          </div>
        </div>

        <div class="stat-card low-rating">
          <div class="stat-icon">
            <i class="fas fa-thumbs-down"></i>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ getLowRatingCount() }}</span>
            <span class="stat-label">差评 (1-2星)</span>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-section">
        <div class="section-header">
          <i class="fas fa-list-ul"></i>
          <span>全部评论</span>
          <span class="comment-count">{{ comments.length }} 条</span>
        </div>

        <div v-if="comments.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="far fa-comment-alt"></i>
          </div>
          <p>暂无评论</p>
          <p class="empty-hint">等待用户发表评价</p>
        </div>

        <div v-else class="comments-list">
          <transition-group name="comment-fade" tag="div">
            <div class="comment-card" v-for="comment in paginatedComments" :key="comment.id">
              <!-- 评分徽章 -->
              <div class="rating-badge" :class="'level-' + getRatingLevel(comment.rating)">
                <i class="fas fa-star"></i>
                {{ comment.rating }}.0
              </div>

              <!-- 卡片主体 -->
              <div class="card-body">
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

                <div class="card-top">
                  <div class="order-badge">
                    <i class="fas fa-hashtag"></i>
                    订单 #{{ comment.orderId }}
                  </div>
                  <div class="user-info">
                    <div class="user-avatar">
                      <i class="fas fa-user"></i>
                    </div>
                    <span>用户 ID: {{ comment.userId }}</span>
                  </div>
                </div>

                <div class="card-content">
                  <div class="content-wrapper">
                    <i class="fas fa-quote-right content-quote"></i>
                    <p>{{ comment.content }}</p>
                  </div>
                </div>

                <div v-if="comment.reply" class="admin-reply-preview">
                  <div class="reply-tag">
                    <i class="fas fa-reply"></i>
                    商家回复
                  </div>
                  <p class="reply-text">{{ comment.reply }}</p>
                </div>

                <div class="card-footer">
                  <div class="time-info">
                    <i class="far fa-calendar-check"></i>
                    <span>{{ formatDate(comment.createdAt) }}</span>
                  </div>
                  <button class="delete-action-btn" @click="showDeleteConfirm(comment)">
                    <i class="fas fa-trash-alt"></i>
                    删除
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

    <!-- 删除确认对话框 -->
    <transition name="modal-fade">
      <div v-if="showDeleteDialog" class="modal-overlay" @click.self="showDeleteDialog = false">
        <div class="modal-container">
          <div class="modal-icon danger">
            <i class="fas fa-exclamation-circle"></i>
          </div>
          <h3 class="modal-title">确认删除</h3>
          <p class="modal-text">
            您确定要删除这条评论吗？<br/>
            <small>订单号: #{{ targetComment?.orderId }}</small>
          </p>
          <div class="modal-actions">
            <button class="btn-cancel" @click="showDeleteDialog = false">
              <i class="fas fa-times-circle"></i>
              取消操作
            </button>
            <button class="btn-delete" @click="confirmDelete">
              <i class="fas fa-skull-crossbones"></i>
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
const loading = ref(true)
const showDeleteDialog = ref(false)
const deleteTargetId = ref(null)
const targetComment = ref(null)
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
})

const loadComments = async () => {
  try {
    const response = await axios.get('/api/comments')
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
  } catch (error) {
    console.error('Load comments error:', error)
  } finally {
    loading.value = false
  }
}

const deleteComment = async (comment) => {
  if (confirm(`确定要删除订单 ${comment.orderId} 的评论吗？`)) {
    try {
      await axios.delete(`/api/comments/${comment.id}`)
      await loadComments()
    } catch (error) {
      console.error('Delete comment error:', error)
    }
  }
}

const showDeleteConfirm = (comment) => {
  targetComment.value = comment
  showDeleteDialog.value = true
}

const confirmDelete = async () => {
  if (targetComment.value) {
    try {
      await axios.delete(`/api/comments/${targetComment.value.id}`)
      await loadComments()
      showDeleteDialog.value = false
      targetComment.value = null
    } catch (error) {
      console.error('Delete comment error:', error)
    }
  }
}

const getRatingLevel = (rating) => {
  if (rating >= 4) return 'high'
  if (rating === 3) return 'medium'
  return 'low'
}

const getHighRatingCount = () => comments.value.filter(c => c.rating >= 4).length
const getMediumRatingCount = () => comments.value.filter(c => c.rating === 3).length
const getLowRatingCount = () => comments.value.filter(c => c.rating <= 2).length

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}
</script>

<style scoped>
.comment-page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 35px;
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
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
  width: 220px;
  height: 220px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -5%;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.header-icon {
  font-size: 52px;
  margin-bottom: 14px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

.page-title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.page-subtitle {
  font-size: 16px;
  opacity: 0.95;
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
  width: 44px;
  height: 44px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #f45c43;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
  margin-bottom: 28px;
}

.stat-card {
  background: white;
  border-radius: 14px;
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border-left: 5px solid transparent;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.stat-card.total {
  border-left-color: #1890ff;
}

.stat-card.high-rating {
  border-left-color: #52c41a;
}

.stat-card.medium-rating {
  border-left-color: #faad14;
}

.stat-card.low-rating {
  border-left-color: #ff4d4f;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.total .stat-icon {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
}

.high-rating .stat-icon {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.medium-rating .stat-icon {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
}

.low-rating .stat-icon {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #888;
  font-weight: 500;
  margin-top: 2px;
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
  border-bottom: 3px solid #f45c43;
}

.section-header i {
  color: #f45c43;
  font-size: 20px;
}

.comment-count {
  margin-left: auto;
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
  color: white;
  padding: 4px 14px;
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
  font-size: 68px;
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
  gap: 18px;
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
  box-shadow: 0 8px 28px rgba(235, 51, 73, 0.15);
  transform: translateY(-3px);
  border-color: #f45c43;
}

.rating-badge {
  position: absolute;
  top: 18px;
  right: 18px;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.rating-badge i {
  font-size: 18px;
  margin-bottom: 2px;
}

.rating-badge.level-high {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.rating-badge.level-medium {
  background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
}

.rating-badge.level-low {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
}

.card-body {
  padding: 24px;
  padding-right: 90px;
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

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 14px;
  border-bottom: 2px dashed #f0f0f0;
}

.order-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.order-badge i {
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
  font-size: 14px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.card-content {
  margin-bottom: 16px;
}

.content-wrapper {
  position: relative;
  padding-left: 36px;
  min-height: 40px;
}

.content-quote {
  position: absolute;
  left: 0;
  top: 0;
  font-size: 26px;
  color: #f45c43;
  opacity: 0.25;
}

.content-wrapper p {
  color: #555;
  line-height: 1.8;
  font-size: 15px;
}

.admin-reply-preview {
  background: linear-gradient(135deg, #fff7e6 0%, #fffbe6 100%);
  border-left: 4px solid #faad14;
  border-radius: 10px;
  padding: 14px 16px;
  margin-bottom: 14px;
}

.reply-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #faad14;
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 8px;
}

.reply-text {
  color: #666;
  line-height: 1.6;
  font-size: 14px;
  padding-left: 20px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #999;
}

.time-info i {
  color: #f45c43;
}

.delete-action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #fff1f0 0%, #fff7f7 100%);
  color: #cf1322;
  border: 2px solid #ffa39e;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 600;
}

.delete-action-btn:hover {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
  border-color: #ff4d4f;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(207, 19, 34, 0.3);
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(6px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 18px;
  padding: 36px;
  text-align: center;
  max-width: 420px;
  width: 92%;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.85) translateY(-30px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 18px;
  font-size: 32px;
}

.modal-icon.danger {
  background: linear-gradient(135deg, #fff1f0 0%, #fff7f7 100%);
  color: #ff4d4f;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.modal-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.modal-text {
  color: #666;
  margin-bottom: 28px;
  line-height: 1.7;
  font-size: 15px;
}

.modal-text small {
  color: #999;
  font-size: 13px;
}

.modal-actions {
  display: flex;
  gap: 14px;
  justify-content: center;
}

.btn-cancel,
.btn-delete {
  flex: 1;
  padding: 14px 22px;
  border-radius: 12px;
  border: none;
  font-size: 15px;
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
  transform: translateY(-2px);
}

.btn-delete {
  background: linear-gradient(135deg, #cf1322 0%, #ff4d4f 100%);
  color: white;
  box-shadow: 0 4px 14px rgba(207, 19, 34, 0.3);
}

.btn-delete:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 22px rgba(207, 19, 34, 0.4);
}

/* 动画 */
.comment-fade-enter-active,
.comment-fade-leave-active {
  transition: all 0.35s ease;
}

.comment-fade-enter-from,
.comment-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px) scale(0.97);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
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
  color: #dc3545;
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
  border-color: #dc3545;
  color: #dc3545;
  transform: translateY(-1px);
}

.page-btn.active {
  background: linear-gradient(135deg, #dc3545, #c82333);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(220, 53, 69, 0.35);
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
