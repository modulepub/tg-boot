<template>
	<el-dialog v-model="visible" :title="!isEdit ? '新增公众号配置' : '修改公众号配置'" :close-on-click-modal="false" width="680px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="150px">
			<el-form-item label="配置编码" prop="wxMpConfigCode">
				<el-input v-model="dataForm.wxMpConfigCode" placeholder="主键，唯一" :disabled="isEdit"></el-input>
			</el-form-item>
			<el-form-item label="配置名称" prop="wxMpConfigName">
				<el-input v-model="dataForm.wxMpConfigName" placeholder="如：卿卿公众号"></el-input>
			</el-form-item>
			<el-form-item label="公众号 AppId" prop="wxMpConfigAppId">
				<el-input v-model="dataForm.wxMpConfigAppId" placeholder="wxXXXXXXXX"></el-input>
			</el-form-item>
			<el-form-item label="公众号 AppSecret" prop="wxMpConfigAppSecret">
				<el-input v-model="dataForm.wxMpConfigAppSecret" type="password" show-password placeholder="微信公众平台 AppSecret"></el-input>
			</el-form-item>
			<el-form-item label="消息校验 Token" prop="wxMpConfigToken">
				<div class="token-field">
					<el-input v-model="dataForm.wxMpConfigToken" placeholder="3-32 位英文或数字，需与微信公众平台一致"></el-input>
					<el-button type="primary" plain @click="generateToken">随机生成</el-button>
				</div>
			</el-form-item>
			<el-form-item label="EncodingAESKey" prop="wxMpConfigAesKey">
				<el-input v-model="dataForm.wxMpConfigAesKey" placeholder="消息加解密密钥（明文模式可留空）"></el-input>
			</el-form-item>
			<el-form-item label="启用状态" prop="wxMpConfigEnabledStatusCode">
				<el-select v-model="dataForm.wxMpConfigEnabledStatusCode" placeholder="请选择" style="width: 100%">
					<el-option label="启用" value="1" />
					<el-option label="停用" value="0" />
				</el-select>
			</el-form-item>
			<el-divider content-position="left">AI 自动回复</el-divider>
			<el-form-item label="AI 自动回复" prop="wxMpConfigAiAutoReplyStatusCode">
				<el-select v-model="dataForm.wxMpConfigAiAutoReplyStatusCode" placeholder="请选择" style="width: 100%">
					<el-option label="关闭（仅人工回复）" value="0" />
					<el-option label="开启（智能体自动回复）" value="1" />
				</el-select>
			</el-form-item>
			<el-form-item label="接管智能体" prop="wxMpConfigAiAgentCode">
				<el-select v-model="dataForm.wxMpConfigAiAgentCode" filterable clearable placeholder="请选择 AI 智能体" style="width: 100%">
					<el-option v-for="item in agentList" :key="item.aiAgentCode" :label="formatAgentLabel(item)" :value="item.aiAgentCode" />
				</el-select>
			</el-form-item>
			<el-divider content-position="left">关注回复</el-divider>
			<el-form-item label="关注回复" prop="wxMpConfigSubscribeReplyStatusCode">
				<el-select v-model="dataForm.wxMpConfigSubscribeReplyStatusCode" placeholder="请选择" style="width: 100%">
					<el-option label="关闭" value="0" />
					<el-option label="开启（回复图文消息）" value="1" />
				</el-select>
			</el-form-item>
			<template v-if="dataForm.wxMpConfigSubscribeReplyStatusCode === '1'">
				<el-form-item label="图文标题" prop="subscribeReplyTitle">
					<el-input v-model="subscribeReply.title" placeholder="图文消息标题"></el-input>
				</el-form-item>
				<el-form-item label="图文描述" prop="subscribeReplyDescription">
					<el-input v-model="subscribeReply.description" type="textarea" placeholder="图文消息描述（可选）"></el-input>
				</el-form-item>
				<el-form-item label="封面图" prop="subscribeReplyPicUrl">
					<tg-upload-image v-model:image-url="subscribeReply.picUrl" biz="wx" width="160px" height="160px">
						<template #tip>限 1 张，JPG/PNG/GIF，单张不超过 5M；上传后自动填入图片地址</template>
					</tg-upload-image>
				</el-form-item>
				<el-form-item label="跳转链接" prop="subscribeReplyUrl">
					<el-input v-model="subscribeReply.url" placeholder="用户点击图文后跳转的链接"></el-input>
				</el-form-item>
			</template>
			<el-form-item label="备注" prop="wxMpConfigRemark">
				<el-input v-model="dataForm.wxMpConfigRemark" type="textarea" placeholder="备注"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

interface AiAgentOption {
	aiAgentCode: string
	aiAgentName?: string
	aiAgentModel?: string
	aiAgentEnabledCode?: string
}

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()
const isEdit = ref(false)
const agentList = ref<AiAgentOption[]>([])

