<template>
  <div class="xy-block" :style="{top:param.top,left:param.left+left}">
    <div :class="['head',param.labelList.length == 1?'head-active':'']" :style="{width:param.width}">
      <div class="head-cell" v-for="(item,i) in param.labelList" :key="i" @click="change(item)">
        <div v-for="(x,j) in skuList" :key="j" v-if="x.labelUuid == param.labelUuid">
          <text class="iconfont" v-if="item.skuNum && param.labelList.length > 1">{{firstChoose && item.skuNum == x.skuNum?'&#xe661;':'&#xe64a;'}}</text>
        </div>
        <text class="head-title">{{item.skuName}}</text>
      </div>
    </div>
    <div :class="['body',param.labelList.length == 1?'body-active':'']" :style="{width:param.width,height:param.height}">

    </div>
  </div>
</template>
<script>
export default {
  props: {
    param: {
      type: Object,
      default: {}
    },
    listLabelSkuComb: {
      type: Array,
      default: []
    },
    left: {
      type: Number,
      default: 120
    }
  },
  computed: {
    skuList() {
      if (this.listLabelSkuComb.length == 0) return [];
      let arr = this.listLabelSkuComb[this.index];
      return arr;
    }
  },
  data() {
    return {
      firstChoose: false,
      index: 0
    };
  },
  methods: {
    change(val) {
      let z = 0;
      this.listLabelSkuComb.map((x, i) => {
        x.map(y => {
          if (y.skuNum == val.skuNum) {
            if (z != 0) return;
            z = i;
            this.index = i;
            this.firstChoose = true;
            this.$emit("confirm", this.listLabelSkuComb[i]);
            // this.$console(this.listLabelSkuComb);
            // this.$console(y);
          }
        });
      });
    }
  }
};
</script>
<style scoped>
.iconfont {
  font-family: iconfont;
  font-size: 34;
  color: #fff;
}
.xy-block {
  position: absolute;
}
.head {
  background-color: #ee4035;
  border-top-left-radius: 10;
  border-top-right-radius: 10;
  opacity: 0.6;
}
.head-active {
  background-color: #fbb735;
}
.head-cell {
  height: 50;
  flex-direction: row;
  padding-left: 15;
  align-items: center;
}
/* .head-cell-cell {
  flex-direction: row;
  align-items: center;
} */
.head-title {
  color: #ffffff;
  padding-left: 10;
  font-size: 23;
}
.body {
  border-style: solid;
  border-color: #ee4035;
  border-width: 2;
}
.body-active {
  border-color: #fbb735;
}
</style>
