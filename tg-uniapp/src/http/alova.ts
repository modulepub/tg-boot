import type { uniappRequestAdapter } from '@alova/adapter-uniapp'
import type { IResponse } from './types'
import AdapterUniapp from '@alova/adapter-uniapp'
import { createAlova } from 'alova'
import { createServerTokenAuthentication } from 'alova/client'
import VueHook from 'alova/vue'
import { toLoginPage } from '@/utils/toLoginPage'
import { isBizGlobalMessageCode, isBizSuccess, pickBizMessage } from './tools/bizResponse'
import { ContentTypeEnum, ResultEnum, ShowMessage } from './tools/enum'

// 配置动态Tag
export const API_DOMAINS = {
  DEFAULT: import.meta.env.VITE_SERVER_BASEURL,
  SECONDARY: import.meta.env.VITE_SERVER_BASEURL_SECONDARY,
}

/**
 * 创建请求实例
 */
const { onAuthRequired, onResponseRefreshToken } = createServerTokenAuthentication<
  typeof VueHook,
  typeof uniappRequestAdapter
>({
  // 如果下面拦截不到，请使用 refreshTokenOnSuccess by 群友@琛
  refreshTokenOnError: {
    isExpired: (error) => {
      return error.response?.status === ResultEnum.Unauthorized
    },
    handler: async () => {
      try {
        // await authLogin();
      }
      catch (error) {
        // 切换到登录页
        toLoginPage({ mode: 'reLaunch' })
        throw error
      }
    },
  },
})

/**
 * alova 请求实例
 */
const alovaInstance = createAlova({
  baseURL: API_DOMAINS.DEFAULT,
  ...AdapterUniapp(),
  timeout: 5000,
  statesHook: VueHook,

  beforeRequest: onAuthRequired((method) => {
    // 设置默认 Content-Type
    method.config.headers = {
      ContentType: ContentTypeEnum.JSON,
      Accept: 'application/json, text/plain, */*',
      ...method.config.headers,
    }

    const { config } = method
    const ignoreAuth = !config.meta?.ignoreAuth
    console.log('ignoreAuth===>', ignoreAuth)
    // 处理认证信息   自行处理认证问题
    if (ignoreAuth) {
      const token = 'getToken()'
      if (!token) {
        throw new Error('[请求错误]：未登录')
      }
      // method.config.headers.token = token;
    }

    // 处理动态域名
    if (config.meta?.domain) {
      method.baseURL = config.meta.domain
      console.log('当前域名', method.baseURL)
    }
  }),

  responded: onResponseRefreshToken((response, method) => {
    const { config } = method
    const { requestType } = config
    const {
      statusCode,
      data: rawData,
      errMsg,
    } = response as UniNamespace.RequestSuccessCallbackResult

    // 处理特殊请求类型（上传/下载）
    if (requestType === 'upload' || requestType === 'download') {
      return response
    }

    // 处理 HTTP 状态码错误
    if (statusCode !== 200) {
      const errorMessage = ShowMessage(statusCode) || `HTTP请求错误[${statusCode}]`
      console.error('errorMessage===>', errorMessage)
      uni.showToast({
        title: errorMessage,
        icon: 'error',
      })
      throw new Error(`${errorMessage}：${errMsg}`)
    }

    const raw = rawData as IResponse & Record<string, any>
    const { code, data } = raw

    if (isBizSuccess(code)) {
      return data
    }

    const msg = pickBizMessage(raw)
    const codeStr = String(code ?? '').trim()

    if (isBizGlobalMessageCode(code)) {
      if (config.meta?.toast !== false) {
        uni.showToast({
          title: msg,
          icon: 'none',
        })
      }
      throw Object.assign(new Error(`请求错误[${codeStr}]：${msg}`), { rawResponse: raw })
    }

    throw Object.assign(new Error(`请求错误[${codeStr}]：${msg}`), { rawResponse: raw })
  }),
})

export const http = alovaInstance
