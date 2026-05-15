/** jump / 登录弹窗共用的跳转选项，避免 store 与 jump 循环依赖 */
export interface IJumpOptions {
  /** 小程序内路径跳转时使用 redirectTo */
  replace?: boolean
  /** 为 true 时未登录先弹登录，成功后再执行本次跳转 */
  requireLogin?: boolean
  /** 内部使用：已完成登录校验后再跳转，避免重复弹窗 */
  _skipLoginCheck?: boolean
}
