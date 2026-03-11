<template>
  <div class="login-container" :class="[isDark ? 'dark-theme' : 'light-theme']">
    <div class="theme-toggle" @click="isDark = !isDark">
      <el-icon :size="20">
        <Sunny v-if="isDark"/>
        <Moon v-else/>
      </el-icon>
    </div>

    <div class="login-wrapper">
      <div class="login-left">
        <div class="brand-header">
          <div class="logo-icon">
            <el-icon :size="18">
              <OfficeBuilding/>
            </el-icon>
          </div>
          <div class="brand-text">
            <span class="brand-name">经济开发区管理平台</span>
          </div>
        </div>

        <div class="illustration-content">
          <img src="@/assets/ILLUSTRATION.png" alt="illustration" class="login-3d-img">
        </div>

        <div class="brand-footer">
          <h1 class="brand-title">智慧园区<br><span class="highlight">数创未来</span></h1>
          <p class="brand-desc">精准监管，高效服务，赋能产业升级。</p>
        </div>
      </div>

      <div class="login-right">
        <!-- 响应式小屏幕标题 -->
        <div class="responsive-title">
          <div class="logo-icon">
            <el-icon :size="18">
              <OfficeBuilding/>
            </el-icon>
          </div>
          <span class="text">经济开发区管理平台</span>
        </div>

        <div class="login-content">
          <div class="segmented-tabs">
            <div
                class="tab-item"
                :class="{ active: loginMode === 'account' }"
                @click="loginMode = 'account'"
            >
              账号密码登录
            </div>
            <div
                class="tab-item"
                :class="{ active: loginMode === 'mobile' }"
                @click="loginMode = 'mobile'"
            >
              手机验证登录
            </div>
          </div>

          <div class="welcome-text">
            <h2>欢迎回来</h2>
            <p>请输入您的登录凭据以访问系统</p>
          </div>

          <el-form label-width="0" size="large" class="custom-form">
            <div class="input-label">{{ loginMode === 'account' ? '账号' : '手机号码' }}</div>
            <el-form-item class="custom-input-item">
              <el-input
                  v-model="username"
                  :placeholder="loginMode === 'account' ? '请输入账号' : '请输入手机号'"
              >
                <template #prefix>
                  <el-icon>
                    <User/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="input-label">{{ loginMode === 'account' ? '密码' : '验证码' }}</div>
            <el-form-item class="custom-input-item">
              <el-input
                  v-model="password"
                  :type="loginMode === 'account' ? 'password' : 'text'"
                  :placeholder="loginMode === 'account' ? '请输入密码' : '请输入 6 位验证码'"
                  class="verify-input"
              >
                <template #prefix>
                  <el-icon>
                    <Lock/>
                  </el-icon>
                </template>
                <template #append v-if="loginMode === 'mobile'">
                  <el-button link class="code-btn-custom">获取验证码</el-button>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item class="custom-input-item slider-form-item">
              <div class="slider-track" ref="sliderTrack">
                <div class="slider-fill" :style="{ width: sliderWidth + 'px' }"></div>
                <div class="slider-text" :class="{ 'is-passed': isPassed }">
                  {{ isPassed ? '验证通过' : '请按住滑块，拖到最右边' }}
                </div>
                <div
                    class="slider-btn"
                    @mousedown="onSliderStart"
                    @touchstart="onSliderStart"
                    :style="{ transform: `translateX(${sliderWidth}px)` }"
                    :class="{ 'is-passed': isPassed }"
                >
                  <el-icon v-if="!isPassed">
                    <DArrowRight/>
                  </el-icon>
                  <el-icon v-else>
                    <CircleCheckFilled/>
                  </el-icon>
                </div>
              </div>
            </el-form-item>

            <div class="form-footer-actions">
              <el-checkbox v-model="rememberMe" class="custom-checkbox">记住我</el-checkbox>
              <el-link type="primary" :underline="false" class="forget-pwd">忘记密码？</el-link>
            </div>

            <el-form-item class="submit-item">
              <el-button
                  type="primary"
                  @click="handleLogin"
                  class="submit-btn"
                  :disabled="!isPassed"
              >
                登 录
                <el-icon class="icon-right">
                  <Right/>
                </el-icon>
              </el-button>
            </el-form-item>

            <div class="register-link">
              还没有账号？
              <el-link type="primary" :underline="false" @click="goToRegister">立即注册</el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onUnmounted} from 'vue'
import {useUserStore} from '@/store/user.js'
import {loginApi} from '@/api/login.js'
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import {DArrowRight, CircleCheckFilled, OfficeBuilding, User, Lock, Right, Sunny, Moon} from '@element-plus/icons-vue'

