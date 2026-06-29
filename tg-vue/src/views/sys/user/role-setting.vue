<template>
	<el-dialog v-model="visible" :title="title" width="820px" @close="handleClose">
		<el-card>
			<el-space style="margin-bottom: 12px">
				<el-button type="primary" icon="Plus" @click="addRow">新增</el-button>
			</el-space>
			<el-table v-loading="loading" :data="orgRoleRows" border class="layout-table">
				<el-table-column label="机构" header-align="center" min-width="260">
					<template #default="scope">
						<el-tree-select
							v-model="scope.row.orgCode"
							:data="orgTree"
							node-key="orgCode"
							value-key="orgCode"
							check-strictly
							:render-after-expand="false"
							:props="{ label: 'orgName', children: 'children' }"
							placeholder="请选择机构"
							filterable
							clearable
							style="width: 100%"
						/>
					</template>
				</el-table-column>
				<el-table-column label="角色" header-align="center" min-width="260">
					<template #default="scope">
						<el-select v-model="scope.row.roleCodes" placeholder="请选择角色" clearable filterable multiple style="width: 100%">
							<el-option v-for="role in roleList" :key="role.roleCode" :label="role.roleName" :value="role.roleCode"></el-option>
						</el-select>
					</template>
				</el-table-column>
				<el-table-column label="操作" header-align="center" align="center" width="100">
					<template #default="scope">
						<el-button type="danger" link @click="removeRow(scope.$index)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</el-card>
		<template #footer>
			<el-space>
				<el-button @click="visible = false">取消</el-button>
				<el-button type="primary" @click="handleSave">保存</el-button>
			</el-space>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import service from '@/utils/request'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refresh-data-list'])

interface OrgRoleRow {
	rowKey: number
	orgCode: string
	roleCodes: string[]
}

const visible = ref(false)
const userCode = ref('')
const userRealName = ref('')
const title = ref('角色设置')
const loading = ref(false)
const orgRoleRows = ref<OrgRoleRow[]>([])
const orgTree = ref<any[]>([])
const roleList = ref<any[]>([])

watch(
	() => visible.value,
	newVal => {
		if (newVal) {
			title.value = `${userRealName.value} - 角色设置`
			loadData()
		}
	}
)

const init = (userCodeParam: string, name: string) => {
	userCode.value = userCodeParam
	userRealName.value = name
	visible.value = true
}

defineExpose({ init })

const createRow = (): OrgRoleRow => ({
	rowKey: Date.now() + Math.random(),
	orgCode: '',
	roleCodes: []
})

const addRow = () => {
	orgRoleRows.value.push(createRow())
}

const removeRow = (index: number) => {
	orgRoleRows.value.splice(index, 1)
}

const loadOrgTree = async () => {
	const res = await service.get('/mgt/sysOrganization/listCompany')
	orgTree.value = res.data || []
}

const loadRoleList = async () => {
	const res = await service.get('/mgt/sysRole/list')
	roleList.value = res.data.records || []
}

const loadUserOrgRoles = async () => {
	const res = await service.get('/mgt/sysUserOrganization/listByUserCode', {
		params: { userCode: userCode.value }
	})
	const records = res.data || []
	const orgMap = new Map<string, string[]>()
	records.forEach((item: any) => {
		if (!item.orgCode || !item.roleCode) {
			return
		}
		const roles = orgMap.get(item.orgCode) || []
		if (!roles.includes(item.roleCode)) {
			roles.push(item.roleCode)
		}
		orgMap.set(item.orgCode, roles)
	})
	orgRoleRows.value = Array.from(orgMap.entries()).map(([orgCode, roleCodes]) => ({
		rowKey: Date.now() + Math.random(),
		orgCode,
		roleCodes
	}))
	if (orgRoleRows.value.length === 0) {
		addRow()
	}
}

const loadData = async () => {
	loading.value = true
	try {
		await Promise.all([loadOrgTree(), loadRoleList()])
		await loadUserOrgRoles()
	} catch (error) {
		console.error('加载角色设置失败:', error)
		orgRoleRows.value = [createRow()]
	} finally {
		loading.value = false
	}
}

const handleSave = async () => {
	const orgSet = new Set<string>()
	const sysUserOrganizationList: Array<{ orgCode: string; roleCode: string }> = []

	for (const row of orgRoleRows.value) {
		if (!row.orgCode) {
			continue
		}
		if (orgSet.has(row.orgCode)) {
			ElMessage.warning('同一机构请勿重复添加，请合并到一行')
			return
		}
		orgSet.add(row.orgCode)
		const roleCodes = row.roleCodes || []
		if (roleCodes.length === 0) {
			ElMessage.warning('请为每个机构至少选择一个角色')
			return
		}
		roleCodes.forEach(roleCode => {
			sysUserOrganizationList.push({
				orgCode: row.orgCode,
				roleCode
			})
		})
	}

	try {
		await service.post('/mgt/sysUserOrganization/save', {
			userCode: userCode.value,
			sysUserOrganizationList
		})
		visible.value = false
		emit('refresh-data-list')
		ElMessage.success('角色设置保存成功')
	} catch (error) {
		console.error('保存角色设置失败:', error)
		ElMessage.error('角色设置保存失败')
	}
}

const handleClose = () => {
	visible.value = false
}
</script>

<style scoped></style>
