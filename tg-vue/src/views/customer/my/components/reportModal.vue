<template>
	<el-dialog v-model="visible" :title="'联络报告'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px" @keyup.enter="submitHandle()">
			<el-form-item label="客户编号" prop="cusCode">
				<el-input v-model="dataForm.cusCode" disabled placeholder="客户编号"></el-input>
			</el-form-item>
			<el-form-item label="客户姓名" prop="cusName">
				<el-input v-model="dataForm.cusName" disabled placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item label="手机号" prop="cusPhone">
				<el-input v-model="dataForm.cusPhone" placeholder="手机号"></el-input>
			</el-form-item>
			<el-form-item label="微信号" prop="cusWechatId">
				<el-input v-model="dataForm.cusWechatId" placeholder="微信号"></el-input>
			</el-form-item>
			<el-form-item label="联络方式" prop="contactRecordMethodCode">
				<tg-dict-select
					v-model="dataForm.contactRecordMethodCode"
					dict-code="contactRecordMethodCode"
					clearable
					placeholder="联络方式"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="跟踪描述" prop="contactRecordDescription">
				<el-input rows="4" datatype="textarea" v-model="dataForm.contactRecordDescription" type="textarea" placeholder="跟踪描述"></el-input>
			</el-form-item>
			<el-form-item label="是否意向" prop="cusIntentionStatusCode">
				<tg-dict-select
					v-model="dataForm.cusIntentionStatusCode"
					dict-code="cusIntentionStatusCode"
					clearable
					placeholder="是否意向"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="意向等级" prop="cusIntentionLevelCode">
				<tg-dict-select v-model="dataForm.cusIntentionLevelCode" dict-code="cusIntentionLevelCode" clearable placeholder="意向等级"></tg-dict-select>
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
	contactRecordTalkDuration: 0,
	contactRecordMethodCode: '1',
	contactRecordSourceCode: '1',
	contactRecordDescription: '',
	cusIntentionStatusCode: '',
	contactRecordFile: '',
	contactRecordVoiceText: '',
	cusIntentionLevelCode: '',
	cusWechatId: '',
	cusCode: '',
	cusName: '',
	cusPhone: ''
})

const report = (form?: any) => {
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	visible.value = true
	form.id = ''
	Object.assign(dataForm, form)
}

const dataRules = ref({
	contactRecordName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	cusIntentionStatusCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	cusIntentionLevelCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	contactRecordMethodCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	contactRecordDescription: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		service.post('/cus/customer/customerContactRecord/add', dataForm).then(() => {
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
	report
})
</script>
