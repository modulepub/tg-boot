import { defineStore } from 'pinia'
import { ITheme } from '@/store/theme/interface'
import cache from '@/utils/cache'
import service from '@/utils/request'

// 字典项类型
export interface DictItem {
	dictItemValue: string
	dictItemText: string
	dictItemColor?: string
}

// 字典类型
export interface Dict {
	dictCode: string
	dictItemList: DictItem[]
}

export const useAppStore = defineStore('appStore', {
	state: () => ({
		// sidebar 是否展开
		sidebarOpened: cache.getSidebarOpened(),
		// 国际化
		language: cache.getLanguage(),
		// 组件大小
		componentSize: cache.getComponentSize(),
		// 字典列表
		dictList: [] as Dict[],
		// 主题
		theme: cache.getTheme()
	}),
	actions: {
		setSidebarOpened() {
			this.sidebarOpened = !this.sidebarOpened
			cache.setSidebarOpened(this.sidebarOpened)
		},
		setSidebarStatus(status: boolean) {
			this.sidebarOpened = status
			cache.setSidebarOpened(this.sidebarOpened)
		},
		setLanguage(locale: string) {
			this.language = locale
			cache.setLanguage(locale)
		},
		setComponentSize(size: string) {
			this.componentSize = size
			cache.setComponentSize(size)
		},
		async getDictListAction() {
			const { data } = await service.get('/pub/dict/listAll')
			this.dictList = data || []
		},
		setTheme(theme: ITheme) {
			this.theme = theme
			cache.setTheme(theme)
		}
	}
})
