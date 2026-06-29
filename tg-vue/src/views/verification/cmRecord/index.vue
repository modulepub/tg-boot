image.png<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="cmRecordBizCode">
				<el-input v-model="state.queryForm.cmRecordBizCode" placeholder="业务编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cmRecordUserCode">
				<el-input v-model="state.queryForm.cmRecordUserCode" placeholder="用户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cmRecordUserName">
				<el-input v-model="state.queryForm.cmRecordUserName" placeholder="姓名" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cmRecordSourceModuleCode">
				<el-input v-model="state.queryForm.cmRecordSourceModuleCode" placeholder="来源模块" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cmRecordPluginCode">
				<el-input v-model="state.queryForm.cmRecordPluginCode" placeholder="插件编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="cmRecordContentTypeCode">
				<el-select v-model="state.queryForm.cmRecordContentTypeCode" placeholder="内容类型" clearable style="width: 120px">
					<el-option label="文字" value="TEXT" />
					<el-option label="图片" value="IMAGE" />
					<el-option label="视频" value="VIDEO" />
				</el-select>
			</el-form-item>
			<el-form-item prop="cmRecordProcessCode">
				<el-select v-model="state.queryForm.cmRecordProcessCode" placeholder="流程状态" clearable style="width: 120px">
					<el-option label="待审核" value="0" />
					<el-option label="审核中" value="1" />
					<el-option label="审核结束" value="2" />
				</el-select>
			</el-form-item>
			<el-form-item prop="cmRecordPassedStatusCode">
				<el-select v-model="state.queryForm.cmRecordPassedStatusCode" placeholder="是否通过" clearable style="width: 120px">
					<el-option label="否" value="0" />
					<el-option label="是" value="1" />
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
		<el-space class="layout-toolbar">
			<el-button v-auth="'cmRecordApprove'" icon="Select" plain type="success" @click="batchApprove()">批量通过</el-button>
			<el-button v-auth="'cmRecordReject'" icon="CloseBold" plain type="warning" @click="batchReject()">批量驳回</el-button>
			<el-button v-auth="'cmRecordDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
		</el-space>
		<el-table
			v-loading="state.dataListLoading"
			:data="state.dataList"
			border
			class="layout-table"
			row-key="cmRecordCode"
			@selection-change="selectionChangeHandle"
		>
			<el-table-column type="selection" reserve-selection width="50" align="center" />
			<el-table-column prop="cmRecordContentTypeCode" label="类型" width="80" align="center" />
			<el-table-column prop="cmRecordContent" label="审核内容" min-width="180" show-overflow-tooltip>
				<template #default="scope">
					<el-image
						v-if="isImageContent(scope.row)"
						:src="scope.row.cmRecordContent"
						fit="cover"
						class="thumb"
						@click="openMedia(scope.row)"
					/>
					<div v-else-if="isVideoContent(scope.row)" class="thumb video-thumb" @click="openMedia(scope.row)">
						<video :src="scope.row.cmRecordContent" preload="metadata" muted />
						<el-icon class="video-play"><VideoPlay /></el-icon>
					</div>
					<span v-else>{{ scope.row.cmRecordContent }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="cmRecordPluginCode" label="插件" width="140" show-overflow-tooltip align="center">
				<template #default="scope">
					{{ scope.row.cmRecordPluginCode || '人工审核' }}
				</template>
			</el-table-column>
			<el-table-column prop="cmRecordAsyncStatusCode" label="异步" width="72" align="center">
				<template #default="scope">
					<el-tag v-if="isStatusYes(scope.row.cmRecordAsyncStatusCode)" type="warning">是</el-tag>
					<span v-else class="muted">否</span>
				</template>
			</el-table-column>
			<el-table-column prop="cmRecordProcessCode" label="流程" width="100" align="center">
				<template #default="scope">
					<el-tag :type="processTagType(scope.row.cmRecordProcessCode)">
						{{ processLabel(scope.row.cmRecordProcessCode) }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="cmRecordPassedStatusCode" label="是否通过" width="88" align="center">
				<template #default="scope">
					<template v-if="isFinished(scope.row.cmRecordProcessCode)">
						<el-tag v-if="isStatusYes(scope.row.cmRecordPassedStatusCode)" type="success">是</el-tag>
						<el-tag v-else type="danger">否</el-tag>
					</template>
					<span v-else class="muted">—</span>
				</template>
			</el-table-column>
			<el-table-column prop="cmRecordNotPassedReason" label="未通过原因" min-width="160" show-overflow-tooltip align="center" />
			<el-table-column prop="cmRecordBizCode" label="业务编码" min-width="120" show-overflow-tooltip align="center" />
			<el-table-column prop="cmRecordUserCode" label="用户编码" min-width="120" show-overflow-tooltip align="center" />
			<el-table-column prop="cmRecordUserName" label="姓名" min-width="100" show-overflow-tooltip align="center" />
			<el-table-column prop="createTime" label="提交时间" min-width="160" show-overflow-tooltip align="center" />
			<el-table-column prop="cmRecordAuditAt" label="审核时间" min-width="160" show-overflow-tooltip align="center" />
			<el-table-column label="详情" width="80" align="center">
				<template #default="scope">
					<el-button type="primary" link @click="openDetail(scope.row)">查看</el-button>
				</template>
			</el-table-column>
			<el-table-column label="操作" fixed="right" width="180" align="center">
				<template #default="scope">
					<template v-if="canAudit(scope.row.cmRecordProcessCode)">
						<el-button v-auth="'cmRecordApprove'" type="primary" link @click="approveHandle(scope.row.id)">通过</el-button>
						<el-button v-auth="'cmRecordReject'" type="danger" link @click="openReject(scope.row.id)">驳回</el-button>
					</template>
					<el-button v-auth="'cmRecordDelete'" type="danger" link @click="deleteBatchHandle(scope.row.cmRecordCode)">删除</el-button>
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
	</el-card>

	<el-dialog v-model="detailVisible" title="审核详情" width="760px">
		<el-descriptions v-if="detailRow" :column="2" border>
			<el-descriptions-item label="记录编码">{{ detailRow.cmRecordCode }}</el-descriptions-item>
			<el-descriptions-item label="来源模块">{{ detailRow.cmRecordSourceModuleCode }}</el-descriptions-item>
			<el-descriptions-item label="业务编码">{{ detailRow.cmRecordBizCode || '—' }}</el-descriptions-item>
			<el-descriptions-item label="用户编码">{{ detailRow.cmRecordUserCode || '—' }}</el-descriptions-item>
			<el-descriptions-item label="姓名">{{ detailRow.cmRecordUserName || '—' }}</el-descriptions-item>
			<el-descriptions-item label="内容类型">{{ detailRow.cmRecordContentTypeCode }}</el-descriptions-item>
			<el-descriptions-item label="插件">{{ detailRow.cmRecordPluginCode || '人工审核' }}</el-descriptions-item>
			<el-descriptions-item label="trace_id" :span="2">{{ detailRow.cmRecordVendorTraceId || '—' }}</el-descriptions-item>
			<el-descriptions-item label="审核内容" :span="2">
				<div class="content-block">{{ detailRow.cmRecordContent }}</div>
				<el-image
					v-if="isImageContent(detailRow)"
					:src="detailRow.cmRecordContent"
					fit="contain"
					class="preview-img"
					:preview-src-list="[detailRow.cmRecordContent]"
				/>
				<video
					v-else-if="isVideoContent(detailRow)"
					class="preview-video"
					:src="detailRow.cmRecordContent"
					controls
					preload="metadata"
				/>
			</el-descriptions-item>
			<el-descriptions-item label="备注" :span="2">
				<pre class="remark-pre">{{ detailRow.cmRecordRemark || '—' }}</pre>
			</el-descriptions-item>
			<el-descriptions-item v-if="detailRow.cmRecordNotPassedReason" label="未通过原因" :span="2">
				{{ detailRow.cmRecordNotPassedReason }}
			</el-descriptions-item>
		</el-descriptions>
	</el-dialog>

	<el-dialog v-model="mediaVisible" title="内容预览" width="640px" align-center destroy-on-close>
		<div class="media-preview-wrap">
			<el-image
				v-if="mediaRow && isImageContent(mediaRow)"
				:src="mediaRow.cmRecordContent"
				fit="contain"
				class="media-preview-img"
				:preview-src-list="[mediaRow.cmRecordContent]"
			/>
			<video
				v-else-if="mediaRow && isVideoContent(mediaRow)"
				:src="mediaRow.cmRecordContent"
				class="media-preview-video"
				controls
				autoplay
			/>
		</div>
		<template v-if="mediaRow && canAudit(mediaRow.cmRecordProcessCode)" #footer>
			<el-button v-auth="'cmRecordApprove'" type="primary" @click="approveFromMedia">通过</el-button>
			<el-button v-auth="'cmRecordReject'" type="danger" @click="rejectFromMedia">驳回</el-button>
		</template>
	</el-dialog>

	<el-dialog v-model="rejectVisible" title="驳回原因" width="480px">
		<el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请填写驳回原因" maxlength="500" show-word-limit />
		<template #footer>
			<el-button @click="rejectVisible = false">取消</el-button>
			<el-button type="danger" @click="confirmReject">确认驳回</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="CmRecordIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const PROCESS_MAP: Record<string, string> = {
	PENDING: '待审核',
	REVIEWING: '审核中',
	FINISHED: '审核结束',
	'0': '待审核',
	'1': '审核中',
	'2': '审核结束'
}

const queryRef = ref()
const detailVisible = ref(false)
const detailRow = ref<any>(null)
const mediaVisible = ref(false)
const mediaRow = ref<any>(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectIds = ref<string[]>([])

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/verification/cmRecord/list',
	deleteUrl: '/mgt/verification/cmRecord/delete',
	primaryKey: 'cmRecordCode',
	queryForm: {
		cmRecordBizCode: '',
		cmRecordUserCode: '',
		cmRecordUserName: '',
		cmRecordSourceModuleCode: '',
		cmRecordPluginCode: '',
		cmRecordContentTypeCode: '',
		cmRecordProcessCode: '',
		cmRecordPassedStatusCode: ''
	}
})

