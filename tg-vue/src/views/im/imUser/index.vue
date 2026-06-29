<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="keyword">
				<el-input v-model="state.queryForm.keyword" placeholder="昵称/姓名/用户编码" clearable style="width: 220px"></el-input>
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
		<el-space>
			<el-button v-auth="'imImUserSync'" icon="Connection" type="primary" @click="syncDialogVisible = true">同步用户</el-button>
			<el-button v-auth="'imImUserRefresh'" icon="Refresh" type="primary" plain :loading="refreshAllLoading" @click="refreshAllHandle">全部刷新</el-button>
			<el-button v-auth="'imImUserDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="imUserUserCode" label="用户编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="imUserNickName" label="昵称" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="imUserRealName" label="真实姓名" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column label="头像" width="72" align="center">
				<template #default="scope">
					<el-avatar v-if="scope.row.imUserAvatar" :src="scope.row.imUserAvatar" :size="40"></el-avatar>
					<span v-else>-</span>
				</template>
			</el-table-column>
			<el-table-column prop="imUserTag" label="标签" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="updateTime" label="更新时间" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" width="300" align="center">
				<template #default="scope">
					<el-button v-auth="'imImUserTag'" type="primary" link @click="tagHandle(scope.row)">设置标签</el-button>
					<el-button v-auth="'imImUserMsgRecord'" type="warning" link @click="msgRecordHandle(scope.row)">
						消息记录
						<span v-if="scope.row.imUserUnreadCount" class="msg-unread-count">
							{{ scope.row.imUserUnreadCount > 99 ? '99+' : scope.row.imUserUnreadCount }}
						</span>
					</el-button>
					<el-button v-if="scope.row.imUserSystemFriend" v-auth="'imImUserSendMsg'" type="success" link @click="sendMsgHandle(scope.row)">发消息</el-button>
					<el-button v-else v-auth="'imImUserAddFriend'" type="primary" link @click="addFriendHandle(scope.row)">添加好友</el-button>
					<el-button v-auth="'imImUserDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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
		<sync-dialog v-model="syncDialogVisible" @success="getDataList"></sync-dialog>
		<message-record-dialog
			v-model="msgRecordDialogVisible"
			:user-code="msgRecordUserCode"
			:peer-nick-name="msgRecordNickName"
		></message-record-dialog>

		<!-- 设置标签弹窗 -->
		<el-dialog v-model="tagDialogVisible" title="设置标签" width="400px">
			<el-form :model="tagForm">
				<el-form-item label="标签">
					<el-input v-model="tagForm.tag" placeholder="请输入标签" clearable></el-input>
				</el-form-item>
			</el-form>
			<template #footer>
				<el-button @click="tagDialogVisible = false">取消</el-button>
				<el-button type="primary" @click="tagConfirmHandle">确定</el-button>
			</template>
		</el-dialog>

		<!-- 发送消息弹窗 -->
		<el-dialog v-model="sendMsgDialogVisible" title="发送消息" width="500px">
			<el-form :model="sendMsgForm" label-width="80px">
				<el-form-item label="接收人">
					<span>{{ sendMsgForm.toUserNickName }}</span>
				</el-form-item>
				<el-form-item label="消息类型">
					<el-select v-model="sendMsgForm.typeCode" style="width: 100%">
						<el-option label="文本" value="text"></el-option>
						<el-option label="图文" value="rich"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item v-if="sendMsgForm.typeCode === 'text'" label="内容">
					<el-input v-model="sendMsgForm.content" type="textarea" :rows="4" placeholder="请输入消息内容"></el-input>
				</el-form-item>
				<template v-if="sendMsgForm.typeCode === 'rich'">
					<el-form-item label="标题">
						<el-input v-model="sendMsgForm.title" placeholder="请输入标题"></el-input>
					</el-form-item>
					<el-form-item label="内容">
						<el-input v-model="sendMsgForm.content" type="textarea" :rows="2" placeholder="请输入内容"></el-input>
					</el-form-item>
					<el-form-item label="图片">
						<tg-upload-image v-model:image-url="sendMsgForm.imageUrl" biz="im" width="160px" height="160px">
							<template #tip>限 1 张，JPG/PNG，单张不超过 5M</template>
						</tg-upload-image>
					</el-form-item>
					<el-form-item label="链接">
						<el-input v-model="sendMsgForm.linkUrl" placeholder="请输入跳转链接"></el-input>
					</el-form-item>
				</template>
			</el-form>
			<template #footer>
				<el-button @click="sendMsgDialogVisible = false">取消</el-button>
				<el-button type="primary" @click="sendMsgConfirmHandle">发送</el-button>
			</template>
		</el-dialog>
	</el-card>
