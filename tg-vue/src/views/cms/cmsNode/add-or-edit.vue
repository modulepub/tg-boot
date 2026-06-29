<template>
	<el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false">
		<el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
			<el-form-item label="编码" prop="nodeCode">
				<el-input v-model="dataForm.nodeCode" placeholder="不填写可自动生成"></el-input>
			</el-form-item>
			<el-form-item label="父级编码" prop="nodeParentCode">
				<el-select v-model="dataForm.nodeParentCode" placeholder="请选择父级编码" style="width: 100%">
					<template v-for="item in treeData" :key="item.nodeCode">
						<el-option :label="item.nodeName" :value="item.nodeCode">
							<template #default>
								<span>{{ item.nodeName }}</span>
							</template>
						</el-option>
						<el-option v-for="child in item.children" :key="child.nodeCode" :label="'└─ ' + child.nodeName" :value="child.nodeCode">
							<template #default>
								<span style="padding-left: 20px">└─ {{ child.nodeName }}</span>
							</template>
						</el-option>
					</template>
				</el-select>
			</el-form-item>
			<el-form-item label="节点类型" prop="">
				<tg-dict-select disabled="" v-model="dataForm.nodeTypeCode" dict-code="nodeTypeCode" clearable placeholder="节点类型"></tg-dict-select>
			</el-form-item>
			<el-form-item label="内容名称" prop="nodeName">
				<el-input v-model="dataForm.nodeName" placeholder="内容名称"></el-input>
			</el-form-item>
			<el-form-item label="内容头图" prop="nodeHeadImg">
				<tg-upload-images :limit="1" v-model="dataForm.nodeHeadImg" biz="avatar" placeholder="内容头图"></tg-upload-images>
			</el-form-item>
			<el-form-item label="发布时间" prop="nodePublishTime">
				<el-date-picker v-model="dataForm.nodePublishTime" placeholder="发布时间"></el-date-picker>
			</el-form-item>
			<el-form-item label="发布状态" prop="nodePublishStatusCode">
				<tg-dict-select v-model="dataForm.nodePublishStatusCode" dict-code="nodePublishStatusCode" clearable placeholder="发布状态"></tg-dict-select>
			</el-form-item>

			<el-form-item label="内容类型" prop="nodeContentTypeCode">
				<tg-dict-select v-model="dataForm.nodeContentTypeCode" dict-code="nodeContentTypeCode" clearable placeholder="内容类型"></tg-dict-select>
			</el-form-item>
			<el-form-item label="链接" prop="nodeLink">
				<el-input v-model="dataForm.nodeLink" placeholder="链接"></el-input>
			</el-form-item>
			<el-form-item v-if="dataForm.nodeContentTypeCode === 'file'" label="文件" prop="nodeFile">
				<tg-upload-file v-model="dataForm.nodeFile" biz="avatar" :limit="1" placeholder="文件"></tg-upload-file>
			</el-form-item>
			<el-form-item label="内容摘要" prop="nodeSummary">
				<el-input type="textarea" v-model="dataForm.nodeSummary" placeholder="内容摘要"></el-input>
			</el-form-item>
			<el-form-item label="正文" prop="nodeContent">
				<tg-editor v-model="dataForm.nodeContent" placeholder="正文"></tg-editor>
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
const dataFormRef = ref()

const dataForm = reactive({
	id: '',
	createBy: '',
	createTime: '',
	updateBy: '',
	updateTime: '',
	orgCode: '',
	deleted: '',
	seqNo: '',
	version: '',
	nodeParentCode: '',
	nodeCode: '',
	nodeName: '',
	nodeHeadImg: '',
	nodeSummary: '',
	nodePublishTime: '',
	nodePublishStatusCode: '',
	nodeTypeCode: '',
	nodeContentTypeCode: '',
	nodeLink: '',
	nodeFile: '',
	nodeContent: ''
})

interface TreeNode {
	nodeCode: string
	nodeName: string
	children?: TreeNode[]
}

const treeData = ref<TreeNode[]>([])

const getTreeData = async () => {
	try {
		const response = await service.get('/mgt/cms/cmsNode/listCatalogTree', {
			params: {
				nodeCode: 'root'
			}
		})

		const processTreeData = (data: any) => {
			if (data) {
				if (data.children === null) {
					data.children = []
				} else if (Array.isArray(data.children)) {
					data.children.forEach(processTreeData)
				}
			}
			return data
		}
		const processedData = processTreeData(response.data)
		treeData.value = [processedData]
	} catch (error) {
		console.error('获取树数据失败:', error)
	}
}

const init = (id?: number, parentCode?: string, nodeTypeCode?: string) => {
	visible.value = true
	dataForm.id = ''
	dataForm.nodeCode = ''

	// 重置表单数据
	if (dataFormRef.value) {
		dataFormRef.value.resetFields()
	}

	// 获取树数据
	getTreeData()

	// 设置父节点编码
	if (parentCode) {
		dataForm.nodeParentCode = parentCode
	}
	if (nodeTypeCode) {
		dataForm.nodeTypeCode = nodeTypeCode
	} else {
		dataForm.nodeTypeCode = 'document'
	}

	if (id) {
		getCmsNode(id)
	}
}

const getCmsNode = (id: number) => {
	service.get('/mgt/cms/cmsNode/queryById?id=' + id).then(res => {
		if (res && res.data) {
			Object.assign(dataForm, res.data)
		}
	})
}

const dataRules = ref({
	nodeParentCode: [{ required: true, message: '请选择', trigger: 'change' }],
	nodePublishStatusCode: [{ required: true, message: '请选择', trigger: 'change' }],
	nodeContentTypeCode: [{ required: true, message: '请选择', trigger: 'change' }],
	nodeFile: [
		{
			validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
				if (dataForm.nodeContentTypeCode === 'file' && !value) {
					callback(new Error('请上传文件'))
				} else {
					callback()
				}
			},
			trigger: 'change'
		}
	]
})

// 表单提交
const submitHandle = () => {
	dataFormRef.value.validate((valid: boolean) => {
		if (!valid) {
			return false
		}
		let http: any
		if (dataForm.id) {
			http = service.post('/mgt/cms/cmsNode/edit', dataForm)
		} else {
			http = service.post('/mgt/cms/cmsNode/add', dataForm)
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
