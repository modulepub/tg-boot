/**
 * by 菲鸽 on 2025-08-19
 * 路由拦截，通常也是登录拦截
 * 黑、白名单的配置，请看 config.ts 文件， EXCLUDE_LOGIN_PATH_LIST
 */
import { useTokenStore } from '@/store/token'
import { tabbarStore } from '@/tabbar/store'
import { getLastPage, parseUrlToObj } from '@/utils/index'
import { useLoginModalStore } from '@/store/loginModal'
import { isNeedLoginPath } from './config'

export const FG_LOG_ENABLE = false

export const navigateToInterceptor = {
  // 注意，这里的url是 '/' 开头的，如 '/pages/index/index'，跟 'pages.json' 里面的 path 不同
  // 增加对相对路径的处理，BY 网友 @ideal
  invoke(options: { url?: string, query?: Record<string, string> }) {
    const url = typeof options?.url === 'string' ? options.url : ''
    /** 返回 undefined 会被 uni 视为继续原跳转；false 为拦截 */
    if (!url.trim()) {
      return true
    }
    const query = options?.query
    let { path, query: _query } = parseUrlToObj(url)

    FG_LOG_ENABLE && console.log('\n\n路由拦截器:-------------------------------------')
    FG_LOG_ENABLE && console.log('路由拦截器 1: url->', url, ', query ->', query)
    const myQuery = { ..._query, ...query }
    FG_LOG_ENABLE && console.log('路由拦截器 2: path->', path, ', _query ->', _query)
    FG_LOG_ENABLE && console.log('路由拦截器 3: myQuery ->', myQuery)

    // 处理相对路径
    if (!path.startsWith('/')) {
      const currentPath = getLastPage()?.route || ''
      const normalizedCurrentPath = currentPath.startsWith('/') ? currentPath : `/${currentPath}`
      const baseDir = normalizedCurrentPath.substring(0, normalizedCurrentPath.lastIndexOf('/'))
      path = `${baseDir}/${path}`
    }

    tabbarStore.setAutoCurIdx(path)

    const tokenStore = useTokenStore()
    const hasLogin = tokenStore.updateNowTime().hasLogin

    if (!hasLogin && isNeedLoginPath(path)) {
      let fullPath = path
      if (Object.keys(myQuery).length) {
        const queryStr = Object.keys(myQuery).map(key => `${key}=${encodeURIComponent(myQuery[key])}`).join('&')
        fullPath += `?${queryStr}`
      }
      useLoginModalStore().open({
        pathOrUrl: fullPath,
        title: '详情',
      })
      return false
    }

    return true
  },
}

export const routeInterceptor = {
  install() {
    uni.addInterceptor('navigateTo', navigateToInterceptor)
    uni.addInterceptor('reLaunch', navigateToInterceptor)
    uni.addInterceptor('redirectTo', navigateToInterceptor)
    uni.addInterceptor('switchTab', navigateToInterceptor)
  },
}
