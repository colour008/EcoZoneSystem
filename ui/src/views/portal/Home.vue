<template>
  <div class="portal-home">
    <el-carousel height="450px" motion-blur class="hero-carousel">
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <div class="banner-item" :style="{ backgroundImage: `url(${item.img})` }">
          <div class="banner-mask"></div>
          <div class="banner-content">
            <h2 class="animate-title">{{ item.title }}</h2>
            <p class="animate-subtitle">{{ item.subtitle }}</p>
            <el-button type="primary" size="large" round class="banner-btn" @click="goTo('/enterprise')">立即入驻
            </el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="service-hall">
      <div class="hall-inner">
        <div class="service-card" @click="goTo('/enterprise')">
          <div class="icon-wrapper">
            <el-icon>
              <OfficeBuilding/>
            </el-icon>
          </div>
          <h3>企业入驻</h3>
          <p>一键提交资料，快速审批</p>
        </div>
        <div class="service-card" @click="goTo('/policy')">
          <div class="icon-wrapper">
            <el-icon>
              <Document/>
            </el-icon>
          </div>
          <h3>政策申报</h3>
          <p>精准匹配，直达企业</p>
        </div>
        <div class="service-card" @click="goTo('/contact')">
          <div class="icon-wrapper">
            <el-icon>
              <Service/>
            </el-icon>
          </div>
          <h3>园区服务</h3>
          <p>在线提单，极速响应</p>
        </div>
        <div class="service-card" @click="goTo('/contact')">
          <div class="icon-wrapper">
            <el-icon>
              <ChatLineSquare/>
            </el-icon>
          </div>
          <h3>办事咨询</h3>
          <p>全天候管家贴心服务</p>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="30">
        <el-col :xs="24" :sm="24" :md="16" :lg="16">
          <div class="content-section-box">
            <el-tabs v-model="activeTab" class="news-tabs" @tab-change="handleTabChange">
              <el-tab-pane label="政策中心" name="policy">
                <ul class="news-list" v-loading="loading">
                  <li v-for="item in policyList" :key="item.id" class="news-item" @click="goToArticle(item.id)">
                    <div class="news-main">
                      <span class="news-title" :title="item.title">{{ item.title }}</span>
                      <el-tag size="small" effect="plain" class="news-tag">最新发布</el-tag>
                    </div>
                    <span class="date">{{ item.createTime?.split(' ')[0] || item.date }}</span>
                  </li>
                </ul>
                <div class="more-btn-wrap">
                  <el-button link type="primary" @click="goTo('/policy')">
                    查看全部政策
                    <el-icon>
                      <ArrowRight/>
                    </el-icon>
                  </el-button>
                </div>
              </el-tab-pane>

              <el-tab-pane label="园区动态" name="dynamics">
                <ul class="news-list" v-loading="loading">
                  <li v-for="news in dynamicsList" :key="news.id" class="news-item" @click="goToArticle(news.id)">
                    <div class="news-main">
                      <span class="news-title" :title="news.title">{{ news.title }}</span>
                    </div>
                    <span class="date">{{ news.createTime?.split(' ')[0] || news.date }}</span>
                  </li>
                </ul>
                <div class="more-btn-wrap">
                  <el-button link type="primary" @click="goTo('/news')">
                    查看全部动态
                    <el-icon>
                      <ArrowRight/>
                    </el-icon>
                  </el-button>
                </div>
              </el-tab-pane>

              <el-tab-pane label="通知公告" name="notices">
                <ul class="news-list" v-loading="loading">
                  <li v-for="notice in noticeList" :key="notice.id" class="news-item" @click="goToArticle(notice.id)">
                    <div class="news-main">
                      <span class="news-title" :title="notice.title">{{ notice.title }}</span>
                    </div>
                    <span class="date">{{ notice.createTime?.split(' ')[0] || notice.date }}</span>
                  </li>
                </ul>
                <div class="more-btn-wrap">
                  <el-button link type="primary" @click="goTo('/notice')">
                    查看全部公告
                    <el-icon>
                      <ArrowRight/>
                    </el-icon>
                  </el-button>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>

        <el-col :xs="24" :sm="24" :md="8" :lg="8">
          <div class="enterprise-section-box">
            <div class="custom-header">
              <h3>名企风采</h3>
              <el-button link @click="goTo('/enterprise')">更多</el-button>
            </div>
            <el-carousel height="420px" indicator-position="outside" arrow="hover" v-loading="entLoading">
              <el-carousel-item v-for="ent in enterpriseList" :key="ent.id">
                <div class="ent-slide-card" @click="goTo('/enterprise')">
                  <div class="ent-cover-wrap">
                    <img v-if="ent.cover" :src="ent.cover" :alt="ent.companyName"/>
                    <div v-else class="empty-cover">
                      <el-icon>
                        <OfficeBuilding/>
                      </el-icon>
                    </div>
                    <div class="ent-status-tag">{{ ent.industry }}</div>
                  </div>
                  <div class="ent-info">
                    <h4 class="one-line">{{ ent.companyName }}</h4>
                    <p class="three-line">{{ ent.plainText }}</p>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="park-brand-section">
      <div class="container-inner">
        <el-row :gutter="80" align="middle">
          <el-col :md="11" :sm="24">
            <div class="brand-visual">
              <img src="https://images.unsplash.com/photo-1497366811353-6870744d04b2?q=80&w=2069&auto=format&fit=crop"
                   alt="园区全景"/>
              <div class="play-btn-float">
                <el-icon>
                  <VideoPlay/>
                </el-icon>
              </div>
            </div>
          </el-col>
          <el-col :md="13" :sm="24">
            <div class="brand-text">
              <span class="sub-title">ABOUT PARK</span>
              <h2 class="main-title">园区名片</h2>
              <p class="description">
                经济开发区成立于 2010 年，是区域创新发展的核心引擎。我们坚持“产城融合”理念，聚集全球高端要素，重点扶持高新科技企业，提供全方位、智能化的深度服务。
              </p>
              <div class="brand-stats">
                <div class="stat-item">
                  <div class="val">1,200<span>+</span></div>
                  <div class="lab">入驻企业</div>
                </div>
                <div class="stat-item">
                  <div class="val">85<span>%</span></div>
                  <div class="lab">高新技术占比</div>
                </div>
                <div class="stat-item">
                  <div class="val">300<span>亿</span></div>
                  <div class="lab">年产值规模</div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ArrowRight, ChatLineSquare, Document, OfficeBuilding, Service, VideoPlay} from "@element-plus/icons-vue";
