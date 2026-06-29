<template>
	<el-tree-select
		v-model="model"
		:data="orgList"
		node-key="orgCode"
		value-key="orgCode"
		check-strictly
		show-checkbox
		multiple
		:render-after-expand="false"
		:props="{ label: 'orgName', children: 'children' }"
		style="width: 100%"
		:clearable="clearable"
		:placeholder="placeholder"
		:disabled="disabled"
	/>
</template>

<script setup lang="ts" name="TgOrgSelect">
import { ref } from 'vue'
import service from '@/utils/request'

defineProps({
	clearable: {
		type: Boolean,
		required: false,
		default: () => true
	},
	disabled: {
		type: Boolean,
		required: false,
		default: () => false
	},
	placeholder: {
		type: String,
		required: false,
		default: () => ''
	}
})

const model = defineModel<string[]>({ default: () => [] })
const orgList = ref([])

// 获取机构列表
const getOrgList = async () => {
	const res = await service.get('/mgt/sysOrganization/listCompany')
	orgList.value = res.data
}

getOrgList()
</script>
