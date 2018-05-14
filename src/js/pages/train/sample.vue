<template>
  <div class="sample">
    <div class="train-prv">
      <imgTailor :useType="useType" :localUrl="localUrl" :groupUuid="groupUuid" :descLists="descLists" @uploadSucc="uploadSucc" v-if="isPic" @cancel="cancel"></imgTailor>
    </div>
    <div class="sample-cell" ref="sample">
      <xy-submit type="picter" :state="false" title="切换" @onclick="changCamera"></xy-submit>
      <xy-submit type="picter" :state="false" :title="useTypeName" @onclick="changUseType"></xy-submit>
      <xy-submit type="picter" :state="false" @onclick="picClick"></xy-submit>
    </div>
  </div>
</template>

<script>
const utils = weex.requireModule("utils");
import xySubmit from "./submit";
import imgTailor from "./imgTailor";
import { getGroupLabelList } from "Config/apis";
export default {
  props: {
    currentLabel: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      isPic: false,
      localUrl: "",
      descLists: [],
      isCameraL: true,
      groupUuid: "",
      useType: 1 //"int,使用类型: 1-训练；2-测试",
    };
  },
  components: { xySubmit, imgTailor },
  watch: {
    isPic(val) {
      this.$emit("onsubmit", !val, this.isCameraL);
      if (!val) {
        this.emitLists = [];
        this.isDrawing = false;
      }
    },
    currentLabel(val) {
      this.init(val);
    }
  },
  created() {
    this.init(this.currentLabel);
  },
  computed: {
    useTypeName() {
      return this.useType == 1 ? "训练" : "测试";
    }
  },
  methods: {
    init(val) {
      let { groupUuid } = val;
      if (!groupUuid) return;
      this.groupUuid = groupUuid;
      this.isPic = false;
      this.getGroupLabelList();
    },
    picClick() {
      if (this.descLists.length == 0) {
        this.$notice.toast({
          message: "请先增加标签"
        });
        return;
      }
      if (this.isPic) return;

      // this.localUrl =
      //   "/storage/emulated/0/Android/data/com.tianji.jingsuan/cache/documents/R1524551028438.jpg";
      // this.isPic = true;
      // return;
      utils.takePicter(this.isCameraL, res => {
        if (!res) return;
        this.localUrl = res;
        this.isPic = true;
        this.$notice.toast({
          message: "截屏成功"
        });
      });
    },
    getGroupLabelList() {
      let data = {
        groupUuid: this.groupUuid,
        isActive: 1
      };
      getGroupLabelList(data)
        .then(res => {
          if (res.resultStatus) {
            this.descLists = res.data;
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.$notice.toast({
            message: err
          });
        });
    },
    changCamera() {
      utils.setShowOrHide(this.isCameraL);
      this.isCameraL = !this.isCameraL;
    },
    changUseType() {
      if (this.useType == 1) {
        this.useType = 2;
      } else {
        this.useType = 1;
      }
    },
    cancel() {
      this.isPic = false;
      this.$emit("cancel");
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
.train-prv {
  width: 960;
  height: 720;
}
.sample {
  padding-top: 91;
  flex-direction: row;
  position: absolute;
  left: 0;
  bottom: 0;
}
.sample-cell {
  margin-left: 60;
  background-color: #49453e;
  width: 160;
  height: 720;
  padding: 40 0;
  border-radius: 20;
  align-items: center;
  justify-content: space-around;
}
</style>
