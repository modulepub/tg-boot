<template>
  <view class="me-page">
    <TechBackdrop class="me-tech" />
    <SiteHeaderBar />
    <scroll-view scroll-y class="me-scroll" :show-scrollbar="false" enable-flex>
      <view class="me-shell me-scroll-inner">
    <view class="profile-card">
      <image class="avatar" :src="avatarUrl" mode="aspectFill" @click="handleAvatarClick" />
      <view class="user-main">
        <text class="nickname" :class="!hasLogin ? 'login-link' : ''" @click="handleNicknameClick">
          {{ hasLogin ? displayName : '去登录' }}
        </text>
        <text class="intro">
          {{ hasLogin ? '欢迎回来，今天也要元气满满。' : '登录后可同步你的专属服务和权益。' }}
        </text>
      </view>
    </view>

    <view class="section-card section-stat">
      <view class="stat-grid">
        <view class="stat-item stat-item-clickable" @click="openRecommendList">
          <text class="stat-value">{{ myStatistic.recommendTotal }}</text>
          <text class="stat-label">推荐</text>
        </view>
        <view class="stat-item stat-item-clickable" @click="openLikeMeList">
          <text class="stat-value">{{ myStatistic.likeMeTotal }}</text>
          <text class="stat-label">喜欢我</text>
        </view>
        <view class="stat-item stat-item-clickable" @click="openMyLikeList">
          <text class="stat-value">{{ myStatistic.meLikeTotal }}</text>
          <text class="stat-label">我喜欢</text>
        </view>
        <view class="stat-item stat-item-clickable" @click="openMatchList">
          <text class="stat-value">{{ myStatistic.friendTotal }}</text>
          <text class="stat-label">好友</text>
        </view>
      
      </view>
    </view>

    <view class="section-card">
      <view class="section-title">
        服务中心
      </view>
      <view class="service-list">
        <view v-for="item in serviceList" :key="item.title" class="service-item" @click="handleClick(item.title)">
          <view class="service-left">
            <text class="service-dot" />
            <text class="service-title">{{ item.title }}</text>
          </view>
          <text class="service-arrow">›</text>
        </view>
      </view>
    </view>
        <view class="safe-bottom" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'MePage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '',
    'mp-alipay': {
      defaultTitle: '',
      transparentTitle: 'always',
      titlePenetrate: 'YES',
      titleBarColor: '#ffffff',
    },
  },
})

import { useCustomerStore } from '@/store/customer'
import { useTokenStore } from '@/store/token'
import { useUserStore } from '@/store/user'
import { http } from '@/http/http'
import { jump } from '@/utils/jump'
import { toLoginPage } from '@/utils/toLoginPage'
import TechBackdrop from '@/components/login/TechBackdrop.vue'
import SiteHeaderBar from '@/components/site/SiteHeaderBar.vue'

interface IMeActionItem {
  title: string
  icon: string
}

interface IMyStatistic {
  likeMeTotal: number
  meLikeTotal: number
  friendTotal: number
  recommendTotal: number
}

const tokenStore = useTokenStore()
const userStore = useUserStore()
const customerStore = useCustomerStore()

const serviceList: IMeActionItem[] = [
  { title: '会员中心', icon: '' },
  { title: '我的顾问', icon: '' },
  { title: '邀请好友', icon: '' },
  { title: '服务中心', icon: '' },
  { title: '设置', icon: '' },
]

const myStatistic = ref<IMyStatistic>({
  likeMeTotal: 0,
  meLikeTotal: 0,
  friendTotal: 0,
  recommendTotal: 0,
})

const avatarUrl = computed(() => userStore.userInfo.avatar || '/static/avatar-default.svg')
const displayName = computed(() => userStore.userInfo.nickname || userStore.userInfo.username || '未命名用户')
const hasLogin = computed(() => tokenStore.updateNowTime().hasLogin)

const handleAvatarClick = () => {
  jump('/pages/h5/intake-profile', '资料', { requireLogin: true })
}

const handleNicknameClick = () => {
  if (hasLogin.value) {
    return
  }
  toLoginPage()
}

const openMatchList = () => {
  jump('/pages-sub/dating/customer/friend-list', '好友', { requireLogin: true })
}

const openRecommendList = () => {
  jump('/pages/me/recommend-list', '推荐列表', { requireLogin: true })
}

const openLikeMeList = () => {
  jump('/pages/me/liked-me-list', '喜欢我', { requireLogin: true })
}

const openMyLikeList = () => {
  jump('/pages/me/my-like-list', '我喜欢', { requireLogin: true })
}

const handleClick = (title: string) => {
  if (title === '会员中心') {
    jump('/pages-sub/dating/customer/benefits', '会员中心', { requireLogin: true })
    return
  }

  if (title === '我的顾问') {
    jump('/pages-sub/dating/matchmaker/my-list', '我的顾问', { requireLogin: true })
    return
  }

  if (title === '邀请好友') {
    jump('/pages-sub/dating/invite/promote', '邀请好友', { requireLogin: true })
    return
  }

  if (title === '设置') {
    jump('/pages/settings/index', '设置')
    return
  }

  if (title === '服务中心') {
    jump('h5:/matchmaker', '服务中心')
    return
  }

  uni.showToast({
    title: `${title} 开发中`,
    icon: 'none',
  })
}

