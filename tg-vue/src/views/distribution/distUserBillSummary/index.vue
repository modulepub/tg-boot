<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="distUserCode">
				<el-input v-model="state.queryForm.distUserCode" placeholder="用户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distUserNickName">
				<el-input v-model="state.queryForm.distUserNickName" placeholder="用户昵称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distInviterUserCode">
				<el-input v-model="state.queryForm.distInviterUserCode" placeholder="邀请人编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distBizLineCode">
				<el-input v-model="state.queryForm.distBizLineCode" placeholder="业务线" clearable></el-input>
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
			<el-table-column prop="distUserBillSummaryCode" label="汇总编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distUserCode" label="用户编码" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distUserNickName" label="用户昵称" min-width="100"></el-table-column>
			<el-table-column prop="distUserRealName" label="用户真实姓名" min-width="100"></el-table-column>
			<el-table-column prop="distInviterUserCode" label="邀请人编码" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column prop="distInviterUserNickName" label="邀请人昵称" min-width="100"></el-table-column>
			<el-table-column prop="distInviterUserRealName" label="邀请人真实姓名" min-width="110"></el-table-column>
			<el-table-column prop="distBizLineCode" label="业务线" width="88"></el-table-column>
			<el-table-column prop="distPaidTotalAmount" label="付费总金额" width="110" align="right"></el-table-column>
			<el-table-column prop="distInServiceTotalAmount" label="服务期内总金额" width="130" align="right"></el-table-column>
			<el-table-column prop="distSubPaidTotalAmount" label="子级付费总金额" width="130" align="right"></el-table-column>
			<el-table-column prop="distSubInServiceTotalAmount" label="子级服务期内" width="120" align="right"></el-table-column>
			<el-table-column prop="createTime" label="创建时间" min-width="160"></el-table-column>
			<el-table-column prop="updateTime" label="更新时间" min-width="160"></el-table-column>
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

<script setup lang="ts" name="DistributionDistUserBillSummaryIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/distribution/distUserBillSummary/list',
	primaryKey: 'id',
	queryForm: {
		distUserCode: '',
		distUserNickName: '',
		distInviterUserCode: '',
		distBizLineCode: ''
	}
})

const queryRef = ref()
const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
