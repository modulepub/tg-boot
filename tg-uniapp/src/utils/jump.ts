import type { IJumpOptions } from '@/utils/jump.types'
import { useTokenStore } from '@/store/token'
import { useLoginModalStore } from '@/store/loginModal'
import { isNeedLoginPath } from '@/router/config'
import { isTabBarConfiguredPath, openTabBarPage } from '@/utils/tabBarNavigate'

export type { IJumpOptions } from '@/utils/jump.types'

function joinUrl(baseUrl: string, relativePath: string) {
  const normalizedBase = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl
  const normalizedPath = relativePath.startsWith('/') ? relativePath : `/${relativePath}`
  return `${normalizedBase}${normalizedPath}`
}

function isHttpUrl(s: string) {
  return /^https?:\/\//i.test(s)
}

function normalizeInternalPath(path: string) {
  const p = path.trim()
  if (!p)
    return '/'
  return p.startsWith('/') ? p : `/${p}`
}

function openWebviewWithToken(targetUrl: string, title: string) {
  const tokenStore = useTokenStore()
  const accessToken = tokenStore.updateNowTime().validToken

  let url = targetUrl
  if (accessToken) {
    const separator = url.includes('?') ? '&' : '?'
    url = `${url}${separator}accessToken=${encodeURIComponent(accessToken)}`
  }

  console.log('[jump] H5 打开地址:', url)

  uni.navigateTo({
    url: `/pages/h5/webview/index?url=${encodeURIComponent(url)}&title=${encodeURIComponent(title || '详情')}`,
  })
}

/**
 * 统一跳转：仅当参数以 `h5:`、`http://`、`https://` 开头时打开 H5 webview；否则按小程序内路径跳转。
 * 站内路径若 `requireLogin` 或位于需登录名单，未登录时在当前页弹出登录弹窗，成功后继续本次跳转或刷新当前页。
 */
export function jump(pathOrUrl: string, title = '详情', options?: IJumpOptions) {
  const raw = pathOrUrl?.trim()
  if (!raw) {
    uni.showToast({
      title: '跳转地址不能为空',
      icon: 'none',
    })
    return
  }

  if (isHttpUrl(raw)) {
    openWebviewWithToken(raw, title)
    return
  }

  if (/^h5:/i.test(raw)) {
    const remainder = raw.replace(/^h5:/i, '').trim() || '/'
    let targetUrl = remainder
    if (!isHttpUrl(remainder)) {
      const h5BaseUrl = import.meta.env.VITE_H5_JUMP_BASE_URL?.trim()
      if (!h5BaseUrl) {
        uni.showToast({
          title: '未配置H5地址前缀',
          icon: 'none',
        })
        return
      }
      targetUrl = joinUrl(h5BaseUrl, remainder.startsWith('/') ? remainder : `/${remainder}`)
    }
    openWebviewWithToken(targetUrl, title)
    return
  }

  const internalPath = normalizeInternalPath(raw)
  const pathOnly = internalPath.split('?')[0]

  if (!options?._skipLoginCheck) {
    const tokenStore = useTokenStore()
    const hasLogin = tokenStore.updateNowTime().hasLogin
    const needsAuth = options?.requireLogin === true || isNeedLoginPath(pathOnly)
    if (needsAuth && !hasLogin) {
      useLoginModalStore().open({
        pathOrUrl: raw,
        title,
        options: options ? { ...options } : undefined,
      })
      return
    }
  }

  if (isTabBarConfiguredPath(pathOnly)) {
    openTabBarPage(internalPath)
    return
  }

  if (options?.replace) {
    uni.redirectTo({ url: internalPath })
  }
  else {
    uni.navigateTo({ url: internalPath })
  }
}

/**
 * 历史上走 H5 webview 的配置链接：非小程序页、非 http(s)、未带 h5: 时，视为 H5 相对路径并补上 `h5:`（兼容 CMS / 消息等动态链接）。
 */
export function toH5JumpableLink(link: string): string {
  const t = String(link || '').trim()
  if (!t)
    return t
  if (t.startsWith('/pages-sub/legal/'))
    return t.replace(/^\/pages-sub\/legal\//, '/pages/legal/')
  if (t.startsWith('/pages-sub/'))
    return t
  if (t.startsWith('/pages/h5/webview'))
    return t
  if (t.startsWith('/pages/') && !t.startsWith('/pages/h5/') && t !== '/pages/h5')
    return t
  if (isHttpUrl(t) || /^h5:/i.test(t))
    return t
  if (t.startsWith('/'))
    return `h5:${t}`
  return `h5:/${t}`
}
