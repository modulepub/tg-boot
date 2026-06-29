<script lang="ts" setup>
import type { ILoginForm } from '@/api/login'
import { sendSmsCode } from '@/api/login'
import type { ICustomerInfoRes } from '@/api/types/login'
import { editCurrCusInfo } from '@/api/customer'
import type { IDictArea } from '@/api/dict-area'
import { searchDictArea } from '@/api/dict-area'
import { isBizSuccess } from '@/http/tools/bizResponse'
import { useCustomerStore } from '@/store/customer'
import { useTokenStore } from '@/store/token'
import { getEnvBaseUrl } from '@/utils'
import { jump } from '@/utils/jump'
import { nextTick } from 'vue'
import { openPrivacyPolicy, openUserAgreement } from '@/utils/legalPages'

interface IPhotoGroup {
  label: string
  desc: string
  files: string[]
  maxCount: number
  single?: boolean
}

interface IBaseForm {
  /** 展示用：一般为 `dictAreaFullName` */
  city: string
  /** 地区业务编码 `dictAreaCode` */
  cityCode: string
  /** 职业 */
  occupation: string
  hasHouse: boolean
  hasCar: boolean
  hasDisability: boolean
  /** 是否二婚 */
  isRemarriage: boolean
  /** 说说 */
  say: string
}

defineOptions({
  name: 'CustomerIntakePhoto',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '形象上传',
    navigationBarBackgroundColor: '#f5f3f6',
    backgroundColor: '#f5f3f6',
  },
})

const photoGroups = reactive<Record<string, IPhotoGroup>>({
  avatar: {
    label: '头像',
    desc: '必填，只能上传 1 张，建议清晰正脸照。',
    files: [],
    maxCount: 1,
    single: true,
  },
  life: {
    label: '分享生活',
    desc: '选填，可上传多张，展示你的日常状态。',
    files: [],
    maxCount: 9,
  },
  youth: {
    label: '那些年',
    desc: '选填，可上传多张，分享你青春时期的照片。',
    files: [],
    maxCount: 9,
  },
})

const baseForm = reactive<IBaseForm>({
  city: '',
  cityCode: '',
  occupation: '',
  hasHouse: false,
  hasCar: false,
  hasDisability: false,
  isRemarriage: false,
  say: '',
})

type IntakeBoolKey = 'hasHouse' | 'hasCar' | 'hasDisability' | 'isRemarriage'

/** 微信 webview 内点开关会触发布局并重算滚动，易出现顶部大白条；切状态后把滚动位置钉回去 */
function toggleIntakeBool(key: IntakeBoolKey) {
  let y = 0
  if (typeof window !== 'undefined')
    y = window.scrollY || document.documentElement.scrollTop || 0

  baseForm[key] = !baseForm[key]

  if (typeof window === 'undefined')
    return

  nextTick(() => {
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        window.scrollTo(0, y)
      })
    })
  })
}

const showCityPicker = ref(false)
const citySearchInput = ref('')
const cityKeyword = ref('')
const cityList = ref<IDictArea[]>([])
const cityLoading = ref(false)
interface ICityBrowseCrumb {
  code: string | null
  label: string
}
const cityBrowseStack = ref<ICityBrowseCrumb[]>([{ code: null, label: '地区' }])
let citySearchDebounceTimer: ReturnType<typeof setTimeout> | undefined

/** 直辖市「市本级」行政区划码（level=2 视为已选到市） */
const MUNICIPALITY_ROOT_CODES = new Set(['CN-110000', 'CN-310000', 'CN-120000', 'CN-500000'])

/** 是否满足「常驻城市」：地级市/区县（level≥3），或直辖市市本级（上列 code 且 level=2） */
function isResidenceCityLevel(row: IDictArea) {
  const lv = row.dictAreaLevel
  if (lv == null)
    return false
  if (lv >= 3)
    return true
  const code = String(row.dictAreaCode ?? '').trim()
  return lv === 2 && MUNICIPALITY_ROOT_CODES.has(code)
}

const cityBrowseTailCode = computed(() => {
  const tail = cityBrowseStack.value[cityBrowseStack.value.length - 1]
  return tail?.code ?? null
})

watch([cityKeyword, cityBrowseStack], () => {
  if (showCityPicker.value)
    void loadCityAreaList()
}, { deep: true })

