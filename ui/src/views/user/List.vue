<template>
  <div class="user-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="getUserPageList"/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入姓名" clearable @keyup.enter="getUserPageList"/>
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" :value="1"/>
            <el-option label="停用" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getUserPageList">
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
          新增用户
        </el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon>
            <Delete/>
          </el-icon>
          批量删除
        </el-button>
      </div>

      <el-table v-loading="loading" :data="userList" border stripe @selection-change="handleSelectionChange"
                header-cell-class-name="table-header">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="ID" prop="id" width="80" align="center"/>
        <el-table-column label="用户名" prop="username" min-width="120" align="center"/>
        <el-table-column label="姓名" prop="realName" min-width="120" align="center"/>
        <el-table-column label="手机号" prop="phone" min-width="130" align="center"/>
        <el-table-column label="头像" align="center" width="100">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar"
                       style="border: rgba(100,155,185,0.8) 1px solid;background: #ffffff"/>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" min-width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" plain :icon="Edit" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" plain :icon="Delete" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="warning" plain :icon="Warning" size="small" @click="handleReset(scope.row)">重置密码
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                       :total="total" background layout="total, sizes, prev, pager, next, jumper"
                       @size-change="getUserPageList" @current-change="getUserPageList"/>
      </div>
    </div>

    <el-drawer
        v-model="drawerVisible"
        :title="form.id ? '编辑用户' : '新增用户'"
        direction="rtl"
        size="500px"
        @close="handleClose"
    >
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="100px" style="padding: 20px">
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名"/>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id"/>
        </el-form-item>
        <template v-if="!form.id">
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password placeholder="请输入密码"/>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码"/>
          </el-form-item>
        </template>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"/>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload class="avatar-uploader" action="#" :show-file-list="false" :http-request="handleImageUpload">
            <img v-if="form.avatar" :src="form.avatar" class="avatar-img" alt="头像"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import {ref, onMounted, nextTick, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Delete, Edit, Plus, Warning} from '@element-plus/icons-vue'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'

const loading = ref(false)
const submitLoading = ref(false)
const userList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const userFormRef = ref(null)

const queryParams = ref({
  pageNum: 1, pageSize: 10, username: null, realName: null, status: null
})

const drawerVisible = ref(false)
const form = ref({
  id: null, username: '', realName: '', phone: '', avatar: '', status: 0, password: '', confirmPassword: ''
})

// --- 表单校验规则 ---
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.value.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = computed(() => ({
  username: [
    {required: true, message: '用户名不能为空', trigger: 'blur'},
    {min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur'}
  ],
  // 添加手机号校验
  phone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    {
      pattern: /^1[0-9]\d{9}$/,
      message: '请输入正确的11位手机号',
      trigger: 'blur'
    }
  ],
  password: [
    {required: !form.value.id, message: '密码不能为空', trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    {
      required: !form.value.id,
      validator: (rule, value, callback) => {
        if (form.value.id && !value && !form.value.password) {
          callback()
        } else if (value !== form.value.password) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  realName: [
    {required: true, message: '真实姓名不能为空', trigger: 'blur'}
  ]
}))

// 获取列表
const getUserPageList = async () => {
  loading.value = true
  try {
    const res = await userApi.page(queryParams.value)
    // 拦截器保证了 res 存在且 code 为 200
    userList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    // 错误已由拦截器弹窗，这里只需要打印或处理 loading
    console.error('获取列表失败', error)
  } finally {
    loading.value = false
  }
}

// 新增按钮：弹出并清空内容
const handleAdd = () => {
  drawerVisible.value = true
  // nextTick 确保弹窗 DOM 已加载后执行重置
  nextTick(() => {
    if (userFormRef.value) {
      userFormRef.value.resetFields()
    }
    form.value = {
      id: null,
      username: '',
      realName: '',
      phone: '',
      avatar: '',
      status: 1,
      password: '',
      confirmPassword: ''
    }
  })
}

const handleEdit = (row) => {
  drawerVisible.value = true
  nextTick(() => {
    if (userFormRef.value) userFormRef.value.clearValidate()
    form.value = {...row, confirmPassword: ''}
  })
}

// 提交表单：简化判断逻辑
const submitForm = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (!form.value.id) {
          // 新增
          const res = await userApi.add(form.value)
          ElMessage.success(res.msg || '新增成功')
          drawerVisible.value = false
          getUserPageList()
        } else {
          // 编辑
          const {id, ...updateData} = form.value
          if (!updateData.password) {
            delete updateData.password
            delete updateData.confirmPassword
          }
          const res = await userApi.update(id, updateData)
          ElMessage.success(res.msg || '编辑成功')
          drawerVisible.value = false
          getUserPageList()
        }
      } catch (error) {
        console.error('提交失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 抽屉关闭时清空校验
const handleClose = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

const handleImageUpload = async (options) => {
  const url = await uploadFile(options.file)
  form.value.avatar = url
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}
const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10};
  getUserPageList()
}

// --- 删除逻辑 ---
const doDelete = (ids, message) => {
  ElMessageBox.confirm(message, '系统提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      const res = await userApi.delete(ids)
      ElMessage.success(res.msg || '操作成功')
      if (userList.value.length === ids.length && queryParams.value.pageNum > 1) {
        queryParams.value.pageNum--
      }
      getUserPageList()
    } catch (error) {
      console.error('删除失败', error)
    } finally {
      loading.value = false
    }
  }).catch(() => {
  })
}

// --- 单个删除 ---
const handleDelete = (row) => {
  doDelete([row.id], `确定要删除用户 "${row.realName}" 吗？`)
}

// --- 批量删除 ---
const handleBatchDelete = () => {
  const ids = multipleSelection.value.map(item => item.id)
  doDelete(ids, `确定要删除选中的 ${ids.length} 条数据吗？`)
}

// 重置密码
const handleReset = (row) => {
  ElMessageBox.confirm(
      `确定要将用户 "${row.realName}" 的密码重置吗？`,
      '安全警告',
      {confirmButtonText: '确定重置', cancelButtonText: '取消', type: 'warning'}
  ).then(async () => {
    try {
      const res = await userApi.resetPassword(row.id)
      // 这里的 res.data 就是后端返回的 "密码已重置为: 123456"
      ElMessage({
        message: `${res.msg}。${res.data}`,
        type: 'success',
        duration: 0, // 不自动关闭，方便管理员查看新密码
        showClose: true
      })
    } catch (error) {
      console.error('重置失败', error)
    }
  }).catch(() => {
  })
}

onMounted(() => {
  getUserPageList()
})
</script>

<style scoped>
/* avatar-uploader */
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 100px;
  height: 100px;
}

.avatar-img {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.user-container {
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