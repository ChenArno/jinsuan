<template>
  <div class="server">
    <div class="set">
      <text class="set-head">【服务器地址】</text>
      <div class="set-cont">
        <text class="iconfont">&#xe611;</text>
        <input class="set-input" :maxlength="3" v-model="ipBreak1" type="number" />
        <text>.</text>
        <input class="set-input" :maxlength="3" v-model="ipBreak2" type="number" />
        <text>.</text>
        <input class="set-input" :maxlength="3" v-model="ipBreak3" type="number" />
        <text>.</text>
        <input class="set-input" :maxlength="3" v-model="ipBreak4" type="number" />
      </div>
      <text class="default">(默认地址:47.96.255.181)</text>
    </div>
    <div class="set">
      <text class="set-head">【端口】</text>
      <div class="set-cont">
        <text class="iconfont">&#xe624;</text>
        <input class="set-input set-port" v-model="port" type="number" />
      </div>
      <text class="default">(默认地址:80)</text>
    </div>
    <div class="set button">
      <xy-button title="保存" @onclick="enterTrain"></xy-button>
      <text class="default" @click="defaultClick">- 恢复默认 -</text>
      <text class="server-test" @click="serverTest"> 服务器测试 {{test}}</text>
    </div>
    <xy-loading :show="isShow" type="trip"></xy-loading>
  </div>
</template>
<script>
import Server from "Config/server";
import { testApp } from "Config/fetch";
import { xyButton, xyLoading } from "Components";
export default {
  components: { xyButton, xyLoading },
  data() {
    return {
      ip: "47.96.255.181",
      port: 80,
      ipBreak1: 47,
      ipBreak2: 96,
      ipBreak3: 255,
      ipBreak4: 181,
      test: "...",
      isShow: false
    };
  },
  created() {
    if (this.$getDataSync("baseUrl")) {
      let [ip, port] = this.$getDataSync("baseUrl").split(":");
      let ipBreak = ip.split(".");
      this.ipBreak1 = ipBreak[0];
      this.ipBreak2 = ipBreak[1];
      this.ipBreak3 = ipBreak[2];
      this.ipBreak4 = ipBreak[3];
      this.port = port;
    }
  },
  methods: {
    enterTrain() {
      if (
        !this.ipBreak1 ||
        !this.ipBreak2 ||
        !this.ipBreak3 ||
        !this.ipBreak4 ||
        !this.port
      ) {
        this.$notice.toast({
          message: "参数不能为空"
        });
        return;
      }
      let ip = `${this.ipBreak1}.${this.ipBreak2}.${this.ipBreak3}.${
        this.ipBreak4
      }:${this.port}`;
      this.$setDataSync("baseUrl", ip);
      this.$notice.toast({
        message: "服务器设置成功"
      });
    },
    defaultClick() {
      this.ip = Server.BASE_API;
      this.port = Server.BASE_PORT;
      this.$setDataSync("baseUrl", `${this.ip}:${this.port}`);
    },
    serverTest() {
      this.isShow = true;
      this.test = "...";
      testApp()
        .then(res => {
          this.isShow = false;
          this.test = "成功";
        })
        .catch(err => {
          this.isShow = false;
          this.test = "失败";
        });
    }
  }
};
</script>
<style scoped>
.server{
  width: 1430;
}
.iconfont {
  font-family: iconfont;
  font-size: 40;
  color: #808080;
  margin-right: 20;
}
.set {
  align-items: center;
}
.set-head {
  color: #ee4035;
  font-size: 40;
  font-weight: 200;
  margin-top: 50;
  margin-bottom: 50;
}
.set-cont {
  flex-direction: row;
  align-items: flex-end;
}
.set-input {
  width: 150;
  height: 60;
  border-bottom-color: #808080;
  border-bottom-style: solid;
  border-bottom-width: 1;
  text-align: center;
  font-size: 28;
}
.default {
  color: #a6a6a6;
  margin-top: 20;
}
.set-port {
  width: 640;
}
.button {
  margin-top: 30;
}
.server-test {
  color: #ee4035;
  margin-top: 20;
  font-size: 28;
}
</style>

