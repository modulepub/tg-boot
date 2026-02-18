<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px" @keyup.enter="submitHandle()">
			<el-form-item label="客户姓名" prop="cusName">
				<el-input v-model="dataForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item label="身份证号" prop="cusIdCardNum">
				<el-input v-model="dataForm.cusIdCardNum" placeholder="身份证号"></el-input>
			</el-form-item>
			<el-form-item label="客户性别" prop="cusSexCode">
				<tg-dict-select v-model="dataForm.cusSexCode" dict-code="userSexCode" clearable placeholder="客户性别"></tg-dict-select>
			</el-form-item>
			<el-form-item label="年龄" prop="cusAge">
				<el-input v-model="dataForm.cusAge" type="number" placeholder="年龄"></el-input>
			</el-form-item>
			<el-form-item label="身高(cm)" prop="cusHeight">
				<el-input v-model="dataForm.cusHeight" placeholder="身高(cm)"></el-input>
			</el-form-item>
			<el-form-item label="体重（kg)" prop="cusWeight"> <el-input v-model="dataForm.cusWeight" placeholder="体重（kg)"></el-input> </el-form-item>
			<el-form-item label="常驻城市" prop="cusCityResidenceCode">
				<el-input v-model="dataForm.cusCityResidenceCode" placeholder="常驻城市"></el-input>
			</el-form-item>
			<el-form-item label="是否有车" prop="cusHaveCarStatusCode">
				<tg-dict-select v-model="dataForm.cusHaveCarStatusCode" dict-code="cusHaveCarStatusCode" clearable placeholder="是否有车"></tg-dict-select>
			</el-form-item>
			<el-form-item label="是否有房" prop="cusHaveHouseStatusCode">
				<tg-dict-select
					v-model="dataForm.cusHaveHouseStatusCode"
					dict-code="cusHaveHouseStatusCode"
					clearable
					placeholder="是否有房"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="职业描述" prop="cusOccupationalDescription">
				<el-input v-model="dataForm.cusOccupationalDescription" placeholder="职业描述"></el-input>
			</el-form-item>
			<el-form-item label="年收入" prop="cusAnnualIncomeAmount">
				<el-input v-model="dataForm.cusAnnualIncomeAmount" placeholder="年收入"></el-input>
			</el-form-item>
			<el-form-item label="手机号" prop="cusPhone">
				<el-input v-model="dataForm.cusPhone" placeholder="手机号"></el-input>
			</el-form-item>
			<el-form-item label="来源" prop="cusSourceCode">
				<tg-dict-select v-model="dataForm.cusSourceCode" dict-code="cusSourceCode" clearable placeholder="来源"></tg-dict-select>
			</el-form-item>
			<el-form-item label="用户标签" prop="cusTagCode">
				<tg-dict-select v-model="dataForm.cusTagCode" dict-code="cusTagCode" clearable multiple placeholder="用户标签"></tg-dict-select>
			</el-form-item>
			<el-form-item label="客户等级" prop="cusLevelCode">
				<tg-dict-select v-model="dataForm.cusLevelCode" dict-code="cusLevelCode" clearable placeholder="客户等级"></tg-dict-select>
			</el-form-item>
			<el-form-item label="是否意向" prop="cusIntentionStatusCode">
				<tg-dict-select
					v-model="dataForm.cusIntentionStatusCode"
					dict-code="cusIntentionStatusCode"
					clearable
					placeholder="意向等级"
				></tg-dict-select>
			</el-form-item>
			<el-form-item label="意向等级" prop="cusIntentionLevelCode">
				<tg-dict-select v-model="dataForm.cusIntentionLevelCode" dict-code="cusIntentionLevelCode" clearable placeholder="意向等级"></tg-dict-select>
			</el-form-item>
			<el-form-item label="用户描述" prop="cusDesc">
				<el-input v-model="dataForm.cusDesc" type="textarea" placeholder="用户描述"></el-input>
			</el-form-item>

			<el-form-item label="客户需求" prop="cusDemand">
				<el-input rows="20" v-model="dataForm.cusDemand" type="textarea" placeholder="客户需求"></el-input>
			</el-form-item>
			<el-form-item label="用户备注" prop="cusRemark">
				<el-input disabled v-model="dataForm.cusRemark" type="textarea" placeholder="用户备注"></el-input>
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
	cusCode: '',
	cusLifePhoto: '',
	cusName: '',
	cusIdCardNum: '',
	cusIdentityAuthenticatedStatusCode: '',
	cusSexCode: '',
	cusAge: '',
	cusHeight: '',
	cusWeight: '',
	cusMaritalStatusCode: '',
	cusHandholdsNum: '',
	cusCityResidenceCode: '',
	cusHaveCarStatusCode: '',
	cusVehicleLicensePhoto: '',
	cusHaveHouseStatusCode: '',
	cusRealEstateCertificatePhoto: '',
	cusOccupationalDescription: '',
	cusAnnualIncomeAmount: '',
	cusAnnualIncomeAuthenticatedPhoto: '',
	cusPhone: '',
	cusSourceCode: '',
	cusTagCode: '',
	cusLevelCode: '',
	cusIntentionStatusCode: '',
	cusIntentionLevelCode: '',
	cusDesc: '',
	cusRemark: '',
	cusDemand: '',
	cusPoolStatusCode: '',
	cusUserCode: ''
})

const init = (id?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (id) {
		getCustomer(id)
	}
}

const getCustomer = (id: number) => {
	service.get('/mgt/customer/customer/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

const dataRules = ref({
	id: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http: any
		if (dataForm.id) {
			http = service.post('/mgt/customer/customer/edit', dataForm)
		} else {
			http = service.post('/mgt/customer/customer/add', dataForm)
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
