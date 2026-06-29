/** 用户协议、隐私政策（主包 `pages/legal`） */
export function openUserAgreement() {
  uni.navigateTo({ url: '/pages/legal/user-agreement' })
}

export function openPrivacyPolicy() {
  uni.navigateTo({ url: '/pages/legal/privacy-policy' })
}
