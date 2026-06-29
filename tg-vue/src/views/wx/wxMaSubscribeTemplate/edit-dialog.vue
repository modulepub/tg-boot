<template>
	<el-dialog v-model="visible" title="修改订阅消息模板" :close-on-click-modal="false" width="720px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px">
			<el-form-item label="模板编码" prop="wxMaSubscribeTemplateCode">
				<el-input v-model="dataForm.wxMaSubscribeTemplateCode" disabled></el-input>
			</el-form-item>
			<el-form-item label="微信模板 ID" prop="wxMaSubscribeTemplateId">
				<el-input v-model="dataForm.wxMaSubscribeTemplateId" placeholder="微信公众平台订阅消息模板 ID"></el-input>
			</el-form-item>
			<el-form-item label="模板说明" prop="wxMaSubscribeTemplateContent">
				<el-input
					v-model="dataForm.wxMaSubscribeTemplateContent"
					type="textarea"
					:rows="5"
					placeholder="首行建议填写场景名称；后续行可维护字段映射说明"
				></el-input>
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
	wxMaSubscribeTemplateCode: '',
	wxMaSubscribeTemplateId: '',
	wxMaSubscribeTemplateContent: ''
})

const dataRules = ref({
	wxMaSubscribeTemplateId: [{ required: true, message: '请输入微信模板 ID', trigger: 'blur' }]
})

const resetForm = () => {
	dataForm.wxMaSubscribeTemplateCode = ''
	dataForm.wxMaSubscribeTemplateId = ''
	dataForm.wxMaSubscribeTemplateContent = ''
}

const init = (wxMaSubscribeTemplateCode?: string) => {
	visible.value = true
	resetForm()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (wxMaSubscribeTemplateCode) {
		service.get('/mgt/wx/wxMaSubscribeTemplate/queryById?id=' + encodeURIComponent(wxMaSubscribeTemplateCode)).then(res => {
			Object.assign(dataForm, res.data)
		})
	}
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const payload = { ...dataForm }
		service.post('/mgt/wx/wxMaSubscribeTemplate/edit', payload).then(() => {
			ElMessage.success({
				message: '修改成功',
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
