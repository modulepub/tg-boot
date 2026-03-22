import { IHooksOptions } from '@/hooks/interface'
import service from '@/utils/request'
import { onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export const useCrud = (options: IHooksOptions) => {
	const defaultOptions: IHooksOptions = {
		createdIsNeed: true,
		dataListUrl: '',
		deleteUrl: '',
		primaryKey: 'id',
		exportUrl: '',
		queryForm: {},
		dataList: [],
		sortBy: '',
		pageNo: 1,
		pageSize: 10,
		total: 0,
		pageSizes: [10, 20, 50, 100, 200, 500, 1000, 5000],
		dataListLoading: false,
		dataListSelections: [],
		dataListSelectionKeys: []
	}

	const mergeDefaultOptions = (options: any, props: any): IHooksOptions => {
		for (const key in options) {
			if (!Object.getOwnPropertyDescriptor(props, key)) {
				props[key] = options[key]
			}
		}
		return props
	}

	// 覆盖默认值
	const state = mergeDefaultOptions(defaultOptions, options)

	onMounted(() => {
		if (state.createdIsNeed) {
			query()
		}
	})

	const query = () => {
		if (!state.dataListUrl) {
			return
		}

		state.dataListLoading = true

		const queryFormNoEm: any = Object.fromEntries(
			Object.entries(state.queryForm).filter(([key, value]) => {
				return value !== undefined && value !== null && value !== ''
			})
		)
		let params = {
			sortBy: state.sortBy,
			pageNo: state.pageNo,
			pageSize: state.pageSize,
			...queryFormNoEm
		}
		service
			.get(state.dataListUrl, {
				params
			})
			.then((res: any) => {
				state.dataList = res.data.records
				state.total = res.data.total
			})
			.finally(() => {
				state.dataListLoading = false
			})
	}

	const getDataList = () => {
		state.pageNo = 1
		query()
	}

	const sizeChangeHandle = (val: number) => {
		state.pageNo = 1
		state.pageSize = val
		query()
	}

	const currentChangeHandle = (val: number) => {
		state.pageNo = val
		query()
	}

	// 多选
	const selectionChangeHandle = (selections: any[]) => {
		state.dataListSelections = selections
		state.dataListSelectionKeys = selections.map((item: any) => state.primaryKey && item[state.primaryKey])
	}

	// 单选值
	const singleSelectionHandle = () => {
		const selectionKeys = state.dataListSelectionKeys ? state.dataListSelectionKeys : []

		if (selectionKeys.length === 0) {
			ElMessage.warning('请选择一条记录')
			return
		}

		if (selectionKeys.length > 1) {
			ElMessage.warning('只能选择一条记录')
			return
		}

		return selectionKeys[0]
	}

	// 排序
	const sortChangeHandle = (data: any) => {
		const { prop, order } = data

		if (prop && order) {
			state.sortBy = prop
		} else {
			state.sortBy = ''
		}

		query()
	}

	const deleteHandle = (ids: any) => {
		ElMessageBox.confirm('确定进行删除操作?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		})
			.then(() => {
				if (state.deleteUrl != null) {
					service.post(state.deleteUrl, ids).then(() => {
						ElMessage.success('删除成功')

						query()
					})
				}
			})
			.catch(() => {})
	}

	const deleteBatchHandle = (key?: any[]) => {
		let data: any[] = []
		if (key) {
			data = [key]
		} else {
			data = state.dataListSelectionKeys ? state.dataListSelectionKeys : []

			if (data.length === 0) {
				ElMessage.warning('请选择删除记录')
				return
			}
		}

		ElMessageBox.confirm('确定进行删除操作?', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		})
			.then(() => {
				if (state.deleteUrl) {
					service.post(state.deleteUrl, data).then(() => {
						ElMessage.success('删除成功')

						query()
					})
				}
			})
			.catch(() => {})
	}

	const downloadHandle = async (url: string, filename?: string, method: string = 'GET'): Promise<any> => {
		try {
			const res = await service({
				responseType: 'blob',
				url: url,
				method: method
			})
			// 创建a标签
			const down = document.createElement('a')

			// 文件名没传，则使用时间戳
			if (filename) {
				down.download = filename
			} else {
				const downName = res.headers['content-disposition'].split('=')[1]
				down.download = decodeURI(downName)
			}

			// 隐藏a标签
			down.style.display = 'none'

			// 创建下载url
			down.href = URL.createObjectURL(
				new Blob([res.data], {
					type: res.data.type
				})
			)

			// 模拟点击下载
			document.body.appendChild(down)
			down.click()

			// 释放URL
			URL.revokeObjectURL(down.href)
			// 下载完成移除
			document.body.removeChild(down)
		} catch (err: any) {
			ElMessage.error(err.message)
		}
	}

	const reset = (queryRef: any) => {
		queryRef.resetFields()
		if (state.queryFormReset) {
			state.queryForm = Object.assign(state.queryFormReset)
		} else {
			state.queryForm = {}
		}
		getDataList()
	}

	return {
		getDataList,
		sizeChangeHandle,
		currentChangeHandle,
		selectionChangeHandle,
		singleSelectionHandle,
		sortChangeHandle,
		deleteHandle,
		deleteBatchHandle,
		downloadHandle,
		reset
	}
}
