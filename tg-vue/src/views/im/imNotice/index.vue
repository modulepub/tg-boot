<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="imNoticeName">
				<el-input v-model="state.queryForm.imNoticeName" placeholder="通知标题" clearable></el-input>
			</el-form-item>
			<el-form-item prop="imNoticePublishStateCode">
				<el-select v-model="state.queryForm.imNoticePublishStateCode" placeholder="状态" clearable style="width: 120px">
					<el-option label="草稿" value="0"></el-option>
					<el-option label="已发送" value="1"></el-option>
				</el-select>
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
			<el-button v-auth="'imImNoticeAdd'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增通知</el-button>
			<el-button v-auth="'imImNoticeDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="imNoticeName" label="标题" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="imNoticeSenderUserCode" label="发送人" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column label="封面" width="90" align="center">
				<template #default="scope">
					<el-image v-if="scope.row.imNoticeImg" :src="scope.row.imNoticeImg" style="width: 48px; height: 48px" fit="cover" preview-teleported :preview-src-list="[scope.row.imNoticeImg]"></el-image>
				</template>
			</el-table-column>
			<el-table-column prop="imNoticePublishStateCode" label="状态" width="90" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.imNoticePublishStateCode === '1' || scope.row.imNoticePublishStateCode?.code === '1'" type="success">已发送</el-tag>
					<el-tag v-else type="info">草稿</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="imNoticeSendCount" label="成功" width="70" align="center"></el-table-column>
			<el-table-column prop="imNoticeFailCount" label="失败" width="70" align="center"></el-table-column>
			<el-table-column prop="createTime" label="创建时间" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" width="220" align="center">
				<template #default="scope">
					<el-button
						v-if="scope.row.imNoticePublishStateCode === '0' || scope.row.imNoticePublishStateCode?.code === '0' || !scope.row.imNoticePublishStateCode"
						v-auth="'imImNoticePublish'"
						type="success"
						link
						:loading="publishLoading && publishingId === scope.row.id"
						@click="publishHandle(scope.row)"
					>
						全员发送
					</el-button>
					<el-button
						v-if="scope.row.imNoticePublishStateCode === '0' || scope.row.imNoticePublishStateCode?.code === '0' || !scope.row.imNoticePublishStateCode"
						v-auth="'imImNoticeModify'"
						type="primary"
						link
						@click="addOrUpdateHandle(scope.row.id)"
					>
						编辑
					</el-button>
					<el-button v-auth="'imImNoticeDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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
		>
		</el-pagination>
		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="ImImNoticeIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/im/imNotice/list',
	deleteUrl: '/mgt/im/imNotice/delete',
	queryForm: {
		imNoticeName: '',
		imNoticePublishStateCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: string) => {
	addOrEditRef.value.init(id)
}

const publishLoading = ref(false)
const publishingId = ref('')

const publishHandle = (row: any) => {
	if (!row?.id) {
		ElMessage.warning('通知记录无效，请刷新后重试')
		return
	}
	ElMessageBox.confirm(`确认向全部 IM 用户发送通知「${row.imNoticeName}」？`, '全员发送', {
		type: 'warning'
	})
		.then(async () => {
			publishingId.value = row.id
			publishLoading.value = true
			try {
				const res: any = await service.post('/mgt/im/imNotice/publish?id=' + encodeURIComponent(row.id), null, {
					timeout: 300000
				})
				ElMessage.success(res.message || '发送完成')
				getDataList()
			} catch {
				// 错误信息由 request 拦截器统一提示
			} finally {
				publishLoading.value = false
				publishingId.value = ''
			}
		})
		.catch(() => {})
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
