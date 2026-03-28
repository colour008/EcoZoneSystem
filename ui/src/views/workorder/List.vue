<template>
  <div class="workorder-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="工单编号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入单号" clearable style="width: 180px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="诉求标题">
          <el-input v-model="queryParams.title" placeholder="模糊搜索标题" clearable style="width: 180px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable style="width: 120px">
            <el-option label="维修报备" :value="1"/>
            <el-option label="业务咨询" :value="2"/>
            <el-option label="投诉建议" :value="3"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
            <el-option v-for="(val, key) in statusMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card class="box-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe style="width: 100%"
                header-cell-class-name="table-header">
        <el-table-column prop="orderNo" label="工单编号" width="180" show-overflow-tooltip align="center"/>
        <el-table-column prop="enterpriseName" label="提报企业" min-width="180" show-overflow-tooltip align="center"/>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeMap[row.type]?.tag || 'info'">{{ typeMap[row.type]?.label || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="诉求标题" min-width="200" show-overflow-tooltip align="center"/>
        <el-table-column prop="contactPerson" label="提报人" width="120" align="center"/>
        <el-table-column prop="handlerName" label="受理人" width="120" align="center">
          <template #default="{ row }">
            {{ row.handlerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="workerName" label="处理人" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.workerName" type="success" size="small">{{ row.workerName }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.tag" effect="dark" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" align="center"/>

        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" size="small" @click="handleDetail(row)">详情</el-button>
            <el-button
                v-if="row.status === 0"
                link
                type="warning"
                icon="UserFilled"
                size="small"
                @click="handleDispatch(row)">
              受理并指派
            </el-button>
            <el-button
                v-if="row.status === 1"
                link
                type="success"
                icon="Check"
                size="small"
                @click="handleProcess(row)">
              处理反馈
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="getList"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="工单详情与处理流转" size="600px" destroy-on-close>
      <div v-if="currentOrder" class="detail-container">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="工单编号" :span="2">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="提报企业" :span="2">{{ currentOrder.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="业务类型">
            <el-tag :type="typeMap[currentOrder.type]?.tag" size="small">{{
                typeMap[currentOrder.type]?.label
              }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusMap[currentOrder.status]?.tag" effect="dark" size="small">
              {{ statusMap[currentOrder.status]?.label }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentOrder.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="受理专员">{{ currentOrder.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处理人员">{{ currentOrder.workerName || '-' }}</el-descriptions-item>

          <el-descriptions-item label="诉求标题" :span="2">{{ currentOrder.title }}</el-descriptions-item>
          <el-descriptions-item label="诉求详情" :span="2">
            <div class="rich-content-view">{{ currentOrder.content || '无详情说明' }}</div>
          </el-descriptions-item>

          <el-descriptions-item label="附件图片" :span="2"
                                v-if="currentOrder.imageList && currentOrder.imageList.length > 0">
            <div class="image-list">
              <el-image
                  v-for="(img, index) in currentOrder.imageList"
                  :key="index"
                  :src="img"
                  :preview-src-list="currentOrder.imageList"
                  :initial-index="index"
                  fit="cover"
                  class="preview-img"
              />
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">处理进度</el-divider>
        <el-timeline style="margin-top: 20px; padding-left: 10px;">
          <el-timeline-item :timestamp="currentOrder.createTime" type="primary" size="large">
            企业提交诉求 (提报人: <b>{{ currentOrder.contactPerson }}</b>)
          </el-timeline-item>

          <el-timeline-item v-if="currentOrder.acceptTime" :timestamp="currentOrder.acceptTime" type="warning" hollow>
            园区受理指派
            <div style="margin-top: 8px; font-size: 13px; color: #606266;">
              <p>受理专员:
                <el-tag size="small" effect="plain">{{ currentOrder.handlerName || '系统' }}</el-tag>
              </p>
              <p v-if="currentOrder.workerName" style="margin-top: 4px;">
                指派处理人:
                <el-tag size="small" type="success" effect="plain">{{ currentOrder.workerName }}</el-tag>
              </p>
            </div>
          </el-timeline-item>

          <el-timeline-item v-if="currentOrder.finishTime" :timestamp="currentOrder.finishTime" type="success">
            工单处理完成 (处理人: <b>{{ currentOrder.workerName || currentOrder.handlerName }}</b>)
            <div v-if="currentOrder.remark" class="common-opinions" style="margin-top: 8px;">
              处理反馈: {{ currentOrder.remark }}
            </div>
          </el-timeline-item>

          <el-timeline-item v-if="currentOrder.status === 3" type="info">
            企业已评价 (评分: {{ currentOrder.score }}星)
            <div v-if="currentOrder.commentText" class="common-opinions" style="margin-top: 8px;">
              评价内容: {{ currentOrder.commentText }}
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-drawer>

    <el-dialog v-model="dispatchVisible" title="工单受理与指派" width="450px">
      <el-form :model="dispatchForm" ref="dispatchFormRef" label-width="80px">
        <div style="text-align: center; margin-bottom: 20px;">
          <el-icon :size="48" color="#e6a23c">
            <WarningFilled/>
          </el-icon>
          <p style="margin-top: 15px; font-size: 16px;">确认指派该工单吗？</p>
          <p style="color: #909399; font-size: 13px; margin: 10px 0;">
            单号: <b style="color: #469efa">{{ currentOrder?.orderNo }}</b>
          </p>
        </div>

        <el-form-item label="处理人" prop="workerId"
                      :rules="[{ required: true, message: '请选择指派工人', trigger: 'change' }]">
          <el-select v-model="dispatchForm.workerId" placeholder="请选择负责处理的工人/员工" style="width: 100%"
                     filterable>
            <el-option
                v-for="item in workerOptions"
                :key="item.id"
                :label="item.realName"
                :value="item.id"
            >
              <span>{{ item.realName }}</span>
              <span style="float: right; color: #8492a6; font-size: 12px">{{ item.username }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <div style="color: #67c23a; font-size: 13px; margin-top: 10px; padding: 0 10px;">
          <el-icon>
            <InfoFilled/>
          </el-icon>
          受理后，状态将变更为“处理中”，处理人可在 H5 端查看任务。
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dispatchVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDispatch" :loading="submitLoading">确认受理并指派</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="processVisible" title="工单处理反馈" width="500px">
      <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="80px">
        <el-form-item label="处理结果" prop="remark">
          <el-input
              v-model="processForm.remark"
              type="textarea"
              :rows="4"
              placeholder="请详细描述处理结果，该反馈将对企业可见..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProcess" :loading="submitLoading">提交反馈并结案</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {WarningFilled, InfoFilled, UserFilled} from '@element-plus/icons-vue'
import workOrderApi from '@/api/workOrder'
import userApi from '@/api/user'

const typeMap = {
  1: {label: '维修报备', tag: 'danger'},
  2: {label: '业务咨询', tag: 'primary'},
  3: {label: '投诉建议', tag: 'warning'}
}

const statusMap = {
  0: {label: '待受理', tag: 'warning'},
  1: {label: '处理中', tag: 'primary'},
  2: {label: '已办结', tag: 'success'},
  3: {label: '已评价', tag: 'info'}
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1, pageSize: 10, orderNo: '', title: '', type: null, status: null
})

const getList = async () => {
  loading.value = true
  try {
    const res = await workOrderApi.page(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.orderNo = ''
  queryParams.title = ''
  queryParams.type = null
  queryParams.status = null
  handleQuery()
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getList()
}

// 详情逻辑
const detailVisible = ref(false)
const currentOrder = ref(null)
const handleDetail = (row) => {
  if (row.images && typeof row.images === 'string') {
    row.imageList = row.images.split(',')
  } else if (!row.imageList) {
    row.imageList = []
  }
  currentOrder.value = row
  detailVisible.value = true
}

// 指派逻辑
const dispatchVisible = ref(false)
const submitLoading = ref(false)
const workerOptions = ref([])
const dispatchFormRef = ref(null)
const dispatchForm = reactive({orderId: null, workerId: null})

const loadWorkers = async () => {
  const res = await userApi.getWorkers()
  if (res.code === 200) workerOptions.value = res.data
}

const handleDispatch = (row) => {
  currentOrder.value = row
  dispatchForm.orderId = row.id
  dispatchForm.workerId = null
  dispatchVisible.value = true
  loadWorkers()
}

const submitDispatch = async () => {
  await dispatchFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await workOrderApi.dispatch(dispatchForm)
        if (res.code === 200) {
          ElMessage.success('工单已成功受理并指派')
          dispatchVisible.value = false
          getList()
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 处理反馈逻辑
const processVisible = ref(false)
const processFormRef = ref(null)
const processForm = reactive({id: null, remark: ''})
const processRules = {remark: [{required: true, message: '处理反馈不能为空', trigger: 'blur'}]}

const handleProcess = (row) => {
  processForm.id = row.id
  processForm.remark = ''
  processVisible.value = true
}

const submitProcess = async () => {
  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res = await workOrderApi.process(processForm)
        if (res.code === 200) {
          ElMessage.success('工单已处理结案')
          processVisible.value = false
          getList()
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.workorder-container {
  min-height: calc(100vh - 100px);
}

.search-card {
  background-color: #fff;
  padding: 20px 20px 0 20px;
  margin-bottom: 16px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-container {
  padding: 0 10px;
}

.rich-content-view {
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
  min-height: 60px;
  white-space: pre-wrap;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.preview-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid #EBEEF5;
  cursor: pointer;
}

.common-opinions {
  background-color: #f4f4f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 13px;
  color: #909399;
}

:deep(.table-header) {
  background-color: rgba(76, 127, 181, 0.89) !important;
  color: #ffffff;
  font-weight: 500;
}
</style>