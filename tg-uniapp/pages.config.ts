/*
 * 路由表由 dev/build 时 vite-plugin-uni-pages 扫描 src/pages 下各 .vue 的 definePage，合并写入 src/pages.json。
 * 请勿手动删除 pages.json；删后若未及时重启 dev，H5 路由表残缺会导致任意路径都落到首页。
 * 若出现重复路由：停止 dev，修正多余页面或条件编译后再启动，必要时用 git 还原 src/pages.json。
 */
import { defineUniPages } from '@uni-helper/vite-plugin-uni-pages'
import { tabBar } from './src/tabbar/config'

export default defineUniPages({
  globalStyle: {
    navigationStyle: 'default',
    navigationBarTitleText: '加载中……',
    navigationBarBackgroundColor: '#f8f8f8',
    navigationBarTextStyle: 'black',
    backgroundColor: '#FFFFFF',
  },
  easycom: {
    autoscan: true,
    custom: {
      '^fg-(.*)': '@/components/fg-$1/fg-$1.vue',
      '^(?!z-paging-refresh|z-paging-load-more)z-paging(.*)':
        'z-paging/components/z-paging$1/z-paging$1.vue',
    },
  },
  // tabbar 的配置统一在 “./src/tabbar/config.ts”（NO_TABBAR 仍保留占位配置，避免 H5 运行时崩溃）
  tabBar: tabBar as any,
})
