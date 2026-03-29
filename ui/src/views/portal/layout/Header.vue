<template>
  <header class="portal-header" :class="{ 'header-scroll': isScrolled }">
    <div class="header-container">
      <div class="logo" @click="router.push('/home')">
        <div class="logo-icon">
          <el-icon :size="28" color="#409EFF">
            <OfficeBuilding/>
          </el-icon>
        </div>
        <span class="logo-text">经济开发区管理委员会</span>
      </div>

      <!-- 桌面端导航 -->
      <nav class="nav-menu desktop-nav">
        <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            :ellipsis="false"
            router
        >
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/news">要闻导读</el-menu-item>
          <el-menu-item index="/policy">政策中心</el-menu-item>
          <el-menu-item index="/notice">通知公告</el-menu-item>
          <el-menu-item index="/enterprise">企业风采</el-menu-item>
          <el-menu-item v-if="isEnterprise" index="/my-enterprise">企业服务中心</el-menu-item>
        </el-menu>
      </nav>

      <!-- 移动端汉堡按钮 -->
      <div class="mobile-menu-btn" @click="drawerVisible = true">
        <el-icon :size="24">
          <Menu/>
        </el-icon>
      </div>

      <div class="header-right">
        <div v-if="!userStore.token" class="auth-btns">
          <el-button link @click="handleLogin">登录</el-button>
          <el-divider direction="vertical"/>
          <el-button type="primary" round size="small" @click="router.push('/register')">
            立即注册
          </el-button>
        </div>

        <div v-else class="user-info">

          <el-popover
              v-if="userStore.token"
              placement="bottom-end"
              :width="320"
              trigger="click"
              popper-class="notice-center-popper"
              :offset="15"
          >
            <template #reference>
              <el-badge
                  :value="totalNoticeCount"
                  :max="99"
                  :hidden="totalNoticeCount <= 0"
                  class="notice-badge"
                  style="margin-right: 20px;"
              >
                <el-icon class="notice-bell-icon">
                  <Bell/>
                </el-icon>
              </el-badge>
            </template>

            <div class="notice-panel">
              <div class="notice-panel-header">
                <span class="title">待办通知</span>
                <span class="count-tag" v-if="totalNoticeCount > 0">{{ totalNoticeCount }} 条提醒</span>
              </div>

              <div class="notice-panel-body">
                <template v-if="isAdmin || isStaff">
                  <div class="group-label">园区行政</div>
                  <div class="notice-card" @click="handleNav('/business/enterprise/list')">
                    <div class="card-icon ent-bg">
                      <el-icon>
                        <Monitor/>
                      </el-icon>
                    </div>
                    <div class="card-info">
                      <div class="label">入驻（迁出）审核</div>
                      <div class="value">{{ enterprisePendingCount }} <small>家待办</small></div>
                    </div>
                    <el-icon class="arrow-right">
                      <ArrowRight/>
                    </el-icon>
                  </div>
                  <el-divider style="margin: 8px 0"/>
                </template>

                <template v-if="isEnterprise && myEnterpriseNoticeCount > 0">
                  <div class="group-label">入驻（迁出）进度</div>

                  <div v-if="enterpriseStatus === 0 || enterpriseStatus === 4"
                       class="notice-card" @click="handleNav('/my-enterprise')">
                    <div class="card-icon ent-bg">
                      <el-icon><Timer /></el-icon>
                    </div>
                    <div class="card-info">
                      <div class="label">{{ enterpriseStatus === 0 ? '入驻申请' : '迁出申请' }}</div>
                      <div class="value">审核中<small>请耐心等待</small></div>
                    </div>
                    <el-icon class="arrow-right"><ArrowRight /></el-icon>
                  </div>

                  <div v-if="enterpriseStatus === 2"
                       class="notice-card" @click="handleNav('/my-enterprise')">
                    <div class="card-icon order-pending-bg">
                      <el-icon><Warning /></el-icon>
                    </div>
                    <div class="card-info">
                      <div class="label">审核反馈</div>
                      <div class="value" style="color: #f56c6c;">申请被驳回<small>请查看原因</small></div>
                    </div>
                    <el-icon class="arrow-right"><ArrowRight /></el-icon>
                  </div>
                </template>

                <div v-if="isAdmin || isStaff" class="group-label">工单运维</div>

                <div v-if="isAdmin || isStaff" class="notice-card" @click="handleNav('/business/workorder/list')">
                  <div class="card-icon order-pending-bg">
                    <el-icon>
                      <Tools/>
                    </el-icon>
                  </div>
                  <div class="card-info">
                    <div class="label">待受理工单</div>
                    <div class="value">{{ orderStats.pendingCount }} <small>个待分派</small></div>
                  </div>
                  <el-icon class="arrow-right">
                    <ArrowRight/>
                  </el-icon>
                </div>

                <div v-if="isAdmin || isStaff" class="notice-card" @click="handleNav('/business/workorder/list')">
                  <div class="card-icon order-processing-bg">
                    <el-icon>
                      <Loading/>
                    </el-icon>
                  </div>
                  <div class="card-info">
                    <div class="label">待处理工单</div>
                    <div class="value">{{ orderStats.processingCount }} <small>个待处理</small></div>
                  </div>
                  <el-icon class="arrow-right">
                    <ArrowRight/>
                  </el-icon>
                </div>
              </div>

              <div class="notice-panel-footer" @click="fetchAllNotifications">
                <el-icon>
                  <Refresh/>
                </el-icon>
                <span>刷新数据</span>
              </div>
            </div>
          </el-popover>

          <div class="status-guide-area" style="margin-right: 20px;">
          </div>

          <el-dropdown trigger="click" @command="handleCommand">
          </el-dropdown>

          <div class="status-guide-area" style="margin-right: 20px;">
            <el-button
                v-if="enterpriseStatus === null || enterpriseStatus === 3 "
                type="primary"
                size="small"
                plain
                round
                icon="Plus"
                class="guide-btn"
                @click="router.push('/my-enterprise')"
            >
              立即入驻
            </el-button>

            <el-tag
                v-else-if="statusConfig[enterpriseStatus]"
                :type="statusConfig[enterpriseStatus].type"
                size="small"
                effect="light"
                round
                class="status-tag"
                @click="router.push('/my-enterprise')"
            >
              {{ statusConfig[enterpriseStatus].text }}
            </el-tag>
          </div>

          <el-button
              v-if="isStaff && isMobile"
              type="primary"
              size="small"
              round
              icon="Document"
              @click="router.push('/m/worker/list')"
              style="margin-right: 12px;"
          >
            工单处理
          </el-button>

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
                  企业服务中心
                </el-dropdown-item>
                <el-dropdown-item command="profile" icon="User">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout" icon="SwitchButton">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 移动端侧边导航抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        placement="left"
        :width="240"
        append-to-body
    >
      <el-menu
          :default-active="activeMenu"
          mode="vertical"
          router
          @click="drawerVisible = false"
      >
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/news">要闻导读</el-menu-item>
        <el-menu-item index="/policy">政策中心</el-menu-item>
        <el-menu-item index="/notice">通知公告</el-menu-item>
        <el-menu-item index="/enterprise">企业风采</el-menu-item>
        <el-menu-item v-if="isEnterprise" index="/my-enterprise">企业服务中心</el-menu-item>
      </el-menu>
    </el-drawer>

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
  </header>
