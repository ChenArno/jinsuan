import fetch from 'Config/fetch'
/*
*标签组
**/
//GDAI-AppKey组织标签组列表查询
export function getOrgGroupList(data) {
    return fetch({url: `/gdai/appkey/labelGroup/getOrgGroupList`, method: 'GET', data})
}

//GDAI-AppKey标签组下属标签列表查询
export function getGroupLabelList(data) {
    return fetch({url: `/gdai/appkey/labelGroup/getGroupLabelList`, method: 'GET', data})
}

//GDAI-AppKey查询标签组
export function queryGroup(groupUuid) {
    return fetch({url: `/gdai/appkey/labelGroup/queryGroup`, method: 'GET', data:{groupUuid}})
}

//GDAI-AppKey增加标签组
export function addGroup(data) {
    return fetch({url: `/gdai/appkey/labelGroup/addGroup`, method: 'POST', data})
}

//GDAI-AppKey标签组修改
export function updateGroup(data) {
    return fetch({url: `/gdai/appkey/labelGroup/updateGroup`, method: 'PUT', data})
}


/*
*
*标签
**/

// //GDAI-AppKey标签组下属标签列表查询
// export function getGroupTagList(data) {
//     return fetch({url: `/gdai/appkey/label/getGroupLabelList`, method: 'GET', data})
// }

//GDAI-AppKey标签查询
export function getLabel(labelUuid) {
    return fetch({url: `/gdai/appkey/label/getLabel`, method: 'GET', data:{labelUuid}})
}

//GDAI-AppKey标签增加
export function addLabel(data) {
    return fetch({url: `/gdai/appkey/label/addLabel`, method: 'POST', data})
}

//GDAI-AppKey标签修改
export function updateLabel(data) {
    return fetch({url: `/gdai/appkey/label/updateLabel`, method: 'PUT', data})
}
//GDAI-AppKey标签删除
export function deleteLabel(labelUuid) {
    return fetch({url: `/gdai/appkey/label/deleteLabel`, method: 'PUT', data:{labelUuid}})
}

/*
*GDAI-AppKey标签商品
*
**/

//GDAI-AppKey标签修改
export function analysisSku(data) {
    return fetch({url: `/gdai/appkey/labelItem/analysisSku`, method: 'POST', data})
}

//GDAI-AppKey标签修改
export function addSample(data) {
    return fetch({url: `/gdai/appkey/labelSample/addSample`, method: 'POST', data})
}

//GDAI-AppKey资源服务器
export function getActiveRescSerList(data) {
    return fetch({url: `/gdai/appkey/rescServer/getActiveRescSerList`, method: 'GET', data})
}


//GDAI-AppKey标签样图列表查询
export function getSampleList(labelId,useType) {
    return fetch({url: `/gdai/appkey/labelSample/getSampleList`, method: 'GET', data:{labelId,useType}})
}

//GDAI-AppKey标签样本图删除
export function deleteSampleImage(imgId) {
    return fetch({url: `/gdai/appkey/labelSample/deleteSampleImage`, method: 'PUT', data:{imgId}})
}

//设备以SkuNum增加订单(返回支付码）
export function addOrderWithSkuNumByDevice(data) {
    return fetch({url: `/order/appkeyApi/addOrderWithSkuNumByDevice`, method: 'POST', data})
}

//AppKey公共基础-获取AES加解密信息
export function getAsKeys() {
    return fetch({url: `/org/appKeyCombase/getAsKeys`, method: 'GET'})
}

//AppKey公共基础-获取TJ消息通道服务信息
export function getTjMqInfo() {
    return fetch({url: `/org/appKeyCombase/getTjMqInfo`, method: 'GET'})
}

//GDAI-AppKey日志修改状态
export function updateRecgLogStatus(logId) {
    return fetch({url: `/gdai/appkey/labelItem/updateRecgLogStatus`, method: 'PUT', data:{logId}})
}
