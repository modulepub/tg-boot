<template>
	<el-form label-width="100px" class="menu-button-fields">
		<el-form-item label="按钮类型">
			<el-select v-model="button.type" placeholder="请选择" style="width: 100%">
				<el-option label="点击（click）" value="click" />
				<el-option label="跳转网页（view）" value="view" />
				<el-option label="小程序（miniprogram）" value="miniprogram" />
			</el-select>
		</el-form-item>
		<el-alert
			v-if="button.type === 'miniprogram'"
			type="warning"
			:closable="false"
			show-icon
			title="小程序菜单须先在微信公众平台「广告与服务 → 小程序管理」中关联该小程序，否则发布报错 45064。"
			style="margin-bottom: 12px"
		/>
		<el-form-item v-if="button.type === 'click'" label="菜单 Key">
			<el-input v-model="button.key" placeholder="点击事件 Key，如 MENU_ABOUT"></el-input>
		</el-form-item>
		<el-form-item v-if="button.type === 'view' || button.type === 'miniprogram'" label="跳转 URL">
			<el-input v-model="button.url" placeholder="网页链接或备用网页（miniprogram 可填 http://mp.weixin.qq.com）"></el-input>
		</el-form-item>
		<template v-if="button.type === 'miniprogram'">
			<el-form-item label="小程序 AppId" required>
				<el-select v-model="button.appid" filterable allow-create default-first-option placeholder="选择或输入 AppId" style="width: 100%">
					<el-option
						v-for="item in miniAppList"
						:key="item.wxMiniConfigAppId"
						:label="formatMiniAppLabel(item)"
						:value="item.wxMiniConfigAppId"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="页面路径" required>
				<el-input v-model="button.pagepath" placeholder="pages/index/index（勿带前导 /）"></el-input>
			</el-form-item>
		</template>
	</el-form>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import service from '@/utils/request'

interface MiniAppOption {
	wxMiniConfigAppId: string
	wxMiniConfigName?: string
	wxMiniConfigCode?: string
}

defineProps<{
	button: {
		type?: string
		key?: string
		url?: string
		appid?: string
		pagepath?: string
	}
}>()

const miniAppList = ref<MiniAppOption[]>([])

const formatMiniAppLabel = (item: MiniAppOption) => {
	const name = item.wxMiniConfigName || item.wxMiniConfigCode || item.wxMiniConfigAppId
	return `${name} (${item.wxMiniConfigAppId})`
}

const loadMiniApps = () => {
	service.get('/mgt/wx/wxMiniConfig/list', { params: { pageNo: 1, pageSize: 200 } }).then((res: any) => {
		miniAppList.value = (res.data?.records || []).filter((r: MiniAppOption & { wxMiniConfigEnabledCode?: string }) => r.wxMiniConfigEnabledCode !== '0')
	})
}

onMounted(() => {
	loadMiniApps()
})
</script>
