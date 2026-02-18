<template>
	<el-select v-model="modelAsString" :placeholder="placeholder" :clearable="clearable" :multiple="multiple">
		<el-option v-for="data in dataList" :key="data.dictItemValue" :label="data.dictItemText" :value="data.dictItemValue">{{
			data.dictItemText
		}}</el-option>
	</el-select>
</template>

<script setup lang="ts" name="TgDictSelect">
import { getDictDataList } from '@/utils/tool'
import { useAppStore } from '@/store/modules/app'
import { computed } from 'vue'

const appStore = useAppStore()
const props = defineProps({
	dictCode: {
		type: String,
		required: true
	},
	clearable: {
		type: Boolean,
		required: false,
		default: () => false
	},
	placeholder: {
		type: String,
		required: false,
		default: () => ''
	},
	multiple: {
		type: Boolean,
		required: false,
		default: () => false
	}
})

const model = defineModel<string>()
const modelAsString = computed({
	get() {
		if (props.multiple) {
			if (typeof model.value === 'string' && model.value) {
				return model.value.split(',').filter(item => item !== '')
			}
			return []
		}
		return model.value != undefined ? model.value.toString() : ''
	},
	set(value) {
		if (props.multiple && Array.isArray(value)) {
			model.value = value.join(',')
		} else {
			model.value = typeof value === 'string' ? value : ''
		}
	}
})

const dataList = getDictDataList(appStore.dictList, props.dictCode)
</script>
