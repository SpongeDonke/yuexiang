<template>
  <div class="card">
    <h2 class="page-title">系统管理</h2>
    <div class="section-title">系统信息</div>
    <div class="list-item">
      <h4>系统版本</h4>
      <p>v1.0.0</p>
    </div>
    <div class="list-item">
      <h4>数据库状态</h4>
      <p>正常</p>
    </div>
    <div class="list-item">
      <h4>服务器状态</h4>
      <p>运行中</p>
    </div>
    
    <div class="section-title" style="margin-top: 30px;">系统设置</div>
    <div class="form-group">
      <label>系统名称</label>
      <input type="text" v-model="systemSettings.name" />
    </div>
    <div class="form-group">
      <label>系统描述</label>
      <textarea v-model="systemSettings.description" rows="3"></textarea>
    </div>
    <button class="btn btn-primary" @click="saveSettings">保存设置</button>
    <div v-if="message" class="success-message">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const systemSettings = ref({
  name: '洗护平台管理系统',
  description: '专业的洗护服务管理平台'
})

const message = ref('')

const saveSettings = () => {
  // 这里可以实现保存系统设置的功能
  message.value = '设置保存成功'
  setTimeout(() => {
    message.value = ''
  }, 3000)
}
</script>
