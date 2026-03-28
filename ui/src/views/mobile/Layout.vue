<template>
  <div class="m-layout">
    <div class="m-body">
      <router-view/>
    </div>

    <van-tabbar v-model="active" route active-color="#4f46e5">
      <van-tabbar-item icon="home-o" to="/home">门户首页</van-tabbar-item>

      <template v-if="userStore.roles.includes('ROLE_ENTERPRISE')">
        <van-tabbar-item icon="records" to="/m/enterprise/order">我的诉求</van-tabbar-item>
        <van-tabbar-item icon="hotel-o" to="/portal/my-enterprise">企业档案</van-tabbar-item>
      </template>

      <template v-if="userStore.roles.includes('ROLE_WORKER')">
        <van-tabbar-item icon="orders-o" to="/m/worker/list">任务大厅</van-tabbar-item>
      </template>

      <van-tabbar-item icon="user-o" to="/index">管理后台</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useUserStore} from '@/store/user';

const userStore = useUserStore();
const active = ref(0);
</script>

<style scoped>
.m-body {
  padding-bottom: 50px; /* 为 Tabbar 留出空间 */
}
</style>