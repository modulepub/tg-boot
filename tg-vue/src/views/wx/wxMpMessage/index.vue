<template>
	<div class="wx-mp-message-center">
		<el-card class="layout-query">
			<el-form :inline="true">
				<el-form-item label="公众号">
					<el-select v-model="wxMpConfigCode" filterable placeholder="请选择公众号" style="width: 280px" @change="onConfigChange">
						<el-option v-for="item in mpConfigList" :key="item.wxMpConfigCode" :label="formatConfigLabel(item)" :value="item.wxMpConfigCode" />
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-button icon="Refresh" @click="loadConversations">刷新</el-button>
				</el-form-item>
			</el-form>
		</el-card>

		<el-card class="wx-mp-message-card" :body-style="{ padding: 0, height: 'calc(100vh - 220px)' }">
			<div class="wx-mp-layout">
				<aside class="wx-mp-conv-panel">
					<div class="wx-mp-conv-header">粉丝会话</div>
					<el-scrollbar v-loading="convLoading" class="wx-mp-conv-scroll">
						<div
							v-for="item in conversations"
							:key="item.wxMpFanCode"
							class="wx-mp-conv-item"
							:class="{ active: activeFan?.wxMpFanOpenId === item.wxMpFanOpenId }"
							@click="selectFan(item)"
						>
							<el-avatar :size="40">{{ (item.wxMpFanNickname || item.wxMpFanOpenId || '?').slice(0, 1) }}</el-avatar>
							<div class="wx-mp-conv-body">
								<div class="wx-mp-conv-row">
									<span class="nick">{{ item.wxMpFanNickname || '微信用户' }}</span>
									<span class="time">{{ formatTime(item.wxMpFanLastMessageTime) }}</span>
								</div>
								<div class="preview">{{ item.wxMpFanLastMessageContent || ' ' }}</div>
							</div>
						</div>
						<el-empty v-if="!convLoading && conversations.length === 0" description="暂无会话" />
					</el-scrollbar>
				</aside>

				<section class="wx-mp-chat-panel">
					<template v-if="activeFan">
						<div class="wx-mp-chat-header">
							<span>{{ activeFan.wxMpFanNickname || '微信用户' }}</span>
							<span class="openid">{{ activeFan.wxMpFanOpenId }}</span>
						</div>
						<div v-loading="msgLoading" class="wx-mp-chat-messages">
							<div
								v-for="msg in messages"
								:key="msg.wxMpMessageCode"
								class="wx-mp-msg-row"
								:class="{ out: msg.wxMpMessageDirectionCode === 'out' }"
							>
								<div class="wx-mp-msg-bubble">
									<div class="content">{{ msg.wxMpMessageContent }}</div>
									<div class="meta">
										<span>{{ formatTime(msg.createTime) }}</span>
										<el-tag v-if="msg.wxMpMessageReplySourceCode === 'auto_ai'" size="small" type="success">AI</el-tag>
										<el-tag v-else-if="msg.wxMpMessageReplySourceCode === 'manual'" size="small" type="warning">人工</el-tag>
									</div>
								</div>
							</div>
							<el-empty v-if="!msgLoading && messages.length === 0" description="暂无消息" />
						</div>
						<div class="wx-mp-chat-input">
							<el-input v-model="draftText" type="textarea" :rows="3" placeholder="输入回复内容…" @keydown.enter.exact.prevent="sendReply" />
							<el-button type="primary" :loading="sending" @click="sendReply">发送</el-button>
						</div>
					</template>
					<el-empty v-else description="请选择左侧会话" />
				</section>
			</div>
		</el-card>
	</div>
</template>

<script setup lang="ts" name="WxWxMpMessageIndex">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

interface MpConfigOption {
	wxMpConfigCode: string
	wxMpConfigName?: string
	wxMpConfigAppId?: string
}

interface FanRow {
	wxMpFanCode: string
	wxMpFanOpenId: string
	wxMpFanNickname?: string
	wxMpFanLastMessageContent?: string
	wxMpFanLastMessageTime?: string
}

interface MessageRow {
	wxMpMessageCode: string
	wxMpMessageDirectionCode: string
	wxMpMessageContent?: string
	wxMpMessageReplySourceCode?: string
	createTime?: string
}

const mpConfigList = ref<MpConfigOption[]>([])
const wxMpConfigCode = ref('')
const conversations = ref<FanRow[]>([])
const messages = ref<MessageRow[]>([])
const activeFan = ref<FanRow | null>(null)
const convLoading = ref(false)
const msgLoading = ref(false)
const sending = ref(false)
const draftText = ref('')

const formatConfigLabel = (item: MpConfigOption) => {
	const name = item.wxMpConfigName || item.wxMpConfigCode
	return item.wxMpConfigAppId ? `${name} (${item.wxMpConfigAppId})` : name
}

const formatTime = (val?: string) => {
	if (!val) return ''
	return val.replace('T', ' ').slice(0, 16)
}

