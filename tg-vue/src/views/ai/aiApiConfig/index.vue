<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="aiApiConfigName">
				<el-input v-model="state.queryForm.aiApiConfigName" placeholder="配置名称"></el-input>
			</el-form-item>
			<el-form-item prop="aiProviderCode">
				<el-input v-model="state.queryForm.aiProviderCode" placeholder="提供商编码"></el-input>
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
			<el-button icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="aiApiConfigCode" label="配置编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="aiApiConfigName" label="配置名称" min-width="120"></el-table-column>
			<el-table-column prop="aiProviderCode" label="提供商" width="120"></el-table-column>
			<el-table-column prop="aiApiConfigBaseUrl" label="Base URL" min-width="200" show-overflow-tooltip></el-table-column>
			<el-table-column prop="aiApiConfigDefaultModel" label="默认模型" min-width="120"></el-table-column>
			<el-table-column prop="aiApiConfigInputPricePer1k" label="输入单价/1K" width="110"></el-table-column>
			<el-table-column prop="aiApiConfigOutputPricePer1k" label="输出单价/1K" width="110"></el-table-column>
			<el-table-column prop="aiApiConfigEnabledCode" label="启用" width="80">
				<template #default="{ row }">{{ row.aiApiConfigEnabledCode === '1' ? '启用' : '停用' }}</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.aiApiConfigCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.aiApiConfigCode)">删除</el-button>
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
		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="AiAiApiConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/ai/aiApiConfig/list',
	deleteUrl: '/mgt/ai/aiApiConfig/delete',
	primaryKey: 'aiApiConfigCode',
	queryForm: {
		aiApiConfigName: '',
		aiProviderCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (code?: string) => addOrEditRef.value.init(code)

const { getDataList, deleteBatchHandle, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
