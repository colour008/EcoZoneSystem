<template>
  <div class="inquiry-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="申请人">
          <el-input v-model="queryParams.applicantName" placeholder="姓名" clearable style="width: 150px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.companyName" placeholder="搜索企业" clearable style="width: 180px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="全部类型" clearable style="width: 130px">
            <el-option v-for="(label, key) in typeMap" :key="key" :label="label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option v-for="(val, key) in statusMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="跟进人">
          <el-input v-model="queryParams.handlerName" placeholder="姓名" clearable style="width: 120px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item class="search-btns">
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

    <div class="card-list" v-loading="loading">
      <el-empty v-if="inquiryList.length === 0 && !loading" :image-size="100" description="暂无意向留言数据"/>

      <el-row :gutter="24">
        <el-col
            v-for="item in inquiryList"
            :key="item.id"
            :xs="24" :sm="12" :md="8" :lg="6"
            class="card-col"
        >
          <el-card
              class="inquiry-card"
              :class="`status-border-${item.status}`"
              shadow="hover"
              :body-style="{ padding: '0px' }"
          >
            <div class="card-inner">
              <div class="card-header">
                <el-tag size="small" effect="plain" class="type-tag">{{ typeMap[item.type] }}</el-tag>
                <el-tag :type="statusMap[item.status]?.type" size="small" round>
                  {{ statusMap[item.status]?.label }}
                </el-tag>
              </div>

              <div class="main-info">
                <h4 class="applicant-name">{{ item.applicantName }}</h4>
                <div class="company-name" :title="item.companyName">
                  <el-icon>
                    <OfficeBuilding/>
                  </el-icon>
                  <span>{{ item.companyName || '个人申请' }}</span>
                </div>
              </div>

              <div class="detail-grid">
                <div class="grid-item">
                  <span class="grid-label">联系电话</span>
                  <span class="grid-value"><el-icon><Phone/></el-icon> {{ item.contactPhone }}</span>
                </div>
                <div class="grid-item">
                  <span class="grid-label">处理人员</span>
                  <span class="grid-value" :class="{'unassigned': !item.handlerName}">
                    <el-icon><User/></el-icon> {{ item.handlerName || '未指派' }}
                  </span>
                </div>
              </div>

              <div class="divider"></div>

              <div class="remark-summary">
                <el-icon>
                  <ChatDotSquare/>
                </el-icon>
                <p class="summary-text">{{ item.remark || '此申请人未填写留言内容内容' }}</p>
              </div>

              <div class="card-footer">
                <span class="date-info">{{ item.createTime?.substring(0, 10) }}</span>
                <div class="actions">
                  <el-button link type="primary" size="small" @click="handleView(item)">详情</el-button>
                  <el-dropdown trigger="click">
                    <el-button link type="primary" size="small">
                      处理
                      <el-icon class="el-icon--right">
                        <ArrowDown/>
                      </el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu class="modern-dropdown">
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
                        <el-dropdown-item v-if="item.status !== 2 && item.companyName" divided class="convert-item"
                                          @click="handleConvert(item)">
                          <el-icon><Select/></el-icon>
                          转为入驻企业
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="pagination-container">
      <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          background
          :page-sizes="[8, 16, 32]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
      />
    </div>

    <el-dialog v-model="detailVisible" title="意向留言详情" width="750px" class="modern-dialog" destroy-on-close>
      <div class="divider"></div>
      <div class="dialog-inner">
        <div class="profile-header">
          <div class="profile-title">
            <el-tag type="primary" effect="dark">{{ typeMap[currentInquiry.type] }}</el-tag>
          </div>
          <el-tag :type="statusMap[currentInquiry.status]?.type" effect="dark" style="margin-left: 20px">
            {{ statusMap[currentInquiry.status]?.label }}
          </el-tag>
        </div>
        <el-descriptions :column="2" border class="modern-descriptions">
          <el-descriptions-item label="申请人">{{ currentInquiry.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ currentInquiry.companyName || '个人申请' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentInquiry.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentInquiry.createTime }}</el-descriptions-item>
          <el-descriptions-item label="负责人员">{{ currentInquiry.handlerName || '待指派' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentInquiry.updateTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="留言原文" :span="2">
            <div class="dialog-remark-box">{{ currentInquiry.remark || '无' }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="跟进结果" :span="2" v-if="currentInquiry.handleResult">
            <div class="dialog-result-box">{{ currentInquiry.handleResult }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">已 阅</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignVisible" title="指派跟进人员" width="420px" class="round-dialog">
      <el-form label-position="top">
        <el-form-item label="选择园区负责人" required>
          <el-select v-model="assignForm.handlerId" placeholder="搜索员工姓名" filterable style="width: 100%">
            <el-option v-for="user in userOptions" :key="user.id" :label="user.realName" :value="user.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignVisible = false">取消</el-button>
          <el-button type="primary" :loading="btnLoading" @click="submitAssign">确认指派</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="recordVisible" title="更新跟进记录" width="520px" class="round-dialog">
      <el-form :model="recordForm" label-position="top">
        <el-form-item label="跟进阶段">
          <el-radio-group v-model="recordForm.status" class="modern-radio">
            <el-radio-button :label="1">进行中</el-radio-button>
            <el-radio-button :label="3">已完结</el-radio-button>
            <el-radio-button :label="4">无效</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="详细反馈记录" required>
          <el-input v-model="recordForm.result" type="textarea" :rows="5"
                    placeholder="请记录沟通细节、客户意向度等信息..."/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="recordVisible = false">取消</el-button>
          <el-button type="primary" :loading="btnLoading" @click="submitRecord">保存进度</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Phone, UserFilled, Search, Refresh, Document, OfficeBuilding,
  EditPen, Select, ArrowDown, Calendar, User, ChatDotSquare
} from '@element-plus/icons-vue'
import inquiryApi from '@/api/inquiry'
import noticeApi from '@/api/notice'

// 业务映射
const statusMap = {
  0: {label: '待处理', type: 'danger'},
  1: {label: '跟进中', type: 'primary'},
  2: {label: '转入驻', type: 'success'},
  3: {label: '已完结', type: 'info'},
  4: {label: '已关闭', type: 'info'}
}
const typeMap = {
  1: '企业入驻', 2: '人才求职', 3: '配套服务', 4: '留言反馈'
}

// 变量定义
const loading = ref(false)
const btnLoading = ref(false)
const inquiryList = ref([])
const total = ref(0)
const userOptions = ref([])
const queryParams = ref({
  pageNum: 1, pageSize: 8, applicantName: '', companyName: '',
  contactPhone: '', type: null, status: null, handlerName: ''
})

const detailVisible = ref(false)
const assignVisible = ref(false)
const recordVisible = ref(false)
const currentInquiry = ref({})
const assignForm = ref({id: null, handlerId: null})
const recordForm = ref({id: null, result: '', status: 1})

// 接口方法
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
  queryParams.value = {
    pageNum: 1,
    pageSize: 8,
    applicantName: '',
    companyName: '',
    contactPhone: '',
    type: null,
    status: null,
    handlerName: ''
  }
  handleQuery()
}
const handleView = (row) => {
  currentInquiry.value = {...row};
  detailVisible.value = true
}
const openAssign = async (row) => {
  assignForm.value.id = row.id;
  assignForm.value.handlerId = row.handlerId;
  assignVisible.value = true
  if (userOptions.value.length === 0) {
    try {
      const res = await noticeApi.getActiveUsers();
      userOptions.value = res.data
    } catch (e) {
      ElMessage.error('人员列表加载失败')
    }
  }
}
const submitAssign = async () => {
  if (!assignForm.value.handlerId) return ElMessage.warning('请选择负责人')
  btnLoading.value = true
  try {
    await inquiryApi.assignHandler(assignForm.value.id, assignForm.value.handlerId);
    ElMessage.success('指派成功');
    assignVisible.value = false;
    getList()
  } finally {
    btnLoading.value = false
  }
}
const openRecord = (row) => {
  recordForm.value.id = row.id;
  recordForm.value.result = row.handleResult || '';
  recordForm.value.status = row.status === 0 ? 1 : row.status;
  recordVisible.value = true
}
const submitRecord = async () => {
  if (!recordForm.value.result) return ElMessage.warning('内容不能为空')
  btnLoading.value = true
  try {
    await inquiryApi.recordFollowUp(recordForm.value);
    ElMessage.success('记录成功');
    recordVisible.value = false;
    getList()
  } finally {
    btnLoading.value = false
  }
}
const handleConvert = (row) => {
  ElMessageBox.confirm(`确认将“${row.companyName}”转化为入驻企业？系统将自动创建管理账号。`, '操作提示', {
    confirmButtonText: '立即转化', cancelButtonText: '取消', type: 'success'
  }).then(async () => {
    try {
      await inquiryApi.convertToEnterprise(row.id);
      ElMessage.success('转化成功！');
      getList()
    } catch (e) {
      ElMessage.error('操作失败')
    }
  }).catch(() => {
  })
}

onMounted(() => getList())
</script>

<style scoped>
.inquiry-container {
  padding: 20px;
  background-color: #f6f8fa;
  min-height: calc(100vh - 84px);
}

/* 搜索栏：轻盈质感 */
.search-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px 24px 8px 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px -5px rgba(0, 0, 0, 0.05);
  border: 1px solid #ebeef5;
}

.search-btns {
  float: right;
}

/* 卡片容器布局 */
.card-list {
  margin-top: 10px;
}

.card-col {
  margin-bottom: 24px;
}

/* 核心卡片样式：大厂简约风 */
.inquiry-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  background: #fff;
  position: relative;
}

