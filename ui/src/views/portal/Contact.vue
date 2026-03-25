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
      <el-row :gutter="40">
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
            <h2 class="section-title">意向登记与留言</h2>
            <div class="divider"></div>
            <p class="form-tip">
              请填写您的联系方式及相关意向（入驻、求职或服务需求），我们将在 2 个工作日内安排专人与您取得联系。
            </p>

            <el-form
                ref="inquiryFormRef"
                :model="inquiryForm"
                :rules="rules"
                label-position="top"
                size="large"
                class="inquiry-form"
            >
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

              <el-form-item label="意向/留言主题" prop="type">
                <el-select v-model="inquiryForm.type" placeholder="请选择主旨意向" style="width: 100%">
                  <el-option label="企业入驻意向咨询" :value="1"/>
                  <el-option label="人才求职与HR对接" :value="2"/>
                  <el-option label="园区配套服务需求" :value="3"/>
                  <el-option label="一般性留言反馈" :value="4"/>
                </el-select>
              </el-form-item>

              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="inquiryForm.contactPhone" placeholder="请填写手机或固话"/>
              </el-form-item>

              <el-form-item label="留言详情（请输入意向描述或留言内容）" prop="remark">
                <el-input
                    v-model="inquiryForm.remark"
                    type="textarea"
                    :rows="6"
                    placeholder="例如：主营高性能模拟芯片研发，需租用 A 座约 800㎡ 空间..."
                />
              </el-form-item>

              <div class="form-footer">
                <el-button
                    type="primary"
                    :loading="submitting"
                    @click="submitForm"
                    round
                    size="large"
                    class="gradient-btn"
                    icon="Promotion"
                >
                  确认提交意向
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

// 数据模型
const wechatQrUrl = ref('http://192.168.5.229:9000/myproject/qr_code.jpg') // 替换为真实的园区微信二维码URL
const inquiryFormRef = ref(null)
const submitting = ref(false)

// 表单模型 (示例 DTO 结构)
const inquiryForm = reactive({
  applicantName: '',
  companyName: '',
  type: null,
  contactPhone: '',
  remark: ''
})

// 校验规则
const rules = reactive({
  applicantName: [{required: true, message: '请输入您的姓名', trigger: 'blur'}],
  contactPhone: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
  type: [{required: true, message: '请选择意向主题', trigger: 'change'}],
  remark: [{required: true, message: '请输入留言内容详情', trigger: 'blur'}]
})

// 提交表单逻辑 (待对接接口)
const submitForm = async () => {
  if (!inquiryFormRef.value) return
  await inquiryFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submitting.value = true
      // TODO: 调用前端公共接口提交留言，例如 commonApi.submitInquiry(inquiryForm)
      console.log('提交的数据:', inquiryForm)

      // 模拟请求成功
      await new Promise(resolve => setTimeout(resolve, 1500))

      ElMessage({
        message: '意向提交成功，我们将在2个工作日内联系您！',
        type: 'success',
        plain: true
      })
      // 重置表单
      inquiryFormRef.value.resetFields()
    } catch (err) {
      ElMessage.error('网络拥堵，提交失败，请重试')
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
/* ====== 基础容器样式与背景 ====== */
.contact-wrapper {
  background-color: #f4f7f9; /* 极简灰背景，反衬白色卡片 */
  min-height: calc(100vh - 100px);
  padding-bottom: 60px;
}

/* ====== 顶部 HeroBanner 区：商务科技设计 ====== */
.contact-hero {
  position: relative;
  width: 100%;
  height: 280px;
  background: url('@/assets/contact-banner.jpg') no-repeat center center; /* 替换为一张园区的全景商务图 */
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

/* 半透明深色遮罩 */
.hero-bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(29, 38, 113, 0.85) 0%, rgba(13, 13, 13, 0.7) 100%);
  backdrop-filter: blur(2px);
  z-index: 1;
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

/* ====== 主体内容区 ====== */
.contact-main {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 5;
}

/* 卡片工具样式 (高级白) */
.card-style {
  background: #ffffff;
  border-radius: 16px; /* 圆角更大，更具现代感 */
  padding: 40px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

/* 标题工具样式 */
.section-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px 0;
}

.divider {
  width: 40px;
  height: 4px;
  background-color: #409EFF;
  border-radius: 2px;
  margin-bottom: 30px;
}

/* ====== 左侧联系渠道信息区 ====== */
.info-side {
  margin-bottom: 30px;
}

.sticky-side {
  position: sticky; /* 粘性布局，滚动时信息栏跟随 */
  top: 100px;
}

.info-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.info-card {
  border-radius: 12px;
  background: #f8fafc; /* 微灰卡片背景 */
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

/* 动态颜色区分不同类型联系方式 */
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
  font-weight: 700;
  color: #409EFF !important;
}

/* 微信二维码区 (现代化卡片设计) */
.qr-section {
  padding: 30px;
}

.qr-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.qr-code {
  width: 90px;
  height: 90px;
  border-radius: 10px;
  flex-shrink: 0;
  border: 4px solid #f8fafc;
}

.qr-text h5 {
  font-size: 16px;
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

/* ====== 右侧留言反馈表单区 ====== */
.form-side {
  margin-bottom: 30px;
}

.form-tip {
  font-size: 15px;
  color: #64748b;
  line-height: 1.8;
  margin-bottom: 35px;
  margin-top: -10px;
}

.inquiry-form {
  padding: 0 10px;
}

/* 针对此页面的 ElementPlus Form Item样式定制 */
:deep(.el-form-item__label) {
  font-weight: 500 !important;
  color: #606266 !important;
  font-size: 14px;
  padding-bottom: 5px !important;
}

:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background-color: #fcfdfe; /* 微白输入框 */
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

/* 现代化渐变按钮 */
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