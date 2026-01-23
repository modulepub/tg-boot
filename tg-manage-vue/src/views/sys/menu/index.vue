<template>
	<el-card class="mod__menu">
		<el-space>
			<el-space>
				<el-button icon="Refresh" @click="refreshList()">刷新</el-button>
				<el-button icon="Plus" type="primary" @click="addOrModifyHandle(false, null)">新增</el-button>
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
			:key="tableKey"
			ref="tableRef"
			v-loading="state.dataListLoading"
			:default-expand-all="isExpandAll"
			:data="state.dataList"
			show-overflow-tooltip
			row-key="id"
			border
			class="layout-table"
			:tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
			@expand-change="handleExpandChange"
		>
			<el-table-column prop="perName" label="名称" header-align="center" min-width="150"></el-table-column>
			<el-table-column prop="perCode" label="编码" header-align="center" align="center" width="120"></el-table-column>
			<el-table-column prop="perIcon" label="图标" header-align="center" align="center">
				<template #default="scope">
					<tg-icon :icon="scope.row.perIcon"></tg-icon>
				</template>
			</el-table-column>
			<el-table-column prop="perTypeCode" label="类型" header-align="center" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.perTypeCode === '0'" type="info">菜单</el-tag>
					<el-tag v-if="scope.row.perTypeCode === '1'" type="success">按钮</el-tag>
					<el-tag v-if="scope.row.perTypeCode === '2'" type="warning">接口</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="perOpenStyleCode" label="打开方式" header-align="center" align="center">
				<template #default="scope">
					<span v-if="scope.row.perTypeCode !== '0'"></span>
					<el-tag v-else-if="scope.row.perOpenStyleCode === '0'">内部打开</el-tag>
					<el-tag v-else type="info">外部打开</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="seqNo" label="排序" header-align="center" align="center"></el-table-column>
			<el-table-column prop="perUrl" label="路由" header-align="center" align="center" width="150"></el-table-column>
			<el-table-column prop="perAuthority" label="授权标识" header-align="center" align="center" width="150"></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="220">
				<template #default="scope">
					<el-button type="primary" link @click="addOrModifyHandle(false, scope.row)">新增下级</el-button>
					<el-button type="primary" link @click="addOrModifyHandle(true, scope.row)">修改</el-button>
					<el-button type="primary" link @click="deleteHandle([scope.row.id])">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<add-or-modify ref="addOrModifyRef" @refresh-data-list="handleRefreshDataList"></add-or-modify>
	</el-card>
</template>

<script setup lang="ts">
import { reactive, ref, nextTick } from 'vue'
import AddOrModify from './add-or-modify.vue'
import { ArrowDown, ArrowUp, Refresh } from '@element-plus/icons-vue'
import service from '@/utils/request'
import { ElMessage } from 'element-plus/es'

const state = reactive({
	dataList: [] as any[],
	dataListLoading: false,
	deleteUrl: '/cus/sysPermission/delete'
})

const addOrModifyRef = ref()
const tableRef = ref()
const expandedKeys = ref<any[]>([])
const tableKey = ref(0)

const addOrModifyHandle = (isUpdate: Boolean, row: any) => {
	addOrModifyRef.value.init(isUpdate, row)
}

// 刷新列表
const refreshList = async () => {
	// 增加tableKey，强制表格完全重新渲染
	tableKey.value++
	// 清空展开的节点，确保重新加载子菜单数据
	expandedKeys.value = []
	// 重新加载数据，确保列表更新
	await getDataList()
	// 等待DOM更新，确保表格完全重新渲染
	await nextTick()
	// 再等待一小段时间，确保表格实例完全创建
	await new Promise(resolve => setTimeout(resolve, 200))
	// 保持当前展开状态
	if (isExpandAll.value) {
		await expandAllNodes(state.dataList)
	}
}

