import {createApp} from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue' // 导入所有图标
import SvgIcon from '@/components/SvgIcon.vue'
import router from '@/router'
import {createPinia} from 'pinia'
import './style.css' // <--- 全局样式
import 'vant/lib/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 1. 创建应用和 Pinia 实例
const app = createApp(App)
const pinia = createPinia()

// 2. 注册插件（注意：Pinia 必须在路由和权限逻辑之前注册）
app.use(pinia) // 1. 先注册 Pinia
app.use(router) // 2. 再注册路由

// 3. 引入权限控制（确保在 pinia 和 router 注册之后）
import '@/utils/permission'


// 4. 注册 Element Plus
app.use(ElementPlus, {
    locale: zhCn, // 全局中文语言
    size: 'default' // 全局组件尺寸（可选：large / default / small）
})

app.component('SvgIcon', SvgIcon)

// 5. 全局注册图标
try {
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
        app.component(key, component)
    }
    console.log('✅ Element Plus 图标注册成功')
} catch (error) {
    console.error('❌ Element Plus 图标注册失败：', error)
}

// 6. 全局配置（可选：添加全局属性/方法，方便业务使用）
app.config.globalProperties.$ELEMENT = {
    size: 'default',
    zIndex: 3000 // 全局z-index配置，避免弹窗层级问题
}

// 7. 挂载应用到DOM
app.mount('#app')

// 8.开发环境提示
if (import.meta.env.DEV) {
    console.log('🚀 系统启动成功')
}