<script lang="ts" setup>
defineOptions({
  name: 'H5MatchmakerPage',
})

definePage({
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '服务中心',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

const PC_REGISTER_URL = 'https://www.iqingqing.net'

type TabKey = 'company' | 'qualify' | 'homepage' | 'orders' | 'services' | 'customers'

interface ITabItem {
  key: TabKey
  label: string
}

const tabs: ITabItem[] = [
  { key: 'company', label: '公司入驻' },
  { key: 'qualify', label: '红娘资质申请' },
  { key: 'homepage', label: '红娘主页' },
  { key: 'orders', label: '订单管理' },
  { key: 'services', label: '服务管理' },
  { key: 'customers', label: '客户维护' },
]

const HOMEPAGE_PHOTO_MAX = 9
const HOMEPAGE_TAG_MAX = 4

/** 形象照（本地临时路径，演示；联调后换正式 URL） */
const homepagePhotos = ref<string[]>([])

/** mock 服务标签，最多选 4 个 */
const mockHomepageTags = [
  { id: 'ht1', label: '残疾人群体' },
  { id: 'ht2', label: '情感咨询' },
  { id: 'ht3', label: '形象指导' },
  { id: 'ht4', label: '家长沟通' },
  { id: 'ht5', label: '周末约见策划' },
  { id: 'ht6', label: '高知人群' },
  { id: 'ht7', label: '二婚专项' },
  { id: 'ht8', label: '海外背景' },
]

const selectedHomepageTagIds = ref<string[]>([])

function isHomepageTagSelected(id: string) {
  return selectedHomepageTagIds.value.includes(id)
}

function toggleHomepageTag(id: string) {
  const list = selectedHomepageTagIds.value
  const i = list.indexOf(id)
  if (i >= 0) {
    list.splice(i, 1)
    return
  }
  if (list.length >= HOMEPAGE_TAG_MAX) {
    uni.showToast({ title: `最多选择 ${HOMEPAGE_TAG_MAX} 个标签`, icon: 'none' })
    return
  }
  list.push(id)
}

function chooseHomepagePhotos() {
  const remain = HOMEPAGE_PHOTO_MAX - homepagePhotos.value.length
  if (remain <= 0) {
    uni.showToast({ title: `最多上传 ${HOMEPAGE_PHOTO_MAX} 张`, icon: 'none' })
    return
  }
  uni.chooseImage({
    count: remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const raw = res.tempFilePaths
      const paths = Array.isArray(raw) ? raw : raw ? [String(raw)] : []
      const next = [...homepagePhotos.value, ...paths].slice(0, HOMEPAGE_PHOTO_MAX)
      homepagePhotos.value = next
    },
  })
}

function removeHomepagePhoto(index: number) {
  homepagePhotos.value.splice(index, 1)
}

function saveHomepageMock() {
  uni.showToast({ title: '已保存（演示）', icon: 'success' })
}

const activeTab = ref<TabKey>('company')

/** mock：可选入驻公司 */
const mockCompanies = [
  { id: 'org_001', name: '卿卿婚恋（平台直营）' },
  { id: 'org_002', name: '杭州心悦红娘工作室' },
  { id: 'org_003', name: '沪上佳缘服务中心' },
  { id: 'org_004', name: '城南初见婚恋咨询' },
]

const companyNames = computed(() => mockCompanies.map(c => c.name))
const companyIndex = ref(0)

watch(companyIndex, (i) => {
  if (i < 0 || i >= mockCompanies.length)
    companyIndex.value = 0
})

const qualifyForm = reactive({
  years: '',
  intent: '',
})

function onCompanyPickerChange(e: { detail: { value: string | number } }) {
  const i = Number(e.detail.value)
  companyIndex.value = Number.isFinite(i) ? i : 0
}

/** mock 订单 */
const mockOrders = reactive([
  {
    id: 'O20260508001',
    title: 'VIP 一对一牵线（季度）',
    amount: 6888,
    status: '进行中',
    statusType: 'progress' as const,
    date: '2026-05-08',
  },
  {
    id: 'O20260421009',
    title: '线下约见策划服务',
    amount: 399,
    status: '已完成',
    statusType: 'done' as const,
    date: '2026-04-21',
  },
  {
    id: 'O20260415003',
    title: '形象顾问加急',
    amount: 0,
    status: '已退款',
    statusType: 'refund' as const,
    date: '2026-04-15',
  },
])

interface IServiceItem {
  id: string
  name: string
  price: number
  unit: string
  /** 是否对外展示/接单（演示） */
  enabled: boolean
  /** 服务条款（演示文案，接口返回后替换） */
  terms: string
}

/** mock 服务定价（列表展示；点击进入详情修改） */
const servicePrices = reactive<IServiceItem[]>([
  {
    id: 'svc_1',
    name: '初次面谈（60 分钟）',
    price: 199,
    unit: '次',
    enabled: true,
    terms: '1. 面谈时长约 60 分钟，超时部分可按双方协商另行计费。\n2. 需提前 24 小时预约改期；无故缺席视为已消费。\n3. 内容为婚恋辅导与需求梳理，不构成法律或心理咨询结论。\n4. 未成年人请勿购买或使用本服务。',
  },
  {
    id: 'svc_2',
    name: '牵线套餐（含 3 次推荐）',
    price: 2888,
    unit: '套',
    enabled: true,
    terms: '1. 套餐内含至多 3 次符合条件的候选人推荐，有效期自签约日起 90 日。\n2. 推荐不限定必然促成恋爱或婚姻关系。\n3. 客户需如实提供资料；虚假信息导致的纠纷平台不承担责任。\n4. 退款规则以签约协议及平台公示为准。',
  },
  {
    id: 'svc_3',
    name: '周末线下约见陪同',
    price: 520,
    unit: '次',
    enabled: false,
    terms: '1. 陪同范围限于约定公共场所，不涉及私人住所。\n2. 客户双方人身安全与财物自行负责，请遵守法律法规。\n3. 因不可抗力或一方临时取消，改期规则以平台客服确认为准。\n4. 禁止违法违规或违背公序良俗的要求。',
  },
  {
    id: 'svc_4',
    name: '情感复盘与方案调整',
    price: 360,
    unit: '次',
    enabled: true,
    terms: '1. 单次服务含沟通复盘与后续行动建议，不提供无限次追问。\n2. 资料仅用于本次服务，严格保密。\n3. 辅导意见仅供参考，重大决策请自行审慎判断。\n4. 不满意可申请客服介入，争议处理以平台规则为准。',
  },
])

const serviceDetailId = ref<string | null>(null)

const serviceDetailForm = reactive({
  priceInput: '',
  enabled: true,
})

const serviceDetailTarget = computed(() =>
  servicePrices.find(s => s.id === serviceDetailId.value) ?? null,
)

watch(activeTab, (t) => {
  if (t !== 'services')
    serviceDetailId.value = null
})

function openServiceDetail(s: IServiceItem) {
  serviceDetailId.value = s.id
  serviceDetailForm.priceInput = String(s.price)
  serviceDetailForm.enabled = s.enabled
}

function closeServiceDetail() {
  serviceDetailId.value = null
}

function onDetailSwitchChange(e: { detail: { value: boolean | string } }) {
  const v = e.detail.value
  serviceDetailForm.enabled = v === true || v === 'true'
}

function saveServiceDetailMock() {
  const target = serviceDetailTarget.value
  if (!target)
    return
  const p = Number(serviceDetailForm.priceInput)
  if (!Number.isFinite(p) || p < 0) {
    uni.showToast({ title: '请输入有效价格', icon: 'none' })
    return
  }
  target.price = p
  target.enabled = serviceDetailForm.enabled
  uni.showToast({ title: '已保存', icon: 'success' })
}

/** mock 客户（客户维护仅展示匹配意向） */
const mockCustomers = reactive([
  {
    id: 'cus_01',
    name: '李沐晴',
    phone: '13822156821',
    intent: '希望匹配同城、本科以上、年龄 28–34 岁男士，接受周末见面。',
  },
  {
    id: 'cus_02',
    name: '周予安',
    phone: '15901739043',
    intent: '偏好温和稳重型，注重性格合拍；希望优先安排线下见面，周六下午方便。',
  },
  {
    id: 'cus_03',
    name: '陈嘉树',
    phone: '18621650877',
    intent: '意向对象：同城或一小时通勤圈，能接受轻微年龄差，看重三观与家庭观念一致。',
  },
])

function maskPhone(p: string) {
  if (p.length < 7)
    return p
  return `${p.slice(0, 3)}****${p.slice(-4)}`
}

function dialPhone(phone: string) {
  if (!phone?.trim()) {
    uni.showToast({ title: '暂无号码', icon: 'none' })
    return
  }
  uni.makePhoneCall({
    phoneNumber: phone.trim(),
    fail: () => {
      uni.showToast({ title: '无法拨号', icon: 'none' })
    },
  })
}

function copyText(text: string, okMsg = '已复制') {
  uni.setClipboardData({
    data: text,
    success: () => uni.showToast({ title: okMsg, icon: 'success' }),
  })
}

function submitQualifyMock() {
  if (!String(qualifyForm.years).trim()) {
    uni.showToast({ title: '请填写从业年限', icon: 'none' })
    return
  }
  if (!qualifyForm.intent.trim()) {
    uni.showToast({ title: '请填写申请意向', icon: 'none' })
    return
  }
  uni.showToast({ title: '已提交（演示数据）', icon: 'success' })
}

function statusClass(type: 'progress' | 'done' | 'refund') {
  if (type === 'done')
    return 'st-done'
  if (type === 'refund')
    return 'st-refund'
  return 'st-progress'
}
</script>

<template>
  <view class="page">
    <view class="hero">
      <text class="hero-title">服务中心</text>
      <text class="hero-sub">红娘工作台</text>
    </view>

    <scroll-view
      class="tabs-scroll"
      scroll-x
      :show-scrollbar="false"
    >
      <view class="tabs-row">
        <view
          v-for="t in tabs"
          :key="t.key"
          class="tab-pill"
          :class="{ active: activeTab === t.key }"
          @click="activeTab = t.key"
        >
          <text class="tab-text">{{ t.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 公司入驻 -->
    <view v-show="activeTab === 'company'" class="panel fade-in">
      <view class="pc-card">
        <view class="pc-icon-wrap">
          <text class="pc-icon">💻</text>
        </view>
        <text class="pc-title">公司入驻请使用电脑端</text>
        <text class="pc-desc">
          移动端不提供入驻流程。请在电脑浏览器打开官网完成企业注册与资质认证。
        </text>
        <view class="url-box">
          <text class="url-text">{{ PC_REGISTER_URL }}</text>
        </view>
        <view class="pc-actions">
          <view class="btn primary" @click="copyText(PC_REGISTER_URL, '链接已复制')">
            <text class="btn-txt">复制官网链接</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 红娘资质申请 -->
    <view v-show="activeTab === 'qualify'" class="panel fade-in">
      <view class="section-card">
        <text class="section-h">红娘资质申请</text>

        <text class="subsection-label">意向公司</text>
        <picker mode="selector" :range="companyNames" :value="companyIndex" @change="onCompanyPickerChange">
          <view class="picker-row mt-field">
            <text class="picker-label">选择公司</text>
            <text class="picker-value">{{ mockCompanies[companyIndex]?.name }}</text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>

        <view class="field">
          <text class="label">从业年限</text>
          <input
            v-model="qualifyForm.years"
            class="input"
            type="digit"
            placeholder="例如填写 3，表示 3 年"
            placeholder-class="ph"
          >
        </view>

        <view class="field">
          <text class="label">申请意向</text>
          <textarea
            v-model="qualifyForm.intent"
            class="textarea"
            placeholder="说明希望入驻的原因、可提供的资源或服务方向等"
            placeholder-class="ph"
            :maxlength="500"
          />
        </view>
      </view>

      <view class="btn primary block mt-btn" @click="submitQualifyMock">
        <text class="btn-txt">提交资质申请</text>
      </view>
    </view>

    <!-- 红娘主页 -->
    <view v-show="activeTab === 'homepage'" class="panel fade-in">
      <view class="hint-bar">
        <text class="hint-bar-txt">上传形象照、选择对外展示的服务标签（演示数据，可选 {{ HOMEPAGE_TAG_MAX }} 个标签）</text>
      </view>

      <view class="section-card">
        <text class="section-h">形象照片</text>
        <text class="hp-photo-tip">支持多张，建议清晰人像与工作场景，单张不超过 9 张</text>
        <view class="hp-photo-grid">
          <view
            v-for="(src, idx) in homepagePhotos"
            :key="`${src}-${idx}`"
            class="hp-photo-cell"
          >
            <image class="hp-photo-img" :src="src" mode="aspectFill" />
            <view class="hp-photo-del" @click.stop="removeHomepagePhoto(idx)">
              <text class="hp-photo-del-x">×</text>
            </view>
          </view>
          <view
            v-if="homepagePhotos.length < HOMEPAGE_PHOTO_MAX"
            class="hp-photo-add"
            @click="chooseHomepagePhotos"
          >
            <text class="hp-photo-add-plus">+</text>
            <text class="hp-photo-add-txt">添加照片</text>
          </view>
        </view>
      </view>

      <view class="section-card">
        <view class="hp-tag-head">
          <text class="section-h hp-tag-title">服务标签</text>
          <text class="hp-tag-count">已选 {{ selectedHomepageTagIds.length }}/{{ HOMEPAGE_TAG_MAX }}</text>
        </view>
        <view class="hp-tag-grid">
          <view
            v-for="tag in mockHomepageTags"
            :key="tag.id"
            class="hp-tag-pill"
            :class="{ selected: isHomepageTagSelected(tag.id) }"
            @click="toggleHomepageTag(tag.id)"
          >
            <text class="hp-tag-pill-txt">{{ tag.label }}</text>
          </view>
        </view>
      </view>

      <view class="btn primary block mt-btn" @click="saveHomepageMock">
        <text class="btn-txt">保存红娘主页</text>
      </view>
    </view>

    <!-- 订单管理 -->
    <view v-show="activeTab === 'orders'" class="panel fade-in">
      <view
        v-for="o in mockOrders"
        :key="o.id"
        class="order-card"
      >
        <view class="order-top">
          <text class="order-title">{{ o.title }}</text>
          <text class="order-amt">¥{{ o.amount }}</text>
        </view>
        <view class="order-meta">
          <text class="order-id">{{ o.id }}</text>
          <text class="order-date">{{ o.date }}</text>
        </view>
        <view class="order-foot">
          <text class="status-pill" :class="statusClass(o.statusType)">{{ o.status }}</text>
        </view>
      </view>
    </view>

    <!-- 服务管理 -->
    <view v-show="activeTab === 'services'" class="panel fade-in svc-panel">
      <!-- 列表 -->
      <view v-if="!serviceDetailId" class="svc-list-wrap">
        <view class="hint-bar">
          <text class="hint-bar-txt">点击条目查看详情，可修改价格与上下架状态</text>
        </view>
        <view
          v-for="s in servicePrices"
          :key="s.id"
          class="svc-list-item"
          @click="openServiceDetail(s)"
        >
          <view class="svc-list-body">
            <text class="svc-name">{{ s.name }}</text>
            <text class="svc-unit">按{{ s.unit }}计费</text>
            <view class="svc-list-row">
              <text class="svc-list-price">¥{{ s.price }}</text>
              <text class="svc-shelf-pill" :class="s.enabled ? 'up' : 'down'">{{ s.enabled ? '上架' : '下架' }}</text>
            </view>
          </view>
          <text class="svc-list-arrow">›</text>
        </view>
      </view>

      <!-- 详情 -->
      <view v-else-if="serviceDetailTarget" class="svc-detail-wrap">
        <view class="svc-detail-bar">
          <view class="svc-back" @click="closeServiceDetail">
            <text class="svc-back-icon">‹</text>
            <text class="svc-back-txt">返回</text>
          </view>
          <text class="svc-detail-bar-title">服务详情</text>
          <view class="svc-detail-bar-placeholder" />
        </view>

        <view class="svc-detail-card">
          <text class="svc-detail-name">{{ serviceDetailTarget.name }}</text>
          <text class="svc-detail-unit">计费单位：{{ serviceDetailTarget.unit }}</text>

          <view class="svc-terms-block">
            <text class="svc-detail-label">服务条款</text>
            <scroll-view scroll-y class="svc-terms-scroll" :show-scrollbar="false">
              <text class="svc-terms-text">{{ serviceDetailTarget.terms }}</text>
            </scroll-view>
          </view>

          <view class="svc-detail-field">
            <text class="svc-detail-label">价格（元）</text>
            <view class="svc-price-wrap detail">
              <text class="currency">¥</text>
              <input
                v-model="serviceDetailForm.priceInput"
                class="price-input"
                type="digit"
                placeholder="请输入价格"
                placeholder-class="ph"
              >
            </view>
          </view>

          <view class="svc-detail-field row-between">
            <view>
              <text class="svc-detail-label">上架状态</text>
              <text class="svc-detail-hint">关闭后用户端不展示该服务</text>
            </view>
            <switch
              :checked="serviceDetailForm.enabled"
              color="#018d71"
              @change="onDetailSwitchChange($event)"
            />
          </view>

          <view class="btn primary block svc-detail-save" @click="saveServiceDetailMock">
            <text class="btn-txt">保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 客户维护 -->
    <view v-show="activeTab === 'customers'" class="panel fade-in">
      <view
        v-for="c in mockCustomers"
        :key="c.id"
        class="cus-card"
      >
        <view class="cus-head">
          <text class="cus-name">{{ c.name }}</text>
          <view class="phone-row" @click="dialPhone(c.phone)">
            <text class="phone-label">电话</text>
            <text class="phone-num">{{ maskPhone(c.phone) }}</text>
            <text class="dial-mini">拨打</text>
          </view>
        </view>
        <view class="intent-block">
          <text class="intent-label">匹配意向</text>
          <text class="intent-text">{{ c.intent }}</text>
        </view>
      </view>
    </view>

    <view class="bottom-space" />
  </view>
</template>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  padding: calc(24rpx + env(safe-area-inset-top)) 24rpx calc(24rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
  background: linear-gradient(180deg, #faf8fc 0%, #f3eef6 55%, #faf8fc 100%);
}

.hero {
  margin-bottom: 20rpx;
  padding: 28rpx 26rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #fff5f9 0%, #ffffff 48%, #f2faf7 100%);
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.07);
}

.hero-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #3a2633;
  letter-spacing: 1rpx;
}

.hero-sub {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.45;
  color: #8e7a86;
}

.tabs-scroll {
  width: 100%;
  margin-bottom: 22rpx;
  white-space: nowrap;
}

.tabs-row {
  display: inline-flex;
  flex-direction: row;
  align-items: stretch;
  gap: 14rpx;
  padding: 4rpx 2rpx 8rpx;
}

.tab-pill {
  flex-shrink: 0;
  padding: 18rpx 28rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.85);
  border: 2rpx solid rgba(74, 46, 59, 0.08);
  box-shadow: 0 6rpx 20rpx rgba(74, 46, 59, 0.05);
  transition: transform 0.15s ease;
}

.tab-pill.active {
  background: linear-gradient(135deg, #018d71 0%, #02a882 100%);
  border-color: transparent;
  box-shadow: 0 10rpx 28rpx rgba(1, 141, 113, 0.28);
}

.tab-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #5c4a55;
}

.tab-pill.active .tab-text {
  color: #ffffff;
}

.panel {
  animation: fadeIn 0.22s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.pc-card {
  padding: 40rpx 32rpx 36rpx;
  border-radius: 24rpx;
  background: #fff;
  box-shadow: 0 16rpx 48rpx rgba(74, 46, 59, 0.08);
  text-align: center;
}

.pc-icon-wrap {
  width: 96rpx;
  height: 96rpx;
  margin: 0 auto 20rpx;
  border-radius: 28rpx;
  background: linear-gradient(145deg, #e8f7f3 0%, #d4f0ea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.pc-icon {
  font-size: 48rpx;
}

.pc-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #34252f;
}

.pc-desc {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
  line-height: 1.55;
  color: #7a6570;
}

.url-box {
  margin-top: 28rpx;
  padding: 20rpx 22rpx;
  border-radius: 16rpx;
  background: #f7f2f5;
  border: 1rpx dashed rgba(1, 141, 113, 0.35);
}

.url-text {
  font-size: 26rpx;
  color: #018d71;
  font-weight: 500;
  word-break: break-all;
}

.pc-actions {
  margin-top: 32rpx;
}

.btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24rpx 32rpx;
  border-radius: 999rpx;
}

.btn.primary {
  background: linear-gradient(135deg, #018d71 0%, #0aa67f 100%);
  box-shadow: 0 12rpx 32rpx rgba(1, 141, 113, 0.28);
}

.btn.block {
  width: 100%;
  box-sizing: border-box;
}

.btn-txt {
  font-size: 30rpx;
  font-weight: 600;
  color: #ffffff;
}

.section-card {
  margin-bottom: 20rpx;
  padding: 28rpx 24rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 10rpx 36rpx rgba(74, 46, 59, 0.06);
}

.section-h {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #34252f;
  margin-bottom: 22rpx;
}

.subsection-label {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #5c4a55;
  margin-bottom: 10rpx;
}

.mt-field {
  margin-bottom: 22rpx;
}

.field {
  margin-bottom: 22rpx;
}

.label {
  display: block;
  font-size: 24rpx;
  color: #8b7a84;
  margin-bottom: 10rpx;
}

.input {
  width: 100%;
  box-sizing: border-box;
  padding: 22rpx 20rpx;
  font-size: 28rpx;
  color: #34252f;
  background: #faf7f9;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
}

.textarea {
  width: 100%;
  box-sizing: border-box;
  min-height: 160rpx;
  padding: 22rpx 20rpx;
  font-size: 28rpx;
  color: #34252f;
  background: #faf7f9;
  border-radius: 16rpx;
  line-height: 1.45;
}

.ph {
  color: #c4b8bf;
}

.picker-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 22rpx 20rpx;
  background: #faf7f9;
  border-radius: 16rpx;
}

.picker-label {
  font-size: 28rpx;
  color: #6b5a66;
  flex-shrink: 0;
  margin-right: 16rpx;
}

.picker-value {
  flex: 1;
  font-size: 28rpx;
  color: #34252f;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.picker-arrow {
  font-size: 36rpx;
  color: #c4b8bf;
  flex-shrink: 0;
}

.mt-btn {
  margin-top: 8rpx;
}

.order-card {
  margin-bottom: 18rpx;
  padding: 26rpx 24rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 10rpx 36rpx rgba(74, 46, 59, 0.06);
}

.order-top {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16rpx;
}

.order-title {
  flex: 1;
  font-size: 30rpx;
  font-weight: 600;
  color: #34252f;
  line-height: 1.35;
}

.order-amt {
  font-size: 32rpx;
  font-weight: 700;
  color: #018d71;
  flex-shrink: 0;
}

.order-meta {
  margin-top: 14rpx;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.order-id {
  font-size: 24rpx;
  color: #a898a3;
  font-family: ui-monospace, Menlo, Monaco, Consolas, monospace;
}

.order-date {
  font-size: 24rpx;
  color: #a898a3;
}

.order-foot {
  margin-top: 18rpx;
}

.status-pill {
  display: inline-block;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.st-progress {
  background: rgba(1, 141, 113, 0.12);
  color: #018d71;
}

.st-done {
  background: rgba(88, 110, 189, 0.12);
  color: #4a5cad;
}

.st-refund {
  background: rgba(189, 120, 88, 0.12);
  color: #b5673f;
}

.hint-bar {
  margin-bottom: 16rpx;
  padding: 18rpx 20rpx;
  border-radius: 16rpx;
  background: rgba(1, 141, 113, 0.08);
}

.hint-bar-txt {
  font-size: 24rpx;
  line-height: 1.45;
  color: #5a6d68;
}

.svc-list-wrap {
  padding-bottom: 8rpx;
}

.svc-list-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 16rpx;
  padding: 26rpx 22rpx;
  border-radius: 20rpx;
  background: #fff;
  box-shadow: 0 8rpx 28rpx rgba(74, 46, 59, 0.06);
  border: 2rpx solid rgba(255, 255, 255, 0.95);
}

.svc-list-body {
  flex: 1;
  min-width: 0;
}

.svc-list-row {
  margin-top: 14rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.svc-list-price {
  font-size: 34rpx;
  font-weight: 700;
  color: #018d71;
}

.svc-shelf-pill {
  flex-shrink: 0;
  padding: 8rpx 18rpx;
  border-radius: 999rpx;
  font-size: 22rpx;
  font-weight: 600;
}

.svc-shelf-pill.up {
  background: rgba(1, 141, 113, 0.14);
  color: #018d71;
}

.svc-shelf-pill.down {
  background: rgba(139, 122, 132, 0.14);
  color: #7a6570;
}

.svc-list-arrow {
  flex-shrink: 0;
  margin-left: 12rpx;
  font-size: 40rpx;
  color: #d4c8cf;
  font-weight: 300;
}

.svc-detail-wrap {
  animation: fadeIn 0.2s ease;
}

.svc-detail-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.svc-back {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 4rpx;
  width: 160rpx;
}

.svc-back-icon {
  font-size: 44rpx;
  color: #018d71;
  font-weight: 300;
  line-height: 1;
}

.svc-back-txt {
  font-size: 28rpx;
  font-weight: 600;
  color: #018d71;
}

.svc-detail-bar-title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 700;
  color: #34252f;
}

.svc-detail-bar-placeholder {
  width: 160rpx;
}

.svc-detail-card {
  padding: 32rpx 26rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.07);
}

.svc-detail-name {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #34252f;
  line-height: 1.35;
}

.svc-detail-unit {
  display: block;
  margin-top: 10rpx;
  font-size: 26rpx;
  color: #8b7a84;
}

.svc-terms-block {
  margin-top: 28rpx;
}

.svc-terms-scroll {
  height: 360rpx;
  margin-top: 14rpx;
  padding: 20rpx 22rpx;
  box-sizing: border-box;
  border-radius: 16rpx;
  background: #faf7f9;
  border: 1rpx solid rgba(74, 46, 59, 0.06);
}

.svc-terms-text {
  display: block;
  font-size: 26rpx;
  line-height: 1.65;
  color: #5c4a55;
  white-space: pre-wrap;
  word-break: break-word;
}

.svc-detail-field {
  margin-top: 28rpx;
}

.svc-detail-field.row-between {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
}

.svc-detail-label {
  display: block;
  font-size: 26rpx;
  font-weight: 600;
  color: #5c4a55;
}

.svc-detail-hint {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #a898a3;
  line-height: 1.4;
}

.svc-detail-save {
  margin-top: 40rpx;
}

.svc-name {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #34252f;
  line-height: 1.35;
}

.svc-unit {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #a898a3;
}

.svc-price-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 12rpx 18rpx;
  border-radius: 14rpx;
  background: #faf7f9;
  border: 2rpx solid rgba(1, 141, 113, 0.15);
}

