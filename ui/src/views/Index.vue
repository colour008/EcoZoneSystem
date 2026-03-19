<template>
  <div class="layout-container">
    <header class="layout-header">
      <div class="header-left">
        <div class="logo-section" :style="{ width: isCollapse ? '64px' : '210px' }">
          <div class="logo">
            <img src="/icon.png" alt="logo">
          </div>
          <span v-if="!isCollapse" class="logo-text">经济开发区管理平台</span>
        </div>

        <div class="collapse-btn-wrapper" @click="isCollapse = !isCollapse">
          <el-icon class="collapse-btn">
            <Fold v-if="!isCollapse"/>
            <Expand v-else/>
          </el-icon>
        </div>

        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/index/dashboard' }">首页</el-breadcrumb-item>

            <el-breadcrumb-item
                v-for="(item, index) in breadcrumbList"
                :key="item.path"
                v-show="item.meta.title !== '首页'"
            >
              <svg-icon
                  v-if="item.meta.icon"
                  :name="item.meta.icon"
                  style="margin-right: 4px;"
              />
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <div class="header-right">
        <div class="header-action-icons">
          <el-icon title="搜索">
            <Search/>
          </el-icon>
          <el-icon title="全屏" @click="toggleFullScreen">
            <FullScreen v-if="!isFullscreen"/>
            <Aim v-else/>
          </el-icon>
          <el-icon title="通知">
            <Bell/>
          </el-icon>
        </div>

        <el-dropdown trigger="click">
         <span class="user-info">
    <el-avatar :size="30"
               :src="userInfo.avatar || '/DefaultUser.svg'" style="background: #ffffff;border: 1px solid #ec8800"/>
    <span class="username">{{ userInfo.realName || '未知用户' }}</span>
    <el-icon><ArrowDown/></el-icon>
  </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openProfile">
                <el-icon>
                  <User/>
                </el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon>
                  <SwitchButton/>
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <el-dialog
          v-model="profileVisible"
          title="个人资料修改"
          width="500px"
          destroy-on-close
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
          <el-divider content-position="center" class="my-divider">修改密码（不改请留空）
          </el-divider>
          <el-form-item label="新密码" prop="password">
            <el-input v-model="profileForm.password" type="password" show-password placeholder="请输入新密码"/>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="profileForm.confirmPassword" type="password" show-password
                      placeholder="请再次输入新密码"/>
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

    </header>

    <div class="layout-body">
      <aside class="layout-sidebar" :class="{ 'is-collapse': isCollapse }">
        <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            :collapse="isCollapse"
            :unique-opened="true"
        >
        <template v-for="menu in userStore.routes" :key="menu.path">
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
              <svg-icon v-if="menu.icon" :name="menu.icon" />
              <span>{{ menu.menuName }}</span>
            </template>

            <el-menu-item
                v-for="child in menu.children"
                :key="child.path"
                :index="resolvePath(menu.path, child.path)"
            @click="handleMenuClick(child, menu.path)"
            >
              <svg-icon v-if="child.icon" :name="child.icon" />
            <span>{{ child.menuName }}</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item
              v-else
              :index="menu.path"
              @click="handleMenuClick(menu, '')"
          >
            <svg-icon v-if="menu.icon" :name="menu.icon" />
            <span>{{ menu.menuName }}</span>
          </el-menu-item>
        </template>
        </el-menu>
      </aside>

      <main class="layout-main">
        <div class="app-content">
          <router-view v-slot="{ Component, route: currentRoute }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" :key="currentRoute.fullPath"/>
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import {ref, watch, onMounted, computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Fold, Expand, ArrowDown, Odometer, House, Setting, Key,
  Search, FullScreen, Bell, User, SwitchButton, Aim, Plus, UserFilled
} from '@element-plus/icons-vue'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'
import {resolvePath} from '@/utils/path'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const breadcrumbList = ref([])

const isCollapse = ref(false)
const activeMenu = ref(route.path)
const isFullscreen = ref(false)
const profileVisible = ref(false)
const profileLoading = ref(false)
const profileFormRef = ref(null)

// 获取用户信息
const userInfo = computed(() => userStore.userInfo)

const getBreadcrumb = () => {
  let matched = route.matched.filter(item => item.meta && item.meta.title)

  // 核心逻辑：如果当前路由有父级标题（parentTitle），手动把它塞进面包屑数组前面
  const current = matched[matched.length - 1]
  if (current && current.meta.parentTitle) {
    // 构造一个虚拟的父级节点
    const parentNode = {
      path: '', // 目录通常不可点击，所以路径设为空
      meta: { title: current.meta.parentTitle }
    }
    // 插入到当前节点之前
    matched.splice(matched.length - 1, 0, parentNode)
  }

  breadcrumbList.value = matched
}