const dataForm = reactive({
	wxMpConfigCode: '',
	wxMpConfigName: '',
	wxMpConfigAppId: '',
	wxMpConfigAppSecret: '',
	wxMpConfigToken: '',
	wxMpConfigAesKey: '',
	wxMpConfigEnabledStatusCode: '1',
	wxMpConfigAiAutoReplyStatusCode: '0',
	wxMpConfigAiAgentCode: '',
	wxMpConfigSubscribeReplyStatusCode: '0',
	wxMpConfigSubscribeReplyJson: '',
	wxMpConfigRemark: ''
})

const subscribeReply = reactive({
	title: '',
	description: '',
	picUrl: '',
	url: ''
})

const dataRules = ref({
	wxMpConfigCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
	wxMpConfigAppId: [{ required: true, message: '请输入 AppId', trigger: 'blur' }],
	wxMpConfigAppSecret: [{ required: true, message: '请输入 AppSecret', trigger: 'blur' }]
})

const resetForm = () => {
	dataForm.wxMpConfigCode = ''
	dataForm.wxMpConfigName = ''
	dataForm.wxMpConfigAppId = ''
	dataForm.wxMpConfigAppSecret = ''
	dataForm.wxMpConfigToken = ''
	dataForm.wxMpConfigAesKey = ''
	dataForm.wxMpConfigEnabledStatusCode = '1'
	dataForm.wxMpConfigAiAutoReplyStatusCode = '0'
	dataForm.wxMpConfigAiAgentCode = ''
	dataForm.wxMpConfigSubscribeReplyStatusCode = '0'
	dataForm.wxMpConfigSubscribeReplyJson = ''
	dataForm.wxMpConfigRemark = ''
	subscribeReply.title = ''
	subscribeReply.description = ''
	subscribeReply.picUrl = ''
	subscribeReply.url = ''
}

const parseSubscribeReplyJson = (json?: string) => {
	subscribeReply.title = ''
	subscribeReply.description = ''
	subscribeReply.picUrl = ''
	subscribeReply.url = ''
	if (!json) {
		return
	}
	try {
		const obj = JSON.parse(json)
		subscribeReply.title = obj.title || ''
		subscribeReply.description = obj.description || ''
		subscribeReply.picUrl = obj.picUrl || ''
		subscribeReply.url = obj.url || ''
	} catch {
		// ignore invalid json
	}
}

const buildSubscribeReplyJson = () => {
	if (dataForm.wxMpConfigSubscribeReplyStatusCode !== '1') {
		return ''
	}
	return JSON.stringify({
		title: subscribeReply.title.trim(),
		description: subscribeReply.description.trim(),
		picUrl: subscribeReply.picUrl.trim(),
		url: subscribeReply.url.trim()
	})
}

const loadAgentList = () => {
	service.get('/mgt/ai/aiAgent/list?pageNo=1&pageSize=200').then((res: any) => {
		agentList.value = (res.data?.records || []).filter((item: AiAgentOption) => item.aiAgentEnabledCode === '1')
	})
}

const formatAgentLabel = (item: AiAgentOption) => {
	const name = item.aiAgentName || item.aiAgentCode
	return item.aiAgentModel ? `${name} (${item.aiAgentModel})` : name
}

const TOKEN_CHARS = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'

const generateToken = () => {
	const len = 32
	const bytes = new Uint8Array(len)
	crypto.getRandomValues(bytes)
	dataForm.wxMpConfigToken = Array.from(bytes, b => TOKEN_CHARS[b % TOKEN_CHARS.length]).join('')
	ElMessage.success('已生成 Token，保存后请同步到微信公众平台「消息推送」配置')
}

onMounted(() => {
	loadAgentList()
})

const init = (wxMpConfigCode?: string) => {
	visible.value = true
	isEdit.value = !!wxMpConfigCode
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (wxMpConfigCode) {
		service.get('/mgt/wx/wxMpConfig/queryById?id=' + encodeURIComponent(wxMpConfigCode)).then(res => {
			Object.assign(dataForm, res.data)
			parseSubscribeReplyJson(res.data?.wxMpConfigSubscribeReplyJson)
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		if (dataForm.wxMpConfigSubscribeReplyStatusCode === '1') {
			if (!subscribeReply.title.trim()) {
				ElMessage.warning('请填写图文标题')
				return false
			}
			if (!subscribeReply.picUrl.trim()) {
				ElMessage.warning('请上传封面图')
				return false
			}
			if (!subscribeReply.url.trim()) {
				ElMessage.warning('请填写跳转链接')
				return false
			}
		}
		const payload = { ...dataForm, wxMpConfigSubscribeReplyJson: buildSubscribeReplyJson() }
		const http = isEdit.value ? service.post('/mgt/wx/wxMpConfig/edit', payload) : service.post('/mgt/wx/wxMpConfig/add', payload)
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
.token-field {
	display: flex;
	gap: 8px;
	width: 100%;
}
.token-field .el-input {
	flex: 1;
}
</style>
