<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="手机号"></el-input>
			</el-form-item>
			<el-form-item prop="cusIdCardNum">
				<el-input v-model="state.queryForm.cusIdCardNum" placeholder="身份证号"></el-input>
			</el-form-item>
			<el-form-item prop="cusSourceCode">
				<el-input v-model="state.queryForm.cusSourceCode" placeholder="客户来源"></el-input>
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
	</el-card>

	<el-card>
		<el-tabs v-model="activeTab" @tab-change="handleTabChange">
			<el-tab-pane v-for="item in poolStatusDict" :key="item.dictItemValue" :label="item.dictItemText" :name="item.dictItemValue"></el-tab-pane>
		</el-tabs>
		<el-space style="margin-top: 10px">
			<template v-if="poolStatusDict && poolStatusDict.find(item => item.dictItemValue === activeTab)?.dictItemValue === '0'">
				<el-space>
					<tg-excel-upload
						push="/mgt/customer/customer/add"
						template="https://matchlove.oss-cn-beijing.aliyuncs.com/%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5.xlsx"
					>
						导入
					</tg-excel-upload>
				</el-space>
				<el-space>
					<tg-excel-download
						:data="`${state.dataListUrl}?pageNo=${state.pageNo}&pageSize=${state.pageSize}`"
						template="https://matchlove.oss-cn-beijing.aliyuncs.com/exportTemplate.xlsx"
					>
						导出
					</tg-excel-download>
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
			</template>
			<template v-if="poolStatusDict && poolStatusDict.find(item => item.dictItemValue === activeTab)?.dictItemValue === '1'">
				<el-space>
					<el-button v-auth="'assignPromotionPeople'" icon="Plus" plain type="primary" @click="assignHandle('contactCustomer')"
						>分配销售人员</el-button
					>
				</el-space>
				<el-space>
					<el-button v-auth="'asignServerPeople'" icon="Plus" plain type="primary" @click="assignHandle('serveCustomers')">分配服务人员</el-button>
				</el-space>
			</template>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="cusCode" label="客户编号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip>
				<template #default="scope">
					<el-button type="primary" link @click="kycHandle(scope.row.id)">{{ scope.row.cusName }}</el-button>
				</template>
			</el-table-column>
			<el-table-column prop="cusPhone" label="手机号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusIdCardNum" label="身份证号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSourceCode" label="客户来源" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<tg-dict-column prop="cusTagCode" label="用户标签" dict-code="cusTagCode" show-overflow-tooltip></tg-dict-column>
			<tg-dict-column prop="cusIntentionStatusCode" label="是否意向" dict-code="cusIntentionStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusFollowUpStatusCode" label="跟进状态" dict-code="cusFollowUpStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusAssignSalesStatusCode" label="销售人员" dict-code="cusAssignSalesStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusAssignServersStatusCode" label="服务人员" dict-code="cusAssignServersStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusDealtStatusCode" label="是否成交" dict-code="cusDealtStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusDealtCompleteStatusCode" label="是否完单" dict-code="cusDealtCompleteStatusCode"></tg-dict-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="250">
				<template #default="scope">
					<el-button type="primary" link @click="kycHandle(scope.row.id)">KYC视图</el-button>
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
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

<script setup lang="ts" name="CustomerCustomerIndex">
import { useCrud } from '@/hooks'
import { reactive, ref, computed, onMounted } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import Kyc from './kyc.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import TgUserDialog from '@/components/tg-user/tg-user-dialog/index.vue'
import { useAppStore, Dict } from '@/store/modules/app'

const appStore = useAppStore()
const activeTab = ref('')

// 获取入库状态字典数据
const poolStatusDict = computed(() => {
	const dict = appStore.dictList.find((item: Dict) => item.dictCode === 'cusPoolStatusCode')
	return dict ? dict.dictItemList : []
})
console.log('poolStatusDict:', poolStatusDict.value)
const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/customer/customer/list',
	deleteUrl: '/mgt/customer/customer/delete',
	queryForm: {
		seqNo: '',
		orgCode: '',
		version: '',
		deleted: '',
		cusLifePhoto: '',
		cusName: '',
		cusIdCardNum: '',
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
		cusPoolStatusCode: '',
		cusUserCode: ''
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
let pendingPromotionTaskCode = 'serveCustomers'
//处理器
const assignHandle = (promotionTaskCode: any) => {
	pendingPromotionTaskCode = promotionTaskCode
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
				promotionTaskCode: pendingPromotionTaskCode
			}

			service.post('/mgt/customer/customerPromotionRelation/assign', postData).then(() => {
				ElMessage.success('分配成功')
				getDataList()
			})
		})
		.catch(() => {})
}

const handleTabChange = (tab: string) => {
	// 直接使用标签页的name作为查询条件，因为name已经设置为字典值
	state.queryForm.cusPoolStatusCode = tab
	// 重置页码并重新获取数据
	state.pageNo = 1
	getDataList()
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

// 组件挂载时获取字典数据并初始化activeTab
onMounted(() => {
	// 确保字典数据已加载
	const loadDictAndInit = async () => {
		if (appStore.dictList.length === 0) {
			await appStore.getDictListAction()
		}

		// 字典数据加载完成后，默认选中第一项
		if (poolStatusDict.value.length > 0) {
			activeTab.value = poolStatusDict.value[0].dictItemValue
			// 初始化查询条件
			state.queryForm.cusPoolStatusCode = activeTab.value
			// 重置页码并获取数据
			state.pageNo = 1
			getDataList()
		}
	}

	loadDictAndInit()
})
</script>
