import { http } from '@/http/http'

/** 推荐次数权益商品类目编码（与后台商品分类编码一致） */
export const PUB_GOODS_CATEGORY_RECOMMEND_RIGHT = 'cusRecommendRightValue'

/** 添加好友次数权益商品类目编码 */
export const PUB_GOODS_CATEGORY_ADD_FRIEND_RIGHT = 'cusAddFriendRightValue'

/** 红娘牵线次数权益商品类目编码 */
export const PUB_GOODS_CATEGORY_MATCH_RIGHT = 'cusMatchRightValue'

export interface ITdGoodsItem {
  id?: string
  seqNo?: number | string
  tdGdCode?: string
  tdGdName?: string
  tdGdPrice?: number | string
  tdGdDescription?: string
  tdGdTag?: string
  tdGdInventoryNum?: number | string
  tdGdCgyCode?: string
  tdGdEnabledCode?: string
}

export interface IPubTradeGoodsPageRes {
  records?: ITdGoodsItem[]
  total?: number
  current?: number
  size?: number
}

export interface IPubTradeGoodsListQuery {
  pageNo?: number
  pageSize?: number
  /** 商品类目编码 */
  tdGdCgyCode?: string
}

/** 公开-商品-分页列表（Swagger：公开-商品-分页列表查询） */
export function getPubTradeGoodsList(params: IPubTradeGoodsListQuery) {
  return http.get<IPubTradeGoodsPageRes>('/pub/trade/tradeGoods/list', params)
}

/** 推荐次数规格商品列表（固定类目 cusRecommendRightValue） */
export function getRecommendRightGoodsList(pageNo = 1, pageSize = 50) {
  return getPubTradeGoodsList({
    pageNo,
    pageSize,
    tdGdCgyCode: PUB_GOODS_CATEGORY_RECOMMEND_RIGHT,
  })
}

/** 添加好友次数规格商品列表（固定类目 cusAddFriendRightValue） */
export function getAddFriendRightGoodsList(pageNo = 1, pageSize = 50) {
  return getPubTradeGoodsList({
    pageNo,
    pageSize,
    tdGdCgyCode: PUB_GOODS_CATEGORY_ADD_FRIEND_RIGHT,
  })
}

/** 红娘牵线规格商品列表（固定类目 cusMatchRightValue） */
export function getMatchRightGoodsList(pageNo = 1, pageSize = 50) {
  return getPubTradeGoodsList({
    pageNo,
    pageSize,
    tdGdCgyCode: PUB_GOODS_CATEGORY_MATCH_RIGHT,
  })
}
