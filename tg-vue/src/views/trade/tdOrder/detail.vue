<template>
	<el-dialog v-model="visible" title="订单详情" width="900px" :close-on-click-modal="false">
		<el-descriptions :column="2" border>
			<el-descriptions-item label="订单编号">{{ dataForm.tdOdCode || '-' }}</el-descriptions-item>
			<el-descriptions-item label="订单金额">{{ dataForm.tdOdAmount || '-' }}</el-descriptions-item>
			<el-descriptions-item label="退款金额">{{ dataForm.tdOdRefundAmount || '-' }}</el-descriptions-item>
			<el-descriptions-item label="支付状态">{{ dataForm.tdOdPaidCode || '-' }}</el-descriptions-item>
			<el-descriptions-item label="下单人账号">{{ dataForm.tdOdSysUserCode || '-' }}</el-descriptions-item>
			<el-descriptions-item label="下单人姓名">{{ dataForm.tdOdSysUserRealName || '-' }}</el-descriptions-item>
			<el-descriptions-item label="下单人电话">{{ dataForm.tdOdSysUserPhone || '-' }}</el-descriptions-item>
			<el-descriptions-item label="订单备注" :span="2">{{ dataForm.tdOdRemark || '-' }}</el-descriptions-item>
		</el-descriptions>

		<el-divider>订单明细（td_order_goods）</el-divider>
		<el-table v-loading="goodsLoading" :data="goodsList" border>
			<el-table-column prop="tdOdGdCode" label="订单商品编码" min-width="150" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdCode" label="商品编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdGdName" label="商品名称" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="tdOdGdNum" label="数量" width="90"></el-table-column>
			<el-table-column prop="tdGdPrice" label="单价" width="120"></el-table-column>
			<el-table-column prop="tdOdGdAmount" label="金额" width="120"></el-table-column>
			<el-table-column prop="tdOdPaidCode" label="支付状态" width="120"></el-table-column>
		</el-table>

		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import service from '@/utils/request'

const visible = ref(false)
const goodsLoading = ref(false)
const goodsList = ref<any[]>([])

const dataForm = reactive({
	id: '',
	tdOdCode: '',
	tdOdRemark: '',
	tdOdAmount: '',
	tdOdSysUserCode: '',
	tdOdSysUserPhone: '',
	tdOdSysUserRealName: '',
	tdOdPaidCode: '',
	tdOdRefundAmount: ''
})

const init = (id: string) => {
	visible.value = true
	Object.assign(dataForm, {
		id: '',
		tdOdCode: '',
		tdOdRemark: '',
		tdOdAmount: '',
		tdOdSysUserCode: '',
		tdOdSysUserPhone: '',
		tdOdSysUserRealName: '',
		tdOdPaidCode: '',
		tdOdRefundAmount: ''
	})
	goodsList.value = []

	service.get('/mgt/trade/tdOrder/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data || {})
		loadOrderGoods(res.data?.tdOdCode)
	})
}

const loadOrderGoods = (tdOdCode?: string) => {
	if (!tdOdCode) {
		goodsList.value = []
		return
	}
	goodsLoading.value = true
	service
		.get('/mgt/trade/tdOrderGoods/list', {
			params: {
				pageNo: 1,
				pageSize: 200,
				tdOdCode
			}
		})
		.then(res => {
			goodsList.value = res.data?.records || []
		})
		.finally(() => {
			goodsLoading.value = false
		})
}

defineExpose({
	init
})
</script>
