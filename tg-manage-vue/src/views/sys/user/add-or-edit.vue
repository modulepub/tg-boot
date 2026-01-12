<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px" @keyup.enter="submitHandle()">
			<el-row>
				<el-col :span="12">
					<el-form-item prop="userName" label="用户名">
						<el-input v-model="dataForm.userName" placeholder="用户名"></el-input>
					</el-form-item>
					<el-form-item prop="orgId" label="所属机构">
						<ma-org-select v-model="dataForm.orgCodeList" placeholder="请选择"></ma-org-select>
					</el-form-item>
					<el-form-item prop="mobile" label="手机号">
						<el-input v-model="dataForm.userPhone" placeholder="手机号"></el-input>
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item prop="userRealName" label="姓名">
						<el-input v-model="dataForm.userRealName" placeholder="姓名"></el-input>
					</el-form-item>
					<el-form-item prop="userSexCode" label="性别">
						<ma-dict-radio v-model="dataForm.userSexCode" dict-code="userSexCode"></ma-dict-radio>
					</el-form-item>
					<el-form-item prop="userEnabledCode" label="是否启用">
						<ma-dict-radio v-model="dataForm.userEnabledCode" dict-code="userEnabledCode"></ma-dict-radio>
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
	userEnabledCode: '',
	userSexCode: '',
	userPhone: '',
	orgCodeList: [] as any[]
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	// id 存在则为修改
	if (id) {
		getUser(id)
	}
}
// 获取信息
const getUser = (id: number) => {
	service.get('/mgt/sysUser/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

const dataRules = ref({
	userName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	userRealName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	userPhone: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	orgCodes: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
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
