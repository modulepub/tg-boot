<template>
	<el-card>
		<template #header>
			<div class="card-header">
				<span>系统测试</span>
			</div>
		</template>

		<el-alert
			type="info"
			show-icon
			:closable="false"
			title="用于生成或清除标记为「测试数据」的 mock 样本。一键生成会重新上传头像/生活照，并覆盖 mock 标记客户的完整资料（含昵称、说说及推荐/喜欢/联系人等冗余快照）。"
			class="mb-16"
		/>

		<el-space wrap>
			<el-button type="primary" :loading="seedLoading" @click="seedHandle">
				一键生成测试数据
			</el-button>
			<el-button type="danger" :loading="clearLoading" @click="clearHandle">
				一键删除测试数据
			</el-button>
		</el-space>

		<el-descriptions v-if="lastResult" class="result-box" :column="1" border title="最近操作结果">
			<el-descriptions-item label="摘要">
				{{ lastResult.message || '—' }}
			</el-descriptions-item>
			<template v-if="lastResultType === 'seed'">
				<el-descriptions-item label="公司">
					{{ lastResult.companyName }}（{{ lastResult.companyCode || '—' }}）
				</el-descriptions-item>
				<el-descriptions-item label="红娘 / 男嘉宾 / 女嘉宾">
					{{ lastResult.matchmakerCount }} / {{ lastResult.maleCustomerCount }} / {{ lastResult.femaleCustomerCount }}
				</el-descriptions-item>
				<el-descriptions-item label="关联数">
					{{ lastResult.relationCount }}
				</el-descriptions-item>
				<el-descriptions-item label="登录说明">
					测试账号可使用返回手机号 + 验证码 666666 登录
				</el-descriptions-item>
			</template>
			<template v-else-if="lastResultType === 'clear'">
				<el-descriptions-item label="删除统计">
					公司 {{ lastResult.companyCount }}、红娘 {{ lastResult.matchmakerCount }}、客户 {{ lastResult.customerCount }}、
					关联 {{ lastResult.relationCount }}、商品 {{ lastResult.goodsCount }}、用户 {{ lastResult.userCount }}
				</el-descriptions-item>
			</template>
		</el-descriptions>
	</el-card>
</template>

<script setup lang="ts" name="DatingSystemTestIndex">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus/es'
import service from '@/utils/request'

interface ISeedResult {
	companyCode?: string
	companyName?: string
	matchmakerCount?: number
	maleCustomerCount?: number
	femaleCustomerCount?: number
	relationCount?: number
	message?: string
}

interface IClearResult {
	companyCount?: number
	matchmakerCount?: number
	customerCount?: number
	relationCount?: number
	goodsCount?: number
	userCount?: number
	message?: string
}

const seedLoading = ref(false)
const clearLoading = ref(false)
const lastResult = ref<(ISeedResult & IClearResult) | null>(null)
const lastResultType = ref<'seed' | 'clear' | ''>('')

const seedHandle = () => {
	ElMessageBox.confirm(
		'将重新上传 mock 素材并覆盖已有测试数据（头像、生活照、昵称、说说、完整资料及冗余快照），是否继续？',
		'生成测试数据',
		{
			type: 'warning',
			confirmButtonText: '确定',
			cancelButtonText: '取消'
		}
	).then(() => {
		seedLoading.value = true
		service.post('/mgt/dating/mock/seed', null)
			.then((res: { data?: ISeedResult }) => {
				lastResult.value = res.data ?? null
				lastResultType.value = 'seed'
				ElMessage.success(res.data?.message || '测试数据已生成')
			})
			.finally(() => {
				seedLoading.value = false
			})
	}).catch(() => {})
}

const clearHandle = () => {
	ElMessageBox.confirm('将逻辑删除全部测试标记数据，此操作不可恢复，是否继续？', '删除测试数据', {
		type: 'error',
		confirmButtonText: '确定删除',
		cancelButtonText: '取消'
	}).then(() => {
		clearLoading.value = true
		service.post('/mgt/dating/mock/clear', null, { params: { confirm: 'yes' } })
			.then((res: { data?: IClearResult }) => {
				lastResult.value = res.data ?? null
				lastResultType.value = 'clear'
				ElMessage.success(res.data?.message || '测试数据已清除')
			})
			.finally(() => {
				clearLoading.value = false
			})
	}).catch(() => {})
}
</script>

<style scoped>
.card-header {
	font-weight: 600;
}
.mb-16 {
	margin-bottom: 16px;
}
.result-box {
	margin-top: 24px;
}
</style>
