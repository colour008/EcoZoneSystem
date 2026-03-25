<template>
  <div class="portal-home">
    <el-carousel height="450px" motion-blur class="hero-carousel">
      <el-carousel-item v-for="(item, index) in banners" :key="index">
        <div class="banner-item" :style="{ backgroundImage: `url(${item.img})` }">
          <div class="banner-content">
            <h2>{{ item.title }}</h2>
            <p>{{ item.subtitle }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <div class="service-hall">
      <div class="hall-inner">
        <div class="service-card" @click="goTo('/my-enterprise')">
          <el-icon class="icon">
            <OfficeBuilding/>
          </el-icon>
          <h3>企业入驻</h3>
          <p>一键提交资料，快速审批</p>
        </div>
        <div class="service-card" @click="goTo('/policy')">
          <el-icon class="icon">
            <Document/>
          </el-icon>
          <h3>政策申报</h3>
          <p>精准匹配，直达企业</p>
        </div>
        <div class="service-card" @click="goTo('/contact')">
          <el-icon class="icon">
            <Service/>
          </el-icon>
          <h3>园区服务</h3>
          <p>在线提单，极速响应</p>
        </div>
        <div class="service-card" @click="goTo('/contact')">
          <el-icon class="icon">
            <ChatLineSquare/>
          </el-icon>
          <h3>办事咨询</h3>
          <p>全天候管家贴心服务</p>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-row :gutter="40">
        <el-col :span="16">
          <el-tabs v-model="activeTab" class="news-tabs">
            <el-tab-pane label="园区动态" name="dynamics">
              <ul class="news-list">
                <li v-for="news in dynamicsList" :key="news.id" class="news-item" @click="goToArticle(news.id)">
                </li>
              </ul>
            </el-tab-pane>
            <el-tab-pane label="通知公告" name="notices">
              <ul class="news-list">
                <li v-for="notice in noticeList" :key="notice.id" class="news-item">
                  <span class="dot"></span>
                  <a href="#" class="title" :title="notice.title">{{ notice.title }}</a>
                  <span class="date">{{ notice.date }}</span>
                </li>
              </ul>
            </el-tab-pane>
          </el-tabs>
          <div class="more-link">
            <el-button link type="primary">查看更多资讯
              <el-icon>
                <ArrowRight/>
              </el-icon>
            </el-button>
          </div>
        </el-col>

        <el-col :span="8">
          <div class="park-profile">
            <div class="section-title">
              <h3>园区名片</h3>
            </div>
            <div class="profile-video">
              <img src="@/assets/background.jpg" alt="园区风貌"/>
              <div class="play-btn">
                <el-icon>
                  <VideoPlay/>
                </el-icon>
              </div>
            </div>
            <p class="intro-text">
              经济开发区成立于 2010 年，规划面积 50 平方公里。聚焦电子信息、节能环保、文化传媒三大主导产业，致力于打造集研发、生产、办公于一体的现代化、智慧化生态园区。
            </p>
            <div class="data-stats">
              <div class="stat-item">
                <h4>1,200<span>+</span></h4>
                <p>入驻企业</p>
              </div>
              <div class="stat-item">
                <h4>85<span>%</span></h4>
                <p>高新技术占比</p>
              </div>
              <div class="stat-item">
                <h4>300<span>亿</span></h4>
                <p>年产值</p>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {ArrowRight, ChatLineSquare, Document, OfficeBuilding, Service, VideoPlay} from "@element-plus/icons-vue";

const router = useRouter()
const activeTab = ref('dynamics')

// 导航函数
const goTo = (path) => {
  router.push(path)
}

// 虚拟数据：轮播图
const banners = ref([
  {
    img: 'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=2070&auto=format&fit=crop',
    title: '科技赋能，创新引领',
    subtitle: '打造国际一流的智慧产业新城'
  },
  {
    img: 'https://images.unsplash.com/photo-1497366216548-37526070297c?q=80&w=2069&auto=format&fit=crop',
    title: '优越环境，助力成长',
    subtitle: '为入驻企业提供全生命周期服务'
  }
])

// 虚拟数据：园区动态
const dynamicsList = ref([
  {id: 1, title: '2026年首季度园区招商引资实现“开门红”，新增规上企业15家', date: '2026-03-20'},
  {id: 2, title: '芯创半导体二期研发中心正式奠基开工', date: '2026-03-18'},
  {id: 3, title: '市领导一行莅临我区考察绿色环保产业发展情况', date: '2026-03-15'},
  {id: 4, title: '“智慧园区2.0”系统全面上线，开启数字化运营新篇章', date: '2026-03-10'},
  {id: 5, title: '园区企业代表团赴欧洲开展经贸交流与技术合作', date: '2026-03-05'},
  {id: 6, title: '第四届经济开发区创新创业大赛圆满落幕', date: '2026-02-28'}
])

// 虚拟数据：通知公告 (未来这里可以直接对接你的 biz_notice 接口)
const noticeList = ref([
  {id: 1, title: '关于启动2026年度“园区之星”评选的通知', date: '2026-03-21'},
  {id: 2, title: '紧急：园区A座高压线路停电检修通知', date: '2026-03-21'},
  {id: 3, title: '关于优化企业入驻审批流程的公示', date: '2026-03-21'},
  {id: 4, title: '关于组织申报2026年省级高新技术企业培育库的通知', date: '2026-03-12'},
  {id: 5, title: '园区班车春季运行时刻表调整通知', date: '2026-03-01'}
])

// 跳转到详情页
const goToArticle = (id) => {
  router.push(`/article/${id}`)
}
</script>

<style scoped>
.portal-home {
  background-color: #f5f7fa;
  padding-bottom: 60px;
}

/* 轮播图样式 */
.hero-carousel {
  width: 100%;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, rgba(0, 40, 90, 0.8), rgba(0, 0, 0, 0.1));
}

