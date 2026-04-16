<template>
  <div class="inquiry-manage-container">
    <div class="glass-search-card">
      <el-form :model="queryParams" inline class="modern-form">
        <el-form-item label="申请人">
          <el-input v-model="queryParams.applicantName" placeholder="姓名" clearable @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.companyName" placeholder="搜索企业关键词" clearable
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 130px">
            <el-option v-for="(val, key) in statusMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="处理人">
          <el-input v-model="queryParams.handlerName" placeholder="跟进人姓名" clearable @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon>
              <Search/>
            </el-icon>
            查询
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

    <div class="content-body" v-loading="loading">
      <el-empty v-if="inquiryList.length === 0 && !loading" description="暂无相关意向留言"/>

      <el-row :gutter="20">
        <el-col v-for="item in inquiryList" :key="item.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="modern-inquiry-card" :class="`status-accent-${item.status}`" shadow="hover">
            <div class="card-head">
              <div class="head-left">
                <span class="type-indicator">{{ typeMap[item.type] }}</span>
                <span class="time-tag">
                  <el-icon><Clock/></el-icon>
                  {{ item.createTime?.split(' ')[0] }} 提交
               </span>
              </div>
              <el-tag :type="statusMap[item.status]?.type" size="small" effect="light" round>
                {{ statusMap[item.status]?.label }}
              </el-tag>
            </div>

            <div class="card-main">
              <h3 class="user-name">{{ item.applicantName }}</h3>
              <div class="company-info">
                <el-icon>
                  <OfficeBuilding/>
                </el-icon>
                <span>{{ item.companyName || '个人意向' }}</span>
              </div>
              <div class="contact-info">
                <el-icon>
                  <Phone/>
                </el-icon>
                <span>{{ item.contactPhone }}</span>
              </div>
            </div>

            <div class="card-divider"></div>

            <div class="card-foot">
              <div class="handler-box">
                <span class="label">跟进人:</span>
                <span class="value" :class="{ 'none': !item.handlerName }">
                  {{ item.handlerName || '未分配' }}
                </span>
              </div>
              <div class="action-group">
                <el-button link type="primary" @click="handleView(item)">详情</el-button>
                <el-dropdown trigger="click">
                  <el-button link type="danger">操作
                    <el-icon class="el-icon--right">
                      <ArrowDown/>
                    </el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <template v-if="![2, 3, 4].includes(item.status)">
                        <el-dropdown-item @click="openAssign(item)">
                          <el-icon>
                            <UserFilled/>
                          </el-icon>
                          指派负责人
                        </el-dropdown-item>
                        <el-dropdown-item @click="openRecord(item)">
                          <el-icon>
                            <EditPen/>
                          </el-icon>
                          跟进记录
                        </el-dropdown-item>
                        <el-dropdown-item v-if="item.companyName" divided class="text-success"
                                          @click="handleConvert(item)">
                          <el-icon><Select/></el-icon>
                          转为入驻企业
                        </el-dropdown-item>
                      </template>
                      <el-dropdown-item v-if="item.status === 4" class="text-danger" @click="handleDelete(item)">
                        <el-icon>
                          <Delete/>
                        </el-icon>
                        彻底删除
                      </el-dropdown-item>
                      <el-dropdown-item v-if="[2, 3].includes(item.status)" disabled>流程已结束</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination-footer">
      <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[8, 16, 24]"
          layout="total, sizes, prev, pager, next"
          background
          @size-change="getList"
          @current-change="getList"
      />
    </div>

    <el-dialog v-model="detailVisible" title="意向留言详情" width="800px" custom-class="inquiry-detail-dialog"
               destroy-on-close>
      <div class="detail-container">
        <el-descriptions :column="2" border class="info-desc">
          <el-descriptions-item label="申请人">{{ currentInquiry.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="意向类型">
            <el-tag size="small">{{ typeMap[currentInquiry.type] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="企业名称" :span="2">{{
              currentInquiry.companyName || '个人'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentInquiry.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentInquiry.createTime }}</el-descriptions-item>
          <el-descriptions-item label="留言备注" :span="2">
            <div class="remark-content">{{ currentInquiry.remark || '无备注内容' }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="follow-timeline-section">
          <div class="section-title">跟进动态流水</div>
          <div class="timeline-box">
            <el-timeline v-if="followHistory.length > 0">
              <el-timeline-item
                  v-for="(record, index) in followHistory"
                  :key="index"
                  :timestamp="record.createTime"
                  :type="index === 0 ? 'primary' : ''"
                  :hollow="index !== 0"
              >
                <div class="record-card">
                  <div class="record-header">
                    <span class="handler">处理人：{{ record.handlerName }}</span>
                  </div>
                  <p class="record-text">{{ record.content }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else :image-size="60" description="暂无跟进过程记录"/>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="指派跟进负责人" width="400px">
      <el-form label-position="top">
        <el-form-item label="选择处理人" required>
          <el-select v-model="assignForm.handlerId" filterable placeholder="请输入姓名搜索" style="width: 100%">
            <el-option v-for="user in userOptions" :key="user.id" :label="user.realName" :value="user.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" :loading="btnLoading" @click="submitAssign">确认指派</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordVisible" title="录入跟进结果" width="500px">
      <el-form :model="recordForm" label-position="top">
        <el-form-item label="跟进后状态更新" required>
          <el-radio-group v-model="recordForm.status">
            <el-radio-button :label="1">跟进中</el-radio-button>
            <el-radio-button :label="3">已完结</el-radio-button>
            <el-radio-button :label="4">无效留言</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="本次沟通详情" required>
          <el-input
              v-model="recordForm.result"
              type="textarea"
              :rows="4"
              placeholder="请详细描述本次沟通的具体情况、客户反馈及下一步计划..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordVisible = false">取消</el-button>
        <el-button type="primary" :loading="btnLoading" @click="submitRecord">保存记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Search, Refresh, OfficeBuilding, Phone, UserFilled, EditPen,
  ArrowDown, Select, Delete, Clock
} from '@element-plus/icons-vue'
import inquiryApi from '@/api/inquiry'
import noticeApi from '@/api/notice'

// 映射常量
const statusMap = {
  0: {label: '待处理', type: 'danger'},
  1: {label: '跟进中', type: 'primary'},
  2: {label: '已转入驻', type: 'success'},
  3: {label: '已完结', type: 'info'},
  4: {label: '无效记录', type: 'info'}
}
const typeMap = {1: '企业入驻', 2: '人才求职', 3: '配套服务', 4: '其他反馈'}

// 响应式数据
const loading = ref(false)
const btnLoading = ref(false)
const inquiryList = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1, pageSize: 8, applicantName: '', companyName: '',
  status: null, handlerName: ''
})

const detailVisible = ref(false)
const currentInquiry = ref({})
const followHistory = ref([])

const assignVisible = ref(false)
const assignForm = ref({id: null, handlerId: null})
const userOptions = ref([])

const recordVisible = ref(false)
const recordForm = ref({id: null, result: '', status: 1})

// 核心逻辑方法
const getList = async () => {
  loading.value = true
  try {
    const res = await inquiryApi.getAdminPage(queryParams.value)
    inquiryList.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList()
}

const resetQuery = () => {
  queryParams.value = {pageNum: 1, pageSize: 8, applicantName: '', companyName: '', status: null, handlerName: ''}
  handleQuery()
}

// 详情查看：合并主表与跟进记录表
const handleView = async (row) => {
  currentInquiry.value = {...row}
  followHistory.value = []
  detailVisible.value = true
  try {
    const res = await inquiryApi.getFollowRecords(row.id)
    followHistory.value = res.data || []
  } catch (e) {
    console.error("加载跟进记录失败")
  }
}

// 指派
const openAssign = async (row) => {
  assignForm.value = {id: row.id, handlerId: row.handlerId}
  assignVisible.value = true
  if (userOptions.value.length === 0) {
    const res = await noticeApi.getActiveUsers()
    userOptions.value = res.data
  }
}

const submitAssign = async () => {
  if (!assignForm.value.handlerId) return ElMessage.warning('请选择负责人')
  btnLoading.value = true
  try {
    await inquiryApi.assignHandler(assignForm.value.id, assignForm.value.handlerId)
    ElMessage.success('指派成功')
    assignVisible.value = false
    getList()
  } finally {
    btnLoading.value = false
  }
}

// 写跟进（后端会同时更新主表状态并插入明细表）
const openRecord = (row) => {
  recordForm.value = {id: row.id, result: '', status: row.status === 0 ? 1 : row.status}
  recordVisible.value = true
}

const submitRecord = async () => {
  if (!recordForm.value.result) return ElMessage.warning('跟进内容不能为空')
  btnLoading.value = true
  try {
    await inquiryApi.recordFollowUp(recordForm.value)
    ElMessage.success('记录已更新')
    recordVisible.value = false
    getList()
  } finally {
    btnLoading.value = false
  }
}

// 转入驻
const handleConvert = (row) => {
  ElMessageBox.confirm(`确认将“${row.companyName}”转为入驻企业？系统将自动生成企业账号，初始密码为联系电话。`, '转化确认', {
    type: 'success', confirmButtonText: '立即转化'
  }).then(async () => {
    await inquiryApi.convertToEnterprise(row.id)
    ElMessage.success('转化成功')
    getList()
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认彻底删除该无效留言？删除后不可恢复。', '警告', {type: 'warning'}).then(async () => {
    await inquiryApi.deleteInquiry(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

onMounted(() => getList())
</script>

<style scoped>
/* ===== 全局容器 ===== */
.inquiry-manage-container {
  min-height: calc(100vh - 100px);
}

/* ===== 顶部搜索卡片 ===== */
.glass-search-card {
  background: #FFFFFF;
  padding: 20px 24px;
  border-radius: 10px;
  border: 1px solid #E5E6EB;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.modern-form {
  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 16px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #E5E6EB inset;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #165DFF inset;
    }
  }

  :deep(.el-select__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #E5E6EB inset;
    transition: all 0.2s ease;

    &:hover {
      box-shadow: 0 0 0 1px #165DFF inset;
    }
  }

  :deep(.el-button--primary) {
    background: #165DFF;
    border-color: #165DFF;
    border-radius: 8px;
    font-weight: 500;

    &:hover {
      background: #4080FF;
      border-color: #4080FF;
    }
  }

  :deep(.el-button) {
    border-radius: 8px;
    font-weight: 500;
  }
}

.modern-inquiry-card {
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  background: #FFFFFF;
}

.modern-inquiry-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 6px;
}

/* 待处理-暖橙渐变 */
.status-accent-0::before {
  background: linear-gradient(90deg, #FF7D00, #FF9A2E);
}

/* 跟进中-主色渐变 */
.status-accent-1::before {
  background: linear-gradient(90deg, #165DFF, #4080FF);
}

/* 已转入驻-绿渐变 */
.status-accent-2::before {
  background: linear-gradient(90deg, #00B42A, #36D399);
}

/* 完结/无效-灰渐变 */
.status-accent-3::before, .status-accent-4::before {
  background: linear-gradient(90deg, #86909C, #C9CDD4);
}

.modern-inquiry-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  background: rgba(22, 93, 255, 0.01);
}

/* 卡片头部 */
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 12px;
}

.head-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #86909C;
  background: rgba(134, 144, 156, 0.05);
  padding: 2px 8px;
  border-radius: 4px;
}

.time-tag .el-icon {
  font-size: 13px;
}

.type-indicator {
  font-size: 12px;
  color: #4E5969;
  background: #F2F3F5;
  padding: 3px 10px;
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
  border: none;
}

/* 卡片主体 */
.card-main {
  padding: 0 20px 16px;
}

.user-name {
  margin: 0 0 12px 0;
  font-size: 19px;
  color: #1D2129;
  font-weight: 600;
}

.company-info, .contact-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4E5969;
  margin-bottom: 8px;
}

:deep(.el-icon) {
  color: #86909C;
  font-size: 16px;
}

/* 分割线 */
.card-divider {
  height: 1px;
  background: rgba(13, 67, 117, 0.33);
  margin: 10px 0;
}

/* 卡片底部 */
.card-foot {
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #F7F8FA;
  border-radius: 5px;
}

.handler-box {
  font-size: 13px;
}

.handler-box .label {
  color: #86909C;
  margin-right: 4px;
}

.handler-box .value {
  color: #1D2129;
  font-weight: 500;
}

.handler-box .value.none {
  color: #C9CDD4;
  font-style: italic;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 8px;

  :deep(.el-button--text) {
    color: #165DFF;
    font-weight: 500;

    &:hover {
      color: #4080FF;
    }
  }

  :deep(.el-dropdown-menu) {
    border-radius: 12px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    border: 1px solid #E5E6EB;
  }
}

/* ===== 详情弹窗样式 ===== */
:deep(.inquiry-detail-dialog .el-dialog__header) {
  background: #F7F8FA;
  padding: 16px 24px;
  border-radius: 12px 12px 0 0;
  margin: 0;
}

.detail-container {
  padding: 10px 0;
}

.info-desc {
  :deep(.el-descriptions__item-label) {
    color: #86909C;
    font-weight: 500;
  }

  :deep(.el-descriptions__item-content) {
    color: #1D2129;
  }

  :deep(.el-descriptions__table) {
    border: 1px solid #E5E6EB;
  }
}

.remark-content {
  padding: 14px 0;
  border-radius: 8px;
  color: #1D2129;
  line-height: 1.7;
  font-size: 14px;
}

.follow-timeline-section {
  margin-top: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1D2129;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #165DFF;
}

.timeline-box {
  padding: 10px 0;
  max-height: 450px;
  overflow-y: auto;
}

.record-card {
  background: #FFFFFF;
  border: 1px solid #E5E6EB;
  padding: 16px;
  border-radius: 10px;
  margin-bottom: 12px;
}

.record-header {
  margin-bottom: 8px;
  font-size: 13px;
  color: #86909C;
}

.record-text {
  margin: 0;
  font-size: 14px;
  color: #1D2129;
  white-space: pre-wrap;
  line-height: 1.6;
}

/* ===== 分页组件 ===== */
.pagination-footer {
  margin-top: 32px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    .el-pagination__total, .el-pagination__sizes {
      color: #4E5969;
      font-size: 14px;
    }

    .el-pager li {
      min-width: 32px;
      height: 32px;
      border-radius: 8px;
      font-size: 14px;
    }

    .el-pager li.is-active {
      background: #165DFF;
      color: #fff;
      border-color: #165DFF;
    }
  }
}

/* ===== 通用状态文本 ===== */
.text-success {
  color: #00B42A !important;
}

.text-danger {
  color: #F53F3F !important;
}

/* ===== 空状态优化 ===== */
:deep(.el-empty) {
  padding: 60px 0;

  .el-empty__description {
    color: #86909C;
    font-size: 14px;
  }
}

:deep(.el-dialog) {
  border-radius: 15px;
  overflow: hidden;
}
</style>