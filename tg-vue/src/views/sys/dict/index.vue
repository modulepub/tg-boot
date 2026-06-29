<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList">
			<el-form-item prop="dictCode">
				<el-input v-model="state.queryForm.dictCode" placeholder="字典编码"></el-input>
			</el-form-item>
			<el-form-item prop="dictName">
				<el-input v-model="state.queryForm.dictName" placeholder="字典名称"></el-input>
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

		<el-table
			v-loading="state.dataListLoading"
			:data="state.dataList"
			border
			align="center"
			show-overflow-tooltip
			class="layout-table"
			row-key="id"
			lazy
			:load="load"
			:tree-props="{ children: 'children', hasChildren: 'hasChild' }"
			@selection-change="selectionChangeHandle"
			@sort-change="sortChangeHandle"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="dictCode" label="字典编码" header-align="center" align="center"> </el-table-column>
			<el-table-column prop="dictName" label="字典名称" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="createBy" label="创建人" header-align="center" align="center" width="180"></el-table-column>
			<el-table-column prop="createTime" label="创建时间" header-align="center" align="center" width="180"></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="200">
				<template #default="scope">
					<el-button type="primary" link @click="showDictDataHandle(scope.row)">字典配置</el-button>
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
		<!-- 新增 / 修改 -->
		<dict-add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></dict-add-or-edit>
		<!-- 字典配置 -->
		<el-drawer v-if="dictDataVisible" v-model="dictDataVisible" :title="dictDataTitle" :size="800" :close-on-press-escape="false">
			<dict-item :dict-code="dictCode"></dict-item>
		</el-drawer>
	</el-card>
</template>

<script setup lang="ts" name="SysdictCode">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import DictAddOrEdit from './dict-add-or-edit.vue'
import DictItem from './dict-item.vue'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sysDict/list',
	deleteUrl: '/mgt/sysDict/delete',
	queryForm: {
		dictName: '',
		dictCode: ''
	}
})

const dictDataVisible = ref(false)
const dictDataTitle = ref()
const dictCode = ref()
const showDictDataHandle = (row: any) => {
	dictCode.value = row.dictCode
	dictDataTitle.value = '字典配置 - ' + row.dictCode
	dictDataVisible.value = true
}
// 当数据变化时，处理刷新
const nodeMap = new Map()
const load = (tree: any, treeNode: unknown, resolve: (data: any[]) => void) => {
	if (!nodeMap.has(tree.id)) {
		nodeMap.set(tree.id, { tree, treeNode, resolve })
	}

	service.get('/sys/dict/type/list?pid=' + tree.id).then((res: any) => {
		if (res.data.length > 0) {
			resolve(res.data)
		} else {
			resolve([])
			// location.reload()
		}
	})
}

const queryRef = ref()
const addOrEditRef = ref()
const addOrEditHandle = (id?: Number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, sizeChangeHandle, selectionChangeHandle, sortChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
