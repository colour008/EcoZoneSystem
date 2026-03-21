<template>
  <div class="enterprise-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.companyName" placeholder="请输入企业名称" clearable
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="queryParams.userName" placeholder="请输入负责人姓名" clearable @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="信用代码">
          <el-input v-model="queryParams.creditCode" placeholder="请输入信用代码" clearable @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option v-for="(val, key) in statusMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
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
          申请入驻
        </el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon>
            <Delete/>
          </el-icon>
          批量删除
        </el-button>
      </div>

      <el-table
          ref="enterpriseTableRef"
          v-loading="loading"
          :data="enterpriseList"
          border stripe
          @selection-change="handleSelectionChange"
          header-cell-class-name="table-header"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="ID" prop="id" width="70" align="center"/>
        <el-table-column label="企业名称" prop="companyName" min-width="180" align="center" show-overflow-tooltip/>
        <el-table-column label="信用代码" prop="creditCode" min-width="140" align="center">
          <template #default="scope">
            <el-text class="mx-1" type="primary" size="small">{{ scope.row.creditCode }}</el-text>
          </template>
        </el-table-column>
        <el-table-column label="相关负责人" prop="userName" min-width="100" align="center">
          <template #default="scope">
            <el-tag effect="plain" size="small">{{ scope.row.userName || '未绑定' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="行业" prop="industry" width="120" align="center"/>
        <el-table-column label="楼宇/房间" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.buildingNo || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系方式" min-width="150" align="center">
          <template #default="scope">
            <div style="font-size: 12px">
              <div>{{ scope.row.contactPerson }}</div>
              <div style="color: #909399">{{ scope.row.contactPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusMap[scope.row.status]?.type" effect="light">
              {{ statusMap[scope.row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="170" align="center"/>

        <el-table-column label="操作" align="center" min-width="200" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button link type="success" plain :icon="Check" size="small" @click="handleAudit(scope.row, 1)">通过
              </el-button>
              <el-button link type="danger" plain :icon="Close" size="small" @click="handleAudit(scope.row, 2)">驳回
              </el-button>
            </template>
            <el-button link type="primary" plain :icon="View" size="small" @click="handleDetail(scope.row)">详情
            </el-button>
            <el-button link type="danger" plain :icon="Delete" size="small" @click="handleDelete(scope.row)">删除
            </el-button>
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
            @size-change="getList"
            @current-change="getList"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="提交企业入驻申请" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" style="padding: 10px 20px">
        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请与工商注册名称一致"/>
        </el-form-item>
        <el-form-item label="信用代码" prop="creditCode">
          <el-input v-model="form.creditCode" placeholder="18位统一社会信用代码"/>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-input v-model="form.industry" placeholder="例如：软件开发"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向楼宇" prop="buildingNo">
              <el-input v-model="form.buildingNo" placeholder="楼栋-房间号"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="手机或座机"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div style="padding: 10px 20px">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确认提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, Plus, Check, Close, View, Delete} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'

// 状态映射配置
const statusMap = {
  0: {label: '待审核', type: 'warning'},
  1: {label: '已入驻', type: 'success'},
  2: {label: '已驳回', type: 'danger'},
  3: {label: '已迁出', type: 'info'}
}

const loading = ref(false)
const submitLoading = ref(false)
const enterpriseList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const enterpriseTableRef = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  companyName: '',
  userName: '',
  creditCode: '',
  status: null
})

const form = ref({
  companyName: '',
  creditCode: '',
  buildingNo: '',
  industry: '',
  contactPerson: '',
  contactPhone: ''
})

const rules = {
  companyName: [{required: true, message: '企业名称不能为空', trigger: 'blur'}],
  creditCode: [
    {required: true, message: '信用代码不能为空', trigger: 'blur'},
    {pattern: /^[0-9A-Z]{18}$/, message: '请输入正确的18位统一社会信用代码', trigger: 'blur'}
  ],
  contactPerson: [{required: true, message: '联系人不能为空', trigger: 'blur'}],
  contactPhone: [{required: true, message: '联系电话不能为空', trigger: 'blur'}]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await enterpriseApi.page(queryParams.value)
    enterpriseList.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 10, companyName: '', userName: '', creditCode: '', status: null}
  handleQuery()
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handleAdd = () => {
  form.value = {companyName: '', creditCode: '', buildingNo: '', industry: '', contactPerson: '', contactPhone: ''}
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await enterpriseApi.apply(form.value)
        ElMessage.success('申请提交成功，请等待系统审核')
        dialogVisible.value = false
        getList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleAudit = (row, status) => {
  const text = status === 1 ? '通过' : '驳回'
  ElMessageBox.confirm(`确定要 [${text}] 企业 "${row.companyName}" 的入驻申请吗？`, '审核确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: status === 1 ? 'success' : 'warning'
  }).then(async () => {
    await enterpriseApi.audit(row.id, status)
    ElMessage.success('审核操作成功')
    getList()
  }).catch(() => {
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要彻底删除企业 "${row.companyName}" 的数据吗？此操作不可恢复。`, '警告', {
    type: 'error'
  }).then(async () => {
    // 假设 api 有 delete 接口，这里对应你 Mapper 里的 deleteByIds
    await enterpriseApi.delete([row.id])
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {
  })
}

const handleBatchDelete = () => {
  const ids = multipleSelection.value.map(item => item.id)
  ElMessageBox.confirm(`确定要批量删除选中的 ${ids.length} 家企业吗？`, '警告', {
    type: 'error'
  }).then(async () => {
    await enterpriseApi.delete(ids)
    ElMessage.success('批量删除成功')
    enterpriseTableRef.value.clearSelection()
    getList()
  }).catch(() => {
  })
}

const handleDetail = (row) => {
  ElMessage.info(`查看企业: ${row.companyName} 的详细资料`)
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.enterprise-container {
  min-height: calc(100vh - 84px);
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
  display: flex;
  justify-content: flex-end;
}
</style>