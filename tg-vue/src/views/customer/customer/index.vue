<template>
	<el-card class="layout-query">
		<el-tabs v-model="activeTab" @tab-change="handleTabChange">
			<el-tab-pane label="已实名客户" name="verified">
				<customer-list-tab ref="verifiedRef" tab-key="verified"></customer-list-tab>
			</el-tab-pane>
			<el-tab-pane label="未实名客户" name="unverified">
				<customer-list-tab ref="unverifiedRef" tab-key="unverified"></customer-list-tab>
			</el-tab-pane>
			<el-tab-pane label="点亮爱与诚 用户" name="lsLit">
				<customer-list-tab ref="lsLitRef" tab-key="lsLit"></customer-list-tab>
			</el-tab-pane>
		</el-tabs>
	</el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import CustomerListTab from '@/views/customer/customer/customerListTab.vue'

const activeTab = ref('verified')
const verifiedRef = ref()
const unverifiedRef = ref()
const lsLitRef = ref()

const tabRefMap: Record<string, typeof verifiedRef> = {
	verified: verifiedRef,
	unverified: unverifiedRef,
	lsLit: lsLitRef
}

const handleTabChange = (name: string) => {
	const tabRef = tabRefMap[name]
	if (tabRef?.value?.reset) {
		tabRef.value.reset()
	}
}

onMounted(() => {
	activeTab.value = 'verified'
})
</script>
