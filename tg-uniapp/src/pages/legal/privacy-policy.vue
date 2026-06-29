<script lang="ts" setup>
defineOptions({
  name: 'LegalPrivacyPolicy',
})

definePage({
  style: {
    navigationBarTitleText: '隐私政策',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

const appName = String(import.meta.env.VITE_APP_TITLE || '卿卿').trim() || '卿卿'
const updateDate = '2026年5月4日'
</script>

<template>
  <view class="legal-page">
    <scroll-view scroll-y class="legal-scroll" enable-flex>
      <view class="legal-inner">
        <view class="legal-title">
          {{ appName }}隐私政策
        </view>
        <view class="legal-meta">
          更新日期：{{ updateDate }}｜本政策说明我们如何收集、使用、存储、共享与保护您的个人信息
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            一、引言
          </view>
          <view class="legal-p">
            我们深知个人信息对您的重要性，将严格遵守《中华人民共和国个人信息保护法》《中华人民共和国网络安全法》《儿童个人信息网络保护规定》等相关法律法规，采取相应的安全保护措施，尽力保护您的个人信息安全可控。
          </view>
          <view class="legal-p">
            本政策适用于{{ appName }}微信小程序向您提供的功能与服务。如某类功能设有专门说明或授权提示，以该说明或授权为准；未尽事宜以本政策为准。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            二、我们如何收集与使用个人信息
          </view>
          <view class="legal-p">
            我们仅在为实现产品功能、履行法定义务或取得您同意所必需的范围内处理个人信息，典型场景包括：
          </view>
          <view class="legal-p">
            1）账号与登录：手机号码、短信验证码，用于创建账号、登录验证、安全风控与客服联络；
          </view>
          <view class="legal-p">
            2）资料与展示：您主动填写的昵称、头像、常驻城市、职业、照片、自我介绍等，用于资料卡展示、匹配推荐与红娘服务；
          </view>
          <view class="legal-p">
            3）沟通与社交：您在聊天、申请好友、打招呼等场景产生的文本与多媒体信息，用于实现即时通讯与社交功能；
          </view>
          <view class="legal-p">
            4）实名认证：在您选择完成实名时，按监管及合作方要求收集姓名、身份证件信息等，用于身份核验；
          </view>
          <view class="legal-p">
            5）订单与支付：与交易相关的订单号、支付状态、权益信息，用于完成履约、对账与客服；
          </view>
          <view class="legal-p">
            6）设备与日志：设备型号、操作系统、网络类型、IP 地址、操作日志、崩溃信息，用于保障服务安全、排查故障与统计分析（在合法前提下可能进行去标识化处理）。
          </view>
          <view class="legal-p">
            当您使用相册、相机、麦克风等敏感能力时，我们将通过系统授权弹窗征得您的同意；您可在系统设置中撤回授权，但可能导致部分功能不可用。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            三、Cookie 与本地存储
          </view>
          <view class="legal-p">
            为保障登录态、偏好设置与基础性能，我们可能使用本地存储或同类技术。您可通过清除缓存或卸载小程序等方式进行管理。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            四、我们如何共享、转让与公开披露
          </view>
          <view class="legal-p">
            我们不会向无关第三方出售您的个人信息。仅在以下情形下共享：
          </view>
          <view class="legal-p">
            1）取得您的单独同意；
          </view>
          <view class="legal-p">
            2）为实现支付、实名认证、即时通讯、短信验证、云存储、数据分析等目的，向具有合法资质的合作方提供必要信息，并与其签署严格的保密与数据处理协议，要求其按法律与本政策处理；
          </view>
          <view class="legal-p">
            3）根据法律法规、诉讼争议解决需要，或按行政、司法机关依法提出的要求提供。
          </view>
          <view class="legal-p">
            如因合并、分立、资产转让等导致信息转移，我们将要求继受方继续受本政策约束，或重新征得您的授权。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            五、存储地点与期限
          </view>
          <view class="legal-p">
            原则上，我们在中华人民共和国境内运营中收集和产生的个人信息存储于境内。在为实现目的所必需的最短期限内保存您的信息；超出保存期限后，我们将删除或匿名化处理，法律法规另有规定的除外。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            六、我们如何保护信息安全
          </view>
          <view class="legal-p">
            我们采用符合业界标准的安全防护措施，包括权限控制、传输加密、访问审计、最小必要授权等，防止数据遭到未经授权的访问、披露、篡改或丢失。请您理解，任何网络环境并非绝对安全，请妥善保管验证码与设备。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            七、您的权利
          </view>
          <view class="legal-p">
            在法律规定的范围内，您有权查阅、复制、更正、补充、删除您的个人信息，有权撤回同意、限制或拒绝特定处理、注销账号（如产品提供相应入口）。您可通过「设置—联系我们」或客服渠道提交请求，我们将在法定期限内答复。注销后，我们将停止为您提供服务并依法删除或匿名化处理相关信息，法律法规另有规定的除外。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            八、未成年人保护
          </view>
          <view class="legal-p">
            本小程序主要面向成年人。若您为未成年人，请在监护人指导下阅读本政策并在取得监护人同意后使用。我们不会主动收集未成年人的个人信息；如发现误收集，将尽快删除。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            九、政策更新
          </view>
          <view class="legal-p">
            我们可能适时修订本政策。重大变更将通过弹窗、公告等显著方式提示。若您继续使用服务，即表示同意更新后的政策。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            十、联系我们
          </view>
          <view class="legal-p">
            如您对本政策或个人信息处理有任何疑问、意见或投诉，请通过本小程序「设置—联系我们」与我们取得联系。我们将在验证您的身份后尽快回复。
          </view>
        </view>

        <view class="legal-foot">
          {{ appName }}团队
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.legal-page {
  height: 100vh;
  box-sizing: border-box;
  background: #faf8fc;
}

.legal-scroll {
  height: 100%;
  box-sizing: border-box;
}

.legal-inner {
  padding: 28rpx 32rpx calc(28rpx + env(safe-area-inset-bottom));
  box-sizing: border-box;
}

.legal-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #2d1f28;
  line-height: 1.45;
}

.legal-meta {
  margin-top: 16rpx;
  font-size: 22rpx;
  color: #8b7585;
  line-height: 1.55;
}

.legal-block {
  margin-top: 36rpx;
}

.legal-h1 {
  font-size: 28rpx;
  font-weight: 600;
  color: #3d2a35;
  margin-bottom: 14rpx;
}

.legal-p {
  font-size: 26rpx;
  color: #5c4a55;
  line-height: 1.85;
  margin-bottom: 16rpx;
  text-align: justify;
}

.legal-foot {
  margin-top: 40rpx;
  padding-bottom: 24rpx;
  font-size: 24rpx;
  color: #a896a0;
  text-align: center;
}
</style>
