<template>
  <header class="portal-header" :class="{ 'header-scroll': isScrolled }">
    <div class="header-container">
      <div class="logo" @click="router.push('/home')">
        <div class="logo-icon">
          <el-icon :size="28" color="#409EFF">
            <OfficeBuilding/>
          </el-icon>
        </div>
        <span class="logo-text">经济开发区管理平台</span>
      </div>

      <nav class="nav-menu">
        <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            router
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/news">园区动态</el-menu-item>
          <el-menu-item index="/policy">政策中心</el-menu-item>
          <el-menu-item v-if="isAdmin || isStaff" index="/my-enterprise">入驻园区</el-menu-item>
          <el-menu-item index="/about">关于我们</el-menu-item>
        </el-menu>
      </nav>

      <div class="header-right">
        <div v-if="!userStore.token" class="auth-btns">
          <el-button link @click="handleLogin">登录</el-button>
          <el-divider direction="vertical"/>
          <el-button type="primary" round size="small" @click="router.push('/register')">
            立即入驻
          </el-button>
        </div>

        <div v-else class="user-info">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar-wrapper">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar"
                         style="background: transparent; border: rgb(250,179,1) 1px outset">
                {{ userStore.userInfo?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="nickname">{{ userStore.userInfo?.realName || '用户' }}</span>
              <el-icon>
                <ArrowDown/>
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                    v-if="isAdmin || isStaff"
                    command="dashboard"
                    icon="Monitor"
                    style="color: #409EFF;"
                >
                  进入管理后台
                </el-dropdown-item>

                <el-dropdown-item
                    v-if="isEnterprise"
                    command="enterprise"
                    icon="Memo"
                    style="color: #67C23A;"
                >
                  企业工作台
                </el-dropdown-item>
                <el-dropdown-item command="profile" icon="User">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout" icon="SwitchButton">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </header>
  <el-dialog
      v-model="profileVisible"
      title="个人资料修改"
      width="500px"
      destroy-on-close
      append-to-body
  >
    <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="100px"
        style="padding: 10px 20px"
    >
      <el-form-item label="用户名">
        <el-input v-model="profileForm.username" disabled/>
        <div style="font-size: 12px; color: #999">账号名不可修改</div>
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="profileForm.realName" placeholder="请输入姓名"/>
      </el-form-item>
      <el-divider content-position="center" class="my-divider">修改密码（不改请留空）</el-divider>
      <el-form-item label="新密码" prop="password">
        <el-input v-model="profileForm.password" type="password" show-password placeholder="请输入新密码"/>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="profileForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"/>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="profileForm.phone" placeholder="请输入手机号"/>
      </el-form-item>
      <el-form-item label="头像">
        <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :http-request="handleProfileAvatarUpload"
        >
          <img v-if="profileForm.avatar" :src="profileForm.avatar" class="profile-avatar" alt="头像"/>
          <el-icon v-else class="avatar-uploader-icon">
            <Plus/>
          </el-icon>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="profileVisible = false">取消</el-button>
      <el-button type="primary" :loading="profileLoading" @click="submitProfile">保存修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, onMounted, onUnmounted, computed} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {useUserStore} from '@/store/user'
import {OfficeBuilding, ArrowDown, Plus} from '@element-plus/icons-vue'
import {ElMessageBox, ElMessage} from 'element-plus'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 角色判断的计算属性
const isAdmin = computed(() => userStore.roles.includes('ROLE_ADMIN'))
const isStaff = computed(() => userStore.roles.includes('ROLE_STAFF'))
const isEnterprise = computed(() => userStore.roles.includes('ROLE_ENTERPRISE'))

const profileVisible = ref(false)
const profileLoading = ref(false)
const profileFormRef = ref(null)
const profileForm = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  avatar: '',
  password: '',
  confirmPassword: ''
})

const profileRules = {
  realName: [{required: true, message: '姓名不能为空', trigger: 'blur'}],
  phone: [{pattern: /^1[0-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur'}],
  password: [{min: 6, message: '密码长度至少为6位', trigger: 'blur'}],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (profileForm.value.password && value !== profileForm.value.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const isScrolled = ref(false)
const activeMenu = computed(() => route.path)

// 监听页面滚动，改变 Header 样式
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// 登录跳转逻辑（核心：带上 redirect）
const handleLogin = () => {
  router.push({
    path: '/login',
    query: {redirect: route.fullPath}
  })
}

const openProfile = () => {
  const {id, username, realName, phone, avatar} = userStore.userInfo
  profileForm.value = {
    id, username, realName, phone, avatar,
    password: '', confirmPassword: ''
  }
  profileVisible.value = true
}

const handleProfileAvatarUpload = async (options) => {
  try {
    const url = await uploadFile(options.file)
    profileForm.value.avatar = url
    ElMessage.success('头像上传成功')
  } catch (e) {
    ElMessage.error('上传失败')
  }
}

const submitProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      profileLoading.value = true
      try {
        const updateData = {...profileForm.value}
        if (!updateData.password) {
          delete updateData.password
          delete updateData.confirmPassword
        }
        await userApi.updateProfile(updateData)
        ElMessage.success('个人资料更新成功')

        // 同步状态到 Pinia
        userStore.setUserInfo({
          ...userStore.userInfo,
          realName: updateData.realName,
          phone: updateData.phone,
          avatar: updateData.avatar
        })
        profileVisible.value = false
      } catch (error) {
        console.error(error)
      } finally {
        profileLoading.value = false
      }
    }
  })
}

// 下拉菜单命令处理
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      openProfile()
      break
    case 'dashboard':
      // 管理员/员工去后台
      router.push('/index/dashboard')
      break
    case 'enterprise':
      // 企业用户去“我的申请/工作台”
      router.push('/my-enterprise')
      break
    case 'logout':
      confirmLogout()
      break
  }
}

// 退出登录
const confirmLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout() // 假设你的 store 里有清除 token 和缓存的方法
    ElMessage.success('已安全退出')
    router.push('/home')
  })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.portal-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 1000;
  transition: all 0.3s;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.header-scroll {
  height: 60px;
  background: #fff;
}

.header-container {
  max-width: 1240px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  margin-right: 40px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  background: linear-gradient(45deg, #1677ff, #409eff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
}

/* 导航 */
.nav-menu {
  flex: 1;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
  background: transparent;
}

:deep(.el-menu-item) {
  font-size: 16px;
  height: 70px !important;
  line-height: 70px !important;
}

.header-scroll :deep(.el-menu-item) {
  height: 60px !important;
  line-height: 60px !important;
}

/* 右侧 */
.header-right {
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}


.user-avatar-wrapper:hover {
  background: #f5f7fa;
}

.nickname {
  font-size: 14px;
  color: #606266;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.auth-btns .el-button--link {
  color: #606266;
}

/* 个人资料对话框样式 */
.profile-avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

:deep(.my-divider .el-divider__text) {
  font-size: 13px;
  color: #f56c6c;
}
</style>