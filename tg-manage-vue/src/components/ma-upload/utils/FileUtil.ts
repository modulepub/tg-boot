// 缓存
class FileUtil {
	getFullPath = (filePath: string): string => {
		return 'https://oss.iqingqing.net' + filePath
	}
}

export default new FileUtil()
