<template>
  <div class="head">
    <div class="formdate">
      <text class="font week">{{nowWeek}}</text>
      <text class="font date-cell">{{nowDate}}</text>
    </div>
    <div class="xy-time">
      <image class="back-line" src="bmlocal://assets/line.png"></image>
      <div class="xy-time-cell" v-for="i in 2" :key="i">
        <image class="back-time" src="bmlocal://assets/time.png"></image>
        <text class="xy-time-text">{{hour.substr(i-1,1)}}</text>
      </div>
      <text class="maohao">:</text>
      <div class="xy-time-cell" v-for="i in 2" :key="i">
        <image class="back-time" src="bmlocal://assets/time.png"></image>
        <text class="xy-time-text">{{min.substr(i-1,1)}}</text>
      </div>
    </div>
    <div class="right-button" @click="onlongPress">
      <image class="wifi-cell" :src="`bmlocal://assets/wifi-${wifi}${state}.png`"></image>
    </div>
    <div class="right-button right-button-active" @click="closeApp">
      <text class="iconfont">&#xe654;</text>
    </div>
    <xy-popover ref="wxc-popover" :buttons="btns" :position="popoverPosition" :arrowPosition="popoverArrowPosition" @wxcPopoverButtonClicked="popoverButtonClicked"></xy-popover>
  </div>
</template>
<script>
const globalEvent = weex.requireModule("globalEvent");
import { format } from "Utils";
import { xyPopover } from "Components";
const utils = weex.requireModule("utils");
export default {
  data() {
    return {
      mydate: "",
      weekday: [
        "星期日",
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六"
      ],
      nowWeek: "",
      nowDate: "",
      hour: "00",
      min: "00",
      wifi: "no",
      active: 0,
      btns: [
        {
          text: "采样",
          key: "key-scan"
        },
        {
          text: "设置",
          key: "key-qrcode"
        }
      ],
      popoverPosition: { x: -20, y: 70 },
      popoverArrowPosition: { pos: "top", x: -30 },
      state: "-red",
      cameraLState: "",
      cameraRState: "",
      weightState: "",
      isExit: false
    };
  },
  watch: {
    mydate(val) {
      this.nowWeek = this.weekday[val.getDay()];
      this.nowDate = `${format(val, "YYYY")}年${format(val, "MM")}月${format(
        val,
        "DD"
      )}日`;
      this.hour = format(val, "HH");
      this.min = format(val, "mm");
    }
  },
  mounted() {
    this.setTime();
    this.deviceAdd();
  },
  components: { xyPopover },
  methods: {
    setTime() {
      this.mydate = new Date();
      utils.getWifiStrong(res => {
        let wifi = parseInt(res);
        if (wifi >= -50) {
          this.wifi = "full";
        } else if (wifi >= -70 && wifi < -50) {
          this.wifi = "three";
        } else if (wifi >= -200 && wifi < -70) {
          this.wifi = "one";
        } else {
          this.wifi = "no";
        }
      });
      setTimeout(() => {
        this.setTime();
      }, 1000);
    },
    // headClick() {
    //   this.$emit("onclick");
    // },
    // openSet() {
    //   this.$router.open({
    //     name: "set"
    //   });
    // },
    closeApp() {
      if (!this.isExit) {
        utils.finish(this.isExit, res => {
          this.isExit = true;
        });
        return;
      }
      // utils.finish();
      this.$router.finish();
    },
    onlongPress() {
      this.$refs["wxc-popover"].wxcPopoverShow();
    },
    popoverButtonClicked({ key, index }) {
      if (index == 0) {
        this.$emit("onclick", true);
        return;
      }
      this.$emit("isopenRun");
      this.$router.open({
        name: "set"
      });
    },
    deviceAdd() {
      globalEvent.addEventListener("connectState", res => {
        let { connectState, device } = res;
        if (device == "cameraL") {
          this.cameraLState = connectState;
        } else if (device == "cameraR") {
          this.cameraRState = connectState;
        } else {
          this.weightState = connectState;
        }
        if (this.cameraLState == "成功" && this.weightState == "成功") {
          this.state = "";
        } else {
          this.state = "-red";
        }
      });
    }
  }
};
</script>

<style scoped>
.head {
  background-color: #f5a713;
  width: 1920;
  height: 120;
  padding-left: 50;
  flex-direction: row;
  align-items: center;
}
.formdate {
  width: 218;
  height: 120;
  align-items: flex-start;
  justify-content: center;
}
.font {
  color: #ffffff;
}
.week {
  font-size: 40;
  font-weight: 800;
}
.xy-time {
  height: 120;
  justify-content: center;
  flex-direction: row;
  align-items: center;
}
.xy-time-cell {
  height: 120;
  width: 40;
  justify-content: center;
  align-items: center;
  margin-right: 15;
}
.maohao {
  color: #ffffff;
  margin-right: 15;
  font-size: 50;
  font-weight: bold;
}
.xy-time-text {
  font-size: 50;
  color: #ffffff;
}
.back-time {
  width: 40;
  height: 70;
  position: absolute;
  top: 25;
}
.back-line {
  width: 3;
  height: 52;
  margin-right: 22;
}
.date-cell {
  font-size: 24;
  margin-top: 15;
}
.right-button {
  height: 120;
  width: 120;
  right: 0;
  top: 0;
  position: absolute;
  align-items: center;
  justify-content: center;
}
.right-button-active{
  right: 120;
}
.wifi-cell {
  width: 54;
  height: 40;
}
.iconfont {
  font-family: iconfont;
  font-size: 50;
  color: #ffffff;
  margin-right: 20;
}
.menu {
  height: 120;
  flex-direction: row;
  align-items: center;
  margin-left: 700;
}
.white {
  font-size: 40;
  color: #ffffff;
  margin: 0 30;
}
.white-active {
  border-bottom-color: #ffffff;
  border-bottom-width: 2;
  border-bottom-style: solid;
}
</style>

