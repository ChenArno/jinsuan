<template>
  <div class="cont" ref="set">
    <image class="shop" src="bmlocal://assets/shop.png"></image>
    <div class="label">
      <div class="label-cell">
        <text class="desc">重量</text>
        <text class="desc-now">{{weight-zero}}</text>
        <text class="desc-unit">g</text>
      </div>
      <div class="label-cell">
        <text class="desc">清零</text>
        <xy-button title="清零" @onclick="setZero"></xy-button>
      </div>
      <div class="label-cell">
        <text class="desc">毛重</text>
        <text class="desc-input">{{grossWeight}}</text>
        <!-- <input ref="input" class="desc-input" type="number" v-model="grossWeight" /> -->
        <text class="desc-unit">g</text>
        <xy-button title="确定" mini @onclick="setGross"></xy-button>
      </div>
      <div class="label-cell">
        <text class="desc">采样</text>
        <xy-button title="采样" @onclick="enterTrain"></xy-button>
      </div>
    </div>
  </div>
</template>
<script>
import { xyButton } from "Components";
const animation = weex.requireModule("animation");
const utils = weex.requireModule("utils");
const globalEvent = weex.requireModule("globalEvent");
export default {
  props: ["picture"],
  data() {
    return {
      device: "暂无设备",
      weight: 0,
      standard: 0,
      zero: 0,
      grossWeight: 0 //毛重
    };
  },
  components: { xyButton },
  eros: {
    backAppeared() {
      this.onblur();
    }
  },
  mounted() {
    if (this.$getDataSync("standard")) {
      this.grossWeight = parseInt(this.$getDataSync("standard"));
    }
    animation.transition(this.$refs.set, {
      styles: {
        // transform:'translateX(10%)'
        opacity: 1
      },
      duration: 1000
    });
    this.dataListen();
  },
  methods: {
    dataListen() {
      globalEvent.addEventListener("usb.device.realtime", params => {
        let { msg } = params;
        if (msg) {
          this.weight = parseInt(msg);
        }
      });
    },
    setZero() {
      utils.getStandard(res => {
        this.zero = parseInt(res);
        this.$setDataSync("zero", res);
        this.$notice.toast({
          message: "清零成功"
        });
      });
    },
    setGross() {
      this.grossWeight = this.weight - this.zero;
      this.$setDataSync("standard", this.grossWeight.toString());
      this.$notice.toast({
        message: "设置成功"
      });
    },
    enterTrain() {
      this.$router.open({
        name: "train",
        type: "PUSH"
      });
      this.$emit("onclick", false);
    },
    onblur() {
      this.$refs.input && this.$refs.input.blur();
    }
  }
};
</script>
<style scoped>
.cont {
  position: absolute;
  top: 30;
  left: 180;
  bottom: 30;
  right: 185;
  background-color: #ffffff;
  /* border-radius: 100; */
  opacity: 0;
}
.shop {
  width: 766;
  height: 343;
  position: absolute;
  bottom: 238;
  /* left: 157; */
}
.label {
  position: absolute;
  top: 280;
  right: 0;
  height: 400;
  width: 785;
  justify-content: space-around;
  align-items: center;
}
.label-cell {
  flex-direction: row;
  align-items: center;
}
.desc {
  font-size: 28;
  color: #000000;
  margin-right: 20;
  width: 110;
  text-align: right;
}
.desc-input {
  width: 265;
  height: 60;
  border-color: #a6a4a1;
  border-style: solid;
  border-width: 1;
  border-radius: 50;
  text-align: center;
  line-height: 60;
}
.desc-unit {
  padding: 0 10;
}
.desc-now {
  width: 400;
  height: 60;
  border-color: #a6a4a1;
  border-style: solid;
  border-width: 1;
  border-radius: 50;
  text-align: center;
  line-height: 60;
}
</style>