const { getDataList, sizeChangeHandle, currentChangeHandle, selectionChangeHandle, deleteBatchHandle, reset } = useCrud(state)

function enumCode(raw: unknown): string {
	if (raw == null || raw === '') return ''
	if (typeof raw === 'object' && raw !== null && 'code' in raw) return String((raw as { code: string }).code)
	return String(raw)
}

function isStatusYes(code: unknown): boolean {
	return enumCode(code) === '1'
}

function processLabel(raw: unknown): string {
	const code = enumCode(raw)
	return PROCESS_MAP[code] || code || '—'
}

function processTagType(raw: unknown): '' | 'success' | 'warning' | 'info' | 'danger' {
	const code = enumCode(raw)
	if (code === '2' || code === 'FINISHED') return 'success'
	if (code === '1' || code === 'REVIEWING') return 'warning'
	if (code === '0' || code === 'PENDING') return 'info'
	return 'info'
}

function canAudit(raw: unknown): boolean {
	const code = enumCode(raw)
	// 待审核与审核中均可人工裁决（审核中多为第三方异步未回调，需人工兜底）
	return code === '0' || code === 'PENDING' || code === '1' || code === 'REVIEWING'
}

function isFinished(raw: unknown): boolean {
	const code = enumCode(raw)
	return code === '2' || code === 'FINISHED'
}

