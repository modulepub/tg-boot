<template>
	<tg-data-table
		:key="visible + ''"
		v-model:visible="visible"
		title="用户选择"
		url="/mgt/sysUser/list"
		:multiple
		:columns="[
			{ field: 'userName', label: '用户名' },
			{ field: 'userRealName', label: '姓名' },
			{ field: 'userMobile', label: '手机号' },
			{ field: 'createTime', label: '创建时间' }
		]"
		:query-params="[
			{ field: 'userName', label: '用户名' },
			{ field: 'userMobile', label: '手机号' }
		]"
		@select="onSelect"
	></tg-data-table>
</template>

<script setup lang="ts" name="TgUserDialog">
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
