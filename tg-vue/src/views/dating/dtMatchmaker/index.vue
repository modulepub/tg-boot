<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="mkName">
				<el-input v-model="state.queryForm.mkName" placeholder="红娘姓名" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkPhone">
				<el-input v-model="state.queryForm.mkPhone" placeholder="电话" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkUserCode">
				<el-input v-model="state.queryForm.mkUserCode" placeholder="用户号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="mkCompanyName">
				<el-input v-model="state.queryForm.mkCompanyName" placeholder="婚介所名称" clearable></el-input>
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
				<el-button v-auth="'datingDtMatchmakerAdd'" icon="Plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
			</el-space>
			<el-space>
				<el-button v-auth="'datingDtMatchmakerDelete'" icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
			</el-space>
		</el-space>
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table" @selection-change="selectionChangeHandle">
			<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
			<el-table-column prop="mkName" label="姓名" header-align="center" align="center" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkPhone" label="电话" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkUserCode" label="用户号" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCityName" label="城市" header-align="center" align="center" min-width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkCompanyName" label="婚介所" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkIdentityStatusCode" label="认证状态" header-align="center" align="center" width="100" show-overflow-tooltip></el-table-column>
			<el-table-column prop="mkScore" label="评分" header-align="center" align="center" width="80"></el-table-column>
			<el-table-column prop="mkServiceUserCount" label="服务人数" header-align="center" align="center" width="100"></el-table-column>
			<el-table-column label="操作" fixed="right" header-align="center" align="center" width="150">
				<template #default="scope">
					<el-button v-auth="'datingDtMatchmakerModify'" type="primary" link @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
					<el-button v-auth="'datingDtMatchmakerDelete'" type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
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

		<add-or-edit ref="addOrEditRef" @refreshDataList="getDataList"></add-or-edit>
	</el-card>
</template>

<script setup lang="ts" name="DatingDtMatchmakerIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'
import AddOrEdit from './add-or-edit.vue'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/dating/dtMatchmaker/list',
	deleteUrl: '/mgt/dating/dtMatchmaker/delete',
	queryForm: {
		mkName: '',
		mkPhone: '',
		mkUserCode: '',
		mkCompanyName: ''
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const addOrUpdateHandle = (id?: string) => {
	addOrEditRef.value.init(id)
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, reset } = useCrud(state)
</script>
