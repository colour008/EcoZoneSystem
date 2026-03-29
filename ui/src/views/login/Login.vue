<template>
  <div class="login-container" :class="[isDark ? 'dark-theme' : 'light-theme']">

    <div class="login-left">
      <div class="brand-logo">
        <div class="logo-icon">
          <el-icon :size="25" color="var(--text-main)">
            <OfficeBuilding/>
          </el-icon>
        </div>
        <span class="brand-name">经济开发区管理平台</span>
        <el-button type="primary" size="default" icon="Back" @click="router.push('/home')"
                   class="back-btn">
          返回首页
        </el-button>
      </div>

      <div class="illustration-content">
        <img src="@/assets/ILLUSTRATION.png" alt="illustration" class="login-3d-img"/>
        <h2 class="illustration-title">数字赋能园区，智慧连接未来</h2>
        <p class="illustration-desc">数字化赋能政务，让企业服务更高效</p>
      </div>
    </div>

    <div class="login-right">
      <div class="mobile-top-bar">
        <el-button type="primary" size="small" icon="Back" @click="router.push('/home')"
                   class="mobile-back-btn">
          返回首页
        </el-button>
        <span class="mobile-brand-name">经济开发区管理平台</span>
        <div class="action-icon mobile-theme-toggle" @click="isDark = !isDark">
          <el-icon :size="16">
            <Sunny v-if="!isDark"/>
            <Moon v-else/>
          </el-icon>
        </div>
      </div>

      <div class="desktop-top-actions">
        <div class="action-pill">
          <div class="action-icon" @click="isDark = !isDark">
            <el-icon :size="16">
              <Sunny v-if="!isDark"/>
              <Moon v-else/>
            </el-icon>
          </div>
        </div>
      </div>

      <div class="form-wrapper">
        <div class="welcome-text">
          <h2>欢迎加入经济开发区 👋</h2>
          <p style="font-size: 15px">请登录账号后提交申请入驻资料</p>
        </div>

        <el-form label-width="0" size="large" class="custom-form">
          <template v-if="loginMode === 'account'">
            <el-form-item class="custom-input-item">
              <el-input v-model="username" placeholder="请输入账号">
                <template #prefix>
                  <el-icon>
                    <User/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item class="custom-input-item">
              <el-input v-model="password" type="password" placeholder="请输入密码" show-password>
                <template #prefix>
                  <el-icon>
                    <Lock/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </template>

          <template v-if="loginMode === 'mobile'">
            <el-form-item class="custom-input-item">
              <el-input v-model="username" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item class="custom-input-item">
              <el-input v-model="password" placeholder="请输入 6 位验证码">
                <template #append>
                  <el-button link class="code-btn-custom">获取验证码</el-button>
                </template>
              </el-input>
            </el-form-item>
          </template>

          <el-form-item class="slider-form-item">
            <div class="slider-track" ref="sliderTrack">
              <div class="slider-fill" :style="{ width: sliderWidth + 'px' }"></div>
              <div class="slider-text" :class="{ 'is-passed': isPassed }">
                {{ isPassed ? '验证通过' : '请按住滑块拖动' }}
              </div>
              <div
                  class="slider-btn"
                  @mousedown="onSliderStart"
                  @touchstart="onSliderStart"
                  :style="{ transform: `translateX(${sliderWidth}px)` }"
                  :class="{ 'is-passed': isPassed }"
              >
                <el-icon v-if="!isPassed" :size="14">
                  <DArrowRight/>
                </el-icon>
                <el-icon v-else :size="14" style="color: #929496">
                  <CircleCheckFilled/>
                </el-icon>
              </div>
            </div>
          </el-form-item>

          <div class="form-footer-actions">
            <el-checkbox v-model="rememberMe" class="custom-checkbox">记住账号</el-checkbox>
            <el-link type="primary" underline="never" class="forget-pwd">忘记密码？</el-link>
          </div>

          <el-form-item class="submit-item">
            <el-button type="primary" @click="handleLogin" class="submit-btn" :disabled="!isPassed"
                       :loading="loginLoading">
              登 录
            </el-button>
          </el-form-item>

          <div class="alt-login-buttons">
            <el-button class="alt-btn" @click="loginMode = 'account'">账号登录</el-button>
            <el-button class="alt-btn" @click="loginMode = 'mobile'">手机号登录</el-button>
          </div>

          <div class="third-party-login">
            <el-divider class="custom-divider">其他登录方式</el-divider>
            <div class="social-icons">
              <div class="social-icon wechat">
                <svg viewBox="0 0 1024 1024" width="20" height="20">
                  <path
                      d="M682.666667 362.666667c-174.933333 0-320 123.733333-320 277.333333 0 85.333333 46.933333 162.133333 123.733333 213.333333l-34.133333 68.266667 81.066667-42.666667c46.933333 17.066667 98.133333 25.6 149.333333 25.6 174.933333 0 320-123.733333 320-277.333333S857.6 362.666667 682.666667 362.666667z m-110.933334 162.133333c-17.066667 0-34.133333-12.8-34.133333-29.866667s17.066667-29.866667 34.133333-29.866667 34.133333 12.8 34.133333 29.866667-17.066667 29.866667-34.133333 29.866667z m221.866667 0c-17.066667 0-34.133333-12.8-34.133333-29.866667s17.066667-29.866667 34.133333-29.866667 34.133333 12.8 34.133333 29.866667-17.066667 29.866667-34.133333 29.866667z M362.666667 588.8c0-8.533333 0-17.066667 4.266666-25.6C209.066667 541.866667 85.333333 422.4 85.333333 277.333333 85.333333 123.733333 243.2 0 426.666667 0s341.333333 123.733333 341.333333 277.333333c0 29.866667-4.266667 55.466667-12.8 81.066667-21.333333-4.266667-42.666667-4.266667-64-4.266667-200.533333 0-362.666667 149.333333-362.666667 334.933334 0 34.133333 8.533333 64 21.333334 93.866666-4.266667 4.266667-12.8 4.266667-17.066667 4.266667-46.933333 0-89.6-12.8-128-34.133334l-68.266666 38.4 29.866666-59.733333c-64-46.933333-102.4-110.933333-102.4-187.733334z m-106.666667-362.666667c-21.333333 0-38.4-17.066667-38.4-38.4s17.066667-38.4 38.4-38.4 38.4 17.066667 38.4 38.4-17.066667 38.4-38.4 38.4z m315.733333 0c-21.333333 0-38.4-17.066667-38.4-38.4s17.066667-38.4 38.4-38.4 38.4 17.066667 38.4 38.4-17.066667 38.4-38.4 38.4z"
                      fill="#07C160"></path>
                </svg>
              </div>
              <div class="social-icon github">
                <svg viewBox="0 0 1024 1024" width="20" height="20">
                  <path
                      d="M511.6 76.3C264.3 76.2 64 276.4 64 523.5 64 718.9 189.3 885 363.8 946c23.5 5.9 19.9-10.8 19.9-22.2v-77.5c-135.7 15.9-141.2-73.9-150.3-88.9C215 726 171.5 718 184.5 703c30.9-15.9 62.4 4 98.9 57.9 26.4 39.1 77.9 32.5 104 26 5.7-23.5 17.9-44.5 34.7-60.8-140.6-25.2-199.2-111-199.2-213 0-49.5 16.3-95 48.3-131.7-20.4-60.5 1.9-112.3 4.9-120 58.1-5.2 118.5 41.6 123.2 45.3 33-8.9 70.7-13.6 112.9-13.6 42.4 0 80.2 4.9 113.5 13.9 11.3-8.6 67.3-48.8 121.3-43.9 2.9 7.7 24.7 58.3 5.5 118 32.4 36.8 48.9 82.7 48.9 132.3 0 102.2-59 188.1-200 212.9 23.5 23.2 38.1 55.4 38.1 91v112.5c0.8 9 0 27.9 25 21.5C898.1 872.4 1024 737.5 1024 523.5c0-246.7-199.9-447.2-446.8-447.2h-65.6z"
                      :fill="isDark ? '#FFFFFF' : '#1E293B'"></path>
                </svg>
              </div>
              <div class="social-icon google">
                <svg viewBox="0 0 1024 1024" width="20" height="20">
                  <path
                      d="M523.8 438.9v164.2h255.4c-10.4 53.6-40.4 99.2-85.3 131.4v109.1h138c80.8-74.4 127.4-184.1 127.4-311.2 0-21.7-1.9-42.6-5.6-62.8L523.8 438.9z"
                      fill="#4285F4"></path>
                  <path
                      d="M512 1024c136 0 250.2-45.1 333.6-122l-138-109.1c-45.1 30.2-102.7 48.1-195.6 48.1-150.3 0-277.5-101.5-322.9-238h-143v110.6C129.5 869 304.7 1024 512 1024z"
                      fill="#34A853"></path>
                  <path
                      d="M189.1 603c-11.4-34-18-70.5-18-108.3s6.6-74.3 18-108.3V275.8h-143C17.2 334.3 0 411.3 0 494.7s17.2 160.4 46.1 218.9l143-110.6z"
                      fill="#FBBC05"></path>
                  <path
                      d="M512 189.3c74 0 140.5 25.4 192.8 75.3l144.6-144.6C761.9 41.5 647.7 0 512 0 304.7 0 129.5 155 46.1 310.5l143 110.6C234.5 284.6 361.7 189.3 512 189.3z"
                      fill="#EA4335"></path>
                </svg>
              </div>
            </div>

            <div class="register-link">
              还没有账号？
              <el-link type="primary" underline="never" @click="goToRegister">注册账号</el-link>
            </div>
          </div>
        </el-form>
      </div>

      <div class="footer-copyright">
        Copyright © 2026 JamHoo
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
import {
  DArrowRight,
  CircleCheckFilled,
  OfficeBuilding,
  Sunny,
  Moon,
  User,
  Lock, Back
} from '@element-plus/icons-vue'

