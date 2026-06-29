<template>
	<el-dropdown trigger="click" @command="orgChange">
		<span class="org-select">
			{{ currentOrg.orgName || '选择机构' }}
			<el-icon class="el-icon--right">
				<arrow-down />
			</el-icon>
		</span>
		<template #dropdown>
			<el-dropdown-menu>
				<el-dropdown-item v-for="org in orgList" :key="org.orgCode" :command="org" :disabled="currentOrg.orgCode === org.orgCode">
					{{ org.orgName }}
				</el-dropdown-item>
			</el-dropdown-menu>
		</template>
	</el-dropdown>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ArrowDown } from '@element-plus/icons-vue'
import service from '@/utils/request'
import { useUserStore } from '@/store/modules/user'
const userStore = useUserStore()
interface Org {
	orgCode: string
	orgName: string
	selected?: boolean
}

const orgList = ref<Org[]>([])
const currentOrg = ref<Org>({
	orgCode: '',
	orgName: ''
})

// 获取机构列表
const getOrgList = async () => {
	try {
		const res = await service.get('/cus/sysUserOrganization/listByUser')
		orgList.value = res.data || []
		// 设置当前机构：优先 selected，否则取排序后的第一个
		const selected = orgList.value.find(item => item.selected)
		currentOrg.value = selected || orgList.value[0] || { orgCode: '', orgName: '' }
	} catch (error) {
		console.error('获取机构列表失败:', error)
	}
}

// 切换机构
const orgChange = async (org: Org) => {
	if (currentOrg.value.orgCode === org.orgCode) {
		return
	}
	currentOrg.value = org
	const res = await service.post('/cus/sysUserOrganization/changeCurrentOrg', { orgCode: org.orgCode })
	userStore.setToken(res.data.accessToken)
	// 切换机构后回到首页并刷新，重新拉取菜单与权限
	window.location.href = '/home/index'
}

onMounted(() => {
	getOrgList()
})
</script>

<style lang="scss" scoped>
.org-select {
	display: flex;
	align-items: center;
	padding: 0 8px;
	cursor: pointer;
	user-select: none;

	.el-icon--right {
		margin-left: 4px;
		font-size: 12px;
	}
}
</style>
