<script lang="ts" setup>
import type { IMockActivity } from '@/mock/h5-activities'
import { getMockActivityCities, MOCK_ACTIVITIES } from '@/mock/h5-activities'

defineOptions({
  name: 'H5ActivityListPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '活动列表',
    navigationBarBackgroundColor: '#fff5f8',
    backgroundColor: '#fff5f8',
  },
})

const cityOptions = computed(() => ['全部', ...getMockActivityCities()])
const cityIndex = ref(0)
const selectedCity = computed(() => cityOptions.value[cityIndex.value] ?? '全部')

const filteredList = computed(() => {
  const city = selectedCity.value
  if (city === '全部')
    return MOCK_ACTIVITIES
  return MOCK_ACTIVITIES.filter(a => a.city === city)
})

function onCityChange(ev: UniApp.PickerChangeEvent) {
  const i = Number(ev.detail.value)
  cityIndex.value = Number.isFinite(i) ? i : 0
}

function openDetail(item: IMockActivity) {
  uni.navigateTo({
    url: `/pages/h5/activity-detail?id=${encodeURIComponent(item.id)}`,
  })
}
</script>

<template>
  <view class="page">
    <view class="page-head">
      <text class="page-title">活动列表</text>
      <text class="page-sub">线下活动（演示数据）</text>
    </view>
    <view class="toolbar">
      <text class="toolbar-label">城市</text>
      <picker mode="selector" :range="cityOptions" :value="cityIndex" @change="onCityChange">
        <view class="picker-box">
          <text class="picker-text">{{ selectedCity }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <view v-if="filteredList.length === 0" class="empty">
      当前城市暂无活动
    </view>

    <view
      v-for="item in filteredList"
      :key="item.id"
      class="card"
      @click="openDetail(item)"
    >
      <text class="card-title">{{ item.title }}</text>
      <view class="row">
        <text class="tag">{{ item.city }}</text>
        <text class="enrolled">已报名 {{ item.enrolledCount }} 人</text>
      </view>
      <view class="line">
        <text class="k">时间</text>
        <text class="v">{{ item.startTime }}</text>
      </view>
      <view class="line">
        <text class="k">地点</text>
        <text class="v">{{ item.location }}</text>
      </view>
      <view class="more">
        查看详情 ›
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(12rpx + env(safe-area-inset-top)) 24rpx calc(32rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: linear-gradient(180deg, #fff5f8 0%, #faf8fc 45%, #ffffff 100%);
}

.page-head {
  margin-bottom: 24rpx;
}

.page-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #5c1f3d;
  line-height: 1.3;
}

.page-sub {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #a894a8;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding: 20rpx 22rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(251, 207, 224, 0.65);
  box-shadow: 0 8rpx 24rpx rgba(190, 58, 118, 0.06);
}

.toolbar-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #7b2149;
}

.picker-box {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  border-radius: 999px;
  background: #fff0f6;
  border: 1px solid rgba(236, 72, 153, 0.25);
}

.picker-text {
  font-size: 26rpx;
  color: #be185d;
  font-weight: 600;
}

.picker-arrow {
  font-size: 20rpx;
  color: #db2777;
}

.empty {
  padding: 80rpx 24rpx;
  text-align: center;
  font-size: 28rpx;
  color: #a894a8;
}

.card {
  margin-bottom: 20rpx;
  padding: 26rpx 24rpx;
  border-radius: 22rpx;
  background: #ffffff;
  border: 1px solid rgba(251, 224, 233, 0.9);
  box-shadow: 0 12rpx 32rpx rgba(163, 74, 112, 0.07);
}

.card-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #5c1f3d;
  line-height: 1.45;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14rpx;
}

.tag {
  padding: 4rpx 14rpx;
  border-radius: 999px;
  font-size: 22rpx;
  color: #b8326c;
  background: rgba(253, 228, 239, 0.95);
}

.enrolled {
  font-size: 24rpx;
  color: #9c6782;
}

.line {
  display: flex;
  align-items: flex-start;
  margin-top: 14rpx;
  gap: 16rpx;
}

.k {
  flex-shrink: 0;
  width: 56rpx;
  font-size: 24rpx;
  color: #a894a8;
}

.v {
  flex: 1;
  min-width: 0;
  font-size: 26rpx;
  color: #6b5a68;
  line-height: 1.5;
}

.more {
  margin-top: 18rpx;
  font-size: 24rpx;
  color: #db2777;
  font-weight: 600;
}
</style>
