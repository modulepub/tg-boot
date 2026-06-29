<template>
	<el-card>
		<el-alert title="调用示例：选择智能体后发送消息，系统将记录对话内容与消耗明细。" type="info" show-icon :closable="false" style="margin-bottom: 16px" />

		<el-form :model="form" label-width="120px" style="max-width: 800px">
			<el-form-item label="智能体">
				<el-select v-model="form.aiAgentCode" filterable placeholder="请选择智能体" style="width: 100%">
					<el-option
						v-for="item in agentList"
						:key="item.aiAgentCode"
						:label="formatAgentLabel(item)"
						:value="item.aiAgentCode"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="用户编码">
				<el-input v-model="form.userCode" placeholder="用于记录消耗明细"></el-input>
			</el-form-item>
			<el-form-item label="会话编码">
				<el-input v-model="form.aiChatSessionCode" placeholder="留空则新建会话；续聊时填入已有会话编码"></el-input>
			</el-form-item>
			<el-form-item label="消息">
				<el-input v-model="form.message" type="textarea" :rows="4" placeholder="输入要发送的消息"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" :loading="loading" @click="sendChat">发送</el-button>
				<el-button @click="clearChat">清空对话</el-button>
			</el-form-item>
		</el-form>

		<el-divider>对话记录</el-divider>
		<div v-if="chatHistory.length === 0" style="color: #909399">暂无对话，发送消息后显示</div>
		<div v-else class="chat-history">
			<div v-for="(msg, idx) in chatHistory" :key="idx" class="chat-item" :class="'role-' + msg.role">
				<span class="role">{{ roleLabel(msg.role) }}：</span>
				<span class="text">{{ msg.content }}</span>
			</div>
		</div>

		<el-divider v-if="lastResponse">本次调用结果</el-divider>
		<el-descriptions v-if="lastResponse" :column="2" border size="small">
			<el-descriptions-item label="会话编码">{{ lastResponse.aiChatSessionCode }}</el-descriptions-item>
			<el-descriptions-item label="模型">{{ lastResponse.model }}</el-descriptions-item>
			<el-descriptions-item label="输入 tokens">{{ lastResponse.promptTokens }}</el-descriptions-item>
			<el-descriptions-item label="输出 tokens">{{ lastResponse.completionTokens }}</el-descriptions-item>
			<el-descriptions-item label="总价(元)">{{ lastResponse.totalPrice }}</el-descriptions-item>
			<el-descriptions-item label="消耗明细">{{ lastResponse.aiUsageRecordCode }}</el-descriptions-item>
		</el-descriptions>
	</el-card>
</template>

<script setup lang="ts" name="AiAiChatDemoIndex">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

interface AiAgentOption {
	aiAgentCode: string
	aiAgentName: string
	aiAgentModel?: string
	aiAgentEnabledCode?: string
}

const loading = ref(false)
const agentList = ref<AiAgentOption[]>([])
const form = reactive({
	aiAgentCode: '',
	userCode: 'admin',
	aiChatSessionCode: '',
	message: ''
})

const chatHistory = ref<Array<{ role: string; content: string }>>([])
const lastResponse = ref<any>(null)

const roleLabel = (role: string) => {
	if (role === 'user') return '用户'
	if (role === 'assistant') return '助手'
	if (role === 'system') return '系统'
	return role
}

const formatAgentLabel = (item: AiAgentOption) => {
	const name = item.aiAgentName || item.aiAgentCode
	const model = item.aiAgentModel ? ` · ${item.aiAgentModel}` : ''
	return `${name}${model}`
}

const loadAgentList = () => {
	service.get('/mgt/ai/aiAgent/list?pageNo=1&pageSize=200').then((res: any) => {
		const rows: AiAgentOption[] = res.data?.records || []
		agentList.value = rows.filter(item => item.aiAgentEnabledCode === '1')
	})
}

onMounted(() => {
	loadAgentList()
})

const sendChat = () => {
	if (!form.aiAgentCode || !form.userCode || !form.message) {
		ElMessage.warning('请选择智能体并填写用户编码和消息')
		return
	}
	loading.value = true
	service
		.post('/mgt/ai/chat', {
			aiAgentCode: form.aiAgentCode,
			userCode: form.userCode,
			aiChatSessionCode: form.aiChatSessionCode || undefined,
			message: form.message
		})
		.then(res => {
			lastResponse.value = res.data
			if (res.data?.aiChatSessionCode) {
				form.aiChatSessionCode = res.data.aiChatSessionCode
			}
			if (res.data?.messages) {
				chatHistory.value = res.data.messages.map((m: any) => ({
					role: m.aiChatMessageRoleCode,
					content: m.aiChatMessageContent
				}))
			}
			form.message = ''
			ElMessage.success('调用成功')
		})
		.finally(() => {
			loading.value = false
		})
}

const clearChat = () => {
	chatHistory.value = []
	lastResponse.value = null
	form.aiChatSessionCode = ''
	form.message = ''
}
</script>

<style scoped>
.chat-history {
	max-height: 400px;
	overflow-y: auto;
	border: 1px solid #ebeef5;
	border-radius: 8px;
	padding: 12px;
}
.chat-item {
	margin-bottom: 12px;
	line-height: 1.6;
}
.chat-item.role-user .role {
	color: #409eff;
}
.chat-item.role-assistant .role {
	color: #67c23a;
}
.text {
	white-space: pre-wrap;
	word-break: break-word;
}
</style>