const loadMpConfigs = async () => {
	const { data } = await service.get('/mgt/wx/wxMpConfig/list', { params: { pageNo: 1, pageSize: 200 } })
	mpConfigList.value = (data?.records || []).filter((r: MpConfigOption & { wxMpConfigEnabledStatusCode?: string }) => r.wxMpConfigEnabledStatusCode !== '0')
	if (!wxMpConfigCode.value && mpConfigList.value.length > 0) {
		wxMpConfigCode.value = mpConfigList.value[0].wxMpConfigCode
	}
}

const loadConversations = async () => {
	if (!wxMpConfigCode.value) {
		conversations.value = []
		return
	}
	convLoading.value = true
	try {
		const { data } = await service.get('/mgt/wx/wxMpMessage/conversations', {
			params: { wxMpConfigCode: wxMpConfigCode.value }
		})
		conversations.value = data || []
	} finally {
		convLoading.value = false
	}
}

const loadMessages = async () => {
	if (!wxMpConfigCode.value || !activeFan.value) {
		messages.value = []
		return
	}
	msgLoading.value = true
	try {
		const { data } = await service.get('/mgt/wx/wxMpMessage/messages', {
			params: {
				wxMpConfigCode: wxMpConfigCode.value,
				openId: activeFan.value.wxMpFanOpenId
			}
		})
		messages.value = data || []
	} finally {
		msgLoading.value = false
	}
}

const selectFan = (fan: FanRow) => {
	activeFan.value = fan
	loadMessages()
}

const onConfigChange = () => {
	activeFan.value = null
	messages.value = []
	loadConversations()
}

const sendReply = async () => {
	if (!activeFan.value || !draftText.value.trim()) {
		ElMessage.warning('请输入回复内容')
		return
	}
	sending.value = true
	try {
		await service.post('/mgt/wx/wxMpMessage/reply', {
			wxMpConfigCode: wxMpConfigCode.value,
			wxMpFanOpenId: activeFan.value.wxMpFanOpenId,
			content: draftText.value.trim()
		})
		draftText.value = ''
		ElMessage.success('回复成功')
		await loadMessages()
		await loadConversations()
	} finally {
		sending.value = false
	}
}

onMounted(async () => {
	await loadMpConfigs()
	await loadConversations()
})
</script>

<style scoped>
.wx-mp-layout {
	display: flex;
	height: 100%;
}
.wx-mp-conv-panel {
	width: 320px;
	border-right: 1px solid #ebeef5;
	display: flex;
	flex-direction: column;
}
.wx-mp-conv-header {
	padding: 12px 16px;
	font-weight: 600;
	border-bottom: 1px solid #ebeef5;
}
.wx-mp-conv-scroll {
	flex: 1;
}
.wx-mp-conv-item {
	display: flex;
	gap: 10px;
	padding: 12px 16px;
	cursor: pointer;
	border-bottom: 1px solid #f2f6fc;
}
.wx-mp-conv-item.active,
.wx-mp-conv-item:hover {
	background: #f5f7fa;
}
.wx-mp-conv-body {
	flex: 1;
	min-width: 0;
}
.wx-mp-conv-row {
	display: flex;
	justify-content: space-between;
	gap: 8px;
}
.nick {
	font-weight: 500;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.time {
	color: #909399;
	font-size: 12px;
	flex-shrink: 0;
}
.preview {
	color: #909399;
	font-size: 13px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	margin-top: 4px;
}
.wx-mp-chat-panel {
	flex: 1;
	display: flex;
	flex-direction: column;
	min-width: 0;
}
.wx-mp-chat-header {
	padding: 12px 16px;
	border-bottom: 1px solid #ebeef5;
	display: flex;
	flex-direction: column;
	gap: 4px;
}
.openid {
	color: #909399;
	font-size: 12px;
}
.wx-mp-chat-messages {
	flex: 1;
	overflow-y: auto;
	padding: 16px;
	background: #fafafa;
}
.wx-mp-msg-row {
	display: flex;
	margin-bottom: 12px;
}
.wx-mp-msg-row.out {
	justify-content: flex-end;
}
.wx-mp-msg-bubble {
	max-width: 70%;
	background: #fff;
	border: 1px solid #ebeef5;
	border-radius: 8px;
	padding: 10px 12px;
}
.wx-mp-msg-row.out .wx-mp-msg-bubble {
	background: #ecf5ff;
	border-color: #d9ecff;
}
.content {
	white-space: pre-wrap;
	word-break: break-word;
}
.meta {
	margin-top: 6px;
	display: flex;
	align-items: center;
	gap: 8px;
	color: #909399;
	font-size: 12px;
}
.wx-mp-chat-input {
	padding: 12px 16px;
	border-top: 1px solid #ebeef5;
	display: flex;
	gap: 12px;
	align-items: flex-end;
}
.wx-mp-chat-input .el-textarea {
	flex: 1;
}
</style>
