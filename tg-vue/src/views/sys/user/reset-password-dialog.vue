<template>
	<el-dialog v-model="visible" title="重置密码" width="420px" @close="handleClose">
		<p class="reset-tip">
			用户 <strong>{{ userRealName }}</strong> 的密码已重置为：
		</p>
		<el-input v-model="password" readonly>
			<template #append>
				<el-button @click="handleCopy">复制</el-button>
			</template>
		</el-input>
		<p class="reset-warn">请妥善保管新密码，关闭弹窗后将无法再次查看。</p>
		<template #footer>
			<el-button type="primary" @click="visible = false">关闭</el-button>
		</template>
	</el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const password = ref('')
const userRealName = ref('')

const init = (newPassword: string, name: string) => {
	password.value = newPassword
	userRealName.value = name
	visible.value = true
}

const handleCopy = () => {
	const text = password.value.trim()
	if (!text) {
		return
	}
	navigator.clipboard.writeText(text).then(() => ElMessage.success('已复制'))
}

const handleClose = () => {
	password.value = ''
	userRealName.value = ''
}

defineExpose({ init })
</script>

<style scoped lang="scss">
.reset-tip {
	margin: 0 0 12px;
}

.reset-warn {
	margin: 12px 0 0;
	font-size: 12px;
	color: var(--el-color-warning);
}
</style>
