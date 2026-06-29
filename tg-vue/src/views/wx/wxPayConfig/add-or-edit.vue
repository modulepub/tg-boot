<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="840px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="配置编码" prop="wxPayConfigCode">
				<el-input v-model="dataForm.wxPayConfigCode" placeholder="主键，唯一" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="微信 AppId" prop="wxPayConfigAppId">
				<el-input v-model="dataForm.wxPayConfigAppId" placeholder="wxXXXXXXXX"></el-input>
			</el-form-item>
			<el-form-item label="微信商户号" prop="wxPayConfigMchId">
				<el-input v-model="dataForm.wxPayConfigMchId" placeholder="商户号 mch_id"></el-input>
			</el-form-item>
			<el-form-item label="APIv3 密钥" prop="wxPayConfigApiV3Key">
				<el-input v-model="dataForm.wxPayConfigApiV3Key" type="password" show-password placeholder="api v3 key"></el-input>
			</el-form-item>
			<el-form-item label="支付通知 URL" prop="wxPayConfigNotifyUrl">
				<el-input v-model="dataForm.wxPayConfigNotifyUrl" placeholder="与 WxPayCallback 地址一致"></el-input>
			</el-form-item>
			<el-form-item label="API 私钥 PEM" prop="wxPayConfigPrivateKey">
				<el-input
					v-model="dataForm.wxPayConfigPrivateKey"
					type="textarea"
					:rows="8"
					placeholder="粘贴 apiclient_key.pem 全文（含 BEGIN PRIVATE KEY / END PRIVATE KEY）"
					class="mono-textarea"
				/>
			</el-form-item>
			<el-form-item label="API 证书 PEM" prop="wxPayConfigPrivateCert">
				<el-input
					v-model="dataForm.wxPayConfigPrivateCert"
					type="textarea"
					:rows="8"
					placeholder="粘贴 apiclient_cert.pem 全文（含 BEGIN CERTIFICATE / END CERTIFICATE）"
					class="mono-textarea"
				/>
			</el-form-item>
			<el-form-item label="沙箱环境" prop="wxPayConfigUseSandbox">
				<el-select v-model="dataForm.wxPayConfigUseSandbox" placeholder="请选择" style="width: 100%">
					<el-option label="否" :value="0" />
					<el-option label="是" :value="1" />
				</el-select>
			</el-form-item>
			<el-form-item label="启用状态" prop="wxPayConfigEnabledCode">
				<el-select v-model="dataForm.wxPayConfigEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="wxPayConfigRemark">
				<el-input v-model="dataForm.wxPayConfigRemark" type="textarea" placeholder="备注"></el-input>
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
	wxPayConfigCode: '',
	wxPayConfigAppId: '',
	wxPayConfigMchId: '',
	wxPayConfigApiV3Key: '',
	wxPayConfigNotifyUrl: '',
	wxPayConfigPrivateKey: '',
	wxPayConfigPrivateCert: '',
	wxPayConfigUseSandbox: 0,
	wxPayConfigEnabledCode: '1',
	wxPayConfigRemark: ''
})

const dataRules = ref({
	wxPayConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	wxPayConfigAppId: [{ required: true, message: '请输入 AppId', trigger: 'blur' }],
	wxPayConfigMchId: [{ required: true, message: '请输入商户号', trigger: 'blur' }]
})

const resetForm = () => {
	dataForm.wxPayConfigCode = ''
	dataForm.wxPayConfigAppId = ''
	dataForm.wxPayConfigMchId = ''
	dataForm.wxPayConfigApiV3Key = ''
	dataForm.wxPayConfigNotifyUrl = ''
	dataForm.wxPayConfigPrivateKey = ''
	dataForm.wxPayConfigPrivateCert = ''
	dataForm.wxPayConfigUseSandbox = 0
	dataForm.wxPayConfigEnabledCode = '1'
	dataForm.wxPayConfigRemark = ''
}

const init = (wxPayConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!wxPayConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (wxPayConfigCode) {
		service.get('/mgt/wx/wxPayConfig/queryById?id=' + encodeURIComponent(wxPayConfigCode)).then(res => {
			Object.assign(dataForm, res.data)
			if (dataForm.wxPayConfigUseSandbox !== 0 && dataForm.wxPayConfigUseSandbox !== 1) {
				dataForm.wxPayConfigUseSandbox = Number(dataForm.wxPayConfigUseSandbox) === 1 ? 1 : 0
			}
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const payload = { ...dataForm }
		const http = isEdit.value ? service.post('/mgt/wx/wxPayConfig/edit', payload) : service.post('/mgt/wx/wxPayConfig/add', payload)
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

<style scoped>
.mono-textarea :deep(textarea) {
	font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
	font-size: 12px;
	line-height: 1.45;
}
</style>
