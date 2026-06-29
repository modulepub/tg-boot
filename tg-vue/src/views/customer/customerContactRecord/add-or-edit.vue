<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="编号" prop="contactRecordCode">
				<el-input v-model="dataForm.contactRecordCode" placeholder="编号"></el-input>
			</el-form-item>
			<el-form-item label="记录归属人" prop="userCode">
				<el-input v-model="dataForm.userCode" placeholder="记录归属人"></el-input>
			</el-form-item>
			<el-form-item label="记录人姓名" prop="userRealName">
				<el-input v-model="dataForm.userRealName" placeholder="记录人姓名"></el-input>
			</el-form-item>
			<el-form-item label="通话时长（s)" prop="contactRecordTalkDuration">
				<el-input v-model="dataForm.contactRecordTalkDuration" placeholder="通话时长（s)"></el-input>
			</el-form-item>
			<el-form-item label="上传录音文件" prop="contactRecordFile">
				<tg-upload-file v-model="dataForm.contactRecordFile" biz="avatar" placeholder="录音文件"></tg-upload-file>
			</el-form-item>
			<el-form-item label="跟踪描述" prop="contactRecordDescription">
				<el-input v-model="dataForm.contactRecordDescription" placeholder="跟踪描述"></el-input>
			</el-form-item>
			<el-form-item label="是否意向" prop="cusIntentionStatusCode">
				<tg-dict-select
					v-model="dataForm.cusIntentionStatusCode"
					dict-code="cusIntentionStatusCode"
					clearable
					placeholder="是否意向"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="客户编号" prop="cusCode">
				<el-input v-model="dataForm.cusCode" placeholder="客户编号"></el-input>
			</el-form-item>
			<el-form-item label="客户姓名" prop="cusName">
				<el-input v-model="dataForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item label="手机号" prop="cusPhone">
				<el-input v-model="dataForm.cusPhone" placeholder="手机号"></el-input>
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
	id: '',
	seqNo: '',
	orgCode: '',
	updateBy: '',
	updateTime: '',
	createBy: '',
	createTime: '',
	version: '',
	deleted: '',
	contactRecordCode: '',
	contactRecordName: '',
	userCode: '',
	userRealName: '',
	contactRecordTalkDuration: '',
	contactRecordFile: '',
	contactRecordDescription: '',
	cusIntentionStatusCode: '',
	cusCode: '',
	cusName: '',
	cusPhone: ''
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (id) {
		getCustomerContactRecord(id)
	}
}

const getCustomerContactRecord = (id: number) => {
	service.get('/mgt/customer/customerContactRecord/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

const dataRules = ref({
	contactRecordName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	cusPhone: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http: any
		if (dataForm.id) {
			http = service.post('/mgt/customer/customerContactRecord/edit', dataForm)
		} else {
			http = service.post('/mgt/customer/customerContactRecord/add', dataForm)
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
