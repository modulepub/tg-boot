<template>
	<el-card>
		<template #header>
			<div class="card-header">
				<span>业绩测试数据</span>
			</div>
		</template>

		<el-alert
			type="info"
			show-icon
			:closable="false"
			title="选择一位推广人后，系统会生成若干「测试」下线账号，下线随机下不同类型的会员订单（钻石/黑钻/金钻），并复用真实账单逻辑累加到推广人的「下级客户总付费 / 下级服务期内总付费」。清除时会删除全部测试下线及其账单，并把推广人业绩恢复为生成前的真实数据。"
			class="mb-16"
		/>

		<el-form :model="form" label-width="120px" class="seed-form">
			<el-form-item label="推广人">
				<div class="promoter-line">
					<el-tag v-if="form.promoterUserCode" type="success" class="promoter-tag">
						{{ promoterLabel }}
					</el-tag>
					<span v-else class="promoter-empty">未选择</span>
					<el-button type="primary" plain @click="openPicker">选择推广人</el-button>
				</div>
			</el-form-item>
			<el-form-item label="生成下线数量">
				<el-input-number v-model="form.downlineCount" :min="1" :max="500" />
				<span class="hint">个（1~500），每位下线随机开 1~2 单会员</span>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" :loading="seedLoading" @click="seedHandle">生成业绩数据</el-button>
				<el-button type="danger" :loading="clearLoading" @click="clearHandle">清除业绩测试数据</el-button>
			</el-form-item>
		</el-form>

		<el-descriptions v-if="lastResult" class="result-box" :column="1" border title="最近操作结果">
			<el-descriptions-item label="摘要">{{ lastResult.message || '—' }}</el-descriptions-item>
			<template v-if="lastResultType === 'seed'">
				<el-descriptions-item label="推广人">
					{{ lastResult.promoterName }}（{{ lastResult.promoterUserCode }}）
				</el-descriptions-item>
				<el-descriptions-item label="下线 / 订单">
					{{ lastResult.downlineCount }} 位 / {{ lastResult.orderCount }} 笔
				</el-descriptions-item>
				<el-descriptions-item label="会员类型分布">
					钻石 {{ lastResult.standardMemberOrders }}、黑钻 {{ lastResult.premiumMemberOrders }}、金钻 {{ lastResult.diamondMemberOrders }}
				</el-descriptions-item>
				<el-descriptions-item label="下级客户总付费 +">
					¥{{ lastResult.totalPaidAmount }}（服务期内 ¥{{ lastResult.totalInServiceAmount }}）
				</el-descriptions-item>
				<el-descriptions-item label="登录说明">
					测试下线可用返回手机号 + 验证码 666666 登录
				</el-descriptions-item>
			</template>
			<template v-else-if="lastResultType === 'clear'">
				<el-descriptions-item label="删除统计">
					汇总 {{ lastResult.summaryCount }}、结算明细 {{ lastResult.settleRecordCount }}、事件 {{ lastResult.eventCount }}、
					下线用户 {{ lastResult.userCount }}、重算推广人 {{ lastResult.promoterRecomputedCount }}
				</el-descriptions-item>
			</template>
		</el-descriptions>
	</el-card>

	<el-dialog v-model="pickerVisible" title="选择推广人" width="760px" top="6vh">
		<el-form :inline="true" :model="pickerQuery" @keyup.enter="searchUsers(1)">
			<el-form-item>
				<el-input v-model="pickerQuery.userPhone" placeholder="手机号" clearable />
			</el-form-item>
			<el-form-item>
				<el-input v-model="pickerQuery.userRealName" placeholder="真实姓名" clearable />
			</el-form-item>
			<el-form-item>
				<el-input v-model="pickerQuery.userNickName" placeholder="昵称" clearable />
			</el-form-item>
			<el-form-item>
				<el-button type="primary" icon="Search" @click="searchUsers(1)">查询</el-button>
			</el-form-item>
		</el-form>

		<el-table
			v-loading="pickerLoading"
			:data="pickerList"
			border
			height="380"
			highlight-current-row
			@current-change="handleRowSelect"
		>
			<el-table-column width="55" align="center">
				<template #default="{ row }">
					<el-radio v-model="selectedUserCode" :value="row.userCode" @change="handleRowSelect(row)"><span></span></el-radio>
				</template>
			</el-table-column>
			<el-table-column prop="userRealName" label="真实姓名" min-width="100" />
			<el-table-column prop="userNickName" label="昵称" min-width="120" show-overflow-tooltip />
			<el-table-column prop="userPhone" label="手机号" min-width="120" />
			<el-table-column prop="userCode" label="用户编码" min-width="160" show-overflow-tooltip />
		</el-table>
		<el-pagination
			class="picker-pager"
			:current-page="pickerPageNo"
			:page-size="pickerPageSize"
			:total="pickerTotal"
			layout="total, prev, pager, next"
			@current-change="searchUsers"
		/>

		<template #footer>
			<el-button @click="pickerVisible = false">取消</el-button>
			<el-button type="primary" :disabled="!selectedRow" @click="confirmPicker">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="DistributionSystemTestIndex">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus/es'
import service from '@/utils/request'

