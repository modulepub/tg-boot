/// <reference types="vite/client" />
/// <reference types="vite-svg-loader" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'

  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  /** 网站标题，应用名称 */
  readonly VITE_APP_TITLE: string
  /** H5 入口 index.html 的初始标签标题（优先于 VITE_APP_TITLE） */
  readonly VITE_HTML_TITLE?: string
  /** 展示用版本号（与 manifest versionName 保持一致，小程序线上版优先用平台返回） */
  readonly VITE_APP_VERSION?: string
  /** 服务端口号 */
  readonly VITE_SERVER_PORT: string
  /** 后台接口地址 */
  readonly VITE_SERVER_BASEURL: string
  /** H5跳转地址前缀 */
  readonly VITE_H5_JUMP_BASE_URL: string
  /** 第三方实名跳转地址（与小程序端 VITE_TENCENT_REALNAME_URL 对齐） */
  readonly VITE_TENCENT_REALNAME_URL?: string
  /** 联系我们：客服热线 */
  readonly VITE_CONTACT_HOTLINE?: string
  /** 联系我们：邮箱 */
  readonly VITE_CONTACT_EMAIL?: string
  /** 联系我们：服务时间说明 */
  readonly VITE_CONTACT_HOURS?: string
  /** H5是否需要代理 */
  readonly VITE_APP_PROXY_ENABLE: 'true' | 'false'
  /** H5是否需要代理，需要的话有个前缀 */
  readonly VITE_APP_PROXY_PREFIX: string
  /** 后端是否有统一前缀 /api */
  readonly VITE_SERVER_HAS_API_PREFIX: 'true' | 'false'
  /** 认证模式，'single' | 'double' ==> 单token | 双token */
  readonly VITE_AUTH_MODE: 'single' | 'double'
  /** 是否清除console */
  readonly VITE_DELETE_CONSOLE: string
  // 更多环境变量...
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare const __VITE_APP_PROXY__: 'true' | 'false'
