<template>
	<el-button icon="Upload" @click="openDialog">
		<slot />
	</el-button>

	<el-dialog v-model="dialogVisible" title="导入Excel" width="600px" :close-on-click-modal="false">
		<div class="upload-dialog-content">
			<div class="template-download">
				<el-button type="primary" icon="Download" @click="downloadTemplate">下载模板</el-button>
			</div>
			<div class="upload-area">
				<el-upload
					:action="uploadUrl"
					:headers="{ Authorization: 'Bearer ' + cache.getToken() }"
					:before-upload="beforeUpload"
					:on-success="handleSuccess"
					:show-file-list="false"
					drag
				>
					<el-icon class="el-icon--upload"><upload-filled /></el-icon>
					<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
				</el-upload>
			</div>
			<div class="history-section">
				<div class="history-title">历史导入记录</div>
				<el-table :data="historyList" border size="small" max-height="300">
					<el-table-column prop="excelName" label="文件名" show-overflow-tooltip></el-table-column>

					<el-table-column prop="completed" label="是否完成" width="80" align="center">
						<template #default="scope">
							<el-tag :type="scope.row.completed ? 'success' : 'danger'" size="small">{{ scope.row.completed ? '已完成' : '未完成' }}</el-tag>
						</template>
					</el-table-column>
					<el-table-column prop="hasError" label="异常情况" width="80" align="center">
						<template #default="scope">
							<el-tag :type="!scope.row.hasError ? 'success' : 'danger'" size="small">{{ !scope.row.hasError ? '未发现' : '异常' }}</el-tag>
						</template>
					</el-table-column>
					<el-table-column prop="beginImportTime" label="导入时间" show-overflow-tooltip align="center"></el-table-column>
					<el-table-column prop="completeImportTime" label="完成时间" show-overflow-tooltip align="center"></el-table-column>
					<el-table-column label="操作" fixed="right" header-align="center" align="center" width="100">
						<template #default="scope">
							<el-button type="primary" link @click="resultHandle(scope.row)">下载结果</el-button>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
	</el-dialog>
</template>

<script setup lang="ts" name="TgExcelImport">
import { ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'
import constant from '@/utils/constant'
import { ElMessage } from 'element-plus/es'
import cache from '@/utils/cache'
import service from '@/utils/request'
import { Storage } from '@/utils/storage'
import { push } from 'echarts/types/src/component/dataZoom/history'

const props = defineProps({
	push: {
		type: String,
		default: ''
	},
	template: {
		type: String,
		default: ''
	}
})

const dialogVisible = ref(false)

let historyList = ref([])

const openDialog = () => {
	dialogVisible.value = true
	getStatus()
}

const downloadTemplate = () => {
	if (props.template) {
		window.open(props.template, '_blank')
	} else {
		ElMessage.warning('模板下载链接未配置')
	}
}
let pushUrl
if (props.push.startsWith('http')) {
	pushUrl = `${props.push}`
} else {
	pushUrl = `${constant.apiUrl}${props.push}`
}

const uploadUrl = constant.apiUrl + `/cus/excel/import?pushUrl=${pushUrl}`

const handleSuccess: UploadProps['onSuccess'] = (res, file) => {
	let importExcelStatus = Storage.getItem('importExcelStatus')
	if (importExcelStatus) {
		importExcelStatus = importExcelStatus + ',' + res.data
		Storage.setItem('importExcelStatus', importExcelStatus)
	} else {
		importExcelStatus = res.data
		Storage.setItem('importExcelStatus', importExcelStatus)
	}
	console.log('importExcelStatus', importExcelStatus)
	ElMessage.success({
		message: '上传成功',
		duration: 500,
		onClose: () => {
			dialogVisible.value = false
		}
	})
}

const beforeUpload: UploadProps['beforeUpload'] = file => {
	if (file.size / 1024 / 1024 / 1024 / 1024 > 1) {
		ElMessage.error('文件大小不能超过100M')
		return false
	}
	return true
}

const getStatus = () => {
	let importExcelStatus = Storage.getItem('importExcelStatus')
	service.get(`/cus/excel/getImportStatus?keys=${importExcelStatus}`, {}).then(res => {
		historyList.value = res.data
		console.log('上传状态{}', historyList.value)
	})
}
const resultHandle = (params: { completed: any; batchId: any }) => {
	if (params.completed) {
		let url = `${constant.apiUrl}/cus/excel/downloadImportResult?batchId=${params.batchId}`
		service.get(url, { responseType: 'blob' }).then(res => {
			let blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
			let fileName = params.batchId + '.xlsx'
			let a = document.createElement('a')
			a.href = URL.createObjectURL(blob)
			a.download = fileName
			a.click()
			URL.revokeObjectURL(a.href)
		})
	} else {
		ElMessage.warning('模板下载链接未配置')
	}
}
</script>

<style scoped>
.upload-dialog-content {
	padding: 10px;
}

.template-download {
	margin-bottom: 20px;
	text-align: right;
}

.upload-area {
	margin-bottom: 20px;
}

.history-section {
	border-top: 1px solid #ebeef5;
	padding-top: 15px;
}

.history-title {
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 10px;
	color: #303133;
}

:deep(.el-upload-dragger) {
	padding: 20px;
}
</style>
