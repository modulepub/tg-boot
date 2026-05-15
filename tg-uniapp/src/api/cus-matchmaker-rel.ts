import type { CustomRequestOptions } from '@/http/types'
import { http } from '@/http/http'

/** 与后端 `DtCusMatchmakerRel` 对齐 */
export interface IDtCusMatchmakerRel {
  id?: string
  cusMkRelCode?: string
  /** 是否展示红娘主页：常见 `1` 展示 / `0` 不展示 */
  cusMkRelShowStatusCode?: string
  mkCode?: string
  mkPhone?: string
  mkWorkPhoto?: string
  mkName?: string
  mkCompanyName?: string
  mkCityName?: string
  /** 红娘说说 */
  mkMoment?: string
  cusCode?: string
  cusName?: string
  cusAvatar?: string
  cusSexCode?: string
  cusMoment?: string
  [key: string]: any
}

export interface IMyMatchmakerPageRes {
  records?: IDtCusMatchmakerRel[]
  total?: number
}

/** 公开分页（与后端 `IPage<DtCusMatchmakerRel>` 对齐） */
export interface IPubCusMatchmakerRelPageRes {
  records?: IDtCusMatchmakerRel[]
  total?: number
}

/** 公开-红娘主页 TA 推荐的男女嘉宾（按红娘编码分页） */
export function getPubGuestListByMkCode(
  params: {
    mkCode: string
    pageNo?: number
    pageSize?: number
    /** 仅展示主页意向：`1` */
    cusMkRelShowStatusCode?: string
  },
  options?: Partial<CustomRequestOptions>,
) {
  const { pageNo = 1, pageSize = 50, mkCode, ...rest } = params
  return http.get<IPubCusMatchmakerRelPageRes>(
    '/pub/dating/dtCusMatchmakerRel/listByMkCode',
    { pageNo, pageSize, mkCode, ...rest },
    undefined,
    options,
  )
}

/** 公开-客户红娘关系分页列表查询（按客户编码） */
export function getPubMatchmakerListByCusCode(
  params: {
    cusCode: string
    pageNo?: number
    pageSize?: number
    /** 仅查主页展示：`1` */
    cusMkRelShowStatusCode?: string
  },
  options?: Partial<CustomRequestOptions>,
) {
  const { pageNo = 1, pageSize = 50, ...rest } = params
  return http.get<IPubCusMatchmakerRelPageRes>(
    '/pub/dating/dtCusMatchmakerRel/listByCusCode',
    { pageNo, pageSize, ...rest },
    undefined,
    options,
  )
}

/** 用户端-我的顾问列表 */
export function getMyMatchmakerList(params: { pageNo: number, pageSize: number }) {
  return http.get<IMyMatchmakerPageRes>('/cus/dating/dtCusMatchmakerRel/myMatchmakerList', params)
}

/** 用户端-更新主页展示开关（cusMkRelCode + 状态码 0/1） */
export function updateCusMkRelShowStatus(data: { cusMkRelCode: string, cusMkRelShowStatusCode: string }) {
  return http.post<void>('/cus/dating/dtCusMatchmakerRel/updateCusMkRelShowStatus', data)
}
