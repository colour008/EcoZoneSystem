<template>
  <div class="m-apply-container">
    <van-nav-bar title="发起服务诉求" left-arrow @click-left="router.back()" fixed placeholder/>

    <div class="m-content">
      <div class="m-notice-card">
        <van-icon name="info-o" size="18"/>
        <span>请详细描述您的诉求，园区管委会将在 24 小时内受理。</span>
      </div>

      <van-form @submit="onSubmit">
        <van-cell-group inset class="m-form-group">
          <van-field
              v-model="typeName"
              is-link
              readonly
              name="picker"
              label="诉求类型"
              placeholder="点击选择类型"
              @click="showTypePicker = true"
              required
          />

          <van-field
              v-model="form.title"
              label="诉求标题"
              placeholder="请简述您的问题"
              required
              maxlength="20"
              show-word-limit
          />

          <van-field
              v-model="form.content"
              rows="4"
              autosize
              label="详细描述"
              type="textarea"
              placeholder="请详细说明您的具体需求或故障情况..."
              required
          />
        </van-cell-group>

        <van-cell-group inset class="m-form-group" title="现场照片 (可选)">
          <div class="m-upload-box">
            <van-uploader
                v-model="fileList"
                multiple
                :max-count="4"
                :after-read="afterRead"
                upload-text="点击上传"
            />
          </div>
        </van-cell-group>

        <van-cell-group inset class="m-form-group" title="联系方式">
          <van-field v-model="form.contactPerson" label="联系人" placeholder="请输入姓名" required/>
          <van-field v-model="form.contactPhone" type="tel" label="手机号" placeholder="请输入联系电话" required/>
        </van-cell-group>

        <div class="m-submit-bar">
          <van-button
              round
              block
              type="primary"
              native-type="submit"
              class="m-gradient-btn"
              :loading="submitting"
          >
            提交诉求
          </van-button>
        </div>
      </van-form>
    </div>

    <van-popup v-model:show="showTypePicker" position="bottom" round>
      <van-picker
          :columns="typeColumns"
          @confirm="onConfirmType"
          @cancel="showTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import {showToast, showSuccessToast} from 'vant';
import workOrderApi from '@/api/workOrder';
import enterpriseApi from '@/api/enterprise';
import {uploadFile} from '@/utils/upload';

const router = useRouter();
const submitting = ref(false);
const showTypePicker = ref(false);
const fileList = ref([]);
const typeName = ref('');

const form = reactive({
  type: 1,
  title: '',
  content: '',
  contactPerson: '',
  contactPhone: '',
  images: '',
  enterpriseId: null
});

const typeColumns = [
  {text: '维修服务', value: 1},
  {text: '政策咨询', value: 2},
  {text: '投诉建议', value: 3},
];

const onConfirmType = ({selectedOptions}) => {
  form.type = selectedOptions[0].value;
  typeName.value = selectedOptions[0].text;
  showTypePicker.value = false;
};

const afterRead = async (file) => {
  file.status = 'uploading';
  file.message = '上传中...';
  try {
    const url = await uploadFile(file.file);
    file.status = 'done';
    file.url = url;
  } catch (e) {
    file.status = 'failed';
    file.message = '上传失败';
  }
};

const onSubmit = async () => {
  // 组装图片
  form.images = fileList.value.filter(i => i.url).map(i => i.url).join(',');

  submitting.value = true;
  try {
    await workOrderApi.submit(form);
    showSuccessToast('提报成功');
    router.replace('/m/enterprise/order');
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  const res = await enterpriseApi.getMyEnterprise();
  if (res.data) {
    form.enterpriseId = res.data.id;
    form.contactPerson = res.data.contactPerson;
    form.contactPhone = res.data.contactPhone;
  }
});
</script>

<style scoped>
.m-apply-container {
  min-height: 100vh;
  background: #f7f8fa;
}

.m-content {
  padding-bottom: 100px;
}

.m-notice-card {
  margin: 12px;
  padding: 12px 16px;
  background: #eef2ff;
  color: #4f46e5;
  border-radius: 8px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.m-form-group {
  margin-top: 12px !important;
  overflow: hidden;
}

.m-upload-box {
  padding: 16px;
}

.m-submit-bar {
  margin: 32px 16px 16px;
}

.m-gradient-btn {
  background: linear-gradient(90deg, #4f46e5 0%, #3b82f6 100%) !important;
  border: none;
  font-weight: 600;
}

:deep(.van-nav-bar__title) {
  font-weight: 600;
}
</style>