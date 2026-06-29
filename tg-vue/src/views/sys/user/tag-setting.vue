<template>
	<el-dialog v-model="visible" :title="title" width="560px" @close="handleClose">
		<el-card v-loading="loading">
			<div class="tag-add">
				<el-input
					v-model="newTag"
					placeholder="输入标签名称后回车或点击添加"
					clearable
					maxlength="64"
					style="width: 320px"
					@keyup.enter="addTag"
				></el-input>
				<el-button type="primary" icon="Plus" @click="addTag">添加</el-button>
			</div>
			<div class="tag-list">
				<template v-if="tagList.length">
					<el-tag
						v-for="tag in tagList"
						:key="tag.id"
						class="tag-item"
						type="primary"
						closable
						@close="removeTag(tag)"
					>
						{{ tag.tagName }}
					</el-tag>
				</template>
				<el-empty v-else description="暂无标签" :image-size="60" />
			</div>
		</el-card>
		<template #footer>
			<el-button type="primary" @click="handleClose">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import service from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const emit = defineEmits(['refresh-data-list'])

interface UserTag {
	id: string
	tagCode: string
	tagName: string
}

const visible = ref(false)
const loading = ref(false)
const userCode = ref('')
const userRealName = ref('')
const title = ref('标签管理')
const tagList = ref<UserTag[]>([])
const newTag = ref('')
const changed = ref(false)

const init = (userCodeParam: string, name: string) => {
	userCode.value = userCodeParam
	userRealName.value = name || ''
	title.value = name ? `${name} - 标签管理` : '标签管理'
	newTag.value = ''
	changed.value = false
	visible.value = true
	loadTags()
}

defineExpose({ init })

const loadTags = async () => {
	loading.value = true
	try {
		const res = await service.get('/mgt/sysUserTag/listByUserCode', {
			params: { userCode: userCode.value }
		})
		tagList.value = res.data || []
	} catch (error) {
		console.error('加载用户标签失败:', error)
		tagList.value = []
	} finally {
		loading.value = false
	}
}

const addTag = async () => {
	const tagName = newTag.value.trim()
	if (!tagName) {
		ElMessage.warning('请输入标签名称')
		return
	}
	if (tagList.value.some(item => item.tagName === tagName)) {
		ElMessage.warning('该标签已存在')
		return
	}
	try {
		await service.post('/mgt/sysUserTag/add', { userCode: userCode.value, tagName })
		newTag.value = ''
		changed.value = true
		ElMessage.success('添加成功')
		await loadTags()
	} catch (error) {
		console.error('添加用户标签失败:', error)
	}
}

const removeTag = async (tag: UserTag) => {
	try {
		await ElMessageBox.confirm(`确认删除标签「${tag.tagName}」？`, '删除标签', { type: 'warning' })
	} catch {
		return
	}
	try {
		await service.post('/mgt/sysUserTag/delete', [tag.id])
		changed.value = true
		ElMessage.success('删除成功')
		await loadTags()
	} catch (error) {
		console.error('删除用户标签失败:', error)
	}
}

const handleClose = () => {
	visible.value = false
	if (changed.value) {
		emit('refresh-data-list')
	}
}
</script>

<style scoped>
.tag-add {
	display: flex;
	gap: 8px;
	margin-bottom: 16px;
}
.tag-list {
	min-height: 60px;
}
.tag-item {
	margin: 0 8px 8px 0;
}
</style>
