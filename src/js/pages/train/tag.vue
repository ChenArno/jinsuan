<template>
  <div class="tag-cont">
    <scroller>
      <div class="tag-list">
        <div class="tag-cell" v-for="(item,i) in lists" :key="i">
          <image class="tag-img" :src="item.fullImgUrl"></image>
          <text class="iconfont amplification" @click="amplification(item)">&#xe60b;</text>
          <text class="iconfont deleTag" @click="deleTag(item,i)">&#xe613;</text>
        </div>
      </div>
    </scroller>
    <xy-image :show="imgShow" :InfoMsg="InfoMsg" @buttonClose="buttonClose"></xy-image>
    <xy-dialog title="提示" content="是否确认删除" :show="dialogShow" :single="false" :show-no-prompt="false" @wxcDialogCancelBtnClicked="wxcDialogCancelBtnClicked" @wxcDialogConfirmBtnClicked="wxcDialogConfirmBtnClicked">
    </xy-dialog>
    <xy-loading :show="loadShow" type="trip"></xy-loading>
  </div>
</template>

<script>
import { xyDialog, xyLoading, xyImage } from "Components";
import { getSampleList, deleteSampleImage } from "Config/apis";
export default {
  props: {
    rightHead: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      loadShow: false,
      dialogShow: false,
      id: "",
      lists: [],
      i: -1,
      imgId: "",
      InfoMsg: {},
      imgShow: false,
      useType:1
    };
  },
  components: {
    xyDialog,
    xyLoading,
    xyImage
  },
  watch: {
    rightHead(val) {
      let { id } = val;
      if (!id) {
        this.lists = [];
        return;
      }
      this.id = id;
    },
    id(val) {
      this.getSampleList(val);
    }
  },
  created() {
    this.addeventUseType();
    let { id } = this.rightHead;
    if (!id) return;
    this.id = id;
  },
  methods: {
    addeventUseType(){
      this.$event.on('useTypeAdd', params => {
        this.$console(params);
        this.useType = params;
      })
    },
    getSampleList(id) {
      this.loadShow = true;
      this.lists = [];
      getSampleList(id,this.useType)
        .then(({ resultStatus, msg, data }) => {
          this.loadShow = false;
          if (resultStatus) {
            this.lists = data;
            return;
          }
          this.$notice.toast({
            message: msg
          });
        })
        .catch(err => {
          this.loadShow = false;
          this.$notice.toast({
            message: err
          });
        });
    },
    amplification(val) {
      this.imgShow = true;
      this.InfoMsg = val;
    },
    deleTag(val, i) {
      this.imgId = val.id;
      this.i = i;
      this.dialogShow = true;
    },
    wxcDialogCancelBtnClicked() {
      this.i = -1;
      this.dialogShow = false;
      this.imgId = "";
    },
    wxcDialogConfirmBtnClicked() {
      this.loadShow = true;
      deleteSampleImage(this.imgId)
        .then(({ resultStatus, msg }) => {
          this.loadShow = false;
          if (resultStatus) {
            this.dialogShow = false;
            this.lists.splice(this.i, 1);
            this.i = -1;
            this.imgId = "";
            return;
          }
          this.$notice.toast({
            message: msg
          });
        })
        .catch(err => {
          this.loadShow = false;
        });
    },
    buttonClose() {
      this.imgShow = false;
    }
  }
};
</script>
<style scoped>
.tag-cont {
  margin-top: 33;
  width: 1000;
  height: 800;
  background-color: #ffffff;
}
.tag-list {
  flex-direction: row;
  flex-wrap: wrap;
}
.tag-cell {
  padding: 0 20 20 0;
}
.tag-img {
  width: 220;
  height: 220;
  border-radius: 10;
}
.iconfont {
  font-family: iconfont;
  font-size: 52;
  color: #ffffff;
  position: absolute;
  left: 84;
}
.amplification {
  top: 34;
  font-size: 68;
  left: 80;
}
.deleTag {
  top: 134;
}
</style>
