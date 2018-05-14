package com.tianji.jingsuan.utils;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

/**
 * Created by chencentury on 2018/5/9.
 */

public class MqObject {
    private static MqObject mqObject;
    private String userName;
    private String Password;
    private String Host;
    private int Port;
    private String VirtualHost;

    public MqObject(String msg) {
        this.init(msg);
    }

//    public static MqObject getMqObject(String msg) {
//        if(mqObject == null){
//            mqObject = new MqObject(msg);
//        }
//        mqObject.init(msg);
//        return mqObject;
//    }

    private void init(String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            this.Host = jsonObject.getString("tjmqAddress");
            this.Port = jsonObject.getInt("tjmqPort");
            this.Password = jsonObject.getString("tjmqSecret");
            this.userName = jsonObject.getString("tjmqUser");
            this.VirtualHost = jsonObject.getString("tjmqVirtualHost");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setVirtualHost(String virtualHost) {
        this.VirtualHost = virtualHost;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPort(int port) {
        this.Port = port;
    }

    public void setHost(String host) {
        this.Host = host;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getHost() {
        return Host;
    }

    public String getPassword() {
        return Password;
    }

    public int getPort() {
        return Port;
    }

    public String getUserName() {
        return userName;
    }

    public String getVirtualHost() {
        return VirtualHost;
    }
}
