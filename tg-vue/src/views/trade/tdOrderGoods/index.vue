<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="tdOdCode">
				<el-input v-model="state.queryForm.tdOdCode" placeholder="订单编号"></el-input>
			</el-form-item>
			<el-form-item prop="tdGdCode">
				<el-input v-model="state.queryForm.tdGdCode" placeholder="商品编码"></el-input>
			</el-form-item>
			<el-form-item prop="tdGdName">
				<el-input v-model="state.queryForm.tdGdName" placeholder="商品名称"></el-input>
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
			<el-table-column prop="tdOdCode" label="订单编号" min-width="150" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdGdCode" label="订单商品编码" min-width="150" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdCode" label="商品编码" min-width="120" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdName" label="商品名称" min-width="130" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdGdNum" label="数量" width="90" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdGdPrice" label="单价" width="110" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdOdGdAmount" label="金额" width="110" header-align="center" align="center"></el-table-column>
			<el-table-column prop="tdOdPaidCode" label="支付状态" width="110" header-align="center" align="center"></el-table-column>
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
	</el-card>
</template>

<script setup lang="ts" name="TradeTdOrderGoodsIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/trade/tdOrderGoods/list',
	queryForm: {
		tdOdCode: '',
		tdGdCode: '',
		tdGdName: ''
	}
})

const queryRef = ref()

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
