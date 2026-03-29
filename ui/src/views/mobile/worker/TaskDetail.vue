<template>
  <div class="m-detail-container">
    <van-nav-bar title="工单详情" left-arrow @click-left="router.back()" fixed placeholder/>

    <div class="detail-body" v-if="task">
      <div class="section-card status-card">
        <div class="status-banner" :class="'status-' + task.status">
          {{ statusMap[task.status]?.label }}
        </div>
        <div class="order-no">单号：{{ task.orderNo }}</div>
        <h2 class="task-title">{{ task.title }}</h2>
        <div class="type-tag-box">
          <van-tag :type="typeMap[task.type]?.tag" size="medium">{{ typeMap[task.type]?.label }}</van-tag>
          <span class="create-time">{{ task.createTime }} 提报</span>
        </div>
      </div>

      <div class="section-card info-card">
        <div class="section-header">基本信息</div>
        <van-cell-group :border="false">
          <van-cell title="提报企业" :value="task.enterpriseName"/>
          <van-cell title="联系人员" :value="task.contactPerson"/>
          <van-cell title="联系电话" :value="task.contactPhone" @click="handleCall(task.contactPhone)">
            <template #value>
              <span class="phone-link">{{ task.contactPhone }}</span>
            </template>
          </van-cell>
          <van-cell title="受理专员" :value="task.handlerName || '系统自动'"/>
          <van-cell title="处理人员" :value="task.workerName || '-'"/>
        </van-cell-group>
      </div>

      <div class="section-card content-card">
        <div class="section-header">诉求内容</div>
        <div class="rich-content-view">{{ task.content || '无详情说明' }}</div>

        <div class="task-imgs" v-if="taskImages.length">
          <div class="img-wrapper" v-for="(img, index) in taskImages" :key="index">
            <van-image
                width="80"
                height="80"
                radius="8"
                fit="cover"
                :src="img"
                @click="handlePreviewTask(index)"
            />
          </div>
        </div>
        <van-image-preview v-model:show="showImgPreview" :images="taskImages" :start-position="startPos"/>
      </div>

      <div class="section-card timeline-card">
        <div class="section-header">处理进度</div>
        <van-steps direction="vertical" :active="task.status" active-color="#4f46e5">
          <van-step>
            <h3>企业提交诉求</h3>
            <p>{{ task.createTime }}</p>
          </van-step>
          <van-step v-if="task.acceptTime">
            <h3>园区受理指派</h3>
            <p>{{ task.acceptTime }}</p>
            <p class="step-detail">受理人: {{ task.handlerName }}</p>
          </van-step>
          <van-step v-if="task.finishTime">
            <h3>工单处理完成</h3>
            <p>{{ task.finishTime }}</p>
            <p class="step-detail" v-if="task.remark">反馈: {{ task.remark }}</p>
          </van-step>
          <van-step v-if="task.status === 3">
            <h3>企业已评价</h3>
            <p>评分: {{ task.score }}星</p>
          </van-step>
        </van-steps>
      </div>

      <div class="section-card feedback-form" v-if="task.status === 1">
        <div class="section-header">处理结果反馈</div>
        <van-field
            v-model="feedback.remark"
            rows="3"
            autosize
            label="处理说明"
            type="textarea"
            placeholder="请详细描述处理结果，该反馈将对企业可见..."
            required
        />
        <div class="upload-label">现场处理图片附件</div>
        <van-uploader
            v-model="fileList"
            :after-read="afterRead"
            multiple
            :max-count="3"
            upload-icon="plus"
        />

        <div class="submit-box">
          <van-button round block type="primary" @click="handleComplete" :loading="submitting">
            提交反馈并办结
          </van-button>
        </div>
      </div>

      <div class="section-card completed-info" v-if="task.status >= 2">
        <div class="section-header">完工反馈</div>
        <div class="feedback-content">
          <p><strong>反馈结果：</strong>{{ task.remark || '未填写说明' }}</p>
          <div class="task-imgs" v-if="finishImages.length">
            <div class="img-wrapper" v-for="(img, index) in finishImages" :key="index">
              <van-image
                  width="80"
                  height="80"
                  radius="8"
                  fit="cover"
                  :src="img"
                  @click="handlePreviewFinish(index)"
              />
            </div>
          </div>
        </div>
        <van-image-preview v-model:show="showFinishImgPreview" :images="finishImages" :start-position="startPos"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {showSuccessToast, showFailToast, showLoadingToast, closeToast} from 'vant';
import workOrderApi from '@/api/workOrder';
import {uploadFile} from '@/utils/upload';

const route = useRoute();
const router = useRouter();
const task = ref(null);
const fileList = ref([]);
const submitting = ref(false);
const showImgPreview = ref(false);
const showFinishImgPreview = ref(false);
const startPos = ref(0);

