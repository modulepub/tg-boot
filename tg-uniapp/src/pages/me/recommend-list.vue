<template>
  <view class="recommend-list-page">
    <view class="top-bar">
      <view class="top-title">
        推荐列表
      </view>
      <view class="intent-btn" @click="goEditIntent">
        修改推荐意向
      </view>
    </view>
    <view class="total-row">
      共 {{ recommendTotal }} 人
    </view>
    <view class="more-btn" @click="goBenefits">
      获取更多推荐
    </view>

    <scroll-view
      scroll-y
      class="list-scroll"
      :lower-threshold="120"
      @scrolltolower="onScrollToLower"
    >
      <view v-if="!loading && recommendList.length === 0" class="empty-box">
        暂无推荐数据
      </view>

      <view v-else class="list-wrap">
        <view
          v-for="item in recommendList"
          :key="item.rowKey"
          class="list-item"
          @click="goProfile(item)"
        >
          <image :src="item.avatar" mode="aspectFill" class="avatar" />
          <view class="main">
            <view class="name-row">
              <text class="name">{{ item.name }}</text>
              <text class="gender" :class="item.gender === 'female' ? 'female' : 'male'">
                {{ item.gender === 'female' ? '♀' : '♂' }}
              </text>
              <text class="meta">{{ item.age }}岁 · {{ item.city }}</text>
            </view>

            <view class="intent">
              推荐意向：{{ item.intentLine }}
            </view>

            <view v-if="item.matchPercent != null" class="match">
              匹配度 {{ item.matchPercent }}%
            </view>
          </view>
          <text class="arrow">›</text>
        </view>
      </view>

      <view v-if="loading && recommendList.length === 0" class="hint">
        加载中…
      </view>
      <view v-else-if="loadingMore" class="hint">
        加载更多…
      </view>
      <view v-else-if="listFinished && recommendList.length > 0" class="hint muted">
        没有更多了
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { IRecommendHistoryRecord } from '@/api/recommend'
import { getRecommendHistoryList } from '@/api/recommend'

interface IListRow {
  rowKey: string
  cusCode: string
  avatar: string
  name: string
  gender: 'male' | 'female'
  age: number
  city: string
  intentLine: string
  /** 推荐意向是否接受残疾优先（传给资料卡 disabled） */
  acceptDisabledPriority: boolean
  matchPercent?: number
}

defineOptions({
  name: 'MyRecommendListPage',
})

definePage({
  needLogin: true,
  style: {
    navigationBarTitleText: '推荐列表',
  },
})

