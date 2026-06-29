<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cusCode">
				<el-input v-model="state.queryForm.cusCode" placeholder="客户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cusNickName">
				<el-input v-model="state.queryForm.cusNickName" placeholder="客户昵称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="submitMkName">
				<el-input v-model="state.queryForm.submitMkName" placeholder="提交红娘" clearable></el-input>
			</el-form-item>
			<el-form-item prop="assetCertProcessCode">
				<el-select v-model="state.queryForm.assetCertProcessCode" placeholder="审核状态" clearable style="width: 130px">
					<el-option label="审核中" value="1" />
					<el-option label="审核通过" value="2" />
					<el-option label="审核拒绝" value="3" />
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
			<el-table-column prop="cusNickName" label="客户昵称" header-align="center" align="center" min-width="110" show-overflow-tooltip></el-table-column>
			<el-table-column prop="cusCode" label="客户编码" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="submitMkName" label="提交红娘" header-align="center" align="center" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="assetCertProcessCode" label="审核状态" header-align="center" align="center" width="100">
				<template #default="scope">
					<el-tag :type="processTagType(scope.row.assetCertProcessCode)">
						{{ processLabel(scope.row.assetCertProcessCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="提交时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="auditAt" label="审核时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="rejectReason" label="驳回原因" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column label="证件照片" header-align="center" align="center" width="120">
				<template #default="scope">
					<el-button type="primary" link @click="openDetail(scope.row)">查看</el-button>
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="160">
				<template #default="scope">
					<template v-if="isReviewing(scope.row.assetCertProcessCode)">
						<el-button v-auth="'vtAssetCertApprove'" type="primary" link @click="approveHandle(scope.row.id)">通过</el-button>
						<el-button v-auth="'vtAssetCertReject'" type="danger" link @click="openReject(scope.row.id)">驳回</el-button>
					</template>
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

	<el-dialog v-model="detailVisible" title="证件照片" width="720px">
		<div v-if="detailRow" class="photo-grid">
			<div class="photo-item">
				<div class="photo-label">行驶证</div>
				<el-image v-if="detailRow.vehicleLicensePhoto" :src="detailRow.vehicleLicensePhoto" fit="contain" class="photo-img" :preview-src-list="[detailRow.vehicleLicensePhoto]" />
				<span v-else class="processed-tip">未上传</span>
			</div>
			<div class="photo-item">
				<div class="photo-label">房产证</div>
				<el-image v-if="detailRow.realEstateCertificatePhoto" :src="detailRow.realEstateCertificatePhoto" fit="contain" class="photo-img" :preview-src-list="[detailRow.realEstateCertificatePhoto]" />
				<span v-else class="processed-tip">未上传</span>
			</div>
			<div class="photo-item">
				<div class="photo-label">婚姻状态证明</div>
				<el-image v-if="detailRow.maritalStatusProofPhoto" :src="detailRow.maritalStatusProofPhoto" fit="contain" class="photo-img" :preview-src-list="[detailRow.maritalStatusProofPhoto]" />
				<span v-else class="processed-tip">未上传</span>
			</div>
			<div class="photo-item photo-item--full">
				<div class="photo-label">诚实守信录制视频</div>
				<video
					v-if="detailRow.honestyVideoFile"
					class="video-preview"
					:src="detailRow.honestyVideoFile"
					controls
					preload="metadata"
				/>
				<el-link v-else-if="detailRow.honestyVideoFile" :href="detailRow.honestyVideoFile" target="_blank" type="primary">打开视频</el-link>
				<span v-else class="processed-tip">未上传</span>
			</div>
		</div>
	</el-dialog>

	<el-dialog v-model="rejectVisible" title="驳回原因" width="480px">
		<el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请填写驳回原因" maxlength="500" show-word-limit />
		<template #footer>
			<el-button @click="rejectVisible = false">取消</el-button>
			<el-button type="danger" @click="confirmReject">确认驳回</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="VtAssetCertIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const PROCESS_MAP: Record<string, string> = {
	REVIEWING: '审核中',
	APPROVED: '审核通过',
	REJECTED: '审核拒绝',
	'1': '审核中',
	'2': '审核通过',
	'3': '审核拒绝'
}

const queryRef = ref()
const detailVisible = ref(false)
const detailRow = ref<any>(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectId = ref('')

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/verification/vtAssetCert/list',
	queryForm: {
		cusCode: '',
		cusNickName: '',
		submitMkName: '',
		assetCertProcessCode: ''
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
	if (code === '2' || code === 'APPROVED')
		return 'success'
	if (code === '1' || code === 'REVIEWING')
		return 'warning'
	if (code === '3' || code === 'REJECTED')
		return 'danger'
	return 'info'
}

function isReviewing(raw: unknown): boolean {
	const code = processCode(raw)
	return code === '1' || code === 'REVIEWING'
}

function openDetail(row: any) {
	detailRow.value = row
	detailVisible.value = true
}

async function approveHandle(id: string) {
	await ElMessageBox.confirm('确认审核通过？通过后将更新客户车产、房产、婚姻状态认证信息。', '确认通过', {
		type: 'warning',
		confirmButtonText: '通过',
		cancelButtonText: '取消'
	})
	await service.post('/mgt/verification/vtAssetCert/approve', null, { params: { id } })
	ElMessage.success('审核通过')
	getDataList()
}

function openReject(id: string) {
	rejectId.value = id
	rejectReason.value = ''
	rejectVisible.value = true
}

async function confirmReject() {
	if (!rejectReason.value.trim()) {
		ElMessage.warning('请填写驳回原因')
		return
	}
	await service.post('/mgt/verification/vtAssetCert/reject', {
		id: rejectId.value,
		rejectReason: rejectReason.value.trim()
	})
	ElMessage.success('已驳回')
	rejectVisible.value = false
	getDataList()
}
</script>

<style scoped>
.processed-tip {
	color: var(--el-text-color-placeholder);
}
.photo-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 16px;
}
.photo-item--full {
	grid-column: 1 / -1;
}
.video-preview {
	width: 100%;
	max-height: 280px;
	border-radius: 8px;
	background: #000;
}
.photo-label {
	margin-bottom: 8px;
	font-weight: 600;
}
.photo-img {
	width: 100%;
	height: 180px;
	border-radius: 8px;
	background: #f5f5f5;
}
</style>
