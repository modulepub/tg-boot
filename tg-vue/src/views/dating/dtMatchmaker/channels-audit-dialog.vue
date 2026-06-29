<template>
	<el-dialog v-model="visible" title="视频号审核" width="560px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<div v-loading="loading">
			<template v-if="detail">
				<el-descriptions :column="1" border size="small" class="audit-desc">
					<el-descriptions-item label="红娘姓名">{{ detail.mkName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="用户号">{{ detail.mkUserCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="婚介所">{{ detail.mkCompanyName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="视频号 ID">{{ detail.mkChannelsFinderUserName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="生效状态">
						<el-tag :type="isChannelsEffective(detail.mkChannelsAuditStatusCode) ? 'success' : 'info'">
							{{ isChannelsEffective(detail.mkChannelsAuditStatusCode) ? '已生效' : '未生效' }}
						</el-tag>
					</el-descriptions-item>
					<el-descriptions-item label="审核流程">
						<el-tag :type="processTagType(detail.mkChannelsProcessCode)">
							{{ channelsProcessLabel(detail.mkChannelsProcessCode) }}
						</el-tag>
					</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkChannelsRejectReason" label="失败原因">
						{{ detail.mkChannelsRejectReason }}
					</el-descriptions-item>
					<el-descriptions-item v-if="detail.mkChannelsAuditAt" label="审核时间">
						{{ detail.mkChannelsAuditAt || '—' }}
					</el-descriptions-item>
				</el-descriptions>

				<el-form v-if="canAudit" ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="90px" class="reject-form">
					<el-form-item label="失败原因" prop="rejectReason">
						<el-input
							v-model="rejectForm.rejectReason"
							type="textarea"
							:rows="3"
							placeholder="审核失败时必填"
							maxlength="500"
							show-word-limit
						/>
					</el-form-item>
				</el-form>
			</template>
		</div>

		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
			<template v-if="canAudit">
				<el-button v-auth="'datingDtMatchmakerAudit'" type="danger" :loading="rejecting" @click="rejectHandle">审核失败</el-button>
				<el-button v-auth="'datingDtMatchmakerAudit'" type="primary" :loading="approving" @click="approveHandle">审核通过</el-button>
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
	rejectReason: [{ required: true, message: '请填写失败原因', trigger: 'blur' }]
}

function processKey(code: unknown) {
	const raw = code
	if (raw != null && typeof raw === 'object' && 'code' in (raw as object)) {
		return String((raw as { code?: string }).code ?? '').trim()
	}
	return String(raw ?? '').trim()
}

function channelsProcessLabel(code: unknown) {
	const key = processKey(code)
	if (key === 'DRAFT' || key === '0') return '待提交'
	if (key === 'REVIEWING' || key === '1') return '待审核'
	if (key === 'APPROVED' || key === '2') return '审核通过'
	if (key === 'REJECTED' || key === '3') return '审核失败'
	return key || '—'
}

function processTagType(code: unknown) {
	const key = processKey(code)
	if (key === 'REVIEWING' || key === '1') return 'warning'
	if (key === 'APPROVED' || key === '2') return 'success'
	if (key === 'REJECTED' || key === '3') return 'danger'
	return 'info'
}

function isChannelsEffective(code: unknown) {
	const key = processKey(code)
	return key === 'YES' || key === '1' || key === 'true'
}

const canAudit = computed(() => {
	if (!detail.value) {
		return false
	}
	const key = processKey(detail.value.mkChannelsProcessCode)
	return key === 'REVIEWING' || key === '1'
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
	ElMessageBox.confirm('确认通过该视频号配置？通过后将可在小程序展示入口。', '审核通过', { type: 'warning' }).then(() => {
		approving.value = true
		service.post('/mgt/dating/dtMatchmaker/approveChannels', null, { params: { id: detail.value!.id } }).then(() => {
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
		ElMessageBox.confirm('确认驳回该视频号配置？', '审核失败', { type: 'warning' }).then(() => {
			rejecting.value = true
			service.post('/mgt/dating/dtMatchmaker/rejectChannels', {
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
.reject-form {
	margin-top: 8px;
}
</style>
