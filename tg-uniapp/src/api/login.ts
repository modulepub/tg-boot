import type { IAuthLoginRes, ICaptcha, ICustomerInfoRes, IDoubleTokenRes, IUpdateInfo, IUpdatePassword, IUserInfoRes } from './types/login'
import { http } from '@/http/http'

/**
 * 手机号验证码登录表单
 */
export interface ILoginForm {
  phone: string
  smsAuthCode: string
  source?: string
}

/**
 * 获取验证码
 * @returns ICaptcha 验证码
 */
export function getCode() {
  return http.get<ICaptcha>('/user/getCode')
}

/**
 * 发送短信验证码
 */
export function sendSmsCode(data: { phone: string, code: string, captchaKey: string }) {
  return http.post<void>('/pub/auth/sendSms', data)
}

/**
 * 用户登录
 * @param loginForm 登录表单
 */
export function login(loginForm: ILoginForm) {
  /** 错误文案由业务层 toast，避免与 tokenStore 重复弹窗 */
  return http.post<IAuthLoginRes>(
    '/pub/auth/phoneLogin',
    loginForm,
    undefined,
    undefined,
    { hideErrorToast: true },
  )
}

/**
 * 刷新token
 */
export function refreshToken() {
  return http.post<IDoubleTokenRes>('/cus/sysUser/refreshToken')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return http.get<IUserInfoRes>('/cus/sysUser/getInfo')
}

/**
 * 获取当前客户信息
 */
export function getCurrCusInfo() {
  return http.get<ICustomerInfoRes>('/cus/customer/getCurrCusInfo')
}

/**
 * 退出登录
 */
export function logout() {
  return http.post<void>('/cus/sysUser/logout')
}

/**
 * 修改用户信息
 */
export function updateInfo(data: IUpdateInfo) {
  return http.post('/cus/sysUser/editUserInfo', data)
}

/**
 * 修改用户密码
 */
export function updateUserPassword(data: IUpdatePassword) {
  return http.post('/user/updatePassword', data)
}

/**
 * 获取微信登录凭证
 * @returns Promise 包含微信登录凭证(code)
 */
export function getWxCode() {
  return new Promise<UniApp.LoginRes>((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: res => resolve(res),
      fail: err => reject(new Error(err)),
    })
  })
}

/**
 * 微信登录
 * @param params 微信登录参数，包含code
 * @returns Promise 包含登录结果
 */
export function wxLogin(data: { code: string }) {
  return http.post<IAuthLoginRes>(
    '/auth/wxLogin',
    data,
    undefined,
    undefined,
    { hideErrorToast: true },
  )
}

/**
 * 小程序 code 换取微信身份信息（openId）
 */
export function wxMaLoginByCode(data: { appId: string, code: string }) {
  return http.post<IUserInfoRes>('/pub/wxMaLogin', data)
}
