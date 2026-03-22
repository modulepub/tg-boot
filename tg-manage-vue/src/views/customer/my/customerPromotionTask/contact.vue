<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item prop="cusIdNo">
				<el-input v-model="state.queryForm.cusIdNo" placeholder="证件号"></el-input>
			</el-form-item>
      <el-form-item prop="cusIntentionStatusCode">
        <tg-dict-select
          v-model="state.queryForm.cusIntentionStatusCode"
          dict-code="cusIntentionStatusCode"
          clearable
          placeholder="是否意向"
        ></tg-dict-select>
      </el-form-item>
      <el-form-item prop="cusIntentionLevelCode">
        <tg-dict-select
          v-model="state.queryForm.cusIntentionLevelCode"
          dict-code="cusIntentionLevelCode"
          clearable
          placeholder="意向等级"
        ></tg-dict-select>
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
				<el-button icon="RefreshRight" @click="resetR(queryRef)">重置</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-tabs v-model="activeTab" @tab-change="handleTabChange">
			<el-tab-pane
				v-for="item in followUpStatusDict"
				:key="item.dictItemValue"
				:label="item.dictItemText"
				:name="item.dictItemValue"
			>
				<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
					<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
					<el-table-column prop="cusCode" label="客户编号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column prop="cusName" label="客户姓名" header-align="center"  align="center" show-overflow-tooltip></el-table-column>
					<el-table-column prop="cusPhone" label="手机号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column
						prop="cusIdNo"
						label="证件号"
						header-align="center"
						width="100"
						align="center"
						show-overflow-tooltip
					></el-table-column>
					<tg-dict-column prop="promotionTaskTypeCode" label="任务类型"  dict-code="promotionTaskTypeCode"></tg-dict-column>
					<tg-dict-column prop="cusFollowUpStatusCode" label="是否跟进" dict-code="cusFollowUpStatusCode"></tg-dict-column>
					<tg-dict-column prop="cusDealtStatusCode" label="成交状态"  dict-code="cusDealtStatusCode"></tg-dict-column>
					<tg-dict-column prop="cusIntentionStatusCode" label="是否意向"  dict-code="cusIntentionStatusCode"></tg-dict-column>
					<tg-dict-column prop="cusIntentionLevelCode" label="意向等级"  dict-code="cusIntentionLevelCode"></tg-dict-column>
					<el-table-column prop="createTime" label="分配时间"  header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column label="操作" fixed="right" header-align="center" align="center" width="280">
						<template #default="scope">
							<el-button type="primary" link @click="contactReportHandle(scope.row)">联络报告</el-button>
							<el-button type="primary" link @click="reportDetailHandle(scope.row.cusCode)">联络记录</el-button>
							<el-button v-if="scope.row.cusDealtStatusCode == '0'" type="primary" link @click="dealtHandle(scope.row.promotionTaskCode)">成交</el-button>
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
			</el-tab-pane>
		</el-tabs>

		<!-- 弹窗, 新增 / 修改 -->
		<report-modal ref="reportModal" @refresh-data-list="getDataList"></report-modal>
		<report-detail-modal ref="reportDetailModal"></report-detail-modal>
	</el-card>
</template>

<script setup lang="ts" name="CustomercustomerIndex">
import { useCrud } from '@/hooks'
import { reactive, ref, computed, onMounted } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import ReportModal from '../components/reportModal.vue'
import ReportDetailModal from '../components/reportDetailModal.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import { useAppStore, Dict } from '@/store/modules/app'

const appStore = useAppStore()
const activeTab = ref('')

// 获取是否跟进字典数据
const followUpStatusDict = computed(() => {
	const dict = appStore.dictList.find((item: Dict) => item.dictCode === 'cusFollowUpStatusCode')
	return dict ? dict.dictItemList : []
})

const state: IHooksOptions = reactive({
	dataListUrl: '/cus/customer/customerPromotionTask/myCusList',
	deleteUrl: '/mgt/customer/customer/delete',
	queryForm: {
		cusPhone: '',
		cusSourceCode: '',
		cusTagCode: '',
		cusLevelCode: '',
		cusIntentionStatusCode: '',
		cusDesc: '',
		cusDemand: '',
		cusPoolStatusCode: '',
		cusUserCode: '',
    promotionTaskTypeCode:'contact',
		createDateRangeArray: []
	}
})
const queryRef = ref()
const reportModal = ref()
const reportDetailModal = ref()

// 处理tab切换
const handleTabChange = (tabName: string) => {
	state.queryForm.cusFollowUpStatusCode = tabName
	state.pageNo = 1
	getDataList()
}

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
				promotionTaskCode: code
			}
			service.post('/cus/customer/customerPromotionTask/complete', data).then(() => {
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
        promotionTaskCode: code
			}
			service.post('/cus/customer/customerPromotionTask/dealt', data).then(() => {
				ElMessage.success('操作成功')
				getDataList()
			})
		})
		.catch(() => {})
}
const resetR= (queryRef: any) => {
  queryRef.resetFields()
  state.queryForm = {}
  state.queryForm.promotionTaskTypeCode = 'contact'
  state.queryForm.cusFollowUpStatusCode = activeTab.value
  getDataList()

}
const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

// 组件挂载时获取字典数据并初始化activeTab
onMounted(() => {
	// 确保字典数据已加载
	const loadDictAndInit = async () => {
		if (appStore.dictList.length === 0) {
			await appStore.getDictListAction()
		}

		// 字典数据加载完成后，默认选中第一项
		if (followUpStatusDict.value.length > 0) {
			activeTab.value = followUpStatusDict.value[0].dictItemValue
			// 初始化查询条件
			state.queryForm.cusFollowUpStatusCode = activeTab.value
			// 重置页码并获取数据
			state.pageNo = 1
			getDataList()
		}
	}

	loadDictAndInit()
})
</script>
