<template>
  <div class="icon-select-container">
    <el-input
        v-model="filterValue"
        placeholder="搜索图标名称..."
        clearable
        class="search-input"
    >
      <template #prefix>
        <el-icon>
          <Search/>
        </el-icon>
      </template>
    </el-input>

    <div class="icon-list" v-if="filteredIcons.length > 0">
      <div
          v-for="item in filteredIcons"
          :key="item"
          class="icon-item"
          :class="{ active: modelValue === item }"
          @click="selectIcon(item)"
          :title="item"
      >
        <div class="icon-wrapper">
          <el-icon :size="22">
            <component :is="item"/>
          </el-icon>
        </div>
        <span class="icon-name">{{ item }}</span>
      </div>
    </div>

    <el-empty v-else :image-size="60" description="未找到相关图标"/>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import {Search} from '@element-plus/icons-vue'

// 优化：显式声明 v-model
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const allIcons = Object.keys(ElementPlusIconsVue)
const filterValue = ref('')

const filteredIcons = computed(() => {
  const keyword = filterValue.value.toLowerCase().trim()
  if (!keyword) return allIcons
  return allIcons.filter(name => name.toLowerCase().includes(keyword))
})

const selectIcon = (name) => {
  emit('update:modelValue', name)
}

onMounted(() => {
  // 只有在 modelValue 有值时才滚动
  if (props.modelValue) {
    nextTick(() => {
      const activeItem = document.querySelector('.icon-item.active');
      if (activeItem) {
        activeItem.scrollIntoView({ block: 'center', behavior: 'smooth' });
      }
    });
  }
});
</script>

<style scoped>
.icon-select-container {
  padding: 8px;
  background-color: #fff;
  border-radius: 8px;
}

/* 搜索框聚焦时的平滑过渡 */
.search-input :deep(.el-input__wrapper) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  max-height: 280px;
  overflow-y: auto;
  margin-top: 15px;
  padding: 4px;
}

/* 滚动条美化：更细、更现代 */
.icon-list::-webkit-scrollbar {
  width: 4px;
}

.icon-list::-webkit-scrollbar-thumb {
  background: #eef0f5;
  border-radius: 4px;
}

.icon-list::-webkit-scrollbar-track {
  background: transparent;
}

.icon-item {
  height: 72px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  background: #fafafa;
  transition: all 0.2s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.icon-wrapper {
  margin-bottom: 4px;
  transition: transform 0.2s;
}

/* 悬停效果：图标放大 + 整体上浮 */
.icon-item:hover {
  background-color: #fff;
  border-color: #409eff;
  color: #409eff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.icon-item:hover .icon-wrapper {
  transform: scale(1.15);
}

/* 选中态：色彩反转或高亮边框 */
.icon-item.active {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.icon-item.active .icon-name {
  color: #fff;
}

.icon-name {
  font-size: 11px;
  color: #666;
  width: 85%;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>