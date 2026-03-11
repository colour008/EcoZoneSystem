<template>
  <div class="layout-container">
    <header class="layout-header">
      <div class="header-left">
        <el-icon class="logo-icon" :size="24"><Platform /></el-icon>
        <span class="logo-text">经济开发区管理平台</span>
        <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
          <Fold v-if="!isCollapse"/>
          <Expand v-else/>
        </el-icon>
      </div>

      <div class="header-right">
        <el-dropdown trigger="click">
          <span class="user-info">
            <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span class="username">Admin</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
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
              <el-icon><Odometer /></el-icon>
              <span>后台面板</span>
            </template>
            <el-menu-item index="/index/dashboard">
              <el-icon><House /></el-icon>主控台
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>权限管理</span>
            </template>
            <el-menu-item index="/index/access/list">
              <el-icon><Key /></el-icon>权限列表
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
            <keep-alive>
              <component :is="Component" :key="route.path" />
            </keep-alive>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsStore } from '@/store/tags'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  Platform, Fold, Expand, ArrowDown, Odometer, House, Setting, Key
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsStore = useTagsStore()
const userStore = useUserStore()

const isCollapse = ref(false) // 控制侧边栏折叠
const activeMenu = ref(route.path) // 高亮当前菜单
const activeTab = ref(route.path)  // 高亮当前标签

// 监听路由变化，动态添加标签页
watch(
    () => route.path,
    () => {
      addTags()
      activeMenu.value = route.path
      activeTab.value = route.path
    }
)

// 添加标签页方法
const addTags = () => {
  const { name, path, meta } = route
  if (name && path !== '/login') {
    tagsStore.addView({
      name,
      path,
      title: meta.title || name
    })
  }
}

// 点击标签页跳转
const clickTab = (tab) => {
  router.push(tab.props.name)
}

// 移除标签页逻辑
const removeTab = (targetPath) => {
  const views = tagsStore.visitedViews
  let currentActivePath = activeTab.value

  // 如果删除的是当前处于激活状态的标签，需要将路由往前或往后退一个
  if (currentActivePath === targetPath) {
    views.forEach((tab, index) => {
      if (tab.path === targetPath) {
        const nextTab = views[index + 1] || views[index - 1]
        if (nextTab) {
          currentActivePath = nextTab.path
        } else {
          currentActivePath = '/index/dashboard' // 全删光了兜底回主页
        }
      }
    })
  }

  activeTab.value = currentActivePath
  tagsStore.delView(targetPath)
  router.push(currentActivePath)
}

// 退出登录逻辑
const handleLogout = () => {
  userStore.setToken('') // 清除 token
  ElMessage.success('已安全退出')
  router.push('/login')
}

// 页面加载完毕初始化标签
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
  background-color: #f3f4f6;
}

/* 顶部导航栏 (模仿图片中的深紫/蓝色) */
.layout-header {
  height: 60px;
  background: #4f46e5;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-icon {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px;
  border-radius: 6px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  margin-right: 30px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.collapse-btn:hover {
  opacity: 0.8;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
  outline: none;
}

.username {
  font-size: 14px;
}

/* =========== 下半部分布局 =========== */
.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 侧边栏样式 */
.layout-sidebar {
  width: 220px;
  background-color: #ffffff;
  border-right: 1px solid #e5e7eb;
  transition: width 0.3s ease;
  overflow-y: auto;
}

.layout-sidebar.is-collapse {
  width: 64px;
}

.sidebar-menu {
  border-right: none;
}

/* 右侧主内容区 */
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 历史标签页 (TagsView) */
.tags-view-container {
  height: 40px;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .05);
  padding: 4px 10px 0;
  box-sizing: border-box;
}

/* 覆盖 Element-Plus 默认 Tabs 的一些多余边框 */
:deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none !important;
}
:deep(.el-tabs__nav) {
  border: none !important;
}
:deep(.el-tabs__item) {
  height: 30px !important;
  line-height: 30px !important;
  font-size: 12px !important;
  border: 1px solid #d8dce5 !important;
  margin-right: 5px;
  border-radius: 2px;
  background: #fff;
  color: #495060;
  transition: all 0.3s;
}
:deep(.el-tabs__item.is-active) {
  background-color: #4f46e5 !important;
  color: #fff !important;
  border-color: #4f46e5 !important;
}

/* 核心内容区 */
.app-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f3f4f6;
}
</style>