async function loadCityAreaList() {
  cityLoading.value = true
  try {
    const kw = cityKeyword.value.trim()
    const parentCode = kw ? undefined : (cityBrowseTailCode.value ?? undefined)
    const page = await searchDictArea({
      keyword: kw || undefined,
      parentCode,
      pageNo: 1,
      pageSize: 50,
    })
    cityList.value = page?.records ?? []
  }
  catch {
    cityList.value = []
  }
  finally {
    cityLoading.value = false
  }
}

function onCitySearchInput() {
  if (citySearchDebounceTimer)
    clearTimeout(citySearchDebounceTimer)
  citySearchDebounceTimer = setTimeout(() => {
    cityKeyword.value = citySearchInput.value.trim()
  }, 320)
}

function openCityPicker() {
  showCityPicker.value = true
  citySearchInput.value = ''
  cityKeyword.value = ''
  cityBrowseStack.value = [{ code: null, label: '地区' }]
}

function closeCityPicker() {
  showCityPicker.value = false
}

function canDrillArea(row: IDictArea) {
  const lv = row.dictAreaLevel
  return lv != null && lv < 3 && !cityKeyword.value.trim()
}

function drillArea(row: IDictArea) {
  const code = String(row.dictAreaCode ?? '').trim()
  const label = String(row.dictAreaName ?? row.dictAreaFullName ?? code).trim()
  if (!code)
    return
  citySearchInput.value = ''
  cityKeyword.value = ''
  cityBrowseStack.value = [...cityBrowseStack.value, { code, label }]
}

function popCityBrowse(index: number) {
  if (index < 0 || index >= cityBrowseStack.value.length - 1)
    return
  cityBrowseStack.value = cityBrowseStack.value.slice(0, index + 1)
  citySearchInput.value = ''
  cityKeyword.value = ''
}

function pickCityArea(row: IDictArea) {
  if (!isResidenceCityLevel(row)) {
    uni.showToast({
      title: '请选至市级：地级市/区县，或直辖市市本级',
      icon: 'none',
    })
    return
  }
  baseForm.city = String(row.dictAreaFullName ?? row.dictAreaName ?? '').trim()
  baseForm.cityCode = String(row.dictAreaCode ?? '').trim()
  closeCityPicker()
}

function cityAreaRowLabel(row: IDictArea) {
  return String(row.dictAreaFullName ?? row.dictAreaName ?? '').trim() || '—'
}

const tokenStore = useTokenStore()
const customerStore = useCustomerStore()

const stepSaving = ref(false)
const showLoginSheet = ref(false)
const goNextAfterLogin = ref(false)
const loginAgree = ref(true)
const loginLoading = ref(false)
const loginCountdown = ref(0)
const loginForm = reactive<ILoginForm>({
  phone: '',
  smsAuthCode: '',
})

const loginSmsBtnText = computed(() => (loginCountdown.value > 0 ? `${loginCountdown.value}s后重发` : '获取验证码'))
const loginSmsDisabled = computed(() => loginCountdown.value > 0 || !isValidPhone(loginForm.phone))
const loginCanSubmit = computed(() =>
  isValidPhone(loginForm.phone) && !!loginForm.smsAuthCode.trim() && loginAgree.value && !loginLoading.value,
)

function isValidPhone(phone: string) {
  return /^(?:(?:\+|00)86)?1[3-9]\d{9}$/.test(phone)
}

function boolToDictCode(v: boolean) {
  return v ? '1' : '0'
}

function isAffirmativeCode(raw: unknown) {
  const s = String(raw ?? '').trim().toLowerCase()
  return s === '1' || s === 'y' || s === 'true' || s === 'yes'
}

function parseUploadUrl(res: string) {
  const parsed = JSON.parse(res) as { code: number | string, data?: { fullFilePath?: string, filePath?: string, urlPredix?: string }, message?: string, msg?: string }
  if (!isBizSuccess(parsed.code)) {
    throw new Error(String(parsed.message ?? parsed.msg ?? '上传失败'))
  }
  const data = parsed.data || {}
  if (data.fullFilePath)
    return data.fullFilePath
  if (data.urlPredix || data.filePath)
    return `${data.urlPredix || ''}${data.filePath || ''}`
  throw new Error('上传结果缺少文件地址')
}

function isRemoteImagePath(p: string) {
  const s = String(p ?? '').trim()
  return /^https?:\/\//i.test(s) || s.startsWith('//')
}

