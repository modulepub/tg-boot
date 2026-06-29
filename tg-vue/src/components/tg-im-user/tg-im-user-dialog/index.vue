<template>
	<tg-data-table
		:key="visible + ''"
		v-model:visible="visible"
		title="IM用户选择"
		url="/mgt/im/imUser/list"
		:multiple
		:columns="[
			{ field: 'imUserUserCode', label: '用户编码' },
			{ field: 'imUserNickName', label: '昵称' },
			{ field: 'imUserRealName', label: '姓名' }
		]"
		:query-params="[
			{ field: 'keyword', label: '昵称/姓名/编码' },
			{ field: 'imUserNickName', label: '昵称' },
			{ field: 'imUserRealName', label: '姓名' }
		]"
		@select="onSelect"
	></tg-data-table>
</template>

<script setup lang="ts" name="TgImUserDialog">
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
