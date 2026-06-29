<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px">
			<el-row>
				<el-col :span="24">
					<el-form-item label="头像" prop="userAvatar">
						<tg-upload-image v-model:image-url="dataForm.userAvatar" biz="avatar" width="120px" height="120px" border-radius="50%">
							<template #tip>限 1 张，JPG/PNG，单张不超过 5M</template>
						</tg-upload-image>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item prop="userName" label="用户名">
						<el-input v-model="dataForm.userName" placeholder="用户名"></el-input>
					</el-form-item>
					<el-form-item prop="mobile" label="手机号">
						<el-input v-model="dataForm.userPhone" placeholder="手机号"></el-input>
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item prop="userRealName" label="姓名">
						<el-input v-model="dataForm.userRealName" placeholder="姓名"></el-input>
					</el-form-item>
					<el-form-item prop="userNickName" label="昵称">
						<el-input v-model="dataForm.userNickName" placeholder="昵称"></el-input>
					</el-form-item>
					<el-form-item prop="userLoginRestrictStatusCode" label="限制登录">
						<tg-dict-radio v-model="dataForm.userLoginRestrictStatusCode" dict-code="userLoginRestrictStatusCode"></tg-dict-radio>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()

const dataForm = reactive({
	id: '',
	userName: '',
	userRealName: '',
	userNickName: '',
	userAvatar: '',
	userLoginRestrictStatusCode: '0',
	userSexCode: '',
	userPhone: ''
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (id) {
		getUser(id)
	}
}

const getUser = (id: number) => {
	service.get('/mgt/sysUser/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
		dataForm.userLoginRestrictStatusCode = dataForm.userLoginRestrictStatusCode || '0'
	})
}

const dataRules = ref({
	userName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	userRealName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	userPhone: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http
		if (dataForm.id) {
			http = service.post('/mgt/sysUser/edit', dataForm)
		} else {
			http = service.post('/mgt/sysUser/add', dataForm)
		}

		http.then(() => {
			ElMessage.success({
				message: '操作成功',
				duration: 500,
				onClose: () => {
					visible.value = false
					emit('refreshDataList')
				}
			})
		})
	})
}

defineExpose({
	init
})
</script>
