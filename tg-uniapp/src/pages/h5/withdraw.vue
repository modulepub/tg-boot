<script lang="ts" setup>
defineOptions({
  name: 'H5WithdrawPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '申请提现',
    navigationBarBackgroundColor: '#fdf8fb',
    backgroundColor: '#fdf8fb',
  },
})

/** 仅支持四大行（演示） */
const bigFourBanks = [
  { code: 'icbc', name: '中国工商银行' },
  { code: 'abc', name: '中国农业银行' },
  { code: 'boc', name: '中国银行' },
  { code: 'ccb', name: '中国建设银行' },
]

const bankNames = bigFourBanks.map(b => b.name)
const bankIndex = ref(0)

const form = reactive({
  cardNo: '',
  amount: '',
})

function onBankChange(e: { detail: { value: string | number } }) {
  const i = Number(e.detail.value)
  bankIndex.value = Number.isFinite(i) ? i : 0
}

function goRecords() {
  uni.navigateTo({
    url: '/pages/h5/withdraw-records',
  })
}

function submitWithdrawMock() {
  const card = form.cardNo.replace(/\D/g, '')
  if (card.length < 16) {
    uni.showToast({ title: '请填写本人有效银行卡号', icon: 'none' })
    return
  }
  if (!form.amount.trim() || Number(form.amount) <= 0) {
    uni.showToast({ title: '请输入提现金额', icon: 'none' })
    return
  }
  uni.showToast({ title: '已提交（演示数据）', icon: 'success' })
}
</script>

<template>
  <view class="page">
    <view class="hero">
      <text class="hero-title">提现到本人银行卡</text>
      <text class="hero-sub">为保障资金安全，仅支持本人名下四大行储蓄卡，信息将加密传输（当前为演示环境）。</text>
    </view>

    <view class="card">
      <text class="section-h">收款信息</text>

      <text class="label">开户银行</text>
      <picker mode="selector" :range="bankNames" :value="bankIndex" @change="onBankChange">
        <view class="picker-row">
          <text class="picker-val">{{ bigFourBanks[bankIndex]?.name }}</text>
          <text class="picker-arw">›</text>
        </view>
      </picker>

      <view class="field">
        <text class="label">银行卡号</text>
        <input
          v-model="form.cardNo"
          class="input"
          type="number"
          maxlength="19"
          placeholder="请输入与实名一致的本人卡号"
          placeholder-class="ph"
        >
      </view>

      <view class="field">
        <text class="label">提现金额（元）</text>
        <input
          v-model="form.amount"
          class="input"
          type="digit"
          placeholder="请输入本次提现金额"
          placeholder-class="ph"
        >
      </view>
    </view>

    <view class="btn-row">
      <view class="btn ghost" @click="goRecords">
        <text class="btn-txt ghost-txt">提现记录</text>
      </view>
    </view>

    <view class="btn primary block" @click="submitWithdrawMock">
      <text class="btn-txt">确认提现</text>
    </view>

    <view class="bottom-space" />
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(24rpx + env(safe-area-inset-top)) 24rpx calc(32rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: linear-gradient(180deg, #fff5f9 0%, #faf8fc 45%, #f6f4fb 100%);
}

.hero {
  margin-bottom: 24rpx;
  padding: 28rpx 26rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #fdf2f8 0%, #ffffff 55%, #faf5ff 100%);
  border: 2rpx solid rgba(233, 213, 255, 0.55);
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.06);
}

.hero-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: #34252f;
}

.hero-sub {
  display: block;
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.55;
  color: #8b7a84;
}

.card {
  padding: 28rpx 24rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.07);
  margin-bottom: 28rpx;
}

.section-h {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #34252f;
  margin-bottom: 22rpx;
}

.label {
  display: block;
  font-size: 24rpx;
  color: #8b7a84;
  margin-bottom: 10rpx;
}

.picker-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 22rpx 20rpx;
  margin-bottom: 22rpx;
  background: #faf7f9;
  border-radius: 16rpx;
}

.picker-val {
  flex: 1;
  font-size: 28rpx;
  font-weight: 600;
  color: #34252f;
}

.picker-arw {
  font-size: 36rpx;
  color: #c4b8bf;
}

.field {
  margin-bottom: 22rpx;
}

.input {
  width: 100%;
  box-sizing: border-box;
  padding: 22rpx 20rpx;
  font-size: 28rpx;
  color: #34252f;
  background: #faf7f9;
  border-radius: 16rpx;
}

.ph {
  color: #c4b8bf;
}

.btn-row {
  margin-bottom: 16rpx;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 22rpx 32rpx;
  border-radius: 999rpx;
}

.btn.ghost {
  border: 2rpx solid rgba(190, 24, 93, 0.35);
  background: rgba(255, 255, 255, 0.9);
}

.btn.primary {
  background: linear-gradient(135deg, #be185d 0%, #c026d3 100%);
  box-shadow: 0 14rpx 36rpx rgba(190, 24, 93, 0.28);
}

.btn.block {
  width: 100%;
  box-sizing: border-box;
}

.btn-txt {
  font-size: 30rpx;
  font-weight: 600;
  color: #ffffff;
}

.ghost-txt {
  color: #be185d;
}

.bottom-space {
  height: 16rpx;
}
</style>
