<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="wxMpConfigCode">
				<el-input v-model="state.queryForm.wxMpConfigCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item prop="wxMpConfigAppId">
				<el-input v-model="state.queryForm.wxMpConfigAppId" placeholder="公众号 AppId"></el-input>
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
			<el-table-column prop="wxMpConfigCode" label="配置编码" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigName" label="配置名称" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigAppId" label="AppId" header-align="center" align="center" min-width="180" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigToken" label="Token" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigEnabledStatusCode" label="启用" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ formatEnabledText(row.wxMpConfigEnabledStatusCode) }}
				</template>
			</el-table-column>
			<el-table-column prop="wxMpConfigAiAutoReplyStatusCode" label="AI回复" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ row.wxMpConfigAiAutoReplyStatusCode === '1' ? '开启' : '关闭' }}
				</template>
			</el-table-column>
			<el-table-column prop="wxMpConfigSubscribeReplyStatusCode" label="关注回复" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ row.wxMpConfigSubscribeReplyStatusCode === '1' ? '开启' : '关闭' }}
				</template>
			</el-table-column>
			<el-table-column prop="wxMpConfigAiAgentCode" label="智能体" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigMenuPublishedTime" label="菜单发布时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMpConfigRemark" label="备注" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="220">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.wxMpConfigCode)">修改</el-button>
					<el-button type="primary" link @click="menuEditorHandle(scope.row)">菜单</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.wxMpConfigCode)">删除</el-button>
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
		<menu-editor ref="menuEditorRef" @refreshDataList="getDataList"></menu-editor>
	</el-card>
</template>

<script setup lang="ts" name="WxWxMpConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import MenuEditor from './menu-editor.vue'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/wx/wxMpConfig/list',
	deleteUrl: '/mgt/wx/wxMpConfig/delete',
	primaryKey: 'wxMpConfigCode',
	queryForm: {
		wxMpConfigCode: '',
		wxMpConfigAppId: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const menuEditorRef = ref()

const addOrUpdateHandle = (wxMpConfigCode?: string) => {
	addOrEditRef.value.init(wxMpConfigCode)
}

const menuEditorHandle = (row: { wxMpConfigCode: string; wxMpConfigName?: string }) => {
	menuEditorRef.value.init(row.wxMpConfigCode, row.wxMpConfigName)
}

const refreshRuntimeHandle = () => {
	service.post('/mgt/wx/wxMpConfig/refreshRuntime').then(() => {
		ElMessage.success('已刷新微信公众号运行时配置')
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
