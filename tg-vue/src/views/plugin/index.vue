<template>
	<el-card class="layout-query">
		<el-space>
			<el-button type="primary" :loading="loading" icon="Refresh" @click="fetchList">刷新</el-button>
		</el-space>
		<p class="hint">
			数据为进程内存快照（自启动时扫描 <code>tg.plugins.directory</code> 目录），重启或更换 JAR 后需刷新或重启服务。
		</p>
	</el-card>

	<el-card>
		<el-table v-loading="loading" :data="dataList" border class="layout-table" empty-text="暂无插件记录">
			<el-table-column prop="jarFileName" label="JAR 文件" min-width="200" show-overflow-tooltip />
			<el-table-column prop="pluginCode" label="插件编码" width="140" show-overflow-tooltip />
			<el-table-column prop="pluginName" label="名称" width="160" show-overflow-tooltip />
			<el-table-column prop="pluginDescription" label="描述" min-width="180" show-overflow-tooltip />
			<el-table-column label="加载状态" width="130" align="center">
				<template #default="{ row }">
					<el-tag v-if="row.loadState" :type="tagType(row.loadState)" disable-transitions>
						{{ loadStateLabel(row.loadState) }}
					</el-tag>
					<span v-else>—</span>
				</template>
			</el-table-column>
			<el-table-column prop="message" label="说明" min-width="200" show-overflow-tooltip />
			<el-table-column prop="autoConfigurationClasses" label="AutoConfiguration" min-width="220" show-overflow-tooltip />
		</el-table>
	</el-card>
</template>

<script setup lang="ts" name="PluginIndex">
import { onMounted, ref } from 'vue'
import service from '@/utils/request'

interface PluginRow {
	jarFileName?: string
	pluginCode?: string
	pluginName?: string
	pluginDescription?: string
	loadState?: string
	message?: string
	autoConfigurationClasses?: string
}

const loading = ref(false)
const dataList = ref<PluginRow[]>([])

const loadStateLabel = (s: string) => {
	const map: Record<string, string> = {
		LOADED: '已加载',
		FAILED: '失败',
		NO_AUTO_CONFIGURATION: '无自动配置'
	}
	return map[s] || s
}

const tagType = (s: string): 'success' | 'warning' | 'info' | 'danger' => {
	if (s === 'LOADED') {
		return 'success'
	}
	if (s === 'FAILED') {
		return 'danger'
	}
	return 'warning'
}

const fetchList = () => {
	loading.value = true
	service
		.get('/mgt/plugin/install/list')
		.then((res: any) => {
			dataList.value = Array.isArray(res.data) ? res.data : []
		})
		.finally(() => {
			loading.value = false
		})
}

onMounted(() => {
	fetchList()
})
</script>

<style scoped lang="scss">
.hint {
	margin: 12px 0 0;
	font-size: 13px;
	line-height: 1.6;
	color: var(--el-text-color-secondary);

	code {
		padding: 0 6px;
		font-size: 12px;
		background: var(--el-fill-color-light);
		border-radius: 4px;
	}
}
</style>
