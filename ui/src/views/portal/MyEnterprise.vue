<template>
  <div class="my-enterprise-container">
    <header class="page-header">
      <div class="header-content">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>我的企业</el-breadcrumb-item>
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
              <el-button type="success" size="large" icon="Promotion" @click="handleOpenUpdateIntro"
                         class="gradient-btn-success">
                编辑企业简介
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
            <el-button v-if="enterpriseInfo.status === 1" link type="primary" icon="Edit" @click="handleOpenUpdateInfo"
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
            <div class="info-item"><span class="label">注册资本</span><span
                class="value">{{ enterpriseInfo.registeredCapital }} 万</span></div>
            <div class="info-item"><span class="label">所属行业</span><span class="value">{{
                enterpriseInfo.industry
              }}</span></div>
          </div>

          <el-descriptions :column="2" border class="custom-descriptions">
            <el-descriptions-item label="业务联系人">{{ enterpriseInfo.contactPerson || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ enterpriseInfo.contactPhone || '-' }}</el-descriptions-item>
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
            <el-button v-if="enterpriseInfo.status === 1" link type="primary" icon="Edit" @click="handleOpenUpdateIntro"
                       class="edit-link">
              编辑简介
            </el-button>
          </div>
          <div class="decoration-container">
            <div class="rich-content-view" v-if="enterpriseInfo.introduction"
                 v-html="enterpriseInfo.introduction"></div>
            <el-empty v-else description="暂无简介，点击上方按钮进行编辑"/>
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
    </main>

    <el-dialog v-model="infoDialogVisible" :title="isEdit ? '修改企业资料' : '填报入驻申请'" width="800px"
               destroy-on-close>
      <el-form ref="infoFormRef" :model="infoForm" :rules="rules" label-position="top">
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
              <el-input v-model="infoForm.industry"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="infoForm.contactPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="infoForm.contactPhone"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="营业执照" prop="licenseUrl">
          <el-upload class="license-uploader-pro" action="/api/common/upload" :show-file-list="false"
                     :on-success="res => infoForm.licenseUrl = res.data.url">
            <img v-if="infoForm.licenseUrl" :src="infoForm.licenseUrl" class="license-img-pro"/>
            <div v-else class="uploader-placeholder">
              <el-icon>
                <Plus/>
              </el-icon>
              <span>点击上传</span>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="infoDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitInfoForm" class="gradient-btn">
          {{ isEdit ? '确认修改' : '确认提交申请' }}
        </el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="introDrawerVisible" title="编辑企业详细简介" size="850px" destroy-on-close>
      <div class="drawer-content">
        <el-form :model="introForm" label-position="top">
          <div class="deco-section">
            <h3 class="deco-title">企业风采介绍 (支持图文视频)</h3>
            <el-form-item label-width="0">
              <WangEditor v-model="introForm.introduction" height="600px"/>
            </el-form-item>
          </div>
          <div class="drawer-footer">
            <el-button type="primary" size="large" :loading="submitting" @click="submitIntroUpdate"
                       class="full-btn gradient-btn-success">
              保存并发布简介
            </el-button>
          </div>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import {
  InfoFilled,
  Plus,
  Monitor,
  Refresh,
  Edit,
  OfficeBuilding,
  Timer,
  Loading,
  Promotion, User
} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import WangEditor from '@/components/WangEditor/index.vue'
import {ElMessage, ElMessageBox} from 'element-plus'

// 状态控制
const loading = ref(true)
const submitting = ref(false)
const infoDialogVisible = ref(false)
const introDrawerVisible = ref(false)
const isEdit = ref(false)
const infoFormRef = ref(null)

// 数据模型
const enterpriseInfo = ref({id: null, status: null, introduction: ''})
const auditHistory = ref([])

// 表单数据
const infoForm = ref({
  id: null,
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  industry: '',
  contactPerson: '',
  contactPhone: '',
  licenseUrl: ''
})
const introForm = ref({introduction: ''})

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
  licenseUrl: [{required: true, message: '请上传营业执照', trigger: 'change'}]
}

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

// 打开入驻申请弹窗
const handleOpenApply = () => {
  isEdit.value = false
  Object.assign(infoForm.value, enterpriseInfo.value)
  if (enterpriseInfo.value.status === 2 || enterpriseInfo.value.status === 3) {
    ElMessageBox.confirm('重新提交将进入新一轮人工审核流程，确认继续？', '重申提示', {type: 'warning'}).then(() => {
      infoDialogVisible.value = true
    })
  } else {
    infoDialogVisible.value = true
  }
}

// 打开“修改企业资料”弹窗 (所有字段)
const handleOpenUpdateInfo = () => {
  isEdit.value = true
  infoForm.value = {
    id: enterpriseInfo.value.id,
    companyName: enterpriseInfo.value.companyName,
    creditCode: enterpriseInfo.value.creditCode,
    legalPerson: enterpriseInfo.value.legalPerson,
    registeredCapital: enterpriseInfo.value.registeredCapital,
    industry: enterpriseInfo.value.industry,
    contactPerson: enterpriseInfo.value.contactPerson,
    contactPhone: enterpriseInfo.value.contactPhone,
    licenseUrl: enterpriseInfo.value.licenseUrl
  }
  infoDialogVisible.value = true
}

// 提交企业资料表单 (包含入驻申请和常规修改)
const submitInfoForm = async () => {
  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submitting.value = true
      if (isEdit.value) {
        // 调用修改接口
        await enterpriseApi.updateMyEnterprise(infoForm.value)
        ElMessage.success('企业资料已更新')
      } else {
        // 调用入驻申请接口
        await enterpriseApi.apply(infoForm.value)
        ElMessage.success('申请提交成功，请等待审核')
      }
      infoDialogVisible.value = false
      await initData()
    } finally {
      submitting.value = false
    }
  })
}

// 打开“编辑企业简介”抽屉
const handleOpenUpdateIntro = () => {
  introForm.value = {
    id: enterpriseInfo.value.id,
    introduction: enterpriseInfo.value.introduction
  }
  introDrawerVisible.value = true
}

// 提交简介修改
const submitIntroUpdate = async () => {
  try {
    submitting.value = true
    await enterpriseApi.updateMyEnterprise(introForm.value)
    ElMessage.success('简介更新成功')
    introDrawerVisible.value = false
    await initData()
  } finally {
    submitting.value = false
  }
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
      ElMessage.success('迁出申请已送达管理员')
      await initData()
    } finally {
      loading.value = false
    }
  }).catch(() => {
  })
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
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

.deco-section {
  margin-bottom: 30px;
}

.deco-title {
  font-size: 17px;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #3b82f6;
}

.drawer-content {
  padding: 0 30px 40px;
}

.drawer-footer {
  margin-top: 20px;
}

.full-btn {
  width: 100%;
}

.reject-alert-box {
  margin-top: 20px;
  border-radius: 10px;
}

.opinion-text {
  color: #ef4444;
  font-size: 13px;
}

.auditor-tag {
  font-size: 13px;
  display: flex;
  color: #0a65c5;
  padding: 5px 0;
  align-items: center;
  gap: 8px;
  margin-left: 0;
}
</style>