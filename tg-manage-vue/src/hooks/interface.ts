export interface IHooksOptions {
	// 是否在创建页面时，调用数据列表接口
	createdIsNeed?: boolean
	// 数据列表 Url
	dataListUrl?: string
	// 删除 Url
	deleteUrl?: string
	// 主键key，用于删除场景
	primaryKey?: string
	// 导出 Url
	exportUrl?: string
	// 查询条件
	queryForm?: any
	// 数据列表
	dataList?: any[]
	// 排序字段
	sortBy?: string
	// 当前页码
	pageNo?: number
	// 每页数
	pageSize?: number
	// 总条数
	total?: number
	pageSizes?: any[]
	// 数据列表，loading状态
	dataListLoading?: boolean
	// 数据列表，多选项
	dataListSelections?: any[]
	dataListSelectionKeys?: any[]
}
