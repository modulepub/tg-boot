<template>
  <view class="intent-page">
    <view v-if="intentLoading" class="loading-hint">
      加载意向中…
    </view>
    <view class="card">
      <view class="field">
        <text class="label">年龄区间</text>
        <view class="age-value">
          {{ form.minAge }} - {{ form.maxAge }} 岁
        </view>
        <view class="slider-row">
          <text class="slider-label">最小</text>
          <slider
            class="slider"
            :min="18"
            :max="60"
            :step="1"
            :value="form.minAge"
            activeColor="#7c5cf6"
            backgroundColor="#e7ecf5"
            @change="onMinAgeChange"
          />
        </view>
        <view class="slider-row">
          <text class="slider-label">最大</text>
          <slider
            class="slider"
            :min="18"
            :max="60"
            :step="1"
            :value="form.maxAge"
            activeColor="#7c5cf6"
            backgroundColor="#e7ecf5"
            @change="onMaxAgeChange"
          />
        </view>
      </view>

      <view class="field">
        <text class="label">所属城市</text>
        <view class="selector" @click="cityPanelOpen = !cityPanelOpen">
          <text class="selector-text">
            {{ selectedCityText }}
          </text>
          <text class="selector-arrow">{{ cityPanelOpen ? '▲' : '▼' }}</text>
        </view>
        <view v-if="cityPanelOpen" class="city-panel">
          <view v-if="cityOptions.length === 0" class="city-empty">
            暂无客户城市数据
          </view>
          <template v-else>
            <view
              v-for="row in cityOptions"
              :key="row.cusCityResidenceCode"
              class="city-option"
              @click="toggleCity(row)"
            >
              <text class="city-name">{{ row.cusCityResidenceName }}</text>
              <text class="city-check">{{ isCitySelected(row.cusCityResidenceCode) ? '✓' : '' }}</text>
            </view>
          </template>
        </view>
      </view>

      <view class="field">
        <text class="label">选择性别</text>
        <view class="gender-row">
          <view
            class="gender-item"
            :class="{ active: form.gender === 'male' }"
            @click="form.gender = 'male'"
          >
            男
          </view>
          <view
            class="gender-item"
            :class="{ active: form.gender === 'female' }"
            @click="form.gender = 'female'"
          >
            女
          </view>
        </view>
      </view>

      <view class="field switch-row">
        <text class="label">是否有房</text>
        <switch :checked="form.hasHouse" @change="e => form.hasHouse = e.detail.value" />
      </view>

      <view class="field switch-row">
        <text class="label">是否有车</text>
        <switch :checked="form.hasCar" @change="e => form.hasCar = e.detail.value" />
      </view>

      <view class="field switch-row">
        <text class="label">是否接受残疾情况（优先推荐）</text>
        <switch :checked="form.disabled" @change="e => form.disabled = e.detail.value" />
      </view>
    </view>

    <button class="save-btn" :disabled="saving" @click="onSave">
      {{ saving ? '保存中…' : '去看看' }}
    </button>
  </view>
</template>

<script lang="ts" setup>
import type { ICusCityResidenceOption } from '@/api/customer'
import { getCustomerResidenceCities } from '@/api/customer'
import type { IDtRecommendIntentDTO } from '@/api/recommend'
import { getRecommendIntent, saveRecommendIntent } from '@/api/recommend'

defineOptions({
  name: 'RecommendEditIntentPage',
})

definePage({
  style: {
    navigationBarTitleText: '我的意愿',
  },
})

const form = reactive({
  minAge: 18,
  maxAge: 60,
  /** 意向里保存的常驻城市编码（逗号拼接存库） */
  cityCodes: [] as string[],
  gender: 'female' as 'male' | 'female',
  hasHouse: false,
  hasCar: false,
  disabled: false,
})
const cityPanelOpen = ref(false)
const intentLoading = ref(false)
const saving = ref(false)
/** 当前用户意向（含 id，供编辑提交） */
const loadedIntent = ref<IDtRecommendIntentDTO | null>(null)
const cityOptions = ref<ICusCityResidenceOption[]>([])

function cityLabel(code: string | undefined): string {
  const c = String(code ?? '').trim()
  if (!c)
    return ''
  const row = cityOptions.value.find(
    r => String(r.cusCityResidenceCode ?? '').trim() === c,
  )
  return String(row?.cusCityResidenceName ?? '').trim() || c
}

const selectedCityText = computed(() =>
  form.cityCodes.length ? form.cityCodes.map(cityLabel).filter(Boolean).join('、') : '不限制',
)

function isCitySelected(code: string | undefined) {
  const c = String(code ?? '').trim()
  return c.length > 0 && form.cityCodes.includes(c)
}

function isFemaleSexCode(raw: unknown): boolean {
  const s = String(raw ?? '').trim().toLowerCase()
  return s.includes('female') || s === '2'
}

