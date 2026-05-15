<template>
  <view class="recommend-page">
    <view class="stack-wrap">
      <view v-if="currentCard" class="stack-stage">
        <view v-if="nextCard" class="recommend-card next-card" :style="nextCardStyle">
          <image class="card-cover" :src="nextCard.cusAvatar || defaultAvatar" mode="aspectFill" />
          <view v-if="hasRecommendMeta(nextCard)" class="meta-tags-corner" @click.stop>
            <text
              v-if="nextCard.recommendedSourceCode"
              class="meta-tag meta-tag--source"
              @click.stop="showSourceExplain(nextCard.recommendedSourceCode)"
            >
              {{ sourceChannelLabel(nextCard.recommendedSourceCode) }}
            </text>
            <text
              v-if="formatRecommendScore(nextCard.recommendedMatchScore)"
              class="meta-tag meta-tag--score"
              @click.stop="showMatchScoreExplain"
            >
              {{ formatRecommendScore(nextCard.recommendedMatchScore) }}
            </text>
          </view>
        </view>

        <view
          class="recommend-card active-card"
          :style="activeCardStyle"
          @click="onCardClick"
          @touchstart="onTouchStart"
          @touchmove.stop.prevent="onTouchMove"
          @touchend="onTouchEnd"
        >
          <image class="card-cover" :src="currentCard.cusAvatar || defaultAvatar" mode="aspectFill" />
          <view v-if="hasRecommendMeta(currentCard)" class="meta-tags-corner" @click.stop>
            <text
              v-if="currentCard.recommendedSourceCode"
              class="meta-tag meta-tag--source"
              @click.stop="showSourceExplain(currentCard.recommendedSourceCode)"
            >
              {{ sourceChannelLabel(currentCard.recommendedSourceCode) }}
            </text>
            <text
              v-if="formatRecommendScore(currentCard.recommendedMatchScore)"
              class="meta-tag meta-tag--score"
              @click.stop="showMatchScoreExplain"
            >
              {{ formatRecommendScore(currentCard.recommendedMatchScore) }}
            </text>
          </view>
          <view class="card-mask">
            <view class="card-info">
              <view class="name-row">
                <text class="name">{{ currentCard.cusName || '匿名用户' }}</text>
              </view>
              <view class="age-line">
                <text class="age-pill">
                  {{ currentCard.cusAge }}岁
                </text>
                <text class="verified-pill">
                  已实名
                </text>
              </view>
              <view class="city">
                最近活动于 {{ currentCard.cusCityResidenceName || '未知城市' }}
              </view>
              <view class="moment">
                {{ currentCard.cusMoment || '这个人很神秘，快去了解一下吧。' }}
              </view>
            </view>
          </view>
          <view class="stamp dislike-stamp" :style="{ opacity: dislikeOpacity }">
            不喜欢
          </view>
          <view class="stamp like-stamp" :style="{ opacity: likeOpacity }">
            喜欢
          </view>

          <view class="action-footer" @click.stop>
            <view class="action-btn dislike-btn" @click.stop="triggerAction('0')">
              ×
            </view>
            <view class="action-btn like-btn" @click.stop="triggerAction('1')">
              ❤
            </view>
          </view>
        </view>
      </view>

      <scroll-view
        v-else
        class="empty-scroll"
        scroll-y
        :show-scrollbar="false"
        enable-flex
      >
        <view class="empty-wrap">
          <view class="empty-hero">
            <text class="empty-hero-title">今日推荐已全部浏览完毕</text>
            <text class="empty-hero-desc">本轮公益推荐列表您已全部看完，稍作休息或获取更多名额后继续缘分之旅。</text>
          </view>

          <view class="empty-card">
            <view class="empty-card-label">
              <view class="empty-card-label-dot empty-card-label-dot--rose" />
              <text class="empty-card-label-text">公益推荐额度</text>
            </view>
            <text class="empty-card-body">
              平台每日为用户提供约 <text class="empty-em">20 位</text> 公益智能推荐嘉宾（不含付费加码、红娘定向等额外通道），推荐诚意遇见真实缘分。
            </text>
          </view>

          <view class="empty-card">
            <view class="empty-card-label">
              <view class="empty-card-label-dot empty-card-label-dot--violet" />
              <text class="empty-card-label-text">匹配成功后如何联系</text>
            </view>
            <text class="empty-card-body">
              双方相互喜欢、匹配成功后，即可在平台内直接发起沟通。<text class="empty-em">家长用户</text>可直接拨打电话联系对方；<text class="empty-em">本人账号</text>建议优先发送系统消息破冰，礼貌得体更易建立信任。
            </text>
          </view>

          <view class="empty-card empty-card--accent">
            <view class="empty-card-label">
              <view class="empty-card-label-dot empty-card-label-dot--gold" />
              <text class="empty-card-label-text">需要更多推荐？</text>
            </view>
            <text class="empty-card-body">
              若希望当日浏览更多嘉宾，可选购平台「推荐次数」等福利权益，解锁额外推荐配额（具体以权益说明为准）。
            </text>
          </view>

          <view class="empty-card empty-card--vision">
            <view class="empty-card-label">
              <view class="empty-card-label-dot empty-card-label-dot--sky" />
              <text class="empty-card-label-text">平台愿景</text>
            </view>
            <text class="empty-card-body">
              我们致力于把婚恋服务做稳、做强、做长久：<text class="empty-em">短期目标</text>是搭建合法合规、值得信赖的国际婚恋服务平台，回应多元化的择偶与婚恋需求，助力缓解适龄单身男女地域与圈层分布不均带来的择偶难题。
            </text>
          </view>

          <button class="reload-btn" @click="goBenefits">
            <text class="reload-btn-text">获取更多推荐</text>
          </button>
          <view class="empty-foot-note">下滑可重温上述说明 · 按钮将前往权益页面</view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { IPreferencePayload, IRecommendItem } from '@/api/recommend'