import noticeApi from '@/api/notice'
import enterpriseApi from '@/api/enterprise'

const router = useRouter()
const activeTab = ref('policy')
const loading = ref(false)
const entLoading = ref(false)

const policyList = ref([])
const dynamicsList = ref([])
const noticeList = ref([])
const enterpriseList = ref([])

const banners = ref([
  {
    img: 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=2070&auto=format&fit=crop',
    title: '科技赋能 · 智领未来',
    subtitle: '致力于打造全球领先的数字化产业集群，为创新者提供无限可能'
  },
  {
    img: 'https://images.unsplash.com/photo-1497366216548-37526070297c?q=80&w=2069&auto=format&fit=crop',
    title: '全维度服务 · 助力成长',
    subtitle: '政务、金融、人才、技术四位一体，全心全意解决企业痛点'
  }
])

const getFirstImg = (html) => {
  if (!html) return null;
  const imgRegex = /<img[^>]+src=["']([^"']+)["']/i;
  const match = html.match(imgRegex);
  return match ? match[1] : null;
};

const getPlainText = (html) => {
  if (!html) return '';
  let text = html.replace(/<[^>]+>/g, '').replace(/&nbsp;/ig, ' ').replace(/\s+/g, ' ');
  return text.trim();
};

const fetchHomeData = async (type) => {
  loading.value = true
  try {
    const res = await noticeApi.getPublicList({type, pageNum: 1, pageSize: 6})
    if (res && res.data) {
      if (type === 1) policyList.value = res.data.records
      if (type === 2) dynamicsList.value = res.data.records
      if (type === 3) noticeList.value = res.data.records
    }
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchEnterpriseData = async () => {
  entLoading.value = true
  try {
    const res = await enterpriseApi.getEnterpriseShowPage({pageNum: 1, pageSize: 5})
    if (res.data && res.data.records) {
      enterpriseList.value = res.data.records.map(item => ({
        ...item,
        cover: getFirstImg(item.introduction),
        plainText: getPlainText(item.introduction)
      }))
    }
  } catch (e) {
    console.error('获取企业数据失败', e)
  } finally {
    entLoading.value = false
  }
}

const handleTabChange = (name) => {
  if (name === 'policy' && policyList.value.length === 0) fetchHomeData(1)
  if (name === 'dynamics' && dynamicsList.value.length === 0) fetchHomeData(2)
  if (name === 'notices' && noticeList.value.length === 0) fetchHomeData(3)
}

const goTo = (path) => router.push(path)
const goToArticle = (id) => router.push({path: '/portal/detail', query: {id: id}})

onMounted(() => {
  fetchHomeData(1)
  fetchHomeData(2)
  fetchHomeData(3)
  fetchEnterpriseData()
})
</script>

<style scoped>
.portal-home {
  background-color: #f8fafc;
  color: #1e293b;
}

/* 1. Hero Carousel */
.hero-carousel {
  width: 100%;
  overflow: hidden;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.8) 0%, rgba(15, 23, 42, 0.2) 100%);
}

