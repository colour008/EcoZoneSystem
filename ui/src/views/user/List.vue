<template>
  <div class="user-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="用户名">
          <el-input
              v-model="queryParams.username"
              placeholder="请输入用户名"
              clearable
              @keyup.enter="getUserPageList"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
              v-model="queryParams.realName"
              placeholder="请输入姓名"
              clearable
              @keyup.enter="getUserPageList"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
              v-model="queryParams.phone"
              placeholder="请输入手机号"
              clearable
              @keyup.enter="getUserPageList"
          />
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 120px"
          >
            <el-option label="正常" :value="1"/>
            <el-option label="禁用" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getUserPageList">
            <el-icon><Search/></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh/></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table
          v-loading="loading"
          :data="userList"
          border
          stripe
          style="width: 100%"
          header-cell-class-name="table-header"
      >
        <el-table-column label="ID" prop="id" width="80" align="center"/>
        <el-table-column label="用户名" prop="username" min-width="120" align="center"/>
        <el-table-column label="姓名" prop="realName" min-width="120" align="center"/>
        <el-table-column label="手机号" prop="phone" min-width="130" align="center"/>
        <el-table-column label="账号状态" align="center" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" align="center" min-width="180"/>
        <el-table-column label="更新时间" prop="updateTime" align="center" min-width="180"/>
        <el-table-column label="操作" align="center" width="180">
          <template #default="scope">
            <el-button type="primary" plain :icon="Edit" size="small">编辑</el-button>
            <el-button type="danger" plain :icon="Delete" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            background
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getUserPageList"
            @current-change="getUserPageList"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Search, Refresh, Delete, Edit} from '@element-plus/icons-vue'
import {getUserPage} from '@/api/user'
import {formatDate} from '@/utils/date'

// 加载状态
const loading = ref(false)
// 用户列表
const userList = ref([])
// 总条数
const total = ref(0)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  username: null,
  realName: null,
  phone: null,
  status: null
})

// 获取分页用户列表
const getUserPageList = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams.value)
    // 适配后端统一返回体
    userList.value = res.data.records || []
    total.value = res.data.total || 0
    // 格式化日期
    userList.value.forEach(item => {
      if (item.createTime) item.createTime = formatDate(item.createTime)
      if (item.updateTime) item.updateTime = formatDate(item.updateTime)
    })
  } catch (error) {
    ElMessage.error('查询用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 重置查询条件
const resetQuery = () => {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    username: null,
    realName: null,
    phone: null,
    status: null
  }
  getUserPageList()
}

// 页面加载请求数据
onMounted(() => {
  getUserPageList()
})
</script>

<style scoped>
.user-container {
  min-height: calc(100vh - 80px);
}

.search-card {
  background: #fff;
  border-radius: 5px;
  padding: 15px 14px;
  margin-bottom: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.table-card {
  background: #fff;
  border-radius: 5px;
  padding: 15px 14px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.table-header) {
  background-color: #dceeff !important;
  color: #333;
  font-weight: 600;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.search-form {
  --el-form-inline-item-margin-right: 16px;
}

.button-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}
</style>