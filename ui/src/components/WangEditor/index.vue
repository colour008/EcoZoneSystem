<template>
  <div class="editor-wrapper">
    <Toolbar
        class="editor-toolbar"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        mode="default"
    />
    <Editor
        class="editor-content"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        mode="default"
        @onCreated="handleCreated"
        @onChange="handleChange"
    />
    <div class="editor-footer">
      <span>字数统计: {{ wordCount }} 字</span>
    </div>
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css'
import {onBeforeUnmount, ref, shallowRef, watch, computed} from 'vue'
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import {uploadFile} from "@/utils/upload.js"
import {ElMessage} from 'element-plus'

const props = defineProps({
  modelValue: {type: String, default: ''},
  placeholder: {type: String, default: '请输入详细内容...'},
  height: {type: String, default: '400px'}
})

const emit = defineEmits(['update:modelValue'])

const editorRef = shallowRef()
const valueHtml = ref('')

// 1. 同步数据
watch(() => props.modelValue, (newVal) => {
  if (newVal === valueHtml.value) return
  valueHtml.value = newVal
}, {immediate: true})

// 2. 工具栏配置
const toolbarConfig = {
  // 如果需要精简菜单，可以在这里 excludeKeys
  excludeKeys: []
}

// 3. 编辑器全功能配置
const editorConfig = {
  placeholder: props.placeholder,
  autoFocus: false,
  scroll: true,
  MENU_CONF: {
    // --- 图片上传配置 ---
    uploadImage: {
      async customUpload(file, insertFn) {
        // 校验大小 (e.g. 5MB)
        if (file.size > 5 * 1024 * 1024) {
          ElMessage.error('图片大小不能超过 5MB')
          return
        }
        try {
          const url = await uploadFile(file)
          // 参数说明：url, alt, href
          insertFn(url, file.name, url)
        } catch (e) {
          ElMessage.error('图片上传失败')
        }
      }
    },
    // --- 视频上传配置 (新增) ---
    uploadVideo: {
      async customUpload(file, insertFn) {
        if (file.size > 100 * 1024 * 1024) {
          ElMessage.error('视频不能超过 100MB')
          return
        }
        try {
          const url = await uploadFile(file)
          // insertFn 的参数：url, poster
          insertFn(url)
          ElMessage.success('视频上传成功，已自动优化展示尺寸')
        } catch (e) {
          ElMessage.error('视频上传失败')
        }
      }
    }
  }
}

// 4. 计算字数
const wordCount = ref(0)

const handleCreated = (editor) => {
  editorRef.value = editor
}

const handleChange = (editor) => {
  const html = editor.getHtml()
  const text = editor.getText().replace(/\s/g, '') // 纯文本字数
  wordCount.value = text.length

  const isEmpty = html === '<p><br></p>'
  emit('update:modelValue', isEmpty ? '' : html)
}

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>

<style scoped>
.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
}

.editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.editor-content {
  /* 使用 props 动态设置高度 */
  height: v-bind(height) !important;
  overflow-y: hidden;
}

.editor-footer {
  padding: 4px 12px;
  border-top: 1px solid #f2f6fc;
  font-size: 12px;
  color: #909399;
  text-align: right;
  background-color: #fafafa;
}

/* 强制约束编辑器内视频的默认显示尺寸 */
:deep(.w-e-text-container [data-w-e-type="video"]) {
  width: 400px !important;
  height: auto !important;
  margin: 10px 0;
  border-radius: 4px;
  overflow: hidden;
  background-color: #000000 !important;
  border: 1px solid #333;
}

:deep(.w-e-video-container) {
  background-color: #000000 !important;
}

/* 兼容处理编辑器内的 video 标签原生样式 */
:deep(.w-e-text-container video) {
  width: 100% !important; /* 让它撑满上面设定的 400px 容器 */
  max-width: 100%;
  height: auto !important;
  display: block;
  background-color: #000000 !important;
  object-fit: contain;
}

:deep(.w-e-text-container [data-w-e-type="video"].w-e-selected) {
  box-shadow: 0 0 0 2px #183555;
}

:deep(.w-e-video-container) {
  position: relative;
  min-height: 225px; /* 16:9 比例的 400px 宽度对应高度 */
  background: #000 url('src/assets/loading.gif') no-repeat center;
}

:deep(.w-e-text-container img) {
  max-width: 100%;
}
</style>