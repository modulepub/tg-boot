<template>
	<div class="im-message-center">
		<el-card class="im-center-card" :body-style="{ padding: 0, height: '100%' }">
			<div class="im-center-layout">
				<!-- 左侧会话列表 -->
				<aside class="im-conv-panel">
					<div class="im-conv-header">
						<span class="im-conv-title">消息列表</span>
						<el-badge v-if="unreadTotal > 0" :value="unreadTotal > 99 ? '99+' : unreadTotal" class="im-unread-badge" />
						<el-button link type="primary" :loading="convLoading" @click="syncConversations">刷新</el-button>
					</div>
					<el-scrollbar class="im-conv-scroll">
						<div
							v-for="item in conversations"
							:key="item.conversationCode"
							class="im-conv-item"
							:class="{ 'im-conv-item--active': activeConv?.conversationCode === item.conversationCode }"
							@click="selectConversation(item)"
						>
							<el-avatar :size="44" :src="item.avatar || undefined">{{ item.nick?.slice(0, 1) || '?' }}</el-avatar>
							<div class="im-conv-body">
								<div class="im-conv-row">
									<span class="im-conv-nick">{{ item.nick }}</span>
									<span class="im-conv-time">{{ formatConvTime(item.lastMessageTime) }}</span>
								</div>
								<div class="im-conv-row">
									<span class="im-conv-preview">{{ item.lastMessage || ' ' }}</span>
									<el-badge v-if="item.unreadCount > 0" :value="item.unreadCount > 99 ? '99+' : item.unreadCount" />
								</div>
							</div>
						</div>
						<el-empty v-if="!convLoading && conversations.length === 0" description="暂无会话" />
					</el-scrollbar>
				</aside>

				<!-- 右侧聊天区 -->
				<section class="im-chat-panel">
					<template v-if="activeConv">
						<div class="im-chat-header">
							<el-avatar :size="36" :src="activeConv.avatar || undefined">{{ activeConv.nick?.slice(0, 1) }}</el-avatar>
							<span class="im-chat-peer">{{ activeConv.nick }}（{{ activeConv.userID }}）</span>
							<el-tag v-if="wsConnected" type="success" size="small">已连接</el-tag>
							<el-tag v-else type="info" size="small">连接中…</el-tag>
						</div>

						<div ref="msgScrollRef" v-loading="msgLoading" class="im-chat-messages">
							<div v-for="item in chatMessages" :key="item.id" class="im-msg-row" :class="{ 'im-msg-row--out': item.isSelf }">
								<div class="im-msg-bubble">
									<template v-if="item.type === 'text'">
										<div class="im-msg-text">{{ item.text }}</div>
									</template>
									<template v-else-if="item.type === 'image'">
										<el-image
											class="im-msg-image"
											:src="FileUtil.getFullPath(item.imageUrl || '')"
											:preview-src-list="[FileUtil.getFullPath(item.imageUrl || '')]"
											fit="cover"
										/>
									</template>
									<template v-else-if="item.type === 'rich'">
										<div class="im-msg-rich">
											<div class="im-msg-rich-title">{{ item.richTitle }}</div>
											<div v-if="item.richText" class="im-msg-rich-desc">{{ item.richText }}</div>
											<el-image v-if="item.richImageUrl" class="im-msg-rich-img" :src="FileUtil.getFullPath(item.richImageUrl)" fit="cover" />
										</div>
									</template>
									<template v-else-if="item.type === 'wx_exchange_request'">
										<div class="im-wx-card">
											<div class="im-wx-title">交换微信</div>
											<div class="im-wx-desc">
												{{
													item.isSelf
														? '已向对方发起交换微信，等待对方回复。'
														: `${item.wxRequestFromNick || '对方'}想与您交换微信号，您是否同意？`
												}}
											</div>
											<div v-if="!item.isSelf" class="im-wx-actions">
												<el-button type="primary" size="small" @click="onAgreeWxExchange(item)">同意</el-button>
												<el-button size="small" @click="onDeclineWxExchange">忽略</el-button>
											</div>
										</div>
									</template>
									<template v-else-if="item.type === 'wx_exchange_accept'">
										<div class="im-wx-card">
											<div class="im-wx-title">交换微信</div>
											<div class="im-wx-desc">已同意交换微信，微信号如下：</div>
											<div v-if="item.wxExchangePeerWx" class="im-wx-id">
												<span>{{ item.wxExchangePeerWx }}</span>
												<el-button link type="primary" @click="copyWxId(item.wxExchangePeerWx)">复制</el-button>
											</div>
										</div>
									</template>
									<template v-else>
										<div class="im-msg-text">{{ item.text }}</div>
									</template>
									<div class="im-msg-time">{{ formatMsgTime(item.time) }}</div>
								</div>
							</div>
							<el-empty v-if="!msgLoading && chatMessages.length === 0" description="暂无消息，发送一条开始聊天吧" />
						</div>

						<div class="im-chat-toolbar">
							<el-upload :show-file-list="false" :http-request="handleImageUpload" accept="image/*">
								<el-button link type="primary">图片</el-button>
							</el-upload>
							<el-button link type="primary" @click="openWxExchangeForSend">交换微信</el-button>
						</div>
						<div class="im-chat-input">
							<el-input
								v-model="draftText"
								type="textarea"
								:rows="3"
								placeholder="输入消息…"
								@keydown.enter.exact.prevent="onSendText"
							/>
							<el-button type="primary" :loading="sending" @click="onSendText">发送</el-button>
						</div>
					</template>
					<el-empty v-else class="im-chat-empty" description="请从左侧选择会话" />
				</section>
			</div>
		</el-card>

		<!-- 微信号输入 -->
		<el-dialog v-model="wxDialogVisible" :title="wxDialogMode === 'accept' ? '同意交换微信' : '发起交换微信'" width="420px">
			<el-form label-width="80px">
				<el-form-item label="微信号">
					<el-input v-model="wxDraft" placeholder="请输入微信号" clearable />
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="wxDialogVisible = false">取消</el-button>
				<el-button type="primary" :loading="wxSending" @click="confirmWxDialog">确定</el-button>
			</template>
		</el-dialog>
	</div>
