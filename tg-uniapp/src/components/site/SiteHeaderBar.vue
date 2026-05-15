<script lang="ts" setup>
import { jump } from '@/utils/jump'
import { openTabBarPage } from '@/utils/tabBarNavigate'

defineOptions({
  name: 'SiteHeaderBar',
})

const brandName = import.meta.env.VITE_SITE_BRAND_NAME?.trim() || '首页'
const menuOpen = ref(false)

function closeMenu() {
  menuOpen.value = false
}

function goHome() {
  closeMenu()
  openTabBarPage('/pages/index/index')
}

function goPersonalCenter() {
  closeMenu()
  jump('/pages/me/me', '个人中心', { requireLogin: true })
}
</script>

<template>
  <view class="site-header-zone">
    <view class="site-shell">
      <header class="site-header">
        <text class="brand" @click="goHome">{{ brandName }}</text>
        <view class="header-actions">
          <button type="default" class="btn-menu" hover-class="btn-menu-hover" @click="menuOpen = true">
            我的
          </button>
        </view>
      </header>
    </view>
  </view>

  <view v-if="menuOpen" class="menu-mask" @click="closeMenu" />
  <view v-if="menuOpen" class="menu-panel menu-panel-compact">
    <view class="menu-item" @click="goPersonalCenter">
      <text class="menu-label">个人中心</text>
      <text class="menu-arrow">›</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.site-header-zone {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  padding-top: calc(env(safe-area-inset-top) + 12px);
}

.site-shell {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
  padding-left: 16px;
  padding-right: 16px;
}

@media (min-width: 768px) {
  .site-shell {
    padding-left: 32px;
    padding-right: 32px;
  }
}

.site-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.brand {
  font-size: 22px;
  font-weight: 700;
  color: #fbcfe8;
  letter-spacing: 0.02em;
}

.header-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.btn-menu {
  margin: 0;
  padding: 0 14px;
  height: 36px;
  line-height: 36px;
  font-size: 14px;
  color: #e2e8f0;
  background: rgba(15, 23, 42, 0.45);
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 999px;
}

.btn-menu::after {
  border: none;
}

.btn-menu-hover {
  opacity: 0.88;
}

.menu-mask {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(15, 23, 42, 0.35);
}

.menu-panel {
  position: fixed;
  top: calc(env(safe-area-inset-top) + 52px);
  right: calc((100vw - min(1200px, 100vw)) / 2 + 16px);
  z-index: 2001;
  width: min(300px, calc(100vw - 24px));
  padding: 8px 0;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 18px 40px rgba(148, 27, 83, 0.18);
  border: 1px solid #fce7f3;
}

.menu-panel-compact {
  width: min(240px, calc(100vw - 24px));
  padding: 6px 0;
}

@media (min-width: 768px) {
  .menu-panel {
    right: calc((100vw - min(1200px, 100vw)) / 2 + 32px);
  }
}

.menu-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
}

.menu-item:active {
  background: #fdf2f8;
}

.menu-label {
  font-size: 15px;
  color: #334155;
}

.menu-arrow {
  font-size: 18px;
  color: #cbd5e1;
  margin-left: 8px;
}
</style>
