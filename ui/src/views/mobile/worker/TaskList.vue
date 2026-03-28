<template>
  <div class="m-worker-container">
    <van-nav-bar title="待办任务大厅" fixed placeholder/>

    <van-tabs v-model:active="activeStatus" @change="onRefresh" sticky offset-top="46px">
      <van-tab title="待处理" name="1"/>
      <van-tab title="已完成" name="2"/>
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多任务了"
          @load="onLoad"
          class="m-task-list"
      >
        <div
            v-for="item in list"
            :key="item.id"
            class="task-card"
            @click="router.push(`/m/worker/detail/${item.id}`)"
        >
          <div class="card-header">
            <span class="type-tag" :class="'type-' + item.type">{{ typeMap[item.type] }}</span>
            <span class="time">{{ item.createTime }}</span>
          </div>

          <div class="card-content">
            <h3 class="title">{{ item.title }}</h3>
            <div class="info-row">
              <van-icon name="location-o"/>
              <span>{{ item.enterpriseName || '申请企业' }} ({{ item.buildingNo || '暂无楼宇' }})</span>
            </div>
            <div class="info-row">
              <van-icon name="contact-o"/>
              <span>联系人：{{ item.contactPerson }} {{ item.contactPhone }}</span>
            </div>
          </div>

          <div class="card-footer">
            <span class="status-dot" :class="item.status === 1 ? 'active' : ''"></span>
            <span class="status-text">{{ item.status === 1 ? '处理中' : '已结案' }}</span>
            <van-button
                size="small"
                round
                type="primary"
                class="action-btn"
                v-if="item.status === 1"
            >
              立即处理
            </van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import workOrderApi from '@/api/workOrder';

const router = useRouter();
const activeStatus = ref('1');
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);

const typeMap = {1: '报修', 2: '咨询', 3: '投诉'};

const onLoad = async () => {
  try {
    const res = await workOrderApi.getWorkerTaskPage({
      pageNum: pageNum.value,
      pageSize: 10,
      status: Number(activeStatus.value)
    });

    if (refreshing.value) {
      list.value = [];
      refreshing.value = false;
    }

    list.value.push(...res.data.records);
    pageNum.value++;

    if (list.value.length >= res.data.total) {
      finished.value = true;
    }
  } catch (e) {
    finished.value = true;
  } finally {
    loading.value = false;
  }
};

const onRefresh = () => {
  finished.value = false;
  pageNum.value = 1;
  onLoad();
};
</script>

<style scoped>
.m-worker-container {
  min-height: 100vh;
  background: #f4f6f8;
}

.m-task-list {
  padding: 12px;
}

.task-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.type-tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: bold;
}

.type-1 {
  background: #eef2ff;
  color: #4f46e5;
}

.type-2 {
  background: #fff7ed;
  color: #f97316;
}

.type-3 {
  background: #fef2f2;
  color: #ef4444;
}

.time {
  color: #94a3b8;
  font-size: 12px;
}

.card-content .title {
  font-size: 16px;
  color: #1e293b;
  margin: 0 0 10px 0;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 13px;
  margin-bottom: 6px;
}

.card-footer {
  display: flex;
  align-items: center;
  border-top: 1px dashed #e2e8f0;
  padding-top: 12px;
  margin-top: 12px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
  margin-right: 6px;
}

.status-dot.active {
  background: #10b981;
  animation: pulse 2s infinite;
}

.status-text {
  flex: 1;
  font-size: 13px;
  color: #64748b;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 6px rgba(16, 185, 129, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(16, 185, 129, 0);
  }
}
</style>