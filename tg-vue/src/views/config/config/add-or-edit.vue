<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="配置编码" prop="configCode">
				<el-input v-model="dataForm.configCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item label="配置名称" prop="configName">
				<el-input v-model="dataForm.configName" placeholder="配置名称"></el-input>
			</el-form-item>
			<el-form-item label="配置类型" prop="configTypeCode">
				<tg-dict-select v-model="dataForm.configTypeCode" dict-code="configTypeCode" clearable placeholder="配置类型"></tg-dict-select>
			</el-form-item>
			<el-form-item label="是否启用" prop="configEnableStatusCode">
				<tg-dict-select
					v-model="dataForm.configEnableStatusCode"
					dict-code="configEnableStatusCode"
					clearable
					placeholder="是否启用"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="配置内容" prop="configContent">
				<el-input type="textarea" rows="10" v-model="dataForm.configContent" placeholder="配置内容"></el-input>
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
	configCode: '',
	configName: '',
	configTypeCode: '',
	configEnableStatusCode: '',
	configContent: ''
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (id) {
		getConfig(id)
	}
}

const getConfig = (id: number) => {
	service.get('/mgt/config/config/queryById?id=' + id).then(res => {
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
			http = service.post('/mgt/config/config/edit', dataForm)
		} else {
			http = service.post('/mgt/config/config/add', dataForm)
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