function isAffirmativeCode(raw: unknown): boolean {
  const s = String(raw ?? '').trim().toLowerCase()
  return s === '1' || s === 'y' || s === 'true' || s === 'yes'
}

function parseCityList(code?: string): string[] {
  const s = String(code ?? '').trim()
  if (!s)
    return []
  return s.split(/[,，、]/).map(x => x.trim()).filter(Boolean)
}

function applyIntentDto(dto: IDtRecommendIntentDTO) {
  const min = dto.intentionMinAge
  const max = dto.intentionMaxAge
  if (min != null && !Number.isNaN(Number(min)))
    form.minAge = Math.min(60, Math.max(18, Number(min)))
  if (max != null && !Number.isNaN(Number(max)))
    form.maxAge = Math.min(60, Math.max(18, Number(max)))
  if (form.minAge > form.maxAge) {
    const t = form.minAge
    form.minAge = form.maxAge
    form.maxAge = t
  }
  form.cityCodes = parseCityList(dto.intentionCityCode)
  form.gender = isFemaleSexCode(dto.intentionSexCode) ? 'female' : 'male'
  form.hasHouse = isAffirmativeCode(dto.intentionHaveHouseCode)
  form.hasCar = isAffirmativeCode(dto.intentionHaveCarCode)
  form.disabled = isAffirmativeCode(dto.intentionDisabledStatusCode)
}

function mergeCityOptions(apiList: ICusCityResidenceOption[], selectedCodes: string[]): ICusCityResidenceOption[] {
  const byCode = new Map<string, ICusCityResidenceOption>()
  for (const r of apiList) {
    const code = String(r.cusCityResidenceCode ?? '').trim()
    if (!code)
      continue
    byCode.set(code, {
      cusCityResidenceCode: code,
      cusCityResidenceName: String(r.cusCityResidenceName ?? '').trim() || code,
    })
  }
  for (const code of selectedCodes) {
    const c = String(code ?? '').trim()
    if (!c || byCode.has(c))
      continue
    byCode.set(c, { cusCityResidenceCode: c, cusCityResidenceName: c })
  }
  return [...byCode.values()].sort((a, b) =>
    String(a.cusCityResidenceName ?? '').localeCompare(String(b.cusCityResidenceName ?? ''), 'zh-Hans-CN'),
  )
}

/** 将意向里存的片段解析为城市编码（兼容历史：存的是名称时按名称反查编码） */
function resolveIntentCityCodes(tokens: string[], options: ICusCityResidenceOption[]): string[] {
  const codeSet = new Set(
    options.map(o => String(o.cusCityResidenceCode ?? '').trim()).filter(Boolean),
  )
  const byName = new Map<string, string>()
  for (const o of options) {
    const c = String(o.cusCityResidenceCode ?? '').trim()
    const n = String(o.cusCityResidenceName ?? '').trim()
    if (c && n)
      byName.set(n, c)
  }
  const out: string[] = []
  for (const raw of tokens) {
    const t = String(raw ?? '').trim()
    if (!t)
      continue
    if (codeSet.has(t)) {
      out.push(t)
      continue
    }
    const fromName = byName.get(t)
    if (fromName) {
      out.push(fromName)
      continue
    }
    out.push(t)
  }
  return [...new Set(out)]
}

async function loadCityOptions() {
  try {
    const list = await getCustomerResidenceCities()
    const arr = Array.isArray(list) ? list : []
    cityOptions.value = mergeCityOptions(arr, form.cityCodes)
  }
  catch {
    cityOptions.value = mergeCityOptions([], form.cityCodes)
  }
}

async function loadRecommendIntent() {
  intentLoading.value = true
  try {
    const dto = await getRecommendIntent()
    loadedIntent.value = dto ?? null
    applyIntentDto(dto ?? {})
    await loadCityOptions()
    form.cityCodes = resolveIntentCityCodes(form.cityCodes, cityOptions.value)
    cityOptions.value = mergeCityOptions(cityOptions.value, form.cityCodes)
  }
  catch {
    loadedIntent.value = null
    uni.showToast({ title: '意向加载失败', icon: 'none' })
    await loadCityOptions()
    form.cityCodes = resolveIntentCityCodes(form.cityCodes, cityOptions.value)
    cityOptions.value = mergeCityOptions(cityOptions.value, form.cityCodes)
  }
  finally {
    intentLoading.value = false
  }
}

function boolToDictCode(v: boolean): string {
  return v ? '1' : '0'
}

function buildIntentPayload(base: IDtRecommendIntentDTO): IDtRecommendIntentDTO {
  return {
    intentionCode: String(base.intentionCode ?? '').trim(),
    intentionUserCode: base.intentionUserCode,
    intentionName: base.intentionName?.trim() || '我的推荐意向',
    intentionMinAge: form.minAge,
    intentionMaxAge: form.maxAge,
    intentionCityCode: form.cityCodes.length ? form.cityCodes.join(',') : null,
    intentionSexCode: form.gender === 'female' ? '2' : '1',
    intentionHaveHouseCode: boolToDictCode(form.hasHouse),
    intentionHaveCarCode: boolToDictCode(form.hasCar),
    intentionDisabledStatusCode: boolToDictCode(form.disabled),
  }
}

