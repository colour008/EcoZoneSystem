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

        <el-table-column label="企业基本信息" min-width="180" align="center">
          <template #default="scope">
            <div style="font-weight: 200; color: #303133">{{ scope.row.companyName }}</div>
            <div style="font-size: 12px; color: #6d96e6; margin-top: 4px; display: flex; align-items: center; justify-content: center;">
              <el-icon style="margin-right: 4px"><Postcard/></el-icon>
              信用代码：{{ scope.row.creditCode }}

              <el-tooltip content="点击预览营业执照" placement="top" v-if="scope.row.licenseUrl">
                <el-image
                    style="width: 16px; height: 16px; margin-left: 8px; cursor: pointer; color: #409EFF"
                    :src="scope.row.licenseUrl"
                    :preview-src-list="[scope.row.licenseUrl]"
                    preview-teleported
                >
                  <template #error>
                    <el-icon><Picture /></el-icon>
                  </template>
                  <template #viewer>
                    <el-icon @click.stop><View /></el-icon>
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
              <el-tag :type="statusMap[scope.row.status]?.type" effect="light" style="cursor: help">
                {{ statusMap[scope.row.status]?.label }}
              </el-tag>
            </el-tooltip>
            <el-tag v-else :type="statusMap[scope.row.status]?.type" effect="light">
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

        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.status === 0">
              <el-button link type="success" :icon="Check" @click="handleAudit(scope.row, 1)">通过</el-button>
              <el-button link type="danger" :icon="Close" @click="handleAudit(scope.row, 2)">驳回</el-button>
            </template>
            <el-button link type="primary" :icon="View" @click="handleDetail(scope.row)">详情</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
          width="650px"
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
          <el-descriptions-item label-class-name="desc-label" :span="2">
            <template #label>
              <el-icon><Picture /></el-icon>
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
        </el-descriptions>

        <div class="opinion-section">
          <div class="opinion-title">
            <el-icon>
              <EditPen/>
            </el-icon>
            审核意见
          </div>
          <div class="opinion-content">
            {{ enterpriseDetail.auditOpinion || '暂无审核备注信息' }}
          </div>
        </div>

        <template #footer>
          <el-button type="primary" plain @click="detailVisible = false">确 认</el-button>
        </template>
      </el-dialog>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '修改企业信息' : '提交入驻申请'" width="750px"
               destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="padding: 10px">

        <el-divider content-position="left">
          <el-icon>
            <InfoFilled/>
          </el-icon>
          工商基础信息
        </el-divider>
        <el-form-item label="企业全称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请按营业执照名称填写"/>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="18位社会信用代码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法人代表" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="姓名"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="注册资本" prop="registeredCapital">
              <el-input-number v-model="form.registeredCapital" :precision="2" :step="100" :min="0"
                               style="width: 100%"/>
              <div class="form-tip">单位：万元</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-input v-model="form.industry" placeholder="如：人工智能、生物医药"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          <el-icon>
            <Location/>
          </el-icon>
          园区入驻意向
        </el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="意向楼宇" prop="buildingNo">
              <el-input v-model="form.buildingNo" placeholder="例如：A座-808"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租用面积" prop="rentArea">
              <el-input-number v-model="form.rentArea" :precision="2" :step="10" :min="0" style="width: 100%"/>
              <div class="form-tip">单位：平方米 (㎡)</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          <el-icon>
            <Phone/>
          </el-icon>
          联系方式
        </el-divider>
        <el-row :gutter="20">
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

        <el-form-item label="营业执照附件" prop="licenseUrl">
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
      </el-form>

      <template #footer>
        <div style="padding: 10px">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">提交申请</el-button>
        </div>
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
  Picture
} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'
import {uploadFile} from "@/utils/upload.js";

// 1. 状态映射配置 (严格匹配 tinyint 0-3)
const statusMap = {
  0: {label: '待审核', type: 'warning'},
  1: {label: '已入驻', type: 'success'},
  2: {label: '已驳回', type: 'danger'},
  3: {label: '已迁出', type: 'info'}
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

// 定义详情弹窗控制
const detailVisible = ref(false)
const enterpriseDetail = ref({})

// 3. 查询参数 (对应 EnterprisePageQueryDTO)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  companyName: '',
  industry: '',
  buildingNo: '',
  status: null
})

// 4. 表单模型 (对应 EnterpriseDTO)
const form = ref({
  id: null,
  companyName: '',
  creditCode: '',
  legalPerson: '',
  registeredCapital: 0,
  buildingNo: '',
  rentArea: 0,
  industry: '',
  contactPerson: '',
  contactPhone: '',
  licenseUrl: '',
})