function uploadLocalImage(filePath: string, biz: string) {
  return new Promise<string>((resolve, reject) => {
    const token = tokenStore.updateNowTime().validToken
    uni.uploadFile({
      url: `${getEnvBaseUrl()}/file/upload`,
      filePath,
      name: 'file',
      header: token ? { Authorization: `Bearer ${token}` } : {},
      formData: { biz },
      success: (uploadRes) => {
        try {
          resolve(parseUploadUrl(uploadRes.data as string))
        }
        catch (e: any) {
          reject(e)
        }
      },
      fail: reject,
    })
  })
}

function intakePhotoBiz(groupKey: keyof typeof photoGroups) {
  return groupKey === 'avatar' ? 'avatar' : 'customer'
}

function briefPause(ms: number) {
  return new Promise<void>(resolve => setTimeout(resolve, ms))
}

/**
 * 将某一组内本地临时路径逐个上传并替换为远端 URL（严格串行，避免并发上传）。
 * 多图分组每成功一张即 sync 一次，只带当前已就绪的远端 URL，降低后端按批处理问题。
 */
async function flushLocalFilesInGroup(groupKey: keyof typeof photoGroups) {
  const group = photoGroups[groupKey]
  const biz = intakePhotoBiz(groupKey)
  const snapshot = group.files.map(p => String(p ?? '').trim()).filter(Boolean)
  if (!snapshot.length) {
    group.files = []
    return
  }

  const built: string[] = []
  const afterUploadPauseMs = 160

  for (let idx = 0; idx < snapshot.length; idx++) {
    const s = snapshot[idx]
    if (isRemoteImagePath(s)) {
      built.push(s)
      continue
    }

    const url = await uploadLocalImage(s, biz)
    built.push(url)

    if (group.single) {
      group.files = [url]
      if (tokenStore.hasLogin)
        await syncPhotoFieldsToCustomer()
      return
    }

    const rest = snapshot.slice(idx + 1)
    group.files = [...built, ...rest]
    if (tokenStore.hasLogin)
      await syncPhotoFieldsToCustomer()

    const hasMoreLocal = rest.some(p => !isRemoteImagePath(p))
    if (hasMoreLocal)
      await briefPause(afterUploadPauseMs)
  }

  if (group.single)
    group.files = built.slice(0, 1)
  else
    group.files = built

  if (tokenStore.hasLogin)
    await syncPhotoFieldsToCustomer()
}

/** 已登录：用当前三组照片的远端地址写回客户资料 */
async function syncPhotoFieldsToCustomer() {
  if (!tokenStore.hasLogin)
    return

  const cusCode = String(customerStore.customerInfo?.cusCode ?? '').trim()
  const av = photoGroups.avatar.files[0]
  const life = photoGroups.life.files.filter(f => isRemoteImagePath(String(f)))
  const youth = photoGroups.youth.files.filter(f => isRemoteImagePath(String(f)))

  const body: Record<string, unknown> = {
    cusAvatar: (av && isRemoteImagePath(String(av)) ? av : null) as string | null,
    cusLifePhoto: life.length ? life.join(',') : null,
    cusTeenagePhoto: youth.length ? youth.join(',') : null,
  }
  if (cusCode)
    body.cusCode = cusCode

  const dto = await editCurrCusInfo(body)
  customerStore.setCustomerInfo(dto)
  applyCustomerToForm(dto)
}

function splitPhotoUrls(raw: unknown) {
  const s = String(raw ?? '').trim()
  if (!s)
    return []
  return s.split(/[,，|]/).map(x => x.trim()).filter(Boolean)
}

function applyCustomerToForm(c: ICustomerInfoRes | Record<string, any>) {
  const row = c || {}
  baseForm.city = String(row.cusCityResidenceName ?? '').trim()
  baseForm.cityCode = String(row.cusCityResidenceCode ?? '').trim()
  baseForm.occupation = String(row.cusOccupationalDescription ?? '').trim()
  baseForm.hasHouse = isAffirmativeCode(row.cusHaveHouseStatusCode)
  baseForm.hasCar = isAffirmativeCode(row.cusHaveCarStatusCode)
  baseForm.hasDisability = isAffirmativeCode(row.cusDisabledStatusCode)
  baseForm.isRemarriage = isAffirmativeCode(row.cusRemarriageStatusCode)
  baseForm.say = String(row.cusMoment ?? '').trim()

  const av = String(row.cusAvatar ?? '').trim()
  photoGroups.avatar.files = av ? [av] : []
  photoGroups.life.files = splitPhotoUrls(row.cusLifePhoto)
  photoGroups.youth.files = splitPhotoUrls(row.cusTeenagePhoto)
}

