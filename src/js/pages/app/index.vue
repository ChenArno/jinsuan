<template>
  <div>

    <head ref="head" @onclick="menuClick" @isopenRun="openRun=false"></head>
    <background :openRun="openRun" :startTime="startTime" @onstart="response" :next="next" @getImage="getImage" @isopenRun="openRun=true">
      <startPage v-if="next == 0"></startPage>
      <picter v-if="next == 1" :picture="src" :listGdaiLabel="listGdaiLabel" @nextStep="nextStep" @orderPay="orderPay"></picter>
      <order v-if="next == 2" @nextStep="nextStep" :orderResult="orderResult"></order>
      <setting v-if="setShow" @onclick="menuClick"></setting>
    </background>
    <popup :show="show"></popup>
    <mqtools-view ref="mq"></mqtools-view>
  </div>
</template>
<script>
import startPage from "./startPage";
import picter from "./picter";
import head from "./head";
import background from "./background";
import popup from "./popup";
import setting from "./setting";
import order from "./order";
import { analysisSku } from "Config/apis";
import { getAppInfo } from "Config/fetch";
import Server from "Config/server";
const utils = weex.requireModule("utils");
// import mq from "Config/mq";
import baseic from "Config/basic";
// 1920*1128  750*441  2.56
export default {
  data() {
    return {
      deviceSerial: "",
      groupUuid: "",
      next: 0,
      startTime: false,
      show: false,
      src: "",
      listGdaiLabel: [],
      curHomeBackTriggerTimes: 1,
      maxHomeBackTriggerTimes: 5,
      setShow: false,
      orderResult: {},
      mq: null,
      openRun: true //是否监听重力
    };
  },
  created() {
    // 安卓自定义退出 app
    // this.mq = new mq();
    getAppInfo().then(res => {
      new baseic(this.$refs.mq);
      // this.mq.mqInit(this.$refs.mq);
    });
    // this.deviceSerial = this.$getDataSync("deviceSerial");
    // if (!this.deviceSerial) {
    //   this.$notice.toast({
    //     message: "请先设置设备序列号"
    //   });
    // }
    // let { groupUuid } = this.$getDataJsonSync("groupUuid");
    // this.groupUuid = groupUuid;
    // if (!this.groupUuid) {
    //   this.$notice.toast({
    //     message: "请先设置样本组的uuid"
    //   });
    // }
    if (!this.$getDataSync("zero")) {
      this.$utils.toast("您好，第一次使用请点击无线图标进行采样设置。");
      return;
    }
  },
  components: { head, background, popup, startPage, picter, setting, order },
  methods: {
    response(val) {
      this.next = 0;
      if (val) {
        this.show = false;
        return;
      }
      this.startTime = false;
      this.show = true;
    },
    nextStep(val) {
      this.next = val;
      if (val == 1) {
        this.next = 1;
      } else if (val == 2) {
        this.openRun = false;
        return;
      }
      this.openRun = true;
    },
    getImage({ localPath, netPath, weight }, count) {
      if (!localPath || !localPath[1]) {
        this.show = false;
        return;
      }
      let stand = this.$getDataSync("standard");
      let zero = this.$getDataSync("zero");
      let getrid = stand ? parseInt(stand) : 0 + zero ? parseInt(zero) : 0;
      weight = weight - getrid;
      if (weight < 0) {
        this.show = false;
        utils.playSound(2);
        localPath.map(v => {
          utils.deleteImage(v);
        });
        this.$notice.toast({
          message: "请重新摆正托盘位置"
        });
        return;
      }
      let { groupUuid } = this.$getDataJsonSync("groupUuid");
      let deviceSerial = this.$getDataSync("deviceSerial");
      let { accUrl } = this.$getDataJsonSync("RescSer");
      if (!groupUuid) {
        this.$notice.toast({
          message: "请先设置样本组的uuid"
        });
        return;
      }
      if (!accUrl) {
        this.$notice.toast({
          message: "资源服务器未设置"
        });
        return;
      }
      let imgUrlList = netPath.map(v => {
        return accUrl + v;
      });
      //网络请求传到后台服务器 netpath
      let data = {
        imgUrlList,
        totalWeight: weight,
        deviceSerial,
        deviceType: Server.deviceType,
        groupUuid
      };
      this.listGdaiLabel = [];
      analysisSku(data)
        .then(res => {
          this.show = false;
          if (res.resultStatus) {
            if (count && count > 1) return;
            this.src = localPath[1];
            this.listGdaiLabel = res.data;
            this.nextStep(1);
            return;
          }
          if (res.status == 3010) {
            utils.playSound(2);
            this.$notice.toast({
              message: "请重新摆放结算商品..."
            });
            netPath.map(v => {
              utils.deleteImage(v);
            });
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          utils.playSound(2);
          this.show = false;
          this.$notice.toast({
            message: err
          });
          localPath.map(v => {
            utils.deleteImage(v);
          });
        });
    },
    androidFinishApp() {
      // const globalEvent = weex.requireModule("globalEvent");
      // globalEvent.addEventListener("homeBack", options => {
      //   this.curHomeBackTriggerTimes === this.maxHomeBackTriggerTimes &&
      //     this.$router.finish();
      //   this.curHomeBackTriggerTimes++;
      //   this.$notice.toast({
      //     message: `点击返回${
      //       this.maxHomeBackTriggerTimes
      //     }次之后，会关闭应用，当前点击第${this.curHomeBackTriggerTimes}次`
      //   });
      // });
    },
    menuClick(val) {
      if (this.setShow) {
        this.setShow = false;
        this.openRun = val;
        return;
      }
      this.openRun = false;
      this.setShow = true;
    },
    orderPay({ pay }) {
      this.orderResult = pay;
    }
  }
};
</script>
<style scoped>

</style>