interface ISysUser {
	userCode: string
	userNickName?: string
	userRealName?: string
	userPhone?: string
}

interface ISeedResult {
	promoterUserCode?: string
	promoterName?: string
	downlineCount?: number
	orderCount?: number
	totalPaidAmount?: number | string
	totalInServiceAmount?: number | string
	standardMemberOrders?: number
	premiumMemberOrders?: number
	diamondMemberOrders?: number
	message?: string
}

interface IClearResult {
	summaryCount?: number
	settleRecordCount?: number
	eventCount?: number
	userCount?: number
	promoterRecomputedCount?: number
	message?: string
}

const form = reactive({
	promoterUserCode: '',
	promoterName: '',
	downlineCount: 5
})

const promoterLabel = computed(() =>
	form.promoterName ? `${form.promoterName}（${form.promoterUserCode}）` : form.promoterUserCode
)

const seedLoading = ref(false)
const clearLoading = ref(false)
const lastResult = ref<(ISeedResult & IClearResult) | null>(null)
const lastResultType = ref<'seed' | 'clear' | ''>('')

// ---- 推广人选择弹窗 ----
const pickerVisible = ref(false)
const pickerLoading = ref(false)
const pickerList = ref<ISysUser[]>([])
const pickerQuery = reactive({ userPhone: '', userRealName: '', userNickName: '' })
const pickerPageNo = ref(1)
const pickerPageSize = ref(10)
const pickerTotal = ref(0)
const selectedRow = ref<ISysUser | null>(null)
const selectedUserCode = ref('')

const openPicker = () => {
	pickerVisible.value = true
	selectedRow.value = null
	selectedUserCode.value = ''
	searchUsers(1)
}

const handleRowSelect = (row: ISysUser | null) => {
	if (row) {
		selectedRow.value = row
		selectedUserCode.value = row.userCode
	}
}

const searchUsers = (pageNo: number) => {
	pickerPageNo.value = pageNo
	pickerLoading.value = true
	service
		.get('/mgt/sysUser/list', {
			params: {
				userPhone: pickerQuery.userPhone || undefined,
				userRealName: pickerQuery.userRealName ? `%${pickerQuery.userRealName}%` : undefined,
				userNickName: pickerQuery.userNickName ? `%${pickerQuery.userNickName}%` : undefined,
				pageNo,
				pageSize: pickerPageSize.value
			}
		})
		.then((res: { data?: { records?: ISysUser[]; total?: number } }) => {
			pickerList.value = res.data?.records ?? []
			pickerTotal.value = res.data?.total ?? 0
		})
		.finally(() => {
			pickerLoading.value = false
		})
}

const confirmPicker = () => {
	if (!selectedRow.value) {
		return
	}
	form.promoterUserCode = selectedRow.value.userCode
	form.promoterName = selectedRow.value.userRealName || selectedRow.value.userNickName || selectedRow.value.userCode
	pickerVisible.value = false
}

// ---- 生成 / 清除 ----
const seedHandle = () => {
	if (!form.promoterUserCode) {
		ElMessage.warning('请先选择推广人')
		return
	}
	ElMessageBox.confirm(
		`将为「${promoterLabel.value}」生成 ${form.downlineCount} 位测试下线及其会员订单，是否继续？`,
		'生成业绩测试数据',
		{ type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
	)
		.then(() => {
			seedLoading.value = true
			service
				.post('/mgt/distribution/mock/seed', null, {
					params: {
						promoterUserCode: form.promoterUserCode,
						downlineCount: form.downlineCount
					}
				})
				.then((res: { data?: ISeedResult }) => {
					lastResult.value = res.data ?? null
					lastResultType.value = 'seed'
					ElMessage.success(res.data?.message || '业绩测试数据已生成')
				})
				.finally(() => {
					seedLoading.value = false
				})
		})
		.catch(() => {})
}

const clearHandle = () => {
	ElMessageBox.confirm('将删除全部业绩测试下线及其账单数据，并重算推广人业绩，是否继续？', '清除业绩测试数据', {
		type: 'error',
		confirmButtonText: '确定清除',
		cancelButtonText: '取消'
	})
		.then(() => {
			clearLoading.value = true
			service
				.post('/mgt/distribution/mock/clear', null, { params: { confirm: 'yes' } })
				.then((res: { data?: IClearResult }) => {
					lastResult.value = res.data ?? null
					lastResultType.value = 'clear'
					ElMessage.success(res.data?.message || '业绩测试数据已清除')
				})
				.finally(() => {
					clearLoading.value = false
				})
		})
		.catch(() => {})
}
</script>

<style scoped>
.card-header {
	font-weight: 600;
}
.mb-16 {
	margin-bottom: 16px;
}
.seed-form {
	max-width: 560px;
}
.promoter-line {
	display: flex;
	align-items: center;
	gap: 12px;
}
.promoter-tag {
	font-size: 13px;
}
.promoter-empty {
	color: #909399;
}
.hint {
	margin-left: 10px;
	color: #909399;
	font-size: 12px;
}
.result-box {
	margin-top: 24px;
}
.picker-pager {
	margin-top: 12px;
	justify-content: flex-end;
}
</style>
