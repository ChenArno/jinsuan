<template>
  <div class="button" @click="onclick" :class="[mini?'button-mini':'',disabled?'button-disabled':'button-normal']">
    <text class="button-cell">{{title}}</text>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      type: String,
      default: "确认"
    },
    mini:{
      type:Boolean
    },
    disabled:{
      type:Boolean,
      default:false
    }
  },
  methods:{
    onclick(){
      if(this.disabled) return;
      this.$emit("onclick");
    }
  }
};
</script>
<style scoped>
.button {
  width: 440;
  height: 70;
  border-radius: 30;
  justify-content: center;
  align-items: center;
}
.button-normal{
  background-color: #ee4035;
}
.button-mini{
  width: 125;
}
.button-cell {
  color: #ffffff;
  font-size: 24;
}
.button-normal:active {
  background-color: rgba(238, 64, 53, 0.6);
}
.button-disabled{
  background-color: #cccccc;
}
</style>

