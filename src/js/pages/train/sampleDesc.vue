<template>
  <div>
    <div class="right-head">
      <text class="right-desc">样本{{rightHead.isGroup?'组':''}}</text>
      <text class="iconfont" @click="updateLabels">&#xe61f;</text>
    </div>
    <div class="right-cell">
      <text class="cell-title">UUID</text>
      <text class="cell-val">{{uuid}}</text>
      <text class="cell-title">名称</text>
      <text class="cell-val">{{label}}</text>
      <!-- <text class="cell-title">时间</text>
      <text class="cell-val">{{time}}</text> -->
      <text :class="['cell-title',useType == 1?'cell-title-active':'']" v-if="uuid && !rightHead.isGroup">训练:{{rightHead.trainCount?rightHead.trainCount:0}}张</text>
      <text :class="['cell-title',useType == 2?'cell-title-active':'']" v-if="uuid && !rightHead.isGroup">| 测试:{{rightHead.testCount?rightHead.testCount:0}}张</text>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    rightHead: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      useType: 1
    };
  },
  computed: {
    uuid() {
      let { isGroup, groupUuid, labelUuid } = this.rightHead;
      return isGroup ? groupUuid : labelUuid;
    },
    label() {
      let { isGroup, groupName, labelName } = this.rightHead;
      return isGroup ? groupName : labelName;
    },
    time() {
      return "";
    }
  },
  methods: {
    updateLabels() {
      this.$emit("onclick", this.rightHead);
    },
    onclick() {
      this.useType = this.useType == 1 ? 2 : 1;
      this.$event.emit("useTypeAdd", this.useType);
    }
  }
};
</script>
<style scoped>
.right-head {
  flex-direction: row;
  align-items: center;
  height: 40;
}
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #a6a6a6;
  margin-left: 10;
}
.right-desc {
  color: #333333;
  font-size: 30;
}
.right-cell {
  margin-top: 20;
  flex-direction: row;
  height: 54;
  align-items: center;
}
.cell-title {
  font-size: 24;
  color: #666666;
  margin-right: 10;
}
.cell-title-active{
  color: #ee4035;
}
.cell-val {
  border-style: solid;
  border-color: #dadada;
  border-width: 1;
  font-size: 24;
  padding: 0 10;
  border-radius: 10;
  margin-right: 19;
  width: 250;
  height: 54;
  line-height: 54;
  lines: 1;
}
</style>
