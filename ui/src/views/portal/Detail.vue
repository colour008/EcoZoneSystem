<template>
  <div class="detail-container">
    <div class="breadcrumb-section">
      <div class="container">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item @click="handleGoBack" style="cursor: pointer;">返回列表</el-breadcrumb-item>
          <el-breadcrumb-item>正文详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div class="main-content container">
      <div class="article-wrapper">
        <div class="article-footer">
          <el-button type="primary" size="small" plain @click="handleGoBack">
            <el-icon>
              <Back/>
            </el-icon>
            返回
          </el-button>
        </div>
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div style="padding: 40px;">
              <el-skeleton-item variant="h1" style="width: 70%; height: 40px; margin-bottom: 20px;"/>
              <div style="display: flex; gap: 20px; margin-bottom: 40px;">
                <el-skeleton-item variant="text" style="width: 100px;"/>
                <el-skeleton-item variant="text" style="width: 150px;"/>
              </div>
              <el-skeleton-item variant="p" style="width: 100%; height: 20px; margin-bottom: 10px;"/>
              <el-skeleton-item variant="p" style="width: 100%; height: 20px; margin-bottom: 10px;"/>
              <el-skeleton-item variant="p" style="width: 80%; height: 20px; margin-bottom: 10px;"/>
            </div>
          </template>

          <template #default>
            <div class="article-box" v-if="detail">
              <div class="article-header">
                <h1 class="article-title">{{ detail.title }}</h1>
                <div class="article-meta">
                  <div class="meta-left">
                    <el-tag :type="getTypeTag(detail.type)" size="small" class="type-tag">
                      {{ getTypeName(detail.type) }}
                    </el-tag>
                    <span class="meta-item">
                      <el-icon><User/></el-icon> {{ detail.publisherName || '园区管理方' }}
                    </span>
                    <span class="meta-item">
                      <el-icon><Clock/></el-icon> {{ detail.publishTime }}
                    </span>
                    <span class="meta-item">
                      <el-icon><View/></el-icon> {{ detail.viewCount }} 次阅读
                    </span>
                  </div>

                  <div class="meta-right hidden-xs-only">
                    <span class="action-btn" @click="handlePrint">
                      <el-icon><Printer/></el-icon> 打印
                    </span>
                    <el-divider direction="vertical"/>
                    <span class="font-size-control">
                      字号：
                      <span :class="['font-btn', { active: fontSize === 'small' }]"
                            @click="fontSize = 'small'">小</span>
                      <span :class="['font-btn', { active: fontSize === 'medium' }]"
                            @click="fontSize = 'medium'">中</span>
                      <span :class="['font-btn', { active: fontSize === 'large' }]"
                            @click="fontSize = 'large'">大</span>
                    </span>
                  </div>
                </div>
              </div>

              <div class="article-summary" v-if="detail.summary">
                <strong>导读：</strong>{{ detail.summary }}
              </div>

              <div
                  class="article-content"
                  :class="`font-size-${fontSize}`"
                  v-html="detail.content"
              ></div>


              <div class="article-nav-cards" v-if="detail.prevNotice || detail.nextNotice">
                <div class="nav-card prev" :class="{ 'empty': !detail.prevNotice }"
                     @click="detail.prevNotice && goToArticle(detail.prevNotice.id)">
                  <div class="nav-bg" v-if="detail.prevNotice"
                       :style="{ backgroundImage: `url(${detail.prevNotice.coverUrl || detail.prevNotice.cover_url || ''})` }"></div>
                  <div class="nav-mask"></div>
                  <div class="nav-content">
                    <span class="nav-label">上一篇</span>
                    <span class="nav-title" :title="detail.prevNotice?.title || '已经是第一篇了'">
                      {{ detail.prevNotice?.title || '已经是第一篇了' }}
                    </span>
                  </div>
                </div>

                <div class="nav-card next" :class="{ 'empty': !detail.nextNotice }"
                     @click="detail.nextNotice && goToArticle(detail.nextNotice.id)">
                  <div class="nav-bg" v-if="detail.nextNotice"
                       :style="{ backgroundImage: `url(${detail.nextNotice.coverUrl || detail.nextNotice.cover_url || ''})` }"></div>
                  <div class="nav-mask"></div>
                  <div class="nav-content">
                    <span class="nav-label">下一篇</span>
                    <span class="nav-title" :title="detail.nextNotice?.title || '已经是最后一篇了'">
                      {{ detail.nextNotice?.title || '已经是最后一篇了' }}
                    </span>
                  </div>
                </div>
              </div>

            </div>

            <el-empty v-else description="未能获取到内容信息，该信息可能已被撤回或删除"/>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {User, Clock, View, Printer, Back} from '@element-plus/icons-vue'
import noticeApi from '@/api/notice'
import {ElMessage} from 'element-plus'

const route = useRoute()
const router = useRouter()

// 状态
const loading = ref(true)
const detail = ref(null)
const fontSize = ref('medium') // 默认字号

