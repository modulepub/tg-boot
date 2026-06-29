<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :width="600" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="80px">
			<el-form-item prop="dictItemText" label="字典标签">
				<el-input v-model="dataForm.dictItemText" placeholder="字典标签"></el-input>
			</el-form-item>
			<el-form-item prop="dictItemValue" label="字典值">
				<el-input v-model="dataForm.dictItemValue" placeholder="字典值"></el-input>
			</el-form-item>
			<el-form-item prop="dictItemColor" label="标签颜色">
				<el-color-picker v-model="dataForm.dictItemColor" placeholder="选择颜色" show-alpha></el-color-picker>
			</el-form-item>
			<el-form-item prop="seqNo" label="排序">
				<el-input-number v-model="dataForm.seqNo" controls-position="right" :min="0" aria-label="排序"></el-input-number>
			</el-form-item>
			<el-form-item prop="dictItemDescription" label="备注">
				<el-input v-model="dataForm.dictItemDescription" placeholder="备注"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()
const dataForm = reactive({
	id: '',
	dictCode: '',
	dictItemText: '',
	dictItemValue: '',
	dictItemColor: '',
	seqNo: 0,
	dictItemDescription: ''
})

const dataRules = ref({
	dictItemText: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	dictItemValue: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	// id 存在则为修改
	if (id) {
		getDictData(id)
	}
}

const getDictData = (id: number) => {
	service.get('/mgt/sysDictItem/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http
		if (dataForm.id) {
			http = service.post('/mgt/sysDictItem/edit', dataForm)
		} else {
			http = service.post('/mgt/sysDictItem/add', dataForm)
		}
		http.then(() => {
			ElMessage.success({
				message: '操作成功',
				duration: 500,
				onClose: () => {
					visible.value = false
					emit('refreshDataList')
				}
			})
		})
	})
}

defineExpose({
	init,
	dataForm
})
</script>
