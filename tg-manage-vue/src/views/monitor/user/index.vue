<template>
	<el-card>
		<el-table
			v-loading="state.dataListLoading"
			align="center"
			show-overflow-tooltip
			:data="state.dataList"
			border
			style="width: 100%"
			@selection-change="selectionChangeHandle"
		>
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="userName" label="用户名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userRealName" label="姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button type="primary" link @click="logoutHandle(scope.row.userName)">踢出</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			:current-page="state.pageNo"
			:page-size="state.pageSize"
			:total="state.total"
			layout="total, sizes, prev, pager, next, jumper"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
		>
		</el-pagination>
	</el-card>
</template>

<script setup lang="ts">
import { useCrud } from '@/hooks'
import { reactive } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '@/utils/request'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sysUser/listOnline'
})

const logoutHandle = (userName: string) => {
	ElMessageBox.confirm('确定踢出该用户?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	})
		.then(() => {
			let params = {
				userName: userName
			}
			service.post('/mgt/sysUser/takeOff', params).then(() => {
				ElMessage.success({
					message: '操作成功',
					duration: 500
				})
				getDataList()
			})
		})
		.catch(() => {})
}
const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle } = useCrud(state)
</script>