</template>

<script setup lang="ts" name="ImMessageCenterIndex">
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import {
	MGT_SYSTEM_USER_CODE,
	type IImConversation,
	type IImWebSocketMessage,
	listMgtImConversations,
	listMgtImMessages,
	markMgtImMessagesRead
} from '@/api/im'
import FileUtil from '@/utils/FileUtil'
import service from '@/utils/request'
import { useUserStore } from '@/store/modules/user'
import { ImStompClient } from '@/utils/im/im-stomp-client'
import {
	formatLastMessagePreview,
	isContactRemovedWsMessage,
	isWxExchangeAcceptRaw,
	parseDateTimeToSeconds,
	sendCustomJsonMessage,
	sendTextMessage,
	toRawChatMessage,
	tryParseCustomJson,
	type IRawChatMessage
} from '@/utils/im/im-message'

type IConversationView = {
	conversationCode: string
	userID: string
	avatar: string
	nick: string
	lastMessage: string
	lastMessageTime: number
	unreadCount: number
}

type IChatMessageView = {
	id: string
	isSelf: boolean
	type: string
	text: string
	imageUrl?: string
	richTitle?: string
	richText?: string
	richImageUrl?: string
	wxRequestId?: string
	wxRequestFromNick?: string
	wxExchangePeerWx?: string
	time: number
	messageCode: string
	raw: IRawChatMessage
}

const SYSTEM_USER_CODE = MGT_SYSTEM_USER_CODE
const WX_PENDING_PREFIX = 'mgt_wx_pending_'
const WX_REPLIED_PREFIX = 'mgt_wx_replied_'
const SYSTEM_WX_STORAGE_KEY = 'mgt_system_wx_id'

const userStore = useUserStore()
const stompClient = new ImStompClient()

