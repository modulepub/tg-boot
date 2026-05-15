/**
 * 从嵌入在小程序 web-view / uni-app App web-view 中的 H5，
 * 跳转回客户端（matchmaker-app）实名页面并关闭当前 web-view 栈页。
 *
 * 说明：微信 web-view 内默认不会注入可用的 `wx.miniProgram`，需加载官方 JSSDK 后才有跳转 API。
 * @see https://developers.weixin.qq.com/miniprogram/dev/component/web-view.html
 */

export const APP_REALNAME_PAGE_PATH = '/pages-sub/dating/customer/realname'

const UNI_WEBVIEW_JS = 'https://js.cdn.aliyun.dcloud.net.cn/dev/uni-app/uni.webview.1.5.6.js'
/** 微信客户端 web-view 内需引入后才有 wx.miniProgram */
const WX_JSSDK_URL = 'https://res.wx.qq.com/open/js/jweixin-1.6.0.js'

interface IWxMiniProgram {
  redirectTo?: (opts: { url: string }) => void
  navigateTo?: (opts: { url: string }) => void
}

function buildRealnameUrl(name: string, idCard: string): string {
  const params = new URLSearchParams()
  const n = name.trim()
  const id = idCard.trim()
  if (n)
    params.set('name', n)
  if (id)
    params.set('idCard', id)
  const qs = params.toString()
  return qs ? `${APP_REALNAME_PAGE_PATH}?${qs}` : APP_REALNAME_PAGE_PATH
}

function loadScript(src: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const existed = document.querySelector(`script[src="${src}"]`)
    if (existed) {
      resolve()
      return
    }
    const s = document.createElement('script')
    s.src = src
    s.async = true
    s.onload = () => resolve()
    s.onerror = () => reject(new Error(`Failed to load ${src}`))
    document.head.appendChild(s)
  })
}

function pickWxMiniProgram(): IWxMiniProgram | undefined {
  const wxObj = (window as unknown as { wx?: { miniProgram?: IWxMiniProgram } }).wx
  const mp = wxObj?.miniProgram
  if (mp && (typeof mp.redirectTo === 'function' || typeof mp.navigateTo === 'function'))
    return mp
  return undefined
}

/**
 * 等待微信注入 wx.miniProgram（多数场景需先加载 jweixin-1.6.0.js）
 */
async function ensureWxMiniProgramBridge(): Promise<IWxMiniProgram | undefined> {
  let mp = pickWxMiniProgram()
  if (mp)
    return mp

  try {
    await loadScript(WX_JSSDK_URL)
  }
  catch {
    // 离线 / 拦截广告脚本等
  }

  for (let i = 0; i < 60; i++) {
    mp = pickWxMiniProgram()
    if (mp)
      return mp
    await new Promise(r => setTimeout(r, 50))
  }

  return pickWxMiniProgram()
}

function tryOpenViaWxMiniProgram(mp: IWxMiniProgram, url: string): boolean {
  if (typeof mp.redirectTo === 'function') {
    try {
      mp.redirectTo({ url })
      return true
    }
    catch {
      // 开发者工具部分版本 redirectTo 异常时可换 navigateTo
    }
  }
  if (typeof mp.navigateTo === 'function') {
    try {
      mp.navigateTo({ url })
      return true
    }
    catch {
      return false
    }
  }
  return false
}

function runWhenUniBridgeReady(onReady: () => void) {
  const g = window as unknown as { UniAppJSBridge?: unknown }
  if (g.UniAppJSBridge)
    onReady()
  else
    document.addEventListener('UniAppJSBridgeReady', onReady, { once: true })
}

/**
 * @returns 是否已成功交给客户端接管跳转（为 true 时不应再执行 H5 兜底跳转）
 */
export async function openNativeRealnamePage(name: string, idCard: string): Promise<boolean> {
  const url = buildRealnameUrl(name, idCard)

  const wxMp = await ensureWxMiniProgramBridge()
  if (wxMp && tryOpenViaWxMiniProgram(wxMp, url))
    return true

  try {
    await loadScript(UNI_WEBVIEW_JS)
    await new Promise<void>((resolve, reject) => {
      const t = setTimeout(() => reject(new Error('UniAppJSBridge timeout')), 8000)
      runWhenUniBridgeReady(() => {
        clearTimeout(t)
        resolve()
      })
    })
    const uniGlobal = (window as unknown as {
      uni?: { redirectTo?: (opts: { url: string }) => void; navigateTo?: (opts: { url: string }) => void }
    }).uni
    if (typeof uniGlobal?.redirectTo === 'function') {
      try {
        uniGlobal.redirectTo({ url })
        return true
      }
      catch {
        /* empty */
      }
    }
    if (typeof uniGlobal?.navigateTo === 'function') {
      try {
        uniGlobal.navigateTo({ url })
        return true
      }
      catch {
        /* empty */
      }
    }
  }
  catch {
    // 非 App web-view 或未注入桥
  }

  return false
}
