<template>
  <div class="my-enterprise-container">
    <header class="page-header">
      <div class="header-content">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>我的入驻</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="title-row">
          <div class="title-left">
            <h1>企业入驻管理</h1>
            <el-tag :type="statusMap[enterpriseInfo.status]?.tagType" effect="dark" round>
              {{ statusMap[enterpriseInfo.status]?.text || '未申请' }}
            </el-tag>
          </div>

          <div class="title-right" v-if="(enterpriseInfo.status === 2 || enterpriseInfo.status === 3) && !isReApplying">
            <el-button type="warning" plain icon="Refresh" @click="handleReApply">
              再次申请入驻
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content">
      <section v-if="enterpriseInfo.status !== null && (enterpriseInfo.status !== 3 || isReApplying)"
               class="status-steps card-style">
        <el-steps :active="isReApplying ? 0 : stepActive" finish-status="success" align-center>
          <el-step title="提交申请" :description="isReApplying ? '' : enterpriseInfo.createTime"></el-step>
          <el-step
              title="平台审核"
              :status="(enterpriseInfo.status === 2 && !isReApplying) ? 'error' : ''"
              :description="(enterpriseInfo.status === 2 && !isReApplying) ? '审核未通过' : '人工核验中'"
          ></el-step>
          <el-step title="完成入驻" description="正式入驻园区"></el-step>
        </el-steps>
      </section>

      <transition name="el-zoom-in-top">
        <el-alert
            v-if="enterpriseInfo.status === 2 && !isReApplying"
            title="您的入驻申请已被驳回"
            type="error"
            :closable="false"
            show-icon
            class="reject-alert"
        >
          <template #default>
            <p class="reject-reason">驳回理由：<strong>{{ enterpriseInfo.auditOpinion || '资料不全' }}</strong></p>
            <p>请点击右上角“再次申请”修改资料后重新提交。</p>
          </template>
        </el-alert>
      </transition>

      <section v-if="enterpriseInfo.status === 3 && !isReApplying" class="moved-out-section card-style">
        <el-empty description="您的企业已办理迁出，如需重新入驻请点击上方按钮">
          <el-button type="primary" icon="Refresh" @click="handleReApply">立即重新申请</el-button>
        </el-empty>
      </section>

      <section v-else class="form-section card-style" v-loading="loading">
        <div class="section-title">
          <el-icon>
            <InfoFilled/>
          </el-icon>
          <span>{{ isReApplying ? '重新填报入驻信息' : '基本入驻信息' }}</span>
        </div>

        <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-position="top"
            :disabled="isReadOnly"
        >
          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="企业名称" prop="companyName">
                <el-input v-model="formData.companyName" placeholder="请与营业执照保持一致"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="统一社会信用代码" prop="creditCode">
                <el-input v-model="formData.creditCode" placeholder="18 位信用代码"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="40">
            <el-col :span="8">
              <el-form-item label="法人代表" prop="legalPerson">
                <el-input v-model="formData.legalPerson"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="注册资本 (万元)" prop="registeredCapital">
                <el-input-number v-model="formData.registeredCapital" :precision="2" :step="10" style="width:100%"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="所属行业" prop="industry">
                <el-select v-model="formData.industry" placeholder="请选择行业" style="width:100%">
                  <el-option label="信息技术" value="IT"/>
                  <el-option label="生物医药" value="Bio"/>
                  <el-option label="智能制造" value="Manufacturing"/>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">联系人信息</el-divider>

          <el-row :gutter="40">
            <el-col :span="12">
              <el-form-item label="经办人姓名" prop="contactPerson">
                <el-input v-model="formData.contactPerson"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="formData.contactPhone"/>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="营业执照电子版" prop="licenseUrl">
            <div class="upload-wrapper">
              <el-upload
                  class="license-uploader"
                  action="/api/common/upload"
                  :show-file-list="false"
                  :on-success="handleUploadSuccess"
                  :disabled="isReadOnly"
              >
                <img v-if="formData.licenseUrl" :src="formData.licenseUrl" class="license-img"/>
                <el-icon v-else class="uploader-icon">
                  <Plus/>
                </el-icon>
              </el-upload>
              <div class="upload-tip" v-if="!isReadOnly">支持 jpg/png，大小不超过 5MB</div>
            </div>
          </el-form-item>

          <div class="form-actions" v-if="!isReadOnly">
            <el-button size="large" @click="resetForm">重置</el-button>
            <el-button
                type="primary"
                size="large"
                :loading="submitting"
                @click="submitForm"
                class="submit-btn"
            >
              {{ (enterpriseInfo.status === 2 || enterpriseInfo.status === 3) ? '修改并重新提交' : '提交入驻申请' }}
            </el-button>
          </div>
        </el-form>
      </section>

      <section v-if="auditHistory.length > 0" class="audit-timeline card-style">
        <div class="section-title">审核历程</div>
        <el-timeline>
          <el-timeline-item
              v-for="(log, index) in auditHistory"
              :key="index"
              :type="log.status === 1 ? 'success' : (log.status === 2 ? 'danger' : 'primary')"
              :timestamp="log.createTime"
          >
            <h4>{{ log.auditAction }}</h4>
            <p v-if="log.opinion">意见：{{ log.opinion }}</p>
            <p v-if="log.auditorName" class="auditor">审核人：{{ log.auditorName }}</p>
          </el-timeline-item>
        </el-timeline>
      </section>
    </main>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import {InfoFilled, Plus} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(true)
