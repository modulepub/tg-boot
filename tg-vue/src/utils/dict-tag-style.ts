/**
 * 静态字典未配置 dictItemColor、或后端未带颜色时，表格标签仍有可读背景色。
 * 优先使用字典中的显式颜色；否则按字典编码 + 取值做酌情 fallback（稳定、可迭代）。
 */

const PALETTE = ['#409EFF', '#67C23A', '#E6A23C', '#9266DC', '#36CFC9', '#FF69B4', '#13c2c2', '#fa8c16']

function hashKey(dictCode: string, value: string): number {
	let h = 0
	const key = `${dictCode}:${value}`
	for (let i = 0; i < key.length; i++) {
		h = (h * 31 + key.charCodeAt(i)) >>> 0
	}
	return h
}

/**
 * @param explicit 来自 dict_item / STATIC_DICT_LIST 的 dictItemColor
 */
export function resolveDictTagColor(dictCode: string, dictItemValue: string, explicit?: string | null): string {
	const exp = explicit?.trim()
	if (exp) {
		return exp
	}

	const v = dictItemValue ?? ''
	if (!v.trim()) {
		return '#909399'
	}

	// 纯数字类字典：常见开关 / 等级（酌情，覆盖多数 varchar 数字列）
	if (/^-?\d+$/.test(v)) {
		const n = parseInt(v, 10)
		if (n === -1) {
			return '#F56C6C'
		}
		if (n === 0) {
			return '#909399'
		}
		if (n === 1) {
			return '#67C23A'
		}
		if (n === 2) {
			return '#E6A23C'
		}
		if (n >= 3) {
			return '#F56C6C'
		}
	}

	return PALETTE[hashKey(dictCode, v) % PALETTE.length]
}
