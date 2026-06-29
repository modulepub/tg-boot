import constant from '@/utils/constant'
import { useAppStore } from '@/store/modules/app'

// 把路径转换成驼峰命名
export const pathToCamel = (path: string): string => {
	return path.replace(/\/(\w)/g, (_all, letter) => letter.toUpperCase())
}

// 是否外链
export const isExternalLink = (url: string): boolean => {
	return /^(https?:|http?:|\/\/|^{{\s?apiUrl\s?}})/.test(url)
}

// 替换外链参数
export const replaceLinkParam = (url: string): string => {
	return url.replace('{{apiUrl}}', constant.apiUrl)
}

// 转换文件大小格式
export const convertSizeFormat = (size: number): string => {
	const unit = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB']
	let index = Math.floor(Math.log(size) / Math.log(1024))
	let newSize = size / Math.pow(1024, index)

	// 保留的小数位数
	return newSize.toFixed(2) + ' ' + unit[index]
}

// 生成uuid
export const generateUUID = () => {
	let uuid = ''
	for (let i = 0; i < 32; i++) {
		let random = (Math.random() * 16) | 0
		if (i === 8 || i === 12 || i === 16 || i === 20) {
			uuid += '-'
		}
		uuid += (i === 12 ? 4 : i === 16 ? (random & 3) | 8 : random).toString(16)
	}
	return uuid
}

// 获取svg图标(id)列表
export const getIconList = (): string[] => {
	const rs: string[] = []
	const list = document.querySelectorAll('svg symbol[id^="icon-"]')
	for (let i = 0; i < list.length; i++) {
		rs.push(list[i].id)
	}
	return rs
}

// 获取字典Label（表格标签等）；每项含 dictItemValue 便于计算 fallback 颜色
const getDictItem = (dictCode: string, dictItemValue: string | number | null | undefined) => {
	const dictItemArray: any[] = []
	const appStore = useAppStore()
	const dict: any = appStore.dictList.find((element: any) => element.dictCode === dictCode)
	const raw =
		dictItemValue === null || dictItemValue === undefined || dictItemValue === ''
			? ''
			: String(dictItemValue).trim()

	if (!raw) {
		dictItemArray.push({
			dictItemText: '-',
			dictItemColor: '',
			dictItemValue: ''
		})
		return dictItemArray
	}

	if (dict) {
		raw.split(',').forEach((part: string) => {
			const value = part.trim()
			const val = dict.dictItemList.find((element: any) => String(element.dictItemValue) === value)
			if (val) {
				dictItemArray.push({
					dictItemText: val.dictItemText,
					dictItemColor: val.dictItemColor ?? '',
					dictItemValue: value
				})
			} else {
				dictItemArray.push({
					dictItemText: value,
					dictItemColor: '',
					dictItemValue: value
				})
			}
		})
	} else {
		dictItemArray.push({
			dictItemText: raw,
			dictItemColor: '',
			dictItemValue: raw
		})
	}
	return dictItemArray
}

// 获取字典数据列表
export const getDictDataList = (dictList: any[], dictCode: string) => {
	const dict = dictList.find((element: any) => element.dictCode === dictCode)
	if (dict) {
		return dict.dictItemList
	} else {
		return []
	}
}

// 树形数据转换
export const treeDataTranslate = (data: any[], id?: string, pid?: string): any[] => {
	const res: any[] = []
	const temp: any = {}
	id = id || 'id'
	pid = pid || 'pid'
	for (let i = 0; i < data.length; i++) {
		temp[data[i][id]] = data[i]
	}
	for (let k = 0; k < data.length; k++) {
		if (!temp[data[k][pid]] || data[k][id] === data[k][pid]) {
			res.push(data[k])
			continue
		}
		if (!temp[data[k][pid]]['children']) {
			temp[data[k][pid]]['children'] = []
		}
		temp[data[k][pid]]['children'].push(data[k])
	}
	return res
}

// 生成数字字母混合字符串
export default getDictItem
