declare module '*.svg'
declare module '*.png'
declare module '*.jpg'
declare module '*.gif'
declare module '*.scss'
declare module '*.ts'
declare module '*.js'

declare module '*.vue' {
	import type { DefineComponent } from 'vue'
	const component: DefineComponent<{}, {}, any>
	export default component
}

declare module '@kangc/v-md-editor'
declare module '@kangc/v-md-editor/lib/theme/github.js'

declare module 'TG-form-design'

interface ImportMetaEnv {
	readonly VITE_APP_PUBLIC_BASE: string
	readonly VITE_API_URL: string
	readonly VITE_FILE_PREFIX: string
}

interface ImportMeta {
	readonly env: ImportMetaEnv
}
