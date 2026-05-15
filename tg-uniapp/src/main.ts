import { createSSRApp } from 'vue'
import App from './App.vue'
import { requestInterceptor } from './http/interceptor'
import { routeInterceptor } from './router/interceptor'
import { refreshCustomerIfLoggedIn } from '@/utils/refreshCustomerSession'

import store from './store'
import '@/style/index.scss'
/** 仅当 html 含类名 `no-native-tabbar` 时生效，用于 H5 隐藏原生底栏 */
import '@/style/h5-hide-native-tabbar.scss'
// #ifdef H5
import { selectedTabbarStrategy, TABBAR_STRATEGY_MAP } from '@/tabbar/config'

/** 尽早打上标记，避免首屏原生 Tab 闪烁 */
if (typeof document !== 'undefined' && selectedTabbarStrategy === TABBAR_STRATEGY_MAP.NO_TABBAR) {
  document.documentElement.classList.add('no-native-tabbar')
}
// #endif
import 'virtual:uno.css'

export function createApp() {
  const app = createSSRApp(App)
  app.use(store)
  app.use(routeInterceptor)
  app.use(requestInterceptor)

  /** 每个页面 onShow 时刷新客户资料（与 getInfo 策略一致） */
  app.mixin({
    onShow() {
      refreshCustomerIfLoggedIn()
    },
  })

  return {
    app,
  }
}
