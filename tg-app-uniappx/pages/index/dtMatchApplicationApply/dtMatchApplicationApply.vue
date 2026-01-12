<!--suppress ALL -->
<template>
  <view>
    <view v-if="matchmakers.length>0" style="background-color: #fff;color: #333333;margin-top: 30rpx;padding: 20px 32rpx;border-radius: 40rpx;">
      <view style="display: flex;align-items: center;margin-bottom: 20rpx;">
        <image src="xx" style="width: 30rpx;height: 30rpx;margin-right: 20rpx;"></image>
        <view style="font-size: 28rpx;font-weight: 600;color: #333333;">TA的红娘</view>
      </view>
      <view
          style="height: 215rpx;border-radius: 29rpx;padding: 20rpx;overflow-y: auto;display: flex;flex-direction: column;scrollbar-width: thin;scrollbar-color: #ccc transparent;position: relative;">
        <view style="display: flex;flex-wrap: wrap;justify-content: flex-start;">
          <!-- 红娘头像列表 -->
          <view v-for="(matchmaker, index) in matchmakers" :key="index"
                style="width: 33.33%;padding: 10rpx;box-sizing: border-box;">

            <view @click="queryDtMatchApplication()" style="display: flex;flex-direction: column;align-items: center;">
              <image :src="matchmaker.cmMt?matchmaker.cmMt.userAvatar:''" style="width: 120rpx;height: 120rpx;border-radius: 50%;"
                     mode="aspectFill"></image>
              <view style="margin-top: 10rpx;font-size: 24rpx;color: #333;">
                <text class="cuIcon-service" style="margin-left: 10rpx;color: #e70aef;"></text>
                红娘牵线
              </view>
            </view>

          </view>
        </view>
        <!-- 滚动提示指示器 -->
        <view v-if="matchmakers.length > 3"
              style="position: absolute;bottom: 10rpx;right: 20rpx;width: 20rpx;height: 20rpx;border: 2rpx solid #ccc;border-top: none;border-left: none;transform: rotate(45deg);opacity: 0.6;"></view>
      </view>
      <view v-if="showQx" mode="center" round="20" @close="showQx=false" @safeAreaInsetBottom="false">
        <view
            style="background: linear-gradient(180deg, #eab3fb 0%, #FFFFFF 100%);padding: 30rpx;border-radius: 40rpx;">
          <view style="display: flex;flex-direction: column;align-items: center;">
            <view @click="$loginModal.showModal()"
                  style="font-size: 32rpx;font-weight: 600;color: #333;margin-top: 20rpx;text-align: center;">
              联系对方的专属红娘
            </view>
            <view style="font-size: 32rpx;font-weight: 600;color: #333;margin-top: 20rpx;text-align: center;">
              专属红娘可以直接联系到TA，红娘牵线的目的依据双方诉求，促成双方获得直接沟通的机会，红娘应当尽心为当事人提供信息甄别与筛选服务，也应当实事求是为客户促成真实、真诚的沟通渠道，以上是红娘确认完成任务的基础，经由客户二次确认后服务费才会到红娘的账户，欢迎客户监督红娘服务是否真实，如若红娘违反诚信原则，平台绝不姑息，同时我们也会对监督的人进行现金奖励。
            </view>
            <view class="padding flex flex-direction">
              <button class="cu-btn bg-white bg-gradual-blue" @click="dtMatchApplicationApply()">
                支付100脱单币签约服务
              </button>
            </view>
          </view>
        </view>
      </view>
      <view v-if="showHn" mode="center" round="20" @close="showHn=false" @safeAreaInsetBottom="false">
        <view
            style="background: linear-gradient(180deg, #eab3fb 0%, #FFFFFF 100%);padding: 30rpx;border-radius: 40rpx;">
          <view style="display: flex;flex-direction: column;align-items: center;">
            <view @click="$loginModal.showModal()"
                  style="font-size: 32rpx;font-weight: 600;color: #333;margin-top: 20rpx;text-align: center;">
              红娘已收到您的请求
            </view>
            <view style="font-size: 32rpx;font-weight: 600;color: #333;margin-top: 20rpx;text-align: center;">
              请不要脱离平台与红娘交易，平台可以客观的，最大限度的保证及督促红娘提供的服务真实可靠，为客户、红娘双方架设出具有法律效力的合同，从而保障双方共同利益。
            </view>
            <view class="padding flex flex-direction">
              <button class="cu-btn bg-white bg-gradual-blue" @click="contactHisMk">联系他/她</button>
            </view>
          </view>
        </view>
      </view>

    </view>
  </view>

</template>

<script>


export default {
  name: "dtMatchApplicationApply",
  components: {},
  props: {
    pursuedTarget: {
      type: Object,
      default: {}
    },
  },
  watch:{
    pursuedTarget:{
      immediate:true,
      deep:true,
      handler(newValue, oldValue) {
        this.getRecommend(newValue)
      }
    }
  },
  data() {
    return {
      matchmakers:[],
      showHn: false,
      showQx: false,
      showMore: false
    }
  },



  methods: {
    getRecommend(newValue) {
      this.$http.get(`/cus/dating/dtCusMatchmaker/list?cmCusCode=${newValue.userCode}`).then(res => {
        this.matchmakers = res.data.records
        console.log('matchmakers',this.matchmakers)
        this.$forceUpdate()
      })
    },
    queryDtMatchApplication() {
      this.showQx = false
      this.$tool.loading('查询中……')
      let data = {
        dtMaMatchmakerSysUserCode: this.matchmaker.sysUserCode,
        dtMaPursuedSysUserCode: this.pursuedTarget.sysUserCode
      }
      this.$http.get('/cus/dating/dtMatch/list', {params: data}).then((res) => {
        this.$tool.loaded()
        if (res.result.records.length > 0) {
          this.$tool.goTo(`/pages/matchmaker/userInfoCard?sysUserMkSysUserCode=${this.matchmaker.userCode}`)
        } else {
          this.showQx = true
        }
      })
    },
    dtMatchApplicationApply() {
      this.showQx = false
      this.$tool.loading('支付中……')
      let data = {
        dtMaMatchmakerSysUserCode: this.matchmaker.sysUserCode,
        dtMaPursuedSysUserCode: this.pursuedTarget.sysUserCode
      }
      this.$http.post('/cus/dating/dtMatch/apply', data).then((res) => {
        this.$tool.loaded()
        if (res.result.tdOdPaidCode === '0') {
          this.$tool.confirm('余额不足，请充值！', true, '充值').then(() => {
            this.$tool.goTo(`/pages/account/recharge/recharge?currencyCode=TDB`)
          })
        } else {
          this.$tool.confirm('红娘已经收到您的牵线请求，请耐心等待。', true).then(() => {
          })
        }
      })
    },
  }


}
</script>
