<template>
  <div class="menu-container">
    <div class="search-card">
      <el-form inline class="search-form">
        <el-form-item>
          <el-button type="primary" @click="getMenuList">
            <el-icon>
              <Refresh/>
            </el-icon>
            刷新菜单
          </el-button>
          <el-button type="success" @click="handleAdd(0)">
            <el-icon>
              <Plus/>
            </el-icon>
            新增顶级目录
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table
          v-loading="loading"
          :data="menuList"
          row-key="id"
          border
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          header-cell-class-name="table-header"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="180"/>
        <el-table-column prop="icon" label="图标" align="center" width="80">
          <template #default="scope">
            <el-icon v-if="scope.row.icon && scope.row.icon !== '#'">
              <component :is="scope.row.icon"/>
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 'M'" effect="light">目录</el-tag>
            <el-tag v-else-if="scope.row.type === 'C'" type="success" effect="light">菜单</el-tag>
            <el-tag v-else type="info" effect="light">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" align="center" width="80"/>
        <el-table-column prop="perms" label="权限标识" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip/>
        <el-table-column label="状态" align="center" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.type !== 'F'" link type="primary" plain :icon="Plus" size="small"  @click="handleAdd(scope.row.id)">新增
            </el-button>
            <el-button link type="warning" plain :icon="Edit" size="small"  @click="handleEdit(scope.row)">修改</el-button>
            <el-button link type="danger" plain :icon="Delete" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="form.id ? '编辑菜单' : '新增菜单'" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                  v-model="form.parentId"
                  :data="menuOptions"
                  :props="{ label: 'menuName', value: 'id', children: 'children' }"
                  value-key="id"
                  placeholder="选择上级菜单"
                  check-strictly
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="type">
              <el-radio-group v-model="form.type">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.type !== 'F'">
            <el-form-item label="菜单图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入图标名称 (如 Setting)">
                <template #prefix>
                  <el-icon v-if="form.icon">
                    <component :is="form.icon"/>
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.type !== 'F'">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.type === 'C'">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.type !== 'M'">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="请输入权限标识"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus, Edit, Delete, Refresh} from '@element-plus/icons-vue'
import menuApi from '@/api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const menuList = ref([])
const menuOptions = ref([])
const menuFormRef = ref(null)

const form = ref({
  id: null,
  parentId: 0,
  menuName: '',
  icon: '',
  type: 'M',
  orderNum: 0,
  path: '',
  component: '',
  perms: '',
  status: 1
})

const rules = {
  menuName: [{required: true, message: "菜单名称不能为空", trigger: "blur"}],
  orderNum: [{required: true, message: "菜单顺序不能为空", trigger: "blur"}],
  path: [{required: true, message: "路由地址不能为空", trigger: "blur"}],
  type: [{required: true, message: "类型不能为空", trigger: "change"}]
}

const getMenuList = async () => {
  loading.value = true
  try {
    const res = await menuApi.list()
    menuList.value = res.data
    // 构造下拉选择的树（加入“顶级类目”）
    menuOptions.value = [{id: 0, menuName: '主目录', children: res.data}]
  } finally {
    loading.value = false
  }
}

const handleAdd = (parentId) => {
  dialogVisible.value = true
  nextTick(() => {
    menuFormRef.value?.resetFields()
    form.value = {id: null, parentId: parentId || 0, type: 'M', orderNum: 0, status: 1, icon: ''}
  })
}

const handleEdit = (row) => {
  dialogVisible.value = true
  nextTick(() => {
    form.value = {...row}
  })
}

const submitForm = async () => {
  await menuFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.value.id) {
          await menuApi.update(form.value.id, form.value)
          ElMessage.success("修改成功")
        } else {
          await menuApi.add(form.value)
          ElMessage.success("新增成功")
        }
        dialogVisible.value = false
        getMenuList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除菜单 "${row.menuName}" 吗？`, "警告", {
    type: 'warning'
  }).then(async () => {
    await menuApi.delete(row.id)
    ElMessage.success("删除成功")
    getMenuList()
  }).catch(() => {
  })
}

onMounted(() => {
  getMenuList()
})
</script>

<style scoped>
.menu-container {
  padding: 0;
}

.search-card, .table-card {
  background: #fff;
  border-radius: 5px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.table-header) {
  background-color: rgba(76, 127, 181, 0.89) !important;
  color: #ffffff;
}
</style>