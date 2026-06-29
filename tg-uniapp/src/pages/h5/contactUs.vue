<script lang="ts" setup>
defineOptions({
  name: 'H5ContactUsPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '联系我们',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

/** 可从环境变量读取，未配置则展示占位文案 */
const serviceHotline = import.meta.env.VITE_CONTACT_HOTLINE?.trim() || ''
const serviceEmail = import.meta.env.VITE_CONTACT_EMAIL?.trim() || ''
const serviceHours = import.meta.env.VITE_CONTACT_HOURS?.trim() || '工作日 9:00 - 18:00'

function copyText(label: string, text: string) {
  if (!text) {
    uni.showToast({ title: '暂未配置', icon: 'none' })
    return
  }
  uni.setClipboardData({
    data: text,
    success: () => {
      uni.showToast({ title: `已复制${label}`, icon: 'success' })
    },
  })
}
</script>

<template>
  <view class="page">
    <view class="card">
      <text class="title">联系我们</text>
      <text class="desc">如需帮助，可通过以下方式联系平台（以下为示例配置项，请在环境变量中填写真实信息）。</text>
    </view>

    <view class="card row" @click="copyText('电话', serviceHotline)">
      <view class="row-main">
        <text class="label">客服热线</text>
        <text class="value">{{ serviceHotline || '请在 env 配置 VITE_CONTACT_HOTLINE' }}</text>
      </view>
      <text class="hint">复制</text>
    </view>

    <view class="card row" @click="copyText('邮箱', serviceEmail)">
      <view class="row-main">
        <text class="label">邮箱</text>
        <text class="value">{{ serviceEmail || '请在 env 配置 VITE_CONTACT_EMAIL' }}</text>
      </view>
      <text class="hint">复制</text>
    </view>

    <view class="card">
      <text class="label">服务时间</text>
      <text class="value mt">{{ serviceHours }}</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(24rpx + env(safe-area-inset-top)) 24rpx calc(24rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #faf8fc;
}

.card {
  margin-bottom: 20rpx;
  padding: 28rpx 24rpx;
  border-radius: 20rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(74, 46, 59, 0.06);
}

.card.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  display: block;
  font-size: 34rpx;
  font-weight: 600;
  color: #2a3247;
}

.desc {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  line-height: 1.55;
  color: #7f8aa3;
}

.row-main {
  flex: 1;
  min-width: 0;
}

.label {
  display: block;
  font-size: 26rpx;
  color: #8e6c7b;
}

.value {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  color: #3f2a33;
  word-break: break-all;
}

.value.mt {
  margin-top: 12rpx;
}

.hint {
  margin-left: 16rpx;
  font-size: 26rpx;
  color: #ff4f8b;
}
</style>
