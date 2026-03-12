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
          <span class="breadcrumb-item active">{{ route.meta.title || route.name }}</span>
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
              <span>权限管理</span>
            </template>
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
              closable
              @tab-remove="removeTab"
              @tab-click="clickTab"
          >
            <el-tab-pane
                v-for="item in tagsStore.visitedViews"
                :key="item.path"
                :label="item.title"
                :name="item.path"
            ></el-tab-pane>
          </el-tabs>
        </div>

        <div class="app-content">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <keep-alive>
                <component :is="Component" :key="route.path"/>
              </keep-alive>
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
import {useTagsStore} from '@/store/tags'
import {useUserStore} from '@/store/user'
import {ElMessage, ElMessageBox} from 'element-plus' // 引入 ElMessageBox 进行弹窗
import {
  Platform, Fold, Expand, ArrowDown, Odometer, House, Setting, Key,
  Search, FullScreen, Bell, User, SwitchButton // 引入 User 和 SwitchButton 图标
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = ref(route.path)
const activeTab = ref(route.path)

watch(
    () => route.path,
    () => {
      addTags()
      activeMenu.value = route.path
      activeTab.value = route.path
    }
)

const addTags = () => {
  const {name, path, meta} = route
  if (name && path !== '/login') {
    tagsStore.addView({
      name,
      path,
      title: meta.title || name
    })
  }
}

const clickTab = (tab) => {
  router.push(tab.props.name)
}

const removeTab = (targetPath) => {
  const views = tagsStore.visitedViews
  let currentActivePath = activeTab.value

  if (currentActivePath === targetPath) {
    views.forEach((tab, index) => {
      if (tab.path === targetPath) {
        const nextTab = views[index + 1] || views[index - 1]
        if (nextTab) {
          currentActivePath = nextTab.path
        } else {
          currentActivePath = '/index/dashboard'
        }
      }
    })
  }

  activeTab.value = currentActivePath
  tagsStore.delView(targetPath)
  router.push(currentActivePath)
}

// 修改后的退出登录逻辑：增加了二次确认弹窗
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
        // 点击取消，无需操作
      })
}

onMounted(() => {
  addTags()
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

:deep(.el-menu-item.is-active) {
  background-color: #e6f7ff !important;
  color: #0960bd !important;
  border-right: 3px solid #0960bd;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 标签页栏 */
.tags-view-container {
  height: 40px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 5px 10px;
  border-bottom: 1px solid #d8dce5;
  border-top: 1px solid #d8dce5;
}

:deep(.el-tabs__header) {
  margin: 0 !important;
  border-bottom: none !important;
}

:deep(.el-tabs__nav) {
  border: none !important;
}

:deep(.el-tabs__item) {
  height: 26px !important;
  line-height: 24px !important;
  font-size: 12px !important;
  border: 1px solid #d9d9d9 !important;
  margin-right: 4px;
  border-radius: 2px !important;
  background: #fff;
  color: #515a6e;
  padding: 0 10px !important;
}

:deep(.el-tabs__item.is-active) {
  background-color: #0960bd !important;
  color: #fff !important;
  border-color: #0960bd !important;
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