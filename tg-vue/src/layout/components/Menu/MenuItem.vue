<template>
	<el-sub-menu v-if="menu.children.length > 0" :key="menu.path" :index="menu.path">
		<template #title>
			<span class="submenu-title" @click="handleSubMenuTitleClick(menu, $event)">
				<tg-icon v-if="showIcon" :icon="menu.meta.icon"></tg-icon>
				<span>{{ menu.meta.title }}</span>
			</span>
		</template>
		<menu-item v-for="sub in menu.children" :key="sub.path" :menu="sub"></menu-item>
	</el-sub-menu>
	<el-menu-item v-else :key="menu.path" :index="menuIndex(menu)" @click="handleClickMenu(menu)">
		<tg-icon v-if="showIcon" :icon="menu.meta.icon"></tg-icon>
		<template #title>
			{{ menu.meta.title }}
		</template>
	</el-menu-item>
</template>

<script setup lang="ts">
import { computed, PropType } from 'vue'
import { useRouter } from 'vue-router'
import { isExternalLink, replaceLinkParam } from '@/utils/tool'
import { useAppStore } from '@/store/modules/app'

const appStore = useAppStore()

// 显示icon图标
const showIcon = computed(() => {
	return appStore.theme.layout !== 'columns'
})

defineProps({
	menu: {
		type: Object as PropType<any>,
		required: true
	}
})

const router = useRouter()

const menuIndex = (menu: any) => {
	if (menu.meta && menu.meta.url && menu.meta.url.indexOf('?') > -1 && !isExternalLink(menu.meta.url)) {
		return '/' + menu.meta.url
	}
	return menu.path
}

// 目录菜单标题点击：仅当存在真实落地页时跳转，否则交给 el-sub-menu 展开子菜单
const handleSubMenuTitleClick = (menu: any, event: MouseEvent) => {
	if (!menu.meta?.landingPage || !menu.meta?.url || isExternalLink(menu.meta.url)) {
		return
	}
	event.stopPropagation()
	const url = menu.meta.url.split('?')[0]
	if (!menu.meta.newOpen) {
		router.push('/' + url)
		return
	}
	window.open('#' + url, '_blank')
}

// 菜单点击事件
const handleClickMenu = (menu: any) => {
	// 不是新开页面，则直接切换路由
	if (!menu.meta.newOpen) {
		router.push(menuIndex(menu))
		return
	}

	// 新开页面逻辑
	if (isExternalLink(menu.meta.url)) {
		// 外链
		window.open(replaceLinkParam(menu.meta.url), '_blank')
	} else {
		// 内部组件
		window.open('#' + menu.meta.url, '_blank')
	}
}
</script>

<style scoped lang="scss">
.submenu-title {
	display: inline-flex;
	align-items: center;
	width: 100%;
}
</style>
