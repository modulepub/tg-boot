<template>
  <view class="liked-page">
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
      <view v-else-if="likedList.length === 0" class="empty-box">
        暂无喜欢你的人
      </view>

      <view v-else class="grid-list">
        <view
          v-for="item in likedList"
          :key="item.id"
          class="grid-card"
          @click="goProfile(item)"
        >
          <image class="cover" :src="item.avatar" mode="aspectFill" />
          <view
            class="heart-fab"
            :class="{ disabled: item.mutual || heartLoadingId === item.id }"
            @click.stop="onHeartTap(item)"
          >
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
import { getLikeMeList, savePreference } from '@/api/recommend'

interface ILikedUser {
  id: string
  peerCusCode: string
  avatar: string
  name: string
  gender: 'male' | 'female'
  age: number
  city: string
  /** 与后端 preferenceMutuaStatusCode === '1' 一致 */
  mutual: boolean
}

defineOptions({
  name: 'LikedMeListPage',
})

definePage({
  needLogin: true,
  style: {
    navigationBarTitleText: '喜欢我',
  },
})

const likedList = ref<ILikedUser[]>([])
const pageNo = ref(1)
const pageSize = 6
const isRefreshing = ref(false)
const isLoadingMore = ref(false)
const hasMore = ref(true)
const initialLoaded = ref(false)
const listLoading = ref(false)
const heartLoadingId = ref<string | null>(null)

const loadMoreText = computed(() => {
  if (!initialLoaded.value) {
    return ''
  }
  if (isLoadingMore.value) {
    return '加载中...'
  }
  if (hasMore.value) {
    return '上滑加载更多'
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

/** 「喜欢我的」行：对方为 preferenceCus*（发起方），与后端 DtPreference 注解一致 */
function mapRowToLiked(row: IDtPreferenceRecord): ILikedUser {
  const peer = String(row.preferenceCusCode ?? '').trim()
  const rowId = String((row.id ?? peer) || Math.random().toString(36).slice(2))
  const mutual = isMutual(row)
  const name = String(row.preferenceCusName ?? '').trim() || (peer ? '嘉宾' : '未知')
  const avatar = resolveCusAvatar(row.preferenceCusAvatar) || '/static/avatar-default.svg'
  const city = String(row.preferenceCusCityResidenceName ?? '').trim() || '—'
  return {
    id: rowId,
    peerCusCode: peer,
    avatar,
    name,
    gender: mapSexToGender(row.preferenceCusSexCode),
    age: Number(row.preferenceCusAge ?? 0) || 0,
    city,
    mutual,
  }
}

async function loadLiked(reset: boolean) {
  if (listLoading.value) {
    return
  }
  if (!reset && !hasMore.value) {
    return
  }
  const nextPage = reset ? 1 : pageNo.value
  listLoading.value = true
  try {
    const res = await getLikeMeList({ pageNo: nextPage, pageSize })
    const total = Number(res?.total ?? 0)
    const records = res?.records ?? []
    const chunk = records.map(row => mapRowToLiked(row))
    if (reset) {
      likedList.value = chunk
    }
    else {
      likedList.value = [...likedList.value, ...chunk]
    }
    pageNo.value = nextPage + 1
    hasMore.value = likedList.value.length < total && records.length > 0
  }
  catch {
    if (reset) {
      likedList.value = []
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
    await loadLiked(true)
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
    await loadLiked(false)
  }
  finally {
    isLoadingMore.value = false
  }
}

async function onHeartTap(item: ILikedUser) {
  if (!item.peerCusCode || item.mutual || heartLoadingId.value) {
    return
  }
  heartLoadingId.value = item.id
  try {
    await savePreference({
      preferenceTargetCusCode: item.peerCusCode,
      preferenceLikeStatusCode: '1',
    })
    uni.showToast({ title: '已喜欢', icon: 'success' })
    pageNo.value = 1
    hasMore.value = true
    await loadLiked(true)
  }
  catch {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
  finally {
    heartLoadingId.value = null
  }
}

function goProfile(user: ILikedUser) {
  if (!user.peerCusCode) {
    return
  }
  uni.navigateTo({
    url: `/pages-sub/dating/customer/profile-card?cusCode=${encodeURIComponent(user.peerCusCode)}`,
  })
}

onLoad(() => {
  void loadLiked(true)
})
</script>

<style lang="scss" scoped>
.liked-page {
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
}

.heart-fab.disabled {
  pointer-events: none;
  opacity: 0.92;
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
