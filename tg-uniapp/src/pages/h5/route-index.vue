<script lang="ts" setup>
defineOptions({
  name: 'H5RouteIndexPage',
})

definePage({
  style: {
    navigationBarTitleText: 'H5 页面索引',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

interface IH5RouteItem {
  title: string
  path: string
}

/** `src/pages/h5` 下已注册页面路径（与 pages.json 保持一致，新增 h5 页时请同步追加） */
const h5Routes: IH5RouteItem[] = [
  { title: '活动列表', path: '/pages/h5/activity-list' },
  { title: '服务中心', path: '/pages/h5/matchmaker' },
  { title: '推广指南', path: '/pages/h5/promote-guide' },
  { title: '申请提现', path: '/pages/h5/withdraw' },
  { title: '提现记录', path: '/pages/h5/withdraw-records' },
  { title: '资料填写入口（跳转形象上传）', path: '/pages/h5/intake' },
  { title: '形象上传', path: '/pages/h5/intake-photo' },
  { title: '基本资料（编辑卡片）', path: '/pages/h5/intake-profile' },
  { title: '实名认证', path: '/pages/h5/realNameAuthentication' },
  { title: '联系我们', path: '/pages/h5/contactUs' },
  { title: '路由索引（本页）', path: '/pages/h5/route-index' },
]

function normalizePath(p: string) {
  return p.startsWith('/') ? p : `/${p}`
}

function openH5Page(url: string) {
  const pages = getCurrentPages()
  const cur = normalizePath(pages[pages.length - 1]?.route ?? '')
  const target = normalizePath(url)
  if (cur === target) {
    uni.showToast({ title: '已在当前页', icon: 'none' })
    return
  }
  uni.navigateTo({
    url: target,
    fail: () => {
      uni.showToast({ title: '无法打开', icon: 'none' })
    },
  })
}

function copyPath(path: string) {
  uni.setClipboardData({
    data: path,
    success: () => uni.showToast({ title: '路径已复制', icon: 'success' }),
  })
}
</script>

<template>
  <view class="page">
    <view class="hint">
      以下为 <text class="mono">pages/h5</text> 目录页面：点击一行跳转，点「复制」可复制路径。
    </view>
    <view
      v-for="item in h5Routes"
      :key="item.path"
      class="card row"
      @click="openH5Page(item.path)"
    >
      <view class="row-main">
        <text class="title">{{ item.title }}</text>
        <text class="path">{{ item.path }}</text>
      </view>
      <text class="copy-btn" @click.stop="copyPath(item.path)">复制</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(16rpx + env(safe-area-inset-top)) 24rpx calc(24rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #faf8fc;
}

.hint {
  margin-bottom: 20rpx;
  padding: 0 8rpx;
  font-size: 26rpx;
  color: #6b5a66;
  line-height: 1.5;
}

.mono {
  font-family: ui-monospace, Menlo, Monaco, Consolas, monospace;
  font-size: 24rpx;
  color: #4a3d44;
}

.card {
  margin-bottom: 16rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(74, 46, 59, 0.06);
}

.row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.row-main {
  flex: 1;
  min-width: 0;
  margin-right: 16rpx;
}

.title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #2d2430;
  margin-bottom: 8rpx;
}

.path {
  display: block;
  font-size: 24rpx;
  font-family: ui-monospace, Menlo, Monaco, Consolas, monospace;
  color: #8b7a84;
  word-break: break-all;
}

.copy-btn {
  flex-shrink: 0;
  font-size: 26rpx;
  color: #018d71;
  padding: 12rpx 20rpx;
  border-radius: 999rpx;
  background: rgba(1, 141, 113, 0.08);
}
</style>
