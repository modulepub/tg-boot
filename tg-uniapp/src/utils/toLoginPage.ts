import { debounce } from '@/utils/debounce'
import { useLoginModalStore } from '@/store/loginModal'
import type { IPendingJump } from '@/store/loginModal'

interface ToLoginPageOptions {
  mode?: 'navigateTo' | 'reLaunch'
  queryString?: string
}

/**
 * 打开登录弹窗（不再跳转独立登录页）。
 * 带 `?redirect=` 时，登录成功后自动执行该跳转；否则登录成功后刷新当前页面。
 */
export const toLoginPage = debounce((options: ToLoginPageOptions = {}) => {
  const { queryString = '' } = options
  let pending: IPendingJump | null = null
  const m = queryString.match(/[?&]redirect=([^&]+)/)
  if (m?.[1]) {
    try {
      const pathOrUrl = decodeURIComponent(m[1])
      pending = { pathOrUrl, title: '详情' }
    }
    catch {
      /* empty */
    }
  }
  useLoginModalStore().open(pending)
}, 500)