const loginMode = ref('account')
const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const isDark = ref(false) // 默认明亮模式
const userStore = useUserStore()
const router = useRouter()

// --- 滑动验证逻辑 ---
const isPassed = ref(false)
const sliderWidth = ref(0)
const sliderTrack = ref(null)
let isDragging = false
let startX = 0

const onSliderStart = (e) => {
  if (isPassed.value) return
  isDragging = true
  startX = (e.type === 'mousedown' ? e.clientX : e.touches[0].clientX) - sliderWidth.value
  window.addEventListener('mousemove', onSliderMove)
  window.addEventListener('touchmove', onSliderMove, {passive: false})
  window.addEventListener('mouseup', onSliderEnd)
  window.addEventListener('touchend', onSliderEnd)
}

const onSliderMove = (e) => {
  if (!isDragging) return
  if (e.type === 'touchmove') e.preventDefault()
  const currentX = e.type === 'mousemove' ? e.clientX : e.touches[0].clientX
  let moveX = currentX - startX
  const maxWidth = sliderTrack.value.offsetWidth - 50
  if (moveX < 0) moveX = 0
  if (moveX >= maxWidth) {
    moveX = maxWidth
    isPassed.value = true
    onSliderEnd()
  }
  sliderWidth.value = moveX
}

const onSliderEnd = () => {
  isDragging = false
  if (!isPassed.value) sliderWidth.value = 0
  window.removeEventListener('mousemove', onSliderMove)
  window.removeEventListener('touchmove', onSliderMove)
  window.removeEventListener('mouseup', onSliderEnd)
  window.removeEventListener('touchend', onSliderEnd)
}

// --- 业务逻辑 ---
const handleLogin = async () => {
  if (!isPassed.value) return
  const res = await loginApi({
    username: username.value,
    password: password.value,
    mode: loginMode.value
  })

  if (res.bizCode === 0) {
    userStore.setToken(res.data.token)
    ElMessage.success('登录成功')
    router.push('/index')
  } else {
    ElMessage.error(res.msg)
    isPassed.value = false
    sliderWidth.value = 0
  }
}

const goToRegister = () => ElMessage.info('跳转至注册页面')

onUnmounted(() => onSliderEnd())
</script>

<style scoped>
/* ================== 主题基础变量 ================== */
.light-theme {
  --bg-color: #fbfbfb;
  --bg-gradient: radial-gradient(circle at center, #e8e8e8 0%, #ffffff 100%);
  --card-bg-left: linear-gradient(145deg, #2767ac, #3276bd);
  --card-bg-right: rgba(255, 255, 255, 0.9);
  --text-main: #1e293b;
  --text-sub: #64748b;
  --input-bg: #f8fafc;
  --input-border: rgba(0, 0, 0, 0.1);
  --slider-bg: #f1f5f9;
  --toggle-color: #1e293b;
  --border-color: rgba(0, 0, 0, 0.08);
}

.dark-theme {
  --bg-color: #050d14;
  --bg-gradient: radial-gradient(circle at center, #09141d 0%, #050d14 100%);
  --glass-bg: rgba(255, 255, 255, 0.04);
  --card-bg-left: linear-gradient(145deg, rgba(21, 29, 41, 0.8), rgba(13, 19, 28, 0.8));
  --card-bg-right: rgba(23, 33, 38, 0.85);
  --text-main: #ffffff;
  --text-sub: #8a99a0;
  --input-bg: rgba(0, 0, 0, 0.2);
  --input-border: rgba(255, 255, 255, 0.1);
  --slider-bg: rgba(0, 0, 0, 0.3);
  --toggle-color: #ffffff;
  --border-color: rgba(255, 255, 255, 0.08);
}

/* ================== 基础布局 ================== */
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-color);
  background-image: var(--bg-gradient);
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  transition: all 0.3s ease;
}

.theme-toggle {
  position: absolute;
  top: 30px;
  right: 40px;
  cursor: pointer;
  z-index: 100;
  background: var(--glass-bg);
  color: var(--text-main);
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color);
}


.login-wrapper {
  display: flex;
  align-items: center;
  gap: 15px;
  width: 1050px;
  max-width: 95%;
}

.login-left, .login-right {
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  background: var(--glass-bg);
  transition: background 0.3s;
}

.login-left {
  width: 600px;
  height: 580px;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: var(--card-bg-left);
}

.login-right {
  width: 450px;
  height: 580px;
  padding: 40px;
  background: var(--card-bg-right);
  display: flex;
  flex-direction: column;
}

