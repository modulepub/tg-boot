<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增' : '修改'" :close-on-click-modal="false" width="640px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="130px">
			<el-form-item label="配置编码" prop="wxMiniConfigCode">
				<el-input v-model="dataForm.wxMiniConfigCode" placeholder="主键，唯一" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="配置名称" prop="wxMiniConfigName">
				<el-input v-model="dataForm.wxMiniConfigName" placeholder="如：卿卿小程序"></el-input>
			</el-form-item>
			<el-form-item label="小程序 AppId" prop="wxMiniConfigAppId">
				<el-input v-model="dataForm.wxMiniConfigAppId" placeholder="wxXXXXXXXX"></el-input>
			</el-form-item>
			<el-form-item label="小程序 AppSecret" prop="wxMiniConfigAppSecret">
				<el-input v-model="dataForm.wxMiniConfigAppSecret" type="password" show-password placeholder="微信公众平台 AppSecret"></el-input>
			</el-form-item>
			<el-form-item label="消息格式" prop="wxMiniConfigMsgDataFormat">
				<el-select v-model="dataForm.wxMiniConfigMsgDataFormat" placeholder="请选择" style="width: 100%">
					<el-option label="JSON" value="JSON" />
					<el-option label="XML" value="XML" />
				</el-select>
			</el-form-item>
			<el-form-item label="消息推送 Token" prop="wxMiniConfigToken">
				<el-input v-model="dataForm.wxMiniConfigToken" placeholder="与微信「消息推送」一致，用于 URL 验签">
					<template #append>
						<el-button @click="genToken">随机生成</el-button>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item label="EncodingAESKey" prop="wxMiniConfigAesKey">
				<el-input v-model="dataForm.wxMiniConfigAesKey" placeholder="安全模式 AES 解密用，明文模式可留空">
					<template #append>
						<el-button @click="genAesKey">随机生成</el-button>
					</template>
				</el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="wxMiniConfigEnabledCode">
				<el-select v-model="dataForm.wxMiniConfigEnabledCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注" prop="wxMiniConfigRemark">
				<el-input v-model="dataForm.wxMiniConfigRemark" type="textarea" placeholder="备注"></el-input>
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
	wxMiniConfigCode: '',
	wxMiniConfigName: '',
	wxMiniConfigAppId: '',
	wxMiniConfigAppSecret: '',
	wxMiniConfigMsgDataFormat: 'JSON',
	wxMiniConfigToken: '',
	wxMiniConfigAesKey: '',
	wxMiniConfigEnabledCode: '1',
	wxMiniConfigRemark: ''
})

const dataRules = ref({
	wxMiniConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	wxMiniConfigAppId: [{ required: true, message: '请输入 AppId', trigger: 'blur' }],
	wxMiniConfigAppSecret: [{ required: true, message: '请输入 AppSecret', trigger: 'blur' }]
})

const randomString = (length: number) => {
	const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
	let result = ''
	const cryptoObj = window.crypto || (window as any).msCrypto
	if (cryptoObj?.getRandomValues) {
		const buf = new Uint32Array(length)
		cryptoObj.getRandomValues(buf)
		for (let i = 0; i < length; i++) {
			result += chars[buf[i] % chars.length]
		}
	}
	else {
		for (let i = 0; i < length; i++) {
			result += chars[Math.floor(Math.random() * chars.length)]
		}
	}
	return result
}

// Token：微信要求 3-32 位字母数字，这里固定取 32 位
const genToken = () => {
	dataForm.wxMiniConfigToken = randomString(32)
}

// EncodingAESKey：微信要求 43 位字母数字
const genAesKey = () => {
	dataForm.wxMiniConfigAesKey = randomString(43)
}

const resetForm = () => {
	dataForm.wxMiniConfigCode = ''
	dataForm.wxMiniConfigName = ''
	dataForm.wxMiniConfigAppId = ''
	dataForm.wxMiniConfigAppSecret = ''
	dataForm.wxMiniConfigMsgDataFormat = 'JSON'
	dataForm.wxMiniConfigToken = ''
	dataForm.wxMiniConfigAesKey = ''
	dataForm.wxMiniConfigEnabledCode = '1'
	dataForm.wxMiniConfigRemark = ''
}

const init = (wxMiniConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!wxMiniConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (wxMiniConfigCode) {
		service.get('/mgt/wx/wxMiniConfig/queryById?id=' + encodeURIComponent(wxMiniConfigCode)).then(res => {
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
		const http = isEdit.value ? service.post('/mgt/wx/wxMiniConfig/edit', payload) : service.post('/mgt/wx/wxMiniConfig/add', payload)
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
