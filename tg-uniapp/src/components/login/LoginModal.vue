<script lang="ts" setup>
import { computed, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { sendSmsCode } from '@/api/login'
import { useLoginModalStore } from '@/store/loginModal'
import { useTokenStore } from '@/store/token'
import { openPrivacyPolicy, openUserAgreement } from '@/utils/legalPages'

defineOptions({
  name: 'LoginModal',
})

const loginModalStore = useLoginModalStore()
const { visible } = storeToRefs(loginModalStore)
const tokenStore = useTokenStore()

const phone = ref('')
const smsCode = ref('')
const agreed = ref(false)
const countdown = ref(0)
const submitting = ref(false)

let timer: ReturnType<typeof setInterval> | null = null

function resetForm() {
  phone.value = ''
  smsCode.value = ''
  agreed.value = false
  countdown.value = 0
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

watch(visible, (v) => {
  if (v)
    resetForm()
})

function isValidPhone(p: string) {
  return /^1\d{10}$/.test(String(p).trim())
}

function startCountdown() {
  countdown.value = 60
  if (timer)
    clearInterval(timer)
  timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0 && timer) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

const sendDisabled = computed(() => countdown.value > 0 || !isValidPhone(phone.value))

function onPhoneInput(e: { detail?: { value?: string } }) {
  phone.value = String(e.detail?.value ?? '').replace(/\D/g, '').slice(0, 11)
}

function onSmsInput(e: { detail?: { value?: string } }) {
  smsCode.value = String(e.detail?.value ?? '').replace(/\D/g, '').slice(0, 6)
}

async function handleSendSms() {
  if (!isValidPhone(phone.value)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  try {
    await sendSmsCode({
      phone: phone.value.trim(),
      code: '',
      captchaKey: '',
    })
    uni.showToast({ title: '短信已发送', icon: 'success' })
    startCountdown()
  }
  catch {
    // http 层 toast
  }
}

/** 登录成功后留在当前页：不执行待跳转、不做路由刷新（token 已在 store.login 中更新） */
function runAfterLoginSuccess() {
  loginModalStore.close()
}

async function handleLogin() {
  if (!agreed.value) {
    uni.showToast({ title: '请先阅读并同意协议', icon: 'none' })
    return
  }
  if (!isValidPhone(phone.value) || !smsCode.value.trim()) {
    uni.showToast({ title: '请输入手机号与验证码', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await tokenStore.login({
      phone: phone.value.trim(),
      smsAuthCode: smsCode.value.trim(),
    })
    runAfterLoginSuccess()
  }
  finally {
    submitting.value = false
  }
}

function handleClose() {
  resetForm()
  loginModalStore.close()
}
</script>

<template>
  <view v-if="visible" class="login-modal-root" @click="handleClose">
    <view class="modal-sheet" @click.stop>
      <view class="sheet-inner">
        <text class="welcome">手机号验证码登录</text>

        <view class="field">
          <text class="label">手机号</text>
          <input
            :value="phone"
            class="input"
            type="text"
            inputmode="numeric"
            :maxlength="11"
            placeholder="请输入手机号"
            placeholder-class="ph"
            @input="onPhoneInput"
          >
        </view>

        <view class="field row-code">
          <view class="code-main">
            <text class="label">验证码</text>
            <input
              :value="smsCode"
              class="input"
              type="text"
              inputmode="numeric"
              :maxlength="6"
              placeholder="短信验证码"
              placeholder-class="ph"
              @input="onSmsInput"
            >
          </view>
          <button
            class="btn-code"
            :disabled="sendDisabled"
            hover-class="btn-code-hover"
            @click="handleSendSms"
          >
            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
          </button>
        </view>

        <label class="agree-row">
          <checkbox :checked="agreed" color="#018d71" style="transform: scale(0.86)" @tap.stop="agreed = !agreed" />
          <text class="agree-text">我已阅读并同意</text>
          <text class="link" @click.stop="openUserAgreement">《用户协议》</text>
          <text class="agree-text">与</text>
          <text class="link" @click.stop="openPrivacyPolicy">《隐私政策》</text>
        </label>

        <button
          class="btn-submit"
          :disabled="submitting"
          hover-class="btn-submit-hover"
          @click="handleLogin"
        >
          {{ submitting ? '登录中…' : '登录' }}
        </button>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.login-modal-root {
  position: fixed;
  inset: 0;
  z-index: 9998;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: calc(24rpx + env(safe-area-inset-top)) 28rpx calc(24rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  /* 不铺底色，首页/当前页在下方可见；仅全屏接收「点空白关闭」 */
  background: transparent;
}

.modal-sheet {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 520px;
  box-sizing: border-box;
}

.sheet-inner {
  width: 100%;
  max-width: 440px;
  margin: 0 auto;
  padding: 32rpx 28rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.97);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow:
    0 4rpx 24rpx rgba(15, 23, 42, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.55) inset;
  box-sizing: border-box;
}

.welcome {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #2d2430;
  margin-bottom: 40rpx;
}

.field {
  margin-bottom: 28rpx;
}

.row-code {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
  gap: 16rpx;
}

.code-main {
  flex: 1;
  min-width: 0;
}

.label {
  display: block;
  font-size: 26rpx;
  color: #5c4d56;
  margin-bottom: 12rpx;
}

.input {
  width: 100%;
  height: 96rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
  font-size: 30rpx;
  color: #2d2430;
  background: #f5f3f6;
  border-radius: 16rpx;
  border: 1px solid transparent;
}

.ph {
  color: #b5a8b0;
}

.btn-code {
  flex-shrink: 0;
  height: 96rpx;
  line-height: 96rpx;
  padding: 0 24rpx;
  margin: 0;
  font-size: 26rpx;
  color: #018d71;
  background: rgba(1, 141, 113, 0.1);
  border-radius: 16rpx;
  border: none;
}

.btn-code::after {
  border: none;
}

.btn-code[disabled] {
  opacity: 0.45;
  color: #666;
}

.btn-code-hover {
  opacity: 0.85;
}

.agree-row {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 36rpx;
  font-size: 24rpx;
  color: #6b5a66;
  line-height: 1.6;
}

.agree-text {
  margin-right: 4rpx;
}

.link {
  color: #018d71;
}

.btn-submit {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  margin: 0;
  padding: 0;
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #019f7c 0%, #018d71 100%);
  border-radius: 48rpx;
  border: none;
}

.btn-submit::after {
  border: none;
}

.btn-submit[disabled] {
  opacity: 0.55;
}

.btn-submit-hover {
  opacity: 0.92;
}
</style>
