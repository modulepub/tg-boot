<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-alert
				type="info"
				:closable="false"
				show-icon
				title="请在云盾控制台开通「信息核验 → N要素 → 手机号二要素」；本系统调用实人认证 Mobile2MetaVerify，仅需 RAM 的 AccessKeyId / AccessKeySecret，无需 AuthCode。"
				style="margin-bottom: 16px"
			/>
			<el-form-item label="配置编码" prop="npConfigCode">
				<el-input v-model="dataForm.npConfigCode" placeholder="主键，唯一，如 default" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="npConfigEnabledCode">
				<el-select v-model="dataForm.npConfigEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="渠道编码" prop="npConfigProviderCode">
				<el-input v-model="dataForm.npConfigProviderCode" placeholder="默认 aliyun_cloudauth"></el-input>
			</el-form-item>
			<el-form-item label="AccessKeyId" prop="npConfigAccessKeyId">
				<el-input v-model="dataForm.npConfigAccessKeyId" placeholder="RAM 控制台 AccessKey，以 LTAI 开头"></el-input>
			</el-form-item>
			<el-form-item label="AccessKeySecret" prop="npConfigAccessKeySecret">
				<el-input v-model="dataForm.npConfigAccessKeySecret" type="password" show-password placeholder="阿里云 AccessKeySecret"></el-input>
			</el-form-item>
			<el-form-item label="接入点" prop="npConfigEndpoint">
				<el-input v-model="dataForm.npConfigEndpoint" placeholder="cloudauth.aliyuncs.com"></el-input>
			</el-form-item>
			<el-form-item label="传参掩码" prop="npConfigMask">
				<el-input v-model="dataForm.npConfigMask" placeholder="normal 或 md5"></el-input>
			</el-form-item>
			<el-form-item label="备注" prop="npConfigRemark">
				<el-input v-model="dataForm.npConfigRemark" type="textarea" placeholder="备注"></el-input>
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
	npConfigCode: '',
	npConfigEnabledCode: '1',
	npConfigProviderCode: 'aliyun_cloudauth',
	npConfigAccessKeyId: '',
	npConfigAccessKeySecret: '',
	npConfigEndpoint: 'cloudauth.aliyuncs.com',
	npConfigMask: 'normal',
	npConfigRemark: ''
})

const dataRules = ref({
	npConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	npConfigEnabledCode: [{ required: true, message: '请选择启用状态', trigger: 'change' }]
})

const resetForm = () => {
	dataForm.npConfigCode = ''
	dataForm.npConfigEnabledCode = '1'
	dataForm.npConfigProviderCode = 'aliyun_cloudauth'
	dataForm.npConfigAccessKeyId = ''
	dataForm.npConfigAccessKeySecret = ''
	dataForm.npConfigEndpoint = 'cloudauth.aliyuncs.com'
	dataForm.npConfigMask = 'normal'
	dataForm.npConfigRemark = ''
}

const init = (npConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!npConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (npConfigCode) {
		service.get('/mgt/verification/vtNpConfig/queryById?id=' + encodeURIComponent(npConfigCode)).then(res => {
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
		const http = isEdit.value ? service.post('/mgt/verification/vtNpConfig/edit', payload) : service.post('/mgt/verification/vtNpConfig/add', payload)
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