const loginMode = ref('account')
const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const isDark = ref(false) // 默认采用亮色模式
const userStore = useUserStore()
const router = useRouter()

// --- 滑动验证逻辑 ---
const isPassed = ref(false)
const sliderWidth = ref(0)
const sliderTrack = ref(null)
const loginLoading = ref(false) // 定义 loading 状态
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
  // 减去滑块自身的宽度，保证滑块不溢出轨道
  const maxWidth = sliderTrack.value.offsetWidth - 42
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

// ================== 核心修改逻辑开始 ==================
/**
 * 判断是否为移动端环境
 */
const isMobileDevice = () => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  if (!isPassed.value || loginLoading.value) return

  loginLoading.value = true
  try {
    const res = await loginApi({
      username: username.value,
      password: password.value,
      mode: loginMode.value
    })

    const {token, user, roles, permissions} = res.data

    // 存储用户信息
    userStore.setToken(token)
    userStore.setUserInfo(user)
    userStore.setRoles(roles)
    userStore.setPermissions(permissions)

    ElMessage.success('欢迎回来')

    setTimeout(() => {
      const isMobile = isMobileDevice()
      const redirectPath = router.currentRoute.value.query.redirect

      // 检查 roles 数组中是否包含 STAFF 或 WORKER
      const isStaffOrWorker = roles.some(role => ['ROLE_STAFF', 'ROLE_WORKER'].includes(role))

      if (isStaffOrWorker) {
        // 员工/工人角色，强制忽略 redirect，直接跳对应页面
        if (isMobile) {
          // 移动端：强制跳工单列表
          router.push('/m/worker/list')
        } else {
          // PC端：强制跳管理工作台，不使用redirect
          router.push('/home')
        }
        return
      }

      // 普通用户/企业角色，才走重定向逻辑
      router.push(redirectPath || '/home')

    }, 200)

  } catch (error) {
    isPassed.value = false
    sliderWidth.value = 0
    console.error('登录异常:', error)
    ElMessage.error('登录失败，请检查账号密码')
  } finally {
    loginLoading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}

