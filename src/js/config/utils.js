const utils = weex.requireModule("utils");

class Utils {
  constructor() {
    return this
  }

  install(Vue) {
    Vue.prototype.$console = (res) => {
      let str = typeof res === "string"
        ? res
        : JSON.stringify(res);
      utils.console(str);
    }
  }
}

export default Utils