const convLoading = ref(false)
const msgLoading = ref(false)
const sending = ref(false)
const wsConnected = ref(false)
const conversations = ref<IConversationView[]>([])
const activeConv = ref<IConversationView | null>(null)
const chatMessages = ref<IChatMessageView[]>([])
const rawMessageList = ref<IRawChatMessage[]>([])
const draftText = ref('')
const conversationCode = ref('')
const msgScrollRef = ref<HTMLElement | null>(null)

const wxDialogVisible = ref(false)
const wxDialogMode = ref<'send_request' | 'accept'>('send_request')
const wxDraft = ref('')
const wxSending = ref(false)
const pendingAcceptRequestId = ref('')

const unreadTotal = computed(() => conversations.value.reduce((sum, c) => sum + Number(c.unreadCount || 0), 0))

function mapConversation(row: IImConversation): IConversationView {
	const code = String(row.conversationCode || '').trim()
	const peer = String(row.peerUserCode || '').trim()
	return {
		conversationCode: code,
		userID: peer,
		avatar: String(row.peerAvatar || '').trim(),
		nick: String(row.peerNickName || peer).trim(),
		lastMessage: formatLastMessagePreview(row.lastMessageContent),
		lastMessageTime: parseDateTimeToSeconds(row.lastMessageTime),
		unreadCount: Number(row.unreadCount || 0)
	}
}

function toChatView(raw: IRawChatMessage): IChatMessageView {
	return {
		id: raw.id,
		isSelf: raw.flow === 'out',
		type: raw.type,
		text: raw.text,
		imageUrl: raw.imageUrl,
		richTitle: raw.richTitle,
		richText: raw.richText,
		richImageUrl: raw.richImageUrl,
		wxRequestId: raw.wxRequestId,
		wxRequestFromNick: raw.wxRequestFromNick,
		wxExchangePeerWx: raw.wxExchangePeerWx,
		time: raw.time,
		messageCode: raw.messageCode,
		raw
	}
}

