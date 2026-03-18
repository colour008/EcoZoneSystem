<template>
  <div class="role-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable
                    @keyup.enter="getRolePageList"/>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="queryParams.roleCode" placeholder="请输入角色编码" clearable
                    @keyup.enter="getRolePageList"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getRolePageList">
            <el-icon>
              <Search/>
            </el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div style="margin-bottom: 15px">
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus/>
          </el-icon>
          新增角色
        </el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon>
            <Delete/>
          </el-icon>
          批量删除
        </el-button>
      </div>

      <el-table v-loading="loading" :data="roleList" border stripe @selection-change="handleSelectionChange"
                header-cell-class-name="table-header">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="ID" prop="id" width="100" align="center"/>
        <el-table-column label="角色名称" prop="roleName" min-width="150" align="center"/>
        <el-table-column label="角色编码" prop="roleCode" min-width="150" align="center">
          <template #default="scope">
            <el-tag type="danger" size="small" effect="light">{{ scope.row.roleCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="180" align="center"/>
        <el-table-column label="更新时间" prop="updateTime" min-width="180" align="center"/>
        <el-table-column label="操作" align="center" min-width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain :icon="Edit" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="success" plain :icon="Setting" size="small" @click="handlePermission(scope.row)">
              分配权限
            </el-button>
            <el-button type="danger" plain :icon="Delete" size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                       :total="total" background layout="total, sizes, prev, pager, next, jumper"
                       @size-change="getRolePageList" @current-change="getRolePageList"/>
      </div>
    </div>

    <el-drawer
        v-model="drawerVisible"
        :title="form.id ? '编辑角色' : '新增角色'"
        direction="rtl"
        size="450px"
        @close="handleClose"
    >
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px" style="padding: 20px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称"/>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
          <div style="font-size: 12px; color: #ea6565; margin-top: 5px">
            编码唯一，创建后通常不修改。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="flex: auto; padding: 20px">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Delete, Edit, Plus, Setting} from '@element-plus/icons-vue'
import roleApi from '@/api/role'

const loading = ref(false)
const submitLoading = ref(false)
const roleList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const roleFormRef = ref(null)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleCode: ''
})

const drawerVisible = ref(false)
const form = ref({
  id: null,
  roleName: '',
  roleCode: ''
})

// 校验规则
const rules = {
  roleName: [{required: true, message: '角色名称不能为空', trigger: 'blur'}],
  roleCode: [{required: true, message: '角色编码不能为空', trigger: 'blur'}]
}

// 获取分页列表
const getRolePageList = async () => {
  loading.value = true
  try {
    const res = await roleApi.page(queryParams.value)
    roleList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载角色列表失败', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10, roleName: '', roleCode: ''}
  getRolePageList()
}

// 新增
const handleAdd = () => {
  drawerVisible.value = true
  form.value = {id: null, roleName: '', roleCode: ''}
  nextTick(() => {
    roleFormRef.value?.clearValidate()
  })
}

// 修改
const handleEdit = (row) => {
  drawerVisible.value = true
  form.value = {...row} // 回显数据
  nextTick(() => {
    roleFormRef.value?.clearValidate()
  })
}

// 提交
const submitForm = async () => {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (!form.value.id) {
          await roleApi.add(form.value)
          ElMessage.success('新增角色成功')
        } else {
          // 这里的 form.value.id 对应后端接口的 @PathVariable Long id
          await roleApi.update(form.value.id, form.value)
          ElMessage.success('修改角色成功')
        }
        drawerVisible.value = false
        getRolePageList()
      } catch (error) {
        // 报错会自动由 axios 拦截器处理弹出你的业务异常信息
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 分配权限（占位）
const handlePermission = (row) => {
  ElMessage.info(`正在为角色 [${row.roleName}] 开发权限分配功能...`)
}

// 选中变化
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 删除逻辑封装
const doDelete = (ids, message) => {
  ElMessageBox.confirm(message, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await roleApi.delete(ids)
      ElMessage.success('删除成功')
      getRolePageList()
    } catch (error) {
      // 如果角色被占用，后端抛出的 BusinessException 会在这里被拦截器捕获并弹出红字提醒
      console.error('删除失败', error)
    } finally {
      loading.value = false
    }
  }).catch(() => {
  })
}

const handleDelete = (row) => {
  doDelete([row.id], `确定要删除角色 "${row.roleName}" 吗？`)
}

const handleBatchDelete = () => {
  const ids = multipleSelection.value.map(item => item.id)
  doDelete(ids, `确定要批量删除选中的 ${ids.length} 个角色吗？`)
}

const handleClose = () => {
  roleFormRef.value?.resetFields()
}

onMounted(() => {
  getRolePageList()
})
</script>

<style scoped>
.role-container {
  min-height: calc(100vh - 80px);
}

.search-card, .table-card {
  background: #fff;
  border-radius: 5px;
  padding: 15px 14px;
  margin-bottom: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.table-header) {
  background-color: rgba(76, 127, 181, 0.89) !important;
  color: #ffffff;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>