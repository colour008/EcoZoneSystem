<template>
  <div class="showcase-wrapper">
    <div class="hero-section">
      <div class="hero-bg-overlay"></div>
      <div class="hero-content">
        <h1 class="title">园区企业风采</h1>
        <p class="subtitle">汇聚行业精英，共创商业价值</p>
        <div class="search-box">
          <el-input
              v-model="queryParams.companyName"
              placeholder="搜索您感兴趣的企业..."
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
          >
            <template #append>
              <el-button icon="Search" @click="handleSearch" class="search-btn"/>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <div class="main-container">
      <el-skeleton :loading="loading" animated :count="6">
        <template #default>
          <div v-if="enterpriseList.length === 0" class="empty-state">
            <el-empty description="暂无企业入驻信息"/>
          </div>

          <el-row :gutter="24" class="enterprise-grid" v-else>
            <el-col
                v-for="ent in enterpriseList"
                :key="ent.id"
                :xs="24" :sm="12" :md="12" :lg="8" :xl="8"
            >
              <el-card class="ent-card" shadow="hover" @click="openDetail(ent)">
                <div class="card-media">
                  <el-carousel
                      v-if="ent.mediaList && ent.mediaList.length > 0"
                      height="220px"
                      arrow="hover"
                      :autoplay="true"
                      :interval="4000"
                      indicator-position="none"
                  >
                    <el-carousel-item v-for="(media, index) in ent.mediaList" :key="index">
                      <div v-if="media.type === 'video'" class="video-container">
                        <video
                            :src="media.url"
                            class="media-video"
                            muted
                            loop
                            playsinline
                            onmouseover="this.play()"
                            onmouseout="this.pause()"
                        ></video>
                        <div class="video-play-icon">
                          <el-icon>
                            <VideoPlay/>
                          </el-icon>
                        </div>
                      </div>

                      <el-image
                          v-else-if="media.type === 'image'"
                          :src="media.url"
                          fit="cover"
                          class="media-img"
                      >
                        <template #error>
                          <div class="image-slot">
                            <el-icon>
                              <Picture/>
                            </el-icon>
                          </div>
                        </template>
                      </el-image>
                    </el-carousel-item>
                  </el-carousel>
                  <div v-else class="media-placeholder">
                    <div class="placeholder-content">
                      <el-icon class="placeholder-icon">
                        <OfficeBuilding/>
                      </el-icon>
                      <span>{{ ent.companyName }}</span>
                    </div>
                  </div>
                  <div class="industry-badge" v-if="ent.industry">
                    {{ ent.industry }}
                  </div>
                </div>

                <div class="card-body">
                  <h3 class="ent-name line-clamp-1" :title="ent.companyName">
                    {{ ent.companyName }}
                  </h3>
                  <p class="ent-desc line-clamp-2">
                    {{ ent.plainText || '该企业暂未留下详细简介...' }}
                  </p>

                  <el-divider border-style="dashed"/>

                  <div class="ent-meta">
                    <div class="meta-item">
                      <el-icon>
                        <Location/>
                      </el-icon>
                      <span>入驻楼宇：{{ ent.buildingNo || '暂无' }}</span>
                    </div>
                    <div class="meta-item">
                      <el-icon>
                        <User/>
                      </el-icon>
                      <span>联系人：{{ ent.contactPerson || '暂无' }}</span>
                    </div>
                    <div class="meta-item highlight-phone">
                      <el-icon>
                        <Phone/>
                      </el-icon>
                      <span>联系电话：{{ ent.contactPhone || '暂无' }}</span>
                    </div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
                v-model:current-page="queryParams.pageNum"
                v-model:page-size="queryParams.pageSize"
                :page-sizes="[6, 12, 24]"
                background
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="getList"
                @current-change="getList"
            />
          </div>
        </template>
      </el-skeleton>
    </div>
    <el-dialog
        v-model="detailVisible"
        :title="currentEnt?.companyName"
        width="60%"
        destroy-on-close
        class="detail-dialog"
        align-center
    >
      <div class="dialog-header-row">
        <div class="detail-header-info">
          <span class="info-item primary"><el-icon><CollectionTag/></el-icon>{{ currentEnt?.industry }}</span>
          <span class="info-item success">
            <el-icon><Location/></el-icon> {{ currentEnt?.buildingNo || '暂无地址' }}
          </span>
          <span class="info-item info">
            <el-icon><User/></el-icon> {{ currentEnt?.contactPerson || '暂无联系人' }}
          </span>
          <span class="info-item danger">
            <el-icon><Phone/></el-icon> {{ currentEnt?.contactPhone || '暂无电话' }}
          </span>
        </div>

        <div class="meta-right">
          <span class="action-btn" @click.stop="handlePrint">
          <el-icon><Printer/></el-icon>打印
          </span>
        </div>
      </div>
      <el-divider style="margin:10px 0 !important; "/>

      <div
          class="rich-text-container"
          v-html="currentEnt?.introduction || '<p style=\'text-align:center;color:#999\'>暂无详细介绍</p>'"
      ></div>
    </el-dialog>
  </div>
