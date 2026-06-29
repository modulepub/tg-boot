<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增红娘' : '编辑红娘'" width="720px" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="110px">
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
					<el-form-item label="所在城市" prop="mkCityCode">
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
				<el-col :span="12">
					<el-form-item label="婚介所" prop="mkCompanyCode">
						<el-select
							v-model="dataForm.mkCompanyCode"
							filterable
							clearable
							placeholder="请选择已认证婚介所"
							style="width: 100%"
							@change="onCompanyChange"
						>
							<el-option
								v-for="item in companyOptions"
								:key="item.mkCompanyCode"
								:label="item.mkCompanyName"
								:value="item.mkCompanyCode"
							/>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="24">
					<el-form-item label="标签" prop="mkTags">
						<div class="mk-tag-grid">
							<el-check-tag
								v-for="tag in MK_SKILL_OPTIONS"
								:key="tag"
								:checked="selectedMkTags.includes(tag)"
								class="mk-tag-item"
								@change="(checked: boolean) => setMkTag(tag, checked)"
							>
								{{ tag }}
							</el-check-tag>
						</div>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="认证状态" prop="mkIdentityStatusCode">
						<el-select v-model="dataForm.mkIdentityStatusCode" clearable placeholder="是否已认证" style="width: 100%">
							<el-option label="是" value="1" />
							<el-option label="否" value="0" />
						</el-select>
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
				<el-col :span="12">
					<el-form-item label="视频号" prop="mkChannelsFinderUserName">
						<el-input v-model="dataForm.mkChannelsFinderUserName" placeholder="sph 开头的 finderUserName" maxlength="64" clearable></el-input>
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
import { cityCascaderOptions, parseCityToCascader, resolveCityFromCascader } from '@/utils/matchmakerCity'

const emit = defineEmits(['refreshDataList'])

const MK_SKILL_OPTIONS = [
	'情感答疑',
	'特殊人群',
	'相亲活动',
	'形象改造',
	'跨国婚恋',
	'经营圈子',
	'恋爱陪跑'
] as const

type CompanyOption = { mkCompanyCode: string; mkCompanyName: string }

const visible = ref(false)
const dataFormRef = ref()
const userPickerVisible = ref(false)
const mkUserDisplayText = ref('')
const cityCascaderValue = ref<string[]>([])
const companyOptions = ref<CompanyOption[]>([])
const selectedMkTags = ref<string[]>([])

const formatUserDisplay = (u: { userRealName?: string; userName?: string; userCode?: string }) => {
	const name = u.userRealName || u.userName || ''
	const login = u.userName ? `（${u.userName}）` : ''
	return `${name}${login}`.trim() || u.userCode || ''
}

const statusCodeKey = (code: unknown) => {
	if (code != null && typeof code === 'object' && 'code' in (code as object)) {
		return String((code as { code?: string }).code ?? '').trim()
	}
	return String(code ?? '').trim()
}

const onCityCascaderChange = (value: string[] | undefined) => {
	const resolved = resolveCityFromCascader(value)
	dataForm.mkCityCode = resolved.cityCode
	dataForm.mkCityName = resolved.cityName
}

const parseMkTags = (raw?: string) => {
	if (!raw?.trim()) {
		return []
	}
	return raw
		.split(/[,，]/)
		.map(item => item.trim())
		.filter(Boolean)
}

const syncMkTagsToForm = () => {
	dataForm.mkTags = selectedMkTags.value.join(',')
}

const setSelectedMkTagsFromRaw = (raw?: string) => {
	const parsed = parseMkTags(raw)
	selectedMkTags.value = parsed.filter(tag => MK_SKILL_OPTIONS.includes(tag as (typeof MK_SKILL_OPTIONS)[number]))
	syncMkTagsToForm()
}

const setMkTag = (tag: string, checked: boolean) => {
	const index = selectedMkTags.value.indexOf(tag)
	if (checked && index < 0) {
		selectedMkTags.value.push(tag)
	}
	if (!checked && index >= 0) {
		selectedMkTags.value.splice(index, 1)
	}
	syncMkTagsToForm()
}

const ensureCompanyOption = (code?: string, name?: string) => {
	const mkCompanyCode = String(code ?? '').trim()
	const mkCompanyName = String(name ?? '').trim()
	if (!mkCompanyCode) {
		return
	}
	if (!companyOptions.value.some(item => item.mkCompanyCode === mkCompanyCode)) {
		companyOptions.value.unshift({ mkCompanyCode, mkCompanyName: mkCompanyName || mkCompanyCode })
	}
}

const loadCompanyOptions = async () => {
	try {
		const { data } = await service.get('/mgt/dating/dtMatchmakingCompany/list', {
			params: { mkCompanyIdentityStatusCode: '1', pageNo: 1, pageSize: 500 }
		})
		companyOptions.value = (data?.records || [])
			.map((item: { mkCompanyCode?: string; mkCompanyName?: string }) => ({
				mkCompanyCode: String(item.mkCompanyCode ?? '').trim(),
				mkCompanyName: String(item.mkCompanyName ?? '').trim()
			}))
			.filter((item: CompanyOption) => item.mkCompanyCode)
	} catch {
		companyOptions.value = []
	}
}

const onCompanyChange = (code: string) => {
	if (!code) {
		dataForm.mkCompanyName = ''
		return
	}
	const item = companyOptions.value.find(row => row.mkCompanyCode === code)
	dataForm.mkCompanyName = item?.mkCompanyName || ''
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
	mkScore: undefined as number | undefined,
	mkChannelsFinderUserName: ''
})

const dataForm = reactive(emptyForm())

const applyLoadedForm = () => {
	dataForm.mkIdentityStatusCode = statusCodeKey(dataForm.mkIdentityStatusCode)
	cityCascaderValue.value = parseCityToCascader(dataForm.mkCityName, dataForm.mkCityCode)
	setSelectedMkTagsFromRaw(dataForm.mkTags)
	ensureCompanyOption(dataForm.mkCompanyCode, dataForm.mkCompanyName)
}

const init = (id?: string) => {
	visible.value = true
	userPickerVisible.value = false
	Object.assign(dataForm, emptyForm())
	mkUserDisplayText.value = ''
	cityCascaderValue.value = []
	selectedMkTags.value = []
	void loadCompanyOptions()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		service.get('/mgt/dating/dtMatchmaker/queryById?id=' + encodeURIComponent(id)).then(res => {
			Object.assign(dataForm, res.data)
			dataForm.mkIdentityStatusCode = statusCodeKey(dataForm.mkIdentityStatusCode)
			syncDisplayFromUserCode(dataForm.mkUserCode)
			applyLoadedForm()
		})
	}
}

const dataRules = {
	mkUserCode: [{ required: true, message: '请选择用户', trigger: ['change', 'blur'] }],
	mkName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
	mkChannelsFinderUserName: [
		{
			validator: (_rule: unknown, value: string, callback: (err?: Error) => void) => {
				const v = String(value ?? '').trim()
				if (v && !v.startsWith('sph')) {
					callback(new Error('视频号 ID 需以 sph 开头'))
					return
				}
				callback()
			},
			trigger: 'blur'
		}
	]
}

const submitHandle = () => {
	syncMkTagsToForm()
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

<style scoped>
.mk-tag-grid {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.mk-tag-item {
	cursor: pointer;
}
</style>
