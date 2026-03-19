<template>
  <el-icon :size="size" :color="color" class="svg-icon">
    <component v-if="isElementIcon" :is="name"/>

    <Icon v-else :icon="name"/>
  </el-icon>
</template>

<script setup>
import {computed} from 'vue'
import {Icon} from '@iconify/vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  size: [Number, String],
  color: String
})

// 判断是否为 Element Plus 的图标
const isElementIcon = computed(() => {
  return Object.keys(ElementPlusIconsVue).includes(props.name)
})
</script>

<style scoped>
.svg-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}
</style>