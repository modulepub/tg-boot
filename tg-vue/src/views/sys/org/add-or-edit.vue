<template>
	<el-dialog v-model="visible" :title="dialogTitle" :close-on-click-modal="false" draggable>
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="120px">
			<template v-if="action !== 'adjust'">
				<el-form-item v-if="action === 'add'" prop="orgCategoryCode" label="类型">
					<el-radio-group v-model="dataForm.orgCategoryCode">
						<el-radio value="com">公司</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item v-else prop="orgCategoryCode" label="类型">
					<el-tag v-if="dataForm.orgCategoryCode === 'com'" type="primary">公司</el-tag>
					<el-tag v-else type="info">部门</el-tag>
				</el-form-item>
				<el-form-item prop="orgName" label="名称">
					<el-input v-model="dataForm.orgName" placeholder="名称"></el-input>
				</el-form-item>
				<el-form-item prop="seqNo" label="排序">
					<el-input-number v-model="dataForm.seqNo" controls-position="right" :min="0" aria-label="排序"></el-input-number>
				</el-form-item>
			</template>
			<el-form-item v-if="action === 'adjust'" label="当前机构">
				<el-input :model-value="dataForm.orgName" disabled></el-input>
			</el-form-item>
			<el-form-item
				v-if="showParentSelect"
				prop="orgParentCode"
				:label="action === 'adjust' ? '调至上级机构' : '上级机构'"
			>
				<el-tree-select
					v-model="dataForm.orgParentCode"
					:data="orgList"
					value-key="orgCode"
					check-strictly
					:render-after-expand="false"
					style="width: 100%"
					clearable
					:props="{
						label: 'orgName',
						children: 'children',
						disabled: 'disabled'
					}"
					placeholder="请选择上级机构"
				/>
			</el-form-item>
		</el-form>
		<template #footer>
			<el-button @click="visible = false">取消</el-button>
			<el-button type="primary" @click="submitHandle()">确定</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus/es'
import service from '@/utils/request'

const emit = defineEmits(['refreshDataList'])

type OrgAction = 'add' | 'addChild' | 'edit' | 'adjust'

const visible = ref(false)
const dataFormRef = ref()
const orgList = ref<any[]>([])
const action = ref<OrgAction>('add')

const dataForm = reactive({
	id: '',
	orgName: '',
	orgCode: '',
	orgParentCode: '' as string | null,
	orgCategoryCode: 'com',
	seqNo: 0
})

const dialogTitle = computed(() => {
	const titles: Record<OrgAction, string> = {
		add: '新增机构',
		addChild: '新增下级机构',
		edit: '修改机构',
		adjust: '调机构'
	}
	return titles[action.value]
})

const showParentSelect = computed(() => {
	if (action.value === 'add') {
		return false
	}
	if (action.value === 'addChild') {
		return true
	}
	if (action.value === 'adjust') {
		return dataForm.orgCategoryCode !== 'com'
	}
	return dataForm.orgCategoryCode !== 'com' || !!dataForm.orgParentCode
})

const collectDescendantCodes = (node: any): string[] => {
	const codes = [node.orgCode]
	for (const child of node.children || []) {
		codes.push(...collectDescendantCodes(child))
	}
	return codes
}

const findNodeByCode = (list: any[], orgCode: string): any | null => {
	for (const item of list || []) {
		if (item.orgCode === orgCode) {
			return item
		}
		const found = findNodeByCode(item.children, orgCode)
		if (found) {
			return found
		}
	}
	return null
}

const disableSelfAndDescendants = (list: any[], currentOrgCode: string): any[] => {
	const rootList = mapTreeData(list)
	const currentNode = findNodeByCode(rootList, currentOrgCode)
	const disabledCodes = new Set(currentNode ? collectDescendantCodes(currentNode) : [currentOrgCode])
	const markDisabled = (items: any[]): any[] =>
		(items || []).map((item: any) => ({
			...item,
			disabled: disabledCodes.has(item.orgCode),
			children: markDisabled(item.children)
		}))
	return markDisabled(rootList)
}

const mapTreeData = (list: any[]): any[] => {
	return (list || []).map((item: any) => ({
		...item,
		children: item.children ? mapTreeData(item.children) : []
	}))
}

const getOrgList = async () => {
	const res = await service.get('/mgt/sysOrganization/listTree')
	let list = mapTreeData(res.data || [])
	if (action.value === 'adjust' || action.value === 'edit') {
		list = disableSelfAndDescendants(list, dataForm.orgCode)
	}
	orgList.value = list
}

const init = async (act: OrgAction, row?: any) => {
	action.value = act
	visible.value = true

	Object.assign(dataForm, {
		id: '',
		orgName: '',
		orgCode: '',
		orgParentCode: null,
		orgCategoryCode: 'com',
		seqNo: 0
	})

	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	if (act === 'addChild' && row) {
		dataForm.orgParentCode = row.orgCode
		dataForm.orgCategoryCode = 'dept'
	} else if (row) {
		const res = await service.get('/mgt/sysOrganization/queryById?id=' + row.id)
		Object.assign(dataForm, res.data)
		if (!dataForm.orgParentCode) {
			dataForm.orgParentCode = null
		}
	}

	await getOrgList()
}

const dataRules = ref({
	orgName: [{ required: true, message: '必填项不能为空', trigger: 'blur' }],
	seqNo: [{ required: true, message: '必填项不能为空', trigger: 'blur' }]
})

const submitHandle = () => {
	if ((action.value === 'addChild' || action.value === 'adjust') && !dataForm.orgParentCode) {
		ElMessage.warning('请选择上级机构')
		return
	}
	if (action.value === 'adjust' && dataForm.orgCategoryCode === 'com') {
		ElMessage.warning('顶级公司不支持调机构')
		return
	}
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		const payload = { ...dataForm }
		if (!payload.orgParentCode) {
			payload.orgParentCode = null
		}
		let http
		if (action.value === 'adjust') {
			http = service.post('/mgt/sysOrganization/adjust', {
				id: payload.id,
				orgParentCode: payload.orgParentCode
			})
		} else if (payload.id) {
			http = service.post('/mgt/sysOrganization/edit', payload)
		} else {
			http = service.post('/mgt/sysOrganization/add', payload)
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