const pageNo = ref(1)
const pageSize = 10
const recommendTotal = ref(0)
const recommendList = ref<IListRow[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const listFinished = ref(false)

function isFemaleSexCode(raw: unknown): boolean {
  const s = String(raw ?? '').trim().toLowerCase()
  return s.includes('female') || s === '2'
}

function isAffirmativeCode(raw: unknown): boolean {
  const s = String(raw ?? '').trim().toLowerCase()
  return s === '1' || s === 'y' || s === 'true' || s === 'yes'
}

function resolveCusAvatar(avatar: unknown) {
  const first = String(avatar ?? '')
    .split(',')
    .map(item => item.trim())
    .find(Boolean)
  return first || '/static/avatar-default.svg'
}

function formatAgeRange(min?: number, max?: number) {
  const hasMin = min != null && !Number.isNaN(Number(min))
  const hasMax = max != null && !Number.isNaN(Number(max))
  if (hasMin && hasMax)
    return `${min}-${max}岁`
  if (hasMin)
    return `${min}岁起`
  if (hasMax)
    return `${max}岁及以下`
  return '年龄不限'
}

function formatIntentLine(row: IRecommendHistoryRecord): string {
  const age = formatAgeRange(row.intentionMinAge, row.intentionMaxAge)
  const city = String(row.intentionCityCode ?? '').trim() || '城市不限'
  const house = isAffirmativeCode(row.intentionHaveHouseCode) ? '要求有房' : '不要求房'
  const car = isAffirmativeCode(row.intentionHaveCarCode) ? '要求有车' : '不要求车'
  const dis = isAffirmativeCode(row.intentionDisabledStatusCode) ? '接受残疾优先' : '不接受残疾优先'
  return `${age}，${city}，${house}，${car}，${dis}`
}

function mapRecord(row: IRecommendHistoryRecord): IListRow | null {
  const cusCode = String(row.cusCode ?? '').trim()
  if (!cusCode)
    return null
  const id = String(row.id ?? '').trim()
  return {
    rowKey: id || cusCode,
    cusCode,
    avatar: resolveCusAvatar(row.cusAvatar),
    name: String(row.cusName ?? '').trim() || '用户',
    gender: isFemaleSexCode(row.cusSexCode) ? 'female' : 'male',
    age: Number(row.cusAge ?? 0) || 0,
    city: String(row.cusCityResidenceName ?? '').trim() || '未知城市',
    intentLine: formatIntentLine(row),
    acceptDisabledPriority: isAffirmativeCode(row.intentionDisabledStatusCode),
    matchPercent: row.matchPercent != null && !Number.isNaN(Number(row.matchPercent))
      ? Number(row.matchPercent)
      : undefined,
  }
}

async function loadHistory(reset: boolean) {
  if (loading.value || loadingMore.value)
    return
  if (!reset && listFinished.value)
    return

  const nextPage = reset ? 1 : pageNo.value
  if (reset) {
    loading.value = true
    listFinished.value = false
  }
  else {
    loadingMore.value = true
  }

  try {
    const res = await getRecommendHistoryList({ pageNo: nextPage, pageSize })
    const raw = res?.records ?? []
    const mapped = raw.map(mapRecord).filter((x): x is IListRow => x != null)
    const total = Number(res?.total ?? 0)
    recommendTotal.value = total
    if (reset)
      recommendList.value = mapped
    else
      recommendList.value = [...recommendList.value, ...mapped]

    pageNo.value = nextPage + 1
    const noMore = mapped.length === 0 || recommendList.value.length >= total
    listFinished.value = noMore || raw.length < pageSize
  }
  catch {
    if (reset) {
      recommendList.value = []
      recommendTotal.value = 0
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
    loadingMore.value = false
  }
}

function onScrollToLower() {
  void loadHistory(false)
}

onMounted(() => {
  void loadHistory(true)
})

function goProfile(item: IListRow) {
  const disabled = item.acceptDisabledPriority ? '1' : '0'
  uni.navigateTo({
    url: `/pages-sub/dating/customer/profile-card?cusCode=${encodeURIComponent(item.cusCode)}&disabled=${disabled}`,
  })
}

function goEditIntent() {
  uni.navigateTo({
    url: '/pages/recommend/edit-intent',
  })
}

function goBenefits() {
  uni.navigateTo({
    url: '/pages-sub/dating/customer/benefits',
  })
}
</script>

<style lang="scss" scoped>
.recommend-list-page {
  min-height: 100vh;
  background: #f6f8fc;
  padding: 20rpx 24rpx calc(env(safe-area-inset-bottom) + 20rpx);
  box-sizing: border-box;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.top-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #2a3247;
}

.intent-btn {
  padding: 10rpx 20rpx;
  border-radius: 999px;
  font-size: 24rpx;
  color: #fff;
  background: linear-gradient(90deg, #5e72f4 0%, #7c5cf6 100%);
}

.list-scroll {
  height: calc(100vh - 186rpx - env(safe-area-inset-bottom));
}

.total-row {
  margin-bottom: 14rpx;
  color: #7f8aa3;
  font-size: 25rpx;
}

.more-btn {
  margin-bottom: 16rpx;
  border-radius: 999px;
  height: 66rpx;
  line-height: 66rpx;
  text-align: center;
  font-size: 26rpx;
  color: #7c5cf6;
  background: #f3f0ff;
}

.empty-box {
  margin-top: 24rpx;
  border-radius: 20rpx;
  background: #fff;
  padding: 60rpx 20rpx;
  text-align: center;
  color: #9aa5ba;
  font-size: 28rpx;
}

.hint {
  text-align: center;
  padding: 24rpx 0 8rpx;
  font-size: 24rpx;
  color: #7f8aa3;
}

.hint.muted {
  color: #b3bbcc;
}

.list-wrap {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  border-radius: 20rpx;
  background: #fff;
  padding: 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(49, 63, 96, 0.07);
}

.avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  flex-shrink: 0;
}

.main {
  min-width: 0;
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.name {
  font-size: 30rpx;
  font-weight: 600;
  color: #2a3247;
}

.gender {
  font-size: 30rpx;
  font-weight: 600;
}

.gender.male {
  color: #3b82f6;
}

.gender.female {
  color: #ec4899;
}

.meta {
  font-size: 24rpx;
  color: #7f8aa3;
}

.intent {
  margin-top: 8rpx;
  color: #5f6d86;
  font-size: 24rpx;
  line-height: 1.5;
}

.match {
  margin-top: 8rpx;
  color: #16a34a;
  font-size: 24rpx;
  font-weight: 600;
}

.arrow {
  color: #b3bbcc;
  font-size: 34rpx;
  line-height: 1;
}
</style>
