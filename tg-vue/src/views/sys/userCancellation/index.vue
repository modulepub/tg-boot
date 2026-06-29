<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="userPhone">
				<el-input v-model="state.queryForm.userPhone" placeholder="手机号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="userNickName">
				<el-input v-model="state.queryForm.userNickName" placeholder="昵称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="userCode">
				<el-input v-model="state.queryForm.userCode" placeholder="用户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cancellationProcessCode">
				<el-select v-model="state.queryForm.cancellationProcessCode" placeholder="处理状态" clearable style="width: 130px">
					<el-option label="待处理" value="0" />
					<el-option label="已处理" value="1" />
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
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
			<el-table-column prop="userNickName" label="昵称" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userPhone" label="手机号" header-align="center" align="center" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userCode" label="用户编码" header-align="center" align="center" min-width="180" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cancellationProcessCode" label="处理状态" header-align="center" align="center" width="100">
				<template #default="scope">
					<el-tag :type="processTagType(scope.row.cancellationProcessCode)">
						{{ processLabel(scope.row.cancellationProcessCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="申请时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="processAt" label="处理时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="processBy" label="处理人" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="120">
				<template #default="scope">
					<el-button
						v-if="isPending(scope.row.cancellationProcessCode)"
						v-auth="'sysUserCancellationProcess'"
						type="primary"
						link
						@click="processHandle(scope.row.id)"
					>
						执行注销
					</el-button>
					<span v-else class="processed-tip">—</span>
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
	</el-card>
</template>

<script setup lang="ts" name="SysUserCancellationIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const PROCESS_MAP: Record<string, string> = {
	PENDING: '待处理',
	PROCESSED: '已处理',
	'0': '待处理',
	'1': '已处理'
}

const queryRef = ref()

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/system/sysUserCancellation/list',
	queryForm: {
		userPhone: '',
		userNickName: '',
		userCode: '',
		cancellationProcessCode: ''
	}
})

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

function processCode(raw: unknown): string {
	if (raw == null || raw === '')
		return ''
	if (typeof raw === 'object' && raw !== null && 'code' in raw)
		return String((raw as { code: string }).code)
	return String(raw)
}

function processLabel(raw: unknown): string {
	const code = processCode(raw)
	return PROCESS_MAP[code] || code || '—'
}

function processTagType(raw: unknown): '' | 'success' | 'warning' | 'info' | 'danger' {
	const code = processCode(raw)
	if (code === '1' || code === 'PROCESSED')
		return 'success'
	if (code === '0' || code === 'PENDING')
		return 'warning'
	return 'info'
}

function isPending(raw: unknown): boolean {
	const code = processCode(raw)
	return code === '0' || code === 'PENDING'
}

async function processHandle(id: string) {
	await ElMessageBox.confirm(
		'确认将该注销申请标记为已处理？处理完成后用户账号将被注销且无法恢复。',
		'确认处理',
		{ type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
	)
	await service.post('/mgt/system/sysUserCancellation/process', null, { params: { id } })
	ElMessage.success('已处理')
	getDataList()
}
</script>

<style scoped>
.processed-tip {
	color: var(--el-text-color-placeholder);
}
</style>
