<template>
	<el-dialog v-model="visible" title="红娘资质平台审核" width="720px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<div v-loading="loading">
			<template v-if="detail">
				<div v-if="detail.mkWorkPhoto" class="photo-block">
					<div class="photo-label">工作照</div>
					<el-image
						:src="detail.mkWorkPhoto"
						fit="cover"
						class="photo-item"
						preview-teleported
						:preview-src-list="[detail.mkWorkPhoto]"
					/>
				</div>

				<el-descriptions :column="2" border size="small" class="audit-desc">
					<el-descriptions-item label="姓名">{{ detail.mkName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="用户号">{{ detail.mkUserCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="电话">{{ detail.mkPhone || '—' }}</el-descriptions-item>
					<el-descriptions-item label="年龄">{{ detail.mkAge ?? '—' }}</el-descriptions-item>
					<el-descriptions-item label="证件号" :span="2">{{ detail.mkIdNo || '—' }}</el-descriptions-item>
					<el-descriptions-item label="所在城市">{{ detail.mkCityName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="婚介所">{{ detail.mkCompanyName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="标签" :span="2">{{ detail.mkTags || '—' }}</el-descriptions-item>
					<el-descriptions-item label="说说" :span="2">{{ detail.mkMoment || '—' }}</el-descriptions-item>
					<el-descriptions-item label="视频号" :span="2">{{ detail.mkChannelsFinderUserName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="审核状态">
						<el-tag :type="processTagType(detail.mkIdentityProcessCode)">
							{{ processLabel(detail.mkIdentityProcessCode) }}
						</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="已认证">
						<el-tag :type="isCertified(detail.mkIdentityStatusCode) ? 'success' : 'info'">
							{{ isCertified(detail.mkIdentityStatusCode) ? '是' : '否' }}
						</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="申请时间" :span="2">{{ detail.createTime || '—' }}</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkVideoCommitmentFile" label="视频承诺文件" :span="2">
						<el-link :href="detail.mkVideoCommitmentFile" target="_blank" type="primary">查看文件</el-link>
					</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkServiceAgreementFile" label="服务协议文件" :span="2">
						<el-link :href="detail.mkServiceAgreementFile" target="_blank" type="primary">查看文件</el-link>
					</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkEnterpriseAuditAt" label="企业审核时间" :span="2">{{ detail.mkEnterpriseAuditAt || '—' }}</el-descriptions-item>
				</el-descriptions>

				<el-alert
					v-if="canAudit"
					type="info"
					:closable="false"
					show-icon
					class="audit-tip"
					title="平台审核：企业已通过并上传附件，确认资质后可对外展示。"
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

				<el-descriptions v-else-if="detail.mkPlatformRejectReason || detail.mkIdentityRejectReason" :column="1" border size="small" class="audit-desc">
					<el-descriptions-item v-if="detail.mkIdentityRejectReason" label="企业驳回原因">{{ detail.mkIdentityRejectReason }}</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkPlatformRejectReason" label="平台驳回原因">{{ detail.mkPlatformRejectReason }}</el-descriptions-item>
					<el-descriptions-item label="审核人">{{ detail.mkPlatformAuditBy || detail.mkIdentityAuditBy || '—' }}</el-descriptions-item>
					<el-descriptions-item label="审核时间">{{ detail.mkPlatformAuditAt || detail.mkIdentityAuditAt || '—' }}</el-descriptions-item>
				</el-descriptions>
			</template>
		</div>

		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
			<template v-if="canAudit">
				<el-button v-auth="'datingDtMatchmakerAudit'" type="danger" :loading="rejecting" @click="rejectHandle">驳回</el-button>
				<el-button v-auth="'datingDtMatchmakerAudit'" type="primary" :loading="approving" @click="approveHandle">通过</el-button>
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

const canAudit = computed(() => {
	if (!detail.value) {
		return false
	}
	const key = processKey(detail.value.mkIdentityProcessCode)
	return key === 'PLATFORM_REVIEWING' || key === '4'
})

function loadDetail(id: string) {
	loading.value = true
	detail.value = null
	service.get('/mgt/dating/dtMatchmaker/queryById', { params: { id } }).then(res => {
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
	ElMessageBox.confirm('确认通过该红娘资质平台审核？', '审核通过', { type: 'warning' }).then(() => {
		approving.value = true
		service.post('/mgt/dating/dtMatchmaker/approve', null, { params: { id: detail.value!.id } }).then(() => {
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
		ElMessageBox.confirm('确认驳回该红娘资质申请？', '审核驳回', { type: 'warning' }).then(() => {
			rejecting.value = true
			service.post('/mgt/dating/dtMatchmaker/reject', {
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
.photo-item {
	width: 120px;
	height: 120px;
	border-radius: 6px;
}
.audit-tip {
	margin-bottom: 12px;
}
.reject-form {
	margin-top: 8px;
}
</style>
