<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="smsTemplateCode">
				<el-input v-model="state.queryForm.smsTemplateCode" placeholder="模板编码"></el-input>
			</el-form-item>
			<el-form-item prop="smsProviderCode">
				<el-input v-model="state.queryForm.smsProviderCode" placeholder="渠道编码"></el-input>
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
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="smsTemplateCode" label="模板编码" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsProviderCode" label="渠道" header-align="center" align="center" min-width="120"></el-table-column>
			<el-table-column prop="smsTemplateId" label="渠道模板 ID" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsTemplateContent" label="模板内容" header-align="center" align="center" min-width="200" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsTemplateEnabledCode" label="启用" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ formatEnabledText(row.smsTemplateEnabledCode) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.smsTemplateCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.smsTemplateCode)">删除</el-button>
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

<script setup lang="ts" name="SmsSmsTemplateIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sms/smsTemplate/list',
	deleteUrl: '/mgt/sms/smsTemplate/delete',
	primaryKey: 'smsTemplateCode',
	queryForm: {
		smsTemplateCode: '',
		smsProviderCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()

const addOrUpdateHandle = (smsTemplateCode?: string) => {
	addOrEditRef.value.init(smsTemplateCode)
}

const formatEnabledText = (val: unknown) => {
	const v = val === null || val === undefined ? '' : String(val).trim()
	if (v === '1') return '启用'
	if (v === '0') return '停用'
	return v === '' ? '—' : v
}

const { getDataList, deleteBatchHandle, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
