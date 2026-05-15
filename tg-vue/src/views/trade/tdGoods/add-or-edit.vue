<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" width="680px">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="110px" @keyup.enter="submitHandle()">
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
			<el-form-item label="商品类目" prop="tdGdCgyCode">
				<el-select v-model="dataForm.tdGdCgyCode" placeholder="请选择商品类目" filterable clearable style="width: 100%" @change="handleCategoryChange">
					<el-option v-for="item in categoryOptions" :key="item.tdGdCgyCode" :label="item.tdGdCgyName" :value="item.tdGdCgyCode" />
				</el-select>
			</el-form-item>
			<el-form-item label="库存数量" prop="tdGdInventoryNum">
				<el-input v-model="dataForm.tdGdInventoryNum" placeholder="库存数量"></el-input>
			</el-form-item>
			<el-form-item label="描述" prop="tdGdDescription">
				<el-input v-model="dataForm.tdGdDescription" type="textarea" placeholder="商品描述"></el-input>
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
const categoryOptions = ref<{ tdGdCgyCode: string; tdGdCgyName: string }[]>([])

const dataForm = reactive({
	id: '',
	tdGdCode: '',
	tdGdName: '',
	tdGdTag: '',
	tdGdPrice: '',
	tdGdCgyCode: '',
	tdGdCgyName: '',
	tdGdInventoryNum: '',
	tdGdDescription: ''
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

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''
	getCategoryOptions()
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (id) {
		service.get('/mgt/trade/tdGoods/queryById?id=' + id).then(res => {
			Object.assign(dataForm, res.data)
		})
	}
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
