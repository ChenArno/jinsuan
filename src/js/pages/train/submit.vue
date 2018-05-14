<template>
  <div class="sample-btn" :class="[type,state?'':'picter-active']" @click="subClick">
    <text class="sample-btn-cell" v-if="title">{{title}}</text>
    <text class="iconfont" v-else>&#xe620;</text>
  </div>
</template>
<script>
export default {
  props: ["type", "state","title"],
  methods: {
    subClick() {
      this.$emit("onclick");
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
.sample-btn {
  width: 100;
  height: 100;
  border-radius: 50;
  justify-content: center;
  align-items: center;
}
.picter {
  background-color: #999999;
}
.picter-active {
  background-color: #ee4035;
}
.picter-active:active {
  background-color: #cc2f26;
}
.cancel {
  background-color: #ee4035;
}
.cancel:active {
  background-color: #cc2f26;
}
.sample-btn-cell {
  color: #ffffff;
  font-size: 24;
}
.submit {
  background-color: #31bf9f;
}
.submit:active {
  background-color: #229e82;
}
</style>
