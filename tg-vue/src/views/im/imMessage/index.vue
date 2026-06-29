<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="conversationCode">
				<el-input v-model="state.queryForm.conversationCode" placeholder="会话编码" clearable style="width: 220px"></el-input>
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
			<el-table-column prop="messageCode" label="消息编码" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="conversationCode" label="会话编码" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="fromUserCode" label="发送方" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="toUserCode" label="接收方" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="typeCode" label="类型" width="80" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.typeCode === 'text'" type="primary">文本</el-tag>
					<el-tag v-else-if="scope.row.typeCode === 'rich'" type="success">图文</el-tag>
					<el-tag v-else>{{ scope.row.typeCode }}</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="内容" min-width="200" show-overflow-tooltip>
				<template #default="scope">
					<span v-if="scope.row.typeCode === 'text'">{{ scope.row.content }}</span>
					<span v-else-if="scope.row.typeCode === 'rich'">[图文] {{ scope.row.title }}</span>
					<span v-else>-</span>
				</template>
			</el-table-column>
			<el-table-column prop="readStatusCode" label="已读" width="80" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.readStatusCode === '1'" type="success">已读</el-tag>
					<el-tag v-else type="warning">未读</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="sendStatusCode" label="状态" width="80" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.sendStatusCode === '1'" type="success">成功</el-tag>
					<el-tag v-else type="danger">失败</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="创建时间" min-width="160" show-overflow-tooltip></el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		></el-pagination>
	</el-card>
</template>

<script setup lang="ts" name="ImImMessageIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/im/message/list',
	queryForm: {
		conversationCode: ''
	}
})

const queryRef = ref()

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