onUnmounted(() => onSliderEnd())
</script>

<style scoped>
/* ================== 主题基础变量 ================== */
.light-theme {
  --bg-left: linear-gradient(135deg, #e4eaf4 0%, #f4f7fc 100%);
  --bg-right: #ffffff;
  --text-main: #404040;
  --text-sub: #64748b;
  --input-bg: #ffffff;
  --input-border: #dcdfe6;
  --input-focus: #ffffff;
  --slider-bg: #f8fafc;
  --slider-border: #e2e8f0;
  --slider-btn-bg: #ffffff;
  --slider-btn-icon: #64748b;
  --divider-color: #e2e8f0;
  --tool-bg: #f1f5f9;
  --tool-icon: #64748b;
  --bg-img: url('@/assets/light-img.jpg');
}

.dark-theme {
  --bg-left: #040910;
  --bg-right: #15181c;
  --text-main: #e5e7eb;
  --text-sub: #8a99a0;
  --input-bg: transparent;
  --input-border: #2e3338;
  --input-focus: #2e3033;
  --slider-bg: #1c2024;
  --slider-border: #2e3338;
  --slider-btn-bg: #2e3338;
  --slider-btn-icon: #8a99a0;
  --divider-color: #2e3338;
  --tool-bg: #22262b;
  --tool-icon: #8a99a0;
  --bg-img: url('@/assets/dark-img.jpg');
}

/* ================== 基础整体布局 ================== */
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

.login-left {
  flex: 1;
  background: var(--bg-img) no-repeat center center;
  background-size: cover;
  display: flex;
  flex-direction: column;
  position: relative;
  transition: background 0.3s ease;
}


.login-left::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  backdrop-filter: blur(8px);
  z-index: 1;
}

