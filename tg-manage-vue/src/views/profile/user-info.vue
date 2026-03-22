<template>
	<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
		<el-form-item prop="realName" label="姓名">
			<el-input v-model="dataForm.userRealName" placeholder="姓名"></el-input>
		</el-form-item>
		<el-form-item prop="gender" label="性别">
			<tg-dict-radio v-model="dataForm.userSexCode" dict-code="userSexCode"></tg-dict-radio>
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
import { useUserStore } from '@/store/modules/user'
import service from '@/utils/request'

const userStore = useUserStore()
const { t } = useI18n()
const dataFormRef: any = ref(null)

const dataForm = reactive({
	userRealName: userStore.user.userRealName,
	userSexCode: userStore.user.userSexCode
})

const dataRules = ref({
	userRealName: [{ required: true, message: t('required'), trigger: 'blur' }]
})

const handleDataForm = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}

		// 修改登录用户信息
		service.post('/cus/sysUser/editUserInfo', dataForm).then(() => {
			// 更新状态管理
			userStore.user.userRealName = dataForm.userRealName
			userStore.user.userSexCode = dataForm.userSexCode

			ElMessage.success('修改成功')
		})
	})
}
</script>
