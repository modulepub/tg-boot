/**
 * 通用接口约定：code 为字符串；说明文案由后端字段 **`message`** 返回。
 * - "0"：成功（兼容历史数字 0 / 200）
 * - "1"：校验失败，全局 toast 展示 `message`
 * - "2"：其他异常，全局 toast 展示 `message`
 * - 其余：不透传 toast，由业务 catch 根据 raw 处理
 */

export function normalizeBizCode(code: unknown): string {
  if (code === null || code === undefined)
    return ''
  return String(code).trim()
}

/** 业务成功："0"；兼容旧版数字 0、200 及字符串 "200" */
export function isBizSuccess(code: unknown): boolean {
  const s = normalizeBizCode(code)
  return s === '0' || s === '200'
}

/** 全局 toast 类错误（校验失败 / 通用异常） */
export function isBizGlobalMessageCode(code: unknown): boolean {
  const s = normalizeBizCode(code)
  return s === '1' || s === '2'
}

/** 读取后端 `message`（优先）；其余字段仅兜底 */
export function pickBizMessage(payload: Record<string, any> | null | undefined): string {
  if (!payload)
    return '请求错误'
  const keys = ['message', 'msg', 'errorMessage', 'resultMsg'] as const
  for (const k of keys) {
    const text = String(payload[k] ?? '').trim()
    if (text)
      return text
  }
  return '请求错误'
}

/** 是否未授权（HTTP 401 或业务码 401） */
export function isUnauthorizedBizCode(code: unknown): boolean {
  const s = normalizeBizCode(code)
  return s === '401' || code === 401
}