</template>

<script setup lang="ts" name="ImImUserIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import { IHooksOptions } from '@/hooks/interface'
import SyncDialog from './sync-dialog.vue'
import MessageRecordDialog from './message-record-dialog.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/im/imUser/list',
	deleteUrl: '/mgt/im/imUser/delete',
	queryForm: {
		keyword: ''
	}
})

const queryRef = ref()
const syncDialogVisible = ref(false)
const refreshAllLoading = ref(false)

const refreshAllHandle = () => {
	ElMessageBox.confirm('确认从系统用户拉取资料并刷新全部 IM 用户？', '全部刷新', {
		type: 'warning'
	}).then(async () => {
		refreshAllLoading.value = true
		try {
			const res: any = await service.post('/mgt/im/imUser/refreshAll')
			ElMessage.success(res.message || '刷新成功')
			getDataList()
		} finally {
			refreshAllLoading.value = false
		}
	}).catch(() => {})
}

const msgRecordDialogVisible = ref(false)
const msgRecordUserCode = ref('')
const msgRecordNickName = ref('')

const msgRecordHandle = (row: any) => {
	msgRecordUserCode.value = row.imUserUserCode
	msgRecordNickName.value = row.imUserNickName || row.imUserUserCode
	msgRecordDialogVisible.value = true
}

// 标签弹窗
const tagDialogVisible = ref(false)
const tagForm = reactive({
	userCode: '',
	tag: ''
})
const currentRow = ref<any>(null)

const tagHandle = (row: any) => {
	currentRow.value = row
	tagForm.userCode = row.imUserUserCode
	tagForm.tag = row.imUserTag || ''
	tagDialogVisible.value = true
}

const tagConfirmHandle = async () => {
	const res: any = await service.post('/mgt/im/imUser/tag?userCode=' + encodeURIComponent(tagForm.userCode) + '&tag=' + encodeURIComponent(tagForm.tag))
	ElMessage.success(res.message || '设置成功')
	tagDialogVisible.value = false
	getDataList()
}

// 发送消息弹窗
const sendMsgDialogVisible = ref(false)
const sendMsgForm = reactive({
	toUserCode: '',
	toUserNickName: '',
	typeCode: 'text',
	content: '',
	title: '',
	imageUrl: '',
	linkUrl: ''
})

const sendMsgHandle = (row: any) => {
	if (!row.imUserSystemFriend) {
		ElMessage.warning('请先添加好友')
		return
	}
	sendMsgForm.toUserCode = row.imUserUserCode
	sendMsgForm.toUserNickName = row.imUserNickName || row.imUserUserCode
	sendMsgForm.typeCode = 'text'
	sendMsgForm.content = ''
	sendMsgForm.title = ''
	sendMsgForm.imageUrl = ''
	sendMsgForm.linkUrl = ''
	sendMsgDialogVisible.value = true
}

const addFriendHandle = async (row: any) => {
	const res: any = await service.post('/mgt/im/imUser/addFriend?userCode=' + encodeURIComponent(row.imUserUserCode))
	ElMessage.success(res.message || '添加好友成功')
	getDataList()
}

const sendMsgConfirmHandle = async () => {
	if (sendMsgForm.typeCode === 'text' && !sendMsgForm.content.trim()) {
		ElMessage.warning('请输入消息内容')
		return
	}
	if (sendMsgForm.typeCode === 'rich' && !sendMsgForm.title.trim()) {
		ElMessage.warning('请输入标题')
		return
	}
	if (sendMsgForm.typeCode === 'rich' && !sendMsgForm.imageUrl.trim()) {
		ElMessage.warning('请上传图片')
		return
	}
	const payload = {
		toUserCode: sendMsgForm.toUserCode,
		typeCode: sendMsgForm.typeCode,
		content: sendMsgForm.content,
		title: sendMsgForm.title,
		imageUrl: sendMsgForm.imageUrl,
		linkUrl: sendMsgForm.linkUrl
	}
	const res: any = await service.post('/mgt/im/message/send', payload)
	ElMessage.success(res.message || '发送成功')
	sendMsgDialogVisible.value = false
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>

<style scoped>
.text-muted {
	color: var(--el-text-color-secondary);
}

.msg-unread-count {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	min-width: 16px;
	height: 16px;
	margin-left: 4px;
	padding: 0 4px;
	border-radius: 8px;
	background: var(--el-color-danger);
	color: #fff;
	font-size: 11px;
	line-height: 1;
	vertical-align: text-top;
}
</style>
