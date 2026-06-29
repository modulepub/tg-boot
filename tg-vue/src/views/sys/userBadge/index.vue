<template>
	<el-card class="layout-query">
		<el-form ref="queryRef" :inline="true" :model="state.queryForm" @keyup.enter="getDataList()">
			<el-form-item prop="userPhone">
				<el-input v-model="state.queryForm.userPhone" placeholder="手机号" clearable></el-input>
			</el-form-item>
			<el-form-item prop="userNickName">
				<el-input v-model="state.queryForm.userNickName" placeholder="昵称" clearable></el-input>
			</el-form-item>
			<el-form-item prop="userCode">
				<el-input v-model="state.queryForm.userCode" placeholder="用户编码" clearable></el-input>
			</el-form-item>
			<el-form-item prop="badgeKey">
				<el-select v-model="state.queryForm.badgeKey" placeholder="角标类型" clearable style="width: 160px">
					<el-option v-for="item in BADGE_KEY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
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
		<el-table v-loading="state.dataListLoading" :data="state.dataList" border class="layout-table">
			<el-table-column prop="userNickName" label="昵称" header-align="center" align="center" min-width="120" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userPhone" label="手机号" header-align="center" align="center" min-width="130" show-overflow-tooltip></el-table-column>
			<el-table-column prop="userCode" label="用户编码" header-align="center" align="center" min-width="180" show-overflow-tooltip></el-table-column>
			<el-table-column prop="badgeKey" label="角标类型" header-align="center" align="center" min-width="140">
				<template #default="scope">
					{{ badgeKeyLabel(scope.row.badgeKey) }}
				</template>
			</el-table-column>
			<el-table-column prop="badgeCount" label="角标数量" header-align="center" align="center" width="100">
				<template #default="scope">
					<el-tag :type="Number(scope.row.badgeCount) > 0 ? 'danger' : 'info'">
						{{ scope.row.badgeCount ?? 0 }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="updateTime" label="更新时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
			<el-table-column prop="createTime" label="创建时间" header-align="center" align="center" min-width="160" show-overflow-tooltip></el-table-column>
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

<script setup lang="ts" name="SysUserBadgeIndex">
import { useCrud } from '@/hooks'
import { reactive, ref } from 'vue'
import { IHooksOptions } from '@/hooks/interface'

const BADGE_KEY_OPTIONS = [
	{ value: 'me_recommend', label: '我的-推荐' },
	{ value: 'me_like_me', label: '我的-喜欢我' },
	{ value: 'me_my_like', label: '我的-我喜欢' },
	{ value: 'me_contact', label: '我的-联系人' }
]

const BADGE_KEY_MAP = Object.fromEntries(BADGE_KEY_OPTIONS.map(item => [item.value, item.label]))

const queryRef = ref()

const state: IHooksOptions = reactive({
	dataListUrl: '/mgt/system/sysUserBadge/list',
	queryForm: {
		userPhone: '',
		userNickName: '',
		userCode: '',
		badgeKey: ''
	}
})

const { getDataList, sizeChangeHandle, currentChangeHandle, reset } = useCrud(state)

function badgeKeyLabel(raw: unknown): string {
	const key = String(raw ?? '').trim()
	return BADGE_KEY_MAP[key] || key || '—'
}
</script>
