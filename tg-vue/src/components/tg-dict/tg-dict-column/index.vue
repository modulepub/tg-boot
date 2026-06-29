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
			<el-tag
				v-for="(item, index) in getDictItem(dictCode, scope.row[prop])"
				:key="index"
				:color="resolveDictTagColor(dictCode, item.dictItemValue, item.dictItemColor)"
				effect="dark"
				disable-transitions
				style="border: none; margin-right: 4px"
			>
				{{ item.dictItemText }}
			</el-tag>
		</template>
	</el-table-column>
</template>

<script setup lang="ts" name="TgDictColumn">
import getDictItem from '@/utils/tool'
import { resolveDictTagColor } from '@/utils/dict-tag-style'

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
