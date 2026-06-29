<template>
	<el-card>
		<el-space>
			<el-space>
				<el-button type="primary" icon="Plus" @click="addOrEditHandle('add')">新增</el-button>
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
			ref="tableRef"
			v-loading="state.dataListLoading"
			:default-expand-all="isExpandAll"
			:data="state.dataList"
			row-key="id"
			border
			class="layout-table"
			:tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
		>
			<el-table-column prop="orgName" label="名称" header-align="left" min-width="200"></el-table-column>
			<el-table-column prop="orgCategoryCode" label="类型" header-align="center" align="center" width="100">
				<template #default="scope">
					<el-tag v-if="scope.row.orgCategoryCode === 'com'" type="primary">公司</el-tag>
					<el-tag v-else type="info">部门</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="seqNo" label="排序" header-align="center" align="center" width="80" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="220">
				<template #default="scope">
					<el-button type="primary" link @click="addOrEditHandle('addChild', scope.row)">新增下级</el-button>
					<el-button type="primary" link @click="addOrEditHandle('edit', scope.row)">修改</el-button>
					<el-button v-if="scope.row.orgCategoryCode !== 'com'" type="primary" link @click="addOrEditHandle('adjust', scope.row)">调机构</el-button>
					<el-button type="primary" link @click="deleteHandle([scope.row.id])">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref } from 'vue'
import AddOrEdit from './add-or-edit.vue'
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import service from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const state = reactive({
	dataList: [] as any[],
	dataListLoading: false
})

const addOrEditRef = ref()
const tableRef = ref()

type OrgAction = 'add' | 'addChild' | 'edit' | 'adjust'

const addOrEditHandle = (action: OrgAction, row?: any) => {
	addOrEditRef.value.init(action, row)
}

const mapTreeData = (list: any[]): any[] => {
	return (list || []).map((item: any) => ({
		...item,
		hasChildren: !!item.children && item.children.length > 0,
		children: item.children ? mapTreeData(item.children) : []
	}))
}

const getDataList = async () => {
	state.dataListLoading = true
	try {
		const res = await service.get('/mgt/sysOrganization/listTree')
		state.dataList = mapTreeData(res.data || [])
	} catch (error: any) {
		ElMessage.error(`获取机构列表失败: ${error.message || '未知错误'}`)
	} finally {
		state.dataListLoading = false
	}
}

const deleteHandle = async (ids: string[]) => {
	try {
		await ElMessageBox.confirm('确定进行删除操作?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		})
		await service.post('/mgt/sysOrganization/delete', ids)
		ElMessage.success('删除成功')
		await getDataList()
	} catch (error) {
		if (error !== 'cancel' && error !== 'close') {
			ElMessage.error('删除失败')
		}
	}
}

const isExpandAll = ref(false)
const refreshTable = ref(true)

const toggleExpandAll = async () => {
	refreshTable.value = false
	isExpandAll.value = !isExpandAll.value
	await getDataList()
	nextTick(() => {
		refreshTable.value = true
	})
}

getDataList()
</script>
