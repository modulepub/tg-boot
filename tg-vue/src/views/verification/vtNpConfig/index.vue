<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="npConfigCode">
				<el-input v-model="state.queryForm.npConfigCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item prop="npConfigProviderCode">
				<el-input v-model="state.queryForm.npConfigProviderCode" placeholder="渠道编码"></el-input>
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
				<el-button icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button icon="Refresh" type="warning" plain @click="refreshRuntimeHandle()">刷新运行时配置</el-button>
			</el-space>
			<el-space>
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="npConfigCode" label="配置编码" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="npConfigProviderCode" label="渠道" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="npConfigAccessKeyId" label="AccessKeyId" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="npConfigEndpoint" label="接入点" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="npConfigEnabledCode" label="启用" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ formatEnabledText(row.npConfigEnabledCode) }}
				</template>
			</el-table-column>
			<el-table-column prop="npConfigRemark" label="备注" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.npConfigCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.npConfigCode)">删除</el-button>
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

<script setup lang="ts" name="VerificationVtNpConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/verification/vtNpConfig/list',
	deleteUrl: '/mgt/verification/vtNpConfig/delete',
	primaryKey: 'npConfigCode',
	queryForm: {
		npConfigCode: '',
		npConfigProviderCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()

const addOrUpdateHandle = (npConfigCode?: string) => {
	addOrEditRef.value.init(npConfigCode)
}

const refreshRuntimeHandle = () => {
	service.post('/mgt/verification/vtNpConfig/refreshRuntime').then(() => {
		ElMessage.success('已刷新二要素核验运行时配置')
	})
}

const formatEnabledText = (val: unknown) => {
	const v = val === null || val === undefined ? '' : String(val).trim()
	if (v === '1') return '启用'
	if (v === '0') return '停用'
	return v === '' ? '—' : v
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
