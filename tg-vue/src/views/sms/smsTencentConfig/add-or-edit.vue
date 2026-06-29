<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-alert
				type="info"
				:closable="false"
				show-icon
				title="腾讯云短信配置：请在腾讯云控制台获取 SecretId / SecretKey / SdkAppId，签名需提前申请通过。"
				style="margin-bottom: 16px"
			/>
			<el-form-item label="配置编码" prop="smsTencentConfigCode">
				<el-input v-model="dataForm.smsTencentConfigCode" placeholder="主键，唯一，如 default" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="smsTencentConfigEnabledCode">
				<el-select v-model="dataForm.smsTencentConfigEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="SecretId" prop="smsTencentConfigSecretId">
				<el-input v-model="dataForm.smsTencentConfigSecretId" placeholder="腾讯云 SecretId"></el-input>
			</el-form-item>
			<el-form-item label="SecretKey" prop="smsTencentConfigSecretKey">
				<el-input v-model="dataForm.smsTencentConfigSecretKey" type="password" show-password placeholder="腾讯云 SecretKey"></el-input>
			</el-form-item>
			<el-form-item label="SdkAppId" prop="smsTencentConfigSdkAppId">
				<el-input v-model="dataForm.smsTencentConfigSdkAppId" placeholder="短信 SdkAppId"></el-input>
			</el-form-item>
			<el-form-item label="默认签名" prop="smsTencentConfigSignName">
				<el-input v-model="dataForm.smsTencentConfigSignName" placeholder="短信签名"></el-input>
			</el-form-item>
			<el-form-item label="接入地域" prop="smsTencentConfigRegion">
				<el-input v-model="dataForm.smsTencentConfigRegion" placeholder="ap-guangzhou"></el-input>
			</el-form-item>
			<el-form-item label="备注" prop="smsTencentConfigRemark">
				<el-input v-model="dataForm.smsTencentConfigRemark" type="textarea" placeholder="备注"></el-input>
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
	smsTencentConfigCode: '',
	smsTencentConfigEnabledCode: '1',
	smsTencentConfigSecretId: '',
	smsTencentConfigSecretKey: '',
	smsTencentConfigSdkAppId: '',
	smsTencentConfigSignName: '',
	smsTencentConfigRegion: 'ap-guangzhou',
	smsTencentConfigRemark: ''
})

const dataRules = ref({
	smsTencentConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	smsTencentConfigEnabledCode: [{ required: true, message: '请选择启用状态', trigger: 'change' }]
})

const resetForm = () => {
	dataForm.smsTencentConfigCode = ''
	dataForm.smsTencentConfigEnabledCode = '1'
	dataForm.smsTencentConfigSecretId = ''
	dataForm.smsTencentConfigSecretKey = ''
	dataForm.smsTencentConfigSdkAppId = ''
	dataForm.smsTencentConfigSignName = ''
	dataForm.smsTencentConfigRegion = 'ap-guangzhou'
	dataForm.smsTencentConfigRemark = ''
}

const init = (smsTencentConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!smsTencentConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (smsTencentConfigCode) {
		service.get('/mgt/sms/smsTencentConfig/queryById?id=' + encodeURIComponent(smsTencentConfigCode)).then(res => {
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
		const http = isEdit.value ? service.post('/mgt/sms/smsTencentConfig/edit', payload) : service.post('/mgt/sms/smsTencentConfig/add', payload)
		http.then(() => {
			ElMessage.success({
				message: '操作成功（已刷新运行时配置）',
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
