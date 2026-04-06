<template>
  <div class="detail-container">
    <div class="breadcrumb-section">
      <div class="container">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item @click="$router.go(-1)" style="cursor: pointer;">返回列表</el-breadcrumb-item>
          <el-breadcrumb-item>正文详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div class="main-content container">
      <div class="article-wrapper">
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

              <div class="article-footer">
                <el-button type="primary" plain @click="$router.go(-1)">
                  <el-icon>
                    <Back/>
                  </el-icon>
                  返回上一页
                </el-button>
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
import {ref, onMounted} from 'vue'
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
    1: '通知公告',
    2: '园区动态',
    3: '政策文件'
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

onMounted(() => {
  getDetail()
})
</script>

<style scoped>
.detail-container {
  min-height: 80vh;
  background-color: #f5f7fa; /* 更柔和的政务灰底色 */
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

/* 文章容器：控制最大宽度以提升阅读体验 */
.article-wrapper {
  max-width: 900px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  min-height: 500px;
  padding: 40px 50px;
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
  text-align: center; /* 政务公文通常居中 */
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

.type-tag {
  margin-right: 5px;
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

/* --- 富文本正文深度样式控制 --- */
.article-content {
  color: #333;
  line-height: 2;
  word-wrap: break-word;
  overflow: hidden;
}

/* 字号控制类 */
.font-size-small {
  font-size: 14px;
}

.font-size-medium {
  font-size: 16px;
}

.font-size-large {
  font-size: 18px;
}

/* 深度拦截后端传来的 HTML 标签 */
:deep(.article-content p) {
  margin-bottom: 1.2em;
  text-indent: 2em; /* 首行缩进，符合中文公文规范 */
}

:deep(.article-content img) {
  max-width: 100% !important;
  height: auto !important;
  display: block;
  margin: 20px auto;
  border-radius: 4px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

:deep(.article-content table) {
  width: 100% !important;
  border-collapse: collapse;
  margin-bottom: 20px;
}

:deep(.article-content table th),
:deep(.article-content table td) {
  border: 1px solid #dcdfe6;
  padding: 10px;
}

:deep(.article-content table th) {
  background-color: #f5f7fa;
}

/* 底部 */
.article-footer {
  margin-top: 50px;
  padding-top: 20px;
  border-top: 1px dashed #ebeef5;
  text-align: center;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .article-wrapper {
    padding: 20px 15px;
  }

  .article-title {
    font-size: 22px;
    text-align: left;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  /* 移动端隐藏打印区域 */
  .hidden-xs-only {
    display: none;
  }
}

/* 打印时专有样式 */
@media print {
  .breadcrumb-section,
  .meta-right,
  .article-footer {
    display: none !important;
  }

  .article-wrapper {
    box-shadow: none;
    padding: 0;
  }

  .detail-container {
    background-color: #fff;
  }
}
</style>