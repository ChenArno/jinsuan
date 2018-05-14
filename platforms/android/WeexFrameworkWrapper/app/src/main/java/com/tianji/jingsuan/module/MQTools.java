package com.tianji.jingsuan.module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXVContainer;
import com.tianji.jingsuan.utils.MqObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chencentury on 2018/4/28.
 */

public class MQTools extends WXComponent<View> {
    public static final String TAG = "======>";
    private Boolean DEBUG = false;
    private MQHelper mqHelper;
    private WXSDKInstance mWXSDKInstance;

    public MQTools(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        if (mWXSDKInstance == null) {
            mWXSDKInstance = instance;
        }
    }

    @Override
    protected View initComponentHostView(@NonNull Context context) {
        View view = new View(context);

        return view;
    }

    @JSMethod
    public void init(String ret, String keyBytes, String iv, JSCallback success, JSCallback error) {
        try {
            String result = AESKit.AesCbcDecrypt(ret, keyBytes, iv);
            success.invoke(result);
        } catch (Exception e) {
            Log.i(TAG, "result: " + e.toString());
            error.invoke(e.toString());
            e.printStackTrace();
        }
    }


    private Boolean isMqConnect = false;

    //tjmqAddress,tjmqPort,tjmqSecret,tjmqUser,tjmqVirtualHost
    @JSMethod
    public void MqConnect(final String msg, final String mac) {
        if (DEBUG) return;
        final MqObject mqObject = new MqObject(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mqHelper = MQHelper.getMqHelper();
                mqHelper.setMQMessageListener(mqMessageListener);
//                connectMqServer(msg, mac);
                while (true) { // MQ创建通道失败要进行重新创建,MQ服务器连接失败要进行重连
//                    if(mqHelper.getConn() != null) return;
                    Boolean isConnect = mqHelper.connectRabbitMQServer(mqObject);
                    if (isConnect) {
                        System.out.println("MQ服务器连接成功");
                        boolean isCreateChannel = MQHelper.getMqHelper().createChannel(mac, null, null);
                        if (isCreateChannel) {
                            System.out.println("MQ创建通道成功");
                            break;
                        } else {
                            System.out.println("MQ创建通道失败");
                        }
                    } else {
                        System.out.println("MQ服务器连接失败");
                    }
//                                            JSONObject jsonObject = new JSONObject(msg);
//                        String tjmqAddress = jsonObject.getString("tjmqAddress");
//                        int tjmqPort = jsonObject.getInt("tjmqPort");
//                        String tjmqSecret = jsonObject.getString("tjmqSecret");
//                        String tjmqUser = jsonObject.getString("tjmqUser");
//                        String tjmqVirtualHost = jsonObject.getString("tjmqVirtualHost");
//
//                        succ = mqHelper.connectRabbitMQServer(tjmqUser, tjmqSecret, tjmqVirtualHost, tjmqAddress, tjmqPort);
//                    succ = mqHelper.connectRabbitMQServer("admin1","123456",tjmqVirtualHost,"172.16.0.192",5672);
//                        if (succ) {
//                            mqHelper.createChannel(mac,null,null);
//                        }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "error: " + e.toString());
                    }
                }
            }
        }).start();
    }

    private void connectMqServer(MqObject msg, String mac) {
        if (!isMqConnect) {
            Boolean isConnect = mqHelper.connectRabbitMQServer(msg);
            if (isConnect) {
                isMqConnect = MQHelper.getMqHelper().createChannel(mac, null, null);
            }
            try {
                Thread.sleep(500);
                connectMqServer(msg, mac);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "error: " + e.toString());
            }
        }
    }

    public void close() {
        if (mqHelper != null) {
            mqHelper.close();
        }
    }

    private MQHelper.MQMessageListener mqMessageListener = new MQHelper.MQMessageListener() {
        @Override
        public void getMessage(String consumerTag, long deliveryTag, String message) {
            Map<String, Object> params = new HashMap<>();
            params.put("data", message);
            mWXSDKInstance.fireGlobalEventCallback("mq.sendMsg", params);
            mqHelper.deleteMessage(consumerTag, deliveryTag);
        }
    };

}
