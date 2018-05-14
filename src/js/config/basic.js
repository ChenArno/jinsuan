import Storage from "Config/storage";
import {
    getActiveRescSerList,
    getOrgGroupList,
    getAsKeys,
    getTjMqInfo
} from "./apis";
const utils = weex.requireModule("utils");
const modal = weex.requireModule('bmModal')
const s = new Storage();

class Basic {
    static mqHelper = null;
    constructor(mq) {
        this.mqHelper = mq;
        this.init();
        return this;
    }

    init() {
        this.rescSerInit();
        this.deviceInit();
    }

    rescSerInit() {
        let RescSer = s.getDataJsonSync("RescSer");
        if (!RescSer.id) {
            getActiveRescSerList().then(res => {
                if (res.data && res.data.length > 0) {
                    let [f] = res.data;
                    s.setDataSync("RescSer", f);
                    return
                }
                modal.toast({
                    message: '服务器oss地址不存在' // 展示内容
                })
            })
        }
    }

    deviceInit() {
        let deviceSerial = s.getDataSync("deviceSerial");
        if (!deviceSerial) {
            utils.getMac(res => {
                if (!res || res == "null" || res == "please open wifi") {
                    modal.toast({
                        message: "please open wifi"
                    })
                    return;
                }
                let deviceSerial = res
                    .replace(/:/g, '')
                    .toUpperCase();
                s.setDataSync("deviceSerial", deviceSerial);
                this.groupInit(deviceSerial);
            })
            return
        }
        this.groupInit(deviceSerial);
    }

    groupInit(deviceSerial) {
        this.mqInit(deviceSerial);
        let groupUuid = s.getDataJsonSync("groupUuid");
        if (!groupUuid.id) {
            let data = {
                deviceSerial,
                deviceType: "guide_tablet",
                isActive: 1
            };
            getOrgGroupList(data).then(res => {
                if (res.data && res.data.length > 0) {
                    let [f] = res.data;
                    s.setDataSync("groupUuid", f);
                    return
                }
                modal.toast({
                    message: '样本组不存在，请进入样本采集'
                })
            })
        }
    }
    mqInit(mac) {
        let MQHelper = s.getDataJsonSync("MQHelper");
        if (!MQHelper.tjmqUser) {
            modal.showLoading({
                message: '加载中...'
            });
            getAsKeys().then(res => {
                if (res.resultStatus) {
                    getTjMqInfo().then(r => {
                        modal.hideLoading()
                        if (r.resultStatus) {
                            this.mqHelper.init(r.data.tjmqInfo, res.data.keyOne, res.data.keyTwo, result => {
                                s.setDataSync("MQHelper", result);
                                this.mqConnect(result, mac);
                            }, err => {
                                utils.toast(err);
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
        this.mqConnect(MQHelper, mac);
    }

    mqConnect(val, mac) {
        this.mqHelper.MqConnect(val, mac);
    }
}

export default Basic
