<template>
	<el-dialog v-model="dialogVisible" title="KYC视图" width="80%">
		<el-tabs @tab-click="handleTabClick">
			<el-tab-pane label="基本信息">
				<el-form v-if="customerInfo" label-width="120px" :inline="true">
					<el-form-item label="客户姓名" style="width: 250px">
						{{ customerInfo.cusName }}
					</el-form-item>
					<el-form-item label="手机号" style="width: 250px">
						{{ customerInfo.cusPhone }}
					</el-form-item>
					<el-form-item label="证件号" style="width: 300px">
						{{ customerInfo.cusIdNo }}
					</el-form-item>
					<el-form-item label="客户来源" style="width: 250px">
						{{ getDictText('cusSourceCode', customerInfo.cusSourceCode) }}
					</el-form-item>
					<el-form-item label="用户标签" style="width: 250px">
						{{ getDictText('cusTagCode', customerInfo.cusTagCode) }}
					</el-form-item>
					<el-form-item label="是否意向" style="width: 250px">
						{{ getDictText('cusIntentionStatusCode', customerInfo.cusIntentionStatusCode) }}
					</el-form-item>
					<el-form-item label="意向等级" style="width: 250px">
						{{ getDictText('cusIntentionLevelCode', customerInfo.cusIntentionLevelCode) }}
					</el-form-item>
					<el-form-item label="跟进状态" style="width: 250px">
						{{ getDictText('cusFollowUpStatusCode', customerInfo.cusFollowUpStatusCode) }}
					</el-form-item>
					<el-form-item label="入库状态" style="width: 250px">
						{{ getDictText('cusPoolStatusCode', customerInfo.cusPoolStatusCode) }}
					</el-form-item>
					<el-form-item label="销售人员" style="width: 250px">
						{{ getDictText('cusAssignSalesStatusCode', customerInfo.cusAssignSalesStatusCode) }}
					</el-form-item>
					<el-form-item label="服务人员" style="width: 250px">
						{{ getDictText('cusAssignServersStatusCode', customerInfo.cusAssignServersStatusCode) }}
					</el-form-item>
					<el-form-item label="是否成交" style="width: 250px">
						{{ getDictText('cusDealtStatusCode', customerInfo.cusDealtStatusCode) }}
					</el-form-item>
					<el-form-item label="是否完单" style="width: 250px">
						{{ getDictText('cusDealtCompleteStatusCode', customerInfo.cusDealtCompleteStatusCode) }}
					</el-form-item>
				</el-form>
				<el-alert v-else type="info" title="加载中..." :closable="false" />
			</el-tab-pane>
			<el-tab-pane label="联络记录">
				<el-card>
					<el-table v-loading="contactRecordLoading" :data="contactRecordList" border>
						<el-table-column
							prop="contactRecordCode"
							width="100"
							label="记录编号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column width="100" prop="cusCode" label="客户编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column width="100" prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column
							width="100"
							prop="cusPhone"
							label="客户手机号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="100"
							prop="userCode"
							label="员工账号"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							prop="userRealName"
							label="员工姓名"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="120"
							prop="contactRecordTalkDuration"
							label="通话时长（s)"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<tg-file-column  prop="contactRecordFile" label="录音文件" header-align="center" align="center"></tg-file-column>
						<el-table-column
							width="100"
							prop="contactRecordVoiceText"
							label="通话文字"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column
							width="100"
							prop="contactRecordDescription"
							label="跟踪描述"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<tg-dict-column width="100" prop="cusIntentionStatusCode" label="是否意向" dict-code="cusIntentionStatusCode"></tg-dict-column>
						<tg-dict-column width="100" prop="cusIntentionLevelCode" label="意向等级" dict-code="cusIntentionLevelCode"></tg-dict-column>
						<el-table-column prop="contactRecordTime" label="通话时间" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					</el-table>
					<el-pagination
						v-if="contactRecordTotal > 0"
						:current-page="contactRecordPageNo"
						:page-size="contactRecordPageSize"
						:total="contactRecordTotal"
						layout="total, sizes, prev, pager, next, jumper"
						@size-change="handleContactRecordSizeChange"
						@current-change="handleContactRecordCurrentChange"
					>
					</el-pagination>
				</el-card>
			</el-tab-pane>
			<el-tab-pane label="营销记录">
				<el-card>
					<el-table v-loading="promotionRelationLoading" :data="promotionRelationList" border>
						<el-table-column
							prop="promotionTaskCode"
							label="任务编码"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<el-table-column prop="userCode" label="员工号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
						<el-table-column
							prop="userRealName"
							label="员工姓名"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
						<tg-dict-column prop="promotionTaskTypeCode" label="任务类型"dict-code="promotionTaskTypeCode"></tg-dict-column>
						<el-table-column
							prop="createTime"
							label="分配时间"
							header-align="center"
							align="center"
							show-overflow-tooltip
						></el-table-column>
					</el-table>
					<el-pagination
						v-if="promotionRelationTotal > 0"
						:current-page="promotionRelationPageNo"
						:page-size="promotionRelationPageSize"
						:total="promotionRelationTotal"
						layout="total, sizes, prev, pager, next, jumper"
						@size-change="handlePromotionRelationSizeChange"
						@current-change="handlePromotionRelationCurrentChange"
					>
					</el-pagination>
				</el-card>
			</el-tab-pane>
		</el-tabs>
	</el-dialog>
