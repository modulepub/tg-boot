import type { IUserInfoRes } from '@/api/types/login'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getUserInfo,
} from '@/api/login'

// 初始化状态
const userInfoState: IUserInfoRes = {
  userId: -1,
  username: '',
  nickname: '',
  avatar: '/static/avatar-default.svg',
}

export const useUserStore = defineStore(
  'user',
  () => {
    // 定义用户信息
    const userInfo = ref<IUserInfoRes>({ ...userInfoState })
    // 设置用户信息
    const normalizeUserInfo = (val: IUserInfoRes | Record<string, any>): IUserInfoRes => {
      const raw = val as Record<string, any>
      const normalizedUserCode = String(raw.userCode ?? raw.user_code ?? raw.code ?? '').trim()
      return {
        ...raw,
        userCode: normalizedUserCode,
        userId: Number(raw.userId ?? raw.id ?? -1),
        username: String(raw.username ?? raw.userName ?? raw.userPhone ?? ''),
        nickname: String(raw.nickname ?? raw.userNickName ?? raw.userRealName ?? raw.userName ?? ''),
        avatar: String(raw.avatar ?? raw.userAvatar ?? userInfoState.avatar),
      }
    }

    const setUserInfo = (val: IUserInfoRes | Record<string, any>) => {
      console.log('设置用户信息', val)
      userInfo.value = normalizeUserInfo(val)
    }
    const setUserAvatar = (avatar: string) => {
      userInfo.value.avatar = avatar
      console.log('设置用户头像', avatar)
      console.log('userInfo', userInfo.value)
    }
    // 删除用户信息
    const clearUserInfo = () => {
      userInfo.value = { ...userInfoState }
      uni.removeStorageSync('user')
    }

    /**
     * 获取用户信息
     */
    const fetchUserInfo = async () => {
      const res = await getUserInfo()
      setUserInfo(res)
      return res
    }

    return {
      userInfo,
      clearUserInfo,
      fetchUserInfo,
      setUserInfo,
      setUserAvatar,
    }
  },
  {
    persist: true,
  },
)
