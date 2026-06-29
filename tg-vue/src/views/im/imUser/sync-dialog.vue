<template>
	<el-dialog v-model="visible" title="同步 IM 用户" width="720px" destroy-on-close @open="onOpen">
		<el-form :inline="true" @submit.prevent="loadList">
			<el-form-item>
				<el-input v-model="keyword" placeholder="编码/昵称/姓名/手机号" clearable style="width: 220px" @keyup.enter="loadList"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" icon="Search" @click="loadList">查询</el-button>
			</el-form-item>
		</el-form>
		<el-table v-loading="loading" :data="dataList" border max-height="400" @selection-change="onSelectionChange">
			<el-table-column type="selection" width="48" align="center"></el-table-column>
			<el-table-column prop="userCode" label="用户编码" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userNickName" label="昵称" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userRealName" label="真实姓名" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userPhone" label="手机号" width="120" show-overflow-tooltip></el-table-column>
		</el-table>
		<el-pagination
			class="layout-pagination"
			:current-page="pageNo"
			:page-size="pageSize"
			:total="total"
			layout="total, prev, pager, next"
			@current-change="onPageChange"
		></el-pagination>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" :loading="submitting" :disabled="!selections.length" @click="confirmSync">确定同步</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts" name="ImImUserSyncDialog">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const visible = defineModel<boolean>()
const emit = defineEmits(['success'])

const loading = ref(false)
const submitting = ref(false)
const keyword = ref('')
const dataList = ref<any[]>([])
const selections = ref<any[]>([])
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadList = async () => {
	loading.value = true
	try {
		const res: any = await service.get('/mgt/sysUser/listImUnsynced', {
			params: {
				keyword: keyword.value || undefined,
				pageNo: pageNo.value,
				pageSize: pageSize.value
			}
		})
		dataList.value = res.data?.records || []
		total.value = res.data?.total || 0
	} finally {
		loading.value = false
	}
}

const onOpen = () => {
	keyword.value = ''
	pageNo.value = 1
	selections.value = []
	loadList()
}

const onSelectionChange = (rows: any[]) => {
	selections.value = rows
}

const onPageChange = (p: number) => {
	pageNo.value = p
	loadList()
}

const confirmSync = async () => {
	const userCodes = selections.value.map((r) => r.userCode).filter(Boolean)
	if (!userCodes.length) {
		return
	}
	submitting.value = true
	try {
		const res: any = await service.post('/mgt/im/imUser/sync', userCodes)
		ElMessage.success(res.message || '同步完成')
		visible.value = false
		emit('success')
	} finally {
		submitting.value = false
	}
}
</script>