</template>

<script setup lang="ts" name="CustomerKyc">
import { ref, reactive, onMounted } from 'vue'
import service from '@/utils/request'
import { useAppStore } from '@/store/modules/app'
import { getDictDataList } from '@/utils/tool'

const appStore = useAppStore()

const dialogVisible = ref(false)
const customerId = ref<number | undefined>(undefined)
const customerInfo = ref<any>(null)

// 获取字典文本
const getDictText = (dictCode: string, dictValue: string) => {
	if (!dictValue) {
		return ''
	}

	// 处理逗号分隔的多选字典值
	const dictValues = dictValue.split(',')
	const dictList = getDictDataList(appStore.dictList, dictCode)

	const dictTexts = dictValues.map(value => {
		const dictItem = dictList.find((item: any) => item.dictItemValue === value)
		return dictItem ? dictItem.dictItemText : value
	})

	return dictTexts.join(', ')
}

// 联络记录相关
const contactRecordList = ref<any[]>([])
const contactRecordLoading = ref(false)
const contactRecordPageNo = ref(1)
const contactRecordPageSize = ref(10)
const contactRecordTotal = ref(0)

// 营销记录相关
const promotionRelationList = ref<any[]>([])
const promotionRelationLoading = ref(false)
const promotionRelationPageNo = ref(1)
const promotionRelationPageSize = ref(10)
const promotionRelationTotal = ref(0)

// 初始化方法
const init = (id: number) => {
	customerId.value = id
	dialogVisible.value = true
	customerInfo.value = null

	// 先加载客户基本信息，然后再加载联络记录和营销记录
	loadCustomerInfo().then(() => {
		loadContactRecordList()
		loadPromotionRelationList()
	})
}

// 加载客户基本信息
const loadCustomerInfo = () => {
	return new Promise(resolve => {
		if (customerId.value) {
			service.get('/mgt/customer/customer/queryById?id=' + customerId.value).then((res: any) => {
				customerInfo.value = res.data
				resolve(res.data)
			})
		} else {
			resolve(null)
		}
	})
}

// 加载联络记录
const loadContactRecordList = () => {
	if (customerInfo.value && customerInfo.value.cusCode) {
		contactRecordLoading.value = true
		service
			.get('/mgt/customer/customerContactRecord/list', {
				params: {
					pageNo: contactRecordPageNo.value,
					pageSize: contactRecordPageSize.value,
					cusCode: customerInfo.value.cusCode
				}
			})
			.then((res: any) => {
				contactRecordList.value = res.data.records || res.data.list || []
				contactRecordTotal.value = res.data.total
				contactRecordLoading.value = false
			})
	}
}

// 加载营销记录
const loadPromotionRelationList = () => {
	if (customerInfo.value && customerInfo.value.cusCode) {
		promotionRelationLoading.value = true
		service
			.get('/mgt/customer/customerPromotionTask/list', {
				params: {
					pageNo: promotionRelationPageNo.value,
					pageSize: promotionRelationPageSize.value,
					cusCode: customerInfo.value.cusCode
				}
			})
			.then((res: any) => {
				promotionRelationList.value = res.data.records || res.data.list || []
				promotionRelationTotal.value = res.data.total
				promotionRelationLoading.value = false
			})
	}
}

// 联络记录分页处理
const handleContactRecordSizeChange = (size: number) => {
	contactRecordPageSize.value = size
	loadContactRecordList()
}

const handleContactRecordCurrentChange = (current: number) => {
	contactRecordPageNo.value = current
	loadContactRecordList()
}

// 营销记录分页处理
const handlePromotionRelationSizeChange = (size: number) => {
	promotionRelationPageSize.value = size
	loadPromotionRelationList()
}

const handlePromotionRelationCurrentChange = (current: number) => {
	promotionRelationPageNo.value = current
	loadPromotionRelationList()
}

// 处理tab点击事件
const handleTabClick = (tab: any) => {
	const tabLabel = tab.props.label

	if (tabLabel === '基本信息') {
		loadCustomerInfo()
	} else if (tabLabel === '联络记录') {
		loadContactRecordList()
	} else if (tabLabel === '营销记录') {
		loadPromotionRelationList()
	}
}

defineExpose({
	init
})
</script>

<style scoped>
/* 可以添加自定义样式 */
</style>
