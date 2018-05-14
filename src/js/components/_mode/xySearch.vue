<template>
  <div>
    <div class="wxc-search-bar">
      <input @blur="onBlur" @focus="onFocus" @input="onInput" @return="onSubmit" :change="onchange" v-model="value" ref="search-input" type="text" placeholder="输入查询内容" :class="['search-bar-input']" />
      <image class="search-bar-close" v-if="showClose" :aria-hidden="true" @click="closeClicked" src="bmlocal://assets/closeicon.png"></image>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      showClose:false,
      value: ""
    };
  },
  watch: {
    value(v) {
      this.showClose = v.length > 0
    }
  },
  methods: {
    onBlur() {
      const self = this;
      setTimeout(() => {
        // self.detectShowClose();
        self.$emit("wxcSearchbarInputOnBlur", { value: self.value });
      }, 10);
    },
    onFocus() {
      // this.detectShowClose();
    },
    onInput(){

    },
    onSubmit(e){

    },
    closeClicked() {
      this.value = "";
    },
    onchange(e) {
      // this.value = e.value;
      // this.$console(e);
    }
  }
};
</script>
<style scoped>
.wxc-search-bar {
  background-color: #ffffff;
  width: 260px;
  height: 60;
  flex-direction: row;
}
.search-bar-input {
  position: absolute;
  padding-top: 0;
  padding-left: 20;
  padding-right: 20;
  padding-bottom: 0;
  font-size: 26px;
  width: 240;
  height: 60px;
  line-height: 60px;
  background-color: #e5e5e5;
  border-radius: 29px;
  border-style: solid;
  border-width: 1;
  border-color: #dadada;
}
.search-bar-close {
  position: absolute;
  width: 30px;
  height: 30px;
  right: 30;
  top: 15;
}
</style>
