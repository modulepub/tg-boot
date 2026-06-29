<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="smsProviderCode">
				<el-input v-model="state.queryForm.smsProviderCode" placeholder="渠道编码"></el-input>
			</el-form-item>
			<el-form-item prop="smsTemplateCode">
				<el-input v-model="state.queryForm.smsTemplateCode" placeholder="模板编码"></el-input>
			</el-form-item>
			<el-form-item prop="mobile">
				<el-input v-model="state.queryForm.mobile" placeholder="手机号"></el-input>
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
		<el-space style="margin-bottom: 16px;">
			<el-button icon="Plus" type="primary" @click="showSendDialog()">发送短信</el-button>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="smsProviderCode" label="渠道" header-align="center" align="center" min-width="100"></el-table-column>
			<el-table-column prop="smsTemplateCode" label="模板编码" header-align="center" align="center" min-width="140" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mobile" label="手机号" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="successCode" label="是否成功" header-align="center" align="center" width="100">
				<template #default="{ row }">
					<el-tag v-if="row.successCode === '1'" type="success" size="small">成功</el-tag>
					<el-tag v-else-if="row.successCode === '0'" type="danger" size="small">失败</el-tag>
					<span v-else>—</span>
				</template>
			</el-table-column>
			<el-table-column prop="providerRequestJson" label="请求参数" header-align="center" align="center" min-width="220">
				<template #default="{ row }">
					<json-cell :text="resolveRequestJson(row)" />
				</template>
			</el-table-column>
			<el-table-column prop="providerResponseJson" label="返回参数" header-align="center" align="center" min-width="220">
				<template #default="{ row }">
					<json-cell :text="row.providerResponseJson" />
				</template>
			</el-table-column>
			<el-table-column prop="errorMessage" label="错误信息" header-align="center" align="center" min-width="200" show-overflow-tooltip>
				<template #default="{ row }">
					<span :class="{ 'error-text': row.successCode === '0' }">{{ row.errorMessage || '—' }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" label="发送时间" header-align="center" align="center" min-width="160"></el-table-column>
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

	<!-- 发送短信弹窗 -->
	<el-dialog v-model="sendVisible" title="发送短信" :close-on-click-modal="false" width="620px">
		<el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="100px">
			<el-form-item label="渠道" prop="providerCode">
				<el-select v-model="sendForm.providerCode" placeholder="请选择渠道" style="width: 100%">
					<el-option label="腾讯云短信" value="tencent" />
					<el-option label="创蓝短信" value="chuangLan" />
					<el-option label="玄武 MOS" value="mosSmsSdk" />
				</el-select>
			</el-form-item>
			<el-form-item label="短信模板" prop="smsTemplateCode">
				<el-select
					v-model="sendForm.smsTemplateCode"
					placeholder="请选择短信模板"
					style="width: 100%"
					@change="onTemplateChange"
				>
					<el-option
						v-for="tpl in templateList"
						:key="tpl.smsTemplateCode"
						:label="tpl.smsTemplateCode + '（' + (tpl.smsTemplateContent || '无内容') + '）'"
						:value="tpl.smsTemplateCode"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="手机号" prop="mobile">
				<el-input v-model="sendForm.mobile" placeholder="13800138000"></el-input>
			</el-form-item>

			<!-- 动态参数输入 -->
			<el-form-item v-if="paramInputs.length > 0" label="模板参数">
				<div style="width: 100%">
					<div v-for="(p, idx) in paramInputs" :key="idx" style="margin-bottom: 8px; display: flex; align-items: center;">
						<span style="width: 60px; color: #909399;">参数 {{ idx + 1 }}</span>
						<el-input v-model="p.value" placeholder="请输入参数值" style="flex: 1" @input="buildTemplateParamsJson" />
					</div>
					<div style="margin-top: 8px; padding: 8px; background: #f5f7fa; border-radius: 4px; font-size: 12px; color: #606266;">
						<strong>构造的 JSON 数组：</strong>
						<code style="color: #409eff;">{{ sendForm.templateParams || '[]' }}</code>
					</div>
				</div>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="sendVisible = false">取消</el-button>
			<el-button type="primary" @click="submitSend()">发送</el-button>
		</template>
	</el-dialog>

	<el-dialog v-model="jsonDialogVisible" :title="jsonDialogTitle" width="720px" destroy-on-close>
		<pre class="json-dialog-body">{{ jsonDialogContent }}</pre>
		<template #footer>
			<el-button @click="jsonDialogVisible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="SmsSmsSendLogIndex">
import { useCrud } from '@/hooks'
import { defineComponent, h, reactive, ref } from 'vue'
import { ElButton, ElMessage } from 'element-plus/es'
import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'

const jsonDialogVisible = ref(false)
const jsonDialogTitle = ref('')
const jsonDialogContent = ref('')

const openJsonDialog = (title: string, text: string) => {
	jsonDialogTitle.value = title
	jsonDialogContent.value = formatJsonForDisplay(text)
	jsonDialogVisible.value = true
}

const formatJsonForDisplay = (text: string) => {
	if (!text || !text.trim()) {
		return ''
	}
	try {
		return JSON.stringify(JSON.parse(text), null, 2)
	} catch {
		return text
	}
}

const previewText = (text: string, maxLen = 80) => {
	const t = (text || '').trim()
	if (!t) return ''
	return t.length > maxLen ? t.slice(0, maxLen) + '…' : t
}

const resolveRequestJson = (row: { providerRequestJson?: string; templateParams?: string }) => {
	if (row.providerRequestJson && row.providerRequestJson.trim()) {
		return row.providerRequestJson
	}
	if (row.templateParams && row.templateParams.trim()) {
		return row.templateParams
	}
	return ''
}

const JsonCell = defineComponent({
	name: 'JsonCell',
	props: {
		text: { type: String, default: '' }
	},
	setup(props) {
		return () => {
			const raw = (props.text || '').trim()
			if (!raw) {
				return h('span', '—')
			}
			return h('div', { class: 'json-cell' }, [
				h('span', { class: 'json-cell-preview', title: raw }, previewText(raw)),
				h(
					ElButton,
					{
						link: true,
						type: 'primary',
						size: 'small',
						onClick: () => openJsonDialog('完整内容', raw)
					},
					() => '查看完整'
				)
			])
		}
	}
})

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sms/smsSendLog/list',
	deleteUrl: '/mgt/sms/smsSendLog/delete',
	primaryKey: 'smsSendLogCode',
	queryForm: {
		smsProviderCode: '',
		smsTemplateCode: '',
		mobile: ''
	}
})