</template>

<script setup>
import html2pdf from 'html2pdf.js'
import {ref, onMounted, nextTick} from 'vue'
import {
  Search,
  Location,
  User,
  Phone,
  Picture,
  OfficeBuilding,
  VideoPlay,
  Printer,
  CollectionTag
} from '@element-plus/icons-vue'
import enterpriseApi from '@/api/enterprise'

const loading = ref(false)
const total = ref(0)
const enterpriseList = ref([])
const detailVisible = ref(false)
const currentEnt = ref(null)

const queryParams = ref({
  pageNum: 1,
  pageSize: 6,
  companyName: ''
})

// 打印功能
const handlePrint = async () => {
  await nextTick()

  const ent = currentEnt.value
  if (!ent) return

  const printContainer = document.createElement('div')

  printContainer.innerHTML = `
    <div class="print-wrapper">

      <h1 class="title">${ent.companyName || ''}</h1>

    <div class="meta">
        <span class="info-item primary">行业：${ent.industry || '-'}</span>
        <span class="info-item success">楼宇：${ent.buildingNo || '-'}</span>
        <span class="info-item info">联系人：${ent.contactPerson || '-'}</span>
        <span class="info-item danger">电话：${ent.contactPhone || '-'}</span>
    </div>

      <hr />

     <div class="content">
        ${ent.introduction || ''}
     </div>

    </div>

    <style>
      .print-wrapper{
        padding:20px;
        font-family:'PingFang SC',Arial,serif;
        color:#333;
      }

      .title{
        text-align:center;
        margin-bottom:10px;
      }

      .meta{
        display:flex;
        flex-wrap:wrap;
        gap:12px;
        font-size:13px;
        margin-bottom:12px;
      }

      .content{
        line-height:1.8;
        font-size:14px;
      }

      .content img{
        max-width:100%;
        display:block;
        margin:12px auto;
        border-radius:6px;
      }

      .content video{
        max-width:100%;
        display:block;
        margin:12px auto;
        border-radius:6px;
      }

      .info-item{
        display:inline-flex;
        align-items:center;
        padding:4px 10px;
        border-radius:5px;
        font-size:13px;
      }

      .info-item.primary{ background:#e8f3ff !important; color:#1989fa !important; }
      .info-item.success{ background:#e6f7ee !important; color:#00b42a !important; }
      .info-item.info{ background:#f4f6f8 !important; color:#4e5969 !important; }
      .info-item.danger{ background:#fef0f0 !important; color:#f56c6c !important; }

    </style>
  `

  const opt = {
    margin: 0.4,

    filename: `${ent.companyName || '企业风采'}.pdf`,

    image: {
      type: 'jpeg',
      quality: 1
    },

    html2canvas: {
      scale: 4,
      dpi: 300,
      letterRendering: true,
      useCORS: true,
      allowTaint: true,
      backgroundColor: '#ffffff',
      logging: false,
      scrollY: 0
    },

    jsPDF: {
      unit: 'in',
      format: 'a4',
      orientation: 'portrait'
    },

    pagebreak: {
      mode: ['css', 'legacy']
    }
  }

  await html2pdf().set(opt).from(printContainer).toPdf().get('pdf').then(pdf => {
    window.open(pdf.output('bloburl'))
  })
}

