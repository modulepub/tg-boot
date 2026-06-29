/**
 * 重新加载当前页（保持 path + query），用于登录成功后刷新 onLoad 数据
 */
export function reloadCurrentPage() {
  const pages = getCurrentPages()
  if (!pages.length)
    return
  const cur = pages[pages.length - 1] as any
  const route = String(cur.route || '').trim()
  if (!route)
    return
  const path = route.startsWith('/') ? route : `/${route}`
  const opts = (cur.options || {}) as Record<string, string>
  const q = Object.keys(opts)
    .map(k => `${k}=${encodeURIComponent(opts[k] ?? '')}`)
    .join('&')
  const url = q ? `${path}?${q}` : path
  uni.redirectTo({
    url,
    fail: () => uni.reLaunch({ url }),
  })
}
