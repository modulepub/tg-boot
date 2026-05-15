<script lang="ts" setup>
defineOptions({
  name: 'LegalUserAgreement',
})

definePage({
  style: {
    navigationBarTitleText: '用户协议',
    navigationBarBackgroundColor: '#faf8fc',
    backgroundColor: '#faf8fc',
  },
})

const appName = String(import.meta.env.VITE_APP_TITLE || '卿卿').trim() || '卿卿'
const effectiveDate = '2026年5月4日'
</script>

<template>
  <view class="legal-page">
    <scroll-view scroll-y class="legal-scroll" enable-flex>
      <view class="legal-inner">
        <view class="legal-title">
          {{ appName }}用户服务协议
        </view>
        <view class="legal-meta">
          生效日期：{{ effectiveDate }}｜请您在使用前仔细阅读并充分理解本协议
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            一、协议的接受与适用范围
          </view>
          <view class="legal-p">
            欢迎您使用{{ appName }}微信小程序（以下简称「本小程序」）。当您注册、登录或以其他方式实际使用本小程序服务时，即视为您已阅读、理解并同意受本《用户服务协议》（以下简称「本协议」）约束。
          </view>
          <view class="legal-p">
            本小程序面向具有完全民事行为能力的成年人提供婚恋交友信息展示、情感顾问与红娘服务撮合、即时通讯、会员权益及订单相关服务。若您不具备相应民事行为能力，请勿使用本服务。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            二、服务说明与平台角色
          </view>
          <view class="legal-p">
            本小程序为信息服务平台：我们为用户提供资料展示、沟通工具、订单与权益管理等技术与运营支持。用户之间、用户与第三方服务提供者（如认证红娘）之间就线下见面、签约、付费等产生的法律关系，由相关主体依法自行承担；平台可在合理范围内提供协助与争议处理指引，但不构成对任何线下结果或婚恋结果的保证。
          </view>
          <view class="legal-p">
            运营者主体名称、联系方式等信息以微信小程序后台登记公示为准；您亦可通过本小程序「设置—联系我们」获取客服渠道。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            三、账号注册与使用规范
          </view>
          <view class="legal-p">
            您应使用本人真实有效的手机号码完成注册与登录，妥善保管验证码及登录状态，对以您账号进行的活动依法承担责任。如发现账号被盗用或存在异常，请立即联系客服。
          </view>
          <view class="legal-p">
            您承诺提交的资料（包括但不限于头像、生活照、职业、常驻城市、自我介绍等）真实、合法、准确，不得冒用、盗用他人信息，不得含有违法违规、虚假欺诈、淫秽色情、暴力恐怖、侮辱诽谤、骚扰恐吓、广告导流及其他违反法律法规或公序良俗的内容。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            四、实名认证与风险提示
          </view>
          <view class="legal-p">
            为保障交友安全与合规要求，我们可能要求您完成实名认证或补充资料。您理解并同意：实名认证由具备资质的第三方机构或监管认可的方式完成，您应按指引提供必要信息。
          </view>
          <view class="legal-p">
            网络交友存在不确定性。请您理性判断、谨慎交往，注意人身与财产安全；涉及资金往来、投资理财、借贷担保等请求的，务必提高警惕，谨防诈骗。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            五、用户行为规范
          </view>
          <view class="legal-p">
            您不得利用本小程序从事任何违反法律法规、部门规章及微信平台规则的行为，包括但不限于：传销、赌博、色情交易、侵犯他人知识产权与隐私、传播恶意程序、批量注册、数据爬取、干扰系统正常运行等。我们有权依据规则采取警告、限制功能、冻结账号、终止服务、保存记录并向主管部门报告等措施。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            六、订单、付费与退款
          </view>
          <view class="legal-p">
            若您购买会员权益、增值服务或第三方服务，具体权利义务、价格、有效期、使用规则及退费条件以相应商品说明、订单页面、服务协议及国家相关规定为准。您应在支付前仔细阅读并确认。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            七、知识产权
          </view>
          <view class="legal-p">
            本小程序内的界面设计、文案、标识、软件代码等知识产权归运营者或权利人所有。未经授权，您不得复制、传播、反向工程或用于商业目的。您上传的内容，您应保证拥有合法权利；为提供服务之必要，您授予我们在本小程序范围内展示、存储与处理的许可。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            八、免责声明（法定范围内）
          </view>
          <view class="legal-p">
            本小程序按「现状」和「可获得性」提供服务。因不可抗力、网络故障、系统维护、第三方服务异常、您自身原因等造成的服务中断或数据丢失，在法律允许范围内，我们将尽力修复，但不承担超出合理范围的间接损失或预期利益损失。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            九、协议变更与终止
          </view>
          <view class="legal-p">
            我们可能根据法律法规变化及业务调整更新本协议，并通过小程序公告、弹窗等合理方式提示。若您不同意更新内容，请停止使用服务；您继续使用视为接受更新后的协议。您可随时停止使用并申请注销账号（如功能可用），我们将按《隐私政策》处理您的个人信息。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            十、法律适用与争议解决
          </view>
          <view class="legal-p">
            本协议的订立、效力、解释与争议解决适用中华人民共和国大陆地区法律（不含冲突规范）。因本协议引起的争议，双方应友好协商；协商不成的，任一方可向被告住所地有管辖权的人民法院提起诉讼。
          </view>
        </view>

        <view class="legal-block">
          <view class="legal-h1">
            十一、其他
          </view>
          <view class="legal-p">
            本协议未尽事宜，适用《中华人民共和国个人信息保护法》《中华人民共和国网络安全法》《中华人民共和国民法典》等相关法律法规及微信平台规则。若本协议任何条款被认定无效，不影响其余条款效力。
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
