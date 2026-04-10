<template>
  <div class="news-container">
    <div class="main-content container">
      <div class="header-wrapper">
        <div class="title-section">
          <h2 class="page-title">通知公告</h2>
          <span class="sub-title">Announcements</span>
        </div>
        <div class="search-box">
          <el-input
              v-model="queryParams.title"
              placeholder="搜索公告标题..."
              clearable
              class="modern-input"
              @keyup.enter="handleSearch"
              @clear="handleSearch"
          >
            <template #append>
              <el-button type="primary" class="search-btn" @click="handleSearch">
                <el-icon>
                  <Search/>
                </el-icon>
                搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <div class="list-wrapper">
        <el-skeleton :loading="loading" animated :count="4">
          <template #template>
            <div class="skeleton-item">
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 60%; height: 22px; margin-bottom: 12px;"/>
                <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 8px;"/>
                <el-skeleton-item variant="text" style="width: 80%; margin-bottom: 16px;"/>
                <el-skeleton-item variant="text" style="width: 120px;"/>
              </div>
              <el-skeleton-item variant="image" class="skeleton-image"/>
            </div>
          </template>

          <template #default>
            <transition-group name="list-fade" tag="div" v-if="noticeList && noticeList.length > 0">
              <div class="notice-item-wrapper" v-for="item in noticeList" :key="item.id">
                <NoticeItem
                    :item="item"
                    @click="goDetail"
                />
              </div>
            </transition-group>

            <div v-else class="empty-state">
              <el-empty
                  description="暂无相关公告信息"
                  :image-size="200"
              />
            </div>
          </template>
        </el-skeleton>
      </div>

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            background
            @current-change="getList"
            @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {Search} from '@element-plus/icons-vue'
import noticeApi from '@/api/notice'
import NoticeItem from '@/components/NoticeItem.vue'
import { eventBus } from '@/utils/eventBus'

const router = useRouter()

// 状态变量
const loading = ref(false)
const total = ref(0)
const noticeList = ref([])

// 查询参数 (Type: 3 代表通知公告)
const queryParams = reactive({
  type: 3,
  title: '',
  pageNum: 1,
  pageSize: 10
})

/**
 * 获取列表数据
 */
const getList = async () => {
  loading.value = true
  try {
    const res = await noticeApi.getPublicList({...queryParams})

    if (res && res.data) {
      noticeList.value = res.data.records || []
      total.value = res.data.total || 0

      console.log('数据加载成功，条数:', noticeList.value.length)
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    noticeList.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 搜索处理
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  getList()
}

/**
 * 每页条数切换
 */
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

/**
 * 跳转详情
 */
const goDetail = (item) => {
  item.isRead = true;
  router.push({
    path: '/portal/detail',
    query: {id: item.id}
  })
  eventBus.emit('refreshNoticeCount')
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
/* 整体页面背景 */
.news-container {
  min-height: 85vh;
  background-color: #f4f7f9;
  padding: 40px 0 60px;
}

/* 容器限宽与居中 */
.container {
  max-width: 1200px; /* 稍微收敛宽度，阅读体验更好 */
  margin: 0 auto;
  padding: 0 20px;
}

/* --- 头部样式设计 --- */
.header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
  padding: 0 10px;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 0.5px;
}

.sub-title {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 搜索框美化 */
.search-box {
  width: 360px;
}

.modern-input :deep(.el-input__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-radius: 8px 0 0 8px;
  padding: 4px 15px;
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 4px 12px rgba(64, 158, 255, 0.1);
}

.modern-input :deep(.el-input-group__append) {
  background-color: #409eff;
  border: none;
  border-radius: 0 8px 8px 0;
  padding: 0;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.modern-input :deep(.search-btn) {
  color: #ffffff;
  border-radius: 0 8px 8px 0;
  height: 100%;
  border: none;
  margin: 0;
  padding: 0 20px;
  font-weight: 500;
}

.modern-input :deep(.search-btn:hover) {
  background-color: #66b1ff;
}

/* --- 列表容器设计 --- */
.list-wrapper {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03), 0 2px 4px rgba(0, 0, 0, 0.02);
  padding: 10px 30px;
  min-height: 500px;
}

.notice-item-wrapper {
  border-bottom: 1px solid #f0f2f5;
  transition: all 0.3s ease;
}

.notice-item-wrapper:last-child {
  border-bottom: none;
}

.notice-item-wrapper:hover {
  transform: translateX(6px); /* 悬浮时的微互动感 */
}

/* --- 骨架屏美化 --- */
.skeleton-item {
  display: flex;
  padding: 24px 0;
  gap: 30px;
  border-bottom: 1px solid #f0f2f5;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.skeleton-image {
  width: 180px;
  height: 120px;
  border-radius: 8px;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
}

/* --- 分页美化 --- */
.pagination-wrapper {
  margin-top: 30px;
  padding: 20px 0;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #409eff;
  border-radius: 6px;
  font-weight: bold;
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 6px;
}

/* --- 列表动画 --- */
.list-fade-enter-active,
.list-fade-leave-active {
  transition: all 0.4s ease;
}

.list-fade-enter-from,
.list-fade-leave-to {
  opacity: 0;
  transform: translateY(15px);
}

/* --- 移动端响应式适配 --- */
@media (max-width: 768px) {
  .news-container {
    padding: 20px 0;
  }

  .header-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-box {
    width: 100%;
  }

  .list-wrapper {
    padding: 10px 15px;
  }

  .skeleton-item {
    flex-direction: column-reverse;
    gap: 15px;
  }

  .skeleton-image {
    width: 100%;
    height: 160px;
  }
}
</style>