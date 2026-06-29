<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="wxPayConfigCode">
				<el-input v-model="state.queryForm.wxPayConfigCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item prop="wxPayConfigAppId">
				<el-input v-model="state.queryForm.wxPayConfigAppId" placeholder="微信 AppId"></el-input>
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
			<el-table-column prop="wxPayConfigCode" label="配置编码" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxPayConfigAppId" label="AppId" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxPayConfigMchId" label="商户号" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxPayConfigNotifyUrl" label="通知 URL" header-align="center" align="center" min-width="180" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxPayConfigEnabledCode" label="启用" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ formatEnabledText(row.wxPayConfigEnabledCode) }}
				</template>
			</el-table-column>
			<el-table-column prop="wxPayConfigUseSandbox" label="沙箱" header-align="center" align="center" width="72">
				<template #default="{ row }">
					{{ formatSandboxText(row.wxPayConfigUseSandbox) }}
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.wxPayConfigCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.wxPayConfigCode)">删除</el-button>
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

<script setup lang="ts" name="WxWxPayConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/wx/wxPayConfig/list',
	deleteUrl: '/mgt/wx/wxPayConfig/delete',
	primaryKey: 'wxPayConfigCode',
	queryForm: {
		wxPayConfigCode: '',
		wxPayConfigAppId: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()

const addOrUpdateHandle = (wxPayConfigCode?: string) => {
	addOrEditRef.value.init(wxPayConfigCode)
}

const refreshRuntimeHandle = () => {
	service.post('/mgt/wx/wxPayConfig/refreshRuntime').then(() => {
		ElMessage.success('已刷新微信支付运行时配置')
	})
}

/** 与编辑表单一致：1 启用，0 停用 */
const formatEnabledText = (val: unknown) => {
	const v = val === null || val === undefined ? '' : String(val).trim()
	if (v === '1') return '启用'
	if (v === '0') return '停用'
	return v === '' ? '—' : v
}

/** 与编辑表单一致：1 是（沙箱），0 否（正式） */
const formatSandboxText = (val: unknown) => {
	if (val === null || val === undefined || val === '') return '—'
	const n = Number(val)
	if (n === 1) return '是'
	if (n === 0) return '否'
	return String(val)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
