<template>
  <div
      class="notice-item"
      :class="{ 'is-read': item.isRead === 1 }"
      @click="$emit('click', item)"
  >
    <div class="content-wrapper">
      <div class="title-row">
        <span v-if="isLoggedIn && !item.isRead" class="unread-dot"></span>
        <h3 class="title">{{ item.title }}</h3>
      </div>

      <p class="summary">{{ item.summary || '暂无摘要描述...' }}</p>

      <div class="meta-info">
        <span class="time">
          <el-icon><Calendar/></el-icon>
          {{ formatDate(item.publishTime, 'yyyy-MM-dd') }}
        </span>
        <span class="view-count">
          <el-icon><View/></el-icon>
          {{ item.viewCount || 0 }} 次阅读
        </span>
      </div>
    </div>

    <div v-if="item.coverUrl" class="cover-wrapper">
      <el-image
          :src="item.coverUrl"
          fit="cover"
          class="cover-img"
          lazy
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon>
              <Picture/>
            </el-icon>
          </div>
        </template>
      </el-image>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {Calendar, View, Picture} from '@element-plus/icons-vue'
import {formatDate} from '@/utils/date'
import {useUserStore} from "@/store/user.js"

defineProps({
  item: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])

const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.token)

</script>

<style scoped>
.notice-item {
  display: flex;
  padding: 20px;
  background: #fff;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s ease;
  gap: 20px;
}

.notice-item:hover {
  background-color: #fafafa;
}

.content-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.title-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background-color: #f56c6c;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}

.title {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 8px 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.cover-wrapper {
  width: 160px;
  height: 100px;
  flex-shrink: 0;
}

.cover-img {
  width: 100%;
  height: 100%;
  border-radius: 4px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 24px;
}

@media (max-width: 768px) {
  .notice-item {
    flex-direction: column-reverse;
  }

  .cover-wrapper {
    width: 100%;
    height: 180px;
  }
}
</style>