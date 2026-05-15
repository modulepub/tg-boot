import { http } from '@/http/http'

/** 与后端 `DictArea` 一致 */
export interface IDictArea {
  id?: string
  dictAreaCode?: string
  dictAreaParentCode?: string | null
  dictAreaLevel?: number
  dictAreaName?: string
  dictAreaNameEn?: string
  dictAreaFullName?: string
}

/** MyBatis-Plus 分页 JSON */
export interface IDictAreaPage {
  records?: IDictArea[]
  total?: number
  size?: number
  current?: number
  pages?: number
}

export interface IDictAreaSearchQuery {
  keyword?: string
  parentCode?: string
  pageNo?: number
  pageSize?: number
}

/** 公开接口：关键词模糊搜索；无关键词时按 `parentCode` 展开子级（均空则国家级根） */
export function searchDictArea(query: IDictAreaSearchQuery) {
  const q: Record<string, string | number> = {
    pageNo: query.pageNo ?? 1,
    pageSize: query.pageSize ?? 30,
  }
  const kw = String(query.keyword ?? '').trim()
  if (kw)
    q.keyword = kw
  const pc = String(query.parentCode ?? '').trim()
  if (pc)
    q.parentCode = pc
  return http.get<IDictAreaPage>('/pub/dict/area/search', q)
}