async function refreshCustomerAndForm() {
  if (!tokenStore.hasLogin)
    return
  const res = await customerStore.fetchCustomerInfo()
  applyCustomerToForm(res || customerStore.customerInfo)
}

onMounted(() => {
  void refreshCustomerAndForm()
})

function startLoginCountdown() {
  loginCountdown.value = 60
  const timer = setInterval(() => {
    loginCountdown.value -= 1
    if (loginCountdown.value <= 0)
      clearInterval(timer)
  }, 1000)
}

async function handleIntakeSendSms() {
  if (!isValidPhone(loginForm.phone)) {
    uni.showToast({ title: '请输入正确手机号', icon: 'none' })
    return
  }
  try {
    await sendSmsCode({
      phone: loginForm.phone.trim(),
      code: '',
      captchaKey: '',
    })
    uni.showToast({ title: '短信已发送', icon: 'success' })
    startLoginCountdown()
  }
  catch {}
}

async function handleIntakeLoginSubmit() {
  if (!loginAgree.value) {
    uni.showToast({ title: '请先勾选协议', icon: 'none' })
    return
  }
  if (!isValidPhone(loginForm.phone) || !loginForm.smsAuthCode.trim()) {
    uni.showToast({ title: '请输入手机号与验证码', icon: 'none' })
    return
  }
  loginLoading.value = true
  try {
    await tokenStore.login({
      phone: loginForm.phone.trim(),
      smsAuthCode: loginForm.smsAuthCode.trim(),
    })
    showLoginSheet.value = false
    loginForm.smsAuthCode = ''
    await refreshCustomerAndForm()
    if (goNextAfterLogin.value) {
      goNextAfterLogin.value = false
      await proceedGoNext()
    }
  }
  finally {
    loginLoading.value = false
  }
}

function openLoginSheetForNext() {
  goNextAfterLogin.value = true
  showLoginSheet.value = true
}

function closeLoginSheet() {
  showLoginSheet.value = false
  goNextAfterLogin.value = false
}

async function persistStep0Photos() {
  // 选图后已即时上传；此处仅兜底本地残留并联调保存，一般很快完成
  await flushLocalFilesInGroup('avatar')
  await flushLocalFilesInGroup('life')
  await flushLocalFilesInGroup('youth')
  await syncPhotoFieldsToCustomer()
}

async function persistStep1Basic() {
  const cusCode = String(customerStore.customerInfo?.cusCode ?? '').trim()
  const body: Record<string, unknown> = {
    cusCityResidenceCode: baseForm.cityCode.trim() || null,
    cusCityResidenceName: baseForm.city.trim() || null,
    cusOccupationalDescription: baseForm.occupation.trim() || null,
    cusHaveHouseStatusCode: boolToDictCode(baseForm.hasHouse),
    cusHaveCarStatusCode: boolToDictCode(baseForm.hasCar),
    cusDisabledStatusCode: boolToDictCode(baseForm.hasDisability),
    cusRemarriageStatusCode: boolToDictCode(baseForm.isRemarriage),
    cusMoment: baseForm.say.trim() || null,
  }
  if (cusCode)
    body.cusCode = cusCode
  const dto = await editCurrCusInfo(body)
  customerStore.setCustomerInfo(dto)
  applyCustomerToForm(dto)
}

async function proceedGoNext() {
  stepSaving.value = true
  try {
    await persistStep0Photos()
    uni.navigateTo({ url: '/pages/h5/intake-profile' })
  }
  catch (e) {
    console.error(e)
    uni.showToast({ title: '保存失败，请稍后重试', icon: 'none' })
  }
  finally {
    stepSaving.value = false
  }
}

const canGoNext = computed(() => photoGroups.avatar.files.length > 0)

/** H5/WebView 上 @tap 与 @click 会各触发一次，导致选图结束后立刻再次弹出选图 */
const chooseImageBusy = ref(false)

