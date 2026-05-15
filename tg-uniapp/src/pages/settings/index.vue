<template>
  <view class="settings-page">
    <TechBackdrop class="settings-tech" />
    <SiteHeaderBar />
    <scroll-view scroll-y class="settings-scroll" :show-scrollbar="false" enable-flex>
      <view class="settings-shell settings-scroll-inner">
        <view class="card">
          <view class="item" @click="openPrivacyPolicy">
            <text class="item-title">隐私协议</text>
            <text class="arrow">›</text>
          </view>
        </view>

        <view class="card">
          <view
            class="item center-item account-action"
            :class="{ 'account-action--busy': logoutSubmitting }"
            hover-class="account-action--hover"
            :hover-stay-time="120"
            @click="handleLogout"
          >
            <text class="item-title danger">
              {{ logoutSubmitting ? '退出中…' : '退出登录' }}
            </text>
          </view>
        </view>

        <view class="version-foot">
          <text class="version-foot__brand">{{ appTitle }}</text>
          <text class="version-foot__sep">·</text>
          <text class="version-foot__ver">v{{ appVersion }}</text>
        </view>

        <view class="safe-bottom" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import { useTokenStore } from '@/store/token'
import { getDisplayedAppVersion } from '@/utils/appVersion'
import { openPrivacyPolicy as navigatePrivacy } from '@/utils/legalPages'
import TechBackdrop from '@/components/login/TechBackdrop.vue'
import SiteHeaderBar from '@/components/site/SiteHeaderBar.vue'

defineOptions({
  name: 'SettingsPage',
})

definePage({
  needLogin: true,
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '',
    'navigationBarBackgroundColor': '#0b1220',
    'backgroundColor': '#0b1220',
    'mp-alipay': {
      defaultTitle: '',
      transparentTitle: 'always',
      titlePenetrate: 'YES',
      titleBarColor: '#ffffff',
    },
  },
})

const tokenStore = useTokenStore()
const logoutSubmitting = ref(false)

const appTitle = String(import.meta.env.VITE_APP_TITLE || '卿卿').trim() || '卿卿'
const appVersion = ref(getDisplayedAppVersion())

onShow(() => {
  appVersion.value = getDisplayedAppVersion()
})

function openPrivacyPolicy() {
  navigatePrivacy()
}

async function handleLogout() {
  if (!tokenStore.updateNowTime().hasLogin) {
    uni.showToast({ title: '当前未登录', icon: 'none' })
    return
  }
  if (logoutSubmitting.value)
    return

  const res = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      confirmText: '退出',
      cancelText: '取消',
      success: (r) => {
        resolve(Boolean(r.confirm))
      },
      fail: () => resolve(false),
    })
  })
  if (!res)
    return

  logoutSubmitting.value = true
  uni.showLoading({ title: '退出中…', mask: true })
  try {
    await tokenStore.logout()
    uni.showToast({ title: '已退出', icon: 'none' })
    uni.reLaunch({ url: '/pages/me/me' })
  }
  finally {
    logoutSubmitting.value = false
    uni.hideLoading()
  }
}
</script>

<style lang="scss" scoped>
.settings-page {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background: #0b1220;
}

.settings-tech {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.settings-scroll {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  background: transparent;
}

.settings-shell {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}

.settings-scroll-inner {
  padding: 12px 16px 0;
}

@media (min-width: 768px) {
  .settings-scroll-inner {
    padding-left: 32px;
    padding-right: 32px;
  }
}

.card {
  margin-bottom: 20rpx;
  background: #ffffff;
  border-radius: 22rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 24rpx rgba(179, 91, 133, 0.08);
}

.item {
  height: 96rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-title {
  color: #2d3650;
  font-size: 29rpx;
}

.center-item {
  justify-content: center;
}

.account-action {
  transition: opacity 0.15s ease;
}

.account-action--busy .item-title {
  animation: account-logout-pulse 0.9s ease-in-out infinite;
}

@keyframes account-logout-pulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.45;
  }
}

.danger {
  color: #e11d68;
}

.arrow {
  color: #c0a0b1;
  font-size: 34rpx;
  font-weight: 500;
  line-height: 1;
}

.version-foot {
  margin-top: 48rpx;
  padding-bottom: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.version-foot__brand {
  font-size: 22rpx;
  font-weight: 500;
  letter-spacing: 0.12em;
  color: rgba(251, 207, 232, 0.75);
}

.version-foot__sep {
  font-size: 20rpx;
  color: rgba(148, 163, 184, 0.5);
}

.version-foot__ver {
  font-size: 22rpx;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.06em;
  color: rgba(148, 163, 184, 0.85);
}

.safe-bottom {
  height: calc(env(safe-area-inset-bottom) + 24px);
}
</style>

<style lang="scss">
.account-action--hover {
  opacity: 0.88;
}
</style>
