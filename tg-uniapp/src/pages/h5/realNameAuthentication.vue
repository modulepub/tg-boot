<script lang="ts" setup>
import { openNativeRealnamePage } from '@/utils/openNativeRealname'

defineOptions({
  name: 'H5RealNameAuthenticationPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '实名认证',
    navigationBarBackgroundColor: '#f4f6fb',
    backgroundColor: '#f4f6fb',
  },
})

const realnameInfo = reactive({
  name: '',
  idCard: '',
})

const submitting = ref(false)

onLoad((options) => {
  realnameInfo.name = decodeURIComponent(String(options?.name || '')).trim()
  realnameInfo.idCard = decodeURIComponent(String(options?.idCard || '')).trim()
})

function maskIdCard(idCard: string) {
  if (!idCard || idCard.length < 8)
    return idCard || ''
  return `${idCard.slice(0, 4)}********${idCard.slice(-4)}`
}

async function startRealname() {
  const name = realnameInfo.name.trim()
  const idCard = realnameInfo.idCard.trim()
  if (!name) {
    uni.showToast({ title: '请填写姓名', icon: 'none' })
    return
  }
  if (!idCard || idCard.length < 15) {
    uni.showToast({ title: '请填写正确身份证号', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const opened = await openNativeRealnamePage(name, idCard)
    if (opened)
      return

    const fallbackUrl = import.meta.env.VITE_TENCENT_REALNAME_URL?.trim()
    if (fallbackUrl) {
      // #ifdef H5
      window.location.href = fallbackUrl
      // #endif
      return
    }

    uni.showToast({
      title: '请在微信小程序或 App 内打开本页',
      icon: 'none',
    })
  }
  finally {
    submitting.value = false
  }
}
</script>

<template>
  <view class="realname-page">
    <view class="hero">
      <view class="hero-mark" aria-hidden="true">
        <text class="hero-mark-icon">✓</text>
      </view>
      <text class="hero-title">实名认证</text>
      <text class="hero-desc">信息仅用于身份核验，填写后将跳转至微信认证平台进行实名认证。</text>
    </view>

    <view class="form-card">
      <view class="field">
        <text class="label">姓名</text>
        <view class="input-shell">
          <input
            v-model="realnameInfo.name"
            class="input"
            type="text"
            maxlength="32"
            placeholder="与证件保持一致"
            placeholder-class="input-ph"
          />
        </view>
      </view>

      <view class="field">
        <text class="label">身份证号</text>
        <view class="input-shell">
          <input
            v-model="realnameInfo.idCard"
            class="input"
            type="idcard"
            maxlength="18"
            placeholder="18 位身份证号码"
            placeholder-class="input-ph"
          />
        </view>
      </view>

      <view v-if="realnameInfo.idCard.length >= 8" class="preview">
        <text class="preview-label">脱敏预览</text>
        <text class="preview-value">{{ maskIdCard(realnameInfo.idCard) }}</text>
      </view>
    </view>

    <button
      class="submit-btn"
      :class="{ 'submit-btn--busy': submitting }"
      :disabled="submitting"
      :loading="submitting"
      hover-class="submit-btn--hover"
      @click="startRealname"
    >
      <view class="submit-btn-body">
        <text class="submit-btn-title">{{ submitting ? '正在跳转…' : '发起实名认证' }}</text>
        <text v-if="!submitting" class="submit-btn-caption">进入安全核验流程</text>
      </view>
    </button>
  </view>
</template>

<style lang="scss" scoped>
.realname-page {
  min-height: 100vh;
  padding: calc(20rpx + env(safe-area-inset-top)) 32rpx calc(28rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: linear-gradient(180deg, #eef2ff 0%, #f4f6fb 22%, #f8fafc 100%);
}

.hero {
  margin-bottom: 28rpx;
  padding: 36rpx 8rpx 8rpx;
  text-align: center;
}

.hero-mark {
  margin: 0 auto 24rpx;
  width: 96rpx;
  height: 96rpx;
  border-radius: 28rpx;
  background: linear-gradient(145deg, #fff 0%, #eef2ff 100%);
  box-shadow:
    0 12rpx 32rpx rgba(79, 70, 229, 0.12),
    inset 0 1rpx 0 rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-mark-icon {
  font-size: 44rpx;
  font-weight: 700;
  color: #4f46e5;
  line-height: 1;
}

.hero-title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
  color: #1e293b;
  letter-spacing: 1rpx;
}

.hero-desc {
  display: block;
  margin-top: 16rpx;
  padding: 0 16rpx;
  font-size: 26rpx;
  line-height: 1.55;
  color: #64748b;
}

.form-card {
  border-radius: 24rpx;
  padding: 36rpx 32rpx 32rpx;
  background: #fff;
  box-shadow:
    0 8rpx 40rpx rgba(15, 23, 42, 0.06),
    0 1rpx 0 rgba(255, 255, 255, 0.8) inset;
}

.field + .field {
  margin-top: 28rpx;
}

.label {
  display: block;
  margin-bottom: 14rpx;
  font-size: 26rpx;
  font-weight: 500;
  color: #475569;
}

.input-shell {
  border-radius: 16rpx;
  background: #f1f5f9;
  border: 2rpx solid transparent;
  padding: 4rpx 24rpx;
  transition: border-color 0.15s ease, background 0.15s ease;
}

.input-shell:focus-within {
  background: #fff;
  border-color: #c7d2fe;
}

.input {
  width: 100%;
  height: 80rpx;
  font-size: 30rpx;
  color: #0f172a;
  box-sizing: border-box;
}

.input-ph {
  color: #94a3b8;
  font-size: 28rpx;
}

.preview {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.preview-label {
  font-size: 24rpx;
  color: #94a3b8;
}

.preview-value {
  font-size: 26rpx;
  color: #64748b;
  font-variant-numeric: tabular-nums;
}

.submit-btn {
  margin-top: 40rpx;
  width: 100%;
  height: auto;
  min-height: 112rpx;
  padding: 24rpx 32rpx;
  border-radius: 20rpx;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 48%, #4338ca 100%);
  box-shadow: 0 20rpx 48rpx rgba(79, 70, 229, 0.35);
}

.submit-btn::after {
  border: none;
}

.submit-btn--hover {
  opacity: 0.94;
  transform: translateY(1rpx);
}

.submit-btn--busy {
  box-shadow: 0 12rpx 28rpx rgba(79, 70, 229, 0.22);
}

.submit-btn[disabled] {
  opacity: 0.72;
}

.submit-btn-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
}

.submit-btn-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
  line-height: 1.25;
}

.submit-btn-caption {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.88);
  font-weight: 400;
}
</style>
