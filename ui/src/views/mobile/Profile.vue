<template>
  <div class="m-profile-container">
    <van-nav-bar title="个人中心" fixed placeholder/>

    <div class="user-header-section">
      <div class="header-bg-decoration"></div>
      <div class="user-info-card">
        <div class="user-main">
          <div class="avatar-wrapper">
            <van-image
                round
                width="72"
                height="72"
                fit="cover"
                :src="userInfo.avatar || '/DefaultUser.svg'"
                class="user-avatar"
            />
            <div class="edit-badge" @click="openEdit">
              <van-icon name="photograph"/>
            </div>
          </div>
          <div class="user-text">
            <div class="nickname-row">
              <span class="nickname">{{ userInfo.realName || '未知用户' }}</span>
              <van-tag
                  v-for="role in userStore.roles"
                  :key="role"
                  class="role-tag"
              >
                {{ role === 'ROLE_ADMIN' ? '管理员' : role === 'ROLE_WORKER' ? '工作人员' : '企业用户' }}
              </van-tag>
            </div>
            <div class="username-id">账号: {{ userInfo.username }}</div>
            <div style="font-size: 13px; opacity: 0.85;">联系电话: {{ userInfo.phone }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="content-body">
      <div class="menu-group">
        <div class="group-title">账号设置</div>
        <van-cell-group inset :border="false">
          <van-cell is-link center @click="openEdit">
            <template #title><span class="cell-label">个人资料修改</span></template>
            <template #icon>
              <div class="icon-box color-1">
                <van-icon name="contact"/>
              </div>
            </template>
          </van-cell>
          <van-cell is-link center @click="openPasswordEdit">
            <template #title><span class="cell-label">安全密码设置</span></template>
            <template #icon>
              <div class="icon-box color-2">
                <van-icon name="shield-o"/>
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <div class="menu-group">
        <div class="group-title">系统服务</div>
        <van-cell-group inset :border="false">
          <van-cell is-link center @click="goToAdmin">
            <template #title><span class="cell-label">进入管理后台</span></template>
            <template #value><span class="cell-sub-val">PC版</span></template>
            <template #icon>
              <div class="icon-box color-3">
                <van-icon name="desktop-o"/>
              </div>
            </template>
          </van-cell>
          <van-cell is-link center>
            <template #title><span class="cell-label">关于平台</span></template>
            <template #value><span class="cell-sub-val">v1.2.0</span></template>
            <template #icon>
              <div class="icon-box color-4">
                <van-icon name="info-o"/>
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <div class="logout-section">
        <van-button block round class="custom-logout-btn" @click="handleLogout">
          退出当前账号
        </van-button>
      </div>
    </div>

    <van-popup
        v-model:show="showEdit"
        position="bottom"
        round
        class="modern-popup"
        :style="{ height: 'auto', minHeight: '40%' }"
    >
      <div class="popup-header">修改个人资料</div>
      <van-form @submit="submitProfile" class="popup-form">
        <van-cell-group :border="false">
          <van-field label="账号" :model-value="profileForm.username" disabled input-align="right" required/>
          <van-field
              v-model="profileForm.realName"
              label="姓名"
              placeholder="请输入您的姓名"
              required
              input-align="right"
          />
          <van-field
              v-model="profileForm.phone"
              label="手机号码"
              placeholder="请输入手机号"
              required
              type="tel"
              input-align="right"
          />
          <van-cell title="更换头像" center>
            <template #value>
              <van-uploader :after-read="handleAvatarUpload" max-count="1">
                <van-image
                    round
                    width="44"
                    height="44"
                    fit="cover"
                    :src="profileForm.avatar || '/DefaultUser.svg'"
                    class="form-avatar"
                />
              </van-uploader>
            </template>
          </van-cell>
        </van-cell-group>
        <div class="popup-footer">
          <van-button round block type="primary" native-type="submit" :loading="loading" class="submit-btn">
            同步资料更新
          </van-button>
        </div>
      </van-form>
    </van-popup>

    <van-popup
        v-model:show="showPwdEdit"
        position="bottom"
        round
        class="modern-popup"
    >
      <div class="popup-header">安全设置</div>
      <van-form @submit="submitPassword" class="popup-form">
        <van-cell-group :border="false">
          <van-field
              v-model="pwdForm.password"
              type="password"
              label="新密码"
              placeholder="不少于6位新密码"
              required
          />
          <van-field
              v-model="pwdForm.confirmPassword"
              type="password"
              label="确认新密码"
              placeholder="请再次确认密码"
              required
          />
        </van-cell-group>
        <div class="popup-footer">
          <van-button round block type="warning" native-type="submit" :loading="loading" class="submit-btn">
            确认重置密码
          </van-button>
        </div>
      </van-form>
    </van-popup>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue';
import {useRouter} from 'vue-router';
import {useUserStore} from '@/store/user';
import {showConfirmDialog, showSuccessToast, showFailToast, showLoadingToast, closeToast} from 'vant';
import userApi from '@/api/user';
import {uploadFile} from '@/utils/upload';

const router = useRouter();
const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);

