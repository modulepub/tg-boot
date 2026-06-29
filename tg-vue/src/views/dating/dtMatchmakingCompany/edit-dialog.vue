<template>
	<el-dialog v-model="visible" title="编辑企业信息" width="760px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<el-alert
			v-if="showSubmitTip"
			type="info"
			:closable="false"
			show-icon
			class="edit-tip"
			title="补全资料后保存，再点击列表「提交」代提交审核。"
		/>
		<el-form ref="dataFormRef" v-loading="loading" :model="dataForm" label-width="130px" class="edit-form">
			<el-row :gutter="16">
				<el-col :span="12">
					<el-form-item label="企业名称" prop="mkCompanyName">
						<el-input v-model="dataForm.mkCompanyName" placeholder="请填写企业名称" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="统一社会信用代码" prop="mkCompanyUsciCode">
						<el-input v-model="dataForm.mkCompanyUsciCode" placeholder="请填写信用代码" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="公司电话" prop="mkCompanyTel">
						<el-input v-model="dataForm.mkCompanyTel" placeholder="请填写公司电话" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="法人姓名" prop="mkCompanyLegalName">
						<el-input v-model="dataForm.mkCompanyLegalName" placeholder="请填写法人姓名" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="法人证件号" prop="mkCompanyLegalIdNo">
						<el-input v-model="dataForm.mkCompanyLegalIdNo" placeholder="请填写法人证件号" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="所在城市" prop="mkCompanyCityCode">
						<el-cascader
							v-model="cityCascaderValue"
							:options="cityCascaderOptions"
							:props="{ expandTrigger: 'hover' }"
							clearable
							filterable
							placeholder="请选择城市"
							style="width: 100%"
							@change="onCityCascaderChange"
						/>
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="公司地址" prop="mkCompanyAddressDetail">
						<el-input v-model="dataForm.mkCompanyAddressDetail" placeholder="请填写公司地址" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="办公/门头照片" prop="mkCompanyPhotos">
						<tg-upload-images v-model="dataForm.mkCompanyPhotos" biz="dating" :limit="3">
							<template #tip>最多 3 张，JPG/PNG/GIF，单张不超过 5M</template>
						</tg-upload-images>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="对公银行账号" prop="mkCompanyPublicAccountNo">
						<el-input v-model="dataForm.mkCompanyPublicAccountNo" placeholder="请填写对公银行账号" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="开户行" prop="mkCompanyBankName">
						<el-input v-model="dataForm.mkCompanyBankName" placeholder="请填写开户行" clearable />
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="开户地" prop="mkCompanyBankLocation">
						<el-input v-model="dataForm.mkCompanyBankLocation" placeholder="请填写开户地" clearable />
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>

		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" :loading="saving" @click="submitHandle">保存</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import service from '@/utils/request'
import { cityCascaderOptions, parseCityToCascader, resolveCityFromCascader } from '@/utils/matchmakerCity'

const emit = defineEmits<{ refresh: [] }>()

const visible = ref(false)
const loading = ref(false)
const saving = ref(false)
const dataFormRef = ref<FormInstance>()
const cityCascaderValue = ref<string[]>([])

const emptyForm = () => ({
	id: '',
	mkCompanyName: '',
	mkCompanyTel: '',
	mkCompanyUsciCode: '',
	mkCompanyLegalName: '',
	mkCompanyLegalIdNo: '',
	mkCompanyCityCode: '',
	mkCompanyCityName: '',
	mkCompanyAddressDetail: '',
	mkCompanyPhotos: '',
	mkCompanyPublicAccountNo: '',
	mkCompanyBankName: '',
	mkCompanyBankLocation: ''
})

const dataForm = reactive(emptyForm())
const processCode = ref('')

const showSubmitTip = computed(() => {
	const key = String(processCode.value ?? '').trim()
	return key === 'DRAFT' || key === '0' || key === 'REJECTED' || key === '3'
})

function fillForm(detail: Record<string, any>) {
	processCode.value = String(detail.mkCompanyIdentityProcessCode ?? '').trim()
	dataForm.id = detail.id || ''
	dataForm.mkCompanyName = detail.mkCompanyName || ''
	dataForm.mkCompanyTel = detail.mkCompanyTel || ''
	dataForm.mkCompanyUsciCode = detail.mkCompanyUsciCode || ''
	dataForm.mkCompanyLegalName = detail.mkCompanyLegalName || ''
	dataForm.mkCompanyLegalIdNo = detail.mkCompanyLegalIdNo || ''
	dataForm.mkCompanyCityCode = detail.mkCompanyCityCode || ''
	dataForm.mkCompanyCityName = detail.mkCompanyCityName || ''
	dataForm.mkCompanyAddressDetail = detail.mkCompanyAddressDetail || ''
	dataForm.mkCompanyPhotos = detail.mkCompanyPhotos || ''
	dataForm.mkCompanyPublicAccountNo = detail.mkCompanyPublicAccountNo || ''
	dataForm.mkCompanyBankName = detail.mkCompanyBankName || ''
	dataForm.mkCompanyBankLocation = detail.mkCompanyBankLocation || ''
	cityCascaderValue.value = parseCityToCascader(dataForm.mkCompanyCityName, dataForm.mkCompanyCityCode)
}

function onCityCascaderChange(value: string[] | undefined) {
	const resolved = resolveCityFromCascader(value)
	dataForm.mkCompanyCityCode = resolved.cityCode
	dataForm.mkCompanyCityName = resolved.cityName
}

function loadDetail(id: string) {
	loading.value = true
	service.get('/mgt/dating/dtMatchmakingCompany/queryById', { params: { id } }).then(res => {
		fillForm(res.data || {})
	}).finally(() => {
		loading.value = false
	})
}

const init = (id: string) => {
	Object.assign(dataForm, emptyForm())
	cityCascaderValue.value = []
	processCode.value = ''
	visible.value = true
	loadDetail(id)
}

function submitHandle() {
	if (!dataForm.id) {
		return
	}
	saving.value = true
	service.post('/mgt/dating/dtMatchmakingCompany/updateApply', { ...dataForm }).then(() => {
		ElMessage.success('保存成功')
		visible.value = false
		emit('refresh')
	}).finally(() => {
		saving.value = false
	})
}

function onClosed() {
	Object.assign(dataForm, emptyForm())
	cityCascaderValue.value = []
	processCode.value = ''
}

defineExpose({ init })
</script>

<style scoped>
.edit-tip {
	margin-bottom: 16px;
}

.edit-form {
	margin-top: 4px;
}
</style>
