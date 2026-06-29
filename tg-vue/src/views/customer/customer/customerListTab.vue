<template>
	<el-card style="margin-bottom: 10px">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusName" placeholder="客户姓名（%模糊搜索%）"></el-input>
			</el-form-item>
			<el-form-item prop="cusName">
				<el-input v-model="state.queryForm.cusNickName" placeholder="客户昵称（%模糊搜索%）"></el-input>
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
			<el-form-item v-if="tabKey !== 'verified' && tabKey !== 'unverified'" prop="cusIdentityAuthenticatedStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusIdentityAuthenticatedStatusCode"
					dict-code="cusIdentityAuthenticatedStatusCode"
					clearable
					placeholder="是否实名"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusAssignSalesStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusAssignSalesStatusCode"
					dict-code="cusAssignSalesStatusCode"
					clearable
					placeholder="是否有分配销售人员"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusFollowUpStatusCode">
				<tg-dict-select
					v-model="state.queryForm.cusFollowUpStatusCode"
					dict-code="cusFollowUpStatusCode"
					clearable
					placeholder="是否有跟进人员"
				></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusDealtStatusCode">
				<tg-dict-select v-model="state.queryForm.cusDealtStatusCode" dict-code="cusDealtStatusCode" clearable placeholder="是否成交"></tg-dict-select>
			</el-form-item>
			<el-form-item prop="cusAuditProcessCode">
				<tg-dict-select
					v-model="state.queryForm.cusAuditProcessCode"
					dict-code="cusAuditProcessCode"
					clearable
					placeholder="审核状态"
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
		<el-table
			v-loading="state.dataListLoading"
			:data="state.dataList"
			border
			class="layout-table"
			@selection-change="selectionChangeHandle"
			@sort-change="sortChangeHandle"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column label="头像" header-align="center" align="center" width="72">
				<template #default="scope">
					<el-image
						v-if="resolveCustomerAvatarUrl(scope.row)"
						:src="resolveCustomerAvatarUrl(scope.row)"
						:preview-src-list="resolveCustomerAvatarPreviewList(scope.row)"
						fit="cover"
						preview-teleported
						class="customer-list-avatar"
					/>
					<el-avatar v-else :size="40">{{ avatarFallbackText(scope.row) }}</el-avatar>
				</template>
			</el-table-column>
			<el-table-column prop="cusCode" label="客户编号" header-align="center" align="center" width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusName" label="客户姓名" header-align="center" align="center" show-overflow-tooltip>
				<template #default="scope">
					<el-button type="primary" link @click="kycHandle(scope.row.id)">{{ resolveCustomerDisplayName(scope.row) }}</el-button>
				</template>
			</el-table-column>
			<el-table-column
				prop="cusReferrerUserCode"
				label="推荐人用户编码"
				header-align="center"
				align="center"
				width="140"
				show-overflow-tooltip
			>
				<template #default="scope">{{ scope.row.cusReferrerUserCode || '—' }}</template>
			</el-table-column>
			<el-table-column
				prop="cusReferrerUserName"
				label="推荐人用户姓名"
				header-align="center"
				align="center"
				width="120"
				show-overflow-tooltip
			>
				<template #default="scope">{{ scope.row.cusReferrerUserName || '—' }}</template>
			</el-table-column>
			<el-table-column prop="cusNickName" label="昵称" header-align="center" align="center" width="100" show-overflow-tooltip>
				<template #default="scope">{{ scope.row.cusNickName || '—' }}</template>
			</el-table-column>
			<el-table-column
				prop="cusPhone"
				label="手机号"
				sortable="custom"
				header-align="center"
				width="120"
				align="center"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column prop="cusIdNo" label="证件号" header-align="center" width="120" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusSourceCode" label="客户来源" header-align="center" align="center" show-overflow-tooltip>
				<template #default="scope">{{ scope.row.cusSourceCode || '—' }}</template>
			</el-table-column>
			<tg-dict-column
				prop="cusIdentityAuthenticatedStatusCode"
				label="实名"
				width="80"
				dict-code="cusIdentityAuthenticatedStatusCode"
			></tg-dict-column>
			<tg-dict-column prop="cusLsStatusCode" label="爱与诚" width="90" dict-code="cusLsStatusCode"></tg-dict-column>
			<tg-dict-column prop="cusAuditProcessCode" label="审核状态" width="100" dict-code="cusAuditProcessCode"></tg-dict-column>
			<el-table-column label="会员" header-align="center" align="center" width="150">
				<template #default="scope">
					<template v-if="scope.row.cusMemberTypeName">
						<el-tag size="small" type="warning">{{ scope.row.cusMemberTypeName }}</el-tag>
						<div
							class="member-expire"
							:class="{ 'is-expired': isMemberExpired(scope.row.cusMemberExpireTime) }"
						>
							{{ formatMemberExpire(scope.row.cusMemberExpireTime) }}
						</div>
					</template>
					<el-tag v-else size="small" type="info">非会员</el-tag>
				</template>
			</el-table-column>
			<el-table-column
				prop="createTime"
				label="创建时间"
				header-align="center"
				align="center"
				min-width="160"
				show-overflow-tooltip
			></el-table-column>
			<el-table-column label="操作区域" header-align="center" align="center" width="220">
				<template #default="scope">
					<el-space wrap :size="4">
						<el-tag v-if="scope.row.cusUserCode" size="small" type="success">绑定</el-tag>
						<el-tag v-else size="small" type="info">未绑定</el-tag>
						<el-tag v-if="scope.row.cusIdentityAuthenticatedStatusCode === '1'" size="small" type="success">实名</el-tag>
						<el-tag v-else size="small" type="warning">未实名</el-tag>
						<el-tag v-if="scope.row.cusLsStatusCode === '1'" size="small" type="danger">爱与诚</el-tag>
						<el-tag v-if="scope.row.cusAssignSalesStatusCode === '1'" size="small">销售</el-tag>
						<el-tag v-if="scope.row.cusFollowUpStatusCode === '1'" size="small">跟进</el-tag>
						<el-tag v-if="scope.row.cusDealtStatusCode === '1'" size="small" type="success">成交</el-tag>
					</el-space>
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="200">
				<template #default="scope">
					<el-button type="primary" link @click="kycHandle(scope.row.id)">KYC视图</el-button>
					<el-button type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
					<el-button v-if="scope.row.cusUserCode" type="warning" link @click="giftMemberHandle(scope.row)">赠送会员</el-button>
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

		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
		<kyc ref="kycRef"></kyc>
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive, ref, onMounted, watch } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'
import Kyc from './kyc.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'
import { resolveCustomerAvatarPreviewList, resolveCustomerAvatarUrl, resolveCustomerDisplayName } from './customerDisplay'

