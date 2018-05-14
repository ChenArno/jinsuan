<template>
  <div class="cont">
    <div class="shop" v-if="isShow">
      <image class="shop-cell" src="bmlocal://assets/shop.png"></image>
    </div>
    <div class="label">
      <image class="vanz" src="bmlocal://assets/vanz.png"></image>
      <text class="desc">欢迎使用智能结算台</text>
      <!-- <text class="desc">请将所有商品</text> -->
      <div class="depot-desc">
        <text class="desc-yellow">正面放置 请勿堆叠</text>
      </div>
    </div>
  </div>
</template>

<script>
const globalEvent = weex.requireModule("globalEvent");
const utils = weex.requireModule("utils");
export default {
  props: ["isShow"],
  data() {
    return {};
  },
  mounted() {
    // this.cameraLState = this.$storage.getSync("cameraL");
    // this.cameraRState = this.$storage.getSync("cameraR");
    // this.weightState = this.$storage.getSync("usb");
    // globalEvent.addEventListener("connectState", res => {
    //   let { connectState, device } = res;
    //   if (device == "cameraL") {
    //     this.cameraLState = connectState;
    //     this.$storage.setSync("cameraL", this.cameraLState);
    //   } else if (device == "cameraR") {
    //     this.cameraRState = connectState;
    //     this.$storage.setSync("cameraR", this.cameraRState);
    //   } else {
    //     this.weightState = connectState;
    //     this.$storage.setSync("usb", this.weightState);
    //   }
    // });
  }
};
</script>

<style scoped>
.cont {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
}
.shop {
  width: 960;
  height: 720;
  position: absolute;
  top: 100;
  left: 180;
  background-color: #ffffff;
  justify-content: center;
  align-items: center;
}
.shop-cell {
  width: 766;
  height: 343;
}
.label {
  position: absolute;
  top: 200;
  right: 18;
  height: 519;
  width: 785;
  justify-content: space-between;
  align-items: center;
}
.desc {
  color: #fbb735;
  font-size: 50;
  font-weight: 500;
}
.depot-desc {
  flex-direction: row;
}
.desc-yellow {
  color: #fd6157;
  font-size: 54;
  font-weight: 300;
}
.vanz {
  width: 390;
  height: 202;
  margin-bottom: 110;
}
</style>

