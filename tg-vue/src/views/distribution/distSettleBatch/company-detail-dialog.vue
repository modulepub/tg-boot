<template>
	<el-dialog v-model="visible" title="企业详情" width="760px" :close-on-click-modal="false" destroy-on-close @closed="onClosed">
		<div v-loading="loading">
			<template v-if="detail">
				<el-descriptions :column="2" border size="small">
					<el-descriptions-item label="企业编码">{{ detail.mkCompanyCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="企业名称">{{ detail.mkCompanyName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="统一社会信用代码">{{ detail.mkCompanyUsciCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="公司电话">{{ detail.mkCompanyTel || '—' }}</el-descriptions-item>
					<el-descriptions-item label="法人姓名">{{ detail.mkCompanyLegalName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="法人证件号">{{ detail.mkCompanyLegalIdNo || '—' }}</el-descriptions-item>
					<el-descriptions-item label="公司地址" :span="2">{{ detail.mkCompanyAddressDetail || '—' }}</el-descriptions-item>
					<el-descriptions-item label="对公账号">{{ detail.mkCompanyPublicAccountNo || '—' }}</el-descriptions-item>
					<el-descriptions-item label="开户行">{{ detail.mkCompanyBankName || '—' }}</el-descriptions-item>
					<el-descriptions-item label="开户地" :span="2">{{ detail.mkCompanyBankLocation || '—' }}</el-descriptions-item>
					<el-descriptions-item label="管理员用户号">{{ detail.mkCompanyAdminUserCode || '—' }}</el-descriptions-item>
					<el-descriptions-item label="创建时间">{{ detail.createTime || '—' }}</el-descriptions-item>
				</el-descriptions>
			</template>
		</div>
		<template #footer>
			<el-button @click="visible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const visible = ref(false)
const loading = ref(false)
const detail = ref<Record<string, any> | null>(null)

const onClosed = () => {
	detail.value = null
}

const init = async (mkCompanyCode: string) => {
	const code = String(mkCompanyCode || '').trim()
	if (!code) {
		ElMessage.warning('缺少公司编码')
		return
	}
	visible.value = true
	loading.value = true
	detail.value = null
	try {
		const listRes: any = await service.get('/mgt/dating/dtMatchmakingCompany/list', {
			params: { mkCompanyCode: code, pageNo: 1, pageSize: 1 }
		})
		const company = listRes?.data?.records?.[0]
		if (!company?.id) {
			ElMessage.warning('未找到该企业')
			visible.value = false
			return
		}
		const detailRes: any = await service.get('/mgt/dating/dtMatchmakingCompany/queryById', {
			params: { id: company.id }
		})
		detail.value = detailRes?.data || null
	} catch {
		visible.value = false
	} finally {
		loading.value = false
	}
}

defineExpose({ init })
</script>
