<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="userCode">
				<el-input v-model="state.queryForm.userCode" placeholder="用户编码"></el-input>
			</el-form-item>
			<el-form-item prop="aiAgentCode">
				<el-input v-model="state.queryForm.aiAgentCode" placeholder="智能体编码"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button icon="Search" type="primary" @click="getDataList()">查询</el-button>
			</el-form-item>
			<el-form-item>
				<el-button icon="RefreshRight" @click="reset(queryRef)">重置</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
			<el-table-column prop="aiChatSessionCode" label="会话编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="aiChatSessionTitle" label="标题" min-width="200" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userCode" label="用户" width="120"></el-table-column>
			<el-table-column prop="aiAgentCode" label="智能体" min-width="120"></el-table-column>
			<el-table-column prop="createTime" label="创建时间" min-width="160"></el-table-column>
			<el-table-column label="操作" fixed="right" width="120">
				<template #default="scope">
					<el-button type="primary" link @click="viewMessages(scope.row.aiChatSessionCode)">查看对话</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		></el-pagination>

		<el-dialog v-model="messageVisible" title="对话内容" width="720px">
			<div v-loading="messageLoading" class="chat-messages">
				<div v-for="(msg, idx) in messages" :key="idx" class="chat-message" :class="'role-' + msg.aiChatMessageRoleCode">
					<div class="role-label">{{ roleLabel(msg.aiChatMessageRoleCode) }}</div>
					<div class="content">{{ msg.aiChatMessageContent }}</div>
				</div>
			</div>
		</el-dialog>
	</el-card>
</template>

<script setup lang="ts" name="AiAiChatSessionIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/ai/aiChatSession/list',
	queryForm: { userCode: '', aiAgentCode: '' }
})

const queryRef = ref()
const messageVisible = ref(false)
const messageLoading = ref(false)
const messages = ref<Array<{ aiChatMessageRoleCode: string; aiChatMessageContent: string }>>([])

const roleLabel = (role: string) => {
	if (role === 'user') return '用户'
	if (role === 'assistant') return '助手'
	if (role === 'system') return '系统'
	return role
}

const viewMessages = (sessionCode: string) => {
	messageVisible.value = true
	messageLoading.value = true
	messages.value = []
	service.get('/mgt/ai/aiChatSession/messages?aiChatSessionCode=' + encodeURIComponent(sessionCode)).then(res => {
		messages.value = res.data || []
		messageLoading.value = false
	})
}

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>

<style scoped>
.chat-messages {
	max-height: 480px;
	overflow-y: auto;
}
.chat-message {
	margin-bottom: 16px;
	padding: 12px;
	border-radius: 8px;
	background: #f5f7fa;
}
.chat-message.role-user {
	background: #ecf5ff;
}
.chat-message.role-assistant {
	background: #f0f9eb;
}
.role-label {
	font-weight: bold;
	margin-bottom: 6px;
	font-size: 13px;
	color: #606266;
}
.content {
	white-space: pre-wrap;
	word-break: break-word;
	line-height: 1.6;
}
</style>