/* 确保内容在伪元素之上 */
.brand-logo, .illustration-content {
  position: relative;
  z-index: 2;
}

.login-right {
  width: 55%;
  max-width: 700px;
  background-color: var(--bg-right);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  transition: background-color 0.3s ease;
  padding: 30px 0;
}

/* ================== 左侧品牌与插画 ================== */
.brand-logo {
  position: absolute;
  top: 30px;
  left: 40px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  z-index: 10;
}

.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-btn {
  margin-left: 20px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 6px;
}

.brand-name {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-main);
  letter-spacing: 0.5px;
}

.illustration-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 40px;
}

.login-3d-img {
  width: 100%;
  max-width: 420px;
  margin-bottom: 40px;
  object-fit: contain;
  animation: floating 4s ease-in-out infinite;
}

@keyframes floating {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

.illustration-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-main);
  margin: 0 0 12px 0;
  letter-spacing: 1px;
}

.illustration-desc {
  font-size: 15px;
  color: var(--text-sub);
  margin: 0;
}

/* ================== 顶部操作栏 ================== */
/* 移动端顶部栏 */
.mobile-top-bar {
  display: none;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-sizing: border-box;
}

.mobile-back-btn {
  font-size: 12px;
  border-radius: 6px;
}

.mobile-brand-name {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-main);
}

.mobile-theme-toggle {
  width: 32px;
  height: 32px;
  background: var(--tool-bg);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 桌面端顶部操作栏 */
.desktop-top-actions {
  display: flex;
  position: absolute;
  top: 30px;
  right: 40px;
}

.action-pill {
  display: flex;
  align-items: center;
  background: var(--tool-bg);
  border-radius: 20px;
  padding: 6px 14px;
  gap: 16px;
  border: 1px solid var(--divider-color);
}

.action-icon {
  cursor: pointer;
  color: var(--tool-icon);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;
}

.action-icon:hover {
  color: var(--text-main);
}

/* ================== 表单区域布局 ================== */
.form-wrapper {
  flex: 1;
  width: 100%;
  max-width: 450px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 20px;
  box-sizing: border-box;
}

.welcome-text {
  margin-bottom: 30px;
}

.welcome-text h2 {
  color: var(--text-main);
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.welcome-text p {
  color: var(--text-sub);
  font-size: 13px;
  margin: 0;
}

/* ================== 输入框复写 ================== */
.custom-input-item {
  margin-bottom: 22px;
}

:deep(.el-input__wrapper) {
  background-color: transparent !important;
  background-image: none !important;
  box-shadow: 0 0 0 1px var(--input-border) inset !important;
  border-radius: 6px;
  padding: 0 15px;
  transition: all 0.3s ease;
  height: 44px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--input-focus) inset !important;
  border: #637a9c 1px solid !important;
}

/* 自动填充样式覆盖 */
:deep(.el-input__inner:-webkit-autofill) {
  -webkit-text-fill-color: var(--text-main) !important;
}

.dark-theme :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px var(--bg-right) inset !important;
}

.light-theme :deep(.el-input__inner:-webkit-autofill) {
  -webkit-box-shadow: 0 0 0 1000px var(--bg-right) inset !important;
}

:deep(.el-input__inner::placeholder) {
  color: var(--text-sub);
}

:deep(.el-input-group__append) {
  background-color: var(--input-bg) !important;
  box-shadow: 0 0 0 1px var(--input-border) inset !important;
  border-left: none !important;
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
  padding: 0 15px;
}

.code-btn-custom {
  color: var(--text-main) !important;
  font-size: 13px;
  text-align: center;
  margin: 0 auto;
  padding-left: 10px;
}

