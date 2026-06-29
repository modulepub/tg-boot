import type { CustomRequestOptions } from '@/http/types'
import { http } from '@/http/http'
import { ContentTypeEnum } from '@/http/tools/enum'

/** 用户端-订单-创建订单 单行明细（BizTradeOrderService.OrderGoodsDTO） */
export interface IOrderGoodsCreateItem {
  tdGdCode: string
  /** 下单数量 */
  tdOdGdNum: number
}

export interface ITdOrder {
  tdOdCode?: string
  tdOdAmount?: number | string
  tdOdRemark?: string
  tdOdPaidCode?: string
}

/** 与后端 `TdOrderGoods` 对齐（用户端 listByUser 分页行） */
export interface ITdOrderGoods {
  id?: string
  tdGdCode?: string
  tdGdName?: string
  tdGdPrice?: number | string
  tdGdDescription?: string
  tdGdSysUserCode?: string
  tdGdSysUserRealName?: string
  tdGdSysUserPhone?: string
  tdOdGdCode?: string
  tdOdCode?: string
  tdOdGdNum?: number | string
  tdOdGdAmount?: number | string
  tdOdPaidCode?: string
  createTime?: string
  [key: string]: any
}

export interface IPageTdOrderGoods {
  records?: ITdOrderGoods[]
  total?: number
}

export interface IPrePayReq {
  tdOdCode: string
  tdOdAmount: number | string
  tdOdRemark?: string
  tdPaidChannelCode: string
  platParam: Record<string, any>
}

export interface IPrePayRes {
  platParam?: Record<string, any>
}

/** 用户端-订单-创建订单（body 为 JSON 数组） */
export function createTradeOrder(goodsList: IOrderGoodsCreateItem[]) {
  return http<ITdOrder>({
    url: '/cus/trade/tradeOrder/create',
    method: 'POST',
    data: JSON.stringify(goodsList),
    header: {
      'Content-Type': ContentTypeEnum.JSON,
    },
  })
}

/** 公开-订单-通过订单编码查询 */
export function getPubTradeOrderByCode(tdOdCode: string) {
  return http.get<ITdOrder>('/pub/trade/tradeOrder/queryByCode', { tdOdCode })
}

/** 公开-综合支付-预支付接口 */
export function prePayByPub(data: IPrePayReq) {
  return http.post<IPrePayRes>('/pub/pay/prePay', data)
}

/** 用户端-订单商品-分页列表（当前下单人，可按供应商账号 `tdGdSysUserCode` 筛选红娘） */
export function getCusTradeOrderGoodsListByUser(
  params: { pageNo: number, pageSize: number, tdGdSysUserCode?: string },
  options?: Partial<CustomRequestOptions>,
) {
  const q: Record<string, any> = { pageNo: params.pageNo, pageSize: params.pageSize }
  const vendor = String(params.tdGdSysUserCode ?? '').trim()
  if (vendor)
    q.tdGdSysUserCode = vendor
  return http.get<IPageTdOrderGoods>(
    '/cus/trade/tradeOrderGoods/listByUser',
    q,
    undefined,
    options,
  )
}
