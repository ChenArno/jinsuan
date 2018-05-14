<template>
  <div class="body">
    <div class="body-cell">
      <div class="cont-left">
        <div class="search">
          <!-- <xy-searchbar :showCancel="true" @wxcSearchbarCloseClicked="CloseClicked" @wxcSearchbarInputOnInput="onInput"></xy-searchbar>
          <xy-button mini title="查询" @onclick="onsearch"></xy-button> -->
          <text @click="sortBy">样本列表</text>
        </div>
        <div class="left">
          <scroller>
            <div class="sample" :class="[currentLabel.groupUuid == item.groupUuid?'sample-active':'']" v-for="item in sampleLists" :key="item.id">
              <div class="sample-more">
                <text class="iconfont" @click="openLabel(item)">&#xe650;</text>
              </div>
              <!-- <text class="iconfont" @click="updateLabels(item)">&#xe61f;</text> -->
              <text class="sample-desc" @click="changeLabel(item)">{{item.groupName.length>8?item.groupName+"...":item.groupName}}</text>
              <text class="sample-num" @click="changeLabel(item)">{{item.labelCount?item.labelCount:0}}</text>
            </div>
            <div class="sample sample-add" @click="addSample">
              <text class="iconfont" @click="addSample">&#xe655;</text>
            </div>
          </scroller>
          <div class="left-tag" ref="tag">
            <div class="tag-left" @click="closeTag"></div>
            <scroller class="tag-right">
              <div class="sample tag" :class="[rightHead.labelUuid == item.labelUuid?'sample-active':'']" v-for="(item,i) in tagLists" :key="i">
                <!-- <text class="iconfont" @click="updateTagClick(item)">&#xe61f;</text> -->
                <text class="sample-desc tag-desc" @click="openTagClick(item)">{{item.labelName}}</text>
                <text class="sample-num">{{item.trainCount?item.trainCount:0}}-{{item.testCount?item.testCount:0}}</text>
              </div>
              <div class="sample sample-add tag" @click="addTagClick">
                <text class="iconfont" @click="addTagClick">&#xe655;</text>
              </div>
            </scroller>
          </div>
        </div>
      </div>
      <div class="cont-right">
        <sample-desc :rightHead="rightHead" @onclick="updateGroupOrTag"></sample-desc>
        <Preview-view ref="preview" class="preview" :style="{width:preview.width,height:preview.height}"></Preview-view>
        <sample :currentLabel="currentLabel" @onsubmit="onsubmit" v-if="rightLabelOrtag" @uploadSucc="uploadSucc" @cancle="cancle"></sample>
        <tag :rightHead="rightHead" v-else></tag>
      </div>
    </div>
    <xy-mask :mask-styles="maskStyles" :border-radius="10" duration="200" :has-overlay="true" :overlay-can-close="false" :show="maskShow" mask-bg-color="transparent">
      <div class="input-box">
        <div class="box-del">
          <text class="iconfont" @click="maskShow = false">&#xe60c;</text>
        </div>
        <xy-input title="标签组" v-model="form.groupName" placeholder="名称"></xy-input>
        <xy-input title="水平值" type="number" v-model="form.confidenceLevelValue" placeholder="范围[0.00-100.00]"></xy-input>
        <xy-button :title="addOrUpdate?'添加':'修改'" @onclick="addOrUpdateClick"></xy-button>
      </div>
    </xy-mask>
    <xy-loading :show="loadShow" type="trip"></xy-loading>
  </div>
</template>

