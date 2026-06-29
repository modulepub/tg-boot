import type { CustomRequestOptions } from '@/http/types'
import { http } from '@/http/http'

/** 与后端 `DtMatch` 对齐（用户端我申请的牵线） */
export interface IDtMatch {
  id?: string
  mtCode?: string
  mtName?: string
  mtMkCode?: string
  mtPursuingCusCode?: string
  mtPursuedCusCode?: string
  /** 被追求者姓名 */
  mtPursuedCusName?: string
  /** 被追求者头像 */
  mtPursuedCusAvatar?: string
  /** null/空：沟通中；0：失败；1：成功 */
  mtPassedStatusCode?: string | null
  createTime?: string
  [key: string]: any
}

export interface IDtMatchMyPursuedPageQuery {
  pageNo: number
  pageSize: number
  /** 按红娘筛选，对应 `mtMkCode` */
  mtMkCode?: string
}

export interface IDtMatchPageRes {
  records?: IDtMatch[]
  total?: number
}

/** 用户端-我申请牵线的分页列表 */
export function getMyPursuedMatchPageList(params: IDtMatchMyPursuedPageQuery) {
  return http.get<IDtMatchPageRes>('/cus/dating/dtMatch/myPursuedlist', params)
}

/** 用户端-发起牵线申请（POST `/cus/dating/dtMatch/apply`） */
export function applyDtMatch(
  data: Pick<IDtMatch, 'mtMkCode' | 'mtPursuingCusCode' | 'mtPursuedCusCode'> & Partial<IDtMatch>,
  options?: Partial<CustomRequestOptions>,
) {
  return http.post<IDtMatch>('/cus/dating/dtMatch/apply', data, undefined, undefined, options)
}
