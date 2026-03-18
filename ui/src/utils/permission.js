import { useUserStore } from '@/store/user'

/**
 * 权限校验逻辑
 */
export function checkPermi(value) {
    if (value && value instanceof Array && value.length > 0) {
        const userStore = useUserStore()
        const permissions = userStore.permissions
        const permissionRoles = value

        // 只要 permissions 数组里包含传入的任意一个权限标识，就返回 true
        const hasPermission = permissions.some(permission => {
            return permission === '*:*:*' || permissionRoles.includes(permission)
        })

        return hasPermission
    } else {
        console.error(`need roles! Like v-hasPermi="['sys:user:add']"`)
        return false
    }
}

/**
 * 定义 Vue 指令
 */
export default {
    mounted(el, binding) {
        const { value } = binding
        if (!checkPermi(value)) {
            // 如果没有权限，直接从 DOM 中移除该元素
            el.parentNode && el.parentNode.removeChild(el)
        }
    }
}