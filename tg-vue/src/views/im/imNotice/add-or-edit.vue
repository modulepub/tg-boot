<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增全员通知' : '编辑全员通知'" width="760px" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="发送人" prop="imNoticeSenderUserCode">
				<el-input :model-value="senderDisplayText" placeholder="请选择 IM 用户" readonly clearable @clear="clearSender">
					<template #append>
						<el-button icon="Search" @click="senderPickerVisible = true">选择</el-button>
					</template>
				</el-input>
			</el-form-item>
			<tg-im-user-dialog
				v-if="senderPickerVisible"
				:key="String(senderPickerVisible)"
				v-model="senderPickerVisible"
				:multiple="false"
				@select="onSenderSelect"
			></tg-im-user-dialog>
			<el-form-item label="标题" prop="imNoticeName">
				<el-input v-model="dataForm.imNoticeName" placeholder="通知标题" maxlength="128" show-word-limit clearable></el-input>
			</el-form-item>
			<el-form-item label="正文" prop="imNoticeText">
				<el-input v-model="dataForm.imNoticeText" type="textarea" :rows="4" placeholder="图文正文说明" maxlength="2000" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="封面图" prop="imNoticeImg">
				<tg-upload-image v-model:image-url="dataForm.imNoticeImg" biz="im" width="160px" height="160px">
					<template #tip>限 1 张，JPG/PNG，单张不超过 5M</template>
				</tg-upload-image>
			</el-form-item>
			<el-form-item label="跳转链接" prop="imNoticeUrl">
				<el-input v-model="dataForm.imNoticeUrl" placeholder="可选，H5 或小程序链接" clearable></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">保存草稿</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()
const senderPickerVisible = ref(false)
const senderNickName = ref('')
const senderRealName = ref('')

const dataForm = reactive({
	id: '',
	imNoticeCode: '',
	imNoticeName: '',
	imNoticeText: '',
	imNoticeImg: '',
	imNoticeUrl: '',
	imNoticeSenderUserCode: '',
	imNoticeTargetTypeCode: 'all',
	imNoticePublishStateCode: '0'
})

const dataRules = {
	imNoticeSenderUserCode: [{ required: true, message: '请选择发送人', trigger: 'change' }],
	imNoticeName: [{ required: true, message: '请输入标题', trigger: 'blur' }],
	imNoticeText: [{ required: true, message: '请输入正文', trigger: 'blur' }],
	imNoticeImg: [{ required: true, message: '请上传封面图', trigger: 'change' }]
}

const senderDisplayText = computed(() => {
	const code = dataForm.imNoticeSenderUserCode
	if (!code) return ''
	const nick = senderNickName.value
	const real = senderRealName.value
	const label = [real, nick].filter(Boolean).join(' / ')
	return label ? `${label}（${code}）` : code
})

const init = (id?: string) => {
	visible.value = true
	senderNickName.value = ''
	senderRealName.value = ''
	nextTick(() => {
		dataFormRef.value?.resetFields()
		dataForm.id = ''
		dataForm.imNoticeCode = ''
		dataForm.imNoticeTargetTypeCode = 'all'
		dataForm.imNoticePublishStateCode = '0'
		if (id) {
			service.get('/mgt/im/imNotice/queryById', { params: { id } }).then((res: any) => {
				Object.assign(dataForm, res.data)
				if (res.data?.imNoticePublishStateCode?.code) {
					dataForm.imNoticePublishStateCode = res.data.imNoticePublishStateCode.code
				}
				if (res.data?.imNoticeTargetTypeCode?.code) {
					dataForm.imNoticeTargetTypeCode = res.data.imNoticeTargetTypeCode.code
				}
			})
		}
	})
}

const onSenderSelect = (rows: any[]) => {
	const row = rows?.[0]
	if (!row) return
	dataForm.imNoticeSenderUserCode = row.imUserUserCode
	senderNickName.value = row.imUserNickName || ''
	senderRealName.value = row.imUserRealName || ''
	senderPickerVisible.value = false
}

const clearSender = () => {
	dataForm.imNoticeSenderUserCode = ''
	senderNickName.value = ''
	senderRealName.value = ''
}

const submitHandle = () => {
	dataFormRef.value?.validate((valid: boolean) => {
		if (!valid) return
		const url = !dataForm.id ? '/mgt/im/imNotice/add' : '/mgt/im/imNotice/edit'
		service.post(url, dataForm).then(() => {
			ElMessage.success('保存成功')
			visible.value = false
			emit('refreshDataList')
		})
	})
}

defineExpose({ init })
</script>
