<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="aiAgentName">
				<el-input v-model="state.queryForm.aiAgentName" placeholder="智能体名称"></el-input>
			</el-form-item>
			<el-form-item prop="aiApiConfigCode">
				<el-input v-model="state.queryForm.aiApiConfigCode" placeholder="接口配置编码"></el-input>
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
			<el-table-column type="selection" width="50"></el-table-column>
			<el-table-column prop="aiAgentCode" label="智能体编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="aiAgentName" label="名称" min-width="120"></el-table-column>
			<el-table-column prop="aiApiConfigCode" label="接口配置" min-width="140"></el-table-column>
			<el-table-column prop="aiAgentModel" label="模型" min-width="120"></el-table-column>
			<el-table-column prop="aiAgentPersona" label="人设" min-width="200" show-overflow-tooltip></el-table-column>
			<el-table-column prop="aiAgentEnabledCode" label="启用" width="80">
				<template #default="{ row }">{{ row.aiAgentEnabledCode === '1' ? '启用' : '停用' }}</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.aiAgentCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.aiAgentCode)">删除</el-button>
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

<script setup lang="ts" name="AiAiAgentIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/ai/aiAgent/list',
	deleteUrl: '/mgt/ai/aiAgent/delete',
	primaryKey: 'aiAgentCode',
	queryForm: { aiAgentName: '', aiApiConfigCode: '' }
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (code?: string) => addOrEditRef.value.init(code)

const { getDataList, deleteBatchHandle, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
