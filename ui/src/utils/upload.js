import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 文件上传工具函数
 * @param {File} file 文件对象
 * @returns {Promise<string>} 返回上传后的URL
 */
export const uploadFile = async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    const token = localStorage.getItem('token') // 获取 token
    try {
        // 根据你的项目环境，这里可能需要根据实际情况修改 baseURL 或从 process.env 获取
        const res = await axios.post('/api/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': `Bearer ${token}`
            }
        })

        // 适配你的 Result<String> 返回体
        if (res.data.code === 200) {
            return res.data.data // 返回 URL 字符串
        } else {
            ElMessage.error(res.data.msg || '上传失败')
            throw new Error(res.data.msg)
        }
    } catch (error) {
        ElMessage.error('网络错误，文件上传失败')
        throw error
    }
}