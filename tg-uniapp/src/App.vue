<script setup lang="ts">
import { onHide, onLaunch, onShow } from '@dcloudio/uni-app'
import { navigateToInterceptor } from '@/router/interceptor'
import { useCustomerStore } from '@/store/customer'
import { useTokenStore } from '@/store/token'
import { useUserStore } from '@/store/user'
import { selectedTabbarStrategy, TABBAR_STRATEGY_MAP } from '@/tabbar/config'
import { currRoute } from '@/utils'
import { refreshCustomerIfLoggedIn } from '@/utils/refreshCustomerSession'

const tokenStore = useTokenStore()
const userStore = useUserStore()
const customerStore = useCustomerStore()
const LINK_TOKEN_EXPIRES_IN = 60 * 60 * 24 * 30

function getAccessTokenFromQuery(query?: Record<string, string | undefined>) {
  return query?.accessToken?.trim() || ''
}

function getAccessTokenFromH5Location() {
  // #ifdef H5
  try {
    const href = window.location.href
    const queryFromHref = href.includes('?') ? href.split('?').slice(1).join('?') : ''
    const queryPart = queryFromHref.split('#')[0]
    const hashPart = href.includes('#') ? href.split('#').slice(1).join('#') : ''
    const hashQueryPart = hashPart.includes('?') ? hashPart.split('?').slice(1).join('?') : ''
    const searchStr = [queryPart, hashQueryPart].filter(Boolean).join('&')
    if (!searchStr) {
      return ''
    }
    const params = new URLSearchParams(searchStr)
    return params.get('accessToken')?.trim() || ''
  }
  catch (error) {
    console.error('解析 H5 地址 accessToken 失败:', error)
    return ''
  }
  // #endif
  return ''
}

async function applyAccessTokenFromQuery(query?: Record<string, string | undefined>) {
  const accessToken = getAccessTokenFromQuery(query) || getAccessTokenFromH5Location()
  if (!accessToken) {
    return
  }
  const currentToken = tokenStore.updateNowTime().validToken
  if (currentToken === accessToken) {
    return
  }
  tokenStore.setTokenInfo({
    token: accessToken,
    expiresIn: LINK_TOKEN_EXPIRES_IN,
  })
  try {
    await userStore.fetchUserInfo()
    await customerStore.fetchCustomerInfo()
  }
  catch (error) {
    console.error('链接 accessToken 获取用户/客户信息失败:', error)
  }
}

/** NO_TABBAR 时 pages.json 仍需合法 tabBar，原生栏用 hideTabBar 收起（含 H5） */
function hideNativeTabBarIfNoTabStrategy() {
  if (selectedTabbarStrategy !== TABBAR_STRATEGY_MAP.NO_TABBAR)
    return
  uni.hideTabBar({
    animation: false,
    fail: () => {},
  })
}

onLaunch(async (options) => {
  console.log('App.vue onLaunch', options)
  hideNativeTabBarIfNoTabStrategy()
  await applyAccessTokenFromQuery(options?.query as Record<string, string | undefined>)
})
onShow(async (options) => {
  console.log('App.vue onShow', options)
  hideNativeTabBarIfNoTabStrategy()
  await applyAccessTokenFromQuery(options?.query as Record<string, string | undefined>)
  refreshCustomerIfLoggedIn()
  // 处理直接进入页面路由的情况：如 h5 直接输入路由、微信小程序分享后进入等
  // https://github.com/feige996/unibest/issues/192
  // 勿在无 path 时固定 invoke('/')，否则每次 onShow 会把 tab 同步逻辑打成首页，易与 H5 history 跳转冲突
  const entryUrl = options?.path
    ? `/${options.path}`
    : (currRoute().path || '/pages/index/index')
  navigateToInterceptor.invoke({
    url: entryUrl,
    query: options?.query as Record<string, string> | undefined,
  })
})
onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss"></style>
