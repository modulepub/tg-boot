<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="idempotentKey">
				<el-input v-model="state.queryForm.idempotentKey" placeholder="幂等键 / 业务键" clearable></el-input>
			</el-form-item>
			<el-form-item prop="toOpenId">
				<el-input v-model="state.queryForm.toOpenId" placeholder="接收人 openId" clearable></el-input>
			</el-form-item>
			<el-form-item prop="templateId">
				<el-select v-model="state.queryForm.templateId" placeholder="消息场景" clearable style="width: 220px">
					<el-option
						v-for="item in templateOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					/>
				</el-select>
			</el-form-item>
			<el-form-item prop="sendStatusCode">
				<el-select v-model="state.queryForm.sendStatusCode" placeholder="发送状态" clearable style="width: 120px">
					<el-option label="成功" value="1" />
					<el-option label="失败" value="0" />
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
			<el-table-column prop="createTime" label="发送时间" header-align="center" align="center" min-width="160" />
			<el-table-column label="消息场景" header-align="center" align="center" min-width="140">
				<template #default="{ row }">
					{{ resolveSceneName(row) }}
				</template>
			</el-table-column>
			<el-table-column prop="idempotentKey" label="幂等键" header-align="center" align="center" min-width="180" show-overflow-tooltip />
			<el-table-column prop="toOpenId" label="接收 openId" header-align="center" align="center" min-width="200" show-overflow-tooltip />
			<el-table-column prop="sendStatusCode" label="状态" header-align="center" align="center" width="88">
				<template #default="{ row }">
					<el-tag v-if="row.sendStatusCode === '1'" type="success" size="small">成功</el-tag>
					<el-tag v-else-if="row.sendStatusCode === '0'" type="danger" size="small">失败</el-tag>
					<span v-else>—</span>
				</template>
			</el-table-column>
			<el-table-column prop="wxErrCode" label="微信错误码" header-align="center" align="center" width="100" />
			<el-table-column prop="wxErrMsg" label="错误信息" header-align="center" align="center" min-width="160" show-overflow-tooltip />
			<el-table-column prop="jumpPage" label="跳转页" header-align="center" align="center" min-width="200" show-overflow-tooltip />
			<el-table-column prop="sendDataJson" label="发送内容" header-align="center" align="center" min-width="220">
				<template #default="{ row }">
					<span v-if="!row.sendDataJson">—</span>
					<el-button v-else link type="primary" @click="openJsonDialog('发送内容', row.sendDataJson)">查看</el-button>
				</template>
			</el-table-column>
			<el-table-column prop="templateId" label="模板 ID" header-align="center" align="center" min-width="200" show-overflow-tooltip />
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="88">
				<template #default="{ row }">
					<el-button link type="primary" @click="showDetail(row.id)">详情</el-button>
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

	<el-dialog v-model="jsonDialogVisible" :title="jsonDialogTitle" width="720px" destroy-on-close>
		<pre class="json-dialog-body">{{ jsonDialogContent }}</pre>
		<template #footer>
			<el-button @click="jsonDialogVisible = false">关闭</el-button>
		</template>
	</el-dialog>

	<el-dialog v-model="detailVisible" title="订阅消息发送详情" width="760px" destroy-on-close>
		<el-descriptions v-if="detailRow" :column="1" border>
			<el-descriptions-item label="发送时间">{{ detailRow.createTime || '—' }}</el-descriptions-item>
			<el-descriptions-item label="消息场景">{{ resolveSceneName(detailRow) }}</el-descriptions-item>
			<el-descriptions-item label="幂等键">{{ detailRow.idempotentKey || '—' }}</el-descriptions-item>
			<el-descriptions-item label="接收 openId">{{ detailRow.toOpenId || '—' }}</el-descriptions-item>
			<el-descriptions-item label="发送状态">
				<el-tag v-if="detailRow.sendStatusCode === '1'" type="success" size="small">成功</el-tag>
				<el-tag v-else-if="detailRow.sendStatusCode === '0'" type="danger" size="small">失败</el-tag>
				<span v-else>—</span>
			</el-descriptions-item>
			<el-descriptions-item label="微信错误码">{{ detailRow.wxErrCode || '—' }}</el-descriptions-item>
			<el-descriptions-item label="错误信息">{{ detailRow.wxErrMsg || '—' }}</el-descriptions-item>
			<el-descriptions-item label="跳转页">{{ detailRow.jumpPage || '—' }}</el-descriptions-item>
			<el-descriptions-item label="模板 ID">{{ detailRow.templateId || '—' }}</el-descriptions-item>
			<el-descriptions-item label="发送内容">
				<pre v-if="detailRow.sendDataJson" class="json-inline">{{ formatJsonForDisplay(detailRow.sendDataJson) }}</pre>
				<span v-else>—</span>
			</el-descriptions-item>
		</el-descriptions>
		<template #footer>
			<el-button @click="detailVisible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="WxWxMaSubscribeSendLogIndex">
