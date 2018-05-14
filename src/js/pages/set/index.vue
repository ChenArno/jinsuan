<template>
  <div>

    <back-head>
      <scroller class="scroller" scroll-direction="horizontal" flex-direction="row">
        <div class="menu" v-for="i in menus" :key="i.id">
          <text :class="['menu-cell',active == i.id?'menu-cell-active':'']" @click="menuClick(i)">{{i.label}}</text>
        </div>
      </scroller>
    </back-head>
    <backGround ref="back">
      <server></server>
      <pic-server></pic-server>
    </backGround>

  </div>
</template>
<script>
import backHead from "../backHead.vue";
import backGround from "./background";
import server from "./server";
import picServer from "./picServer";

export default {
  components: { backHead, backGround, server, picServer },
  data() {
    return {
      menus: [{ id: 1, label: "服务器设置" }, { id: 2, label: "图像设置" }],
      active: 1
    };
  },
  beforeCreate() {
    const domModule = weex.requireModule("dom");
    domModule.addRule("fontFace", {
      fontFamily: "iconfont",
      src: "url('bmlocal://iconfont/xy.ttf')"
    });
  },
  methods: {
    menuClick(val) {
      this.active = val.id;
      this.$refs.back.activeClick(val.id);
    }
  }
};
</script>
<style scoped>
.scroller {
  flex-direction: row;
  align-items: center;
}
.menu {
  width: 250;
  justify-content: center;
  align-items: center;
}
.menu-cell {
  font-size: 36;
  color: #ffffff;
  border-bottom-style: solid;
  border-bottom-width: 2;
  border-bottom-color: transparent;
  padding-bottom: 20;
}
.menu-cell:active {
  color: rgba(255, 255, 255, 0.6);
}
.menu-cell-active {
  color: #ee4035;
  border-bottom-color: #ee4035;
}
</style>
