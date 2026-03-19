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
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="getUserPageList"/>
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
        <el-table-column label="所属角色" align="center" min-width="150">
          <template #default="scope">
            <el-tag v-if="scope.row.roleName" size="small" effect="plain">
              {{ scope.row.roleName }}
            </el-tag>
            <span v-else style="color: #999; font-size: 12px">暂无角色</span>
          </template>
        </el-table-column>
        <el-table-column label="头像" align="center" width="100">
          <template #default="scope">
            <el-avatar :size="35" :src="scope.row.avatar || '/DefaultUser.svg'"
                       style="border: rgba(100,155,185,0.8) 1px solid;background: #ffffff"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="180" align="center"/>
        <el-table-column label="更新时间" prop="updateTime" min-width="180" align="center"/>
        <el-table-column label="状态" align="center" min-width="100">
          <template #default="scope">
            <el-tooltip
                :content="scope.row.id === currentUserId ? '不可操作当前登录账号' : '权限不足：无法操作高职级用户'"
                placement="top"
                :disabled="canIAction(scope.row)"
            >
              <el-switch
                  v-model="scope.row.status"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="正常"
                  inactive-text="停用"
                  inline-prompt
                  :disabled="!canIAction(scope.row)"
                  :loading="!!scope.row.statusLoading"
                  @change="(val) => handleStatusChange(scope.row, val)"
              />
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" min-width="220" fixed="right">
          <template #default="scope">
            <el-button link type="primary" plain :icon="Edit" size="small"
                       :disabled="!canIAction(scope.row)"
                       @click="handleEdit(scope.row)">
              编辑
            </el-button>

            <el-button link type="warning" plain :icon="Warning" size="small"
                       :disabled="!canIAction(scope.row)"
                       @click="handleReset(scope.row)">
              重置密码
            </el-button>

            <el-button link type="danger" plain :icon="Delete" size="small"
                       :disabled="!canIAction(scope.row)"
                       @click="handleDelete(scope.row)">
              删除
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
        <el-form-item label="分配角色" prop="roleIds">
          <el-select
              v-model="form.roleIds"
              multiple
              placeholder="请选择角色"
              style="width: 100%"
          >
            <el-option
                v-for="item in filteredRoleOptions"
                :key="item.id"
                :label="item.roleName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
              class="avatar-uploader"
              action="#"
              :show-file-list="false"
              :http-request="handleImageUpload"
              :before-upload="beforeAvatarUpload"
              accept=".jpg,.jpeg,.png,.gif"
          >
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
import roleApi from '@/api/role'
import {ref, onMounted, nextTick, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Delete, Edit, Plus, Warning} from '@element-plus/icons-vue'
import userApi from '@/api/user'
import {uploadFile} from '@/utils/upload'
import {useUserStore} from '@/store/user'

const loading = ref(false)
const submitLoading = ref(false)
const userList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const userFormRef = ref(null)
const userStore = useUserStore()
const roleOptions = ref([])


const queryParams = ref({
  pageNum: 1, pageSize: 10, username: null, realName: null, status: null, phone: null
})

const drawerVisible = ref(false)
const form = ref({
  id: null, username: '', realName: '', phone: '', avatar: '', status: 0, password: '', confirmPassword: '', roleIds: []
})

// 获取当前登录人的 ID 和 权限权重
const currentUserId = computed(() => userStore.userInfo?.id)
const myLevel = computed(() => userStore.userInfo?.topRoleSort ?? 999)

// 获取角色列表
const getRoleOptions = async () => {
  try {
    const res = await roleApi.listAll()
    roleOptions.value = res.data
  } catch (error) {
    console.error('获取角色列表失败', error)
  }
}

/**
 * 判断当前登录用户是否可以操作列表中的某行数据
 * @param row 列表行数据
 */
const canIAction = (row) => {
  // A. 不能操作自己
  if (row.id === currentUserId.value) {
    return false
  }

  // B. 超级管理员 (Level 为 0) 拥有特权，可以操作所有人（除自己外）
  if (myLevel.value === 0) {
    return true
  }

  // C. 职级判定：如果我的权重数值(如 10) > 目标的权重数值(如 5)，说明目标职级比我高
  // 注意：数值越大等级越低
  if (row.topRoleSort !== null && myLevel.value > row.topRoleSort) {
    return false
  }

  return true
}

/**
 * 核心：过滤后的角色选项
 * 只有当角色的 roleSort >= 我的 topRoleSort 时，才允许我看到并分配
 */
const filteredRoleOptions = computed(() => {
  // 1. 如果我是超级管理员 (Level 0)，看到所有角色
  if (myLevel.value === 0) {
    return roleOptions.value
  }

  // 2. 过滤逻辑：只保留等级（数值）大于或等于我的角色
  // 例如：我的等级是 10，那么我只能分配 10, 11, 12... 的角色
  return roleOptions.value.filter(role => {
    // 如果角色没有设置排序，默认视为最低权限（可以显示）
    if (role.roleSort === null || role.roleSort === undefined) return true
    return role.roleSort >= myLevel.value
  })
})