const avatarFallbackText = (row: Record<string, unknown>) => {
	const name = resolveCustomerDisplayName(row)
	return name === '—' ? '客' : name.slice(0, 1)
}

const isMemberExpired = (time?: string) => {
	if (!time) {
		return false
	}
	return new Date(time).getTime() < Date.now()
}

const formatMemberExpire = (time?: string) => {
	if (!time) {
		return '永久有效'
	}
	return `${isMemberExpired(time) ? '已过期' : '到期'} ${time}`
}

export type CustomerTabKey = 'verified' | 'unverified' | 'lsLit'

const props = defineProps<{
	tabKey: CustomerTabKey
}>()

const tabPresetQuery = (): Record<string, string> => {
	switch (props.tabKey) {
		case 'verified':
			return { cusIdentityAuthenticatedStatusCode: '1' }
		case 'unverified':
			return { cusIdentityAuthenticatedStatusCode: '0' }
		case 'lsLit':
			return { cusLsStatusCode: '1' }
		default:
			return {}
	}
}

const buildBaseQueryForm = () => ({
	cusName: '',
	cusNickName: '',
	cusPhone: '',
	cusIdNo: '',
	cusSourceCode: '',
	cusIdentityAuthenticatedStatusCode: '',
	cusLsStatusCode: '',
	cusAssignSalesStatusCode: '',
	cusFollowUpStatusCode: '',
	cusDealtStatusCode: '',
	cusAuditProcessCode: '',
	cusPoolStatusCode: '',
	...tabPresetQuery()
})

const state: IHooksOptions = reactive({
	createdIsNeed: false,
	dataListUrl: '/mgt/customer/customer/list',
	deleteUrl: '/mgt/customer/customer/delete',
	sortBy: '-createTime',
	queryForm: buildBaseQueryForm(),
	queryFormReset: buildBaseQueryForm()
})

const applyTabPreset = () => {
	const preset = tabPresetQuery()
	Object.assign(state.queryForm, buildBaseQueryForm(), preset)
	state.queryFormReset = buildBaseQueryForm()
}

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

const kycRef = ref()
const kycHandle = (id: number) => {
	kycRef.value.init(id)
}

const giftMemberHandle = (row: Record<string, any>) => {
	const name = resolveCustomerDisplayName(row)
	ElMessageBox.confirm(`确定为客户「${name}」赠送会员吗？将直接为其开通会员（无需付费）。`, '赠送会员', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			service.post('/mgt/customer/customer/giftMember', { id: row.id }).then(() => {
				ElMessage.success('赠送会员成功')
				getDataList()
			})
		})
		.catch(() => {})
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, sortChangeHandle, deleteBatchHandle, reset } = useCrud(state)

watch(
	() => props.tabKey,
	() => {
		applyTabPreset()
		state.pageNo = 1
		getDataList()
	}
)

onMounted(() => {
	applyTabPreset()
	state.pageNo = 1
	getDataList()
})

defineExpose({
	reset: () => {
		state.dataListSelectionKeys = []
		applyTabPreset()
		if (queryRef.value) {
			reset(queryRef.value)
		}
	}
})
</script>

<style scoped>
.customer-list-avatar {
	width: 40px;
	height: 40px;
	border-radius: 50%;
	cursor: pointer;
	vertical-align: middle;
}
.member-expire {
	margin-top: 2px;
	font-size: 12px;
	color: #67c23a;
}
.member-expire.is-expired {
	color: #f56c6c;
}
</style>
