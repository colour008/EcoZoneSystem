<template>
  <div class="portal-layout">
    <Header/>

    <main class="portal-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </main>

    <Footer/>

    <el-backtop :right="40" :bottom="40"/>
  </div>
</template>

<script setup>
import Header from './Header.vue'
import Footer from './Footer.vue'
</script>

<style scoped>
.portal-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* 保证页脚始终在最下面 */
}

.portal-main {
  flex: 1; /* 自动撑开剩余空间 */
  padding-top: 70px; /* 留出 Header 的高度，防止内容被遮挡 */
  background-color: #f5f7fa;
}

/* 页面切换动画：让交互更现代化 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* ===================== 统一移动端响应式适配 ===================== */
/* 平板适配 */
@media (max-width: 1024px) {
  .portal-main {
    padding-top: 60px;
  }
}

/* 手机适配 */
@media (max-width: 768px) {
  .portal-main {
    padding-top: 60px;
  }

  .el-backtop {
    right: 20px !important;
    bottom: 20px !important;
  }
}

/* 小屏手机适配 */
@media (max-width: 375px) {
  .portal-main {
    padding-top: 60px;
  }
}
</style>