const loading = ref(false);
const showEdit = ref(false);
const showPwdEdit = ref(false);

const profileForm = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  avatar: ''
});

const pwdForm = ref({
  password: '',
  confirmPassword: ''
});

const openEdit = () => {
  const {id, username, realName, phone, avatar} = userStore.userInfo;
  profileForm.value = {id, username, realName, phone, avatar};
  showEdit.value = true;
};

const openPasswordEdit = () => {
  pwdForm.value = {password: '', confirmPassword: ''};
  showPwdEdit.value = true;
};

const handleAvatarUpload = async (fileItem) => {
  showLoadingToast('上传中...');
  try {
    const url = await uploadFile(fileItem.file);
    profileForm.value.avatar = url;
    showSuccessToast('上传成功');
  } catch (e) {
    showFailToast('图片上传失败');
  } finally {
    closeToast();
  }
};

const submitProfile = async () => {
  if (!profileForm.value.realName) return showFailToast('姓名不能为空');
  loading.value = true;
  try {
    await userApi.updateProfile(profileForm.value);
    userStore.setUserInfo({
      ...userStore.userInfo,
      realName: profileForm.value.realName,
      phone: profileForm.value.phone,
      avatar: profileForm.value.avatar
    });
    showSuccessToast('资料已同步');
    showEdit.value = false;
  } catch (e) {
    showFailToast(e.message || '更新失败');
  } finally {
    loading.value = false;
  }
};

const submitPassword = async () => {
  if (pwdForm.value.password.length < 6) return showFailToast('密码长度至少6位');
  if (pwdForm.value.password !== pwdForm.value.confirmPassword) return showFailToast('两次输入密码不一致');
  loading.value = true;
  try {
    await userApi.updateProfile({
      id: userStore.userInfo.id,
      password: pwdForm.value.password
    });
    showSuccessToast('重置成功，请重新登录');
    userStore.logout();
    router.push('/login');
  } catch (e) {
    showFailToast(e.message || '修改失败');
  } finally {
    loading.value = false;
  }
};

const goToAdmin = () => router.push('/index');

const handleLogout = () => {
  showConfirmDialog({
    title: '安全退出',
    message: '退出后将返回登录界面，确认吗？',
    confirmButtonColor: '#ee0a24'
  }).then(() => {
    userStore.logout();
    router.push('/login');
  }).catch(() => {
  });
};
</script>

<style scoped>
/* 容器 */
.m-profile-container {
  min-height: 100vh;
  background-color: #f7f8fa;
}


/* 头部视觉区域 */
.user-header-section {
  position: relative;
  background: linear-gradient(135deg, rgb(25, 25, 25) 0%, rgb(44, 44, 44) 100%);
  height: 140px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.header-bg-decoration {
  position: absolute;
  top: -10%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  filter: blur(40px);
}

.user-info-card {
  width: 100%;
  z-index: 1;
}

.user-main {
  display: flex;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  padding: 2px;
  background: rgba(103, 103, 103, 0.3);
  border-radius: 50%;
}

.user-avatar {
  display: block;
}

.edit-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #ffffff;
  color: #4f46e5;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-text {
  margin-left: 18px;
  color: #ffffff;
}

.nickname-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.nickname {
  font-size: 20px;
  font-weight: 600;
}

.role-tag {
  background: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  color: #ffffff !important;
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 5px;
}

.username-id {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 5px;
}

/* 主体列表内容 */
.content-body {
  position: relative;
  margin-top: 10px;
  padding: 0 4px 40px;
}

.menu-group {
  margin-bottom: 20px;
}

.group-title {
  padding: 0 20px 8px;
  font-size: 13px;
  color: #8e9297;
  font-weight: 500;
}

:deep(.van-cell-group--inset) {
  margin: 0 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

:deep(.van-cell) {
  padding: 14px 16px;
}

.icon-box {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 18px;
  color: #ffffff;
}

.color-1 {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.color-2 {
  background: linear-gradient(135deg, #10b981, #059669);
}

.color-3 {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.color-4 {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
}

.cell-label {
  font-weight: 500;
  color: #323233;
}

.cell-sub-val {
  color: #969799;
  font-size: 13px;
}

/* 底部按钮 */
.logout-section {
  margin: 40px 16px 0;
}

.custom-logout-btn {
  border: none;
  background-color: #ffffff;
  color: #ee0a24;
  font-weight: 600;
  height: 50px;
  box-shadow: 0 2px 10px rgba(238, 10, 36, 0.05);
}

/* 弹窗样式美化 */
.modern-popup {
  padding-bottom: env(safe-area-inset-bottom);
}

.popup-header {
  text-align: center;
  font-size: 18px;
  font-weight: 700;
  padding: 24px 0 16px;
  color: #1a1a1a;
}

.popup-form {
  padding: 0 10px 20px;
}

.form-avatar {
  border: 1px solid #f2f3f5;
}

.popup-footer {
  margin: 32px 16px 12px;
}

.submit-btn {
  height: 48px;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 8px 16px rgba(79, 70, 229, 0.2);
}

:deep(.van-field__label) {
  color: #646566;
  font-weight: 500;
}
</style>