// --- 表单校验规则函数 ---
const validateConfirmPassword = (rule, value, callback) => {
  // 1. 如果是编辑状态，且密码框和确认密码框都为空，则放行
  if (form.value.id && !form.value.password && !value) {
    return callback()
  }

  // 2. 如果确认密码为空
  if (value === '') {
    return callback(new Error('请再次输入密码'))
  }

  // 3. 核心比对
  if (value !== form.value.password) {
    return callback(new Error('两次输入密码不一致!'))
  }

  callback()
}

const rules = computed(() => ({
  username: [
    {required: true, message: '用户名不能为空', trigger: 'blur'},
    {min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur'}
  ],
  phone: [
    {required: true, message: '手机号不能为空', trigger: 'blur'},
    { pattern: /^1[0-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  password: [
    {required: !form.value.id, message: '密码不能为空', trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  confirmPassword: [
    {
      required: !form.value.id,
      validator: validateConfirmPassword, // 直接引用函数名
      trigger: 'blur'
    }
  ],
  realName: [
    {required: true, message: '真实姓名不能为空', trigger: 'blur'}
  ],
  roleIds: [
    {
      type: 'array',
      required: true,
      message: '请至少分配一个角色',
      trigger: 'change'
    }
  ]
}))

// 获取列表
const getUserPageList = async () => {
  loading.value = true
  try {
    const res = await userApi.page(queryParams.value)
    userList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取列表失败', error)
  } finally {
    loading.value = false
  }
}

// --- 头像上传前校验 ---
const beforeAvatarUpload = (rawFile) => {
  // 1. 定义允许的图片格式
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']

  // 2. 校验格式
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('上传头像图片只能是 JPG/JPEG/PNG/GIF 格式!')
    return false // 阻止上传
  }

  // 3. 校验大小 (2MB = 2 * 1024 * 1024 bytes)
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false // 阻止上传
  }

  // 4. 校验通过，允许上传
  return true
}


// 新增按钮：弹出并清空内容
const handleAdd = () => {
  drawerVisible.value = true
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
      confirmPassword: '',
      roleIds: [] // 确保初始化了空数组
    }
  })
}

// 修改 handleEdit 逻辑
const handleEdit = async (row) => {
  loading.value = true
  try {
    const res = await userApi.getById(row.id)
    const userInfo = res.data

    drawerVisible.value = true

    nextTick(() => {
      if (userFormRef.value) userFormRef.value.clearValidate()

      // 填充表单
      form.value = {
        ...userInfo,
        confirmPassword: '',
        password: ''
      }

      // 【关键保护】：如果该用户原本拥有的角色中，包含了我无权操作的高级角色
      // 我们需要确保这些角色 ID 不会被错误地通过下拉框改掉
      // filteredRoleOptions 会自动处理显示逻辑，但为了安全，
      // 我们要确保 form.roleIds 里只包含我能看到的（或者是原样保留）
    })
  } catch (error) {
    console.error('获取用户详情失败', error)
  } finally {
    loading.value = false
  }
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
          const updateData = {...form.value}
          if (!updateData.password) {
            delete updateData.password
            delete updateData.confirmPassword
          }
          // 确保 ID 一定存在
          const id = form.value.id
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


//  handleImageUpload
const handleImageUpload = async (options) => {
  // 只有 beforeAvatarUpload 返回 true，这里才会被调用
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

const handleDelete = (row) => {
  const message = `确定要删除用户 <b style="color: #409EFF;">${row.realName}</b> (账号: ${row.username}) 吗？`

  ElMessageBox.confirm(message, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    dangerouslyUseHTMLString: true
  }).then(() => {
    doDeleteRequest([row.id])
  })
}

/**
 * 完善后的批量删除逻辑
 */
const handleBatchDelete = () => {
  // 1. 获取原始选中的所有行
  const selectedRows = multipleSelection.value
  if (!selectedRows.length) return

  // 2. 过滤出真正有权操作的行（排除自己和高职级用户）
  const validRows = selectedRows.filter(row => canIAction(row))
  const invalidCount = selectedRows.length - validRows.length
  const validIds = validRows.map(row => row.id)

  // 3. 情况 A：选中的全部都不能操作
  if (validIds.length === 0) {
    return ElMessageBox.alert(
        '选中的用户均包含<b>高职级账号</b>或<b>当前登录账号</b>，系统已自动拦截删除请求。',
        '权限不足',
        { dangerouslyUseHTMLString: true, type: 'error' }
    )
  }

  // 4. 情况 B：部分能删，部分不能删，或者全部能删
  let confirmMessage = ''
  if (invalidCount > 0) {
    confirmMessage = `
      <div style="color: #606266; line-height: 1.6;">
        当前选中 <span style="color: #409EFF; font-weight: bold;">${selectedRows.length}</span> 名用户：<br/>
        - 可操作人数：<span style="color: #67C23A; font-weight: bold;">${validRows.length}</span> 名<br/>
        - 已自动排除：<span style="color: #F56C6C; font-weight: bold;">${invalidCount}</span> 名 (高职级或本人)<br/><br/>
        <span style="color: #E6A23C;">确定要继续删除这 <b style="font-size: 16px;">${validRows.length}</b> 个有效账号吗？</span><br/>
        <small style="color: #909399;">此操作不可撤销！</small>
      </div>
    `
  } else {
    confirmMessage = `
      <div style="color: #606266;">
        确定要删除选中的 <span style="color: #F56C6C; font-weight: bold; font-size: 16px;">${validRows.length}</span> 名用户吗？<br/>
        <small style="color: #909399;">此操作不可回滚，请谨慎操作。</small>
      </div>
    `
  }

  // 5. 弹出详细确认框
  ElMessageBox.confirm(confirmMessage, '危险操作确认', {
    confirmButtonText: '确定永久删除',
    cancelButtonText: '取消',
    confirmButtonClass: 'el-button--danger', // 让确定按钮变红
    type: 'warning',
    dangerouslyUseHTMLString: true,
    distinguishCancelAndClose: true
  }).then(() => {
    // 调用之前的执行删除方法
    doDeleteRequest(validIds)
  }).catch(() => {
    // 用户取消删除
  })
}

/**
 * 提取出的实际请求逻辑（保持代码整洁）
 */
const doDeleteRequest = async (ids) => {
  loading.value = true
  try {
    const res = await userApi.delete(ids)
    ElMessage.success(res.msg || `成功删除 ${ids.length} 名用户`)

    // 自动翻页逻辑：如果当前页删光了，跳回上一页
    if (userList.value.length === ids.length && queryParams.value.pageNum > 1) {
      queryParams.value.pageNum--
    }
    getUserPageList()
  } catch (error) {
    console.error('批量删除失败', error)
  } finally {
    loading.value = false
  }
}

/**
 * 完善后的重置密码
 * @param row 目标用户信息
 */
const handleReset = (row) => {
  // 1. 基础权限校验（虽然按钮已置灰，但函数内部建议再校验一次）
  if (!canIAction(row)) {
    return ElMessage.error('权限不足：无法操作职级高于您的账号')
  }

  ElMessageBox.confirm(
      `确认要重置用户 "${row.realName}" (账号: ${row.username}) 的登录密码吗？`,
      '安全警告',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning',
        buttonSize: 'default'
      }
  ).then(async () => {
    // 开启 loading 效果
    loading.value = true
    try {
      const res = await userApi.resetPassword(row.id)
      // res.msg: "密码已重置成功", res.data: "123456"
      ElMessageBox.alert(
          `用户 <b>${row.realName}</b> 的密码重置成功✅️<br/><br/>
         <span style="color: #f56c6c; font-size: 18px; font-weight: 500; letter-spacing: 1px;">${res.data}</span><br/><br/>
         请告知用户登录后及时修改。`,
          '重置结果',
          {
            dangerouslyUseHTMLString: true,
            type: 'success',
            confirmButtonText: '知道了'
          }
      )
    } catch (error) {
      console.error('重置密码请求失败', error)
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 用户点击取消，无需处理
  })
}

/**
 * 完善后的状态切换
 * @param row 目标行
 * @param val 切换后的新值 (0 或 1)
 */
const handleStatusChange = (row, val) => {
  const statusName = val === 1 ? '启用' : '停用'

  // 1. 职级校验拦截
  if (!canIAction(row)) {
    // 强制恢复原状（因为 switch 已经偏向新值了）
    row.status = val === 1 ? 0 : 1
    return ElMessage.error('权限不足：无法修改高职级账号状态')
  }

  ElMessageBox.confirm(
      `您确定要 <span style="color: ${val === 1 ? '#67C23A' : '#F56C6C'}; font-weight: bold;">${statusName}</span> 用户 "${row.realName}" 吗？`,
      '系统提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: val === 1 ? 'success' : 'error',
        dangerouslyUseHTMLString: true
      }
  ).then(async () => {
    // 2. 开启行内 Loading
    row.statusLoading = true
    try {
      await userApi.changeStatus(row.id, val)
      ElMessage.success(`用户 "${row.realName}" 已${statusName}`)
    } catch (error) {
      // 3. 失败时恢复原状
      row.status = val === 1 ? 0 : 1
      console.error('状态修改 API 调用失败', error)
    } finally {
      row.statusLoading = false
    }
  }).catch(() => {
    // 4. 点击取消时，必须手动将 switch 拨回到原来的状态
    row.status = val === 1 ? 0 : 1
  })
}

onMounted(() => {
  getUserPageList()
  getRoleOptions()
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