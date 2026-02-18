<template>
	<el-table-column
		:prop="prop"
		:label="label"
		:header-align="headerAlign"
		:align="align"
		:width="width"
		:min-width="minWidth"
		:class-name="className"
	>
		<template #default="scope">
			<span
				v-for="(item, index) in getDictItem(dictCode, scope.row[prop])"
				:key="index"
				:style="`background-color:${item.dictItemColor};color:white;border:none;`"
				class="el-tag"
				>{{ item.dictItemText }}</span
			>
		</template>
	</el-table-column>
</template>

<script setup lang="ts" name="TgDictColumn">
import getDictItem from '@/utils/tool'

const props = defineProps({
	prop: {
		type: String,
		required: true
	},
	label: {
		type: String,
		required: true
	},
	dictCode: {
		type: String,
		required: true
	},
	headerAlign: {
		type: String,
		required: false,
		default: () => 'center'
	},
	align: {
		type: String,
		required: false,
		default: () => 'center'
	},
	width: {
		type: String,
		required: false,
		default: () => ''
	},
	minWidth: {
		type: String,
		required: false,
		default: () => ''
	},
	className: {
		type: String,
		required: false,
		default: () => ''
	}
})
</script>