// 获取详情
const getDetail = async () => {
  const id = route.query.id
  if (!id) {
    ElMessage.error('缺少参数')
    router.go(-1)
    return
  }

  loading.value = true
  try {
    const res = await noticeApi.getDetail(id)
    if (res && res.data) {
      detail.value = res.data
    }
  } catch (error) {
    console.error('获取详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 字典翻译辅助函数
const getTypeName = (type) => {
  const map = {
    1: '政策文件',
    2: '园区动态',
    3: '通知公告'
  }
  return map[type] || '其他'
}

const getTypeTag = (type) => {
  const map = {
    1: 'danger',
    2: 'primary',
    3: 'success'
  }
  return map[type] || 'info'
}

// 打印功能
const handlePrint = () => {
  window.print()
}

// 跳转到指定文章
const goToArticle = (id) => {
  if (!id) return;
  router.push({path: route.path, query: {id}})
}

// 定义文章类型到路由路径的映射
const typeRouteMap = {
  1: '/policy',   // 政策文件
  2: '/news',     // 园区动态
  3: '/notice',   // 通知公告
}

// 自定义返回方法
const handleGoBack = () => {
  // 如果详情数据已加载，根据类型跳转
  if (detail.value && detail.value.type) {
    const targetPath = typeRouteMap[detail.value.type] || '/'
    router.push(targetPath)
  } else {
    // 兜底：如果数据没加载出来或者没有对应类型，返回首页
    router.push('/')
  }
}

// 监听路由参数变化
watch(() => route.query.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    getDetail()
    window.scrollTo({top: 0, behavior: 'smooth'})
  }
})

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.detail-container {
  min-height: 80vh;
  background-color: #f5f7fa;
  padding-bottom: 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 面包屑 */
.breadcrumb-section {
  background-color: #fff;
  padding: 15px 0;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  margin-bottom: 24px;
}

/* 文章容器 */
.article-wrapper {
  max-width: 960px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  min-height: 500px;
  padding: 10px 50px 50px;
}

/* 头部样式 */
.article-header {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.article-title {
  font-size: 28px;
  color: #303133;
  line-height: 1.4;
  margin: 0 0 20px 0;
  text-align: center;
  font-weight: 600;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.meta-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 交互按钮区 */
.meta-right {
  display: flex;
  align-items: center;
}

.action-btn {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s;
}

.action-btn:hover {
  color: #409eff;
}

.font-size-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.font-btn {
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.3s;
}

.font-btn.active {
  background-color: #409eff;
  color: #fff;
}

/* 摘要 */
.article-summary {
  background-color: #f4f4f5;
  padding: 15px 20px;
  border-radius: 4px;
  color: #606266;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 30px;
  border-left: 4px solid #909399;
}

/* 富文本正文 */
.article-content {
  color: #333;
  line-height: 2;
  word-wrap: break-word;
  overflow: hidden;
}

.font-size-small {
  font-size: 14px;
}

.font-size-medium {
  font-size: 16px;
}

.font-size-large {
  font-size: 18px;
}

:deep(.article-content p) {
  margin-bottom: 1.2em;
  text-indent: 2em;
}

:deep(.article-content img) {
  max-width: 100% !important;
  height: auto !important;
  display: block;
  margin: 20px auto;
  border-radius: 4px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

/* 底部返回按钮 */
.article-footer {
  padding: 20px 0 30px;
  text-align: left;
}

/* --- 上一篇/下一篇 背景卡片版样式 --- */
.article-nav-cards {
  display: flex;
  gap: 20px;
  border-top: 1px dashed #ebeef5;
  padding-top: 30px;
  margin-top: 10px;
}

.nav-card {
  flex: 1;
  position: relative;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 0 25px;
  transition: transform 0.3s ease;
  width: 50%;
  box-sizing: border-box;
  background-color: #f4f4f5; /* 无图时的背景色 */
}

/* 背景图片层 */
.nav-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.6s ease;
  z-index: 1;
}

/* 渐变遮罩层：确保文字清晰 */
.nav-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(32, 32, 32, 0.5) 0%, rgba(89, 89, 89, 0.41) 100%);
  z-index: 2;
  transition: background 0.3s;
}

.nav-card.next .nav-mask {
  background: linear-gradient(90deg, rgba(32, 32, 32, 0.5) 0%, rgba(89, 89, 89, 0.41) 100%);
}

/* 文字内容层 */
.nav-content {
  position: relative;
  z-index: 3;
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.nav-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.nav-title {
  font-size: 15px;
  color: #ffffff;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 悬停特效 */
.nav-card:hover:not(.empty) {
  transform: translateY(-4px);
}

.nav-card:hover:not(.empty) .nav-bg {
  transform: scale(1.1);
}

.nav-card:hover:not(.empty) .nav-mask {
  background: rgba(0, 0, 0, 0.5); /* 悬停时遮罩变均匀，高亮图片 */
}

.nav-card.empty {
  cursor: not-allowed;
  background-color: #f8f9fa;
}

.nav-card.empty .nav-mask {
  background: none;
}

.nav-card.empty .nav-title,
.nav-card.empty .nav-label {
  color: #909399;
}

.nav-card.next {
  text-align: right;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .article-wrapper {
    padding: 20px 15px;
  }

  .article-nav-cards {
    flex-direction: column;
    gap: 15px;
  }

  .nav-card {
    width: 100%;
    height: 80px;
  }
}

@media print {
  /* 隐藏页头 */
  .portal-header {
    display: none !important;
  }

  /* 隐藏页脚 */
  .footer {
    display: none !important;
  }

  /* 调整打印时的边距，让内容占满纸张 */
  @page {
    margin: 1cm;
  }

  /* 详情页容器去掉背景色和阴影，确保打印清晰 */
  .detail-container {
    background-color: #fff !important;
    padding: 0 !important;
  }

  .article-wrapper {
    box-shadow: none !important;
    padding: 0 !important;
    margin: 0 auto !important;
    width: 100% !important;
    max-width: 100% !important;
  }

  /* 隐藏面包屑 */
  .breadcrumb-section {
    display: none !important;
  }

  /* 隐藏底部返回按钮 */
  .article-footer {
    display: none !important;
  }

  /* 隐藏上一篇和下一篇卡片*/
  .article-nav-cards {
    display: none !important;
  }

  /* 隐藏打印字号信息 */
  .meta-right{
    display: none !important;
  }
}
</style>