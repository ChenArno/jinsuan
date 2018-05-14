<template>
  <div class="left">
    <scroller>
      <div class="sample" v-for="item in sampleLists" :key="item.id">
        <text class="iconfont" @click="updateLabel(item)">&#xe61f;</text>
        <text class="sample-desc" @click="openLabel(item)">{{item.groupName.length>8?item.groupName+"...":item.groupName}}</text>
        <text class="iconfont" @click="changeLabel(item)">&#xe650;</text>
      </div>
      <div class="sample sample-add" @click="addSample">
        <text class="iconfont" @click="addSample">&#xe655;</text>
      </div>
    </scroller>
    <div class="left-tag" ref="tag">
      <div class="tag-left" @click="closeTag"></div>
      <scroller class="tag-right">
        <div class="sample tag" v-for="(item,i) in tagLists" :key="i">
          <text class="iconfont" @click="updateTag(item)">&#xe61f;</text>
          <text class="sample-desc" @click="openTag(item)">{{item.labelName}}</text>
        </div>
        <div class="sample sample-add tag" @click="addTag">
          <text class="iconfont" @click="addTag">&#xe655;</text>
        </div>
      </scroller>
    </div>
  </div>
</template>
<script>
export default {
  data(){
    
  }
};
</script>
<style scoped>
.left {
  border-radius: 10;
  border-style: solid;
  border-color: #e6e6e6;
  border-width: 1;
  width: 332;
  height: 600;
}
.sample {
  width: 330;
  height: 80;
  border-bottom-style: solid;
  border-bottom-color: #e6e6e6;
  border-bottom-width: 1;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  padding: 0 20;
}
.sample-desc {
  color: #666666;
  font-size: 24;
  height: 80;
  line-height: 80;
  lines: 1;
  max-width: 180;
}
.sample-desc:active {
  color: #333333;
  background-color: rgba(255, 255, 255, 0.8);
}
.sample-add {
  justify-content: center;
  /* border-bottom-width: 0; */
}
.left-tag {
  position: absolute;
  bottom: 0;
  width: 330;
  height: 600;
  right: -330;
  border-style: solid;
  border-color: #e6e6e6;
  border-width: 1;
  flex-direction: row;
}
.tag-left {
  width: 130;
  height: 535;
  background-color: rgba(255, 255, 255, 0.7);
}
.tag-right {
  background-color: #ffffff;
  border-left-style: solid;
  border-left-color: #e6e6e6;
  border-left-width: 1;
}
.tag {
  width: 200;
}
.tag:active {
  background-color: #fff7e9;
}
</style>
