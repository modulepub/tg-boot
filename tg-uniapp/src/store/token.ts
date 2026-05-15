import type {
  ILoginForm,
} from '@/api/login'
import type { IAuthLoginRes, IDoubleTokenRes } from '@/api/types/login'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue' // 修复：导入 computed
import {
  login as _login,
  logout as _logout,
  refreshToken as _refreshToken,
  wxLogin as _wxLogin,
  getWxCode,
} from '@/api/login'
import { isDoubleTokenRes, isSingleTokenRes } from '@/api/types/login'
import { pickBizMessage } from '@/http/tools/bizResponse'
import { isDoubleTokenMode } from '@/utils'
import { useCustomerStore } from './customer'
import { useUserStore } from './user'

/** `http` reject 的通用响应包：`{ code, message, data }`，此处统一弹出后端 message */
function toastAuthFailure(error: unknown, fallback: string) {
  if (error && typeof error === 'object' && !Array.isArray(error)) {
    const rec = error as Record<string, unknown>
    if ('code' in rec || 'message' in rec || 'msg' in rec) {
      const text = pickBizMessage(rec as Record<string, any>)
      if (text !== '请求错误') {
        uni.showToast({ title: text, icon: 'none' })
        return
      }
    }
  }
  uni.showToast({ title: fallback, icon: 'none' })
}

// 初始化状态
const tokenInfoState = isDoubleTokenMode
  ? {
      accessToken: '',
      accessExpiresIn: 0,
      refreshToken: '',
      refreshExpiresIn: 0,
    }
  : {
      token: '',
      expiresIn: 0,
    }

