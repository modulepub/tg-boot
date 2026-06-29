<template>
	<el-dialog v-model="visible" title="测试发送订阅消息" width="720px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<el-form ref="dataFormRef" v-loading="loading" :model="dataForm" :rules="dataRules" label-width="120px">
			<el-form-item label="模板编码">
				<el-input v-model="dataForm.wxMaSubscribeTemplateCode" disabled></el-input>
			</el-form-item>
			<el-form-item label="微信模板 ID">
				<el-input v-model="templateId" disabled></el-input>
			</el-form-item>
			<el-form-item label="小程序配置">
				<el-select v-model="dataForm.wxMiniConfigCode" placeholder="默认启用配置" clearable style="width: 100%">
					<el-option
						v-for="item in miniConfigOptions"
						:key="item.wxMiniConfigCode"
						:label="`${item.wxMiniConfigName || item.wxMiniConfigCode}（${item.wxMiniConfigAppId}）`"
						:value="item.wxMiniConfigCode"
					/>
				</el-select>
			</el-form-item>
			<el-form-item label="接收用户" prop="userCode">
				<el-input
					:model-value="userDisplayText"
					placeholder="请选择系统用户（需已绑定小程序 openId）"
					readonly
					clearable
					@clear="clearUser"
				>
					<template #append>
						<el-button icon="Search" @click="userPickerVisible = true">选择</el-button>
					</template>
				</el-input>
				<div v-if="selectedUserOpenId" class="field-hint">openId：{{ selectedUserOpenId }}</div>
				<div v-else-if="dataForm.userCode && openIdChecked" class="field-hint field-hint-warn">该用户未绑定 openId，发送将失败</div>
			</el-form-item>
			<el-form-item label="跳转页面">
				<el-input v-model="dataForm.page" placeholder="可选，不含开头 /，如 pages/index/index"></el-input>
			</el-form-item>
			<el-form-item label="模板参数">
				<div class="param-block">
					<el-empty v-if="fieldRows.length === 0" description="未解析到字段，可手动添加" :image-size="48" />
					<div v-for="(row, index) in fieldRows" :key="row.fieldKey + '-' + index" class="param-row">
						<el-input v-model="row.fieldKey" placeholder="字段 key" class="param-key" :disabled="row.fromTemplate" />
						<el-input v-model="row.fieldValue" :placeholder="row.fieldLabel || '字段值'" class="param-value" />
						<el-button v-if="!row.fromTemplate" type="danger" link icon="Delete" @click="removeFieldRow(index)" />
					</div>
					<el-button type="primary" link icon="Plus" @click="addFieldRow">添加字段</el-button>
				</div>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" :loading="submitting" @click="submitHandle">发送</el-button>
		</template>
		<tg-user-dialog
			v-if="userPickerVisible"
			:key="String(userPickerVisible)"
			v-model="userPickerVisible"
			:multiple="false"
			@select="onUserSelect"
		></tg-user-dialog>
	</el-dialog>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

interface FieldRow {
	fieldKey: string
	fieldLabel: string
	fieldValue: string
	fromTemplate: boolean
}

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const dataFormRef = ref()
const userPickerVisible = ref(false)
const templateId = ref('')
const userDisplayText = ref('')
const selectedUserOpenId = ref('')
const openIdChecked = ref(false)
const miniConfigOptions = ref<any[]>([])
const fieldRows = ref<FieldRow[]>([])

const dataForm = reactive({
	wxMaSubscribeTemplateCode: '',
	userCode: '',
	wxMiniConfigCode: '',
	page: ''
})

const dataRules = {
	userCode: [{ required: true, message: '请选择接收用户', trigger: ['change', 'blur'] }]
}

const formatUserDisplay = (u: { userRealName?: string; userName?: string; userCode?: string }) => {
	const name = u.userRealName || u.userName || ''
	const login = u.userName ? `（${u.userName}）` : ''
	return `${name}${login}`.trim() || u.userCode || ''
}

const loadMiniConfigs = async () => {
	try {
		const { data } = await service.get('/mgt/wx/wxMiniConfig/list', { params: { pageNo: 1, pageSize: 100 } })
		miniConfigOptions.value = data?.records || []
	} catch {
		miniConfigOptions.value = []
	}
}

const loadFields = async (code: string) => {
	try {
		const { data } = await service.get('/mgt/wx/wxMaSubscribeTemplate/listFields', {
			params: { wxMaSubscribeTemplateCode: code }
		})
		const list = Array.isArray(data) ? data : []
		fieldRows.value = list.map((item: { fieldKey?: string; fieldLabel?: string }) => ({
			fieldKey: item.fieldKey || '',
			fieldLabel: item.fieldLabel || '',
			fieldValue: '',
			fromTemplate: true
		}))
	} catch {
		fieldRows.value = []
	}
}

