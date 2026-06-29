<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="mkCompanyCode">
				<el-input v-model="state.queryForm.mkCompanyCode" placeholder="公司编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkCompanyName">
				<el-input v-model="state.queryForm.mkCompanyName" placeholder="公司名称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="distSettledStatusCode">
				<el-select v-model="state.queryForm.distSettledStatusCode" placeholder="结算状态" clearable style="width: 120px">
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
			<el-table-column prop="distSettleBatchCode" label="批次编码" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyCode" label="公司编码" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyName" label="公司名称" min-width="140" show-overflow-tooltip>
				<template #default="scope">
					<el-button
						v-if="scope.row.mkCompanyCode"
						type="primary"
						link
						@click="openCompanyDetail(scope.row.mkCompanyCode)"
					>
						{{ scope.row.mkCompanyName || scope.row.mkCompanyCode }}
					</el-button>
					<span v-else>{{ scope.row.mkCompanyName || '—' }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="distApplyAt" label="申请日期" min-width="160"></el-table-column>
			<el-table-column prop="distSettleTotalAmount" label="结算总金额" width="120" align="right"></el-table-column>
			<el-table-column prop="distSettledStatusCode" label="是否结算完成" width="120" align="center">
				<template #default="scope">
					<el-tag v-if="isSettledYes(scope.row.distSettledStatusCode)" type="success">已完成</el-tag>
					<el-tag v-else type="warning">未完成</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="distSettledAt" label="完成时间" min-width="160"></el-table-column>
			<el-table-column prop="mkCompanyAdminUserCode" label="申请管理员" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" width="100" align="center">
				<template #default="scope">
					<el-button
						v-if="!isSettledYes(scope.row.distSettledStatusCode)"
						type="primary"
						link
						@click="completeHandle(scope.row)"
					>
						完成
					</el-button>
					<span v-else class="done-text">—</span>
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
		/>
	</el-card>

	<company-detail-dialog ref="companyDetailDialogRef" />
</template>

<script setup lang="ts" name="DistributionDistSettleBatchIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import CompanyDetailDialog from './company-detail-dialog.vue'
import { IHooksOptions } from '@/hooks/interface'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/distribution/distSettleBatch/list',
	primaryKey: 'id',
	queryForm: {
		mkCompanyCode: '',
		mkCompanyName: '',
		distSettledStatusCode: ''
	}
})

const queryRef = ref()
const companyDetailDialogRef = ref<InstanceType<typeof CompanyDetailDialog>>()
const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

const openCompanyDetail = (mkCompanyCode: string) => {
	companyDetailDialogRef.value?.init(mkCompanyCode)
}

const isSettledYes = (code: unknown) => {
	const value = String(code ?? '').trim().toUpperCase()
	return value === '1' || value === 'YES'
}

const completeHandle = async (row: Record<string, string>) => {
	const id = row?.id
	const distSettleBatchCode = row?.distSettleBatchCode
	if (!id && !distSettleBatchCode) {
		ElMessage.warning('缺少批次标识，无法完成结算')
		return
	}
	try {
		await ElMessageBox.confirm('确认将该批次标记为结算完成？', '提示', {
			type: 'warning',
			confirmButtonText: '确定',
			cancelButtonText: '取消'
		})
		await service.post('/mgt/distribution/distSettleBatch/complete', null, {
			params: {
				id: id || undefined,
				distSettleBatchCode: distSettleBatchCode || undefined
			}
		})
		ElMessage.success('操作成功')
		getDataList()
	} catch {
		// 用户取消或请求失败（失败提示由 request 拦截器处理）
	}
}
</script>

<style scoped>
.done-text {
	color: #94a3b8;
}
</style>
