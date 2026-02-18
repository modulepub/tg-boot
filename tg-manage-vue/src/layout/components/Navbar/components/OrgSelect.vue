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
		// 设置默认机构
		for (let item of res.data) {
			if (item.selected) {
				currentOrg.value = item
			}
		}
	} catch (error) {
		console.error('获取机构列表失败:', error)
	}
}

// 切换机构
const orgChange = async (org: Org) => {
	currentOrg.value = org
	// 这里可以添加切换机构的逻辑，比如保存到store或localStorage
	const res = await service.post('/mgt/sysUserOrganization/changeCurrentOrg', org)
	console.log('切换机构:', org, res)
	userStore.setToken(res.data.accessToken)
	userStore.setRefreshToken(res.data.refreshToken)
	window.location.reload()
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
