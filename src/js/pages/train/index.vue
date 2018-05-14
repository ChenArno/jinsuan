<template>
  <div>

    <back-head :isnotAllow="true">

    </back-head>
    <div class="cont">
      <train-back></train-back>
    </div>
  </div>

</template>

<script>
import backHead from "../backHead";
import trainBack from "./trainBack";
export default {
  data() {
    return {};
  },
  beforeCreate() {
    const domModule = weex.requireModule("dom");
    domModule.addRule("fontFace", {
      fontFamily: "iconfont",
      src: "url('bmlocal://iconfont/xy.ttf')"
    });
  },
  components: { backHead, trainBack }
};
</script>

<style scoped>

</style>
