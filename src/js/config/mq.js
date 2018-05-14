import { getAsKeys, getTjMqInfo} from "./apis";
const utils = weex.requireModule("utils");
import Storage from "Config/storage";
const modal = weex.requireModule('bmModal')
const s = new Storage();

class MQ{
  static helper = null;
  constructor(){
    // this.mqInit(mq);
    return this;
  }

  mqInit(mq) {
    this.helper = mq;
    let MQHelper = s.getDataJsonSync("MQHelper");
    if (!MQHelper.tjmqUser) {
      modal.showLoading({message: '加载中...'});
      getAsKeys().then(res => {
        if (res.resultStatus) {
          getTjMqInfo().then(r => {
            modal.hideLoading()
            if (r.resultStatus) {
              this.helper.init(r.data.tjmqInfo, res.data.keyOne, res.data.keyTwo, result => {
                s.setDataSync("MQHelper", result);
                this.mqConnect(result);
              });
            }
          }).catch(err => {
            modal.hideLoading()
          });
        }
      }).catch(err => {
        modal.hideLoading()
      });
      return
    }
    this.mqConnect(MQHelper);
  }

  mqConnect(val){
    let mac = s.getDataSync("deviceSerial");
    this.helper.MqConnect(val,mac);
  }
}

export default MQ