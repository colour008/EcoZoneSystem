<template>
  <div class="news-container">
    <div class="breadcrumb-section">
      <div class="container">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>园区动态</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div class="main-content container">
      <div class="filter-wrapper">
        <h2 class="page-title">园区动态</h2>
        <div class="search-box">
          <el-input
              v-model="queryParams.title"
              placeholder="搜索标题..."
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon>
                  <Search/>
                </el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <div class="list-wrapper">
        <el-skeleton :loading="loading" animated :count="3">
          <template #template>
            <div style="padding: 20px; display: flex; gap: 20px">
              <div style="flex: 1">
                <el-skeleton-item variant="h3" style="width: 50%"/>
                <el-skeleton-item variant="text" style="margin-top: 15px"/>
                <el-skeleton-item variant="text" style="width: 30%; margin-top: 15px"/>
              </div>
              <el-skeleton-item variant="image" style="width: 160px; height: 100px"/>
            </div>
          </template>

          <template #default>
            <div v-if="noticeList && noticeList.length > 0">
              <NoticeItem
                  v-for="item in noticeList"
                  :key="item.id"
                  :item="item"
                  @click="goDetail"
              />
            </div>

            <el-empty
                v-else
                description="暂无相关动态信息"
                :image-size="200"
            />
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

const router = useRouter()

// 状态变量
const loading = ref(false)
const total = ref(0)
const noticeList = ref([])

// 查询参数 (Type: 2 代表园区动态)
const queryParams = reactive({
  type: 2,
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
    console.error('获取动态列表失败:', error)
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
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.news-container {
  min-height: 80vh;
  background-color: #f8f9fa;
  padding-bottom: 40px;
}

/* 容器限宽 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 面包屑部分 */
.breadcrumb-section {
  background-color: #fff;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

/* 标题与搜索过滤栏 */
.filter-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  color: #303133;
  position: relative;
  padding-left: 15px;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background-color: #409eff; /* 政务蓝主色调 */
  border-radius: 2px;
}

.search-box {
  width: 300px;
}

/* 列表容器 */
.list-wrapper {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  min-height: 400px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 深度适配 Element Plus 搜索框圆角 */
:deep(.el-input-group__append) {
  background-color: #409eff;
  color: #fff;
  box-shadow: none;
}

:deep(.el-input-group__append:hover) {
  background-color: #66b1ff;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .filter-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .search-box {
    width: 100%;
  }
}
</style>