const syncUserOpenId = async (userCode: string) => {
	openIdChecked.value = false
	selectedUserOpenId.value = ''
	if (!userCode) {
		return
	}
	try {
		const { data } = await service.get('/mgt/sysUser/list', {
			params: { userCode, pageNo: 1, pageSize: 1 }
		})
		const user = data?.records?.[0]
		selectedUserOpenId.value = String(user?.userWxOpenId ?? '').trim()
		openIdChecked.value = true
	} catch {
		openIdChecked.value = true
	}
}

const clearUser = () => {
	dataForm.userCode = ''
	userDisplayText.value = ''
	selectedUserOpenId.value = ''
	openIdChecked.value = false
	nextTick(() => dataFormRef.value?.validateField('userCode'))
}

const onUserSelect = (rows: any[]) => {
	const u = rows?.[0]
	if (!u?.userCode) {
		return
	}
	dataForm.userCode = u.userCode
	userDisplayText.value = formatUserDisplay(u)
	selectedUserOpenId.value = String(u.userWxOpenId ?? '').trim()
	openIdChecked.value = true
	nextTick(() => dataFormRef.value?.validateField('userCode'))
}

const addFieldRow = () => {
	fieldRows.value.push({ fieldKey: '', fieldLabel: '', fieldValue: '', fromTemplate: false })
}

const removeFieldRow = (index: number) => {
	fieldRows.value.splice(index, 1)
}

const buildDataMap = () => {
	const data: Record<string, string> = {}
	for (const row of fieldRows.value) {
		const key = String(row.fieldKey ?? '').trim()
		if (!key) {
			continue
		}
		data[key] = String(row.fieldValue ?? '')
	}
	return data
}

const resetForm = () => {
	dataForm.wxMaSubscribeTemplateCode = ''
	dataForm.userCode = ''
	dataForm.wxMiniConfigCode = ''
	dataForm.page = ''
	templateId.value = ''
	userDisplayText.value = ''
	selectedUserOpenId.value = ''
	openIdChecked.value = false
	fieldRows.value = []
}

const onClosed = () => {
	resetForm()
	userPickerVisible.value = false
}

const init = async (wxMaSubscribeTemplateCode?: string) => {
	if (!wxMaSubscribeTemplateCode) {
		return
	}
	visible.value = true
	loading.value = true
	resetForm()
	dataForm.wxMaSubscribeTemplateCode = wxMaSubscribeTemplateCode
	try {
		await loadMiniConfigs()
		const { data } = await service.get(
			'/mgt/wx/wxMaSubscribeTemplate/queryById?id=' + encodeURIComponent(wxMaSubscribeTemplateCode)
		)
		templateId.value = data?.wxMaSubscribeTemplateId || ''
		await loadFields(wxMaSubscribeTemplateCode)
	} finally {
		loading.value = false
	}
	nextTick(() => dataFormRef.value?.clearValidate())
}

const submitHandle = () => {
	dataFormRef.value.validate(async (valid: boolean) => {
		if (!valid) {
			return
		}
		if (!selectedUserOpenId.value) {
			await syncUserOpenId(dataForm.userCode)
		}
		if (!selectedUserOpenId.value) {
			ElMessage.warning('该用户未绑定微信小程序 openId')
			return
		}
		submitting.value = true
		try {
			await service.post('/mgt/wx/wxMaSubscribeTemplate/sendTest', {
				wxMaSubscribeTemplateCode: dataForm.wxMaSubscribeTemplateCode,
				userCode: dataForm.userCode,
				wxMiniConfigCode: dataForm.wxMiniConfigCode || undefined,
				page: dataForm.page || undefined,
				data: buildDataMap()
			})
			ElMessage.success('发送成功')
			visible.value = false
		} finally {
			submitting.value = false
		}
	})
}

defineExpose({ init })
</script>

<style scoped>
.field-hint {
	margin-top: 4px;
	font-size: 12px;
	color: var(--el-text-color-secondary);
	word-break: break-all;
}
.field-hint-warn {
	color: var(--el-color-warning);
}
.param-block {
	width: 100%;
}
.param-row {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-bottom: 8px;
}
.param-key {
	width: 140px;
	flex-shrink: 0;
}
.param-value {
	flex: 1;
}
</style>
