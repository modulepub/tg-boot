<template>
	<el-row :gutter="10">
		<el-col :span="5">
			<tg-data-tree-left
				:props="{ label: 'orgName', children: 'children' }"
				url="/mgt/sysOrganization/listCompany"
				title="机构列表"
				@click="handleClick"
			/>
		</el-col>
		<el-col :span="19">
			<el-card class="layout-query">
				<el-form ref="queryRef" :inline="true" :model="state.queryForm">
					<el-form-item prop="username">
						<el-input v-model="state.queryForm.userName" placeholder="用户名"></el-input>
					</el-form-item>
					<el-form-item prop="mobile">
						<el-input v-model="state.queryForm.userPhone" placeholder="手机号"></el-input>
					</el-form-item>
					<el-form-item prop="gender">
						<tg-dict-select v-model="state.queryForm.userSexCode" dict-code="userSexCode" clearable placeholder="性别"></tg-dict-select>
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
						<el-button icon="Plus" type="primary" @click="addOrEditHandle()">新增</el-button>
					</el-space>
					<el-space>
						<el-button icon="Delete" plain type="danger" @click="deleteBatchHandle()">批量删除</el-button>
					</el-space>
				</el-space>

				<el-table
					v-loading="state.dataListLoading"
					align="center"
					show-overflow-tooltip
					:data="state.dataList"
					border
					class="layout-table"
					@selection-change="selectionChangeHandle"
				>
					<el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
					<el-table-column prop="userRealName" label="姓名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column prop="userName" label="用户名" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column prop="userPhone" label="手机号" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column prop="userOrgNames" label="所属机构" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<tg-dict-column prop="userEnabledCode" label="是否启用" dict-code="userEnabledCode"></tg-dict-column>
					<el-table-column prop="createTime" label="创建时间" header-align="center" align="center" show-overflow-tooltip></el-table-column>
					<el-table-column label="操作" fixed="right" header-align="center" align="center" width="220">
						<template #default="scope">
							<el-button type="primary" link @click="addOrEditHandle(scope.row.id)">修改</el-button>
							<el-button type="primary" link @click="roleSettingHandle(scope.row)">角色</el-button>
							<el-button type="primary" link @click="deleteBatchHandle(scope.row.id)">删除</el-button>
						</template>
					</el-table-column>
				</el-table>
				<el-pagination
					:current-page="state.pageNo"
					:page-sizes="state.pageSizes"
					:page-size="state.pageSize"
					:total="state.total"
					layout="total, sizes, prev, pager, next, jumper"
					@size-change="sizeChangeHandle"
					@current-change="currentChangeHandle"
				>
				</el-pagination>
			</el-card>
		</el-col>
	</el-row>

	<!-- 弹窗, 新增 / 修改 -->
	<add-or-edit ref="addOrEditRef" @refresh-data-list="getDataList"></add-or-edit>
	<!-- 弹窗, 角色设置 -->
	<role-setting ref="roleSettingRef" @refresh-data-list="getDataList"></role-setting>
</template>

<script setup lang="ts" name="userIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import AddOrEdit from './add-or-edit.vue'
import RoleSetting from './role-setting.vue'
import { IHooksOptions } from '@/hooks/interface'

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/sysUser/list',
	deleteUrl: '/mgt/sysUser/delete',
	order: '-createTime',
	queryForm: {
		userName: null,
		userPhone: null,
		userSexCode: null,
		orgCode: null
	}
})

const queryRef = ref()
const addOrEditRef = ref()
const roleSettingRef = ref()

const addOrEditHandle = (id?: number) => {
	addOrEditRef.value.init(id)
}

const roleSettingHandle = (row: any) => {
	roleSettingRef.value.init(row.userCode, row.userRealName)
}

const handleClick = (obj: any) => {
	state.queryForm.orgCode = obj.orgCode
	getDataList()
}

const { getDataList, selectionChangeHandle, sizeChangeHandle, currentChangeHandle, deleteBatchHandle, downloadHandle, reset } = useCrud(state)
</script>
