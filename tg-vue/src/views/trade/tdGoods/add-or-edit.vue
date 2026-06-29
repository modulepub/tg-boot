<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" width="900px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="110px">
			<el-form-item label="商品编码" prop="tdGdCode">
				<el-input v-model="dataForm.tdGdCode" placeholder="商品编码"></el-input>
			</el-form-item>
			<el-form-item label="商品名称" prop="tdGdName">
				<el-input v-model="dataForm.tdGdName" placeholder="商品名称"></el-input>
			</el-form-item>
			<el-form-item label="商品标签" prop="tdGdTag">
				<el-input v-model="dataForm.tdGdTag" placeholder="商品标签"></el-input>
			</el-form-item>
			<el-form-item label="商品价格" prop="tdGdPrice">
				<el-input v-model="dataForm.tdGdPrice" placeholder="商品价格"></el-input>
			</el-form-item>
			<el-form-item label="服务期(天)" prop="tdGdDayPeriod">
				<el-input-number v-model="dataForm.tdGdDayPeriod" :min="0" :step="1" :precision="0" placeholder="0 表示无服务期" style="width: 100%" />
			</el-form-item>
			<el-form-item label="商品类目" prop="tdGdCgyCode">
				<el-select v-model="dataForm.tdGdCgyCode" placeholder="请选择商品类目" filterable clearable style="width: 100%" @change="handleCategoryChange">
					<el-option v-for="item in categoryOptions" :key="item.tdGdCgyCode" :label="item.tdGdCgyName" :value="item.tdGdCgyCode" />
				</el-select>
			</el-form-item>
			<el-form-item label="库存数量" prop="tdGdInventoryNum">
				<el-input v-model="dataForm.tdGdInventoryNum" placeholder="库存数量"></el-input>
			</el-form-item>
			<el-form-item label="分佣比例" prop="tdGdCommissionRate">
				<el-input-number v-model="dataForm.tdGdCommissionRate" :min="0" :max="1" :step="0.01" :precision="4" style="width: 100%" />
			</el-form-item>
			<el-form-item label="上架状态" prop="tdGdEnabledCode">
				<el-radio-group v-model="dataForm.tdGdEnabledCode">
					<el-radio value="1">上架</el-radio>
					<el-radio value="0">下架</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="是否隐藏" prop="tdGdHiddenStatusCode">
				<el-radio-group v-model="dataForm.tdGdHiddenStatusCode">
					<el-radio value="0">显示</el-radio>
					<el-radio value="1">隐藏</el-radio>
				</el-radio-group>
				<span class="hidden-tip">隐藏后移动端不展示该商品</span>
			</el-form-item>
			<el-form-item label="描述" prop="tdGdDescription">
				<tg-editor v-if="editorReady" :key="editorMountKey" v-model="dataForm.tdGdDescription" placeholder="商品描述" style="height: 320px;" />
			</el-form-item>
			<el-form-item label="商品权益">
				<div class="benefit-toolbar">
					<el-button type="primary" link @click="addBenefitRow">新增权益</el-button>
				</div>
				<el-table :data="dataForm.benefitList" border size="small" empty-text="暂无权益，可点击「新增权益」添加">
					<el-table-column label="权益key" min-width="160">
						<template #default="{ row }">
							<el-select v-model="row.tdGdBnfKey" placeholder="请选择或输入" filterable allow-create default-first-option style="width: 100%">
								<el-option v-for="item in benefitKeyOptions" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
						</template>
					</el-table-column>
					<el-table-column label="权益值" width="140">
						<template #default="{ row }">
							<el-input-number v-model="row.tdGdBnfValue" :min="1" :step="1" :precision="0" style="width: 100%" />
						</template>
					</el-table-column>
					<el-table-column label="权益描述" min-width="180">
						<template #default="{ row }">
							<el-input v-model="row.tdGdBnfDesc" placeholder="权益描述" />
						</template>
					</el-table-column>
					<el-table-column label="操作" width="80" align="center">
						<template #default="{ $index }">
							<el-button type="danger" link @click="removeBenefitRow($index)">删除</el-button>
						</template>
					</el-table-column>
				</el-table>
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
const editorReady = ref(false)
const editorMountKey = ref(0)
const dataFormRef = ref()
const categoryOptions = ref<{ tdGdCgyCode: string; tdGdCgyName: string }[]>([])

interface IBenefitRow {
	id?: string
	tdGdBnfKey?: string
	tdGdBnfValue?: number
	tdGdBnfDesc?: string
}

const benefitKeyOptions = [
	{ value: 'addFriendNum', label: 'addFriendNum（添加好友）' },
	{ value: 'recNum', label: 'recNum（推荐）' },
	{ value: 'matchNum', label: 'matchNum（牵线）' }
]

const dataForm = reactive({
	id: '',
	tdGdCode: '',
	tdGdName: '',
	tdGdTag: '',
	tdGdPrice: '',
	tdGdDayPeriod: undefined as number | undefined,
	tdGdCgyCode: '',
	tdGdCgyName: '',
	tdGdInventoryNum: '',
	tdGdCommissionRate: 0.9,
	tdGdEnabledCode: '1',
	tdGdHiddenStatusCode: '0',
	tdGdDescription: '',
	benefitList: [] as IBenefitRow[]
})

const dataRules = ref({
	tdGdCode: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
	tdGdName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
	tdGdCgyCode: [{ required: true, message: '请选择商品类目', trigger: 'change' }]
})

const getCategoryOptions = async () => {
	const res = await service.get('/mgt/trade/tdGoodsCategory/list', {
		params: {
			pageNo: 1,
			pageSize: 9999
		}
	})
	categoryOptions.value = res?.data?.records || []
}

const handleCategoryChange = (categoryCode?: string) => {
	const category = categoryOptions.value.find(item => item.tdGdCgyCode === categoryCode)
	dataForm.tdGdCgyName = category?.tdGdCgyName || ''
}

const addBenefitRow = () => {
	dataForm.benefitList.push({
		tdGdBnfKey: '',
		tdGdBnfValue: 10,
		tdGdBnfDesc: ''
	})
}

const removeBenefitRow = (index: number) => {
	dataForm.benefitList.splice(index, 1)
}

const init = async (id?: number) => {
	editorReady.value = false
	visible.value = true
	dataForm.id = ''
	getCategoryOptions()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		const res = await service.get('/mgt/trade/tdGoods/queryById?id=' + id)
		Object.assign(dataForm, res.data)
		dataForm.benefitList = Array.isArray(res.data?.benefitList) ? res.data.benefitList : []
		if (dataForm.tdGdEnabledCode !== '0')
			dataForm.tdGdEnabledCode = '1'
		dataForm.tdGdHiddenStatusCode = res.data?.tdGdHiddenStatusCode === '1' ? '1' : '0'
	} else {
		dataForm.tdGdDescription = ''
		dataForm.benefitList = []
		dataForm.tdGdEnabledCode = '1'
		dataForm.tdGdHiddenStatusCode = '0'
	}
	editorMountKey.value += 1
	editorReady.value = true
}

const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const http = dataForm.id ? service.post('/mgt/trade/tdGoods/edit', dataForm) : service.post('/mgt/trade/tdGoods/add', dataForm)
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
.benefit-toolbar {
	margin-bottom: 8px;
}
.hidden-tip {
	margin-left: 12px;
	font-size: 12px;
	color: #909399;
}
</style>