/* ================== 滑块验证 ================== */
.slider-form-item {
  margin-bottom: 22px;
}

.slider-track {
  background-color: var(--slider-bg);
  border: 1px solid var(--slider-border);
  height: 42px;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  width: 100%;
  transition: border 0.3s, background 0.3s;
}

.slider-fill {
  position: absolute;
  height: 100%;
  background: #31dd77;
  opacity: 0.5;
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
  color: #ffffff;
  background-color: #56cf87;
  border-radius: 6px;
}

.slider-btn {
  position: absolute;
  width: 42px;
  height: 42px;
  top: 0;
  left: 0;
  background: var(--slider-btn-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 3;
  color: var(--slider-btn-icon);
  border-right: 1px solid var(--slider-border);
  transition: background 0.3s;
}

.slider-btn.is-passed {
  background: var(--input-focus) !important;
  color: #fff !important;
  border-right: none;
}

/* ================== 表单操作项 ================== */
.form-footer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 10px;
}

:deep(.custom-checkbox .el-checkbox__label) {
  color: var(--text-sub);
  font-size: 13px;
}

.forget-pwd {
  font-size: 13px;
  color: #1677ff;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 6px;
  font-size: 15px;
  letter-spacing: 2px;
}

/* 其他登录方式按钮 */
.alt-login-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}

.alt-btn {
  flex: 1;
  height: 40px;
  background: transparent !important;
  border: 1px solid var(--input-border) !important;
  color: var(--text-main) !important;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.3s;
}

.alt-btn:hover {
  border-color: rgba(83, 83, 83, 0.21) !important;
  color: #ffffff !important;
  background: #a6d3ff !important;
}

/* 第三方登录 */
:deep(.custom-divider .el-divider__text) {
  background-color: var(--bg-right);
  color: var(--text-sub);
  font-size: 12px;
  padding: 0 10px;
  transition: background-color 0.3s;
}

:deep(.custom-divider.el-divider--horizontal) {
  border-top-color: var(--divider-color);
  margin: 10px 0 20px 0;
}

.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.social-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: opacity 0.3s;
}

.social-icon:hover {
  opacity: 0.8;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: var(--text-sub);
}

.footer-copyright {
  font-size: 12px;
  color: var(--text-sub);
  margin-top: auto;
  padding-bottom: 10px;
}

/* ================== 响应式适配 ================== */
/* 平板端适配 768px - 1200px */
@media (max-width: 1200px) {
  .login-left {
    flex: 0.8;
  }

  .login-right {
    width: 50%;
  }

  .login-3d-img {
    max-width: 320px;
  }

  .illustration-title {
    font-size: 20px;
  }

  .illustration-desc {
    font-size: 14px;
  }

  .form-wrapper {
    max-width: 400px;
  }
}

/* 移动端适配 <768px */
@media (max-width: 768px) {
  .login-left {
    display: none !important;
  }

  .login-right {
    width: 100%;
    max-width: 100%;
    padding: 20px 0;
    height: 100dvh; /* 适配移动端动态视口高度 */
  }

  .mobile-top-bar {
    display: flex;
  }

  .desktop-top-actions {
    display: none;
  }

  .form-wrapper {
    max-width: 100%;
    padding: 0 24px;
    margin-top: 20px;
  }

  .welcome-text h2 {
    font-size: 28px;
  }

  .welcome-text p {
    font-size: 14px;
  }

  /* 触摸尺寸优化 */
  :deep(.el-input__wrapper) {
    height: 48px;
  }

  .slider-track {
    height: 48px;
  }

  .slider-btn {
    width: 48px;
    height: 48px;
  }

  .slider-text {
    line-height: 46px;
    font-size: 14px;
  }

  .submit-btn {
    height: 48px;
    font-size: 16px;
  }

  .alt-btn {
    height: 44px;
    font-size: 14px;
  }

  /* 图标触摸区域放大 */
  .social-icon {
    width: 40px;
    height: 40px;
  }

  .social-icon svg {
    width: 24px;
    height: 24px;
  }

  /* 底部安全距离适配 */
  .footer-copyright {
    padding-bottom: env(safe-area-inset-bottom);
  }
}

/* 超小屏适配 <375px */
@media (max-width: 375px) {
  .welcome-text h2 {
    font-size: 24px;
  }

  .form-wrapper {
    padding: 0 20px;
  }

  .mobile-brand-name {
    font-size: 14px;
  }
}
</style>