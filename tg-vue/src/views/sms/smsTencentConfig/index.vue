<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="smsTencentConfigCode">
				<el-input v-model="state.queryForm.smsTencentConfigCode" placeholder="配置编码"></el-input>
			</el-form-item>
			<el-form-item prop="smsTencentConfigSignName">
				<el-input v-model="state.queryForm.smsTencentConfigSignName" placeholder="短信签名"></el-input>
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
			<el-table-column prop="smsTencentConfigCode" label="配置编码" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsTencentConfigSdkAppId" label="SdkAppId" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsTencentConfigSignName" label="默认签名" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="smsTencentConfigRegion" label="地域" header-align="center" align="center" width="130"></el-table-column>
			<el-table-column prop="smsTencentConfigEnabledCode" label="启用" header-align="center" align="center" width="88">
				<template #default="{ row }">
					{{ formatEnabledText(row.smsTencentConfigEnabledCode) }}
				</template>
			</el-table-column>
			<el-table-column prop="smsTencentConfigRemark" label="备注" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.smsTencentConfigCode)">修改</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.smsTencentConfigCode)">删除</el-button>
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

<script setup lang="ts" name="SmsSmsTencentConfigIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sms/smsTencentConfig/list',
	deleteUrl: '/mgt/sms/smsTencentConfig/delete',
	primaryKey: 'smsTencentConfigCode',
	queryForm: {
		smsTencentConfigCode: '',
		smsTencentConfigSignName: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()

const addOrUpdateHandle = (smsTencentConfigCode?: string) => {
	addOrEditRef.value.init(smsTencentConfigCode)
}

const refreshRuntimeHandle = () => {
	service.post('/mgt/sms/smsTencentConfig/refreshRuntime').then(() => {
		ElMessage.success('已刷新腾讯云短信运行时配置')
	})
}

const formatEnabledText = (val: unknown) => {
	const v = val === null || val === undefined ? '' : String(val).trim()
	if (v === '1') return '启用'
	if (v === '0') return '停用'
	return v === '' ? '—' : v
}

const { getDataList, deleteBatchHandle, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
