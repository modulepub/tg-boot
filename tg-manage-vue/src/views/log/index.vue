<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="logCode">
				<el-input v-model="state.queryForm.logCode" placeholder="日志编码"></el-input>
			</el-form-item>
			<el-form-item prop="logName">
				<el-input v-model="state.queryForm.logName" placeholder="日志名称"></el-input>
			</el-form-item>
			<el-form-item prop="logMethodName">
				<el-input v-model="state.queryForm.logMethodName" placeholder="方法名"></el-input>
			</el-form-item>
			<el-form-item prop="logContent">
				<el-input v-model="state.queryForm.logContent" placeholder="日志内容"></el-input>
			</el-form-item>
			<el-form-item prop="logClientIp">
				<el-input v-model="state.queryForm.logClientIp" placeholder="客户端IP"></el-input>
			</el-form-item>
			<el-form-item prop="logTransactionCode">
				<el-input v-model="state.queryForm.logTransactionCode" placeholder="事务编码"></el-input>
			</el-form-item>
			<el-form-item prop="logUserName">
				<el-input v-model="state.queryForm.logUserName" placeholder="用户名"></el-input>
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
			<el-space>
				<el-button v-auth="'log:log:save'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button v-auth="'log:log:delete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="logCode" label="日志编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logName" label="日志名称" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logMethodName" label="方法名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logContent" label="日志内容" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logClientIp" label="客户端IP" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logTransactionCode" label="事务编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="logUserName" label="用户名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="createTime" label="创建日期" header-align="center" align="center" show-overflow-tooltip></el-table-column>

			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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

		<!-- 弹窗, 新增 / 修改 -->
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="${ModuleName}logIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/log/log/list',
	deleteUrl: '/mgt/log/log/delete',
	queryForm: {
		logCode: '',
		logName: '',
		logMethodName: '',
		logContent: '',
		logDescription: '',
		logClientIp: '',
		logTransactionCode: '',
		logUserCode: '',
		logUserName: '',
		deleted: '',
		version: '',
		seqNo: '',
		orgCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