function chooseImages(groupKey: keyof typeof photoGroups) {
  if (chooseImageBusy.value)
    return

  const group = photoGroups[groupKey]
  const remain = group.maxCount - group.files.length
  if (remain <= 0) {
    uni.showToast({
      title: `最多上传 ${group.maxCount} 张`,
      icon: 'none',
    })
    return
  }

  chooseImageBusy.value = true
  uni.chooseImage({
    count: group.single ? 1 : remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const raw = res.tempFilePaths
      const paths: string[] = Array.isArray(raw) ? [...raw] : raw ? [String(raw)] : []
      if (group.single) {
        group.files = paths.slice(0, 1)
      }
      else {
        group.files.push(...paths.slice(0, remain))
      }

      if (!tokenStore.hasLogin)
        return

      try {
        await flushLocalFilesInGroup(groupKey)
        await syncPhotoFieldsToCustomer()
      }
      catch (e) {
        console.error(e)
        uni.showToast({ title: '图片上传失败，请重试', icon: 'none' })
      }
    },
    complete: () => {
      setTimeout(() => {
        chooseImageBusy.value = false
      }, 400)
    },
  })
}

function removeImage(groupKey: keyof typeof photoGroups, index: number) {
  photoGroups[groupKey].files.splice(index, 1)
  if (!tokenStore.hasLogin)
    return
  void (async () => {
    try {
      await flushLocalFilesInGroup(groupKey)
      await syncPhotoFieldsToCustomer()
    }
    catch (e) {
      console.error(e)
      uni.showToast({ title: '更新失败，请重试', icon: 'none' })
    }
  })()
}

async function goNext() {
  if (stepSaving.value)
    return
  if (!canGoNext.value) {
    uni.showToast({
      title: '请先上传头像',
      icon: 'none',
    })
    return
  }
  if (!tokenStore.hasLogin) {
    openLoginSheetForNext()
    return
  }
  await proceedGoNext()
}
</script>

