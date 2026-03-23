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
            <h1>{{ enterpriseInfo.companyName || '企业档案' }}</h1>
            <el-tag :type="statusMap[enterpriseInfo.status]?.tagType" effect="dark" round>
              {{ statusMap[enterpriseInfo.status]?.text || '未申请' }}
            </el-tag>
          </div>

          <div class="title-right">
            <el-button v-if="enterpriseInfo.status === null" type="primary" icon="Plus" @click="handleOpenApply">
              立即申请入驻
            </el-button>
            <el-button v-else-if="enterpriseInfo.status === 2 || enterpriseInfo.status === 3" type="warning"
                       icon="Refresh" @click="handleOpenApply">
              重新提交入驻申请
            </el-button>

            <el-button v-else-if="enterpriseInfo.status === 1" type="success" icon="Promotion"
                       @click="handleOpenDecoration">
              装修企业风采
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content" v-loading="loading">
      <template v-if="enterpriseInfo.id">
        <section class="status-steps card-style" v-if="enterpriseInfo.status === 0 || enterpriseInfo.status === 2">
          <el-steps :active="stepActive" finish-status="success" align-center>
            <el-step title="提交申请" :description="enterpriseInfo.createTime"></el-step>
            <el-step
                title="平台审核"
                :status="enterpriseInfo.status === 2 ? 'error' : ''"
                :description="enterpriseInfo.status === 2 ? '审核未通过' : '人工核验中'"
            ></el-step>
            <el-step title="完成入驻" description="正式入驻园区"></el-step>
          </el-steps>

          <el-alert
              v-if="enterpriseInfo.status === 2"
              title="审核未通过反馈"
              type="error"
              :closable="false"
              show-icon
              class="reject-alert-box"
          >
            <template #default>
              <p class="reject-reason">驳回理由：{{ enterpriseInfo.auditOpinion || '资料需要完善，请核对后重新提交' }}</p>
              <p class="reject-tip">提示：您可以点击右上角的“重新提交入驻申请”按钮来修正信息。</p>
            </template>
          </el-alert>
        </section>

        <section class="intro-display-section card-style">
          <div class="section-title">
            <el-icon>
              <Monitor/>
            </el-icon>
            <span>企业风采展示</span>

            <el-button
                v-if="enterpriseInfo.id"
                link
                type="primary"
                icon="Edit"
                @click="handleOpenDecoration"
                style="margin-left: auto"
            >
              编辑简介
            </el-button>
          </div>

          <div class="rich-content-view" v-if="enterpriseInfo.introduction" v-html="enterpriseInfo.introduction"></div>
          <el-empty v-else description="暂无图文简介，点击上方按钮装修企业风采">
            <el-button v-if="enterpriseInfo.status === 1" type="primary" plain @click="handleOpenDecoration">
              立即去装修
            </el-button>
          </el-empty>
        </section>

        <section class="info-grid card-style">
          <div class="section-title">
            <el-icon>
              <InfoFilled/>
            </el-icon>
            <span>基础档案</span>
          </div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="信用代码">{{ enterpriseInfo.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="法人代表">{{ enterpriseInfo.legalPerson }}</el-descriptions-item>
            <el-descriptions-item label="注册资本">{{ enterpriseInfo.registeredCapital }} 万元</el-descriptions-item>
            <el-descriptions-item label="所属行业">{{ enterpriseInfo.industry }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ enterpriseInfo.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ enterpriseInfo.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="营业执照" :span="2">
              <el-image v-if="enterpriseInfo.licenseUrl" style="width: 120px; border-radius: 4px;"
                        :src="enterpriseInfo.licenseUrl" :preview-src-list="[enterpriseInfo.licenseUrl]" fit="contain"
                        preview-teleported/>
            </el-descriptions-item>
          </el-descriptions>
        </section>

        <section v-if="auditHistory.length > 0" class="audit-timeline card-style">
          <div class="section-title">流转记录</div>
          <el-timeline>
            <el-timeline-item v-for="(log, index) in auditHistory" :key="index"
                              :type="log.status === 1 ? 'success' : (log.status === 2 ? 'danger' : 'primary')"
                              :timestamp="log.createTime">
              <h4>{{ log.auditAction }}</h4>
              <p v-if="log.opinion" class="opinion-text">反馈：{{ log.opinion }}</p>
            </el-timeline-item>
          </el-timeline>
        </section>
      </template>

      <el-empty v-else description="您尚未提交入驻申请">
        <el-button type="primary" size="large" @click="handleOpenApply">立即申请入驻</el-button>
      </el-empty>
    </main>

    <el-dialog v-model="applyDialogVisible" title="填报入驻申请" width="700px" destroy-on-close>
      <el-form ref="applyFormRef" :model="applyForm" :rules="rules" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业全称" prop="companyName">
              <el-input v-model="applyForm.companyName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用代码" prop="creditCode">
              <el-input v-model="applyForm.creditCode"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="法人代表" prop="legalPerson">
              <el-input v-model="applyForm.legalPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="注册资本" prop="registeredCapital">
              <el-input-number v-model="applyForm.registeredCapital" style="width:100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属行业" prop="industry">
              <el-input v-model="applyForm.industry"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="营业执照" prop="licenseUrl">
          <el-upload class="license-uploader" action="/api/common/upload" :show-file-list="false"
                     :on-success="res => applyForm.licenseUrl = res.data.url">
            <img v-if="applyForm.licenseUrl" :src="applyForm.licenseUrl" class="license-img" alt="营业执照"/>
            <el-icon v-else class="uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApply">提交审核</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="decoDrawerVisible" title="装修企业风采" size="850px" destroy-on-close>
      <el-form :model="decoForm" label-position="top" style="padding: 20px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="业务联系人">
              <el-input v-model="decoForm.contactPerson"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="decoForm.contactPhone"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="企业风采简介 (支持图文视频)">
          <WangEditor v-model="decoForm.introduction" height="500px"/>
        </el-form-item>
        <div style="margin-top: 30px">
          <el-button type="primary" size="large" :loading="submitting" @click="submitDecoration" style="width: 100%">
            保存并更新门户
          </el-button>
        </div>
      </el-form>
    </el-drawer>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import {InfoFilled, Plus, Monitor, Refresh, Promotion, Edit} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import WangEditor from '@/components/WangEditor/index.vue'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(true)
const submitting = ref(false)
const applyDialogVisible = ref(false)
const decoDrawerVisible = ref(false)
const applyFormRef = ref(null)

const enterpriseInfo = ref({id: null, status: null, introduction: ''})

const applyForm = ref({
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  industry: '',
  licenseUrl: ''
})
const decoForm = ref({contactPerson: '', contactPhone: '', introduction: ''})

const statusMap = {
  0: {text: '资料审核中', tagType: 'warning'},
  1: {text: '正式入驻', tagType: 'success'},
  2: {text: '审核驳回', tagType: 'danger'},
  3: {text: '已迁出', tagType: 'info'}
}

const stepActive = computed(() => enterpriseInfo.value.status === 0 ? 1 : (enterpriseInfo.value.status === 1 ? 3 : 1))

const rules = {
  companyName: [{required: true, message: '请输入企业名称', trigger: 'blur'}],
  creditCode: [{required: true, pattern: /^[A-Z0-9]{18}$/, message: '18位信用代码格式错误', trigger: 'blur'}],
  licenseUrl: [{required: true, message: '请上传营业执照', trigger: 'change'}]
}

const auditHistory = ref([])

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

const handleOpenApply = () => {
  Object.assign(applyForm.value, enterpriseInfo.value)
  if (enterpriseInfo.value.status === 2 || enterpriseInfo.value.status === 3) {
    ElMessageBox.confirm('重新提交将进入新审核流程', '提示', {type: 'warning'}).then(() => {
      applyDialogVisible.value = true
    })
  } else {
    applyDialogVisible.value = true
  }
}

const submitApply = async () => {
  await applyFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submitting.value = true
      await enterpriseApi.apply(applyForm.value)
      ElMessage.success('入驻申请已提交，请等待审核')
      applyDialogVisible.value = false
      await initData()
    } finally {
      submitting.value = false
    }
  })
}

