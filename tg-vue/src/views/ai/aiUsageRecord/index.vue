<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="userCode">
				<el-input v-model="state.queryForm.userCode" placeholder="用户编码"></el-input>
			</el-form-item>
			<el-form-item prop="aiAgentCode">
				<el-input v-model="state.queryForm.aiAgentCode" placeholder="智能体编码"></el-input>
			</el-form-item>
			<el-form-item prop="aiUsageRecordSuccessCode">
				<el-select v-model="state.queryForm.aiUsageRecordSuccessCode" placeholder="状态" clearable>
					<el-option label="成功" value="1" />
					<el-option label="失败" value="0" />
				</el-select>
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
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
			<el-table-column prop="aiUsageRecordCode" label="明细编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userCode" label="用户" width="120"></el-table-column>
			<el-table-column prop="aiAgentCode" label="智能体" min-width="120"></el-table-column>
			<el-table-column prop="aiApiConfigCode" label="接口配置" min-width="120"></el-table-column>
			<el-table-column prop="aiUsageRecordModel" label="模型" min-width="120"></el-table-column>
			<el-table-column prop="aiUsageRecordPromptTokens" label="输入 tokens" width="110"></el-table-column>
			<el-table-column prop="aiUsageRecordCompletionTokens" label="输出 tokens" width="110"></el-table-column>
			<el-table-column prop="aiUsageRecordInputUnitPrice" label="输入单价/1K" width="110"></el-table-column>
			<el-table-column prop="aiUsageRecordOutputUnitPrice" label="输出单价/1K" width="110"></el-table-column>
			<el-table-column prop="aiUsageRecordTotalPrice" label="总价(元)" width="100"></el-table-column>
			<el-table-column prop="aiUsageRecordSuccessCode" label="状态" width="80">
				<template #default="{ row }">{{ row.aiUsageRecordSuccessCode === '1' ? '成功' : '失败' }}</template>
			</el-table-column>
			<el-table-column prop="createTime" label="时间" min-width="160"></el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		></el-pagination>
	</el-card>
</template>

<script setup lang="ts" name="AiAiUsageRecordIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/ai/aiUsageRecord/list',
	queryForm: {
		userCode: '',
		aiAgentCode: '',
		aiUsageRecordSuccessCode: ''
	}
})

const queryRef = ref()
const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
