import { defineStore } from 'pinia'
import cache from '@/utils/cache'
import service from '@/utils/request'

export const useUserStore = defineStore('userStore', {
	state: () => ({
		// 用户信息
		user: {
			id: '',
			userName: '',
			userAvatar: '',
			userCode: '',
			userOrgCode: '',
			userRealName: '',
			userSexCode: '',
			userPhone: ''
		},
		// 权限列表
		authorityList: [],
		// 访问token
		token: cache.getToken(),
		// 刷新token
		refreshToken: cache.getRefreshToken()
	}),
	actions: {
		setUser(val: any) {
			this.user = val
		},
		setToken(val: any) {
			this.token = val
			cache.setToken(val)
		},
		setRefreshToken(val: any) {
			this.refreshToken = val
			cache.setRefreshToken(val)
		},
		// 账号登录
		async accountLoginAction(loginForm: any) {
			const { data } = await service.post('/pub/auth/login', loginForm)
			this.setToken(data.accessToken)
			this.setRefreshToken(data.refreshToken)
		},
		// 手机号登录
		async mobileLoginAction(loginForm: any) {
			const { data } = await service.post('/pub/auth/phoneLogin', loginForm)
			this.setToken(data.accessToken)
			this.setRefreshToken(data.refreshToken)
		},
		// 第三方登录
		async thirdLoginAction(loginForm: any) {
			const { data } = await service.post('/pub/auth/third', loginForm)
			this.setToken(data.accessToken)
			this.setRefreshToken(data.refreshToken)
		},
		// 获取用户信息
		async getUserInfoAction() {
			const { data } = await service.get('/cus/sysUser/getInfo')
			this.setUser(data)
		},
		// 获取权限
		async getAuthorityListAction() {
			const { data } = await service.get('/cus/sysPermission/authority')
			this.authorityList = data || []
		},
		// 用户退出
		async logoutAction() {
			await service.post('/cus/sysUser/logout')

			// 移除 token
			this.setToken(null)
			this.setRefreshToken(null)
		}
	}
})
