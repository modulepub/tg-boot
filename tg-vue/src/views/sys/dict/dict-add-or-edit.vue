<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="80px">
			<el-form-item prop="dictName" label="字典名称">
				<el-input v-model="dataForm.dictName" placeholder="字典名称"></el-input>
			</el-form-item>
			<el-form-item prop="dictCode" label="字典编码">
				<el-input v-model="dataForm.dictCode" placeholder="字典编码"></el-input>
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
	dictName: ''
})

const dataRules = ref({
	dictCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	dictName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
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
		getDictType(id)
	}
}

const getDictType = (id: number) => {
	service.get('/mgt/sysDict/queryById?id=' + id).then(res => {
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
			http = service.post('/mgt/sysDict/edit', dataForm)
		} else {
			http = service.post('/mgt/sysDict/add', dataForm)
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
	init
})
</script>
