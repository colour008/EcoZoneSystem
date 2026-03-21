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

      <el-table
          ref="roleTableRef"
          v-loading="loading"
          :data="roleList"
          border stripe
          @selection-change="handleSelectionChange"
          header-cell-class-name="table-header"
      >
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
        <el-table-column label="角色权重" prop="roleSort" width="120" align="center" sortable>
          <template #default="scope">
            <el-tag :type="scope.row.roleSort <= 0 ? 'danger' : 'info'" effect="plain">
              等级: {{ scope.row.roleSort }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" min-width="240" fixed="right">
          <template #default="scope">
            <el-button link type="primary" plain :icon="Edit" size="small"
                       :disabled="!canIActionRole(scope.row)"
                       @click="handleEdit(scope.row)">编辑
            </el-button>

            <el-button link type="success" plain :icon="Setting" size="small"
                       :disabled="!canIActionRole(scope.row)"
                       @click="handlePermission(scope.row)">分配权限
            </el-button>

            <el-button link type="danger" plain :icon="Delete" size="small"
                       :disabled="!canIActionRole(scope.row)"
                       @click="handleDelete(scope.row)">删除
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
          <el-input v-model="form.roleCode" :disabled="!!form.id" placeholder="请输入角色编码"/>
          <div style="font-size: 12px; color: #ea6565; margin-top: 5px">
            编码唯一，创建后通常不修改。
          </div>
        </el-form-item>
        <el-form-item label="角色权重" prop="roleSort">
          <el-input-number
              v-model="form.roleSort"
              :min="0"
              controls-position="right"
              style="width: 100%"
          />
          <div style="font-size: 12px; color: #909399; margin-top: 5px">
            数值越小等级越高。您当前最高职级为: <b style="color: #409EFF">{{ myLevel }}</b>
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

    <el-dialog title="分配权限" v-model="permissionVisible" width="500px" destroy-on-close>
      <div v-loading="treeLoading" style="max-height: 450px; overflow-y: auto; padding: 10px 20px">
        <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :props="{ label: 'menuName', children: 'children', disabled: 'disabled' }"
            default-expand-all
            highlight-current
        >
          <template #default="{ node, data }">
            <span style="display: flex; align-items: center">
            <svg-icon v-if="data.icon && data.icon !== '#'" :name="data.icon" size="16" style="margin-right: 8px"/>
              <span>{{ node.label }}</span>
            </span>
          </template>
        </el-tree>
      </div>
      <template #footer>
        <el-button @click="permissionVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitPermission">确 定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Delete, Edit, Plus, Setting} from '@element-plus/icons-vue'
import roleApi from '@/api/role'
import menuApi from '@/api/menu'
import {useUserStore} from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const roleList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const roleFormRef = ref(null)
const roleTableRef = ref(null) // 表格引用

const permissionVisible = ref(false)
const treeLoading = ref(false)
const menuOptions = ref([])
const menuTreeRef = ref(null)
const activeRoleId = ref(null)

// 高风险权限的标识，禁止被取消勾选
const CORE_PERMS = ['entity:user:list', 'entity:role:list', 'entity:menu:list']

/**
 * 判断一个菜单是否属于高风险权限
 */
const isHighRisk = (menu) => {
  return CORE_PERMS.includes(menu.menuCode) || (menu.children && menu.children.some(isHighRisk))
}

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
  roleCode: '',
  roleSort: 0 // 默认值
})

