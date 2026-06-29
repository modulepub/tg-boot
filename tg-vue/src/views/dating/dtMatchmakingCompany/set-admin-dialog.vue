<template>
	<el-dialog v-model="visible" title="设置管理员" width="520px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<el-form ref="dataFormRef" v-loading="loading" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="企业名称">
				<span>{{ companyName || '—' }}</span>
			</el-form-item>
			<el-form-item label="管理员" prop="adminUserCode">
				<el-input
					:model-value="adminDisplayText"
					placeholder="请选择系统用户"
					readonly
					clearable
					@clear="clearAdmin"
				>
					<template #append>
						<el-button icon="Search" @click="userPickerVisible = true">选择</el-button>
					</template>
				</el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" :loading="submitting" @click="submitHandle">确定</el-button>
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

const emit = defineEmits(['refresh'])

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const dataFormRef = ref()
const userPickerVisible = ref(false)
const companyName = ref('')
const adminDisplayText = ref('')

const dataForm = reactive({
	id: '',
	adminUserCode: ''
})

const formatUserDisplay = (u: { userRealName?: string; userName?: string; userCode?: string }) => {
	const name = u.userRealName || u.userName || ''
	const login = u.userName ? `（${u.userName}）` : ''
	return `${name}${login}`.trim() || u.userCode || ''
}

const syncDisplayFromUserCode = async (userCode: string) => {
	if (!userCode) {
		adminDisplayText.value = ''
		return
	}
	try {
		const { data } = await service.get('/mgt/sysUser/list', {
			params: { userCode, pageNo: 1, pageSize: 1 }
		})
		const r = data?.records?.[0]
		adminDisplayText.value = r ? formatUserDisplay(r) : userCode
	} catch {
		adminDisplayText.value = userCode
	}
}

const clearAdmin = () => {
	dataForm.adminUserCode = ''
	adminDisplayText.value = ''
	nextTick(() => dataFormRef.value?.validateField('adminUserCode'))
}

const onUserSelect = (rows: any[]) => {
	const u = rows?.[0]
	if (!u?.userCode) {
		return
	}
	dataForm.adminUserCode = u.userCode
	adminDisplayText.value = formatUserDisplay(u)
	nextTick(() => dataFormRef.value?.validateField('adminUserCode'))
}

const dataRules = {
	adminUserCode: [{ required: true, message: '请选择管理员', trigger: ['change', 'blur'] }]
}

const onClosed = () => {
	dataForm.id = ''
	dataForm.adminUserCode = ''
	companyName.value = ''
	adminDisplayText.value = ''
	userPickerVisible.value = false
}

const init = (row: { id?: string; mkCompanyName?: string; mkCompanyAdminUserCode?: string }) => {
	if (!row.id) {
		return
	}
	visible.value = true
	loading.value = true
	companyName.value = row.mkCompanyName || ''
	dataForm.id = row.id
	dataForm.adminUserCode = row.mkCompanyAdminUserCode || ''
	adminDisplayText.value = ''
	syncDisplayFromUserCode(dataForm.adminUserCode).finally(() => {
		loading.value = false
	})
	nextTick(() => dataFormRef.value?.clearValidate())
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return
		}
		submitting.value = true
		service
			.post('/mgt/dating/dtMatchmakingCompany/setAdmin', {
				id: dataForm.id,
				adminUserCode: dataForm.adminUserCode
			})
			.then(() => {
				ElMessage.success('设置成功')
				visible.value = false
				emit('refresh')
			})
			.finally(() => {
				submitting.value = false
			})
	})
}

defineExpose({ init })
</script>
