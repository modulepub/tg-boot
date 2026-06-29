/**
 * 与后端 dict / dict_item 初始化数据一致，便于后台管理与国际化迁移。
 * dictItemText 暂为中文展示文案；后续可改为 i18n key。
 */

export interface DictItemRow {
	dictItemValue: string
	dictItemText: string
	dictItemColor?: string
}

export interface DictRow {
	dictCode: string
	dictItemList: DictItemRow[]
}

export const STATIC_DICT_LIST: DictRow[] = [
	{
		dictCode: 'userSexCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '男', dictItemColor: 'rgb(8, 106, 234)' },
			{ dictItemValue: '2', dictItemText: '女', dictItemColor: 'rgb(248, 166, 166)' }
		]
	},
	{
		dictCode: 'cusPoolStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已入库', dictItemColor: 'rgb(89, 254, 6)' },
			{ dictItemValue: '0', dictItemText: '未入库', dictItemColor: 'rgb(244, 6, 6)' }
		]
	},
	{
		dictCode: 'cusIntentionLevelCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '高', dictItemColor: 'rgb(255, 153, 0)' },
			{ dictItemValue: '2', dictItemText: '中', dictItemColor: 'rgb(251, 240, 21)' },
			{ dictItemValue: '3', dictItemText: '低', dictItemColor: 'rgb(246, 244, 155)' }
		]
	},
	{
		dictCode: 'cusLevelCode',
		dictItemList: [{ dictItemValue: '1', dictItemText: '普通客户', dictItemColor: '#909399' }]
	},
	{
		dictCode: 'cusTagCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '优质', dictItemColor: 'rgb(84, 234, 9)' },
			{ dictItemValue: '2', dictItemText: '沟通良好', dictItemColor: 'rgb(255, 11, 11)' }
		]
	},
	{
		dictCode: 'cusSourceCode',
		dictItemList: [{ dictItemValue: '1', dictItemText: '导入', dictItemColor: '#409EFF' }]
	},
	{
		dictCode: 'cusIntentionStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '有意向', dictItemColor: 'rgb(242, 130, 10)' },
			{ dictItemValue: '2', dictItemText: '中', dictItemColor: '#E6A23C' },
			{ dictItemValue: '0', dictItemText: '无意向', dictItemColor: 'rgb(139, 135, 135)' }
		]
	},
	{
		dictCode: 'cusHaveHouseStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '有', dictItemColor: 'rgb(17, 234, 9)' },
			{ dictItemValue: '0', dictItemText: '无', dictItemColor: 'rgb(189, 174, 174)' }
		]
	},
	{
		dictCode: 'cusHaveCarStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '有', dictItemColor: 'rgb(51, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '无', dictItemColor: 'rgb(255, 221, 0)' }
		]
	},
	{
		dictCode: 'cusDealtStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已成交', dictItemColor: 'rgb(123, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未成交', dictItemColor: 'rgb(244, 5, 5)' }
		]
	},
	{
		dictCode: 'cusAssignSalesStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已分配', dictItemColor: 'rgb(26, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未分配', dictItemColor: 'rgb(247, 171, 171)' }
		]
	},
	{
		dictCode: 'cusAssignServersStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已分配', dictItemColor: 'rgb(68, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未分配', dictItemColor: 'rgb(251, 156, 156)' }
		]
	},
	{
		dictCode: 'cusDealtCompleteStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已完单', dictItemColor: 'rgb(174, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未完单', dictItemColor: 'rgb(243, 25, 25)' }
		]
	},
	{
		dictCode: 'cusFollowUpStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已跟进', dictItemColor: 'rgb(132, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未跟进', dictItemColor: 'rgb(252, 12, 12)' }
		]
	},
	{
		dictCode: 'cusIdentityAuthenticatedStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已实名', dictItemColor: 'rgb(77, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未实名', dictItemColor: 'rgb(251, 171, 66)' }
		]
	},
	{
		dictCode: 'cusLsStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已点亮', dictItemColor: 'rgb(255, 77, 77)' },
			{ dictItemValue: '0', dictItemText: '未点亮', dictItemColor: 'rgb(180, 180, 180)' }
		]
	},
	{
		dictCode: 'cusAuditProcessCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '待修改', dictItemColor: '#E6A23C' },
			{ dictItemValue: '2', dictItemText: '审核中', dictItemColor: '#409EFF' },
			{ dictItemValue: '3', dictItemText: '审核通过', dictItemColor: '#67C23A' }
		]
	},
	{
		dictCode: 'userEnabledCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已启用', dictItemColor: 'rgb(77, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未启用', dictItemColor: 'rgb(251, 171, 66)' }
		]
	},
	{
		dictCode: 'contactRecordSourceCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '手工创建', dictItemColor: 'rgb(248, 170, 170)' },
			{ dictItemValue: '2', dictItemText: '电访系统', dictItemColor: 'rgb(150, 236, 11)' },
			{ dictItemValue: '3', dictItemText: '企业微信', dictItemColor: 'rgb(241, 124, 6)' }
		]
	},
	{
		dictCode: 'contactRecordMethodCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '电话', dictItemColor: 'rgb(6, 251, 88)' },
			{ dictItemValue: '2', dictItemText: '个人微信', dictItemColor: 'rgb(220, 248, 6)' },
			{ dictItemValue: '3', dictItemText: '企业微信', dictItemColor: 'rgb(161, 251, 16)' }
		]
	},
	{
		dictCode: 'nodePublishStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已发布', dictItemColor: '#67C23A' },
			{ dictItemValue: '0', dictItemText: '未发布', dictItemColor: '#909399' }
		]
	},
	{
		dictCode: 'nodeContentTypeCode',
		dictItemList: [
			{ dictItemValue: 'text', dictItemText: '文本', dictItemColor: '#409EFF' },
			{ dictItemValue: 'link', dictItemText: '链接', dictItemColor: '#E6A23C' },
			{ dictItemValue: 'citation', dictItemText: '引用', dictItemColor: '#9266DC' },
			{ dictItemValue: 'file', dictItemText: '文件', dictItemColor: '#67C23A' }
		]
	},
	{
		dictCode: 'nodeTypeCode',
		dictItemList: [
			{ dictItemValue: 'catalog', dictItemText: '栏目', dictItemColor: '#36CFC9' },
			{ dictItemValue: 'document', dictItemText: '文章', dictItemColor: '#409EFF' }
		]
	},
	{
		dictCode: 'cusFollowUpReminderTypeCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '待跟进', dictItemColor: '#E6A23C' },
			{ dictItemValue: '2', dictItemText: '3-6个月跟进提醒', dictItemColor: '#F56C6C' }
		]
	},
	{
		dictCode: 'configTypeCode',
		dictItemList: [{ dictItemValue: 'fileConfig', dictItemText: '文件系统', dictItemColor: '#409EFF' }]
	},
	{
		dictCode: 'configEnableStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '已启用', dictItemColor: 'rgb(140, 255, 0)' },
			{ dictItemValue: '0', dictItemText: '未启用', dictItemColor: 'rgb(100, 99, 99)' }
		]
	},
	{
		dictCode: 'cusIdTypeCode',
		dictItemList: [
			{ dictItemValue: 'idCard', dictItemText: '身份证', dictItemColor: '#67C23A' },
			{ dictItemValue: 'passport', dictItemText: '护照', dictItemColor: '#E6A23C' }
		]
	},
	{
		dictCode: 'relationTypeCode',
		dictItemList: [{ dictItemValue: '1', dictItemText: '客户关系', dictItemColor: 'rgb(237, 29, 29)' }]
	},
	{
		dictCode: 'userLoginRestrictStatusCode',
		dictItemList: [
			{ dictItemValue: '0', dictItemText: '不限制', dictItemColor: 'rgb(77, 255, 0)' },
			{ dictItemValue: '1', dictItemText: '限制登录', dictItemColor: 'rgb(255, 77, 77)' }
		]
	},
	{
		dictCode: 'relationPassedStatusCode',
		dictItemList: [
			{ dictItemValue: '1', dictItemText: '通过', dictItemColor: '#67C23A' },
			{ dictItemValue: '0', dictItemText: '不通过', dictItemColor: '#909399' },
			{ dictItemValue: '-1', dictItemText: '驳回', dictItemColor: '#F56C6C' }
		]
	}
]
