<template>
	<el-card class="layout-query">
		<el-tabs v-model="activeTab" @tab-change="handleTabChange">
			<el-tab-pane :label="'未入库'">
				<not-in-store ref="notInStoreRef"></not-in-store>
			</el-tab-pane>
			<el-tab-pane :label="'待分配销售'">
				<in-store ref="inStoreRef"></in-store>
			</el-tab-pane>
			<el-tab-pane :label="'已分配销售'">
				<assigned ref="assignedRef"></assigned>
			</el-tab-pane>
			<el-tab-pane :label="'3-6个月跟进'">
				<followUp ref="followUpRef"></followUp>
			</el-tab-pane>
		</el-tabs>
	</el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import notInStore from '@/views/customer/customer/notInStore.vue'
import InStore from '@/views/customer/customer/InStore.vue'
import Assigned from '@/views/customer/customer/assigned.vue'
import FollowUp from '@/views/customer/customer/followUp.vue'
const activeTab = ref('')
const notInStoreRef = ref()
const inStoreRef = ref()
const assignedRef = ref()
const followUpRef = ref()

const handleTabChange = (name: string) => {
	switch (name) {
		case '0':
			if (notInStoreRef.value && notInStoreRef.value.reset) {
				notInStoreRef.value.reset()
			}
			break
		case '1':
			if (inStoreRef.value && inStoreRef.value.reset) {
				inStoreRef.value.reset()
			}
			break
		case '2':
			if (assignedRef.value && assignedRef.value.reset) {
				assignedRef.value.reset()
			}
			break
		case '3':
			if (followUpRef.value && followUpRef.value.reset) {
				followUpRef.value.reset()
			}
			break
	}
}

onMounted(() => {
	activeTab.value = '0'
})
</script>
