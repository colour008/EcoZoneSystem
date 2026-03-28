<template>
  <div class="m-order-container">
    <van-nav-bar title="我的诉求列表" fixed placeholder/>

    <van-tabs v-model:active="activeStatus" @change="onTabChange" sticky offset-top="46px">
      <van-tab title="全部" name="null"/>
      <van-tab title="待处理" name="0"/>
      <van-tab title="处理中" name="1"/>
      <van-tab title="已完成" name="2"/>
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
          class="m-order-list"
      >
        <div v-for="item in list" :key="item.id" class="order-card">
          <div class="card-head">
            <span class="order-id">单号：{{ item.id }}</span>
            <van-tag :type="statusTagMap[item.status]" size="medium">{{ statusTextMap[item.status] }}</van-tag>
          </div>

          <div class="card-body">
            <div class="order-type">[{{ typeMap[item.type] }}]</div>
            <div class="order-title">{{ item.title }}</div>
            <div class="order-time">{{ item.createTime }}</div>
          </div>

          <div class="card-foot" v-if="item.status === 2">
            <van-button size="small" plain round type="primary" @click="goEvaluate(item)">评价服务</van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <div class="fab-btn" @click="router.push('/m/enterprise/apply')">
      <van-icon name="plus" size="24"/>
      <span>发起</span>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import workOrderApi from '@/api/workOrder';

const router = useRouter();
const activeStatus = ref('null');
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);

const typeMap = {1: '维修', 2: '咨询', 3: '投诉'};
const statusTextMap = {0: '待受理', 1: '处理中', 2: '已办结', 3: '已评价'};
const statusTagMap = {0: 'default', 1: 'warning', 2: 'success', 3: 'primary'};

const onLoad = async () => {
  if (refreshing.value) {
    list.value = [];
    refreshing.value = false;
  }

  try {
    const res = await workOrderApi.getMyPage({
      pageNum: pageNum.value,
      pageSize: 10,
      status: activeStatus.value === 'null' ? null : Number(activeStatus.value)
    });

    list.value.push(...res.data.records);
    pageNum.value++;

    if (list.value.length >= res.data.total) {
      finished.value = true;
    }
  } finally {
    loading.value = false;
  }
};

const onRefresh = () => {
  finished.value = false;
  pageNum.value = 1;
  onLoad();
};

const onTabChange = () => {
  onRefresh();
};
</script>

<style scoped>
.m-order-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.m-order-list {
  padding: 12px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f2f3f5;
  padding-bottom: 10px;
  margin-bottom: 12px;
}

.order-id {
  font-size: 12px;
  color: #969799;
}

.card-body .order-type {
  font-size: 13px;
  color: #4f46e5;
  font-weight: 600;
  margin-bottom: 4px;
}

.card-body .order-title {
  font-size: 15px;
  font-weight: 500;
  color: #323233;
  margin-bottom: 8px;
}

.card-body .order-time {
  font-size: 12px;
  color: #969799;
}

.card-foot {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.fab-btn {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #4f46e5, #3b82f6);
  border-radius: 50%;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.4);
  font-size: 10px;
  z-index: 99;
}
</style>