.svc-price-wrap.detail {
  width: 100%;
  box-sizing: border-box;
  margin-top: 14rpx;
}

.currency {
  font-size: 26rpx;
  font-weight: 600;
  color: #018d71;
  margin-right: 4rpx;
}

.price-input {
  flex: 1;
  min-width: 0;
  font-size: 30rpx;
  font-weight: 700;
  color: #34252f;
  text-align: right;
}

.cus-card {
  margin-bottom: 18rpx;
  padding: 26rpx 24rpx;
  border-radius: 22rpx;
  background: #fff;
  box-shadow: 0 12rpx 40rpx rgba(74, 46, 59, 0.07);
  border: 1rpx solid rgba(255, 255, 255, 0.9);
}

.cus-head {
  padding-bottom: 18rpx;
  border-bottom: 1rpx solid #f0eaee;
}

.cus-name {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #34252f;
}

.phone-row {
  margin-top: 16rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.phone-label {
  font-size: 24rpx;
  color: #a898a3;
  margin-right: 12rpx;
}

.phone-num {
  flex: 1;
  font-size: 28rpx;
  font-weight: 600;
  color: #4a3d44;
  letter-spacing: 1rpx;
}

.dial-mini {
  font-size: 24rpx;
  color: #018d71;
  font-weight: 600;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(1, 141, 113, 0.1);
}

.intent-block {
  margin-top: 18rpx;
}

.intent-label {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #8b7a84;
  margin-bottom: 10rpx;
}

.intent-text {
  display: block;
  font-size: 28rpx;
  line-height: 1.6;
  color: #4a3d44;
}

.bottom-space {
  height: 24rpx;
}

.hp-photo-tip {
  display: block;
  font-size: 24rpx;
  color: #9a8894;
  line-height: 1.45;
  margin-bottom: 20rpx;
}

.hp-photo-grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 16rpx;
}

