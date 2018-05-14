<template>
  <div class="body">
    <div class="back">
      <image class="back-img" src="bmlocal://assets/back.png"></image>
      <div class="tab-page-wrap">
        <div class="main" ref="main">
          <slot></slot>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
const animation = weex.requireModule("animation");
export default {
  methods: {
    activeClick(val) {
      const dist = (val - 1) * 1430;
      animation.transition(
        this.$refs.main,
        {
          styles: {
            transform: `translateX(${-dist}px)`
          },
          duration: 300,
          timingFunction: "cubic-bezier(0.25, 0.46, 0.45, 0.94)",
          delay: 0
        },
        () => {}
      );
    }
  }
};
</script>
<style scoped>
.body {
  width: 1920;
  height: 1080;
  background-color: rgb(251, 183, 53);
  align-items: center;
}
.back {
  width: 1845;
  height: 780;
  margin-top: 86;
  margin-left: 48;
  position: relative;
}
.back-img {
  width: 1845;
  height: 780;
  position: absolute;
  left: 0;
  right: 0;
}
.tab-page-wrap {
  width: 1430;
  padding: 0 291 50 199;
  flex: 1;
  overflow: hidden;
}
.main {
  position: absolute;
  flex: 1;
  flex-direction: row;
}
</style>
