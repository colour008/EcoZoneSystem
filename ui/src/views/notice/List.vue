<template>
  <div class="notice-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <el-form :model="queryParams" inline class="search-form">
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题" clearable style="width: 200px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable style="width: 130px">
            <el-option v-for="(val, key) in typeMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 120px">
            <el-option v-for="(val, key) in statusMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="queryParams.visibility" placeholder="请选择" clearable style="width: 130px">
            <el-option v-for="(val, key) in visibilityMap" :key="key" :label="val.label" :value="Number(key)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="queryParams.publisherName" placeholder="请输入发布人" clearable style="width: 150px"
                    @keyup.enter="handleQuery"/>
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
        <el-form-item class="action-bar">
          <el-button type="success" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新建公告
          </el-button>
        </el-form-item>
      </el-form>
    </div>


    <!-- 卡片列表 -->
    <div class="card-list" v-loading="loading">
      <el-empty v-if="noticeList.length === 0 && !loading" :image-size="80" description="暂无公告数据"/>

      <el-row :gutter="20">
        <el-col
            v-for="item in noticeList"
            :key="item.id"
            :xs="24" :sm="12" :md="8" :lg="6" :xl="6"
            class="card-col"
        >
          <el-card class="notice-card" shadow="hover" :body-style="{ padding: '0px' }">
            <!-- 封面图 -->
            <div v-if="item.coverUrl" class="card-cover">
              <el-image :src="item.coverUrl" fit="cover" class="cover-img"/>
            </div>

            <div v-else class="card-cover no-cover">
              <div class="no-cover-content">
                <el-icon class="no-cover-icon">
                  <Document/>
                </el-icon>
                <span class="no-cover-text">{{ typeMap[item.type]?.label || '暂无分类' }}</span>
              </div>
            </div>

            <!-- 卡片内容 -->
            <div class="card-content">
              <div class="card-header">
                <el-tag :type="typeMap[item.type]?.type" size="small" effect="dark" class="type-tag">
                  {{ typeMap[item.type]?.label }}
                </el-tag>
                <el-tag :type="statusMap[item.status]?.type" size="small" effect="plain">
                  {{ statusMap[item.status]?.label }}
                </el-tag>
              </div>

              <div class="card-title" :title="item.title">{{ item.title }}</div>
              <div class="card-summary" :title="item.summary">{{ item.summary || '暂无摘要' }}</div>

              <div class="card-meta">
                <div class="meta-item">
                  <el-icon>
                    <User/>
                  </el-icon>
                  <span>{{ item.publisherName || '未知' }}</span>
                </div>
                <div class="meta-item">
                  <el-icon>
                    <View/>
                  </el-icon>
                  <span>{{ item.viewCount }} 阅读</span>
                </div>
                <div class="meta-item">
                  <el-icon>
                    <Calendar/>
                  </el-icon>
                  <span>{{ item.publishTime ? item.publishTime.substring(0, 10) : '未发布' }}</span>
                </div>
              </div>

              <div class="card-actions">
                <el-button link type="primary" size="small" @click="handleDetail(item)">详情</el-button>

                <template v-if="item.status === 0 || item.status === 2">
                  <el-button link type="warning" size="small" @click="handleEdit(item)">编辑</el-button>
                </template>

                <template v-if="item.status === 0">
                  <el-button link type="success" size="small" @click="handlePublish(item)">发布</el-button>
                </template>

                <template v-if="item.status === 1">
                  <el-button link type="danger" size="small" @click="handleRecall(item)">撤回</el-button>
                </template>

                <template v-if="item.status === 2">
                  <el-button link type="success" size="small" @click="handlePublish(item)">重新发布</el-button>
                </template>

                <template v-if="item.status !== 3">
                  <el-button link type="info" size="small" @click="handleArchive(item)">归档</el-button>
                </template>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 分页 -->
    <div class="pagination">
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

    <!-- 新增/编辑对话框 -->
    <el-drawer
        v-model="dialogVisible"
        :title="form.id ? '编辑公告' : '新建公告'"
        size="800px"
        direction="rtl"
        destroy-on-close
        class="notice-drawer"
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
          label-position="top"
          style="padding: 0 20px"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="公告标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入公告标题" clearable/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="公告类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
                <el-option v-for="(val, key) in typeMap" :key="key" :label="val.label" :value="Number(key)"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="公告摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="请输入公告摘要"/>
        </el-form-item>

        <el-form-item label="封面图片" prop="coverUrl">
          <div class="cover-upload-wrapper">
            <el-upload
                class="cover-uploader"
                action="#"
                :show-file-list="false"
                :http-request="handleCoverUpload"
                :before-upload="beforeCoverUpload"
                accept=".jpg,.jpeg,.png,.gif"
            >
              <div v-if="form.coverUrl" class="cover-preview-container">
                <img :src="form.coverUrl" class="cover-preview-img" alt="封面"/>
                <div class="cover-actions">
                  <span class="cover-action-item" @click.stop="handleCoverPreview">
                    <el-icon><ZoomIn/></el-icon>
                    <span>预览</span>
                  </span>
                  <span class="cover-action-item" @click.stop="handleCoverRemove">
                    <el-icon><Delete/></el-icon>
                    <span>删除</span>
                  </span>
                </div>
              </div>
              <div v-else class="cover-placeholder">
                <el-icon class="cover-icon">
                  <Plus/>
                </el-icon>
                <span class="cover-text">上传封面</span>
              </div>
            </el-upload>
            <div class="upload-tip">建议尺寸 750x422，支持 JPG/PNG，小于 5MB</div>
          </div>
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <div class="editor-container">
            <Editor
                v-if="dialogVisible"
                v-model="form.content"
                placeholder="请输入公告正文内容..."
            />
          </div>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="可见性" prop="visibility">
              <el-select v-model="form.visibility" placeholder="请选择" style="width: 100%">
                <el-option v-for="(val, key) in visibilityMap" :key="key" :label="val.label" :value="Number(key)"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="推送范围" prop="targetScope">
              <el-select v-model="form.targetScope" placeholder="请选择" style="width: 100%">
                <el-option v-for="(val, key) in targetScopeMap" :key="key" :label="val.label" :value="Number(key)"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.targetScope === 1">
            <el-form-item label="指定接收用户" prop="targetUserIds">
              <el-select
                  v-model="form.targetUserIds"
                  multiple
                  filterable
                  clearable
                  collapse-tags
                  collapse-tags-tooltip
                  :max-collapse-tags="20"
                  placeholder="输入姓名搜索或直接选择"
                  popper-class="user-grid-popper"
                  style="width: 100%"
              >
                <template #prefix>
                  <el-icon>
                    <User/>
                  </el-icon>
                </template>

                <el-option
                    v-for="user in userOptions"
                    :key="user.id"
                    :label="user.name"
                    :value="user.id"
                >
                  <div class="user-option-item">
                    <span>{{ user.name }}</span>
                  </div>
                </el-option>
              </el-select>
              <div class="input-tip">选择后，该公告仅选中的用户在登录后可见</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="dialogVisible = false" round>取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm" round>
            {{ form.status === 2 ? '保存修改' : '保存草稿' }}
          </el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 详情对话框 -->
    <el-drawer
        v-model="detailVisible"
        title="公告详情"
        size="700px"
        direction="rtl"
        destroy-on-close
    >
      <div v-if="noticeDetail.id" class="detail-content drawer-detail">
        <div class="detail-header">
          <h2>{{ noticeDetail.title }}</h2>
          <div class="detail-meta">
            <el-tag :type="typeMap[noticeDetail.type]?.type" size="small" effect="dark">
              {{ typeMap[noticeDetail.type]?.label }}
            </el-tag>
            <el-tag :type="statusMap[noticeDetail.status]?.type" size="small" effect="plain">
              {{ statusMap[noticeDetail.status]?.label }}
            </el-tag>
            <span class="meta-text">
              <el-icon><User/></el-icon>
              {{ noticeDetail.publisherName || '未知' }}
            </span>
            <span class="meta-text">
              <el-icon><Calendar/></el-icon>
              {{ noticeDetail.publishTime || '未发布' }}
            </span>
            <span class="meta-text">
              <el-icon><View/></el-icon>
              {{ noticeDetail.viewCount }} 阅读
            </span>
          </div>
        </div>

        <div v-if="noticeDetail.coverUrl" class="detail-cover">
          <el-image :src="noticeDetail.coverUrl" fit="contain" class="detail-cover-img"/>
        </div>

        <div class="detail-body">
          <div class="rich-content-view" v-html="noticeDetail.content || '暂无内容'"></div>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" plain @click="detailVisible = false">关 闭</el-button>
      </template>
    </el-drawer>

    <!-- 封面预览器 -->
    <el-image-viewer
        v-if="showCoverViewer"
        :url-list="[form.coverUrl]"
        @close="showCoverViewer = false"
    />
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Search, Refresh, Plus, View, User, Calendar, Document, ZoomIn, Delete
} from '@element-plus/icons-vue'
import noticeApi from '@/api/notice'
import Editor from '@/components/WangEditor/index.vue'
import {uploadFile} from "@/utils/upload.js";