/* ================== 响应式小屏幕样式 ================== */
.responsive-title {
  display: none;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 24px;
}

.responsive-title .logo-icon {
  width: 34px;
  height: 34px;
  background: #3b82f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.responsive-title .text {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
}

@media (max-width: 800px) {
  .login-left {
    display: none !important;
  }

  .login-wrapper {
    justify-content: center;
  }

  .login-right {
    width: 100%;
    max-width: 420px;
    height: auto;
  }

  .responsive-title {
    display: flex;
  }
  .theme-toggle {
    position: absolute;
    top: 5px;
    right: 40px;
  }
}

/* ================== 插画与动画 ================== */
.illustration-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-3d-img {
  max-width: 90%;
  max-height: 280px;
  object-fit: contain;
  animation: floating 3.5s ease-in-out infinite;
}

@keyframes floating {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

/* ================== 表单元素 ================== */
.input-label {
  color: var(--text-main);
  font-size: 12px;
  margin-bottom: 8px;
}

.welcome-text h2 {
  color: var(--text-main);
  margin-bottom: 8px;
  font-size: 20px;
}

.welcome-text p {
  color: var(--text-sub);
  font-size: 12px;
  margin-bottom: 24px;
}

.register-link {
  text-align: center;
  margin-top: 15px;
  font-size: 13px;
  color: var(--text-sub);
}

:deep(.el-input__wrapper) {
  background-color: transparent !important;
  background-image: none !important;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  box-shadow: none !important;
  transition: all 0.3s ease;
  &.is-focus {
    box-shadow: 0 0 0 1px #3b82f6 inset !important;
  }
}

:deep(.el-input__inner) {
  background-color: transparent !important;
  &:-webkit-autofill {
    transition: background-color 5000s ease-in-out 0s !important;
    -webkit-text-fill-color: #606266 !important;
  }
}

/* 验证码按钮背景 */
:deep(.el-input-group__append) {
  background-color: transparent !important;
  box-shadow: none !important;
  border-top: rgba(83, 83, 83, 0.21) 1px solid;
  border-bottom: rgba(83, 83, 83, 0.21) 1px solid;
  border-right: rgba(83, 83, 83, 0.21) 1px solid;
  padding: 0 30px;
}

.code-btn-custom {
  color: #3b82f6 !important;
  font-size: 12px;
  background: transparent !important;
}

/* ================== 滑块 ================== */
.slider-form-item {
  height: 42px;
  margin-top: 10px;
  margin-bottom: 22px;
}

.slider-track {
  background-color: var(--slider-bg);
  border: 1px solid var(--input-border);
  height: 42px;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.slider-fill {
  position: absolute;
  height: 100%;
  background: rgba(59, 130, 246, 0.2);
  z-index: 1;
}

.slider-text {
  position: absolute;
  width: 100%;
  height: 100%;
  text-align: center;
  line-height: 40px;
  font-size: 13px;
  color: var(--text-sub);
  z-index: 2;
  user-select: none;
}

.slider-text.is-passed {
  color: #3b82f6;
}

.slider-btn {
  position: absolute;
  width: 50px;
  height: 42px;
  top: 0;
  left: 0;
  background: #2e3b45;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 3;
  color: #fff;
}

.light-theme .slider-btn {
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  color: #999;
}

.slider-btn.is-passed {
  background: #3b82f6 !important;
  color: #fff !important;
}

/* ================== 其他 ================== */
.brand-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 34px;
  height: 34px;
  background: #3b82f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.brand-name {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.brand-title {
  color: #fff;
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 10px;
}

.brand-title .highlight {
  color: rgb(255, 255, 255);
}

.brand-desc {
  color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  line-height: 1.5;
}

.segmented-tabs {
  display: flex;
  background: rgba(128, 128, 128, 0.1);
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 24px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 13px;
  color: var(--text-sub);
  cursor: pointer;
  border-radius: 6px;
}

.tab-item.active {
  background: #3b82f6;
  color: #fff;
}

.form-footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  margin-top: -10px;
}

.forget-pwd {
  font-size: 12px;
  color: #3b82f6 !important;
}

:deep(.custom-checkbox .el-checkbox__label) {
  color: var(--text-sub);
  font-size: 12px;
}

.submit-btn {
  width: 100%;
  height: 42px;
  background: #3b82f6 !important;
  border: none;
  font-weight: bold;
}

.submit-btn:disabled {
  background: rgba(59, 130, 246, 0.3) !important;
  color: rgb(255, 255, 255);
  cursor: not-allowed;
}
</style>