export const useTokenStore = defineStore(
  'token',
  () => {
    // 定义用户信息
    const tokenInfo = ref<IAuthLoginRes>({ ...tokenInfoState })

    // 添加一个时间戳 ref 作为响应式依赖
    const nowTime = ref(Date.now())
    /**
     * 更新响应式数据:now
     * 确保isTokenExpired/isRefreshTokenExpired重新计算,而不是用错误过期缓存值
     * 可useTokenStore内部适时调用;也可链式调用:tokenStore.updateNowTime().hasLogin
     * @returns 最新的tokenStore实例
     */
    const updateNowTime = () => {
      nowTime.value = Date.now()
      return useTokenStore()
    }

    // 设置用户信息
    const normalizeTokenInfo = (val: IAuthLoginRes | Record<string, any>): IAuthLoginRes => {
      const parseExpireSeconds = (expireVal: unknown, now: number) => {
        const n = Number(expireVal || 0)
        if (!n || Number.isNaN(n))
          return 0
        // 秒级 duration
        if (n < 1e9)
          return n
        // 秒级时间戳
        if (n >= 1e9 && n < 1e12)
          return n > Math.floor(now / 1000) ? n - Math.floor(now / 1000) : 0
        // 毫秒级时间戳
        return n > now ? Math.floor((n - now) / 1000) : 0
      }

      const raw = val as Record<string, any>
      // 兼容双token返回（含 accessTokenExpire / refreshTokenExpire 毫秒时间戳）
      if (raw.accessToken && raw.refreshToken) {
        const now = Date.now()
        const accessExpiresIn = parseExpireSeconds(raw.accessExpiresIn || raw.accessTokenExpire, now)
        const refreshExpiresIn = parseExpireSeconds(raw.refreshExpiresIn || raw.refreshTokenExpire, now)
        return {
          accessToken: String(raw.accessToken),
          refreshToken: String(raw.refreshToken),
          accessExpiresIn: accessExpiresIn || 7200,
          refreshExpiresIn: refreshExpiresIn || 604800,
        }
      }
      // 兼容单token返回（token/accessToken + expiresIn/accessTokenExpire）
      if (raw.token || raw.accessToken) {
        const now = Date.now()
        const token = String(raw.token || raw.accessToken)
        // 优先读取秒级 expiresIn，其次兼容 tokenExpire/accessTokenExpire 的时间戳格式
        let expiresIn = parseExpireSeconds(raw.expiresIn || raw.tokenExpire || raw.accessTokenExpire, now)
        // 兜底给一个较短有效期，避免请求头完全丢失
        if (!expiresIn || Number.isNaN(expiresIn)) {
          expiresIn = 7200
        }
        return { token, expiresIn }
      }
      // 标准返回直接透传（放在兼容分支之后，避免被不完整结构提前命中）
      if (isSingleTokenRes(val as IAuthLoginRes) || isDoubleTokenRes(val as IAuthLoginRes)) {
        return val as IAuthLoginRes
      }
      return { ...tokenInfoState } as IAuthLoginRes
    }

    const setTokenInfo = (val: IAuthLoginRes | Record<string, any>) => {
      updateNowTime()
      const normalized = normalizeTokenInfo(val)
      tokenInfo.value = normalized

      // 计算并存储过期时间
      const now = Date.now()
      if (isSingleTokenRes(normalized)) {
        // 单token模式
        const expireTime = now + normalized.expiresIn * 1000
        uni.setStorageSync('accessTokenExpireTime', expireTime)
      }
      else if (isDoubleTokenRes(normalized)) {
        // 双token模式
        const accessExpireTime = now + normalized.accessExpiresIn * 1000
        const refreshExpireTime = now + normalized.refreshExpiresIn * 1000
        uni.setStorageSync('accessTokenExpireTime', accessExpireTime)
        uni.setStorageSync('refreshTokenExpireTime', refreshExpireTime)
      }
    }

    /**
     * 判断token是否过期
     */
    const isTokenExpired = computed(() => {
      if (!tokenInfo.value) {
        return true
      }

      const now = nowTime.value
      const expireTime = uni.getStorageSync('accessTokenExpireTime')

      if (!expireTime)
        return true
      return now >= expireTime
    })

    /**
     * 判断refreshToken是否过期
     */
    const isRefreshTokenExpired = computed(() => {
      if (!isDoubleTokenMode)
        return true

      const now = nowTime.value
      const refreshExpireTime = uni.getStorageSync('refreshTokenExpireTime')

      if (!refreshExpireTime)
        return true
      return now >= refreshExpireTime
    })

    /**
     * 登录成功后处理逻辑
     * @param tokenInfo 登录返回的token信息
     */
    async function _postLogin(tokenInfo: IAuthLoginRes) {
      setTokenInfo(tokenInfo)
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      const customerStore = useCustomerStore()
      await customerStore.fetchCustomerInfo()
    }

    /**
     * 用户登录
     * 有的时候后端会用一个接口返回token和用户信息，有的时候会分开2个接口，一个获取token，一个获取用户信息
     * （各有利弊，看业务场景和系统复杂度），这里使用2个接口返回的来模拟
     * @param loginForm 登录参数
     * @returns 登录结果
     */
    const login = async (loginForm: ILoginForm) => {
      try {
        const res = await _login(loginForm)
        console.log('普通登录-res: ', res)
        await _postLogin(res)
        uni.showToast({
          title: '登录成功',
          icon: 'success',
        })
        return res
      }
      catch (error) {
        console.error('登录失败:', error)
        toastAuthFailure(error, '登录失败，请重试')
        throw error
      }
      finally {
        updateNowTime()
      }
    }

    /**
     * 微信登录
     * 有的时候后端会用一个接口返回token和用户信息，有的时候会分开2个接口，一个获取token，一个获取用户信息
     * （各有利弊，看业务场景和系统复杂度），这里使用2个接口返回的来模拟
     * @returns 登录结果
     */
    const wxLogin = async () => {
      try {
        // 获取微信小程序登录的code
        const code = await getWxCode()
        console.log('微信登录-code: ', code)
        const res = await _wxLogin(code)
        console.log('微信登录-res: ', res)
        await _postLogin(res)
        uni.showToast({
          title: '登录成功',
          icon: 'success',
        })
        return res
      }
      catch (error) {
        console.error('微信登录失败:', error)
        toastAuthFailure(error, '微信登录失败，请重试')
        throw error
      }
      finally {
        updateNowTime()
      }
    }

    /**
     * 退出登录 并 删除用户信息
     */
    const clearAuthState = () => {
      updateNowTime()
      uni.removeStorageSync('accessTokenExpireTime')
      uni.removeStorageSync('refreshTokenExpireTime')
      tokenInfo.value = { ...tokenInfoState }
      uni.removeStorageSync('token')
      const userStore = useUserStore()
      userStore.clearUserInfo()
      const customerStore = useCustomerStore()
      customerStore.clearCustomerInfo()
    }

    const logout = async (withRequest = true) => {
      try {
        if (withRequest) {
          await _logout()
        }
      }
      catch (error) {
        console.error('退出登录失败:', error)
      }
      finally {
        clearAuthState()
      }
    }

    /**
     * 刷新token
     * @returns 刷新结果
     */
    const refreshToken = async () => {
      if (!isDoubleTokenMode) {
        console.error('单token模式不支持刷新token')
        throw new Error('单token模式不支持刷新token')
      }

      try {
        // 安全检查，确保refreshToken存在
        if (!isDoubleTokenRes(tokenInfo.value) || !tokenInfo.value.refreshToken) {
          throw new Error('无效的refreshToken')
        }

        const res = await _refreshToken()
        console.log('刷新token-res: ', res)
        setTokenInfo(res)
        return res
      }
      catch (error) {
        console.error('刷新token失败:', error)
        throw error
      }
      finally {
        updateNowTime()
      }
    }

    /**
     * 获取有效的token
     * 注意：在computed中不直接调用异步函数，只做状态判断
     * 实际的刷新操作应由调用方处理
     * 建议这样使用 tokenStore.updateNowTime().validToken
     */
    const getValidToken = computed(() => {
      // token已过期，返回空
      if (isTokenExpired.value) {
        return ''
      }

      if (isDoubleTokenRes(tokenInfo.value)) {
        return tokenInfo.value.accessToken
      }
      return isSingleTokenRes(tokenInfo.value) ? tokenInfo.value.token : ''
    })

    /**
     * 检查是否有登录信息（不考虑token是否过期）
     */
    const hasLoginInfo = computed(() => {
      if (!tokenInfo.value) {
        return false
      }
      if (isDoubleTokenRes(tokenInfo.value)) {
        return !!tokenInfo.value.accessToken
      }
      return isSingleTokenRes(tokenInfo.value) && !!tokenInfo.value.token
    })

    /**
     * 检查是否已登录且token有效
     * 建议这样使用tokenStore.updateNowTime().hasLogin
     */
    const hasValidLogin = computed(() => {
      console.log('hasValidLogin', hasLoginInfo.value, !isTokenExpired.value)
      return hasLoginInfo.value && !isTokenExpired.value
    })

    /**
     * 尝试获取有效的token，如果过期且可刷新，则刷新token
     * @returns 有效的token或空字符串
     */
    const tryGetValidToken = async (): Promise<string> => {
      updateNowTime()
      if (!getValidToken.value && isDoubleTokenMode && !isRefreshTokenExpired.value) {
        try {
          await refreshToken()
          return getValidToken.value
        }
        catch (error) {
          console.error('尝试刷新token失败:', error)
          return ''
        }
      }
      return getValidToken.value
    }

    return {
      // 核心API方法
      login,
      wxLogin,
      logout,
      clearAuthState,

      // 认证状态判断（最常用的）
      hasLogin: hasValidLogin,

      // 内部系统使用的方法
      refreshToken,
      tryGetValidToken,
      validToken: getValidToken,

      // 调试或特殊场景可能需要直接访问的信息
      tokenInfo,
      setTokenInfo,
      updateNowTime,
    }
  },
  {
    // 添加持久化配置，确保刷新页面后token信息不丢失
    persist: true,
  },
)
