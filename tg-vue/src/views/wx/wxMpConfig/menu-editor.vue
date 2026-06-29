<template>
	<el-dialog v-model="visible" :title="dialogTitle" :close-on-click-modal="false" width="960px" top="5vh">
		<div class="menu-toolbar">
			<el-space wrap>
				<el-button type="primary" icon="Plus" :disabled="topButtons.length >= 3" @click="addTopButton">添加一级菜单</el-button>
				<el-button icon="Download" @click="fetchRemoteMenu">从微信拉取</el-button>
				<el-button type="success" @click="saveMenu">保存到数据库</el-button>
				<el-button type="warning" @click="publishMenu">发布到微信</el-button>
			</el-space>
			<div v-if="publishedTime" class="published-tip">最近发布时间：{{ publishedTime }}</div>
		</div>

		<el-tabs v-if="topButtons.length" v-model="activeTab" type="border-card" class="menu-tabs">
			<el-tab-pane v-for="(btn, index) in topButtons" :key="index" :label="btn.name || `菜单${index + 1}`" :name="String(index)">
				<div class="menu-pane">
					<el-form label-width="100px">
						<el-form-item label="菜单名称">
							<el-input v-model="btn.name" maxlength="16" show-word-limit placeholder="最多 16 字符"></el-input>
						</el-form-item>
						<el-form-item label="菜单类型">
							<el-radio-group v-model="btn.mode" @change="onTopModeChange(btn)">
								<el-radio value="leaf">叶子菜单</el-radio>
								<el-radio value="parent">含子菜单</el-radio>
							</el-radio-group>
						</el-form-item>
					</el-form>

					<menu-button-fields v-if="btn.mode === 'leaf'" :button="btn" />

					<template v-else>
						<div class="sub-toolbar">
							<el-button type="primary" link icon="Plus" :disabled="(btn.sub_button || []).length >= 5" @click="addSubButton(btn)">
								添加子菜单
							</el-button>
						</div>
						<el-collapse v-if="btn.sub_button && btn.sub_button.length">
							<el-collapse-item v-for="(sub, subIndex) in btn.sub_button" :key="subIndex" :title="sub.name || `子菜单${subIndex + 1}`">
								<menu-button-fields :button="sub" />
								<div class="sub-actions">
									<el-button type="danger" link @click="removeSubButton(btn, subIndex)">删除子菜单</el-button>
								</div>
							</el-collapse-item>
						</el-collapse>
						<el-empty v-else description="请添加子菜单（最多 5 个）" />
					</template>

					<div class="top-actions">
						<el-button type="danger" plain @click="removeTopButton(index)">删除一级菜单</el-button>
					</div>
				</div>
			</el-tab-pane>
		</el-tabs>

		<el-empty v-else description="暂无菜单，请添加一级菜单（最多 3 个）" />

		<el-divider>JSON 预览</el-divider>
		<el-input v-model="jsonPreview" type="textarea" :rows="8" readonly></el-input>
	</el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus/es'
import service from '@/utils/request'
import MenuButtonFields from './menu-button-fields.vue'

interface MenuButton {
	name?: string
	type?: string
	key?: string
	url?: string
	appid?: string
	pagepath?: string
	mode?: 'leaf' | 'parent'
	sub_button?: MenuButton[]
}

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const wxMpConfigCode = ref('')
const configName = ref('')
const publishedTime = ref('')
const activeTab = ref('0')
const topButtons = ref<MenuButton[]>([])

const dialogTitle = computed(() => `公众号菜单配置${configName.value ? ' - ' + configName.value : ''}`)
const jsonPreview = computed(() => JSON.stringify(buildMenuPayload(), null, 2))

const createLeafButton = (): MenuButton =>
	reactive({
		name: '',
		mode: 'leaf',
		type: 'click',
		key: '',
		url: '',
		appid: '',
		pagepath: ''
	})

const createParentButton = (): MenuButton =>
	reactive({
		name: '',
		mode: 'parent',
		sub_button: []
	})

const onTopModeChange = (btn: MenuButton) => {
	if (btn.mode === 'parent') {
		btn.type = undefined
		btn.key = undefined
		btn.url = undefined
		btn.appid = undefined
		btn.pagepath = undefined
		btn.sub_button = btn.sub_button || []
	} else {
		btn.sub_button = undefined
		btn.type = btn.type || 'click'
	}
}

const addTopButton = () => {
	if (topButtons.value.length >= 3) {
		ElMessage.warning('一级菜单最多 3 个')
		return
	}
	topButtons.value.push(createLeafButton())
	activeTab.value = String(topButtons.value.length - 1)
}

const removeTopButton = (index: number) => {
	topButtons.value.splice(index, 1)
	if (topButtons.value.length === 0) {
		activeTab.value = '0'
	} else if (Number(activeTab.value) >= topButtons.value.length) {
		activeTab.value = String(topButtons.value.length - 1)
	}
}

const addSubButton = (btn: MenuButton) => {
	if (!btn.sub_button) {
		btn.sub_button = []
	}
	if (btn.sub_button.length >= 5) {
		ElMessage.warning('子菜单最多 5 个')
		return
	}
	btn.sub_button.push(createLeafButton())
}

const removeSubButton = (btn: MenuButton, subIndex: number) => {
	btn.sub_button?.splice(subIndex, 1)
}

