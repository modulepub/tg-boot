import { defineStore } from 'pinia'
import { generateRoutes, dashboardMenu } from '@/router'
import { RouteRecordRaw } from 'vue-router'
import service from '@/utils/request'

export const useRouterStore = defineStore('routerStore', {
	state: () => ({
		menuRoutes: [] as RouteRecordRaw[],
		searchMenu: [] as RouteRecordRaw[],
		routes: [] as RouteRecordRaw[]
	}),
	actions: {
		async getMenuRoutes() {
			this.menuRoutes = []
			const { data } = await service.get('/cus/sysPermission/getByCode?code=manage')
			const routes = generateRoutes(data?.children)

			// 控制台菜单
			const dashboardRoutes = generateRoutes(dashboardMenu)
			this.menuRoutes.push(...dashboardRoutes)

			// 后端菜单
			this.menuRoutes.push(...routes)

			return this.menuRoutes
		},
		setSearchMenu(routers: RouteRecordRaw[]) {
			this.searchMenu = routers
		},
		setRoutes(routers: RouteRecordRaw[]) {
			this.routes = routers
		}
	}
})
