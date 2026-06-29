<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="mkName">
				<el-input v-model="state.queryForm.mkName" placeholder="红娘姓名" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkPhone">
				<el-input v-model="state.queryForm.mkPhone" placeholder="电话" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkUserCode">
				<el-input v-model="state.queryForm.mkUserCode" placeholder="用户号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkCompanyName">
				<el-input v-model="state.queryForm.mkCompanyName" placeholder="婚介所名称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkIdentityProcessCode">
				<el-select v-model="state.queryForm.mkIdentityProcessCode" placeholder="审核状态" clearable style="width: 130px">
					<el-option label="待提交" value="0" />
					<el-option label="企业审核中" value="1" />
					<el-option label="平台审核中" value="4" />
					<el-option label="已通过" value="2" />
					<el-option label="已驳回" value="3" />
				</el-select>
			</el-form-item>
			<el-form-item prop="mkIdentityStatusCode">
				<el-select v-model="state.queryForm.mkIdentityStatusCode" placeholder="已认证" clearable style="width: 100px">
					<el-option label="是" value="1" />
					<el-option label="否" value="0" />
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
				<el-button v-auth="'datingDtMatchmakerAdd'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button v-auth="'datingDtMatchmakerDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
			<el-space>
				<el-button
					v-auth="'datingDtMatchmakerInitGoods'"
					icon="Refresh"
					plain
					type="warning"
					:loading="initAllGoodsLoading"
					@click="initAllGoodsHandle"
				>
					一键初始化全部服务
				</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="mkName" label="姓名" header-align="center" align="center" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkPhone" label="电话" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkUserCode" label="用户号" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCityName" label="城市" header-align="center" align="center" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyName" label="婚介所" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkIdentityProcessCode" label="审核状态" header-align="center" align="center" width="100">
				<template #default="scope">
					<el-tag :type="processTagType(scope.row.mkIdentityProcessCode)">
						{{ processLabel(scope.row.mkIdentityProcessCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="mkIdentityStatusCode" label="已认证" header-align="center" align="center" width="80">
				<template #default="scope">
					<el-tag v-if="isCertified(scope.row.mkIdentityStatusCode)" type="success">是</el-tag>
					<el-tag v-else type="info">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="mkIdentityAuditAt" label="审核时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkChannelsFinderUserName" label="视频号" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkChannelsProcessCode" label="视频号审核" header-align="center" align="center" width="110">
				<template #default="scope">
					<el-tag v-if="scope.row.mkChannelsFinderUserName" :type="channelsProcessTagType(scope.row.mkChannelsProcessCode)">
						{{ channelsProcessLabel(scope.row.mkChannelsProcessCode) }}
					</el-tag>
					<span v-else>—</span>
				</template>
			</el-table-column>
			<el-table-column prop="mkScore" label="评分" header-align="center" align="center" width="80"></el-table-column>
			<el-table-column prop="mkServiceUserCount" label="服务人数" header-align="center" align="center" width="100"></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="360">
				<template #default="scope">
					<el-button
						v-if="canDirectApprove(scope.row)"
						v-auth="'datingDtMatchmakerAudit'"
						type="success"
						link
						@click="directApproveHandle(scope.row)"
					>
						直接通过
					</el-button>
					<el-button
						v-if="canPlatformAudit(scope.row)"
						v-auth="'datingDtMatchmakerAudit'"
						type="primary"
						link
						@click="auditHandle(scope.row.id)"
					>
						平台审核
					</el-button>
					<el-button
						v-if="canChannelsAudit(scope.row)"
						v-auth="'datingDtMatchmakerAudit'"
						type="warning"
						link
						@click="channelsAuditHandle(scope.row.id)"
					>
						视频号审核
					</el-button>
					<el-button v-if="!canPlatformAudit(scope.row)" type="primary" link @click="auditHandle(scope.row.id)">查看</el-button>
					<el-button v-auth="'datingDtMatchmakerModify'" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
					<el-button v-auth="'datingDtMatchmakerDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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

		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>
		<audit-dialog ref="auditDialogRef" @refresh="getDataList"></audit-dialog>
		<channels-audit-dialog ref="channelsAuditDialogRef" @refresh="getDataList"></channels-audit-dialog>
	</el-card>
</template>

<script setup lang="ts" name="DatingDtMatchmakerIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'
import AddOrEdit from './add-or-edit.vue'
import AuditDialog from './audit-dialog.vue'
import ChannelsAuditDialog from './channels-audit-dialog.vue'

const PROCESS_MAP: Record<string, string> = {
	DRAFT: '待提交',
	REVIEWING: '企业审核中',
	PLATFORM_REVIEWING: '平台审核中',
	APPROVED: '已通过',
	REJECTED: '已驳回',
	'0': '待提交',
	'1': '企业审核中',
	'2': '已通过',
	'3': '已驳回',
	'4': '平台审核中'
}

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/dating/dtMatchmaker/list',
	deleteUrl: '/mgt/dating/dtMatchmaker/delete',
	queryForm: {
		mkName: '',
		mkPhone: '',
		mkUserCode: '',
		mkCompanyName: '',
		mkIdentityProcessCode: '',
		mkIdentityStatusCode: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const auditDialogRef = ref()
const channelsAuditDialogRef = ref()
const initAllGoodsLoading = ref(false)

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
	if (key === 'PLATFORM_REVIEWING' || key === '4') {
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

function isCertified(code: unknown) {
	const key = processKey(code)
	return key === 'YES' || key === '1' || key === 'true'
}

function canPlatformAudit(row: { mkIdentityProcessCode?: unknown }) {
	const key = processKey(row.mkIdentityProcessCode)
	return key === 'PLATFORM_REVIEWING' || key === '4'
}

function canDirectApprove(row: { mkIdentityProcessCode?: unknown; mkIdentityStatusCode?: unknown }) {
	const key = processKey(row.mkIdentityProcessCode)
	if (key === 'APPROVED' || key === '2') {
		return false
	}
	return !isCertified(row.mkIdentityStatusCode)
}

function channelsProcessLabel(code: unknown) {
	const key = processKey(code)
	if (key === 'DRAFT' || key === '0') return '待提交'
	if (key === 'REVIEWING' || key === '1') return '待审核'
	if (key === 'APPROVED' || key === '2') return '审核通过'
	if (key === 'REJECTED' || key === '3') return '审核失败'
	return key || '—'
}

function channelsProcessTagType(code: unknown) {
	const key = processKey(code)
	if (key === 'REVIEWING' || key === '1') return 'warning'
	if (key === 'APPROVED' || key === '2') return 'success'
	if (key === 'REJECTED' || key === '3') return 'danger'
	return 'info'
}

function canChannelsAudit(row: { mkChannelsProcessCode?: unknown; mkChannelsFinderUserName?: string }) {
	if (!String(row.mkChannelsFinderUserName ?? '').trim()) {
		return false
	}
	const key = processKey(row.mkChannelsProcessCode)
	return key === 'REVIEWING' || key === '1'
}

const addOrUpdateHandle = (id?: string) => {
	addOrEditRef.value.init(id)
}

const auditHandle = (id: string) => {
	auditDialogRef.value?.init(id)
}

const channelsAuditHandle = (id: string) => {
	channelsAuditDialogRef.value?.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)

const directApproveHandle = (row: { id?: string; mkName?: string }) => {
	if (!row.id) {
		return
	}
	const name = row.mkName || '该红娘'
	ElMessageBox.confirm(`确认直接通过「${name}」的资质审核？将跳过企业与平台审核流程。`, '直接通过', {
		type: 'warning'
	}).then(() => {
		service.post('/mgt/dating/dtMatchmaker/directApprove', null, { params: { id: row.id } }).then(() => {
			ElMessage.success('已通过')
			getDataList()
		})
	}).catch(() => {})
}

const initAllGoodsHandle = () => {
	ElMessageBox.confirm(
		'将为全部已认证红娘初始化/更新平台默认服务模板（爱之诚、线下约见、包结婚），已存在的商品会同步更新。是否继续？',
		'一键初始化全部服务',
		{ type: 'warning' }
	).then(() => {
		initAllGoodsLoading.value = true
		service.post('/mgt/dating/dtMatchmaker/initAllGoods')
			.then(res => {
				const data = res.data as {
					message?: string
					total?: number
					successCount?: number
					failedCount?: number
					failedLabels?: string[]
				}
				const summary = data?.message || '初始化完成'
				if ((data?.failedCount ?? 0) > 0) {
					const failed = (data?.failedLabels ?? []).join('、')
					ElMessage.warning(`${summary}${failed ? `，失败：${failed}` : ''}`)
				} else {
					ElMessage.success(summary)
				}
			})
			.finally(() => {
				initAllGoodsLoading.value = false
			})
	}).catch(() => {})
}
</script>
