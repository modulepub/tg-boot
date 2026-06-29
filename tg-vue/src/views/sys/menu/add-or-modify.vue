<template>
	<el-dialog v-model="visible" :title="!update ? '新增' : '修改'" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px">
			<el-form-item prop="type" label="类型">
				<el-radio-group v-model="dataForm.perTypeCode" :disabled="update" @change="menuTypeChange(dataForm.perParentCode)">
					<el-radio :value="'0'">菜单</el-radio>
					<el-radio :value="'1'">按钮</el-radio>
					<el-radio :value="'2'">接口</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item prop="perName" label="名称">
				<el-input v-model="dataForm.perName" placeholder="名称"></el-input>
			</el-form-item>
			<el-form-item prop="perCode" label="编码">
				<el-input v-model="dataForm.perCode" placeholder="编码"></el-input>
			</el-form-item>
			<el-form-item prop="pid" label="上级菜单">
				<el-tree-select
					v-model="dataForm.perParentCode"
					:data="menuList"
					value-key="perCode"
					check-strictly
					:render-after-expand="false"
					style="width: 100%"
					clearable
					:props="{
						label: 'perName',
						children: 'children',
						hasChildren: 'hasChildren'
					}"
				/>
			</el-form-item>
			<el-form-item prop="perUrl" label="路由">
				<el-input v-model="dataForm.perUrl" placeholder="路由"></el-input>
			</el-form-item>
			<el-form-item prop="seqNo" label="排序">
				<el-input-number v-model="dataForm.seqNo" controls-position="right" :min="0" aria-label="排序"></el-input-number>
			</el-form-item>
			<el-form-item v-if="dataForm.perTypeCode === '0'" prop="openStyle" label="打开方式">
				<el-radio-group v-model="dataForm.perOpenStyleCode">
					<el-radio :value="'0'">内部打开</el-radio>
					<el-radio :value="'1'">外部打开</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item v-if="dataForm.perTypeCode === '0'" prop="icon" label="图标" class="popover-list">
				<SelectIcon v-model="dataForm.perIcon" />
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
import SelectIcon from '@/components/tg-icon-select/index.vue'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const menuList = ref([])
const dataFormRef = ref()

const dataForm = reactive({
	id: '',
	perTypeCode: '0',
	perName: '',
	perCode: '',
	perParentCode: '',
	parentName: '',
	perUrl: '',
	perIcon: '',
	perOpenStyleCode: '0',
	seqNo: '0'
})

const update = ref(false)

const init = async (isUpdate: boolean, row: any) => {
	visible.value = true
	update.value = isUpdate

	// 手动重置所有表单字段
	Object.assign(dataForm, {
		id: '',
		perTypeCode: '0',
		perName: '',
		perCode: '',
		perParentCode: 'root',
		parentName: '',
		perUrl: '',
		perIcon: '',
		perOpenStyleCode: '0',
		seqNo: '0'
	})

	// 存在则为修改
	if (row) {
		await getMenu(isUpdate, row)
	}

	// 菜单列表
	getMenuList()
}

// 菜单类型改变
const menuTypeChange = (perParentCode: any) => {
	getMenuList()
	dataForm.perParentCode = perParentCode
}

// 获取菜单列表
const getMenuList = async () => {
	// 使用新接口获取所有菜单结构
	const res = await service.get('/mgt/sysPermission/getTree', {
		params: {
			code: 'root'
		}
	})
	// 检查响应数据格式，确保正确解析
	const menuData = res.data || {}
	const children = menuData.children || []
	// 为每个菜单项添加hasChildren属性，确保可以展开
	menuList.value = children.map((item: any) => ({
		...item,
		hasChildren: !!item.children && item.children.length > 0
	}))
}

// 获取信息
const getMenu = async (isUpdate: boolean, row: any) => {
	if (isUpdate) {
		// 是修改，调用接口查询详情数据
		try {
			const res = await service.get('/mgt/sysPermission/queryById', {
				params: {
					id: row.id // 假设接口使用 perCode 作为 ID
				}
			})
			if (res.data) {
				Object.assign(dataForm, res.data)
			}
		} catch (error) {
			console.error('获取菜单详情失败:', error)
			ElMessage.error('获取菜单详情失败')
		}
	} else {
		// 是新增，重置表单数据
		dataForm.perParentCode = row.perCode
		dataForm.id = ''
		dataForm.perTypeCode = '0'
		dataForm.perName = ''
		dataForm.perCode = ''
		dataForm.parentName = ''
		dataForm.perUrl = ''
		dataForm.seqNo = '0'
		dataForm.perIcon = ''
		dataForm.perOpenStyleCode = '0'
	}
}

const dataRules = ref({
	name: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	parentName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http
		if (update.value) {
			http = service.post('/mgt/sysPermission/edit', dataForm)
		} else {
			http = service.post('/mgt/sysPermission/add', dataForm)
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

<style lang="scss" scoped>
.mod__menu {
	::v-deep(.el-popover.el-popper) {
		overflow-x: hidden;
	}

	.popover-list {
		::v-deep(.el-input__inner) {
			cursor: pointer;
		}
		::v-deep(.el-input__suffix) {
			cursor: pointer;
		}
	}
}
</style>