.banner-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #fff;
  max-width: 800px;
  padding: 0 20px;
}

.banner-content h2 {
  font-size: 52px;
  font-weight: 800;
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.banner-content p {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 40px;
  font-weight: 300;
  line-height: 1.6;
}

.banner-btn {
  padding: 12px 40px;
  font-size: 16px;
  transition: 0.3s;
}

/* 2. Service Hall */
.service-hall {
  max-width: 1240px;
  margin: -60px auto 60px;
  position: relative;
  z-index: 10;
  padding: 0 20px;
}

.hall-inner {
  display: flex;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.service-card {
  flex: 1;
  text-align: center;
  padding: 45px 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-right: 1px solid #f1f5f9;
}

.service-card:last-child {
  border-right: none;
}

.service-card:hover {
  background-color: #fff;
  transform: translateY(-8px);
  box-shadow: inset 0 -4px 0 #409EFF;
}

.icon-wrapper {
  width: 64px;
  height: 64px;
  background: #eff6ff;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: #409EFF;
  font-size: 32px;
  transition: 0.3s;
}

.service-card:hover .icon-wrapper {
  background: #409EFF;
  color: #fff;
  transform: rotate(10deg);
}

.service-card h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
}

.service-card p {
  font-size: 14px;
  color: #64748b;
}

/* 3. Main Content */
.main-content {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 20px 80px;
}

.content-section-box, .enterprise-section-box {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  height: 100%;
}

/* News List */
.news-tabs {
  min-height: 480px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #f1f5f9;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  height: 50px;
}

.news-list {
  list-style: none;
  padding: 0;
  margin-top: 20px;
}

.news-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 0;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: 0.2s;
}

.news-item:hover {
  transform: translateX(10px);
}

.news-main {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  overflow: hidden;
}

.news-title {
  font-size: 15px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-item:hover .news-title {
  color: #409EFF;
}

.news-tag {
  border-radius: 4px;
}

.date {
  font-size: 14px;
  color: #94a3b8;
  font-family: monospace;
}

.more-btn-wrap {
  text-align: center;
  margin-top: 30px;
}

/* Right Carousel */
.custom-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.custom-header h3 {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
  position: relative;
}

.custom-header h3::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 30px;
  height: 3px;
  background: #409EFF;
}

.ent-slide-card {
  cursor: pointer;
  background: #f8fafc;
  border-radius: 12px;
  overflow: hidden;
}

.ent-cover-wrap {
  position: relative;
  width: 100%;
  height: 200px;
}

.ent-cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ent-status-tag {
  position: absolute;
  bottom: 12px;
  left: 12px;
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(4px);
  color: #fff;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.ent-info {
  padding: 20px;
}

.ent-info h4 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #1e293b;
}

.ent-info p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

/* 4. Brand Section (Park Profile) */
.park-brand-section {
  background: #fff;
  padding: 100px 0;
  border-top: 1px solid #f1f5f9;
}

.container-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 0 20px;
}

.brand-visual {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
}

.brand-visual img {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.play-btn-float {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 40px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: 0.3s;
}

.play-btn-float:hover {
  background: #409EFF;
  transform: translate(-50%, -50%) scale(1.1);
}

.brand-text .sub-title {
  color: #409EFF;
  font-weight: 700;
  letter-spacing: 2px;
  font-size: 14px;
  display: block;
  margin-bottom: 12px;
}

.brand-text .main-title {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 24px;
  color: #0f172a;
}

.brand-text .description {
  font-size: 17px;
  color: #475569;
  line-height: 1.8;
  margin-bottom: 40px;
  text-align: justify;
}

.brand-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item .val {
  font-size: 32px;
  font-weight: 800;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-item .val span {
  font-size: 18px;
  margin-left: 2px;
}

.stat-item .lab {
  font-size: 14px;
  color: #94a3b8;
  font-weight: 500;
}

/* Utilities */
.one-line {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.three-line {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

@media (max-width: 768px) {
  .banner-content h2 {
    font-size: 32px;
  }

  .service-card {
    flex: 0 0 50%;
    border-bottom: 1px solid #f1f5f9;
  }

  .brand-visual {
    margin-bottom: 40px;
  }
}
</style>