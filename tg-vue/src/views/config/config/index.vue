<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="configCode">
				<el-input v-model="state.queryForm.configCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item prop="configName">
				<el-input v-model="state.queryForm.configName" placeholder="配置名称"></el-input>
			</el-form-item>
			<el-form-item prop="configTypeCode">
				<tg-dict-select v-model="state.queryForm.configTypeCode" dict-code="configTypeCode" clearable placeholder="配置类型"></tg-dict-select>
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
				<el-button v-auth="'configConfigAdd'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button v-auth="'configConfigDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="configCode" label="配置编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="configName" label="配置名称" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<tg-dict-column prop="configTypeCode" label="配置类型" dict-code="configTypeCode" show-overflow-tooltip></tg-dict-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button v-auth="'configConfigEdit'" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
					<el-button v-auth="'configConfigDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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
		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="ConfigconfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/config/config/list',
	deleteUrl: '/mgt/config/config/delete',
	queryForm: {
		orgCode: '',
		deleted: '',
		seqNo: '',
		version: '',
		configCode: '',
		configName: '',
		configTypeCode: '',
		configEnableStatusCode: '',
		configContent: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>