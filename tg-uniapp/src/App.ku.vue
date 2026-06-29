<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import LoginModal from '@/components/login/LoginModal.vue'
import FgTabbar from '@/tabbar/index.vue'
import { selectedTabbarStrategy, TABBAR_STRATEGY_MAP } from './tabbar/config'
import { isPageTabbar } from './tabbar/store'
import { currRoute } from './utils'

/** 统一 path，避免 hash、缺少前缀时误判，导致底栏盖住 WebView 内嵌页（如 /pages/h5/intake） */
function normalizeRoutePath(raw: string) {
  let p = String(raw || '').split('?')[0].trim()
  const hashIdx = p.indexOf('#')
  if (hashIdx !== -1)
    p = p.slice(hashIdx + 1)
  if (p && !p.startsWith('/'))
    p = `/${p}`
  return p
}

function computeShowTabbar(): boolean {
  if (selectedTabbarStrategy === TABBAR_STRATEGY_MAP.NO_TABBAR)
    return false
  const { path: rawPath } = currRoute()
  const path = normalizeRoutePath(rawPath)
  if (path === '/' || path === '')
    return true
  // 独立 H5 子包页：严禁显示自定义底栏（z-index 1000 会挡住页面内 fixed 操作条）
  if (path.startsWith('/pages/h5/'))
    return false
  return isPageTabbar(path)
}

const isCurrentPageTabbar = ref(false)

function syncTabbarVisibility() {
  try {
    isCurrentPageTabbar.value = computeShowTabbar()
    console.log('App.ku.vue tabbar', { route: currRoute(), show: isCurrentPageTabbar.value })
  }
  catch (e) {
    console.warn('App.ku.vue syncTabbarVisibility', e)
    isCurrentPageTabbar.value = false
  }
}

onShow(() => {
  syncTabbarVisibility()
})

onMounted(() => {
  syncTabbarVisibility()
  nextTick(syncTabbarVisibility)
})

const helloKuRoot = ref('Hello AppKuVue')

const exposeRef = ref('this is form app.Ku.vue')

defineExpose({
  exposeRef,
})
</script>

<template>
  <view>
    <!-- 这个先隐藏了，知道这样用就行 -->
    <view class="hidden text-center">
      {{ helloKuRoot }}，这里可以配置全局的东西
    </view>

    <KuRootView />

    <FgTabbar v-if="isCurrentPageTabbar" />

    <LoginModal />
  </view>
</template>
