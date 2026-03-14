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
            <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in route.matched.filter(m => m.meta.title)" :key="item.path">
              <el-icon v-if="item.meta.icon" style="vertical-align: middle; margin-right: 4px;">
                <component :is="item.meta.icon"/>
              </el-icon>
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
          <el-icon title="全屏">
            <FullScreen/>
          </el-icon>
          <el-icon title="通知">
            <Bell/>
          </el-icon>
        </div>

        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="28" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"/>
            <span class="username">Admin</span>
            <el-icon><ArrowDown/></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
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
    </header>

    <div class="layout-body">
      <aside class="layout-sidebar" :class="{ 'is-collapse': isCollapse }">
        <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            :collapse="isCollapse"
            :unique-opened="true"
            router
        >
          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <Odometer/>
              </el-icon>
              <span>后台面板</span>
            </template>
            <el-menu-item index="/index/dashboard">
              <el-icon>
                <House/>
              </el-icon>
              <span>主控台</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon>
                <Setting/>
              </el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/index/user/list">
              <el-icon>
                <User/>
              </el-icon>
              <span>用户列表</span>
            </el-menu-item>
            <el-menu-item index="/index/access/list">
              <el-icon>
                <Key/>
              </el-icon>
              <span>权限列表</span>
            </el-menu-item>
          </el-sub-menu>
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
import {ref, watch, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Platform, Fold, Expand, ArrowDown, Odometer, House, Setting, Key,
  Search, FullScreen, Bell, User, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = ref(route.path)

// 监听路由变化，仅用于更新侧边栏的高亮状态
watch(
    () => route.path,
    (newPath) => {
      activeMenu.value = newPath
    },
    {immediate: true}
)

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm(
      '确认退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        userStore.setToken('')
        ElMessage.success('已安全退出')
        router.push('/login')
      })
      .catch(() => {
      })
}

onMounted(() => {
  activeMenu.value = route.path
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
  height: 48px;
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
  font-size: 25px;
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
</style>