// 初始加载顶级菜单列表
const getDataList = async () => {
	state.dataListLoading = true
	try {
		// 使用新接口获取所有菜单结构
		const res = await service.get('/mgt/sysPermission/getByCode', {
			params: {
				code: 'root'
			}
		})
		console.log('菜单数据:', res)
		// 检查响应数据格式，确保正确解析
		const menuData = res.data || {}
		const children = menuData.children || []
		// 为每个菜单项添加hasChildren属性，确保可以展开
		state.dataList = children.map((item: any) => ({
			...item,
			hasChildren: !!item.children && item.children.length > 0
		}))
	} catch (error: any) {
		console.error('获取菜单列表失败:', error)
		ElMessage.error(`获取菜单列表失败: ${error.message || '未知错误'}`)
	} finally {
		state.dataListLoading = false
	}
}

// 删除菜单
const deleteHandle = async (ids: any[]) => {
	try {
		await service.post(state.deleteUrl, ids)
		ElMessage.success('删除成功')
		// 增加tableKey，强制表格完全重新渲染
		tableKey.value++
		// 清空展开的节点，确保重新加载子菜单数据
		expandedKeys.value = []
		// 重新加载数据，确保列表更新
		await getDataList()
		// 等待DOM更新
		await nextTick()
		// 保持当前展开状态
		if (isExpandAll.value) {
			await expandAllNodes(state.dataList)
		}
	} catch (error) {
		ElMessage.error('删除失败')
	}
}

// 由于改为一次性加载所有数据，不再需要load函数

// 处理节点展开/折叠事件
const handleExpandChange = (row: any, expandedRows: any[]) => {
	// 记录当前展开的节点ID
	expandedKeys.value = expandedRows.map(item => item.id)
}

// 是否展开，默认全部折叠
const isExpandAll = ref(false)

// 递归展开所有节点
const expandAllNodes = async (nodes: any[]) => {
	for (const node of nodes) {
		// 先将当前节点设置为展开状态
		if (!expandedKeys.value.includes(node.id)) {
			expandedKeys.value.push(node.id)
		}

		// 等待DOM更新
		await nextTick()

		// 使用表格的toggleRowExpansion方法展开当前节点
		if (tableRef.value) {
			tableRef.value.toggleRowExpansion(node, true)
			// 等待一小段时间，确保展开操作完成
			await new Promise(resolve => setTimeout(resolve, 50))
		}

		// 如果有子节点，递归展开
		if (node.children && node.children.length > 0) {
			await expandAllNodes(node.children)
		}

		// 等待一小段时间，确保操作完成
		await new Promise(resolve => setTimeout(resolve, 50))
	}
}

// 切换 展开和折叠
const toggleExpandAll = async () => {
	isExpandAll.value = !isExpandAll.value
	if (isExpandAll.value) {
		// 重新加载数据
		await getDataList()
		// 等待DOM更新，确保表格完全渲染
		await nextTick()
		// 再等待一小段时间，确保表格实例完全准备好
		await new Promise(resolve => setTimeout(resolve, 200))
		// 递归展开所有节点
		await expandAllNodes(state.dataList)
	} else {
		// 折叠所有节点
		expandedKeys.value = []
		// 重新加载数据，使用默认折叠状态
		await getDataList()
		// 等待DOM更新
		await nextTick()
		// 确保表格完全折叠
		if (tableRef.value) {
			state.dataList.forEach(item => {
				tableRef.value.toggleRowExpansion(item, false)
			})
		}
	}
}

// 处理数据刷新事件
const handleRefreshDataList = async () => {
	// 增加tableKey，强制表格完全重新渲染
	tableKey.value++
	// 清空展开的节点，确保重新加载子菜单数据
	expandedKeys.value = []
	// 重新加载数据，确保列表更新
	await getDataList()
	// 等待DOM更新，确保表格完全重新渲染
	await nextTick()
	// 再等待一小段时间，确保表格实例完全创建
	await new Promise(resolve => setTimeout(resolve, 200))
	// 保持当前展开状态
	if (isExpandAll.value) {
		await expandAllNodes(state.dataList)
	}
}

// 初始加载数据
getDataList()
</script>
