<template>
  <div class="flex-center" style="height:100vh;flex-direction:column;">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="goLogin">返回登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

// 获取路由实例
const router = useRouter()

// 跳转登录
const goLogin = () => {
  router.push('/login')
}
</script>