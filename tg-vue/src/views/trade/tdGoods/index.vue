<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="tdGdCode">
				<el-input v-model="state.queryForm.tdGdCode" placeholder="商品编码"></el-input>
			</el-form-item>
			<el-form-item prop="tdGdName">
				<el-input v-model="state.queryForm.tdGdName" placeholder="商品名称"></el-input>
			</el-form-item>
			<el-form-item prop="tdGdTag">
				<el-input v-model="state.queryForm.tdGdTag" placeholder="商品标签"></el-input>
			</el-form-item>
			<el-form-item prop="tdGdCgyCode">
				<el-select v-model="state.queryForm.tdGdCgyCode" placeholder="商品分类" clearable filterable style="width: 180px">
					<el-option v-for="item in categoryOptions" :key="item.tdGdCgyCode" :label="item.tdGdCgyName" :value="item.tdGdCgyCode" />
				</el-select>
			</el-form-item>
			<el-form-item prop="tdGdEnabledCode">
				<el-select v-model="state.queryForm.tdGdEnabledCode" placeholder="上架状态" clearable style="width: 120px">
					<el-option label="上架" value="1" />
					<el-option label="下架" value="0" />
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
		<el-space>
			<el-space>
				<el-button icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="tdGdCode" label="商品编码" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdName" label="商品名称" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdTag" label="商品标签" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdPrice" label="价格" header-align="center" align="center" width="100"></el-table-column>
			<el-table-column prop="tdGdDayPeriod" label="服务期(天)" header-align="center" align="center" width="100"></el-table-column>
			<el-table-column label="分佣比例" header-align="center" align="center" width="100">
				<template #default="scope">
					{{ formatCommissionRate(scope.row.tdGdCommissionRate) }}
				</template>
			</el-table-column>
			<el-table-column prop="tdGdCgyName" label="分类名称" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdInventoryNum" label="库存" header-align="center" align="center" width="100"></el-table-column>
			<el-table-column prop="tdGdEnabledCode" label="上架状态" header-align="center" align="center" width="100">
				<template #default="{ row }">
					<el-tag :type="isShelfOn(row.tdGdEnabledCode) ? 'success' : 'info'" size="small">
						{{ formatShelfText(row.tdGdEnabledCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="tdGdHiddenStatusCode" label="是否隐藏" header-align="center" align="center" width="90">
				<template #default="{ row }">
					<el-tag :type="row.tdGdHiddenStatusCode === '1' ? 'warning' : 'info'" size="small">
						{{ row.tdGdHiddenStatusCode === '1' ? '隐藏' : '显示' }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="280">
				<template #default="scope">
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
					<el-button
						v-if="!isShelfOn(scope.row.tdGdEnabledCode)"
						type="success"
						link
						@click="toggleShelfHandle(scope.row, '1')"
					>
						上架
					</el-button>
					<el-button
						v-else
						type="warning"
						link
						@click="toggleShelfHandle(scope.row, '0')"
					>
						下架
					</el-button>
					<el-button type="primary" link @click="openSalesDialog(scope.row)">查看销售数据</el-button>
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

		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>

		<el-dialog v-model="salesVisible" title="销售数据" width="980px" :close-on-click-modal="false">
			<el-descriptions :column="3" border>
				<el-descriptions-item label="商品编码">{{ currentGoods.tdGdCode || '-' }}</el-descriptions-item>
				<el-descriptions-item label="商品名称">{{ currentGoods.tdGdName || '-' }}</el-descriptions-item>
				<el-descriptions-item label="分类名称">{{ currentGoods.tdGdCgyName || '-' }}</el-descriptions-item>
			</el-descriptions>
			<el-table v-loading="salesLoading" :data="salesList" border class="layout-table" style="margin-top: 12px">
				<el-table-column prop="tdOdCode" label="订单编号" min-width="150" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="tdOdGdCode" label="订单商品编码" min-width="150" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="tdOdGdNum" label="数量" width="90" header-align="center" align="center"></el-table-column>
				<el-table-column prop="tdGdPrice" label="单价" width="110" header-align="center" align="center"></el-table-column>
				<el-table-column prop="tdOdGdAmount" label="金额" width="110" header-align="center" align="center"></el-table-column>
				<el-table-column prop="tdOdPaidCode" label="支付状态" width="110" header-align="center" align="center"></el-table-column>
				<el-table-column prop="tdOdSysUserRealName" label="下单人" min-width="120" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="tdOdSysUserPhone" label="下单人电话" min-width="130" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			</el-table>
			<el-pagination
				:current-page="salesPageNo"
				:page-size="salesPageSize"
				:total="salesTotal"
				layout="total, sizes, prev, pager, next, jumper"
				@size-change="salesSizeChangeHandle"
				@current-change="salesCurrentChangeHandle"
			>
			</el-pagination>
		</el-dialog>
	</el-card>
</template>

<script setup lang="ts" name="TradeTdGoodsIndex">
import { useCrud } from '@/hooks'
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/trade/tdGoods/list',
	deleteUrl: '/mgt/trade/tdGoods/delete',
	queryForm: {
		tdGdCode: '',
		tdGdName: '',
		tdGdTag: '',
		tdGdCgyCode: '',
		tdGdEnabledCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const categoryOptions = ref<{ tdGdCgyCode: string; tdGdCgyName: string }[]>([])
const salesVisible = ref(false)
const salesLoading = ref(false)
const salesList = ref<any[]>([])
const salesPageNo = ref(1)
const salesPageSize = ref(10)
const salesTotal = ref(0)
const currentGoods = reactive({
	tdGdCode: '',
	tdGdName: '',
	tdGdCgyName: ''
})

const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const formatCommissionRate = (rate?: number | string) => {
	const n = Number(rate)
	if (Number.isNaN(n))
		return '90%'
	return `${Math.round(n * 100)}%`
}

const isShelfOn = (val: unknown) => String(val ?? '1').trim() === '1'

const formatShelfText = (val: unknown) => {
	const v = String(val ?? '').trim()
	if (v === '1' || v === '')
		return '上架'
	if (v === '0')
		return '下架'
	return v
}

const toggleShelfHandle = async (row: any, tdGdEnabledCode: '0' | '1') => {
	if (!row?.id) {
		ElMessage.warning('商品记录无效')
		return
	}
	const actionText = tdGdEnabledCode === '1' ? '上架' : '下架'
	try {
		await ElMessageBox.confirm(`确认${actionText}商品「${row.tdGdName || row.tdGdCode}」？`, `${actionText}确认`, {
			type: 'warning'
		})
		const res: any = await service.post('/mgt/trade/tdGoods/updateShelf', null, {
			params: { id: row.id, tdGdEnabledCode }
		})
		ElMessage.success(res?.message || `${actionText}成功`)
		getDataList()
	}
	catch {
		/** 用户取消 */
	}
}

const getSalesList = async () => {
	if (!currentGoods.tdGdCode) return
	salesLoading.value = true
	try {
		const res = await service.get('/mgt/trade/tdOrderGoods/list', {
			params: {
				tdGdCode: currentGoods.tdGdCode,
				pageNo: salesPageNo.value,
				pageSize: salesPageSize.value
			}
		})
		salesList.value = res?.data?.records || []
		salesTotal.value = res?.data?.total || 0
	} finally {
		salesLoading.value = false
	}
}

const openSalesDialog = (row: any) => {
	currentGoods.tdGdCode = row.tdGdCode || ''
	currentGoods.tdGdName = row.tdGdName || ''
	currentGoods.tdGdCgyName = row.tdGdCgyName || ''
	salesPageNo.value = 1
	salesVisible.value = true
	getSalesList()
}

const salesSizeChangeHandle = (val: number) => {
	salesPageSize.value = val
	salesPageNo.value = 1
	getSalesList()
}

const salesCurrentChangeHandle = (val: number) => {
	salesPageNo.value = val
	getSalesList()
}

const getCategoryOptions = async () => {
	const res = await service.get('/mgt/trade/tdGoodsCategory/list', {
		params: {
			pageNo: 1,
			pageSize: 9999
		}
	})
	categoryOptions.value = res?.data?.records || []
}

onMounted(() => {
	getCategoryOptions()
})

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