const refreshUserInfo = async () => {
  if (!hasLogin.value) {
    return
  }
  try {
    await Promise.all([
      userStore.fetchUserInfo(),
      customerStore.fetchCustomerInfo(),
    ])
  }
  catch (error) {
    console.error('刷新用户/客户信息失败:', error)
  }
}

function resetMyStatistic() {
  myStatistic.value = {
    likeMeTotal: 0,
    meLikeTotal: 0,
    friendTotal: 0,
    recommendTotal: 0,
  }
}

async function fetchMyStatistic() {
  if (!hasLogin.value) {
    resetMyStatistic()
    return
  }

  try {
    const data = await http.get<IMyStatistic>('/cus/dating/statistic/myStatistic')
    myStatistic.value = {
      likeMeTotal: Number(data?.likeMeTotal ?? 0),
      meLikeTotal: Number(data?.meLikeTotal ?? 0),
      friendTotal: Number(data?.friendTotal ?? 0),
      recommendTotal: Number(data?.recommendTotal ?? 0),
    }
  }
  catch {
    resetMyStatistic()
  }
}

onShow(() => {
  void refreshUserInfo()
  void fetchMyStatistic()
})

</script>

<style lang="scss" scoped>
.me-page {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background: #0b1220;
}

.me-tech {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.me-scroll {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  background: transparent;
}

.me-shell {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}

.me-scroll-inner {
  padding: 12px 16px 0;
}

@media (min-width: 768px) {
  .me-scroll-inner {
    padding-left: 32px;
    padding-right: 32px;
  }
}

.profile-card,
.section-card {
  position: relative;
  z-index: 1;
}

.profile-card {
  margin-top: 0;
  padding: 30rpx;
  border-radius: 24rpx;
  background: #ffffff;
  box-shadow: 0 18rpx 40rpx rgba(44, 62, 111, 0.12);
  display: flex;
  align-items: center;
}

.avatar {
  width: 108rpx;
  height: 108rpx;
  border-radius: 50%;
  border: 4rpx solid #eef1ff;
  flex-shrink: 0;
}

.user-main {
  margin-left: 22rpx;
  flex: 1;
  min-width: 0;
}

.nickname {
  display: block;
  color: #1f2a44;
  font-size: 34rpx;
  font-weight: 600;
}

.nickname.login-link {
  color: #5e72f4;
}

.intro {
  display: block;
  margin-top: 10rpx;
  color: #7d879b;
  font-size: 24rpx;
  line-height: 1.45;
}

.section-card {
  margin-top: 24rpx;
  padding: 24rpx;
  border-radius: 24rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 32rpx rgba(43, 56, 84, 0.08);
}

.section-title {
  color: #2a3247;
  font-size: 30rpx;
  font-weight: 600;
  margin-bottom: 18rpx;
}

.section-stat {
  padding-top: 18rpx;
  padding-bottom: 18rpx;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 0;
  text-align: center;
}

.stat-item-clickable {
  cursor: pointer;
}

.stat-value {
  display: block;
  font-size: 42rpx;
  line-height: 1.1;
  color: #1f2a44;
  font-weight: 600;
}

.stat-label {
  display: block;
  margin-top: 10rpx;
  color: #6f7690;
  font-size: 24rpx;
  white-space: nowrap;
}

.service-list {
  display: flex;
  flex-direction: column;
}

.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18rpx 2rpx;
  border-top: 1rpx solid #eef1f6;
}

.service-item:first-child {
  border-top: none;
}

.service-left {
  display: flex;
  align-items: center;
  min-width: 0;
}

.service-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #6c7bff;
  margin-right: 16rpx;
}

.service-title {
  color: #2d3650;
  font-size: 27rpx;
}

.service-arrow {
  color: #b8bfd1;
  font-size: 34rpx;
  font-weight: 500;
  line-height: 1;
}

@media (max-width: 360px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16rpx;
  }
}

@media (min-width: 768px) {
  .profile-card {
    padding: 28px 26px;
    border-radius: 18px;
  }

  .avatar {
    width: 88px;
    height: 88px;
  }

  .nickname {
    font-size: 22px;
  }

  .intro {
    font-size: 14px;
  }

  .section-card {
    padding: 22px 24px;
    border-radius: 18px;
  }

  .section-title {
    font-size: 17px;
  }

  .stat-value {
    font-size: 28px;
  }

  .stat-label {
    font-size: 13px;
  }

  .service-title {
    font-size: 16px;
  }
}

.safe-bottom {
  height: calc(env(safe-area-inset-bottom) + 24px);
}

</style>