// 5. 校验规则
const rules = {
  companyName: [{required: true, message: '企业全称不能为空', trigger: 'blur'}],
  creditCode: [
    {required: true, message: '信用代码不能为空', trigger: 'blur'},
    {pattern: /^[0-9A-Z]{18}$/, message: '请输入18位大写字母或数字', trigger: 'blur'}
  ],
  legalPerson: [{required: true, message: '法人代表不能为空', trigger: 'blur'}],
  contactPerson: [{required: true, message: '联系人不能为空', trigger: 'blur'}],
  contactPhone: [{required: true, message: '联系电话不能为空', trigger: 'blur'}],
  licenseUrl: [{required: true, message: '请上传营业执照附件', trigger: 'change'}]
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
    buildingNo: '', rentArea: 0, industry: '', contactPerson: '', contactPhone: '', licenseUrl: ''
  }
  dialogVisible.value = true
}

// --- 营业执照上传前校验 ---
const beforeLicenseUpload = (rawFile) => {
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
  if (!allowedTypes.includes(rawFile.type)) {
    ElMessage.error('上传营业执照图片只能是 JPG/JPEG/PNG/GIF 格式!')
    return false
  }
  const isLt5M = rawFile.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.error('上传营业执照图片大小不能超过 5MB!')
    return false
  }
  return true
}

// --- 核心：处理上传逻辑 ---
const handleImageUpload = async (options) => {
  try {
    // 调用你 utils/upload.js 中的上传工具
    const url = await uploadFile(options.file)
    // 赋值给表单，这样预览图 <img> 标签就能通过 :src="form.licenseUrl" 显示出来
    form.value.licenseUrl = url
    formRef.value.validateField('licenseUrl')
    ElMessage.success('执照上传成功')
  } catch (error) {
    ElMessage.error('上传失败，请稍后重试')
  }
}

// 预览大图
const handlePreview = () => {
  if (!form.value.licenseUrl) return
  showViewer.value = true
}

// 删除图片
const handleRemove = () => {
  ElMessageBox.confirm('确定要删除已上传的执照图片吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    form.value.licenseUrl = ''
    ElMessage.success('已移除')
  })
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.value.id) {
          await enterpriseApi.update(form.value)
          ElMessage.success('企业信息更新成功')
        } else {
          await enterpriseApi.apply(form.value)
          ElMessage.success('入驻申请已提交，请耐心等待系统审核')
        }
        dialogVisible.value = false
        getList()
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleAudit = (row, status) => {
  const isPass = status === 1
  const title = isPass ? '通过申请' : '驳回申请'

  if (isPass) {
    ElMessageBox.confirm(`确定要批准企业 "${row.companyName}" 入驻园区吗？`, title, {
      confirmButtonText: '确定',
      type: 'success'
    }).then(async () => {
      await enterpriseApi.audit({id: row.id, status: 1})
      ElMessage.success('操作成功，已批准入驻')
      getList()
    }).catch(() => {
    })
  } else {
    ElMessageBox.prompt('请填写驳回原因：', title, {
      confirmButtonText: '确认驳回',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '驳回原因不能为空',
      type: 'warning'
    }).then(async ({value}) => {
      await enterpriseApi.audit({id: row.id, status: 2, auditOpinion: value})
      ElMessage.success('已驳回该申请')
      getList()
    }).catch(() => {
    })
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要彻底删除 "${row.companyName}" 的档案吗？`, '警告', {type: 'error'})
      .then(async () => {
        await enterpriseApi.delete([row.id])
        ElMessage.success('删除成功')
        getList()
      }).catch(() => {
  })
}

const handleBatchDelete = () => {
  const ids = multipleSelection.value.map(item => item.id)
  ElMessageBox.confirm(`确定要批量删除选中的 ${ids.length} 家企业记录吗？`, '重大警告', {type: 'error'})
      .then(async () => {
        await enterpriseApi.delete(ids)
        ElMessage.success('批量删除成功')
        getList()
      }).catch(() => {
  })
}

// 详情展示弹窗
const handleDetail = (row) => {
  enterpriseDetail.value = row
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.enterprise-container {
  min-height: calc(100vh - 100px);
}

.search-card, .table-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
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
  margin-top: 20px;
  border: 1px dashed #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  background-color: #fffaf4;
}

.opinion-title {
  display: flex;
  align-items: center;
  font-weight: normal;
  color: #e6a23c;
  margin-bottom: 8px;
  font-size: 14px;
}

.opinion-title .el-icon {
  margin-right: 5px;
}

.opinion-content {
  font-size: 13px;
  line-height: 1.6;
  color: #666;
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
  background-color: #f9f9f9;
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
</style>