function isImageContent(row: any): boolean {
	return row?.cmRecordContentTypeCode === 'IMAGE' && row?.cmRecordContent
}

function isVideoContent(row: any): boolean {
	return row?.cmRecordContentTypeCode === 'VIDEO' && row?.cmRecordContent
}

function openDetail(row: any) {
	detailRow.value = row
	detailVisible.value = true
}

function openMedia(row: any) {
	mediaRow.value = row
	mediaVisible.value = true
}

function approveFromMedia() {
	const row = mediaRow.value
	mediaVisible.value = false
	approveHandle(row.id)
}

function rejectFromMedia() {
	const row = mediaRow.value
	mediaVisible.value = false
	openReject(row.id)
}

async function approveHandle(id: string) {
	await ElMessageBox.confirm('确认人工审核通过该内容？', '确认通过', {
		type: 'warning',
		confirmButtonText: '通过',
		cancelButtonText: '取消'
	})
	await service.post('/mgt/verification/cmRecord/approve', null, { params: { id } })
	ElMessage.success('审核通过')
	getDataList()
}

function openReject(id: string) {
	rejectIds.value = [id]
	rejectReason.value = ''
	rejectVisible.value = true
}

function auditableSelections(): any[] {
	const rows = state.dataListSelections || []
	return rows.filter((row: any) => canAudit(row.cmRecordProcessCode))
}

