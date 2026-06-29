import pcaRaw from '@/components/tg-address/pca.json'
import cityCodesRaw from '@/data/pca-city-codes.json'

const MUNICIPALITY_PROVINCES = new Set(['北京市', '天津市', '上海市', '重庆市'])
const MUNICIPALITY_CITY_LABEL = '市辖区'
const PCA = pcaRaw as Record<string, Record<string, string[]>>
const CITY_CODES = cityCodesRaw as Record<string, { code: string; fullName: string }>

export const cityCascaderOptions = Object.entries(PCA).map(([province, cities]) => ({
	value: province,
	label: province,
	children: MUNICIPALITY_PROVINCES.has(province)
		? [{ value: MUNICIPALITY_CITY_LABEL, label: province }]
		: Object.keys(cities).map(city => ({ value: city, label: city }))
}))

const buildCityValue = (province: string, city: string) => {
	if (!province) {
		return ''
	}
	if (MUNICIPALITY_PROVINCES.has(province)) {
		return province
	}
	return city && city !== MUNICIPALITY_CITY_LABEL ? city : ''
}

const buildCityFullName = (province: string, city: string) => {
	if (!province) {
		return ''
	}
	if (MUNICIPALITY_PROVINCES.has(province)) {
		return `中国/${province}`
	}
	const cityName = buildCityValue(province, city)
	return cityName ? `中国/${province}/${cityName}` : ''
}

const findProvinceByCityName = (cityName: string) => {
	const name = cityName.trim()
	if (!name) {
		return null
	}
	if (MUNICIPALITY_PROVINCES.has(name)) {
		return { province: name, city: MUNICIPALITY_CITY_LABEL }
	}
	for (const province of Object.keys(PCA)) {
		if (MUNICIPALITY_PROVINCES.has(province)) {
			continue
		}
		if (PCA[province]?.[name]) {
			return { province, city: name }
		}
	}
	return null
}

export const parseCityToCascader = (cityName: string, cityCode: string): string[] => {
	const name = String(cityName ?? '').trim()
	const code = String(cityCode ?? '').trim()

	if (code.startsWith('CN-') && name.includes('/')) {
		const parts = name.split('/').map(s => s.trim()).filter(Boolean)
		if (parts.length >= 3) {
			return [parts[1], parts[2]]
		}
		if (parts.length === 2 && MUNICIPALITY_PROVINCES.has(parts[1])) {
			return [parts[1], MUNICIPALITY_CITY_LABEL]
		}
	}

	const byCode = Object.entries(CITY_CODES).find(([, item]) => item.code === code)
	if (byCode) {
		const found = findProvinceByCityName(byCode[0])
		if (found) {
			return [found.province, found.city]
		}
	}

	const byName = findProvinceByCityName(name)
	if (byName) {
		return [byName.province, byName.city]
	}

	return []
}

export const resolveCityFromCascader = (value: string[] | undefined) => {
	if (!value?.length) {
		return { cityCode: '', cityName: '' }
	}
	const [province, city] = value
	const displayLabel = buildCityValue(province, city)
	const local = CITY_CODES[displayLabel]
	if (local?.code) {
		return {
			cityCode: local.code,
			cityName: local.fullName || buildCityFullName(province, city)
		}
	}
	return {
		cityCode: '',
		cityName: buildCityFullName(province, city)
	}
}
