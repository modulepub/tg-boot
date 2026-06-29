import { useCustomerStore } from '@/store/customer'
import { useTokenStore } from '@/store/token'

/**
 * 已登录时请求 `/cus/customer/getCurrCusInfo`，更新 Pinia 中的客户资料（含牵线次数等）。
 * 与 `getInfo` 一致：应在界面展示前刷新，避免沿用持久化缓存的旧权益数据。
 */
export function refreshCustomerIfLoggedIn() {
  try {
    const tokenStore = useTokenStore()
    if (!tokenStore.updateNowTime().hasLogin)
      return
    const customerStore = useCustomerStore()
    void customerStore.fetchCustomerInfo().catch((error) => {
      console.error('刷新客户信息失败:', error)
    })
  }
  catch (error) {
    console.error('refreshCustomerIfLoggedIn', error)
  }
}
