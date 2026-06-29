<template>
	<el-dialog v-model="dialogVisible" title="联络记录" width="80%" :before-close="handleClose">
		<el-card class="layout-query">
			<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
				<el-form-item prop="userCode">
					<el-input v-model="state.queryForm.userCode" placeholder="员工账号"></el-input>
				</el-form-item>
				<el-form-item prop="cusIntentionStatusCode">
					<tg-dict-select
						v-model="state.queryForm.cusIntentionStatusCode"
						dict-code="cusIntentionStatusCode"
						clearable
						placeholder="是否意向"
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
			<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
				<el-table-column prop="contactRecordCode" label="编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="cusCode" label="客户编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="cusPhone" label="客户手机号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="cusWechatId" label="客户微信号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="userCode" label="员工账号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column prop="userRealName" label="员工姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<tg-dict-column prop="contactRecordSourceCode" label="联络记录来源" dict-code="contactRecordSourceCode"></tg-dict-column>
				<tg-dict-column prop="contactRecordMethodCode" label="联络方式" dict-code="contactRecordMethodCode"></tg-dict-column>
				<el-table-column
					prop="contactRecordTalkDuration"
					label="通话时长（s)"
					header-align="center"
					align="center"
					show-overflow-tooltip
				></el-table-column>
				<tg-file-column prop="contactRecordFile" label="录音文件" header-align="center" align="center"></tg-file-column>
				<el-table-column prop="contactRecordVoiceText" label="通话文字" header-align="center" align="center" show-overflow-tooltip></el-table-column>
				<el-table-column
					prop="contactRecordDescription"
					label="跟踪描述"
					header-align="center"
					align="center"
					show-overflow-tooltip
				></el-table-column>
				<tg-dict-column prop="cusIntentionLevelCode" label="意向等级" dict-code="cusIntentionLevelCode"></tg-dict-column>
				<tg-dict-column prop="cusIntentionStatusCode" label="是否意向" dict-code="cusIntentionStatusCode"></tg-dict-column>
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
	</el-dialog>
</template>

<script setup lang="ts" name="ReportDetailModal">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const dialogVisible = ref(false)
const props = defineProps<{
	cusCode?: string
}>()
const emit = defineEmits<{
	(e: 'close'): void
}>()

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/customer/customerContactRecord/list',
	deleteUrl: '/mgt/customer/customerContactRecord/delete',
	queryForm: {
		seqNo: '',
		orgCode: '',
		version: '',
		deleted: '',
		contactRecordName: '',
		userCode: '',
		contactRecordTimes: '',
		contactRecordTalkDuration: '',
		contactRecordDescription: '',
		cusIntentionStatusCode: '',
		cusCode: '',
		cusPhone: ''
	}
})

const queryRef = ref()

const init = (cusCode: string) => {
	state.queryForm.cusCode = cusCode
	dialogVisible.value = true
	getDataList()
}

const handleClose = () => {
	dialogVisible.value = false
	emit('close')
}

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

defineExpose({
	init
})
</script>
