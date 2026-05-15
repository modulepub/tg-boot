<template>
	<el-tree-select
		v-model="model"
		:data="dataList"
		value-key="id"
		check-strictly
		:render-after-expand="false"
		:props="props"
		style="width: 100%"
		:clearable="clearable"
		:placeholder="placeholder"
		:disabled="disabled"
	/>
</template>

<script setup lang="ts" name="TgDataTreeSelect">
import { PropType, ref, watch } from 'vue'
import { treeDataTranslate } from '@/utils/tool'
import service from '@/utils/request'
interface Prop {
	value: string
	pid: string
	label: string
	children: string
}
const prop = defineProps({
	url: {
		type: String,
		default: ''
	},
	data: {
		type: Array,
		default: () => []
	},
	props: {
		type: Object as PropType<Prop>,
		required: false,
		default: () => ({
			value: 'id',
			pid: 'pid',
			label: 'name',
			children: 'children'
		})
	},
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

watch(
	() => prop.data,
	val => {
		dataList.value = val
	},
	{ deep: true }
)

const model = defineModel<number | string>()

// 获取列表
const dataList = ref<any[]>()
const getDataList = async () => {
	if (prop.url.length === 0) {
		return
	}
	const data = await service.get(prop.url)
	dataList.value = treeDataTranslate(data as any, prop.props.value, prop.props.pid)
}

getDataList()
</script>
