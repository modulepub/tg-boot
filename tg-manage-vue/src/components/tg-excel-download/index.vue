<template>
	<el-button icon="Download" @click="handleDownload">
		<slot />
	</el-button>
</template>

<script setup lang="ts" name="TgExcelDownload">
import constant from '@/utils/constant'
import service from '@/utils/request'

const props = defineProps({
	data: {
		type: String,
		default: ''
	},
	template: {
		type: String,
		default: ''
	}
})

const handleDownload = () => {
	const downloadUrl = `${constant.apiUrl}/cus/excel/export?dataUrl=${constant.apiUrl}${props.data}&templatePath=${props.template}`
	service.get(downloadUrl, { responseType: 'blob' }).then(res => {
		let blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
		let date = new Date()
		let fileName = `${date.getFullYear()}${date.getMonth() + 1}${date.getDate()}${date.getHours()}${date.getMinutes()}${date.getSeconds()}.xlsx`
		let a = document.createElement('a')
		a.href = URL.createObjectURL(blob)
		a.download = fileName
		a.click()
		URL.revokeObjectURL(a.href)
	})
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
