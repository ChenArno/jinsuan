<template>
  <div class="sample">
    <div class="train-prv-cell">
      <div class="train-prv">
        <image ref="box" :src="localUrl" class="preview" @touchstart="onbindstart"></image>
        <div class="block" :ref="'box'+i" :style="{width:item.w,height:item.h,top:item.t,left:item.l}" v-for="(item,i) in emitLists" :key="i" @touchstart="onBlockmoveBindx">
          <text class="iconfont" v-if="item.label" @click="deleTag(item,i)">&#xe613;</text>
          <text class="block-move"></text>
        </div>
        <div class="train-standard" :ref="'tag'+i" :style="{top:item.tagT,left:item.tagL,'align-items': (item.tagL<item.l)?'flex-end':'flex-start'}" v-for="(item,i) in emitLists" :key="i">
          <div class="train-point" v-if="item.label && (item.tagL>item.l)">
            <text class="train-point-good"></text>
          </div>
          <text class="train-point-cell" v-if="item.label">{{item.label}}</text>
          <div class="train-point" v-if="item.label && (item.tagL<item.l)">
            <text class="train-point-good"></text>
          </div>
        </div>
      </div>
      <div class="sample-cell" v-if="!maskShow">
        <xy-submit type="submit" :state="true" title="上传" @onclick="upload"></xy-submit>
        <xy-submit type="picter" :state="isDrawing" title="标注" @onclick="isDrawing = true"></xy-submit>
        <xy-submit type="cancel" :state="true" title="取消" @onclick="$emit('cancel')"></xy-submit>
      </div>
      <div class="tag-list" v-else>
        <list>
          <cell v-for="(t,i) in descLists" :key="i">
            <text class="train-desc-cell" @click="checkItem(t)">{{t.labelName}}</text>
          </cell>
        </list>
        <text class="train-desc-cell cancel" @click="trainCancel" v-if="descLists.length > 0">取消</text>
      </div>
    </div>
    <xy-dialog title="提示" content="是否确认删除" :show="dialogShow" :single="false" :show-no-prompt="false" @wxcDialogCancelBtnClicked="CancelBtnClicked" @wxcDialogConfirmBtnClicked="ConfirmBtnClicked">
    </xy-dialog>
    <xy-loading :show="loadShow" type="trip"></xy-loading>
  </div>
</template>