import { getRecommendList, savePreference } from '@/api/recommend'

defineOptions({
  name: 'RecommendIndex',
})

definePage({
  style: {
    navigationBarTitleText: '推荐',
  },
})

const defaultAvatar = '/static/avatar-default.svg'

const cards = ref<IRecommendItem[]>([])
const currentIndex = ref(0)
const pageNo = ref(1)
const pageSize = 10
const loading = ref(false)
const touching = ref(false)
const animating = ref(false)
const startX = ref(0)
const startY = ref(0)
const dragX = ref(0)
const dragY = ref(0)
const skipNextClick = ref(false)

const currentCard = computed(() => cards.value[currentIndex.value] || null)
const nextCard = computed(() => cards.value[currentIndex.value + 1] || null)

const likeOpacity = computed(() => (dragX.value > 0 ? Math.min(dragX.value / 120, 1) : 0))
const dislikeOpacity = computed(() => (dragX.value < 0 ? Math.min(Math.abs(dragX.value) / 120, 1) : 0))

const activeCardStyle = computed(() => ({
  transform: `translate3d(${dragX.value}px, ${dragY.value}px, 0) rotate(${dragX.value / 48}deg)`,
  transition: touching.value ? 'none' : 'transform 280ms cubic-bezier(0.25, 0.8, 0.25, 1)',
}))

/** 下一张：静止时沉在底部，随滑动进度向上浮入，避免跟横向拖拽绑死的左右闪动感 */
const nextCardStyle = computed(() => {
  const maxDrag = 240
  const ratio = Math.min(Math.abs(dragX.value) / maxDrag, 1)
  const hiddenYRpx = 200
  const translateYRpx = Math.round((1 - ratio) * hiddenYRpx)
  const scale = 0.94 + ratio * 0.06
  const opacity = 0.68 + ratio * 0.32
  return {
    transform: `translate3d(0, ${translateYRpx}rpx, 0) scale(${scale})`,
    opacity,
    transition: touching.value ? 'none' : 'transform 280ms cubic-bezier(0.25, 0.8, 0.25, 1), opacity 280ms ease',
  }
})

function sourceChannelLabel(code: string) {
  switch (String(code || '').trim()) {
    case 'free':
      return '免费推荐'
    case 'pay':
      return '付费推荐'
    case 'matchmaker':
      return '红娘推荐'
    default:
      return '推荐'
  }
}