.hp-photo-cell {
  position: relative;
  width: calc((100% - 32rpx) / 3);
  padding-bottom: calc((100% - 32rpx) / 3);
  height: 0;
  border-radius: 16rpx;
  overflow: hidden;
  background: #f3eef2;
}

.hp-photo-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

.hp-photo-del {
  position: absolute;
  top: 6rpx;
  right: 6rpx;
  width: 44rpx;
  height: 44rpx;
  border-radius: 999rpx;
  background: rgba(30, 20, 35, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hp-photo-del-x {
  color: #fff;
  font-size: 32rpx;
  font-weight: 300;
  line-height: 1;
}

.hp-photo-add {
  width: calc((100% - 32rpx) / 3);
  padding-bottom: calc((100% - 32rpx) / 3);
  height: 0;
  position: relative;
  border-radius: 16rpx;
  border: 2rpx dashed rgba(1, 141, 113, 0.35);
  background: rgba(1, 141, 113, 0.04);
  box-sizing: border-box;
}

.hp-photo-add-plus {
  position: absolute;
  left: 50%;
  top: 38%;
  transform: translate(-50%, -50%);
  font-size: 56rpx;
  font-weight: 300;
  color: #018d71;
  line-height: 1;
}

.hp-photo-add-txt {
  position: absolute;
  left: 50%;
  top: 62%;
  transform: translateX(-50%);
  font-size: 22rpx;
  color: #6b8f86;
  white-space: nowrap;
}

.hp-tag-head {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 8rpx;
}

.hp-tag-title {
  margin-bottom: 0;
}

.hp-tag-count {
  flex-shrink: 0;
  font-size: 24rpx;
  font-weight: 600;
  color: #018d71;
}

.hp-tag-grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 18rpx;
}

.hp-tag-pill {
  padding: 14rpx 26rpx;
  border-radius: 999rpx;
  background: #faf7f9;
  border: 2rpx solid rgba(74, 46, 59, 0.08);
}

.hp-tag-pill.selected {
  background: rgba(1, 141, 113, 0.12);
  border-color: rgba(1, 141, 113, 0.45);
}

.hp-tag-pill-txt {
  font-size: 26rpx;
  color: #5c4a55;
}

.hp-tag-pill.selected .hp-tag-pill-txt {
  color: #018d71;
  font-weight: 600;
}
</style>