// 工具函数：从 HTML 中提取视频和图片 URL 数组
const extractMediaFromHtml = (htmlStr) => {
  if (!htmlStr) return []
  const media = []

  // 1. 提取视频：匹配 <source> 标签中的 src
  // 使用 [^>]* 确保能跳过中间的 poster、controls 等属性
  const videoRegex = /<source[^>]+src=["']([^"']+)["']/gi
  const videoMatches = [...htmlStr.matchAll(videoRegex)]
  videoMatches.forEach(match => {
    media.push({type: 'video', url: match[1]})
  })

  // 2. 提取图片：匹配 <img> 标签中的 src
  const imgRegex = /<img[^>]+src=["']([^"']+)["']/gi
  const imgMatches = [...htmlStr.matchAll(imgRegex)]
  imgMatches.forEach(match => {
    media.push({type: 'image', url: match[1]})
  })

  return media
}

// 工具函数：剔除 HTML 标签，获取纯文本摘要
const extractPlainText = (htmlStr) => {
  if (!htmlStr) return ''
  // 替换掉所有 html 标签、换行符和空格实体
  let text = htmlStr.replace(/<[^>]+>/g, '')
  text = text.replace(/&nbsp;/ig, ' ')
  text = text.replace(/\s+/g, ' ')
  return text.trim()
}

const getList = async () => {
  loading.value = true
  try {
    const res = await enterpriseApi.getEnterpriseShowPage(queryParams.value)

    // 关键点：后端返回的数组字段名是 records
    const rawList = res.data?.records || []

    // 数据预处理
    enterpriseList.value = rawList.map(item => {
      const htmlContent = item.introduction || ''
      return {
        ...item,
        // 1. 提取图片和视频 URL 数组
        mediaList: extractMediaFromHtml(htmlContent),
        // 2. 提取纯文本摘要
        plainText: extractPlainText(htmlContent),
        // 3. 额外检测：是否包含视频 (可选，用于在卡片上展示视频图标)
        hasVideo: htmlContent.includes('<video')
      }
    })

    // 关键点：总条数对应 res.data.total
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取企业风采失败', error)
    enterpriseList.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.value.pageNum = 1
  getList()
}

const openDetail = (ent) => {
  currentEnt.value = ent
  detailVisible.value = true
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
/* ====== 基础布局与变量 ====== */
.showcase-wrapper {
  min-height: calc(100vh - 100px);
  background-color: #f4f7f9;
}

/* ====== 顶部 Hero 区 ====== */
.hero-section {
  width: 100%;
  height: 300px;
  background: url('@/assets/banner2.jpg') no-repeat center center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: white;
  margin-bottom: -40px; /* 让内容卡片向上浮动交叠 */
}

.hero-bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(28, 28, 28, 0.41) 0%, rgba(0, 0, 0, 0.63) 100%);
  z-index: 1;
}

.hero-content {
  z-index: 2;
  width: 100%;
  max-width: 600px;
  padding: 0 20px;
}

.hero-content .title {
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.hero-content .subtitle {
  font-size: 16px;
  color: #e0e6ed;
  margin-bottom: 30px;
}

.search-box {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  overflow: hidden;
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-radius: 8px 0 0 8px;
  padding: 4px 15px;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset, 0 4px 12px rgba(64, 158, 255, 0.1);
}

.search-input :deep(.el-input-group__append) {
  background-color: #409eff;
  border: none;
  border-radius: 0 8px 8px 0;
  padding: 0;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.search-input :deep(.search-btn) {
  color: #ffffff;
  border-radius: 0 8px 8px 0;
  height: 100%;
  border: 1px solid #ffffff;
  margin: 0;
  padding: 0 20px;
  font-weight: 500;
}

.search-input :deep(.search-btn:hover) {
  background-color: #66b1ff;
}


/* ====== 主体内容区 ====== */
.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 40px;
  position: relative;
  z-index: 5;
}

.enterprise-grid {
  margin-top: 20px;
}

/* ====== 卡片高级样式 ====== */
.ent-card {
  border: none;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  background: #fff;
  transform: translateZ(0);
  backface-visibility: hidden;
  -webkit-mask-image: -webkit-radial-gradient(white, black);
}

.ent-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.12), 0 10px 10px rgba(0, 0, 0, 0.08) !important;
}

:deep(.ent-card .el-card__body) {
  padding: 0;
}

/* 媒体展示区 (轮播图/占位图) */
.card-media {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background-color: #f5f7fa;
}

.media-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.ent-card:hover .media-img {
  transform: scale(1.05); /* 悬浮图片微放大效果 */
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

/* 无图片时的默认占位 */
.media-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg, #fdfbfb 0%, #ebedee 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
  color: #a0aebc;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.5;
}

