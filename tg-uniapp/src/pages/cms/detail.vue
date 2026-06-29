<script lang="ts" setup>
defineOptions({
  name: 'CmsDetailPage',
})

definePage({
  excludeLoginPath: true,
  style: {
    navigationBarTitleText: '详情',
    navigationBarBackgroundColor: '#ffffff',
    backgroundColor: '#ffffff',
  },
})

import { onLoad } from '@dcloudio/uni-app'
import type { ICmsNodeDetail } from '@/api/home'
import { getCmsNodeDetailById } from '@/api/home'
import { ref } from 'vue'

const detail = ref<ICmsNodeDetail | null>(null)
const loading = ref(true)
const missing = ref(false)

onLoad((opts) => {
  const id = opts?.id?.trim()
  if (!id) {
    missing.value = true
    loading.value = false
    return
  }
  getCmsNodeDetailById(id)
    .then((res) => {
      detail.value = res || null
      if (res?.nodeName)
        uni.setNavigationBarTitle({ title: res.nodeName })
    })
    .catch(() => {
      missing.value = true
    })
    .finally(() => {
      loading.value = false
    })
})
</script>

<template>
  <view class="shell">
    <view v-if="loading" class="state">
      加载中…
    </view>
    <view v-else-if="missing || !detail" class="state">
      内容不存在或已下线
    </view>
    <view v-else class="article">
      <text class="title">{{ detail.nodeName }}</text>
      <text v-if="detail.nodePublishTime" class="meta">{{ detail.nodePublishTime }}</text>
      <text v-if="detail.nodeSummary" class="summary">{{ detail.nodeSummary }}</text>
      <rich-text v-if="detail.nodeContent" class="rich" :nodes="detail.nodeContent" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
.shell {
  min-height: 100vh;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 20px 48px;
  box-sizing: border-box;
  background: #fff;
}

.state {
  padding: 48px 16px;
  text-align: center;
  color: #64748b;
  font-size: 15px;
}

.article {
  width: 100%;
}

.title {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.35;
}

.meta {
  display: block;
  margin-top: 10px;
  font-size: 13px;
  color: #94a3b8;
}

.summary {
  display: block;
  margin-top: 16px;
  font-size: 15px;
  color: #475569;
  line-height: 1.6;
}

.rich {
  display: block;
  margin-top: 24px;
  font-size: 15px;
  color: #334155;
  line-height: 1.75;
  overflow-wrap: anywhere;
}
</style>
