<template>
  <!-- 全屏背景；顶栏固定在首屏上方不随列表滚动，仅下方内容区滚动 + 内容栏 max-width 居中 -->
  <view class="home-page">
    <TechBackdrop class="home-tech" />

    <SiteHeaderBar />

    <scroll-view scroll-y class="home-scroll" :show-scrollbar="false" enable-flex>
      <view class="home-shell home-scroll-inner">
        <view v-if="loadTip" class="banner-tip">
          {{ loadTip }}
        </view>

        <swiper
          class="banner-swiper"
          circular
          :indicator-dots="bannerList.length > 1"
          indicator-color="rgba(255,255,255,0.45)"
          indicator-active-color="#ffffff"
          :autoplay="bannerList.length > 1"
          :interval="4200"
          :duration="500"
        >
          <swiper-item v-for="(item, idx) in bannerList" :key="item.nodeCode || item.id || idx">
            <image
              class="banner-image"
              :src="item.nodeHeadImg || defaultBanner"
              mode="aspectFill"
              @click="openCmsNode(item, '焦点')"
            />
          </swiper-item>
        </swiper>

        <section v-for="block in sections" :key="block.catalog.nodeCode || block.catalog.id" class="cms-section">
          <view class="section-head">
            <text class="section-title">{{ block.catalog.nodeName || '栏目' }}</text>
            <text v-if="block.catalog.nodeSummary" class="section-sub">{{ block.catalog.nodeSummary }}</text>
          </view>
          <view class="card-grid">
            <view
              v-for="cell in block.items"
              :key="cell.nodeCode || cell.id"
              class="cms-card"
              @click="openCmsNode(cell, block.catalog.nodeName || '')"
            >
              <image v-if="cell.nodeHeadImg" class="cms-card-img" :src="cell.nodeHeadImg" mode="aspectFill" />
              <view class="cms-card-body">
                <text class="cms-card-title">{{ cell.nodeName }}</text>
                <text v-if="cell.nodeSummary" class="cms-card-sum">{{ cell.nodeSummary }}</text>
              </view>
            </view>
          </view>
        </section>

        <view v-if="sections.length === 0 && !loadTip" class="empty-site">
          <text class="empty-title">暂无栏目内容</text>
          <text class="empty-desc">请在 CMS 中为站点根栏目「{{ siteNodeCode || '未配置' }}」配置子栏目及稿件</text>
        </view>

        <view class="safe-bottom" />
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import type { ICmsNodeItem } from '@/api/home'
import { getCmsNodeList } from '@/api/home'
import { jump, toH5JumpableLink } from '@/utils/jump'
import TechBackdrop from '@/components/login/TechBackdrop.vue'
import SiteHeaderBar from '@/components/site/SiteHeaderBar.vue'

defineOptions({
  name: 'Home',
})

definePage({
  type: 'home',
  style: {
    'navigationStyle': 'custom',
    'navigationBarTitleText': '首页',
    'mp-alipay': {
      defaultTitle: '首页',
      transparentTitle: 'always',
      titlePenetrate: 'YES',
      titleBarColor: '#ffffff',
    },
  },
})

interface ICmsSection {
  catalog: ICmsNodeItem
  items: ICmsNodeItem[]
}

const defaultBanner = 'https://picsum.photos/seed/qingqing-home/1200/420'

const siteNodeCode = import.meta.env.VITE_CMS_SITE_NODE_CODE?.trim() || ''

const bannerList = ref<ICmsNodeItem[]>([])
const sections = ref<ICmsSection[]>([])
const loadTip = ref('')

/** 兼容 MyBatis IPage.records 与部分网关的 list / 二次包裹的 data */
function pickCmsPageRecords(res: unknown): ICmsNodeItem[] {
  if (res == null || typeof res !== 'object')
    return []
  const o = res as Record<string, unknown>
  const top = o.records ?? o.list
  if (Array.isArray(top))
    return top as ICmsNodeItem[]
  const inner = o.data
  if (inner != null && typeof inner === 'object') {
    const d = inner as Record<string, unknown>
    const nested = d.records ?? d.list
    if (Array.isArray(nested))
      return nested as ICmsNodeItem[]
  }
  return []
}

function normNodeType(t?: string) {
  return String(t ?? '').trim().toLowerCase()
}

function isCatalogNode(node: ICmsNodeItem) {
  return normNodeType(node.nodeTypeCode) === 'catalog'
}

function isDocumentNode(node: ICmsNodeItem) {
  return normNodeType(node.nodeTypeCode) === 'document'
}

function isBannerCatalog(node: ICmsNodeItem) {
  if (!isCatalogNode(node))
    return false
  const n = (node.nodeName || '').trim()
  const c = (node.nodeCode || '').trim()
  if (/轮播|banner|顶部/i.test(n))
    return true
  return /^topbanner$/i.test(c)
}

