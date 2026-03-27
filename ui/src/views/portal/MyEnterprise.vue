<template>
  <div class="my-enterprise-container">
    <header class="page-header">
      <div class="header-content">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>企业服务中心</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="title-row">
          <div class="title-left">
            <div class="company-icon">
              <el-icon>
                <OfficeBuilding/>
              </el-icon>
            </div>
            <div class="name-box">
              <h1>{{ enterpriseInfo.companyName || '企业档案' }}</h1>
              <div class="status-badge">
                <el-tag :type="statusMap[enterpriseInfo.status]?.tagType" effect="dark" round size="small">
                  {{ statusMap[enterpriseInfo.status]?.text || '未申请' }}
                </el-tag>
              </div>
            </div>
          </div>

          <div class="title-right">
            <el-button v-if="enterpriseInfo.status === null" type="primary" size="large" icon="Plus"
                       @click="handleOpenApply" class="gradient-btn">
              立即申请入驻
            </el-button>

            <el-button v-else-if="enterpriseInfo.status === 2 || enterpriseInfo.status === 3" type="warning"
                       size="large"
                       icon="Refresh" @click="handleOpenApply">
              重新提交申请
            </el-button>

            <template v-else-if="enterpriseInfo.status === 1">
              <el-button type="primary" size="large" icon="Edit" @click="handleOpenUpdateInfo">
                修改企业资料
              </el-button>
              <el-button
                  type="danger"
                  plain
                  size="large"
                  icon="Close"
                  @click="handleApplyMoveOut"
              >
                申请迁出
              </el-button>
            </template>

            <el-button v-else-if="enterpriseInfo.status === 4" type="success" size="large" icon="Loading"
                       class="gradient-btn-success">
              迁出申请审核中……
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content" v-loading="loading">
      <el-tabs v-model="activeTab" class="custom-tabs" @tab-change="handleTabChange">

        <el-tab-pane label="企业档案" name="info">
          <template v-if="enterpriseInfo.id">
            <section class="status-steps card-style"
                     v-if="enterpriseInfo.status === 0 || enterpriseInfo.status === 2 || enterpriseInfo.status === 4">
              <div class="card-header-simple">业务办理状态</div>
              <el-steps :active="stepActive" finish-status="success" align-center>
                <el-step title="提交申请" :description="enterpriseInfo.createTime"></el-step>
                <el-step
                    title="审核中"
                    :status="enterpriseInfo.status === 2 ? 'error' : (enterpriseInfo.status === 4 ? 'process' : '')"
                    :description="enterpriseInfo.status === 2 ? '审核未通过' : '人工核验中'"
                ></el-step>
                <el-step title="办理完成" description="正式入驻/已迁出"></el-step>
              </el-steps>

              <el-alert v-if="enterpriseInfo.status === 2" title="驳回反馈" type="error" :closable="false" show-icon
                        class="reject-alert-box">
                <template #default>
                  <p class="reject-reason">原因：{{ enterpriseInfo.auditOpinion || '资料不齐' }}</p>
                </template>
              </el-alert>
            </section>

            <section class="info-grid card-style">
              <div class="section-title">
                <el-icon>
                  <InfoFilled/>
                </el-icon>
                <span>企业基础信息</span>
                <el-button v-if="enterpriseInfo.status === 1" link type="primary" icon="Edit"
                           @click="handleOpenUpdateInfo"
                           class="edit-link">
                  修改基本资料
                </el-button>
              </div>
              <div class="info-dashboard">
                <div class="info-item"><span class="label">信用代码</span><span class="value">{{
                    enterpriseInfo.creditCode
                  }}</span></div>
                <div class="info-item"><span class="label">法人代表</span><span class="value">{{
                    enterpriseInfo.legalPerson
                  }}</span></div>
                <div class="info-item"><span class="label">入驻楼宇</span><span class="value">{{
                    enterpriseInfo.buildingNo || '待分配'
                  }}</span></div>
                <div class="info-item"><span class="label">租用面积</span><span class="value">{{
                    enterpriseInfo.rentArea || 0
                  }} ㎡</span></div>
              </div>

              <el-descriptions :column="2" border class="custom-descriptions">
                <el-descriptions-item label="所属行业">{{ enterpriseInfo.industry || '-' }}</el-descriptions-item>
                <el-descriptions-item label="注册资本">{{ enterpriseInfo.registeredCapital }} 万</el-descriptions-item>
                <el-descriptions-item label="业务联系人">{{
                    enterpriseInfo.contactPerson || '-'
                  }}
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ enterpriseInfo.contactPhone || '-' }}</el-descriptions-item>
                <el-descriptions-item label="计划租约" :span="2">
                  {{ enterpriseInfo.leaseStartDate || '-' }} 至 {{ enterpriseInfo.leaseEndDate || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="营业执照预览" :span="2">
                  <div class="license-preview-box">
                    <el-image v-if="enterpriseInfo.licenseUrl" :src="enterpriseInfo.licenseUrl"
                              :preview-src-list="[enterpriseInfo.licenseUrl]" fit="contain" preview-teleported/>
                    <el-empty v-else :image-size="40" description="未上传"/>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </section>

            <section class="intro-display-section card-style">
              <div class="section-title">
                <el-icon>
                  <Monitor/>
                </el-icon>
                <span>企业简介</span>
              </div>
              <div class="decoration-container">
                <div class="rich-content-view" v-if="enterpriseInfo.introduction"
                     v-html="enterpriseInfo.introduction"></div>
                <el-empty v-else description="暂无简介"/>
              </div>
            </section>

            <section v-if="auditHistory.length > 0" class="audit-timeline card-style">
              <div class="section-title">
                <el-icon>
                  <Timer/>
                </el-icon>
                <span>流转审批记录</span></div>
              <el-timeline>
                <el-timeline-item v-for="(log, index) in auditHistory" :key="index"
                                  :type="log.status === 1 ? 'success' : (log.status === 2 ? 'danger' : 'primary')"
                                  :timestamp="log.createTime">
                  <el-card shadow="never" class="log-card">
                    <h4>{{ log.auditAction }}</h4>
                    <p v-if="log.opinion" class="opinion-text">反馈：{{ log.opinion }}</p>
                    <span class="auditor-tag">
                          <el-icon><User/></el-icon> {{ log.auditorName || '申请人' }}
                    </span>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </section>
          </template>

          <el-empty v-else :image-size="200" description="您尚未加入园区企业档案库">
            <el-button type="primary" size="large" class="gradient-btn" icon="Plus" @click="handleOpenApply">
              提交入驻申请
            </el-button>
          </el-empty>
        </el-tab-pane>

        <el-tab-pane label="诉求工单" name="workorder">
          <div class="work-order-section">
            <div class="work-order-actions card-style shadow-sm">
              <el-button type="primary" size="large" icon="Plus" @click="handleOpenWorkOrder" class="gradient-btn">
                发起服务诉求
              </el-button>
              <div class="filter-box">
                <el-radio-group v-model="workOrderQuery.status" @change="loadWorkOrders" size="default">
                  <el-radio-button :label="null">全部</el-radio-button>
                  <el-radio-button :label="0">待受理</el-radio-button>
                  <el-radio-button :label="1">处理中</el-radio-button>
                  <el-radio-button :label="2">已办结</el-radio-button>
                  <el-radio-button :label="3">已评价</el-radio-button>
                </el-radio-group>
              </div>
            </div>

            <div class="order-list">
              <el-empty v-if="workOrders.length === 0" description="暂无相关诉求记录"/>
              <el-card v-for="item in workOrders" :key="item.id" class="order-item-card card-style" shadow="hover">
                <div class="order-card-header">
                  <div class="order-no">单号：{{ item.id }}</div>
                  <el-tag :type="orderStatusMap[item.status]?.type">{{ orderStatusMap[item.status]?.text }}</el-tag>
                </div>
                <div class="order-body">
                  <div class="order-main-info">
                    <h3 class="order-title">
                      <el-tag size="small" effect="plain" class="type-tag">{{ orderTypeMap[item.type] }}</el-tag>
                      {{ item.title }}
                    </h3>
                    <p class="order-desc">{{ item.content }}</p>

                    <div class="order-images" v-if="item.images">
                      <el-image
                          v-for="(img, idx) in item.images.split(',')"
                          :key="idx"
                          :src="img"
                          :preview-src-list="item.images.split(',')"
                          :initial-index="idx"
                          preview-teleported
                          class="order-img"
                      />
                    </div>
                  </div>
                  <div class="order-meta">
                    <div class="meta-item">
                      <el-icon>
                        <Timer/>
                      </el-icon>
                      提交时间：{{ item.createTime }}
                    </div>
                    <div class="meta-item" v-if="item.handlerName">
                      <el-icon>
                        <User/>
                      </el-icon>
                      受理人：{{ item.handlerName }}
                    </div>
                  </div>
                </div>

                <div class="order-feedback" v-if="item.status >= 2">
                  <div class="feedback-title">处理反馈：</div>
                  <div class="feedback-content">{{ item.remark || '已妥善处理您的诉求。' }}</div>
                </div>

                <div class="order-footer" v-if="item.status === 2">
                  <el-button type="primary" plain icon="ChatDotRound" @click="handleOpenEvaluate(item)">评价服务
                  </el-button>
                </div>
                <div class="order-footer" v-if="item.status === 3">
                  <div class="evaluated-box">
                    <span>我的评价：</span>
                    <el-rate v-model="item.score" disabled show-score text-color="#ff9900"/>
                  </div>
                </div>
              </el-card>

              <div class="pagination-container" v-if="total > 0">
                <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="total"
                    :page-size="workOrderQuery.pageSize"
                    v-model:current-page="workOrderQuery.pageNum"
                    @current-change="loadWorkOrders"
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </main>

    <el-dialog v-model="infoDialogVisible" :title="isEdit ? '修改企业资料' : '填报入驻申请'" width="900px"
               top="5vh" destroy-on-close>
      <el-form ref="infoFormRef" :model="infoForm" :rules="rules" label-position="top">
        <el-divider content-position="left">基础工商信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="infoForm.companyName" placeholder="请填写企业全称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="社会信用代码" prop="creditCode">
              <el-input v-model="infoForm.creditCode" placeholder="18位统一社会信用代码"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="法人代表" prop="legalPerson">
              <el-input v-model="infoForm.legalPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="注册资本(万)" prop="registeredCapital">
              <el-input-number v-model="infoForm.registeredCapital" :precision="2" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属行业" prop="industry">
              <el-input v-model="infoForm.industry" placeholder="例如：软件开发"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">入驻意向信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="意向楼宇/编号" prop="buildingNo">
              <el-input v-model="infoForm.buildingNo" placeholder="如：A座 302"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请租用面积 (㎡)" prop="rentArea">
              <el-input-number v-model="infoForm.rentArea" :precision="2" :min="0" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计划租期" prop="leaseRange">
              <el-date-picker
                  v-model="infoForm.leaseRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业务联系人" prop="contactPerson">
              <el-input v-model="infoForm.contactPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="infoForm.contactPhone"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="企业介绍 (支持图文展示)" prop="introduction">
          <div style="border: 1px solid #dcdfe6; border-radius: 4px; overflow: hidden">
            <WangEditor v-model="infoForm.introduction" height="300px"/>
          </div>
        </el-form-item>

        <el-form-item label="营业执照" prop="licenseUrl">
          <el-upload class="license-uploader-pro" action="#"
                     :show-file-list="false"
                     :http-request="handleImageUpload"
                     :before-upload="beforeLicenseUpload"
                     accept=".jpg,.jpeg,.png,.gif">
            <div v-if="infoForm.licenseUrl" class="image-preview-container">
              <img :src="infoForm.licenseUrl" class="license-img-pro" alt="营业执照附件"/>
              <div class="image-actions">
                    <span class="action-item" @click.stop="handlePreview">
                      <el-icon><ZoomIn/></el-icon>
                      <span>预览</span>
                    </span>
                <span class="action-item" @click.stop="handleRemove">
                      <el-icon><Delete/></el-icon>
                      <span>删除</span>
                    </span>
              </div>
            </div>
            <div v-else class="uploader-placeholder">
              <el-icon class="license-icon">
                <Plus/>
              </el-icon>
              <span class="license-text">上传营业执照</span>
            </div>
          </el-upload>
          <div class="upload-tip">请上传清晰的营业执照扫描件 (支持图片格式，小于 5MB)</div>
        </el-form-item>
        <el-image-viewer
            v-if="showViewer"
            :url-list="[infoForm.licenseUrl]"
            @close="showViewer = false"
        />
      </el-form>
      <template #footer>
        <el-button @click="infoDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitInfoForm" class="gradient-btn">
          {{ isEdit ? '确认保存' : '提交入驻申请' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="workOrderVisible" title="发起服务诉求" width="650px" destroy-on-close>
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="诉求类型" prop="type">
              <el-select v-model="orderForm.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="维修服务" :value="1"/>
                <el-option label="政策咨询" :value="2"/>
                <el-option label="投诉建议" :value="3"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="诉求标题" prop="title">
              <el-input v-model="orderForm.title" placeholder="简述您的诉求"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细内容描述" prop="content">
          <el-input v-model="orderForm.content" type="textarea" :rows="4"
                    placeholder="请详细描述您遇到的问题或需求..."/>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="orderForm.contactPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="orderForm.contactPhone"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="现场照片/补充附件 (可选)">
          <el-upload
              action="#"
              list-type="picture-card"
              :http-request="handleOrderImageUpload"
              :on-remove="handleOrderImageRemove"
              accept=".jpg,.jpeg,.png"
          >
            <el-icon>
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="workOrderVisible = false">取消</el-button>
        <el-button type="primary" :loading="woSubmitting" @click="submitWorkOrder" class="gradient-btn">提交诉求
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="evaluateVisible" title="服务评价" width="450px" destroy-on-close>
      <div class="evaluate-body">
        <p style="margin-bottom: 15px; color: #64748b;">请对本次工单处理结果进行打分：</p>
        <el-rate v-model="evaluateForm.score" :texts="['极差', '失望', '一般', '满意', '非常惊喜']" show-text/>
        <el-input
            v-model="evaluateForm.commentText"
            type="textarea"
            :rows="3"
            placeholder="写下您的评价内容（选填）..."
            style="margin-top: 25px"
        />
      </div>
      <template #footer>
        <el-button @click="evaluateVisible = false">取消</el-button>
        <el-button type="primary" :loading="evalSubmitting" @click="submitEvaluate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {
  InfoFilled,
  Plus,
  Monitor,
  Refresh,
  Edit,
  OfficeBuilding,
  Timer,
  Loading,
  Promotion,
  User,
  Delete,
  ZoomIn,
  ChatDotRound
} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import workOrderApi from '@/api/workOrder'
import WangEditor from '@/components/WangEditor/index.vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {uploadFile} from "@/utils/upload.js"

// ================== 原有：企业资料状态控制 ==================
const activeTab = ref('info') // 选项卡控制
const loading = ref(true)
const submitting = ref(false)
const infoDialogVisible = ref(false)
const isEdit = ref(false)
const infoFormRef = ref(null)
const showViewer = ref(false)

// 原有：企业数据模型
const enterpriseInfo = ref({id: null, status: null, introduction: ''})
const auditHistory = ref([])
const infoForm = ref({
  id: null,
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  industry: '',
  buildingNo: '',
  rentArea: 0,
  leaseRange: [],
  leaseStartDate: '',
  leaseEndDate: '',
  contactPerson: '',
  contactPhone: '',
  licenseUrl: '',
  introduction: ''
})

const statusMap = {
  0: {text: '资料审核中', tagType: 'warning'},
  1: {text: '正式入驻', tagType: 'success'},
  2: {text: '审核驳回', tagType: 'danger'},
  3: {text: '已迁出', tagType: 'info'},
  4: {text: '迁出申请中', tagType: 'warning'}
}

const stepActive = computed(() => {
  const status = enterpriseInfo.value.status;
  if (status === 0) return 1;
  if (status === 1 || status === 4) return 2;
  if (status === 3) return 3;
  return 0;
});

const rules = {
  companyName: [{required: true, message: '请输入企业全称', trigger: 'blur'}],
  creditCode: [{required: true, pattern: /^[A-Z0-9]{18}$/, message: '请输入18位大写信用代码', trigger: 'blur'}],
  legalPerson: [{required: true, message: '请输入法人代表', trigger: 'blur'}],
  industry: [{required: true, message: '请输入所属行业', trigger: 'blur'}],
  buildingNo: [{required: true, message: '请输入意向楼宇或编号', trigger: 'blur'}],
  rentArea: [{required: true, message: '请输入租用面积', trigger: 'blur'}],
  leaseRange: [{required: true, message: '请选择计划租期', trigger: 'change'}],
  contactPerson: [{required: true, message: '请输入联系人', trigger: 'blur'}],
  contactPhone: [{required: true, pattern: /^1[0-9]\d{9}$/, message: '请输入11位手机号', trigger: 'blur'}],
  licenseUrl: [{required: true, message: '请上传营业执照', trigger: 'change'}],
  introduction: [{required: true, message: '请编写企业简介（支持图文）', trigger: 'blur'}]
}

// ================== 新增：工单相关状态与模型 ==================
const workOrderVisible = ref(false)
const evaluateVisible = ref(false)
const woSubmitting = ref(false)
const evalSubmitting = ref(false)

const workOrders = ref([])
const total = ref(0)
const workOrderQuery = reactive({
  pageNum: 1,
  pageSize: 5,
  status: null,
  enterpriseId: null
})

const orderFormRef = ref(null)
const orderForm = ref({
  type: 1,
  title: '',
  content: '',
  contactPerson: '',
  contactPhone: '',
  images: ''
})
const orderImages = ref([]) // 用于暂存已上传的图片URL

const evaluateForm = reactive({
  id: null,
  score: 5,
  commentText: ''
})

const orderRules = {
  type: [{required: true, message: '请选择诉求类型', trigger: 'change'}],
  title: [{required: true, message: '请输入诉求标题', trigger: 'blur'}],
  content: [{required: true, message: '请输入详细内容描述', trigger: 'blur'}],
  contactPerson: [{required: true, message: '请输入联系人', trigger: 'blur'}],
  contactPhone: [{required: true, pattern: /^1[0-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}]
}

const orderStatusMap = {
  0: {text: '待受理', type: 'info'},
  1: {text: '处理中', type: 'warning'},
  2: {text: '已办结', type: 'success'},
  3: {text: '已评价', type: 'primary'}
}
const orderTypeMap = {1: '维修服务', 2: '政策咨询', 3: '投诉建议'}


// ================== 初始化逻辑 ==================
const initData = async () => {
  loading.value = true
  try {
    const res = await enterpriseApi.getMyEnterprise()
    if (res.data) {
      enterpriseInfo.value = res.data
      const historyRes = await enterpriseApi.getAuditHistory(res.data.id)
      auditHistory.value = historyRes.data
    }
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// ================== 原有：企业业务方法 ==================
const handleOpenApply = () => {
  isEdit.value = false
  resetForm()
  if (enterpriseInfo.value.id) {
    Object.assign(infoForm.value, enterpriseInfo.value)
    if (enterpriseInfo.value.leaseStartDate && enterpriseInfo.value.leaseEndDate) {
      infoForm.value.leaseRange = [enterpriseInfo.value.leaseStartDate, enterpriseInfo.value.leaseEndDate]
    }
  }

  if (enterpriseInfo.value.status === 2 || enterpriseInfo.value.status === 3) {
    ElMessageBox.confirm('重申将进入人工审核，确认继续？', '提示', {type: 'warning'}).then(() => {
      infoDialogVisible.value = true
    })
  } else {
    infoDialogVisible.value = true
  }
}

const handleOpenUpdateInfo = () => {
  isEdit.value = true
  Object.assign(infoForm.value, enterpriseInfo.value)
  if (enterpriseInfo.value.leaseStartDate && enterpriseInfo.value.leaseEndDate) {
    infoForm.value.leaseRange = [enterpriseInfo.value.leaseStartDate, enterpriseInfo.value.leaseEndDate]
  }
  infoDialogVisible.value = true
}

const resetForm = () => {
  infoForm.value = {
    registeredCapital: 0,
    rentArea: 0,
    leaseRange: [],
    introduction: '',
    licenseUrl: ''
  }
}

const beforeLicenseUpload = (rawFile) => {
  const isImg = rawFile.type.startsWith('image/')
  if (!isImg) {
    ElMessage.error('只能上传图片格式文件')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片不能超过 5MB')
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  try {
    const url = await uploadFile(options.file)
    infoForm.value.licenseUrl = url
    infoFormRef.value.validateField('licenseUrl')
    ElMessage.success('执照上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handlePreview = () => showViewer.value = true
const handleRemove = () => {
  infoForm.value.licenseUrl = ''
  ElMessage.success('已移除')
}

const submitInfoForm = async () => {
  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (infoForm.value.leaseRange && infoForm.value.leaseRange.length === 2) {
      infoForm.value.leaseStartDate = infoForm.value.leaseRange[0]
      infoForm.value.leaseEndDate = infoForm.value.leaseRange[1]
    }

    try {
      submitting.value = true
      if (isEdit.value) {
        await enterpriseApi.updateMyEnterprise(infoForm.value)
        ElMessage.success('企业资料更新成功')
      } else {
        await enterpriseApi.apply(infoForm.value)
        ElMessage.success('申请提交成功，请等待管理员审核')
      }
      infoDialogVisible.value = false
      await initData()
    } finally {
      submitting.value = false
    }
  })
}

const handleApplyMoveOut = () => {
  ElMessageBox.prompt('请输入申请迁出的原因', '迁出园区申请', {
    confirmButtonText: '提交申请',
    cancelButtonText: '取消',
    inputPattern: /^.{5,200}$/,
    inputErrorMessage: '原因字数需在 5-200 字之间'
  }).then(async ({value}) => {
    try {
      loading.value = true
      await enterpriseApi.applyMoveOut(value)
      ElMessage.success('迁出申请已送达')
      await initData()
    } finally {
      loading.value = false
    }
  }).catch(() => {
  })
}

// ================== 新增：工单业务方法 ==================

// 切换选项卡
const handleTabChange = (name) => {
  if (name === 'workorder') {
    loadWorkOrders()
  }
}

// 加载工单列表
const loadWorkOrders = async () => {
  // 防御逻辑：如果当前账号还没有关联企业，直接清空列表并返回，不发请求
  if (!enterpriseInfo.value || !enterpriseInfo.value.id) {
    workOrders.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    // 请求前，将当前企业的ID动态赋值给查询参数
    workOrderQuery.enterpriseId = enterpriseInfo.value.id

    const res = await workOrderApi.getMyPage(workOrderQuery)
    workOrders.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    console.error('加载工单失败', err)
  } finally {
    loading.value = false
  }
}

// 工单图片上传
const handleOrderImageUpload = async (options) => {
  try {
    const url = await uploadFile(options.file)
    orderImages.value.push(url)
    orderForm.value.images = orderImages.value.join(',')
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

// 工单图片移除
const handleOrderImageRemove = (file, uploadFiles) => {
  // 简化处理：重置图片数组。实际场景应通过对比 response url 删除特定一项
  orderImages.value = []
  orderForm.value.images = ''
  ElMessage.warning('图片已移除，如需多图请重新整理上传')
}

// 打开提报工单
const handleOpenWorkOrder = () => {
  if (!enterpriseInfo.value.id || enterpriseInfo.value.status !== 1) {
    ElMessage.warning('企业正式入驻后方可提报工单')
    return
  }
  // 初始化表单时，带上 enterpriseId
  orderForm.value = {
    type: 1,
    title: '',
    content: '',
    contactPerson: enterpriseInfo.value.contactPerson || '',
    contactPhone: enterpriseInfo.value.contactPhone || '',
    images: '',
    enterpriseId: enterpriseInfo.value.id // 新增：提报工单时绑定当前企业
  }
  orderImages.value = []
  workOrderVisible.value = true
}

// 提交工单
const submitWorkOrder = async () => {
  await orderFormRef.value.validate(async (valid) => {
    if (!valid) return
    woSubmitting.value = true
    try {
      await workOrderApi.submit(orderForm.value)
      ElMessage.success('诉求提报成功，请耐心等待园区处理')
      workOrderVisible.value = false
      loadWorkOrders()
    } finally {
      woSubmitting.value = false
    }
  })
}

// 打开评价窗口
const handleOpenEvaluate = (row) => {
  evaluateForm.id = row.id
  evaluateForm.score = 5
  evaluateForm.commentText = ''
  evaluateVisible.value = true
}

// 提交评价
const submitEvaluate = async () => {
  evalSubmitting.value = true
  try {
    await workOrderApi.evaluate(evaluateForm)
    ElMessage.success('评价提交成功，感谢您的反馈！')
    evaluateVisible.value = false
    loadWorkOrders()
  } catch (e) {
    console.error(e)
  } finally {
    evalSubmitting.value = false
  }
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
/* ================= 原有样式保持完全不变 ================= */
.my-enterprise-container {
  background-color: #f8fafc;
  min-height: 100vh;
  padding-bottom: 60px;
}

.page-header {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  padding: 40px 10% 60px;
  color: #fff;
  margin-bottom: -40px;
}

.header-content :deep(.el-breadcrumb__inner) {
  color: rgba(255, 255, 255, 0.7) !important;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 25px;
}

.title-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.company-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.name-box h1 {
  margin: 0;
  font-size: 28px;
}

.status-badge {
  margin-top: 8px;
}

.gradient-btn {
  background: linear-gradient(90deg, #4f46e5 0%, #3b82f6 100%) !important;
  border: none !important;
}

.gradient-btn-success {
  background: linear-gradient(90deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.card-style {
  background: #ffffff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

.info-dashboard {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item .label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 6px;
}

.info-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 19px;
  font-weight: 600;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f1f5f9;
}

.edit-link {
  margin-left: auto;
  font-weight: normal;
}

.rich-content-view {
  line-height: 1.8;
  color: #334155;
}

.rich-content-view :deep(img) {
  max-width: 100%;
  border-radius: 12px;
}

.license-preview-box {
  padding: 10px;
  background: #f8fafc;
  border-radius: 8px;
}

.license-preview-box .el-image {
  width: 140px;
  border-radius: 4px;
}

.license-uploader-pro {
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  width: 160px;
  height: 210px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
}

.license-img-pro {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.uploader-placeholder {
  text-align: center;
  color: #94a3b8;
}

.upload-tip {
  font-size: 12px;
  color: #ef4444;
  margin-top: 8px;
}

.image-preview-container {
  width: 100%;
  height: 100%;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 10px;
}

.image-preview-container:hover .image-actions {
  opacity: 1;
}

.action-item {
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
}

.action-item .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.action-item:hover {
  color: #409eff;
}

.reject-alert-box {
  margin-top: 20px;
}

.log-card h4 {
  margin: 0 0 10px;
  color: #1e293b;
}

.opinion-text {
  color: #64748b;
  background: #f1f5f9;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
}

:deep(.el-divider__text) {
  background-color: #fff;
  font-weight: bold;
  color: #4f46e5;
}

/* ================= 新增：工单专属样式 ================= */

.custom-tabs {
  background: transparent;
}

.custom-tabs :deep(.el-tabs__header) {
  margin: 0 0 24px;
  background: #fff;
  padding: 0 30px;
  border-radius: 16px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
  border-bottom: none;
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none; /* 隐藏底部灰线，让其完全像浮动卡片 */
}

.work-order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
}

.order-item-card {
  margin-top: 15px;
  transition: all 0.3s;
}

.order-card-header {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 12px;
  margin-bottom: 15px;
}

.order-no {
  font-size: 13px;
  color: #94a3b8;
}

.order-title {
  margin: 0 0 10px;
  font-size: 17px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1e293b;
}

.order-desc {
  color: #475569;
  line-height: 1.6;
  font-size: 14px;
  background: #f8fafc;
  padding: 12px 15px;
  border-radius: 8px;
  margin-top: 10px;
}

.order-images {
  margin-top: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.order-img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.order-meta {
  margin-top: 15px;
  display: flex;
  gap: 30px;
  font-size: 13px;
  color: #64748b;
}

.order-meta .el-icon {
  vertical-align: middle;
  margin-right: 4px;
}

.order-feedback {
  margin-top: 20px;
  background: #f0fdf4;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #10b981;
}

.feedback-title {
  font-weight: 600;
  color: #065f46;
  margin-bottom: 5px;
}

.feedback-content {
  color: #047857;
  font-size: 14px;
}

.order-footer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e2e8f0;
  text-align: right;
}

.evaluated-box {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  color: #64748b;
  font-size: 14px;
}

.type-tag {
  font-weight: normal;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>