// 增加菜单点击处理函数
const handleMenuClick = (item, parentPath) => {
  const path = item.path

  // 判断逻辑：
  // 1. 后端返回的 isExternal 字段为 1
  // 2. 路径以 http 或 https 开头
  if (item.isExternal === 1 || path.startsWith('http') || path.startsWith('https')) {
    window.open(path, '_blank')
    return
  }

  // 正常内部路由跳转
  const fullPath = resolvePath(parentPath, path)
  router.push(fullPath)
}

// 初始表单数据
const profileForm = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  avatar: '',
  password: '',
  confirmPassword: ''
})

// 表单校验规则
const profileRules = {
  realName: [{required: true, message: '姓名不能为空', trigger: 'blur'}],
  phone: [
    {pattern: /^1[0-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur'}
  ],
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

// 打开个人中心并回显数据
const openProfile = () => {
  const {id, username, realName, phone, avatar} = userStore.userInfo
  profileForm.value = {
    id, username, realName, phone, avatar,
    password: '', confirmPassword: ''
  }
  profileVisible.value = true
}

// 处理头像上传
const handleProfileAvatarUpload = async (options) => {
  const url = await uploadFile(options.file)
  profileForm.value.avatar = url
}

// 提交修改
const submitProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      profileLoading.value = true
      try {
        const updateData = {...profileForm.value}
        // 如果没填密码，移除密码字段，防止后端误修改
        if (!updateData.password) {
          delete updateData.password
          delete updateData.confirmPassword
        }

        await userApi.updateProfile(updateData)
        ElMessage.success('资料更新成功')

        // 重要：同步更新 Pinia 中的数据，Header 才会实时变化
        userStore.setUserInfo({
          ...userStore.userInfo,
          realName: updateData.realName,
          phone: updateData.phone,
          avatar: updateData.avatar
        })

        profileVisible.value = false
      } catch (error) {
        console.error('更新个人资料失败', error)
      } finally {
        profileLoading.value = false
      }
    }
  })
}

// 监听路由变化，仅用于更新侧边栏的高亮状态
watch(
    () => route.path,
    (newPath) => {
      activeMenu.value = newPath
      getBreadcrumb() // <--- 在这里调用，确保每次路由变化都重新生成面包屑
    },
    { immediate: true }
)


// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {type: 'warning'})
      .then(() => {
        userStore.logout() // 使用 store 里的 logout 清理本地存储
        ElMessage.success('已安全退出')
        router.push('/login')
      })
}

// 全屏
const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

onMounted(() => {
  activeMenu.value = route.path
  // 如果登录后停在 /index 或 /，自动跳往主控台
  if (route.path === '/index' || route.path === '/') {
    router.push('/index/dashboard')
  }
})
</script>

<style scoped>
/* =========== 整体布局样式 =========== */
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f0f2f5;
}

/* 顶部导航栏 */
.layout-header {
  height: 55px;
  background: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

.logo-section {
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 100%;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
  overflow: hidden;
  white-space: nowrap;
  border-right: 1px solid #f0f0f0;
}

.logo,
.logo img {
  width: 20px;
  height: 20px;
  display: block;
}

.logo-text {
  margin-left: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #000;
}

.collapse-btn-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 100%;
  cursor: pointer;
  transition: background 0.3s;
}

.collapse-btn-wrapper:hover {
  background-color: #f6f6f6;
}

.collapse-btn {
  font-size: 18px;
  color: #666;
}

.breadcrumb {
  margin-left: 8px;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
}

.breadcrumb .separator {
  margin: 0 8px;
  color: #ccc;
  font-size: 12px;
}

.breadcrumb .active {
  color: #333;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  padding-right: 16px;
}

.header-action-icons {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-right: 16px;
  color: #666;
  font-size: 30px;
}

.header-action-icons i {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background 0.3s;
}

.header-action-icons i:hover {
  background: #f6f6f6;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 8px;
  height: 48px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f6f6f6;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

/* =========== 下半部分布局 =========== */
.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.layout-sidebar {
  width: 210px;
  background-color: #ffffff;
  border-right: 1px solid #f0f0f0;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1);
  z-index: 99;
}

.layout-sidebar.is-collapse {
  width: 64px;
}

:deep(.el-menu) {
  border-right: none;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-content {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-15px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(15px);
}

/* 修改个人资料对话框 */
.profile-avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border: #c8c8c8 1px dashed;
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
}


.my-divider :deep(.el-divider__text) {
  font-size: 13px;
  color: #d54f4f;
}
</style>