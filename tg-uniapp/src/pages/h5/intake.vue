<script lang="ts" setup>
/**
 * 兼容旧链接 `/pages/h5/intake`：资料填写已拆成「形象上传」与「基本资料」两页。
 */
definePage({
  style: {
    navigationBarTitleText: '资料填写',
    navigationBarBackgroundColor: '#f5f3f6',
    backgroundColor: '#f5f3f6',
  },
})

import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.redirectTo({
    url: '/pages/h5/intake-photo',
    fail: () => {
      uni.navigateTo({ url: '/pages/h5/intake-photo' })
    },
  })
})
</script>

<template>
  <view class="page-intake-redirect" />
</template>

<style scoped>
.page-intake-redirect {
  min-height: 100vh;
  background: #f5f3f6;
}
</style>
