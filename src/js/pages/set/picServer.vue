<template>
  <div class="server">
    <div class="set">
      <text class="set-head">【设备编码】</text>
      <div class="set-cont">
        <text class="iconfont">&#xe636;</text>
        <input class="set-input set-port" :disabled="true" v-model="deviceSerial" type="text" />
      </div>
    </div>
    <div class="set">
      <text class="set-head">【样本组】</text>
      <div class="set-cont">
        <text class="iconfont">&#xe6e5;</text>
        <div class="set-input set-port set-desc">
          <text>{{groupObj.groupName}}</text>
          <text class="iconfont arrow" @click="getOrgGroupList">&#xe607;</text>
          <wxc-popover ref="wxc-popover-server" :buttons="serverbtns" :position="serPosition" :arrowPosition="popoverArrowPosition" @wxcPopoverButtonClicked="serClicked"></wxc-popover>
        </div>
      </div>
    </div>
    <div class="set">
      <text class="set-head">【服务器oss】</text>
      <div class="set-cont">
        <text class="iconfont">&#xe761;</text>
        <div class="set-input set-port set-desc">
          <text>{{ossServer.name}}</text>
          <text class="iconfont arrow" @click="getActiveRescSerList">&#xe607;</text>
          <wxc-popover ref="wxc-popover" :buttons="btns" :position="popoverPosition" :arrowPosition="popoverArrowPosition" @wxcPopoverButtonClicked="popoverClicked"></wxc-popover>
        </div>
      </div>
    </div>
    <div class="set button">
      <xy-button title="保存" @onclick="enterTrain"></xy-button>
    </div>
    <xy-loading :show="isShow" type="trip"></xy-loading>
  </div>
</template>
<script>
import Server from "Config/server";
import { WxcPopover } from "weex-ui";
import { getActiveRescSerList, getOrgGroupList } from "Config/apis";
import { xyButton, xyLoading } from "Components";
export default {
  components: { xyButton, xyLoading, WxcPopover },
  data() {
    return {
      deviceSerial: "",
      groupObj: {},
      ossServer: {},
      isShow: false,
      btns: [],
      popoverPosition: { x: -590, y: 800 },
      popoverArrowPosition: { pos: "top", x: -15 },
      RescSers: [],
      serverbtns: [],
      serPosition: { x: -590, y: 600 },
      groupLists: []
    };
  },
  created() {
    this.ossServer = this.$getDataJsonSync("RescSer");
    this.deviceSerial = this.$getDataSync("deviceSerial");
    this.groupObj = this.$getDataJsonSync("groupUuid");
  },
  methods: {
    enterTrain() {
      this.$setDataSync("RescSer", this.ossServer);
      this.$setDataSync("groupUuid", this.groupObj);
      this.$notice.toast({
        message: "修改成功"
      });
    },
    getActiveRescSerList() {
      this.isShow = true;
      getActiveRescSerList()
        .then(res => {
          this.isShow = false;
          if (res.resultStatus) {
            this.RescSers = res.data;
            this.btns = res.data.map(v => {
              return {
                text: v.name,
                key: v.id
              };
            });
            this.$refs["wxc-popover"].wxcPopoverShow();
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.isShow = false;
        });
    },
    popoverClicked({ key, index }) {
      this.RescSers.map(res => {
        if (res.id == key) {
          this.ossServer = res;
        }
      });
    },
    getOrgGroupList() {
      this.isShow = true;
      let data = {
        deviceSerial: this.deviceSerial,
        deviceType: "guide_tablet",
        isActive: 1
      };
      getOrgGroupList(data)
        .then(res => {
          this.isShow = false;
          if (res.resultStatus) {
            this.groupLists = res.data;
            this.serverbtns = res.data.map(v => {
              return {
                text: v.groupName,
                key: v.id
              };
            });
            this.$refs["wxc-popover-server"].wxcPopoverShow();
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.isShow = false;
        });
    },
    serClicked({ key, index }) {
      this.groupLists.map(res => {
        if (res.id == key) {
          this.groupObj = res;
        }
      });
    }
  }
};
</script>
<style scoped>
.server{
  width: 1430;
}
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #808080;
  margin-right: 20;
}
.set {
  align-items: center;
}
.set-head {
  color: #ee4035;
  font-size: 40;
  font-weight: 200;
  margin-top: 50;
  margin-bottom: 50;
}
.set-cont {
  flex-direction: row;
  align-items: flex-end;
}
.set-input {
  width: 150;
  height: 60;
  border-bottom-color: #808080;
  border-bottom-style: solid;
  border-bottom-width: 1;
  text-align: center;
  font-size: 28;
}
.default {
  color: #a6a6a6;
  margin-top: 20;
}
.set-port {
  width: 640;
}
.set-desc {
  align-items: center;
  justify-content: center;
}
.button {
  margin-top: 30;
}
.server-test {
  color: #ee4035;
  margin-top: 20;
  font-size: 28;
}
.arrow {
  position: absolute;
  right: 0;
  top: 0;
  font-size: 60;
}
</style>