const handleOpenDecoration = () => {
  decoForm.value = {
    contactPerson: enterpriseInfo.value.contactPerson,
    contactPhone: enterpriseInfo.value.contactPhone,
    introduction: enterpriseInfo.value.introduction
  }
  decoDrawerVisible.value = true
}

const submitDecoration = async () => {
  try {
    submitting.value = true
    await enterpriseApi.updateMyEnterprise(decoForm.value)
    ElMessage.success('风采维护成功，已即时更新')
    decoDrawerVisible.value = false
    await initData()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.my-enterprise-container {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 50px;
}

.page-header {
  background: #fff;
  padding: 24px 10%;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 30px;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 15px;
}

.title-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main-content {
  max-width: 1100px;
  margin: 0 auto;
}

.card-style {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;
}

/* 驳回反馈框美化 */
.reject-alert-box {
  margin-top: 20px;
  border: 1px solid #fde2e2;
  border-radius: 8px;
}

.reject-reason {
  font-weight: bold;
  margin: 5px 0;
  font-size: 15px;
}

.reject-tip {
  font-size: 13px;
  margin: 0;
  opacity: 0.8;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 20px;
  border-left: 4px solid #409eff;
  padding-left: 12px;
}

.rich-content-view {
  line-height: 1.8;
  color: #444;
}

.rich-content-view :deep(img) {
  display: block;
  margin: 16px auto;
  max-width: 100%;
  border-radius: 8px;
}

.rich-content-view :deep(video),
.rich-content-view :deep([data-w-e-type="video"]) {
  display: block;
  margin: 0 auto;
  padding: 5px;
  max-width: 800px;
  width: 100% !important;
  height: auto !important;
  background: rgba(23, 23, 23, 0.94);
  border: 1px solid rgba(48, 48, 48, 0.89);
  border-radius: 5px;
}

.license-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  width: 140px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.license-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.opinion-text {
  color: #f56c6c;
  font-size: 13px;
  background: #fff5f5;
  padding: 8px;
  border-radius: 4px;
}
</style>