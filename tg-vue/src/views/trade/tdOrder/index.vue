<template>
	<el-row :gutter="16" class="order-statistics">
		<el-col :lg="6" :md="12" :sm="12" :xs="24">
			<el-card v-loading="statisticsLoading" shadow="never" class="stat-card">
				<div class="stat-card__label">下单总金额</div>
				<div class="stat-card__value">{{ formatAmount(statistics.totalOrderAmount) }}</div>
			</el-card>
		</el-col>
		<el-col :lg="6" :md="12" :sm="12" :xs="24">
			<el-card v-loading="statisticsLoading" shadow="never" class="stat-card">
				<div class="stat-card__label">已支付金额</div>
				<div class="stat-card__value stat-card__value--success">{{ formatAmount(statistics.totalPaidAmount) }}</div>
			</el-card>
		</el-col>
		<el-col :lg="6" :md="12" :sm="12" :xs="24">
			<el-card v-loading="statisticsLoading" shadow="never" class="stat-card">
				<div class="stat-card__label">今日下单总金额</div>
				<div class="stat-card__value">{{ formatAmount(statistics.todayOrderAmount) }}</div>
			</el-card>
		</el-col>
		<el-col :lg="6" :md="12" :sm="12" :xs="24">
			<el-card v-loading="statisticsLoading" shadow="never" class="stat-card">
				<div class="stat-card__label">今日已支付金额</div>
				<div class="stat-card__value stat-card__value--success">{{ formatAmount(statistics.todayPaidAmount) }}</div>
			</el-card>
		</el-col>
	</el-row>

	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="tdOdCode">
				<el-input v-model="state.queryForm.tdOdCode" placeholder="订单编号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="tdOdSysUserCode">
				<el-input v-model="state.queryForm.tdOdSysUserCode" placeholder="下单人账号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="tdOdPaidStatusCode">
				<el-select v-model="state.queryForm.tdOdPaidStatusCode" placeholder="支付状态" clearable style="width: 120px">
					<el-option label="已支付" value="1"></el-option>
					<el-option label="未支付" value="0"></el-option>
				</el-select>
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
			<el-form-item>
				<el-button @click="filterToday">今日</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-space>
			<el-space>
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table
			v-loading="state.dataListLoading"
			:data="state.dataList"
			border
			class="layout-table order-table"
			style="width: 100%"
			@selection-change="selectionChangeHandle"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="tdOdCode" label="订单编号" min-width="160" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdAmount" label="订单金额" min-width="100" header-align="center" align="center">
				<template #default="scope">
					{{ formatAmount(scope.row.tdOdAmount) }}
				</template>
			</el-table-column>
			<el-table-column prop="tdOdRefundAmount" label="退款金额" min-width="100" header-align="center" align="center">
				<template #default="scope">
					{{ formatAmount(scope.row.tdOdRefundAmount) }}
				</template>
			</el-table-column>
			<el-table-column prop="tdOdPaidStatusCode" label="支付状态" min-width="100" header-align="center" align="center">
				<template #default="scope">
					<el-tag v-if="scope.row.tdOdPaidStatusCode === '1'" type="success">已支付</el-tag>
					<el-tag v-else type="info">未支付</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="下单时间" min-width="160" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserCode" label="下单人账号" min-width="130" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserRealName" label="下单人姓名" min-width="100" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserPhone" label="下单人电话" min-width="120" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" header-align="center" align="center" min-width="150">
				<template #default="scope">
					<el-button type="primary" link @click="viewDetailHandle(scope.row.id)">查看详情</el-button>
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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

		<order-detail ref="orderDetailRef"></order-detail>
	</el-card>
</template>

<script setup lang="ts" name="TradeTdOrderIndex">
import { useCrud } from '@/hooks'
import { onMounted, reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'
import OrderDetail from './detail.vue'

interface OrderStatistics {
	totalOrderAmount: number | string
	totalPaidAmount: number | string
	todayOrderAmount: number | string
	todayPaidAmount: number | string
}

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/trade/tdOrder/list',
	deleteUrl: '/mgt/trade/tdOrder/delete',
	queryForm: {
		tdOdCode: '',
		tdOdSysUserCode: '',
		tdOdPaidStatusCode: '',
		createDateRangeArray: [] as string[]
	}
})

const statistics = reactive<OrderStatistics>({
	totalOrderAmount: 0,
	totalPaidAmount: 0,
	todayOrderAmount: 0,
	todayPaidAmount: 0
})
const statisticsLoading = ref(false)

const queryRef = ref()
const orderDetailRef = ref()

const formatAmount = (amount: unknown) => {
	if (amount === null || amount === undefined || amount === '') {
		return '-'
	}
	const n = Number(amount)
	return Number.isFinite(n) ? n.toFixed(2) : String(amount)
}

const loadStatistics = () => {
	statisticsLoading.value = true
	service
		.get('/mgt/trade/tdOrder/statistics')
		.then((res: any) => {
			Object.assign(statistics, res.data || {})
		})
		.finally(() => {
			statisticsLoading.value = false
		})
}

const viewDetailHandle = (id: string) => {
	orderDetailRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

const filterToday = () => {
	const today = new Date().toISOString().split('T')[0]
	state.queryForm.createDateRangeArray = [today, today]
	getDataList()
}

onMounted(() => {
	loadStatistics()
})
</script>

<style scoped>
.order-statistics {
	margin-bottom: 16px;
}

.stat-card {
	margin-bottom: 16px;
}

.stat-card__label {
	color: var(--el-text-color-secondary);
	font-size: 14px;
	margin-bottom: 8px;
}

.stat-card__value {
	font-size: 24px;
	font-weight: 600;
	line-height: 1.2;
}

.stat-card__value--success {
	color: var(--el-color-success);
}

.order-table :deep(.el-table__cell) {
	white-space: nowrap;
}
</style>
