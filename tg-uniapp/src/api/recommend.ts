import { http } from '@/http/http'

export interface IRecommendQuery {
  pageNo: number
  pageSize: number
}

export interface IRecommendItem {
  cusCode: string
  cusName?: string
  cusMoment?: string
  cusAvatar?: string
  cusAge?: number
  cusCityResidenceName?: string
  recommendedSourceCode?: string
  recommendedMatchScore?: number | string
}

export interface IRecommendListRes {
  records?: IRecommendItem[]
  total?: number
  size?: number
  current?: number
}

/** 对象推荐历史单条（与后端 DtRecommended 对齐） */
export interface IRecommendHistoryRecord {
  id?: string
  cusCode?: string
  cusAvatar?: string
  cusName?: string
  cusSexCode?: string
  cusAge?: number
  cusCityResidenceName?: string
  intentionMinAge?: number
  intentionMaxAge?: number
  intentionCityCode?: string
  intentionHaveHouseCode?: string
  intentionHaveCarCode?: string
  intentionSexCode?: string
  intentionDisabledStatusCode?: string
  /** 若后端有匹配度字段可对接 */
  matchPercent?: number
  recommendedSourceCode?: string
  recommendedMatchScore?: number | string
}

export interface IRecommendHistoryPage {
  records?: IRecommendHistoryRecord[]
  total?: number
  size?: number
  current?: number
  pages?: number
}

export function getRecommendHistoryList(params: IRecommendQuery) {
  return http.get<IRecommendHistoryPage>('/cus/dating/dtRecommended/historyList', params)
}

/** 推荐意向（用户端 DtIntentionDTO） */
export interface IDtRecommendIntentDTO {
  /** 编辑提交时必填 */
  id?: string
  intentionUserCode?: string
  intentionCode?: string
  intentionName?: string
  intentionMaxAge?: number
  intentionMinAge?: number
  intentionHaveHouseCode?: string
  intentionHaveCarCode?: string
  intentionCityCode?: string | null
  intentionSexCode?: string
  intentionDisabledStatusCode?: string
}

/** 用户端-获得推荐意向 */
export function getRecommendIntent() {
  return http.get<IDtRecommendIntentDTO>('/cus/dating/dtIntention/getDtIntention')
}

/** 用户端-交友意向编辑（请求体同 DtIntention） */
export function saveRecommendIntent(data: IDtRecommendIntentDTO) {
  return http.post<string>('/cus/dating/dtIntention/edit', data)
}

export interface IPreferencePayload {
  preferenceTargetCusCode: string
  preferenceLikeStatusCode: '0' | '1'
}

export function getRecommendList(params: IRecommendQuery) {
  return http.get<IRecommendListRes>('/cus/dating/dtRecommended/list', params)
}

export function savePreference(data: IPreferencePayload) {
  return http.post<void>('/cus/dating/dtPreference/save', data)
}

/** 与后端 `DtPreference` 分页记录对齐（按需扩展字段） */
export interface IDtPreferenceRecord {
  id?: string
  preferenceCode?: string
  /** 发起偏好的客户（如「喜欢我的」里即喜欢你的人） */
  preferenceCusCode?: string
  preferenceCusName?: string
  preferenceCusAge?: number
  preferenceCusAvatar?: string
  preferenceCusCityResidenceCode?: string
  preferenceCusCityResidenceName?: string
  /** 若后端联表返回性别编码，用于列表展示 */
  preferenceCusSexCode?: string
  preferenceTargetCusCode?: string
  preferenceTargetCusName?: string
  preferenceTargetCusAge?: number
  preferenceTargetCusAvatar?: string
  preferenceTargetCusCityResidenceName?: string
  /** 目标嘉宾性别（若库表或联表返回） */
  preferenceTargetCusSexCode?: string
  preferenceLikeStatusCode?: string
  /** 是否相互喜欢：1 双向喜欢 */
  preferenceMutuaStatusCode?: string
}

export interface IPreferenceLikeMePage {
  records?: IDtPreferenceRecord[]
  total?: number
  size?: number
  current?: number
  pages?: number
}

/** 用户端-喜欢我的列表 */
export function getLikeMeList(params: IRecommendQuery) {
  return http.get<IPreferenceLikeMePage>('/cus/dating/dtPreference/likeMeList', params)
}

/** 用户端-我喜欢的列表 */
export function getMyLikeList(params: IRecommendQuery) {
  return http.get<IPreferenceLikeMePage>('/cus/dating/dtPreference/myLikeList', params)
}
