<template>
  <div class="contact-wrapper">
    <div class="contact-hero">
      <div class="hero-bg-overlay"></div>
      <div class="hero-content">
        <h1 class="title">联系我们</h1>
        <p class="subtitle">开放共赢，智造未来。热忱欢迎优秀企业入驻，共谱产业新篇章。</p>
        <el-tag type="info" effect="dark" round class="park-tag">Xinxin 智慧园区经济开发区管理平台</el-tag>
      </div>
    </div>

    <div class="contact-main">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="24" :md="10" :lg="8" class="info-side">
          <div class="sticky-side">
            <h2 class="section-title">官方联系渠道</h2>
            <div class="divider"></div>
            <div class="info-cards">
              <el-card shadow="hover" class="info-card">
                <template #default>
                  <div class="card-inner">
                    <div class="icon-box location-icon">
                      <el-icon>
                        <Location/>
                      </el-icon>
                    </div>
                    <div class="text-box">
                      <h4>园区地址</h4>
                      <p>经济开发区创新大道 88 号，Xinxin大厦裙楼</p>
                    </div>
                  </div>
                </template>
              </el-card>
              <el-card shadow="hover" class="info-card">
                <template #default>
                  <div class="card-inner">
                    <div class="icon-box phone-icon">
                      <el-icon>
                        <PhoneFilled/>
                      </el-icon>
                    </div>
                    <div class="text-box">
                      <h4>招商服务热线 (09:00 - 18:00)</h4>
                      <p class="highlight-text">0512-88888888</p>
                    </div>
                  </div>
                </template>
              </el-card>
              <el-card shadow="hover" class="info-card">
                <template #default>
                  <div class="card-inner">
                    <div class="icon-box email-icon">
                      <el-icon>
                        <Message/>
                      </el-icon>
                    </div>
                    <div class="text-box">
                      <h4>官方服务邮箱</h4>
                      <p>service@xin-park.com</p>
                    </div>
                  </div>
                </template>
              </el-card>
            </div>
            <div class="qr-section card-style">
              <div class="qr-content">
                <el-image :src="wechatQrUrl" class="qr-code" fit="contain">
                  <template #error>
                    <div class="image-slot">QR Code</div>
                  </template>
                </el-image>
                <div class="qr-text">
                  <h5>扫码关注 官方微信公众号</h5>
                  <p>第一时间掌握园区动态、政策速递与企业风采</p>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="24" :md="14" :lg="16" class="form-side">
          <div class="form-container card-style">
            <h2 class="section-title">意向登记或留言</h2>
            <div class="divider"></div>
            <p class="form-tip">请填写您的联系方式及相关意向（入驻、求职或服务需求），我们将在 2
              个工作日内安排专人与您取得联系。</p>
            <el-form ref="inquiryFormRef" :model="inquiryForm" :rules="rules" label-position="top" size="large"
                     class="inquiry-form">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="您的姓名" prop="applicantName">
                    <el-input v-model="inquiryForm.applicantName" placeholder="请填写姓名"/>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="企业名称 (选填)" prop="companyName">
                    <el-input v-model="inquiryForm.companyName" placeholder="请填写企业名称"/>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="意向/留言主题" prop="type">
                    <el-select v-model="inquiryForm.type" placeholder="请选择主旨意向" >
                      <el-option label="企业入驻意向咨询" :value="1"/>
                      <el-option label="人才求职与HR对接" :value="2"/>
                      <el-option label="园区配套服务需求" :value="3"/>
                      <el-option label="一般性留言反馈" :value="4"/>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input v-model="inquiryForm.contactPhone" placeholder="请填写手机或固话"/>
                </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="详情（请输入意向描述或留言内容）" prop="remark">
                <el-input v-model="inquiryForm.remark" type="textarea" :rows="8"
                          placeholder="例如：主营高性能模拟芯片研发，需租用 A 座约 800㎡ 空间..."/>
              </el-form-item>
              <div class="form-footer">
                <el-button type="primary" :loading="submitting" @click="submitForm" round size="large"
                           class="gradient-btn" icon="Promotion">
                  确认提交
                </el-button>
              </div>
            </el-form>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {Location, PhoneFilled, Message, Promotion} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import inquiryApi from '@/api/inquiry'

const wechatQrUrl = ref('http://192.168.5.229:9000/myproject/mmexport1776255166853.jpg')
const inquiryFormRef = ref(null)
const submitting = ref(false)

const inquiryForm = reactive({
  applicantName: '', companyName: '', type: null, contactPhone: '', remark: ''
})

