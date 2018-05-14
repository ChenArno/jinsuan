<!-- CopyRight (C) 2017-2022 Alibaba Group Holding Limited. -->
<!-- Created by Tw93 on 16/10/25. -->
<!-- Updated by Tw93 on 17/01/06. -->
<!--A Mask.-->

<template>
  <div class="container">
    <div class="overlay"></div>
    <xy-overlay :show="show && hasOverlay" v-if="show" v-bind="mergeOverlayCfg" :can-auto-close="overlayCanClose" @wxcOverlayBodyClicking="wxcOverlayBodyClicking" @wxcOverlayBodyClicked="wxcOverlayBodyClicked"></xy-overlay>
    <div ref="wxc-mask" :style="maskStyle" class="wxc-mask" v-if="show" :hack="shouldShow">
      <div class="wxc-mask-cont" :style="contentStyle">
        <slot></slot>
      </div>
      <div class="mask-bottom" :style="{ width: width + 'px' }" @click="closeIconClicked" v-if="showClose">
        <image :src="closeIcon" aria-label="关闭" class="mask-close-icon"></image>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
}
.wxc-mask {
  position: fixed;
  width: 700;
  height: 500;
}
.mask-bottom {
  width: 100px;
  height: 100px;
  background-color: transparent;
  justify-content: center;
  align-items: center;
}
.mask-close-icon {
  width: 64px;
  height: 64px;
}
.wxc-mask-cont{
  align-items: center;
  justify-content: center;
}
</style>

<script>
const animation = weex.requireModule("animation");
import xyOverlay from "./xyOverlay.vue";
export default {
  components: { xyOverlay },
  props: {
    maskStyles: {
      type: Object,
      default: () => ({
        width: 100,
        height: 100,
        top: 0,
        left: 0
      })
    },
    height: {
      type: [String, Number],
      default: 800
    },
    width: {
      type: [String, Number],
      default: 702
    },
    show: {
      type: Boolean,
      default: false
    },
    showClose: {
      type: Boolean,
      default: false
    },
    duration: {
      type: [String, Number],
      default: 300
    },
    hasOverlay: {
      type: Boolean,
      default: true
    },
    hasAnimation: {
      type: Boolean,
      default: true
    },
    timingFunction: {
      type: Array,
      default: () => ["ease-in", "ease-out"]
    },
    overlayCfg: {
      type: Object,
      default: () => ({
        hasAnimation: true,
        timingFunction: ["ease-in", "ease-out"],
        canAutoClose: true,
        duration: 300,
        opacity: 0.6
      })
    },
    borderRadius: {
      type: [String, Number],
      default: 0
    },
    overlayCanClose: {
      type: Boolean,
      default: true
    },
    maskBgColor: {
      type: String,
      default: "#ffffff"
    }
  },
  data: () => ({
    closeIcon:
      "https://gw.alicdn.com/tfs/TB1qDJUpwMPMeJjy1XdXXasrXXa-64-64.png",
    maskTop: 264,
    opened: false
  }),
  computed: {
    mergeOverlayCfg() {
      return {
        ...this.overlayCfg,
        hasAnimation: this.hasAnimation
      };
    },
    maskStyle() {
      const { maskStyles, showClose, hasAnimation, opened } = this;
      const { width, height, left, top, right, bottom } = maskStyles;
      if (!right) {
        return {
          width: width + "px",
          height: height + "px",
          left: left + "px",
          top: top + "px",
          opacity: hasAnimation && !opened ? 0 : 1
        };
      }
      return {
        width: width + "px",
        height: height + "px",
        right: right + "px",
        top: top + "px",
        opacity: hasAnimation && !opened ? 0 : 1
      };
    },
    contentStyle() {
      return {
        width: this.maskStyles.width + "px",
        backgroundColor: this.maskBgColor,
        height: this.maskStyles.height + "px",
        borderRadius: this.borderRadius + "px"
      };
    },
    shouldShow() {
      const { show, hasAnimation } = this;
      hasAnimation &&
        setTimeout(() => {
          this.appearMask(show);
        }, 50);
      return show;
    }
  },
  methods: {
    closeIconClicked() {
      this.appearMask(false);
    },
    wxcOverlayBodyClicking() {
      if (this.hasAnimation) {
        this.appearMask(false);
        this.$emit("wxcOverlayBodyClicking", {});
      }
    },
    wxcOverlayBodyClicked() {
      if (!this.hasAnimation) {
        this.appearMask(false);
        this.$emit("wxcOverlayBodyClicked", {});
      }
    },
    needEmit(bool = false) {
      this.opened = bool;
      !bool && this.$emit("wxcMaskSetHidden", {});
    },
    appearMask(bool, duration = this.duration) {
      const { hasAnimation, timingFunction } = this;
      const maskEl = this.$refs["wxc-mask"];
      if (hasAnimation && maskEl) {
        animation.transition(
          maskEl,
          {
            styles: {
              opacity: bool ? 1 : 0
            },
            duration,
            timingFunction: timingFunction[bool ? 0 : 1],
            delay: 0
          },
          () => {
            this.needEmit(bool);
          }
        );
      } else {
        this.needEmit(bool);
      }
    }
  }
};
</script>