<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="840px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="配置编码" prop="wxVirtualPayConfigCode">
				<el-input v-model="dataForm.wxVirtualPayConfigCode" placeholder="主键，唯一" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="微信 AppId" prop="wxVirtualPayConfigAppId">
				<el-input v-model="dataForm.wxVirtualPayConfigAppId" placeholder="wxXXXXXXXX"></el-input>
			</el-form-item>
			<el-form-item label="米大师 OfferId" prop="wxVirtualPayConfigOfferId">
				<el-input v-model="dataForm.wxVirtualPayConfigOfferId" placeholder="道具后台 OfferId"></el-input>
			</el-form-item>
			<el-form-item label="沙箱 AppKey" prop="wxVirtualPayConfigAppKeySandbox">
				<el-input v-model="dataForm.wxVirtualPayConfigAppKeySandbox" type="password" show-password placeholder="沙箱环境 AppKey"></el-input>
			</el-form-item>
			<el-form-item label="现网 AppKey" prop="wxVirtualPayConfigAppKeyProd">
				<el-input v-model="dataForm.wxVirtualPayConfigAppKeyProd" type="password" show-password placeholder="正式环境 AppKey"></el-input>
			</el-form-item>
			<el-form-item label="通知 URL" prop="wxVirtualPayConfigNotifyUrl">
				<el-input v-model="dataForm.wxVirtualPayConfigNotifyUrl" placeholder="虚拟支付发货/支付回调地址"></el-input>
			</el-form-item>
			<el-form-item label="沙箱环境" prop="wxVirtualPayConfigUseSandbox">
				<el-select v-model="dataForm.wxVirtualPayConfigUseSandbox" placeholder="请选择" style="width: 100%">
					<el-option label="否（现网）" :value="0" />
					<el-option label="是（沙箱）" :value="1" />
				</el-select>
			</el-form-item>
			<el-form-item label="启用状态" prop="wxVirtualPayConfigEnabledCode">
				<el-select v-model="dataForm.wxVirtualPayConfigEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="wxVirtualPayConfigRemark">
				<el-input v-model="dataForm.wxVirtualPayConfigRemark" type="textarea" placeholder="备注"></el-input>
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
	wxVirtualPayConfigCode: '',
	wxVirtualPayConfigAppId: '',
	wxVirtualPayConfigOfferId: '',
	wxVirtualPayConfigAppKeySandbox: '',
	wxVirtualPayConfigAppKeyProd: '',
	wxVirtualPayConfigNotifyUrl: '',
	wxVirtualPayConfigUseSandbox: 0,
	wxVirtualPayConfigEnabledCode: '1',
	wxVirtualPayConfigRemark: ''
})

const dataRules = ref({
	wxVirtualPayConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	wxVirtualPayConfigAppId: [{ required: true, message: '请输入 AppId', trigger: 'blur' }],
	wxVirtualPayConfigOfferId: [{ required: true, message: '请输入 OfferId', trigger: 'blur' }]
})

const resetForm = () => {
	dataForm.wxVirtualPayConfigCode = ''
	dataForm.wxVirtualPayConfigAppId = ''
	dataForm.wxVirtualPayConfigOfferId = ''
	dataForm.wxVirtualPayConfigAppKeySandbox = ''
	dataForm.wxVirtualPayConfigAppKeyProd = ''
	dataForm.wxVirtualPayConfigNotifyUrl = ''
	dataForm.wxVirtualPayConfigUseSandbox = 0
	dataForm.wxVirtualPayConfigEnabledCode = '1'
	dataForm.wxVirtualPayConfigRemark = ''
}

const init = (wxVirtualPayConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!wxVirtualPayConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (wxVirtualPayConfigCode) {
		service.get('/mgt/wx/wxVirtualPayConfig/queryByCode', { params: { wxVirtualPayConfigCode } }).then(res => {
			if (!res.data) {
				ElMessage.warning('未找到该配置，请刷新列表后重试')
				return
			}
			Object.assign(dataForm, res.data)
			if (dataForm.wxVirtualPayConfigUseSandbox !== 0 && dataForm.wxVirtualPayConfigUseSandbox !== 1) {
				dataForm.wxVirtualPayConfigUseSandbox = Number(dataForm.wxVirtualPayConfigUseSandbox) === 1 ? 1 : 0
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
		const http = isEdit.value
			? service.post('/mgt/wx/wxVirtualPayConfig/edit', payload)
			: service.post('/mgt/wx/wxVirtualPayConfig/add', payload)
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