const typeMap = {
  1: {label: '维修报备', tag: 'warning'},
  2: {label: '业务咨询', tag: 'primary'},
  3: {label: '投诉建议', tag: 'danger'}
};

const statusMap = {
  0: {label: '待受理', tag: 'warning'},
  1: {label: '处理中', tag: 'primary'},
  2: {label: '已办结', tag: 'success'},
  3: {label: '已评价', tag: 'default'}
};

const feedback = ref({
  id: route.params.id,
  remark: '',
  finishAttachments: ''
});

// 计算属性：报修图片（兼容后端 List 和 String）
const taskImages = computed(() => {
  if (task.value?.imageList?.length > 0) return task.value.imageList;
  if (typeof task.value?.images === 'string' && task.value.images) return task.value.images.split(',');
  return [];
});

// 计算属性：处理反馈图片（兼容后端 List 和 String）
const finishImages = computed(() => {
  if (task.value?.finishAttachmentList?.length > 0) return task.value.finishAttachmentList;
  if (typeof task.value?.finishAttachments === 'string' && task.value.finishAttachments) {
    return task.value.finishAttachments.split(',');
  }
  return [];
});

const handlePreviewTask = (index) => {
  startPos.value = index;
  showImgPreview.value = true;
};

const handlePreviewFinish = (index) => {
  startPos.value = index;
  showFinishImgPreview.value = true;
};

const handleCall = (phone) => {
  if (phone) window.location.href = `tel:${phone}`;
};

const afterRead = async (fileItem) => {
  const files = Array.isArray(fileItem) ? fileItem : [fileItem];
  for (let item of files) {
    item.status = 'uploading';
    item.message = '上传中...';
    try {
      const url = await uploadFile(item.file);
      item.url = url;
      item.status = 'done';
    } catch (e) {
      item.status = 'failed';
      item.message = '上传失败';
    }
  }
};

const handleComplete = async () => {
  if (!feedback.value.remark) {
    showFailToast('请填写处理说明');
    return;
  }
  if (fileList.value.some(f => f.status === 'uploading')) {
    showFailToast('图片正在上传中');
    return;
  }

  submitting.value = true;
  try {
    feedback.value.finishAttachments = fileList.value
        .filter(i => i.status === 'done' && i.url)
        .map(i => i.url)
        .join(',');

    await workOrderApi.process(feedback.value);
    showSuccessToast('已处理结案');
    setTimeout(() => {
      router.back();
    }, 1000);
  } catch (e) {
    showFailToast(e.message || '提交失败');
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  showLoadingToast({message: '详情加载中...', forbidClick: true});
  try {
    // 路径改为 /work-order/${id}
    const res = await workOrderApi.getById(route.params.id);
    if (res.code === 200 && res.data) {
      task.value = res.data;
    } else {
      // 降级策略
      const fallbackRes = await workOrderApi.getWorkerTaskPage({id: route.params.id});
      if (fallbackRes.data?.records.length) {
        task.value = fallbackRes.data.records.find(r => String(r.id) === String(route.params.id));
      }
    }
    if (!task.value) showFailToast('未找到工单信息');
  } catch (e) {
    showFailToast('加载失败');
    console.error(e);
  } finally {
    closeToast();
  }
});
</script>

<style scoped>
.m-detail-container {
  min-height: 100vh;
  background: #f4f6f8;
  padding-bottom: 40px;
}

.section-card {
  background: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.status-banner {
  position: absolute;
  right: -30px;
  top: 12px;
  transform: rotate(45deg);
  width: 110px;
  text-align: center;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  padding: 3px 0;
  z-index: 10;
}

.status-0 {
  background: #f97316;
}

.status-1 {
  background: #4f46e5;
}

.status-2 {
  background: #10b981;
}

.status-3 {
  background: #94a3b8;
}

.order-no {
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 8px;
}

.task-title {
  font-size: 18px;
  color: #1e293b;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.type-tag-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.create-time {
  font-size: 12px;
  color: #64748b;
}

.section-header {
  font-size: 15px;
  font-weight: bold;
  color: #1e293b;
  border-left: 4px solid #4f46e5;
  padding-left: 8px;
  margin-bottom: 15px;
}

.rich-content-view {
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  white-space: pre-wrap;
  margin-bottom: 12px;
}

.task-imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.phone-link {
  color: #4f46e5;
  text-decoration: underline;
}

.step-detail {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

.upload-label {
  font-size: 14px;
  color: #323233;
  margin: 15px 0 10px;
}

.feedback-content {
  background: #f0fdf4;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #166534;
}

.feedback-content p {
  margin-bottom: 8px;
}

.submit-box {
  margin-top: 20px;
}

:deep(.van-cell) {
  padding: 10px 0;
}

:deep(.van-cell__title) {
  color: #64748b;
}

:deep(.van-cell__value) {
  color: #1e293b;
  font-weight: 500;
}
</style>