function formatConvTime(sec: number) {
	if (!sec) return ''
	const d = new Date(sec * 1000)
	const now = new Date()
	if (d.toDateString() === now.toDateString()) {
		return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
	}
	return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

function formatMsgTime(sec: number) {
	if (!sec) return ''
	return new Date(sec * 1000).toLocaleString('zh-CN', {
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit'
	})
}

function scrollToBottom() {
	nextTick(() => {
		const el = msgScrollRef.value
		if (el) el.scrollTop = el.scrollHeight
	})
}

async function syncConversations() {
	convLoading.value = true
	try {
		const res: any = await listMgtImConversations()
		conversations.value = (res.data || []).map(mapConversation)
	} catch (e) {
		console.error(e)
	} finally {
		convLoading.value = false
	}
}

async function loadMessages() {
	if (!conversationCode.value) return
	msgLoading.value = true
	try {
		const res: any = await listMgtImMessages(conversationCode.value, 1, 100)
		const list = (res.data || []) as any[]
		rawMessageList.value = list
			.map((m) => toRawChatMessage(m, SYSTEM_USER_CODE))
			.sort((a, b) => a.time - b.time)
		chatMessages.value = rawMessageList.value.map(toChatView)
		await markConversationRead(rawMessageList.value)
		await maybeReplyRequesterWxOnAccept(
			rawMessageList.value.filter((m) => m.flow === 'in'),
			rawMessageList.value
		)
		scrollToBottom()
	} catch (e) {
		console.error(e)
	} finally {
		msgLoading.value = false
	}
}

async function selectConversation(item: IConversationView) {
	activeConv.value = item
	conversationCode.value = item.conversationCode
	await loadMessages()
}

async function markConversationRead(rawMessages: IRawChatMessage[]) {
	const unreadCodes = rawMessages
		.filter((m) => m.flow === 'in' && m.toUserCode === SYSTEM_USER_CODE && m.messageCode)
		.map((m) => m.messageCode)
	if (!unreadCodes.length) return
	try {
		await markMgtImMessagesRead({
			messageCodes: unreadCodes,
			fromUserCode: activeConv.value?.userID,
			conversationCode: conversationCode.value
		})
		await syncConversations()
	} catch (e) {
		console.error(e)
	}
}

function captureConversationCode(result: { conversationCode?: string }) {
	const code = String(result?.conversationCode || '').trim()
	if (code) conversationCode.value = code
}

async function onSendText() {
	const text = draftText.value.trim()
	const peer = activeConv.value?.userID
	if (!text || !peer) return
	sending.value = true
	try {
		const res = await sendTextMessage(peer, text)
		captureConversationCode(res.data || {})
		draftText.value = ''
		await loadMessages()
		await syncConversations()
	} catch (e: any) {
		ElMessage.error(e?.message || '发送失败，请先添加好友')
	} finally {
		sending.value = false
	}
}

async function handleImageUpload(options: UploadRequestOptions) {
	const peer = activeConv.value?.userID
	if (!peer) return
	const formData = new FormData()
	formData.append('file', options.file)
	formData.append('biz', 'chat')
	try {
		const { data }: any = await service.postForm('/file/upload', formData)
		const imageUrl = String(data?.fullFilePath || '').trim()
		if (!imageUrl) throw new Error('未获取到图片地址')
		const res = await sendCustomJsonMessage(peer, { type: 'chat_image', imageUrl })
		captureConversationCode(res.data || {})
		await loadMessages()
		await syncConversations()
	} catch (e: any) {
		ElMessage.error(e?.message || '图片发送失败')
		options.onError(e)
	}
}

function getSystemWxId() {
	return localStorage.getItem(SYSTEM_WX_STORAGE_KEY) || ''
}

function saveSystemWxId(wx: string) {
	localStorage.setItem(SYSTEM_WX_STORAGE_KEY, wx)
}

function storePendingRequestWx(requestId: string, wx: string) {
	localStorage.setItem(`${WX_PENDING_PREFIX}${requestId}`, wx)
}

function hasOutgoingWxRequest(list: IRawChatMessage[], requestId: string) {
	return list.some((m) => {
		if (m.flow !== 'out' || m.type !== 'wx_exchange_request') return false
		return m.wxRequestId === requestId
	})
}

function hasRepliedRequestWx(requestId: string) {
	return localStorage.getItem(`${WX_REPLIED_PREFIX}${requestId}`) === '1'
}

function markRequestWxReplied(requestId: string) {
	localStorage.setItem(`${WX_REPLIED_PREFIX}${requestId}`, '1')
}

function resolveSelfRequesterWx(requestId: string) {
	return localStorage.getItem(`${WX_PENDING_PREFIX}${requestId}`) || getSystemWxId()
}

function makeWxRequestId() {
	return `${Date.now()}_${Math.random().toString(36).slice(2, 10)}`
}

function openWxExchangeForSend() {
	if (!activeConv.value) {
		ElMessage.warning('请先选择会话')
		return
	}
	wxDialogMode.value = 'send_request'
	wxDraft.value = getSystemWxId()
	pendingAcceptRequestId.value = ''
	wxDialogVisible.value = true
}

function onAgreeWxExchange(item: IChatMessageView) {
	if (!item.wxRequestId) return
	wxDialogMode.value = 'accept'
	wxDraft.value = getSystemWxId()
	pendingAcceptRequestId.value = item.wxRequestId
	wxDialogVisible.value = true
}

function onDeclineWxExchange() {
	ElMessage.info('已忽略')
}

async function sendWxExchangeRequest(requesterWxId: string) {
	const peer = activeConv.value?.userID
	if (!peer) return
	const wx = requesterWxId.trim()
	if (!wx) {
		ElMessage.warning('微信号不能为空')
		return
	}
	const requestId = makeWxRequestId()
	storePendingRequestWx(requestId, wx)
	saveSystemWxId(wx)
	const payload = {
		type: 'wx_exchange_request',
		requestId,
		fromNick: '系统客服'
	}
	const res = await sendCustomJsonMessage(peer, payload)
	captureConversationCode(res.data || {})
}

async function sendWxAcceptMessage(requestId: string, accepterWxId: string) {
	const peer = activeConv.value?.userID
	if (!peer) return
	const wx = accepterWxId.trim()
	if (!wx) {
		ElMessage.warning('微信号不能为空')
		return
	}
	saveSystemWxId(wx)
	const payload = {
		type: 'wx_exchange_accept',
		requestId,
		accepterWxId: wx,
		fromNick: '系统客服'
	}
	const res = await sendCustomJsonMessage(peer, payload)
	captureConversationCode(res.data || {})
	markRequestWxReplied(requestId)
}

async function confirmWxDialog() {
	const v = wxDraft.value.trim()
	if (!v) {
		ElMessage.warning('微信号不能为空')
		return
	}
	wxSending.value = true
	try {
		if (wxDialogMode.value === 'send_request') {
			await sendWxExchangeRequest(v)
		} else if (pendingAcceptRequestId.value) {
			await sendWxAcceptMessage(pendingAcceptRequestId.value, v)
		}
		wxDialogVisible.value = false
		await loadMessages()
		await syncConversations()
	} catch (e: any) {
		ElMessage.error(e?.message || '操作失败')
	} finally {
		wxSending.value = false
	}
}

async function maybeReplyRequesterWxOnAccept(incoming: IRawChatMessage[], all: IRawChatMessage[]) {
	let sent = false
	for (const msg of incoming) {
		if (!isWxExchangeAcceptRaw(msg)) continue
		const requestId = msg.wxRequestId || ''
		if (!requestId || !hasOutgoingWxRequest(all, requestId) || hasRepliedRequestWx(requestId)) continue
		const wx = resolveSelfRequesterWx(requestId)
		if (!wx) continue
		await sendWxAcceptMessage(requestId, wx)
		sent = true
	}
	if (sent) await loadMessages()
}

function copyWxId(wx: string) {
	const t = String(wx || '').trim()
	if (!t) return
	navigator.clipboard.writeText(t).then(() => ElMessage.success('已复制'))
}

function belongsToActiveConv(msg: IImWebSocketMessage) {
	const code = String(msg.conversationCode || '').trim()
	const peer = activeConv.value?.userID
	if (code && conversationCode.value && code === conversationCode.value) return true
	if (!peer) return false
	const from = String(msg.fromUserCode || '').trim()
	const to = String(msg.toUserCode || '').trim()
	return (from === peer && to === SYSTEM_USER_CODE) || (from === SYSTEM_USER_CODE && to === peer)
}

function handleWsMessage(body: string) {
	let msg: IImWebSocketMessage
	try {
		msg = JSON.parse(body) as IImWebSocketMessage
	} catch {
		return
	}
	if (msg.type === 'unread_count') {
		void syncConversations()
		return
	}
	if (isContactRemovedWsMessage(msg)) {
		void syncConversations()
		return
	}
	if (!belongsToActiveConv(msg)) {
		void syncConversations()
		return
	}
	const raw = toRawChatMessage(msg, SYSTEM_USER_CODE)
	if (msg.type?.startsWith('self_')) {
		// 自己发出的回显，刷新列表即可
		void loadMessages()
		void syncConversations()
		return
	}
	const last = chatMessages.value[chatMessages.value.length - 1]
	if (!last || raw.time >= last.time) {
		chatMessages.value.push(toChatView(raw))
		rawMessageList.value.push(raw)
	} else {
		rawMessageList.value.push(raw)
		rawMessageList.value.sort((a, b) => a.time - b.time)
		chatMessages.value = rawMessageList.value.map(toChatView)
	}
	scrollToBottom()
	void markConversationRead([raw])
	if (isWxExchangeAcceptRaw(raw)) {
		void loadMessages()
	}
	void syncConversations()
}

async function connectWs() {
	const token = userStore.token
	if (!token) return
	stompClient.onDisconnect(() => {
		wsConnected.value = false
	})
	try {
		await stompClient.connect(token)
		wsConnected.value = true
		stompClient.subscribe('/user/queue/messages', handleWsMessage)
		stompClient.subscribe('/user/queue/notifications', handleWsMessage)
	} catch (e) {
		console.error('[IM] WebSocket 连接失败', e)
		wsConnected.value = false
	}
}

onMounted(async () => {
	await syncConversations()
	await connectWs()
})

onUnmounted(() => {
	void stompClient.disconnect()
})
</script>

<style scoped>
.im-message-center {
	height: calc(100vh - 120px);
	min-height: 560px;
}

.im-center-card {
	height: 100%;
}

.im-center-layout {
	display: flex;
	height: 100%;
	min-height: 520px;
}

.im-conv-panel {
	width: 300px;
	flex-shrink: 0;
	border-right: 1px solid var(--el-border-color-lighter);
	display: flex;
	flex-direction: column;
}

.im-conv-header {
	display: flex;
	align-items: center;
	gap: 8px;
	padding: 12px 14px;
	border-bottom: 1px solid var(--el-border-color-lighter);
}

.im-conv-title {
	font-weight: 600;
	flex: 1;
}

.im-conv-scroll {
	flex: 1;
}

.im-conv-item {
	display: flex;
	gap: 10px;
	padding: 12px 14px;
	cursor: pointer;
	border-bottom: 1px solid var(--el-border-color-extra-light);
}

.im-conv-item:hover,
.im-conv-item--active {
	background: var(--el-fill-color-light);
}

.im-conv-body {
	flex: 1;
	min-width: 0;
}

.im-conv-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 6px;
}