<script>
const utils = weex.requireModule("utils");
import { xyDialog, xyLoading } from "Components";
import xySubmit from "./submit";
import trainBackVue from "./trainBack.vue";
import { addSample } from "Config/apis";
export default {
  props: {
    descLists: {
      type: Array,
      default: []
    },
    localUrl: {
      type: String,
      default: ""
    },
    groupUuid: {
      type: String,
      default: ""
    },
    useType: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      baseBox: {
        width: 1400,
        height: 1050
      },
      loadShow: false,
      maskShow: false,
      isDrawing: false,
      emit: {
        w: 0,
        h: 0,
        t: 0,
        l: 0,
        x: 0, //移动距离
        y: 0, //移动距离
        tagT: 0,
        tagL: 0,
        label: "",
        labelUuid: ""
      },
      emitLists: [],
      newEmit: {},
      dialogShow: false,
      isInAnimation: false,
      i: -1,
      gesToken: 0
    };
  },
  components: { xySubmit, xyDialog, xyLoading },
  methods: {
    onbindstart(e) {
      if (!this.isDrawing) return;
      // 解绑动画
      if (this.gesToken != 0) {
        this.$bindingx.unbind({
          eventType: "pan",
          token: this.gesToken
        });
        this.gesToken = 0;
        // return
      }
      let [{ pageX, pageY }] = e.changedTouches;
      this.newEmit = JSON.parse(JSON.stringify(this.emit)); //防止双向绑定
      this.newEmit.t = pageY;
      this.newEmit.l = pageX;
      this.emitLists = [...this.emitLists, ...[this.newEmit]];
      const box = this.emitLists.length - 1;
      let boxRef = e.target.ref;
      this.$nextTick(() => {
        let headerBg = this.$refs["box" + box][0].ref;
        let gesTokenObj = this.$bindingx.bind(
          {
            anchor: boxRef,
            eventType: "pan",
            props: [
              {
                element: headerBg,
                property: "width",
                expression: "x+0"
              },
              {
                element: headerBg,
                property: "height",
                expression: "y+0"
              }
            ]
          },
          e => {
            let { state, deltaX, deltaY } = e;
            if (!this.isDrawing) return;
            if (state === "end") {
              const { width, height } = this.baseBox;
              this.newEmit.w = pageX + deltaX > width ? width - pageX : deltaX;
              this.newEmit.h =
                pageY + deltaY > height ? height - pageY : deltaY;
              this.isDrawing = false;
              this.maskShow = true;
            }
          }
        );
        this.gesToken = gesTokenObj.token;
      });
    },
    onBlockmoveBindx(e) {
      if (this.isInAnimation) return;
      // 解绑动画
      if (this.gesToken != 0) {
        this.$bindingx.unbind({
          eventType: "pan",
          token: this.gesToken
        });
        this.gesToken = 0;
      }
      let { width, height } = e.target.style;
      let [{ pageX, pageY }] = e.changedTouches;
      if (pageX > width - 40 && pageY > height - 40) {
        this.changSize(e);
        return;
      }
      this.moveBlock(e);
    },
    moveBlock(e) {
      let boxRef = e.target.ref;
      let tagRef = null;
      let [fo] = this.emitLists.filter((v, i) => {
        if (this.$refs["box" + i][0].ref !== boxRef) return false;
        tagRef = this.$refs["tag" + i][0].ref;
        return v;
      });
      const expression_x_origin = `x+${fo.x}`;
      const expression_y_origin = `y+${fo.y}`;
      let gesTokenObj = this.$bindingx.bind(
        {
          anchor: boxRef,
          eventType: "pan",
          props: [
            {
              element: boxRef,
              property: "transform.translateX",
              expression: expression_x_origin
            },
            {
              element: boxRef,
              property: "transform.translateY",
              expression: expression_y_origin
            },
            {
              element: tagRef,
              property: "transform.translateX",
              expression: expression_x_origin
            },
            {
              element: tagRef,
              property: "transform.translateY",
              expression: expression_y_origin
            }
          ]
        },
        event => {
          let { state, deltaX, deltaY } = event;
          if (state === "end") {
            const { width, height } = this.baseBox;
            fo.x += deltaX;
            fo.y += deltaY;
            if (deltaX < 0) {
              fo.x = fo.l + deltaX < 0 ? 0 : fo.x;
            } else {
              fo.x =
                fo.l + deltaX + fo.w > width
                  ? width - fo.w - fo.l - deltaX
                  : fo.x;
            }
            if (deltaY < 0) {
              fo.y = fo.t + deltaY < 0 ? 0 : fo.y;
            } else {
              fo.y =
                fo.t + deltaY + fo.h > height
                  ? height - fo.h - fo.t - deltaY
                  : fo.y;
            }
          }
        }
      );
      this.gesToken = gesTokenObj.token;
    },
    changSize(e) {
      let boxRef = e.target.ref;
      let tagRef = null;
      let [fo] = this.emitLists.filter((v, i) => {
        if (this.$refs["box" + i][0].ref !== boxRef) return false;
        tagRef = this.$refs["tag" + i][0].ref;
        return v;
      });
      const expression_x_origin = `x+${fo.w}`;
      const expression_y_origin = `y+${fo.h}`;
      let gesTokenObj = this.$bindingx.bind(
        {
          anchor: boxRef,
          eventType: "pan",
          props: [
            {
              element: boxRef,
              property: "width",
              expression: expression_x_origin
            },
            {
              element: boxRef,
              property: "height",
              expression: expression_y_origin
            }
          ]
        },
        event => {
          let { state, deltaX, deltaY } = event;
          if (state === "end") {
            const { width, height } = this.baseBox;
            fo.w += deltaX;
            fo.h += deltaY;
            fo.tagT += deltaY / 2;
            fo.tagL += deltaX / 2;
            // this.$console(fo.l + "_" + fo.w + "_" + deltaX);
            // this.$console(fo.l + fo.w + "_" + width);
            // fo.w = fo.l + fo.w > width ? width - fo.l : fo.w;
            // fo.h = fo.t + fo.h > height ? height - fo.t : fo.h;
            // fo.tagT = fo.l + fo.w > width?:fo.tagT;
            // fo.tagL = deltaX / 2;
          }
        }
      );
      this.gesToken = gesTokenObj.token;
    },
    trainCancel() {
      // this.descLists = [];
      this.emitLists.splice(this.emitLists.length - 1, 1);
      this.maskShow = false;
    },
    checkItem(val) {
      // this.descLists = [];
      this.maskShow = false;
      let { w, h, t, l } = this.newEmit; //宽度170
      let x = h / 2 + t - 10;
      let tagL = 0;
      if (w + l + 170 > this.baseBox.width) {
        tagL = l - 85;
      } else {
        tagL = w / 2 + l + 10;
      }
      this.newEmit.tagT = x;
      this.newEmit.tagL = tagL;
      this.newEmit.label = val.labelName;
      this.newEmit.labelUuid = val.labelUuid;
    },
    upload() {
      if (this.emitLists.length == 0) {
        this.$notice.toast({
          message: "请先圈出识别物品!" // 展示内容
        });
        return;
      }
      if (!this.localUrl) {
        this.$notice.toast({
          message: "请先拍照!" // 展示内容
        });
        return;
      }
      this.loadShow = true;
      utils.uploadImage(
        this.localUrl,
        this.groupUuid,
        res => {
          this.addSample(res);
        },
        err => {
          this.loadShow = false;
          this.$notice.toast({
            message: err
          });
        }
      );
    },
    addSample(imgUrl) {
      let { width, height } = this.baseBox;
      let labelSampleList = this.emitLists.map(v => {
        return {
          labelUuid: v.labelUuid,
          xValue: ((v.l + v.x) / width).toFixed(6), //l+x
          yValue: ((v.t + v.y) / height).toFixed(6), //t+y
          widthValue: (v.w / width).toFixed(6),
          hightValue: (v.h / height).toFixed(6)
        };
      });
      let data = {
        imgUrl,
        imgWidthValue: width,
        imgHightValue: height,
        useType: this.useType, //"int,使用类型: 1-训练；2-测试",
        labelSampleList
      };
      addSample(data)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            utils.deleteImage(this.localUrl);
            this.$emit("cancel");
            return;
          }
          this.$notice.toast({
            message: res.msg
          });
        })
        .catch(err => {
          this.loadShow = false;
        });
    },
    deleTag(item, i) {
      this.i = i;
      this.dialogShow = true;
    },
    ConfirmBtnClicked() {
      this.dialogShow = false;
      this.emitLists.splice(this.i, 1);
      this.i = -1;
    },
    CancelBtnClicked() {
      this.i = -1;
      this.dialogShow = false;
    },
    changeText() {
      this.maskShow = true;
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
  width: 1400;
  height: 1050;
}
.train-prv-cell {
  flex-direction: row;
}
.preview {
  width: 1400;
  height: 1050;
  border-radius: 20;
}
.sample {
  flex-direction: row;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #312c2c;
  justify-content: center;
  align-items: center;
}
.sample-cell {
  margin-left: 40;
  background-color: #49453e;
  width: 160;
  height: 1050;
  padding: 200 0;
  border-radius: 20;
  align-items: center;
  justify-content: space-around;
}

