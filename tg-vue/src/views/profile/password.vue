<template>
	<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
		<el-form-item prop="oldPassword" :label="$t('profile.oldPassword')">
			<el-input v-model="dataForm.oldPassword" type="password" show-password></el-input>
		</el-form-item>
		<el-form-item prop="newPassword" :label="$t('profile.newPassword')">
			<el-input v-model="dataForm.newPassword" type="password" show-password></el-input>
		</el-form-item>
		<el-form-item prop="confirmPassword" :label="$t('profile.confirmPassword')">
			<el-input v-model="dataForm.confirmPassword" type="password" show-password></el-input>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" @click="handleDataForm">{{ $t('confirm') }}</el-button>
		</el-form-item>
	</el-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { validatePassword } from '@/utils/validate'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const { t } = useI18n()
const dataFormRef: any = ref(null)

const dataForm = reactive({
	oldPassword: '',
	newPassword: '',
	confirmPassword: ''
})

const dataRules = ref({
	oldPassword: [{ required: true, message: t('required'), trigger: 'blur' }],
	newPassword: [{ required: true, validator: validatePassword, trigger: 'blur' }],
	confirmPassword: [{ required: true, message: t('required'), trigger: 'blur' }]
})

const handleDataForm = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}

		// 修改密码
		service.post('/cus/sysUser/changePassword', dataForm).then(() => {
			ElMessage.success('修改成功')
		})
	})
}
</script>
