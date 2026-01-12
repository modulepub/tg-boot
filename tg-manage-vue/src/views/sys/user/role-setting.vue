<template>
	<el-dialog v-model="visible" :title="title" width="700px" @close="handleClose">
		<el-card>
			<el-table v-loading="orgListLoading" :data="orgList" border class="layout-table">
				<el-table-column prop="orgName" label="机构名称" header-align="center" align="center"></el-table-column>
				<el-table-column label="角色" header-align="center" align="center">
					<template #default="scope">
						<el-select v-model="scope.row.roleCodes" placeholder="请选择角色" clearable filterable multiple>
							<el-option v-for="role in roleList" :key="role.roleCode" :label="role.roleName" :value="role.roleCode"></el-option>
						</el-select>
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
const emit = defineEmits(['update:visible', 'refresh-data-list'])

const visible = ref(false)
const userCode = ref('')
const userRealName = ref('')
const title = ref('角色设置')
const orgListLoading = ref(false)
const roleListLoading = ref(false)
const orgList = ref<any[]>([])
const roleList = ref<any[]>([])

// 监听可见性变化
watch(
	() => visible.value,
	newVal => {
		if (newVal) {
			title.value = `${userRealName.value} - 角色设置`
			getOrgList()
			getRoleList()
		}
	}
)

// 初始化方法
const init = (userCodeParam: string, name: string) => {
	userCode.value = userCodeParam
	userRealName.value = name
	visible.value = true
}

// 暴露方法
defineExpose({
	init
})

// 获取机构列表
const getOrgList = async () => {
	orgListLoading.value = true
	try {
		// 使用顶部切换部门的接口获取用户所在机构列表
		// 先检查用户所在机构接口
		let res = await service.get('/mgt/sysUserOrganization/listByUser')
		orgList.value = res.data || []

		// 确保每个机构都有roleCodes字段
		orgList.value.forEach(org => {
			if (!org.roleCodes) {
				org.roleCodes = []
			}
		})

		// 获取用户在各机构的角色信息
		if (userCode.value) {
			await getUserRoles()
		}
	} catch (error) {
		console.error('获取机构列表失败:', error)
		// 初始化空机构列表
		orgList.value = []
	} finally {
		orgListLoading.value = false
	}
}

// 获取角色列表
const getRoleList = async () => {
	roleListLoading.value = true
	try {
		const res = await service.get('/mgt/sysRole/list')
		roleList.value = res.data.records || []
		console.log('roleList:', roleList)
	} catch (error) {
		console.error('获取角色列表失败:', error)
	} finally {
		roleListLoading.value = false
	}
}

// 获取用户在各机构的角色信息
const getUserRoles = async () => {
	try {
		const res = await service.get('/mgt/sysUserOrganizationRole/listByUserCode', {
			params: {
				userCode: userCode.value
			}
		})

		const userRoles = res.data || []
		// 将角色信息映射到机构列表中
		orgList.value.forEach(org => {
			// 查找该机构下的所有角色
			const roles = userRoles.filter((item: any) => item.orgCode === org.orgCode)
			if (roles.length > 0) {
				org.roleCodes = roles.map((role: any) => role.roleCode)
			} else {
				org.roleCodes = []
			}
		})
	} catch (error) {
		console.error('获取用户角色信息失败:', error)
		// 初始化角色数组
		orgList.value.forEach(org => {
			org.roleCodes = []
		})
	}
}

// 保存角色设置
const handleSave = async () => {
	try {
		// 构建提交数据，支持多选角色
		const sysUserOrganizationRoleList: any[] | undefined = []

		orgList.value.forEach(item => {
			const roleCodes = item.roleCodes || []

			// 对于每个选中的角色，创建一条记录
			roleCodes.forEach((roleCode: any) => {
				sysUserOrganizationRoleList.push({
					orgCode: item.orgCode,
					roleCode: roleCode
				})
			})
		})
		let param = {
			userCode: userCode.value,
			sysUserOrganizationRoleList: sysUserOrganizationRoleList
		}
		// 使用正确的接口保存角色设置
		await service.post('/mgt/sysUserOrganizationRole/save', param)

		// 保存成功后关闭弹窗并刷新数据
		visible.value = false
		emit('refresh-data-list')

		ElMessage.success('角色设置保存成功')
	} catch (error) {
		console.error('保存角色设置失败:', error)
		ElMessage.error('角色设置保存失败')
	}
}

// 关闭弹窗
const handleClose = () => {
	visible.value = false
}
</script>

<style scoped></style>
