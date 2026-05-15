<script lang="ts" setup>
import type { IMockActivity } from '@/mock/h5-activities'
import { getMockActivityById } from '@/mock/h5-activities'

defineOptions({
  name: 'H5ActivityDetailPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '活动详情',
    navigationBarBackgroundColor: '#fff5f8',
    backgroundColor: '#fff5f8',
  },
})

const activity = ref<IMockActivity | null>(null)

const navTitle = computed(() => {
  const t = String(activity.value?.title ?? '').trim()
  if (!t)
    return '活动详情'
  return t.length > 16 ? `${t.slice(0, 16)}…` : t
})

function goBack() {
  uni.navigateBack({
    fail: () => {
      uni.redirectTo({ url: '/pages/h5/activity-list' })
    },
  })
}

function onRegister() {
  if (!activity.value) {
    return
  }
  uni.showModal({
    title: '报名确认',
    content: `确认报名「${activity.value.title}」？（演示环境，仅为交互占位）`,
    confirmText: '确认报名',
    cancelText: '再想想',
    success(res) {
      if (res.confirm) {
        uni.showToast({
          title: '报名成功（演示）',
          icon: 'success',
        })
      }
    },
  })
}

onLoad((options) => {
  const id = options?.id ? decodeURIComponent(String(options.id)) : ''
  if (!id.trim()) {
    uni.showToast({ title: '缺少活动信息', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1200)
    return
  }
  const row = getMockActivityById(id.trim())
  activity.value = row ?? null
  if (!activity.value) {
    uni.showToast({ title: '活动不存在', icon: 'none' })
    setTimeout(() => uni.navigateBack(), 1200)
    return
  }
})
</script>

<template>
  <view class="wrap">
    <view class="nav-bar">
      <text class="nav-back" @click="goBack">返回</text>
      <text class="nav-title">{{ navTitle }}</text>
    </view>

    <view v-if="activity" class="main">
      <view class="hero">
        <text class="hero-title">{{ activity.title }}</text>
        <view class="hero-meta">
          <text class="pill">{{ activity.city }}</text>
          <text class="enrolled">已报名 {{ activity.enrolledCount }} 人</text>
        </view>
      </view>

      <view class="block">
        <text class="block-label">开办时间</text>
        <text class="block-value">{{ activity.startTime }}</text>
      </view>
      <view class="block">
        <text class="block-label">活动地点</text>
        <text class="block-value">{{ activity.location }}</text>
      </view>

      <view class="prose block">
        <text class="block-label">活动内容</text>
        <text class="desc">{{ activity.description }}</text>
      </view>
    </view>

    <view class="footer-space" />

    <view class="footer safe-bottom">
      <button class="btn" type="button" @click="onRegister">
        立即报名
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.wrap {
  min-height: 100vh;
  box-sizing: border-box;
  background: linear-gradient(180deg, #fff5f8 0%, #faf8fc 35%, #ffffff 100%);
}

.nav-bar {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: calc(12rpx + env(safe-area-inset-top)) 24rpx 16rpx;
  background: rgba(255, 245, 248, 0.96);
  border-bottom: 1px solid rgba(251, 207, 224, 0.45);
}

.nav-back {
  flex-shrink: 0;
  font-size: 28rpx;
  color: #be185d;
  font-weight: 600;
}

.nav-title {
  flex: 1;
  min-width: 0;
  font-size: 30rpx;
  font-weight: 700;
  color: #5c1f3d;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.main {
  padding: 16rpx 24rpx 24rpx;
}

.hero {
  padding: 28rpx 24rpx;
  border-radius: 22rpx;
  background: #ffffff;
  border: 1px solid rgba(251, 224, 233, 0.9);
  box-shadow: 0 12rpx 32rpx rgba(163, 74, 112, 0.07);
}

.hero-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #5c1f3d;
  line-height: 1.45;
}

.hero-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
}

.pill {
  padding: 6rpx 16rpx;
  border-radius: 999px;
  font-size: 22rpx;
  color: #b8326c;
  background: rgba(253, 228, 239, 0.95);
}

.enrolled {
  font-size: 24rpx;
  color: #9c6782;
}

.block {
  margin-top: 20rpx;
  padding: 22rpx 24rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(251, 224, 233, 0.55);
}

.block-label {
  display: block;
  font-size: 24rpx;
  color: #a894a8;
  margin-bottom: 10rpx;
}

.block-value {
  display: block;
  font-size: 28rpx;
  color: #4a3542;
  line-height: 1.55;
}

.prose .desc {
  display: block;
  font-size: 28rpx;
  color: #5c3449;
  line-height: 1.65;
  white-space: pre-wrap;
}

.footer-space {
  height: 160rpx;
}

.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background: rgba(255, 253, 254, 0.94);
  border-top: 1px solid rgba(251, 207, 224, 0.5);
  backdrop-filter: blur(8px);
}

.btn {
  width: 100%;
  height: 92rpx;
  line-height: 92rpx;
  border-radius: 999px;
  font-size: 32rpx;
  font-weight: 600;
  color: #ffffff;
  background: linear-gradient(120deg, #ec4899, #be185d);
  border: none;
}

.btn::after {
  border: none;
}
</style>
