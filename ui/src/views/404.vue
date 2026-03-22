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
        <el-button type="primary" size="large" @click="handleBack">
          {{ backButtonText }}
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
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 1. 权限与角色状态判断
const hasToken = computed(() => !!userStore.token)
const roles = computed(() => userStore.roles || [])
const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN') || roles.value.includes('ROLE_STAFF'))
const isEnterprise = computed(() => roles.value.includes('ROLE_ENTERPRISE'))

// 2. 动态显示按钮文字
const backButtonText = computed(() => {
  if (!hasToken.value) return '返回门户首页'
  if (isEnterprise.value) return '返回申请页面'
  return '返回工作台'
})

// 3. 核心分流跳转逻辑
const handleBack = () => {
  if (!hasToken.value) {
    // 游客：跳转到门户首页
    router.push('/')
  } else if (isAdmin.value) {
    // 管理员/专员：跳转到后台主控台
    router.push('/index/dashboard')
  } else if (isEnterprise.value) {
    // 企业用户：跳转到其专属的企业入驻页面
    router.push('/my-enterprise')
  } else {
    // 兜底方案：返回首页
    router.push('/')
  }
}

// 4. 登出并返回登录页
const handleToLogin = () => {
  userStore.logout() // 清理 Pinia 和 LocalStorage 状态
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

/* 适配 UI 蓝色风格 */
:deep(.el-button--primary) {
  background-color: #0082ec;
  border-color: #2895ef;
}

:deep(.el-button--primary:hover) {
  background-color: #1a92f0;
  border-color: #1a92f0;
}

.footer-copyright {
  position: absolute;
  bottom: 30px;
  font-size: 13px;
  color: #c0c4cc;
}
</style>