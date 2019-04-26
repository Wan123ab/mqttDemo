package com.wq.mqttdemo.service;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * MQTTClient简单封装类
 */
public class WqMqttClient {

    private Logger logger = LoggerFactory.getLogger(WqMqttClient.class);

    private MqttClient mqttclient;

    private MqttConnectOptions mqttConnectOptions;

    public WqMqttClient() {
        try {
            //注意：创建client需要连接的地址为tcp协议，端口号1883
            mqttclient = new MqttClient("tcp://127.0.0.1:1883","mqttClientDemo");
            mqttConnectOptions = new MqttConnectOptions();

            mqttConnectOptions.setUserName("admin");
            mqttConnectOptions.setPassword("public".toCharArray());
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setConnectionTimeout(10000);
            //设置心跳保持时间
            mqttConnectOptions.setKeepAliveInterval(5000);
            //设置是否自动重连
            mqttConnectOptions.setAutomaticReconnect(true);

            mqttclient.setCallback(new WqMqttCallBack(this));

            connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 尝试连接
     */
    public synchronized void connect() {
        try {
            if (!isConnected()) {
                mqttclient.connect(mqttConnectOptions);
            }
        } catch (MqttException e) {
            logger.error("连接MQTT Broker失败,mqttConnectOptions={}", mqttConnectOptions, e);
        }
    }

    /**
     * 判断是否连接
     *
     * @return boolean
     */
    public boolean isConnected() {
        return mqttclient.isConnected();
    }


    /**
     * 关闭mqtt客户端连接
     */
    public void closeMqttClient() {
        try {
            //判断mqttclient是否连接
            if (mqttclient.isConnected()) {
                //关闭mqttclien连接
                mqttclient.disconnect();
                mqttclient.close();
            }
            mqttclient = null;
        } catch (MqttException e) {
            logger.error("closeMqttClient error", e);
        }
    }

    public void publish(String topic, Object obj) throws Exception {
        MqttMessage message = new MqttMessage();
        String payload = JSONObject.toJSONString(obj);
        try {
            message.setPayload(payload.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("message.setPayload error", e);
        }
        message.setQos(2);
        message.setRetained(false);
        try {
            mqttclient.publish(topic, message);
        } catch (Exception e1) {
            try {
                //调用rest api发送消息
                MqttRestService.publish(topic,message);
            } catch (Exception e) {
                logger.error("发往topic={}payload={}失败", topic, payload, e);
            }
            logger.error("发往topic={}payload={}失败", topic, payload, e1);
        }
    }



}
