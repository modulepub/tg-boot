<template>
	<div style="height: 100%">
		<div style="display: flex; height: 100vh">
			<div style="width: 300px; border-right: 1px solid #e4e7ed; height: 100%; overflow-y: auto">
				<el-card style="height: 100%; border-radius: 0; border: none">
					<template #header>
						<div class="card-header">
							<span>目录树</span>
						</div>
					</template>
					<el-tree
						v-loading="state.treeLoading"
						:data="state.treeData"
						:props="treeProps"
						node-key="nodeCode"
						default-expand-all
						@node-click="handleNodeClick"
						@node-contextmenu="handleNodeContextMenu"
					></el-tree>
					<!-- 右键菜单 -->
					<div
						v-show="false"
						ref="contextMenuRef"
						class="context-menu"
						style="
							position: fixed;
							z-index: 10000;
							background: white;
							border: 1px solid #e4e7ed;
							border-radius: 4px;
							box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
							padding: 4px 0;
							min-width: 120px;
						"
					>
						<div class="context-menu-item" style="padding: 8px 16px; cursor: pointer" @click="handleAddChild">新增子节点</div>
						<div
							v-show="currentNode && currentNode.nodeCode != 'root'"
							class="context-menu-item"
							style="padding: 8px 16px; cursor: pointer"
							@click="handleEdit"
						>
							修改
						</div>
						<div class="context-menu-item" style="padding: 8px 16px; cursor: pointer; color: #f56c6c" @click="handleDelete">删除</div>
					</div>
				</el-card>
			</div>
			<div style="flex: 1; padding: 20px; height: 100%; overflow-y: auto">
				<el-card class="layout-query">
					<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
						<el-form-item prop="nodeParentCode">
							<el-input v-model="state.queryForm.nodeParentCode" placeholder="栏目编码"></el-input>
						</el-form-item>
						<el-form-item prop="nodeName">
							<el-input v-model="state.queryForm.nodeName" placeholder="名称"></el-input>
						</el-form-item>
						<el-form-item prop="nodePublishStatusCode">
							<tg-dict-select
								v-model="state.queryForm.nodePublishStatusCode"
								dict-code="nodePublishStatusCode"
								clearable
								placeholder="发布状态"
							></tg-dict-select>
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
							<el-button v-auth="'cmsCmsNodeAdd'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
						</el-space>
						<el-space>
							<el-button v-auth="'cmsCmsNodeDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
						</el-space>
					</el-space>
					<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
						<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
						<el-table-column
							prop="nodeParentCode"
							label="栏目编码"
							header-align="center"
							align="center"
							show-overflow-tooltip
							width="100"
						></el-table-column>
						<el-table-column
							prop="nodeCode"
							label="内容编码"
							header-align="center"
							align="center"
							show-overflow-tooltip
							width="100"
						></el-table-column>
						<el-table-column
							prop="nodeName"
							label="内容名称"
							header-align="center"
							align="center"
							show-overflow-tooltip
							width="100"
						></el-table-column>
						<tg-image-column prop="nodeHeadImg" label="内容头图" width="100" header-align="center" align="center"></tg-image-column>
						<el-table-column prop="nodeSummary" label="内容摘要" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column
							prop="nodePublishTime"
							label="发布时间"
							header-align="center"
							align="center"
							show-overflow-tooltip
							width="100"
						></el-table-column>
						<tg-dict-column prop="nodePublishStatusCode" label="发布状态" width="100" dict-code="nodePublishStatusCode"></tg-dict-column>
						<tg-dict-column prop="nodeContentTypeCode" label="内容类型" width="100" dict-code="nodeContentTypeCode"></tg-dict-column>
						<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
							<template #default="scope">
								<el-button v-auth="'cmsCmsNodeModify'" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
								<el-button v-auth="'cmsCmsNodeDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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
					<add-or-edit ref="addOrEditRef" @refresh-data-list="getDtaListR"></add-or-edit>
				</el-card>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts" name="CmsnodeIndex">
import { useCrud } from '@/hooks'
import { reactive, ref, onMounted } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import service from '@/utils/request'

const state: any = reactive({
	dataListUrl: '/mgt/cms/cmsNode/listDocument',
	deleteUrl: '/mgt/cms/cmsNode/delete',
	treeData: [],
	treeLoading: false,
	queryForm: {
		nodeParentCode: '',
		nodeCode: '',
		nodeName: '',
		nodeHeadImg: '',
		nodeSummary: '',
		nodePublishTime: '',
		nodePublishStatusCode: '',
		nodeTypeCode: '',
		nodeContentTypeCode: '',
		nodeLink: '',
		nodeContent: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const contextMenuRef = ref()
const currentNode = ref<any>(null)

const treeProps = {
	label: 'nodeName',
	children: 'children'
}

const getTreeData = async () => {
	state.treeLoading = true
	try {
		const response = await service.get('/mgt/cms/cmsNode/listCatalogTree', {
			params: {
				nodeCode: 'root'
			}
		})
		console.log('response.data', response.data)

		const processTreeData = (data: any) => {
			if (data) {
				if (data.children === null) {
					data.children = []
				} else if (Array.isArray(data.children)) {
					data.children.forEach(processTreeData)
				}
			}
			return data
		}
		const processedData = processTreeData(response.data)
		state.treeData = [processedData]
	} catch (error) {
		console.error('获取树数据失败:', error)
	} finally {
		state.treeLoading = false
	}
}

const handleNodeClick = (data: any) => {
	state.queryForm.nodeParentCode = data.nodeCode
	getDataList()
}

const handleNodeContextMenu = (event: any, data: any) => {
	event.preventDefault()
	currentNode.value = data
	if (contextMenuRef.value) {
		contextMenuRef.value.style.left = event.clientX + 'px'
		contextMenuRef.value.style.top = event.clientY + 'px'
		contextMenuRef.value.style.display = 'block'
	}

	document.addEventListener('click', handleClickOutside)
}

const handleClickOutside = () => {
	if (contextMenuRef.value) {
		contextMenuRef.value.style.display = 'none'
	}
	document.removeEventListener('click', handleClickOutside)
}

const handleAddChild = () => {
	if (currentNode.value) {
		// 调用新增方法，并传递父节点编码
		addOrEditRef.value.init(null, currentNode.value.nodeCode, currentNode.value.nodeTypeCode)
	}
	if (contextMenuRef.value) {
		contextMenuRef.value.style.display = 'none'
	}
}

const handleEdit = () => {
	console.log('currentNode.value', currentNode.value)
	if (currentNode.value) {
		// 调用修改方法，传递节点ID
		addOrUpdateHandle(currentNode.value.id)
	}
	if (contextMenuRef.value) {
		contextMenuRef.value.style.display = 'none'
	}
}

const handleDelete = () => {
	if (currentNode.value) {
		// 调用删除方法，传递节点ID
		deleteBatchHandle(currentNode.value.id)
	}
	if (contextMenuRef.value) {
		contextMenuRef.value.style.display = 'none'
	}
}

const addOrUpdateHandle = (id?: number) => {
	if (state.queryForm.nodeParentCode) {
		// 调用新增方法，并传递父节点编码
		addOrEditRef.value.init(id, state.queryForm.nodeParentCode)
	} else {
		addOrEditRef.value.init(id)
	}
}
const getDtaListR = (id?: number) => {
	getTreeData()
	getDataList()
}
const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

onMounted(() => {
	getTreeData()
})
</script>
