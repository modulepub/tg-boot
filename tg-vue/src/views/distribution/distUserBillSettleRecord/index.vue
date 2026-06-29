<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="distPayerUserCode">
				<el-input v-model="state.queryForm.distPayerUserCode" placeholder="付款用户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="tdOdSysUserCode">
				<el-input v-model="state.queryForm.tdOdSysUserCode" placeholder="下单人编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="tdGdName">
				<el-input v-model="state.queryForm.tdGdName" placeholder="商品名称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distSettleBatchCode">
				<el-input v-model="state.queryForm.distSettleBatchCode" placeholder="结算批次编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distSettledStatusCode">
				<el-select v-model="state.queryForm.distSettledStatusCode" placeholder="订单结算" clearable style="width: 120px">
					<el-option label="未完成" value="0"></el-option>
					<el-option label="已完成" value="1"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item prop="distSettleBatchStatusCode">
				<el-select v-model="state.queryForm.distSettleBatchStatusCode" placeholder="批次结算" clearable style="width: 120px">
					<el-option label="未完成" value="0"></el-option>
					<el-option label="已完成" value="1"></el-option>
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
			<el-table-column prop="distUserBillSettleRecordCode" label="记录编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdGdCode" label="订单商品编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdCode" label="商品编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdName" label="商品名称" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column label="分佣比例" width="100" align="center">
				<template #default="scope">
					{{ formatCommissionRate(scope.row.tdGdCommissionRate) }}
				</template>
			</el-table-column>
			<el-table-column prop="distPaidAmount" label="付费金额" width="110" align="right"></el-table-column>
			<el-table-column prop="distCommissionPoolAmount" label="分佣池" width="100" align="right"></el-table-column>
			<el-table-column prop="distInviterCommissionAmount" label="直推分佣" width="100" align="right"></el-table-column>
			<el-table-column prop="distSuperiorCommissionAmount" label="上级分佣" width="100" align="right"></el-table-column>
			<el-table-column prop="distInviterUserCode" label="直推邀请人" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distSuperiorInviterUserCode" label="上级邀请人" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distCommissionAppliedStatusCode" label="分佣已计入" width="100" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.distCommissionAppliedStatusCode)" type="success">是</el-tag>
					<el-tag v-else type="info">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="tdOdSysUserCode" label="下单人编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserRealName" label="下单人姓名" width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distPayerUserCode" label="付款用户编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distInServiceStatusCode" label="服务期内" width="90" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.distInServiceStatusCode)" type="warning">是</el-tag>
					<el-tag v-else type="info">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="distSettleAppliedStatusCode" label="已申请结算" width="100" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.distSettleAppliedStatusCode)" type="success">是</el-tag>
					<el-tag v-else type="info">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="distSettledStatusCode" label="订单结算" width="90" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.distSettledStatusCode)" type="success">已完成</el-tag>
					<el-tag v-else type="warning">未完成</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="distSettleBatchCode" label="结算批次编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distSettleBatchStatusCode" label="批次结算" width="90" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.distSettleBatchStatusCode)" type="success">已完成</el-tag>
					<el-tag v-else-if="isStatusNo(scope.row.distSettleBatchStatusCode)" type="warning">未完成</el-tag>
					<span v-else>—</span>
				</template>
			</el-table-column>
			<el-table-column prop="distServicePeriodEndAt" label="服务期结束" min-width="160"></el-table-column>
			<el-table-column prop="distSettledAt" label="订单结算时间" min-width="160"></el-table-column>
			<el-table-column prop="createTime" label="创建时间" min-width="160"></el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		/>
	</el-card>
</template>

<script setup lang="ts" name="DistributionDistUserBillSettleRecordIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/distribution/distUserBillSettleRecord/list',
	primaryKey: 'id',
	queryForm: {
		distPayerUserCode: '',
		tdOdSysUserCode: '',
		tdGdName: '',
		distSettleBatchCode: '',
		distSettledStatusCode: '',
		distSettleBatchStatusCode: ''
	}
})

const queryRef = ref()
const formatCommissionRate = (rate?: number | string) => {
	const n = Number(rate)
	if (Number.isNaN(n))
		return '90%'
	return `${Math.round(n * 100)}%`
}

const isStatusYes = (code: unknown) => {
	const value = String(code ?? '').trim().toUpperCase()
	return value === '1' || value === 'YES'
}

const isStatusNo = (code: unknown) => {
	const value = String(code ?? '').trim().toUpperCase()
	return value === '0' || value === 'NO'
}
const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
