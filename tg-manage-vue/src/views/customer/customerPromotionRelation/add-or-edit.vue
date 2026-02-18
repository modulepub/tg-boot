<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px" @keyup.enter="submitHandle()">
			<el-form-item label="主键ID" prop="id">
				<el-input v-model="dataForm.id" placeholder="主键ID"></el-input>
			</el-form-item>
			<el-form-item label="序列编号" prop="seqNo">
				<el-input v-model="dataForm.seqNo" placeholder="序列编号"></el-input>
			</el-form-item>
			<el-form-item label="机构编码" prop="orgCode">
				<el-input v-model="dataForm.orgCode" placeholder="机构编码"></el-input>
			</el-form-item>
			<el-form-item label="更新人" prop="updateBy">
				<el-input v-model="dataForm.updateBy" placeholder="更新人"></el-input>
			</el-form-item>
			<el-form-item label="更新时间" prop="updateTime">
				<el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
			</el-form-item>
			<el-form-item label="创建人" prop="createBy">
				<el-input v-model="dataForm.createBy" placeholder="创建人"></el-input>
			</el-form-item>
			<el-form-item label="创建时间" prop="createTime">
				<el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
			</el-form-item>
			<el-form-item label="乐观锁版本号" prop="version">
				<el-input v-model="dataForm.version" placeholder="乐观锁版本号"></el-input>
			</el-form-item>
			<el-form-item label="逻辑删除 0-未删 1-已删" prop="deleted">
				<el-input v-model="dataForm.deleted" placeholder="逻辑删除 0-未删 1-已删"></el-input>
			</el-form-item>
			<el-form-item label="电访关系编码" prop="promotionRelCode">
				<el-input v-model="dataForm.promotionRelCode" placeholder="电访关系编码"></el-input>
			</el-form-item>
			<el-form-item label="客户编码" prop="cusCode">
				<el-input v-model="dataForm.cusCode" placeholder="客户编码"></el-input>
			</el-form-item>
			<el-form-item label="用户编码" prop="userCode">
				<el-input v-model="dataForm.userCode" placeholder="用户编码"></el-input>
			</el-form-item>
			<el-form-item label="用户姓名" prop="userRealName">
				<el-input v-model="dataForm.userRealName" placeholder="用户姓名"></el-input>
			</el-form-item>
			<el-form-item label="客户姓名" prop="cusName">
				<el-input v-model="dataForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item label="客户身份证号" prop="cusIdCardNum">
				<el-input v-model="dataForm.cusIdCardNum" placeholder="客户身份证号"></el-input>
			</el-form-item>
			<el-form-item label="客户手机号" prop="cusPhone">
				<el-input v-model="dataForm.cusPhone" placeholder="客户手机号"></el-input>
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
	promotionRelCode: '',
	cusCode: '',
	userCode: '',
	userRealName: '',
	cusName: '',
	cusIdCardNum: '',
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
		getCustomerTelemarketerRelation(id)
	}
}

const getCustomerTelemarketerRelation = (id: number) => {
	service.get('/mgt/customer/customerPromotionRelation/queryById?id=' + id).then(res => {
		Object.assign(dataForm, res.data)
	})
}

const dataRules = ref({
	id: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	cusCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http: any
		if (dataForm.id) {
			http = service.post('/mgt/customer/customerPromotionRelation/edit', dataForm)
		} else {
			http = service.post('/mgt/customer/customerPromotionRelation/add', dataForm)
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
