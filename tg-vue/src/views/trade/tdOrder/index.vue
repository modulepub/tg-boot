<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="tdOdCode">
				<el-input v-model="state.queryForm.tdOdCode" placeholder="订单编号"></el-input>
			</el-form-item>
			<el-form-item prop="tdOdSysUserCode">
				<el-input v-model="state.queryForm.tdOdSysUserCode" placeholder="下单人账号"></el-input>
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
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="tdOdCode" label="订单编号" min-width="150" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdAmount" label="订单金额" width="110" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdOdRefundAmount" label="退款金额" width="110" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdOdPaidCode" label="支付状态" width="110" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdOdSysUserCode" label="下单人账号" width="130" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserRealName" label="下单人姓名" width="110" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdSysUserPhone" label="下单人电话" width="130" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="180">
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
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import OrderDetail from './detail.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/trade/tdOrder/list',
	deleteUrl: '/mgt/trade/tdOrder/delete',
	queryForm: {
		tdOdCode: '',
		tdOdSysUserCode: ''
	}
})

const queryRef = ref()
const orderDetailRef = ref()

const viewDetailHandle = (id: string) => {
	orderDetailRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
