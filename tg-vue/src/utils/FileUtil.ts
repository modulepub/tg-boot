// 缓存
class FileUtil {
	getFullPath = (filePath: string): string => {
		if (!filePath.startsWith('http')) {
			return import.meta.env.VITE_FILE_PREFIX + filePath
		} else {
			return filePath
		}
	}
}

export default new FileUtil()
