<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="userCode">
				<el-input v-model="state.queryForm.userCode" placeholder="员工号"></el-input>
			</el-form-item>
			<el-form-item prop="cusCode">
				<el-input v-model="state.queryForm.cusCode" placeholder="客户编码"></el-input>
			</el-form-item>
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item prop="cusIdNo">
				<el-input v-model="state.queryForm.cusIdNo" placeholder="客户证件号"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="客户手机号"></el-input>
			</el-form-item>
			<el-form-item prop="cusFollowUpStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusFollowUpStatusCode"
					dict-code="cusFollowUpStatusCode"
					clearable
					placeholder="是否跟进"
				></tg-dict-select>
			</el-form-item>

			<el-form-item prop="cusPoolStatusCode">
				<tg-dict-select v-model="state.queryForm.cusDealtStatusCode" dict-code="cusDealtStatusCode" clearable placeholder="是否成交"></tg-dict-select>
			</el-form-item>
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
				<el-button icon="RefreshRight" @click="reset(queryRef)">重置</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-tabs v-model="activeTab" @tab-click="handleTabClick">
			<el-tab-pane v-for="item in promotionTaskTypeCode" :key="item.dictItemValue" :label="item.dictItemText" :name="item.dictItemValue"></el-tab-pane>
		</el-tabs>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column prop="promotionTaskCode" label="任务编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusCode" label="客户编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userCode" label="员工号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userRealName" label="员工姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusPhone" label="客户手机号" width="100" header-align="center" align="center" show-overflow-tooltip></el-table-column>
      <tg-dict-column prop="cusFollowUpStatusCode" label="是否跟进" dict-code="cusFollowUpStatusCode"></tg-dict-column>
      <tg-dict-column prop="promotionTaskTypeCode" label="任务类型" dict-code="promotionTaskTypeCode"></tg-dict-column>
      <tg-dict-column prop="cusDealtStatusCode" label="成交状态" dict-code="cusDealtStatusCode"></tg-dict-column>
      <tg-dict-column prop="cusDealtCompleteStatusCode" label="完单状态" dict-code="cusDealtCompleteStatusCode"></tg-dict-column>
			<el-table-column prop="createTime" label="分配时间" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
				</template>
			</el-table-column>
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
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import { useAppStore } from '@/store/modules/app'
import { getDictDataList } from '@/utils/tool'

const appStore = useAppStore()
const promotionTaskTypeCode = getDictDataList(appStore.dictList, 'promotionTaskTypeCode')
const activeTab = ref(promotionTaskTypeCode.length > 0 ? promotionTaskTypeCode[0].dictItemValue : '0')

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/customer/customerPromotionTask/list',
	deleteUrl: '/mgt/customer/customerPromotionTask/delete',
	queryForm: {
		seqNo: '',
		orgCode: '',
		version: '',
		deleted: '',
		cusCode: '',
		userCode: '',
		userRealName: '',
    promotionTaskTypeCode: promotionTaskTypeCode.length > 0 ? promotionTaskTypeCode[0].dictItemValue : '',
		cusName: '',
		cusIdNo: '',
		cusPhone: '',
		createDateRangeArray: []
	}
})

const queryRef = ref()
const addOrEditRef = ref()

const handleTabClick = (tab: any) => {
	state.queryForm.promotionTaskTypeCode = tab.props.name
	getDataList()
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
