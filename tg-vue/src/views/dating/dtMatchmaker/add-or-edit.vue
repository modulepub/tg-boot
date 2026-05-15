<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增红娘' : '编辑红娘'" width="720px" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="110px" @keyup.enter="submitHandle()">
			<el-row :gutter="16">
				<el-col :span="12">
					<el-form-item label="用户" prop="mkUserCode">
						<el-input
							:model-value="mkUserDisplayText"
							placeholder="请选择系统用户"
							readonly
							clearable
							@clear="clearMkUser"
						>
							<template #append>
								<el-button icon="Search" @click="userPickerVisible = true">选择</el-button>
							</template>
						</el-input>
					</el-form-item>
				</el-col>
				<tg-user-dialog
					v-if="userPickerVisible"
					:key="String(userPickerVisible)"
					v-model="userPickerVisible"
					:multiple="false"
					@select="onUserSelect"
				></tg-user-dialog>
				<el-col :span="12">
					<el-form-item label="红娘编码" prop="mkCode">
						<el-input v-model="dataForm.mkCode" placeholder="可选，业务编码" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="姓名" prop="mkName">
						<el-input v-model="dataForm.mkName" placeholder="红娘姓名" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="电话" prop="mkPhone">
						<el-input v-model="dataForm.mkPhone" placeholder="联系电话" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="年龄" prop="mkAge">
						<el-input-number v-model="dataForm.mkAge" :min="0" :max="120" controls-position="right" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="证件号" prop="mkIdNo">
						<el-input v-model="dataForm.mkIdNo" placeholder="证件号" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="工作照" prop="mkWorkPhoto">
						<tg-upload-image v-model:image-url="dataForm.mkWorkPhoto" biz="dating" width="160px" height="160px">
							<template #tip>限 1 张，JPG/PNG/GIF，单张不超过 5M</template>
						</tg-upload-image>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="城市编码" prop="mkCityCode">
						<el-input v-model="dataForm.mkCityCode" placeholder="所属城市编码" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="城市名称" prop="mkCityName">
						<el-input v-model="dataForm.mkCityName" placeholder="所属城市名称" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="婚介所编码" prop="mkCompanyCode">
						<el-input v-model="dataForm.mkCompanyCode" placeholder="婚介所编码" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="婚介所名称" prop="mkCompanyName">
						<el-input v-model="dataForm.mkCompanyName" placeholder="婚介所名称" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="标签" prop="mkTags">
						<el-input v-model="dataForm.mkTags" placeholder="多个用逗号分隔" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="认证状态" prop="mkIdentityStatusCode">
						<el-input v-model="dataForm.mkIdentityStatusCode" placeholder="认证状态编码" clearable></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="评分" prop="mkScore">
						<el-input-number v-model="dataForm.mkScore" :min="0" :max="100" :precision="2" :step="0.1" controls-position="right" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="服务人数" prop="mkServiceUserCount">
						<el-input-number v-model="dataForm.mkServiceUserCount" :min="0" :step="1" controls-position="right" style="width: 100%" />
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="说说" prop="mkMoment">
						<el-input v-model="dataForm.mkMoment" type="textarea" :rows="3" placeholder="个人介绍 / 动态" clearable></el-input>
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
import { reactive, ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref()
const userPickerVisible = ref(false)
const mkUserDisplayText = ref('')

const formatUserDisplay = (u: { userRealName?: string; userName?: string; userCode?: string }) => {
	const name = u.userRealName || u.userName || ''
	const login = u.userName ? `（${u.userName}）` : ''
	return `${name}${login}`.trim() || u.userCode || ''
}

const syncDisplayFromUserCode = async (userCode: string) => {
	if (!userCode) {
		mkUserDisplayText.value = ''
		return
	}
	try {
		const { data } = await service.get('/mgt/sysUser/list', {
			params: { userCode, pageNo: 1, pageSize: 1 }
		})
		const r = data?.records?.[0]
		mkUserDisplayText.value = r ? formatUserDisplay(r) : userCode
	} catch {
		mkUserDisplayText.value = userCode
	}
}

const clearMkUser = () => {
	dataForm.mkUserCode = ''
	mkUserDisplayText.value = ''
	nextTick(() => dataFormRef.value?.validateField('mkUserCode'))
}

const onUserSelect = (rows: any[]) => {
	const u = rows?.[0]
	if (!u?.userCode) {
		return
	}
	dataForm.mkUserCode = u.userCode
	mkUserDisplayText.value = formatUserDisplay(u)
	nextTick(() => dataFormRef.value?.validateField('mkUserCode'))
}

const emptyForm = () => ({
	id: '',
	createBy: '',
	createTime: '',
	updateBy: '',
	updateTime: '',
	orgCode: '',
	deleted: 0,
	seqNo: undefined as number | undefined,
	version: '',
	mkUserCode: '',
	mkCode: '',
	mkWorkPhoto: '',
	mkName: '',
	mkAge: undefined as number | undefined,
	mkServiceUserCount: undefined as number | undefined,
	mkPhone: '',
	mkTags: '',
	mkIdNo: '',
	mkCityCode: '',
	mkCityName: '',
	mkCompanyCode: '',
	mkCompanyName: '',
	mkMoment: '',
	mkIdentityStatusCode: '',
	mkScore: undefined as number | undefined
})

const dataForm = reactive(emptyForm())

const init = (id?: string) => {
	visible.value = true
	userPickerVisible.value = false
	Object.assign(dataForm, emptyForm())
	mkUserDisplayText.value = ''
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		service.get('/mgt/dating/dtMatchmaker/queryById?id=' + encodeURIComponent(id)).then(res => {
			Object.assign(dataForm, res.data)
			syncDisplayFromUserCode(dataForm.mkUserCode)
		})
	}
}

const dataRules = {
	mkUserCode: [{ required: true, message: '请选择用户', trigger: ['change', 'blur'] }],
	mkName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const http = dataForm.id
			? service.post('/mgt/dating/dtMatchmaker/edit', { ...dataForm })
			: service.post('/mgt/dating/dtMatchmaker/add', { ...dataForm })
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
