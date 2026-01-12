import axios, { AxiosResponse } from 'axios'
import qs from 'qs'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import cache from '@/utils/cache'
import { ElMessageBox } from 'element-plus/es'

// axios实例
const service = axios.create({
	baseURL: import.meta.env.VITE_API_URL as any,
	timeout: 60000,
	headers: { 'Content-Type': 'application/json;charset=UTF-8' }
})

// 请求拦截器
service.interceptors.request.use(
	(config: any) => {
		const userStore = useUserStore()

		if (userStore?.token) {
			config.headers.Authorization = `Bearer ${userStore.token}`
			console.log(config.headers.Authorization)
		}

		config.headers['Accept-Language'] = cache.getLanguage()

		// 追加时间戳，防止GET请求缓存
		if (config.method?.toUpperCase() === 'GET') {
			config.params = { ...config.params, t: new Date().getTime() }
		}

		if (Object.values(config.headers).includes('application/x-www-form-urlencoded')) {
			config.data = qs.stringify(config.data)
		}

		// 打印完整请求信息，用于调试参数传递
		console.log('请求信息>>', {
			url: config.url,
			method: config.method,
			params: config.params,
			data: config.data
		})

		return config
	},
	error => {
		return Promise.reject(error)
	}
)

// 是否刷新
let isRefreshToken = false
// 重试请求
let requests: any[] = []

// 刷新token
const getRefreshToken = (refreshToken: string) => {
	return service.post('/sys/auth/token?refreshToken=' + refreshToken)
}

// 响应拦截器
service.interceptors.response.use(
	async (response: AxiosResponse<any>) => {
		console.log('返回>>', response)
		// 没有权限，如：未登录、token过期
		if (response.status === 401) {
			return Promise.reject(new Error('登录超时，请重新登录'))
		}
		if (response.status !== 200) {
			return Promise.reject(new Error(response.statusText || 'Error'))
		}

		const res = response.data
		if (Object.prototype.toString.call(res) === '[object Blob]') {
			return response
		}

		// 响应成功
		if (res.code === 0) {
			return res
		} else {
			ElMessage.error(res.message)
			return Promise.reject(new Error(response.statusText || 'Error'))
		}
	},
	error => {
		// 处理错误信息，确保显示正确
		if (error.response) {
			// 服务器返回了错误响应
			if (error.response.status === 401) {
				ElMessage.error('认证失败，请重新登录')
				window.location.reload()
			}
			if (error.response.status === 400) {
				ElMessage.error(error.response.data)
			}
			if (error.response.status === 500) {
				ElMessage.error('服务器内部错误')
			}
		} else if (error.request) {
			// 请求已发送但没有收到响应
			ElMessage.error('网络错误，未收到响应')
		}

		return Promise.reject(error)
	}
)

const handleAuthorized = () => {
	ElMessageBox.confirm('登录超时，请重新登录', '提示', {
		showCancelButton: false,
		closeOnClickModal: false,
		showClose: false,
		confirmButtonText: '重新登录',
		type: 'warning'
	}).then(() => {
		const userStore = useUserStore()

		userStore?.setToken('')
		userStore?.setRefreshToken('')
		location.reload()

		return Promise.reject('登录超时，请重新登录')
	})
}

// 导出 axios 实例
export default service