function sourceChannelExplain(code: string) {
  switch (String(code || '').trim()) {
    case 'free':
      return '系统每天固定数量免费智能推荐付费对象，匹配分通常低于付费推荐。'
    case 'pay':
      return '系统根据您的意向及对方的意向获取匹配率最高的对象。'
    case 'matchmaker':
      return '红娘主动推荐的对象。'
    default:
      return '推荐说明'
  }
}

function showSourceExplain(code: string) {
  const c = String(code || '').trim()
  if (!c)
    return
  uni.showModal({
    title: sourceChannelLabel(c),
    content: sourceChannelExplain(c),
    showCancel: false,
  })
}

function showMatchScoreExplain() {
  uni.showModal({
    title: '匹配分',
    content: '匹配分=己方意向匹配度×0.8+对方意向匹配度×0.2，满分100分。',
    showCancel: false,
  })
}

function formatRecommendScore(raw: unknown): string | null {
  if (raw === undefined || raw === null || raw === '')
    return null
  const n = typeof raw === 'number' ? raw : Number.parseFloat(String(raw))
  if (Number.isNaN(n))
    return null
  return n.toFixed(2)
}

function hasRecommendMeta(card: IRecommendItem | null | undefined) {
  if (!card)
    return false
  return !!(String(card.recommendedSourceCode || '').trim() || formatRecommendScore(card.recommendedMatchScore))
}

function resolveCusAvatar(avatar: unknown) {
  const first = String(avatar ?? '')
    .split(',')
    .map(item => item.trim())
    .find(Boolean)
  return first || ''
}

async function fetchRecommend(reset = false) {
  if (loading.value)
    return

  loading.value = true
  if (reset) {
    currentIndex.value = 0
    dragX.value = 0
    dragY.value = 0
  }

  try {
    const nextPageNo = reset ? 1 : pageNo.value
    const res = await getRecommendList({ pageNo: nextPageNo, pageSize })
    const records = (res?.records ?? []).map(item => ({
      ...item,
      // 后端 cusAvatar 可能是逗号分隔列表，这里只取首图做推荐卡封面
      cusAvatar: resolveCusAvatar(item.cusAvatar),
      cusAge: item.cusAge || 25,
    }))
    cards.value = records
    pageNo.value = nextPageNo + 1
  }
  catch {
    cards.value = []
    uni.showToast({
      title: '推荐数据加载失败',
      icon: 'none',
    })
  }
  finally {
    loading.value = false
  }
}

async function submitPreference(item: IRecommendItem, status: IPreferencePayload['preferenceLikeStatusCode']) {
  if (!item.cusCode)
    return

  const payload: IPreferencePayload = {
    preferenceTargetCusCode: item.cusCode,
    preferenceLikeStatusCode: status,
  }

  try {
    await savePreference(payload)
  }
  catch {
    // mock 数据不存在后端实体，接口失败时仍允许继续滑卡演示
  }
}

function resetCardPosition() {
  touching.value = false
  dragX.value = 0
  dragY.value = 0
}

async function commitSwipe(status: IPreferencePayload['preferenceLikeStatusCode']) {
  if (!currentCard.value || animating.value)
    return

  animating.value = true
  touching.value = false
  // 当前卡斜向上飞出，给下方露出的下一张让位，减少纯横向「闪走」感
  dragX.value = status === '1' ? 420 : -420
  dragY.value = -200

  const target = currentCard.value
  await submitPreference(target, status)

  setTimeout(() => {
    currentIndex.value += 1
    dragX.value = 0
    dragY.value = 0
    animating.value = false
  }, 280)
}

function triggerAction(status: IPreferencePayload['preferenceLikeStatusCode']) {
  commitSwipe(status)
}

function onTouchStart(e: TouchEvent) {
  if (!currentCard.value || animating.value)
    return
  const touch = e.touches[0]
  if (!touch)
    return
  touching.value = true
  startX.value = touch.clientX
  startY.value = touch.clientY
}

function onTouchMove(e: TouchEvent) {
  if (!touching.value || animating.value)
    return
  const touch = e.touches[0]
  if (!touch)
    return
  dragX.value = touch.clientX - startX.value
  dragY.value = touch.clientY - startY.value
}

