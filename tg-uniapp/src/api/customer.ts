import type { ICustomerInfoRes } from '@/api/types/login'
import { http } from '@/http/http'

/** 与后端 CusCityResidenceOptionDTO 一致 */
export interface ICusCityResidenceOption {
  cusCityResidenceCode?: string
  cusCityResidenceName?: string
}

/** 公开接口 `PubCustomerController#queryByCusCode` 返回的客户字段（与后端 Customer 对齐，按需列举） */
export interface IPubCustomerRes {
  cusCode?: string
  cusAvatar?: string
  cusLifePhoto?: string
  cusTeenagePhoto?: string
  cusName?: string
  cusSexCode?: string
  cusAge?: number
  cusCityResidenceName?: string
  cusMaritalStatusCode?: string
  cusOccupationalDescription?: string
  cusHandholdsNum?: number
  cusHaveHouseStatusCode?: string
  cusHaveCarStatusCode?: string
  cusDisabledStatusCode?: string
  cusMoment?: string
  cusRemark?: string
  cusDesc?: string
  cusTagCode?: string
  cusUserCode?: string
  [key: string]: any
}

/** 与 `IPubCustomerRes` 同义，供牵线页等历史引用 */
export type ICustomerProfile = IPubCustomerRes

/** 公开-按客户编码查询客户（嘉宾资料卡等） */
export function getPubCustomerByCusCode(cusCode: string) {
  return http.get<IPubCustomerRes>('/pub/customer/customer/queryByCusCode', { cusCode })
}

/** 用户端：客户表中出现过的常驻城市（编码 + 名称） */
export function getCustomerResidenceCities() {
  return http.get<ICusCityResidenceOption[]>('/cus/customer/getCitys')
}

/** 用户端：编辑当前登录用户绑定客户（请求体传需要更新的字段即可） */
export function editCurrCusInfo(body: Record<string, unknown>) {
  return http.post<ICustomerInfoRes>('/cus/customer/editCurrCusInfo', body)
}
