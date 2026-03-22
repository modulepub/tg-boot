<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px" @keyup.enter="submitHandle()">
			<el-form-item label="文件编码" prop="fileCode">
				<el-input v-model="dataForm.fileCode" placeholder="文件编码"></el-input>
			</el-form-item>
			<el-form-item label="文件名称" prop="fileName">
				<el-input v-model="dataForm.fileName" placeholder="文件名称"></el-input>
			</el-form-item>
			<el-form-item label="文件大小（字节）" prop="fileSize">
				<el-input v-model="dataForm.fileSize" placeholder="文件大小（字节）"></el-input>
			</el-form-item>
			<el-form-item label="文件链接" prop="fileUrl">
				<el-input v-model="dataForm.fileUrl" placeholder="文件链接"></el-input>
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
	createBy: '',
	createTime: '',
	updateBy: '',
	updateTime: '',
	orgCode: '',
	deleted: '',
	seqNo: '',
	version: '',
	fileCode: '',
	fileName: '',
	fileSize: '',
	fileUrl: ''
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (id) {
		getFile(id)
	}
}

const getFile = (id: number) => {
	service.get('/mgt/file/file/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

const dataRules = ref({})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http: any
		if (dataForm.id) {
			http = service.post('/mgt/file/file/edit', dataForm)
		} else {
			http = service.post('/mgt/file/file/add', dataForm)
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
