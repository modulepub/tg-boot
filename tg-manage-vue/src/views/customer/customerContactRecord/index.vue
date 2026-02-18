<template>
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
			<el-form-item prop="contactRecordSourceCode">
				<tg-dict-select
					v-model="state.queryForm.contactRecordSourceCode"
					dict-code="contactRecordSourceCode"
					clearable
					placeholder="联络记录来源"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="contactRecordMethodCode">
				<tg-dict-select
					v-model="state.queryForm.contactRecordMethodCode"
					dict-code="contactRecordMethodCode"
					clearable
					placeholder="联络方式"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusCode">
				<el-input v-model="state.queryForm.cusCode" placeholder="客户编号"></el-input>
			</el-form-item>
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item prop="cusPhone">
				<el-input v-model="state.queryForm.cusPhone" placeholder="手机号"></el-input>
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
			<el-table-column prop="contactRecordCode" label="记录编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusCode" label="客户编号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusPhone" label="客户手机号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusWechatId" label="客户微信号" header-align="center" width="100" align="center" show-overflow-tooltip></el-table-column>
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
			<el-table-column prop="contactRecordDescription" label="跟踪描述" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<tg-dict-column prop="cusIntentionStatusCode" label="是否意向" dict-code="cusIntentionStatusCode"></tg-dict-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
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

		<!-- 弹窗, 新增 / 修改 -->
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="CustomercontactRecordIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

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
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
