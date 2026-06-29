<template>
  <view class="message-page">
    <view class="empty-wrap">
      <text class="empty-title">暂无消息</text>
      <text class="empty-desc">当前没有会话列表，后续若有通知会在此展示。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
defineOptions({
  name: 'MessageIndex',
})

definePage({
  style: {
    navigationBarTitleText: '消息',
    navigationStyle: 'default',
  },
})
</script>

<style scoped lang="scss">
.message-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(180deg, #fff4f8 0%, #ffffff 55%);
}

.empty-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48rpx 40rpx;
}

.empty-title {
  font-size: 32rpx;
  color: #2a1f33;
  font-weight: 600;
}

.empty-desc {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #8b7f92;
  text-align: center;
  line-height: 1.6;
}
</style>
