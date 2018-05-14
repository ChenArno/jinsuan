<template>
  <div>
    <xy-overlay :show="true" v-if="show" ref="overlay" :can-auto-close="false" v-bind="overlayCfg"></xy-overlay>
    <div ref="wxc-popup" v-if="show" :style="{width,height}" class="wxc-popup">
      <div class="popup">
        <!-- <image class="look" src="bmlocal://assets/loading.gif" resize="contain" quality="original"></image> -->
        <loading-view class="loading"></loading-view>
        <text class="look-text">正在识别中,请稍等...</text>
      </div>
    </div>
  </div>
</template>
<script>
import {xyOverlay} from "Components";
const animation = weex.requireModule("animation");
export default {
  components: { xyOverlay },
  props: {
    overlayCfg: {
      type: Object,
      default: () => ({
        hasAnimation: true,
        timingFunction: ["ease-in", "ease-out"],
        duration: 300,
        opacity: 0
      })
    },
    height: {
      type: [Number, String],
      default: 720
    },
    width: {
      type: [Number, String],
      default: 960
    },
    show: {
      type: Boolean
    }
  },
  computed: {
    isNeedShow() {
      setTimeout(() => {
        this.appearPopup(this.show);
      }, 50);
      return this.show;
    }
  },
  data() {
    return {
      isBottomShow: false
    };
  },
  methods: {
    appearPopup(bool, duration = 100) {
      this.isShow = bool;
      const popupEl = this.$refs["wxc-popup"];
      if (!popupEl) {
        return;
      }
      animation.transition(
        popupEl,
        {
          styles: {
            transform: `translateY(380px)`
          },
          duration,
          delay: 0
        },
        () => {}
      );
    }
  }
};
</script>
<style scoped>
.wxc-popup {
  background-color: rgba(0,0,0,0.6);
  opacity: 0.6;
  position: fixed;
  top: 300;
  left: 230;
}
.popup {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  justify-content: center;
  align-items: center;
}
.loading {
  width: 305;
  height: 305;
}
.look-text {
  margin-top: 100;
  font-size: 50;
  color: #ffffff;
}
</style>