.banner-content {
  position: absolute;
  top: 50%;
  left: 10%;
  transform: translateY(-50%);
  color: #fff;
  z-index: 1;
}

.banner-content h2 {
  font-size: 42px;
  margin-bottom: 20px;
  letter-spacing: 2px;
}

.banner-content p {
  font-size: 20px;
  opacity: 0.9;
}

/* 快捷服务大厅 */
.service-hall {
  max-width: 1200px;
  margin: -40px auto 40px;
  position: relative;
  z-index: 10;
}

.hall-inner {
  display: flex;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.service-card {
  flex: 1;
  text-align: center;
  padding: 35px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-right: 1px solid #f0f0f0;
}

.service-card:last-child {
  border-right: none;
}

.service-card:hover {
  background-color: #409EFF;
  color: #fff;
  transform: translateY(-5px);
}

.service-card:hover .icon,
.service-card:hover h3,
.service-card:hover p {
  color: #fff;
}

.service-card .icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 15px;
  transition: color 0.3s;
}

.service-card h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.service-card p {
  font-size: 13px;
  color: #999;
}

/* 主内容区 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 新闻列表样式 */
.news-tabs {
  background: #fff;
  padding: 20px 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  min-height: 400px;
}

:deep(.el-tabs__item) {
  font-size: 18px;
  font-weight: bold;
}

.news-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0 0;
}

.news-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed #ebeef5;
}

.news-item .dot {
  width: 6px;
  height: 6px;
  background-color: #409EFF;
  border-radius: 50%;
  margin-right: 15px;
}

.news-item .title {
  flex: 1;
  color: #333;
  text-decoration: none;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 20px;
  transition: color 0.2s;
}

.news-item .title:hover {
  color: #409EFF;
}

.news-item .date {
  color: #999;
  font-size: 14px;
}

.more-link {
  text-align: right;
  margin-top: 15px;
}

/* 园区名片样式 */
.park-profile {
  background: #fff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  height: 100%;
}

.section-title {
  border-left: 4px solid #409EFF;
  padding-left: 10px;
  margin-bottom: 20px;
}

.section-title h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.profile-video {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 15px;
  cursor: pointer;
}

.profile-video img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}

.play-btn {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 40px;
  color: rgba(255, 255, 255, 0.8);
  transition: color 0.3s;
}

.profile-video:hover .play-btn {
  color: #fff;
}

.intro-text {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 25px;
  text-align: justify;
}

.data-stats {
  display: flex;
  justify-content: space-between;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.stat-item h4 {
  font-size: 24px;
  color: #409EFF;
  margin: 0 0 5px 0;
}

.stat-item h4 span {
  font-size: 14px;
}

.stat-item p {
  font-size: 12px;
  color: #999;
  margin: 0;
}
</style>