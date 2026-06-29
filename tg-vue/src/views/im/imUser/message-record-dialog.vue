<template>
	<el-dialog
		v-model="visible"
		:title="dialogTitle"
		width="760px"
		:close-on-click-modal="false"
		destroy-on-close
		@closed="onClosed"
		@opened="onOpened"
	>
		<div v-loading="loading" class="chat-panel">
			<el-empty v-if="!loading && messages.length === 0" description="暂无消息记录" />
			<div v-else ref="scrollRef" class="chat-messages">
				<div v-for="item in messages" :key="item.messageCode" class="chat-row">
					<div class="chat-bubble" :class="{ 'chat-bubble--out': isOutgoing(item) }">
						<div class="chat-route">
							<span>{{ item.fromUserCode }}</span>
							<span class="chat-route-arrow">→</span>
							<span>{{ item.toUserCode }}</span>
						</div>
						<div v-if="item.typeCode === 'text'" class="chat-text">{{ item.content }}</div>
						<div v-else-if="item.typeCode === 'rich'" class="chat-rich">
							<div class="chat-rich-title">[图文] {{ item.title }}</div>
							<div v-if="item.content" class="chat-rich-desc">{{ item.content }}</div>
						</div>
						<div v-else class="chat-text">{{ item.content || item.typeCode }}</div>
						<div class="chat-meta">
							<el-tag v-if="item.readStatusCode === '1'" type="success" size="small">已读</el-tag>
							<el-tag v-else type="warning" size="small">未读</el-tag>
							<span>{{ item.createTime || '' }}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts" name="ImImUserMessageRecordDialog">
import { computed, nextTick, ref } from 'vue'
import service from '@/utils/request'

const props = defineProps<{
	modelValue: boolean
	userCode: string
	peerNickName: string
}>()

const emit = defineEmits<{
	'update:modelValue': [value: boolean]
}>()

const visible = computed({
	get: () => props.modelValue,
	set: (val: boolean) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => `消息记录 - ${props.peerNickName || props.userCode}`)
const loading = ref(false)
const messages = ref<any[]>([])
const scrollRef = ref<HTMLElement | null>(null)

const isOutgoing = (item: any) => item.fromUserCode === props.userCode

const scrollToBottom = () => {
	nextTick(() => {
		const el = scrollRef.value
		if (el) {
			el.scrollTop = el.scrollHeight
		}
	})
}

const loadMessages = async () => {
	if (!props.userCode) {
		return
	}
	loading.value = true
	try {
		const res: any = await service.get('/mgt/im/message/listByUser', {
			params: { userCode: props.userCode, pageNo: 1, pageSize: 200 }
		})
		messages.value = res.data || []
		scrollToBottom()
	} finally {
		loading.value = false
	}
}

const onOpened = async () => {
	await loadMessages()
}

const onClosed = () => {
	messages.value = []
}
</script>

<style scoped>
.chat-panel {
	min-height: 320px;
	max-height: 480px;
}

.chat-messages {
	max-height: 480px;
	overflow-y: auto;
	padding: 8px 4px;
}

.chat-row {
	display: flex;
	margin-bottom: 12px;
}

.chat-bubble {
	max-width: 92%;
	padding: 10px 12px;
	border-radius: 8px;
	background: var(--el-fill-color-light);
}

.chat-bubble--out {
	margin-left: auto;
	background: var(--el-color-primary-light-9);
}

.chat-route {
	display: flex;
	align-items: center;
	gap: 4px;
	font-size: 12px;
	color: var(--el-text-color-secondary);
	margin-bottom: 6px;
}

.chat-route-arrow {
	color: var(--el-text-color-placeholder);
}

.chat-text {
	white-space: pre-wrap;
	word-break: break-word;
}

.chat-rich-title {
	font-weight: 600;
	margin-bottom: 4px;
}

.chat-rich-desc {
	color: var(--el-text-color-secondary);
	font-size: 13px;
}

.chat-meta {
	margin-top: 6px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 12px;
	font-size: 12px;
	color: var(--el-text-color-secondary);
}
</style>
