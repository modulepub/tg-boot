<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="fileName">
				<el-input v-model="state.queryForm.fileName" placeholder="文件名称"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button icon="Search" type="primary" @click="getDataList()">查询</el-button>
			</el-form-item>
			<el-form-item>
				<el-button icon="RefreshRight" @click="reset(queryRef)">重置</el-button>
			</el-form-item>
		</el-form>
	</el-card>

	<el-card>
		<el-space>
			<el-space>
				<tg-upload-file @input="getDataList" placeholder="上传"></tg-upload-file>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="fileCode" label="文件编码" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="fileName" label="文件名称" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<el-table-column prop="fileSize" label="文件大小（字节）" header-align="center" align="center" show-overflow-tooltip></el-table-column>
			<tg-file-column prop="fileUrl" label="文件链接" header-align="center" align="center"></tg-file-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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

		<!-- 弹窗, 新增 / 修改 -->
		<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="FilefileIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/file/file/list',
	deleteUrl: '/mgt/file/file/delete',
	queryForm: {
		orgCode: '',
		deleted: '',
		seqNo: '',
		version: '',
		fileCode: '',
		fileName: '',
		fileSize: '',
		fileUrl: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
