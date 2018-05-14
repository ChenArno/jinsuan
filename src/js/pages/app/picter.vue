<template>
  <div class="cont">
    <div class="image">
      <image ref="box" class="image-cell" :src="picture" :style="{width:width,height:height}"></image>
      <xy-block v-for="(item,i) in lists" :key="i" :param="item" :listLabelSkuComb="listLabelSkuComb" @confirm="confirm"></xy-block>
    </div>
    <div class="confirm">
      <image class="vanz" src="bmlocal://assets/vanz.png"></image>
      <text class="confirm-cell">请确认识别的商品信息</text>
      <xy-button title="确认" :disabled="disabled" @onclick="onclick"></xy-button>
      <xy-button title="再试一次" @onclick="onclickAgain"></xy-button>
    </div>
    <xy-loading :show="loadShow" type="trip"></xy-loading>
  </div>
</template>
<script>
const globalEvent = weex.requireModule("globalEvent");
import { xyBlock, xyButton, xyLoading } from "Components";
import trainBackVue from "../train/trainBack.vue";
import Server from "Config/server";
import { addOrderWithSkuNumByDevice, updateRecgLogStatus } from "Config/apis";
export default {
  props: {
    picture: {
      type: String,
      default: ""
    },
    listGdaiLabel: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      width: 960,
      height: 720,
      lists: [],
      listLabelSkuComb: [],
      listItemSku: [],
      disabled: true,
      loadShow: false,
      checkOrder: [],
      logId: -1
    };
  },
  components: { xyButton, xyBlock, xyLoading },
  mounted() {
    this.init(this.listGdaiLabel);
  },
  methods: {
    init(val) {
      this.logId = val.logId;
      this.listItemSku = val.listItemSku;
      this.lists = val.listGdaiLabel.map(v => {
        return {
          id: v.id,
          top: v.yValue * this.height,
          left: v.xValue * this.width,
          width: v.widthValue * this.width,
          height: v.hightValue * this.height,
          labelList: v.listLabelSku,
          labelUuid: v.labelUuid
        };
      });
      this.listLabelSkuComb = val.listLabelSkuComb;
      if (this.listLabelSkuComb.length == 1) {
        this.disabled = false;
        this.confirm(this.listLabelSkuComb[0]);
      }
    },
    onclick() {
      this.addOrderWithSkuNumByDevice();
    },
    confirm(val) {
      this.checkOrder = [];
      val.map(v => {
        this.listItemSku.map(x => {
          if (x.skuNum == v.skuNum) {
            this.checkOrder = [...this.checkOrder, ...[x]];
          }
        });
      });
      this.disabled = false;
    },
    onclickAgain() {
      this.loadShow = true;
      updateRecgLogStatus(this.logId)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            this.$emit("nextStep", 0);
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.loadShow = false;
        });
    },
    addOrderWithSkuNumByDevice() {
      this.loadShow = true;
      let orderSkuList = this.checkOrder.map(v => {
        return {
          skuName: v.skuName,
          skuNum: v.skuNum,
          skuLinkNum: v.linkNum,
          spuNum: v.spuNum,
          spuName: v.spuName,
          price: v.price,
          tradeNum: 1
        };
      });
      let data = {
        deviceSerial: this.$getDataSync("deviceSerial"),
        deviceType: Server.deviceType,
        orderSkuList
      };
      // this.$console(data);
      addOrderWithSkuNumByDevice(data)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            this.$emit("orderPay", { pay: res.data });
            this.$emit("nextStep", 2);
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.loadShow = false;
        });
    }
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
  align-items: center;
  flex-direction: row;
  background-color: #ffffff;
}
.image {
  width: 1200;
  margin-left: 20;
  height: 820;
  position: relative;
  justify-content: center;
  align-items: center;
}
.image-cell {
  border-radius: 30;
}
.confirm {
  margin-left: 60;
  width: 470;
  height: 609;
  align-items: center;
  justify-content: space-between;
}
.confirm-cell {
  font-size: 34;
  color: #666666;
}
.vanz{
  width: 390;
  height: 202;
  margin-bottom: 130;
}
</style>
