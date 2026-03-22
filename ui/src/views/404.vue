<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-illustration">
        <svg viewBox="0 0 200 200" width="300" height="300">
          <circle cx="100" cy="100" r="90" fill="#f0f2f5"/>
          <path d="M60 110 L140 110 Q100 150 60 110" stroke="#2895ef" stroke-width="4" fill="none"
                stroke-linecap="round"/>
          <circle cx="70" cy="80" r="5" fill="#303133"/>
          <circle cx="130" cy="80" r="5" fill="#303133"/>
          <text x="50%" y="50" text-anchor="middle" font-size="40" font-weight="bold" fill="#2895ef">404</text>
        </svg>
      </div>

      <h1 class="error-title">页面不小心走丢了...</h1>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除。请检查输入的地址是否正确。</p>

      <div class="error-actions">
        <el-button type="primary" size="large" @click="$router.push('/index/dashboard')">
          返回工作台
        </el-button>
        <el-button size="large" @click="handleToLogin" plain>
          返回登录页
        </el-button>
      </div>
    </div>

    <div class="footer-copyright">
      © 2026 经济开发区管理平台
    </div>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const handleToLogin = () => {
  userStore.logout() // 清除登录状态，确保能重新登录
  router.push('/login')
}
</script>

<style scoped>
.error-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  color: #303133;
  padding: 0 20px;
}

.error-content {
  text-align: center;
  max-width: 500px;
}

.error-illustration {
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

.error-title {
  font-size: 28px;
  margin-bottom: 15px;
  color: #303133;
}

.error-desc {
  font-size: 16px;
  color: #909399;
  line-height: 1.6;
  margin-bottom: 35px;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

/* 适配你项目中的橙色风格 */
:deep(.el-button--primary) {
  background-color: #0082ec;
  border-color: #2895ef;
}

:deep(.el-button--primary:hover) {
  background-color: #0082ec;
  border-color: #2895ef;
}

.footer-copyright {
  position: absolute;
  bottom: 30px;
  font-size: 13px;
  color: #c0c4cc;
}
</style>