<template>
  <div class="enterprise-container">
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.companyName" placeholder="请输入企业名称" clearable style="width: 200px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="所属行业">
          <el-input v-model="queryParams.industry" placeholder="如：电子信息" clearable style="width: 150px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="入驻楼宇">
          <el-input v-model="queryParams.buildingNo" placeholder="如：A座" clearable style="width: 130px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
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
        <el-table-column type="selection" width="50" align="center"/>

        <el-table-column label="企业基本信息" min-width="250" align="center">
          <template #default="scope">
            <div style="font-weight: 200; color: #303133">{{ scope.row.companyName }}</div>
            <div
                style="font-size: 12px; color: #6d96e6; margin-top: 4px; display: flex; align-items: center; justify-content: center;">
              信用代码：{{ scope.row.creditCode }}

              <el-tooltip content="点击预览营业执照" placement="top" v-if="scope.row.licenseUrl">
                <el-image
                    style="width: 30px; height: 20px; margin-left: 10px; cursor: pointer; color: #409EFF; border: rgba(170,189,205,0.87) 1px groove"
                    :src="scope.row.licenseUrl"
                    :preview-src-list="[scope.row.licenseUrl]"
                    preview-teleported
                >
                  <template #error>
                    <el-icon>
                      <Picture/>
                    </el-icon>
                  </template>
                  <template #viewer>
                    <el-icon @click.stop>
                      <View/>
                    </el-icon>
                  </template>
                </el-image>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="法人/资本" width="160" align="center">
          <template #default="scope">
            <div style="color: #606266">{{ scope.row.legalPerson }}</div>
            <el-tag size="small" type="primary" effect="dark" style="margin-top: 4px;border: #0f4780 dotted 1px">
              {{ scope.row.registeredCapital }} 万
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="行业/租用空间" width="220" align="center">
          <template #default="scope">
            <div style="font-size: 13px">{{ scope.row.industry }}</div>
            <div style="font-size: 12px; color: #67C23A; margin-top: 4px">
              <el-icon>
                <OfficeBuilding/>
              </el-icon>
              {{ scope.row.buildingNo || '未分配' }} ({{ scope.row.rentArea || 0 }}㎡)
            </div>
          </template>
        </el-table-column>

        <el-table-column label="关联账号" width="120" align="center">
          <template #default="scope">
            <el-text v-if="scope.row.userName" class="mx-1" type="primary">
              <el-icon style="margin-left: 4px">
                <User/>
              </el-icon>
              {{ scope.row.userName }}
            </el-text>
            <span v-else style="color: #909399; font-size: 12px">未关联</span>
          </template>
        </el-table-column>

        <el-table-column label="联系人" min-width="100" align="center">
          <template #default="scope">
            <div>{{ scope.row.contactPerson }}</div>
            <div style="color: #909399; font-size: 12px">{{ scope.row.contactPhone }}</div>
          </template>
        </el-table-column>

        <el-table-column label="状态" prop="status" width="120" align="center">
          <template #default="scope">
            <el-tooltip v-if="scope.row.status === 2" :content="'驳回理由：' + (scope.row.auditOpinion || '无')"
                        placement="top">
              <el-tag :type="statusMap[scope.row.status]?.type" effect="dark" style="cursor: help">
                {{ statusMap[scope.row.status]?.label }}
              </el-tag>
            </el-tooltip>
            <el-tag v-else :type="statusMap[scope.row.status]?.type" effect="dark">
              {{ statusMap[scope.row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" prop="createTime" width="120" align="center">
          <template #default="scope">
            <span style="font-size: 12px; color: #606266">
              {{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '-' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="更新时间" prop="updateTime" width="120" align="center">
          <template #default="scope">
            <span style="font-size: 12px; color: #606266">
              {{ scope.row.updateTime ? scope.row.updateTime.substring(0, 10) : '-' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <div v-if="scope.row.status === 0" style="margin: 12px 0 15px 0">
              <el-button link type="success" size="small" :icon="Check" @click="handleAudit(scope.row, 1)">通过
              </el-button>
              <el-button link type="danger" size="small" :icon="Close" @click="handleAudit(scope.row, 2)">驳回
              </el-button>
            </div>

            <div v-if="scope.row.status === 4" style="margin: 12px 0 15px 0">
              <el-button link type="primary" size="small" :icon="Check" @click="handleMoveOutAudit(scope.row, 3)">
                准予迁出
              </el-button>
              <el-button link type="warning" size="small" :icon="Close" @click="handleMoveOutAudit(scope.row, 1)">
                驳回迁出
              </el-button>
            </div>
            <div>
              <el-button link type="primary" size="small" :icon="View" @click="handleDetail(scope.row)">详情</el-button>
              <el-button link type="warning" size="small" :icon="EditPen" @click="handleEdit(scope.row)">修改
              </el-button>
              <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row)">删除
              </el-button>
            </div>
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

      <el-dialog
          v-model="detailVisible"
          title="企业入驻电子档案"
          width="800px"
          class="enterprise-detail-dialog"
          destroy-on-close
      >
        <div class="detail-header">
          <div class="company-logo">
            <el-icon>
              <OfficeBuilding/>
            </el-icon>
          </div>
          <div class="company-main-info">
            <h3>{{ enterpriseDetail.companyName }}</h3>
            <el-tag :type="statusMap[enterpriseDetail.status]?.type" size="small" effect="dark">
              {{ statusMap[enterpriseDetail.status]?.label }}
            </el-tag>
          </div>
        </div>

        <el-descriptions :column="2" border class="margin-top">
          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Postcard/>
              </el-icon>
              信用代码
            </template>
            <span class="text-bold">{{ enterpriseDetail.creditCode }}</span>
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <User/>
              </el-icon>
              法人代表
            </template>
            {{ enterpriseDetail.legalPerson }}
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Money/>
              </el-icon>
              注册资本
            </template>
            <span class="price-text">{{ enterpriseDetail.registeredCapital }} 万元</span>
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Tools/>
              </el-icon>
              所属行业
            </template>
            {{ enterpriseDetail.industry }}
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Location/>
              </el-icon>
              租用楼宇
            </template>
            <el-tag type="success" size="small">{{ enterpriseDetail.buildingNo || '待分配' }}</el-tag>
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <FullScreen/>
              </el-icon>
              租用面积
            </template>
            {{ enterpriseDetail.rentArea }} ㎡
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Calendar/>
              </el-icon>
              租约起止
            </template>
            <template v-if="enterpriseDetail.leaseStartDate">
              <el-tag size="small" type="info" effect="plain">
                {{ enterpriseDetail.leaseStartDate }} 至 {{ enterpriseDetail.leaseEndDate }}
              </el-tag>
            </template>
            <span v-else style="color: #909399">尚未签署合同</span>
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Calendar/>
              </el-icon>
              申请日期
            </template>
            {{ enterpriseDetail.createTime }}
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <Phone/>
              </el-icon>
              联系方式
            </template>
            {{ enterpriseDetail.contactPhone }}
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label">
            <template #label>
              <el-icon>
                <User/>
              </el-icon>
              申请账号
            </template>
            {{ enterpriseDetail.userName || '未知账号' }}
          </el-descriptions-item>

          <el-descriptions-item label="申请备注/理由" :span="2" v-if="enterpriseDetail.applyReason">
            <el-tag type="warning" effect="plain">{{ enterpriseDetail.applyReason }}</el-tag>
          </el-descriptions-item>

          <el-descriptions-item label-class-name="desc-label" :span="2">
            <template #label>
              <el-icon>
                <Picture/>
              </el-icon>
              营业执照
            </template>
            <div class="detail-license-preview" v-if="enterpriseDetail.licenseUrl">
              <el-image
                  :src="enterpriseDetail.licenseUrl"
                  :preview-src-list="[enterpriseDetail.licenseUrl]"
                  fit="contain"
                  class="detail-img"
                  preview-teleported
              />
            </div>
            <span v-else style="color: #909399; font-size: 12px">未上传附件</span>
          </el-descriptions-item>
          <el-descriptions-item label-class-name="desc-label" :span="2">
            <template #label>
              <el-icon>
                <InfoFilled/>
              </el-icon>
              企业概况
            </template>
            <div class="rich-content-view" v-html="enterpriseDetail.introduction || '暂无详细介绍'"></div>
          </el-descriptions-item>
        </el-descriptions>

        <div class="opinion-section">
          <div class="opinion-title">
            <el-icon>
              <Timer/>
            </el-icon>
            入驻 / 迁出记录
          </div>

          <div class="timeline-wrapper">
            <el-timeline v-if="auditHistory.length > 0">
              <el-timeline-item
                  v-for="(activity, index) in auditHistory"
                  :key="index"
                  :type="statusMap[activity.status]?.type"
                  :timestamp="activity.createTime"
                  placement="top"
              >
                <el-card shadow="never" class="timeline-card" :status="activity.status">
                  <div class="timeline-card-header">
                    <span class="action-text">
                      {{ activity.auditAction }}
                    </span>
                    <span class="auditor-tag">
                      <el-icon><User/></el-icon> {{ activity.auditorName || '申请人' }}
                    </span>
                  </div>

                  <div class="opinion-box">
                    <div class="opinion-content">
                      <el-icon style="margin-right: 4px">
                        <ChatDotSquare/>
                      </el-icon>
                      “ {{ activity.opinion }} ”
                    </div>
                  </div>

                  <div v-if="activity.status === 2" style="margin-top: 10px">
                    <el-tag type="danger" size="small" closable @close="false">驳回记录</el-tag>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>

            <el-empty v-else :image-size="60" description="暂无流转历史"/>
          </div>
        </div>

        <template #footer>
          <el-button type="primary" plain @click="detailVisible = false">确 认</el-button>
        </template>
      </el-dialog>
    </div>

    <el-dialog
        v-model="dialogVisible"
        :title="form.id ? '编辑企业电子档案' : '提交入驻申请'"
        width="800px"
        class="custom-form-dialog"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          label-position="top"
          style="padding: 0 20px"
      >
        <div class="form-section">
          <h4 class="section-title">
            <el-icon>
              <InfoFilled/>
            </el-icon>
            工商基础信息
          </h4>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="企业全称" prop="companyName">
                <el-input v-model="form.companyName" placeholder="请按营业执照名称填写" clearable/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="信用代码" prop="creditCode">
                <el-input v-model="form.creditCode" placeholder="18位社会信用代码"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="法人代表" prop="legalPerson">
                <el-input v-model="form.legalPerson" placeholder="姓名"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="注册资本" prop="registeredCapital">
                <el-input-number v-model="form.registeredCapital" :precision="2" :step="100" :min="0"
                                 controls-position="right" style="width: 100%"/>
                <span class="input-suffix">万元</span>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="所属行业" prop="industry">
                <el-input v-model="form.industry" placeholder="如：人工智能、生物医药"/>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section mt-2">
          <h4 class="section-title">
            <el-icon>
              <Location/>
            </el-icon>
            园区入驻意向
          </h4>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="意向楼宇" prop="buildingNo">
                <el-input v-model="form.buildingNo" placeholder="例如：A座-808"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="租用面积" prop="rentArea">
                <el-input-number v-model="form.rentArea" :precision="2" :min="0" controls-position="right"
                                 style="width: 100%"/>
                <span class="input-suffix">㎡</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="租约期限" prop="leaseEndDate" class="date-range-item">
                <el-date-picker
                    v-model="form.leaseStartDate"
                    type="date"
                    placeholder="起租日期"
                    value-format="YYYY-MM-DD"
                    style="width: 47%"
                />
                <span class="date-separator">至</span>
                <el-date-picker
                    v-model="form.leaseEndDate"
                    type="date"
                    placeholder="到期日期"
                    value-format="YYYY-MM-DD"
                    style="width: 47%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section mt-2">
          <h4 class="section-title">
            <el-icon>
              <Phone/>
            </el-icon>
            联系方式
          </h4>
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="业务联系人" prop="contactPerson">
                <el-input v-model="form.contactPerson" placeholder="姓名"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="手机或固定电话"/>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section mt-4">
          <h4 class="section-title">
            <el-icon>
              <Tickets/>
            </el-icon>
            企业简介 (图文详情)
          </h4>
          <el-form-item label-width="0" prop="introduction">
            <Editor v-model="form.introduction" placeholder="请输入企业的详细介绍、主营业务及企业风采..."/>
          </el-form-item>
        </div>

        <div class="form-section  mt-2">
          <h4 class="section-title">
            <el-icon>
              <Picture/>
            </el-icon>
            资质附件
          </h4>
          <el-form-item label="营业执照" prop="licenseUrl">
            <div class="license-upload-wrapper">
              <el-upload
                  class="license-uploader"
                  action="#"
                  :show-file-list="false"
                  :http-request="handleImageUpload"
                  :before-upload="beforeLicenseUpload"
                  accept=".jpg,.jpeg,.png,.gif"
              >
                <div v-if="form.licenseUrl" class="image-preview-container">
                  <img :src="form.licenseUrl" class="license-img" alt="营业执照附件"/>
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

                <div v-else class="license-placeholder">
                  <el-icon class="license-icon">
                    <Plus/>
                  </el-icon>
                  <span class="license-text">上传营业执照</span>
                </div>
              </el-upload>

              <div class="upload-tip">请上传清晰的营业执照扫描件 (支持 JPG/JPEG/PNG/GIF，小于 5MB)</div>
            </div>

            <el-image-viewer
                v-if="showViewer"
                :url-list="[form.licenseUrl]"
                @close="showViewer = false"
            />
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" round>取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm" round px-6>
            {{ form.id ? '保存修改' : '立即提交申请' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
        v-model="auditDialogVisible"
        :title="getAuditDialogTitle()"
        width="450px"
        destroy-on-close
    >
      <div style="margin-bottom: 20px; color: #606266;">
        正在处理企业：<strong style="color: #303133">{{ auditForm.companyName }}</strong>
      </div>

      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules">
        <el-form-item label="审批意见" prop="opinion">
          <el-input
              v-model="auditForm.opinion"
              type="textarea"
              :rows="4"
              placeholder="请输入详细的审批意见或原因..."
          />
        </el-form-item>

        <div class="common-opinions">
          <div style="font-size: 12px; color: #909399; margin-bottom: 8px;">快捷回复：</div>
          <el-space wrap>
            <el-tag
                v-for="item in (auditForm.isMoveOut ? moveOutCommonOpinions[auditForm.status] : commonOpinions[auditForm.status])"
                :key="item"
                class="opinion-tag"
                effect="plain"
                @click="auditForm.opinion = item"
            >
              {{ item.length > 10 ? item.substring(0, 10) + '...' : item }}
            </el-tag>
          </el-space>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button :type="getAuditButtonType()" @click="submitAudit">
          {{ getAuditButtonText() }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Search, Refresh, Plus, Check, Close, View, Delete,
  InfoFilled, Location, Phone, Postcard, OfficeBuilding, FullScreen, Calendar, Tools, Money, User, EditPen, ZoomIn,
  Picture, Timer, ChatDotSquare, Tickets
} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import Editor from '@/components/WangEditor/index.vue'
import {uploadFile} from "@/utils/upload.js";

// 1. 状态映射配置
const statusMap = {
  0: {label: '待审核', type: 'warning'},
  1: {label: '已入驻', type: 'success'},
  2: {label: '已驳回', type: 'danger'},
  3: {label: '已迁出', type: 'info'},
  4: {label: '迁出待审', type: 'warning'}
}

// 2. 响应式变量
const loading = ref(false)
const submitLoading = ref(false)
const enterpriseList = ref([])
const total = ref(0)
const multipleSelection = ref([])
const enterpriseTableRef = ref(null)
const dialogVisible = ref(false)
const formRef = ref(null)
const showViewer = ref(false)
const auditHistory = ref([])

// 审批弹窗控制
const auditDialogVisible = ref(false)
const auditFormRef = ref(null)
const auditForm = ref({
  id: null,
  status: 1,
  companyName: '',
  opinion: '',
  isMoveOut: false  // 增加默认值
})

// 快捷审批语配置
const commonOpinions = {
  1: ['准予入驻。', '资料完整，符合园区产业导向，同意入驻。', '欢迎入驻，请尽快办理后续手续。'],
  2: ['资料不全，请补全后重新申请。', '信用代码有误，请核实。', '所属行业不符合本园区准入标准。']
}

// ====================== ✅ 修复点 1：迁出常用语键名修正 ======================
const moveOutCommonOpinions = {
  3: ['准予迁出，请于3日内办理物业交接。', '合同期满，准予迁出。'],
  1: ['租金未结清，请处理后再申请迁出。', '资料填写有误，请核实迁出原因。']
}

const auditRules = {
  opinion: [{required: true, message: '请填写审批意见', trigger: 'blur'}]
}

const detailVisible = ref(false)
const enterpriseDetail = ref({})

// 3. 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  companyName: '',
  industry: '',
  buildingNo: '',
  status: null
})

// 4. 表单模型
const form = ref({
  id: null,
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  buildingNo: '',
  rentArea: 0,
  industry: '',
  introduction: '',
  contactPerson: '',
  contactPhone: '',
  licenseUrl: '',
  leaseStartDate: '',
  leaseEndDate: '',
  applyReason: ''
})

// 租约日期校验
const validateLeaseDates = (rule, value, callback) => {
  const {leaseStartDate, leaseEndDate} = form.value
  if (leaseStartDate && leaseEndDate) {
    const start = new Date(leaseStartDate)
    const end = new Date(leaseEndDate)
    if (end < start) return callback(new Error('结束日期不能早于开始日期'))
  }
  if (leaseStartDate && !leaseEndDate) return callback(new Error('请选择结束日期'))
  callback()
}

// 5. 校验规则
const rules = {
  companyName: [{required: true, message: '企业全称不能为空', trigger: 'blur'}],
  creditCode: [
    {required: true, message: '信用代码不能为空', trigger: 'blur'},
    {pattern: /^[0-9A-Z]{18}$/, message: '请输入18位大写字母或数字', trigger: 'blur'}
  ],
  legalPerson: [{required: true, message: '法人代表不能为空', trigger: 'blur'}],
  registeredCapital: [{required: true, message: '注册资本不能为空', trigger: 'blur'}],
  industry: [{required: true, message: '所属行业不能为空', trigger: 'blur'}],
  contactPerson: [{required: true, message: '联系人不能为空', trigger: 'blur'}],
  contactPhone: [{required: true, message: '联系电话不能为空', trigger: 'blur'}],
  buildingNo: [{required: true, message: '意向楼宇不能为空', trigger: 'blur'}],
  rentArea: [{required: true, message: '租用面积不能为空', trigger: 'blur'}],
  licenseUrl: [{required: true, message: '请上传营业执照照片附件', trigger: 'change'}],
  leaseEndDate: [{required: true, validator: validateLeaseDates, trigger: 'change'}]
}

// 6. 核心业务方法
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
  queryParams.value = {pageNum: 1, pageSize: 10, companyName: '', industry: '', buildingNo: '', status: null}
  handleQuery()
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handleAdd = () => {
  form.value = {
    id: null, companyName: '', creditCode: '', legalPerson: '', registeredCapital: 0,
    buildingNo: '', rentArea: 0, industry: '', introduction: '', contactPerson: '', contactPhone: '',
    licenseUrl: '', leaseStartDate: '', leaseEndDate: ''
  }
  if (formRef.value) formRef.value.clearValidate()
  dialogVisible.value = true
}

// ✅ 新增：动态计算审批弹窗标题
const getAuditDialogTitle = () => {
  const { isMoveOut, status } = auditForm.value
  if (isMoveOut) {
    return status === 3 ? '准予迁出确认' : '驳回迁出确认'
  }
  return status === 1 ? '审批通过' : '审批驳回'
}

// ✅ 新增：动态计算审批按钮样式
const getAuditButtonType = () => {
  const { isMoveOut, status } = auditForm.value
  if (isMoveOut) {
    return status === 3 ? 'primary' : 'warning'
  }
  return status === 1 ? 'success' : 'danger'
}

// ✅ 新增：动态计算审批按钮文字
const getAuditButtonText = () => {
  const { isMoveOut, status } = auditForm.value
  if (isMoveOut) {
    return status === 3 ? '确认准予迁出' : '确认驳回迁出'
  }
  return status === 1 ? '确认通过' : '确认驳回'
}

// 营业执照上传
const beforeLicenseUpload = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('只能上传 JPG/JPEG/PNG/GIF 格式')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 5) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  try {
    const url = await uploadFile(options.file)
    form.value.licenseUrl = url
    formRef.value.validateField('licenseUrl')
    ElMessage.success('执照上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handlePreview = () => showViewer.value = true
const handleRemove = () => {
  ElMessageBox.confirm('确定删除执照图片？', '提示', {type: 'warning'}).then(() => {
    form.value.licenseUrl = ''
    ElMessage.success('已移除')
  })
}

// 修改
const handleEdit = (row) => {
  if (formRef.value) formRef.value.clearValidate()
  form.value = {...row}
  dialogVisible.value = true
}

// 提交
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      form.value.id ? await enterpriseApi.update(form.value) : await enterpriseApi.apply(form.value)
      ElMessage.success(form.value.id ? '修改成功' : '申请提交成功')
      dialogVisible.value = false
      getList()
    } finally {
      submitLoading.value = false
    }
  })
}

// 入驻审批
const handleAudit = (row, status) => {
  auditForm.value = {
    id: row.id,
    status: status,
    companyName: row.companyName,
    opinion: commonOpinions[status][0],
    isMoveOut: false
  }
  auditDialogVisible.value = true
}

// 迁出审批方法，直接使用传入的目标状态
const handleMoveOutAudit = (row, targetStatus) => {
  // targetStatus 3=准予迁出，1=驳回迁出
  auditForm.value = {
    id: row.id,
    status: targetStatus,
    isMoveOut: true,
    companyName: row.companyName,
    opinion: moveOutCommonOpinions[targetStatus][0]
  }
  auditDialogVisible.value = true
}

// ====================== 提交审批（统一处理入驻/迁出） ======================
// ✅ 修复：提交审批的文案和逻辑
const submitAudit = async () => {
  await auditFormRef.value.validate(async (valid) => {
    if (!valid) return

    const {id, status, isMoveOut, companyName, opinion} = auditForm.value
    let confirmTitle, confirmMessage, confirmType

    if (isMoveOut) {
      if (status === 3) {
        confirmTitle = '准予迁出确认'
        confirmMessage = `确认准予 <b>${companyName}</b> 迁出吗？<br>操作后状态变为：已迁出`
        confirmType = 'primary'
      } else {
        confirmTitle = '驳回迁出确认'
        confirmMessage = `确定驳回 <b>${companyName}</b> 的迁出申请吗？<br>操作后状态回到：已入驻`
        confirmType = 'warning'
      }
    } else {
      confirmTitle = status === 1 ? '入驻通过' : '入驻驳回'
      confirmMessage = status === 1
          ? `确认通过 <b>${companyName}</b> 入驻申请？`
          : `确定驳回 <b>${companyName}</b> 入驻申请？`
      confirmType = status === 1 ? 'success' : 'danger'
    }

    ElMessageBox.confirm(confirmMessage, confirmTitle, {
      type: confirmType,
      dangerouslyUseHTMLString: true,
      center: true
    }).then(async () => {
      try {
        isMoveOut
            ? await enterpriseApi.auditMoveOut(id, status, opinion)
            : await enterpriseApi.audit(id, status, opinion)
        ElMessage.success('审批操作已成功执行')
        auditDialogVisible.value = false
        getList()
      } catch (e) {
        console.error(e)
      }
    })
  })
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除 ${row.companyName}？`, '警告', {type: 'error'}).then(async () => {
    await enterpriseApi.delete([row.id])
    ElMessage.success('删除成功')
    getList()
  })
}

// 批量删除
const handleBatchDelete = () => {
  const ids = multipleSelection.value.map(i => i.id)
  ElMessageBox.confirm(`确定删除 ${ids.length} 条记录？`, '重大警告', {type: 'error'}).then(async () => {
    await enterpriseApi.delete(ids)
    ElMessage.success('批量删除成功')
    getList()
  })
}

// 详情
const handleDetail = async (row) => {
  enterpriseDetail.value = row
  detailVisible.value = true
  try {
    const res = await enterpriseApi.getAuditHistory(row.id)
    auditHistory.value = res.data || []
  } catch (e) {
    auditHistory.value = []
  }
}

onMounted(() => getList())
</script>

<style scoped>
.enterprise-container {
  min-height: calc(100vh - 100px);
}

.search-card, .table-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px 20px 0 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.table-header) {
  background-color: rgba(76, 127, 181, 0.89) !important;
  color: #ffffff;
  font-weight: 500;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1;
  margin-top: 4px;
}

:deep(.el-divider) {
  margin: 24px 0 15px 0;
}

:deep(.el-divider__text) {
  font-weight: normal;
  color: #409EFF;
}

/* 详情弹窗美化 */
.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: linear-gradient(to right, #f0f9ff, #ffffff);
  border-radius: 8px;
}

.company-logo {
  width: 50px;
  height: 50px;
  background: #409EFF;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  font-size: 24px;
  margin-right: 15px;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.company-main-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #303133;
}

:deep(.desc-label) {
  background-color: #f5f7fa !important;
  width: 110px;
  font-weight: normal !important;
  color: #606266;
}

.text-bold {
  font-weight: 500;
  color: #303133;
}

.price-text {
  color: #e6a23c;
  font-weight: normal;
}

.opinion-section {
  margin-top: 25px;
  border: none;
  background-color: transparent;
  padding: 0;
}

.opinion-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #409EFF; /* 标题左侧加重 */
}

.opinion-title .el-icon {
  margin-right: 5px;
}

.opinion-content {
  color: #606266;
  font-style: italic;
  font-size: 13px;
}

/* 覆盖 Element Plus 弹窗圆角 */
:deep(.enterprise-detail-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

/* 1. 容器：做成 4:3 或 3:2 的比例，更像证件 */
.license-uploader {
  width: 160px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  background-color: #fafafa;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.license-uploader:hover {
  border-color: #409EFF;
  background-color: #f5f7fa;
}

/* 预览容器开启定位 */
.image-preview-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

/* 2. 预览图：核心是 object-fit: cover，让图片充满容器且不变形 */
.license-img {
  width: 100%;
  height: 100%;
  object-fit: contain; /* 证件类建议用 contain，不裁剪内容 */
  background-color: #fff;
  display: block;
}

/* 核心：遮罩层样式 */
.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: default;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5); /* 半透明黑背景 */
  opacity: 0; /* 初始隐藏 */
  transition: opacity 0.3s; /* 平滑过渡 */
  gap: 15px;
}

/* 鼠标悬停容器时，遮罩层显示 */
.image-preview-container:hover .image-actions {
  opacity: 1;
}

/* 单个按钮样式 */
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  cursor: pointer;
  transition: color 0.2s;
}

.action-item:hover {
  color: #409EFF; /* 悬停变蓝色 */
}

.action-item .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.action-item span {
  font-size: 12px;
}

/* 3. 占位状态：垂直居中布局 */
.license-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.license-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.license-text {
  font-size: 12px;
  line-height: 1;
}

/* 4. 辅助提示文字 */
.upload-tip {
  font-size: 12px;
  color: #ff6c6c;
  margin-top: 10px;
  line-height: 1.4;
}

/* 深度选择器，确保 Element Plus 的容器也是 100% */
:deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 详情页图片预览样式 */
.detail-license-preview {
  padding: 8px;
  background-color: transparent;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  width: fit-content;
  margin-top: 5px;
}

.detail-img {
  width: 200px; /* 详情页展示稍大一点 */
  height: 140px;
  display: block;
  cursor: zoom-in; /* 提示用户可以点击放大 */
}

/* 确保必填星号与文字对齐 */
:deep(.el-form-item.is-required:not(.is-no-asterisk) > .el-form-item__label:before) {
  margin-right: 4px;
}

/* 弹窗整体圆角 */
:deep(.custom-form-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

/* 分段标题样式 */
.section-title {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 400;
  color: #409EFF;
  margin: 0 0 10px 0;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
}

.section-title .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

/* 输入框后缀提示 */
.input-suffix {
  position: absolute;
  right: 40px; /* 避开 input-number 的控制按钮 */
  color: #909399;
  font-size: 12px;
}

/* 日期选择器中间的横线 */
.date-separator {
  margin: 0 10px;
  color: #dcdfe6;
}

.mt-2 {
  margin-top: 2px;
}

/* 表单页脚 */
.dialog-footer {
  padding: 0 20px 5px;
  text-align: right;
}

/* 针对标签置顶的优化 */
:deep(.el-form-item__label) {
  font-weight: 500 !important;
  color: #606266 !important;
  padding-bottom: 2px !important;
}

.timeline-wrapper {
  padding: 5px 5px;
}

.timeline-card {
  border: 1px solid #ebeef5 !important;
  border-radius: 8px !important;
  transition: all 0.3s;
  position: relative;
  margin-bottom: 5px;
}

.timeline-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.action-text {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.auditor-tag {
  font-size: 12px;
  color: #909399;
  background: #f4f4f5;
  padding: 2px 10px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 针对不同状态改变侧边条颜色 */
:deep(.el-timeline-item__node--success + .el-timeline-item__wrapper .timeline-card) {
  border-left-color: #67C23A;
}

:deep(.el-timeline-item__node--danger + .el-timeline-item__wrapper .timeline-card) {
  border-left-color: #F56C6C;
}

:deep(.el-timeline-item__node--info + .el-timeline-item__wrapper .timeline-card) {
  border-left-color: #909399;
}

:deep(.el-timeline-item__node--warning + .el-timeline-item__wrapper .timeline-card) {
  border-left-color: #E6A23C;
}

.opinion-box {
  margin-top: 10px;
  padding: 10px 15px;
  background-color: #f8f9fb;
  border-radius: 6px;
  border-left: 3px solid #dcdfe6;
}

/* 小气泡效果 */
.opinion-box::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 15px;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid #f4f4f5;
}

.timeline-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f2f6fc;
}


/* 滚动条美化 */
.timeline-wrapper::-webkit-scrollbar {
  width: 4px;
}

.timeline-wrapper::-webkit-scrollbar-thumb {
  background: #e4e7ed;
  border-radius: 2px;
}

/* 已通过-绿 */
.timeline-card[status="1"] {
  border-left: 4px solid #67C23A !important;
}

/* 已驳回-红 */
.timeline-card[status="2"] {
  border-left: 4px solid #F56C6C !important;
}

/* 待审核-黄 */

.timeline-card[status="0"] {
  border-left: 4px solid #E6A23C !important;
}

/* 已迁出-灰 */
.timeline-card[status="3"] {
  border-left: 4px solid #909399 !important;
}

.opinion-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.opinion-tag:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.common-opinions {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
}

/* 详情页富文本展示容器 */
.rich-content-view {
  padding: 4px;
  background-color: transparent; /* 给个微灰背景区分内容区 */
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.4;
  color: #606266;
  max-height: 600px; /* 防止内容过长撑开整个弹窗 */
  overflow-y: auto;
}

/* 核心修复：强制约束 v-html 下的所有视频 */
.rich-content-view :deep(video),
.rich-content-view :deep([data-w-e-type="video"]),
.rich-content-view :deep(.w-e-video-container) {
  /* 建议宽度设为 400px 或 100% 以适应 Descriptions 容器 */
  width: 100% !important;
  max-width: 100% !important;
  height: auto !important;
  display: block;
  margin: 10px 0;
  background-color: #000 !important;
  border-radius: 4px;
}

/* 修正图片自适应 */
.rich-content-view :deep(img) {
  max-width: 100% !important;
  height: auto !important;
  object-fit: contain;
  display: block;
  margin: 10px 0;
}

/* 兼容处理编辑器生成的段落间距 */
.rich-content-view :deep(p) {
  margin: 8px 0;
  word-break: break-all; /* 防止长单词/链接撑开 */
}

</style>