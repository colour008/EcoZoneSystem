<template>
  <div class="login-container" :class="[isDark ? 'dark-theme' : 'light-theme']">
    <div class="login-left">
      <div class="brand-logo">
        <div class="logo-icon">
          <el-icon :size="20">
            <OfficeBuilding/>
          </el-icon>
        </div>
        <span class="brand-name">经济开发区管理平台</span>
      </div>

      <div class="illustration-content">
        <img src="@/assets/ILLUSTRATION.png" alt="illustration" class="login-3d-img"/>
        <h2 class="illustration-title">开箱即用的大型中后台管理系统</h2>
        <p class="illustration-desc">工程化、高性能、跨组件库的前端模版</p>
      </div>
    </div>

    <div class="login-right">
      <div class="top-actions">
        <div class="action-pill">
          <div class="action-icon" @click="goToLogin" title="返回登录">
            <el-icon :size="16">
              <Back/>
            </el-icon>
          </div>

          <div class="action-icon" @click="isDark = !isDark">
            <el-icon :size="16">
              <Sunny v-if="!isDark"/>
              <Moon v-else/>
            </el-icon>
          </div>
        </div>
      </div>

      <div class="responsive-title">
        <div class="logo-icon">
          <el-icon :size="20">
            <OfficeBuilding/>
          </el-icon>
        </div>
        <span class="brand-name">经济开发区管理平台</span>
      </div>

      <div class="form-wrapper">
        <div class="welcome-text">
          <h2>创建新账户 🚀</h2>
          <p>欢迎加入经济开发区管理平台</p>
        </div>

        <el-form ref="registerFormRef" :model="regForm" :rules="regRules" label-width="0" size="large">
          <div class="floating-group" :class="{ 'is-floating': regForm.username }">
            <span class="floating-label">设置用户名 (4-20位)</span>
            <el-form-item prop="username" class="custom-input-item">
              <el-input v-model="regForm.username">
                <template #prefix>
                  <el-icon>
                    <User/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="floating-group" :class="{ 'is-floating': regForm.password }">
            <span class="floating-label">设置密码</span>
            <el-form-item prop="password" class="custom-input-item">
              <el-input v-model="regForm.password" type="password" show-password>
                <template #prefix>
                  <el-icon>
                    <Lock/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="floating-group" :class="{ 'is-floating': regForm.confirmPassword }">
            <span class="floating-label">确认密码</span>
            <el-form-item prop="confirmPassword" class="custom-input-item">
              <el-input v-model="regForm.confirmPassword" type="password" show-password>
                <template #prefix>
                  <el-icon>
                    <CircleCheck/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="floating-group" :class="{ 'is-floating': regForm.realName }">
            <span class="floating-label">您的姓名</span>
            <el-form-item prop="realName" class="custom-input-item">
              <el-input v-model="regForm.realName">
                <template #prefix>
                  <el-icon>
                    <Postcard/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="floating-group" :class="{ 'is-floating': regForm.phone }">
            <span class="floating-label">手机号码</span>
            <el-form-item prop="phone" class="custom-input-item">
              <el-input v-model="regForm.phone">
                <template #prefix>
                  <el-icon>
                    <Iphone/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <el-form-item class="submit-item">
            <el-button type="primary" :loading="loading" @click="handleRegister" class="submit-btn">
              注 册
            </el-button>
          </el-form-item>

          <div class="register-link">
            已有账号？
            <el-link type="primary" underline="never" @click="router.push('/login')">立即登录</el-link>
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
import {ref, reactive, computed} from 'vue'
import {useRouter} from 'vue-router'
import {registerApi} from '@/api/login'
import {ElMessage} from 'element-plus'
import {Back, CircleCheck, Iphone, Lock, Moon, OfficeBuilding, Postcard, Sunny, User} from "@element-plus/icons-vue";

const router = useRouter()
const loading = ref(false)
const registerFormRef = ref(null)
const isDark = ref(false) // 默认采用亮色模式

const regForm = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  phone: ''
})


