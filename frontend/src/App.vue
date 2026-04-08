<template>
  <router-view />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)

onMounted(() => {
  const user = localStorage.getItem('user')
  isLoggedIn.value = !!user
  if (!user && router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
})
</script>
