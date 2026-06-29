<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="分类编码" prop="tdGdCgyCode">
				<el-input v-model="dataForm.tdGdCgyCode" placeholder="分类编码"></el-input>
			</el-form-item>
			<el-form-item label="分类名称" prop="tdGdCgyName">
				<el-input v-model="dataForm.tdGdCgyName" placeholder="分类名称"></el-input>
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
	tdGdCgyCode: '',
	tdGdCgyName: ''
})

const dataRules = ref({
	tdGdCgyCode: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
	tdGdCgyName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		service.get('/mgt/trade/tdGoodsCategory/queryById?id=' + id).then(res => {
			Object.assign(dataForm, res.data)
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const http = dataForm.id ? service.post('/mgt/trade/tdGoodsCategory/edit', dataForm) : service.post('/mgt/trade/tdGoodsCategory/add', dataForm)
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
