<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="模板编码" prop="smsTemplateCode">
				<el-input v-model="dataForm.smsTemplateCode" placeholder="主键，唯一" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="渠道" prop="smsProviderCode">
				<el-select v-model="dataForm.smsProviderCode" placeholder="请选择渠道" style="width: 100%">
					<el-option label="腾讯云短信" value="tencent" />
					<el-option label="创蓝短信" value="chuangLan" />
					<el-option label="玄武 MOS" value="mosSmsSdk" />
				</el-select>
			</el-form-item>
			<el-form-item label="渠道模板 ID" prop="smsTemplateId">
				<el-input v-model="dataForm.smsTemplateId" placeholder="腾讯云等渠道返回的模板 ID（如 123456）"></el-input>
			</el-form-item>
			<el-form-item label="模板内容" prop="smsTemplateContent">
				<el-input v-model="dataForm.smsTemplateContent" type="textarea" placeholder="模板内容示例：您的验证码为{1}，请于{2}分钟内填写"></el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="smsTemplateEnabledCode">
				<el-select v-model="dataForm.smsTemplateEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="smsTemplateRemark">
				<el-input v-model="dataForm.smsTemplateRemark" type="textarea" placeholder="备注"></el-input>
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
	smsTemplateCode: '',
	smsProviderCode: 'tencent',
	smsTemplateId: '',
	smsTemplateContent: '',
	smsTemplateEnabledCode: '1',
	smsTemplateRemark: ''
})

const dataRules = ref({
	smsTemplateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
	smsProviderCode: [{ required: true, message: '请选择渠道', trigger: 'change' }]
})

const resetForm = () => {
	dataForm.smsTemplateCode = ''
	dataForm.smsProviderCode = 'tencent'
	dataForm.smsTemplateId = ''
	dataForm.smsTemplateContent = ''
	dataForm.smsTemplateEnabledCode = '1'
	dataForm.smsTemplateRemark = ''
}

const init = (smsTemplateCode?: string) => {
	visible.value = true
	isEdit.value = !!smsTemplateCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (smsTemplateCode) {
		service.get('/mgt/sms/smsTemplate/queryById?id=' + encodeURIComponent(smsTemplateCode)).then(res => {
			Object.assign(dataForm, res.data)
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const payload = { ...dataForm }
		const http = isEdit.value ? service.post('/mgt/sms/smsTemplate/edit', payload) : service.post('/mgt/sms/smsTemplate/add', payload)
		http.then(() => {
			ElMessage.success('操作成功')
			visible.value = false
			emit('refreshDataList')
		})
	})
}

defineExpose({
	init
})
</script>
