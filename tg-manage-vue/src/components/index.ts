import type { App, Plugin } from 'vue'
import TgDictColumn from '@/components/tg-dict/tg-dict-column/index.vue'
import TgDictRadio from '@/components/tg-dict/tg-dict-radio/index.vue'
import TgDictTreeSelect from '@/components/tg-dict/tg-dict-tree-select/index.vue'
import TgDictSelect from '@/components/tg-dict/tg-dict-select/index.vue'
import TgDictCheckbox from '@/components/tg-dict/tg-dict-checkbox/index.vue'
import TgDataColumn from '@/components/tg-data/tg-data-column/index.vue'
import TgDataLabel from '@/components/tg-data/tg-data-label/index.vue'
import TgDataSelect from '@/components/tg-data/tg-data-select/index.vue'
import TgDataTreeSelect from '@/components/tg-data/tg-data-tree-select/index.vue'
import TgDataTreeLeft from '@/components/tg-data/tg-data-tree-left/index.vue'
import TgDataTable from '@/components/tg-data/tg-data-table/index.vue'
import TgImageColumn from '@/components/tg-image-column/index.vue'
import TgFileColumn from '@/components/tg-file-column/index.vue'

import TgIcon from '@/components/tg-icon/index.vue'
import TgEditor from '@/components/tg-editor/index.vue'
import TgMarkdown from '@/components/tg-markdown/index.vue'
import TgAddress from '@/components/tg-address/index.vue'
import TgUserInput from '@/components/tg-user/tg-user-input/index.vue'
import TgUserDialog from '@/components/tg-user/tg-user-dialog/index.vue'
import TgOrgSelect from '@/components/tg-org/tg-org-select/index.vue'
import TgOrgInput from '@/components/tg-org/tg-org-input/index.vue'
import TgRoleInput from '@/components/tg-role-input/index.vue'
import TgPostInput from '@/components/tg-post-input/index.vue'
import TgUploadImage from '@/components/tg-upload-image/index.vue'
import TgUploadImages from '@/components/tg-upload-images/index.vue'
import TgUploadFile from '@/components/tg-upload-file/index.vue'
import TgExcelUpload from '@/components/tg-excel-upload/index.vue'
import TgExcelDownload from '@/components/tg-excel-download/index.vue'

const components = [
	TgEditor,
	TgMarkdown,
	TgAddress,
	TgIcon,
	TgDictColumn,
	TgDictRadio,
	TgDictSelect,
	TgDictCheckbox,
	TgDictTreeSelect,
	TgDataColumn,
	TgDataLabel,
	TgDataSelect,
	TgDataTreeSelect,
	TgDataTreeLeft,
	TgDataTable,
	TgImageColumn,
	TgFileColumn,
	TgUserInput,
	TgUserDialog,
	TgOrgSelect,
	TgOrgInput,
	TgRoleInput,
	TgPostInput,
	TgUploadImage,
	TgUploadImages,
	TgUploadFile,
	TgExcelUpload,
	TgExcelDownload
]

const TgComponent: Plugin = {
	install(Vue: App) {
		components.forEach((component: any) => {
			Vue.component(component.name, component)
		})
	}
}

export default TgComponent