<template>
  <view class="intake-root">
    <view class="intake-inner">
      <view class="intake-header">
        <text class="intake-eyebrow">
          形象上传
        </text>
        <text class="intake-title">
          形象照片
        </text>
        <text class="intake-desc">
          上传真实照片，提升匹配与信任度；完成后请继续填写基本资料。
        </text>
      </view>

      <view class="intake-card">
        <view
            v-for="(group, key) in photoGroups"
            :key="key"
            class="intake-photo-block"
          >
            <view class="intake-photo-head">
              <view class="intake-photo-titles">
                <text class="intake-photo-label">
                  {{ group.label }}
                </text>
                <text class="intake-photo-hint">
                  {{ group.desc }}
                </text>
              </view>
              <text class="intake-photo-count">
                {{ group.files.length }}/{{ group.maxCount }}
              </text>
            </view>
            <view class="intake-photo-grid">
              <view
                v-for="(file, index) in group.files"
                :key="`${file}-${index}`"
                class="intake-photo-thumb"
              >
                <image
                  :src="file"
                  mode="aspectFill"
                  class="intake-photo-img"
                />
                <view
                  class="intake-photo-remove"
                  @tap.stop="removeImage(key as keyof typeof photoGroups, index)"
                >
                  <text class="intake-photo-remove-x">
                    ×
                  </text>
                </view>
              </view>
              <view
                v-if="group.files.length < group.maxCount"
                class="intake-upload-slot"
                @tap.stop="chooseImages(key as keyof typeof photoGroups)"
              >
                <text class="intake-upload-plus">
                  +
                </text>
                <text class="intake-upload-cap">
                  添加
                </text>
              </view>
            </view>
          </view>
      </view>
    </view>

    <view
      v-if="!showCityPicker && !showLoginSheet"
      class="intake-footer-bar"
    >
      <view class="intake-footer-btns intake-footer-btns--single">
        <view
          class="intake-btn intake-btn--primary intake-btn--full"
          :class="{ 'intake-btn--disabled': stepSaving }"
          role="button"
          @tap.stop="goNext"
        >
          {{ stepSaving ? '保存中…' : '下一步：填写资料' }}
        </view>
      </view>
    </view>
    <!-- 未登录点「下一步」：与登录页相同的验证码登录接口 -->
    <view
      v-if="showLoginSheet"
      class="intake-overlay fixed inset-0 z-[9998] flex flex-col justify-end bg-[#00000055]"
      @click.self="closeLoginSheet"
    >
      <view
        class="max-h-[85vh] overflow-y-auto rounded-t-4 bg-white px-4 pt-3 pb-[calc(env(safe-area-inset-bottom,0px)+2.5rem)]"
        @click.stop
      >
        <view class="mb-3 flex items-center justify-between">
          <text class="text-3.5 text-[#8c6f7c]" @click="closeLoginSheet">取消</text>
          <text class="text-3.8 font-600 text-[#4a2e3b]">登录后继续</text>
          <text class="w-10" />
        </view>
        <view class="text-3 text-[#8c6f7c]">
          手机号
        </view>
        <input
          v-model="loginForm.phone"
          class="mt-1 h-9 w-full rounded-2 bg-[#fff3f8] px-3 text-3.5 text-[#3f2a33]"
          type="number"
          :maxlength="11"
          placeholder="请输入手机号"
        />
        <view class="mt-3 text-3 text-[#8c6f7c]">
          短信验证码
        </view>
        <view class="mt-1 flex items-center gap-2">
          <input
            v-model="loginForm.smsAuthCode"
            class="h-9 min-w-0 flex-1 rounded-2 bg-[#fff3f8] px-3 text-3.5 text-[#3f2a33]"
            type="number"
            :maxlength="6"
            placeholder="请输入验证码"
          />
          <button
            class="h-9 shrink-0 rounded-2 border-none bg-[#ffe4ef] px-3 text-3 text-[#ff4f8b] leading-9 after:border-none"
            :disabled="loginSmsDisabled"
            @click="handleIntakeSendSms"
          >
            {{ loginSmsBtnText }}
          </button>
        </view>
        <view class="mt-3 flex items-start">
          <view
            class="mt-0.5 h-4 w-4 flex shrink-0 items-center justify-center rounded-full border text-2.5 text-white"
            :class="loginAgree ? 'border-[#ff4f8b] bg-[#ff4f8b]' : 'border-[#d4c2cb] bg-white'"
            @click.stop="loginAgree = !loginAgree"
          >
            {{ loginAgree ? '✓' : '' }}
          </view>
          <view class="ml-2 min-w-0 flex-1 text-3 text-[#8c6f7c] leading-relaxed">
            <text>我已阅读并同意</text>
            <text class="text-[#ff4f8b]" @click.stop="openUserAgreement">《用户协议》</text>
            <text class="text-[#ff4f8b]" @click.stop="openPrivacyPolicy">《隐私政策》</text>
          </view>
        </view>
        <button
          class="mt-5 h-10 w-full rounded-full border-none bg-[#ff4f8b] text-3.6 text-white leading-10 after:border-none"
          :loading="loginLoading"
          :disabled="!loginCanSubmit || loginLoading"
          @click="handleIntakeLoginSubmit"
        >
          登录
        </button>
      </view>
    </view>

    <!-- 常驻城市：对接 /pub/dict/area/search -->
    <view
      v-if="showCityPicker"
      class="intake-overlay fixed inset-0 z-[9999] flex flex-col justify-end bg-[#00000055]"
      @click.self="closeCityPicker"
    >
      <view class="max-h-[78vh] flex flex-col overflow-hidden rounded-t-4 bg-white" @click.stop>
        <view class="flex shrink-0 items-center justify-between border-b border-[#f3e4ec] px-4 py-3">
          <text class="text-3.5 text-[#8c6f7c]" @click="closeCityPicker">取消</text>
          <text class="text-3.8 font-600 text-[#4a2e3b]">
            <text class="font-600 text-[#ff3d5c]">*</text>选择常驻城市（必选）
          </text>
          <text class="text-3.5 opacity-0">占位</text>
        </view>

        <scroll-view
          v-if="cityBrowseStack.length > 1"
          scroll-x
          class="shrink-0 border-b border-[#f8eef4] whitespace-nowrap"
          :show-scrollbar="false"
        >
          <view class="inline-flex items-center gap-1 px-3 py-2">
            <view
              v-for="(c, idx) in cityBrowseStack"
              :key="`${c.code}-${idx}`"
              class="inline-flex items-center"
            >
              <text
                class="text-3 text-[#ff4f8b]"
                :class="idx === cityBrowseStack.length - 1 ? 'font-600' : ''"
                @click="popCityBrowse(idx)"
              >
                {{ c.label }}
              </text>
              <text v-if="idx < cityBrowseStack.length - 1" class="mx-1 text-3 text-[#d4c2cb]">/</text>
            </view>
          </view>
        </scroll-view>

        <view class="shrink-0 px-3 py-2">
          <input
            v-model="citySearchInput"
            class="h-9 w-full rounded-2 bg-[#fff3f8] px-3 text-3.5 text-[#3f2a33]"
            placeholder="搜索国家、地区、城市…"
            confirm-type="search"
            @input="onCitySearchInput"
          />
          <view class="mt-1.5 px-0.5 text-3 text-[#a28391] leading-relaxed">
            须选到地级市或区县；北京、上海、天津、重庆可选「市」本级。
          </view>
        </view>

        <scroll-view scroll-y class="max-h-[52vh]" :scroll-top="0">
          <view v-if="cityLoading" class="py-10 text-center text-3 text-[#a28391]">
            加载中…
          </view>
          <template v-else>
            <view
              v-for="row in cityList"
              :key="row.id || row.dictAreaCode"
              class="flex items-stretch border-b border-[#f8eef4]"
            >
              <view class="min-w-0 flex-1 px-4 py-3" @click="pickCityArea(row)">
                <view class="text-3.6 text-[#3f2a33]">
                  {{ cityAreaRowLabel(row) }}
                </view>
                <view v-if="row.dictAreaNameEn" class="mt-0.5 truncate text-3 text-[#a28391]">
                  {{ row.dictAreaNameEn }}
                </view>
                <view v-if="!isResidenceCityLevel(row)" class="mt-0.5 text-3 text-[#e08aaf]">
                  未到市级，请使用「下级」或继续搜索
                </view>
              </view>
              <view
                v-if="canDrillArea(row)"
                class="flex w-14 shrink-0 items-center justify-center border-l border-[#f8eef4] text-3 text-[#ff4f8b]"
                @click.stop="drillArea(row)"
              >
                下级
              </view>
            </view>
            <view v-if="!cityList.length" class="py-12 text-center text-3 text-[#b8a0aa]">
              暂无匹配地区
            </view>
          </template>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
