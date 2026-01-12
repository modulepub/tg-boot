<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px" @keyup.enter="submitHandle()">
			<el-form-item label="日志编码" prop="log_code">
				<el-input v-model="dataForm.log_code" placeholder="日志编码"></el-input>
			</el-form-item>
			<el-form-item label="日志名称" prop="log_name">
				<el-input v-model="dataForm.log_name" placeholder="日志名称"></el-input>
			</el-form-item>
			<el-form-item label="方法名" prop="log_method_name">
				<el-input v-model="dataForm.log_method_name" placeholder="方法名"></el-input>
			</el-form-item>
			<el-form-item label="日志内容" prop="log_content">
				<el-input v-model="dataForm.log_content" placeholder="日志内容"></el-input>
			</el-form-item>
			<el-form-item label="日志描述" prop="log_description">
				<el-input v-model="dataForm.log_description" placeholder="日志描述"></el-input>
			</el-form-item>
			<el-form-item label="事务编码" prop="log_transaction_code">
				<el-input v-model="dataForm.log_transaction_code" placeholder="事务编码"></el-input>
			</el-form-item>
			<el-form-item label="用户名" prop="log_user_name">
				<el-input v-model="dataForm.log_user_name" placeholder="用户名"></el-input>
			</el-form-item>
			<el-form-item label="ID" prop="id">
				<el-input v-model="dataForm.id" placeholder="ID"></el-input>
			</el-form-item>
			<el-form-item label="创建人" prop="create_by">
				<el-input v-model="dataForm.create_by" placeholder="创建人"></el-input>
			</el-form-item>
			<el-form-item label="创建日期" prop="create_time">
				<el-input v-model="dataForm.create_time" placeholder="创建日期"></el-input>
			</el-form-item>
			<el-form-item label="更新人" prop="update_by">
				<el-input v-model="dataForm.update_by" placeholder="更新人"></el-input>
			</el-form-item>
			<el-form-item label="更新日期" prop="update_time">
				<el-input v-model="dataForm.update_time" placeholder="更新日期"></el-input>
			</el-form-item>
			<el-form-item label="删除标识" prop="deleted">
				<el-input v-model="dataForm.deleted" placeholder="删除标识"></el-input>
			</el-form-item>
			<el-form-item label="版本" prop="version">
				<el-input v-model="dataForm.version" placeholder="版本"></el-input>
			</el-form-item>
			<el-form-item label="序号" prop="seq_no">
				<el-input v-model="dataForm.seq_no" placeholder="序号"></el-input>
			</el-form-item>
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
	log_code: ''
,	log_name: ''
,	log_method_name: ''
,	log_content: ''
,	log_description: ''
,	log_transaction_code: ''
,	log_user_name: ''
,	id: ''
,	create_by: ''
,	create_time: ''
,	update_by: ''
,	update_time: ''
,	deleted: ''
,	version: ''
,	seq_no: ''
	})

	const init = (id?: number) => {
		visible.value = true
		dataForm.id = ''

		// 重置表单数据
		if (dataFormRef.value) {
			dataFormRef.value.resetFields()
		}

		if (id) {
			getLog(id)
		}
	}

	const getLog = (id: number) => {
		service.get('/mgt/log/queryById?id=' + id).then(res => {
			Object.assign(dataForm, res.data)
		})
	}

	const dataRules = ref({
	log_code: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
,	log_name: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
,	})

	// 表单提交
	const submitHandle = () => {
		dataFormRef.value.validate((valid: boolean) => {
			if (!valid) {
				return false
			}
			let http: any
			if (dataForm.id) {
				http =  service.post('/mgt/log/edit', dataForm)
			} else {
				http =  service.post('/mgt/log/add', dataForm)
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