<script>
const animation = weex.requireModule("animation");
const utils = weex.requireModule("utils");
import { WxcLoading } from "weex-ui";
import sample from "./sample";
import tag from "./tag";
import sampleDesc from "./sampleDesc";
import { xyMask, xyButton, xyInput, xySearchbar, xyLoading } from "Components";
import {
  getOrgGroupList,
  queryGroup,
  addGroup,
  updateGroup,
  getGroupLabelList,
  addLabel,
  getLabel,
  updateLabel
} from "Config/apis";
export default {
  data() {
    return {
      preview: { width: 960, height: 720 },
      deviceSerial: "",
      sampleLists: [],
      tagLists: [],
      maskShow: false,
      maskStyles: {
        width: 1200,
        height: 800,
        top: 200, //1200/2-400
        left: 360 //1920/2-600
      },
      form: {
        groupName: "",
        groupCode: "",
        confidenceLevelValue: 0.2
      },
      rightHead: {},
      loadShow: false,
      currentLabel: {},
      addOrUpdate: true,
      maskLabelOrTag: true,
      rightLabelOrtag: true
    };
  },
  components: {
    sample,
    tag,
    sampleDesc,
    xySearchbar,
    xyButton,
    xyMask,
    xyInput,
    xyLoading
  },
  created() {
    this.deviceSerial = this.$getDataSync("deviceSerial");
    if (!this.deviceSerial) {
      this.$notice.toast({
        message: "请先设置设备序列号"
      });
      return;
    }
    this.getOrgGroupList();
  },
  // mounted() {
  //   setTimeout(()=>{
  //     this.$refs.preview.startView();
  //   },1000)
  // },
  watch: {
    rightLabelOrtag(val) {
      //true显示
      this.onsubmit(val, true);
    },
    currentLabel(val) {
      this.rightHead = val;
      this.rightHead.isGroup = true;
    }
  },
  methods: {
    openLabel(val) {
      this.currentLabel = val;
      let { groupUuid } = val;
      this.getGroupLabelList(groupUuid);
    },
    updateGroupOrTag(val) {
      // this.$console(val);
      const { isGroup } = val;
      if (isGroup) {
        this.updateLabels(val);
        return;
      }
      this.updateTagClick(val);
    },
    updateLabels({ groupName, confidenceLevelValue, groupCode, groupUuid }) {
      queryGroup(groupUuid)
        .then(res => {
          if (res.resultStatus) {
            let {
              groupName,
              groupCode,
              confidenceLevelValue,
              groupUuid,
              sortNum
            } = res.data;
            this.form = {
              groupName,
              groupCode,
              confidenceLevelValue,
              groupUuid,
              sortNum
            };
            this.maskShow = true;
            this.addOrUpdate = false;
            this.maskLabelOrTag = true;
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
    changeLabel(val) {
      this.rightLabelOrtag = true;
      this.currentLabel = val;
      this.rightHead = val;
      this.rightHead.isGroup = true;
    },
    addSample() {
      this.maskLabelOrTag = true;
      this.maskShow = true;
      this.addOrUpdate = true;
      this.form = {
        groupName: "",
        groupCode: "",
        confidenceLevelValue: 0.2
      };
    },
    closeTag() {
      this.tagStyle(true);
    },
    tagStyle(val) {
      animation.transition(this.$refs.tag, {
        styles: {
          transform: val ? "translateX(330px)" : "translateX(-330px)"
        },
        duration: 300
      });
    },
    onsubmit(val, lr) {
      this.$refs.preview.setShowOrHide(val, lr); //true显示
    },
    onsearch() {},
    onInput(e) {
      this.$console(e + "0000");
    },
    CloseClicked() {},
    getOrgGroupList() {
      let data = {
        deviceSerial: this.deviceSerial,
        deviceType: "guide_tablet",
        isActive: 1
      };
      this.loadShow = true;
      getOrgGroupList(data)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            this.sampleLists = res.data;
            let [fo] = res.data;
            this.currentLabel = fo;
          }
        })
        .catch(err => {
          this.loadShow = false;
        });
    },
    getGroupLabelList(val) {
      let data = {
        groupUuid: val,
        isActive: 1
      };
      getGroupLabelList(data)
        .then(res => {
          if (res.resultStatus) {
            this.tagLists = res.data;
            if (res.data.length > 0) {
              let [fo] = res.data;
              this.openTagClick(fo);
            }
            this.tagStyle(false);
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
    boxGroup() {
      let {
        groupCode,
        groupName,
        confidenceLevelValue,
        groupUuid,
        sortNum
      } = this.form;
      let decimal = confidenceLevelValue
        ? parseFloat(confidenceLevelValue)
        : 0.2;
      if (groupName.length < 1 || decimal < 0 || decimal > 100) {
        this.$notice.toast({
          message: "请输入正确的标签组名或者范围值"
        });
        return;
      }
      decimal = decimal > 0 ? decimal.toFixed(2) : 0.2;

      let data = {
        groupName,
        groupCode,
        serverId: this.$getDataJsonSync("RescSer").id,
        confidenceLevelValue: decimal
      };
      if (this.addOrUpdate) {
        data.sortNum = this.sampleLists.length;
        this.addGroup(data);
        return;
      }
      data.groupUuid = groupUuid;
      data.sortNum = sortNum;
      this.updateGroup(data);
    },
    boxTag() {
      let {
        groupCode,
        groupName,
        confidenceLevelValue,
        labelUuid,
        sortNum
      } = this.form;
      let decimal = confidenceLevelValue
        ? parseFloat(confidenceLevelValue)
        : 0.2;
      if (groupName.length < 1 || decimal < 0 || decimal > 100) {
        this.$notice.toast({
          message: "请输入正确的标签名或者范围值"
        });
        return;
      }
      decimal = decimal > 0 ? decimal.toFixed(2) : 0.2;
      let { groupUuid } = this.currentLabel;
      let data = {
        labelName: groupName,
        labelCode: groupCode,
        confidenceLevelValue: decimal,
        labelGroupList: [
          {
            groupUuid
          }
        ]
      };
      if (this.addOrUpdate) {
        data.sortNum = this.tagLists.length;
        this.addLabel(data);
        return;
      }
      data.labelUuid = labelUuid;
      data.sortNum = sortNum;
      this.updateLabel(data);
    },
    addOrUpdateClick() {
      if (this.maskLabelOrTag) {
        this.boxGroup();
        return;
      }
      this.boxTag();
    },
    addGroup(data) {
      this.loadShow = true;
      addGroup(data)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            this.maskShow = false;
            this.getOrgGroupList();
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
    updateGroup(data) {
      this.loadShow = true;
      updateGroup(data)
        .then(res => {
          this.loadShow = false;
          if (res.resultStatus) {
            this.maskShow = false;
            this.getOrgGroupList();
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
    addTagClick() {
      this.maskShow = true;
      this.addOrUpdate = true;
      this.maskLabelOrTag = false;
      this.form = {
        groupName: "",
        groupCode: "",
        confidenceLevelValue: 0.2
      };
    },
    updateTagClick({ labelUuid }) {
      getLabel(labelUuid)
        .then(res => {
          if (res.resultStatus) {
            let {
              confidenceLevelValue,
              labelCode,
              labelName,
              sortNum,
              labelUuid
            } = res.data;
            this.form = {
              groupName: labelName,
              groupCode: labelCode,
              sortNum,
              labelUuid,
              confidenceLevelValue
            };
            this.maskLabelOrTag = false;
            this.maskShow = true;
            this.addOrUpdate = false;
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
    openTagClick(val) {
      this.rightLabelOrtag = false;
      this.rightHead = val;
      this.rightHead.isGroup = false;
      // this.currentLabel = {};
    },
    addLabel(data) {
      addLabel(data)
        .then(res => {
          if (res.resultStatus) {
            let { groupUuid } = this.currentLabel;
            this.getGroupLabelList(groupUuid);
            this.maskShow = false;
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
    updateLabel(data) {
      updateLabel(data)
        .then(res => {
          if (res.resultStatus) {
            let { groupUuid } = this.currentLabel;
            this.getGroupLabelList(groupUuid);
            this.maskShow = false;
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
    cancle() {
      this.getOrgGroupList();
    }
  }
};
</script>
<style scoped>
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #a6a6a6;
}
.iconfont:active {
  color: #808080;
}
.body {
  width: 1920;
  height: 1080;
  background-color: rgb(251, 183, 53);
  /* flex-direction: row; */
  align-items: center;
  /* padding-top: 86; */
  justify-content: center;
}
.body-cell {
  width: 1802;
  height: 1000;
  background-color: #ffffff;
  border-top-left-radius: 160;
  border-top-right-radius: 40;
  border-bottom-left-radius: 40;
  border-bottom-right-radius: 160;
  flex-direction: row;
  padding: 54 92 0 96;
}
.cont-left {
  width: 400;
}
.search {
  height: 62;
  margin-bottom: 38;
  flex-direction: row;
  align-items: center;
}
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
  height: 110;
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
  font-size: 28;
  height: 110;
  line-height: 110;
  lines: 1;
  max-width: 180;
  width: 170;
}
.sample-more {
  height: 110;
  width: 100;
  justify-content: center;
  align-items: center;
}
.tag-desc {
  width: 160;
}
.sample-num {
  color: #666666;
  font-size: 24;
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
  width: 100;
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
  width: 230;
}
.tag:active {
  background-color: #fff7e9;
}
.cont-right {
  width: 1200;
  height: 880;
}
.preview {
  position: absolute;
  bottom: 0;
  left: 0;
  background-color: transparent;
  border-radius: 20;
  background-color: #000000;
}
.input-box {
  width: 600;
  padding: 80 20;
  align-items: center;
  background-color: #ffffff;
  border-radius: 10;
}

.box-del {
  position: absolute;
  right: 20;
  top: 20;
  width: 50;
  height: 50;
}
.box-del-cell {
  color: #ee4035;
}
.sample-active {
  background-color: #fff7e9;
}
</style>
