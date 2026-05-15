<script lang="ts" setup>
defineOptions({
  name: 'H5WithdrawRecordsPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '提现记录',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

interface IWithdrawRecord {
  id: string
  /** 展示用脱敏卡号 */
  cardDisplay: string
  time: string
  amount: number
  /** 是否到账 */
  arrived: boolean
}

/** mock 提现记录 */
const records = reactive<IWithdrawRecord[]>([
  {
    id: 'w1',
    cardDisplay: '工商银行 · 6222 **** **** 1288',
    time: '2026-05-08 14:32',
    amount: 500,
    arrived: true,
  },
  {
    id: 'w2',
    cardDisplay: '建设银行 · 6217 **** **** 9031',
    time: '2026-04-26 09:18',
    amount: 200,
    arrived: true,
  },
  {
    id: 'w3',
    cardDisplay: '农业银行 · 6228 **** **** 5512',
    time: '2026-05-10 11:05',
    amount: 300,
    arrived: false,
  },
])
</script>

<template>
  <view class="page">
    <view class="hint-bar">
      <text class="hint-txt">以下为演示数据；到账时间以银行处理为准。</text>
    </view>

    <view v-if="records.length === 0" class="empty">
      <text class="empty-txt">暂无提现记录</text>
    </view>

    <view
      v-for="r in records"
      :key="r.id"
      class="rec-card"
    >
      <view class="rec-top">
        <text class="rec-amt">¥{{ r.amount.toFixed(2) }}</text>
        <text class="rec-status" :class="r.arrived ? 'ok' : 'pending'">{{ r.arrived ? '已到账' : '处理中' }}</text>
      </view>
      <text class="rec-card-no">{{ r.cardDisplay }}</text>
      <text class="rec-time">{{ r.time }}</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(20rpx + env(safe-area-inset-top)) 24rpx calc(32rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: #faf8fc;
}

.hint-bar {
  margin-bottom: 20rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: rgba(1, 141, 113, 0.08);
}

.hint-txt {
  font-size: 24rpx;
  line-height: 1.45;
  color: #5c6d68;
}

.empty {
  padding: 80rpx 24rpx;
  text-align: center;
}

.empty-txt {
  font-size: 28rpx;
  color: #a898a3;
}

.rec-card {
  margin-bottom: 18rpx;
  padding: 26rpx 24rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 10rpx 36rpx rgba(74, 46, 59, 0.06);
}

.rec-top {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14rpx;
}

.rec-amt {
  font-size: 36rpx;
  font-weight: 700;
  color: #34252f;
}

.rec-status {
  font-size: 22rpx;
  font-weight: 600;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
}

.rec-status.ok {
  background: rgba(1, 141, 113, 0.12);
  color: #018d71;
}

.rec-status.pending {
  background: rgba(217, 119, 6, 0.12);
  color: #b45309;
}

.rec-card-no {
  display: block;
  font-size: 26rpx;
  color: #5c4a55;
  margin-bottom: 8rpx;
}

.rec-time {
  display: block;
  font-size: 24rpx;
  color: #a898a3;
}
</style>
