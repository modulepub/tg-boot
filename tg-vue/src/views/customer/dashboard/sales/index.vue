<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="createDateRangeArray">
				<el-date-picker
					v-model="state.queryForm.createDateRangeArray"
					type="daterange"
					range-separator="至"
					start-placeholder="开始日期"
					end-placeholder="结束日期"
					value-format="YYYY-MM-DD"
					clearable
				></el-date-picker>
			</el-form-item>
			<el-form-item>
				<el-button icon="Search" type="primary" @click="getDataList()">查询</el-button>
			</el-form-item>
			<el-form-item>
				<el-button icon="RefreshRight" @click="resetToday">今天</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column prop="userCode" label="员工号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userRealName" label="员工姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSalesTotal" label="客户总数" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusFollowUpTotal" label="已跟进" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusFollowUpRate" label="跟进率" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusAnswerTotal" label="接听数" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusAnswerRate" label="有效通话数" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column
				prop="cusEffectiveCallDurationTotal"
				label="接听率"
				header-align="center"
				align="center"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column
				prop="cusEffectiveCallDurationRate"
				label="有效通话率"
				header-align="center"
				align="center"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column prop="cusFollowUpRate" label="跟进率" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusIntentionTotal" label="意向客户数" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusIntentionRate" label="意向率" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSalesDealtTotal" label="已成交" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSalesDealtRate" label="成交率" header-align="center" align="center" show-overflow-tooltip></el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:page-sizes="state.pageSizes"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		>
		</el-pagination>

		<!-- 弹窗, 新增 / 修改 -->
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import { useAppStore } from '@/store/modules/app'

const today = new Date().toISOString().split('T')[0]

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/customer/dashboard/salesPerformanceList',
	deleteUrl: '/mgt/customer/customerPromotionRelation/delete',
	queryForm: {
		createDateRangeArray: [today, today]
	}
})
const queryRef = ref()
const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

const resetToday = () => {
	const today = new Date().toISOString().split('T')[0]
	state.queryForm.createDateRangeArray = [today, today]
	getDataList()
}
</script>
