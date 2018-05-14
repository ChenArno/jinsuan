<template>
  <div class="container">
    <xy-overlay :show="show && hasOverlay" v-if="show" v-bind="mergeOverlayCfg" :can-auto-close="overlayCanClose" @wxcOverlayBodyClicking="wxcOverlayBodyClicking" @wxcOverlayBodyClicked="wxcOverlayBodyClicked"></xy-overlay>
    <div ref="wxc-mask" class="wxc-mask" v-if="show">
      <div class="wxc-mask-body">
        <image class="wxc-mask-cell" :src="InfoMsg.fullImgUrl" :style="maskStyle"></image>
        <xy-block :left="50" v-for="(item,i) in lists" :key="i" :param="item"></xy-block>
      </div>
      <image src="bmlocal://assets/close.png" class="mask-close-icon" @click="$emit('buttonClose')"></image>
    </div>
  </div>
</template>
<script>
import xyOverlay from "./xyOverlay";
import xyBlock from "./xyBlock";
export default {
  props: {
    show: {
      type: Boolean,
      default: false
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
    maskStyle: {
      type: Object,
      default: () => ({ width: 800, height: 600 })
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
    overlayCanClose: {
      type: Boolean,
      default: false
    },
    InfoMsg: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      opened: false,
      lists: []
    };
  },
  watch: {
    InfoMsg(val) {
      const { imgWidthValue, imgHightValue, labelSampleList } = val;
      const {width,height} = this.maskStyle;
      this.lists = labelSampleList.map(v => {
        return {
          id: v.labelUuid,
          top: v.yValue * height,
          left: v.xValue * width,
          width: v.widthValue * width,
          height: v.hightValue * height,
          labelList:[{skuName:v.labelName}]
        };
      });
      // this.maskStyle = {
      //   width: imgWidthValue,
      //   height: imgHightValue
      // };
    }
  },
  components: { xyOverlay, xyBlock },
  computed: {
    mergeOverlayCfg() {
      return {
        ...this.overlayCfg,
        hasAnimation: this.hasAnimation
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
    },
    needEmit(bool = false) {
      this.opened = bool;
      !bool && this.$emit("wxcMaskSetHidden", {});
    }
  }
};
</script>
<style scoped>
.container {
  position: fixed;
  width: 1920;
  justify-content: center;
  align-items: center;
}
.wxc-mask {
  position: fixed;
  left: 560;
  top: 250;
  width: 900;
  height: 800;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  /* background-color: fuchsia; */
}
.wxc-mask-body {
  width: 900;
  height: 700;
  align-items: center;
  padding-top: 50;
}
.mask-close-icon {
  width: 64px;
  height: 64px;
}
</style>