const submitting = ref(false)
const formRef = ref(null)
const isReApplying = ref(false) // 是否正处于“再次申请”的可编辑状态

// 初始企业状态信息
const enterpriseInfo = ref({
  id: null,
  status: null, // null:未申请, 0:待审, 1:通过, 2:驳回, 3:已迁出
  auditOpinion: '',
  createTime: ''
})

// 表单数据
const formData = ref({
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  industry: '',
  contactPerson: '',
  contactPhone: '',
  licenseUrl: ''
})

// 校验规则
const rules = {
  companyName: [{required: true, message: '请输入企业名称', trigger: 'blur'}],
  creditCode: [{required: true, pattern: /^[A-Z0-9]{18}$/, message: '请输入18位信用代码', trigger: 'blur'}],
  contactPhone: [{required: true, pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur'}],
  licenseUrl: [{required: true, message: '请上传营业执照', trigger: 'change'}]
}

// 状态字典
const statusMap = {
  0: {text: '资料审核中', tagType: 'warning'},
  1: {text: '正式入驻', tagType: 'success'},
  2: {text: '审核驳回', tagType: 'danger'},
  3: {text: '已迁出', tagType: 'info'}
}

// 进度条控制
const stepActive = computed(() => {
  if (enterpriseInfo.value.status === 0) return 1
  if (enterpriseInfo.value.status === 1) return 3
  if (enterpriseInfo.value.status === 2) return 1
  return 0
})

// 是否只读逻辑完善
const isReadOnly = computed(() => {
  // 1. 如果用户点击了“再次申请”，则必须解锁
  if (isReApplying.value) return false
  // 2. 如果从未申请，解锁
  if (enterpriseInfo.value.status === null) return false
  // 3. 其他状态（待审核、已入驻、驳回未点再次申请、迁出未点再次申请）均只读
  return true
})

const auditHistory = ref([])

// 初始化：获取数据
const initData = async () => {
  loading.value = true
  try {
    const res = await enterpriseApi.getMyEnterprise()
    if (res.data) {
      enterpriseInfo.value = res.data
      Object.assign(formData.value, res.data)
      const historyRes = await enterpriseApi.getAuditHistory(res.data.id)
      auditHistory.value = historyRes.data
    }
  } catch (err) {
    console.error('获取信息失败', err)
  } finally {
    loading.value = false
  }
}

// 处理再次申请的动作
const handleReApply = () => {
  ElMessageBox.confirm(
      '重新申请将进入新的审核流程，确定继续吗？',
      '提示',
      {confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'}
  ).then(() => {
    isReApplying.value = true
    ElMessage.info('请更新您的入驻信息后点击提交')
  }).catch(() => {
  })
}

// 提交申请
const submitForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        await enterpriseApi.apply(formData.value)
        ElMessage.success('申请提交成功！请等待审核')
        isReApplying.value = false // 提交成功后重置编辑标识
        await initData() // 刷新最新状态（变为待审核 0）
      } catch (err) {
        console.error(err)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 图片上传成功
const handleUploadSuccess = (res) => {
  formData.value.licenseUrl = res.data.url
}

const resetForm = () => formRef.value.resetFields()

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

.header-content h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
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
  max-width: 1000px;
  margin: 0 auto;
}

.card-style {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 25px;
}

.section-title .el-icon {
  color: #409eff;
}

.reject-alert {
  margin-bottom: 24px;
}

.license-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 178px;
  height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.license-uploader:hover {
  border-color: #409eff;
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

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f2f5;
}

.submit-btn {
  padding-left: 40px;
  padding-right: 40px;
}

.auditor {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.moved-out-section {
  text-align: center;
  padding: 60px 0;
}

:deep(.el-step.is-horizontal .el-step__line) {
  height: 2px;
}
</style>