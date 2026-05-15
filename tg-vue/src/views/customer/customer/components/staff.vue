<template>
	<tg-data-table
		:key="visible + ''"
		v-model:visible="visible"
		title="员工选择"
		url="/mgt/customer/staff/list"
		:multiple
		:columns="[
			{ field: 'userName', label: '用户名' },
			{ field: 'userRealName', label: '姓名' },
			{ field: 'userMobile', label: '手机号' },
			{ field: 'todayContactTaskNum', label: '今日联络任务数' },
			{ field: 'createTime', label: '创建时间' }
		]"
		:query-params="[
			{ field: 'userName', label: '用户名' },
			{ field: 'userMobile', label: '手机号' }
		]"
		@select="onSelect"
	></tg-data-table>
</template>

<script setup lang="ts">
defineProps({
	multiple: {
		type: Boolean,
		required: false,
		default: false
	}
})

const visible = defineModel<boolean>()

const emit = defineEmits(['select'])
const onSelect = (rows: any[]) => {
	emit('select', rows)
}
</script>
