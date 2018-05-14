import Server from './server'
import md5 from "js-md5";
import {formatTime} from "Utils"
import Storage from "Config/storage";
const utils = weex.requireModule("utils");
const bmAxios = weex.requireModule('bmAxios')
const bmRouter = weex.requireModule('bmRouter')
const bmModal = weex.requireModule('bmModal')

let timeout = 1000 * 20;
const s = new Storage();
let baseUrl = s.getDataSync("baseUrl");

if (!baseUrl) {
  baseUrl = Server.BASE_API + ":" + Server.BASE_PORT;
  s.setDataSync("baseUrl", baseUrl);
}
const Appkey = () => {
  return s
    .getDataJsonSync('getAppInfo')
    .appKey
};
const AppSecret = () => {
  return s
    .getDataJsonSync('getAppInfo')
    .appSecret
};
const requestBaseUrl = () => {
  let url = `${Server
    .BASE_HTTP}${s
    .getDataSync("baseUrl")}${Server
    .MODULE_URL}`
  return url;
};
const testApp = () => {
  return new Promise((reslove, reject) => {
    const deviceSerial = s.getDataSync("deviceSerial");
    bmAxios.fetch({
      method: 'GET',
      url: requestBaseUrl() + `/org/register/deviceValidatorGetAppkey?deviceSerial=${deviceSerial}&deviceType=${Server.deviceType}`,
      header: {},
      data: {},
      timeout
    }, (resData) => {
      const {status, errorMsg, data} = resData;
      if (status !== 200) {
        bmModal.toast({message: '登录超时，请先检查网络 '}) 
        reject();
        return
      }
      const dataNew = data.data;
      if (!dataNew || !dataNew.appKey) {
        bmModal.toast({message: data.msg})
        reject();
        return
      }
      reslove(dataNew);
    })
  })
}
const getAppInfo = () => {
  return new Promise((reslove, reject) => {
    if (!s.getDataJsonSync('getAppInfo').appKey) {
      utils.getMac(res => {
        if (!res || res == "null" || res == "please open wifi") {
          bmModal.toast({message: "please open wifi"})
          reject();
          return;
        }
        let deviceSerial = res
          .replace(/:/g, '')
          .toUpperCase();
        s.setDataSync("deviceSerial", deviceSerial);
        testApp().then((dataNew)=>{
          s.setDataSync('getAppInfo', dataNew);
          reslove();
        }).catch(err=>{
          reject(err);
        });
      })
      return;
    }
    reslove()
  })
}

const responseHandler = function (resData, resolve, reject) {
  const {status, errorMsg, data} = resData;
  if (status !== 200) {
    bmModal.toast({message: '请求超时或者失败，请重新确认'}) // 展示内容
    reject(resData);
  } else {
    resolve(data);
  }
}

function methodIsGet(data) {
  if (!data) 
    return "";
  let request = "";
  for (let i in data) {
    request += `&${i}=${data[i]}`
  }
  request = request && "?" + request.substr(1);
  return request;
}

function methodIsNotGet(data) {
  return data.replace(/\s/g, '');
}
function getHeader(method, data) {
  let AppKey = Appkey();
  let CreateTime = formatTime(new Date());
  let requestTime = CreateTime.replace(/\s/g, '');
  let request = "";
  if (method === "GET") {
    request = methodIsGet(data) && methodIsGet(data).substr(1);
  } else {
    request = methodIsNotGet(data);
  }
  request = request || AppKey;
  let md5Var = `${request}&sign=${AppSecret()}&createTime=${requestTime}`;
  let ApiAuthorization = md5(md5Var).toUpperCase();
  return {AppKey, CreateTime, ApiAuthorization}
}

const ISGET = ({method, url, data}) => {
  return new Promise((resolve, reject) => {
    let requestUrl = `${requestBaseUrl()}${url}${methodIsGet(data)}`;
    let header = getHeader(method, data);
    bmAxios.fetch({
      method: 'GET',
      url: requestUrl,
      header,
      data: {},
      timeout
    }, (resData) => {
      responseHandler(resData, resolve, reject);
    })
  })
}

const ISNOTGET = ({method, url, data}) => {
  return new Promise((resolve, reject) => {
    let requestUrl = `${requestBaseUrl()}${url}`;
    utils.sortBy(data, succ => { //首字母排序
      data = JSON.parse(succ);
      let header = getHeader(method, succ);
      bmAxios.fetch({
        method,
        url: requestUrl,
        header,
        data,
        timeout
      }, (resData) => {
        responseHandler(resData, resolve, reject);
      })
    });
  })
}

const Service = function (options) {
  const {method, url, data} = options
  if (method === "GET") {
    return ISGET(options);
  }
  return ISNOTGET(options);
};

const upload = function (options) {
  let {url, maxCount, allowCrop, params} = options;
  return {
    url: s.getDataSync("baseUrl") + url,
    maxCount: maxCount || 1,
    allowCrop: allowCrop || true,
    params: {}, // 传递的参数
    header: _isLogin(url)
  }
}
export default Service

export {upload, getAppInfo,testApp}