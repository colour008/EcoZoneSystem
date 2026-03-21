<template>
  <div class="article-container">
    <div class="article-box">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>园区动态</el-breadcrumb-item>
        <el-breadcrumb-item>正文</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="article-header" v-if="article">
        <h1 class="title">{{ article.title }}</h1>
        <div class="meta-info">
          <span>发布时间：{{ article.publishTime }}</span>
          <span>来源：经济开发区管委会</span>
          <span>浏览量：{{ article.viewCount }} 次</span>
        </div>
      </div>

      <div v-if="loading" class="loading-box" v-loading="true"></div>

      <div
          v-else-if="article"
          class="article-content"
          v-html="article.content"
      ></div>

      <el-empty v-else description="文章不存在或已删除"/>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRoute} from 'vue-router'
import request from '@/api/request.js'


const route = useRoute()
const article = ref(null)
const loading = ref(true)

// 获取文章数据
const fetchArticleDetail = async () => {
  const articleId = route.params.id // 从 URL 中获取 /article/1 里的 1
  try {
    loading.value = true
    // 模拟接口调用，实际开发中放开下面的注释
    const res = await request.get(`/api/portal/notice/${articleId}`)
    article.value = res.data

    // 模拟数据注入
    article.value = {
      id: articleId,
      title: '关于启动2026年度“园区之星”评选的通知',
      publishTime: '2026-03-21 09:00:00',
      viewCount: 157,
      content: `
        <p style="text-indent: 2em; line-height: 2;">各入驻企业：</p>
        <p style="text-indent: 2em; line-height: 2;">为进一步激发园区企业创新活力，树立优秀企业标杆，经研究决定，正式启动2026年度“园区之星”评选活动。现将有关事项通知如下：</p>
        <p style="text-indent: 2em; line-height: 2;"><strong>一、评选对象</strong><br/>在园区内注册并实际运营的各类企业。</p>
        <p style="text-indent: 2em; line-height: 2;"><strong>二、评选标准</strong><br/>涵盖经济效益、科技创新、社会责任等多个维度...</p>
        <div style="text-align: center; margin: 20px 0;">
          <img src="https://images.unsplash.com/photo-1554469384-e58fac16e23a?w=600" style="max-width: 100%; border-radius: 4px;" alt="会议图片"/>
        </div>
        <p style="text-align: right; margin-top: 50px;">经济开发区管理委员会<br/>2026年3月21日</p>
      `
    }
  } catch (error) {
    console.error('获取文章失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchArticleDetail()
})
</script>

<style scoped>
.article-container {
  background-color: #f5f7fa;
  padding: 40px 0;
  min-height: calc(100vh - 120px);
}

.article-box {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  padding: 40px 60px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

.breadcrumb {
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.article-header {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 1px dashed #ddd;
  padding-bottom: 20px;
}

.title {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
  letter-spacing: 1px;
}

.meta-info {
  color: #999;
  font-size: 14px;
}

.meta-info span {
  margin: 0 15px;
}

.loading-box {
  height: 300px;
}

/* 核心：为 v-html 注入的富文本提供默认样式 */
.article-content {
  font-size: 16px;
  color: #444;
  line-height: 2;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* 使用 :deep() 穿透 scoped，作用于 v-html 生成的标签 */
.article-content :deep(p) {
  margin-bottom: 15px;
  text-align: justify;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 10px auto;
}
</style>