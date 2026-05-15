<template>
	<el-card style="margin-bottom: 10px">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名（%模糊搜索%）"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="手机号（%模糊搜索%）"></el-input>
			</el-form-item>
			<el-form-item prop="cusIdNo">
				<el-input v-model="state.queryForm.cusIdNo" placeholder="证件号（%模糊搜索%）"></el-input>
			</el-form-item>
			<el-form-item prop="cusSourceCode">
				<el-input v-model="state.queryForm.cusSourceCode" placeholder="客户来源（%模糊搜索%）"></el-input>
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
			<el-form-item prop="cusPoolStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusAssignSalesStatusCode"
					dict-code="cusAssignSalesStatusCode"
					clearable
					placeholder="是否分配销售人员"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusPoolStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusAssignServersStatusCode"
					dict-code="cusAssignServersStatusCode"
					clearable
					placeholder="是否分配服务人员"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusDealtCompleteStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusDealtCompleteStatusCode"
					dict-code="cusDealtCompleteStatusCode"
					clearable
					placeholder="是否完单"
				></tg-dict-select>
			</el-form-item>
			<el-form-item>
				<el-button icon="Search" type="primary" @click="getDataList()">查询</el-button>
			</el-form-item>
			<el-form-item>
				<el-button icon="RefreshRight" @click="reset(queryRef)">重置</el-button>
			</el-form-item>
		</el-form>
		<el-space style="margin-top: 10px">
			<el-space>
				<tg-excel-import
					push="http://127.0.0.1:9999/mgt/customer/customer/add"
					template="https://matchlove.oss-cn-beijing.aliyuncs.com/%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5.xlsx"
				>
					导入
				</tg-excel-import>
			</el-space>
			<el-space>
				<el-button icon="Plus" plain type="primary" @click="inPoolBatchHandle()">入库</el-button>
			</el-space>

			<el-space>
				<el-button icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button v-auth="'customerCustomerDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="cusCode" label="客户编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip>
				<template #default="scope">
					<el-button type="primary" link @click="kycHandle(scope.row.id)">{{ scope.row.cusName }}</el-button>
				</template>
			</el-table-column>
			<el-table-column prop="cusPhone" label="手机号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusIdNo" label="证件号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSourceCode" label="客户来源" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<tg-dict-column prop="cusTagCode" label="用户标签" dict-code="cusTagCode" show-overflow-tooltip></tg-dict-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="250">
				<template #default="scope">
					<el-button v-auth="'customerCustomerDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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
		<!-- KYC弹窗 -->
		<kyc ref="kycRef"></kyc>
		<!-- 用户选择弹窗 -->
		<tg-user-dialog
			v-if="userSelectManagerVisible"
			v-model:visible="userSelectManagerVisible"
			:multiple="false"
			@select="assignDoneHandle"
		></tg-user-dialog>
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive, ref, computed, onMounted } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import Kyc from './kyc.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import TgUserDialog from '@/components/tg-user/tg-user-dialog/index.vue'
const state: IHooksOptions = reactive({
	createdIsNeed: false,
	dataListUrl: '/mgt/customer/customer/list',
	deleteUrl: '/mgt/customer/customer/delete',
	queryForm: {
		seqNo: '',
		orgCode: '',
		version: '',
		deleted: '',
		cusLifePhoto: '',
		cusName: '',
		cusIdNo: '',
		cusIdentityAuthenticatedStatusCode: '',
		cusSexCode: '',
		cusAge: '',
		cusHeight: '',
		cusWeight: '',
		cusMaritalStatusCode: '',
		cusHandholdsNum: '',
		cusCityResidenceCode: '',
		cusHaveCarStatusCode: '',
		cusVehicleLicensePhoto: '',
		cusHaveHouseStatusCode: '',
		cusRealEstateCertificatePhoto: '',
		cusOccupationalDescription: '',
		cusAnnualIncomeAmount: '',
		cusAnnualIncomeAuthenticatedPhoto: '',
		cusPhone: '',
		cusSourceCode: '',
		cusTagCode: '',
		cusLevelCode: '',
		cusIntentionStatusCode: '',
		cusDesc: '',
		cusDemand: '',
		cusPoolStatusCode: '0',
		userCode: ''
	},
	queryFormReset: {
		cusPoolStatusCode: '0'
	}
})
const inPoolBatchHandle = (key?: any[]) => {
	let data: any[] = []
	if (key) {
		data = [key]
	} else {
		data = state.dataListSelectionKeys ? state.dataListSelectionKeys : []

		if (data.length === 0) {
			ElMessage.warning('请选择入库的记录')
			return
		}
	}

	ElMessageBox.confirm('确定进行入库操作?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			service.post('/mgt/customer/customer/inPool', data).then(() => {
				ElMessage.success('入库成功')
				getDataList()
			})
		})
		.catch(() => {})
}
const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

// KYC处理函数
const kycRef = ref()
const kycHandle = (id: number) => {
	kycRef.value.init(id)
}
// 用户选择弹窗状态
const userSelectManagerVisible = ref(false)
// 待分配的客户编码列表
const pendingAssignCusCodes = ref<any[]>([])
let pendingPromotionTaskTypeCode = 'contact'
//处理器
const assignHandle = (promotionTaskTypeCode: any) => {
	pendingPromotionTaskTypeCode = promotionTaskTypeCode
	let data: any[] = []
	data = state.dataListSelectionKeys ? state.dataListSelectionKeys : []

	if (data.length === 0) {
		ElMessage.warning('请选择客户')
		return
	}
	// 保存待分配的客户编码
	for (let item of state.dataListSelections as any) {
		pendingAssignCusCodes.value.push(item.cusCode)
	}
	// 打开用户选择弹窗
	userSelectManagerVisible.value = true
}
const assignDoneHandle = (users: any[]) => {
	if (users.length === 0) {
		ElMessage.warning('请选择管户人员')
		return
	}

	ElMessageBox.confirm('确定进行分配操作?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			// 提取选中用户的id作为userCodeList
			const userCodeList = users.map((user: any) => user.userCode)

			let postData = {
				cusCodeList: pendingAssignCusCodes.value,
				userCodeList: userCodeList,
				promotionTaskTypeCode: pendingPromotionTaskTypeCode
			}

			service.post('/mgt/customer/customerPromotionTask/assign', postData).then(() => {
				ElMessage.success('分配成功')
				getDataList()
				pendingAssignCusCodes.value = []
			})
		})
		.catch(() => {})
}
const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

onMounted(() => {
	const loadDictAndInit = async () => {
		state.queryForm.cusPoolStatusCode = '0'
		state.pageNo = 1
		getDataList()
	}

	loadDictAndInit()
})

defineExpose({
	reset: () => {
		state.dataListSelectionKeys = []
		if (queryRef.value) {
			reset(queryRef.value)
		}
	}
})
</script>