/* 悬浮行业标签 */
.industry-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.65);
  backdrop-filter: blur(4px);
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  z-index: 10;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 文本信息区 */
.card-body {
  padding: 20px;
}

.ent-name {
  font-size: 18px;
  color: #303133;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.ent-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  height: 42px; /* 固定高度，配合 line-clamp-2 */
  margin-bottom: 0;
}

.ent-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.meta-item .el-icon {
  margin-right: 6px;
  font-size: 15px;
}

.highlight-phone {
  color: #409EFF;
  font-weight: 500;
}

/* ====== 工具类 ====== */
.line-clamp-1 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

/* ====== 详情弹窗样式 ====== */

/* 弹窗头部布局：左侧信息 + 右侧打印按钮 */
.dialog-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 15px;
  padding: 5px 0;
}

/* 左侧信息区自动占满剩余空间 */
.detail-header-info {
  flex: 1;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 18px;
}

.info-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 5px;
  font-size: 13px;
  white-space: nowrap;
}

/* 标签颜色样式（还原原来的色彩） */
.info-item.primary {
  background: #e8f3ff;
  color: #1989fa;
}

.info-item.success {
  background: #e6f7ee;
  color: #00b42a;
}

.info-item.info {
  background: #f4f6f8;
  color: #4e5969;
}

.info-item.danger {
  background: #fef0f0;
  color: #f56c6c;
}

/* 图标统一大小 */
.info-item .el-icon {
  font-size: 13px;
}

/* 打印按钮样式优化 */
.meta-right {
  flex-shrink: 0; /* 防止按钮被挤压 */
}


/* 限定富文本内部图片宽度，防止撑破弹窗 */
.rich-text-container {
  line-height: 1.8;
  color: #333;
  font-size: 15px;
}

:deep(.rich-text-container img) {
  max-width: 100%;
  max-height: 90vh !important;
  object-fit: contain;
  height: auto !important;
  display: block;
  border-radius: 8px;
  margin: 10px 0;
  border: #050505 1px dotted;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

:deep(.rich-text-container video) {
  max-width: 100%;
  border-radius: 8px;
}

.video-container {
  width: 100%;
  height: 100%;
  position: relative;
  background: #000;
}

.media-video {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保证视频填满容器 */
}

/* 在视频中心显示一个小的播放图标提示 */
.video-play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 40px;
  pointer-events: none; /* 图标不挡住鼠标事件 */
  transition: opacity 0.3s;
}

.video-container:hover .video-play-icon {
  opacity: 0; /* 鼠标移上去播放时隐藏图标 */
}


/* ===================== 统一移动端响应式适配 ===================== */
/* 平板适配 (768px-1200px) */
@media (max-width: 1200px) {
  .main-container {
    max-width: 100%;
    padding: 0 15px 30px;
  }
}

/* 手机适配 (<768px) */
@media (max-width: 768px) {
  /* Hero区适配 */
  .hero-section {
    height: 220px;
    margin-bottom: -20px;
  }

  .hero-content .title {
    font-size: 24px;
  }

  .hero-content .subtitle {
    font-size: 14px;
    margin-bottom: 20px;
  }

  .search-box {
    margin: 0 10px;
  }

  /* 卡片媒体区适配 */
  .card-media {
    height: 180px;
  }

  .placeholder-icon {
    font-size: 36px;
  }

  /* 卡片内容区适配 */
  .card-body {
    padding: 15px;
  }

  .ent-name {
    font-size: 16px;
  }

  .ent-desc {
    font-size: 12px;
  }

  .meta-item {
    font-size: 12px;
  }

  /* 分页适配 */
  .pagination-wrapper {
    margin-top: 20px;
    transform: scale(0.9);
  }

  /* 弹窗适配 */
  :deep(.detail-dialog) {
    --el-dialog-width: 90% !important;
  }

  .detail-header-info {
    font-size: 12px;
    gap: 8px;
  }

  .rich-text-container {
    font-size: 14px;
  }
}

/* 小屏手机适配 (<375px) */
@media (max-width: 375px) {
  .hero-section {
    height: 200px;
  }

  .card-media {
    height: 160px;
  }

  .pagination-wrapper {
    transform: scale(0.85);
  }
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.action-btn:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

</style>

<style>
.detail-dialog .el-dialog__header {
  padding: 10px 30px !important;
}

.detail-dialog .el-dialog__body {
  padding: 0 30px !important;
}
</style>