function onTouchEnd() {
  if (!touching.value || animating.value)
    return
  const threshold = 90
  if (dragX.value > threshold) {
    skipNextClick.value = true
    commitSwipe('1')
    return
  }
  if (dragX.value < -threshold) {
    skipNextClick.value = true
    commitSwipe('0')
    return
  }
  resetCardPosition()
}

function goBenefits() {
  uni.navigateTo({
    url: '/pages-sub/dating/customer/benefits',
  })
}

function goToProfileCard(item: IRecommendItem) {
  const code = item.cusCode ? `?cusCode=${encodeURIComponent(item.cusCode)}` : ''
  uni.navigateTo({
    url: `/pages-sub/dating/customer/profile-card${code}`,
  })
}

function onCardClick() {
  if (!currentCard.value)
    return

  if (skipNextClick.value) {
    skipNextClick.value = false
    return
  }

  goToProfileCard(currentCard.value)
}

onShow(() => {
  fetchRecommend(true)
})
</script>

<style lang="scss" scoped>
.recommend-page {
  height: 100vh;
  box-sizing: border-box;
  overflow: hidden;
  background: linear-gradient(180deg, #fff4f7 0%, #fdf8fb 42%, #ffffff 100%);
  padding: 0 24rpx calc(env(safe-area-inset-bottom) + 28rpx);
}

.stack-wrap {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 0;
}

.stack-stage {
  position: relative;
  width: 100%;
  flex: 1;
  min-height: 0;
}

.recommend-card {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 140rpx;
  overflow: hidden;
  border-radius: 34rpx;
  background: #111;
}

.next-card {
  z-index: 1;
  will-change: transform, opacity;
}

.active-card {
  z-index: 2;
  box-shadow: 0 24rpx 44rpx rgba(47, 20, 37, 0.3);
}

.card-cover {
  width: 100%;
  height: 100%;
  display: block;
  background: #f9d0e2;
}

.card-mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 48%;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(16, 11, 16, 0.86) 70%);
}

.card-info {
  position: absolute;
  left: 28rpx;
  right: 28rpx;
  bottom: 150rpx;
}

.name-row {
  display: flex;
  align-items: center;
}

.name {
  font-size: 42rpx;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5rpx;
}

.meta-tags-corner {
  position: absolute;
  top: 22rpx;
  right: 20rpx;
  z-index: 4;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: nowrap;
  gap: 8rpx;
  max-width: 75%;
  pointer-events: auto;
}

.meta-tag {
  font-size: 18rpx;
  line-height: 1.25;
  padding: 6rpx 14rpx;
  border-radius: 999px;
  letter-spacing: 0.2rpx;
  color: rgba(255, 255, 255, 0.94);
  background: rgba(15, 23, 42, 0.35);
  border: 1rpx solid rgba(255, 255, 255, 0.28);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.meta-tag--source {
  background: rgba(255, 255, 255, 0.16);
  border-color: rgba(255, 255, 255, 0.38);
  color: rgba(255, 255, 255, 0.98);
}

.meta-tag--score {
  background: rgba(131, 77, 125, 0.28);
  border-color: rgba(233, 213, 227, 0.42);
  color: rgba(253, 242, 248, 0.98);
}

.age-line {
  margin-top: 8rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.age-pill {
  display: inline-block;
  padding: 4rpx 14rpx;
  border-radius: 999px;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.94);
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.28);
}

.verified-pill {
  display: inline-block;
  padding: 4rpx 14rpx;
  border-radius: 999px;
  font-size: 24rpx;
  color: #fde68a;
  background: rgba(0, 0, 0, 0.26);
  border: 1px solid rgba(253, 230, 138, 0.45);
}

.city {
  margin-top: 12rpx;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.9);
}

.moment {
  margin-top: 10rpx;
  font-size: 28rpx;
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.96);
}

.stamp {
  position: absolute;
  top: 80rpx;
  padding: 10rpx 18rpx;
  border-width: 3rpx;
  border-style: solid;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 700;
  background: rgba(0, 0, 0, 0.16);
  opacity: 0;
  transition: opacity 150ms ease;
}

.dislike-stamp {
  right: 28rpx;
  color: #fcd34d;
  border-color: #fcd34d;
  transform: rotate(10deg);
}