</template>

<script setup>
import {ref, onMounted, onUnmounted, computed, watch} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {useUserStore} from '@/store/user'
import workOrderApi from '@/api/workOrder'
import {
  OfficeBuilding,
  ArrowDown,
  Plus,
  Menu,
  Refresh,
  ArrowRight,
  Loading,
  Tools,
  Monitor, Bell, Warning, Timer
} from '@element-plus/icons-vue'
import {ElMessageBox, ElMessage} from 'element-plus'
import userApi from '@/api/user'
import enterpriseApi from '@/api/enterprise'
import {uploadFile} from '@/utils/upload'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 定义统计数据响应式变量
const enterprisePendingCount = ref(0)
const myEnterpriseNoticeCount = ref(0)
const orderStats = ref({
  pendingCount: 0,
  processingCount: 0
})
let noticeTimer = null

// 计算总通知数
const totalNoticeCount = computed(() => {
  if (!userStore.token) return 0
  let count = 0

  // 管理员/职员逻辑
  if (isAdmin.value || isStaff.value) {
    count += (enterprisePendingCount.value || 0)
    count += (orderStats.value.pendingCount || 0)
    count += (orderStats.value.processingCount || 0)
  }

  // 企业用户逻辑 (加上新接口的返回数)
  if (isEnterprise.value) {
    count += (myEnterpriseNoticeCount.value || 0)
  }

  return count
})

