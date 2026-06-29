<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增智能体' : '修改智能体'" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="智能体编码" prop="aiAgentCode">
				<el-input v-model="dataForm.aiAgentCode" placeholder="留空自动生成" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="名称" prop="aiAgentName">
				<el-input v-model="dataForm.aiAgentName" placeholder="如 客服助手"></el-input>
			</el-form-item>
			<el-form-item label="接口配置" prop="aiApiConfigCode">
				<el-select
					v-model="dataForm.aiApiConfigCode"
					clearable
					filterable
					placeholder="请选择（可选，留空则用首个已启用配置）"
					style="width: 100%"
					@change="onApiConfigChange"
				>
					<el-option
						v-for="item in apiConfigList"
						:key="item.aiApiConfigCode"
						:label="formatApiConfigLabel(item)"
						:value="item.aiApiConfigCode"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="模型" prop="aiAgentModel">
				<el-select
					v-model="dataForm.aiAgentModel"
					clearable
					filterable
					placeholder="留空则使用接口默认模型"
					style="width: 100%"
				>
					<el-option v-for="model in modelOptions" :key="model" :label="model" :value="model" />
				</el-select>
			</el-form-item>
			<el-form-item label="人设 / 系统提示词" prop="aiAgentPersona">
				<el-input v-model="dataForm.aiAgentPersona" type="textarea" :rows="6" placeholder="你是一个专业的客服助手..."></el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="aiAgentEnabledCode">
				<el-select v-model="dataForm.aiAgentEnabledCode" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="aiAgentRemark">
				<el-input v-model="dataForm.aiAgentRemark" type="textarea"></el-input>
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

interface AiApiConfigOption {
	aiApiConfigCode: string
	aiApiConfigName: string
	aiApiConfigDefaultModel?: string
	aiApiConfigEnabledCode?: string
}

const emit = defineEmits(['refreshDataList'])
const visible = ref(false)
const dataFormRef = ref()
const isEdit = ref(false)
const apiConfigList = ref<AiApiConfigOption[]>([])
const modelOptions = ref<string[]>([])

const COMMON_MODELS = [
	'gpt-4o',
	'gpt-4o-mini',
	'gpt-4-turbo',
	'deepseek-chat',
	'deepseek-reasoner',
	'moonshot-v1-8k',
	'moonshot-v1-32k',
	'glm-4-flash',
	'glm-4-plus'
]

const dataForm = reactive({
	aiAgentCode: '',
	aiAgentName: '',
	aiApiConfigCode: '',
	aiAgentModel: '',
	aiAgentPersona: '',
	aiAgentEnabledCode: '1',
	aiAgentRemark: ''
})

const dataRules = ref({
	aiAgentName: [{ required: true, message: '请输入名称', trigger: 'blur' }]
})

const formatApiConfigLabel = (item: AiApiConfigOption) => {
	const name = item.aiApiConfigName || item.aiApiConfigCode
	const model = item.aiApiConfigDefaultModel ? ` · ${item.aiApiConfigDefaultModel}` : ''
	return `${name}${model}`
}

const refreshModelOptions = () => {
	const set = new Set<string>()
	const selected = apiConfigList.value.find(item => item.aiApiConfigCode === dataForm.aiApiConfigCode)

	if (selected?.aiApiConfigDefaultModel) {
		set.add(selected.aiApiConfigDefaultModel)
	} else {
		apiConfigList.value.forEach(item => {
			if (item.aiApiConfigDefaultModel) {
				set.add(item.aiApiConfigDefaultModel)
			}
		})
	}

	COMMON_MODELS.forEach(model => set.add(model))
	if (dataForm.aiAgentModel) {
		set.add(dataForm.aiAgentModel)
	}
	modelOptions.value = Array.from(set)
}

const loadApiConfigList = () => {
	return service.get('/mgt/ai/aiApiConfig/list?pageNo=1&pageSize=200').then((res: any) => {
		const rows: AiApiConfigOption[] = res.data?.records || []
		apiConfigList.value = rows.filter(item => item.aiApiConfigEnabledCode === '1')
		refreshModelOptions()
	})
}

const onApiConfigChange = () => {
	refreshModelOptions()
	if (dataForm.aiAgentModel && !modelOptions.value.includes(dataForm.aiAgentModel)) {
		dataForm.aiAgentModel = ''
	}
}

const resetForm = () => {
	Object.assign(dataForm, {
		aiAgentCode: '',
		aiAgentName: '',
		aiApiConfigCode: '',
		aiAgentModel: '',
		aiAgentPersona: '',
		aiAgentEnabledCode: '1',
		aiAgentRemark: ''
	})
	modelOptions.value = []
}

const init = (code?: string) => {
	visible.value = true
	isEdit.value = !!code
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	loadApiConfigList().then(() => {
		if (code) {
			service.get('/mgt/ai/aiAgent/queryById?id=' + encodeURIComponent(code)).then(res => {
				Object.assign(dataForm, res.data)
				refreshModelOptions()
			})
		}
	})
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) return
		const http = isEdit.value
			? service.post('/mgt/ai/aiAgent/edit', { ...dataForm })
			: service.post('/mgt/ai/aiAgent/add', { ...dataForm })
		http.then(() => {
			ElMessage.success('操作成功')
			visible.value = false
			emit('refreshDataList')
		})
	})
}

defineExpose({ init })
</script>
