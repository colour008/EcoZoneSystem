<template>
  <div class="m-detail-container">
    <van-nav-bar title="任务详情" left-arrow @click-left="router.back()" fixed placeholder/>

    <div class="detail-body" v-if="task">
      <div class="section-card">
        <div class="status-banner" :class="'status-' + task.status">
          {{ task.status === 1 ? '待执行' : '已完工' }}
        </div>
        <h2 class="task-title">{{ task.title }}</h2>
        <p class="task-desc">{{ task.content }}</p>
        <van-image-preview v-model:show="showImgPreview" :images="taskImages"/>
        <div class="task-imgs" v-if="taskImages.length">
          <img v-for="img in taskImages" :key="img" :src="img" @click="showImgPreview = true"/>
        </div>
      </div>

      <div class="section-card feedback-form" v-if="task.status === 1">
        <div class="section-header">处理结果反馈</div>
        <van-form @submit="handleComplete">
          <van-field
              v-model="feedback.remark"
              rows="3"
              autosize
              label="处理说明"
              type="textarea"
              placeholder="请简要说明处理过程或结果..."
              required
          />
          <div class="upload-label">现场照片证明</div>
          <van-uploader v-model="fileList" :after-read="afterRead" multiple :max-count="3"/>

          <div class="submit-box">
            <van-button round block type="primary" native-type="submit" :loading="submitting">
              确认提交并结案
            </van-button>
          </div>
        </van-form>
      </div>

      <div class="section-card completed-info" v-else>
        <div class="section-header">完工反馈</div>
        <p><strong>反馈说明：</strong>{{ task.remark || '无' }}</p>
        <div class="task-imgs" v-if="task.images">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {showSuccessToast, showFailToast} from 'vant';
import workOrderApi from '@/api/workOrder';
import {uploadFile} from '@/utils/upload';

const route = useRoute();
const router = useRouter();
const task = ref(null);
const fileList = ref([]);
const submitting = ref(false);
const showImgPreview = ref(false);

const feedback = ref({
  id: route.params.id,
  remark: '',
  images: ''
});

const taskImages = computed(() => {
  return task.value?.images ? task.value.images.split(',') : [];
});

const afterRead = async (file) => {
  file.status = 'uploading';
  try {
    const url = await uploadFile(file.file);
    file.url = url;
    file.status = 'done';
  } catch {
    file.status = 'failed';
  }
};

const handleComplete = async () => {
  submitting.value = true;
  feedback.value.images = fileList.value.map(i => i.url).join(',');
  try {
    await workOrderApi.process(feedback.value);
    showSuccessToast('已完成任务');
    router.back();
  } catch (e) {
    showFailToast('操作失败');
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  // 注意：此处你可以根据实际 API 情况获取单条详情
  // 如果没有单条详情接口，通常从列表跳转带过来或通过列表接口过滤
  const res = await workOrderApi.getWorkerTaskPage({id: route.params.id});
  task.value = res.data.records[0];
});
</script>

<style scoped>
.m-detail-container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 40px;
}

.section-card {
  background: #fff;
  margin: 12px;
  padding: 20px;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.status-banner {
  position: absolute;
  right: -25px;
  top: 10px;
  transform: rotate(45deg);
  width: 100px;
  text-align: center;
  color: #fff;
  font-size: 11px;
  padding: 2px 0;
}

.status-1 {
  background: #f97316;
}

.status-2 {
  background: #10b981;
}

.task-title {
  font-size: 20px;
  color: #1e293b;
  margin-top: 0;
}

.task-desc {
  color: #64748b;
  line-height: 1.6;
  font-size: 14px;
}

.task-imgs {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.task-imgs img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.section-header {
  font-size: 16px;
  font-weight: bold;
  border-left: 4px solid #4f46e5;
  padding-left: 10px;
  margin-bottom: 15px;
}

.upload-label {
  font-size: 14px;
  color: #323233;
  margin: 15px 0 10px;
}

.submit-box {
  margin-top: 30px;
}
</style>