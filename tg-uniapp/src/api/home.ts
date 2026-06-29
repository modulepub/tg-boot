import { http } from '@/http/http'

/** CMS 列表项（与后台 CmsNodeDTO 对齐，列表接口不含大字段正文） */
export interface ICmsNodeItem {
  id?: string
  nodeParentCode?: string
  nodeCode?: string
  nodeName?: string
  nodeHeadImg?: string
  nodeSummary?: string
  nodeLink?: string
  nodeTypeCode?: string
  nodeContentTypeCode?: string
  nodePublishTime?: string
}

/** CMS 详情（公开 queryById，含 nodeContent） */
export interface ICmsNodeDetail extends ICmsNodeItem {
  nodeContent?: string
}

export interface ICmsNodeListRes {
  records?: ICmsNodeItem[]
  total?: number
}

export interface IHomeStatisticRes {
  recommentTotal?: number
  matchedTotal?: number
  marriedTotal?: number
  recommentTotalDetail?: string
  matchedTotalDetail?: string
  marriedTotalDetail?: string
}

/**
 * 查询某父栏目下的已发布子节点（分页）
 * @param nodeParentCode 父级 nodeCode，站点官网一般为配置的站点根栏目编码
 */
export function getCmsNodeList(nodeParentCode: string, pageSize = 80, pageNo = 1) {
  return http.get<ICmsNodeListRes>('/pub/cms/cmsNode/list', {
    nodeParentCode,
    pageSize,
    pageNo,
  })
}

/** 公开详情（含正文 HTML），入参为节点主键 id */
export function getCmsNodeDetailById(id: string) {
  return http.get<ICmsNodeDetail>('/pub/cms/cmsNode/queryById', { id })
}

export function getHomeStatisticCenter() {
  return http.get<IHomeStatisticRes>('/pub/dating/statistic/center')
}
