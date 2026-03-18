
/**
 * 路径解析工具函数
 */
export const resolvePath = (parentPath = '', childPath = '') => {
    // 1. 如果子路径是外部链接，直接返回子路径
    if (/^(https?:|mailto:|tel:)/.test(childPath)) {
        return childPath
    }

    // 2. 确保父路径以 / 开头
    let parent = parentPath.startsWith('/') ? parentPath : '/' + parentPath

    // 3. 确保子路径不以 / 开头（避免拼接出 //）
    let child = childPath.startsWith('/') ? childPath.substring(1) : childPath

    // 4. 合并并利用正则把多个连续斜杠替换为单个 /
    return `${parent}/${child}`.replace(/\/+/g, '/')
}