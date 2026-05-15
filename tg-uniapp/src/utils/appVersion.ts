/**
 * 界面展示用版本号：优先小程序线上包 / App 运行时版本，否则读 `VITE_APP_VERSION`（与 manifest versionName 对齐）。
 */
export function getDisplayedAppVersion(): string {
  const fallback = String(import.meta.env.VITE_APP_VERSION || '1.0.0').trim() || '1.0.0'

  try {
    if (typeof uni !== 'undefined' && typeof uni.getAccountInfoSync === 'function') {
      const acc = uni.getAccountInfoSync() as Record<string, any>
      const v = acc?.miniProgram?.version
      if (v && String(v).trim())
        return String(v).trim()
    }
  }
  catch {
    // ignore
  }

  try {
    const plusRuntime = (globalThis as any)?.plus?.runtime
    const v = plusRuntime?.version
    if (v && String(v).trim())
      return String(v).trim()
  }
  catch {
    // ignore
  }

  return fallback
}
