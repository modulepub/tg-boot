<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增 AI 接口配置' : '修改 AI 接口配置'" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="配置编码" prop="aiApiConfigCode">
				<el-input v-model="dataForm.aiApiConfigCode" placeholder="留空自动生成" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="配置名称" prop="aiApiConfigName">
				<el-input v-model="dataForm.aiApiConfigName" placeholder="如 OpenAI 生产环境"></el-input>
			</el-form-item>
			<el-form-item label="提供商" prop="aiProviderCode">
				<el-select v-model="dataForm.aiProviderCode" placeholder="请选择" style="width: 100%">
					<el-option label="OpenAI" value="openai" />
					<el-option label="Azure OpenAI" value="azureOpenai" />
					<el-option label="DeepSeek" value="deepseek" />
					<el-option label="Moonshot" value="moonshot" />
					<el-option label="智谱 AI" value="zhipu" />
					<el-option label="自定义 OpenAI 兼容" value="custom" />
				</el-select>
			</el-form-item>
			<el-form-item label="Base URL" prop="aiApiConfigBaseUrl">
				<el-input v-model="dataForm.aiApiConfigBaseUrl" placeholder="https://api.openai.com/v1"></el-input>
			</el-form-item>
			<el-form-item label="API Key" prop="aiApiConfigApiKey">
				<el-input v-model="dataForm.aiApiConfigApiKey" type="password" show-password placeholder="sk-..."></el-input>
			</el-form-item>
			<el-form-item label="默认模型" prop="aiApiConfigDefaultModel">
				<el-input v-model="dataForm.aiApiConfigDefaultModel" placeholder="gpt-4o-mini"></el-input>
			</el-form-item>
			<el-form-item label="输入单价/1K tokens" prop="aiApiConfigInputPricePer1k">
				<el-input-number v-model="dataForm.aiApiConfigInputPricePer1k" :min="0" :precision="8" :step="0.001" style="width: 100%"></el-input-number>
			</el-form-item>
			<el-form-item label="输出单价/1K tokens" prop="aiApiConfigOutputPricePer1k">
				<el-input-number v-model="dataForm.aiApiConfigOutputPricePer1k" :min="0" :precision="8" :step="0.001" style="width: 100%"></el-input-number>
			</el-form-item>
			<el-form-item label="启用状态" prop="aiApiConfigEnabledCode">
				<el-select v-model="dataForm.aiApiConfigEnabledCode" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="aiApiConfigRemark">
				<el-input v-model="dataForm.aiApiConfigRemark" type="textarea"></el-input>
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
const isEdit = ref(false)

const dataForm = reactive({
	aiApiConfigCode: '',
	aiApiConfigName: '',
	aiProviderCode: 'openai',
	aiApiConfigBaseUrl: 'https://api.openai.com/v1',
	aiApiConfigApiKey: '',
	aiApiConfigDefaultModel: 'gpt-4o-mini',
	aiApiConfigInputPricePer1k: 0,
	aiApiConfigOutputPricePer1k: 0,
	aiApiConfigEnabledCode: '1',
	aiApiConfigRemark: ''
})

const dataRules = ref({
	aiApiConfigName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
	aiProviderCode: [{ required: true, message: '请选择提供商', trigger: 'change' }],
	aiApiConfigBaseUrl: [{ required: true, message: '请输入 Base URL', trigger: 'blur' }],
	aiApiConfigApiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }],
	aiApiConfigDefaultModel: [{ required: true, message: '请输入默认模型', trigger: 'blur' }]
})

const resetForm = () => {
	Object.assign(dataForm, {
		aiApiConfigCode: '',
		aiApiConfigName: '',
		aiProviderCode: 'openai',
		aiApiConfigBaseUrl: 'https://api.openai.com/v1',
		aiApiConfigApiKey: '',
		aiApiConfigDefaultModel: 'gpt-4o-mini',
		aiApiConfigInputPricePer1k: 0,
		aiApiConfigOutputPricePer1k: 0,
		aiApiConfigEnabledCode: '1',
		aiApiConfigRemark: ''
	})
}

const init = (code?: string) => {
	visible.value = true
	isEdit.value = !!code
	resetForm()
	if (dataFormRef.value) dataFormRef.value.resetFields()
	if (code) {
		service.get('/mgt/ai/aiApiConfig/queryById?id=' + encodeURIComponent(code)).then(res => {
			Object.assign(dataForm, res.data)
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) return
		const http = isEdit.value
			? service.post('/mgt/ai/aiApiConfig/edit', { ...dataForm })
			: service.post('/mgt/ai/aiApiConfig/add', { ...dataForm })
		http.then(() => {
			ElMessage.success('操作成功')
			visible.value = false
			emit('refreshDataList')
		})
	})
}

defineExpose({ init })
</script>