.im-conv-nick {
	font-size: 14px;
	font-weight: 500;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.im-conv-time {
	font-size: 12px;
	color: var(--el-text-color-secondary);
	flex-shrink: 0;
}

.im-conv-preview {
	font-size: 12px;
	color: var(--el-text-color-secondary);
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	flex: 1;
}

.im-chat-panel {
	flex: 1;
	display: flex;
	flex-direction: column;
	min-width: 0;
}

.im-chat-header {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 12px 16px;
	border-bottom: 1px solid var(--el-border-color-lighter);
}

.im-chat-peer {
	font-weight: 600;
	flex: 1;
}

.im-chat-messages {
	flex: 1;
	overflow-y: auto;
	padding: 16px;
	background: var(--el-fill-color-blank);
}

.im-chat-empty {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
}

.im-msg-row {
	display: flex;
	margin-bottom: 14px;
}

.im-msg-row--out {
	justify-content: flex-end;
}

.im-msg-bubble {
	max-width: 72%;
	padding: 10px 12px;
	border-radius: 10px;
	background: var(--el-fill-color-light);
}

.im-msg-row--out .im-msg-bubble {
	background: var(--el-color-primary-light-8);
}

.im-msg-text {
	white-space: pre-wrap;
	word-break: break-word;
	line-height: 1.5;
}

.im-msg-image {
	max-width: 200px;
	max-height: 200px;
	border-radius: 6px;
}

.im-msg-rich-title {
	font-weight: 600;
	margin-bottom: 4px;
}

.im-msg-rich-desc {
	font-size: 13px;
	color: var(--el-text-color-secondary);
}

.im-msg-rich-img {
	max-width: 180px;
	margin-top: 6px;
	border-radius: 6px;
}

.im-msg-time {
	font-size: 11px;
	color: var(--el-text-color-placeholder);
	margin-top: 6px;
}

.im-wx-card {
	min-width: 220px;
}

.im-wx-title {
	font-weight: 600;
	margin-bottom: 6px;
}

.im-wx-desc {
	font-size: 13px;
	line-height: 1.5;
}

.im-wx-actions {
	margin-top: 10px;
	display: flex;
	gap: 8px;
}

.im-wx-id {
	margin-top: 8px;
	display: flex;
	align-items: center;
	gap: 8px;
	font-weight: 500;
}

.im-chat-toolbar {
	display: flex;
	gap: 12px;
	padding: 8px 16px 0;
	border-top: 1px solid var(--el-border-color-extra-light);
}

.im-chat-input {
	display: flex;
	gap: 10px;
	padding: 10px 16px 14px;
	align-items: flex-end;
}

.im-chat-input .el-textarea {
	flex: 1;
}
</style>
