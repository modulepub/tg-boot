<template>
	<el-card>
		<el-space>
			<el-space>
				<el-button type="primary" icon="Plus" @click="addOrEditHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button plain @click="toggleExpandAll()">
					<template v-if="!isExpandAll">
						全部展开&nbsp;<el-icon><ArrowDown /></el-icon>
					</template>
					<template v-else>
						全部收起&nbsp;<el-icon><ArrowUp /></el-icon>
					</template>
				</el-button>
			</el-space>
		</el-space>
		<el-table
			v-if="refreshTable"
			v-loading="state.dataListLoading"
			:default-expand-all="isExpandAll"
			:data="state.dataList"
			row-key="id"
			border
			class="layout-table"
		>
			<el-table-column prop="orgName" label="名称" header-align="left"></el-table-column>
			<el-table-column prop="seqNo" label="排序" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrEditHandle(scope.row.id)">修改</el-button>
					<el-button type="primary" link @click="deleteHandle([scope.row.id])">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { nextTick, reactive, ref } from 'vue'
import AddOrEdit from './add-or-edit.vue'
import { IHooksOptions } from '@/hooks/interface'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sysOrganization/list',
	deleteUrl: '/mgt/sysOrganization/delete',
	isPage: false
})

const addOrEditRef = ref()
const addOrEditHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, deleteHandle } = useCrud(state)

// 是否展开，默认全部折叠
const isExpandAll = ref(false)
// 是否重新渲染表格状态
const refreshTable = ref(true)

// 切换 展开和折叠
const toggleExpandAll = () => {
	refreshTable.value = false
	isExpandAll.value = !isExpandAll.value
	nextTick(() => {
		refreshTable.value = true
	})
}
</script>