$bg: #f5f3f6;
$card: #ffffff;
$text: #2c2428;
$muted: #8b7f85;
$line: #ebe6ea;
$accent: #e85d7a;
$accent-soft: #fdf4f6;

.intake-root {
  overflow-anchor: none;
  overscroll-behavior-y: none;
  box-sizing: border-box;
  min-height: 100%;
  background: $bg;
  padding: 28rpx 32rpx 0;
  padding-bottom: 24rpx;
}

.intake-inner {
  max-width: 680rpx;
  margin: 0 auto;
}

.intake-header {
  margin-bottom: 36rpx;
  padding: 0 4rpx;
}

.intake-eyebrow {
  display: block;
  font-size: 22rpx;
  letter-spacing: 0.12em;
  color: $muted;
  text-transform: uppercase;
  margin-bottom: 12rpx;
}

.intake-title {
  display: block;
  font-size: 44rpx;
  font-weight: 600;
  color: $text;
  letter-spacing: -0.02em;
  line-height: 1.25;
}

.intake-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 26rpx;
  color: $muted;
  line-height: 1.5;
}

.intake-stepper {
  margin-top: 28rpx;
}

.intake-stepper-bar {
  height: 6rpx;
  border-radius: 999rpx;
  background: $line;
  overflow: hidden;
}

.intake-stepper-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #e85d7a 0%, #f07890 100%);
  transition: width 0.35s ease;
}

.intake-stepper-text {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
}

.intake-step-tag {
  font-size: 22rpx;
  color: #b5aab0;
}

.intake-step-tag--active {
  color: $accent;
  font-weight: 500;
}

.intake-card {
  background: $card;
  border-radius: 24rpx;
  padding: 36rpx 32rpx 40rpx;
  box-shadow: 0 8rpx 40rpx rgba(44, 36, 40, 0.06);
  border: 1rpx solid rgba(235, 230, 234, 0.9);
}

.intake-photo-block + .intake-photo-block {
  margin-top: 48rpx;
  padding-top: 40rpx;
  border-top: 1rpx solid $line;
}

.intake-photo-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.intake-photo-label {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: $text;
}

.intake-photo-hint {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $muted;
  line-height: 1.45;
}

.intake-photo-count {
  flex-shrink: 0;
  font-size: 24rpx;
  color: $muted;
  font-variant-numeric: tabular-nums;
}

