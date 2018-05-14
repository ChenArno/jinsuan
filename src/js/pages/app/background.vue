<template>
  <div class="cont">
    <div class="body">
      <div class="back">
        <!-- <text>{{weight}}g</text> -->
        <image class="back-img" src="bmlocal://assets/back.png"></image>
        <Camera-view ref="camera" class="camera"></Camera-view>
        <slot></slot>
      </div>
    </div>
    <div class="circle" v-if="next == 0">
      <div class="circle-cell">
        <text class="circle-time">{{countdown}}</text>
      </div>
    </div>
  </div>
</template>
<script>
import { format } from "Utils";
const globalEvent = weex.requireModule("globalEvent");
const utils = weex.requireModule("utils");
// 1920*1128  750*441  2.56
export default {
  props: ["startTime", "next", "openRun"],
  data() {
    return {
      countdown: 3,
      weight: 0,
      actualWeight: 0,
      diff: 20,
      timeOut: null,
      zero: 20,
      isPlaying: false
    };
  },
  eros: {
    appeared() {
      this.$refs.camera.uvcInit();
    }
  },
  mounted() {
    this.dataListen();
    globalEvent.addEventListener("viewDidAppear", ({ type }) => {
      if (type == "back") {
        this.$emit("isopenRun");
        this.$storage.get("router.backParams").then(({ name }) => {
          if (name === "train") {
            this.$refs.camera.uvcInitAgain();
          }
          this.$storage.deleteSync("router.backParams");
        });
      }
    });
  },
  watch: {
    startTime(val) {
      if (!this.openRun) return;
      if (val) {
        this.startcountdown();
      } else {
        this.countdown = 3;
      }
    },
    weight(val, old) {
      if (!this.openRun) return;
      this.countdown = 3;
      if (this.timeOut) {
        clearTimeout(this.timeOut);
      }
      this.zero = this.$getDataSync("zero")
        ? parseInt(this.$getDataSync("zero"))
        : 0;

      if (val <= 25) {
        //处理不正常重量
        this.$emit("onstart", true);
        return;
      }
      if (!this.isPlaying) {
        utils.playSound(1);
        this.isPlaying = true;
      }
      this.startcountdown();
    },
    countdown(val) {
      if (val < 2) {
        this.isPlaying = false;
        this.$emit("onstart", false);
        utils.getStandard(res => {
          this.actualWeight = parseInt(res);
          this.$refs.camera.photo(val => {
            if (typeof val == "string") {
              this.uploadImage(["", val]);
              return;
            }
            let { leftPath, rightPath } = val;
            if (!rightPath) {
              this.$emit("getImage", {});
              return;
            }
            this.uploadImage([leftPath, rightPath]);
          });
        });
      }
    }
  },
  methods: {
    dataListen() {
      globalEvent.addEventListener("usb.device.receive", params => {
        let { msg } = params;
        if (msg) {
          this.weight = parseInt(msg);
        }
      });
    },
    startcountdown() {
      if (this.countdown < 2) {
        return;
      }
      this.countdown--;
      this.timeOut = setTimeout(() => {
        this.startcountdown();
      }, 1000);
    },
    uploadImage(params) {
      let succ = [];
      params.map((v, i) => {
        if (!v) return;
        this.$refs.camera.uploadImage(
          v,
          success => {
            succ = [...succ, ...[success]];
            if (i == 1) {
              let data = {
                localPath: params,
                netPath: succ,
                weight: this.actualWeight
              };
              if (this.countdown > 1) {
                return;
              }
              this.$emit("getImage", data, this.countdown);
            }
          },
          error => {
            this.$notice.toast({
              message: error
            });
            this.$emit("getImage", {});
            if (!v) return;
            utils.deleteImage(v);
          }
        );
      });
    }
  }
};
</script>
<style scoped>
.cont {
  position: relative;
}

.body {
  width: 1920;
  height: 1140;
  background-color: rgb(251, 183, 53);
}
.back {
  width: 1845;
  height: 900;
  margin-top: 86;
  margin-left: 48;
  position: relative;
}
.back-img {
  width: 1845;
  height: 900;
}

.circle {
  width: 150;
  height: 150;
  border-radius: 75;
  border-style: solid;
  border-color: #ffffff;
  border-width: 4;
  position: absolute;
  right: 30;
  top: 900;
  justify-content: center;
  align-items: center;
}
.circle-cell {
  width: 120;
  height: 120;
  border-radius: 60;
  border-style: solid;
  border-color: #ffffff;
  border-width: 4;
  justify-content: center;
  align-items: center;
}
.circle-time {
  font-size: 60;
  color: #fd6157;
}

.camera {
  width: 960;
  height: 720;
  position: absolute;
  top: 100;
  left: 180;
  background-color: #000000;
  border-radius: 20;
}
.weight {
  width: 1;
  height: 1;
  position: absolute;
  top: 0;
  right: 0;
}
</style>
