<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" width="760px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="110px">
			<el-form-item label="配置 key" prop="appConfigKey">
				<el-input v-model="dataForm.appConfigKey" placeholder="如 matchmaker" :disabled="!!dataForm.id"></el-input>
			</el-form-item>
			<el-form-item label="配置值 JSON" prop="appConfigValue">
				<el-input
					v-model="dataForm.appConfigValue"
					type="textarea"
					:rows="14"
					placeholder='{"h5UrlPrefix":"https://h5.example.com","h5UrlUseHash":false}'
					class="mono-textarea"
				/>
				<div class="json-toolbar">
					<el-button link type="primary" @click="formatJsonHandle">格式化 JSON</el-button>
				</div>
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
	appConfigKey: '',
	appConfigValue: ''
})

const dataRules = ref({
	appConfigKey: [{ required: true, message: '请输入配置 key', trigger: 'blur' }],
	appConfigValue: [{ required: true, message: '请输入配置值 JSON', trigger: 'blur' }]
})

const resetForm = () => {
	dataForm.id = ''
	dataForm.appConfigKey = ''
	dataForm.appConfigValue = ''
}

const init = (id?: string) => {
	visible.value = true
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		service.get('/mgt/system/sysAppConfig/queryById?id=' + encodeURIComponent(id)).then(res => {
			Object.assign(dataForm, res.data)
			if (dataForm.appConfigValue) {
				try {
					dataForm.appConfigValue = JSON.stringify(JSON.parse(dataForm.appConfigValue), null, 2)
				} catch {
					/* 保持原样 */
				}
			}
		})
	}
}

const formatJsonHandle = () => {
	try {
		const parsed = JSON.parse(dataForm.appConfigValue || '{}')
		dataForm.appConfigValue = JSON.stringify(parsed, null, 2)
	} catch {
		ElMessage.error('JSON 格式不正确')
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		try {
			JSON.parse(dataForm.appConfigValue)
		} catch {
			ElMessage.error('配置值必须是合法 JSON')
			return false
		}
		const payload = { ...dataForm }
		const http = dataForm.id
			? service.post('/mgt/system/sysAppConfig/edit', payload)
			: service.post('/mgt/system/sysAppConfig/add', payload)
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

<style scoped>
.mono-textarea :deep(textarea) {
	font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
	font-size: 12px;
	line-height: 1.45;
}

.json-toolbar {
	margin-top: 6px;
}
</style>