async function batchApprove() {
	const rows = auditableSelections()
	if (!rows.length) {
		ElMessage.warning('请勾选待审核的记录')
		return
	}
	await ElMessageBox.confirm(`确认人工审核通过选中的 ${rows.length} 条内容？`, '批量通过', {
		type: 'warning',
		confirmButtonText: '通过',
		cancelButtonText: '取消'
	})
	for (const row of rows) {
		await service.post('/mgt/verification/cmRecord/approve', null, { params: { id: row.id } })
	}
	ElMessage.success(`已通过 ${rows.length} 条`)
	getDataList()
}

function batchReject() {
	const rows = auditableSelections()
	if (!rows.length) {
		ElMessage.warning('请勾选待审核的记录')
		return
	}
	rejectIds.value = rows.map((row: any) => row.id)
	rejectReason.value = ''
	rejectVisible.value = true
}

async function confirmReject() {
	if (!rejectReason.value.trim()) {
		ElMessage.warning('请填写驳回原因')
		return
	}
	const reason = rejectReason.value.trim()
	for (const id of rejectIds.value) {
		await service.post('/mgt/verification/cmRecord/reject', { id, rejectReason: reason })
	}
	ElMessage.success('已驳回')
	rejectVisible.value = false
	getDataList()
}
</script>

<style scoped>
.layout-toolbar {
	margin-bottom: 12px;
}
.muted {
	color: var(--el-text-color-placeholder);
}
.content-block {
	word-break: break-all;
	margin-bottom: 8px;
}
.remark-pre {
	margin: 0;
	white-space: pre-wrap;
	word-break: break-all;
	font-family: inherit;
	font-size: 13px;
	max-height: 240px;
	overflow: auto;
}
.preview-img {
	max-width: 100%;
	max-height: 280px;
	margin-top: 8px;
	border-radius: 8px;
}
.preview-video {
	width: 100%;
	max-height: 280px;
	margin-top: 8px;
	border-radius: 8px;
	background: #000;
}
.thumb {
	width: 56px;
	height: 56px;
	border-radius: 6px;
	cursor: pointer;
	display: inline-block;
	vertical-align: middle;
	border: 1px solid var(--el-border-color-lighter);
}
.video-thumb {
	position: relative;
	overflow: hidden;
	background: #000;
}
.video-thumb video {
	width: 100%;
	height: 100%;
	object-fit: cover;
}
.video-thumb .video-play {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	font-size: 22px;
	color: #fff;
	opacity: 0.9;
	pointer-events: none;
}
.media-preview-wrap {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 200px;
}
.media-preview-img {
	max-width: 100%;
	max-height: 60vh;
}
.media-preview-video {
	max-width: 100%;
	max-height: 60vh;
	background: #000;
	border-radius: 8px;
}
</style>