const queryRef = ref()
const sendVisible = ref(false)
const sendFormRef = ref()
const sendForm = reactive({
	providerCode: 'tencent',
	smsTemplateCode: '',
	mobile: '',
	templateParams: ''
})

const sendRules = ref({
	providerCode: [{ required: true, message: '请选择渠道', trigger: 'change' }],
	smsTemplateCode: [{ required: true, message: '请选择短信模板', trigger: 'change' }],
	mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
})

// 短信模板列表
const templateList = ref<any[]>([])
// 动态参数输入框
const paramInputs = ref<{ value: string }[]>([])

// 加载启用的短信模板
const loadTemplateList = () => {
	service.get('/mgt/sms/smsTemplate/list?pageNo=1&pageSize=100').then((res: any) => {
		const rows = res.data?.records || res.data || []
		templateList.value = rows.filter((r: any) => r.smsTemplateEnabledCode === '1')
	})
}

// 根据模板内容解析出参数数量，生成输入框
const onTemplateChange = (code: string) => {
	const tpl = templateList.value.find((t: any) => t.smsTemplateCode === code)
	paramInputs.value = []
	if (tpl && tpl.smsTemplateContent) {
		// 简单统计 {1}、{2} 等占位符数量
		const matches = tpl.smsTemplateContent.match(/\{(\d+)\}/g) || []
		const count = matches.length
		for (let i = 0; i < count; i++) {
			paramInputs.value.push({ value: '' })
		}
	}
	sendForm.templateParams = '[]'
}

// 实时构造 templateParams JSON 数组
const buildTemplateParamsJson = () => {
	const arr = paramInputs.value.map(p => p.value || '')
	sendForm.templateParams = JSON.stringify(arr)
}

const showSendDialog = () => {
	sendForm.providerCode = 'tencent'
	sendForm.smsTemplateCode = ''
	sendForm.mobile = ''
	sendForm.templateParams = ''
	paramInputs.value = []
	sendVisible.value = true
	loadTemplateList()
	if (sendFormRef.value) {
		sendFormRef.value.resetFields()
	}
}

const submitSend = () => {
	sendFormRef.value.validate((valid: boolean) => {
		if (!valid) return false
		let params: string[] = []
		if (sendForm.templateParams && sendForm.templateParams.trim()) {
			try {
				params = JSON.parse(sendForm.templateParams)
			} catch (e) {
				ElMessage.error('模板参数格式错误')
				return false
			}
		}
		const payload = {
			providerCode: sendForm.providerCode,
			smsTemplateCode: sendForm.smsTemplateCode,
			mobile: sendForm.mobile,
			templateParams: params
		}
		service.post('/mgt/sms/send', payload).then(() => {
			ElMessage.success('发送成功，已记录日志')
			sendVisible.value = false
			getDataList()
		}).catch(() => {
			getDataList()
		})
	})
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)
</script>

<style scoped>
.json-cell {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 4px;
}
.json-cell-preview {
	max-width: 200px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	font-size: 12px;
	color: var(--el-text-color-secondary);
}
.json-dialog-body {
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
.error-text {
	color: var(--el-color-danger);
}
</style>