.like-stamp {
  left: 28rpx;
  color: #f472b6;
  border-color: #f472b6;
  transform: rotate(-12deg);
}

.action-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 28rpx;
  z-index: 6;
  display: flex;
  justify-content: center;
  gap: 50rpx;
}

.action-btn {
  width: 118rpx;
  height: 78rpx;
  border-radius: 999px;
  background: rgba(18, 20, 26, 0.9);
  text-align: center;
  line-height: 78rpx;
  font-size: 48rpx;
  font-weight: 700;
}

.dislike-btn {
  color: #f6c94d;
}

.like-btn {
  color: #f43f7b;
}

.empty-scroll {
  width: 100%;
  flex: 1;
  min-height: 0;
  height: 0;
}

.empty-wrap {
  padding: 8rpx 4rpx calc(28rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.empty-hero {
  text-align: center;
  padding: 28rpx 28rpx 36rpx;
  margin-bottom: 8rpx;
  border-radius: 28rpx;
  background: linear-gradient(165deg, #ffffff 0%, #fff5f9 48%, #faf5ff 100%);
  border: 1rpx solid rgba(233, 213, 255, 0.55);
  box-shadow: 0 12rpx 40rpx rgba(120, 60, 120, 0.08);
}

.empty-hero-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: #7e1d4f;
  letter-spacing: 0.5rpx;
  line-height: 1.35;
}

.empty-hero-desc {
  display: block;
  margin-top: 16rpx;
  font-size: 26rpx;
  color: #8b6b7e;
  line-height: 1.65;
  padding: 0 8rpx;
}

.empty-card {
  margin-top: 20rpx;
  padding: 28rpx 26rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.92);
  border: 1rpx solid rgba(233, 213, 255, 0.55);
  box-shadow: 0 6rpx 22rpx rgba(90, 40, 80, 0.06);
}

.empty-card--accent {
  background: linear-gradient(135deg, #fffafc 0%, #fdf4ff 100%);
  border-color: rgba(236, 72, 153, 0.35);
}

.empty-card--vision {
  background: linear-gradient(135deg, #f8fafc 0%, #faf5ff 55%, #fff7ed 100%);
  border-color: rgba(167, 139, 250, 0.35);
}

.empty-card-label {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 14rpx;
}

.empty-card-label-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.empty-card-label-dot--rose {
  background: linear-gradient(180deg, #f472b6 0%, #db2777 100%);
  box-shadow: 0 0 0 6rpx rgba(244, 114, 182, 0.2);
}

.empty-card-label-dot--violet {
  background: linear-gradient(180deg, #a78bfa 0%, #7c3aed 100%);
  box-shadow: 0 0 0 6rpx rgba(167, 139, 250, 0.22);
}

.empty-card-label-dot--gold {
  background: linear-gradient(180deg, #fcd34d 0%, #f59e0b 100%);
  box-shadow: 0 0 0 6rpx rgba(252, 211, 77, 0.28);
}

.empty-card-label-dot--sky {
  background: linear-gradient(180deg, #38bdf8 0%, #0284c7 100%);
  box-shadow: 0 0 0 6rpx rgba(56, 189, 248, 0.22);
}

.empty-card-label-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #431832;
}

.empty-card-body {
  display: block;
  font-size: 26rpx;
  color: #6b5564;
  line-height: 1.72;
  letter-spacing: 0.2rpx;
}

.empty-em {
  color: #9d174d;
  font-weight: 600;
}

.footer-action {
  margin-top: 20rpx;
}

.reload-btn {
  width: 100%;
  margin-top: 36rpx;
  border: none;
  border-radius: 999px;
  height: 92rpx;
  line-height: 92rpx;
  padding: 0;
  color: #fff;
  background: linear-gradient(92deg, #db2777 0%, #ec4899 48%, #f43f7b 100%);
  font-size: 30rpx;
  box-shadow: 0 14rpx 36rpx rgba(219, 39, 119, 0.35);
}

.reload-btn::after {
  border: none;
}

.reload-btn-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #ffffff;
}

.empty-foot-note {
  margin-top: 20rpx;
  text-align: center;
  font-size: 22rpx;
  color: #b895a8;
  line-height: 1.5;
}
</style>