// 获取数据的核心方法
const fetchAllNotifications = async () => {
  if (!userStore.token) return
  const roles = userStore.roles || []
  const isAdminOrStaff = roles.includes('ROLE_ADMIN') || roles.includes('ROLE_STAFF')
  const isEnterpriseUser = roles.includes('ROLE_ENTERPRISE')

  try {
    // 准备请求列表
    const requests = [workOrderApi.getStatistics()]

    // 如果是管理层，查全量待审核
    if (isAdminOrStaff) {
      requests.push(enterpriseApi.getPendingCount())
    } else {
      requests.push(Promise.resolve(null))
    }

    // 如果是企业用户，查个人通知数
    if (isEnterpriseUser) {
      requests.push(enterpriseApi.getMyNoticeCount())
    } else {
      requests.push(Promise.resolve(null))
    }

    const [orderRes, entPendingRes, myNoticeRes] = await Promise.all(requests)

    if (orderRes.code === 200) orderStats.value = orderRes.data

    // 管理端：更新全量待办
    if (entPendingRes && entPendingRes.code === 200) {
      enterprisePendingCount.value = entPendingRes.data
    }

    // 企业端：更新个人通知
    if (myNoticeRes && myNoticeRes.code === 200) {
      myEnterpriseNoticeCount.value = myNoticeRes.data
    }
  } catch (error) {
    console.error('同步通知数据失败', error)
  }
}

// 处理通知点击跳转 (根据路由结构调整)
const handleNav = (path) => {
  router.push(path)
}

// 移动端抽屉状态
const drawerVisible = ref(false)
const isMobile = ref(window.innerWidth <= 768) // 与CSS媒体查询一致
const handleResize = () => {
  isMobile.value = window.innerWidth <= 768
}

// --- 企业状态逻辑开始 ---
const enterpriseStatus = ref(null)

const statusConfig = {
  0: {text: '入驻审核中', type: 'warning'},
  1: {text: '已入驻', type: 'success'},
  2: {text: '入驻已驳回', type: 'danger'},
  4: {text: '迁出待审核', type: 'info'}
}

const fetchStatus = async () => {
  if (!userStore.token) return
  try {
    const res = await enterpriseApi.getMyEnterprise()
    enterpriseStatus.value = res.data ? res.data.status : null
  } catch (err) {
    enterpriseStatus.value = null
  }
}

watch(() => userStore.token, (val) => {
  if (val) fetchStatus()
  else enterpriseStatus.value = null
}, {immediate: true})
// --- 企业状态逻辑结束 ---

const isAdmin = computed(() => userStore.roles.includes('ROLE_ADMIN'))
const isStaff = computed(() =>
    userStore.roles.includes('ROLE_STAFF') || userStore.roles.includes('ROLE_WORKER')
)
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

const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

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

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      openProfile()
      break
    case 'dashboard':
      router.push('/index/dashboard')
      break
    case 'enterprise':
      router.push('/my-enterprise')
      break
    case 'logout':
      confirmLogout()
      break
  }
}

const confirmLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已安全退出')
    router.push('/home')
  })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleResize)
  if (userStore.token) {
    fetchAllNotifications()
    noticeTimer = setInterval(fetchAllNotifications, 60000)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  window.addEventListener('resize', handleResize)
  if (noticeTimer) clearInterval(noticeTimer)
})
</script>

<style scoped>
/* ====== 基础导航栏样式优化 ====== */
.portal-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px) saturate(180%);
  z-index: 1000;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.03);
}