const serializeButton = (btn: MenuButton): Record<string, unknown> => {
	const item: Record<string, unknown> = { name: btn.name || '' }
	if (btn.sub_button && btn.sub_button.length > 0) {
		item.sub_button = btn.sub_button.map(serializeButton)
		return item
	}
	item.type = btn.type || 'click'
	if (item.type === 'click') {
		item.key = btn.key || ''
	}
	if (item.type === 'view') {
		item.url = btn.url || ''
	}
	if (item.type === 'miniprogram') {
		item.url = btn.url || 'http://mp.weixin.qq.com'
		item.appid = (btn.appid || '').trim()
		item.pagepath = normalizePagePath(btn.pagepath)
	}
	return item
}

const normalizePagePath = (pagepath?: string) => {
	let t = (pagepath || '').trim()
	while (t.startsWith('/')) {
		t = t.slice(1)
	}
	return t
}

const validateButtons = (buttons: MenuButton[], path = '菜单'): string | null => {
	for (let i = 0; i < buttons.length; i++) {
		const btn = buttons[i]
		const label = btn.name?.trim() || `第${i + 1}项`
		const current = `${path}「${label}」`
		if (btn.mode === 'parent' || (btn.sub_button && btn.sub_button.length > 0)) {
			const err = validateButtons(btn.sub_button || [], current)
			if (err) return err
			continue
		}
		if (!btn.name?.trim()) {
			return `${current}：菜单名称不能为空`
		}
		const type = btn.type || 'click'
		if (type === 'click' && !btn.key?.trim()) {
			return `${current}：菜单 Key 不能为空`
		}
		if (type === 'view' && !btn.url?.trim()) {
			return `${current}：跳转 URL 不能为空`
		}
		if (type === 'miniprogram') {
			if (!btn.appid?.trim()) {
				return `${current}：小程序 AppId 不能为空`
			}
			if (!normalizePagePath(btn.pagepath)) {
				return `${current}：小程序页面路径不能为空（如 pages/index/index）`
			}
		}
	}
	return null
}

const buildMenuPayload = () => ({
	button: topButtons.value.map(serializeButton)
})

const parseButton = (raw: Record<string, unknown>): MenuButton => {
	const sub = raw.sub_button as Record<string, unknown>[] | undefined
	if (sub && sub.length > 0) {
		return reactive({
			name: String(raw.name || ''),
			mode: 'parent',
			sub_button: sub.map(parseButton)
		})
	}
	return reactive({
		name: String(raw.name || ''),
		mode: 'leaf',
		type: String(raw.type || 'click'),
		key: String(raw.key || ''),
		url: String(raw.url || ''),
		appid: String(raw.appid || ''),
		pagepath: String(raw.pagepath || '')
	})
}

const applyMenuJson = (menuJson: string) => {
	const parsed = JSON.parse(menuJson || '{"button":[]}')
	const buttons = Array.isArray(parsed.button) ? parsed.button : []
	topButtons.value = buttons.map((item: Record<string, unknown>) => parseButton(item))
	if (topButtons.value.length > 0) {
		activeTab.value = '0'
	}
}

const loadMenu = async () => {
	const { data } = await service.get('/mgt/wx/wxMpConfig/menu', {
		params: { wxMpConfigCode: wxMpConfigCode.value }
	})
	publishedTime.value = data?.publishedTime || ''
	if (!data?.menuJson) {
		topButtons.value = []
		return
	}
	try {
		applyMenuJson(data.menuJson)
	} catch {
		topButtons.value = []
		ElMessage.error('菜单 JSON 解析失败')
	}
}

const init = async (code: string, name?: string) => {
	wxMpConfigCode.value = code
	configName.value = name || ''
	visible.value = true
	await loadMenu()
}

const saveMenu = async () => {
	const err = validateButtons(topButtons.value)
	if (err) {
		ElMessage.warning(err)
		return
	}
	await service.post('/mgt/wx/wxMpConfig/saveMenu', {
		wxMpConfigCode: wxMpConfigCode.value,
		menuJson: JSON.stringify(buildMenuPayload())
	})
	ElMessage.success('菜单已保存到数据库')
	emit('refreshDataList')
}

const publishMenu = async () => {
	const err = validateButtons(topButtons.value)
	if (err) {
		ElMessage.warning(err)
		return
	}
	await ElMessageBox.confirm('确认将当前菜单发布到微信服务器？', '发布菜单', { type: 'warning' })
	await service.post('/mgt/wx/wxMpConfig/saveMenu', {
		wxMpConfigCode: wxMpConfigCode.value,
		menuJson: JSON.stringify(buildMenuPayload())
	})
	await service.post('/mgt/wx/wxMpConfig/publishMenu', { wxMpConfigCode: wxMpConfigCode.value })
	ElMessage.success('菜单已发布到微信')
	await loadMenu()
	emit('refreshDataList')
}

const fetchRemoteMenu = async () => {
	const { data } = await service.get('/mgt/wx/wxMpConfig/fetchRemoteMenu', {
		params: { wxMpConfigCode: wxMpConfigCode.value }
	})
	try {
		applyMenuJson(data || '{"button":[]}')
		ElMessage.success('已从微信拉取当前菜单')
	} catch {
		ElMessage.error('远程菜单解析失败')
	}
}

watch(visible, val => {
	if (!val) {
		topButtons.value = []
		publishedTime.value = ''
	}
})

defineExpose({ init })
</script>

<style scoped>
.menu-toolbar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
	flex-wrap: wrap;
	gap: 8px;
}
.published-tip {
	color: #909399;
	font-size: 13px;
}
.menu-tabs {
	min-height: 320px;
}
.menu-pane {
	padding: 8px 4px;
}
.sub-toolbar {
	margin-bottom: 8px;
}
.sub-actions,
.top-actions {
	margin-top: 12px;
}
</style>
