<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="appConfigKey">
				<el-input v-model="state.queryForm.appConfigKey" placeholder="配置 key" clearable></el-input>
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
				<el-button icon="Plus" type="primary" @click="addOrEditHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="appConfigKey" label="配置 key" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="appConfigValue" label="配置值（JSON）" header-align="center" align="center" min-width="320" show-overflow-tooltip></el-table-column>
			<el-table-column prop="updateTime" label="更新时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="createTime" label="创建时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrEditHandle(scope.row.id)">修改</el-button>
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

		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="SysAppConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const queryRef = ref()
const addOrEditRef = ref()

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/system/sysAppConfig/list',
	deleteUrl: '/mgt/system/sysAppConfig/delete',
	queryForm: {
		appConfigKey: ''
	}
})

const { getDataList, sizeChangeHandle, currentChangeHandle, selectionChangeHandle, deleteBatchHandle, reset } = useCrud(state)

const addOrEditHandle = (id?: string) => {
	addOrEditRef.value.init(id)
}
</script>