.header-scroll {
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.header-container {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

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

.nav-menu {
  flex: 1;
}

:deep(.el-menu--horizontal) {
  border-bottom: none !important;
  background: transparent !important;
  display: flex;
  justify-content: flex-start;
  height: 100%;
}

:deep(.el-menu-item) {
  font-size: 16px;
  font-weight: 500;
  color: #4b5563 !important;
  height: 70px !important;
  line-height: 70px !important;
  padding: 0 24px !important;
  transition: all 0.3s ease !important;
  position: relative;
  background: transparent !important;
}

:deep(.el-menu--horizontal .el-menu-item.is-active) {
  border-bottom: none !important;
  color: #1677ff !important;
  font-weight: 600;
}

:deep(.el-menu-item)::after {
  content: "";
  position: absolute;
  bottom: 15px;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #1677ff, #409eff);
  border-radius: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateX(-50%);
}

:deep(.el-menu-item:hover) {
  color: #1677ff !important;
  background: transparent !important;
}

:deep(.el-menu-item:hover)::after {
  width: 20px;
  opacity: 0.6;
}

:deep(.el-menu-item.is-active)::after {
  width: 32px;
  opacity: 1;
}

.header-scroll :deep(.el-menu-item) {
  height: 60px !important;
  line-height: 60px !important;
}

.header-scroll :deep(.el-menu-item)::after {
  bottom: 10px;
}

/* ====== 右侧用户区与状态引导美化 ====== */
.user-info {
  display: flex;
  align-items: center;
}

.guide-btn {
  border-style: dashed !important;
  background: rgba(64, 158, 255, 0.05) !important;
  transition: all 0.3s ease;
}

.guide-btn:hover {
  border-style: solid !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  color: #0f4780;
}

.status-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0 12px;
}

.status-tag:hover {
  transform: translateY(-1px);
  filter: brightness(0.95);
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 50px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.user-avatar-wrapper:hover {
  background: rgba(22, 119, 255, 0.05);
  border-color: rgba(22, 119, 255, 0.1);
}

.nickname {
  font-size: 12px;
  color: #374151;
}

.auth-btns .el-button--primary {
  padding: 8px 20px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(22, 119, 255, 0.2);
  transition: all 0.3s;
}

.auth-btns .el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(22, 119, 255, 0.3);
}

.auth-btns .el-button--link {
  color: #606266;
}

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


/* 通知铃铛基础样式 */
.notice-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  line-height: 1;
  transition: all 0.3s;
}

.notice-bell-icon {
  font-size: 22px;
  color: #606266;
  transition: all 0.3s;
}

.notice-badge:hover .notice-bell-icon {
  color: #409EFF;
  transform: scale(1.1) rotate(15deg);
}

/* 小红点右上角精准定位 */
:deep(.el-badge__content.is-fixed) {
  top: 4px !important;
  right: 4px !important;
  transform: translateY(-50%) translateX(50%) scale(0.8);
  border: none;
  background-color: #f56c6c;
  box-shadow: 0 0 4px rgba(245, 108, 108, 0.4);
  z-index: 10;
}

/* 下拉面板全局样式（需使用 :global 因为 Popper 挂载在 body） */
:global(.notice-center-popper) {
  padding: 0 !important;
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12) !important;
}

.notice-panel-header {
  padding: 14px 16px;
  background: #f8faff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f2f5;
}

.notice-panel-header .title {
  font-weight: 600;
  color: #1d2129;
  font-size: 14px;
}

.count-tag {
  font-size: 10px;
  background: #ff4d4f;
  color: #fff;
  padding: 1px 6px;
  border-radius: 10px;
}

.notice-panel-body {
  padding: 8px;
  max-height: 380px;
  overflow-y: auto;
}

.group-label {
  font-size: 11px;
  color: #86909c;
  padding: 6px 8px;
}

.notice-card {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.notice-card:hover {
  background: #f2f3f5;
}

.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  margin-right: 12px;
}

.ent-bg {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.order-pending-bg {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-processing-bg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-info .label {
  font-size: 12px;
  color: #4e5969;
}

.card-info .value {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}

.card-info .value small {
  font-weight: 400;
  font-size: 11px;
  color: #86909c;
  margin-left: 2px;
}

.arrow-right {
  margin-left: auto;
  color: #c9cdd4;
  font-size: 14px;
}

.notice-panel-footer {
  padding: 10px;
  text-align: center;
  color: #409eff;
  font-size: 12px;
  cursor: pointer;
  border-top: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 移动端汉堡按钮 */
.mobile-menu-btn {
  display: none;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #4b5563;
  margin-right: 15px;
}

/* ===================== 统一移动端响应式适配 ===================== */
@media (max-width: 1024px) {
  .desktop-nav {
    display: none !important;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .logo {
    margin-right: 20px;
  }

  .logo-text {
    font-size: 18px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 15px;
  }

  .logo {
    gap: 8px;
  }

  .logo-text {
    font-size: 16px;
  }

  .status-guide-area {
    display: none !important;
  }

  .nickname {
    display: none;
  }

  .user-avatar-wrapper {
    padding: 4px;
  }

  .auth-btns {
    transform: scale(0.9);
  }
}

@media (max-width: 375px) {
  .logo-text {
    display: none;
  }
}
</style>