.tag-list {
  margin-left: 40;
  width: 160;
  height: 1050;
  background-color: #49453e;
  border-radius: 20;
}
.train-desc {
  width: 160;
}
.train-desc-cell {
  height: 90;
  line-height: 90;
  text-align: center;
  color: #ffffff;
  lines: 1;
  font-size: 21;
}
.train-desc-cell:active {
  background-color: #ee4035;
}
.cancel {
  background-color: #2f2c27;
  border-bottom-right-radius: 20;
  border-bottom-left-radius: 20;
}
.block {
  border-style: solid;
  border-color: #ee4035;
  border-width: 2;
  position: absolute;
  align-items: center;
  justify-content: center;
}
.train-standard {
  position: absolute;
  flex-direction: row;
}
.train-point {
  width: 20;
  height: 20;
  border-radius: 10;
  background-color: rgba(0, 0, 0, 0.6);
  justify-content: center;
  align-items: center;
  margin: 10;
}
.train-point-good {
  width: 10;
  height: 10;
  border-radius: 5;
  background-color: #ffffff;
}
.train-point-cell {
  background-color: rgba(0, 0, 0, 0.6);
  width: 130;
  height: 40;
  border-radius: 19;
  color: #ffffff;
  line-height: 40;
  text-align: center;
  font-size: 20;
}
.block-move {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 40;
  height: 40;
  background-color: #ee4035;
}
</style>
