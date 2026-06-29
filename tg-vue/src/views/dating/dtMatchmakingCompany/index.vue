<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="mkCompanyName">
				<el-input v-model="state.queryForm.mkCompanyName" placeholder="企业名称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkCompanyTel">
				<el-input v-model="state.queryForm.mkCompanyTel" placeholder="公司电话" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkCompanyIdentityProcessCode">
				<el-select v-model="state.queryForm.mkCompanyIdentityProcessCode" placeholder="入驻状态" clearable style="width: 140px">
					<el-option label="待提交" value="0" />
					<el-option label="审核中" value="1" />
					<el-option label="已通过" value="2" />
					<el-option label="已驳回" value="3" />
				</el-select>
			</el-form-item>
			<el-form-item prop="mkCompanyTransferStatusCode">
				<el-select v-model="state.queryForm.mkCompanyTransferStatusCode" placeholder="转账状态" clearable style="width: 140px">
					<el-option label="已转账" value="1" />
					<el-option label="未转账" value="0" />
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
				<el-dropdown v-auth="'datingDtMatchmakingCompanyDelete'" @command="handleBatchCommand">
					<el-button>
						批量管理
						<el-icon class="el-icon--right"><ArrowDown /></el-icon>
					</el-button>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="delete">删除</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="mkCompanyName" label="企业名称" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyUsciCode" label="信用代码" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyLegalName" label="法人" width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyTel" label="电话" width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyIdentityProcessCode" label="入驻状态" width="100" align="center">
				<template #default="scope">
					<el-tag :type="processTagType(scope.row.mkCompanyIdentityProcessCode)">
						{{ processLabel(scope.row.mkCompanyIdentityProcessCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="mkCompanyIdentityStatusCode" label="已认证" width="80" align="center">
				<template #default="scope">
					<el-tag v-if="isCertified(scope.row.mkCompanyIdentityStatusCode)" type="success">是</el-tag>
					<el-tag v-else type="info">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="mkCompanyVerifyAmount" label="认证金额" width="100" align="right">
				<template #default="scope">
					{{ formatAmount(scope.row.mkCompanyVerifyAmount) }}
				</template>
			</el-table-column>
			<el-table-column prop="mkCompanyTransferStatusCode" label="转账状态" width="110" align="center">
				<template #default="scope">
					<el-tag :type="transferTagType(scope.row)">
						{{ transferLabel(scope.row) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="mkCompanyAdminUserRealName" label="管理员" min-width="120" show-overflow-tooltip>
				<template #default="scope">
					{{ scope.row.mkCompanyAdminUserRealName || scope.row.mkCompanyAdminUserCode || '—' }}
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="申请时间" min-width="160"></el-table-column>
			<el-table-column label="操作" fixed="right" width="360" align="center">
				<template #default="scope">
					<el-button type="primary" link @click="editHandle(scope.row.id)">编辑企业信息</el-button>
					<el-button
						v-if="canSubmit(scope.row.mkCompanyIdentityProcessCode)"
						v-auth="'datingDtMatchmakingCompanyAudit'"
						type="primary"
						link
						@click="submitHandle(scope.row)"
					>
						提交
					</el-button>
					<el-button
						v-if="isReviewing(scope.row.mkCompanyIdentityProcessCode)"
						v-auth="'datingDtMatchmakingCompanyAudit'"
						type="primary"
						link
						@click="auditHandle(scope.row.id)"
					>
						审核
					</el-button>
					<el-button v-else type="primary" link @click="auditHandle(scope.row.id)">查看</el-button>
					<el-button type="primary" link @click="setAdminHandle(scope.row)">设置管理员</el-button>
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
		/>

		<audit-dialog ref="auditDialogRef" @refresh="getDataList"></audit-dialog>
		<edit-dialog ref="editDialogRef" @refresh="getDataList"></edit-dialog>
		<set-admin-dialog ref="setAdminDialogRef" @refresh="getDataList"></set-admin-dialog>
	</el-card>
</template>

<script setup lang="ts" name="DatingDtMatchmakingCompanyIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'
import AuditDialog from './audit-dialog.vue'
import EditDialog from './edit-dialog.vue'
import SetAdminDialog from './set-admin-dialog.vue'

const PROCESS_MAP: Record<string, string> = {
	DRAFT: '待提交',
	REVIEWING: '审核中',
	APPROVED: '已通过',
	REJECTED: '已驳回',
	'0': '待提交',
	'1': '审核中',
	'2': '已通过',
	'3': '已驳回'
}

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/dating/dtMatchmakingCompany/list',
	deleteUrl: '/mgt/dating/dtMatchmakingCompany/delete',
	primaryKey: 'id',
	queryForm: {
		mkCompanyName: '',
		mkCompanyTel: '',
		mkCompanyIdentityProcessCode: '',
		mkCompanyTransferStatusCode: ''
	}
})

const queryRef = ref()
const auditDialogRef = ref()
const editDialogRef = ref()
const setAdminDialogRef = ref()

function processKey(code: unknown) {
	const raw = code
	if (raw != null && typeof raw === 'object' && 'code' in (raw as object)) {
		return String((raw as { code?: string }).code ?? '').trim()
	}
	return String(raw ?? '').trim()
}

function processLabel(code: unknown) {
	const key = processKey(code)
	return PROCESS_MAP[key] || key || '—'
}

function processTagType(code: unknown) {
	const key = processKey(code)
	if (key === 'REVIEWING' || key === '1') {
		return 'warning'
	}
	if (key === 'APPROVED' || key === '2') {
		return 'success'
	}
	if (key === 'REJECTED' || key === '3') {
		return 'danger'
	}
	return 'info'
}

function isReviewing(code: unknown) {
	const key = processKey(code)
	return key === 'REVIEWING' || key === '1'
}

function canSubmit(code: unknown) {
	const key = processKey(code)
	return key === 'DRAFT' || key === '0' || key === 'REJECTED' || key === '3'
}

function isCertified(code: unknown) {
	const key = processKey(code)
	return key === 'YES' || key === '1' || key === 'true'
}

function formatAmount(amount: unknown) {
	if (amount == null || amount === '') {
		return '—'
	}
	const n = Number(amount)
	return Number.isFinite(n) ? n.toFixed(2) : String(amount)
}

function statusKey(code: unknown) {
	const raw = code
	if (raw != null && typeof raw === 'object' && 'code' in (raw as object)) {
		return String((raw as { code?: string }).code ?? '').trim()
	}
	return String(raw ?? '').trim()
}

function isTransferredRow(row: { mkCompanyTransferStatusCode?: unknown; mkCompanyVerifySkipCode?: unknown }) {
	if (String(row.mkCompanyVerifySkipCode ?? '').trim() === '1') {
		return true
	}
	const key = statusKey(row.mkCompanyTransferStatusCode)
	return key === 'YES' || key === '1' || key === 'true'
}

function transferLabel(row: { mkCompanyTransferStatusCode?: unknown; mkCompanyVerifySkipCode?: unknown }) {
	if (String(row.mkCompanyVerifySkipCode ?? '').trim() === '1') {
		return '无需再次转账'
	}
	return isTransferredRow(row) ? '已转账' : '未转账'
}

function transferTagType(row: { mkCompanyTransferStatusCode?: unknown; mkCompanyVerifySkipCode?: unknown }) {
	if (String(row.mkCompanyVerifySkipCode ?? '').trim() === '1') {
		return 'info'
	}
	return isTransferredRow(row) ? 'success' : 'warning'
}

const auditHandle = (id: string) => {
	auditDialogRef.value?.init(id)
}

const editHandle = (id?: string) => {
	if (!id) {
		return
	}
	editDialogRef.value?.init(id)
}

const setAdminHandle = (row: { id?: string; mkCompanyName?: string; mkCompanyAdminUserCode?: string }) => {
	setAdminDialogRef.value?.init(row)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

const handleBatchCommand = (command: string) => {
	if (command === 'delete') {
		deleteBatchHandle()
	}
}

const submitHandle = (row: { id?: string; mkCompanyName?: string }) => {
	if (!row.id) {
		return
	}
	const name = row.mkCompanyName || '该企业'
	ElMessageBox.confirm(`确认为「${name}」代提交入驻审核？提交后将进入审核中状态。`, '代提交审核', {
		type: 'warning'
	}).then(() => {
		service.post('/mgt/dating/dtMatchmakingCompany/submitForReview', null, { params: { id: row.id } }).then(() => {
			ElMessage.success('已提交审核')
			getDataList()
		})
	}).catch(() => {})
}
</script>
