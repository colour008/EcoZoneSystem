<template>
  <div class="portal-container">
    <div class="banner-section" :style="{backgroundImage: `url(${bannerImg})`}">
      <div class="banner-mask">
        <h1>经济开发区管理平台</h1>
        <p>构建智慧园区，助力企业成长</p>
      </div>
    </div>

    <div class="service-grid">
      <div class="grid-item" @click="$router.push('/login')">
        <el-icon>
          <OfficeBuilding/>
        </el-icon>
        <span>企业入驻</span>
      </div>
      <div class="grid-item">
        <el-icon>
          <Document/>
        </el-icon>
        <span>政策指南</span>
      </div>
      <div class="grid-item">
        <el-icon>
          <Service/>
        </el-icon>
        <span>工单报修</span>
      </div>
      <div class="grid-item">
        <el-icon>
          <ChatLineSquare/>
        </el-icon>
        <span>联系我们</span>
      </div>
    </div>

    <div class="news-section">
      <div class="section-title">
        <h2>园区动态</h2>
        <el-button link>查看更多 >></el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card v-for="item in newsList" :key="item.id" class="news-item" shadow="never">
            <span class="news-tag">{{ item.type === 1 ? '政策' : '公告' }}</span>
            <span class="news-title">{{ item.title }}</span>
            <span class="news-date">{{ item.publishTime }}</span>
          </el-card>
        </el-col>
        <el-col :span="8">
          <div class="quick-contact">
            <h3>联系电话</h3>
            <p class="phone">0512-12345678</p>
            <p>办公时间：9:00 - 17:30</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import bannerImg from '@/assets/background.jpg'
import {ref, onMounted} from 'vue'
// 这里调用你现有的 noticeApi 即可实现数据同步
// const newsList = ref([])
</script>

<style scoped>
.banner-section {
  height: 400px;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.banner-mask {
  background: rgba(0, 0, 0, 0.4);
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.service-grid {
  display: flex;
  justify-content: space-around;
  padding: 40px 10%;
  background: #fff;
  margin-top: -50px; /* 向上悬浮效果 */
  position: relative;
  z-index: 10;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.3s;
}

.grid-item:hover {
  transform: translateY(-5px);
}

.grid-item .el-icon {
  font-size: 40px;
  color: #409EFF;
  margin-bottom: 10px;
}

.news-section {
  padding: 40px 10%;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #409EFF;
  margin-bottom: 20px;
}

.news-item {
  border-bottom: 1px dashed #eee;
  cursor: pointer;
}

.news-tag {
  color: #409EFF;
  margin-right: 10px;
  font-weight: bold;
}

.quick-contact {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.phone {
  font-size: 24px;
  color: #409EFF;
  font-weight: bold;
}
</style>