.intake-photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.intake-photo-thumb {
  position: relative;
  width: 156rpx;
  height: 156rpx;
  border-radius: 20rpx;
  overflow: hidden;
  background: #f0ecee;
}

.intake-photo-img {
  width: 100%;
  height: 100%;
  display: block;
}

.intake-photo-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  background: rgba(0, 0, 0, 0.45);
  border-bottom-left-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.intake-photo-remove-x {
  font-size: 32rpx;
  color: #fff;
  line-height: 1;
  font-weight: 300;
}

.intake-upload-slot {
  width: 156rpx;
  height: 156rpx;
  border-radius: 20rpx;
  border: 2rpx dashed #d9d0d5;
  background: #faf9fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.intake-upload-plus {
  font-size: 52rpx;
  font-weight: 300;
  color: #c4b8be;
  line-height: 1;
}

.intake-upload-cap {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: $muted;
}

.intake-field {
  margin-bottom: 28rpx;
}

.intake-field--stack {
  margin-bottom: 0;
  margin-top: 8rpx;
}

.intake-field-label {
  display: block;
  font-size: 26rpx;
  color: $muted;
  margin-bottom: 12rpx;
}

.intake-req {
  color: $accent;
  margin-left: 4rpx;
}

.intake-field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 88rpx;
  padding: 0 28rpx;
  background: #f8f6f7;
  border-radius: 16rpx;
  border: 1rpx solid transparent;
}

.intake-field-value {
  flex: 1;
  font-size: 30rpx;
  color: $text;
}

.intake-field-placeholder {
  color: #b8a8b0;
}

.intake-chevron {
  font-size: 36rpx;
  color: #c4b8be;
  font-weight: 300;
  margin-left: 12rpx;
}

.intake-field-input {
  display: block;
  width: 100%;
  height: 88rpx;
  padding: 0 28rpx;
  font-size: 30rpx;
  color: $text;
  background: #f8f6f7;
  border-radius: 16rpx;
  border: 1rpx solid transparent;
  box-sizing: border-box;
}

.intake-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx 28rpx;
  font-size: 28rpx;
  color: $text;
  background: #f8f6f7;
  border-radius: 16rpx;
  border: 1rpx solid transparent;
  box-sizing: border-box;
  line-height: 1.55;
}

.intake-switch-card {
  background: #f8f6f7;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 28rpx;
}

.intake-switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 28rpx;
  border-bottom: 1rpx solid rgba(235, 230, 234, 0.9);
}

.intake-switch-row--last {
  border-bottom: none;
}

.intake-switch-label {
  font-size: 30rpx;
  color: $text;
}

.intake-switch-track {
  position: relative;
  width: 96rpx;
  height: 56rpx;
  flex-shrink: 0;
  border-radius: 9999rpx;
  background: #cfc4c9;
}

.intake-switch-track--on {
  background: $accent;
}

.intake-switch-thumb {
  position: absolute;
  top: 4rpx;
  left: 4rpx;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.intake-switch-thumb--on {
  transform: translateX(40rpx);
}

.intake-footer-bar {
  position: relative;
  z-index: 1;
  touch-action: manipulation;
  max-width: 680rpx;
  margin: 32rpx auto 0;
  padding-top: 12rpx;
  padding-bottom: max(16rpx, calc(12rpx + env(safe-area-inset-bottom, 0px)));
}

.intake-footer-btns {
  display: flex;
  align-items: stretch;
  gap: 20rpx;
}

.intake-footer-btns--single {
  display: flex;
}

.intake-btn--full {
  flex: 1;
}

.intake-btn {
  flex: 1;
  min-height: 96rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 30rpx;
  font-weight: 500;
  line-height: 1.25;
  border-radius: 999rpx;
}

.intake-btn--secondary {
  flex: 0.85;
  background: $card;
  color: $accent;
  border: 2rpx solid #e8d5dc;
}

.intake-btn--primary {
  flex: 1.25;
  background: linear-gradient(135deg, #e85d7a 0%, #d9466a 100%);
  color: #fff;
  box-shadow: 0 8rpx 28rpx rgba(232, 93, 122, 0.28);
}

.intake-btn--disabled {
  opacity: 0.42;
  pointer-events: none;
}

.intake-overlay {
  isolation: isolate;
}
</style>