// 存储用户列表
const userOptions = ref([])

// 获取活跃用户列表
const getUserList = async () => {
  try {
    const res = await noticeApi.getActiveUsers()
    if (res.data && Array.isArray(res.data)) {
      userOptions.value = res.data.map(user => ({
        id: user.id,
        name: user.realName
      }))
    }
  } catch (e) {
    console.error('获取用户列表失败', e)
    ElMessage.error('加载用户列表失败')
  }
}

// 状态映射
const typeMap = {
  1: {label: '政策推送', type: 'primary'},
  2: {label: '园区动态', type: 'success'},
  3: {label: '通知公告', type: 'warning'},
  4: {label: '内部通报', type: 'danger'}
}

const statusMap = {
  0: {label: '草稿', type: 'info'},
  1: {label: '已发布', type: 'success'},
  2: {label: '已撤回', type: 'warning'},
  3: {label: '已归档', type: 'info'}
}

const visibilityMap = {
  0: {label: '仅内部(B端)', type: 'info'},
  1: {label: '公开可见(C端)', type: 'success'}
}

const targetScopeMap = {
  0: {label: '全部用户', type: 'success'},
  1: {label: '指定用户', type: 'warning'}
}

// 响应式变量
const loading = ref(false)
const submitLoading = ref(false)
const noticeList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const showCoverViewer = ref(false)
const noticeDetail = ref({})

