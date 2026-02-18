<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="手机号"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="是否意向"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="意向等级"></el-input>
			</el-form-item>
			<el-form-item prop="cusIdCardNum">
				<el-input v-model="state.queryForm.cusIdCardNum" placeholder="身份证号"></el-input>
			</el-form-item>
			<el-form-item prop="cusUserCode">
				<el-input v-model="state.queryForm.cusUserCode" placeholder="客户用户号"></el-input>
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
		</el-form>
	</el-card>

	<el-card>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="cusCode" label="客户编号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusPhone" label="手机号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusIdCardNum" label="身份证号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<tg-dict-column prop="cusFollowUpStatusCode" label="是否跟进" dict-code="cusFollowUpStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusDealtStatusCode" label="成交状态" dict-code="cusDealtStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusDealtCompleteStatusCode" label="完单状态" dict-code="cusDealtCompleteStatusCode"></tg-dict-column>
			<el-table-column prop="createTime" label="分配时间" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="280">
				<template #default="scope">
					<el-button type="primary" link @click="contactReportHandle(scope.row)">联络报告</el-button>
					<el-button type="primary" link @click="reportDetailHandle(scope.row.cusCode)">联络记录</el-button>
					<el-button v-if="scope.row.cusDealtStatusCode == '0'" type="primary" link @click="dealtHandle(scope.row.promotionRelCode)">成交</el-button>
					<el-button v-if="scope.row.cusDealtCompleteStatusCode == '0'" type="primary" link @click="completeHandle(scope.row.promotionRelCode)"
						>完单</el-button
					>
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

		<!-- 弹窗, 新增 / 修改 -->
		<report-modal ref="reportModal" @refresh-data-list="getDataList"></report-modal>
		<report-detail-modal ref="reportDetailModal"></report-detail-modal>
	</el-card>
</template>

<script setup lang="ts" name="CustomercustomerIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import ReportModal from '../components/reportModal.vue'
import ReportDetailModal from '../components/reportDetailModal.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/cus/customer/customerPromotionRelation/myCusList',
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
		cusUserCode: '',
		createDateRangeArray: []
	}
})
const queryRef = ref()
const reportModal = ref()
const reportDetailModal = ref()
const contactReportHandle = (form: any) => {
	reportModal.value.report(form)
}
const reportDetailHandle = (cusCode: string) => {
	reportDetailModal.value.init(cusCode)
}

const completeHandle = (code: string) => {
	ElMessageBox.confirm('确定进行完单操作?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			let data = {
				promotionRelCode: code
			}
			service.post('/cus/customer/customerPromotionRelation/complete', data).then(() => {
				ElMessage.success('操作成功')
				getDataList()
			})
		})
		.catch(() => {})
}
const dealtHandle = (code: string) => {
	ElMessageBox.confirm('确定进行成交操作?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			let data = {
				promotionRelCode: code
			}
			service.post('/cus/customer/customerPromotionRelation/dealt', data).then(() => {
				ElMessage.success('操作成功')
				getDataList()
			})
		})
		.catch(() => {})
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>
