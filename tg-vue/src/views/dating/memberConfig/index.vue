<template>
	<el-card v-loading="loading">
		<template #header>
			<div class="card-header">
				<span>会员配置</span>
			</div>
		</template>

		<el-alert
			type="info"
			show-icon
			:closable="false"
			title="开启后，新用户注册即自动赠送钻石会员·体验 7 天（商品编码 freevip，复用客户管理「赠送会员」核心流程）。系统监听用户注册消息，仅对首次注册的新用户生效。"
			class="mb-16"
		/>

		<el-form :model="form" label-width="220px" class="config-form">
			<el-form-item label="注册即赠钻石会员·体验7天">
				<el-switch
					v-model="form.registerGift"
					active-text="开启"
					inactive-text="关闭"
					:loading="saving"
					@change="saveHandle"
				/>
			</el-form-item>
		</el-form>
	</el-card>
</template>

<script setup lang="ts" name="DatingMemberConfigIndex">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const STATUS_YES = '1'
const STATUS_NO = '0'

const loading = ref(false)
const saving = ref(false)

const form = reactive({
	registerGift: false
})

const getConfig = () => {
	loading.value = true
	service
		.get('/mgt/dating/memberConfig/get')
		.then((res: { data?: { cfgRegisterGiftFreevipStatusCode?: string } }) => {
			form.registerGift = res.data?.cfgRegisterGiftFreevipStatusCode === STATUS_YES
		})
		.finally(() => {
			loading.value = false
		})
}

const saveHandle = (val: boolean) => {
	saving.value = true
	service
		.post('/mgt/dating/memberConfig/save', {
			cfgRegisterGiftFreevipStatusCode: val ? STATUS_YES : STATUS_NO
		})
		.then(() => {
			ElMessage.success('保存成功')
		})
		.catch(() => {
			// 保存失败时回滚开关状态
			form.registerGift = !val
		})
		.finally(() => {
			saving.value = false
		})
}

onMounted(() => {
	getConfig()
})
</script>

<style scoped>
.card-header {
	font-weight: 600;
}
.mb-16 {
	margin-bottom: 16px;
}
.config-form {
	max-width: 640px;
}
</style>
