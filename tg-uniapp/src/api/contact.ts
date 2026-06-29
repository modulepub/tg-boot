import type { CustomRequestOptions } from '@/http/types'
import { http } from '@/http/http'

export interface IDtContactDTO {
  contactPassedStatusCode?: string
  cusCode?: string
  userCode?: string
  contactCode?: string
  contactSourceCode?: string
  /** 联系人申请表 */
  contactApplyCode?: string
  contactApplyPassedStatusCode?: string
  contactApplySourceCode?: string
  contactApplyGreeting?: string
  cusAvatar?: string
  cusName?: string
  cusAge?: number
  createTime?: string
  updateTime?: string
  [key: string]: any
}

export interface IDtContactPageQuery {
  pageNo: number
  pageSize: number
  /** 是否通过（与后端字典一致，已通过传 `1`）— 联系人表 */
  contactPassedStatusCode?: string
}

/** 联系人申请分页查询参数（GET，与后端 `DtContactApply` 查询条件绑定；「对方发起」接口会额外强制当前用户客户编码） */
export interface IDtContactApplyPageQuery {
  pageNo: number
  pageSize: number
  /** 申请处理状态 */
  contactApplyPassedStatusCode?: string
  cusCode?: string
  userCode?: string
}

/** 是否为待处理的联系人申请（未审核：空或 `0`） */
export function isDtContactApplyPending(row: Pick<IDtContactDTO, 'contactApplyPassedStatusCode' | 'contactPassedStatusCode'>): boolean {
  const st = String(row.contactApplyPassedStatusCode ?? row.contactPassedStatusCode ?? '').trim()
  return st === '' || st === '0'
}

/** 匹配来源文案：`contactSourceCode` / `contactApplySourceCode` 仅三者有值，其余返回 undefined 不作展示 */
export function formatDtContactSourceLabel(code?: string | null): string | undefined {
  const c = String(code ?? '').trim()
  if (c === 'friendRequest')
    return '添加好友'
  if (c === 'datingEvent')
    return '相亲交友活动'
  if (c === 'matchmakerMatching')
    return '红娘牵线'
  return undefined
}

export interface IDtContactPageRes {
  records?: IDtContactDTO[]
  total?: number
}

/** 用户端-联系人-分页列表查询 */
export function getDtContactPageList(params: IDtContactPageQuery) {
  return http.get<IDtContactPageRes>('/cus/dating/dtContact/list', params)
}

/**
 * 用户端-对方发起的联系人申请-分页列表查询
 * `GET /cus/dating/dtContactApply/applyListByThem`
 */
export function getDtContactApplyPageListByThem(params: IDtContactApplyPageQuery) {
  return http.get<IDtContactPageRes>('/cus/dating/dtContactApply/applyListByThem', params)
}

/**
 * 用户端-我发起的联系人申请-分页列表查询
 * `GET /cus/dating/dtContactApply/applyListByMe`
 */
export function getDtContactApplyPageListByMe(params: IDtContactApplyPageQuery) {
  return http.get<IDtContactPageRes>('/cus/dating/dtContactApply/applyListByMe', params)
}

/** 用户端-检查联系人申请状态 */
export function checkDtContactApplyStatus(cusCode: string) {
  return http.post<IDtContactDTO | null>(
    '/cus/dating/dtContactApply/check',
    { cusCode },
    undefined,
    undefined,
    { hideErrorToast: true },
  )
}

/** 用户端-发起联系人申请 */
export function applyDtContactApply(
  data: { cusCode: string, contactApplyGreeting?: string, contactSourceCode?: string },
  options?: Partial<CustomRequestOptions>,
) {
  return http.post<void>('/cus/dating/dtContactApply/apply', data, undefined, undefined, options)
}

/** 用户端-联系人申请-申请通过 */
export function passDtContactApply(
  data: { contactApplyCode: string },
  options?: Partial<CustomRequestOptions>,
) {
  return http.post<void>('/cus/dating/dtContactApply/pass', data, undefined, undefined, options)
}

/** 用户端-联系人申请-申请拒绝 */
export function rejectDtContactApply(
  data: { contactApplyCode: string },
  options?: Partial<CustomRequestOptions>,
) {
  return http.post<void>('/cus/dating/dtContactApply/reject', data, undefined, undefined, options)
}

/** 用户端-联系人-移除（解除好友） */
export function removeDtContact(
  data: { contactCode: string },
  options?: Partial<CustomRequestOptions>,
) {
  return http.post<void>('/cus/dating/dtContact/remove', data, undefined, undefined, options)
}
