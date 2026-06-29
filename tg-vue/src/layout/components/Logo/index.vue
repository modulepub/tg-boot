<template>
	<div v-if="appStore.sidebarOpened" class="sidebar-logo">
		<img class="logo-img" src="/logo.png" alt="logo" width="30" height="30" />
		<span class="logo-title"> {{ $t('app.logoText') }}</span>
	</div>
	<div v-else class="sidebar-logo sidebar-logo-expend">
		<span>{{ $t('app.miniLogoText') }}</span>
	</div>
</template>

<script setup lang="ts">
import { useAppStore } from '@/store/modules/app'
const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.sidebar-logo {
	width: 230px !important;
	height: var(--theme-header-height);
	line-height: var(--theme-header-height);
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: rgb(0 21 41 / 2%) 0 1px 4px;
	color: var(--theme-logo-text-color);
	font-size: 18px;

	//overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;

	.el-avatar {
		width: 30px;
		height: 30px;
		vertical-align: middle;
	}
	.logo-img {
		filter: brightness(1.85) saturate(1.2);
	}
	.logo-title {
		margin-left: 10px;
		font-weight: 600;
		font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', 'Source Han Sans SC', sans-serif;
		letter-spacing: 0.5px;
	}
}
.sidebar-logo-expend {
	width: 100% !important;
}
</style>
