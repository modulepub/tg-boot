<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px" @keyup.enter="submitHandle()">
			<el-form-item prop="roleName" label="名称">
				<el-input v-model="dataForm.roleName" placeholder="名称"></el-input>
			</el-form-item>
			<el-form-item prop="roleCode" label="编码">
				<el-input v-model="dataForm.roleCode" placeholder="编码"></el-input>
			</el-form-item>
			<el-form-item prop="remark" label="备注">
				<el-input v-model="dataForm.roleDescription" placeholder="备注"></el-input>
			</el-form-item>
			<el-form-item label="菜单权限">
				<el-tree
					ref="menuListTree"
					:data="menuList"
					:props="{ label: 'perName', children: 'children' }"
					node-key="perCode"
					accordion
					show-checkbox
				></el-tree>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const menuList = ref([])
const menuListTree = ref()
const dataFormRef = ref()

const dataForm = reactive({
	id: '',
	roleName: '',
	roleCode: '',
	sysRolePermissionList: [] as any[],
	orgIdList: [],
	roleDescription: ''
})

const init = async (roleCode?: number) => {
	visible.value = true
	dataForm.id = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}
	if (menuListTree.value) {
		menuListTree.value.setCheckedKeys([])
	}

	// 获取菜单列表
	await getMenuList()

	// id 存在则为修改
	if (roleCode) {
		getRole(roleCode)
	}
}

// 获取菜单列表
const getMenuList = async () => {
	const res = await service.get('/mgt/sysPermission/getTree?code=root')
	menuList.value = res.data.children
}

// 由于改为一次性加载所有数据，不再需要loadNode函数

// 获取信息
const getRole = (roleCode: number) => {
	service.get('/mgt/sysRole/queryByCode?code=' + roleCode).then(res => {
		Object.assign(dataForm, res.data)

		dataForm.sysRolePermissionList.forEach(item => menuListTree.value.setChecked(item.perCode, true))
	})
}

const dataRules = ref({
	roleName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	roleCode: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		dataForm.sysRolePermissionList = [...menuListTree.value.getHalfCheckedKeys(), ...menuListTree.value.getCheckedKeys()]
		let http
		if (dataForm.id) {
			http = service.put('/mgt/sysRole/edit', dataForm)
		} else {
			http = service.post('/mgt/sysRole/add', dataForm)
		}
		http.then(() => {
			ElMessage.success({
				message: '操作成功',
				duration: 500,
				onClose: () => {
					visible.value = false
					emit('refreshDataList')
				}
			})
		})
	})
}

defineExpose({
	init
})
</script>