.inquiry-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
}

/* 卡片左侧状态条 */
.status-border-0 {
  border-left: 5px solid #f56c6c;
}

/* 待处理-红 */
.status-border-1 {
  border-left: 5px solid #409eff;
}

/* 跟进中-蓝 */
.status-border-2 {
  border-left: 5px solid #67c23a;
}

/* 已入驻-绿 */
.status-border-3 {
  border-left: 5px solid #909399;
}

/* 已完结-灰 */

.card-inner {
  padding: 18px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.type-tag {
  border-color: #d9ecff;
  color: #409eff;
  background-color: #f0f7ff;
}

.main-info {
  margin-bottom: 16px;
}

.applicant-name {
  margin: 0 0 6px 0;
  font-size: 17px;
  font-weight: 600;
  color: #2c3e50;
}

.company-name {
  font-size: 13px;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  gap: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 信息网格 */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 15px;
}

.grid-label {
  display: block;
  font-size: 11px;
  color: #94a3b8;
  text-transform: uppercase;
  margin-bottom: 4px;
}

.grid-value {
  font-size: 13px;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.grid-value .el-icon {
  color: #64748b;
}

.unassigned {
  color: #cbd5e1;
  font-style: italic;
}

.divider {
  height: 1px;
  background: radial-gradient(circle, #f0f0f0 0%, #ffffff 100%);
  margin: 12px 0;
}

/* 留言摘抄 */
.remark-summary {
  background: #f8fafc;
  padding: 10px 12px;
  border-radius: 8px;
  margin-bottom: 15px;
  display: flex;
  gap: 8px;
}

.remark-summary .el-icon {
  margin-top: 3px;
  color: #94a3b8;
}

.summary-text {
  margin: 0;
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  height: 36px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部区域 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-info {
  font-size: 12px;
  color: #94a3b8;
}

.actions {
  display: flex;
  gap: 8px;
}

.convert-item {
  color: #67c23a !important;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}


:deep(.round-dialog){
  border-radius: 12px;
  overflow: hidden;
}

/* 详情弹窗美化 */
:deep(.modern-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  margin: -20px -20px 20px -20px;
  border-bottom: 1px solid #f1f5f9;
}

.status-bg-0 {
  background: #f56c6c;
}

.status-bg-1 {
  background: #409eff;
}

.status-bg-2 {
  background: #67c23a;
}

.profile-title h3 {
  margin: 0;
  font-size: 20px;
  color: #1e293b;
}


.profile-title p {
  margin: 4px 0 0 0;
  color: #64748b;
}

.modern-descriptions :deep(.el-descriptions__label) {
  width: 120px;
  background: #f8fafc !important;
  color: #475569;
  font-weight: 600;
}

.dialog-remark-box, .dialog-result-box {
  padding: 12px;
  line-height: 1.6;
  border-radius: 8px;
}

.dialog-remark-box {
  background: #f0f7ff;
  color: #1e40af;
}

.dialog-result-box {
  background: #f0fdf4;
  color: #166534;
  border: 1px dashed #bbf7d0;
}

.modern-radio :deep(.el-radio-button__inner) {
  padding: 10px 24px;
}
</style>