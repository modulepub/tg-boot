<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="wxMaSubscribeTemplateCode">
				<el-input v-model="state.queryForm.wxMaSubscribeTemplateCode" placeholder="模板编码"></el-input>
			</el-form-item>
			<el-form-item prop="wxMaSubscribeTemplateId">
				<el-input v-model="state.queryForm.wxMaSubscribeTemplateId" placeholder="微信模板 ID"></el-input>
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
		<el-alert
			title="模板编码由开发通过 SQL 维护，与业务发送逻辑绑定；此处仅可修改微信模板 ID 与模板说明。"
			type="info"
			:closable="false"
			show-icon
			class="layout-alert"
		/>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
			<el-table-column prop="wxMaSubscribeTemplateCode" label="模板编码" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMaSubscribeTemplateId" label="微信模板 ID" header-align="center" align="center" min-width="280" show-overflow-tooltip></el-table-column>
			<el-table-column prop="wxMaSubscribeTemplateContent" label="模板说明" header-align="center" align="center" min-width="260" show-overflow-tooltip></el-table-column>
			<el-table-column prop="updateTime" label="更新时间" header-align="center" align="center" width="170"></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<el-button type="primary" link @click="editHandle(scope.row.wxMaSubscribeTemplateCode)">修改</el-button>
					<el-button type="success" link @click="sendTestHandle(scope.row.wxMaSubscribeTemplateCode)">发送测试</el-button>
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

		<edit-dialog ref="editDialogRef" @refreshDataList="getDataList"></edit-dialog>
		<send-test-dialog ref="sendTestDialogRef"></send-test-dialog>
	</el-card>
</template>

<script setup lang="ts" name="WxWxMaSubscribeTemplateIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import EditDialog from './edit-dialog.vue'
import SendTestDialog from './send-test-dialog.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/wx/wxMaSubscribeTemplate/list',
	queryForm: {
		wxMaSubscribeTemplateCode: '',
		wxMaSubscribeTemplateId: ''
	}
})

const queryRef = ref()
const editDialogRef = ref()
const sendTestDialogRef = ref()

const editHandle = (wxMaSubscribeTemplateCode?: string) => {
	editDialogRef.value.init(wxMaSubscribeTemplateCode)
}

const sendTestHandle = (wxMaSubscribeTemplateCode?: string) => {
	sendTestDialogRef.value.init(wxMaSubscribeTemplateCode)
}

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>

<style scoped>
.layout-alert {
	margin-bottom: 16px;
}
</style>
