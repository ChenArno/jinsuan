<template>
  <div class="cont">
    <div class="order">
      <div class="order-head">
        <text class="iconfont">&#xe635;</text>
        <text class="order-head-text">请确认您的订单信息，并完成支付</text>
      </div>
      <div class="order-list">
        <text class="order-list-cell sku-name">商品名</text>
        <text class="order-list-cell">数量</text>
        <text class="order-list-cell original">商品价格</text>
        <text class="order-list-cell">优惠价格</text>
      </div>
      <scroller class="scro">
        <div class="order-list" v-for="(item,i) in lists" :key="i">
          <text class="order-list-cell sku-name">{{i+1}}.{{item.skuName}}</text>
          <text class="order-list-cell">{{item.tradeNum}}件</text>
          <text class="order-list-cell original">￥{{item.originalPrice}}</text>
          <text class="order-list-cell">￥{{item.itemPrice}}</text>
        </div>
      </scroller>
      <div class="order-foot order-shad">
        <text class="foot-desc">共计</text>
        <text class="foot-red"> {{lists.length}} </text>
        <text class="foot-desc">件 商品金额</text>
        <text class="pay-dollor"> {{totalAmount}} </text>
        <text class="foot-desc">元</text>
      </div>
      <div class="order-foot">
        <text class="foot-desc">应付金额</text>
        <text class="foot-red"> {{realTotalAmount}} </text>
        <text class="foot-desc">元</text>
      </div>
    </div>
    <div class="pay">
      <div class="pay-pic">
        <div class="pay-zhifubao" v-if="aliPayResult.resultStatus">
          <text class="pay-text">支付宝支付</text>
          <Qcode-view class="code" :code="{url:aliPayResult.data.codeUrl,type:1}"></Qcode-view>
        </div>
        <div class="pay-zhifubao" v-if="weixinPayResult.resultStatus">
          <text class="pay-text">微信支付</text>
          <Qcode-view class="code" :code="{url:weixinPayResult.data.codeUrl,type:2}"></Qcode-view>
        </div>
      </div>
      <div class="pay-submit">
        <text class="pay-desc">在支付宝或微信首页打开扫一扫</text>
        <text class="pay-desc">扫描二维码 输入金额完成支付</text>
      </div>
      <div class="pay-button">
        <xy-button :title="'支付成功，欢迎下次光临!('+countdown+')'" v-if="orderMsg.resultStatus" @onclick="onclick"></xy-button>
        <xy-button title="取消" @onclick="onclick" v-else></xy-button>
      </div>
    </div>
  </div>
</template>

<script>
const globalEvent = weex.requireModule("globalEvent");
import { xyButton } from "Components";
const utils = weex.requireModule("utils");
export default {
  props: ["orderResult"],
  data() {
    return {
      aliPayResult: {},
      weixinPayResult: {},
      totalAmount: 0,
      realTotalAmount: 0,
      lists: [],
      countdown: 3,
      orderMsg: {}
    };
  },
  components: { xyButton },
  mounted() {
    const { aliPayResult, weixinPayResult, order } = this.orderResult;
    this.aliPayResult = aliPayResult;
    this.weixinPayResult = weixinPayResult;
    this.lists = order.detailList;
    this.totalAmount = order.totalAmount;
    this.realTotalAmount = order.realTotalAmount;

    globalEvent.addEventListener("mq.sendMsg", params => {
      let { data } = params;
      if (data) {
        this.orderMsg = JSON.parse(data);
        utils.playSound(3);
        this.startcountdown();
      }
    });
  },
  methods: {
    startcountdown() {
      if (this.countdown < 1) {
        this.$emit("nextStep", 0);
        return;
      }
      this.countdown--;
      setTimeout(() => {
        this.startcountdown();
      }, 1000);
    },
    onclick() {
      this.$emit("nextStep", 0);
    }
  }
};
</script>

<style scoped>
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #666;
}
.cont {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  padding: 40 100 0;
  flex-direction: row;
  background-color: #ffffff;
}
.order {
  width: 945;
  height: 800;
  padding-left: 100;
}
.order-head {
  height: 100;
  flex-direction: row;
  align-items: center;
  padding-bottom: 20;
  border-bottom-style: dashed;
  border-bottom-width: 2;
  border-bottom-color: rgb(251, 183, 53);
}
.order-head-text {
  padding: 7 0;
  font-size: 36;
  color: #666666;
  margin-left: 10;
}
.order-foot {
  height: 95;
  flex-direction: row;
  justify-content: flex-end;
  align-items: center;
}
.order-shad {
  border-top-style: dashed;
  border-top-width: 2;
  border-top-color: rgb(251, 183, 53);
}
.foot-desc {
  font-size: 32;
  color: #000000;
}
.foot-red {
  font-size: 38;
  color: #ee4035;
}
.pay-dollor {
  font-size: 32;
  color: #000000;
}
/* .scro{
  background-color: gold;
} */
.order-list {
  height: 80;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
.order-list-cell {
  color: #000000;
  font-size: 32;
}
.pay {
  width: 680;
  height: 800;
  padding: 89 0;
}
.pay-pic {
  flex-direction: row;
  justify-content: space-around;
}
.pay-text {
  height: 80;
  justify-content: center;
}
.pay-zhifubao {
  align-items: center;
  justify-content: center;
}
.code {
  width: 200;
  height: 200;
}
.pay-submit {
  align-items: center;
  margin-top: 30;
  height: 130;
  justify-content: space-around;
}
.pay-desc {
  font-size: 24;
  color: #333333;
}
.pay-button {
  align-items: center;
  height: 160;
  justify-content: space-around;
}
.sku-name {
  lines: 1;
  width: 400;
}
.original:active {
  border-bottom-style: solid;
  border-bottom-color: #666666;
  border-bottom-width: 1;
  position: absolute;
  top: 10;
  left: 0;
}
</style>

