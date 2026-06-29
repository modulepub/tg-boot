<template>
	<el-dialog v-model="visible" :title="dialogTitle" width="980px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<el-table v-loading="loading" :data="records" border class="layout-table">
			<el-table-column prop="createTime" label="阅读时间" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="nodeReadRecordUserCode" label="用户编码" min-width="120" show-overflow-tooltip>
				<template #default="scope">
					{{ scope.row.nodeReadRecordUserCode || '—' }}
				</template>
			</el-table-column>
			<el-table-column prop="userNickName" label="昵称" min-width="100" show-overflow-tooltip>
				<template #default="scope">
					{{ scope.row.userNickName || '—' }}
				</template>
			</el-table-column>
			<el-table-column prop="userPhone" label="手机号" min-width="120" show-overflow-tooltip>
				<template #default="scope">
					{{ scope.row.userPhone || '—' }}
				</template>
			</el-table-column>
			<el-table-column prop="nodeReadRecordClientIp" label="IP" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="nodeReadRecordIpLocation" label="IP归属地" min-width="160" show-overflow-tooltip>
				<template #default="scope">
					{{ scope.row.nodeReadRecordIpLocation || '—' }}
				</template>
			</el-table-column>
			<el-table-column prop="nodeReadRecordProgress" label="阅读进度" width="100" align="center">
				<template #default="scope">
					{{ Number(scope.row.nodeReadRecordProgress ?? 0) }}%
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			class="layout-pagination"
			:current-page="pageNo"
			:page-size="pageSize"
			:total="total"
			layout="total, prev, pager, next"
			@current-change="handlePageChange"
		></el-pagination>
		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const visible = ref(false)
const loading = ref(false)
const records = ref<Record<string, any>[]>([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const nodeCode = ref('')
const nodeName = ref('')

const dialogTitle = computed(() => {
	const name = String(nodeName.value || '').trim()
	return name ? `阅读记录 - ${name}` : '阅读记录'
})

const onClosed = () => {
	records.value = []
	pageNo.value = 1
	total.value = 0
	nodeCode.value = ''
	nodeName.value = ''
}

const loadRecords = async () => {
	const code = String(nodeCode.value || '').trim()
	if (!code) {
		return
	}
	loading.value = true
	try {
		const res: any = await service.get('/mgt/cms/cmsNodeReadRecord/list', {
			params: {
				nodeCode: code,
				pageNo: pageNo.value,
				pageSize: pageSize.value
			}
		})
		records.value = res?.data?.records ?? []
		total.value = Number(res?.data?.total ?? 0)
	} catch {
		records.value = []
		total.value = 0
	} finally {
		loading.value = false
	}
}

const handlePageChange = (value: number) => {
	pageNo.value = value
	void loadRecords()
}

const init = async (row: Record<string, any>) => {
	const code = String(row?.nodeCode || '').trim()
	if (!code) {
		ElMessage.warning('缺少文章编码')
		return
	}
	nodeCode.value = code
	nodeName.value = String(row?.nodeName || '').trim()
	pageNo.value = 1
	visible.value = true
	await loadRecords()
}

defineExpose({ init })
</script>

<style scoped>
.layout-pagination {
	margin-top: 16px;
	justify-content: flex-end;
}
</style>
