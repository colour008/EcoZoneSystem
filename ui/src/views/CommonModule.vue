<template>
  <div class="page-container">
    <div class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="关键字">
          <el-input v-model="queryParams.keyword" placeholder="请输入" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search/></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh/></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="table-operator">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus/></el-icon>新增</el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon><Delete/></el-icon>批量删除
        </el-button>
      </div>

      <el-table v-loading="loading" :data="dataList" border stripe @selection-change="handleSelectionChange" header-cell-class-name="table-header">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="名称" prop="name" align="center" />

        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            background
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getList"
            @current-change="getList"
        />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" :title="form.id ? '编辑' : '新增'" size="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="padding: 20px">
      </el-form>
      <template #footer>
        <div style="padding: 20px">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
/**
 * 规范：按需引入 -> 定义状态 -> 核心逻辑 -> 生命周期
 */
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/targetModule' // 替换为具体模块

// --- 基础状态 ---
const loading = ref(false)
const submitLoading = ref(false)
const drawerVisible = ref(false)
const dataList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const formRef = ref(null)

// --- 查询与表单数据 ---
const queryParams = ref({ pageNum: 1, pageSize: 10, keyword: '' })
const form = ref({ id: null, name: '' })
const rules = {
  name: [{ required: true, message: '必填项', trigger: 'blur' }]
}

// --- 核心方法 ---
const getList = async () => {
  loading.value = true
  try {
    const res = await api.page(queryParams.value)
    dataList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryParams.value.pageNum = 1; getList() }

const resetQuery = () => {
  queryParams.value = { pageNum: 1, pageSize: 10 }
  getList()
}

const handleAdd = () => {
  drawerVisible.value = true
  nextTick(() => {
    formRef.value?.resetFields()
    form.value = { id: null, name: '' }
  })
}

const handleEdit = (row) => {
  drawerVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
    form.value = { ...row }
  })
}

const handleSubmit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      form.value.id ? await api.update(form.value.id, form.value) : await api.add(form.value)
      ElMessage.success('操作成功')
      drawerVisible.value = false
      getList()
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除数据?`, '警告', { type: 'warning' }).then(async () => {
    await api.delete([row.id])
    ElMessage.success('删除成功')
    getList()
  })
}

const handleSelectionChange = (val) => { multipleSelection.value = val }

onMounted(getList)
</script>

<style scoped>
/* 统一样式变量与阴影 */
.page-container { min-height: calc(100vh - 84px); }
.search-card, .table-card {
  background: #fff;
  border-radius: 4px;
  padding: 18px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
.table-operator { margin-bottom: 18px; }
.pagination-container { margin-top: 20px; text-align: right; }

/* 深度选择器修改表头颜色 */
:deep(.table-header) {
  background-color: #f5f7fa !important;
  color: #606266;
  font-weight: bold;
}
</style>