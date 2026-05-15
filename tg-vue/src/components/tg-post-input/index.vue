<template>
	<el-input v-model="postName" :clearable :disabled readonly :placeholder @clear="model = ''">
		<template #append>
			<el-button icon="Search" @click="visible = true"></el-button>
		</template>
	</el-input>
	<tg-data-table
		v-if="!disabled && visible"
		:key="visible + ''"
		v-model:visible="visible"
		title="岗位选择"
		url="/sys/post/page"
		:multiple
		:columns="[
			{ field: 'postCode', label: '岗位编码' },
			{ field: 'postName', label: '岗位名称' }
		]"
		:query-params="[
			{ field: 'postCode', label: '岗位编码' },
			{ field: 'postName', label: '岗位名称' }
		]"
		@select="onSelect"
	></tg-data-table>
</template>

<script setup lang="ts" name="TgPostInput">
import { ref, watch } from 'vue'

const props = defineProps({
	multiple: {
		type: Boolean,
		required: false,
		default: false
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

const model = defineModel<any | any[]>()
const visible = ref(false)
const postName = ref()

watch(
	() => model.value,
	async val => {
		if (val) {
			const idList = props.multiple ? val : [val]
			if (idList.length > 0) {
				postName.value = ''
			}
		} else {
			postName.value = ''
		}
	},
	{
		immediate: true
	}
)

const emit = defineEmits(['select'])
const onSelect = (rows: any[]) => {
	if (props.multiple) {
		model.value = rows.map((item: any) => item.id)
	} else {
		model.value = rows[0].id
	}

	emit('select', rows)
}
</script>