import { onMounted, reactive, ref } from 'vue'
import { useCrud } from '@/hooks'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

interface TemplateOptionRow {
	templateId: string
	sendCount?: number
	sceneName?: string
}

const templateSceneMap = ref<Record<string, string>>({})
const templateOptions = ref<{ value: string; label: string }[]>([])

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/wx/wxMaSubscribeSendLog/list',
	queryForm: {
		idempotentKey: '',
		toOpenId: '',
		templateId: '',
		sendStatusCode: '',
	},
})

const queryRef = ref()
const jsonDialogVisible = ref(false)
const jsonDialogTitle = ref('')
const jsonDialogContent = ref('')
const detailVisible = ref(false)
const detailRow = ref<Record<string, any> | null>(null)

function resolveSceneName(row: { templateId?: string, idempotentKey?: string }) {
	const byTemplate = templateSceneMap.value[row.templateId || '']
	if (byTemplate)
		return byTemplate
	const key = String(row.idempotentKey || '')
	if (key.startsWith('friend_req:'))
		return '收到好友申请通知'
	if (key.startsWith('friend_ok:'))
		return '添加好友成功通知'
	if (key.startsWith('match_req:'))
		return '牵线请求通知'
	if (key.startsWith('free_rec:'))
		return '相亲对象推荐通知'
	return '—'
}

async function loadTemplateOptions() {
	try {
		const res: any = await service.get('/mgt/wx/wxMaSubscribeSendLog/listTemplateOptions')
		const list: TemplateOptionRow[] = res?.data || []
		const sceneMap: Record<string, string> = {}
		templateOptions.value = list.map((item) => {
			const value = String(item.templateId || '').trim()
			const sceneName = String(item.sceneName || value).trim()
			if (value)
				sceneMap[value] = sceneName
			const count = Number(item.sendCount ?? 0)
			const label = count > 0 ? `${sceneName}（${count}）` : sceneName
			return { value, label }
		})
		templateSceneMap.value = sceneMap
	}
	catch {
		templateOptions.value = []
		templateSceneMap.value = {}
	}
}

function formatJsonForDisplay(text: string) {
	if (!text || !text.trim())
		return ''
	try {
		return JSON.stringify(JSON.parse(text), null, 2)
	}
	catch {
		return text
	}
}

function openJsonDialog(title: string, text: string) {
	jsonDialogTitle.value = title
	jsonDialogContent.value = formatJsonForDisplay(text)
	jsonDialogVisible.value = true
}

function showDetail(id: string) {
	if (!id)
		return
	service.get('/mgt/wx/wxMaSubscribeSendLog/queryById', { params: { id } }).then((res: any) => {
		detailRow.value = res.data || null
		detailVisible.value = true
	})
}

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

onMounted(() => {
	void loadTemplateOptions()
})
</script>

<style scoped>
.json-dialog-body,
.json-inline {
	margin: 0;
	max-height: 60vh;
	overflow: auto;
	padding: 12px;
	background: var(--el-fill-color-light);
	border-radius: 4px;
	font-size: 12px;
	line-height: 1.5;
	white-space: pre-wrap;
	word-break: break-all;
}
</style>
