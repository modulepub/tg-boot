import type { CustomRequestOptions } from '@/http/types'
import { http } from '@/http/http'

/** 与后端 `DtMatchmaker` 对齐（用户端分页查询） */
export interface IDtMatchmaker {
  id?: string
  mkCode?: string
  mkUserCode?: string
  mkWorkPhoto?: string
  mkName?: string
  mkAge?: number
  mkServiceUserCount?: number
  mkTags?: string
  mkCityCode?: string
  mkCityName?: string
  mkCompanyCode?: string
  mkCompanyName?: string
  mkMoment?: string
  mkScore?: number | string
  createTime?: string
  [key: string]: any
}

export interface IDtMatchmakerPageQuery {
  pageNo: number
  pageSize: number
  /** 精确城市（与后端 `mkCityName` 绑定） */
  mkCityName?: string
  /** 模糊姓名：`%关键字%` 走后端 `like` */
  mkName?: string
  /** 排序：如 `-mkScore`、`-mkServiceUserCount`、`-createTime`（与 WebQueryUtil `sortBy` 一致） */
  sortBy?: string
}

export interface IDtMatchmakerPageRes {
  records?: IDtMatchmaker[]
  total?: number
}

/** 用户端-红娘信息分页列表查询 */
export function getCusDtMatchmakerPageList(params: IDtMatchmakerPageQuery) {
  return http.get<IDtMatchmakerPageRes>('/cus/dating/dtMatchmaker/list', params)
}

/** 用户端-红娘所在城市分组列表（去重） */
export function getCusDtMatchmakerCityNames() {
  return http.get<string[]>('/cus/dating/dtMatchmaker/listMkCityNames')
}

/** 与后端 `TdGoodsDTO` 对齐（红娘售卖服务/商品） */
export interface ITdGoodsDTO {
  id?: string
  seqNo?: number
  tdGdCode?: string
  tdGdPrice?: number | string
  tdGdDescription?: string
  tdGdSysUserCode?: string
  tdGdSysUserRealName?: string
  tdGdName?: string
  tdGdEnabledCode?: string
  tdGdPeriod?: string
  tdGdTag?: string
  [key: string]: any
}

/** 用户端-通过红娘编码查询红娘信息 */
export function getCusDtMatchmakerByMkCode(mkCode: string) {
  return http.get<IDtMatchmaker>('/cus/dating/dtMatchmaker/queryByMkCode', { mkCode })
}

/** 用户端-通过红娘编码查询红娘售卖的服务 */
export function getCusDtMatchmakerGoodsByMkCode(mkCode: string, options?: Partial<CustomRequestOptions>) {
  return http.get<ITdGoodsDTO[]>('/cus/dating/dtMatchmaker/queryGoodsByMkCode', { mkCode }, undefined, options)
}

/** 公开-红娘主页（与 `/pub/dating/dtMatchmaker/info` 一致，可不登录浏览） */
export function getPubDtMatchmakerInfoByMkCode(mkCode: string, options?: Partial<CustomRequestOptions>) {
  return http.post<IDtMatchmaker>('/pub/dating/dtMatchmaker/info', { mkCode }, undefined, undefined, options)
}
