<template>
  <view class="my-like-page">
    <scroll-view
      scroll-y
      class="list-scroll"
      :refresher-enabled="true"
      :refresher-triggered="isRefreshing"
      :lower-threshold="120"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view v-if="!initialLoaded" class="empty-box">
        加载中…
      </view>
      <view v-else-if="myLikeList.length === 0" class="empty-box">
        暂无我喜欢的人
      </view>

      <view v-else class="grid-list">
        <view
          v-for="item in myLikeList"
          :key="item.id"
          class="grid-card"
          @click="goProfile(item)"
        >
          <image class="cover" :src="item.avatar" mode="aspectFill" />
          <view class="heart-fab">
            <view v-if="item.mutual" class="hearts-linked" aria-label="相互喜欢">
              <text class="heart heart-left">♥</text>
              <text class="heart heart-right">♥</text>
            </view>
            <text v-else class="heart-single">♥</text>
          </view>
          <view class="mask">
            <view class="line1">
              <text class="name">{{ item.name }}</text>
              <text class="gender" :class="item.gender === 'male' ? 'male' : 'female'">
                {{ item.gender === 'male' ? '♂' : '♀' }}
              </text>
              <text v-if="item.age > 0" class="age">{{ item.age }}岁</text>
            </view>
            <view class="city">
              {{ item.city }}
            </view>
          </view>
        </view>

        <view class="list-footer">
          {{ loadMoreText }}
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { IDtPreferenceRecord } from '@/api/recommend'
import { getMyLikeList } from '@/api/recommend'

interface IMyLikeUser {
  id: string
  targetCusCode: string
  avatar: string
  name: string
  gender: 'male' | 'female'
  age: number
  city: string
  /** 与后端 preferenceMutuaStatusCode === '1' 一致 */
  mutual: boolean
}

defineOptions({
  name: 'MyLikeListPage',
})

definePage({
  needLogin: true,
  style: {
    navigationBarTitleText: '我喜欢',
  },
})

const pageNo = ref(1)
const pageSize = 6
const isRefreshing = ref(false)
const isLoadingMore = ref(false)
const hasMore = ref(true)
const myLikeList = ref<IMyLikeUser[]>([])
const initialLoaded = ref(false)
const listLoading = ref(false)

const loadMoreText = computed(() => {
  if (!initialLoaded.value) {
    return ''
  }
  if (isLoadingMore.value) {
    return '加载中...'
  }
  if (hasMore.value) {
    return '下滑加载更多'
  }
  return '没有更多了'
})

function resolveCusAvatar(avatar: unknown): string {
  const s = typeof avatar === 'string' ? avatar.trim() : ''
  if (!s) {
    return ''
  }
  return s.split(',')[0]?.trim() || ''
}

function mapSexToGender(cusSexCode: unknown): 'male' | 'female' {
  const sex = String(cusSexCode ?? '').toLowerCase()
  if (sex.includes('female') || sex === '2') {
    return 'female'
  }
  return 'male'
}

function isMutual(row: IDtPreferenceRecord): boolean {
  return String(row.preferenceMutuaStatusCode ?? '').trim() === '1'
}

/** 「我喜欢的」行：被喜欢对象为 preferenceTarget 字段 */
function mapRowToMyLike(row: IDtPreferenceRecord): IMyLikeUser {
  const target = String(row.preferenceTargetCusCode ?? '').trim()
  const rowId = String((row.id ?? target) || Math.random().toString(36).slice(2))
  const name = String(row.preferenceTargetCusName ?? '').trim() || (target ? '嘉宾' : '未知')
  const avatar = resolveCusAvatar(row.preferenceTargetCusAvatar) || '/static/avatar-default.svg'
  const city = String(row.preferenceTargetCusCityResidenceName ?? '').trim() || '—'
  return {
    id: rowId,
    targetCusCode: target,
    avatar,
    name,
    gender: mapSexToGender(row.preferenceTargetCusSexCode),
    age: Number(row.preferenceTargetCusAge ?? 0) || 0,
    city,
    mutual: isMutual(row),
  }
}

async function loadMyLikes(reset: boolean) {
  if (listLoading.value) {
    return
  }
  if (!reset && !hasMore.value) {
    return
  }
  const nextPage = reset ? 1 : pageNo.value
  listLoading.value = true
  try {
    const res = await getMyLikeList({ pageNo: nextPage, pageSize })
    const total = Number(res?.total ?? 0)
    const records = res?.records ?? []
    const chunk = records.map(row => mapRowToMyLike(row))
    if (reset) {
      myLikeList.value = chunk
    }
    else {
      myLikeList.value = [...myLikeList.value, ...chunk]
    }
    pageNo.value = nextPage + 1
    hasMore.value = myLikeList.value.length < total && records.length > 0
  }
  catch {
    if (reset) {
      myLikeList.value = []
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
  finally {
    listLoading.value = false
    initialLoaded.value = true
  }
}

async function onRefresh() {
  if (isRefreshing.value) {
    return
  }
  isRefreshing.value = true
  try {
    pageNo.value = 1
    hasMore.value = true
    await loadMyLikes(true)
  }
  finally {
    isRefreshing.value = false
  }
}

async function onLoadMore() {
  if (isLoadingMore.value || !hasMore.value || !initialLoaded.value) {
    return
  }
  isLoadingMore.value = true
  try {
    await loadMyLikes(false)
  }
  finally {
    isLoadingMore.value = false
  }
}

function goProfile(item: IMyLikeUser) {
  if (!item.targetCusCode) {
    return
  }
  uni.navigateTo({
    url: `/pages-sub/dating/customer/profile-card?cusCode=${encodeURIComponent(item.targetCusCode)}`,
  })
}

onLoad(() => {
  void loadMyLikes(true)
})
</script>

<style lang="scss" scoped>
.my-like-page {
  min-height: 100vh;
  background: #f6f8fc;
  padding: 20rpx 24rpx calc(env(safe-area-inset-bottom) + 20rpx);
  box-sizing: border-box;
}

.list-scroll {
  height: calc(100vh - 40rpx - env(safe-area-inset-bottom));
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

.grid-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.grid-card {
  position: relative;
  height: 330rpx;
  overflow: hidden;
  border-radius: 18rpx;
  background: #fff;
}

.cover {
  width: 100%;
  height: 100%;
  display: block;
}

.heart-fab {
  position: absolute;
  right: 10rpx;
  top: 10rpx;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 52rpx;
  height: 52rpx;
  padding: 0 12rpx;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 4rpx 14rpx rgba(244, 63, 123, 0.25);
  pointer-events: none;
}

.heart-single {
  color: #f43f7b;
  font-size: 34rpx;
  line-height: 1;
}

.hearts-linked {
  display: flex;
  align-items: center;
  justify-content: center;
}

.heart {
  color: #f43f7b;
  font-size: 28rpx;
  line-height: 1;
}

.heart-right {
  margin-left: -10rpx;
}

.mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12rpx 12rpx 14rpx;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(17, 20, 28, 0.8) 100%);
}

.line1 {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.name {
  color: #fff;
  font-size: 26rpx;
  font-weight: 600;
}

.gender {
  font-size: 24rpx;
  font-weight: 600;
}

.gender.male {
  color: #60a5fa;
}

.gender.female {
  color: #f472b6;
}

.age,
.city {
  color: #e5e7eb;
  font-size: 22rpx;
}

.city {
  margin-top: 4rpx;
}

.list-footer {
  grid-column: 1 / -1;
  text-align: center;
  color: #9aa5ba;
  font-size: 24rpx;
  padding: 10rpx 0 16rpx;
}
</style>
