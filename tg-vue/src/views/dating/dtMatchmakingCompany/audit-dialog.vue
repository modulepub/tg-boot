<template>
	<el-dialog v-model="visible" title="企业入驻审核" width="760px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<div v-loading="loading">
			<template v-if="detail">
				<el-descriptions :column="2" border size="small" class="audit-desc">
					<el-descriptions-item label="企业名称" :span="2">{{ detail.mkCompanyName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="统一社会信用代码">{{ detail.mkCompanyUsciCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="公司电话">{{ detail.mkCompanyTel || '—' }}</el-descriptions-item>
					<el-descriptions-item label="法人姓名">{{ detail.mkCompanyLegalName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="法人证件号">{{ detail.mkCompanyLegalIdNo || '—' }}</el-descriptions-item>
					<el-descriptions-item label="公司地址" :span="2">{{ detail.mkCompanyAddressDetail || '—' }}</el-descriptions-item>
					<el-descriptions-item label="对公账号">{{ detail.mkCompanyPublicAccountNo || '—' }}</el-descriptions-item>
					<el-descriptions-item label="开户行">{{ detail.mkCompanyBankName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="开户地" :span="2">{{ detail.mkCompanyBankLocation || '—' }}</el-descriptions-item>
					<el-descriptions-item label="认证金额">
						<span class="verify-amount">{{ formatVerifyAmount(detail.mkCompanyVerifyAmount) }}</span>
					</el-descriptions-item>
					<el-descriptions-item label="管理员">{{ detail.mkCompanyAdminUserRealName || detail.mkCompanyAdminUserCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="申请时间" :span="2">{{ detail.createTime || '—' }}</el-descriptions-item>
				</el-descriptions>

				<div v-if="photoList.length" class="photo-block">
					<div class="photo-label">办公/门头照片</div>
					<div class="photo-list">
						<el-image
							v-for="(url, idx) in photoList"
							:key="idx"
							:src="url"
							fit="cover"
							class="photo-item"
							preview-teleported
							:preview-src-list="photoList"
							:initial-index="idx"
						/>
					</div>
				</div>

				<el-alert
					v-if="canAudit"
					type="info"
					:closable="false"
					show-icon
					class="audit-tip"
					title="请核对申请人向平台公户转账的认证金额后再操作。"
				/>

				<el-form v-if="canAudit" ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="90px" class="reject-form">
					<el-form-item label="驳回原因" prop="rejectReason">
						<el-input
							v-model="rejectForm.rejectReason"
							type="textarea"
							:rows="3"
							placeholder="驳回时必填"
							maxlength="500"
							show-word-limit
						/>
					</el-form-item>
				</el-form>

				<el-descriptions v-else-if="detail.mkCompanyRejectReason" :column="1" border size="small" class="audit-desc">
					<el-descriptions-item label="驳回原因">{{ detail.mkCompanyRejectReason }}</el-descriptions-item>
					<el-descriptions-item label="审核时间">{{ detail.mkCompanyAuditAt || '—' }}</el-descriptions-item>
				</el-descriptions>
			</template>
		</div>

		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
			<template v-if="canAudit">
				<el-button v-auth="'datingDtMatchmakingCompanyAudit'" type="danger" :loading="rejecting" @click="rejectHandle">驳回</el-button>
				<el-button v-auth="'datingDtMatchmakingCompanyAudit'" type="primary" :loading="approving" @click="approveHandle">通过</el-button>
			</template>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import service from '@/utils/request'

const emit = defineEmits<{ refresh: [] }>()

const visible = ref(false)
const loading = ref(false)
const approving = ref(false)
const rejecting = ref(false)
const detail = ref<Record<string, any> | null>(null)
const rejectFormRef = ref<FormInstance>()
const rejectForm = reactive({ rejectReason: '' })

const rejectRules: FormRules = {
	rejectReason: [{ required: true, message: '请填写驳回原因', trigger: 'blur' }]
}

const PROCESS_REVIEWING = 'REVIEWING'

const canAudit = computed(() => {
	const code = String(detail.value?.mkCompanyIdentityProcessCode ?? '').trim()
	return code === '1' || code === PROCESS_REVIEWING
})

const photoList = computed(() => {
	const raw = String(detail.value?.mkCompanyPhotos ?? '')
	return raw.split(',').map(s => s.trim()).filter(Boolean)
})

function formatVerifyAmount(amount: unknown) {
	if (amount == null || amount === '') {
		return '—'
	}
	const n = Number(amount)
	return Number.isFinite(n) ? `${n.toFixed(2)} 元` : String(amount)
}

function loadDetail(id: string) {
	loading.value = true
	detail.value = null
	service.get('/mgt/dating/dtMatchmakingCompany/queryById', { params: { id } }).then(res => {
		detail.value = res.data || null
	}).finally(() => {
		loading.value = false
	})
}

const init = (id: string) => {
	rejectForm.rejectReason = ''
	visible.value = true
	loadDetail(id)
}

function approveHandle() {
	if (!detail.value?.id) {
		return
	}
	ElMessageBox.confirm('确认已通过公户认证款项核对，并通过该企业入驻申请？', '审核通过', {
		type: 'warning'
	}).then(() => {
		approving.value = true
		service.post('/mgt/dating/dtMatchmakingCompany/approve', null, { params: { id: detail.value!.id } }).then(() => {
			ElMessage.success('审核通过')
			visible.value = false
			emit('refresh')
		}).finally(() => {
			approving.value = false
		})
	}).catch(() => {})
}

function rejectHandle() {
	if (!detail.value?.id || !rejectFormRef.value) {
		return
	}
	rejectFormRef.value.validate(valid => {
		if (!valid) {
			return
		}
		ElMessageBox.confirm('确认驳回该企业入驻申请？', '审核驳回', { type: 'warning' }).then(() => {
			rejecting.value = true
			service.post('/mgt/dating/dtMatchmakingCompany/reject', {
				id: detail.value!.id,
				rejectReason: rejectForm.rejectReason
			}).then(() => {
				ElMessage.success('已驳回')
				visible.value = false
				emit('refresh')
			}).finally(() => {
				rejecting.value = false
			})
		}).catch(() => {})
	})
}

function onClosed() {
	detail.value = null
	rejectForm.rejectReason = ''
}

defineExpose({ init })
</script>

<style scoped>
.audit-desc {
	margin-bottom: 16px;
}
.photo-block {
	margin-bottom: 16px;
}
.photo-label {
	font-size: 14px;
	color: #606266;
	margin-bottom: 8px;
}
.photo-list {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}
.photo-item {
	width: 120px;
	height: 120px;
	border-radius: 6px;
}
.verify-amount {
	color: #e6a23c;
	font-weight: 600;
}
.audit-tip {
	margin-bottom: 12px;
}
.reject-form {
	margin-top: 8px;
}
</style>
