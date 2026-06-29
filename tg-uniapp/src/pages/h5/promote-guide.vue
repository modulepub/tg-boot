<script lang="ts" setup>
defineOptions({
  name: 'H5PromoteGuidePage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '推广指南',
    navigationBarBackgroundColor: '#fdf8fb',
    backgroundColor: '#fdf8fb',
  },
})

const sharePages = [
  { icon: '🏠', name: '首页', desc: '小程序首页推荐给单身朋友' },
  { icon: '💌', name: '嘉宾主页', desc: '某位嘉宾的个人卡片页' },
  { icon: '💐', name: '咨询主页', desc: '情感顾问官方咨询页' },
]

const steps = [
  {
    n: '1',
    title: '打开右上角菜单',
    body: '停留在上述任一已开放分享的页面，点击小程序右上角「···」。',
  },
  {
    n: '2',
    title: '选择分享方式',
    body: '点击「转发给朋友」，把小程序发给微信好友；也可使用「分享到朋友圈」（若当前微信版本展示该入口），让更多人看见。',
  },
]
</script>

<template>
  <view class="page">
    <view class="hero">
      <text class="hero-kicker">邀请有礼</text>
      <text class="hero-title">分享小程序，遇见新朋友</text>
      <text class="hero-lead">
        无论您通过哪种方式把小程序分享出去，只要对方是
        <text class="em">首次注册</text>
        的新用户，即算作您的推广成果，可按规则获得相应收益。
      </text>
    </view>

    <view class="card highlight">
      <view class="card-tag">
        <text class="card-tag-txt">核心规则</text>
      </view>
      <text class="card-p">
        当前小程序已在
        <text class="em">首页</text>
        、
        <text class="em">嘉宾主页</text>
        与
        <text class="em">咨询主页（情感顾问）</text>
        开放分享。好友从这些入口进入并完成
        <text class="em">首次注册</text>
        后，推广即生效；分享形式（转发对话 / 朋友圈等）不影响判定，以平台识别的首次注册为准。
      </text>
    </view>

    <view class="section-head">
      <text class="section-title">哪些页面可以分享？</text>
      <text class="section-sub">以下页面支持唤起微信自带分享能力</text>
    </view>
    <view class="tile-grid">
      <view v-for="p in sharePages" :key="p.name" class="tile">
        <text class="tile-icon">{{ p.icon }}</text>
        <text class="tile-name">{{ p.name }}</text>
        <text class="tile-desc">{{ p.desc }}</text>
      </view>
    </view>

    <view class="section-head">
      <text class="section-title">怎样发起分享？</text>
      <text class="section-sub">跟着两步即可完成</text>
    </view>
    <view class="steps">
      <view v-for="s in steps" :key="s.n" class="step-row">
        <view class="step-badge">
          <text class="step-num">{{ s.n }}</text>
        </view>
        <view class="step-body">
          <text class="step-title">{{ s.title }}</text>
          <text class="step-text">{{ s.body }}</text>
        </view>
      </view>
    </view>

    <view class="card muted">
      <text class="muted-title">温馨提示</text>
      <text class="muted-p">
        · 推广关系与奖励发放以平台系统记录为准，请勿使用刷单、造假等违规手段。
      </text>
      <text class="muted-p">
        · 奖励比例、结算周期与提现规则以小程序内「邀请好友」页及平台公示为准。
      </text>
    </view>

    <view class="bottom-space" />
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(24rpx + env(safe-area-inset-top)) 24rpx calc(40rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: linear-gradient(180deg, #fff5f9 0%, #faf8fc 38%, #f6f4fb 100%);
}

.hero {
  padding: 36rpx 28rpx 32rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #9f1239 0%, #be185d 48%, #a855b8 100%);
  box-shadow: 0 20rpx 56rpx rgba(159, 18, 57, 0.22);
  margin-bottom: 24rpx;
}

.hero-kicker {
  display: inline-block;
  padding: 8rpx 20rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
  background: rgba(255, 255, 255, 0.22);
  letter-spacing: 2rpx;
}

.hero-title {
  display: block;
  margin-top: 22rpx;
  font-size: 40rpx;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.35;
  letter-spacing: 1rpx;
}

.hero-lead {
  display: block;
  margin-top: 18rpx;
  font-size: 28rpx;
  line-height: 1.65;
  color: rgba(255, 255, 255, 0.9);
}

.em {
  font-weight: 700;
  color: #fff9c4;
}

.card {
  padding: 28rpx 26rpx;
  border-radius: 24rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.07);
  margin-bottom: 28rpx;
}

.card.highlight {
  border: 2rpx solid rgba(190, 24, 93, 0.12);
}

.card-tag {
  margin-bottom: 16rpx;
}

.card-tag-txt {
  display: inline-block;
  padding: 6rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 700;
  color: #be185d;
  background: rgba(190, 24, 93, 0.1);
}

.card-p {
  font-size: 28rpx;
  line-height: 1.65;
  color: #4a3d44;
}

.section-head {
  margin-bottom: 16rpx;
  padding-left: 4rpx;
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #34252f;
}

.section-sub {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #9a8894;
}

.tile-grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.tile {
  width: calc((100% - 32rpx) / 3);
  box-sizing: border-box;
  padding: 22rpx 14rpx;
  border-radius: 20rpx;
  background: #ffffff;
  border: 2rpx solid rgba(232, 213, 226, 0.85);
  box-shadow: 0 8rpx 24rpx rgba(74, 46, 59, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.tile-icon {
  font-size: 40rpx;
  line-height: 1;
  margin-bottom: 10rpx;
}

.tile-name {
  font-size: 26rpx;
  font-weight: 700;
  color: #34252f;
}

.tile-desc {
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.45;
  color: #9a8894;
}

.steps {
  margin-bottom: 28rpx;
}

.step-row {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 20rpx;
  margin-bottom: 22rpx;
  padding: 24rpx 22rpx;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 8rpx 28rpx rgba(74, 46, 59, 0.06);
}

.step-badge {
  flex-shrink: 0;
  width: 52rpx;
  height: 52rpx;
  border-radius: 16rpx;
  background: linear-gradient(145deg, #fce7f3 0%, #fae8ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-num {
  font-size: 26rpx;
  font-weight: 800;
  color: #be185d;
}

.step-body {
  flex: 1;
  min-width: 0;
}

.step-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #34252f;
}

.step-text {
  display: block;
  margin-top: 10rpx;
  font-size: 26rpx;
  line-height: 1.55;
  color: #6b5a66;
}

.card.muted {
  background: rgba(255, 255, 255, 0.72);
  border: 2rpx dashed rgba(190, 24, 93, 0.15);
  box-shadow: none;
}

.muted-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #8b7a84;
  margin-bottom: 12rpx;
}

.muted-p {
  display: block;
  font-size: 24rpx;
  line-height: 1.55;
  color: #9a8894;
  margin-bottom: 10rpx;
}

.bottom-space {
  height: 8rpx;
}
</style>
