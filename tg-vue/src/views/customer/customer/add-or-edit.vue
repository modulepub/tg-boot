<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="客户姓名" prop="cusName">
				<el-input v-model="dataForm.cusName" placeholder="客户姓名"></el-input>
			</el-form-item>
			<el-form-item label="客户昵称" prop="cusNickName">
				<el-input v-model="dataForm.cusNickName" placeholder="客户昵称"></el-input>
			</el-form-item>
			<el-form-item v-if="dataForm.id" label="会员信息">
				<div class="member-info">
					<template v-if="dataForm.cusMemberTypeName">
						<el-tag size="small" type="warning">{{ dataForm.cusMemberTypeName }}</el-tag>
						<span class="member-expire" :class="{ 'is-expired': isMemberExpired }">
							{{ formatMemberExpire(dataForm.cusMemberExpireTime) }}
						</span>
						<span class="member-quota">
							每日权益：加好友 {{ dataForm.cusAddFriendDayLimit ?? 0 }} / 推荐 {{ dataForm.cusRecommendDayLimit ?? 0 }} / 牵线
							{{ dataForm.cusMatchDayLimit ?? 0 }}
						</span>
					</template>
					<el-tag v-else size="small" type="info">非会员</el-tag>
				</div>
			</el-form-item>
      <el-form-item label="证件类型" prop="cusIdTypeCode">
        <tg-dict-select
            v-model="dataForm.cusIdTypeCode"
            dict-code="cusIdTypeCode"
            clearable
            placeholder="证件类型"
        ></tg-dict-select>
      </el-form-item>
			<el-form-item label="证件号" prop="cusIdNo">
				<el-input v-model="dataForm.cusIdNo" placeholder="证件号"></el-input>
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
			<el-form-item label="生活城市" prop="cusCityResidenceCode">
				<el-input v-model="dataForm.cusCityResidenceCode" placeholder="生活城市"></el-input>
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
			<el-form-item label="审核状态" prop="cusAuditProcessCode">
				<tg-dict-select
					v-model="dataForm.cusAuditProcessCode"
					dict-code="cusAuditProcessCode"
					clearable
					placeholder="审核状态"
				></tg-dict-select>
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
import { computed, reactive, ref } from 'vue'
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
	cusNickName: '',
	cusMemberTypeCode: '',
	cusMemberTypeName: '',
	cusMemberExpireTime: '',
	cusAddFriendDayLimit: null as number | null,
	cusRecommendDayLimit: null as number | null,
	cusMatchDayLimit: null as number | null,
  cusIdTypeCode: '',
	cusIdNo: '',
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
	cusAuditProcessCode: '',
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

const isMemberExpired = computed(() => {
	if (!dataForm.cusMemberExpireTime) {
		return false
	}
	return new Date(dataForm.cusMemberExpireTime).getTime() < Date.now()
})

const formatMemberExpire = (time?: string) => {
	if (!time) {
		return '永久有效'
	}
	const expired = new Date(time).getTime() < Date.now()
	return `${expired ? '已过期' : '到期'}：${time}`
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

<style scoped>
.member-info {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 8px;
}
.member-expire {
	font-size: 13px;
	color: #67c23a;
}
.member-expire.is-expired {
	color: #f56c6c;
}
.member-quota {
	font-size: 12px;
	color: #909399;
}
</style>