async function resolveBanner(rootRecords: ICmsNodeItem[]): Promise<ICmsNodeItem[]> {
  const bannerCat = rootRecords.find(isBannerCatalog)
  if (bannerCat?.nodeCode) {
    const res = await getCmsNodeList(bannerCat.nodeCode, 50)
    const imgs = pickCmsPageRecords(res).filter(x => x.nodeHeadImg)
    if (imgs.length)
      return imgs
  }
  const rootDocs = rootRecords.filter(r => isDocumentNode(r) && r.nodeHeadImg)
  if (rootDocs.length)
    return rootDocs
  for (const cat of rootRecords.filter(r => isCatalogNode(r) && !isBannerCatalog(r))) {
    if (!cat.nodeCode)
      continue
    const res = await getCmsNodeList(cat.nodeCode, 40)
    const imgs = pickCmsPageRecords(res).filter(x => x.nodeHeadImg)
    if (imgs.length)
      return imgs
  }
  return []
}

async function loadHomeData() {
  loadTip.value = ''
  sections.value = []

  if (!siteNodeCode) {
    loadTip.value = '未配置 VITE_CMS_SITE_NODE_CODE，无法加载站点栏目'
    bannerList.value = [{ nodeHeadImg: defaultBanner }]
    return
  }

  try {
    const rootRes = await getCmsNodeList(siteNodeCode, 100)
    const rootRecords = pickCmsPageRecords(rootRes)

    bannerList.value = await resolveBanner(rootRecords)
    if (bannerList.value.length === 0)
      bannerList.value = [{ nodeHeadImg: defaultBanner }]

    const catalogs = rootRecords.filter(
      r => isCatalogNode(r) && !isBannerCatalog(r),
    )
    const built: ICmsSection[] = []
    for (const cat of catalogs) {
      if (!cat.nodeCode)
        continue
      const childRes = await getCmsNodeList(cat.nodeCode, 100)
      const items = pickCmsPageRecords(childRes).filter(Boolean)
      if (items.length)
        built.push({ catalog: cat, items })
    }
    // 根栏目下仅有稿件、未建子栏目时，原逻辑会得到空列表；补充一层平铺展示
    if (built.length === 0) {
      const rootDocs = rootRecords.filter(r => isDocumentNode(r))
      if (rootDocs.length) {
        built.push({
          catalog: { nodeName: '精选内容', nodeCode: '__flat_root_documents__' },
          items: rootDocs,
        })
      }
    }
    sections.value = built
  }
  catch {
    loadTip.value = 'CMS 内容加载失败，请稍后重试'
    bannerList.value = [{ nodeHeadImg: defaultBanner }]
    sections.value = []
  }
}

function openCmsNode(item: ICmsNodeItem, _sectionTitle = '') {
  if (isCatalogNode(item))
    return
  const link = item.nodeLink?.trim()
  if (link) {
    jump(toH5JumpableLink(link), item.nodeName || _sectionTitle || '详情')
    return
  }
  if (item.id) {
    jump(
      `/pages/cms/detail?id=${encodeURIComponent(item.id)}`,
      item.nodeName || _sectionTitle || '详情',
    )
    return
  }
  uni.showToast({ title: '未配置链接或详情', icon: 'none' })
}

onLoad(() => {
  loadHomeData()
})
</script>

<style lang="scss" scoped>
.home-page {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background: #0b1220;
}

.home-tech {
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
}

.home-scroll {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
  background: transparent;
}

.home-shell {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}

.home-scroll-inner {
  padding: 12px 16px 0;
}

@media (min-width: 768px) {
  .home-scroll-inner {
    padding-left: 32px;
    padding-right: 32px;
  }
}

.banner-tip {
  margin-bottom: 10px;
  padding: 10px 12px;
  font-size: 13px;
  color: #fecdd3;
  background: rgba(190, 24, 93, 0.25);
  border-radius: 10px;
  border: 1px solid rgba(251, 113, 133, 0.45);
}

.banner-swiper {
  width: 100%;
  height: 200px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 16px 36px rgba(190, 24, 93, 0.12);
}

@media (min-width: 768px) {
  .banner-swiper {
    height: 320px;
    border-radius: 20px;
  }
}

.banner-image {
  width: 100%;
  height: 100%;
}

.cms-section {
  margin-top: 28px;
}

.section-head {
  margin-bottom: 12px;
}

.section-title {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: #f9a8d4;
}

.section-sub {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.45;
}

.card-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

@media (min-width: 600px) {
  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 960px) {
  .card-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

.cms-card {
  display: flex;
  flex-direction: column;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #fce7f3;
  box-shadow: 0 10px 26px rgba(157, 23, 77, 0.06);
}

.cms-card:active {
  opacity: 0.94;
}

.cms-card-img {
  width: 100%;
  height: 140px;
  background: #fce7f3;
}

@media (min-width: 768px) {
  .cms-card-img {
    height: 160px;
  }
}

.cms-card-body {
  padding: 12px 14px 14px;
}

.cms-card-title {
  display: block;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.35;
}

.cms-card-sum {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
  line-height: 1.45;
}

.empty-site {
  margin-top: 36px;
  padding: 28px 16px;
  text-align: center;
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.5);
  border: 1px dashed rgba(251, 207, 232, 0.35);
}

.empty-title {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #fbcfe8;
}

.empty-desc {
  display: block;
  margin-top: 10px;
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.5;
}

.safe-bottom {
  height: calc(env(safe-area-inset-bottom) + 24px);
}
</style>
