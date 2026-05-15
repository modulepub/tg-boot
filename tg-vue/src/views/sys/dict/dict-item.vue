<template>
	<el-space>
		<el-button icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
	</el-space>
	<el-table
		v-loading="state.dataListLoading"
		:data="state.dataList"
		border
		show-overflow-tooltip
		class="layout-table"
		@selection-change="selectionChangeHandle"
		@sort-change="sortChangeHandle"
	>
		<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
		<el-table-column prop="dictItemText" label="字典标签" header-align="center" align="center">
			<template #default="scope">
				<el-tag v-if="scope.row.dictItemColor" :color="scope.row.dictItemColor" effect="dark" :style="{ border: 'none' }">
					{{ scope.row.dictItemText }}
				</el-tag>
				<span v-else>{{ scope.row.dictItemText }}</span>
			</template>
		</el-table-column>
		<el-table-column prop="dictItemValue" label="字典值" header-align="center" align="center" show-overflow-tooltip></el-table-column>
		<el-table-column prop="seqNo" label="排序" sortable="custom" header-align="center" align="center" show-overflow-tooltip></el-table-column>
		<el-table-column prop="createTime" label="创建时间" header-align="center" align="center" width="180"></el-table-column>
		<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
			<template #default="scope">
				<el-button type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
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
	<!-- 新增 / 修改 -->
	<dict-item-add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></dict-item-add-or-edit>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import DictItemAddOrEdit from './dict-item-add-or-edit.vue'
import { IHooksOptions } from '@/hooks/interface'

const props = defineProps({
	dictCode: {
		type: String,
		required: true
	}
})

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sysDictItem/list',
	deleteUrl: '/mgt/sysDictItem/delete',
	queryForm: {
		dictCode: props.dictCode
	}
})

const addOrEditRef = ref()
const addOrUpdateHandle = (id?: Number) => {
	addOrEditRef.value.dataForm.dictCode = props.dictCode
	addOrEditRef.value.init(id)
}

const { getDataList, sizeChangeHandle, selectionChangeHandle, sortChangeHandle, currentChangeHandle, deleteBatchHandle } = useCrud(state)
</script>