// 表单校验
const regRules = {
  username: [{required: true, message: '请输入用户名', trigger: 'blur'}, {
    min: 4,
    max: 20,
    message: '长度在4-20位',
    trigger: 'blur'
  }],
  realName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}, {
    min: 6,
    message: '密码不少于6位',
    trigger: 'blur'
  }],
  confirmPassword: [
    {required: true, message: '请再次确认密码', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (value !== regForm.password) callback(new Error('两次输入密码不一致'))
        else callback()
      }, trigger: 'blur'
    }
  ],
  phone: [{pattern: /^1[0-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur'}]
}

// 判断所有字段是否都已经填写且符合基本规则
const isFormValid = computed(() => {
  return (
      regForm.username.length >= 4 &&
      regForm.username.length <= 20 &&
      regForm.realName.trim() !== '' &&
      regForm.password.length >= 6 &&
      regForm.confirmPassword === regForm.password &&
      /^1[0-9]\d{9}$/.test(regForm.phone)
  )
})


// 注册逻辑
const handleRegister = async () => {
  if (!registerFormRef.value) return

  // 1. 执行表单校验
  // validate 方法会触发页面上所有 prop 对应规则的校验，并自动显示红字提示
  await registerFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      // 校验通过，执行注册逻辑
      loading.value = true
      try {
        const res = await registerApi(regForm)
        ElMessage.success('注册成功，请登录')
        setTimeout(() => {
          router.push('/login')
        }, 1200)
      } catch (error) {
        console.error('注册请求失败:', error)
      } finally {
        loading.value = false
      }
    } else {
      // 校验不通过
      // fields 包含了具体哪个字段没过，我们可以精准提示
      const firstError = Object.values(fields)[0][0].message
      ElMessage.warning(`请检查输入: ${firstError}`)
      console.log('表单校验未通过:', fields)
    }
  })
}

// “返回登录”的按钮
const goToLogin = () => {
  router.push('/login')
}
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
  background: var(--bg-left);
  display: flex;
  flex-direction: column;
  position: relative;
  transition: background 0.3s ease;
}

.login-right {
  width: 700px;
  background-color: var(--bg-right);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  transition: background-color 0.3s ease;
}

/* ================== 左侧品牌与插画 ================== */
.brand-logo {
  position: absolute;
  top: 30px;
  left: 40px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-section img {
  width: 32px;
  height: 32px;
  background: #1677ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
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

/* ================== 右侧表单区域布局 ================== */
.top-actions {
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

.form-wrapper {
  flex: 1;
  width: 100%;
  max-width: 450px;
  display: flex;
  flex-direction: column;
  justify-content: center;
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


/* 1. 基础自动填充覆盖：利用内阴影覆盖背景色 */
:deep(.el-input__inner:-webkit-autofill) {
  /* 强制文本颜色，防止在暗色下文字变成黑色看不见 */
  -webkit-text-fill-color: var(--text-main) !important;
}

/* 2. 针对暗色主题的特殊处理 */
.dark-theme :deep(.el-input__inner:-webkit-autofill) {
  /* 使用内阴影将背景色填满，颜色建议与输入框背景或容器背景一致 */
  -webkit-box-shadow: 0 0 0 1000px var(--bg-right) inset !important;
}

/* 3. 针对亮色主题的处理 */
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

.register-link {
  text-align: center;
  font-size: 14px;
  color: var(--text-sub);
}

/* ================== 浮动标签核心样式 ================== */
.floating-group {
  position: relative;
  margin-bottom: 20px; /* 调整间距 */
}

.floating-label {
  position: absolute;
  left: 38px; /* 避开左侧 prefix 图标的位置 */
  top: 12px;
  font-size: 14px;
  color: var(--text-sub);
  transition: all 0.2s ease;
  pointer-events: none; /* 重要：点击穿透到 input */
  z-index: 10;
}

/* 当输入框获得焦点，或者里面有内容时，标签上浮 */
.floating-group:focus-within .floating-label,
.floating-group.is-floating .floating-label {
  top: -10px;
  left: 8px;
  font-size: 12px;
  color: var(--text-sub); /* 激活时的颜色 */
  background-color: var(--bg-right); /* 背景遮盖边框线 */
  padding: 0 4px;
}

/* 覆盖 Input 的 placeholder，因为我们有了浮动标签 */
:deep(.el-input__inner::placeholder) {
  color: transparent !important;
}

/* 调整错误提示位置，防止遮挡下一个标签 */
:deep(.el-form-item__error) {
  position: absolute;
  top: 100%;
  padding-top: 4px; /* 增加一点与输入框的距离 */
  line-height: 1; /* 压缩行高，防止占用太多垂直空间 */
}

.custom-input-item {
  margin-bottom: 35px !important;
}

/* 修改图标颜色，使其更柔和 */
:deep(.el-input__prefix-inner) {
  font-size: 16px;
  color: var(--text-sub);
}

/* 页脚 */
.footer-copyright {
  position: absolute;
  bottom: 20px;
  font-size: 12px;
  color: var(--text-sub);
}

/* 注册按钮 */
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s;
}

/* ================== 响应式小屏幕适配 ================== */
.responsive-title {
  display: none;
  align-items: center;
  gap: 10px;
  width: 100%;
  max-width: 360px;
}

@media (max-width: 900px) {
  .login-left {
    display: none !important;
  }

  .login-right {
    width: 100%;
  }

  .responsive-title {
    display: flex;
  }
}
</style>