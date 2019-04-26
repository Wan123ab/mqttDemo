package com.wq.mqttdemo.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTTClient回调接口
 */
public class WqMqttCallBack implements MqttCallback {

    private WqMqttClient mqttClient;

    public WqMqttCallBack() {
    }

    public WqMqttCallBack(WqMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    /**
     * 断线重连
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("连接异常断开");
        mqttClient.connect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("有消息过来啦，"+message.getPayload().toString());

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