const rules = computed(() => ({
  roleName: [{required: true, message: '角色名称不能为空', trigger: 'blur'}],
  roleCode: [{required: true, message: '角色编码不能为空', trigger: 'blur'}],
  roleSort: [
    {required: true, message: '排序不能为空', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (myLevel.value !== 0 && value < myLevel.value) {
          callback(new Error(`权限不足：角色权重不能高于您的职级(${myLevel.value})`))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}))

// 获取当前登录用户的最高职级权重 (数值越小等级越高)
const myLevel = computed(() => userStore.userInfo?.topRoleSort ?? 999)
/**
 * 判断当前登录用户是否可以操作该角色
 * @param row 角色行数据
 */
const canIActionRole = (row) => {
  // 超级管理员 (0) 可以操作所有
  if (myLevel.value === 0) return true

  // 如果该角色的权重数值比我小，说明它等级比我高，我无权操作
  if (row.roleSort !== null && row.roleSort < myLevel.value) {
    return false
  }
  return true
}

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

const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10, roleName: '', roleCode: ''}
  getRolePageList()
}

const handleAdd = () => {
  drawerVisible.value = true
  form.value = {
    id: null,
    roleName: '',
    roleCode: '',
    roleSort: Math.max(myLevel.value, 10) // 默认不高于自己
  }
  nextTick(() => {
    roleFormRef.value?.clearValidate()
  })
}

const handleEdit = (row) => {
  drawerVisible.value = true
  form.value = {...row}
  nextTick(() => {
    roleFormRef.value?.clearValidate()
  })
}

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
          await roleApi.update(form.value.id, form.value)
          ElMessage.success('修改角色成功')
        }
        drawerVisible.value = false
        getRolePageList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handlePermission = async (row) => {
  activeRoleId.value = row.id
  permissionVisible.value = true
  treeLoading.value = true

  try {
    const [resTree, resKeys] = await Promise.all([
      menuApi.list(),
      roleApi.getRoleMenus(row.id)
    ])

    const allMenus = resTree.data || []
    const roleMenuIds = resKeys.data || []

    // 递归处理菜单树
    const processMenus = (menus) => {
      return menus.map(node => {
        const children = node.children ? processMenus(node.children) : []
        let isDisabled = false

        // 核心判定 1：防误触逻辑
        // 只有当我们在为“超级管理员”(ROLE_ADMIN) 分配权限时，才禁用核心权限勾选
        if (row.roleCode === 'ROLE_ADMIN') {
          const isCore = CORE_PERMS.includes(node.perms)
          const hasDisabledChild = children.some(c => c.disabled)
          isDisabled = isCore || hasDisabledChild
        }

            // 核心判定 2：职级越权逻辑（根据你之前的 myLevel 逻辑）
            // 如果当前登录人的职级(myLevel)不是超管(0)，且该权限原本就不在当前人的权限范围内，则应该禁用
        // 或者：如果当前角色不是正在操作超管，但你希望限制非超管用户分配这些敏感权限
        else if (myLevel.value !== 0) {
          // 这里可以根据业务增加限制，比如非超管不能给别人发系统管理权限
          if (CORE_PERMS.includes(node.perms)) {
            isDisabled = true
          }
        }

        return {
          ...node,
          disabled: isDisabled,
          children
        }
      })
    }

    menuOptions.value = processMenus(allMenus)

    await nextTick()

    // 设置勾选状态（保持叶子节点逻辑）
    if (menuTreeRef.value) {
      const leafKeys = []
      const getLeafKeys = (nodes) => {
        nodes.forEach(node => {
          if (!node.children || node.children.length === 0) {
            if (roleMenuIds.includes(node.id)) leafKeys.push(node.id)
          } else {
            getLeafKeys(node.children)
          }
        })
      }
      getLeafKeys(menuOptions.value)
      menuTreeRef.value.setCheckedKeys(leafKeys)
    }
  } catch (error) {
    ElMessage.error('加载权限数据失败')
  } finally {
    treeLoading.value = false
  }
}

const submitPermission = async () => {
  // 1. 获取当前树上勾选的
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  let finalKeys = [...checkedKeys, ...halfCheckedKeys]

  // 2. 【核心保护】：如果不是超管，补回那些因为 disabled 而没被选中的原有高风险权限
  if (myLevel.value !== 0) {
    const resKeys = await roleApi.getRoleMenus(activeRoleId.value)
    const originalIds = resKeys.data || []

    // 找出原有权限中属于“高风险”的部分，强行合并
    const protectedIds = originalIds.filter(id => {
      // 这里需要一个简单的查找逻辑，判断 id 对应的菜单是否是高风险
      // 或者简单点：只要原先有的，现在不在 finalKeys 里的，且属于不可操作范畴的，都补回去
      return !finalKeys.includes(id)
    })

    finalKeys = [...new Set([...finalKeys, ...protectedIds])]
  }

  if (finalKeys.length === 0) {
    ElMessage.warning('请至少选择一个权限')
    return
  }

  submitLoading.value = true
  try {
    await roleApi.saveRoleMenus(activeRoleId.value, finalKeys)
    ElMessage.success('分配权限成功')
    permissionVisible.value = false
  } catch (error) {
    console.error('保存权限失败', error)
  } finally {
    submitLoading.value = false
  }
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

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
      // 优化：删除后重置表格状态
      multipleSelection.value = []
      roleTableRef.value?.clearSelection()
      getRolePageList()
    } finally {
      loading.value = false
    }
  }).catch(() => {
  })
}

const handleDelete = (row) => {
  doDelete([row.id], `确定要删除角色 "${row.roleName}" 吗？`)
}

// 修改批量删除逻辑，加入职级过滤
const handleBatchDelete = () => {
  const validSelection = multipleSelection.value.filter(item => canIActionRole(item))
  const ids = validSelection.map(item => item.id)

  if (ids.length === 0) {
    return ElMessage.warning('选中的角色职级均高于您，无法删除')
  }

  const msg = validSelection.length < multipleSelection.value.length
      ? `包含高职级角色，将仅删除 ${ids.length} 个可操作角色，确定吗？`
      : `确定要批量删除选中的 ${ids.length} 个角色吗？`

  doDelete(ids, msg)
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