const queryParams = ref({
  pageNum: 1,
  pageSize: 8,
  title: '',
  type: null,
  status: null,
  visibility: null,
  publisherName: ''
})

// 表单模型
const form = ref({
  id: null,
  title: '',
  summary: '',
  coverUrl: '',
  content: '',
  type: 3,
  visibility: 0,
  targetScope: 0,
  targetUserIds: []
})

// 校验规则
const rules = {
  title: [{required: true, message: '公告标题不能为空', trigger: 'blur'}],
  type: [{required: true, message: '请选择公告类型', trigger: 'change'}],
  content: [{required: true, message: '公告内容不能为空', trigger: 'blur'}]
}

// 核心业务方法
const getList = async () => {
  loading.value = true
  try {
    const res = await noticeApi.page(queryParams.value)
    noticeList.value = res.data.records || []
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
  queryParams.value = {
    pageNum: 1, pageSize: 8, title: '', type: null, status: null, visibility: null, publisherName: ''
  }
  handleQuery()
}

const handleAdd = () => {
  form.value = {
    id: null,
    title: '',
    summary: '',
    coverUrl: '',
    content: '',
    type: 3,
    visibility: 0,
    targetScope: 0,
    targetUserIds: []
  }
  if (formRef.value) formRef.value.clearValidate()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  loading.value = true
  try {
    const res = await noticeApi.getDetail(row.id)
    if (res.data) {
      // 确保如果是字符串则转为数组，如果是 null 则转为空数组
      if (typeof res.data.targetUserIds === 'string') {
        res.data.targetUserIds = res.data.targetUserIds.split(',').map(Number)
      }
      form.value = {...res.data}
      dialogVisible.value = true
    }
  } catch (e) {
    ElMessage.error('获取详情失败')
  } finally {
    loading.value = false
  }
}

const handleDetail = async (row) => {
  try {
    const res = await noticeApi.getDetail(row.id)
    noticeDetail.value = res.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

const handlePublish = (row) => {
  ElMessageBox.confirm(`确认发布公告「${row.title}」吗？`, '发布确认', {
    type: 'success'
  }).then(async () => {
    try {
      await noticeApi.publish(row.id)
      ElMessage.success('发布成功')
      getList()
    } catch (e) {
      ElMessage.error('发布失败')
    }
  })
}

const handleRecall = (row) => {
  ElMessageBox.confirm(`确认撤回公告「${row.title}」吗？撤回后用户将不可见。`, '撤回确认', {
    type: 'warning'
  }).then(async () => {
    try {
      await noticeApi.recall(row.id)
      ElMessage.success('已撤回')
      getList()
    } catch (e) {
      ElMessage.error('撤回失败')
    }
  })
}

const handleArchive = (row) => {
  ElMessageBox.confirm(`确认归档公告「${row.title}」吗？`, '归档确认', {
    type: 'info'
  }).then(async () => {
    try {
      await noticeApi.archive(row.id)
      ElMessage.success('已归档')
      getList()
    } catch (e) {
      ElMessage.error('归档失败')
    }
  })
}

// 封面上传
const beforeCoverUpload = (rawFile) => {
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

const handleCoverUpload = async (options) => {
  try {
    const url = await uploadFile(options.file)
    form.value.coverUrl = url
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleCoverPreview = () => showCoverViewer.value = true
const handleCoverRemove = () => {
  ElMessageBox.confirm('确定删除封面图片？', '提示', {type: 'warning'}).then(() => {
    form.value.coverUrl = ''
    ElMessage.success('已移除')
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await noticeApi.save(form.value)
      ElMessage.success('保存成功')
      dialogVisible.value = false
      getList()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  getList()
  getUserList() // 页面初始化时加载用户信息
})
</script>

<style scoped>
.notice-container {
  min-height: calc(100vh - 100px);
}

.search-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px 20px 0 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.action-bar {
  margin-bottom: 20px;
}

.card-list {
  min-height: 400px;
}

.card-col {
  margin-bottom: 20px;
}

.notice-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.notice-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.card-cover {
  width: 100%;
  height: 160px; /* ✅ 微调封面高度，适配5列布局 */
  overflow: hidden;
  position: relative;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.notice-card:hover .cover-img {
  transform: scale(1.05);
}

.no-cover {
  background: linear-gradient(135deg, #619def 0%, #4d79ba 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-cover-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.no-cover-icon {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
}

.no-cover-text {
  font-size: 24px;
  color: #ffffff;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.card-content {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
}

.type-tag {
  font-weight: 500;
}

.card-title {
  height: 44px; /* 必须给固定高度 */
  line-clamp: 2;
  font-size: 15px; /* ✅ 微调字体大小 */
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-summary {
  height: 36px; /* 必须给固定高度 */
  line-clamp: 2;
  font-size: 12px; /* ✅ 微调字体大小 */
  color: #909399;
  line-height: 1.5;
  margin-bottom: 12px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* ✅ 减少摘要行数，适配5列布局 */
  -webkit-box-orient: vertical;
}

.card-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  flex-wrap: wrap; /* ✅ 允许换行，避免内容溢出 */
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px; /* ✅ 微调字体大小 */
  color: #909399;
}

.card-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 3px;
  flex-wrap: wrap; /* ✅ 允许按钮换行 */
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 表单弹窗样式 */
.notice-form-dialog {
  border-radius: 16px;
}

.cover-upload-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.cover-uploader {
  width: 200px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  background-color: #fafafa;
  transition: all 0.3s;
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column; /* 让加号和文字上下排列 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

.cover-uploader:hover {
  border-color: #409EFF;
  background-color: #f5f7fa;
}

.cover-preview-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.cover-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-actions {
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
}

.cover-preview-container:hover .cover-actions {
  opacity: 1;
}

.cover-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  cursor: pointer;
  transition: color 0.2s;
}

.cover-action-item:hover {
  color: #409EFF;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.cover-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.cover-text {
  font-size: 12px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 10px;
}


/* 详情弹窗样式 */
.notice-detail-dialog {
  border-radius: 16px;
}

.detail-content {
  padding: 10px 20px;
}

.detail-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-header h2 {
  margin: 0 0 15px 0;
  font-size: 24px;
  color: #303133;
}

.detail-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.meta-text {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-cover {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
}

.detail-cover-img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
}

.rich-content-view {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}

.rich-content-view :deep(img) {
  max-width: 100% !important;
  height: auto !important;
  display: block;
  margin: 15px 0;
}

.rich-content-view :deep(p) {
  margin: 10px 0;
}

/* 抽屉整体内边距调整 */
:deep(.el-drawer__body) {
  padding: 20px 30px;
}

/* 抽屉底部按钮样式 */
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 编辑器容器高度控制，确保在抽屉里不溢出 */
.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 400px;
}

/* 详情页在抽屉里的间距优化 */
.drawer-detail {
  padding: 0;
}

/* 详情页在抽屉里的间距优化 */
.drawer-detail {
  padding: 0;
}

.drawer-detail .detail-header h2 {
  text-align: left; /* 抽屉内建议左对齐 */
  font-size: 20px;
  margin-bottom: 12px;
}

.drawer-detail .detail-meta {
  justify-content: flex-start; /* 抽屉内建议左对齐 */
  gap: 20px;
}

.drawer-detail .detail-cover-img {
  width: 100%;
  border-radius: 8px;
  margin: 20px 0;
}

/* 这种右侧滑出的交互，通常不需要额外的圆角，保持干练 */
:deep(.el-drawer) {
  box-shadow: -5px 0 25px rgba(0, 0, 0, 0.1);
}

/* 用户选择器选项布局 */
.user-option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-id-tag {
  font-size: 11px;
  color: #a8abb2;
  background: #f4f4f5;
  padding: 0 4px;
  border-radius: 4px;
  margin-left: 10px;
}

/* 调整 Select 选中的标签样式（Element Plus 默认偏大） */
:deep(.el-select .el-tag) {
  background-color: #f0f7ff;
  color: #409eff;
  border: none;
}

/* 输入框下方的辅助文案 */
.input-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.2;
}

/* 优化搜索时的空状态 */
:deep(.el-select-dropdown__empty) {
  padding: 20px 0;
  color: #c0c4cc;
  font-size: 13px;
}

</style>

/* 注意：这里不要加 scoped */
<style>
/* 1. 确保弹窗容器宽度足够容纳 4 列 */
.user-grid-popper .el-select-dropdown__wrap {
  max-height: 300px;
}

/* 2. 核心：将列表容器设为 Flex 布局并允许换行 */
.user-grid-popper .el-select-dropdown__list {
  display: flex !important;
  flex-wrap: wrap !important;
  padding: 10px !important;
  width: 680px !important;
}

/* 3. 每个选项占 25%，实现每行5 个 */
.user-grid-popper .el-select-dropdown__item {
  width: 20% !important;
  padding: 0 5px !important;
  text-align: center;
  height: 34px;
  line-height: 34px;
  box-sizing: border-box;
}

/* 4. 这里的选中状态和悬停效果 */
.user-grid-popper .el-select-dropdown__item.selected {
  background-color: #ecf5ff !important;
  color: #409eff !important;
  font-weight: bold;
}

/* 5. 隐藏多选自带的对勾图标，腾出空间给文字 */
.user-grid-popper .el-select-dropdown__item.selected::after {
  display: none !important;
}
</style>