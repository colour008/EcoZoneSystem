<template>
  <div class="layout-container">
    <header class="layout-header">
      <div class="header-left">
        <div class="logo-section" :style="{ width: isCollapse ? '64px' : '210px' }">
          <el-icon class="logo-icon" :size="25" color="#0960bd">
            <Platform/>
          </el-icon>
          <span v-if="!isCollapse" class="logo-text">经济开发区管理平台</span>
        </div>

        <div class="collapse-btn-wrapper" @click="isCollapse = !isCollapse">
          <el-icon class="collapse-btn">
            <Fold v-if="!isCollapse"/>
            <Expand v-else/>
          </el-icon>
        </div>

        <div class="breadcrumb">
          <span class="breadcrumb-item">首页</span>
          <span class="separator">/</span>
          <span class="breadcrumb-item active">
    <el-icon v-if="route.meta.icon" style="vertical-align: middle; margin-right: 4px; margin-top: -2px;">
      <component :is="route.meta.icon"/>
    </el-icon>
    {{ route.meta.title || route.name }}
  </span>
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
        <div class="tags-view-container">
          <el-tabs
              v-model="activeTab"
              type="card"
              @tab-remove="removeTab"
              @tab-click="clickTab"
          >
            <el-tab-pane name="/index/dashboard" :closable="false">
              <template #label>
                <el-icon style="vertical-align: middle; margin-right: 4px;"><House /></el-icon>
                <span>主控台</span>
              </template>
            </el-tab-pane>

            <el-tab-pane
                v-for="item in otherViews"
                :key="item.path"
                :name="item.path"
                closable
            >
              <template #label>
                <el-icon v-if="item.icon" style="vertical-align: middle; margin-right: 4px;">
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.title }}</span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>

        <div class="app-content">
          <router-view v-slot="{ Component, route: currentRoute }">
            <transition name="fade-transform" mode="out-in">
              <keep-alive v-if="Component">
                <component :is="Component" :key="currentRoute.fullPath"/>
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import {ref, watch, computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useTagsStore} from '@/store/tags'
import {useUserStore} from '@/store/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Platform, Fold, Expand, ArrowDown, Odometer, House, Setting, Key,
  Search, FullScreen, Bell, User, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = ref(route.path)
const activeTab = ref(route.path || '/index/dashboard')

// 计算属性：过滤出非主控台的标签
const otherViews = computed(() => {
  return tagsStore.visitedViews.filter(v => v.path !== '/index/dashboard')
})

// 添加标签（仅处理非主控台标签）
const addTags = () => {
  const {name, path, meta} = route
  if (name && path !== '/login' && path !== '/index/dashboard') {
    tagsStore.addView({name, path, meta})
  }
}

// 监听路由变化（修复重复触发问题）
watch(
    () => route.path,
    (newPath) => {
      addTags()
      activeMenu.value = newPath
      activeTab.value = newPath
    },
    {immediate: true, deep: true}
)

// 点击标签切换路由
const clickTab = (tab) => {
  if (tab.props.name) {
    router.push(tab.props.name)
  }
}

// 删除标签（修复跳转逻辑）
const removeTab = (targetPath) => {
  const dashboardPath = '/index/dashboard'
  // 禁止删除主控台
  if (targetPath === dashboardPath) return

  let currentActivePath = activeTab.value
  // 如果删除的是当前激活标签，跳转到上一个/主控台
  if (currentActivePath === targetPath) {
    const nextTab = otherViews.value.find(v => v.path !== targetPath) || {path: dashboardPath}
    currentActivePath = nextTab.path
  }

  // 更新标签状态 + 跳转路由
  tagsStore.delView(targetPath)
  activeTab.value = currentActivePath
  router.push(currentActivePath)
}

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
        tagsStore.$reset() // 重置标签（保留主控台）
        ElMessage.success('已安全退出')
        router.push('/login')
      })
      .catch(() => {
      })
}

onMounted(() => {
  // 初始化激活主控台标签
  if (!activeTab.value) {
    activeTab.value = '/index/dashboard'
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

.logo-text {
  margin-left: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #000;
}

/* 折叠按钮包装器 */
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

/* 面包屑 */
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

/* 顶部右侧 */
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

/* 标签页栏容器 */
.tags-view-container {
  height: 48px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 10px;
  border-bottom: 1px solid #d8dce5;
  border-top: 1px solid #d8dce5;
}

/* 重置 el-tabs 内部所有可能导致截断的容器 */
:deep(.el-tabs--card) {
  height: auto !important;
  border: none !important;
}

/* 解除 Element Plus 内部 header 的高度和溢出限制 */
:deep(.el-tabs) {
  height: 32px !important;
  border: none !important;
  width: 100%;
}

:deep(.el-tabs__header) {
  margin: 0 !important;
  border: none !important;
  height: 32px !important; /* 与上面保持一致 */
  line-height: 32px !important;
}

:deep(.el-tabs__nav) {
  border: none !important;
  display: flex !important;
  align-items: center !important;
}

:deep(.el-tabs__nav-wrap),
:deep(.el-tabs__nav-scroll) {
  overflow: visible !important;
  height: 32px !important;
}

/* 标签项样式 */
:deep(.el-tabs__item) {
  height: 30px !important;       /* 稍微小于父级，防止触碰边界 */
  line-height: 28px !important;  /* 内部文字居中 */
  display: inline-flex !important;
  align-items: center !important;
  padding: 0 12px !important;
  margin-right: 6px !important;
  border: 1px solid #dcdfe6 !important;
  border-radius: 4px !important;
  background: #fff;
  color: #515a6e;
  font-size: 13px !important;
  box-sizing: border-box !important;
}

:deep(.el-tabs__item.is-active) {
  background-color: #0960bd !important;
  color: #fff !important;
  border-color: #0960bd !important;
}


/* 关闭按钮图标（居中并美化） */
:deep(.el-tabs__item .is-icon-close) {
  width: 14px !important;
  height: 14px !important;
  margin-right: -4px !important;
  border-radius: 50%;
  transition: all 0.3s;
  margin-top: 0 !important; /* 重置之前可能的偏向 */
  margin-left: 6px !important;
}

/* 鼠标悬停效果 */
:deep(.el-tabs__item:hover) {
  border-color: #0960bd !important;
  color: #0960bd;
}

:deep(.el-tabs__item.is-active:hover) {
  color: #fff;
}

/* 修复关闭按钮垂直居中 */
:deep(.el-tabs__item .is-icon-close) {
  margin-top: 1px;
}

:deep(.el-tabs__item .is-icon-close:hover) {
  background-color: #b3d8ff !important;
  color: #0960bd !important;
}

/* 主控台标签样式（置顶） */
:deep(.el-tabs__item:first-child) {
  order: -1;
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