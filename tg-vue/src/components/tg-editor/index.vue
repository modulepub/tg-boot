<template>
	<div style="border: 1px solid #ccc; z-index: 100">
		<!-- 工具栏：须等编辑器实例创建后再挂载，避免 emojisOptions 空指针 -->
		<Toolbar v-if="editorRef" :editor="editorRef" :mode="mode" style="border-bottom: 1px solid #ccc" />
		<!-- 编辑器 -->
		<Editor
			:model-value="initialHtml"
			:style="style"
			:disabled="disabled"
			:default-config="editorConfig"
			:mode="mode"
			@on-created="handleCreated"
			@on-change="handleChange"
		/>
	</div>
</template>

<script lang="ts" setup name="TgEditor">
import '@wangeditor/editor/dist/css/style.css'
import { computed, onBeforeUnmount, shallowRef, watch } from 'vue'
import constant from '@/utils/constant'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { IDomEditor, IEditorConfig } from '@wangeditor/editor'
import cache from '@/utils/cache'

const props = defineProps({
	modelValue: {
		type: String,
		default: ''
	},
	mode: {
		type: String,
		default: 'default' // 可选值：[default | simple]
	},
	placeholder: {
		type: String,
		default: ''
	},
	style: {
		type: String,
		default: 'height: 400px;'
	},
	disabled: {
		type: Boolean,
		default: false
	}
})

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

type InsertFnType = (url: string, alt: string, href: string) => void

// 编辑器配置
const editorConfig: Partial<IEditorConfig> = {
	placeholder: props.placeholder,
	readOnly: props.disabled,
	MENU_CONF: {
		uploadImage: {
			server: constant.uploadUrl + '?access_token=' + cache.getToken(),
			fieldName: 'file',
			// 自定义插入图片
			customInsert(res: any, insertFn: InsertFnType) {
				// res 即服务端的返回结果
				// 从 res 中找到 url alt href ，然后插图图片
				insertFn(res.data.url, res.data.name, '')
			}
		}
	}
}

/** 历史纯文本描述转为编辑器可识别的 HTML */
function normalizeEditorHtml(raw?: string): string {
	const text = String(raw ?? '').trim()
	if (!text) {
		return ''
	}
	if (/<\s*\w+[\s>]/.test(text)) {
		return text
	}
	return text
		.split(/\r?\n/)
		.map(line => line.trim())
		.filter(Boolean)
		.map(line => `<p>${line}</p>`)
		.join('')
}

const initialHtml = computed(() => normalizeEditorHtml(props.modelValue))

function syncEditorHtml(editor: IDomEditor, html: string) {
	const next = normalizeEditorHtml(html)
	if (editor.getHtml() === next) {
		return
	}
	editor.setHtml(next)
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
	const editor = editorRef.value
	if (editor == null) {
		return
	}
	editor.destroy()
})

const handleCreated = (editor: IDomEditor) => {
	editorRef.value = editor
	syncEditorHtml(editor, props.modelValue)
}

// 异步回填（如编辑弹窗 queryById）时同步内容
watch(
	() => props.modelValue,
	val => {
		const editor = editorRef.value
		if (!editor) {
			return
		}
		syncEditorHtml(editor, val)
	}
)

// 编辑器 change 事件触发
const emit = defineEmits(['update:modelValue'])
const handleChange = (editor: IDomEditor) => {
	emit('update:modelValue', editor.getHtml())
}
</script>
