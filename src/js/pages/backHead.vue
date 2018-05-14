<template>
  <div class="head">
    <div class="back" @click="goBack">
      <text class="iconfont">&#xe612;</text>
    </div>
    <div class="menu">
      <slot></slot>
    </div>
    <div class="formdate">
      <text class="font week">{{nowWeek}}</text>
      <text class="font date-cell">{{nowDate}}</text>
    </div>
    <div class="xy-time" @click="headClick">
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
  </div>
</template>
<script>
import { format } from "Utils";
const utils = weex.requireModule("utils");
export default {
  props: ["isnotAllow"],
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
      wifi: "full",
      active: 0
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
  },
  methods: {
    setTime() {
      this.mydate = new Date();
      setTimeout(() => {
        this.setTime();
      }, 1000);
    },
    goBack() {
      if (this.isnotAllow) {
        this.$router.setBackParams({
          name: "train"
        });
      }
      this.$router.back({
        length: 1
      });
    }
  }
};
</script>

<style scoped>
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #ffffff;
}
.iconfont:active {
  color: rgba(255, 255, 255, 0.5);
}
.head {
  background-color: #f5a713;
  width: 1920;
  height: 120;
  padding-left: 50;
  flex-direction: row;
  align-items: center;
  justify-content: flex-end;
}
.back {
  position: absolute;
  top:0;
  left: 0;
  width: 120;
  height: 120;
  align-items: center;
  justify-content: center;
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
.wifi {
  width: 54;
  height: 40;
  right: 40;
  top: 40;
  position: absolute;
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
.menu {
  width: 800;
  height: 120;
  margin-right: 200;
}
</style>