onMounted(() => {
  void loadRecommendIntent()
})

watch(
  () => [...form.cityCodes],
  (selected) => {
    cityOptions.value = mergeCityOptions(cityOptions.value, selected)
  },
)

function onMinAgeChange(e: { detail: { value: number } }) {
  const minAge = Number(e.detail.value || 18)
  form.minAge = minAge
  if (form.minAge > form.maxAge) {
    form.maxAge = form.minAge
  }
}

function onMaxAgeChange(e: { detail: { value: number } }) {
  const maxAge = Number(e.detail.value || 60)
  form.maxAge = maxAge
  if (form.maxAge < form.minAge) {
    form.minAge = form.maxAge
  }
}

function toggleCity(row: ICusCityResidenceOption) {
  const code = String(row.cusCityResidenceCode ?? '').trim()
  if (!code)
    return
  const index = form.cityCodes.indexOf(code)
  if (index >= 0) {
    form.cityCodes.splice(index, 1)
    return
  }
  form.cityCodes.push(code)
  cityOptions.value = mergeCityOptions(cityOptions.value, form.cityCodes)
}

async function onSave() {
  if (saving.value)
    return
  saving.value = true
  try {
    let base = loadedIntent.value
    if (!base?.intentionCode?.trim()) {
      base = await getRecommendIntent()
      loadedIntent.value = base ?? null
    }
    if (!base?.intentionCode?.trim()) {
      uni.showToast({ title: '未获取到意向，请稍后重试', icon: 'none' })
      return
    }
    await saveRecommendIntent(buildIntentPayload(base))
    uni.reLaunch({ url: '/pages/recommend/index' })
  }
  catch {
    uni.showToast({ title: '保存失败', icon: 'none' })
  }
  finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.intent-page {
  min-height: 100vh;
  background: #f6f8fc;
  padding: 24rpx;
  box-sizing: border-box;
}

.loading-hint {
  text-align: center;
  font-size: 26rpx;
  color: #7f8aa3;
  margin-bottom: 16rpx;
}

.card {
  border-radius: 20rpx;
  background: #fff;
  padding: 24rpx;
}

.field {
  margin-bottom: 24rpx;
}

.field:last-child {
  margin-bottom: 0;
}

.label {
  display: block;
  color: #2a3247;
  font-size: 28rpx;
  font-weight: 600;
  margin-bottom: 10rpx;
}

.age-value {
  height: 74rpx;
  border: 1rpx solid #e7ecf5;
  border-radius: 14rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #2a3247;
  background: #fbfcff;
  display: flex;
  align-items: center;
}

.slider-row {
  margin-top: 14rpx;
}

.slider-label {
  color: #7f8aa3;
  font-size: 24rpx;
}

.slider {
  margin-top: 8rpx;
}

.selector {
  height: 74rpx;
  border: 1rpx solid #e7ecf5;
  border-radius: 14rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fbfcff;
}

.selector-text {
  flex: 1;
  min-width: 0;
  color: #2a3247;
  font-size: 28rpx;
}

.selector-arrow {
  color: #9aa5ba;
  font-size: 24rpx;
}

.city-panel {
  margin-top: 10rpx;
  border: 1rpx solid #e7ecf5;
  border-radius: 14rpx;
  background: #fff;
  overflow: hidden;
}

.city-empty {
  padding: 28rpx 20rpx;
  text-align: center;
  font-size: 26rpx;
  color: #9aa5ba;
}

.city-option {
  height: 68rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1rpx solid #f0f3f8;
}

.city-option:first-child {
  border-top: none;
}

.city-name {
  color: #2a3247;
  font-size: 27rpx;
}

.city-check {
  color: #7c5cf6;
  font-size: 30rpx;
  font-weight: 600;
}

.gender-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12rpx;
}

.gender-item {
  height: 72rpx;
  border-radius: 14rpx;
  border: 1rpx solid #e7ecf5;
  background: #fbfcff;
  color: #667085;
  font-size: 27rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gender-item.active {
  color: #5b3df5;
  border-color: #cfc7ff;
  background: #f4f1ff;
  font-weight: 600;
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.switch-row .label {
  margin-bottom: 0;
}

.save-btn {
  margin-top: 28rpx;
  border: none;
  height: 82rpx;
  line-height: 82rpx;
  border-radius: 999px;
  color: #fff;
  font-size: 30rpx;
  background: linear-gradient(90deg, #5e72f4 0%, #7c5cf6 100%);
}

.save-btn[disabled] {
  opacity: 0.65;
}
</style>