const rules = reactive({
  applicantName: [{required: true, message: '请输入您的姓名', trigger: 'blur'}],
  contactPhone: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
  type: [{required: true, message: '请选择意向主题', trigger: 'change'}],
  remark: [{required: true, message: '请输入留言内容详情', trigger: 'blur'}]
})

const submitForm = async () => {
  if (!inquiryFormRef.value) return
  await inquiryFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submitting.value = true
      const res = await inquiryApi.submitPublicInquiry(inquiryForm)
      if (res && res.code === 200) {
        ElMessage({message: res.data || '意向提交成功，我们将在2个工作日内联系您！', type: 'success', plain: true})
        inquiryFormRef.value.resetFields()
      } else {
        ElMessage.error(res.msg || '提交失败，请稍后再试')
      }
    } catch (err) {
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.contact-wrapper {
  background-color: #f4f7f9;
  min-height: calc(100vh - 100px);
  padding-bottom: 60px;
}

.contact-hero {
  position: relative;
  width: 100%;
  height: 300px;
  background: url('@/assets/banner3.webp') no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

.hero-bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(21, 21, 21, 0.36) 0%, rgba(0, 0, 0, 0.55) 100%);z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  text-align: center;
  color: white;
  padding: 0 20px;
}

.hero-content .title {
  font-size: 36px;
  font-weight: 600;
  letter-spacing: 2px;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.hero-content .subtitle {
  font-size: 18px;
  color: #e0e6ed;
  line-height: 1.8;
  margin-bottom: 30px;
  font-weight: 300;
}

.park-tag {
  background-color: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  font-size: 13px;
}

.contact-main {
  max-width: 1350px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 5;
}

.card-style {
  background: #ffffff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 25px -5px rgba(64, 158, 255, 0.15);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.section-title {
  font-size: 22px;
  font-weight: 500;
  color: #1e293b;
  margin: 0 0 10px 0;
}

.divider {
  height: 2px;
  background-color: rgba(19, 130, 246, 0.62);
  border-radius: 2px;
  margin-bottom: 30px;
}

.info-side {
  margin-bottom: 30px;
}

.sticky-side {
  background: #fff;
  border-radius: 16px;
  position: sticky;
  top: 100px;
  padding: 40px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 10px 25px -5px rgba(64, 158, 255, 0.15);
}

.info-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.info-card {
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #edf2f7;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.info-card:hover {
  background: #fff;
  transform: translateY(-5px);
  box-shadow: 0 10px 25px -5px rgba(64, 158, 255, 0.15);
  border-color: #d9ecff;
}

:deep(.info-card .el-card__body) {
  padding: 20px 25px;
}

.card-inner {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.icon-box {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 3px;
}

.location-icon {
  background-color: #ecf5ff;
  color: #409EFF;
}

.phone-icon {
  background-color: #f0f9eb;
  color: #67C23A;
}

.email-icon {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.text-box h4 {
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  margin: 0 0 6px 0;
}

.text-box p {
  font-size: 14px;
  color: #1e293b;
  margin: 0;
  line-height: 1.6;
}

.highlight-text {
  font-size: 20px !important;
  font-weight: 600;
  color: #409EFF !important;
}

.qr-section {
  padding: 30px;
}

.qr-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.qr-code {
  width: 90px;
  height: 90px;
  border-radius: 10px;
  flex-shrink: 0;
  border: 4px solid #f8fafc;
}

.qr-text h5 {
  font-size: 15px;
  color: #1e293b;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.qr-text p {
  font-size: 13px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

.form-side {
  margin-bottom: 30px;
}

.form-tip {
  font-size: 15px;
  color: #64748b;
  line-height: 1.8;
  margin-bottom: 20px;
  margin-top: -10px;
}

.inquiry-form {
  padding: 0 10px;
}

:deep(.el-form-item__label) {
  font-weight: 500 !important;
  color: #606266 !important;
  font-size: 14px;
  padding-bottom: 5px !important;
}

:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background-color: #fcfdfe;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  box-shadow: none;
  transition: border-color 0.2s;
}

:deep(.el-input__wrapper:hover), :deep(.el-textarea__inner:hover) {
  border-color: #c0c4cc;
}

:deep(.el-input__wrapper.is-focus), :deep(.el-textarea__inner:focus) {
  border-color: #409EFF;
  background-color: #fff;
}

.form-footer {
  margin-top: 40px;
  text-align: right;
}

.gradient-btn {
  background: linear-gradient(90deg, #4f46e5 0%, #3b82f6 100%) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.gradient-btn:hover {
  background: linear-gradient(90deg, #6057e9 0%, #4c8ff8 100%) !important;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(59, 130, 246, 0.4);
}
</style>