<template>
	<el-table-column
		:prop="prop"
		:label="label"
		:header-align="headerAlign"
		:align="align"
		:width="width"
		:min-width="minWidth"
		:class-name="className"
	>
		<template #default="scope">
			<template v-if="scope.row[prop]">
				<el-button v-for="(file, index) in getFileList(scope.row[prop])" :key="index" type="primary" link @click="downloadFile(file)">
					<el-icon>
						<Link />
					</el-icon>
					{{ getFileName(file) }}
				</el-button>
			</template>
		</template>
	</el-table-column>
</template>

<script setup lang="ts" name="TgFileColumn">
import { Link } from '@element-plus/icons-vue'

const props = defineProps({
	prop: {
		type: String,
		required: true
	},
	label: {
		type: String,
		required: true
	},
	headerAlign: {
		type: String,
		required: false,
		default: () => 'center'
	},
	align: {
		type: String,
		required: false,
		default: () => 'center'
	},
	width: {
		type: String,
		required: false,
		default: () => ''
	},
	minWidth: {
		type: String,
		required: false,
		default: () => ''
	},
	className: {
		type: String,
		required: false,
		default: () => ''
	}
})

const getFileList = (fileStr: string) => {
	return fileStr.split(',')
}

const getFileName = (filePath: string) => {
	const parts = filePath.split('/')
	return parts[parts.length - 1]
}

const downloadFile = (filePath: string) => {
	let fullUrl = ''
	if (!filePath.startsWith('http')) {
		const filePrefix = import.meta.env.VITE_FILE_PREFIX || ''
		fullUrl = filePrefix + filePath
	} else {
		fullUrl = filePath
	}

	window.open(fullUrl, '_blank')
}
</script>
