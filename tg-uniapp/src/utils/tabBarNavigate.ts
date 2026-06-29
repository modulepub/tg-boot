import { customTabbarEnable, customTabbarList, nativeTabbarList, selectedTabbarStrategy, TABBAR_STRATEGY_MAP } from '@/tabbar/config'

/** 是否与 pages.json TabBar 中配置的页面一致（与是否隐藏原生底栏无关） */
export function isTabBarConfiguredPath(pathNoQuery: string): boolean {
  const list = customTabbarEnable ? customTabbarList : nativeTabbarList
  const normalized = pathNoQuery.startsWith('/') ? pathNoQuery.slice(1) : pathNoQuery
  return list.some(item => item.pagePath === normalized)
}

/**
 * 打开 tabBar 页面。
 * H5 在 NO_TABBAR（隐藏原生底栏）时，`switchTab` 可能触发 uni-h5 内部 `undefined.replace` 异常；
 * 此时改用 `reLaunch`。
 *
 * @param url 须带前导 `/`，可与登录回跳一致携带 query
 */
export function openTabBarPage(url: string) {
  const raw = String(url ?? '').trim()
  if (!raw) {
    return
  }
  const normalized = raw.startsWith('/') ? raw : `/${raw}`
  const pathOnly = normalized.split('?')[0]
  if (selectedTabbarStrategy === TABBAR_STRATEGY_MAP.NO_TABBAR) {
    uni.reLaunch({ url: normalized })
    return
  }
  uni.switchTab({ url: pathOnly })
}
