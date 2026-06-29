/** 客户列表/详情展示辅助 */

export const resolveCustomerAvatarUrl = (row: Record<string, unknown> | null | undefined): string => {
	if (!row) {
		return ''
	}
	const raw = row.cusAvatar || row.cusLifePhoto
	if (!raw) {
		return ''
	}
	return String(raw).split(',')[0].trim()
}

/** 头像/生活照预览列表（逗号分隔多图） */
export const resolveCustomerAvatarPreviewList = (row: Record<string, unknown> | null | undefined): string[] => {
	if (!row) {
		return []
	}
	const urls: string[] = []
	const append = (raw: unknown) => {
		if (!raw) {
			return
		}
		String(raw)
			.split(',')
			.map(s => s.trim())
			.filter(Boolean)
			.forEach(url => {
				if (!urls.includes(url)) {
					urls.push(url)
				}
			})
	}
	append(row.cusAvatar)
	append(row.cusLifePhoto)
	return urls
}

export const resolveCustomerDisplayName = (row: Record<string, unknown> | null | undefined): string => {
	if (!row) {
		return '—'
	}
	const name = [row.cusName, row.cusNickName].map(v => (v == null ? '' : String(v).trim())).find(Boolean)
	return name || '—'
}

export const formatCustomerFieldValue = (value: unknown): string => {
	if (value === null || value === undefined || value === '') {
		return '—'
	}
	if (typeof value === 'object') {
		return JSON.stringify(value)
	}
	return String(value)
}
