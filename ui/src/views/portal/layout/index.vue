<template>
  <div class="portal-wrapper">
    <header class="portal-header">
      <div class="logo">经济开发区门户</div>
      <nav class="nav-menu">
        <router-link to="/home">首页</router-link>
        <router-link to="/policy">政策法规</router-link>
      </nav>
      <div class="user-action">
        <el-button v-if="!hasToken" type="primary" @click="$router.push('/login')">登录 / 注册</el-button>
        <el-button v-else type="success" @click="$router.push('/index/dashboard')">进入控制台</el-button>
      </div>
    </header>

    <main class="portal-main">
      <router-view/>
    </main>

    <footer class="portal-footer">
      <p>© 2026 经济开发区管理委员会 版权所有</p>
    </footer>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useUserStore} from '@/store/user'

const userStore = useUserStore()
const hasToken = computed(() => !!userStore.token)
</script>

<style scoped>
.portal-header {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 50px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 999;
}

.nav-menu a {
  margin: 0 15px;
  text-decoration: none;
  color: #333;
}

.nav-menu a.router-link-active {
  color: #409EFF;
  font-weight: bold;
}

.portal-main {
  min-height: calc(100vh - 120px);
  background: #f5f7fa;
}

.portal-footer {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background: #2b333e;
  color: #a0a5ab;
}
</style>